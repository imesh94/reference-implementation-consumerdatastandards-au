/**
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.openbanking.cds.identity.grant.type.handlers;

import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth.cache.OAuthCache;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.OauthTokenIssuer;
import org.wso2.openbanking.cds.identity.utils.CDSIdentityUtil;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test class for CDS Authorization Code Grant Handler.
 */
@PrepareForTest({CDSIdentityUtil.class, OAuthServerConfiguration.class, OAuthCache.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class CDSAuthorizationCodeGrantHandlerTest extends PowerMockTestCase {

    @Test
    public void issueRefreshTokenTrue() throws Exception {

        getInitDetails();
        mockStatic(CDSIdentityUtil.class);
        when(CDSIdentityUtil.getRefreshTokenValidityPeriod(Mockito.anyString())).thenReturn((long) 350000);

        CDSAuthCodeGrantHandlerMock cdsAuthorizationCodeGrantHandler = new CDSAuthCodeGrantHandlerMock();
        boolean result = cdsAuthorizationCodeGrantHandler.issueRefreshToken();

        Assert.assertTrue(result);
    }

    @Test
    public void issueRefreshTokenFalse() throws Exception {

        getInitDetails();
        mockStatic(CDSIdentityUtil.class);
        when(CDSIdentityUtil.getRefreshTokenValidityPeriod(Mockito.anyString())).thenReturn((long) 0);

        CDSAuthCodeGrantHandlerMock cdsAuthorizationCodeGrantHandler = new CDSAuthCodeGrantHandlerMock();
        boolean result = cdsAuthorizationCodeGrantHandler.issueRefreshToken();

        Assert.assertFalse(result);
    }

    @Test
    public void executeInitialStepSuccess() throws Exception {

        getInitDetails();
        mockStatic(CDSIdentityUtil.class);
        when(CDSIdentityUtil.getRefreshTokenValidityPeriod(Mockito.anyString())).thenReturn((long) 0);
        when(CDSIdentityUtil.getConsentId(Mockito.any())).thenReturn("DummyConsentId");

        OAuth2AccessTokenRespDTO oAuth2AccessTokenRespDTO = new OAuth2AccessTokenRespDTO();
        OAuthTokenReqMessageContext tokReqMsgCtxMock = mock(OAuthTokenReqMessageContext.class);

        CDSAuthorizationCodeGrantHandler cdsAuthorizationCodeGrantHandler = new CDSAuthorizationCodeGrantHandler();
        cdsAuthorizationCodeGrantHandler.executeInitialStep(oAuth2AccessTokenRespDTO, tokReqMsgCtxMock);

        Assert.assertEquals(oAuth2AccessTokenRespDTO.getParameter("cdr_arrangement_id"), "DummyConsentId");
    }

    private void getInitDetails() {

        OAuthServerConfiguration oAuthServerConfigurationMock = mock(OAuthServerConfiguration.class);
        OauthTokenIssuer oauthTokenIssuerMock = mock(OauthTokenIssuer.class);
        mockStatic(OAuthServerConfiguration.class);
        when(OAuthServerConfiguration.getInstance()).thenReturn(oAuthServerConfigurationMock);
        when(oAuthServerConfigurationMock.getIdentityOauthTokenIssuer()).thenReturn(oauthTokenIssuerMock);

        mockStatic(OAuthCache.class);
        OAuthCache oAuthCacheMock = mock(OAuthCache.class);
        when(OAuthCache.getInstance()).thenReturn(oAuthCacheMock);
        when(oAuthCacheMock.isEnabled()).thenReturn(false);
    }
}

class CDSAuthCodeGrantHandlerMock extends CDSAuthorizationCodeGrantHandler {

    @Override
    protected OAuthTokenReqMessageContext getTokenMessageContext() {

        OAuthTokenReqMessageContext tokenReqMessageContextMock = mock(OAuthTokenReqMessageContext.class);
        return tokenReqMessageContextMock;
    }

    @Override
    protected boolean isRegulatory(OAuthTokenReqMessageContext tokenReqMessageContext) {

        return true;
    }
}
