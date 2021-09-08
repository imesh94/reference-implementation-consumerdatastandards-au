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

import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for CDS Auth Servlet
 */
public class OBCDSAuthServletImplTests {

    private static OBCDSAuthServletImpl obCdsAuthServlet;
    private static HttpServletRequest httpServletRequest;
    private static ResourceBundle resourceBundle;


    @BeforeClass
    public void initClass() {

        obCdsAuthServlet = new OBCDSAuthServletImpl();
        httpServletRequest = mock(HttpServletRequest.class);
        resourceBundle = mock(ResourceBundle.class);
    }

    @Test(expectedExceptions = JSONException.class)
    public void testUpdateRequestAttributeWithEmptyDataset() {
        obCdsAuthServlet.updateRequestAttribute(httpServletRequest, new JSONObject(), resourceBundle);
    }

    @Test
    public void testUpdateRequestAttributeWithValidDataset() {

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
