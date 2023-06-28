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

    //TODO: Enable testcase after testing the automation in latest pack
    //@Test
    void "CDS-582_Verify response_mode form_post jwt navigates to Authorization Flow"() {

        //Send PAR request and get the request URI with response type code
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "", auConfiguration.getAppInfoRedirectURL(), ResponseType.CODE.toString())
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //Get Authorisation URL
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(),
                ResponseMode.FORM_POST_JWT, clientId, ResponseType.CODE, true).toURI().toString()

        //Send Get Request for Authorise Endpoint
        def authResponse = AURestAsRequestBuilder.buildRequest()
                                    .get(authoriseUrl)

        //Read HTML response and Extract Response
        String htmlResponse = authResponse.getBody().toString()
        String responseValue = AUTestUtil.readHtmlDocument(htmlResponse, AUConstants.VALUE_KEY)
        Assert.assertNotNull(responseValue)

        //Extract JWT
        mapPayload = AUJWTGenerator.extractJwt(responseJwt)
        Assert.assertNotNull(mapPayload.get(AUConstants.CODE_KEY))

        //Navigate through Authorisation web app
        def automation = doAuthorisationFlowNavigation(authoriseUrl)

        //Get Code From URL
        authorisationCode = AUTestUtil.getCodeFromJwtResponse(automation.currentUrl.get())
        Assert.assertEquals(mapPayload.get(AUConstants.CODE_KEY), authorisationCode)
    }

    //TODO: Enable testcase after testing the automation in latest pack
    //@Test
    void "CDS-583_Verify response_mode=form_post jwt and response_type=code id_token"() {

        //Send PAR request and get the request URI
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "", auConfiguration.getAppInfoRedirectURL(),
                ResponseType.CODE_IDTOKEN.toString())

        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //Get Authorisation URL
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(),
                ResponseMode.FORM_POST_JWT, clientId, ResponseType.CODE_IDTOKEN, true).toURI().toString()

        //Send Get Request for Authorise Endpoint
        def authResponse = AURestAsRequestBuilder.buildRequest()
                                    .get(authoriseUrl)

        //Read HTML response and Extract Response
        String htmlResponse = authResponse.getBody().toString()
        String responseValue = AUTestUtil.readHtmlDocument(htmlResponse, AUConstants.VALUE_KEY)
        Assert.assertNotNull(responseValue)

        //Extract JWT
        mapPayload = AUJWTGenerator.extractJwt(responseJwt)
        Assert.assertNotNull(mapPayload.get(AUConstants.CODE_KEY))
    }

    //TODO: Enable testcase after testing the automation in latest pack
    //@Test
    void "CDS-584_Verify response_mode=form_post jwt and response_type=token"() {

        //Send PAR request and get the request URI
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "", auConfiguration.getAppInfoRedirectURL(),
                ResponseType.TOKEN.toString())

        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //Get Authorisation URL
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(),
                ResponseMode.FORM_POST_JWT, clientId, ResponseType.TOKEN, true).toURI().toString()

        //Send Get Request for Authorise Endpoint
        def authResponse = AURestAsRequestBuilder.buildRequest()
                .get(authoriseUrl)

        //Read HTML response and Extract Response
        String htmlResponse = authResponse.getBody().toString()
        String responseValue = AUTestUtil.readHtmlDocument(htmlResponse, AUConstants.VALUE_KEY)
        Assert.assertNotNull(responseValue)

        //Extract JWT
        mapPayload = AUJWTGenerator.extractJwt(responseJwt)
        Assert.assertNotNull(mapPayload.get(AUConstants.CODE_KEY))
    }

    //TODO: Enable testcase after testing the automation in latest pack
    //@Test
    void "CDS-585_Verify response of the authorisation server to the user agent as HTML response when response_mode=form_post jwt"() {

        //Send PAR request and get the request URI
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "", auConfiguration.getAppInfoRedirectURL(),
                ResponseType.CODE_IDTOKEN.toString())

        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //Get Authorisation URL
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(),
                ResponseMode.FORM_POST_JWT, clientId, ResponseType.CODE_IDTOKEN, true).toURI().toString()

        //Send Get Request for Authorise Endpoint
        def authResponse = AURestAsRequestBuilder.buildRequest()
                .get(authoriseUrl)

        //Read HTML response and Extract Response
        String htmlResponse = authResponse.getBody().toString()

        //Verify input value  attribute has the response JWT
        String responseValue = AUTestUtil.readHtmlDocument(htmlResponse, AUConstants.VALUE_KEY)
        Assert.assertNotNull(responseValue)

        //Verify action attribute has the application redirect url
        String actionValue = AUTestUtil.readHtmlDocument(htmlResponse, "action")
        Assert.assertEquals(actionValue, auConfiguration.getAppInfoRedirectURL())

        //Verify method attribute denotes this is a post request
        String methodValue = AUTestUtil.readHtmlDocument(htmlResponse, "method")
        Assert.assertEquals(methodValue, "post")
    }
}
