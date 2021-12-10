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
import com.wso2.openbanking.accelerator.consent.mgt.dao.constants.ConsentMgtDAOConstants;
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
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Consent persist step CDS implementation.
 */
public class CDSConsentPersistStep implements ConsentPersistStep {

    private static final Log log = LogFactory.getLog(CDSConsentPersistStep.class);
    private final ConsentCoreServiceImpl consentCoreService;

    public CDSConsentPersistStep() {
        this.consentCoreService = new ConsentCoreServiceImpl();
    }

    public CDSConsentPersistStep(ConsentCoreServiceImpl consentCoreService) {
        this.consentCoreService = consentCoreService;
    }

    @Override
    public void execute(ConsentPersistData consentPersistData) throws ConsentException {

        if (consentPersistData.getApproval()) {
            try {
                ConsentData consentData = consentPersistData.getConsentData();
                JSONObject payloadData = consentPersistData.getPayload();
                String userId = consentData.getUserId();
                ArrayList<String> accountIdList = getAccountIdList(payloadData);
                boolean isConsentAmendment = false;
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

                // Consent amendment flow
                if (consentData.getMetaDataMap().containsKey(CDSConsentExtensionConstants.IS_CONSENT_AMENDMENT) &&
                        Boolean.parseBoolean(consentData.getMetaDataMap().get(
                                CDSConsentExtensionConstants.IS_CONSENT_AMENDMENT).toString())) {
                    isConsentAmendment = true;
                    String cdrArrangementId = consentData.getMetaDataMap().get(CDSConsentExtensionConstants.
                            CDR_ARRANGEMENT_ID).toString();
                    String authResorceId = consentData.getMetaDataMap().get(CDSConsentExtensionConstants.
                            AUTH_RESOURCE_ID).toString();
                    String authResourceStatus = consentData.getMetaDataMap().get(CDSConsentExtensionConstants.
                            AUTH_RESOURCE_STATUS).toString();
                    Map<String, ArrayList<String>> accountIdsMap = getCDSAccountIDsMapWithPermissions(accountIdList);
                    // Revoke existing tokens
                    revokeTokens(cdrArrangementId, userId);
                    // Amend consent data
                    String expirationDateTime = consentData.getMetaDataMap().get(
                            CDSConsentExtensionConstants.EXPIRATION_DATE_TIME).toString();
                    long validityPeriod;
                    if (StringUtils.isNotBlank(expirationDateTime) && !CDSConsentExtensionConstants.
                            ZERO.equals(expirationDateTime)) {
                        validityPeriod = ((OffsetDateTime) consentData.getMetaDataMap()
                                .get(CDSConsentExtensionConstants.EXPIRATION_DATE_TIME)).toEpochSecond();
                    } else {
                        validityPeriod = 0;
                    }
                    consentCoreService.amendConsentData(cdrArrangementId, consentResource.getReceipt(),
                            validityPeriod, userId);
                    // Reauthorize consent
                    consentCoreService.reAuthorizeExistingAuthResource(cdrArrangementId, authResorceId,
                            userId, accountIdsMap, authResourceStatus,
                            CDSConsentExtensionConstants.AUTHORIZED_STATUS);
                    // Update consent attributes
                    ArrayList<String> consentAttributesKeyList = new ArrayList<>(consentAttributes.keySet());
                    consentCoreService.deleteConsentAttributes(cdrArrangementId, consentAttributesKeyList);
                    consentCoreService.storeConsentAttributes(cdrArrangementId, consentAttributes);

                } else {
                    // create authorizable consent using the consent resource above
                    DetailedConsentResource createdConsent = null;
                    try {
                        createdConsent = createConsent(consentCoreService, consentResource, consentData);
                    } catch (ConsentManagementException e) {
                        log.error(String.format("Error while creating the consent. %s", e.getMessage()));
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
                    // bind user consented accounts with the create consent
                    bindUserAccountsToConsent(consentCoreService, consentResource, consentData, accountIdList);
                }

                // Get joint account data from consentPersistData
                Object jointAccountIdWithUsersObj = consentPersistData.
                        getMetadata().get(CDSConsentExtensionConstants.MAP_JOINT_ACCOUNTS_ID_WITH_USERS);
                Object usersWithMultipleJointAccountsObj = consentPersistData.
                        getMetadata().get(CDSConsentExtensionConstants.MAP_USER_ID_WITH_JOINT_ACCOUNTS);

                // bind user consented joint accounts with the created consent
                if (jointAccountIdWithUsersObj != null && usersWithMultipleJointAccountsObj != null) {
                    bindJointAccountUsersToConsent(consentResource, consentData,
                            (Map<String, List<String>>) jointAccountIdWithUsersObj,
                            (Map<String, ArrayList<String>>) usersWithMultipleJointAccountsObj, isConsentAmendment);
                }
                // TODO: Data reporting
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
     * Create consent resource to the given parameters.
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
     * Add meta data retrieved from web app to consent attributes.
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
        consentAttributes.put(ConsentMgtDAOConstants.CONSENT_EXPIRY_TIME_ATTRIBUTE,
                getExpirationTimestampAttribute(consentData));
        final Object jointAccountsPayload = consentPersistData.getMetadata()
                .get(CDSConsentExtensionConstants.JOINT_ACCOUNTS_PAYLOAD);
        if (jointAccountsPayload != null && StringUtils.isNotBlank(jointAccountsPayload.toString())) {
            consentAttributes.put(CDSConsentExtensionConstants.JOINT_ACCOUNTS_PAYLOAD, jointAccountsPayload.toString());
        }

        return consentAttributes;
    }

    /**
     * Get latest authorization using updated time and check whether its null or in proper state.
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
     * Get account list from payload data and check for validity.
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
                                                  Map<String, ArrayList<String>> usersWithMultipleJointAccounts,
                                                  boolean isConsentAmendment)
            throws ConsentManagementException {

        final List<String> alreadyAddedUsers = new ArrayList<>();
        // add primary user to already added users list
        alreadyAddedUsers.add(consentData.getUserId());
        // Users who have already stored as auth resources
        final Map<String, AuthorizationResource> reAuthorizableResources = new HashMap<>();
        // Users who need to store as auth resources
        final Set<String> authorizableResources = new HashSet<>();

        final String consentId = StringUtils.isBlank(consentData.getConsentId())
                ? consentData.getMetaDataMap().get(CDSConsentExtensionConstants.CDR_ARRANGEMENT_ID).toString()
                : consentData.getConsentId();
        DetailedConsentResource detailedConsent = consentCoreService.getDetailedConsent(consentId);

        for (Map.Entry<String, List<String>> entry : jointAccountIdWithUsers.entrySet()) {
            List<String> userIdList = entry.getValue();
            for (String userId : userIdList) {
                if (StringUtils.isNotBlank(userId) && !alreadyAddedUsers.contains(userId)) {

                    if (isConsentAmendment) {
                        for (AuthorizationResource authResource : detailedConsent.getAuthorizationResources()) {
                            if (userId.equals(authResource.getUserID())) {
                                reAuthorizableResources.put(userId, authResource);
                            }
                        }
                    }

                    if (!reAuthorizableResources.containsKey(userId)) {
                        authorizableResources.add(userId);
                    }

                    alreadyAddedUsers.add(userId);
                }
            }
        }

        createNewAuthResources(consentId, consentResource, authorizableResources, usersWithMultipleJointAccounts);
    }

    @Generated(message = "Excluding from code coverage since it requires a service call")
    private void createNewAuthResources(String consentId, ConsentResource consentResource,
                                        Set<String> authorizableResources,
                                        Map<String, ArrayList<String>> usersWithMultipleJointAccounts)
            throws ConsentManagementException {

        consentResource.setConsentID(consentId);
        for (String userId : authorizableResources) {
            AuthorizationResource authorizationResource = consentCoreService
                    .createConsentAuthorization(getSecondaryAuthorizationResource(consentId, userId));

            consentCoreService.bindUserAccountsToConsent(consentResource, userId,
                    authorizationResource.getAuthorizationID(), usersWithMultipleJointAccounts.get(userId),
                    CDSConsentExtensionConstants.AUTHORIZED_STATUS,
                    CDSConsentExtensionConstants.AUTHORIZED_STATUS);
        }
    }

    @Generated(message = "Excluding from code coverage since method does not have a logic")
    private AuthorizationResource getSecondaryAuthorizationResource(String consentId, String secondaryUserId) {

        AuthorizationResource newAuthResource = new AuthorizationResource();
        newAuthResource.setConsentID(consentId);
        newAuthResource.setUserID(secondaryUserId);
        newAuthResource.setAuthorizationStatus(CDSConsentExtensionConstants.CREATED_STATUS);
        newAuthResource.setAuthorizationType(CDSConsentExtensionConstants.AUTH_RESOURCE_TYPE_LINKED);

        return newAuthResource;
    }

    @Generated(message = "Excluding from code coverage since method does not have a logic")
    private Map<String, ArrayList<String>> getCDSAccountIDsMapWithPermissions(List<String> accountIds) {
        Map<String, ArrayList<String>> accountIdsMap = new HashMap<>();
        ArrayList<String> permissionsList = new ArrayList<>();
        permissionsList.add("n/a"); // Not applicable for CDS
        for (String accountId : accountIds) {
            accountIdsMap.put(accountId, permissionsList);
        }
        return accountIdsMap;
    }

    /**
     * Revoke tokens for the given user id and arrangement id.
     *
     * @param cdrArrangementId - cdr-arrangement-id
     * @param userId           - userId
     * @throws ConsentManagementException - ConsentManagementException
     */
    private void revokeTokens(String cdrArrangementId, String userId) throws ConsentManagementException {
        DetailedConsentResource detailedConsentResource = consentCoreService.getDetailedConsent(cdrArrangementId);

        try {
            consentCoreService.revokeTokens(detailedConsentResource, userId);
        } catch (IdentityOAuth2Exception e) {
            log.error(String.format("Error occurred while revoking tokens. %s", e.getMessage()));
            throw new ConsentManagementException("Error occurred while revoking tokens.", e);
        }
    }

    /**
     * Method to append the consent expiration time (UTC) as a consent attribute.
     *
     * @param consentData ConsentData
     */
    private String getExpirationTimestampAttribute(ConsentData consentData) {

        Object expireTime = consentData.getMetaDataMap().get(CDSConsentExtensionConstants.EXPIRATION_DATE_TIME);
        long expireTimestamp;
        if (expireTime != null && !CDSConsentExtensionConstants.ZERO.equals(expireTime.toString())) {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(expireTime.toString());
            // Retrieve the UTC timestamp in long from expiry time.
            expireTimestamp = Instant.from(zonedDateTime).getEpochSecond();
        } else {
            OffsetDateTime currentTime = OffsetDateTime.now(ZoneOffset.UTC);
            expireTimestamp = currentTime.plusSeconds(CDSConsentExtensionConstants.CDS_DEFAULT_EXPIRY).toEpochSecond();
        }
        return Long.toString(expireTimestamp);
    }
}
