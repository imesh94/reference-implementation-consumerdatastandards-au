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
 * JARM Validation Tests with Response Mode query.jwt.
 */
class JarmResponseModeQueryJwtValidationTests extends AUTest {

    String authResponseUrl
    String responseJwt
    JWTClaimsSet jwtPayload

    @Test (priority = 1)
    void "CDS-458_Verify response_mode query jwt navigates to Authorization Flow"() {

        doConsentAuthorisation(ResponseMode.QUERY_JWT, ResponseType.CODE, auConfiguration.getAppInfoClientID())
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        jwtPayload = AUJWTGenerator.extractJwt(responseJwt)
    }

    @Test (priority = 1, dependsOnMethods = "CDS-458_Verify response_mode query jwt navigates to Authorization Flow")
    void "CDS-460_Verify the '?' Sign' authorization server need to send the authorization response as HTTP redirect to the redirect URI"() {

        Assert.assertTrue(authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[0].contains("?"))
    }

    @Test (priority = 1, dependsOnMethods = "CDS-458_Verify response_mode query jwt navigates to Authorization Flow")
    void "CDS-461_Verify the decryption of the Response received from query jwt"() {

        Assert.assertNotNull(jwtPayload.getStringClaim(AUConstants.CODE_KEY))
        Assert.assertNotNull(jwtPayload.getStringClaim(AUConstants.STATE_KEY))
        Assert.assertTrue(jwtPayload.getAudience()[0].toString().equalsIgnoreCase(auConfiguration.getAppInfoClientID()))
        Assert.assertTrue(jwtPayload.getIssuer().equalsIgnoreCase(auConfiguration.getConsentAudienceValue()))
    }

    @Test
    void "CDS-587_Verify in query jwt response mode if response_type = code"() {

        doConsentAuthorisation(ResponseMode.QUERY_JWT, ResponseType.CODE, auConfiguration.getAppInfoClientID())
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        jwtPayload = AUJWTGenerator.extractJwt(responseJwt)

        Assert.assertNotNull(jwtPayload.getStringClaim(AUConstants.CODE_KEY))
    }

    @Test
    void "CDS-561_Verify in query jwt response mode if response_type = token"() {

        doConsentAuthorisation(ResponseMode.QUERY_JWT, ResponseType.TOKEN, auConfiguration.getAppInfoClientID())
        authResponseUrl = automationResponse.currentUrl.get()

        //Since the default config enables response_type=token, the response will be a string in json format.
        Assert.assertTrue(authResponseUrl.contains(AUConstants.ACCESS_TOKEN))
    }

    @Test
    void "CDS-562_Verify in query jwt response mode if response_type = code id_token"() {

        doConsentAuthorisation(ResponseMode.QUERY_JWT, ResponseType.CODE_IDTOKEN, auConfiguration.getAppInfoClientID())
        authResponseUrl = automationResponse.currentUrl.get()

        //Since the AU spec supports code id_token, the response will be a string without jwt format.
        Assert.assertTrue(authResponseUrl.contains(AUConstants.CODE_KEY))
        Assert.assertTrue(authResponseUrl.contains(AUConstants.ID_TOKEN_KEY))
    }

    @Test
    void "CDS-563_Verify a User access Token call with the Code received from query jwt"() {

        //Consent Authorisation
        doConsentAuthorisation(ResponseMode.QUERY_JWT, ResponseType.CODE, auConfiguration.getAppInfoClientID())
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
