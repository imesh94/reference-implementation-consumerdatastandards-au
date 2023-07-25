/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 *
 */

package com.wso2.openbanking.cds.identity.auth.extensions.response.handler;

import com.wso2.openbanking.accelerator.common.config.OpenBankingConfigParser;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonConstants;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonUtil;
import com.wso2.openbanking.cds.identity.utils.CDSIdentityUtil;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth2.authz.OAuthAuthzReqMessageContext;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AuthorizeReqDTO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test class for CDS Response Type Handler.
 */
@PrepareForTest({CDSIdentityUtil.class, IdentityCommonUtil.class, OpenBankingConfigParser.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class CDSResponseTypeHandlerImplTest extends PowerMockTestCase {

    private CDSResponseTypeHandlerImpl cdsResponseTypeHandler;
    private static OpenBankingConfigParser openBankingConfigParserMock;

    @BeforeClass
    public void beforeClass() {

        cdsResponseTypeHandler = new CDSResponseTypeHandlerImpl();
    }

    @Test
    public void updateApprovedScopesSuccess() throws Exception {

        Map<String, Object> configMap = new HashMap<>();
        configMap.put(IdentityCommonConstants.CONSENT_ID_CLAIM_NAME, "consent_id");

        PowerMockito.mockStatic(OpenBankingConfigParser.class);
        openBankingConfigParserMock = mock(OpenBankingConfigParser.class);
        PowerMockito.when(OpenBankingConfigParser.getInstance()).thenReturn(openBankingConfigParserMock);
        doReturn(configMap).when(openBankingConfigParserMock).getConfiguration();

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

        Assert.assertTrue(Arrays.asList(updatedScopes).contains("consent_idDummyConsentId"));
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
