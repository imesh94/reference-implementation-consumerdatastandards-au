/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.keymanager.test.authorizationflow

import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.nimbusds.oauth2.sdk.TokenErrorResponse
import com.nimbusds.oauth2.sdk.token.RefreshToken
import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.automation.consent.AUBasicAuthAutomationStep
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.AUPageObjects
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import com.wso2.openbanking.test.framework.automation.AutomationMethod
import com.wso2.openbanking.test.framework.automation.NavigationAutomationStep
import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.Test

import java.nio.charset.Charset

/**
 * Test Class for Consent Amendment flow Validations.
 */
class ConsentAmendmentFlowTest extends AUTest{

    public List<AUAccountScope> scopes = [
            AUAccountScope.BANK_ACCOUNT_BASIC_READ,
            AUAccountScope.BANK_ACCOUNT_DETAIL_READ,
            AUAccountScope.BANK_TRANSACTION_READ,
            AUAccountScope.BANK_REGULAR_PAYMENTS_READ,
            AUAccountScope.BANK_CUSTOMER_BASIC_READ,
            AUAccountScope.BANK_CUSTOMER_DETAIL_READ
    ]

    def clientHeader = "${Base64.encoder.encodeToString(getCDSClient().getBytes(Charset.defaultCharset()))}"
    private def accessTokenResponse, accessTokenResponse2
    private String authorisationCode, secondAuthorisationCode = null
    private String cdrArrangementId, userAccessToken, secondUserAccessToken, refreshToken, secondRefreshToken = null

    @Test(groups = "SmokeTest")
    void "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended"() {

        // Send Authorisation request
        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        // Retrieve the user access token by auth code
        accessTokenResponse = getUserAccessTokenResponse(clientId)
        cdrArrangementId = accessTokenResponse.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        userAccessToken = accessTokenResponse.tokens.accessToken
        refreshToken = accessTokenResponse.tokens.refreshToken

        Assert.assertNotNull(userAccessToken)
        Assert.assertNotNull(refreshToken)
        Assert.assertNotNull(cdrArrangementId)

        //remove an existing scope and add a new scope to amend the consent
        scopes.remove(AUAccountScope.BANK_TRANSACTION_READ)
        scopes.add(AUAccountScope.BANK_PAYEES_READ)

        //Retrieve and assert the request URI from Push Authorization request
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI()).toURI().toString()

        //Consent Amendment Authorisation Flow
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
        authorisationCode = AUTestUtil.getHybridCodeFromUrl(automation.currentUrl.get())

        //Generate Token
        accessTokenResponse2 = getUserAccessTokenResponse(clientId)
        def cdrArrangementId2 = accessTokenResponse2.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        secondUserAccessToken = accessTokenResponse2.tokens.accessToken
        secondRefreshToken = accessTokenResponse2.tokens.refreshToken

        Assert.assertNotNull(userAccessToken)
        Assert.assertNotNull(secondUserAccessToken)
        Assert.assertEquals(cdrArrangementId, cdrArrangementId2, "Amended CDR id is not original CDR id")
    }

    @Test(groups = "SmokeTest",
            dependsOnMethods = "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended")
    void "TC002_Verify account retrieval for amended consent User Access Token to test consent enforcement"() {

        //Get Account Transaction Details
        def responseAfterAmendment = AURequestBuilder.buildBasicRequestWithCustomHeaders(secondUserAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(responseAfterAmendment.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(responseAfterAmendment.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertNotNull(AUTestUtil.parseResponseBody(responseAfterAmendment,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[0]"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(responseAfterAmendment,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[1]"))
    }

    @Test(groups = "SmokeTest",
            dependsOnMethods = "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended")
    void "TC003_Verify account retrieval for original consent User Access Token"() {

        //Get Account Transaction Details
        def responseAfterAmendment = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        // Assert if details of selected accounts cannot be retrieved via accounts get call
        Assert.assertEquals(responseAfterAmendment.statusCode(), AUConstants.STATUS_CODE_401)
        Assert.assertTrue(AUTestUtil.parseResponseBody(responseAfterAmendment, AUConstants.ERROR_DESCRIPTION)
                .contains(AUConstants.INVALID_CREDENTIALS))
        Assert.assertTrue(AUTestUtil.parseResponseBody(responseAfterAmendment, AUConstants.ERROR)
                .contains(AUConstants.INVALID_CLIENT))
    }

    @Test(dependsOnMethods = "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended")
    void "TC004_Verify Token Introspection for newly amended consent user access Token"() {

        Response response = AURequestBuilder.buildIntrospectionRequest(secondRefreshToken)
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        // Assert if Token status is active for latest consent amendment
        Assert.assertTrue(response.jsonPath().get("active"))
    }

    @Test(dependsOnMethods = "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended")
    void "TC005_Verify Token Introspection for previous user access Token"() {

        Response response = AURequestBuilder.buildIntrospectionRequest(refreshToken)
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        // Assert if Token status is NOT active for previous consent amendment - user access token
        Assert.assertFalse(response.jsonPath().get("active"))
    }

    @Test(dependsOnMethods = "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended")
    void "TC006_Verify regenerate Access Token using Refresh Token for amended Consent"() {

        AccessTokenResponse userAccessToken = getUserAccessTokenFormRefreshToken(secondRefreshToken as RefreshToken)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
    }

    @Test(dependsOnMethods = "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended")
    void "TC007_Verify regenerate Access Token using Refresh Token for original Consent"() {

        TokenErrorResponse userAccessToken = AURequestBuilder.getUserTokenFromRefreshTokenErrorResponse(secondRefreshToken as RefreshToken)
        Assert.assertEquals(userAccessToken.toJSONObject().get(AUConstants.ERROR_DESCRIPTION),
                "Persisted access token data not found")
    }

    @Test(groups = "SmokeTest",
            dependsOnMethods = "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended",
            priority = 1)
    void "TC008_Verify an amended consent can be re-amended"() {

        //Re amend the scopes of the consent amendment
        scopes.remove(AUAccountScope.BANK_PAYEES_READ)
        scopes.add(AUAccountScope.BANK_CUSTOMER_DETAIL_READ)

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI()).toURI().toString()

        //Consent Amendment Authorisation Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Verify Account Selection Page
                    assert authWebDriver.isElementSelected(AUTestUtil.getAltSingleAccountXPath())

                    //Click Confirm Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)

                    //Click Authorise Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)
                }
                .execute()

        // Get Code From URL
        authorisationCode = AUTestUtil.getHybridCodeFromUrl(automation.currentUrl.get())

        //Generate Token
        accessTokenResponse2 = getUserAccessTokenResponse(clientId)
        def cdrArrangementId3 = accessTokenResponse2.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        secondUserAccessToken = accessTokenResponse2.tokens.accessToken
        secondRefreshToken = accessTokenResponse2.tokens.refreshToken

        verifyScopes(accessTokenResponse2.toJSONObject().get("scope").toString(), scopes)
        Assert.assertEquals(cdrArrangementId, cdrArrangementId3, "Amended CDR id is not original CDR id")
    }

    @Test(dependsOnMethods = "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended",
            priority = 1)
    void "TC009_Verify a revoked consent cannot be amended"() {

        //Revoke the Consent
        Response cdrResponse = AURequestBuilder.doRevokeConsent(auConfiguration.getAppInfoClientID(), cdrArrangementId)
        Assert.assertEquals(cdrResponse.statusCode(), AUConstants.STATUS_CODE_204)

        //Send Consent Amendment Request
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI()).toURI().toString()

        //Consent Amendment Authorisation Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new NavigationAutomationStep(authoriseUrl, 10))
                .execute()

        // Get Code From URL
        String url = automationResponse.currentUrl.get()
        String errorUrl = url.split("error_description=")[1].split("&")[0].replaceAll("\\+"," ")
        Assert.assertEquals(errorUrl, "There's no sharing arrangement under the provided consent id")
    }

    @Test(priority = 1)
    void "TC010_Verify Status of the refresh token after the Consent Amendment - sharing duration has expired"() {

        // Send Authorisation request
        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        // Retrieve the user access token by auth code
        accessTokenResponse = getUserAccessTokenResponse(clientId)
        cdrArrangementId = accessTokenResponse.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        userAccessToken = accessTokenResponse.tokens.accessToken
        refreshToken = accessTokenResponse.tokens.refreshToken

        Assert.assertNotNull(userAccessToken)
        Assert.assertNotNull(refreshToken)
        Assert.assertNotNull(cdrArrangementId)

        //remove an existing scope and add a new scope to amend the consent
        scopes.remove(AUAccountScope.BANK_TRANSACTION_READ)
        scopes.add(AUAccountScope.BANK_PAYEES_READ)

        //Retrieve and assert the request URI from Push Authorization request
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI()).toURI().toString()

        //Consent Amendment Authorisation Flow
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
        authorisationCode = AUTestUtil.getHybridCodeFromUrl(automation.currentUrl.get())
        Assert.assertNotNull(secondAuthorisationCode)

        //Generate Token
        accessTokenResponse2 = getUserAccessTokenResponse(clientId)
        secondUserAccessToken = accessTokenResponse2.tokens.accessToken
        secondRefreshToken = accessTokenResponse2.tokens.refreshToken

        sleep(25000)

        //Verify the status of the refresh token
        AccessTokenResponse userAccessToken3 = AURequestBuilder.getUserTokenFromRefreshTokenErrorResponse(secondRefreshToken as RefreshToken)
        Assert.assertEquals(userAccessToken3.toJSONObject().get(AUConstants.ERROR_DESCRIPTION),
                "Refresh token is expired.")
    }

    @Test(priority = 1)
    void "TC011_Verify a consent cannot be amended with expired CDR Amendment ID"() {

        // Send Authorisation request
        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        // Retrieve the user access token by auth code
        accessTokenResponse = getUserAccessTokenResponse(clientId)
        cdrArrangementId = accessTokenResponse.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        userAccessToken = accessTokenResponse.tokens.accessToken
        refreshToken = accessTokenResponse.tokens.refreshToken

        Assert.assertNotNull(userAccessToken)
        Assert.assertNotNull(refreshToken)
        Assert.assertNotNull(cdrArrangementId)

        //remove an existing scope and add a new scope to amend the consent
        scopes.remove(AUAccountScope.BANK_TRANSACTION_READ)
        scopes.add(AUAccountScope.BANK_PAYEES_READ)

        //Retrieve and assert the request URI from Push Authorization request
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI()).toURI().toString()

        sleep(25000)

        //Consent Amendment Authorisation Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new NavigationAutomationStep(authoriseUrl, 10))
                .execute()

        Assert.assertTrue(AUTestUtil.getDecodedUrl(automation.currentUrl.get())
                .contains("There's no active sharing arrangement corresponds to consent id " + cdrArrangementId))
    }

    @Test(priority = 1)
    void "TC012_Verify a consent cannot be amended with invalid CDR Amendment ID"() {

        String invalidCDRArrangementID = "80486445-2744-464d-af90-57654f4d5b00"

        //remove an existing scope and add a new scope to amend the consent
        scopes.remove(AUAccountScope.BANK_TRANSACTION_READ)
        scopes.add(AUAccountScope.BANK_PAYEES_READ)

        //Retrieve and assert the request URI from Push Authorization request
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI()).toURI().toString()

        //Consent Amendment Authorisation Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new NavigationAutomationStep(authoriseUrl, 10))
                .execute()

        Assert.assertTrue(AUTestUtil.getDecodedUrl(automation.currentUrl.get())
                .contains("Retrieving consent data failed"))
    }
}
