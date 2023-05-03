/*
 * Copyright (c) 2021-2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataService;
import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataServiceImpl;
import com.wso2.openbanking.accelerator.common.config.OpenBankingConfigParser;
import com.wso2.openbanking.accelerator.common.exception.ConsentManagementException;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
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

import static com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants.
        AUTH_RESOURCE_TYPE_PRIMARY;
import static com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants.CONSENT_STATUS_REVOKED;
import static com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants.DOMS_STATUS;
import static com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants.DOMS_STATUS_PRE_APPROVAL;
import static com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants.
        JOINT_ACCOUNT_PAYLOAD_ACCOUNT_ID;

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

        try {
            AccountMetadataService accountMetadataService = AccountMetadataServiceImpl.getInstance();

            for (Object item : (JSONArray) consentAdminData.getResponsePayload().get("data")) {

                JSONObject itemJSONObject = (JSONObject) item;
                JSONArray consentMappingResourcesArray = (JSONArray) itemJSONObject.get("consentMappingResources");

                for (Object consentMappingResource : consentMappingResourcesArray) {

                    JSONObject cmrJSONObject = (JSONObject) consentMappingResource;
                    String accountId = cmrJSONObject.getAsString(JOINT_ACCOUNT_PAYLOAD_ACCOUNT_ID);

                    Map<String, String> disclosureOptionsMap = accountMetadataService.
                            getGlobalAccountMetadataMap(accountId);

                    String disclosureOptionStatus = disclosureOptionsMap.get(DOMS_STATUS);

                    if (disclosureOptionStatus == null) {
                        disclosureOptionStatus = DOMS_STATUS_PRE_APPROVAL;
                    }
                    cmrJSONObject.put("domsStatus", disclosureOptionStatus);
                }
            }
        } catch (OpenBankingException e) {
            log.warn(e.getMessage());
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
