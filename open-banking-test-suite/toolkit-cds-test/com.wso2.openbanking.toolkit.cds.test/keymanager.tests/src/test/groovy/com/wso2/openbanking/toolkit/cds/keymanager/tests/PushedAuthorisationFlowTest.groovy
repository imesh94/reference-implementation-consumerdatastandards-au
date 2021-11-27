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
import com.wso2.openbanking.test.framework.automation.BasicAuthErrorStep
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUAuthorisationBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import io.restassured.RestAssured
import io.restassured.config.EncoderConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.http.ContentType
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import javax.net.ssl.SSLHandshakeException
import java.nio.charset.Charset

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
    private String headerString = ConfigParser.instance.getBasicAuthUser() + ":" + ConfigParser.instance.getBasicAuthUserPassword()

    private AccessTokenResponse userAccessToken
    private String requestUri
    private String cdrArrangementId = ""
    private String accessToken
    private String refreshToken

    @BeforeClass
    void "Initialize Test Suite"() {

        TestSuite.init()
    }

    @Test(priority = 1)
    void "TC0205001_Data Recipients Initiate authorisation request using PAR"() {

        def response = doPushAuthorisationRequest(headerString, scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)

        requestUri = TestUtil.parseResponseBody(response, "requestUri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertNotNull(requestUri)
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "expiresIn"))
    }

    @Test(priority = 1, dependsOnMethods = "TC0205001_Data Recipients Initiate authorisation request using PAR")
    void "TC0205002_Initiate consent authorisation flow with pushed authorisation request uri"() {

        doConsentAuthorisationViaRequestUri(scopes, requestUri.toURI())

        Assert.assertNotNull(authorisationCode)
    }
    //TODO: Uncomment final assertion after fixing:https://github.com/wso2-enterprise/financial-open-banking/issues/6778
    @Test(priority = 1,
            dependsOnMethods = "TC0205002_Initiate consent authorisation flow with pushed authorisation request uri")
    void "TC0203013_Generate User access token by code generated from PAR model"() {

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
        Assert.assertNotNull(userAccessToken.tokens.refreshToken)
        // Assert.assertNotNull(userAccessToken.getCustomParameters().get("cdr_arrangement_id"))
    }

    @Test
    void "TC0205003_Data Recipients Initiate authorisation request using PAR without MTLS security"() {

        try {
            RestAssured.given()
            parResponse = TestSuite.buildRequest()
                    .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(TestConstants.CONTENT_TYPE_APPLICATION_JSON, ContentType.TEXT)))
                    .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Basic " + Base64.encoder.encodeToString(
                            headerString.getBytes(Charset.forName("UTF-8"))))
                    .formParams(TestConstants.REQUEST_KEY, AUAuthorisationBuilder.getSignedRequestObject(scopeString,
                            6000, true, cdrArrangementId,
                            AppConfigReader.getRedirectURL(), clientId).serialize())
                    .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                    .post(AUConstants.PAR_ENDPOINT)

        } catch (SSLHandshakeException e) {
            Assert.assertTrue(e.getMessage().contains("PKIX path building failed: " +
                    "sun.security.provider.certpath.SunCertPathBuilderException: " +
                    "unable to find valid certification path to requested target"))
        }
    }

    @Test
    void "TC0205004_Initiate consent authorisation flow with expired request uri"() {

        def response = doPushAuthorisationRequest(headerString, scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)

        requestUri = TestUtil.parseResponseBody(response, "requestUri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertNotNull(requestUri)
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "expiresIn"))

        println "\nWaiting for request uri to expire..."
        sleep(65000)

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new BasicAuthErrorStep(new AUAuthorisationBuilder(
                        scopes, requestUri.toURI()).authoriseUrl))
                .execute()

        Assert.assertTrue(TestUtil.getErrorDescriptionFromUrl(automation.currentUrl.get())
                .contains("Expired request URI"))
    }

    @Test(priority = 2)
    void "TC0205006_Establish a new consent for an existing arrangement by passing existing cdr_arrangement_id"() {

        def response = doPushAuthorisationRequest(headerString, scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)

        requestUri = TestUtil.parseResponseBody(response, "requestUri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_201)
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
        response = doPushAuthorisationRequest(headerString, scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)

        requestUri = TestUtil.parseResponseBody(response, "requestUri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertNotNull(requestUri)

        //Authorise New Consent
        doConsentAuthorisationViaRequestUri(scopes, requestUri.toURI())
        Assert.assertNotNull(authorisationCode)
    }

    //TODO: Enabling as False until revocation and Introspection implemented on toolkit
    @Test(enabled = false, priority = 2,
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

    @Test(priority = 3)
    void "TC0205015_Unable to initiate authorisation if the redirect uri mismatch with the application redirect uri"() {

        def response = doPushAuthorisationRequest(headerString, scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)

        requestUri = TestUtil.parseResponseBody(response, "requestUri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_201)
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
        response = doPushAuthorisationRequest(headerString, scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)

        requestUri = TestUtil.parseResponseBody(response, "requestUri")
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertNotNull(requestUri)

        //Unsuccessful Authorisation Flow
        def incorrectRedirectUrl = "https://www.google.com"

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(scopes, requestUri.toURI(), clientId,
                incorrectRedirectUrl)


        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new BasicAuthErrorStep(authorisationBuilder.authoriseUrl))
                .execute()

        def errormessage = URLDecoder.decode(automation.currentUrl.get().split("&")[0]
                .split("=")[1].toString(), "UTF8")
        Assert.assertEquals(errormessage, "invalid_callback")
    }

    //TODO: Enable the test after fixing revoke on Toolkit
    @Test(enabled = false, priority = 3,
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

    //TODO: Enable test after fixing: https://github.com/wso2-enterprise/financial-open-banking/issues/6779
    //TODO: PKJWT Test Fix https://github.com/wso2-enterprise/financial-open-banking/issues/6781
    @Test(enabled = false)
    void "TC0205007_Reject consent authorisation flow when the cdr_arrangement_id define is not related to the authenticated user "() {

        def invalidCdrArrangementId = "db638818-be86-42fc-bdb8-1e2a1011866d"

        def response = doPushAuthorisationRequest(headerString, scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, invalidCdrArrangementId)

        requestUri = TestUtil.parseResponseBody(response, "requestUri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertNotNull(requestUri)
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "expiresIn"))

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(scopes, requestUri.toURI())

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new BasicAuthErrorStep(authorisationBuilder.authoriseUrl))
                .execute()

        def errorMessage = URLDecoder.decode(automation.currentUrl.get().split("&")[1]
                .split("=")[1].toString(), "UTF8")
        Assert.assertEquals(errorMessage, clientId)
    }

    //TODO: Enable test after fixing: https://github.com/wso2-enterprise/financial-open-banking/issues/6779
    @Test(enabled = false)
    void "TC0205008_Reject consent authorisation flow when the cdr_arrangement_id is unrecognized by the Data Holder"() {

        def cdrArrangementId = "abcd1234"

        def response = doPushAuthorisationRequest(headerString, scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)

        requestUri = TestUtil.parseResponseBody(response, "requestUri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertNotNull(requestUri)
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "expiresIn"))

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(scopes, requestUri.toURI())

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new BasicAuthErrorStep(authorisationBuilder.authoriseUrl))
                .execute()

        def errorMessage = URLDecoder.decode(automation.currentUrl.get().split("&")[1]
                .split("=")[1].toString(), "UTF8")
        Assert.assertEquals(errorMessage, clientId)
    }

    @Test
    void "TC0205009_Initiate pushed authorisation consent flow with no sharing duration"() {

        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        def response = doPushAuthorisationRequest(headerString, scopes, AUConstants.SINGLE_ACCESS_CONSENT,
                false, cdrArrangementId)

        requestUri = TestUtil.parseResponseBody(response, "requestUri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_201)

        doConsentAuthorisationViaRequestUri(scopes, requestUri.toURI())
        Assert.assertNotNull(authorisationCode)

        //Generate User Access Token
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
    }

    @Test
    void "TC0205010_Initiate pushed authorisation consent flow with zero sharing duration"() {

        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        def response = doPushAuthorisationRequest(headerString, scopes, AUConstants.SINGLE_ACCESS_CONSENT,
                true, cdrArrangementId)
        requestUri = TestUtil.parseResponseBody(response, "requestUri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_201)

        doConsentAuthorisationViaRequestUri(scopes, requestUri.toURI())
        Assert.assertNotNull(authorisationCode)

        //Generate User Access Token
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
    }

    @Test
    void "TC0205011_Initiate pushed authorisation consent flow with sharing duration greater than one year"() {

        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        def response = doPushAuthorisationRequest(headerString, scopes, AUConstants.ONE_YEAR_DURATION,
                true, cdrArrangementId)
        requestUri = TestUtil.parseResponseBody(response, "requestUri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_201)

        doConsentAuthorisationViaRequestUri(scopes, requestUri.toURI())
        Assert.assertNotNull(authorisationCode)

        //Generate User Access Token
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        Assert.assertNotNull(userAccessToken.tokens.refreshToken)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
    }

    @Test
    void "TC0205012_Initiate pushed authorisation consent flow with negative sharing duration"() {

        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        def response = doPushAuthorisationRequest(headerString, scopes, AUConstants.NEGATIVE_DURATION,
                true, cdrArrangementId)

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(response, "error_description"),
                "Invalid sharing_duration value")
    }


    @Test
    void "TC0205013_Unable to extract request uri if the redirect uri mismatch with the application redirect uri"() {

        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        String scopeString = "openid ${String.join(" ", scopes.collect({ it.scopeString }))}"
        def incorrectRedirectUrl = "https://www.google.com"

        def response = TestSuite.buildRequest()
                .contentType(TestConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Basic " + Base64.encoder.encodeToString(
                        headerString.getBytes(Charset.forName("UTF-8"))))
                .formParams(TestConstants.REQUEST_KEY, AUAuthorisationBuilder.getSignedRequestObject(scopeString,
                        AUConstants.DEFAULT_SHARING_DURATION, true, cdrArrangementId,
                        incorrectRedirectUrl, clientId).serialize())
                .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                .post(AUConstants.PAR_ENDPOINT)

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(response, "error_description"),
                "Registered callback does not match with the provided url.")
    }

    @Test
    void "TC0205016_Unable to extract request uri if the client id mismatch with the application client id"() {

        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        String scopeString = "openid ${String.join(" ", scopes.collect({ it.scopeString }))}"
        def incorrectClientId = "YwSmCUteklf0T3MJdW8IQeM1kLga"

        def parResponse = TestSuite.buildRequest()
                .contentType(TestConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Basic " + Base64.encoder.encodeToString(
                        headerString.getBytes(Charset.forName("UTF-8"))))
                .formParams(TestConstants.REQUEST_KEY, AUAuthorisationBuilder.getSignedRequestObject(scopeString,
                        6000, true, cdrArrangementId, AppConfigReader.getRedirectURL(),
                        incorrectClientId).getAt("parsedString"))
                .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                .post(AUConstants.PAR_ENDPOINT)

        Assert.assertEquals(parResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(parResponse, "error_description"),
                "Cannot find an application associated with the given consumer key : " + incorrectClientId)
    }

    //TODO: Enable and update test after fixing: https://github.com/wso2-enterprise/financial-open-banking/issues/6778
    @Test(enabled = false)
    void "TC0205017_Initiate authorisation flow by passing cdr_arrangement_id without PAR"() {

        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = TestUtil.getJwtTokenPayload(userAccessToken.tokens.accessToken.toString()).get("consent_id")

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true, cdrArrangementId)

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new BasicAuthErrorStep(authorisationBuilder.authoriseUrl))
                .execute()

        def errorMessage = URLDecoder.decode(automation.currentUrl.get().split("&")[1]
                .split("=")[1].toString(), "UTF8")
        Assert.assertEquals(errorMessage, clientId)
    }

    @Test
    void "TC0205018_Unable pass request_uri in the body of PAR request"() {

        def request_uri = "urn::bK4mreEMpZ42Ot4xxMOQdM2OvqhA66Rn"

        def parResponse = TestSuite.buildRequest()
                .contentType(TestConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Basic " + Base64.encoder.encodeToString(
                        headerString.getBytes(Charset.forName("UTF-8"))))
                .formParams(TestConstants.REQUEST_URI_KEY, request_uri)
                .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                .post(AUConstants.PAR_ENDPOINT)

        Assert.assertEquals(parResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(parResponse, "error_description"),
                "Request does not allow request_uri parameter")
    }
}
