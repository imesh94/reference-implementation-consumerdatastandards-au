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

package com.wso2.openbanking.toolkit.cds.test.common.utils

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.RSASSASigner
import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import io.restassured.RestAssured
import io.restassured.config.EncoderConfig
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.bouncycastle.jce.provider.BouncyCastleProvider

import java.security.Key
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Security
import java.security.cert.Certificate

class AURegistrationRequestBuilder {

    /**
     * Get a basic request.
     *
     * @param accessToken
     * @return
     */
    static RequestSpecification buildBasicRequest(String accessToken) {

        return TestSuite.buildRequest()
                .contentType("application/jwt")
                .header("charset", "UTF-8")
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${accessToken}")
                .accept("application/json")
                .config(RestAssured.config()
                .sslConfig(RestAssured.config().getSSLConfig().sslSocketFactory(TestUtil.getSslSocketFactory()))
                .encoderConfig(new EncoderConfig().encodeContentTypeAs(
                "application/jwt", ContentType.TEXT)))
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DCR))
    }

    /**
     * Get a registration request for application creation.
     *
     * @param accessToken
     * @return
     */
    static RequestSpecification buildRegistrationRequest(String claims) {

        return TestSuite.buildRequest()
                .contentType("application/jwt")
                .body(getSignedRequestObject(claims))
                .accept("application/json")
                .config(RestAssured.config()
                .sslConfig(RestAssured.config().getSSLConfig().sslSocketFactory(TestUtil.getSslSocketFactory()))
                .encoderConfig(new EncoderConfig().encodeContentTypeAs(
                "application/jwt", ContentType.TEXT)))
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DCR))
    }

    /**
     * Generate a sign JWT for request object
     * @return a signed JWT
     */
    static String getSignedRequestObject(String claims) {

        KeyStore keyStore = TestUtil.getApplicationKeyStore()
        Certificate certificate = TestUtil.getCertificateFromKeyStore()
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.parse(ConfigParser.instance.signingAlgorithm)).
                keyID(TestUtil.getJwkThumbPrint(certificate)).build()
        Payload payload = new Payload(claims)

        Key signingKey
        signingKey = keyStore.getKey(ConfigParser.getInstance().getApplicationKeystoreAlias(),
                ConfigParser.getInstance().getApplicationKeystorePassword().toCharArray())
        JWSSigner signer = new RSASSASigner((PrivateKey) signingKey)

        Security.addProvider(new BouncyCastleProvider())
        JWSObject jwsObject = new JWSObject(header, new Payload(payload.toString()))
        jwsObject.sign(signer)
        return jwsObject.serialize()
    }

    static String getRegularClaims() {

        long currentTimeInMillis = System.currentTimeMillis()
        long currentTimeInSeconds = currentTimeInMillis / 1000
        return """
            {
                "iss": "740C368F-ECF9-4D29-A2EA-0514A66B0CDN",
                "iat": ${currentTimeInSeconds},
                "exp": ${currentTimeInSeconds + 3600},
                "jti": "${currentTimeInMillis}",
                "aud": "https://www.infosec.cdr.gov.au/token",
                "redirect_uris": [
                    "${AUDCRConstants.REDIRECT_URI}",
                    "${AUDCRConstants.ALTERNATE_REDIRECT_URI}"
                    ],
                "token_endpoint_auth_signing_alg": "PS256",
                "token_endpoint_auth_method": "private_key_jwt",
                "grant_types": [
                    "authorization_code",
                    "refresh_token",
                    "urn:ietf:params:oauth:grant-type:jwt-bearer"
                    ],
                "response_types": [
                    "code id_token"
                    ],
                "application_type": "web",
                "id_token_signed_response_alg": "PS256",
                "id_token_encrypted_response_alg": "RSA-OAEP",
                "id_token_encrypted_response_enc": "A256GCM",
                "request_object_signing_alg": "PS256",
                "software_statement": "${AUDCRConstants.SSA}"
            }
        """
    }

    static String getClaimsWithoutAud() {

        long currentTimeInMillis = System.currentTimeMillis()
        long currentTimeInSeconds = currentTimeInMillis / 1000
        return """
            {
                "iss": "740C368F-ECF9-4D29-A2EA-0514A66B0CDN",
                "iat": ${currentTimeInSeconds},
                "exp": ${currentTimeInSeconds + 3600},
                "jti": "${currentTimeInMillis}",
                "redirect_uris": [
                    "${AUDCRConstants.REDIRECT_URI}"
                    ],
                "token_endpoint_auth_signing_alg": "PS256",
                "token_endpoint_auth_method": "private_key_jwt",
                "grant_types": [
                    "authorization_code",
                    "refresh_token",
                    "urn:ietf:params:oauth:grant-type:jwt-bearer"
                    ],
                "response_types": [
                    "code id_token"
                    ],
                "application_type": "web",
                "id_token_signed_response_alg": "PS256",
                "id_token_encrypted_response_alg": "RSA-OAEP",
                "id_token_encrypted_response_enc": "A256GCM",                
                "request_object_signing_alg": "PS256",
                "software_statement": "${AUDCRConstants.SSA}"
            }
        """
    }

    static String getRegularClaimsWithNonMatchingRedirectUri() {

        long currentTimeInMillis = System.currentTimeMillis()
        long currentTimeInSeconds = currentTimeInMillis / 1000
        return """
            {
                "iss": "740C368F-ECF9-4D29-A2EA-0514A66B0CDN",
                "iat": ${currentTimeInSeconds},
                "exp": ${currentTimeInSeconds + 3600},
                "jti": "${currentTimeInMillis}",
                "aud": "https://www.infosec.cdr.gov.au/token",
                "redirect_uris": [
                    "https://www.google.com/redirects/non-matching-redirect"
                    ],
                "token_endpoint_auth_signing_alg": "PS256",
                "token_endpoint_auth_method": "private_key_jwt",
                "grant_types": [
                    "authorization_code",
                    "refresh_token",
                    "urn:ietf:params:oauth:grant-type:jwt-bearer"
                    ],
                "response_types": [
                    "code id_token"
                    ],
                "application_type": "web",
                "id_token_signed_response_alg": "PS256",
                "id_token_encrypted_response_alg": "RSA-OAEP",
                "id_token_encrypted_response_enc": "A256GCM",
                "request_object_signing_alg": "PS256",
                "software_statement": "${AUDCRConstants.SSA}"
            }
        """
    }

    static String getRegularClaimsWithNewRedirectUri() {

        long currentTimeInMillis = System.currentTimeMillis()
        long currentTimeInSeconds = currentTimeInMillis / 1000
        return """
            {
                "iss": "740C368F-ECF9-4D29-A2EA-0514A66B0CDN",
                "iat": ${currentTimeInSeconds},
                "exp": ${currentTimeInSeconds + 3600},
                "jti": "${currentTimeInMillis}",
                "aud": "https://www.infosec.cdr.gov.au/token",
                "redirect_uris": [
                    "${AUDCRConstants.ALTERNATE_REDIRECT_URI}"
                    ],
                "token_endpoint_auth_signing_alg": "PS256",
                "token_endpoint_auth_method": "private_key_jwt",
                "grant_types": [
                    "authorization_code",
                    "refresh_token",
                    "urn:ietf:params:oauth:grant-type:jwt-bearer"
                    ],
                "response_types": [
                    "code id_token"
                    ],
                "application_type": "web",
                "id_token_signed_response_alg": "PS256",
                "id_token_encrypted_response_alg": "RSA-OAEP",
                "id_token_encrypted_response_enc": "A256GCM",
                "request_object_signing_alg": "PS256",
                "software_statement": "${AUDCRConstants.SSA}"
            }
        """
    }

    static String getClaimsWithoutTokenEndpointAuthSigningAlg() {

        long currentTimeInMillis = System.currentTimeMillis()
        long currentTimeInSeconds = currentTimeInMillis / 1000
        return """
            {
                "iss": "740C368F-ECF9-4D29-A2EA-0514A66B0CDN",
                "iat": ${currentTimeInSeconds},
                "exp": ${currentTimeInSeconds + 3600},
                "jti": "${currentTimeInMillis}",
                "aud": "https://www.infosec.cdr.gov.au/token",
                "redirect_uris": [
                    "${AUDCRConstants.REDIRECT_URI}"
                    ],
                "token_endpoint_auth_method": "private_key_jwt",
                "grant_types": [
                    "authorization_code",
                    "refresh_token",
                    "urn:ietf:params:oauth:grant-type:jwt-bearer"
                    ],
                "response_types": [
                    "code id_token"
                    ],
                "application_type": "web",
                "id_token_signed_response_alg": "PS256",
                "id_token_encrypted_response_alg": "RSA-OAEP",
                "id_token_encrypted_response_enc": "A256GCM",
                "request_object_signing_alg": "PS256",
                "software_statement": "${AUDCRConstants.SSA}"
            }
        """
    }

    static String getClaimsWithoutTokenEndpointAuthMethod() {

        long currentTimeInMillis = System.currentTimeMillis()
        long currentTimeInSeconds = currentTimeInMillis / 1000
        return """
            {
                "iss": "740C368F-ECF9-4D29-A2EA-0514A66B0CDN",
                "iat": ${currentTimeInSeconds},
                "exp": ${currentTimeInSeconds + 3600},
                "jti": "${currentTimeInMillis}",
                "aud": "https://www.infosec.cdr.gov.au/token",
                "redirect_uris": [
                    "${AUDCRConstants.REDIRECT_URI}"
                    ],
                "token_endpoint_auth_signing_alg": "PS256",
                "grant_types": [
                    "authorization_code",
                    "refresh_token",
                    "urn:ietf:params:oauth:grant-type:jwt-bearer"
                    ],
                "response_types": [
                    "code id_token"
                    ],
                "application_type": "web",
                "id_token_signed_response_alg": "PS256",
                "id_token_encrypted_response_alg": "RSA-OAEP",
                "id_token_encrypted_response_enc": "A256GCM",
                "request_object_signing_alg": "PS256",
                "software_statement": "${AUDCRConstants.SSA}"
            }
        """
    }

    static String getClaimsWithoutGrantTypes() {

        long currentTimeInMillis = System.currentTimeMillis()
        long currentTimeInSeconds = currentTimeInMillis / 1000
        return """
            {
                "iss": "740C368F-ECF9-4D29-A2EA-0514A66B0CDN",
                "iat": ${currentTimeInSeconds},
                "exp": ${currentTimeInSeconds + 3600},
                "jti": "${currentTimeInMillis}",
                "aud": "https://www.infosec.cdr.gov.au/token",
                "redirect_uris": [
                    "${AUDCRConstants.REDIRECT_URI}"
                    ],
                "token_endpoint_auth_signing_alg": "PS256",
                "token_endpoint_auth_method": "private_key_jwt",
                "response_types": [
                    "code id_token"
                    ],
                "application_type": "web",
                "id_token_signed_response_alg": "PS256",
                "id_token_encrypted_response_alg": "RSA-OAEP",
                "id_token_encrypted_response_enc": "A256GCM",
                "request_object_signing_alg": "PS256",
                "software_statement": "${AUDCRConstants.SSA}"
            }
        """
    }

    static String getClaimsWithoutResponseTypes() {

        long currentTimeInMillis = System.currentTimeMillis()
        long currentTimeInSeconds = currentTimeInMillis / 1000
        return """
            {
                "iss": "740C368F-ECF9-4D29-A2EA-0514A66B0CDN",
                "iat": ${currentTimeInSeconds},
                "exp": ${currentTimeInSeconds + 3600},
                "jti": "${currentTimeInMillis}",
                "aud": "https://www.infosec.cdr.gov.au/token",
                "redirect_uris": [
                    "${AUDCRConstants.REDIRECT_URI}"
                    ],
                "token_endpoint_auth_signing_alg": "PS256",
                "token_endpoint_auth_method": "private_key_jwt",
                "grant_types": [
                    "authorization_code",
                    "refresh_token",
                    "urn:ietf:params:oauth:grant-type:jwt-bearer"
                    ],
                "application_type": "web",
                "id_token_signed_response_alg": "PS256",
                "id_token_encrypted_response_alg": "RSA-OAEP",
                "id_token_encrypted_response_enc": "A256GCM",
                "request_object_signing_alg": "PS256",
                "software_statement": "${AUDCRConstants.SSA}"
            }
        """
    }

    static String getClaimsWithoutSSA() {

        long currentTimeInMillis = System.currentTimeMillis()
        long currentTimeInSeconds = currentTimeInMillis / 1000
        return """
            {
                "iss": "740C368F-ECF9-4D29-A2EA-0514A66B0CDN",
                "iat": ${currentTimeInSeconds},
                "exp": ${currentTimeInSeconds + 3600},
                "jti": "${currentTimeInMillis}",
                "aud": "https://www.infosec.cdr.gov.au/token",
                "redirect_uris": [
                    "${AUDCRConstants.REDIRECT_URI}"
                    ],
                "token_endpoint_auth_signing_alg": "PS256",
                "token_endpoint_auth_method": "private_key_jwt",
                "grant_types": [
                    "authorization_code",
                    "refresh_token",
                    "urn:ietf:params:oauth:grant-type:jwt-bearer"
                    ],
                "response_types": [
                    "code id_token"
                    ],
                "application_type": "web",
                "id_token_signed_response_alg": "PS256",
                "id_token_encrypted_response_alg": "RSA-OAEP",
                "id_token_encrypted_response_enc": "A256GCM",
                "request_object_signing_alg": "PS256",
            }
        """
    }

    static String getClaimsWithoutIdTokenAlg() {

        long currentTimeInMillis = System.currentTimeMillis()
        long currentTimeInSeconds = currentTimeInMillis / 1000
        return """
            {
                "iss": "740C368F-ECF9-4D29-A2EA-0514A66B0CDN",
                "iat": ${currentTimeInSeconds},
                "exp": ${currentTimeInSeconds + 3600},
                "jti": "${currentTimeInMillis}",
                "aud": "https://www.infosec.cdr.gov.au/token",
                "redirect_uris": [
                    "${AUDCRConstants.REDIRECT_URI}",
                    "${AUDCRConstants.ALTERNATE_REDIRECT_URI}"
                    ],
                "token_endpoint_auth_signing_alg": "PS256",
                "token_endpoint_auth_method": "private_key_jwt",
                "grant_types": [
                    "authorization_code",
                    "refresh_token",
                    "urn:ietf:params:oauth:grant-type:jwt-bearer"
                    ],
                "response_types": [
                    "code id_token"
                    ],
                "application_type": "web",
                "id_token_signed_response_alg": "PS256",
                "id_token_encrypted_response_enc": "A256GCM",
                "request_object_signing_alg": "PS256",
                "software_statement": "${AUDCRConstants.SSA}"
            }
        """
    }

    static String getClaimsWithoutIdTokenEnc() {

        long currentTimeInMillis = System.currentTimeMillis()
        long currentTimeInSeconds = currentTimeInMillis / 1000
        return """
            {
                "iss": "740C368F-ECF9-4D29-A2EA-0514A66B0CDN",
                "iat": ${currentTimeInSeconds},
                "exp": ${currentTimeInSeconds + 3600},
                "jti": "${currentTimeInMillis}",
                "aud": "https://www.infosec.cdr.gov.au/token",
                "redirect_uris": [
                    "${AUDCRConstants.REDIRECT_URI}",
                    "${AUDCRConstants.ALTERNATE_REDIRECT_URI}"
                    ],
                "token_endpoint_auth_signing_alg": "PS256",
                "token_endpoint_auth_method": "private_key_jwt",
                "grant_types": [
                    "authorization_code",
                    "refresh_token",
                    "urn:ietf:params:oauth:grant-type:jwt-bearer"
                    ],
                "response_types": [
                    "code id_token"
                    ],
                "application_type": "web",
                "id_token_signed_response_alg": "PS256",
                "id_token_encrypted_response_alg": "RSA-OAEP",
                "request_object_signing_alg": "PS256",
                "software_statement": "${AUDCRConstants.SSA}"
            }
        """
    }

    static String getClaimsWithInvalidIdTokenAlg() {

        long currentTimeInMillis = System.currentTimeMillis()
        long currentTimeInSeconds = currentTimeInMillis / 1000
        return """
            {
                "iss": "740C368F-ECF9-4D29-A2EA-0514A66B0CDN",
                "iat": ${currentTimeInSeconds},
                "exp": ${currentTimeInSeconds + 3600},
                "jti": "${currentTimeInMillis}",
                "aud": "https://www.infosec.cdr.gov.au/token",
                "redirect_uris": [
                    "${AUDCRConstants.REDIRECT_URI}",
                    "${AUDCRConstants.ALTERNATE_REDIRECT_URI}"
                    ],
                "token_endpoint_auth_signing_alg": "PS256",
                "token_endpoint_auth_method": "private_key_jwt",
                "grant_types": [
                    "authorization_code",
                    "refresh_token",
                    "urn:ietf:params:oauth:grant-type:jwt-bearer"
                    ],
                "response_types": [
                    "code id_token"
                    ],
                "application_type": "web",
                "id_token_signed_response_alg": "PS256",
                "id_token_encrypted_response_alg": "RSA-OAEP-X",
                "id_token_encrypted_response_enc": "A256GCM",
                "request_object_signing_alg": "PS256",
                "software_statement": "${AUDCRConstants.SSA}"
            }
        """
    }

    static String getClaimsWithInvalidIdTokenEnc() {

        long currentTimeInMillis = System.currentTimeMillis()
        long currentTimeInSeconds = currentTimeInMillis / 1000
        return """
            {
                "iss": "740C368F-ECF9-4D29-A2EA-0514A66B0CDN",
                "iat": ${currentTimeInSeconds},
                "exp": ${currentTimeInSeconds + 3600},
                "jti": "${currentTimeInMillis}",
                "aud": "https://www.infosec.cdr.gov.au/token",
                "redirect_uris": [
                    "${AUDCRConstants.REDIRECT_URI}",
                    "${AUDCRConstants.ALTERNATE_REDIRECT_URI}"
                    ],
                "token_endpoint_auth_signing_alg": "PS256",
                "token_endpoint_auth_method": "private_key_jwt",
                "grant_types": [
                    "authorization_code",
                    "refresh_token",
                    "urn:ietf:params:oauth:grant-type:jwt-bearer"
                    ],
                "response_types": [
                    "code id_token"
                    ],
                "application_type": "web",
                "id_token_signed_response_alg": "PS256",
                "id_token_encrypted_response_alg": "RSA-OAEP",
                "id_token_encrypted_response_enc": "A128GCM",
                "request_object_signing_alg": "PS256",
                "software_statement": "${AUDCRConstants.SSA}"
            }
        """
    }

    static String getClaimsWithNonMatchingSoftwareIDandISS() {

        long currentTimeInMillis = System.currentTimeMillis()
        long currentTimeInSeconds = currentTimeInMillis / 1000
        return """
            {
                "iss": "Mock Company",
                "iat": ${currentTimeInSeconds},
                "exp": ${currentTimeInSeconds + 3600},
                "jti": "${currentTimeInMillis}",
                "aud": "https://www.infosec.cdr.gov.au/token",
                "redirect_uris": [
                    "${AUDCRConstants.REDIRECT_URI}",
                    "${AUDCRConstants.ALTERNATE_REDIRECT_URI}"
                    ],
                "token_endpoint_auth_signing_alg": "PS256",
                "token_endpoint_auth_method": "private_key_jwt",
                "grant_types": [
                    "authorization_code",
                    "refresh_token",
                    "urn:ietf:params:oauth:grant-type:jwt-bearer"
                    ],
                "response_types": [
                    "code id_token"
                    ],
                "application_type": "web",
                "id_token_signed_response_alg": "PS256",
                "id_token_encrypted_response_alg": "RSA-OAEP",
                "id_token_encrypted_response_enc": "A256GCM",
                "request_object_signing_alg": "PS256",
                "software_statement": "${AUDCRConstants.SSA}"
            }
        """
    }

    static String getRegularClaimsWithGivenJti(String jti) {

        long currentTimeInSeconds = System.currentTimeMillis() / 1000
        return """
            {
                "iss": "740C368F-ECF9-4D29-A2EA-0514A66B0CDN",
                "iat": ${currentTimeInSeconds},
                "exp": ${currentTimeInSeconds + 3600},
                "jti": "${jti}",
                "aud": "https://www.infosec.cdr.gov.au/token",
                "redirect_uris": [
                    "${AUDCRConstants.REDIRECT_URI}",
                    "${AUDCRConstants.ALTERNATE_REDIRECT_URI}"
                    ],
                "token_endpoint_auth_signing_alg": "PS256",
                "token_endpoint_auth_method": "private_key_jwt",
                "grant_types": [
                    "authorization_code",
                    "refresh_token",
                    "urn:ietf:params:oauth:grant-type:jwt-bearer"
                    ],
                "response_types": [
                    "code id_token"
                    ],
                "application_type": "web",
                "id_token_signed_response_alg": "PS256",
                "id_token_encrypted_response_alg": "RSA-OAEP",
                "id_token_encrypted_response_enc": "A256GCM",
                "request_object_signing_alg": "PS256",
                "software_statement": "${AUDCRConstants.SSA}"
            }
        """
    }
}
