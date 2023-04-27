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
 * JARM Validation Tests with Response Mode query.jwt.
 */
class JarmResponseModeQueryJwtValidationTests extends AUTest {

    String authResponseUrl
    String responseJwt
    HashMap<String, String> mapPayload

    @Test
    void "CDS-458_Verify response_mode query jwt navigates to Authorization Flow"() {

        doConsentAuthorisation(ResponseMode.QUERY_JWT)
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        mapPayload = AUJWTGenerator.extractJwt(responseJwt)
    }

    @Test (dependsOnMethods = "CDS-458_Verify response_mode query jwt navigates to Authorization Flow")
    void "CDS-460_Verify the '?' Sign' authorization server need to send the authorization response as HTTP redirect to the redirect URI"() {

        Assert.assertTrue(authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[0].contains("?"))
    }

    @Test (dependsOnMethods = "CDS-458_Verify response_mode query jwt navigates to Authorization Flow")
    void "CDS-461_Verify the decryption of the Response received from query jwt"() {

        Assert.assertNotNull(mapPayload.get(AUConstants.CODE_KEY))
        Assert.assertNotNull(mapPayload.get(AUConstants.STATE_KEY))
        Assert.assertTrue(mapPayload.get(AUConstants.AUDIENCE_KEY).equalsIgnoreCase(auConfiguration.getAppInfoClientID()))
        Assert.assertTrue(mapPayload.get(AUConstants.ISSUER_KEY).equalsIgnoreCase(auConfiguration.getConsentAudienceValue()))
    }

    @Test
    void "CDS-587_Verify in query jwt response mode if response_type = code"() {

        doConsentAuthorisation(ResponseMode.QUERY_JWT, ResponseType.CODE)
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        mapPayload = AUJWTGenerator.extractJwt(responseJwt)

        Assert.assertNotNull(mapPayload.get(AUConstants.CODE_KEY))
    }

    @Test
    void "CDS-561_Verify in query jwt response mode if response_type = token"() {

        doConsentAuthorisation(ResponseMode.QUERY_JWT, ResponseType.TOKEN)
        authResponseUrl = automationResponse.currentUrl.get()
        Assert.assertTrue(AUTestUtil.getDecodedUrl(authResponseUrl).contains(AUConstants.UNSUPPORTED_RESPONSE_MODE))
    }

    @Test
    void "CDS-562_Verify in query jwt response mode if response_type = code id_token"() {

        doConsentAuthorisation(ResponseMode.QUERY_JWT, ResponseType.CODE_IDTOKEN)
        authResponseUrl = automationResponse.currentUrl.get()
        Assert.assertTrue(AUTestUtil.getDecodedUrl(authResponseUrl).contains(AUConstants.UNSUPPORTED_RESPONSE_MODE))
    }

    @Test
    void "CDS-563_Verify a User access Token call with the Code received from query jwt"() {

        //Consent Authorisation
        doConsentAuthorisation(ResponseMode.QUERY_JWT, ResponseType.CODE)
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        mapPayload = AUJWTGenerator.extractJwt(responseJwt)

        authorisationCode = mapPayload.get(AUConstants.CODE_KEY)
        Assert.assertNotNull(authorisationCode)

        //Generate User Access Token
        generateUserAccessToken()
        Assert.assertNotNull(userAccessToken)
    }
}
