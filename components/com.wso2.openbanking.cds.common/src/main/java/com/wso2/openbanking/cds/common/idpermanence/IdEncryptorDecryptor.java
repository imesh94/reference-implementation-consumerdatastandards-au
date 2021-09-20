/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.common.idpermanence;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Handling the AES encryption and decryption
 */
public class IdEncryptorDecryptor {

    private static final Log log = LogFactory.getLog(IdEncryptorDecryptor.class);

    private static SecretKeySpec secretKey;
    private static byte[] key;

    /**
     * Set resource ID encryption/decryption key
     *
     * @param secret secret key
     */
    public static void setKey(String secret) {
        MessageDigest sha = null;
        try {
            key = secret.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-512");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            log.error("Error while setting the encryption key", e);
        }
    }

    /**
     * Encrypt a string using given secret
     *
     * @param strToEncrypt string to be encrypted
     * @param secret encryption key
     * @return encrypted string
     */
    public static String encrypt(String strToEncrypt, String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.encodeBase64URLSafeString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
            log.error("Error while setting the encryption key", e);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            log.error("Error while encrypting", e);
        }
        return null;
    }

    /**
     * Decrypt a string using given secret
     *
     * @param strToDecrypt string to be decrypted
     * @param secret decryption key
     * @return decrypted string
     */
    public static String decrypt(String strToDecrypt, String secret) throws IllegalArgumentException {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] test = cipher.doFinal(Base64.decodeBase64(strToDecrypt.getBytes()));
            return new String(test, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            log.error("Error while setting the decryption key", e);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            log.error ("Error while decrypting", e);
        }
        return null;
    }
}
