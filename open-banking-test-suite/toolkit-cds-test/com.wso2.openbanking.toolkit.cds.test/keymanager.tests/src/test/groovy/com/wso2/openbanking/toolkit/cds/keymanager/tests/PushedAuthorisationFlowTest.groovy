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
import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.test.framework.automation.AUBasicAuthAutomationStep
import com.wso2.openbanking.test.framework.automation.BasicAuthErrorStep
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.test.framework.automation.WaitForRedirectAutomationStep
import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUAuthorisationBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import io.restassured.RestAssured
import io.restassured.config.EncoderConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.http.ContentType
import org.openqa.selenium.By
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import javax.net.ssl.SSLHandshakeException

/**
 * Pushed Authorisation (PAR) Flow Testing.
 */
class PushedAuthorisationFlowTest extends AbstractAUTests {

    private List<AUConstants.SCOPES> scopes = [
            AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
            AUConstants.SCOPES.BANK_TRANSACTION_READ,
            AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ
    ]
    private String scopeString = "openid ${String.join(" ", scopes.collect({ it.scopeString }))}"
    private String clientId = AppConfigReader.getClientId()

    private AccessTokenResponse userAccessToken
    private String requestUri
    private String cdrArrangementId = ""
    private String accessToken
    private String refreshToken

    @BeforeClass
    void "Initialize Test Suite"() {

        TestSuite.init()
    }

    @Test (priority = 1)
    void "TC0205001_Data Recipients Initiate authorisation request using PAR"() {

        def response = doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)

        requestUri = TestUtil.parseResponseBody(response, "request_uri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(requestUri)
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "expires_in"))
    }

    @Test (priority = 1, dependsOnMethods = "TC0205001_Data Recipients Initiate authorisation request using PAR")
    void "TC0205002_Initiate consent authorisation flow with pushed authorisation request uri"() {

        doConsentAuthorisationViaRequestUri(scopes, requestUri.toURI())

        Assert.assertNotNull(authorisationCode)
    }

    @Test(priority = 1,
            dependsOnMethods = "TC0205002_Initiate consent authorisation flow with pushed authorisation request uri")
    void "TC0203013_Generate User access token by code generated from PAR model"() {

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
        Assert.assertNotNull(userAccessToken.tokens.refreshToken)
        Assert.assertNotNull(userAccessToken.getCustomParameters().get("cdr_arrangement_id"))
    }

    @Test
    void "TC0205003_Data Recipients Initiate authorisation request using PAR without MTLS security"() {

        try {
            RestAssured.given()
                    .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig()
                    .encodeContentTypeAs(TestConstants.CONTENT_TYPE_APPLICATION_JWT, ContentType.TEXT)))
                    .contentType(TestConstants.CONTENT_TYPE_APPLICATION_JWT)
                    .body(AUAuthorisationBuilder.getSignedRequestObject(scopeString,
                        AUConstants.DEFAULT_SHARING_DURATION, true, cdrArrangementId))
                    .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                    .post(AUConstants.PAR_ENDPOINT)

        } catch (SSLHandshakeException e) {
            Assert.assertTrue(e.getMessage().contains("PKIX path building failed: " +
                    "sun.security.provider.certpath.SunCertPathBuilderException: " +
                    "unable to find valid certification path to requested target"))
        }
    }

//TODO: Enable and update the test case after fixing: https://github.com/wso2-enterprise/financial-open-banking/issues/6066
    @Test (enabled = false)
    void "TC0205004_Initiate consent authorisation flow with expired request uri"() {

        def response = doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)

        requestUri = TestUtil.parseResponseBody(response, "request_uri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(requestUri)
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "expires_in"))

        println "\nWaiting for request uri to expire..."
        sleep(65000)

        //Authorise Consent
        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, requestUri.toURI())

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
            driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
            driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
            driver.findElement(By.xpath(AUConstants.CONSENT_CONFIRM_XPATH)).click()
        }
        .addStep(new WaitForRedirectAutomationStep())
                .execute()

        // Get Code From URL
        authorisationCode = TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())
    }

    @Test (priority = 2)
    void "TC0205006_Establish a new consent for an existing arrangement by passing existing cdr_arrangement_id"() {

        def response = doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)

        requestUri = TestUtil.parseResponseBody(response, "request_uri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(requestUri)

        //Authorise Consent
        doConsentAuthorisationViaRequestUri(scopes, requestUri.toURI())
        Assert.assertNotNull(authorisationCode)

        //Generate User Access Token
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)

        accessToken = userAccessToken.tokens.toString()
        refreshToken = userAccessToken.tokens.refreshToken.toString()
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        //Re-establish consent arrangement
        response = doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)

        requestUri = TestUtil.parseResponseBody(response, "request_uri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(requestUri)

        //Authorise New Consent
        doConsentAuthorisationViaRequestUri(scopes, requestUri.toURI())
        Assert.assertNotNull(authorisationCode)
    }

    @Test(priority = 2,
            dependsOnMethods = "TC0205006_Establish a new consent for an existing arrangement by passing existing cdr_arrangement_id")
    void "TC0203014_Tokens get revoked upon successful reestablishment of new consent via PAR model"() {

        def accessTokenIntrospect = AURequestBuilder
                .buildIntrospectionRequest(accessToken)
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        def refreshTokenIntrospect = AURequestBuilder
                .buildIntrospectionRequest(accessToken)
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        Assert.assertTrue((accessTokenIntrospect.jsonPath().get("active")).equals(false))
        Assert.assertTrue((refreshTokenIntrospect.jsonPath().get("active")).equals(false))
    }

    @Test (priority = 3)
    void "TC0205015_Unable to initiate authorisation if the redirect uri mismatch with the application redirect uri"() {

        def response = doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)

        requestUri = TestUtil.parseResponseBody(response, "request_uri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(requestUri)

        //Authorise Consent
        doConsentAuthorisationViaRequestUri(scopes, requestUri.toURI())
        Assert.assertNotNull(authorisationCode)

        //Generate User Access Token
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)

        accessToken = userAccessToken.tokens.toString()
        refreshToken = userAccessToken.tokens.refreshToken.toString()
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        //Re-establish consent arrangement
        response = doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)

        requestUri = TestUtil.parseResponseBody(response, "request_uri")
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(requestUri)

        //Unsuccessful Authorisation Flow
        def incorrectRedirectUrl = "https://www.google.com"

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(scopes, requestUri.toURI(), clientId,
                incorrectRedirectUrl)

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new BasicAuthErrorStep(authorisationBuilder.authoriseUrl))
                .execute()

        def errormessage = URLDecoder.decode(automation.currentUrl.get().split("&")[0].split("=")[1].toString(), "UTF8")
        Assert.assertEquals(errormessage, "invalid_callback")
    }

    //TODO: Enable the test after fixing: https://github.com/wso2-enterprise/financial-open-banking/issues/6068
    @Test( enabled = false, priority = 3,
    dependsOnMethods = "TC0205015_Unable to initiate authorisation if the redirect uri mismatch with the application redirect uri")
    void "TC0203015_Tokens not get revoked upon unsuccessful reestablishment of new consent via PAR model"() {

        def accessTokenIntrospect = AURequestBuilder
                .buildIntrospectionRequest(accessToken)
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        def refreshTokenIntrospect = AURequestBuilder
                .buildIntrospectionRequest(accessToken)
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        Assert.assertTrue((accessTokenIntrospect.jsonPath().get("active")).equals(true))
        Assert.assertTrue((refreshTokenIntrospect.jsonPath().get("active")).equals(true))
    }

    //TODO: Enable test after fixing: https://github.com/wso2-enterprise/financial-open-banking/issues/6069
    @Test (enabled = false)
    void "TC0205007_Reject consent authorisation flow when the cdr_arrangement_id define is not related to the authenticated user "() {

        def cdrArrangementId = "db638818-be86-42fc-bdb8-1e2a1011866d"

        def response = doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)

        requestUri = TestUtil.parseResponseBody(response, "request_uri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(requestUri)
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "expires_in"))

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(scopes, requestUri.toURI())

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new BasicAuthErrorStep(authorisationBuilder.authoriseUrl))
                .execute()

        def errormessage = URLDecoder.decode(automation.currentUrl.get().split("&")[1].split("=")[1].toString(), "UTF8")
        Assert.assertEquals(errormessage, "")
    }

    //TODO: Enable test after fixing: https://github.com/wso2-enterprise/financial-open-banking/issues/6069
    @Test (enabled = false)
    void "TC0205008_Reject consent authorisation flow when the cdr_arrangement_id is unrecognized by the Data Holder"() {

        def cdrArrangementId = "abcd1234"

        def response = doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)

        requestUri = TestUtil.parseResponseBody(response, "request_uri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(requestUri)
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "expires_in"))

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(scopes, requestUri.toURI())

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new BasicAuthErrorStep(authorisationBuilder.authoriseUrl))
                .execute()

        def errormessage = URLDecoder.decode(automation.currentUrl.get().split("&")[1].split("=")[1].toString(), "UTF8")
        Assert.assertEquals(errormessage, "")
    }

    //TODO: Enable test after fixing the issue: https://github.com/wso2-enterprise/financial-open-banking/issues/6070
    @Test (enabled = false)
    void "TC0205009_Initiate pushed authorisation consent flow with no sharing duration" () {

        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        def response = doPushAuthorisationRequest(scopes, AUConstants.SINGLE_ACCESS_CONSENT,
                false, cdrArrangementId)
        requestUri = TestUtil.parseResponseBody(response, "request_uri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        doConsentAuthorisationViaRequestUri(scopes, requestUri.toURI())
        Assert.assertNotNull(authorisationCode)

        //Generate User Access Token
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        Assert.assertNull(userAccessToken.tokens.refreshToken)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
    }

    //TODO: Enable test after fixing the issue: https://github.com/wso2-enterprise/financial-open-banking/issues/6071
    @Test (enabled = false)
    void "TC0205010_Initiate pushed authorisation consent flow with zero sharing duration" () {

        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        def response = doPushAuthorisationRequest(scopes, AUConstants.SINGLE_ACCESS_CONSENT,
                true, cdrArrangementId)
        requestUri = TestUtil.parseResponseBody(response, "request_uri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        doConsentAuthorisationViaRequestUri(scopes, requestUri.toURI())
        Assert.assertNotNull(authorisationCode)

        //Generate User Access Token
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        Assert.assertNull(userAccessToken.tokens.refreshToken)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
    }

    //TODO: Enable test after fixing the issue: https://github.com/wso2-enterprise/financial-open-banking/issues/6072
    @Test (enabled = false)
    void "TC0205011_Initiate pushed authorisation consent flow with sharing duration greater than one year" () {

        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        def response = doPushAuthorisationRequest(scopes, AUConstants.ONE_YEAR_DURATION,
                true, cdrArrangementId)
        requestUri = TestUtil.parseResponseBody(response, "request_uri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        doConsentAuthorisationViaRequestUri(scopes, requestUri.toURI())
        Assert.assertNotNull(authorisationCode)

        //Generate User Access Token
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        Assert.assertNotNull(userAccessToken.tokens.refreshToken)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
    }

    @Test
    void "TC0205012_Initiate pushed authorisation consent flow with negative sharing duration" () {

        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        def response = doPushAuthorisationRequest(scopes, AUConstants.NEGATIVE_DURATION,
                true, cdrArrangementId)

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(response, "title"), "Invalid sharing duration")
    }

//TODO: Enable test after fixing the issue: https://github.com/wso2-enterprise/financial-open-banking/issues/6073
    @Test (enabled = false)
    void "TC0205013_Unable to extract request uri if the redirect uri mismatch with the application redirect uri" () {

        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        String scopeString = "openid ${String.join(" ", scopes.collect({ it.scopeString }))}"
        def incorrectRedirectUrl = "https://www.google.com"

        def response = TestSuite.buildRequest()
                .contentType(TestConstants.CONTENT_TYPE_APPLICATION_JWT)
                .body(AUAuthorisationBuilder.getSignedRequestObject(scopeString,
                AUConstants.DEFAULT_SHARING_DURATION, true, cdrArrangementId, incorrectRedirectUrl)
                .getAt("parsedString"))
                .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                .post(AUConstants.PAR_ENDPOINT)

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(response, "title"), "Invalid sharing duration")
    }

    @Test
    void "TC0205016_Unable to extract request uri if the client id mismatch with the application client id" () {

        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        String scopeString = "openid ${String.join(" ", scopes.collect({ it.scopeString }))}"
        def incorrectClientId = "YwSmCUteklf0T3MJdW8IQeM1kLga"

        def response = TestSuite.buildRequest()
                .contentType(TestConstants.CONTENT_TYPE_APPLICATION_JWT)
                .body(AUAuthorisationBuilder.getSignedRequestObject(scopeString,
                AUConstants.DEFAULT_SHARING_DURATION, true, cdrArrangementId, incorrectClientId)
                .getAt("parsedString"))
                .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                .post(AUConstants.PAR_ENDPOINT)

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(response, "title"), "Invalid client / client_id")
    }

    //TODO: Enable and update test after fixing: https://github.com/wso2-enterprise/financial-open-banking/issues/6074
    @Test (enabled = false)
    void "TC0205017_Initiate authorisation flow by passing cdr_arrangement_id without PAR" () {

        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true, cdrArrangementId)

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new BasicAuthErrorStep(authorisationBuilder.authoriseUrl))
                .execute()

        def errormessage = URLDecoder.decode(automation.currentUrl.get().split("&")[1].split("=")[1].toString(), "UTF8")
        Assert.assertEquals(errormessage, "")
    }

    //Improvement: https://github.com/wso2-enterprise/financial-open-banking/issues/6075
    @Test
    void "TC0205018_Unable pass request_uri in the body of PAR request"() {

        def request_uri = "urn::bK4mreEMpZ42Ot4xxMOQdM2OvqhA66Rn"

        def response = TestSuite.buildRequest()
                .contentType(TestConstants.CONTENT_TYPE_APPLICATION_JWT)
                .body(request_uri)
                .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                .post(AUConstants.PAR_ENDPOINT)

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_500)
    }
}
