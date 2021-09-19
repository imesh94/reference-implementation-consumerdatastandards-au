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

import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.common.utils.CommonConstants;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;


import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for CDS Auth Servlet
 */
@PrepareForTest({OpenBankingCDSConfigParser.class})
@PowerMockIgnore({"com.wso2.openbanking.accelerator.consent.extensions.common.*"})
public class OBCDSAuthServletImplTests extends PowerMockTestCase {


    private static OpenBankingCDSConfigParser openBankingCDSConfigParserMock;
    private static OBCDSAuthServletImpl obCdsAuthServlet;
    private static HttpServletRequest httpServletRequest;
    private static ResourceBundle resourceBundle;
    private static Map<String, Boolean> configMap;

    @BeforeClass
    public void initClass() {
        configMap = new HashMap<>();
        configMap.put(CommonConstants.ACCOUNT_MASKING, true);

        obCdsAuthServlet = new OBCDSAuthServletImpl();
        httpServletRequest = mock(HttpServletRequest.class);
        resourceBundle = mock(ResourceBundle.class);
        openBankingCDSConfigParserMock = mock(OpenBankingCDSConfigParser.class);
    }

    @Test(expectedExceptions = JSONException.class)
    public void testUpdateRequestAttributeWithEmptyDataset() {
        obCdsAuthServlet.updateRequestAttribute(httpServletRequest, new JSONObject(), resourceBundle);
    }

    @Test
    public void testUpdateRequestAttributeWithValidDataset() {

        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        PowerMockito.when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        doReturn(configMap).when(openBankingCDSConfigParserMock).getConfiguration();

        JSONArray dataRequested = new JSONArray();
        JSONArray accounts = new JSONArray();
        JSONObject dataSet = new JSONObject();

        dataSet.put(CDSConsentExtensionConstants.DATA_REQUESTED, dataRequested);
        dataSet.put(CDSConsentExtensionConstants.ACCOUNTS, accounts);
        dataSet.put(CDSConsentExtensionConstants.SP_FULL_NAME, "TestServiceProvider");
        dataSet.put(CDSConsentExtensionConstants.CONSENT_EXPIRY, "ConsentExpiryDate");

        Map<String, Object> returnMap = obCdsAuthServlet.updateRequestAttribute(
                httpServletRequest, dataSet, resourceBundle);

        Assert.assertTrue(!returnMap.isEmpty());
    }

    @Test
    public void testUpdateRequestAttributeWithValidDatasetWithElements() {

        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        PowerMockito.when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        doReturn(configMap).when(openBankingCDSConfigParserMock).getConfiguration();

        JSONArray dataRequested = new JSONArray();
        JSONArray accounts = new JSONArray();
        JSONObject dataSet = new JSONObject();
        JSONArray testArray = new JSONArray();
        JSONObject dataReqJsonElement = new JSONObject();
        JSONObject accJsonElement = new JSONObject();

        // add data to 'data_requested' section
        dataReqJsonElement.put(CDSConsentExtensionConstants.TITLE, "testTitle");
        dataReqJsonElement.put(CDSConsentExtensionConstants.DATA, testArray);
        dataRequested.put(dataReqJsonElement);

        // add accounts data
        accJsonElement.put(CDSConsentExtensionConstants.ACCOUNT_ID, "1234");
        accJsonElement.put(CDSConsentExtensionConstants.DISPLAY_NAME, "test-account");
        accounts.put(accJsonElement);

        dataSet.put(CDSConsentExtensionConstants.DATA_REQUESTED, dataRequested);
        dataSet.put(CDSConsentExtensionConstants.ACCOUNTS, accounts);
        dataSet.put(CDSConsentExtensionConstants.SP_FULL_NAME, "TestServiceProvider");
        dataSet.put(CDSConsentExtensionConstants.CONSENT_EXPIRY, "ConsentExpiryDate");

        Map<String, Object> returnMap = obCdsAuthServlet.updateRequestAttribute(
                httpServletRequest, dataSet, resourceBundle);

        Assert.assertTrue(!returnMap.isEmpty());
    }

    @Test
    public void testUpdateSessionAttribute() {
        Map<String, Object> returnMap = obCdsAuthServlet.updateSessionAttribute(
                httpServletRequest, new JSONObject(), resourceBundle);
        Assert.assertTrue(returnMap.isEmpty());
    }

    @Test
    public void testUpdateConsentData() {
        when(httpServletRequest.getParameter("accounts[]")).thenReturn("1:2:3");
        Map<String, Object> returnMap = obCdsAuthServlet.updateConsentData(httpServletRequest);
        Assert.assertTrue(!returnMap.isEmpty());
    }

    @Test
    public void testUpdateConsentMetaData() {
        Map<String, String> returnMap = obCdsAuthServlet.updateConsentMetaData(httpServletRequest);
        Assert.assertTrue(returnMap.isEmpty());
    }

    @Test
    public void testGetJSPPath() {
        String jspPath = obCdsAuthServlet.getJSPPath();
        Assert.assertTrue("/ob_cds_default.jsp".equals(jspPath));
    }
}