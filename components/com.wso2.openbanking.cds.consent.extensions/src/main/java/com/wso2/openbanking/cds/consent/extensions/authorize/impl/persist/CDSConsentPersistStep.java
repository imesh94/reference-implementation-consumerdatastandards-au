/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */
package com.wso2.openbanking.cds.consent.extensions.authorize.impl.persist;

import com.google.gson.Gson;
import com.wso2.openbanking.accelerator.common.exception.ConsentManagementException;
import com.wso2.openbanking.accelerator.common.util.Generated;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentPersistData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentPersistStep;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.AuthorizationResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.DetailedConsentResource;
import com.wso2.openbanking.accelerator.consent.mgt.service.impl.ConsentCoreServiceImpl;
import com.wso2.openbanking.cds.consent.extensions.authorize.impl.model.AccountConsentRequest;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.CDSDataRetrievalUtil;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.PermissionsEnum;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Consent persist step CDS implementation.
 */
public class CDSConsentPersistStep implements ConsentPersistStep {

    private static final Log log = LogFactory.getLog(CDSConsentPersistStep.class);
    private static final ConsentCoreServiceImpl consentCoreService = new ConsentCoreServiceImpl();

    @Override
    public void execute(ConsentPersistData consentPersistData) throws ConsentException {

        if (consentPersistData.getApproval()) {
            try {
                ConsentData consentData = consentPersistData.getConsentData();
                JSONObject payloadData =  consentPersistData.getPayload();
                //get the consent object
                AccountConsentRequest accountConsentRequest = CDSDataRetrievalUtil
                        .getAccountConsent(consentData, consentData.getMetaDataMap().
                                        get(CDSConsentExtensionConstants.EXPIRATION_DATE_TIME).toString(),
                                (List<PermissionsEnum>) consentData.getMetaDataMap()
                                        .get(CDSConsentExtensionConstants.PERMISSIONS));

                Gson gson = new Gson();
                String requestString = gson.toJson(accountConsentRequest);

                Map<String, String> consentAttributes = new HashMap<>();
                consentAttributes.put(CDSConsentExtensionConstants.COMMON_AUTH_ID,
                        ((JSONObject) payloadData.get(CDSConsentExtensionConstants.METADATA))
                                .getAsString(CDSConsentExtensionConstants.COMMON_AUTH_ID));
                consentAttributes.put(CDSConsentExtensionConstants.SHARING_DURATION_VALUE, consentData.getMetaDataMap()
                        .get(CDSConsentExtensionConstants.SHARING_DURATION_VALUE).toString());

                ConsentResource requestedConsent = new ConsentResource(consentData.getClientId(),
                        requestString, consentData.getType(), CDSConsentExtensionConstants.AWAITING_AUTH_STATUS);

                requestedConsent.setConsentAttributes(consentAttributes);
                requestedConsent
                        .setRecurringIndicator((long) consentData.getMetaDataMap()
                                .get(CDSConsentExtensionConstants.SHARING_DURATION_VALUE) != 0);
                requestedConsent.setValidityPeriod(((OffsetDateTime) consentData.getMetaDataMap()
                        .get(CDSConsentExtensionConstants.EXPIRATION_DATE_TIME)).toEpochSecond());

                DetailedConsentResource createdConsent = null;
                try {
                    createdConsent = createConsent(consentCoreService, requestedConsent, consentData);
                } catch (ConsentManagementException e) {
                    log.error(e.getMessage());
                    throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                            "Error while creating the consent");
                }
                String consentId = createdConsent.getConsentID();
                consentData.setConsentId(consentId);

                ConsentResource consentResource = getConsent(consentCoreService, consentId);

                ArrayList<AuthorizationResource> authorizationResources = getAuthorizationResources(consentCoreService,
                        consentId);

                AuthorizationResource authorizationResource = null;
                long updatedTime = 0;
                for (AuthorizationResource authorizationResourceValue: authorizationResources) {
                    if (authorizationResourceValue.getUpdatedTime() > updatedTime) {
                        updatedTime = authorizationResourceValue.getUpdatedTime();
                        authorizationResource = authorizationResourceValue;
                    }
                }

                if (!authorizationResource.getAuthorizationStatus()
                        .equals(CDSConsentExtensionConstants.CREATED_STATUS)) {
                    log.error("Authorization not in authorizable state");
                    throw new ConsentException(ResponseStatus.BAD_REQUEST, "Authorization not in authorizable state");
                }

                consentData.setAuthResource(authorizationResource);
                consentData.setConsentResource(consentResource);

                if (consentData.getConsentId() == null && consentData.getConsentResource() == null) {
                    log.error("Consent ID not available in consent data");
                    throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                            "Consent ID not available in consent data");
                }

                if (consentData.getAuthResource() == null) {
                    log.error("Auth resource not available in consent data");
                    throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                            "Auth resource not available in consent data");
                }

                if (payloadData.get(CDSConsentExtensionConstants.ACCOUNT_IDS) == null
                        || !(payloadData.get(CDSConsentExtensionConstants.ACCOUNT_IDS) instanceof JSONArray)) {
                    log.error("Account IDs not available in persist request");
                    throw new ConsentException(ResponseStatus.BAD_REQUEST,
                            "Account IDs not available in persist request");
                }

                JSONArray accountIds = (JSONArray) payloadData.get(CDSConsentExtensionConstants.ACCOUNT_IDS);
                ArrayList<String> accountIdsString = new ArrayList<>();
                for (Object account : accountIds) {
                    if (!(account instanceof String)) {
                        log.error("Account IDs format error in persist request");
                        throw new ConsentException(ResponseStatus.BAD_REQUEST,
                                "Account IDs format error in persist request");
                    }
                    accountIdsString.add((String) account);
                }

                // TODO: Joint Account implementation
                // TODO: Re-auth scenario implementation
                // TODO: Revoke existing arrangement
                // TODO: Data reporting

                bindUserAccountsToConsent(consentCoreService, consentResource, consentData, accountIdsString);
            } catch (ConsentManagementException e) {
                throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                        "Exception occurred while persisting consent");
            }
        }
    }

    @Generated(message = "Excluding from code coverage since it requires a service call")
    protected DetailedConsentResource createConsent(ConsentCoreServiceImpl consentCoreService,
                                                    ConsentResource requestedConsent, ConsentData consentData)
            throws ConsentManagementException {

        return consentCoreService.createAuthorizableConsent(requestedConsent, consentData.getUserId(),
                CDSConsentExtensionConstants.CREATED_STATUS, CDSConsentExtensionConstants.AWAITING_AUTH_STATUS,
                true);
    }

    @Generated(message = "Excluding from code coverage since it requires a service call")
    protected ConsentResource getConsent(ConsentCoreServiceImpl consentCoreService, String consentId)
            throws ConsentManagementException {

        return consentCoreService.getConsent(consentId, false);

    }

    @Generated(message = "Excluding from code coverage since it requires a service call")
    protected ArrayList<AuthorizationResource> getAuthorizationResources(ConsentCoreServiceImpl consentCoreService,
                                                                         String consentId)
            throws ConsentManagementException {

        return consentCoreService.searchAuthorizations(consentId);
    }

    @Generated(message = "Excluding from code coverage since it requires a service call")
    protected boolean bindUserAccountsToConsent(ConsentCoreServiceImpl consentCoreService,
                                                ConsentResource consentResource, ConsentData consentData,
                                                ArrayList<String> accountIdsString)
            throws ConsentManagementException {

        return consentCoreService.bindUserAccountsToConsent(consentResource, consentData.getUserId(),
                consentData.getAuthResource().getAuthorizationID(), accountIdsString,
                CDSConsentExtensionConstants.AUTHORIZED_STATUS, CDSConsentExtensionConstants.AUTHORIZED_STATUS);
    }
}
