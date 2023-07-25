/*
 * Copyright (c) 2021-2022, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.identity.authenticator;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
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

/**
 * Test class for CDS Introspection Private Key JWT Authenticator.
 */
@PrepareForTest({OAuth2Util.class, OAuthServerConfiguration.class,
        CDSIntrospectionPrivateKeyJWTClientAuthenticator.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class CDSIntrospectionPrivateKeyJWTClientAuthenticatorTest extends PowerMockTestCase {

    @BeforeClass
    public void beforeClass() {
    }

    @BeforeMethod
    public void beforeMethod() {
    }

    @Test(description = "Test whether can authenticate is engaged for token introspect request")
    public void canAuthenticateTest() throws Exception {

        OAuthClientAuthnContext clientAuthnContext = new OAuthClientAuthnContext();
        OAuthServerConfiguration oAuthServerConfigurationMock = Mockito.mock(OAuthServerConfiguration.class);
        PowerMockito.mockStatic(OAuthServerConfiguration.class);
        when(OAuthServerConfiguration.getInstance()).thenReturn(oAuthServerConfigurationMock);
        MockHttpServletRequest request = new MockHttpServletRequest();
        PowerMockito.mockStatic(OAuth2Util.class);
        CDSIntrospectionPrivateKeyJWTClientAuthenticator authenticator =
                PowerMockito.spy(new CDSIntrospectionPrivateKeyJWTClientAuthenticator());
        Map<String, List> bodyParams = new HashMap<>();
        request.setRequestURI("baseUri/introspect");
        PowerMockito.doReturn(true).when(authenticator, "canSuperAuthenticate",
                any(MockHttpServletRequest.class), any(Map.class), any(OAuthClientAuthnContext.class));
        boolean response = authenticator.canAuthenticate(request, bodyParams, clientAuthnContext);
        assertTrue(response);
    }
}
