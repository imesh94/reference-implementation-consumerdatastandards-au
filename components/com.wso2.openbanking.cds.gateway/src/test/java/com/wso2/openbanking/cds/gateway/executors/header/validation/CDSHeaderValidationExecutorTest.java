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

package com.wso2.openbanking.cds.gateway.executors.header.validation;

import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIRequestContext;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.apimgt.common.gateway.dto.MsgInfoDTO;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for CDSHeaderValidationExecutor.
 */
public class CDSHeaderValidationExecutorTest {

    private static final String VALID_CLIENT_HEADERS = "TW96aWxsYS81LjAgKFgxMTsgTGludXggeDg2XzY0KSBBcHBsZVdlYktpdC81" +
            "MzcuMzYgKEtIVE1MLCBsaWtlIEdlY2tvKSBDaHJvbWUvNzkuMC4zOTQ1Ljg4IFNhZmFyaS81MzcuMzY=";
    private static final String VALID_AUTH_DATE = "Thu, 16 Jan 2020 16:50:15 GMT";
    private static final String VALID_CUSTOMER_IP6_ADDR = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";
    private static final String VALID_INTERACTION_ID = "6ba7b814-9dad-11d1-80b4-00c04fd430c8";
    private final CDSHeaderValidationExecutor uut = new CDSHeaderValidationExecutor();

    @BeforeClass
    public void setup() {
        updateRequestHeaders(StringUtils.EMPTY, StringUtils.EMPTY);
    }

    @Test
    public void testPreProcessRequest() {
        OBAPIRequestContext obApiRequestContextMock = updateRequestHeaders(StringUtils.EMPTY, StringUtils.EMPTY);
        this.uut.preProcessRequest(obApiRequestContextMock);
        verify(obApiRequestContextMock, times(0)).setError(true);
    }

    @Test
    public void testPreProcessRequestWithCustomerIpAddress() {
        OBAPIRequestContext obApiRequestContextMock = updateRequestHeaders
                (CDSHeaderValidationExecutor.X_FAPI_CUSTOMER_IP_ADDRESS, "192.1.2.3", "::", "2001:db8::", "::1234:5678",
                "2001:db8::1234:5678", "2001:db8:1::ab9:C0A8:102", "2001:db8::127.10.11.12", "192.1.1.300", "invalid");

        // Assert valid IP addresses
        this.uut.preProcessRequest(obApiRequestContextMock);
        this.uut.preProcessRequest(obApiRequestContextMock);
        this.uut.preProcessRequest(obApiRequestContextMock);
        this.uut.preProcessRequest(obApiRequestContextMock);
        this.uut.preProcessRequest(obApiRequestContextMock);
        this.uut.preProcessRequest(obApiRequestContextMock);
        this.uut.preProcessRequest(obApiRequestContextMock);
        this.uut.preProcessRequest(obApiRequestContextMock);
        verify(obApiRequestContextMock, times(0)).setError(true);

        // Assert invalid IP addresses
        this.uut.preProcessRequest(obApiRequestContextMock);
        this.uut.preProcessRequest(obApiRequestContextMock);
        verify(obApiRequestContextMock, times(2)).setError(true);
    }

    @Test
    public void testPreProcessRequestWithInvalidClientHeaders() {
        OBAPIRequestContext obApiRequestContextMock = updateRequestHeaders
                (CDSHeaderValidationExecutor.X_CDS_CLIENT_HEADERS, StringUtils.SPACE);

        this.uut.preProcessRequest(obApiRequestContextMock);
        this.uut.preProcessRequest(obApiRequestContextMock);
        verify(obApiRequestContextMock, times(2)).setError(true);
    }

    @Test
    public void testIsValidHttpDate() {
        Assert.assertTrue(this.uut.isValidHttpDate("Sun, 06 Nov 2024 08:49:37 GMT"));
        Assert.assertTrue(this.uut.isValidHttpDate("Sunday, 06-Nov-94 08:49:37 GMT"));
        Assert.assertTrue(this.uut.isValidHttpDate("Sun Nov 16 08:49:37 1994"));
        Assert.assertTrue(this.uut.isValidHttpDate("Sun Nov  6 08:49:37 2024"));

        Assert.assertFalse(this.uut.isValidHttpDate("24/11/2024 17:20:30"));
        Assert.assertFalse(this.uut.isValidHttpDate("24-11-1994 08:10 PM"));
    }

    private OBAPIRequestContext updateRequestHeaders(String key, String... values) {
        Map<String, String> headers = mock(Map.class);
        when(headers.get(CDSHeaderValidationExecutor.X_CDS_CLIENT_HEADERS)).thenReturn(VALID_CLIENT_HEADERS);
        when(headers.get(CDSHeaderValidationExecutor.X_FAPI_AUTH_DATE)).thenReturn(VALID_AUTH_DATE);
        when(headers.get(CDSHeaderValidationExecutor.X_FAPI_CUSTOMER_IP_ADDRESS)).thenReturn(VALID_CUSTOMER_IP6_ADDR);
        when(headers.get(CDSHeaderValidationExecutor.X_FAPI_INTERACTION_ID)).thenReturn(VALID_INTERACTION_ID);

        if (StringUtils.isNotBlank(key)) {
            when(headers.get(key)).thenReturn(StringUtils.EMPTY, values);
        }

        MsgInfoDTO msgInfoDTOMock = mock(MsgInfoDTO.class);
        when(msgInfoDTOMock.getHeaders()).thenReturn(headers);

        OBAPIRequestContext obApiRequestContextMock = mock(OBAPIRequestContext.class);
        when(obApiRequestContextMock.getMsgInfo()).thenReturn(msgInfoDTOMock);

        return obApiRequestContextMock;
    }
}
