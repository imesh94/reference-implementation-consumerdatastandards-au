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
package com.wso2.openbanking.cds.identity.authenticator;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth2.bean.OAuthClientAuthnContext;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertTrue;

@PrepareForTest({OAuth2Util.class, OAuthServerConfiguration.class, CDSTokenPrivateKeyJWTClientAuthenticator.class})
public class CDSTokenPrivateKeyJWTClientAuthenticatorTest extends PowerMockTestCase {

    @BeforeClass
    public void beforeClass() {
    }

    @BeforeMethod
    public void beforeMethod() {
    }

    @Test(description = "Test whether can authenticate is engaged for token request")
    public void canAuthenticateTest() throws Exception {

        OAuthClientAuthnContext clientAuthnContext = new OAuthClientAuthnContext();
        OAuthServerConfiguration oAuthServerConfigurationMock = Mockito.mock(OAuthServerConfiguration.class);
        PowerMockito.mockStatic(OAuthServerConfiguration.class);
        when(OAuthServerConfiguration.getInstance()).thenReturn(oAuthServerConfigurationMock);
        MockHttpServletRequest request = new MockHttpServletRequest();
        PowerMockito.mockStatic(OAuth2Util.class);
        CDSTokenPrivateKeyJWTClientAuthenticator authenticator =
                PowerMockito.spy(new CDSTokenPrivateKeyJWTClientAuthenticator());
        Map<String, List> bodyParams = new HashMap<>();
        request.setRequestURI("baseUri/token");
        PowerMockito.doReturn(true).when(authenticator, "canSuperAuthenticate",
                any(MockHttpServletRequest.class), any(Map.class), any(OAuthClientAuthnContext.class));
        boolean response = authenticator.canAuthenticate(request, bodyParams, clientAuthnContext);
        assertTrue(response);
    }
}
