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

import com.nimbusds.jwt.SignedJWT
import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.nimbusds.oauth2.sdk.AuthorizationCode
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant
import com.nimbusds.oauth2.sdk.AuthorizationGrant
import com.nimbusds.oauth2.sdk.TokenErrorResponse
import com.nimbusds.oauth2.sdk.TokenRequest
import com.nimbusds.oauth2.sdk.TokenResponse
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication
import com.nimbusds.oauth2.sdk.auth.PrivateKeyJWT
import com.nimbusds.oauth2.sdk.http.HTTPRequest
import com.nimbusds.oauth2.sdk.http.HTTPResponse
import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.test.framework.model.AccessTokenJwtDto
import com.wso2.openbanking.test.framework.model.ApplicationAccessTokenDto
import com.wso2.openbanking.test.framework.request.AccessToken
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification

import java.nio.charset.Charset
import java.util.logging.Logger

/**
 * Request Builder Util.
 */
class AURequestBuilder {
    static log = Logger.getLogger(AURequestBuilder.class.toString())
    private AccessTokenJwtDto accessTokenJWTDTO

    static RequestSpecification buildBasicRequest(String userAccessToken, int version) {

        return TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, version)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")

    }

    static RequestSpecification buildBasicRequestWithCustomerIP(String userAccessToken, int version) {

        return TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, version)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
    }

    static RequestSpecification buildBasicRequestWithoutAuthorisationHeader(int version) {

        return TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, version)
    }

    /**
     * Get User Access Token From Authorization Code.
     *
     * @param code authorisation code
     * @return token response
     */
    static AccessTokenResponse getUserToken(String code) {

        def config = ConfigParser.getInstance()

        AuthorizationCode grant = new AuthorizationCode(code)
        URI callbackUri = new URI(config.getRedirectUrl())
        AuthorizationGrant codeGrant = new AuthorizationCodeGrant(grant, callbackUri)

        String assertionString = new AccessTokenJwtDto().getJwt()

        ClientAuthentication clientAuth = new PrivateKeyJWT(SignedJWT.parse(assertionString))

        URI tokenEndpoint = new URI("${config.getBaseUrl()}${TestConstants.TOKEN_ENDPOINT}")

        TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, codeGrant)

        HTTPRequest httpRequest = request.toHTTPRequest()

        def response = TestSuite.buildRequest()
                .contentType(TestConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .body(httpRequest.query)
                .post(tokenEndpoint)

        HTTPResponse httpResponse = new HTTPResponse(response.statusCode())
        httpResponse.setContentType(response.contentType())
        httpResponse.setContent(response.getBody().print())

        return TokenResponse.parse(httpResponse).toSuccessResponse()

    }

    /**
     * Get User Access Token Error Response From Inactive Authorization Code.
     *
     * @param code authorisation code
     * @return token error response
     */
    static TokenErrorResponse getUserTokenErrorResponse(String code) {

        def config = ConfigParser.getInstance()

        AuthorizationCode grant = new AuthorizationCode(code)
        URI callbackUri = new URI(config.getRedirectUrl())
        AuthorizationGrant codeGrant = new AuthorizationCodeGrant(grant, callbackUri)

        String assertionString = new AccessTokenJwtDto().getJwt()

        ClientAuthentication clientAuth = new PrivateKeyJWT(SignedJWT.parse(assertionString))

        URI tokenEndpoint = new URI("${config.getBaseUrl()}${TestConstants.TOKEN_ENDPOINT}")

        TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, codeGrant)

        HTTPRequest httpRequest = request.toHTTPRequest()

        def response = TestSuite.buildRequest()
                .contentType(TestConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .body(httpRequest.query)
                .post(tokenEndpoint)

        HTTPResponse httpResponse = new HTTPResponse(response.statusCode())
        httpResponse.setContentType(response.contentType())
        httpResponse.setContent(response.getBody().print())

        return TokenResponse.parse(httpResponse).toErrorResponse()

    }

    /**
     * Get Application Access Token.
     *
     * @param scopes scopes for token
     * @return access token
     */
    static String getApplicationToken(List<String> scopes, String clientId) {

        def tokenDTO = new ApplicationAccessTokenDto()
        tokenDTO.setScopes(scopes)
        def tokenResponse = AccessToken.getApplicationAccessToken(tokenDTO, clientId)
        def accessToken = TestUtil.parseResponseBody(tokenResponse, "access_token")

        log.info("Got access token $accessToken")

        return accessToken

    }

    /**
     * Build Introspection Request
     *
     * @param token access token
     * @return Introspection Request Specfication
     */
    static RequestSpecification buildIntrospectionRequest(String token) {
        return TestSuite.buildRequest()
                .contentType(ContentType.URLENC)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Basic ${getBasicAuthorizationHeader()}")
                .body("token=${token}")
                .baseUri(ConfigParser.instance.authorisationServerUrl)
    }

    /**
     * Generate Base64(clientID:ClientSectret)
     *
     * @return basic authorization header value
     */
    static String getBasicAuthorizationHeader() {
        String headerString = ConfigParser.instance.clientId + ":" + ConfigParser.instance.clientSecret
        return Base64.encoder.encodeToString(headerString.getBytes(Charset.forName("UTF-8")))
    }

    /**
     * Get token response.
     *
     * @param scopes : scopes for token
     * @return access token response
     */
    static Response getTokenResponse(List<String> scopes, String clientId) {

        def tokenDTO = new ApplicationAccessTokenDto()
        tokenDTO.setScopes(scopes)
        return AccessToken.getApplicationAccessToken(tokenDTO, clientId)
    }

    /**
     * Build Introspection Request for Revoke Access Token
     *
     * @param token access token
     * @return Introspection Request Specification
     */
    static RequestSpecification buildRevokeIntrospectionRequest(String token) {
        return TestSuite.buildRequest()
                .contentType(ContentType.URLENC)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Basic ${getBasicAuthorizationHeader()}")
                .body("token=${token}&token_type_hint=access_token")
                .baseUri(ConfigParser.instance.authorisationServerUrl)
    }
}
