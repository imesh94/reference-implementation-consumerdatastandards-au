/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.consent.extensions.authorize.impl.persist;

import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataServiceImpl;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentPersistData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentPersistStep;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.CDSConsentPersistUtil;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for persisting the business account consent data in the database.
 */
public class CDSBusinessAccountConsentPersistenceStep implements ConsentPersistStep {

    private static final Log log = LogFactory.getLog(CDSBusinessAccountConsentPersistenceStep.class);
    AccountMetadataServiceImpl accountMetadataService = AccountMetadataServiceImpl.getInstance();


    @Override
    public void execute(ConsentPersistData consentPersistData) throws ConsentException {

        if (consentPersistData.getApproval()) {

            JSONObject payloadJsonObject = consentPersistData.getPayload();
            Map<String, Map<String, String>> businessAccountIdUserMap;
            ConsentData consentData = consentPersistData.getConsentData();

            //Get an array of consented account details
            ArrayList<String> consentedAccountIdList = CDSConsentPersistUtil.
                    getConsentedAccountIdList(payloadJsonObject);
            JSONArray consentedAccountsJsonArray = CDSConsentPersistUtil.getRequestedAccounts(consentData,
                    consentedAccountIdList);

            //Get a map of business account id against users with their auth type
            businessAccountIdUserMap = getBusinessAccountIdUsersMap(consentedAccountsJsonArray);

            //Abort the flow if any of the users have revoke permission //Todo: make this configurable
            if (!validateNominatedRepresentativePermissions(businessAccountIdUserMap)) {
                log.error("Users that don't have permissions to be nominated representatives are present in " +
                        "the consent request");
                throw new ConsentException(ResponseStatus.BAD_REQUEST,
                        "Users that don't have permissions to be nominated representatives are present in " +
                                "the consent request");
            }

            try {
                //Add business nominated representative data to the account metadata table
                addNominatedRepresentativeDataToAccountMetadataTable(businessAccountIdUserMap);
                //Add business account data to consentPersistData
                CDSConsentPersistUtil.addNonPrimaryAccountDataToPersistData(businessAccountIdUserMap,
                        consentPersistData);
            } catch (OpenBankingException e) {
                log.error("Error while adding nominated representative data to account metadata table. " +
                        "Aborting consent persistence", e);
                throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                        "Error while adding nominated representative data to account metadata table");
            }
        }
    }

    /**
     * Get a map of valid business account id against users with their auth type.
     *
     * @param consentedAccountsJsonArray Consented accounts json array
     * @return businessAccountIdUserMap
     */
    private Map<String, Map<String, String>> getBusinessAccountIdUsersMap(JSONArray consentedAccountsJsonArray) {

        Map<String, Map<String, String>> businessAccountIdUserMap = new HashMap<>();
        for (Object accountDetails : consentedAccountsJsonArray) {
            if (accountDetails instanceof JSONObject) {
                JSONObject accountJsonObject = (JSONObject) accountDetails;
                if (isValidBusinessAccount(accountJsonObject)) {
                    String consentedAccountId = accountJsonObject.getAsString(
                            CDSConsentExtensionConstants.ACCOUNT_ID);
                    businessAccountIdUserMap.put(consentedAccountId, getUsersFromAccount(accountJsonObject));
                }
            }
        }
        return businessAccountIdUserMap;
    }

    /**
     * Get user ids against auth type map from business account.
     *
     * @param businessAccount Business account json object
     * @return map of user ids against auth type
     */
    private Map<String, String> getUsersFromAccount(JSONObject businessAccount) {
        Map<String, String> userIdList = new HashMap<>();
        Object businessAccountInfo = businessAccount.get(CDSConsentExtensionConstants.BUSINESS_ACCOUNT_INFO);
        if (businessAccountInfo instanceof JSONObject) {
            Object accountOwners = ((JSONObject) businessAccountInfo).get(CDSConsentExtensionConstants.ACCOUNT_OWNERS);
            if (accountOwners instanceof JSONArray) {
                for (Object linkedMember : ((JSONArray) accountOwners)) {
                    if (linkedMember instanceof JSONObject) {
                        userIdList.put(((JSONObject) linkedMember)
                                        .getAsString(CDSConsentExtensionConstants.MEMBER_ID),
                                CDSConsentExtensionConstants.BUSINESS_ACCOUNT_OWNER);
                    }
                }
            }
            Object nominatedRepresentatives = ((JSONObject) businessAccountInfo).get(CDSConsentExtensionConstants
                    .NOMINATED_REPRESENTATIVES);
            if (nominatedRepresentatives instanceof JSONArray) {
                for (Object nominatedRep : ((JSONArray) nominatedRepresentatives)) {
                    if (nominatedRep instanceof JSONObject) {
                        userIdList.put(((JSONObject) nominatedRep)
                                        .getAsString(CDSConsentExtensionConstants.MEMBER_ID),
                                CDSConsentExtensionConstants.NOMINATED_REPRESENTATIVE);
                    }
                }
            }
        }
        return userIdList;
    }

    /**
     * Check whether the account is a business account.
     *
     * @param accountObject Account received from bank backend
     * @return true if account is a business account
     */
    private boolean isValidBusinessAccount(JSONObject accountObject) {

        String accountType = accountObject.getAsString(CDSConsentExtensionConstants.CUSTOMER_ACCOUNT_TYPE);
        return (CDSConsentExtensionConstants.BUSINESS.equalsIgnoreCase(accountType));
    }

    /**
     * Check if any of the users have 'REVOKE' permission and return false.
     *
     * @param businessAccountIdUserMap BusinessAccountIdUserMap
     * @return boolean
     * @throws ConsentException ConsentException
     */
    private boolean validateNominatedRepresentativePermissions(
            Map<String, Map<String, String>> businessAccountIdUserMap) throws ConsentException {

        try {
            for (Map.Entry<String, Map<String, String>> entry : businessAccountIdUserMap.entrySet()) {
                String accountId = entry.getKey();
                Map<String, String> users = entry.getValue();
                for (Map.Entry<String, String> user : users.entrySet()) {
                    String userId = user.getKey();
                    String bnrStatus = accountMetadataService.getAccountMetadataByKey(accountId, userId,
                            CDSConsentExtensionConstants.BNR_PERMISSION);
                    if (CDSConsentExtensionConstants.BNR_REVOKE_PERMISSION.equals(bnrStatus)) {
                        log.error("Business nominated user " + userId + " has REVOKE permission for account " +
                                accountId);
                        return false;
                    }
                }
            }
        } catch (OpenBankingException e) {
            log.error("Error while checking revoke permission for business accounts", e);
            throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                    "Error while checking revoke permission for business accounts");
        }
        return true;
    }

    /**
     * Add business nominated representative data to the account metadata table.
     *
     * @param businessAccountIdUserMap BusinessAccountIdUserMap
     * @throws OpenBankingException OpenBankingException
     */
    private void addNominatedRepresentativeDataToAccountMetadataTable(Map<String, Map<String, String>>
        businessAccountIdUserMap) throws OpenBankingException {

        for (Map.Entry<String, Map<String, String>> entry : businessAccountIdUserMap.entrySet()) {
            String accountId = entry.getKey();
            Map<String, String> users = entry.getValue();
            for (Map.Entry<String, String> user : users.entrySet()) {
                String userId = user.getKey();
                String authType = user.getValue();
                String bnrPermission = CDSConsentExtensionConstants.NOMINATED_REPRESENTATIVE.equals(authType) ?
                        CDSConsentExtensionConstants.BNR_AUTHORIZE_PERMISSION :
                        CDSConsentExtensionConstants.BNR_VIEW_PERMISSION;
                accountMetadataService.addOrUpdateAccountMetadata(accountId, userId,
                        Collections.singletonMap(CDSConsentExtensionConstants.BNR_PERMISSION, bnrPermission));
            }
        }
    }

}
