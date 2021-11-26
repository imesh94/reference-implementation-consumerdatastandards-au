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
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.gateway.util.CDSGatewayConstants;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.apimgt.common.gateway.dto.MsgInfoDTO;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for CDSHeaderValidationExecutor.
 */
@PowerMockIgnore("jdk.internal.reflect.*")
@PrepareForTest({OpenBankingCDSConfigParser.class})
public class CDSHeaderValidationExecutorTest extends PowerMockTestCase {

    private static final String VALID_CLIENT_HEADERS = "TW96aWxsYS81LjAgKFgxMTsgTGludXggeDg2XzY0KSBBcHBsZVdlYktpdC81" +
            "MzcuMzYgKEtIVE1MLCBsaWtlIEdlY2tvKSBDaHJvbWUvNzkuMC4zOTQ1Ljg4IFNhZmFyaS81MzcuMzY=";
    private static final String VALID_AUTH_DATE = "Thu, 16 Jan 2020 16:50:15 GMT";
    private static final String VALID_CUSTOMER_IP6_ADDR = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";
    private static final String VALID_INTERACTION_ID = "6ba7b814-9dad-11d1-80b4-00c04fd430c8";
    private static final String HOLDER_SPECIFIC_IDENTIFIER = "x-HID-v";

    private final CDSHeaderValidationExecutor uut = new CDSHeaderValidationExecutor();

    @BeforeMethod
    public void setup() {
        OpenBankingCDSConfigParser openBankingCDSConfigParserMock = PowerMockito.mock(OpenBankingCDSConfigParser.class);
        when(openBankingCDSConfigParserMock.getHolderSpecificIdentifier()).thenReturn(HOLDER_SPECIFIC_IDENTIFIER);
        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        PowerMockito.when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
    }

    @Test
    public void testPreProcessRequest() {
        OBAPIRequestContext obApiRequestContextMock = getOBAPIRequestContext(StringUtils.EMPTY, StringUtils.EMPTY);
        this.uut.preProcessRequest(obApiRequestContextMock);
        verify(obApiRequestContextMock, times(0)).setError(true);
    }

    @Test
    public void testPreProcessRequestWithCustomerIpAddress() {
        OBAPIRequestContext obApiRequestContextMock = getOBAPIRequestContext
                (CDSGatewayConstants.X_FAPI_CUSTOMER_IP_ADDRESS, "192.1.2.3", "::", "2001:db8::", "::1234:5678",
                        "2001:db8::1234:5678", "2001:db8:1::ab9:C0A8:102", "2001:db8::127.10.11.12",
                        "192.1.1.300", "invalid");

        // Assert valid IP addresses
        this.uut.preProcessRequest(obApiRequestContextMock); // test empty string, should return true
        this.uut.preProcessRequest(obApiRequestContextMock); // test "192.1.2.3", should return true
        this.uut.preProcessRequest(obApiRequestContextMock); // test "::", should return true
        this.uut.preProcessRequest(obApiRequestContextMock); // test "2001:db8::", should return true
        this.uut.preProcessRequest(obApiRequestContextMock); // test "::1234:5678", should return true
        this.uut.preProcessRequest(obApiRequestContextMock); // test "2001:db8::1234:5678", should return true
        this.uut.preProcessRequest(obApiRequestContextMock); // test "2001:db8:1::ab9:C0A8:102", should return true
        this.uut.preProcessRequest(obApiRequestContextMock); // test "2001:db8::127.10.11.12", should return true
        verify(obApiRequestContextMock, times(0)).setError(true);

        // Assert invalid IP addresses
        this.uut.preProcessRequest(obApiRequestContextMock); // test "192.1.1.300", should return false
        this.uut.preProcessRequest(obApiRequestContextMock); // test "invalid", should return false
        verify(obApiRequestContextMock, times(2)).setError(true);
    }

    @Test
    public void testPreProcessRequestWithInvalidClientHeaders() {
        OBAPIRequestContext obApiRequestContextMock = getOBAPIRequestContext
                (CDSGatewayConstants.X_CDS_CLIENT_HEADERS, StringUtils.SPACE);

        this.uut.preProcessRequest(obApiRequestContextMock);
        this.uut.preProcessRequest(obApiRequestContextMock);
        verify(obApiRequestContextMock, times(2)).setError(true);
    }

    @Test
    public void testPreProcessRequestWithAuthDate() {
        OBAPIRequestContext obApiRequestContextMock = getOBAPIRequestContext
                (CDSGatewayConstants.X_FAPI_AUTH_DATE, "Sun, 06 Nov 2024 08:49:37 GMT", "24-11-1994 08:10 PM");
        this.uut.preProcessRequest(obApiRequestContextMock); // test 06 Nov 2024 08:49:37 GMT, should return true

        this.uut.preProcessRequest(obApiRequestContextMock); // test empty string, should return false
        this.uut.preProcessRequest(obApiRequestContextMock); // test 24-11-1994 08:10 PM, should return false

        verify(obApiRequestContextMock, times(2)).setError(true);
    }

    @Test
    public void testPreProcessRequestWithInteractionId() {
        OBAPIRequestContext obApiRequestContextMock = getOBAPIRequestContext
                (CDSGatewayConstants.X_FAPI_INTERACTION_ID, "invalid-uuid", "1-2-3-4-5");
        this.uut.preProcessRequest(obApiRequestContextMock);
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
        Assert.assertFalse(this.uut.isValidHttpDate("Sun, 06 Nov 2024 08:49:37 EST"));
        Assert.assertFalse(this.uut.isValidHttpDate("Sunday, 06-Nov-94 08:49:37 EST"));
    }

    @Test
    public void testIsValidMaxVersion() {
        OBAPIRequestContext obApiRequestContextMock = getOBAPIRequestContext
                (CDSGatewayConstants.MAX_REQUESTED_ENDPOINT_VERSION, "1", "foo", "1-2-3", "1.5", "100");
        this.uut.preProcessRequest(obApiRequestContextMock); // test empty string, should return false
        this.uut.preProcessRequest(obApiRequestContextMock); // test "1", should return true
        this.uut.preProcessRequest(obApiRequestContextMock); // test "foo", should return false
        this.uut.preProcessRequest(obApiRequestContextMock); // test "1-2-3", should return false
        this.uut.preProcessRequest(obApiRequestContextMock); // test "1.5", should return false
        this.uut.preProcessRequest(obApiRequestContextMock); // test "100", should return false

        verify(obApiRequestContextMock, times(5)).setError(true);
    }

    @Test
    public void testIsValidMinVersion() {
        OBAPIRequestContext obApiRequestContextMock = getOBAPIRequestContext
                (CDSGatewayConstants.MIN_REQUESTED_ENDPOINT_VERSION, "2", "1", "foo", "1-2-3", "1.5", "-3");
        this.uut.preProcessRequest(obApiRequestContextMock); // test empty string, should return true
        this.uut.preProcessRequest(obApiRequestContextMock); // test "2", should return true

        this.uut.preProcessRequest(obApiRequestContextMock); // test "1", should return false
        this.uut.preProcessRequest(obApiRequestContextMock); // test "foo", should return false
        this.uut.preProcessRequest(obApiRequestContextMock); // test "1-2-3", should return false
        this.uut.preProcessRequest(obApiRequestContextMock); // test "1.5", should return false

        verify(obApiRequestContextMock, times(4)).setError(true);
    }

    @Test
    public void testIsValidHidVersion() {
        OBAPIRequestContext obApiRequestContextMock = getOBAPIRequestContext
                (HOLDER_SPECIFIC_IDENTIFIER, "2", "1", "foo", "1-2-3", "1.5");
        this.uut.preProcessRequest(obApiRequestContextMock); // test empty string, should return true
        this.uut.preProcessRequest(obApiRequestContextMock); // test "2", should return true

        this.uut.preProcessRequest(obApiRequestContextMock); // test "1", should return false
        this.uut.preProcessRequest(obApiRequestContextMock); // test "foo", should return false
        this.uut.preProcessRequest(obApiRequestContextMock); // test "1-2-3", should return false
        this.uut.preProcessRequest(obApiRequestContextMock); // test "1.5", should return false

        verify(obApiRequestContextMock, times(4)).setError(true);
    }

    private OBAPIRequestContext getOBAPIRequestContext(Map<String, Object> extensions, String key, String... values) {
        Map<String, String> headers = mock(Map.class);
        when(headers.get(CDSGatewayConstants.X_CDS_CLIENT_HEADERS)).thenReturn(VALID_CLIENT_HEADERS);
        when(headers.get(CDSGatewayConstants.X_FAPI_AUTH_DATE)).thenReturn(VALID_AUTH_DATE);
        when(headers.get(CDSGatewayConstants.X_FAPI_CUSTOMER_IP_ADDRESS)).thenReturn(VALID_CUSTOMER_IP6_ADDR);
        when(headers.get(CDSGatewayConstants.X_FAPI_INTERACTION_ID)).thenReturn(VALID_INTERACTION_ID);
        when(headers.get(CDSGatewayConstants.MAX_REQUESTED_ENDPOINT_VERSION)).thenReturn("1");
        when(headers.get(HttpHeaders.AUTHORIZATION)).thenReturn("test-access-token");

        if (StringUtils.isNotBlank(key)) {
            when(headers.get(key)).thenReturn(StringUtils.EMPTY, values);
        }

        MsgInfoDTO msgInfoDTOMock = mock(MsgInfoDTO.class);
        when(msgInfoDTOMock.getHeaders()).thenReturn(headers);
        when(msgInfoDTOMock.getElectedResource())
                .thenReturn("/test-endpoint");
        when(msgInfoDTOMock.getHttpMethod())
                .thenReturn(CDSGatewayConstants.HTTP_GET)
                .thenReturn(CDSGatewayConstants.HTTP_POST);

        OBAPIRequestContext obApiRequestContextMock = mock(OBAPIRequestContext.class);
        when(obApiRequestContextMock.getMsgInfo()).thenReturn(msgInfoDTOMock);

        mockElectedResourceVersion(obApiRequestContextMock, extensions);

        return obApiRequestContextMock;
    }

    private OBAPIRequestContext getOBAPIRequestContext(String key, String... values) {
        Map<String, Object> extensions = new HashMap<>();
        extensions.put(CDSGatewayConstants.X_VERSION, "2");

        return getOBAPIRequestContext(extensions, key, values);
    }

    private void mockElectedResourceVersion(OBAPIRequestContext obApiRequestContextMock,
                                            Map<String, Object> extensions) {
        Operation getOperationMock = mock(Operation.class);
        when(getOperationMock.getExtensions()).thenReturn(extensions);

        Map<String, Object> postExtensions = new HashMap<>();
        postExtensions.put(CDSGatewayConstants.X_VERSION, "2,3,4");
        Operation postOperationMock = mock(Operation.class);
        when(postOperationMock.getExtensions()).thenReturn(postExtensions);

        PathItem pathItemMock = mock(PathItem.class);
        when(pathItemMock.getGet()).thenReturn(getOperationMock);
        when(pathItemMock.getPost()).thenReturn(postOperationMock);

        Paths paths = mock(Paths.class);
        when(paths.get("/test-endpoint")).thenReturn(pathItemMock);
        OpenAPI openAPIMock = mock(OpenAPI.class);
        when(openAPIMock.getPaths()).thenReturn(paths);

        when(obApiRequestContextMock.getOpenAPI()).thenReturn(openAPIMock);
    }
}
