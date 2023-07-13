/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.keymanager.test.par

import com.nimbusds.oauth2.sdk.ResponseMode
import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.request_builder.AUJWTGenerator
import com.wso2.cds.test.framework.utility.AUTestUtil
import org.testng.Assert
import org.testng.annotations.Test

import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * This class contains the test cases to validate the request object of the push authorisation request.
 */
class RequestObjectValidationTest extends AUTest {

    AUJWTGenerator generator = new AUJWTGenerator()

    @Test
    void "OB-1231_Initiate push authorisation flow without request object parameter"() {

        response = auAuthorisationBuilder.doPushAuthorisationRequestWithoutRequestObject(auConfiguration.getAppInfoClientID())

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION), AUConstants.UNABLE_TO_DECODE_JWT)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST_OBJECT)
    }

    @Test
    void "OB-1232_Initiate push authorisation flow with unsigned request object"() {

        def response = auAuthorisationBuilder.doPushAuthorisationRequestWithUnsignedRequestObject(scopes,
                AUConstants.DEFAULT_SHARING_DURATION, true, "")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION),
                AUConstants.UNABLE_TO_DECODE_JWT)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST_OBJECT)
    }

    @Test (priority = 1)
    void "OB-1233_Initiate authorisation consent flow with 'RS256' signature algorithm"() {

        String claims = generator.getRequestObjectClaim(scopes, AUConstants.DEFAULT_SHARING_DURATION, true, "",
        auConfiguration.getAppInfoRedirectURL(), auConfiguration.getAppInfoClientID(),
                auAuthorisationBuilder.getResponseType().toString(), true,
                auAuthorisationBuilder.getState().toString())

        def response = auAuthorisationBuilder.doPushAuthorisationRequest(claims,
                auConfiguration.getAppInfoClientID(), true, "RS256")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION),
                AUConstants.INVALID_ALGORITHM)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST_OBJECT)
    }

    @Test (priority = 1)
    void "OB-1234_Initiate authorisation consent flow with 'PS512' signature algorithm"() {

        String claims = generator.getRequestObjectClaim(scopes, AUConstants.DEFAULT_SHARING_DURATION, true, "",
                auConfiguration.getAppInfoRedirectURL(), auConfiguration.getAppInfoClientID(),
                auAuthorisationBuilder.getResponseType().toString(), true,
                auAuthorisationBuilder.getState().toString())

        def response = auAuthorisationBuilder.doPushAuthorisationRequest(claims,
                auConfiguration.getAppInfoClientID(), true, "PS512")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION),
                AUConstants.INVALID_ALGORITHM)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST_OBJECT)

    }

    @Test
    void "OB-1241_Initiate authorisation consent flow without 'aud' claim in request object"() {

        String claims = generator.getRequestObjectClaim(scopes, AUConstants.DEFAULT_SHARING_DURATION, true, "",
                auConfiguration.getAppInfoRedirectURL(), auConfiguration.getAppInfoClientID(),
                auAuthorisationBuilder.getResponseType().toString(), true,
                auAuthorisationBuilder.getState().toString())

        String modifiedClaimSet = generator.removeClaimsFromRequestObject(claims, "aud")

        def response = auAuthorisationBuilder.doPushAuthorisationRequest(modifiedClaimSet)

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION), AUConstants.MISSING_AUD_VALUE)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST_OBJECT)
    }

    @Test
    void "OB-1242_Initiate authorisation consent flow without 'iss' claim in request object"() {

        String claims = generator.getRequestObjectClaim(scopes, AUConstants.DEFAULT_SHARING_DURATION, true, "",
                auConfiguration.getAppInfoRedirectURL(), auConfiguration.getAppInfoClientID(),
                auAuthorisationBuilder.getResponseType().toString(), true,
                auAuthorisationBuilder.getState().toString())

        String modifiedClaimSet = generator.removeClaimsFromRequestObject(claims, "iss")

        def response = auAuthorisationBuilder.doPushAuthorisationRequest(modifiedClaimSet)

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION), AUConstants.MISSING_ISS_VALUE)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST_OBJECT)
    }

    @Test
    void "OB-1243_Initiate authorisation consent flow without 'exp' claim in request object"() {

        String claims = generator.getRequestObjectClaim(scopes, AUConstants.DEFAULT_SHARING_DURATION, true, "",
                auConfiguration.getAppInfoRedirectURL(), auConfiguration.getAppInfoClientID(),
                auAuthorisationBuilder.getResponseType().toString(), true,
                auAuthorisationBuilder.getState().toString())

        String modifiedClaimSet = generator.removeClaimsFromRequestObject(claims, "exp")

        def response = auAuthorisationBuilder.doPushAuthorisationRequest(modifiedClaimSet)

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION), AUConstants.MISSING_EXP_VALUE)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST_OBJECT)
    }

    @Test
    void "OB-1244_Initiate authorisation consent flow without 'nbf' claim in request object"() {

        String claims = generator.getRequestObjectClaim(scopes, AUConstants.DEFAULT_SHARING_DURATION, true, "",
                auConfiguration.getAppInfoRedirectURL(), auConfiguration.getAppInfoClientID(),
                auAuthorisationBuilder.getResponseType().toString(), true,
                auAuthorisationBuilder.getState().toString())

        String modifiedClaimSet = generator.removeClaimsFromRequestObject(claims, "nbf")
        def response = auAuthorisationBuilder.doPushAuthorisationRequest(modifiedClaimSet)

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION), AUConstants.MISSING_NBF_VALUE)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST_OBJECT)
    }

    @Test
    void "OB-1245_Initiate authorisation consent flow with expired 'exp' claim in request object"() {

        String claims = generator.getRequestObjectClaim(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "", auConfiguration.getAppInfoRedirectURL(),
                auConfiguration.getAppInfoClientID(), auAuthorisationBuilder.getResponseType().toString(), true,
                auAuthorisationBuilder.getState().toString(), ResponseMode.JWT.toString(),
                Instant.now().minus(1, ChronoUnit.HOURS))

        def response = auAuthorisationBuilder.doPushAuthorisationRequest(claims)

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION),
                AUConstants.INVALID_FUTURE_EXPIRY_TIME)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST_OBJECT)
    }

    @Test
    void "OB-1246_Initiate authorisation consent flow with 'nbf' claim with a future time"() {

        Instant time = Instant.now().plus(1, ChronoUnit.HOURS)

        String claims = generator.getRequestObjectClaim(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "", auConfiguration.getAppInfoRedirectURL(),
                auConfiguration.getAppInfoClientID(), auAuthorisationBuilder.getResponseType().toString(), true,
                auAuthorisationBuilder.getState().toString(), ResponseMode.JWT.toString(), time, time)

        def response = auAuthorisationBuilder.doPushAuthorisationRequest(claims)

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION),
                AUConstants.INVALID_FUTURE_NBF_TIME)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST_OBJECT)
    }

    @Test
    void "OB-1248_Initiate push authorisation flow with 'exp' having a lifetime longer than 60 minutes after 'nbf'"() {

        Instant time = Instant.now().plus(2, ChronoUnit.HOURS)

        String claims = generator.getRequestObjectClaim(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "", auConfiguration.getAppInfoRedirectURL(),
                auConfiguration.getAppInfoClientID(), auAuthorisationBuilder.getResponseType().toString(), true,
                auAuthorisationBuilder.getState().toString(), ResponseMode.JWT.toString(), time)

        def response = auAuthorisationBuilder.doPushAuthorisationRequest(claims)

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION),
                AUConstants.INVALID_EXPIRY_TIME)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST_OBJECT)
    }
}
