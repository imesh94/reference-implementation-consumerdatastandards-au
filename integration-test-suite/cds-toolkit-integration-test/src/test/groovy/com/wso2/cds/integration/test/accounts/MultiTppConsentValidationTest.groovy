/*
 * Copyright (c) 2022-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.accounts

import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConstants
import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import com.wso2.openbanking.test.framework.automation.NavigationAutomationStep
import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import com.wso2.cds.test.framework.request_builder.AUAuthorisationBuilder
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil

/**
 * Class contains Multi Tpp Consent Validation Tests.
 */
class MultiTppConsentValidationTest extends AUTest {

    AUConfigurationService auConfiguration = new AUConfigurationService()
    String clientID

    @BeforeClass(alwaysRun = true)
    void setup() {
        auConfiguration.setTppNumber(1)

        deleteApplicationIfExists()
        //Register Second TPP.
        def registrationResponse = tppRegistration()
        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.CREATED)

        clientID = AUTestUtil.parseResponseBody(registrationResponse, "client_id")
        List<String> redirectURI = AUTestUtil.parseResponseBodyList(registrationResponse, "redirect_uris")

        //Write Client Id of TPP2 to config file.
        AUTestUtil.writeXMLContent(auConfiguration.getOBXMLFile().toString(), "Application",
                "ClientID", clientID, auConfiguration.getTppNumber())
    }

    @Test
    void "OB-1313_Revoke sharing arrangement bound to different Tpp"(){
        auConfiguration.setTppNumber(0)

        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        //obtain cdr_arrangement_id from token response
        AccessTokenResponse userAccessTokenRes = getUserAccessTokenResponse()
        String cdrArrangementId = userAccessTokenRes.getCustomParameters().get("cdr_arrangement_id")
        Assert.assertNotNull(cdrArrangementId)

        //retrieve consumer data successfully
        Response response = AURequestBuilder
                .buildBasicRequest(userAccessTokenRes.tokens.accessToken.toString(), AUConstants.CDR_ENDPOINT_VERSION)
                .header(AUConstants.PARAM_FAPI_AUTH_DATE,AUConstants.VALUE_FAPI_AUTH_DATE)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        auConfiguration.setTppNumber(1)

        //Get application access token for TPP2
        setApplicationScope(["openid"])
        String secondAppAccessToken = getApplicationAccessToken(clientID)
        Assert.assertNotNull(secondAppAccessToken)

        auConfiguration.setTppNumber(0)

        AUAuthorisationBuilder authBuilder = new AUAuthorisationBuilder()
        //revoke sharing arrangement using token of TPP2 and cdrArrangementId of TPP1
        Response revocationResponse= authBuilder.doArrangementRevocationWithPkjwt(secondAppAccessToken,cdrArrangementId)
        Assert.assertEquals(revocationResponse.statusCode(), AUConstants.STATUS_CODE_404)

    }

    @Test
    void "OB-1311_Validate consent authorisation with request_uri bound to different tpp"() {
        auConfiguration.setTppNumber(0)

        //authorise sharing arrangement
        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        //obtain cdr_arrangement_id from token response
        AccessTokenResponse userAccessTokenRes = getUserAccessTokenResponse()
        String cdrArrangementId = userAccessTokenRes.getCustomParameters().get("cdr_arrangement_id")
        Assert.assertNotNull(cdrArrangementId)

        //retrieve consumer data successfully
        Response response = doAccountRetrieval(userAccessTokenRes.tokens.accessToken.toString())
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //Send PAR request.
        def parResponse = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)

        def requestUri = AUTestUtil.parseResponseBody(parResponse, AUConstants.REQUEST_URI)
        Assert.assertEquals(parResponse.statusCode(), AUConstants.STATUS_CODE_201)

        //Send consent authorisation using request_uri bound to TPP1 with client id of TPP2
        auConfiguration.setTppNumber(1)
        def authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(requestUri.toURI(),
                auConfiguration.getAppInfoClientID()).toURI().toString()

        def automationResponse = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new NavigationAutomationStep(authoriseUrl, 10))
                .execute()

        String url = automationResponse.currentUrl.get()
        String errorUrl

        errorUrl = url.split("oauthErrorCode=")[1].split("&")[0].replaceAll("\\+"," ")
        Assert.assertEquals(errorUrl, AUConstants.INVALID_CLIENT)

        errorUrl = url.split("oauthErrorMsg=")[1].split("&")[0].replaceAll("\\+"," ")
        Assert.assertEquals(errorUrl, "application.not.found")

    }

    @Test
    void "OB-1312_Validate PAR request with cdr_arrangement_id belongs to different TPP"() {
        auConfiguration.setTppNumber(0)

        //authorise sharing arrangement
        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        //obtain cdr_arrangement_id from token response
        AccessTokenResponse userAccessTokenRes = getUserAccessTokenResponse()
        String cdrArrangementId = userAccessTokenRes.getCustomParameters().get("cdr_arrangement_id")
        Assert.assertNotNull(cdrArrangementId)

        auConfiguration.setTppNumber(1)
        //Send PAR request.
        def parResponse = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId, auConfiguration.getAppInfoClientID())

        Assert.assertEquals(parResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(parResponse, AUConstants.ERROR_DESCRIPTION),
                "Service provider metadata retrieval failed. Error retrieving service provider tenant domain for client_id: '${auConfiguration.getAppInfoClientID()}' ")
        Assert.assertEquals(AUTestUtil.parseResponseBody(parResponse, AUConstants.ERROR), AUConstants.INVALID_REQUEST)
    }

    @AfterClass(alwaysRun = true)
    void tearDown() {
        deleteApplicationIfExists(clientId)
    }
}

