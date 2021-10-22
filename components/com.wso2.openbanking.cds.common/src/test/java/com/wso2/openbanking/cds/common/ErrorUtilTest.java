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

package com.wso2.openbanking.cds.common;

import com.wso2.openbanking.cds.common.error.handling.models.CDSErrorMeta;
import com.wso2.openbanking.cds.common.error.handling.util.ErrorConstants;
import com.wso2.openbanking.cds.common.error.handling.util.ErrorUtil;
import com.wso2.openbanking.cds.common.util.CommonTestDataProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;

/**
 * Test class for Error Util Functionality.
 */
public class ErrorUtilTest {

    @Test(dataProvider = "ClientErrorTestDataProvider", dataProviderClass = CommonTestDataProvider.class)
    public void testClientErrorScenarios(String errorCode, Boolean assertion) {

        HashSet<String> statusCodes = new HashSet<>();
        statusCodes.add(errorCode);
        Boolean isClientError = ErrorUtil.isAnyClientErrors(statusCodes);

        Assert.assertEquals(isClientError, assertion);
    }

    @Test(dataProvider = "HttpsCodeTestDataProvider", dataProviderClass = CommonTestDataProvider.class)
    public void testGetHttpsErrorCodeMethod(String errorCode, int assertion) {

        HashSet<String> statusCodes = new HashSet<>();
        statusCodes.add(errorCode);
        int httpsCode = ErrorUtil.getHTTPErrorCode(statusCodes);

        Assert.assertEquals(httpsCode, assertion);
    }

    @Test(dataProvider = "ErrorObjectTestDataProvider", dataProviderClass = CommonTestDataProvider.class)
    public void testGetErrorObject(ErrorConstants.AUErrorEnum error, String errorMessage, CDSErrorMeta metaData) {

        JSONObject errorJson = ErrorUtil.getErrorObject(error, errorMessage, metaData);
        Assert.assertEquals(errorJson.get(ErrorConstants.ERROR_ENUM), error);
    }

    @Test(dataProvider = "ErrorObjectTestDataProvider", dataProviderClass = CommonTestDataProvider.class)
    public void testGetErrorJson(ErrorConstants.AUErrorEnum error, String errorMessage, CDSErrorMeta metaData) {

        JSONObject errorJson = ErrorUtil.getErrorObject(error, errorMessage, metaData);
        JSONArray errorJsonArray = new JSONArray();
        errorJsonArray.put(errorJson);
        String errorJsonString = ErrorUtil.getErrorJson(errorJsonArray);
        Assert.assertNotNull(errorJsonString);
    }
}
