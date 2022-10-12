/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.gateway.throttling;

import com.wso2.openbanking.accelerator.gateway.internal.GatewayDataHolder;
import com.wso2.openbanking.accelerator.gateway.throttling.OBThrottlingExtensionImpl;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.common.utils.CDSCommonUtils;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.apimgt.common.gateway.dto.ExtensionResponseDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.MsgInfoDTO;
import org.wso2.carbon.apimgt.common.gateway.dto.RequestContextDTO;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doReturn;

/**
 * CDSThrottleDataPublisherImpl Test
 */
@PrepareForTest({OpenBankingCDSConfigParser.class, CDSCommonUtils.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class CDSThrottleDataPublisherImplTest extends PowerMockTestCase {

    private static final String X_FAPI_CUSTOMER_IP_ADDRESS = "x-fapi-customer-ip-address";
    private static final String CUSTOMER_STATUS = "customerStatus";
    private static final String CUSTOMER_PRESENT_STATUS = "customerPresent";
    private static final String UNATTENDED_STATUS = "unattended";
    private static final String AUTHORIZATION_STATUS = "authorizationStatus";
    private static final String SECURED_STATUS = "secured";
    private static final String PUBLIC_STATUS = "public";
    private static final String NULL_STRING = "null";
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_HEADER = "authorizationHeader";
    private static final String ENCRYPTED_TOKEN = "encrypted-token";
    private static final String SAMPLE_AUTH_HEADER = "Bearer some-string";
    private static final String SAMPLE_IP_ADDRESS = "192.168.1.1";

    OpenBankingCDSConfigParser openBankingCDSConfigParserMock;
    CDSCommonUtils cdsCommonUtilsMock;
    CDSThrottleDataPublisherImpl cdsThrottleDataPublisher;
    OBThrottlingExtensionImpl obThrottlingExtension;
    RequestContextDTO requestContextDTO;
    MsgInfoDTO msgInfoDTO;

    @BeforeClass
    public void beforeClass() {

        cdsThrottleDataPublisher = new CDSThrottleDataPublisherImpl();
        GatewayDataHolder.getInstance().setThrottleDataPublisher(cdsThrottleDataPublisher);
        obThrottlingExtension = new OBThrottlingExtensionImpl();
        requestContextDTO = Mockito.mock(RequestContextDTO.class);
        msgInfoDTO = Mockito.mock(MsgInfoDTO.class);

    }

    @Test
    public void testGetCustomPropertiesWithAuthHeader() {

        openBankingCDSConfigParserMock = PowerMockito.mock(OpenBankingCDSConfigParser.class);
        cdsCommonUtilsMock = PowerMockito.mock(CDSCommonUtils.class);
        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        PowerMockito.mockStatic(CDSCommonUtils.class);
        PowerMockito.when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        PowerMockito.when(CDSCommonUtils.encryptAccessToken(Mockito.anyString())).thenReturn(ENCRYPTED_TOKEN);
        doReturn(true).when(openBankingCDSConfigParserMock).isTokenEncryptionEnabled();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(AUTHORIZATION, SAMPLE_AUTH_HEADER);
        Mockito.when(requestContextDTO.getMsgInfo()).thenReturn(msgInfoDTO);
        Mockito.when(msgInfoDTO.getHeaders()).thenReturn(headerMap);
        ExtensionResponseDTO extensionResponseDTO = obThrottlingExtension.preProcessRequest(requestContextDTO);

        Assert.assertEquals(extensionResponseDTO.getCustomProperty().get(AUTHORIZATION_HEADER), ENCRYPTED_TOKEN);
        Assert.assertEquals(extensionResponseDTO.getCustomProperty().get(AUTHORIZATION_STATUS), SECURED_STATUS);
    }

    @Test
    public void testGetCustomPropertiesWithCustomerIp() {

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(X_FAPI_CUSTOMER_IP_ADDRESS, SAMPLE_IP_ADDRESS);
        Mockito.when(requestContextDTO.getMsgInfo()).thenReturn(msgInfoDTO);
        Mockito.when(msgInfoDTO.getHeaders()).thenReturn(headerMap);
        ExtensionResponseDTO extensionResponseDTO = obThrottlingExtension.preProcessRequest(requestContextDTO);

        Assert.assertEquals(extensionResponseDTO.getCustomProperty().get(X_FAPI_CUSTOMER_IP_ADDRESS),
                SAMPLE_IP_ADDRESS);
        Assert.assertEquals(extensionResponseDTO.getCustomProperty().get(CUSTOMER_STATUS), CUSTOMER_PRESENT_STATUS);
    }

    @Test
    public void testGetCustomPropertiesWithoutAuthHeaderOrIP() {

        Map<String, String> headerMap = new HashMap<>();
        Mockito.when(requestContextDTO.getMsgInfo()).thenReturn(msgInfoDTO);
        Mockito.when(msgInfoDTO.getHeaders()).thenReturn(headerMap);
        ExtensionResponseDTO extensionResponseDTO = obThrottlingExtension.preProcessRequest(requestContextDTO);

        Assert.assertEquals(extensionResponseDTO.getCustomProperty().get(AUTHORIZATION_HEADER), NULL_STRING);
        Assert.assertEquals(extensionResponseDTO.getCustomProperty().get(AUTHORIZATION_STATUS), PUBLIC_STATUS);
        Assert.assertEquals(extensionResponseDTO.getCustomProperty().get(X_FAPI_CUSTOMER_IP_ADDRESS), NULL_STRING);
        Assert.assertEquals(extensionResponseDTO.getCustomProperty().get(CUSTOMER_STATUS), UNATTENDED_STATUS);
    }

}
