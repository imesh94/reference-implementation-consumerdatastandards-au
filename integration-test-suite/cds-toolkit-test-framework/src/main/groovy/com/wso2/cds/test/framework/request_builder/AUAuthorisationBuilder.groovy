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

import com.nimbusds.oauth2.sdk.ResponseMode
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConstants
import com.nimbusds.oauth2.sdk.AuthorizationRequest
import com.nimbusds.oauth2.sdk.ResponseType
import com.nimbusds.oauth2.sdk.Scope
import com.nimbusds.oauth2.sdk.id.ClientID
import com.nimbusds.oauth2.sdk.id.State
import com.wso2.openbanking.test.framework.request_builder.SignedObject
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import io.restassured.response.Response
import com.wso2.cds.test.framework.utility.AURestAsRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import java.nio.charset.Charset

/**
 * Class for AU Authorization handling
 * Contains functions for execute authorization request
 */
class AUAuthorisationBuilder {

    private AuthorizationRequest request
    private AUConfigurationService auConfiguration

    private URI endpoint
    private ResponseType responseType
    private ClientID clientID
    private URI redirectURI
    private State state
    private int tppNumber
    private static CodeVerifier codeVerifier = new CodeVerifier()
    AUJWTGenerator generator = new AUJWTGenerator()

    AUAuthorisationBuilder() {
        auConfiguration = new AUConfigurationService()
    }

    /**
     * AU Authorisation Builder for Default Authorisation Flow
     * @param scopes
     * @param sharingDuration
     * @param sendSharingDuration
     * @param cdrArrangementId
     * @param client_id
     */
    AuthorizationRequest getAuthorizationRequest(List<AUAccountScope> scopes, Long sharingDuration, Boolean sendSharingDuration,
                                                 String cdrArrangementId = "", String clientID = getClientID().getValue(),
                                                 ResponseType response_type = getResponseType()) {

        AUJWTGenerator generator = new AUJWTGenerator()
        String scopeString = "openid ${String.join(" ", scopes.collect({ it.scopeString }))}"

        String requestObjectClaims = generator.getRequestObjectClaim(scopes, sharingDuration, sendSharingDuration, cdrArrangementId,
                getRedirectURI().toString(), null, response_type.toString(), true, getState().toString())

        request = new AuthorizationRequest.Builder(response_type, new ClientID(clientID))
                .responseType(response_type)
                .endpointURI(getEndpoint())
                .redirectionURI(getRedirectURI())
                .requestObject(generator.getSignedAuthRequestObject(requestObjectClaims))
                .scope(new Scope(scopeString))
                .state(getState())
                .codeChallenge(getCodeVerifier(), CodeChallengeMethod.S256)
                .customParameter("prompt", "login")
                .build()
        return request
    }

    /**
     * AU Authorisation Builder for Pushed Authorisation Flow
     * @param scopes  scope of the request
     * @param requestUri request uri
     * @param clientID client id of the application
     * @param isStateParamPresent state parameter is present or not
     * @return AuthorizationRequest
     */
    AuthorizationRequest getAuthorizationRequest(List<AUAccountScope> scopes, URI requestUri,
                                                 String clientID = getClientID().getValue(),
                                                 boolean isStateParamPresent = true) {
        String scopeString = "openid ${String.join(" ", scopes.collect({ it.scopeString }))}"

        if(isStateParamPresent) {
            request = new AuthorizationRequest.Builder(getResponseType(), new ClientID(clientID))
                    .responseType(ResponseType.parse("code"))
                    .scope(new Scope(scopeString))
                    .requestURI(requestUri)
                    .redirectionURI(getRedirectURI())
                    .state(getState())
                    .codeChallenge(getCodeVerifier(), CodeChallengeMethod.S256)
                    .endpointURI(getEndpoint())
                    .customParameter("prompt", "login")
                    .build()
        } else {
            request = new AuthorizationRequest.Builder(getResponseType(), new ClientID(clientID))
                    .responseType(ResponseType.parse("code"))
                    .scope(new Scope(scopeString))
                    .requestURI(requestUri)
                    .redirectionURI(getRedirectURI())
                    .codeChallenge(getCodeVerifier(), CodeChallengeMethod.S256)
                    .endpointURI(getEndpoint())
                    .customParameter("prompt", "login")
                    .build()
        }

        return request
    }

    /**
     * AU Authorisation Builder for Pushed Authorisation Flow
     * @param scopes
     * @param requestUri
     * @param client_id
     * @param redirect_uri
     * @return
     */
    AuthorizationRequest getAuthorizationRequest(List<AUAccountScope> scopes, URI requestUri, String client_id, String redirect_uri) {
        String scopeString = "openid ${String.join(" ", scopes.collect({ it.scopeString }))}"
        request = new AuthorizationRequest.Builder(getResponseType(), new ClientID(client_id))
                .responseType(ResponseType.parse("code"))
                .scope(new Scope(scopeString))
                .requestURI(requestUri)
                .redirectionURI(redirect_uri.toURI())
                .endpointURI(getEndpoint())
                .customParameter("prompt", "login")
                .build()
        return request
    }

    /**
     * Get authorization request with response_mode.
     * @param scopes
     * @param sharingDuration
     * @param sendSharingDuration
     * @param cdrArrangementId
     * @param clientID
     * @param response_type
     * @param response_mode
     * @return
     */
    AuthorizationRequest getAuthorizationRequest(List<AUAccountScope> scopes, URI requestUri, ResponseMode response_mode,
                                                 String clientID = getClientID().getValue(),
                                                 ResponseType responseType = getResponseType(),
                                                 boolean isStateParamPresent = true) {

        String scopeString = "openid ${String.join(" ", scopes.collect({ it.scopeString }))}"

        if(isStateParamPresent) {
            request = new AuthorizationRequest.Builder(getResponseType(), new ClientID(clientID))
                    .responseType(responseType)
                    .responseMode(response_mode)
                    .scope(new Scope(scopeString))
                    .requestURI(requestUri)
                    .redirectionURI(getRedirectURI())
                    .state(getState())
                    .endpointURI(getEndpoint())
                    .customParameter("prompt", "login")
                    .build()
        } else {
            request = new AuthorizationRequest.Builder(getResponseType(), new ClientID(clientID))
                    .responseType(responseType)
                    .responseMode(response_mode)
                    .scope(new Scope(scopeString))
                    .requestURI(requestUri)
                    .redirectionURI(getRedirectURI())
                    .endpointURI(getEndpoint())
                    .customParameter("prompt", "login")
                    .build()
        }
        return request
    }

    /**
     * AU Authorisation Builder for Pushed Authorisation Flow
     * @param headerString
     * @param scopes
     * @param sharingDuration
     * @param sendSharingDuration
     * @param cdrArrangementId
     * @param clientId
     * @return
     */
    Response doPushAuthorisationRequest(List<AUAccountScope> scopes, long sharingDuration,
                                        boolean sendSharingDuration, String cdrArrangementId,
                                        String clientId = getClientID().getValue(),
                                        String redirectUrl = getRedirectURI().toString(),
                                        String responseType = getResponseType().toString(),
                                        String state = getState().toString(),
                                        boolean isStateParamRequired = true) {

        Response parResponse

        String assertionString = generator.getClientAssertionJwt(clientId)

        def bodyContent = [
                (AUConstants.CLIENT_ID_KEY)            : (clientId),
                (AUConstants.CLIENT_ASSERTION_TYPE_KEY): (AUConstants.CLIENT_ASSERTION_TYPE),
                (AUConstants.CLIENT_ASSERTION_KEY)     : assertionString,
        ]

        String requestObjectClaims

        if(isStateParamRequired) {
            requestObjectClaims = generator.getRequestObjectClaim(scopes, sharingDuration, sendSharingDuration,
                    cdrArrangementId, redirectUrl, clientId, responseType, true, state)

            parResponse = AURestAsRequestBuilder.buildRequest()
                    .contentType(AUConstants.ACCESS_TOKEN_CONTENT_TYPE)
                    .formParams(bodyContent)
                    .formParams(AUConstants.REQUEST_KEY, generator.getSignedAuthRequestObject(requestObjectClaims).serialize())
                    .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                    .post(AUConstants.PAR_ENDPOINT)
        } else {

            requestObjectClaims = generator.getRequestObjectClaim(scopes, sharingDuration, sendSharingDuration,
                    cdrArrangementId, redirectUrl, clientId, responseType, false, state)

            parResponse = AURestAsRequestBuilder.buildRequest()
                    .contentType(AUConstants.ACCESS_TOKEN_CONTENT_TYPE)
                    .formParams(bodyContent)
                    .formParams(AUConstants.REQUEST_KEY, generator.getSignedAuthRequestObject(requestObjectClaims).serialize())
                    .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                    .post(AUConstants.PAR_ENDPOINT)
        }

        return parResponse
    }

    /**
     * Push Authorisation Request with private_key_jwt authentication method.
     * @param scopes
     * @param sharingDuration
     * @param sendSharingDuration
     * @param cdrArrangementId
     * @param clientId
     * @return
     */
    Response doPushAuthorisationRequestWithPkjwt(List<AUAccountScope> scopes, long sharingDuration,
                                                 boolean sendSharingDuration, String cdrArrangementId,
                                                 String clientId = getClientID().getValue(),
                                                 String redirectUrl = getRedirectURI().toString(),
                                                 String responseType = getResponseType().toString()) {

        AUJWTGenerator generator = new AUJWTGenerator()

        String assertionString = new SignedObject().getJwt()

        def bodyContent = [(AUConstants.CLIENT_ID_KEY)            : (getClientID().getValue()),
                           (AUConstants.CLIENT_ASSERTION_TYPE_KEY): (AUConstants.CLIENT_ASSERTION_TYPE),
                           (AUConstants.CLIENT_ASSERTION_KEY)     : assertionString,
                           "cdr_arrangement_id"                   : cdrArrangementId]

        String requestObjectClaims = generator.getRequestObjectClaim(scopes, sharingDuration, sendSharingDuration,
                cdrArrangementId, redirectUrl, clientId, responseType, false, state.toString())

        def parResponse = AURestAsRequestBuilder.buildRequest()
                .contentType(AUConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .formParams(bodyContent)
                .formParams(AUConstants.REQUEST_KEY, generator.getSignedAuthRequestObject(requestObjectClaims).serialize())
                .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                .post(AUConstants.PAR_ENDPOINT)

        return parResponse
    }

    /**
     * Sharing Arrangement Revocation with private_key_jwt authentication method.
     * @param applicationAccessToken
     * @param cdrArrangementId
     * @param clientId
     * @return
     */
    Response doArrangementRevocationWithPkjwt(String applicationAccessToken, String cdrArrangementId,
                                              String clientId = getClientID().getValue()) {
        AUJWTGenerator generator = new AUJWTGenerator()
        String assertionString = new SignedObject().getJwt()

        def bodyContent = [
                (AUConstants.CLIENT_ID_KEY)            : (clientId),
                (AUConstants.CLIENT_ASSERTION_TYPE_KEY): (AUConstants.CLIENT_ASSERTION_TYPE),
                (AUConstants.CLIENT_ASSERTION_KEY)     : assertionString,
                "cdr_arrangement_id"                   : cdrArrangementId]

        def revocationResponse = AURestAsRequestBuilder.buildRequest()
                .contentType(AUConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .header(AUConstants.X_V_HEADER, AUConstants.CDR_ENDPOINT_VERSION)
                .header(AUConstants.AUTHORIZATION_HEADER_KEY, "${AUConstants.AUTHORIZATION_BEARER_TAG} ${applicationAccessToken}")
                .formParams(bodyContent)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CDR_ARRANGEMENT))
                .delete("${AUConstants.CDR_ARRANGEMENT_ENDPOINT}/${cdrArrangementId}")

        return revocationResponse
    }

    /**
     * Provide authorization request
     * @return
     */
    AuthorizationRequest getRequest() {
        return request
    }

    /**
     * Getter for other functions
     */
    int getTppNumber() {
        return tppNumber;
    }

    private ResponseType getResponseType() {
        if (responseType == null) {
            responseType = new ResponseType("code")
        }
        return responseType
    }

    ClientID getClientID() {
        if (clientID == null) {
            clientID = new ClientID(auConfiguration.getAppInfoClientID(tppNumber))
        }
        return clientID
    }

    private URI getRedirectURI() {
        if (redirectURI == null) {
            redirectURI = new URI(auConfiguration.getAppInfoRedirectURL(tppNumber))
        }
        return redirectURI
    }

    private State getState() {
        if (state == null) {
            state = new State(UUID.randomUUID().toString())
        }
        return state
    }

    private URI getEndpoint() {
        if (endpoint == null) {
            endpoint = new URI("${auConfiguration.getServerAuthorisationServerURL()}/oauth2/authorize/")
        }
        return endpoint
    }

    /**
     * Setter of parameters
     */
    void setEndpoint(String endpoint) {
        this.endpoint = new URI(endpoint)
    }

    void setResponseType(String responseType) {
        this.responseType = new ResponseType(responseType)
    }

    void setClientID(String clientID) {
        this.clientID = new ClientID(clientID)
    }

    void setTppNumber(int tpp) {
        this.tppNumber = tpp
    }

    void setRedirectURI(String redirectURI) {
        this.redirectURI = new URI(redirectURI)
    }

    void setState(String state) {
        this.state = new State(state)
    }
  
    /**
     * Get Code Verifier.
     * @return
     *  */
    CodeVerifier getCodeVerifier() {
        return codeVerifier
    }

    /**
     * AU Authorisation Builder for Pushed Authorisation Flow with String value for Sharing Duration.
     * @param headerString
     * @param scopes
     * @param sharingDuration
     * @param sendSharingDuration
     * @param cdrArrangementId
     * @param clientId
     * @return
     */
    Response doPushAuthRequestForStringSharingDuration(List<AUAccountScope> scopes, String sharingDuration,
                                                       String cdrArrangementId,
                                                       String clientId = getClientID().getValue(),
                                                       String redirectUrl = getRedirectURI().toString(),
                                                       String responseType = getResponseType().toString()) {


        AUJWTGenerator generator = new AUJWTGenerator()
        String scopeString = "openid ${String.join(" ", scopes.collect({ it.scopeString }))}"
        Response parResponse

        String assertionString = generator.getClientAssertionJwt(clientId)

        def bodyContent = [
                (AUConstants.CLIENT_ID_KEY)            : (clientId),
                (AUConstants.CLIENT_ASSERTION_TYPE_KEY): (AUConstants.CLIENT_ASSERTION_TYPE),
                (AUConstants.CLIENT_ASSERTION_KEY)     : assertionString,
        ]

            parResponse = AURestAsRequestBuilder.buildRequest()
                    .contentType(AUConstants.ACCESS_TOKEN_CONTENT_TYPE)
                    .formParams(bodyContent)
                    .formParams(AUConstants.REQUEST_KEY, generator.getSignedAuthRequestObjectForStringSharingDuration(scopeString,
                            sharingDuration, cdrArrangementId, redirectUrl, clientId, responseType).serialize())
                    .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                    .post(AUConstants.PAR_ENDPOINT)

        return parResponse
    }

    /**
     * AU Authorisation Builder for Pushed Authorisation Flow without Scopes.
     * @param headerString
     * @param sharingDuration
     * @param sendSharingDuration
     * @param cdrArrangementId
     * @param clientId
     * @return
     */
    Response doPushAuthRequestWithoutScopes(long sharingDuration, String cdrArrangementId,
                                            String clientId = getClientID().getValue(),
                                            String redirectUrl = getRedirectURI().toString(),
                                            String responseType = getResponseType().toString()) {

        AUJWTGenerator generator = new AUJWTGenerator()
        Response parResponse

        String assertionString = generator.getClientAssertionJwt(clientId)

        def bodyContent = [
                (AUConstants.CLIENT_ID_KEY)            : (clientId),
                (AUConstants.CLIENT_ASSERTION_TYPE_KEY): (AUConstants.CLIENT_ASSERTION_TYPE),
                (AUConstants.CLIENT_ASSERTION_KEY)     : assertionString,
        ]

        parResponse = AURestAsRequestBuilder.buildRequest()
                .contentType(AUConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .formParams(bodyContent)
                .formParams(AUConstants.REQUEST_KEY, generator.getSignedAuthRequestObjectWithoutScopes(sharingDuration,
                        cdrArrangementId, redirectUrl, clientId, responseType).serialize())
                .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                .post(AUConstants.PAR_ENDPOINT)

        return parResponse
    }

    /**
     * AU Push Authorisation Request Builder without Request Object.
     * @param clientId
     * @return
     */
    Response doPushAuthorisationRequestWithoutRequestObject(String clientId = getClientID().getValue()) {

        AUJWTGenerator generator = new AUJWTGenerator()
        Response parResponse

        String assertionString = generator.getClientAssertionJwt(clientId)

        def bodyContent = [
                (AUConstants.CLIENT_ID_KEY)            : (clientId),
                (AUConstants.CLIENT_ASSERTION_TYPE_KEY): (AUConstants.CLIENT_ASSERTION_TYPE),
                (AUConstants.CLIENT_ASSERTION_KEY)     : assertionString,
        ]

        parResponse = AURestAsRequestBuilder.buildRequest()
                    .contentType(AUConstants.ACCESS_TOKEN_CONTENT_TYPE)
                    .formParams(bodyContent)
                    .formParams(AUConstants.REQUEST_KEY, "")
                    .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                    .post(AUConstants.PAR_ENDPOINT)

        return parResponse
    }

    /**
     * AU Authorisation Builder for Pushed Authorisation Flow with unsigned Request Object.
     * @param scopes - List of scopes
     * @param sharingDuration - Sharing Duration
     * @param cdrArrangementId - CDR Arrangement Id
     * @param clientId - Client Id
     * @param redirectUrl - Redirect Url
     * @param responseType - Response Type
     * @return - Response
     */
    Response doPushAuthorisationRequestWithUnsignedRequestObject(List<AUAccountScope> scopes, Long sharingDuration,
                                                                 boolean sendSharingDuration,
                                                                 String cdrArrangementId,
                                                                 String clientId = getClientID().getValue(),
                                                                 String redirectUrl = getRedirectURI().toString(),
                                                                 String responseType = getResponseType().toString()) {


        AUJWTGenerator generator = new AUJWTGenerator()
        Response parResponse

        String assertionString = generator.getClientAssertionJwt(clientId)

        def bodyContent = [
                (AUConstants.CLIENT_ID_KEY)            : (clientId),
                (AUConstants.CLIENT_ASSERTION_TYPE_KEY): (AUConstants.CLIENT_ASSERTION_TYPE),
                (AUConstants.CLIENT_ASSERTION_KEY)     : assertionString,
        ]

        String claims = generator.getRequestObjectClaim(scopes, sharingDuration, sendSharingDuration,
                cdrArrangementId, redirectUrl, clientId, responseType, true, state.toString())

        parResponse = AURestAsRequestBuilder.buildRequest()
                .contentType(AUConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .formParams(bodyContent)
                .formParams(AUConstants.REQUEST_KEY, claims)
                .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                .post(AUConstants.PAR_ENDPOINT)

        return parResponse
    }

    /**
     * AU Authorisation Builder for Pushed Authorisation Flow with modified request object.
     * @param scopes List of scopes
     * @param requestObjectClaims Modified request object claims
     * @param clientId  Client ID
     * @param isStateParamRequired Boolean value to check if state param is required
     * @return Response
     */
    Response doPushAuthorisationRequest(String requestObjectClaims, String clientId = getClientID().getValue(),
                                        boolean isStateParamRequired = true, String algorithm = null) {

        Response parResponse

        String assertionString = generator.getClientAssertionJwt(clientId)

        def bodyContent = [
                (AUConstants.CLIENT_ID_KEY)            : (clientId),
                (AUConstants.CLIENT_ASSERTION_TYPE_KEY): (AUConstants.CLIENT_ASSERTION_TYPE),
                (AUConstants.CLIENT_ASSERTION_KEY)     : assertionString,
        ]

        if (algorithm != null) {
            generator.setSigningAlgorithm(algorithm)
        }

        if (isStateParamRequired) {

            parResponse = AURestAsRequestBuilder.buildRequest()
                    .contentType(AUConstants.ACCESS_TOKEN_CONTENT_TYPE)
                    .formParams(bodyContent)
                    .formParams(AUConstants.REQUEST_KEY, generator.getSignedAuthRequestObject(requestObjectClaims).serialize())
                    .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                    .post(AUConstants.PAR_ENDPOINT)
        } else {

            parResponse = AURestAsRequestBuilder.buildRequest()
                    .contentType(AUConstants.ACCESS_TOKEN_CONTENT_TYPE)
                    .formParams(bodyContent)
                    .formParams(AUConstants.REQUEST_KEY, generator.getSignedAuthRequestObject(requestObjectClaims).serialize())
                    .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                    .post(AUConstants.PAR_ENDPOINT)
        }

        return parResponse
    }
}
