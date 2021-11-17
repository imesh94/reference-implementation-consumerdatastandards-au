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
import org.apache.commons.lang3.StringUtils;
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
                JSONObject payloadData = consentPersistData.getPayload();
                // get the consent model to be created
                AccountConsentRequest accountConsentRequest = CDSDataRetrievalUtil
                        .getAccountConsent(consentData, consentData.getMetaDataMap().
                                        get(CDSConsentExtensionConstants.EXPIRATION_DATE_TIME).toString(),
                                (List<PermissionsEnum>) consentData.getMetaDataMap()
                                        .get(CDSConsentExtensionConstants.PERMISSIONS));

                Gson gson = new Gson();
                String requestString = gson.toJson(accountConsentRequest);

                // add commonAuthId and sharing_duration_value to consent attributes
                Map<String, String> consentAttributes = addMetaDataToConsentAttributes(consentData, consentPersistData);

                // create new consent resource and set attributes to be stored when consent is created
                ConsentResource consentResource = createConsentAndSetAttributes(consentData, requestString,
                        consentAttributes);

                // create authorizable consent using the consent resource above
                DetailedConsentResource createdConsent = null;
                try {
                    createdConsent = createConsent(consentCoreService, consentResource, consentData);
                } catch (ConsentManagementException e) {
                    log.error("Error while creating the consent");
                    throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                            "Error while creating the consent");
                }
                // set consentId for the consentData from obtained from detailed consent resource
                String consentId = createdConsent.getConsentID();
                consentData.setConsentId(consentId);

                // get the latest authorization resource from updated time parameter
                AuthorizationResource authorizationResource = getLatestAuthResource(createdConsent);

                consentData.setAuthResource(authorizationResource);
                consentData.setConsentResource(consentResource);

                if (consentData.getConsentId() == null && consentData.getConsentResource() == null) {
                    log.error("Consent ID not available in consent data");
                    throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                            "Consent ID not available in consent data");
                }

                // get user consented accounts list to bind them with the consent
                ArrayList<String> accountIdList = getAccountIdList(payloadData);

                // TODO: Re-auth scenario implementation
                // TODO: Revoke existing arrangement
                // TODO: Data reporting

                // bind user consented accounts with the create consent
                bindUserAccountsToConsent(consentCoreService, consentResource, consentData, accountIdList);

                // Get joint account data from consentPersistData
                Object jointAccountIdWithUsersObj = consentPersistData.
                        getMetadata().get(CDSConsentExtensionConstants.MAP_JOINT_ACCOUNTS_ID_WITH_USERS);
                Object usersWithMultipleJointAccountsObj = consentPersistData.
                        getMetadata().get(CDSConsentExtensionConstants.MAP_USER_ID_WITH_JOINT_ACCOUNTS);

                // bind user consented joint accounts with the created consent
                if (jointAccountIdWithUsersObj != null && usersWithMultipleJointAccountsObj != null) {
                    bindJointAccountUsersToConsent(consentResource, consentData,
                            (Map<String, List<String>>) jointAccountIdWithUsersObj,
                            (Map<String, ArrayList<String>>) usersWithMultipleJointAccountsObj);
                }
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
                CDSConsentExtensionConstants.CREATED_STATUS, CDSConsentExtensionConstants.AUTH_RESOURCE_TYPE_PRIMARY,
                true);
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

    /**
     * Create consent resource to the given parameters
     *
     * @param consentData       consent data
     * @param requestString     request string of consent resource
     * @param consentAttributes map of consent attributes
     * @return consentResource
     */
    private ConsentResource createConsentAndSetAttributes(ConsentData consentData, String requestString, Map<String,
            String> consentAttributes) {

        ConsentResource consentResource = new ConsentResource(consentData.getClientId(),
                requestString, consentData.getType(), CDSConsentExtensionConstants.AWAITING_AUTH_STATUS);

        consentResource.setConsentAttributes(consentAttributes);
        consentResource
                .setRecurringIndicator((long) consentData.getMetaDataMap()
                        .get(CDSConsentExtensionConstants.SHARING_DURATION_VALUE) != 0);
        if (!CDSConsentExtensionConstants.ZERO.equalsIgnoreCase(consentData.getMetaDataMap()
                .get(CDSConsentExtensionConstants.EXPIRATION_DATE_TIME).toString())) {
            consentResource.setValidityPeriod(((OffsetDateTime) consentData.getMetaDataMap()
                    .get(CDSConsentExtensionConstants.EXPIRATION_DATE_TIME)).toEpochSecond());
        } else {
            consentResource.setValidityPeriod(0);
        }
        return consentResource;
    }

    /**
     * Add meta data retrieved from web app to consent attributes
     *
     * @param consentData        consent data
     * @param consentPersistData consent persist data
     * @return Map of consentAttributes to be stored with consent resource
     */
    private Map<String, String> addMetaDataToConsentAttributes(ConsentData consentData,
                                                               ConsentPersistData consentPersistData) {

        Map<String, String> consentAttributes = new HashMap<>();

        consentAttributes.put(CDSConsentExtensionConstants.COMMON_AUTH_ID,
                consentPersistData.getBrowserCookies().get(CDSConsentExtensionConstants.COMMON_AUTH_ID));
        consentAttributes.put(CDSConsentExtensionConstants.SHARING_DURATION_VALUE, consentData.getMetaDataMap()
                .get(CDSConsentExtensionConstants.SHARING_DURATION_VALUE).toString());

        final Object jointAccountsPayload = consentPersistData.getMetadata()
                .get(CDSConsentExtensionConstants.JOINT_ACCOUNTS_PAYLOAD);
        if (jointAccountsPayload != null && StringUtils.isNotBlank(jointAccountsPayload.toString())) {
            consentAttributes.put(CDSConsentExtensionConstants.JOINT_ACCOUNTS_PAYLOAD, jointAccountsPayload.toString());
        }

        return consentAttributes;
    }

    /**
     * Get latest authorization using updated time and check whether its null or in proper state
     *
     * @param createdConsent consent created
     * @return Latest authorization resource
     */
    private AuthorizationResource getLatestAuthResource(DetailedConsentResource createdConsent)
            throws ConsentException {

        // get authorization resources from created consent
        ArrayList<AuthorizationResource> authorizationResources = createdConsent.getAuthorizationResources();

        long updatedTime = 0;
        AuthorizationResource authorizationResource = null;
        if (!authorizationResources.isEmpty()) {
            for (AuthorizationResource authorizationResourceValue : authorizationResources) {
                if (authorizationResourceValue.getUpdatedTime() > updatedTime) {
                    updatedTime = authorizationResourceValue.getUpdatedTime();
                    authorizationResource = authorizationResourceValue;
                }
            }
        }
        if (authorizationResource == null) {
            log.error("Auth resource not available in consent data");
            throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                    "Auth resource not available in consent data");
        }
        if (!authorizationResource.getAuthorizationStatus()
                .equals(CDSConsentExtensionConstants.CREATED_STATUS)) {
            log.error("Authorization not in authorizable state");
            throw new ConsentException(ResponseStatus.BAD_REQUEST, "Authorization not in authorizable state");
        }
        return authorizationResource;
    }

    /**
     * Get account list from payload data and check for validity
     *
     * @param payloadData payload data of retrieved from persist data
     * @return List of user consented accounts
     */
    private ArrayList<String> getAccountIdList(JSONObject payloadData) throws ConsentException {

        if (payloadData.get(CDSConsentExtensionConstants.ACCOUNT_IDS) == null
                || !(payloadData.get(CDSConsentExtensionConstants.ACCOUNT_IDS) instanceof JSONArray)) {
            log.error("Account IDs not available in persist request");
            throw new ConsentException(ResponseStatus.BAD_REQUEST,
                    "Account IDs not available in persist request");
        }

        JSONArray accountIds = (JSONArray) payloadData.get(CDSConsentExtensionConstants.ACCOUNT_IDS);
        ArrayList<String> accountIdsList = new ArrayList<>();
        for (Object account : accountIds) {
            if (!(account instanceof String)) {
                log.error("Account IDs format error in persist request");
                throw new ConsentException(ResponseStatus.BAD_REQUEST,
                        "Account IDs format error in persist request");
            }
            accountIdsList.add((String) account);
        }
        return accountIdsList;
    }

    @Generated(message = "Excluding from code coverage since it requires a service call")
    private void bindJointAccountUsersToConsent(ConsentResource consentResource, ConsentData consentData,
                                                  Map<String, List<String>> jointAccountIdWithUsers,
                                                  Map<String, ArrayList<String>> usersWithMultipleJointAccounts)
            throws ConsentManagementException {

        final List<String> alreadyAddedUsers = new ArrayList<>();
        // add primary user to already added users list
        alreadyAddedUsers.add(consentData.getUserId());

        for (Map.Entry<String, List<String>> entry : jointAccountIdWithUsers.entrySet()) {
            List<String> userIdList = entry.getValue();
            for (String userId : userIdList) {
                if (StringUtils.isNotBlank(userId) && !alreadyAddedUsers.contains(userId)) {
                    AuthorizationResource createdAuthResource = consentCoreService.createConsentAuthorization(
                            getSecondaryAuthorizationResource(consentData.getConsentId(), userId));

                    consentCoreService.bindUserAccountsToConsent(consentResource, userId,
                            createdAuthResource.getAuthorizationID(), usersWithMultipleJointAccounts.get(userId),
                            CDSConsentExtensionConstants.AUTHORIZED_STATUS,
                            CDSConsentExtensionConstants.AUTHORIZED_STATUS);

                    alreadyAddedUsers.add(userId);
                }
            }
        }
    }

    private AuthorizationResource getSecondaryAuthorizationResource(String consentId, String secondaryUserId) {
        AuthorizationResource newAuthResource = new AuthorizationResource();
        newAuthResource.setConsentID(consentId);
        newAuthResource.setUserID(secondaryUserId);
        newAuthResource.setAuthorizationStatus(CDSConsentExtensionConstants.CREATED_STATUS);
        newAuthResource.setAuthorizationType(CDSConsentExtensionConstants.AUTH_RESOURCE_TYPE_LINKED);

        return newAuthResource;
    }
}
