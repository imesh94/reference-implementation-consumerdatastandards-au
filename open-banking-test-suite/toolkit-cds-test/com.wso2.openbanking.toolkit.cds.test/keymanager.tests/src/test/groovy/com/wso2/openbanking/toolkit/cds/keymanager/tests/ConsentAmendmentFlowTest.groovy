/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.toolkit.cds.keymanager.tests

import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.nimbusds.oauth2.sdk.TokenErrorResponse
import com.wso2.openbanking.test.framework.automation.AUBasicAuthAutomationStep
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.test.framework.automation.WaitForRedirectAutomationStep
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUAuthorisationBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.test.framework.model.AccessTokenJwtDto
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import io.restassured.response.Response
import org.openqa.selenium.By
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Consent Amendment Flow Testing.
 */
class ConsentAmendmentFlowTest extends AbstractAUTests{

    private List<AUConstants.SCOPES> scopes = [
            AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
            AUConstants.SCOPES.BANK_ACCOUNT_DETAIL_READ,
            AUConstants.SCOPES.BANK_TRANSACTION_READ,
            AUConstants.SCOPES.BANK_REGULAR_PAYMENTS_READ,
            AUConstants.SCOPES.BANK_CUSTOMER_BASIC_READ,
            AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ
    ]

    private String authorisationCode, secondAuthorisationCode = null
    private AccessTokenResponse userAccessToken, secondUserAccessToken = null
    private String cdrArrangementId = ""
    private Response parResponse
    private String requestUri
    private String account1Id, account2Id
    private String headerString = ConfigParser.instance.getBasicAuthUser() + ":" + ConfigParser.instance.getBasicAuthUserPassword()

    @BeforeClass(alwaysRun = true)
    void "Initialize Test Suite"() {
        TestSuite.init()
    }

    @Test(groups = "SmokeTest")
    void "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended"() {

        // Get Code From URL
        authorisationCode = doAuthorization(scopes, AUConstants.DEFAULT_SHARING_DURATION, true)
        Assert.assertNotNull(authorisationCode)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")
        Assert.assertNotNull(cdrArrangementId)

        //remove an existing scope and add a new scope to amend the consent
        scopes.remove(AUConstants.SCOPES.BANK_TRANSACTION_READ)
        scopes.add(AUConstants.SCOPES.BANK_PAYEES_READ)

        //Retrieve and assert the request URI from Push Authorization request
        requestUri = TestUtil.parseResponseBody(doPushAuthorisationRequest(headerString, scopes, AUConstants.AMENDED_SHARING_DURATION,
                true, cdrArrangementId), "requestUri")
        Assert.assertNotNull(requestUri)

        //Retrieve the second authorization code
        secondAuthorisationCode = doConsentAmendmentAuthorisationViaRequestUri(scopes, requestUri.toURI(), true)
        Assert.assertNotNull(secondAuthorisationCode)

        //Retrieve the second user access token and assert the CDR arrangement ID is the same.
        secondUserAccessToken = AURequestBuilder.getUserToken(secondAuthorisationCode)
        verifyScopes(secondUserAccessToken.toJSONObject().get("scope").toString(), scopes)
        Assert.assertEquals(cdrArrangementId, secondUserAccessToken.getCustomParameters().get("cdr_arrangement_id"),
                "Amended CDR id is not original CDR id ")
    }

    @Test(groups = "SmokeTest",
            dependsOnMethods = "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended")
    void "TC002_Verify account retrieval for amended consent User Access Token to test consent enforcement"() {

        Response response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 1)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer " + secondUserAccessToken.tokens.accessToken.toString())
                .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                .get("${AUConstants.CDS_100_PATH}${AUConstants.BULK_ACCOUNT_PATH}/")

        // Assert if details of selected accounts can be retrieved via accounts get call
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.jsonPath().getList("data.accounts").size(), 2)
        Assert.assertEquals(response.jsonPath().get("data.accounts[0].accountId"), account1Id)
        Assert.assertEquals(response.jsonPath().get("data.accounts[1].accountId"), account2Id)
    }

    @Test(groups = "SmokeTest",
            dependsOnMethods = "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended")
    void "TC003_Verify account retrieval for original consent User Access Token"() {

        Response response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 1)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer " + userAccessToken.tokens.accessToken.toString())
                .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                .get("${AUConstants.CDS_100_PATH}${AUConstants.BULK_ACCOUNT_PATH}/")

        // Assert if details of selected accounts cannot be retrieved via accounts get call
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_401)
        Assert.assertTrue(response.jsonPath().get("errors.code").toString().contains("AU.CDR.Unauthorized"))
        Assert.assertTrue(response.jsonPath().get("errors.title").toString().contains("Invalid Authorisation Header"))
    }

    @Test(dependsOnMethods = "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended")
    void "TC004_Verify Token Introspection for newly amended consent user access Token"() {

        Response response = AURequestBuilder
                .buildIntrospectionRequest(secondUserAccessToken.tokens.accessToken.toString())
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        // Assert if Token status is active for latest consent amendment
        Assert.assertTrue(response.jsonPath().get("active"))
        Assert.assertEquals(response.jsonPath().get("cdr_arrangement_id"), cdrArrangementId)
    }

    @Test(dependsOnMethods = "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended")
    void "TC005_Verify Token Introspection for previous user access Token"() {

        Response response = AURequestBuilder
                .buildIntrospectionRequest(userAccessToken.tokens.accessToken.toString())
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        // Assert if Token status is NOT active for previous consent amendment - user access token
        Assert.assertFalse(response.jsonPath().get("active"))
    }

    @Test(dependsOnMethods = "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended")
    void "TC006_Verify regenerate Access Token using Refresh Token for amended Consent"() {

        AccessTokenResponse userAccessToken = AUTestUtil.getUserTokenFromRefreshToken(
                secondUserAccessToken.tokens.refreshToken)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
        verifyScopes(userAccessToken.toJSONObject().get("scope").toString(), scopes)
    }

    @Test(dependsOnMethods = "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended")
    void "TC007_Verify regenerate Access Token using Refresh Token for original Consent"() {

        TokenErrorResponse userAccessToken = AUTestUtil.getUserTokenFromRefreshTokenErrorResponse(
                userAccessToken.tokens.refreshToken)
        Assert.assertEquals(userAccessToken.toJSONObject().get("error_description"), "Persisted access token data not found")
    }

    @Test(groups = "SmokeTest",
            dependsOnMethods = "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended",
            priority = 1)
    void "TC008_Verify an amended consent can be re-amended"() {

        //Re amend the scopes of the consent amendment
        scopes.remove(AUConstants.SCOPES.BANK_PAYEES_READ)
        scopes.add(AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ)

        requestUri = TestUtil.parseResponseBody(doPushAuthorisationRequest(headerString, scopes, 20000,
                true, cdrArrangementId), "requestUri")
        Assert.assertNotNull(requestUri)

        //Retrieve the auth code by sending request URI
        String authorisationCode = doConsentAmendmentAuthorisationViaRequestUri(scopes, requestUri.toURI(), false)
        Assert.assertNotNull(authorisationCode)

        AccessTokenResponse userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        verifyScopes(userAccessToken.toJSONObject().get("scope").toString(), scopes)
        Assert.assertEquals(cdrArrangementId, userAccessToken.getCustomParameters().get("cdr_arrangement_id"),
                "Amended CDR id is not original CDR id ")
    }

    @Test(dependsOnMethods = "TC001_Verify Consent Amendment flow when both sharing duration and scope has been amended",
            priority = 1)
    void "TC009_Verify a revoked consent cannot be amended"() {

        //Revoke the Consent
        String assertionString = new AccessTokenJwtDto().getJwt(AppConfigReader.getClientId(),
                ConfigParser.getInstance().getRevocationAudienceValue())

        def bodyContent = [(TestConstants.CLIENT_ID_KEY)            : (AppConfigReader.getClientId()),
                           (TestConstants.CLIENT_ASSERTION_TYPE_KEY): (TestConstants.CLIENT_ASSERTION_TYPE),
                           (TestConstants.CLIENT_ASSERTION_KEY)     : assertionString,
                           "cdr_arrangement_id"                     : cdrArrangementId]

        def response = TestSuite.buildRequest()
                .contentType(TestConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .formParams(bodyContent)
                .baseUri(ConfigParser.instance.baseUrl)
                .post("${AUConstants.CDR_ARRANGEMENT_ENDPOINT}${AUConstants.REVOKE_PATH}")

        //Assert the consent revoke status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_204)

        requestUri = TestUtil.parseResponseBody(doPushAuthorisationRequest(headerString, scopes, AUConstants.AMENDED_SHARING_DURATION,
                true, cdrArrangementId), "requestUri")
        Assert.assertNotNull(requestUri)

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(scopes, requestUri.toURI())

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .execute()

        Assert.assertTrue(TestUtil.getDecodedUrl(automation.currentUrl.get())
                .contains("There's no sharing arrangement under the provided consent id"))
    }

    @Test(priority = 1)
    void "TC010_Verify Status of the refresh token after the Consent Amendment - sharing duration has expired"() {

        authorisationCode = doAuthorization(scopes, AUConstants.DEFAULT_SHARING_DURATION, true)
        Assert.assertNotNull(authorisationCode)
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")
        Assert.assertNotNull(cdrArrangementId)

        scopes.remove(AUConstants.SCOPES.BANK_TRANSACTION_READ)
        scopes.add(AUConstants.SCOPES.BANK_PAYEES_READ)

        requestUri = TestUtil.parseResponseBody(doPushAuthorisationRequest(headerString, scopes, AUConstants.SHORT_SHARING_DURATION,
                true, cdrArrangementId), "requestUri")
        Assert.assertNotNull(requestUri)

        secondAuthorisationCode = doConsentAmendmentAuthorisationViaRequestUri(scopes, requestUri.toURI(), true)
        Assert.assertNotNull(secondAuthorisationCode)
        secondUserAccessToken = AURequestBuilder.getUserToken(secondAuthorisationCode)

        sleep(25000)
        TokenErrorResponse userAccessToken = AUTestUtil.getUserTokenFromRefreshTokenErrorResponse(
                secondUserAccessToken.tokens.refreshToken)
        Assert.assertEquals(userAccessToken.toJSONObject().get("error_description"), "Refresh token is expired.")
    }

    @Test(groups = "SmokeTest", priority = 1)
    void "TC011_Verify a consent cannot be amended with expired CDR Amendment ID "() {

        // Get Code From URL
        authorisationCode = doAuthorization(scopes, AUConstants.SHORT_SHARING_DURATION, true)
        Assert.assertNotNull(authorisationCode)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")
        Assert.assertNotNull(cdrArrangementId)

        //remove an existing scope and add a new scope to amend the consent
        scopes.remove(AUConstants.SCOPES.BANK_TRANSACTION_READ)
        scopes.add(AUConstants.SCOPES.BANK_PAYEES_READ)

        //Retrieve and assert the request URI from Push Authorization request
        requestUri = TestUtil.parseResponseBody(doPushAuthorisationRequest(headerString, scopes, AUConstants.AMENDED_SHARING_DURATION,
                true, cdrArrangementId), "requestUri")
        Assert.assertNotNull(requestUri)
        sleep(25000)

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(new AUAuthorisationBuilder(
                        scopes, requestUri.toURI()).authoriseUrl))
                .execute()

        Assert.assertTrue(TestUtil.getDecodedUrl(automation.currentUrl.get())
                .contains("There's no active sharing arrangement corresponds to consent id " + cdrArrangementId))
    }

    @Test(groups = "SmokeTest", priority = 1)
    void "TC012_Verify a consent cannot be amended with invalid CDR Amendment ID "() {

        String invalidCDRArrangementID = "80486445-2744-464d-af90-57654f4d5b00"

        //remove an existing scope and add a new scope to amend the consent
        scopes.remove(AUConstants.SCOPES.BANK_TRANSACTION_READ)
        scopes.add(AUConstants.SCOPES.BANK_PAYEES_READ)

        //Retrieve and assert the request URI from Push Authorization request
        requestUri = TestUtil.parseResponseBody(doPushAuthorisationRequest(headerString, scopes, AUConstants.AMENDED_SHARING_DURATION,
                true, invalidCDRArrangementID), "requestUri")
        Assert.assertNotNull(requestUri)


        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(new AUAuthorisationBuilder(
                        scopes, requestUri.toURI()).authoriseUrl))
                .execute()

        Assert.assertTrue(TestUtil.getDecodedUrl(automation.currentUrl.get())
                .contains("There's no sharing arrangement under the provided consent id " + invalidCDRArrangementID))
    }

    private String doConsentAmendmentAuthorisationViaRequestUri(List<AUConstants.SCOPES> scopes, URI requestUri, boolean verifyNewLabels) {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, requestUri)

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUTestUtil.getAltSingleAccountXPath())).click()
                    account1Id = driver.findElement(By.xpath(AUConstants.LBL_ACCOUNT_1_ID_XPATH)).getText()
                    account2Id = driver.findElement(By.xpath(AUConstants.LBL_ACCOUNT_2_ID_XPATH)).getText()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                    // Below IF condition is to decide weather to verify the labels of amendment are visible on review page or not
                    if (verifyNewLabels) {
                        Assert.assertTrue(driver.findElement(By.xpath(AUConstants.LBL_NEW_PAYEES_INDICATOR_XPATH)).isDisplayed())
                        Assert.assertTrue(driver.findElement(By.xpath(AUConstants.LBL_NEW_SHARING_DURATION_XPATH)).isDisplayed())
                    }
                    driver.findElement(By.xpath(AUConstants.CONSENT_CONFIRM_XPATH)).click()
                }
                .addStep(new WaitForRedirectAutomationStep())
                .execute()

        return TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())
    }
}
