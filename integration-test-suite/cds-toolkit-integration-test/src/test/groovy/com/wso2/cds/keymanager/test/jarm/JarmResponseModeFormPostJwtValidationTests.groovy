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
import com.wso2.cds.test.framework.utility.AURestAsRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import org.testng.Assert
import org.testng.annotations.Test

/**
 * JARM Validation Tests with Response Mode form_post.jwt.
 */
class JarmResponseModeFormPostJwtValidationTests extends AUTest {

    String authResponseUrl
    String responseJwt
    HashMap<String, String> mapPayload
    JWTClaimsSet jwtPayload

    @Test
    void "CDS-582_Verify response_mode form_post jwt navigates to Authorization Flow"() {

        def clientId = auConfiguration.getAppInfoClientID()

        //Send PAR request and get the request URI with response type code
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "", clientId, auConfiguration.getAppInfoRedirectURL(), ResponseType.CODE.toString(),
        true, ResponseMode.FORM_POST_JWT.toString())

        Assert.assertEquals(response.getStatusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION), AUConstants.UNSUPPORTED_RESPONSE_MODE)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST)
    }

    @Test
    void "CDS-583_Verify response_mode=form_post jwt and response_type=code id_token"() {

        def clientId = auConfiguration.getAppInfoClientID()

        //Send PAR request and get the request URI
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "", clientId, auConfiguration.getAppInfoRedirectURL(),
                ResponseType.CODE_IDTOKEN.toString(), true, ResponseMode.FORM_POST_JWT.toString())

        Assert.assertEquals(response.getStatusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION),
                AUConstants.ERROR_UNSUPPORTED_RESPONSE)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST)
    }

    @Test
    void "CDS-584_Verify response_mode=form_post jwt and response_type=token"() {

        def clientId = auConfiguration.getAppInfoClientID()

        //Send PAR request and get the request URI
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "", clientId, auConfiguration.getAppInfoRedirectURL(),
                ResponseType.TOKEN.toString(), true, ResponseMode.FORM_POST_JWT.toString())

        Assert.assertEquals(response.getStatusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION),
                AUConstants.ERROR_UNSUPPORTED_RESPONSE)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST)
    }

    @Test
    void "CDS-585_Verify response of the authorisation server to the user agent as HTML response when response_mode=form_post jwt"() {

        def clientId = auConfiguration.getAppInfoClientID()

        //Send PAR request and get the request URI
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "", clientId, auConfiguration.getAppInfoRedirectURL(),
                ResponseType.CODE_IDTOKEN.toString(), true, ResponseMode.FORM_POST_JWT.toString())

        Assert.assertEquals(response.getStatusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION),
                AUConstants.ERROR_UNSUPPORTED_RESPONSE)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST)
    }
}
