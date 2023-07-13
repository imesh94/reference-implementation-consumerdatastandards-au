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
    HashMap<String, String> mapPayload
    JWTClaimsSet jwtPayload

    @Test (priority = 1)
    void "CDS-569_Verify response_mode fragment jwt navigates to Authorization Flow"() {

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "", auConfiguration.getAppInfoClientID(),
                auConfiguration.getAppInfoRedirectURL(), ResponseType.CODE.toString(), true,
                ResponseMode.FRAGMENT_JWT.toString())

        Assert.assertEquals(response.getStatusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION), AUConstants.UNSUPPORTED_RESPONSE_MODE)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST)
    }

    @Test
    void "CDS-572_Verify in fragment jwt response mode if response_type = code id_token"() {

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "", auConfiguration.getAppInfoClientID(),
                auConfiguration.getAppInfoRedirectURL(), ResponseType.CODE_IDTOKEN.toString(), true,
                ResponseMode.FRAGMENT_JWT.toString())

        Assert.assertEquals(response.getStatusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION),
                AUConstants.ERROR_UNSUPPORTED_RESPONSE)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST)
    }

    @Test
    void "CDS-574_Verify in fragment jwt response mode if response_type = token"() {

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "", auConfiguration.getAppInfoClientID(),
                auConfiguration.getAppInfoRedirectURL(), ResponseType.TOKEN.toString(), true,
                ResponseMode.FRAGMENT_JWT.toString())

        Assert.assertEquals(response.getStatusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION),
                AUConstants.ERROR_UNSUPPORTED_RESPONSE)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST)
    }

    @Test
    void "CDS-575_Verify in fragment jwt response mode if response_type = none"() {

        def clientId = auConfiguration.getAppInfoClientID()

        //Send PAR request
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "", clientId, auConfiguration.getAppInfoRedirectURL(),
                ResponseType.parse("NONE").toString(), true, ResponseMode.FRAGMENT_JWT.toString())

        Assert.assertEquals(response.getStatusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION),
                AUConstants.ERROR_UNSUPPORTED_RESPONSE)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR), AUConstants.INVALID_REQUEST)
    }
}
