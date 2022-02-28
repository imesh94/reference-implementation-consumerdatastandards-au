/*
 *  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 *  This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material contained
 *  herein is strictly forbidden, unless permitted by WSO2 in accordance with
 *  the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 *  language governing the permissions and limitations under this license,
 *  please see the license as well as any agreement you’ve entered into with
 *  WSO2 governing the purchase of this software and any associated services.
 *
 */

package com.wso2.openbanking.scp.webapp.service;

import com.wso2.openbanking.scp.webapp.exception.TokenGenerationException;
import com.wso2.openbanking.scp.webapp.util.Constants;
import com.wso2.openbanking.scp.webapp.util.Utils;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@PrepareForTest({Utils.class})
public class OAuthServiceTest extends PowerMockTestCase {

    private static final String IAM_BASE_URL = "http://localhost:9446";
    private static final String CLIENT_KEY = "dummy-client-key";
    private static final String CLIENT_SECRET = "dummy-client-secret";
    private static final String RESP_ACCESS_TOKEN = "dummy-access-token";
    private static final String RESP_ID_TOKEN = "dummy-id-token";
    private static final JSONObject TOKEN_RESPONSE_JSON = new JSONObject();
    OAuthService uut;

    @BeforeClass
    public void init() {
        uut = OAuthService.getInstance();
        TOKEN_RESPONSE_JSON.put(Constants.ACCESS_TOKEN, RESP_ACCESS_TOKEN);
        TOKEN_RESPONSE_JSON.put(Constants.ID_TOKEN, RESP_ID_TOKEN);
        TOKEN_RESPONSE_JSON.put(Constants.REFRESH_TOKEN, "dummy-refresh-token");
        TOKEN_RESPONSE_JSON.put(Constants.EXPIRES_IN, 3600);
    }

    @Test(description = "method should return an url with valid parameters")
    public void testGenerateAuthorizationUrl() throws URISyntaxException {
        String authUrl = uut.generateAuthorizationUrl("https://localhost:9446", "dummy-clientId");
        URI uri = new URI(authUrl);

        Assert.assertEquals(uri.getHost(), "localhost");
        Assert.assertEquals(uri.getPort(), 9446);

        Assert.assertTrue(uri.getQuery().contains("code"));
        Assert.assertTrue(uri.getQuery().contains("consentmgt"));
        Assert.assertTrue(uri.getQuery().contains("openid"));

    }

    @Test
    public void testSendAccessTokenRequest() throws TokenGenerationException, UnsupportedEncodingException {
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.sendTokenRequest(Mockito.any(HttpPost.class))).thenReturn(TOKEN_RESPONSE_JSON);

        JSONObject responseJson = uut.sendAccessTokenRequest(IAM_BASE_URL, CLIENT_KEY, CLIENT_SECRET, "dummy-code");
        Assert.assertEquals(responseJson.get(Constants.ACCESS_TOKEN), RESP_ACCESS_TOKEN);
        Assert.assertEquals(responseJson.get(Constants.ID_TOKEN), RESP_ID_TOKEN);
    }

    @Test
    public void testSendRefreshTokenRequest() throws TokenGenerationException, UnsupportedEncodingException {
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.sendTokenRequest(Mockito.any(HttpPost.class))).thenReturn(TOKEN_RESPONSE_JSON);

        JSONObject responseJson = uut
                .sendRefreshTokenRequest(IAM_BASE_URL, CLIENT_KEY, CLIENT_SECRET, "dummy-token", "dummy-scope");
        Assert.assertEquals(responseJson.get(Constants.ACCESS_TOKEN), RESP_ACCESS_TOKEN);
        Assert.assertEquals(responseJson.get(Constants.ID_TOKEN), RESP_ID_TOKEN);
    }

    @Test
    public void testGenerateCookiesFromTokens() {
        HttpServletResponse respMock = Mockito.mock(HttpServletResponse.class);
        HttpServletRequest reqMock = Mockito.mock(HttpServletRequest.class);
        Cookie cookie1 = new Cookie(Constants.ACCESS_TOKEN_COOKIE_NAME + "_P1", "dummy-cookie-1");
        Cookie cookie2 = new Cookie(Constants.COOKIE_BASE_NAME + "2", "dummy-cookie-2");

        // when
        Mockito.when(reqMock.getCookies()).thenReturn(new Cookie[]{cookie1, cookie2});

        uut.generateCookiesFromTokens(TOKEN_RESPONSE_JSON, reqMock, respMock);
        Mockito.verify(respMock, Mockito.times(7)).addCookie(Mockito.any(Cookie.class));
    }

    @Test
    public void testRemoveAllCookiesFromRequest() {
        // mock
        HttpServletRequest reqMock = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse respMock = Mockito.mock(HttpServletResponse.class);
        Cookie cookie1 = new Cookie(Constants.COOKIE_BASE_NAME + "1", "dummy-cookie-1");
        Cookie cookie2 = new Cookie(Constants.COOKIE_BASE_NAME + "2", "dummy-cookie-2");

        // when
        Mockito.when(reqMock.getCookies()).thenReturn(new Cookie[]{cookie1, cookie2});

        // assert
        uut.removeAllCookiesFromRequest(reqMock, respMock);
        Mockito.verify(respMock, Mockito.times(2)).addCookie(Mockito.any(Cookie.class));
    }
}
