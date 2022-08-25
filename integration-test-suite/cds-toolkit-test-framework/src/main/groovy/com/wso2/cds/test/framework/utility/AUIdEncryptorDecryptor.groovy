/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.cds.test.framework.utility

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * Handling the AES encryption and decryption
 */
class AUIdEncryptorDecryptor {

    private static SecretKeySpec secretKey
    private static byte[] key

    /**
     * Set resource ID encryption/decryption key
     * @param secret : secret key
     */
    static void setKey(String secret) {
        MessageDigest sha
        key = secret.getBytes(StandardCharsets.UTF_8)
        sha = MessageDigest.getInstance("SHA-512")
        key = sha.digest(key)
        key = Arrays.copyOf(key, 16)
        secretKey = new SecretKeySpec(key, "AES")

    }

    /**
     * Encrypt a string using given secret
     * @param strToEncrypt : string to be encrypted
     * @param secret : encryption key
     * @return encrypted string
     */
    static String encrypt(String strToEncrypt, String secret) {
        setKey(secret)
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return Base64.getUrlEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)))
    }

    /**
     * Decrypt a string using given secret
     * @param strToDecrypt : string to be decrypted
     * @param secret : decryption key
     * @return decrypted string
     */
    static String decrypt(String strToDecrypt, String secret) {
        setKey(secret)
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        byte[] test = cipher.doFinal(Base64.getUrlDecoder().decode(strToDecrypt))
        return new String(test, StandardCharsets.UTF_8)
    }

}

