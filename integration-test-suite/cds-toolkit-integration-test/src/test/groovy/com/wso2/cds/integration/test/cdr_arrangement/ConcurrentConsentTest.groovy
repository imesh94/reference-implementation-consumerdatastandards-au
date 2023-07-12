/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.cdr_arrangement

import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.automation.consent.AUBasicAuthAutomationStep
import com.wso2.cds.test.framework.constant.AUAccountProfile
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.AUPageObjects
import com.wso2.cds.test.framework.request_builder.AUJWTGenerator
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AURestAsRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import com.wso2.openbanking.test.framework.automation.AutomationMethod
import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.Test

/**
 * Tests Related to Concurrent Consents.
 */
class ConcurrentConsentTest extends AUTest {

    @Test
    void "TC0204001_Retrieve Consumer data using tokens obtained for multiple consents"() {

        List<AUAccountScope> scopes1 = [AUAccountScope.BANK_ACCOUNT_BASIC_READ ]
        List<AUAccountScope> scopes2 = [AUAccountScope.BANK_PAYEES_READ ]
        def clientId = auConfiguration.getAppInfoClientID()

        //Consent Authorisation - 1
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes1, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        doConsentAuthorisationViaRequestUri(scopes1, requestUri.toURI(), clientId, AUAccountProfile.INDIVIDUAL)
        Assert.assertNotNull(authorisationCode)
        def userAccessToken1 = AURequestBuilder.getUserToken(authorisationCode,
                scopes1, auAuthorisationBuilder.getCodeVerifier())

        //Consent Authorisation - 2
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes2, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        doConsentAuthorisationViaRequestUriNoAccountSelection(scopes2, requestUri.toURI(), clientId, AUAccountProfile.INDIVIDUAL)
        Assert.assertNotNull(authorisationCode)
        def userAccessToken2 = AURequestBuilder.getUserToken(authorisationCode,
                scopes1, auAuthorisationBuilder.getCodeVerifier())

        Response response1 = AURequestBuilder
                .buildBasicRequest(userAccessToken1.tokens.accessToken.toString(),
                        AUConstants.CDR_ENDPOINT_VERSION)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response1.statusCode(), AUConstants.STATUS_CODE_200)

        Response response2 = AURequestBuilder
                .buildBasicRequest(userAccessToken2.tokens.accessToken.toString(),
                        AUConstants.CDR_ENDPOINT_VERSION)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PAYEES))
                .get("${AUConstants.BULK_PAYEES}")

        Assert.assertEquals(response2.statusCode(), AUConstants.STATUS_CODE_200)
    }

    @Test
    void "TC0204002_Retrieve Consumer data using invalid tokens obtained for multiple consents"() {

        List<AUAccountScope> scopes1 = [ AUAccountScope.BANK_ACCOUNT_BASIC_READ ]
        List<AUAccountScope> scopes2 = [ AUAccountScope.BANK_PAYEES_READ ]

        //Consent Authorisation - 1
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes1, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        doConsentAuthorisationViaRequestUri(scopes1, requestUri.toURI(), clientId, AUAccountProfile.INDIVIDUAL)
        Assert.assertNotNull(authorisationCode)
        def userAccessToken1 = AURequestBuilder.getUserToken(authorisationCode,
                scopes1, auAuthorisationBuilder.getCodeVerifier())

        //Consent Authorisation - 2
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes2, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        doConsentAuthorisationViaRequestUri(scopes2, requestUri.toURI(), clientId, AUAccountProfile.INDIVIDUAL)
        Assert.assertNotNull(authorisationCode)
        def userAccessToken2 = AURequestBuilder.getUserToken(authorisationCode,
                scopes1, auAuthorisationBuilder.getCodeVerifier())

        Response response1 = AURequestBuilder
                .buildBasicRequest(userAccessToken2.tokens.accessToken.toString(),
                        AUConstants.CDR_ENDPOINT_VERSION)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response1.statusCode(), AUConstants.STATUS_CODE_403)

        Response response2 = AURequestBuilder
                .buildBasicRequest(userAccessToken1.tokens.accessToken.toString(),
                        AUConstants.CDR_ENDPOINT_VERSION)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PAYEES))
                .get("${AUConstants.BULK_PAYEES}")

        Assert.assertEquals(response2.statusCode(), AUConstants.STATUS_CODE_403)
    }

    @Test
    void "TC0902001_Revoke consent using cdr management endpoint"() {

        List<AUAccountScope> sharingScope = [ AUAccountScope.BANK_ACCOUNT_BASIC_READ ]

        //authorise sharing arrangement
        response = auAuthorisationBuilder.doPushAuthorisationRequest(sharingScope, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        doConsentAuthorisationViaRequestUri(sharingScope, requestUri.toURI(), clientId, AUAccountProfile.INDIVIDUAL)
        Assert.assertNotNull(authorisationCode)
        def userAccessTokenResponse = AURequestBuilder.getUserToken(authorisationCode,
                sharingScope, auAuthorisationBuilder.getCodeVerifier())
        String userAccessToken = userAccessTokenResponse.tokens.accessToken.toString()

        //obtain cdr_arrangement_id from token response
        String cdrArrangementId = userAccessTokenResponse.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId)

        //retrieve consumer data successfully
        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.CDR_ENDPOINT_VERSION)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //revoke sharing arrangement
        def revokeResponse = doRevokeCdrArrangement(auConfiguration.getAppInfoClientID(), cdrArrangementId)

        Assert.assertEquals(revokeResponse.statusCode(), AUConstants.STATUS_CODE_204)

        Thread.sleep(120000)

        //try to retrieve consumer data after revocation
        response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.CDR_ENDPOINT_VERSION)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)

        //validate token
        def introspectResponse = AURequestBuilder.buildIntrospectionRequest(userAccessToken,
                auConfiguration.getAppInfoClientID(), 0)
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        Assert.assertTrue((introspectResponse.jsonPath().get("active")).equals(false))
    }

    @Test
    void "TC0203010_Generate User access token by revoked consent"() {

        List<AUAccountScope> sharingScopes = [ AUAccountScope.BANK_ACCOUNT_BASIC_READ ]

        //authorise sharing arrangement
        response = auAuthorisationBuilder.doPushAuthorisationRequest(sharingScopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        doConsentAuthorisationViaRequestUri(sharingScopes, requestUri.toURI(), clientId, AUAccountProfile.INDIVIDUAL)
        Assert.assertNotNull(authorisationCode)
        def userAccessTokenResponse = AURequestBuilder.getUserToken(authorisationCode,
                sharingScopes, auAuthorisationBuilder.getCodeVerifier())
        String userAccessToken = userAccessTokenResponse.tokens.accessToken.toString()

        //obtain cdr_arrangement_id from token response
        String cdrArrangementId = userAccessTokenResponse.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)

        Assert.assertNotNull(cdrArrangementId)

        //retrieve consumer data successfully
        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.CDR_ENDPOINT_VERSION)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //revoke sharing arrangement
        def revokeResponse = doRevokeCdrArrangement(auConfiguration.getAppInfoClientID(), cdrArrangementId)

        Assert.assertEquals(revokeResponse.statusCode(), AUConstants.STATUS_CODE_204)

        //generate user access token
        def errorObject = AURequestBuilder.getUserTokenErrorResponse(authorisationCode)

        Assert.assertEquals(errorObject.toJSONObject().get(AUConstants.ERROR_DESCRIPTION),
                "Inactive authorization code received from token request")
    }

    //When cdr_arrangement_id is sent as a claim in the request object, the corresponding consent should get revoked
    //upon staging new consent
    @Test
    void "TC0204003_Revoke consent using upon staging of a new consent"() {
        List<AUAccountScope> scopes1 = [ AUAccountScope.BANK_ACCOUNT_BASIC_READ ]
        List<AUAccountScope> scopes2 = [ AUAccountScope.BANK_PAYEES_READ ]

        //authorise the first sharing arrangement
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes1, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        doConsentAuthorisationViaRequestUri(scopes1, requestUri.toURI(), clientId, AUAccountProfile.INDIVIDUAL)
        Assert.assertNotNull(authorisationCode)
        def userAccessTokenResponse = AURequestBuilder.getUserToken(authorisationCode,
                scopes1, auAuthorisationBuilder.getCodeVerifier())
        String userAccessToken1 = userAccessTokenResponse.tokens.accessToken.toString()
        String refreshAccessToken1 = userAccessTokenResponse.tokens.refreshToken.toString()

        //obtain cdr_arrangement_id from token response
        String cdrArrangementId1 = userAccessTokenResponse.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId1)

        //authorize the second sharing arrangement
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes1, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId1)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes1, requestUri.toURI()).toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Verify Account Selection Page
                    assert authWebDriver.isElementDisplayed(AUTestUtil.getAltSingleAccountXPath())
                    authWebDriver.clickButtonXpath(AUTestUtil.getAltSingleAccountXPath())

                    //Click Confirm Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)

                    //Click Authorise Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)
                }
                .execute()

        // Get Code From URL
        authorisationCode = AUTestUtil.getCodeFromJwtResponse(automation.currentUrl.get())

        //Get User Access Token
        def userAccessTokenResponse2 = AURequestBuilder.getUserToken(authorisationCode,
                scopes2, auAuthorisationBuilder.getCodeVerifier())
        String userAccessToken2 = userAccessTokenResponse2.tokens.accessToken.toString()
        String refreshAccessToken2 = userAccessTokenResponse2.tokens.refreshToken.toString()

        String cdrArrangementId2 = userAccessTokenResponse2.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId2)

        Thread.sleep(2000)

        //validate first token
        def introspectResponse1 = AURequestBuilder.buildIntrospectionRequest(refreshAccessToken1,
                auConfiguration.getAppInfoClientID(), 0)
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        Assert.assertTrue(introspectResponse1.jsonPath().get("active").toString().contains("false"))

        //validate second token
        def introspectResponse2 = AURequestBuilder.buildIntrospectionRequest(refreshAccessToken2,
                auConfiguration.getAppInfoClientID(), 0)
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        Assert.assertTrue(introspectResponse2.jsonPath().get("active").toString().contains("true"))

    }

    @Test
    void "TC0902002_Revoke consent using cdr management endpoint without cdr arrangement id"() {

        //revoke sharing arrangement without cdr arrangement id
        generator = new AUJWTGenerator()
        String assertionString = generator.getClientAssertionJwt(clientId)

        def bodyContent = [(AUConstants.CLIENT_ID_KEY): (clientId),
                           (AUConstants.CLIENT_ASSERTION_TYPE_KEY): (AUConstants.CLIENT_ASSERTION_TYPE),
                           (AUConstants.CLIENT_ASSERTION_KEY)     : assertionString]

        revocationResponse = AURestAsRequestBuilder.buildRequest()
                .contentType(AUConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .formParams(bodyContent)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CDR_ARRANGEMENT))
                .post("${AUConstants.CDR_ARRANGEMENT_ENDPOINT}")

        Assert.assertEquals(revocationResponse.statusCode(), AUConstants.STATUS_CODE_400)
    }
}
