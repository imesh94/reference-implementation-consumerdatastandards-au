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

package com.wso2.openbanking.cds.consent.extensions.admin.impl;

import com.wso2.openbanking.accelerator.common.config.OpenBankingConfigParser;
import com.wso2.openbanking.accelerator.common.exception.ConsentManagementException;
import com.wso2.openbanking.accelerator.consent.extensions.admin.impl.DefaultConsentAdminHandler;
import com.wso2.openbanking.accelerator.consent.extensions.admin.model.ConsentAdminData;
import com.wso2.openbanking.accelerator.consent.extensions.admin.model.ConsentAdminHandler;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.AuthorizationResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentHistoryResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentMappingResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.DetailedConsentResource;
import com.wso2.openbanking.accelerator.consent.mgt.service.constants.ConsentCoreServiceConstants;
import com.wso2.openbanking.accelerator.consent.mgt.service.impl.ConsentCoreServiceImpl;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.PermissionsEnum;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import com.wso2.openbanking.cds.consent.extensions.common.SecondaryAccountOwnerTypeEnum;
import com.wso2.openbanking.cds.consent.extensions.validate.utils.CDSConsentValidatorUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants.AUTH_RESOURCE_TYPE_PRIMARY;
import static com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants.CONSENT_STATUS_REVOKED;

/**
 * Consent admin handler CDS implementation.
 */
public class CDSConsentAdminHandler implements ConsentAdminHandler {
    protected static final String CONSENT_ID = "consentID";
    protected static final String USER_ID = "userID";
    protected static final String AMENDMENT_REASON_ACCOUNT_WITHDRAWAL = "JAMAccountWithdrawal";
    protected static final String CDR_ARRANGEMENT_ID = "cdrArrangementID";
    private static final Log log = LogFactory.getLog(CDSConsentAdminHandler.class);
    private final ConsentCoreServiceImpl consentCoreService;
    private final ConsentAdminHandler defaultConsentAdminHandler;

    public CDSConsentAdminHandler() {
        this.consentCoreService = new ConsentCoreServiceImpl();
        this.defaultConsentAdminHandler = new DefaultConsentAdminHandler();
    }

    public CDSConsentAdminHandler(ConsentCoreServiceImpl consentCoreService, ConsentAdminHandler consentAdminHandler) {
        this.consentCoreService = consentCoreService;
        this.defaultConsentAdminHandler = consentAdminHandler;
    }

    @Override
    public void handleSearch(ConsentAdminData consentAdminData) throws ConsentException {
        this.defaultConsentAdminHandler.handleSearch(consentAdminData);

        // Filter the consent data based on the profiles if profiles are available in the query params.
        if (consentAdminData.getQueryParams().containsKey(CDSConsentExtensionConstants.PROFILES)) {
            ArrayList profiles = ((ArrayList) consentAdminData.getQueryParams().get(
                    CDSConsentExtensionConstants.PROFILES));
            if (profiles.size() > 0) {
                JSONArray filteredConsentData = new JSONArray();
                for (Object consentObj : (JSONArray) consentAdminData.getResponsePayload().get(
                        CDSConsentExtensionConstants.DATA)) {
                    JSONObject consent = (JSONObject) consentObj;
                    JSONObject consentAttributes = (JSONObject) consent.get(
                            CDSConsentExtensionConstants.CONSENT_ATTRIBUTES);
                    if (consentAttributes.containsKey(CDSConsentExtensionConstants.CUSTOMER_PROFILE_TYPE)) {
                        String customerProfileType = consentAttributes.get(
                                CDSConsentExtensionConstants.CUSTOMER_PROFILE_TYPE).toString().split("-")[0];
                        for (Object profile : profiles) {
                            if (profile.toString().equalsIgnoreCase(customerProfileType)) {
                                filteredConsentData.add(consent);
                            }
                        }
                    }
                }
                JSONObject responseMetadata = (JSONObject) consentAdminData.getResponsePayload().get(
                        CDSConsentExtensionConstants.METADATA);
                responseMetadata.put(CDSConsentExtensionConstants.TOTAL, filteredConsentData.size());
                responseMetadata.put(CDSConsentExtensionConstants.COUNT, filteredConsentData.size());
                consentAdminData.getResponsePayload().put(CDSConsentExtensionConstants.DATA, filteredConsentData);
            }
        }

        // filter secondary user consents if 'secondaryAccountInfo' is available in the query params.
        if (consentAdminData.getQueryParams().containsKey(CDSConsentExtensionConstants.SECONDARY_ACCOUNT_INFO)) {
            filterSecondaryUserConsents(consentAdminData);
        }
    }

    @Override
    public void handleRevoke(ConsentAdminData consentAdminData) throws ConsentException {
        try {
            Map queryParams = consentAdminData.getQueryParams();
            final String consentID = validateAndGetQueryParam(queryParams, CONSENT_ID);

            if (StringUtils.isBlank(consentID)) {
                throw new ConsentException(ResponseStatus.BAD_REQUEST, "Mandatory parameter consent ID not available");
            } else {
                final String userID = validateAndGetQueryParam(queryParams, USER_ID);
                DetailedConsentResource detailedConsentResource = this.consentCoreService.getDetailedConsent(consentID);
                if (detailedConsentResource != null) {
                    if (StringUtils.isNotBlank(userID) && !isPrimaryUserRevoking(detailedConsentResource, userID)) {
                        // Deactivate consent mappings as secondary consent holder
                        deactivateAccountMappings(detailedConsentResource, userID);
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Revoke consent for consentID " + consentID);
                        }
                        // Revoke consent as primary consent holder
                        revokeConsentAsPrimaryUser(detailedConsentResource, userID);
                    }
                }
            }
            consentAdminData.setResponseStatus(ResponseStatus.NO_CONTENT);
        } catch (ConsentManagementException e) {
            throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                    "Exception occurred while revoking consents");
        }
    }

    @Override
    public void handleConsentAmendmentHistoryRetrieval(ConsentAdminData consentAdminData) throws ConsentException {

        JSONObject response = new JSONObject();
        String consentID = null;
        String userID = null;
        Map queryParams = consentAdminData.getQueryParams();

        if (validateAndGetQueryParam(queryParams, CDR_ARRANGEMENT_ID) != null) {
            consentID = validateAndGetQueryParam(queryParams, CDR_ARRANGEMENT_ID);
        }
        if (validateAndGetQueryParam(queryParams, USER_ID) != null) {
            userID = validateAndGetQueryParam(queryParams, USER_ID);
        }

        if (StringUtils.isBlank(consentID)) {
            log.error("Request missing the mandatory query parameter cdrArrangementID");
            throw new ConsentException(ResponseStatus.BAD_REQUEST, "Mandatory query parameter cdrArrangementID " +
                    "not available");
        }

        if (StringUtils.isBlank(userID)) {
            log.error("Request missing the mandatory query parameter userID");
            throw new ConsentException(ResponseStatus.BAD_REQUEST, "Mandatory query parameter userID " +
                    "not available");
        }

        int count, amendmentCount = 0;

        try {
            Map<String, ConsentHistoryResource> results =
                    this.consentCoreService.getConsentAmendmentHistoryData(consentID);

            DetailedConsentResource currentConsentResource = this.consentCoreService.getDetailedConsent(consentID);

            if (isActionByPrimaryUser(currentConsentResource, userID)) {

                JSONArray consentHistory = new JSONArray();
                for (Map.Entry<String, ConsentHistoryResource> result : results.entrySet()) {
                    JSONObject consentResourceJSON = new JSONObject();
                    ConsentHistoryResource consentHistoryResource = result.getValue();
                    DetailedConsentResource detailedConsentResource =
                            consentHistoryResource.getDetailedConsentResource();
                    consentResourceJSON.appendField("historyId", result.getKey());
                    consentResourceJSON.appendField("amendedReason", consentHistoryResource.getReason());
                    consentResourceJSON.appendField("amendedTime", detailedConsentResource.getUpdatedTime());
                    consentResourceJSON.appendField("previousConsentData",
                            this.detailedConsentToJSON(detailedConsentResource));
                    consentHistory.add(consentResourceJSON);
                }
                response.appendField("cdrArrangementId", currentConsentResource.getConsentID());
                response.appendField("currentConsent", this.detailedConsentToJSON(currentConsentResource));
                response.appendField("consentAmendmentHistory", consentHistory);
                count = consentHistory.size();
                amendmentCount = count;

                String currentConsentStatus = currentConsentResource.getCurrentStatus();
                if (CONSENT_STATUS_REVOKED.equalsIgnoreCase(currentConsentStatus)
                        || OpenBankingConfigParser.getInstance().getStatusWordingForExpiredConsents()
                        .equalsIgnoreCase(currentConsentStatus)) {
                    // remove the consent history entry due consent expiration or consent revocation as it is not
                    // lying under the consent amendments nomenclature in CDS
                    amendmentCount = count - 1;
                }
                JSONObject metadata = new JSONObject();
                metadata.appendField("totalCount", count);
                metadata.appendField("totalAmendmentCount", amendmentCount);
                response.appendField("metadata", metadata);
                consentAdminData.setResponseStatus(ResponseStatus.OK);
                consentAdminData.setResponsePayload(response);
            } else {
                consentAdminData.setResponseStatus(ResponseStatus.FORBIDDEN);
            }
        } catch (ConsentManagementException e) {
            log.error(String.format("Error occurred while retrieving consent history data. %s", e.getMessage()));
            throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR, "Error occurred while retrieving " +
                    "consent amendment history data");
        }


    }

    private String validateAndGetQueryParam(Map queryParams, String key) {
        if (queryParams.containsKey(key) && (((ArrayList) queryParams.get(key)).get(0) instanceof String)) {
            return (String) ((ArrayList) queryParams.get(key)).get(0);
        }
        return null;
    }

    /**
     * Method to check requested user's authorization resource type is primary.
     *
     * @param detailedConsentResource detailedConsentResource
     * @param userID                  user id
     * @return true if user's authorization resource type is primary
     */
    private boolean isPrimaryUserRevoking(DetailedConsentResource detailedConsentResource, String userID) {
        for (AuthorizationResource authorizationResource : detailedConsentResource.getAuthorizationResources()) {
            if (userID.equals(authorizationResource.getUserID())) {
                return AUTH_RESOURCE_TYPE_PRIMARY.equals(authorizationResource.getAuthorizationType());
            }
        }
        return true;
    }

    /**
     * Method to deactivate account mapping for secondary consent holders.
     *
     * @param detailedConsentResource detailedConsentResource
     * @param secondaryUserID         id of the secondary consent holder
     * @throws ConsentManagementException when deactivate account mappings fail
     */
    private void deactivateAccountMappings(DetailedConsentResource detailedConsentResource, String secondaryUserID)
            throws ConsentManagementException {
        ArrayList<String> mappingIds = new ArrayList<>();
        ArrayList<AuthorizationResource> authorizationResources = detailedConsentResource.getAuthorizationResources();
        ArrayList<ConsentMappingResource> consentMappings = detailedConsentResource.getConsentMappingResources();

        authorizationResources.stream()
                .filter(consentAuthResource -> secondaryUserID.equals(consentAuthResource.getUserID()))
                .findFirst()
                .ifPresent(auth ->
                        // an Auth Resource found for secondary consent holder
                        consentMappings.stream()
                                .filter(mapping -> mapping.getAuthorizationID().equals(auth.getAuthorizationID()))
                                .map(ConsentMappingResource::getAccountID)
                                .forEach(secondaryAccountID ->
                                        // add every mapping resource that has secondary user's accountID to mappingIds
                                        consentMappings.stream()
                                                .filter(resource -> secondaryAccountID.equals(resource.getAccountID()))
                                                .map(ConsentMappingResource::getMappingID)
                                                .forEach(mappingIds::add)
                                )
                );

        if (!mappingIds.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Deactivate consent mapping accounts for consentID %s and userID %s",
                        detailedConsentResource.getConsentID(), secondaryUserID));
            }

            this.consentCoreService.deactivateAccountMappings(mappingIds);
        }
        //store joint account withdrawal from the consent to consent amendment history
        this.storeJointAccountWithdrawalHistory(detailedConsentResource);
    }

    private void revokeConsentAsPrimaryUser(DetailedConsentResource detailedConsentResource, String userId)
            throws ConsentManagementException {

        String consentID = detailedConsentResource.getConsentID();
        this.consentCoreService.revokeConsentWithReason(consentID, CONSENT_STATUS_REVOKED, null,
                ConsentCoreServiceConstants.CONSENT_REVOKE_FROM_DASHBOARD_REASON);
        // revoke access tokens
        try {
            consentCoreService.revokeTokens(detailedConsentResource, userId);
        } catch (IdentityOAuth2Exception e) {
            log.error(String.format("Error occurred while revoking tokens. Only the consent was revoked " +
                    "successfully. %s", e.getMessage()));
        }
    }

    private JSONObject detailedConsentToJSON(DetailedConsentResource detailedConsentResource) {
        JSONObject consentResource = new JSONObject();

        consentResource.appendField("clientId", detailedConsentResource.getClientID());
        try {
            JSONObject receipt = (JSONObject) (new JSONParser(JSONParser.MODE_PERMISSIVE))
                    .parse(detailedConsentResource.getReceipt());
            JSONArray permissions = (JSONArray) ((JSONObject) receipt.get("accountData")).get("permissions");
            JSONArray cdsPermissions = new JSONArray();
            for (Object scope : permissions) {
                cdsPermissions.add(PermissionsEnum.fromName(scope.toString()));
            }
            consentResource.appendField("permissions", cdsPermissions);
        } catch (ParseException e) {
            log.error(String.format("Error occurred while parsing receipt in consent history. %s", e.getMessage()));
            throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR, "Exception occurred while parsing" +
                    " receipt");
        }

        consentResource.appendField("consentType", detailedConsentResource.getConsentType());
        consentResource.appendField("currentStatus", detailedConsentResource.getCurrentStatus());
        consentResource.appendField("validityPeriod", detailedConsentResource.getValidityPeriod());
        consentResource.appendField("createdTimestamp", detailedConsentResource.getCreatedTime());
        consentResource.appendField("updatedTimestamp", detailedConsentResource.getUpdatedTime());

        Map<String, String> attMap = detailedConsentResource.getConsentAttributes();
        String sharingDuration = attMap.get("sharing_duration_value");
        String expirationDataTime = attMap.get("ExpirationDateTime");

        consentResource.appendField("sharingDuration", sharingDuration);
        consentResource.appendField("expirationDateTime", expirationDataTime);

        ArrayList<AuthorizationResource> authArray = detailedConsentResource.getAuthorizationResources();
        ArrayList<ConsentMappingResource> mappingArray = detailedConsentResource.getConsentMappingResources();

        Map<String, AuthorizationResource> authResourceMap = new HashMap<>();
        Map<String, String> userAuthTypeMap = new HashMap<>();
        for (AuthorizationResource resource : authArray) {
            authResourceMap.put(resource.getAuthorizationID(), resource);
            userAuthTypeMap.put(resource.getUserID(), resource.getAuthorizationType());
        }
        Map<String, JSONArray> userAccountsDataMap = new HashMap<>();
        for (ConsentMappingResource resource : mappingArray) {
            if (resource.getMappingStatus().equalsIgnoreCase("active")) {
                String userId = authResourceMap.get(resource.getAuthorizationID()).getUserID();
                JSONArray accountsArray;
                if (userAccountsDataMap.containsKey(userId)) {
                    accountsArray = userAccountsDataMap.get(userId);
                } else {
                    accountsArray = new JSONArray();
                }
                accountsArray.add(resource.getAccountID());
                userAccountsDataMap.put(userId, accountsArray);
            }
        }

        JSONArray userList = new JSONArray();
        for (Map.Entry<String, JSONArray> userAccountsData : userAccountsDataMap.entrySet()) {
            JSONObject user = new JSONObject();
            String userId = userAccountsData.getKey();
            user.appendField("userId", userId);
            user.appendField("authType", userAuthTypeMap.get(userId));
            user.appendField("accountList", userAccountsData.getValue());
            userList.add(user);
        }
        consentResource.appendField("userList", userList);
        return consentResource;
    }

    /**
     * Method to filter secondary user created consents for searching user including privileged secondary accounts.
     * QueryParams should contain 'secondaryAccountInfo' as a key and userid as the value.
     *
     * @param consentAdminData consentAdminData
     */
    private void filterSecondaryUserConsents(ConsentAdminData consentAdminData) {

        JSONArray filteredConsentData = new JSONArray();

        for (Object consentObj : (JSONArray) consentAdminData.getResponsePayload().get(
                CDSConsentExtensionConstants.DATA)) {
            JSONObject consent = (JSONObject) consentObj;
            JSONArray secondaryAccountOwnerAuthResources = new JSONArray();
            JSONArray consentAuthResources = (JSONArray) consent.get(
                    CDSConsentExtensionConstants.AUTHORIZATION_RESOURCES);
            JSONArray consentMappingsResources = (JSONArray) consent.get(
                    CDSConsentExtensionConstants.CONSENT_MAPPING_RESOURCES);

            // filter secondary account owner auth resources
            String primaryUserId = null;
            for (Object consentAuthResourceObj : consentAuthResources) {
                if (consentAuthResourceObj instanceof JSONObject) {
                    JSONObject authResource = (JSONObject) consentAuthResourceObj;
                    if (SecondaryAccountOwnerTypeEnum.isValidOwnerType(
                            authResource.getAsString(CDSConsentExtensionConstants.AUTH_TYPE))) {
                        secondaryAccountOwnerAuthResources.add(authResource);
                    } else if (authResource.get(CDSConsentExtensionConstants.AUTH_TYPE)
                            .equals(CDSConsentExtensionConstants.AUTH_RESOURCE_TYPE_PRIMARY)) {
                        primaryUserId = (String) authResource.get(CDSConsentExtensionConstants.USER_ID);
                    }
                }
            }

            // case where consent has secondary accounts of the searching user
            if (!secondaryAccountOwnerAuthResources.isEmpty()) {

                // retrieve secondary account mappings for filtered auth resources
                Map<String, Map<String, String>> accountOwnerAgainstAccountWithPrivilegeMap = new HashMap<>();
                JSONArray secondaryAccountInfo = new JSONArray();

                for (Object authResourceObj: secondaryAccountOwnerAuthResources) {
                    if (authResourceObj instanceof JSONObject) {
                        JSONObject authResource = (JSONObject) authResourceObj;
                        String authResourceId = (String) ((JSONObject) authResource)
                                .get(CDSConsentExtensionConstants.AUTHORIZATION_ID);
                        for (Object mappingResourceObj: consentMappingsResources) {
                            if (mappingResourceObj instanceof JSONObject) {
                                JSONObject mappingResource = (JSONObject) mappingResourceObj;
                                if (mappingResource.get(CDSConsentExtensionConstants.AUTHORIZATION_ID)
                                        .equals(authResourceId)) {
                                    Map<String, String> accountWithPrivilegeMap = new HashMap<>();
                                    String accountId = (String) mappingResource.get(
                                            CDSConsentExtensionConstants.ACCOUNT_ID);
                                    Boolean isPrivileged = mappingResource
                                            .get(CDSConsentExtensionConstants.MAPPING_STATUS)
                                            .equals(CDSConsentExtensionConstants.ACTIVE_STATUS) &&
                                            CDSConsentValidatorUtil.isUserEligibleForSecondaryAccountDataSharing(
                                                    accountId, primaryUserId);

                                    accountWithPrivilegeMap.put((String) mappingResource.get(
                                            CDSConsentExtensionConstants.ACCOUNT_ID), isPrivileged.toString());
                                    accountOwnerAgainstAccountWithPrivilegeMap.put(authResource.getAsString(
                                            CDSConsentExtensionConstants.USER_ID), accountWithPrivilegeMap);
                                }
                            }
                        }
                    }
                }
                // append processed secondary account info to consent object
                consent.appendField(CDSConsentExtensionConstants.SECONDARY_ACCOUNT_INFO,
                        getSecondaryAccountInfoArray(primaryUserId, accountOwnerAgainstAccountWithPrivilegeMap));
                filteredConsentData.add(consent);
            }
        }
        JSONObject responseMetadata = (JSONObject) consentAdminData.getResponsePayload().get(
                CDSConsentExtensionConstants.METADATA);
        responseMetadata.put(CDSConsentExtensionConstants.TOTAL, filteredConsentData.size());
        responseMetadata.put(CDSConsentExtensionConstants.COUNT, filteredConsentData.size());
        consentAdminData.getResponsePayload().put(CDSConsentExtensionConstants.DATA, filteredConsentData);
    }

    private JSONObject getSecondaryAccountInfoArray(String primaryUserId,
                                  Map<String, Map<String, String>> accountOwnerAgainstAccountWithPrivilegeMap) {
        JSONObject secondaryAccountInfo = new JSONObject();
        JSONArray secondaryAccountList = new JSONArray();
        secondaryAccountInfo.put(CDSConsentExtensionConstants.ACCOUNT_USER, primaryUserId);

        for (Map.Entry<String, Map<String, String>> accountOwnerAgainstAccountWithPrivilege :
                accountOwnerAgainstAccountWithPrivilegeMap.entrySet()) {
            String accountOwnerUserId = accountOwnerAgainstAccountWithPrivilege.getKey();
            Map<String, String> accountWithPrivilegeMap = accountOwnerAgainstAccountWithPrivilege.getValue();
            JSONArray activeAccountList = new JSONArray();
            JSONArray inactiveAccountList = new JSONArray();

            if (accountOwnerUserId.equals(primaryUserId)) {
                continue;
            }

            for (Map.Entry<String, String> accountWithPrivilege : accountWithPrivilegeMap.entrySet()) {
                String accountId = accountWithPrivilege.getKey();

                if (accountWithPrivilege.getValue().equals("true")) {
                    activeAccountList.add(accountId);
                } else {
                    inactiveAccountList.add(accountId);
                }
            }

            JSONObject secondaryAccountObject = new JSONObject();
            secondaryAccountObject.put(CDSConsentExtensionConstants.ACCOUNT_OWNER, accountOwnerUserId);
            secondaryAccountObject.put(CDSConsentExtensionConstants.ACTIVE_ACCOUNTS, activeAccountList);
            secondaryAccountObject.put(CDSConsentExtensionConstants.INACTIVE_ACCOUNTS, inactiveAccountList);
            secondaryAccountList.add(secondaryAccountObject);
        }
        secondaryAccountInfo.put(CDSConsentExtensionConstants.SECONDARY_ACCOUNTS, secondaryAccountList);
        return secondaryAccountInfo;
    }

    private void storeJointAccountWithdrawalHistory(DetailedConsentResource detailedConsentResource)
            throws ConsentManagementException {

        if (OpenBankingConfigParser.getInstance().isConsentAmendmentHistoryEnabled()) {
            ConsentHistoryResource consentHistoryResource = new ConsentHistoryResource();
            consentHistoryResource.setTimestamp(System.currentTimeMillis() / 1000);
            consentHistoryResource.setReason(AMENDMENT_REASON_ACCOUNT_WITHDRAWAL);
            consentHistoryResource.setDetailedConsentResource(detailedConsentResource);

            this.consentCoreService.storeConsentAmendmentHistory(detailedConsentResource.getConsentID(),
                    consentHistoryResource, null);
        }
    }

    private boolean isActionByPrimaryUser(DetailedConsentResource detailedConsentResource, String userID) {
        for (AuthorizationResource authorizationResource : detailedConsentResource.getAuthorizationResources()) {
            if (userID.equals(authorizationResource.getUserID())) {
                return AUTH_RESOURCE_TYPE_PRIMARY.equals(authorizationResource.getAuthorizationType());
            }
        }
        return false;
    }

    @Override
    public void handleConsentFileSearch(ConsentAdminData consentAdminData) throws ConsentException {
        this.defaultConsentAdminHandler.handleConsentFileSearch(consentAdminData);
    }

    @Override
    public void handleConsentStatusAuditSearch(ConsentAdminData consentAdminData) throws ConsentException {
        this.defaultConsentAdminHandler.handleConsentStatusAuditSearch(consentAdminData);
    }

    @Override
    public void handleTemporaryRetentionDataSyncing(ConsentAdminData consentAdminData) throws ConsentException {
        this.defaultConsentAdminHandler.handleTemporaryRetentionDataSyncing(consentAdminData);
    }

    @Override
    public void handleConsentExpiry(ConsentAdminData consentAdminData) throws ConsentException {
        this.defaultConsentAdminHandler.handleConsentExpiry(consentAdminData);
    }

}
