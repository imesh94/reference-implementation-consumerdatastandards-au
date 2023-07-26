/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 *
 */

package com.wso2.openbanking.cds.gateway.executors.jwt.authentication;

import com.wso2.openbanking.accelerator.common.util.JWTUtils;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIRequestContext;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.gateway.executors.jwt.authentication.cache.JwtJtiCache;
import com.wso2.openbanking.cds.gateway.executors.jwt.authentication.util.JWTAuthenticationExecutorConstants;
import net.minidev.json.JSONObject;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.apimgt.common.gateway.dto.MsgInfoDTO;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * class comment.
 */
@PrepareForTest({ JWTUtils.class, OpenBankingCDSConfigParser.class, JwtJtiCache.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class JWTAuthenticationExecutorTest extends PowerMockTestCase {

    JWTAuthenticationExecutor jwtAuthenticationExecutor;
    OpenBankingCDSConfigParser openBankingCDSConfigParserMock;
    Map<String, String> headers = new HashMap<>();
    OBAPIRequestContext obApiRequestContextMock;
    private static ByteArrayOutputStream outContent;
    private static Logger logger = null;
    private static PrintStream printStream;

    @BeforeClass
    public void initClass() {

        jwtAuthenticationExecutor = Mockito.spy(JWTAuthenticatorExecutorMock.class);
        outContent = new ByteArrayOutputStream();
        printStream = new PrintStream(outContent);
        System.setOut(printStream);
        logger = LogManager.getLogger(JWTAuthenticationExecutorTest.class);
    }

    @Test
    public void testAuthHeaderMisingScenario() {

        outContent.reset();
        getInitialData();

        headers.put(HttpHeaders.AUTHORIZATION, null);
        when(obApiRequestContextMock.isError()).thenReturn(false);
        jwtAuthenticationExecutor.preProcessRequest(obApiRequestContextMock);
        Assert.assertTrue(outContent.toString().contains("Authorization header is null"));
    }

    @Test
    public void testJWKSUrlNullNullScenario() {

        outContent.reset();
        getInitialData();
        when(openBankingCDSConfigParserMock.getJWTAuthJWKSUrl()).thenReturn(null);

        headers.put(HttpHeaders.AUTHORIZATION, JWTAuthenticationExecutorConstants.JWT_TOKEN);
        when(obApiRequestContextMock.isError()).thenReturn(false);
        jwtAuthenticationExecutor.preProcessRequest(obApiRequestContextMock);
        Assert.assertTrue(outContent.toString().contains("JWT authentication jwks url is not configured."));
    }

    @Test
    public void testInvalidTokenScenario() {

        outContent.reset();
        getInitialData();
        when(openBankingCDSConfigParserMock.getJWTAuthJWKSUrl()).thenReturn("DummyJWKSUrl");

        headers.put(HttpHeaders.AUTHORIZATION, JWTAuthenticationExecutorConstants.INVALID_JWT_TOKEN);
        when(obApiRequestContextMock.isError()).thenReturn(false);
        jwtAuthenticationExecutor.preProcessRequest(obApiRequestContextMock);
        Assert.assertTrue(outContent.toString().contains("Error occurred while trying to authenticate. " +
                "The Authorization header values are not defined correctly."));
    }

    @Test
    public void testInvalidAudience() {

        outContent.reset();
        getInitialData();
        when(openBankingCDSConfigParserMock.getJWTAuthJWKSUrl()).thenReturn("DummyJWKSUrl");
        when(openBankingCDSConfigParserMock.getJWTAuthIssuer()).thenReturn("cdr-register");
        when(openBankingCDSConfigParserMock.getJWTAuthSubject()).thenReturn("cdr-register");
        when(openBankingCDSConfigParserMock.getJWTAuthAudience()).thenReturn("wrongAudience");

        headers.put(HttpHeaders.AUTHORIZATION, JWTAuthenticationExecutorConstants.JWT_TOKEN);
        when(obApiRequestContextMock.isError()).thenReturn(false);
        jwtAuthenticationExecutor.preProcessRequest(obApiRequestContextMock);
        Assert.assertTrue(outContent.toString().contains("JWT Token contains invalid audience"));
    }

    @Test
    public void testInvalidIssuer() {

        outContent.reset();
        getInitialData();
        when(openBankingCDSConfigParserMock.getJWTAuthJWKSUrl()).thenReturn("DummyJWKSUrl");
        when(openBankingCDSConfigParserMock.getJWTAuthIssuer()).thenReturn("wrongIssuer");
        when(openBankingCDSConfigParserMock.getJWTAuthSubject()).thenReturn("cdr-register");
        when(openBankingCDSConfigParserMock.getJWTAuthAudience()).thenReturn("https://wso2ob.com");

        headers.put(HttpHeaders.AUTHORIZATION, JWTAuthenticationExecutorConstants.JWT_TOKEN);
        when(obApiRequestContextMock.isError()).thenReturn(false);
        jwtAuthenticationExecutor.preProcessRequest(obApiRequestContextMock);
        Assert.assertTrue(outContent.toString().contains("JWT Token contains invalid issuer"));
    }

    @Test
    public void testInvalidSubject() {

        outContent.reset();
        getInitialData();
        when(openBankingCDSConfigParserMock.getJWTAuthJWKSUrl()).thenReturn("DummyJWKSUrl");
        when(openBankingCDSConfigParserMock.getJWTAuthIssuer()).thenReturn("cdr-register");
        when(openBankingCDSConfigParserMock.getJWTAuthSubject()).thenReturn("wrongSubject");
        when(openBankingCDSConfigParserMock.getJWTAuthAudience()).thenReturn("https://wso2ob.com");

        headers.put(HttpHeaders.AUTHORIZATION, JWTAuthenticationExecutorConstants.JWT_TOKEN);
        when(obApiRequestContextMock.isError()).thenReturn(false);
        jwtAuthenticationExecutor.preProcessRequest(obApiRequestContextMock);
        Assert.assertTrue(outContent.toString().contains("JWT Token contains invalid subject"));
    }

    @Test
    public void testJWTSignatureValidationFailure() {

        outContent.reset();
        getInitialData();
        when(openBankingCDSConfigParserMock.getJWTAuthJWKSUrl()).thenReturn("DummyJWKSUrl");
        when(openBankingCDSConfigParserMock.getJWTAuthIssuer()).thenReturn("cdr-register");
        when(openBankingCDSConfigParserMock.getJWTAuthSubject()).thenReturn("cdr-register");
        when(openBankingCDSConfigParserMock.getJWTAuthAudience()).thenReturn("https://wso2ob.com");

        headers.put(HttpHeaders.AUTHORIZATION, JWTAuthenticationExecutorConstants.JWT_TOKEN);
        when(obApiRequestContextMock.isError()).thenReturn(false);
        jwtAuthenticationExecutor.preProcessRequest(obApiRequestContextMock);
        Assert.assertTrue(outContent.toString().contains("Invalid JWT Signature"));
    }

    private void getInitialData() {

        mockStatic(OpenBankingCDSConfigParser.class);
        openBankingCDSConfigParserMock = mock(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        when(openBankingCDSConfigParserMock.getJWTAuthEnabled()).thenReturn(true);

        obApiRequestContextMock = mock(OBAPIRequestContext.class);
        MsgInfoDTO msgInfoDTOMock = mock(MsgInfoDTO.class);
        when(obApiRequestContextMock.getMsgInfo()).thenReturn(msgInfoDTOMock);
        when(msgInfoDTOMock.getHeaders()).thenReturn(headers);

        mockStatic(JwtJtiCache.class);
        JwtJtiCache jwtJtiCacheMock = mock(JwtJtiCache.class);
        when(JwtJtiCache.getInstance()).thenReturn(jwtJtiCacheMock);
        when(jwtJtiCacheMock.getFromCache(Mockito.anyObject())).thenReturn(null);
    }
}

class JWTAuthenticatorExecutorMock extends JWTAuthenticationExecutor {

    @Override
    protected boolean validateJWTSignature(String jwtString, String configuredJwksUrl, JSONObject jwtHeader) {

        return false;
    }
}

