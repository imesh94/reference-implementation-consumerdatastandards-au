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

package com.wso2.cds.test.framework.request_builder

import com.nimbusds.oauth2.sdk.id.State
import com.wso2.bfsi.test.framework.exception.TestFrameworkException
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.SignedJWT
import com.wso2.openbanking.test.framework.request_builder.JSONRequestGenerator
import com.wso2.openbanking.test.framework.request_builder.PayloadGenerator
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import com.wso2.cds.test.framework.keystore.AUKeyStore
import com.wso2.openbanking.test.framework.keystore.OBKeyStore
import org.apache.commons.lang3.StringUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.json.JSONObject
import org.testng.Reporter

import java.security.Key
import java.security.PrivateKey
import java.security.Security
import java.security.cert.Certificate
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Class for generate JWT
 */
class AUJWTGenerator {

    private AUConfigurationService auConfiguration
    private List<String> scopesList = null // Scopes can be set before generate payload
    private String signingAlgorithm

    AUJWTGenerator() {
        auConfiguration = new AUConfigurationService()
    }

    void setScopes(List<String> scopes) {
        scopesList = scopes
    }

    /**
     * Set signing algorithm
     * @param algorithm
     */
    void setSigningAlgorithm(String algorithm) {
        this.signingAlgorithm = algorithm
    }

    /**
     * Get signing algorithm for methods. IF signing algorithm is null, provide algorithm in configuration
     * @return
     */
    String getSigningAlgorithm() {
        if (signingAlgorithm == null) {
            signingAlgorithm = auConfiguration.getCommonSigningAlgorithm()
        }
        return this.signingAlgorithm
    }


    /**
     * Get Signed object
     * @param claims
     * @return
     */
    String getSignedRequestObject(String claims) {
        Key signingKey
        JWSHeader header
        if (auConfiguration.getMockCDREnabled()) {
            Certificate certificate = AUKeyStore.getCertificateFromMockCDRKeyStore()
            String thumbprint = AUKeyStore.getJwkThumbPrintForSHA256(certificate)
            header = new JWSHeader.Builder(JWSAlgorithm.parse(getSigningAlgorithm()))
                    .keyID(thumbprint).type(JOSEObjectType.JWT).build()
            signingKey = AUKeyStore.getMockCDRSigningKey()
        } else {
            Certificate certificate = OBKeyStore.getApplicationCertificate()
            String thumbprint = OBKeyStore.getJwkThumbPrintForSHA1(certificate)
            header = new JWSHeader.Builder(JWSAlgorithm.parse(getSigningAlgorithm()))
                    .keyID(thumbprint).type(JOSEObjectType.JWT).build()
            signingKey = OBKeyStore.getApplicationSigningKey()
        }
        JWSObject jwsObject = new JWSObject(header, new Payload(claims))
        JWSSigner signer = new RSASSASigner((PrivateKey) signingKey)
        Security.addProvider(new BouncyCastleProvider())
        jwsObject.sign(signer)
        return jwsObject.serialize()
    }

    /**
     * Return JWT for application access token generation
     * @param clientId
     * @return
     * @throws TestFrameworkException
     */
    String getAppAccessTokenJwt(String clientId = null) throws TestFrameworkException {

        JSONObject clientAssertion = new JSONRequestGenerator().addIssuer(clientId)
                .addSubject(clientId).addAudience().addExpireDate().addIssuedAt().addJti().getJsonObject()

        String payload = getSignedRequestObject(clientAssertion.toString())
        String accessTokenJWT = new PayloadGenerator().addGrantType().addScopes(scopesList).addClientAsType()
                .addClientAssertion(payload).addRedirectUri().getPayload()
        return accessTokenJWT
    }
     String getClientAssertionJwt(String clientId=null) {
        JSONObject clientAssertion = new JSONRequestGenerator().addIssuer(clientId)
                .addSubject(clientId).addAudience().addExpireDate().addIssuedAt().addJti().getJsonObject()

        String payload = getSignedRequestObject(clientAssertion.toString())
        return payload
    }

    /**
     * Return JWT for user access token generation
     * @param code
     * @return
     * @throws TestFrameworkException
     */
    String getUserAccessTokenJwt(String code = "") throws TestFrameworkException {

        JSONObject clientAssertion = new JSONRequestGenerator().addIssuer()
                .addSubject().addAudience().addExpireDate().addIssuedAt().addJti().getJsonObject()
        String payload = getSignedRequestObject(clientAssertion.toString())
        String accessTokenJWT = new PayloadGenerator().addGrantType().addCode(code).addScopes().addClientAsType()
                .addClientAssertion(payload).addRedirectUri().addClientID().getPayload()
        return accessTokenJWT
    }

    /**
     * Return signed JWT for Authorization request
     * @param scopeString
     * @param sharingDuration
     * @param sendSharingDuration
     * @param cdrArrangementId
     * @param redirect_uri
     * @param clientId
     * @return
     */
    JWT getSignedAuthRequestObject(String scopeString, Long sharingDuration, Boolean sendSharingDuration,
                                   String cdrArrangementId, String redirect_uri, String clientId, String responseType,
                                   boolean isStateRequired = true, String state) {

        def expiryDate = Instant.now().plus(1, ChronoUnit.HOURS)
        def notBefore = Instant.now()
        String claims

        JSONObject acr = new JSONObject().put("essential", true).put("values", new ArrayList<String>() {
            {
                add("urn:cds.au:cdr:3")
            }
        })
        JSONObject userInfoString = new JSONObject().put("given_name", null).put("family_name", null)
        JSONObject authTimeString = new JSONObject().put("essential", true)
        JSONObject claimsString = new JSONObject().put("id_token", new JSONObject().put("acr", acr).put("auth_time", authTimeString))
                .put("userinfo", userInfoString)
        if (sharingDuration.intValue() != 0 || sendSharingDuration) {
            claimsString.put("sharing_duration", sharingDuration)
        }
        if (!StringUtils.isEmpty(cdrArrangementId)) {
            claimsString.put("cdr_arrangement_id", cdrArrangementId)
        }

        if (isStateRequired) {
            claims = new JSONRequestGenerator()
                    .addAudience()
                    .addResponseType(responseType)
                    .addExpireDate(expiryDate.getEpochSecond().toLong())
                    .addClientID(clientId)
                    .addIssuer(clientId)
                    .addRedirectURI(redirect_uri)
                    .addScope(scopeString)
                    .addState(state)
                    .addNonce()
                    .addCustomValue("max_age", 86400)
                    .addCustomValue("nbf", notBefore.getEpochSecond().toLong())
                    .addCustomJson("claims", claimsString)
                    .getJsonObject().toString()
        } else {
            claims = new JSONRequestGenerator()
                    .addAudience()
                    .addResponseType(responseType)
                    .addExpireDate(expiryDate.getEpochSecond().toLong())
                    .addClientID(clientId)
                    .addIssuer(clientId)
                    .addRedirectURI(redirect_uri)
                    .addScope(scopeString)
                    .addNonce()
                    .addCustomValue("max_age", 86400)
                    .addCustomValue("nbf", notBefore.getEpochSecond().toLong())
                    .addCustomJson("claims", claimsString)
                    .getJsonObject().toString()
        }

        String payload = getSignedRequestObject(claims)

        Reporter.log("Authorisation Request Object")
        Reporter.log("JWS Payload ${new Payload(claims).toString()}")

        return SignedJWT.parse(payload)
    }

}

