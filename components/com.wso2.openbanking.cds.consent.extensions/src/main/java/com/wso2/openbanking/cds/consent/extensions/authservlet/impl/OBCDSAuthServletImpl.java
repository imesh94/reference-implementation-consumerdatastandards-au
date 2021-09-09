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

        //Sets "data_requested" that contains the human-readable scope-requested information
        JSONArray dataRequestedJsonArray = dataSet.getJSONArray(CDSConsentExtensionConstants.DATA_REQUESTED);
        Map<String, List<String>> dataRequested = new LinkedHashMap<>();

        for (int requestedDataIndex = 0; requestedDataIndex < dataRequestedJsonArray.length(); requestedDataIndex++) {
            JSONObject dataObj = dataRequestedJsonArray.getJSONObject(requestedDataIndex);
            String title = dataObj.getString("title");
            JSONArray dataArray = dataObj.getJSONArray("data");

            ArrayList<String> listData = new ArrayList<>();
            for (int dataIndex = 0; dataIndex < dataArray.length(); dataIndex++) {
                listData.add(dataArray.getString(dataIndex));
            }
            dataRequested.put(title, listData);
        }
        returnMaps.put(CDSConsentExtensionConstants.DATA_REQUESTED, dataRequested);

        // add accounts list
        List<Map<String, String>> accountsData = new ArrayList<>();
        JSONArray accountsArray = dataSet.getJSONArray(CDSConsentExtensionConstants.ACCOUNTS);
        for (int accountIndex = 0; accountIndex < accountsArray.length(); accountIndex++) {
            JSONObject object = accountsArray.getJSONObject(accountIndex);
            String accountId = object.getString(CDSConsentExtensionConstants.ACCOUNT_ID);
            String displayName = object.getString(CDSConsentExtensionConstants.DISPLAY_NAME);
            Map<String, String> data = new HashMap<>();
            data.put(CDSConsentExtensionConstants.ACCOUNT_ID, accountId);
            data.put(CDSConsentExtensionConstants.DISPLAY_NAME, displayName);
            accountsData.add(data);
        }
        httpServletRequest.setAttribute(CDSConsentExtensionConstants.ACCOUNTS_DATA, accountsData);
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

        String[] accounts = httpServletRequest.getParameter("accounts[]").split(":");
        returnMaps.put("accountIds", new JSONArray(accounts));

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
}
