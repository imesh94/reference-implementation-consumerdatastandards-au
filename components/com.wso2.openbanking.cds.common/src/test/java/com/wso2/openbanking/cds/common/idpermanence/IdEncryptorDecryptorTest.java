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

package com.wso2.openbanking.cds.common.idpermanence;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Test class for IdEncryptorDecryptor
 */
public class IdEncryptorDecryptorTest {

    int appId = 3;
    String memberId = "5@carbon.super";
    int realResourceId = 222222222;
    String secret = "wso2";
    String encryptedString;
    List<String> urlUnsafeCharacters = Arrays.asList("!", "*", "'", "(", ")", ";", ":", "@", "&", "=", "+",
            "$", ",", "/", "?", "%", "#", "[", "]", " ", "\"", "<", ">", "%", "{", "}", "|", "\\", "^", "`");

    @Test
    public void testEncryption() {

        encryptedString = IdEncryptorDecryptor.
                encrypt(memberId + ":" + appId + ":" + realResourceId, secret);

        Assert.assertNotNull(encryptedString);
        Assert.assertFalse(urlUnsafeCharacters.stream().anyMatch(encryptedString::contains));
    }

    @Test (dependsOnMethods = "testEncryption")
    public void testDecryption() {

        String decryptedString = IdEncryptorDecryptor.decrypt(encryptedString, secret);
        Assert.assertEquals(decryptedString, memberId + ":" + appId + ":" + realResourceId);
    }

}
