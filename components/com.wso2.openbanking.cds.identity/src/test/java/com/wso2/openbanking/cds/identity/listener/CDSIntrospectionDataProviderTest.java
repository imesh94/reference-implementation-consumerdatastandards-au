/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.identity.listener;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth2.dto.OAuth2IntrospectionResponseDTO;

import java.lang.reflect.Method;
import java.util.Map;

public class CDSIntrospectionDataProviderTest {

    @Test(description = "Test additional data setting to introspection response")
    public void testMethod() throws Exception {

        String sampleConsentId = "ConsentId";
        String consentIdClaim = "consent_id";
        OAuth2IntrospectionResponseDTO oAuth2IntrospectionResponseDTO =
                Mockito.mock(OAuth2IntrospectionResponseDTO.class);
        Mockito.when(oAuth2IntrospectionResponseDTO.getScope())
                .thenReturn("consent_id" + sampleConsentId + " sampleScope");

        Method method = CDSIntrospectionDataProvider.class
                .getDeclaredMethod("getAdditionalDataForIntrospectResponse",
                        OAuth2IntrospectionResponseDTO.class, String.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) method.invoke(
                PowerMockito.spy(new CDSIntrospectionDataProvider()),
                oAuth2IntrospectionResponseDTO,
                consentIdClaim);
        Assert.assertNotNull(result.get("cdr_arrangement_id"));
        Assert.assertEquals(sampleConsentId, result.get("cdr_arrangement_id"));
    }
}
