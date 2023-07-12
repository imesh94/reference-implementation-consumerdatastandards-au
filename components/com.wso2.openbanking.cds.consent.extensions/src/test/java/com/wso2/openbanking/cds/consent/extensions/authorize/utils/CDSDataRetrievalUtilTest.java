/*
 * Copyright (c) 2021-2022, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.consent.extensions.authorize.utils;

import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.identity.push.auth.extension.request.validator.util.PushAuthRequestValidatorUtils;
import com.wso2.openbanking.cds.consent.extensions.util.CDSConsentAuthorizeTestConstants;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth.cache.SessionDataCache;
import org.wso2.carbon.identity.oauth.cache.SessionDataCacheEntry;
import org.wso2.carbon.identity.oauth2.model.OAuth2Parameters;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test class for CDSDataRetrievalUtil
 */
@PrepareForTest({SessionDataCacheEntry.class, SessionDataCache.class, PushAuthRequestValidatorUtils.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class CDSDataRetrievalUtilTest extends PowerMockTestCase {

    private static final String SP_QUERY_PARAMS = "redirect_uri=https://www.google.com/redirects/redirect1&" +
            "request=requst-object&client_id=client-id";
    private static final String SP_QUERY_PARAMS_WITH_REQUEST_URI = "redirect_uri=https://www.google.com/" +
            "redirects/redirect1&" + "request_uri=" + "urn:ietf:params:oauth:request_uri:" +
            "XKnDFSbXJWjuf0AY6gOT1EIuvdP8BQLo";
    private static final String SP_QUERY_PARAMS_WITH_STATE = "redirect_uri=https://www.google.com/redirects/" +
            "redirect1&request=requst-object&client_id=client-id&state=samplestate";
    private static final String SCOPES = "common:customer.basic:read common:customer.detail:read openid profile";
    private static final String VALID_RECEIPT =  "{\"accountData\":{\"permissions\":[\"CDRREADACCOUNTSBASIC\"]," +
            "\"expirationDateTime\": \"" + LocalDateTime.now(ZoneOffset.UTC).plusDays(1L) + "Z\"}}";

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
    public void testgetStateParameter() {
        String state = CDSDataRetrievalUtil.getStateParameter(SP_QUERY_PARAMS_WITH_STATE);
        Assert.assertNotNull(state);
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

    @Test
    public void testGetExpiryFromReceipt() {
        Assert.assertNotNull(CDSDataRetrievalUtil.getExpiryFromReceipt(VALID_RECEIPT));
    }

    @Test
    public void testGetExpiryFromReceiptWithInvalidReceipt() {
        try {
            CDSDataRetrievalUtil.getExpiryFromReceipt("{\"Invalid\":\"receipt}");
            Assert.fail("ConsentException Exception expected.");
        } catch (ConsentException e) {
            // ConsentException expected
        }
    }

    @Test
    public void testGetExpiryFromReceiptWithEmptyReceipt() {
        try {
            CDSDataRetrievalUtil.getExpiryFromReceipt("[]");
            Assert.fail("ConsentException Exception expected.");
        } catch (ConsentException e) {
            // ConsentException expected
        }
    }

    @Test
    public void testGetPermissionsFromReceipt() {
        Assert.assertEquals(1, CDSDataRetrievalUtil.getPermissionsFromReceipt(VALID_RECEIPT).size());
    }

    @Test
    public void testGetPermissionsFromReceiptWithEmptyReceipt() {
        try {
            CDSDataRetrievalUtil.getPermissionsFromReceipt("[]");
            Assert.fail("ConsentException Exception expected.");
        } catch (ConsentException e) {
            // ConsentException expected
        }
    }

    @Test
    public void testGetPermissionsFromReceiptWithInvalidReceipt() {
        try {
            CDSDataRetrievalUtil.getPermissionsFromReceipt("{\"Invalid\":\"receipt}");
            Assert.fail("ConsentException Exception expected.");
        } catch (ConsentException e) {
            // ConsentException expected
        }
    }
}
