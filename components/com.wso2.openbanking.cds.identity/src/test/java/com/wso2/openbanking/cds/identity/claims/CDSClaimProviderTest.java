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
package com.wso2.openbanking.cds.identity.claims;

import com.wso2.openbanking.cds.identity.claims.utils.CDSClaimProviderUtil;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth.cache.SessionDataCache;
import org.wso2.carbon.identity.oauth.cache.SessionDataCacheEntry;
import org.wso2.carbon.identity.oauth2.authz.OAuthAuthzReqMessageContext;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AuthorizeReqDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AuthorizeRespDTO;
import org.wso2.carbon.identity.oauth2.model.OAuth2Parameters;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;

import java.util.Map;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({SessionDataCache.class, CDSClaimProviderUtil.class})
public class CDSClaimProviderTest extends PowerMockTestCase {

    private CDSClaimProvider cdsClaimProvider;

    @BeforeClass
    public void beforeClass() {

        cdsClaimProvider = new CDSClaimProvider();
    }

    @Test
    public void getAdditionalClaimsAuthFlowSuccess() throws Exception {

        OAuth2AuthorizeReqDTO oAuth2AuthorizeReqDTO = new OAuth2AuthorizeReqDTO();
        oAuth2AuthorizeReqDTO.setSessionDataKey("DummySessionDataKey");
        OAuthAuthzReqMessageContext oAuthAuthzReqMessageContext =
                new OAuthAuthzReqMessageContext(oAuth2AuthorizeReqDTO);
        oAuthAuthzReqMessageContext.setCodeIssuedTime(1672782);
        oAuthAuthzReqMessageContext.setAccessTokenIssuedTime(1672432);
        oAuthAuthzReqMessageContext.setRefreshTokenvalidityPeriod(7776000);
        OAuth2AuthorizeRespDTO oAuth2AuthorizeRespDTOMock = mock(OAuth2AuthorizeRespDTO.class);

        mockStatic(SessionDataCache.class);
        SessionDataCache sessionDataCacheMock = mock(SessionDataCache.class);
        when(SessionDataCache.getInstance()).thenReturn(sessionDataCacheMock);
        SessionDataCacheEntry sessionDataCacheEntryMock = mock(SessionDataCacheEntry.class);
        when(sessionDataCacheMock.getValueFromCache(Mockito.anyObject())).thenReturn(sessionDataCacheEntryMock);
        OAuth2Parameters oAuth2ParametersMock = mock(OAuth2Parameters.class);
        when(sessionDataCacheEntryMock.getoAuth2Parameters()).thenReturn(oAuth2ParametersMock);
        when(oAuth2ParametersMock.getState()).thenReturn("DummyStateValue");

        mockStatic(CDSClaimProviderUtil.class);
        when(CDSClaimProviderUtil.getHashValue(Mockito.anyString(), Mockito.anyString())).thenReturn("DummyHashValue");
        when(CDSClaimProviderUtil.getEpochDateTime(Mockito.anyLong())).thenReturn((long) 7776000);

        Map<String, Object> results = cdsClaimProvider
                .getAdditionalClaims(oAuthAuthzReqMessageContext, oAuth2AuthorizeRespDTOMock);
        Assert.assertEquals(results.get("s_hash"), "DummyHashValue");
        Assert.assertEquals(results.get("nbf"), (long) 1672432);
        Assert.assertEquals(results.get("auth_time"), (long) 1672782);
        Assert.assertEquals(results.get("sharing_expires_at"), (long) 7776000);
        Assert.assertEquals(results.get("refresh_token_expires_at"), (long) 7776000);
    }

    @Test
    public void getAdditionalClaimsTokenFlowSuccess() throws Exception {

        OAuth2AccessTokenReqDTO oAuth2AccessTokenReqDTO = mock(OAuth2AccessTokenReqDTO.class);
        OAuthTokenReqMessageContext oAuthTokenReqMessageContext =
                new OAuthTokenReqMessageContext(oAuth2AccessTokenReqDTO);
        oAuthTokenReqMessageContext.setRefreshTokenvalidityPeriod(7776000);
        OAuth2AccessTokenRespDTO oAuth2AccessTokenRespDTO = new OAuth2AccessTokenRespDTO();
        oAuth2AccessTokenRespDTO.addParameter("cdr_arrangement_id", "DummyConsentId");

        mockStatic(CDSClaimProviderUtil.class);
        when(CDSClaimProviderUtil.getEpochDateTime(Mockito.anyLong())).thenReturn((long) 7776000);

        Map<String, Object> results = cdsClaimProvider
                .getAdditionalClaims(oAuthTokenReqMessageContext, oAuth2AccessTokenRespDTO);

        Assert.assertEquals(results.get("cdr_arrangement_id"), "DummyConsentId");
        Assert.assertEquals(results.get("sharing_expires_at"), (long) 7776000);
        Assert.assertEquals(results.get("refresh_token_expires_at"), (long) 7776000);
    }
}