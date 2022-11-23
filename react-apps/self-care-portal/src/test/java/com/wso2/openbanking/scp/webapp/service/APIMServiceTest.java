/*
 * Copyright (c) 2021-2022, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.scp.webapp.service;

import com.wso2.openbanking.scp.webapp.util.Constants;
import com.wso2.openbanking.scp.webapp.util.Utils;
import org.apache.http.HttpHeaders;
import org.mockito.Mockito;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.databridge.commons.exception.SessionTimeoutException;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class APIMServiceTest extends PowerMockTestCase {

    private APIMService uut;

    @BeforeClass
    public void init() {
        this.uut = new APIMService();
    }

    @Test(description = "when valid req, then return access token")
    public void testConstructAccessTokenFromCookiesWithValidReq() {
        // mock
        HttpServletRequest reqMock = Mockito.mock(HttpServletRequest.class);

        // when
        Cookie cookie1 = new Cookie(Constants.ACCESS_TOKEN_COOKIE_NAME + "_P1", "dummy-cookie-p1");
        Cookie cookie2 = new Cookie(Constants.ACCESS_TOKEN_COOKIE_NAME + "_P2", "dummy-cookie-p2");

        Mockito.when(reqMock.getCookies()).thenReturn(new Cookie[]{cookie1, cookie2});
        Mockito.when(reqMock.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("dummy-cookie-p1");

        // assert
        Optional<String> optAccessToken = uut.constructAccessTokenFromCookies(reqMock);
        Assert.assertTrue(optAccessToken.isPresent());
        Assert.assertEquals(optAccessToken.get(), "dummy-cookie-p1dummy-cookie-p2");
    }

    @Test(description = "when invalid req, then return empty string")
    public void testConstructAccessTokenFromCookiesWithInvalidReq() {
        // mock
        HttpServletRequest reqMock = Mockito.mock(HttpServletRequest.class);

        // when
        Mockito.when(reqMock.getCookies()).thenReturn(new Cookie[]{});

        // assert
        Optional<String> optAccessToken = uut.constructAccessTokenFromCookies(reqMock);
        Assert.assertFalse(optAccessToken.isPresent());
    }

    @Test(description = "when valid req, then return refresh token")
    public void testConstructRefreshTokenFromCookiesWithValidReq() {
        // mock
        HttpServletRequest reqMock = Mockito.mock(HttpServletRequest.class);

        // when
        Cookie cookie1 = new Cookie(Constants.REFRESH_TOKEN_COOKIE_NAME + "_P1", "dummy-cookie-p1");
        Cookie cookie2 = new Cookie(Constants.REFRESH_TOKEN_COOKIE_NAME + "_P2", "dummy-cookie-p2");

        Mockito.when(reqMock.getCookies()).thenReturn(new Cookie[]{cookie1, cookie2});

        // assert
        Optional<String> optAccessToken = uut.constructRefreshTokenFromCookies(reqMock);
        Assert.assertTrue(optAccessToken.isPresent());
        Assert.assertEquals(optAccessToken.get(), "dummy-cookie-p1dummy-cookie-p2");
    }

    @Test(description = "when invalid req, then return empty string")
    public void testConstructRefreshTokenFromCookiesWithInvalidReq() {
        // mock
        HttpServletRequest reqMock = Mockito.mock(HttpServletRequest.class);

        // when
        Mockito.when(reqMock.getCookies()).thenReturn(new Cookie[]{});

        // assert
        Optional<String> optAccessToken = uut.constructAccessTokenFromCookies(reqMock);
        Assert.assertFalse(optAccessToken.isPresent());
    }

    @Test(description = "if access token is not expired return false")
    public void testIsAccessTokenExpired() throws SessionTimeoutException {
        // mock
        HttpServletRequest reqMock = Mockito.mock(HttpServletRequest.class);

        // when
        LocalDateTime futureDate = LocalDateTime.now().plusSeconds(3600);
        Cookie cookie = new Cookie(Constants.TOKEN_VALIDITY_COOKIE_NAME,
                Utils.formatDateToEncodedString(futureDate));

        Mockito.when(reqMock.getCookies()).thenReturn(new Cookie[]{cookie});

        // assert
        Assert.assertFalse(uut.isAccessTokenExpired(reqMock));
    }

    @Test(description = "if access token is expired return true")
    public void testIsAccessTokenExpiredWithExpiredToken() throws SessionTimeoutException {
        // mock
        HttpServletRequest reqMock = Mockito.mock(HttpServletRequest.class);

        // when
        LocalDateTime oldDate = LocalDateTime.now().minus(Period.ofDays(1));
        Cookie cookie = new Cookie(Constants.TOKEN_VALIDITY_COOKIE_NAME,
                Utils.formatDateToEncodedString(oldDate));

        Mockito.when(reqMock.getCookies()).thenReturn(new Cookie[]{cookie});

        // assert
        Assert.assertTrue(uut.isAccessTokenExpired(reqMock));
    }

    @Test(description = "if validity token is invalid throw SessionTimeoutException",
            expectedExceptions = SessionTimeoutException.class)
    public void testIsAccessTokenExpiredWithInvalidToken() throws SessionTimeoutException {
        // mock
        HttpServletRequest reqMock = Mockito.mock(HttpServletRequest.class);

        // when
        Cookie cookie = new Cookie(Constants.TOKEN_VALIDITY_COOKIE_NAME, "invalid-date");

        Mockito.when(reqMock.getCookies()).thenReturn(new Cookie[]{cookie});

        // assert
        uut.isAccessTokenExpired(reqMock);
    }

}
