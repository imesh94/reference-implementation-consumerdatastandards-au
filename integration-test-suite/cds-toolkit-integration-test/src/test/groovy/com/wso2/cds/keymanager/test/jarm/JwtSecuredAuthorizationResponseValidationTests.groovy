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

import com.nimbusds.jose.JWSHeader
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.oauth2.sdk.ResponseMode
import com.nimbusds.oauth2.sdk.ResponseType
import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.constant.AUAccountProfile
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.request_builder.AUJWTGenerator
import org.testng.Assert
import org.testng.annotations.Test
import org.testng.annotations.BeforeClass
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Jwt Secured Authorization Response Validation Tests.
 */
class JwtSecuredAuthorizationResponseValidationTests extends AUTest {

    String authResponseUrl
    String responseJwt
    JWTClaimsSet jwtPayload
    String clientId
    JWSHeader jwtHeaders


    @BeforeClass (alwaysRun = true)
    void "Send Authorisation Request"() {

        doConsentAuthorisation(ResponseMode.JWT, ResponseType.CODE, auConfiguration.getAppInfoClientID())
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        jwtPayload = AUJWTGenerator.extractJwt(responseJwt)
        jwtHeaders = AUJWTGenerator.extractJwtHeaders(responseJwt)
    }

    @Test
    void "CDS-565_Verify the JWT response contains the client id as aud value"() {

        def audience = jwtPayload.getAudience()[0].toString()
        Assert.assertTrue(audience.equalsIgnoreCase(auConfiguration.getAppInfoClientID()))
    }

    @Test
    void "CDS-564_Verify the JWT response contains the valid issuer of the authorisation server"() {

        def issuer = jwtPayload.getIssuer()
        Assert.assertTrue(issuer.equalsIgnoreCase(auConfiguration.getConsentAudienceValue()))
    }

    @Test
    void "CDS-566_Verify the JWT response contains a future expiration date"() {

        Long exp = jwtPayload.getExpirationTime().getTime() / 1000
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(exp, 0, ZoneOffset.UTC)
        Assert.assertTrue(localDateTime >= LocalDateTime.now())
    }

    @Test
    void "CDS-567_Verify the alg of the JWT response not be none"() {

        def alg = jwtHeaders.algorithm.toString()
        Assert.assertFalse(alg.equals("none"))
        Assert.assertTrue(alg.equals(auConfiguration.getCommonSigningAlgorithm()))
    }

    @Test
    void "CDS-568_Verify the JWT response does not contains state param if it is not included in request"() {

        doConsentAuthorisation(ResponseMode.JWT, ResponseType.CODE, auConfiguration.getAppInfoClientID(),
                AUAccountProfile.INDIVIDUAL, false)
        authResponseUrl = automationResponse.currentUrl.get()
        responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        jwtPayload = AUJWTGenerator.extractJwt(responseJwt)
        Assert.assertNull(jwtPayload.getStringClaim(AUConstants.ALGORITHM_KEY))
    }
}
