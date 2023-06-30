/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.cds.keymanager.test.jarm

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.oauth2.sdk.ResponseMode
import com.nimbusds.oauth2.sdk.ResponseType
import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.request_builder.AUJWTGenerator
import com.wso2.cds.test.framework.utility.AUTestUtil
import org.testng.Assert
import org.testng.annotations.Test

/**
 * JARM Validation Tests with Response Mode jwt.
 */
class JarmResponseModeJwtValidationTests extends AUTest{

    String authResponseUrl
    String responseJwt
    JWTClaimsSet jwtPayload

    @Test
    void "CDS-577_Verify authorisation flow with response method jwt and response type code"() {

        doConsentAuthorisation(ResponseMode.FORM_POST_JWT, ResponseType.CODE, auConfiguration.getAppInfoClientID())
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        jwtPayload = AUJWTGenerator.extractJwt(responseJwt)

        authorisationCode = jwtPayload.getStringClaim(AUConstants.CODE_KEY)
        Assert.assertNotNull(authorisationCode)
        Assert.assertTrue(authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[0].contains("?"))
    }

    @Test
    void "CDS-578_Verify authorisation flow with response method jwt and response type token"() {

        doConsentAuthorisation(ResponseMode.JWT, ResponseType.TOKEN, auConfiguration.getAppInfoClientID())
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        jwtPayload = AUJWTGenerator.extractJwt(responseJwt)

        Assert.assertNotNull(jwtPayload.getClaim(AUConstants.ACCESS_TOKEN))
    }

    @Test
    void "CDS-579_Verify authorisation flow with response method jwt and response type code id_token"() {

        doConsentAuthorisation(ResponseMode.JWT, ResponseType.CODE_IDTOKEN, auConfiguration.getAppInfoClientID())
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        jwtPayload = AUJWTGenerator.extractJwt(responseJwt)

        Assert.assertNotNull(jwtPayload.getStringClaim(AUConstants.CODE_KEY))
        Assert.assertNotNull(jwtPayload.getStringClaim(AUConstants.ID_TOKEN_KEY))
        Assert.assertTrue(authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[0].contains("#"))
    }

    @Test
    void "CDS-580_Verify a User access Token call with the Code received from jwt"() {

        //Consent Authorisation
        doConsentAuthorisation(ResponseMode.JWT, ResponseType.CODE_IDTOKEN, auConfiguration.getAppInfoClientID())
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        jwtPayload = AUJWTGenerator.extractJwt(responseJwt)

        authorisationCode = jwtPayload.getStringClaim(AUConstants.CODE_KEY)
        Assert.assertNotNull(authorisationCode)
        Assert.assertNotNull(jwtPayload.getStringClaim(AUConstants.ID_TOKEN_KEY))
        Assert.assertTrue(authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[0].contains("#"))

        //Generate User Access Token
        generateUserAccessToken(auConfiguration.getAppInfoClientID())
        Assert.assertNotNull(userAccessToken)
    }

    @Test
    void "CDS-581_Verify in jwt response mode if response_type = none"() {

        def clientId = auConfiguration.getAppInfoClientID()

        //Send PAR request
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "", clientId, auConfiguration.getAppInfoRedirectURL(),
                ResponseType.parse("NONE").toString())

        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //Send Authorisation Request
        String authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), ResponseMode.JWT,
                clientId, ResponseType.parse("NONE"), true).toURI().toString()
        automationResponse = doAuthorisationErrorFlow(authoriseUrl)

        authResponseUrl = automationResponse.currentUrl.get()
        Assert.assertTrue(AUTestUtil.getErrorDescriptionFromUrl(authResponseUrl).contains(AUConstants.INVALID_RESPONSE_TYPE))
    }
}
