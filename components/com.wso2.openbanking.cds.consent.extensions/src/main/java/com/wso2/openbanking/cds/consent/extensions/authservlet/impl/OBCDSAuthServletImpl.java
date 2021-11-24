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
package com.wso2.openbanking.cds.consent.extensions.authservlet.impl;

import com.wso2.openbanking.accelerator.consent.extensions.authservlet.model.OBAuthServletInterface;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
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

    @Override
    public Map<String, Object> updateRequestAttribute(HttpServletRequest httpServletRequest, JSONObject dataSet,
                                                      ResourceBundle resourceBundle) {

        Map<String, Object> returnMaps = new HashMap<>();

        // Set "data_requested" that contains the human-readable scope-requested information
        JSONArray dataRequestedJsonArray = dataSet.getJSONArray(CDSConsentExtensionConstants.DATA_REQUESTED);
        Map<String, List<String>> dataRequested = getRequestedDataMap(dataRequestedJsonArray);
        returnMaps.put(CDSConsentExtensionConstants.DATA_REQUESTED, dataRequested);

        // Add accounts list
        JSONArray accountsArray = dataSet.getJSONArray(CDSConsentExtensionConstants.ACCOUNTS);
        List<Map<String, String>> accountsData = getAccountsDataMap(accountsArray);
        httpServletRequest.setAttribute(CDSConsentExtensionConstants.ACCOUNTS_DATA, accountsData);

        //Consent amendment flow
        if (dataSet.has(CDSConsentExtensionConstants.IS_CONSENT_AMENDMENT) &&
                (boolean) dataSet.get(CDSConsentExtensionConstants.IS_CONSENT_AMENDMENT)) {
            // Add new data requested
            JSONArray newDataRequestedJsonArray = dataSet.getJSONArray(CDSConsentExtensionConstants.NEW_DATA_REQUESTED);
            Map<String, List<String>> newDataRequested = getRequestedDataMap(newDataRequestedJsonArray);
            returnMaps.put(CDSConsentExtensionConstants.NEW_DATA_REQUESTED, newDataRequested);
        }

        // Add additional attributes to be displayed
        httpServletRequest.setAttribute(CDSConsentExtensionConstants.SP_FULL_NAME,
                dataSet.getString(CDSConsentExtensionConstants.SP_FULL_NAME));
        httpServletRequest.setAttribute(CDSConsentExtensionConstants.CONSENT_EXPIRY,
                dataSet.getString(CDSConsentExtensionConstants.CONSENT_EXPIRY));
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

        return returnMaps;
    }

    @Override
    public Map<String, String> updateConsentMetaData(HttpServletRequest httpServletRequest) {
        return new HashMap<>();
    }

    @Override
    public String getJSPPath() {
        return "/ob_cds_default.jsp";
    }

    private void updateJointAccountAttributes(JSONObject account, Map<String, Object> data) {
        if (account != null && account.getBoolean(CDSConsentExtensionConstants.IS_JOINT_ACCOUNT_RESPONSE)) {
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

    private List<Map<String, String>> getAccountsDataMap(JSONArray accountsArray) {

        List<Map<String, String>> accountsData = new ArrayList<>();
        for (int accountIndex = 0; accountIndex < accountsArray.length(); accountIndex++) {
            JSONObject object = accountsArray.getJSONObject(accountIndex);
            String accountId = object.getString(CDSConsentExtensionConstants.ACCOUNT_ID);
            String displayName = object.getString(CDSConsentExtensionConstants.DISPLAY_NAME);
            String isPreSelectedAccount = "false";

            if (object.has(CDSConsentExtensionConstants.IS_PRE_SELECTED_ACCOUNT)) {
                isPreSelectedAccount = object.getString(CDSConsentExtensionConstants.IS_PRE_SELECTED_ACCOUNT);
            }
            Map<String, String> data = new HashMap<>();
            data.put(CDSConsentExtensionConstants.ACCOUNT_ID, accountId);
            data.put(CDSConsentExtensionConstants.DISPLAY_NAME, displayName);
            data.put(CDSConsentExtensionConstants.IS_PRE_SELECTED_ACCOUNT, isPreSelectedAccount);
            accountsData.add(data);
        }
        return accountsData;
    }

}
