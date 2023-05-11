/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.consent.extensions.authorize.utils;

import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentPersistData;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains the utility methods used for CDS Consent Persistence steps.
 */
public class CDSConsentPersistUtil {

    private static final Log log = LogFactory.getLog(CDSConsentPersistUtil.class);


    /**
     * Add given non primary account data to the consentPersistData.
     *
     * @param nonPrimaryAccountIdUsersMap Map of non-primary accountId against userIds
     * @param consentPersistData          ConsentPersistData object
     */
    public static void addNonPrimaryAccountDataToPersistData(
            Map<String, Map<String, String>> nonPrimaryAccountIdUsersMap,
            Map<String, ArrayList<String>> nonPrimaryAccountIDWithPermissionsMap,
            ConsentPersistData consentPersistData) {

        Map<String, Map<String, String>> currentNonPrimaryAccountIdUsersMap = new HashMap<>();
        Map<String, List<String>> currentUserIdNonPrimaryAccountsMap = new HashMap<>();

        //Get existing non-primary account data from consentPersistData
        if (consentPersistData.getMetadata().get(CDSConsentExtensionConstants.
                NON_PRIMARY_ACCOUNT_ID_AGAINST_USERS_MAP) != null) {
            currentNonPrimaryAccountIdUsersMap = (Map<String, Map<String, String>>) consentPersistData.getMetadata().
                    get(CDSConsentExtensionConstants.NON_PRIMARY_ACCOUNT_ID_AGAINST_USERS_MAP);
        } else {
            log.debug("Non-primary accountId against users map not available in consentPersistData. " +
                    "Creating new map");
        }
        if (consentPersistData.getMetadata().get(CDSConsentExtensionConstants.
                USER_ID_AGAINST_NON_PRIMARY_ACCOUNTS_MAP) != null) {
            currentUserIdNonPrimaryAccountsMap = (Map<String, List<String>>) consentPersistData.getMetadata().
                    get(CDSConsentExtensionConstants.USER_ID_AGAINST_NON_PRIMARY_ACCOUNTS_MAP);
        } else {
            log.debug("UserIds against non-primary accountId map not available in consentPersistData. " +
                    "Creating new map");
        }

        // update non-primary account permissions if defined
        if (nonPrimaryAccountIDWithPermissionsMap != null && !nonPrimaryAccountIDWithPermissionsMap.isEmpty()) {
            consentPersistData.addMetadata(CDSConsentExtensionConstants.NON_PRIMARY_ACCOUNT_ID_WITH_PERMISSIONS_MAP,
                    nonPrimaryAccountIDWithPermissionsMap);
        }

        //Add new non-primary account data to consent persist data
        Map<String, List<String>> userIdNonPrimaryAccountsMap = getUserIdAgainstAccountsMap(
                nonPrimaryAccountIdUsersMap);
        currentNonPrimaryAccountIdUsersMap.putAll(nonPrimaryAccountIdUsersMap);
        currentUserIdNonPrimaryAccountsMap.putAll(userIdNonPrimaryAccountsMap);
        consentPersistData.addMetadata(CDSConsentExtensionConstants.NON_PRIMARY_ACCOUNT_ID_AGAINST_USERS_MAP,
                currentNonPrimaryAccountIdUsersMap);
        consentPersistData.addMetadata(CDSConsentExtensionConstants.USER_ID_AGAINST_NON_PRIMARY_ACCOUNTS_MAP,
                currentUserIdNonPrimaryAccountsMap);
    }

    /**
     * Get a map of userId against a list of accountIds.
     *
     * @param accountIdUserMap Map of accountId against userIds
     * @return a map of userId against a list of accountIds
     */
    private static Map<String, List<String>> getUserIdAgainstAccountsMap(Map<String,
            Map<String, String>> accountIdUserMap) {

        Map<String, List<String>> userIdAccountsMap = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : accountIdUserMap.entrySet()) {
            final String accountId = entry.getKey();
            final List<String> userIdList = new ArrayList<>(entry.getValue().keySet());
            for (String userId : userIdList) {
                if (userIdAccountsMap.containsKey(userId)) {
                    userIdAccountsMap.get(userId).add(accountId);
                } else {
                    userIdAccountsMap.put(userId, new ArrayList<>(Collections.
                            singletonList(accountId)));
                }
            }
        }
        return userIdAccountsMap;
    }

    /**
     * Get account list from consent persist payload.
     *
     * @param payloadData Payload data retrieved from persist data
     * @return List of user consented accounts
     */
    public static ArrayList<String> getConsentedAccountIdList(JSONObject payloadData) throws ConsentException {

        ArrayList<String> accountIdsList = new ArrayList<>();
        if (payloadData.get(CDSConsentExtensionConstants.ACCOUNT_IDS) instanceof JSONArray) {
            JSONArray accountIds = (JSONArray) payloadData.get(CDSConsentExtensionConstants.ACCOUNT_IDS);
            for (Object account : accountIds) {
                accountIdsList.add((String) account);
            }
        }
        if (accountIdsList.isEmpty()) {
            log.debug("Failed to get consented account list from payload data. This can be caused by a " +
                    "formatting error in sharable accounts response Returning an empty list");
        }
        return accountIdsList;
    }

    /**
     * Get an array of requested account objects from consent data.
     *
     * @param consentData          Consent data from consentPersistData
     * @param requestedAccountList List of requested accounts
     * @return JSONArray of account objects
     */
    public static JSONArray getRequestedAccounts(ConsentData consentData, List<String> requestedAccountList) {

        Object accountsObj = consentData.getMetaDataMap().get(CDSConsentExtensionConstants.ACCOUNTS);
        JSONArray requestedAccountArray = new JSONArray();
        if (accountsObj instanceof JSONArray) {
            JSONArray accountsArray = (JSONArray) accountsObj;
            for (Object accountObj : accountsArray) {
                if (accountObj instanceof JSONObject) {
                    JSONObject accountJsonObj = (JSONObject) accountObj;
                    String accountId = accountJsonObj.getAsString(CDSConsentExtensionConstants.ACCOUNT_ID);
                    if (requestedAccountList.contains(accountId)) {
                        requestedAccountArray.add(accountJsonObj);
                    }
                }
            }
        }
        if (requestedAccountArray.isEmpty()) {
            log.debug("Failed to get requested accounts from consent data. This can be caused by a formatting " +
                    "error in sharable accounts response Returning an empty array");
        }
        return requestedAccountArray;
    }

    /**
     * Add consent attribute to consentPersistData.
     * These data will be added to the Consent_Attributes table.
     *
     * @param attributeKey - Attribute key
     * @param attributeValue - Attribute value
     * @param consentPersistData - ConsentPersistData object
     */
    public static void addConsentAttribute(String attributeKey, String attributeValue,
                                           ConsentPersistData consentPersistData) {
        if (log.isDebugEnabled()) {
            log.debug("Adding consent attribute " + attributeKey + " with value " + attributeValue);
        }

        Map<String, String> consentAttributeMap;
        if (consentPersistData.getMetadata().containsKey(CDSConsentExtensionConstants.CONSENT_ATTRIBUTES)) {
            consentAttributeMap = (Map<String, String>) consentPersistData.getMetadata().get(
                    CDSConsentExtensionConstants.CONSENT_ATTRIBUTES);
        } else {
            consentAttributeMap = new HashMap<>();
        }
        consentAttributeMap.put(attributeKey, attributeValue);
        consentPersistData.addMetadata(CDSConsentExtensionConstants.CONSENT_ATTRIBUTES, consentAttributeMap);
    }

}
