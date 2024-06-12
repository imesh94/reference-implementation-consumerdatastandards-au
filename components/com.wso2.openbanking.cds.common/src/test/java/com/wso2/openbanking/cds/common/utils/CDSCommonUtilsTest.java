/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 *
 */
package com.wso2.openbanking.cds.common.utils;

import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doReturn;

/**
 * Test class for CDSCommonUtils.
 */
@PrepareForTest({OpenBankingCDSConfigParser.class})
@PowerMockIgnore({"javax.crypto.*", "jdk.internal.reflect.*"})
public class CDSCommonUtilsTest extends PowerMockTestCase {

    private static final String STRING_TO_ENCRYPT = "sample-access-token";
    private static final String ENCRYPTED_STRING = "7b7e65d4a069ec690bf45b0ecded4ae6376dee2c6193c5284823314563011536";

    OpenBankingCDSConfigParser openBankingCDSConfigParserMock;
    String encryptedToken;

    @Test
    public void testEncryptAccessToken() {

        openBankingCDSConfigParserMock = PowerMockito.mock(OpenBankingCDSConfigParser.class);
        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        PowerMockito.when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        doReturn("wso2").when(openBankingCDSConfigParserMock).getTokenEncryptionSecretKey();
        encryptedToken = CDSCommonUtils.encryptAccessToken(STRING_TO_ENCRYPT);

        Assert.assertEquals(encryptedToken, ENCRYPTED_STRING);
    }

    @Test
    public void testValidRequestUriKey() {
        String expectedRequestUriKey = "abc123";
        String actualRequestUriKey = CDSCommonUtils
                .getRequestUriKey("abc:123:def:request_uri:" + expectedRequestUriKey);

        Assert.assertEquals(actualRequestUriKey, expectedRequestUriKey);
    }

    @Test
    public void testNullRequestUriKey() {
        String expectedRequestUriKey = null;
        String actualRequestUriKey = CDSCommonUtils.getRequestUriKey(null);

        Assert.assertEquals(actualRequestUriKey, expectedRequestUriKey);
    }

}
