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

package com.wso2.openbanking.cds.gateway.executors.error.handler;

import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIRequestContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIResponseContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OpenBankingExecutorError;
import com.wso2.openbanking.accelerator.gateway.util.GatewayConstants;
import com.wso2.openbanking.cds.common.utils.ErrorConstants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.apimgt.common.gateway.dto.MsgInfoDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for CDS Error Handler class
 */
public class CDSErrorHandlerTest {

    @Mock
    OBAPIRequestContext obApiRequestContextMock;
    @Mock
    OBAPIResponseContext obApiResponseContextMock;
    @Spy
    CDSErrorHandler cdsErrorHandler;
    @Mock
    MsgInfoDTO msgInfoDTOMock;
    ArrayList<OpenBankingExecutorError> errors = new ArrayList<>();
    ArrayList<OpenBankingExecutorError> emptyErrors = new ArrayList<>();
    ArrayList<OpenBankingExecutorError> dcrErrors = new ArrayList<>();
    ArrayList<OpenBankingExecutorError> accountErrors = new ArrayList<>();
    Map<String, String> addedHeaders = new HashMap<>();
    private static final Boolean TRUE = true;
    private static final Boolean FALSE = false;
    private static final String DCR_PATH = "/register";
    private static final String ACCOUNTS_PATH = "/banking/accounts/";

    @BeforeClass
    public void initClass() {

        MockitoAnnotations.initMocks(this);

        cdsErrorHandler = Mockito.spy(CDSErrorHandler.class);
        obApiRequestContextMock = Mockito.mock(OBAPIRequestContext.class);
        obApiResponseContextMock = Mockito.mock(OBAPIResponseContext.class);
        msgInfoDTOMock = Mockito.mock(MsgInfoDTO.class);

        dcrErrors.add(new OpenBankingExecutorError("invalid_software_statement", "invalid_software_statement",
                "Duplicate registrations for a given software_id are not valid",
                ErrorConstants.BAD_REQUEST_CODE));

        accountErrors.add(new OpenBankingExecutorError("AU.CDR.Resource.InvalidBankingAccount",
                "Invalid Banking Account", "ID of the account not found or invalid",
                ErrorConstants.NOT_FOUND_CODE));
    }

    @Test
    public void testPreProcessRequestSuccessScenario() {
        Mockito.doReturn(FALSE).when(obApiRequestContextMock).isError();

        cdsErrorHandler.preProcessRequest(obApiRequestContextMock);
    }

    @Test
    public void testPostProcessRequestSuccessScenario() {
        Mockito.doReturn(FALSE).when(obApiRequestContextMock).isError();

        cdsErrorHandler.postProcessRequest(obApiRequestContextMock);
    }

    @Test
    public void testPreProcessResponseSuccessScenario() {
        Mockito.doReturn(FALSE).when(obApiResponseContextMock).isError();

        cdsErrorHandler.preProcessResponse(obApiResponseContextMock);
    }

    @Test
    public void testPostProcessResponseSuccessScenario() {
        Mockito.doReturn(FALSE).when(obApiResponseContextMock).isError();

        cdsErrorHandler.preProcessResponse(obApiResponseContextMock);
    }

    @Test
    public void testDCRPreProcessRequestErrorScenario() {
        Mockito.doReturn(TRUE).when(obApiRequestContextMock).isError();
        Mockito.doReturn(errors).when(obApiRequestContextMock).getErrors();
        Mockito.doReturn(msgInfoDTOMock).when(obApiRequestContextMock).getMsgInfo();
        Mockito.doReturn(DCR_PATH).when(msgInfoDTOMock).getResource();
        Mockito.doReturn(addedHeaders).when(obApiRequestContextMock).getAddedHeaders();

        cdsErrorHandler.preProcessRequest(obApiRequestContextMock);
        Assert.assertNotNull(obApiRequestContextMock.getAddedHeaders());
        Assert.assertEquals(obApiRequestContextMock.getAddedHeaders().get(GatewayConstants.CONTENT_TYPE_TAG),
                GatewayConstants.JSON_CONTENT_TYPE);
    }

    @Test
    public void testAccountsPreProcessRequestErrorScenario() {
        Mockito.doReturn(TRUE).when(obApiRequestContextMock).isError();
        Mockito.doReturn(accountErrors).when(obApiRequestContextMock).getErrors();
        Mockito.doReturn(msgInfoDTOMock).when(obApiRequestContextMock).getMsgInfo();
        Mockito.doReturn(ACCOUNTS_PATH).when(msgInfoDTOMock).getResource();
        Mockito.doReturn(addedHeaders).when(obApiRequestContextMock).getAddedHeaders();

        cdsErrorHandler.preProcessRequest(obApiRequestContextMock);
        Assert.assertNotNull(obApiRequestContextMock.getAddedHeaders());
        Assert.assertEquals(obApiRequestContextMock.getAddedHeaders().get(GatewayConstants.CONTENT_TYPE_TAG),
                GatewayConstants.JSON_CONTENT_TYPE);
    }

    @Test
    public void testDCRPostProcessRequestErrorScenario() {
        Mockito.doReturn(TRUE).when(obApiRequestContextMock).isError();
        Mockito.doReturn(errors).when(obApiRequestContextMock).getErrors();
        Mockito.doReturn(msgInfoDTOMock).when(obApiRequestContextMock).getMsgInfo();
        Mockito.doReturn(DCR_PATH).when(msgInfoDTOMock).getResource();
        Mockito.doReturn(addedHeaders).when(obApiRequestContextMock).getAddedHeaders();

        cdsErrorHandler.postProcessRequest(obApiRequestContextMock);
        Assert.assertNotNull(obApiRequestContextMock.getAddedHeaders());
        Assert.assertEquals(obApiRequestContextMock.getAddedHeaders().get(GatewayConstants.CONTENT_TYPE_TAG),
                GatewayConstants.JSON_CONTENT_TYPE);
    }

    @Test
    public void testAccountsPostProcessRequestErrorScenario() {
        Mockito.doReturn(TRUE).when(obApiRequestContextMock).isError();
        Mockito.doReturn(accountErrors).when(obApiRequestContextMock).getErrors();
        Mockito.doReturn(msgInfoDTOMock).when(obApiRequestContextMock).getMsgInfo();
        Mockito.doReturn(ACCOUNTS_PATH).when(msgInfoDTOMock).getResource();
        Mockito.doReturn(addedHeaders).when(obApiRequestContextMock).getAddedHeaders();

        cdsErrorHandler.postProcessRequest(obApiRequestContextMock);
        Assert.assertNotNull(obApiRequestContextMock.getAddedHeaders());
        Assert.assertEquals(obApiRequestContextMock.getAddedHeaders().get(GatewayConstants.CONTENT_TYPE_TAG),
                GatewayConstants.JSON_CONTENT_TYPE);
    }

    @Test
    public void testDCRPreProcessResponseErrorScenario() {
        Mockito.doReturn(TRUE).when(obApiResponseContextMock).isError();
        Mockito.doReturn(errors).when(obApiResponseContextMock).getErrors();
        Mockito.doReturn(msgInfoDTOMock).when(obApiResponseContextMock).getMsgInfo();
        Mockito.doReturn(DCR_PATH).when(msgInfoDTOMock).getResource();
        Mockito.doReturn(addedHeaders).when(obApiResponseContextMock).getAddedHeaders();

        cdsErrorHandler.preProcessResponse(obApiResponseContextMock);
        Assert.assertNotNull(obApiRequestContextMock.getAddedHeaders());
        Assert.assertEquals(obApiRequestContextMock.getAddedHeaders().get(GatewayConstants.CONTENT_TYPE_TAG),
                GatewayConstants.JSON_CONTENT_TYPE);
        Assert.assertNotNull(obApiRequestContextMock.getAnalyticsData());
    }

    @Test
    public void testAccountsPreProcessResponseErrorScenario() {
        Mockito.doReturn(TRUE).when(obApiResponseContextMock).isError();
        Mockito.doReturn(accountErrors).when(obApiResponseContextMock).getErrors();
        Mockito.doReturn(msgInfoDTOMock).when(obApiResponseContextMock).getMsgInfo();
        Mockito.doReturn(ACCOUNTS_PATH).when(msgInfoDTOMock).getResource();
        Mockito.doReturn(addedHeaders).when(obApiResponseContextMock).getAddedHeaders();

        cdsErrorHandler.preProcessResponse(obApiResponseContextMock);
        Assert.assertNotNull(obApiRequestContextMock.getAddedHeaders());
        Assert.assertEquals(obApiRequestContextMock.getAddedHeaders().get(GatewayConstants.CONTENT_TYPE_TAG),
                GatewayConstants.JSON_CONTENT_TYPE);
        Assert.assertNotNull(obApiRequestContextMock.getAnalyticsData());
    }

    @Test
    public void testDCRPostProcessResponseErrorScenario() {
        Mockito.doReturn(TRUE).when(obApiResponseContextMock).isError();
        Mockito.doReturn(errors).when(obApiResponseContextMock).getErrors();
        Mockito.doReturn(msgInfoDTOMock).when(obApiResponseContextMock).getMsgInfo();
        Mockito.doReturn(DCR_PATH).when(msgInfoDTOMock).getResource();
        Mockito.doReturn(addedHeaders).when(obApiResponseContextMock).getAddedHeaders();

        cdsErrorHandler.postProcessResponse(obApiResponseContextMock);
        Assert.assertNotNull(obApiRequestContextMock.getAddedHeaders());
        Assert.assertEquals(obApiRequestContextMock.getAddedHeaders().get(GatewayConstants.CONTENT_TYPE_TAG),
                GatewayConstants.JSON_CONTENT_TYPE);
        Assert.assertNotNull(obApiRequestContextMock.getAnalyticsData());
    }

    @Test
    public void testAccountsPostProcessResponseErrorScenario() {
        Mockito.doReturn(TRUE).when(obApiResponseContextMock).isError();
        Mockito.doReturn(accountErrors).when(obApiResponseContextMock).getErrors();
        Mockito.doReturn(msgInfoDTOMock).when(obApiResponseContextMock).getMsgInfo();
        Mockito.doReturn(ACCOUNTS_PATH).when(msgInfoDTOMock).getResource();
        Mockito.doReturn(addedHeaders).when(obApiResponseContextMock).getAddedHeaders();

        cdsErrorHandler.postProcessResponse(obApiResponseContextMock);
        Assert.assertNotNull(obApiRequestContextMock.getAddedHeaders());
        Assert.assertEquals(obApiRequestContextMock.getAddedHeaders().get(GatewayConstants.CONTENT_TYPE_TAG),
                GatewayConstants.JSON_CONTENT_TYPE);
        Assert.assertNotNull(obApiRequestContextMock.getAnalyticsData());
    }

    @Test
    public void testGetDCRErrorJson() {

        JSONArray errorJson = CDSErrorHandler.getDCRErrorJSON(dcrErrors);

        Assert.assertNotNull(errorJson);
        JSONObject error = (JSONObject) errorJson.get(0);
        Assert.assertEquals(error.get(ErrorConstants.ERROR), "invalid_software_statement");
        Assert.assertEquals(error.get(ErrorConstants.ERROR_DESCRIPTION),
                "Duplicate registrations for a given software_id are not valid");
    }

    @Test
    public void testGetEmptyDCRErrorJson() {

        JSONArray errorJson = CDSErrorHandler.getDCRErrorJSON(emptyErrors);

        Assert.assertTrue(errorJson.isEmpty());
    }
}
