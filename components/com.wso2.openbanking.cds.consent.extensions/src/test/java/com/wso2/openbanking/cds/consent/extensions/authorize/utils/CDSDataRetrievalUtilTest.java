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

package com.wso2.openbanking.cds.consent.extensions.authorize.utils;

import com.wso2.openbanking.accelerator.identity.push.auth.extension.request.validator.util.PushAuthRequestValidatorUtils;
import com.wso2.openbanking.cds.consent.extensions.util.CDSConsentAuthorizeTestConstants;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth.cache.SessionDataCache;
import org.wso2.carbon.identity.oauth.cache.SessionDataCacheEntry;
import org.wso2.carbon.identity.oauth2.model.OAuth2Parameters;

import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test class for CDSDataRetrievalUtil
 */
@PrepareForTest({SessionDataCacheEntry.class, SessionDataCache.class, PushAuthRequestValidatorUtils.class})
public class CDSDataRetrievalUtilTest extends PowerMockTestCase {

    private static final String SP_QUERY_PARAMS = "redirect_uri=https://www.google.com/redirects/redirect1&" +
            "request=requst-object&client_id=client-id";
    private static final String SP_QUERY_PARAMS_WITH_REQUEST_URI = "redirect_uri=https://www.google.com/" +
            "redirects/redirect1&" + "request_uri=" + "urn:ietf:params:oauth:request_uri:" +
            "XKnDFSbXJWjuf0AY6gOT1EIuvdP8BQLo";
    private static final String SCOPES = "common:customer.basic:read common:customer.detail:read openid profile";


    @BeforeClass
    public void initClass() {

    }

    @Test
    public void testExtractRequestObject() {
        String requestObject = CDSDataRetrievalUtil.extractRequestObject(SP_QUERY_PARAMS);
        Assert.assertNotNull(requestObject);
    }

    @Test
    public void testgetRedirectURL() {
        String redirectUrl = CDSDataRetrievalUtil.getRedirectURL(SP_QUERY_PARAMS);
        Assert.assertNotNull(redirectUrl);
    }

    @Test
    public void testgetPermissionList() {
        List<PermissionsEnum> permissionList = CDSDataRetrievalUtil.getPermissionList(SCOPES);
        Assert.assertNotNull(permissionList);
    }

    @Test
    public void testExtractRequestObjectWithRequestUri() {

        String requestObjectString = CDSConsentAuthorizeTestConstants.VALID_REQUEST_OBJECT;
        OAuth2Parameters oAuth2Parameters = new OAuth2Parameters();
        oAuth2Parameters.setEssentialClaims(requestObjectString + ":" + "3600666666");

        SessionDataCache sessionDataCacheMock = mock(SessionDataCache.class);
        SessionDataCacheEntry sessionDataCacheEntry = new SessionDataCacheEntry();
        mockStatic(SessionDataCacheEntry.class);
        mockStatic(SessionDataCache.class);
        when(SessionDataCache.getInstance()).thenReturn(sessionDataCacheMock);
        when(sessionDataCacheMock.getValueFromCache(Mockito.anyObject())).thenReturn(sessionDataCacheEntry);

        sessionDataCacheEntry.setoAuth2Parameters(oAuth2Parameters);

        String requestObject = CDSDataRetrievalUtil.extractRequestObject(SP_QUERY_PARAMS_WITH_REQUEST_URI);
        Assert.assertNotNull(requestObject);
    }

    @Test
    public void testExtractRequestObjectWithEncryptedReqObj() throws Exception {

        String requestObjectString = CDSConsentAuthorizeTestConstants.ENCRYPTED_JWT;
        OAuth2Parameters oAuth2Parameters = new OAuth2Parameters();
        oAuth2Parameters.setEssentialClaims(requestObjectString + ":" + "3600666666");

        SessionDataCache sessionDataCacheMock = mock(SessionDataCache.class);
        SessionDataCacheEntry sessionDataCacheEntry = new SessionDataCacheEntry();
        mockStatic(SessionDataCacheEntry.class);
        mockStatic(SessionDataCache.class);
        mockStatic(PushAuthRequestValidatorUtils.class);
        when(SessionDataCache.getInstance()).thenReturn(sessionDataCacheMock);
        when(sessionDataCacheMock.getValueFromCache(Mockito.anyObject())).thenReturn(sessionDataCacheEntry);
        when(PushAuthRequestValidatorUtils.decrypt(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(CDSConsentAuthorizeTestConstants.VALID_REQUEST_OBJECT);

        sessionDataCacheEntry.setoAuth2Parameters(oAuth2Parameters);

        String requestObject = CDSDataRetrievalUtil.extractRequestObject(SP_QUERY_PARAMS_WITH_REQUEST_URI);
        Assert.assertNotNull(requestObject);
    }
}