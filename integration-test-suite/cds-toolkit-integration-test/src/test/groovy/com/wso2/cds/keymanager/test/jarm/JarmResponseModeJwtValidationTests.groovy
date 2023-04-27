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
    HashMap<String, String> mapPayload

    @Test
    void "CDS-577_Verify authorisation flow with response method jwt and response type code"() {

        doConsentAuthorisation(ResponseMode.JWT, ResponseType.CODE)
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        mapPayload = AUJWTGenerator.extractJwt(responseJwt)

        authorisationCode = mapPayload.get(AUConstants.CODE_KEY)
        Assert.assertNotNull(authorisationCode)
    }

    @Test
    void "CDS-578_Verify authorisation flow with response method jwt and response type token"() {

        doConsentAuthorisation(ResponseMode.JWT, ResponseType.TOKEN)
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        mapPayload = AUJWTGenerator.extractJwt(responseJwt)

        authorisationCode = mapPayload.get(AUConstants.CODE_KEY)
        Assert.assertNotNull(authorisationCode)
    }

    @Test
    void "CDS-579_Verify authorisation flow with response method jwt and response type code id_token"() {

        doConsentAuthorisation(ResponseMode.JWT, ResponseType.CODE_IDTOKEN)
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        mapPayload = AUJWTGenerator.extractJwt(responseJwt)

        Assert.assertNotNull(mapPayload.get(AUConstants.CODE_KEY))
        Assert.assertNotNull(mapPayload.get(AUConstants.ID_TOKEN_KEY))
    }

    @Test
    void "CDS-580_Verify a User access Token call with the Code received from jwt"() {

        //Consent Authorisation
        doConsentAuthorisation(ResponseMode.JWT, ResponseType.CODE_IDTOKEN)
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        mapPayload = AUJWTGenerator.extractJwt(responseJwt)

        Assert.assertNotNull(mapPayload.get(AUConstants.CODE_KEY))
        Assert.assertNotNull(mapPayload.get(AUConstants.ID_TOKEN_KEY))

        //Generate User Access Token
        generateUserAccessToken()
        Assert.assertNotNull(userAccessToken)
    }

    @Test
    void "CDS-581_Verify in jwt response mode if response_type = none"() {

        doConsentAuthorisation(ResponseMode.JWT, ResponseType.parse("NONE"))
        authResponseUrl = automationResponse.currentUrl.get()
        Assert.assertTrue(AUTestUtil.getDecodedUrl(authResponseUrl).contains(AUConstants.UNSUPPORTED_RESPONSE_MODE))
    }
}
