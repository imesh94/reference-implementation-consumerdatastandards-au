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
package com.wso2.openbanking.cds.identity.claims.utils;

import com.nimbusds.jose.JWSAlgorithm;
import com.wso2.openbanking.accelerator.common.util.Generated;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.Charsets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Utility methods used for CDS Claim Provider service
 */
public class CDSClaimProviderUtils {

    private static Log log = LogFactory.getLog(CDSClaimProviderUtils.class);

    /**
     * Method to obtain Hash Value for a given String
     *
     * @param value String value that required to be Hashed
     * @return Hashed String
     * @throws IdentityOAuth2Exception
     */
    @Generated(message = "Ignoring since the method require a service call")
    public static String getHashValue(String value, String digestAlgorithm) throws IdentityOAuth2Exception {

        if (digestAlgorithm == null) {
            if (log.isDebugEnabled()) {
                log.debug("Digest algorithm not provided. Therefore loading digest algorithm from identity.xml");
            }
            JWSAlgorithm digAlg = OAuth2Util.mapSignatureAlgorithmForJWSAlgorithm(
                    OAuthServerConfiguration.getInstance().getIdTokenSignatureAlgorithm());
            digestAlgorithm = OAuth2Util.mapDigestAlgorithm(digAlg);
        }
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(digestAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IdentityOAuth2Exception("Error creating the hash value. Invalid Digest Algorithm: " +
                    digestAlgorithm);
        }

        messageDigest.update(value.getBytes(Charsets.UTF_8));
        byte[] digest = messageDigest.digest();
        int leftHalfBytes = 16;
        if ("SHA-384".equals(digestAlgorithm)) {
            leftHalfBytes = 24;
        } else if ("SHA-512".equals(digestAlgorithm)) {
            leftHalfBytes = 32;
        }
        byte[] leftmost = new byte[leftHalfBytes];
        System.arraycopy(digest, 0, leftmost, 0, leftHalfBytes);
        return new String(Base64.encodeBase64Chunked(leftmost), Charsets.UTF_8);
    }

    /**
     * convert consent expirytime to epoch time
     *
     * @param expiryTime consent expiry time in seconds
     * @return expiry time as an epoch time
     */
    public static long getEpochDateTime(long expiryTime) {

        OffsetDateTime currentTime = OffsetDateTime.now(ZoneOffset.UTC);
        return currentTime.plusSeconds(expiryTime).toEpochSecond();
    }
}
