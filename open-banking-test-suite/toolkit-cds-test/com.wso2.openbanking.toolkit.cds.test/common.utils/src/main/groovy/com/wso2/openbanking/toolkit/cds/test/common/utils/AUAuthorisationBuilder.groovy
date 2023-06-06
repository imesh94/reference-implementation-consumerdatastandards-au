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
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.oauth2.sdk.AuthorizationRequest
import com.nimbusds.oauth2.sdk.ResponseType
import com.nimbusds.oauth2.sdk.Scope
import com.nimbusds.oauth2.sdk.id.ClientID
import com.nimbusds.oauth2.sdk.id.State
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod
import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import org.apache.commons.lang3.StringUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.json.JSONObject
import org.testng.Reporter

import java.security.Key
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Security
import java.security.cert.Certificate
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * OAuth related functionality for AU
 */
class AUAuthorisationBuilder {

    private AuthorizationRequest request

    private static params = [
            endpoint     : new URI("${ConfigParser.instance.getAuthorisationServerUrl()}/oauth2/authorize/"),
            response_type: new ResponseType("code id_token"),
            client_id    : new ClientID(AppConfigReader.getClientId()),
            redirect_uri : new URI(AppConfigReader.getRedirectURL()),
            state        : new State(UUID.randomUUID().toString())
    ]

    /**
     * AU Authorisation Builder for Default Authorisation Flow
     * @param scopes
     * @param sharingDuration
     * @param sendSharingDuration
     * @param cdrArrangementId
     * @param client_id
     */
    AUAuthorisationBuilder(List<AUConstants.SCOPES> scopes, Long sharingDuration, Boolean sendSharingDuration,
                           String cdrArrangementId = "", String client_id = AppConfigReader.getClientId()) {

        String scopeString = "openid ${String.join(" ", scopes.collect({ it.scopeString }))}"

        request = new AuthorizationRequest.Builder(params.response_type, new ClientID(client_id))
                .responseType(ResponseType.parse("code id_token"))
                .endpointURI(params.endpoint)
                .redirectionURI(params.redirect_uri)
                .requestObject(getSignedRequestObject(scopeString, sharingDuration, sendSharingDuration, cdrArrangementId,
                        params.redirect_uri.toString(), client_id))
                .scope(new Scope(scopeString))
                .state(params.state)
                .codeChallenge(AURequestBuilder.getCodeVerifier(), CodeChallengeMethod.S256)
                .customParameter("prompt", "login")
                .build()
    }

    /**
     * AU Authorisation Builder for Pushed Authorisation Flow
     * @param scopes
     * @param requestUri
     * @param client_id
     */
    AUAuthorisationBuilder(List<AUConstants.SCOPES> scopes, URI requestUri, String client_id = params.client_id) {

        String scopeString = "openid ${String.join(" ", scopes.collect({ it.scopeString }))}"

        request = new AuthorizationRequest.Builder(params.response_type, new ClientID(client_id))
                .responseType(ResponseType.parse("code id_token"))
                .scope(new Scope(scopeString))
                .requestURI(requestUri)
                .redirectionURI(params.redirect_uri)
                .codeChallenge(AURequestBuilder.getCodeVerifier(), CodeChallengeMethod.S256)
                .endpointURI(params.endpoint)
                .customParameter("prompt", "login")
                .build()
    }

    /**
     * AU Authorisation Builder for Pushed Authorisation Flow with parameters
     * @param scopes
     * @param requestUri
     * @param client_id
     * @param redirect_uri
     */
    AUAuthorisationBuilder(List<AUConstants.SCOPES> scopes, URI requestUri, String client_id, String redirect_uri) {

        String scopeString = "openid ${String.join(" ", scopes.collect({ it.scopeString }))}"

        request = new AuthorizationRequest.Builder(params.response_type, new ClientID(client_id))
                .responseType(ResponseType.parse("code id_token"))
                .scope(new Scope(scopeString))
                .requestURI(requestUri)
                .redirectionURI(redirect_uri.toURI())
                .codeChallenge(AURequestBuilder.getCodeVerifier(), CodeChallengeMethod.S256)
                .endpointURI(params.endpoint)
                .customParameter("prompt", "login")
                .build()
    }

    /**
     * Generate a sign JWT for request object
     * @return a signed JWT
     */
    static JWT getSignedRequestObject(String scopeString, Long sharingDuration, Boolean sendSharingDuration,
                                      String cdrArrangementId,
                                      String redirect_uri = AppConfigReader.getRedirectURL(),
                                      String clientId = AppConfigReader.getClientId()) {

        Key signingKey
        JWSHeader header
        if (ConfigParser.getInstance().mockCDRRegisterEnabled) {
            KeyStore keyStore = TestUtil.getMockADRApplicationKeyStore()
            Certificate certificate = TestUtil.getCertificateFromMockADRKeyStore()
            header = new JWSHeader.Builder(JWSAlgorithm.parse(ConfigParser.instance.signingAlgorithm)).
                    keyID(TestUtil.getJwkThumbPrintForSHA256(certificate)).build()

            signingKey = keyStore.getKey(ConfigParser.getInstance().getMockADRSigningKeystoreAlias(),
                    ConfigParser.getInstance().getMockADRSigningKeystorePassword().toCharArray())
        } else {
            KeyStore keyStore = TestUtil.getApplicationKeyStore()
            Certificate certificate = TestUtil.getCertificateFromKeyStore()
            header = new JWSHeader.Builder(JWSAlgorithm.parse(ConfigParser.instance.signingAlgorithm)).
                    keyID(TestUtil.getJwkThumbPrint(certificate)).build()

            signingKey = keyStore.getKey(AppConfigReader.getApplicationKeystoreAlias(),
                    AppConfigReader.getApplicationKeystorePassword().toCharArray())
        }

        String claims

        def expiryDate = Instant.now().plus(1, ChronoUnit.HOURS)
        def notBefore = Instant.now()

        if (sharingDuration.intValue() == 0 && !sendSharingDuration) {

            claims = """
            {
              "aud": ["${ConfigParser.instance.audienceValue}", "https://localhost:9446"],
              "response_type": "code id_token",
              "exp": ${expiryDate.getEpochSecond().toLong()},
              "client_id": "${clientId}",
              "iss": "${clientId}",
              "redirect_uri": "${redirect_uri}",
              "scope": "${scopeString}",
              "state": "suite",
              "nonce": "${UUID.randomUUID()}",
              "nbf": ${notBefore.getEpochSecond().toLong()},
              "claims": {
                id_token": {
                  "acr": {
                    "essential": true,
                    "values": ["urn:cds.au:cdr:3"]
                  }
                },
              "userinfo": {
                "given_name": null,
                "family_name": null
                }
              }
            }
       """.stripIndent()
        } else {

            claims = """
            {
              "aud": ["${ConfigParser.instance.audienceValue}", "https://localhost:9446"],
              "response_type": "code id_token",
              "exp": ${expiryDate.getEpochSecond().toLong()},
              "client_id": "${clientId}",
              "iss": "${clientId}",
              "redirect_uri": "${redirect_uri}",
              "scope": "${scopeString}",
              "state": "suite",
              "nonce": "${UUID.randomUUID()}",
              "nbf": ${notBefore.getEpochSecond().toLong()},
              "claims": {
                "sharing_duration" : ${sharingDuration},
                "id_token": {
                  "acr": {
                    "essential": true,
                    "values": ["urn:cds.au:cdr:3"]
                  }
                },
              "userinfo": {
                "given_name": null,
                "family_name": null
                }
              }
            }
       """.stripIndent()
        }

        Payload tempPayload = new Payload(claims)
        JSONObject jsonPayload = tempPayload.toJSONObject()

        if (!StringUtils.isEmpty(cdrArrangementId)) {
            JSONObject claimObj = jsonPayload.get("claims")
            claimObj.put(TestConstants.CDR_ARRANGEMENT_ID, cdrArrangementId)
        }

        Payload payload = new Payload(jsonPayload.toString())
        JWSSigner signer = new RSASSASigner((PrivateKey) signingKey)

        Security.addProvider(new BouncyCastleProvider())
        JWSObject jwsObject = new JWSObject(header, new Payload(payload.toString()))
        jwsObject.sign(signer)

        Reporter.log("Authorisation Request Object")
        Reporter.log("JWS Header ${jwsObject.header.toString()}")
        Reporter.log("JWS Payload ${jwsObject.payload.toString()}")

        return SignedJWT.parse(jwsObject.serialize())
    }

    /**
     * Get Authorize URL.
     * @return
     */
    String getAuthoriseUrl() {

        Reporter.log("Built Redirection url: ${request.toURI()}")
        return request.toURI().toString()

    }
}
