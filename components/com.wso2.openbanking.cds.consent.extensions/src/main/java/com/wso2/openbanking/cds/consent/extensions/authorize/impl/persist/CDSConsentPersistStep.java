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
import com.wso2.openbanking.cds.consent.extensions.authorize.impl.utils.CDSDataRetrievalUtil;
import com.wso2.openbanking.cds.consent.extensions.authorize.impl.utils.PermissionsEnum;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    private static final String AUTHORISED_STATUS = "authorised";
    private static final String REJECTED_STATUS = "rejected";

    @Override
    public void execute(ConsentPersistData consentPersistData) throws ConsentException {

        try {
            ConsentData consentData = consentPersistData.getConsentData();
            JSONObject payloadData =  consentPersistData.getPayload();
            //get the consent object
            AccountConsentRequest accountConsentRequest = CDSDataRetrievalUtil.getAccountConsent(consentData,
                            consentData.getMetaDataMap().get("expirationDatetime").toString(),
                    (List<PermissionsEnum>) consentData.getMetaDataMap().get("permissions"));

            Gson gson = new Gson();
            String requestString = gson.toJson(accountConsentRequest);

            Map<String, String> consentAttributes = new HashMap<>();
            consentAttributes.put("commonAuthId", ((JSONObject) payloadData.get("metadata"))
                    .getAsString("commonAuthId"));
            consentAttributes.put("sharing_duration_value", consentData.getMetaDataMap().
                    get("sharing_duration_value").toString());

            ConsentResource requestedConsent = new ConsentResource(consentData.getClientId(),
                requestString, consentData.getType(), "awaitingAuthorization");

            requestedConsent.setConsentAttributes(consentAttributes);

            DetailedConsentResource createdConsent = null;
            try {
                createdConsent = consentCoreService.createAuthorizableConsent(requestedConsent,
                        null, "created", "awaitingAuthorization", true);
            } catch (ConsentManagementException e) {
                log.error(e.getMessage());
            }
            String consentId = createdConsent.getConsentID();
            consentData.setConsentId(consentId);

            ConsentResource consentResource = consentCoreService.getConsent(consentId, false);
            AuthorizationResource authorizationResource = consentCoreService.searchAuthorizations(consentId).get(0);

            if (!authorizationResource.getAuthorizationStatus().equals("created")) {
                log.error("Authorization not in authorizable state");
                //Currently throwing error as 400 response. Developer also have the option of appending a field IS_ERROR
                // to the jsonObject and showing it to the user in the webapp. If so, the IS_ERROR have to be checked in
                // any later steps.
                throw new ConsentException(ResponseStatus.BAD_REQUEST, "Authorization not in authorizable state");
            }

//            consentData.setType(consentResource.getConsentType());
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

            if (payloadData.get("accountIds") == null || !(payloadData.get("accountIds") instanceof JSONArray)) {
                log.error("Account IDs not available in persist request");
                throw new ConsentException(ResponseStatus.BAD_REQUEST,
                        "Account IDs not available in persist request");
            }

            JSONArray accountIds = (JSONArray) payloadData.get("accountIds");
            ArrayList<String> accountIdsString = new ArrayList<>();
            for (Object account : accountIds) {
                if (!(account instanceof String)) {
                    log.error("Account IDs format error in persist request");
                    throw new ConsentException(ResponseStatus.BAD_REQUEST,
                            "Account IDs format error in persist request");
                }
                accountIdsString.add((String) account);
            }

            String consentStatus;
            String authStatus;

            if (consentPersistData.getApproval()) {
                consentStatus = AUTHORISED_STATUS;
                authStatus = AUTHORISED_STATUS;
            } else {
                consentStatus = REJECTED_STATUS;
                authStatus = REJECTED_STATUS;
            }

            // TODO Joint Account Implementation
            // TODO Re-auth Scenario Implementation
            consentCoreService.bindUserAccountsToConsent(consentResource, consentData.getUserId(),
                    consentData.getAuthResource().getAuthorizationID(), accountIdsString, authStatus, consentStatus);
        } catch (ConsentManagementException e) {
            throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                    "Exception occurred while persisting consent");
        }
    }
}
