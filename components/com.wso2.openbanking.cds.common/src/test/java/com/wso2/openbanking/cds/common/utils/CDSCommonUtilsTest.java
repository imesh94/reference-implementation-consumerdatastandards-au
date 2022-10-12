/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
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
 * Test class for CDSCommonUtils
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

}
