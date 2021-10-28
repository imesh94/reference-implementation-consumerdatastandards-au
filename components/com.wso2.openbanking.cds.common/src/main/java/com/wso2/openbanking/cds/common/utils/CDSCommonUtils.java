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

package com.wso2.openbanking.cds.common.utils;

import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * CDS Common Utils.
 */
public class CDSCommonUtils {

    private static final String HMACSHA256 = "HmacSHA256";

    private static final Log LOG = LogFactory.getLog(CDSCommonUtils.class);

    /**
     * Encrypt access token using HmacSHA256
     *
     * @param accessToken String access token
     * @return encrypted token
     */
    public static String encryptAccessToken(String accessToken) {

        try {
            byte[] secretKey = OpenBankingCDSConfigParser.getInstance().getTokenEncryptionSecretKey()
                    .getBytes(StandardCharsets.UTF_8);
            Mac mac = Mac.getInstance(HMACSHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, HMACSHA256);
            mac.init(secretKeySpec);
            accessToken = new String(Hex.encodeHex(mac.doFinal(accessToken.getBytes(StandardCharsets.UTF_8))));
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Unable to encrypt the access token. Invalid encryption algorithm.", e);
        } catch (InvalidKeyException e) {
            LOG.error("Unable to encrypt the access token. Invalid encryption key.", e);
        }
        return accessToken;
    }
}
