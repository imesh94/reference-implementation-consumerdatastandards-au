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

package com.wso2.openbanking.cds.gateway.executors.reporting;

import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIRequestContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIResponseContext;
import com.wso2.openbanking.cds.gateway.utils.GatewayConstants;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.apimgt.common.gateway.dto.APIRequestInfoDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.MsgInfoDTO;

import java.util.HashMap;
import java.util.Map;

public class CDSCommonDataReportingExecutorTest {

    OBAPIRequestContext obApiRequestContextMock;
    OBAPIResponseContext obApiResponseContextMock;
    CDSCommonDataReportingExecutor cdsCommonDataReportingExecutor;
    MsgInfoDTO msgInfoDTOMock;
    APIRequestInfoDTO apiRequestInfoDTOMock;
    Map<String, Object> analyticsData = new HashMap<>();

    @BeforeClass
    public void initClass() {

        cdsCommonDataReportingExecutor = Mockito.spy(new CDSCommonDataReportingExecutor());
        obApiRequestContextMock = Mockito.mock(OBAPIRequestContext.class);
        obApiResponseContextMock = Mockito.mock(OBAPIResponseContext.class);
        msgInfoDTOMock = Mockito.mock(MsgInfoDTO.class);
        apiRequestInfoDTOMock = Mockito.mock(APIRequestInfoDTO.class);
    }


    @Test
    public void testPreProcessRequest() {

        Map<String, String> headers = new HashMap<>();
        headers.put(GatewayConstants.X_FAPI_CUSTOMER_IP_ADDRESS, "ip-address");

        Mockito.doReturn(msgInfoDTOMock).when(obApiRequestContextMock).getMsgInfo();
        Mockito.doReturn(headers).when(msgInfoDTOMock).getHeaders();
        CDSCommonDataReportingExecutor cdsCommonDataReportingExecutor = new CDSCommonDataReportingExecutor();
        Mockito.doReturn(apiRequestInfoDTOMock).when(obApiRequestContextMock).getApiRequestInfo();
        Mockito.doReturn(analyticsData).when(obApiRequestContextMock).getAnalyticsData();
        cdsCommonDataReportingExecutor.preProcessRequest(obApiRequestContextMock);

        Assert.assertEquals(obApiRequestContextMock.getAnalyticsData().get(GatewayConstants.CUSTOMER_STATUS),
                GatewayConstants.CUSTOMER_PRESENT);
    }

    @Test
    public void testPostProcessRequest() {

        Map<String, String> headers = new HashMap<>();
        headers.put(GatewayConstants.AUTHORIZATION, null);

        Mockito.doReturn(msgInfoDTOMock).when(obApiRequestContextMock).getMsgInfo();
        Mockito.doReturn(headers).when(msgInfoDTOMock).getHeaders();
        CDSCommonDataReportingExecutor cdsCommonDataReportingExecutor = new CDSCommonDataReportingExecutor();
        Mockito.doReturn(analyticsData).when(obApiRequestContextMock).getAnalyticsData();
        cdsCommonDataReportingExecutor.postProcessRequest(obApiRequestContextMock);

        Assert.assertEquals(obApiRequestContextMock.getAnalyticsData().get(GatewayConstants.ACCESS_TOKEN_ID),
                null);
    }
}
