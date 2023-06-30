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
 * JARM Validation Tests with Response Mode fragment.jwt.
 */
class JarmResponseModeFragmentJwtValidationTests extends AUTest {

    String authResponseUrl
    String responseJwt
    JWTClaimsSet jwtPayload

    @Test (priority = 1)
    void "CDS-569_Verify response_mode fragment jwt navigates to Authorization Flow"() {

        doConsentAuthorisation(ResponseMode.FRAGMENT_JWT, ResponseType.CODE_IDTOKEN, auConfiguration.getAppInfoClientID())
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        jwtPayload = AUJWTGenerator.extractJwt(responseJwt)
    }

    @Test (priority = 1, dependsOnMethods = "CDS-569_Verify response_mode fragment jwt navigates to Authorization Flow")
    void "CDS-571_Verify the '#' Sign' authorization server need to send the authorization response as HTTP redirect to the redirect URI"() {

        Assert.assertTrue(authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[0].contains("#"))
    }

    @Test (priority = 1, dependsOnMethods = "CDS-569_Verify response_mode fragment jwt navigates to Authorization Flow")
    void "CDS-572_Verify in fragment jwt response mode if response_type = code id_token"() {

        Assert.assertNotNull(jwtPayload.getClaim(AUConstants.CODE_KEY))
        Assert.assertNotNull(jwtPayload.getStringClaim(AUConstants.ID_TOKEN_KEY))
        Assert.assertNotNull(jwtPayload.getStringClaim(AUConstants.STATE_KEY))
        Assert.assertTrue(jwtPayload.getClaim(AUConstants.AUDIENCE_KEY)[0].toString()
                .equalsIgnoreCase(auConfiguration.getAppInfoClientID()))
        Assert.assertTrue(jwtPayload.getStringClaim(AUConstants.ISSUER_KEY).equalsIgnoreCase(auConfiguration.getConsentAudienceValue()))
    }

    @Test
    void "CDS-573_Verify in fragment jwt response mode if response_type = code"() {

        doConsentAuthorisation(ResponseMode.FRAGMENT_JWT, ResponseType.CODE, auConfiguration.getAppInfoClientID())
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        jwtPayload = AUJWTGenerator.extractJwt(responseJwt)

        Assert.assertNotNull(jwtPayload.getStringClaim(AUConstants.CODE_KEY))
    }

    @Test
    void "CDS-574_Verify in fragment jwt response mode if response_type = token"() {

        doConsentAuthorisation(ResponseMode.FRAGMENT_JWT, ResponseType.TOKEN, auConfiguration.getAppInfoClientID())
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        jwtPayload = AUJWTGenerator.extractJwt(responseJwt)

        Assert.assertNotNull(jwtPayload.getClaim(AUConstants.ACCESS_TOKEN))
    }

    @Test
    void "CDS-575_Verify in fragment jwt response mode if response_type = none"() {

        def clientId = auConfiguration.getAppInfoClientID()

        //Send PAR request
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "", clientId, auConfiguration.getAppInfoRedirectURL(),
                ResponseType.parse("NONE").toString())

        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //Send Authorisation Request
        String authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(),
                ResponseMode.FRAGMENT_JWT, clientId, ResponseType.parse("NONE"), true).toURI().toString()
        automationResponse = doAuthorisationErrorFlow(authoriseUrl)

        authResponseUrl = automationResponse.currentUrl.get()
        Assert.assertTrue(AUTestUtil.getErrorDescriptionFromUrl(authResponseUrl).contains(AUConstants.INVALID_RESPONSE_TYPE))
    }

    @Test
    void "CDS-576_Verify a User access Token call with the Code received from fragment jwt"() {

        //Consent Authorisation
        doConsentAuthorisation(ResponseMode.FRAGMENT_JWT, ResponseType.CODE_IDTOKEN, auConfiguration.getAppInfoClientID())
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        jwtPayload = AUJWTGenerator.extractJwt(responseJwt)

        authorisationCode = jwtPayload.getStringClaim(AUConstants.CODE_KEY)
        Assert.assertNotNull(authorisationCode)

        //Generate User Access Token
        generateUserAccessToken()
        Assert.assertNotNull(userAccessToken)
    }
}
