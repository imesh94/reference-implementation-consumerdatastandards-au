/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.identity.listener;

import com.wso2.openbanking.accelerator.common.config.OpenBankingConfigParser;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonConstants;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth2.dto.OAuth2IntrospectionResponseDTO;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@PowerMockIgnore({"jdk.internal.reflect.*"})
@PrepareForTest({OpenBankingConfigParser.class})
public class CDSIntrospectionDataProviderTest extends PowerMockTestCase {

    @Mock
    OpenBankingConfigParser openBankingConfigParser;

    @Test(description = "Test additional data setting to introspection response")
    public void testGetCdrArrangementIdFromScopes() throws Exception {

        Map<String, Object> configMap = new HashMap<>();
        configMap.put(IdentityCommonConstants.CONSENT_ID_CLAIM_NAME, "consent_id");
        PowerMockito.mockStatic(OpenBankingConfigParser.class);
        PowerMockito.when(OpenBankingConfigParser.getInstance()).thenReturn(openBankingConfigParser);
        PowerMockito.when(OpenBankingConfigParser.getInstance()
                .getConfiguration()).thenReturn(configMap);

        String sampleConsentId = "ConsentId";
        OAuth2IntrospectionResponseDTO oAuth2IntrospectionResponseDTO =
                Mockito.mock(OAuth2IntrospectionResponseDTO.class);
        Mockito.when(oAuth2IntrospectionResponseDTO.getScope())
                .thenReturn("consent_id" + sampleConsentId + " sampleScope");

        Method method = CDSIntrospectionDataProvider.class
                .getDeclaredMethod("getCdrArrangementIdFromScopes",
                        OAuth2IntrospectionResponseDTO.class);
        method.setAccessible(true);

        String cdrArrangementId = (String) method.invoke(
                PowerMockito.spy(new CDSIntrospectionDataProvider()),
                oAuth2IntrospectionResponseDTO);
        Assert.assertNotNull(cdrArrangementId);
        Assert.assertEquals(sampleConsentId, cdrArrangementId);
    }
}
