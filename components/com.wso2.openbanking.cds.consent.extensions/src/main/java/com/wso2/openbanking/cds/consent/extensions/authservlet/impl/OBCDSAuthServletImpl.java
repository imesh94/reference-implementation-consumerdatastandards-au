/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.consent.extensions.authservlet.impl;

import com.wso2.openbanking.accelerator.consent.extensions.authservlet.model.OBAuthServletInterface;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;

/**
 * The CDS implementation of servlet extension that handles CDS scenarios
 */
public class OBCDSAuthServletImpl implements OBAuthServletInterface {

    String preSelectedProfileId;
    @Override
    public Map<String, Object> updateRequestAttribute(HttpServletRequest httpServletRequest, JSONObject dataSet,
                                                      ResourceBundle resourceBundle) {

        Map<String, Object> returnMaps = new HashMap<>();
        preSelectedProfileId = "";

        // Set "data_requested" that contains the human-readable scope-requested information
        JSONArray dataRequestedJsonArray = dataSet.getJSONArray(CDSConsentExtensionConstants.DATA_REQUESTED);
        JSONArray businessDataClusterArray = dataSet.getJSONArray(CDSConsentExtensionConstants.BUSINESS_DATA_CLUSTER);
        Map<String, List<String>> dataRequested = getRequestedDataMap(dataRequestedJsonArray);
        Map<String, List<String>> businessDataCluster = getRequestedDataMap(businessDataClusterArray);
        returnMaps.put(CDSConsentExtensionConstants.DATA_REQUESTED, dataRequested);
        returnMaps.put(CDSConsentExtensionConstants.BUSINESS_DATA_CLUSTER, businessDataCluster);

        // Add accounts list
        JSONArray accountsArray = dataSet.getJSONArray(CDSConsentExtensionConstants.ACCOUNTS);
        List<Map<String, Object>> accountsData = getAccountsDataMap(accountsArray);
        httpServletRequest.setAttribute(CDSConsentExtensionConstants.ACCOUNTS_DATA, accountsData);

        // Add customer profiles list
        if (dataSet.has(CDSConsentExtensionConstants.CUSTOMER_PROFILES_ATTRIBUTE)) {
            JSONArray profilesArray = dataSet.getJSONArray(CDSConsentExtensionConstants.CUSTOMER_PROFILES_ATTRIBUTE);
            List<Map<String, Object>> customerProfilesData = getCustomerProfileDataList(profilesArray);
            httpServletRequest.setAttribute(CDSConsentExtensionConstants.PROFILES_DATA_ATTRIBUTE, customerProfilesData);
        }

        // Add selected profile if available (This can be available either in the consent amendment flow or
        // when only a single profile is present for the user.)
        if (dataSet.has(CDSConsentExtensionConstants.PRE_SELECTED_PROFILE_ID)) {
            httpServletRequest.setAttribute(CDSConsentExtensionConstants.PRE_SELECTED_PROFILE_ID,
                    dataSet.get(CDSConsentExtensionConstants.PRE_SELECTED_PROFILE_ID));
            preSelectedProfileId = (String) dataSet.get(CDSConsentExtensionConstants.PRE_SELECTED_PROFILE_ID);
        }

        //Consent amendment flow
        if (dataSet.has(CDSConsentExtensionConstants.IS_CONSENT_AMENDMENT) &&
                (boolean) dataSet.get(CDSConsentExtensionConstants.IS_CONSENT_AMENDMENT)) {
            // Add new data requested
            JSONArray newDataRequestedJsonArray = dataSet.getJSONArray(CDSConsentExtensionConstants.NEW_DATA_REQUESTED);
            Map<String, List<String>> newDataRequested = getRequestedDataMap(newDataRequestedJsonArray);
            returnMaps.put(CDSConsentExtensionConstants.NEW_DATA_REQUESTED, newDataRequested);
            httpServletRequest.setAttribute(CDSConsentExtensionConstants.IS_CONSENT_AMENDMENT, true);
            if (dataSet.has(CDSConsentExtensionConstants.IS_SHARING_DURATION_UPDATED)) {
                httpServletRequest.setAttribute(CDSConsentExtensionConstants.IS_SHARING_DURATION_UPDATED,
                        dataSet.get(CDSConsentExtensionConstants.IS_SHARING_DURATION_UPDATED));
            }
        } else {
            httpServletRequest.setAttribute(CDSConsentExtensionConstants.IS_CONSENT_AMENDMENT, false);
        }

        // Add additional attributes to be displayed
        httpServletRequest.setAttribute(CDSConsentExtensionConstants.SP_FULL_NAME,
                dataSet.getString(CDSConsentExtensionConstants.SP_FULL_NAME));
        httpServletRequest.setAttribute(CDSConsentExtensionConstants.REDIRECT_URL,
                dataSet.getString(CDSConsentExtensionConstants.REDIRECT_URL));
        // Check for zero sharing duration and display as once off consent
        if (CDSConsentExtensionConstants.ZERO.equals(dataSet.getString(CDSConsentExtensionConstants.CONSENT_EXPIRY))) {
            httpServletRequest.setAttribute(CDSConsentExtensionConstants.CONSENT_EXPIRY,
                    CDSConsentExtensionConstants.SINGLE_ACCESS_CONSENT);
        } else {
            httpServletRequest.setAttribute(CDSConsentExtensionConstants.CONSENT_EXPIRY,
                    dataSet.getString(CDSConsentExtensionConstants.CONSENT_EXPIRY));
        }
        httpServletRequest.setAttribute(CDSConsentExtensionConstants.ACCOUNT_MASKING_ENABLED,
                OpenBankingCDSConfigParser.getInstance().isAccountMaskingEnabled());

        return returnMaps;
    }

    @Override
    public Map<String, Object> updateSessionAttribute(HttpServletRequest httpServletRequest, JSONObject jsonObject,
                                                      ResourceBundle resourceBundle) {
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> updateConsentData(HttpServletRequest httpServletRequest) {

        Map<String, Object> returnMaps = new HashMap<>();

        String[] accounts = httpServletRequest.getParameter(
                CDSConsentExtensionConstants.ACCOUNTS_ARRAY).split(":");
        returnMaps.put(CDSConsentExtensionConstants.ACCOUNT_IDS, new JSONArray(accounts));
        returnMaps.put(CDSConsentExtensionConstants.SELECTED_PROFILE_ID, httpServletRequest.getParameter(
                CDSConsentExtensionConstants.SELECTED_PROFILE_ID));
        returnMaps.put(CDSConsentExtensionConstants.SELECTED_PROFILE_NAME, httpServletRequest.getParameter(
                CDSConsentExtensionConstants.SELECTED_PROFILE_NAME));

        return returnMaps;
    }

    @Override
    public Map<String, String> updateConsentMetaData(HttpServletRequest httpServletRequest) {
        return new HashMap<>();
    }

    @Override
    public String getJSPPath() {
        // If profile is already selected, skip the profile selection page
        if (StringUtils.isBlank(preSelectedProfileId)) {
            return "/ob_cds_profile_selection.jsp";
        } else {
            return "/ob_cds_account_selection.jsp";
        }
    }

    /**
     * Update Individual Personal Account Details.
     *
     * @param account: account object
     * @param data: data map
     */
    private void updateIndividualPersonalAccountAttributes(JSONObject account, Map<String, Object> data) {
        if ((account != null && account.getBoolean(CDSConsentExtensionConstants.IS_ELIGIBLE)) &&
                (CDSConsentExtensionConstants.INDIVIDUAL_PROFILE_TYPE.equalsIgnoreCase(
                        account.getString(CDSConsentExtensionConstants.CUSTOMER_ACCOUNT_TYPE))
                        && !account.getBoolean(CDSConsentExtensionConstants.IS_JOINT_ACCOUNT_RESPONSE))) {
            data.put(CDSConsentExtensionConstants.IS_SELECTABLE, true);
        }
    }

    private void updateJointAccountAttributes(JSONObject account, Map<String, Object> data) {
        if (account != null && (account.getBoolean(CDSConsentExtensionConstants.IS_JOINT_ACCOUNT_RESPONSE))
                && !account.getBoolean(CDSConsentExtensionConstants.IS_SECONDARY_ACCOUNT_RESPONSE)) {
            data.put(CDSConsentExtensionConstants.IS_JOINT_ACCOUNT, true);

            String consentElectionStatus = String
                    .valueOf(account.get(CDSConsentExtensionConstants.JOINT_ACCOUNT_CONSENT_ELECTION_STATUS));
            data.put(CDSConsentExtensionConstants.IS_SELECTABLE,
                    CDSConsentExtensionConstants.JOINT_ACCOUNT_PRE_APPROVAL.equalsIgnoreCase(consentElectionStatus));

            JSONObject jointAccountInfo = account.getJSONObject(CDSConsentExtensionConstants.JOINT_ACCOUNT_INFO);
            if (jointAccountInfo != null) {
                data.put(CDSConsentExtensionConstants.LINKED_MEMBERS_COUNT,
                        jointAccountInfo.getJSONArray(CDSConsentExtensionConstants.LINKED_MEMBER).length());
            }
        }
    }

    /**
     * Update Secondary Account Details.
     *
     * @param account: account object
     * @param data: data map
     */
    private void updateSecondaryAccountAttributes(JSONObject account, Map<String, Object> data) {
        if (account != null && account.getBoolean(CDSConsentExtensionConstants.IS_SECONDARY_ACCOUNT_RESPONSE)) {
            data.put(CDSConsentExtensionConstants.IS_SECONDARY_ACCOUNT, true);

            // secondaryAccountPrivilegeStatus depicts whether the account is enabled for secondary user data sharing
            Boolean secondaryAccountPrivilegeStatus = Boolean.valueOf(String
                    .valueOf(account.get(CDSConsentExtensionConstants.SECONDARY_ACCOUNT_PRIVILEGE_STATUS)));

            // handle secondary joint accounts
            if (account.getBoolean(CDSConsentExtensionConstants.IS_JOINT_ACCOUNT_RESPONSE)) {
                String consentElectionStatus = String
                        .valueOf(account.get(CDSConsentExtensionConstants.JOINT_ACCOUNT_CONSENT_ELECTION_STATUS));
                Boolean isPreApproved = CDSConsentExtensionConstants.JOINT_ACCOUNT_PRE_APPROVAL
                        .equalsIgnoreCase(consentElectionStatus);
                data.put(CDSConsentExtensionConstants.IS_SELECTABLE, isPreApproved && secondaryAccountPrivilegeStatus);
            } else {
                data.put(CDSConsentExtensionConstants.IS_SELECTABLE, secondaryAccountPrivilegeStatus);
            }
        }
    }

    private Map<String, List<String>> getRequestedDataMap(JSONArray dataRequestedJsonArray) {

        Map<String, List<String>> dataRequested = new LinkedHashMap<>();
        for (int requestedDataIndex = 0; requestedDataIndex < dataRequestedJsonArray.length(); requestedDataIndex++) {
            JSONObject dataObj = dataRequestedJsonArray.getJSONObject(requestedDataIndex);
            String title = dataObj.getString(CDSConsentExtensionConstants.TITLE);
            JSONArray dataArray = dataObj.getJSONArray(CDSConsentExtensionConstants.DATA);

            ArrayList<String> listData = new ArrayList<>();
            for (int dataIndex = 0; dataIndex < dataArray.length(); dataIndex++) {
                listData.add(dataArray.getString(dataIndex));
            }
            dataRequested.put(title, listData);
        }
        return dataRequested;
    }

    private List<Map<String, Object>> getAccountsDataMap(JSONArray accountsArray) {

        List<Map<String, Object>> accountsData = new ArrayList<>();
        for (int accountIndex = 0; accountIndex < accountsArray.length(); accountIndex++) {
            Map<String, Object> data = new HashMap<>();
            JSONObject account = accountsArray.getJSONObject(accountIndex);
            String accountId = account.getString(CDSConsentExtensionConstants.ACCOUNT_ID);
            String displayName = account.getString(CDSConsentExtensionConstants.DISPLAY_NAME);
            String isPreSelectedAccount = "false";
            updateIndividualPersonalAccountAttributes(account, data);
            updateJointAccountAttributes(account, data);
            updateSecondaryAccountAttributes(account, data);

            if (account.has(CDSConsentExtensionConstants.IS_PRE_SELECTED_ACCOUNT)) {
                isPreSelectedAccount = account.getString(CDSConsentExtensionConstants.IS_PRE_SELECTED_ACCOUNT);
            }
            data.put(CDSConsentExtensionConstants.ACCOUNT_ID, accountId);
            data.put(CDSConsentExtensionConstants.DISPLAY_NAME, displayName);
            data.put(CDSConsentExtensionConstants.IS_PRE_SELECTED_ACCOUNT, isPreSelectedAccount);
            accountsData.add(data);
        }
        return accountsData;
    }

    /**
     * Get the customer profile data list
     *
     * @param customerProfilesArray customer profiles array
     * @return customer profile data list
     */
    private List<Map<String, Object>> getCustomerProfileDataList(JSONArray customerProfilesArray) {

        List<Map<String, Object>> profileDataList = new ArrayList<>();
        for (int i = 0; i < customerProfilesArray.length(); i++) {
            JSONObject customerProfile = customerProfilesArray.getJSONObject(i);
            String profileId = customerProfile.getString(CDSConsentExtensionConstants.PROFILE_ID);
            String profileName = customerProfile.getString(CDSConsentExtensionConstants.PROFILE_NAME);
            JSONArray accountIdsArray = customerProfile.getJSONArray(CDSConsentExtensionConstants.ACCOUNT_IDS);

            List<String> accountIdsList = new ArrayList<>();
            for (int j = 0; j < accountIdsArray.length(); j++) {
                String accountId = accountIdsArray.getString(j);
                accountIdsList.add(accountId);
            }

            Map<String, Object> customerProfileMap = new HashMap<>();
            customerProfileMap.put(CDSConsentExtensionConstants.PROFILE_ID, profileId);
            customerProfileMap.put(CDSConsentExtensionConstants.PROFILE_NAME, profileName);
            customerProfileMap.put(CDSConsentExtensionConstants.ACCOUNT_IDS, accountIdsList);

            profileDataList.add(customerProfileMap);
        }
        return profileDataList;
    }

}
