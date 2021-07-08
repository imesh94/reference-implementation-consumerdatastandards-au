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
package com.wso2.openbanking.cds.identity.auth.extensions.response.handler;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonUtil;
import com.wso2.openbanking.cds.identity.utils.CDSIdentityUtil;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth2.authz.OAuthAuthzReqMessageContext;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AuthorizeReqDTO;

import java.util.Arrays;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({CDSIdentityUtil.class, IdentityCommonUtil.class})
public class CDSResponseTypeHandlerImplTest extends PowerMockTestCase {

    private CDSResponseTypeHandlerImpl cdsResponseTypeHandler;

    @BeforeClass
    public void beforeClass() {

        cdsResponseTypeHandler = new CDSResponseTypeHandlerImpl();
    }

    @Test
    public void updateApprovedScopesSuccess() throws Exception {

        OAuth2AuthorizeReqDTO oAuth2AuthorizeReqDTO = new OAuth2AuthorizeReqDTO();
        oAuth2AuthorizeReqDTO.setConsumerKey("DummyClientId");
        OAuthAuthzReqMessageContext oAuthAuthzReqMessageContext =
                new OAuthAuthzReqMessageContext(oAuth2AuthorizeReqDTO);
        String[] scopeArray = {"openid", "profile", "bank:accounts.basic:read", "bank:accounts.detail:read"};
        oAuthAuthzReqMessageContext.setApprovedScope(scopeArray);

        mockStatic(CDSIdentityUtil.class);
        when(CDSIdentityUtil.getCommonAuthId(Mockito.anyObject())).thenReturn("DummyCommonAuthId");
        when(CDSIdentityUtil.getConsentIdWithCommonAuthId(Mockito.anyString())).thenReturn("DummyConsentId");
        mockStatic(IdentityCommonUtil.class);
        when(IdentityCommonUtil.getRegulatoryFromSPMetaData(Mockito.anyString())).thenReturn(true);

        String[] updatedScopes = cdsResponseTypeHandler.updateApprovedScopes(oAuthAuthzReqMessageContext);

        Assert.assertTrue(Arrays.asList(updatedScopes).contains("OB_CONSENT_ID_DummyConsentId"));
    }

    @Test
    public void updateApprovedScopesNullAuthReqDTO() {

        OAuthAuthzReqMessageContext oAuthAuthzReqMessageContext = new OAuthAuthzReqMessageContext(null);

        String[] updatedScopes = cdsResponseTypeHandler.updateApprovedScopes(oAuthAuthzReqMessageContext);
        Assert.assertTrue(Arrays.asList(updatedScopes).isEmpty());
    }

    @Test
    public void updateApprovedScopesNonRegFlow() throws Exception {

        OAuth2AuthorizeReqDTO oAuth2AuthorizeReqDTO = new OAuth2AuthorizeReqDTO();
        oAuth2AuthorizeReqDTO.setConsumerKey("DummyClientId");
        OAuthAuthzReqMessageContext oAuthAuthzReqMessageContext =
                new OAuthAuthzReqMessageContext(oAuth2AuthorizeReqDTO);
        String[] scopeArray = {"openid", "consentmgt", "bank:accounts.basic:read", "bank:accounts.detail:read"};
        oAuthAuthzReqMessageContext.setApprovedScope(scopeArray);

        mockStatic(CDSIdentityUtil.class);
        when(CDSIdentityUtil.getCommonAuthId(Mockito.anyObject())).thenReturn("DummyCommonAuthId");
        mockStatic(IdentityCommonUtil.class);
        when(IdentityCommonUtil.getRegulatoryFromSPMetaData(Mockito.anyString())).thenThrow(OpenBankingException.class);

        String[] updatedScopes = cdsResponseTypeHandler.updateApprovedScopes(oAuthAuthzReqMessageContext);
        Assert.assertTrue(!Arrays.asList(updatedScopes).contains("OB_CONSENT_ID_DummyConsentId"));
    }

    @Test
    public void updateRefreshTokenValidityPeriodSuccess() {

        OAuth2AuthorizeReqDTO oAuth2AuthorizeReqDTO = new OAuth2AuthorizeReqDTO();
        OAuthAuthzReqMessageContext oAuthAuthzReqMessageContext =
                new OAuthAuthzReqMessageContext(oAuth2AuthorizeReqDTO);

        mockStatic(CDSIdentityUtil.class);
        when(CDSIdentityUtil.getCommonAuthId(Mockito.anyObject())).thenReturn("DummyCommonAuthId");
        when(CDSIdentityUtil.getRefreshTokenValidityPeriod(Mockito.anyString())).thenReturn((long) 7776000);
        when(CDSIdentityUtil.getConsentIdWithCommonAuthId(Mockito.anyString())).thenReturn("DummyConsentId");

        long refreshTokenValidityPeriod = cdsResponseTypeHandler
                .updateRefreshTokenValidityPeriod(oAuthAuthzReqMessageContext);
        Assert.assertEquals(refreshTokenValidityPeriod, 7776000);
    }

    @Test
    public void updateRefreshTokenValidityPeriodWithZeroSharing() {

        OAuth2AuthorizeReqDTO oAuth2AuthorizeReqDTO = new OAuth2AuthorizeReqDTO();
        OAuthAuthzReqMessageContext oAuthAuthzReqMessageContext =
                new OAuthAuthzReqMessageContext(oAuth2AuthorizeReqDTO);

        mockStatic(CDSIdentityUtil.class);
        when(CDSIdentityUtil.getCommonAuthId(Mockito.anyObject())).thenReturn("DummyCommonAuthId");
        when(CDSIdentityUtil.getRefreshTokenValidityPeriod(Mockito.anyString())).thenReturn((long) 0);
        when(CDSIdentityUtil.getConsentIdWithCommonAuthId(Mockito.anyString())).thenReturn("DummyConsentId");

        long refreshTokenValidityPeriod = cdsResponseTypeHandler
                .updateRefreshTokenValidityPeriod(oAuthAuthzReqMessageContext);
        Assert.assertEquals(refreshTokenValidityPeriod, oAuthAuthzReqMessageContext.getRefreshTokenvalidityPeriod());
    }
}
