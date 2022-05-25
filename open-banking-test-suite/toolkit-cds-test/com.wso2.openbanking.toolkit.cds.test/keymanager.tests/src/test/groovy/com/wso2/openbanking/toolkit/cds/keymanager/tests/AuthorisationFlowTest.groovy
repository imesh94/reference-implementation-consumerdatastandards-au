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
import com.wso2.openbanking.test.framework.automation.AUBasicAuthAutomationStep
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.test.framework.automation.WaitForRedirectAutomationStep
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUAuthorisationBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import org.openqa.selenium.By
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.testng.asserts.SoftAssert


/**
 * Authorisation Flow Testing.
 */
class AuthorisationFlowTest {

    private List<AUConstants.SCOPES> scopes = [
            AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
            AUConstants.SCOPES.BANK_TRANSACTION_READ,
            AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ
    ]

    private String authorisationCode
    private AccessTokenResponse userAccessToken

    @BeforeClass (alwaysRun = true)
    void "Initialize Test Suite"() {

        TestSuite.init()
    }

    @Test (groups = "SmokeTest")
    void "TC0202001_Initiate authorisation consent flow"() {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

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

        Assert.assertNotNull(authorisationCode)

    }

    @Test(groups = "SmokeTest", dependsOnMethods = "TC0202001_Initiate authorisation consent flow")
    void "TC0203001_Exchange authorisation code for access token"() {

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
        Assert.assertNotNull(userAccessToken.tokens.refreshToken)
        Assert.assertNotNull(userAccessToken.getCustomParameters().get("cdr_arrangement_id"))
    }

    @Test(groups = "SmokeTest",
            dependsOnMethods = "TC0203001_Exchange authorisation code for access token")
    void "TC0203006_Check the status of the access token after generating user access token"() {

        def response = AURequestBuilder
                .buildIntrospectionRequest(userAccessToken.tokens.accessToken.toString())
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        Assert.assertTrue(response.jsonPath().get("active").equals(true))
    }

    @Test (priority = 1)
    void "OB-1141_Initiate authorisation consent flow with cdr_arrangement_id claim in request object"() {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true, AUConstants.UUID
        )
        String errorMessage = "cdr_arrangement_id can only be sent through PAR request"

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep { driver, context -> driver.navigate().to(authorisationBuilder.authoriseUrl)}.execute()

        String url = automation.currentUrl.get()
        String errorUrl

        if (AUConstants.SOLUTION_VERSION_150.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            errorUrl = url.split("oauthErrorMsg=")[1].split("&")[0].replaceAll("\\+", " ")
        } else {
            errorUrl = url.split("error_description=")[1].split("&")[0].replaceAll("\\+"," ")
        }
        Assert.assertEquals(errorUrl, errorMessage)

    }

    @Test (priority = 1)
    void "TC0202007_Initiate authorisation consent deny flow"() {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
            driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()

            // Extra step for OB-2.0 AU Authentication flow.
            if (TestConstants.SOLUTION_VERSION_300.equals(ConfigParser.getInstance().getSolutionVersion())) {
                driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                driver.findElement(By.xpath(AUConstants.CONFIRM_CONSENT_DENY_XPATH)).click()
            } else {
                driver.findElement(By.xpath(AUConstants.CONSENT_DENY_XPATH)).click()
            }

        }
                .execute()

        if (TestConstants.SOLUTION_VERSION_300.equals(ConfigParser.getInstance().getSolutionVersion())) {
            String url = automation.currentUrl.get()
            def errorMessage = url.split("error_description=")[1].split("&")[0].replaceAll("\\+"," ")
            Assert.assertEquals(errorMessage, "User denied the consent")

        } else {
            String url = automation.currentUrl.get()
            def errorMessage = url.split("=")[1].split("&")[0].replaceAll("\\+", " ")
            Assert.assertEquals(errorMessage, "User denied the consent")
        }
    }

    @Test (priority = 1)
    void "TC0203007_Status of the access token of previous authorisation code after re generating a new authorisation code"() {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

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

        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)

        def response1 = AURequestBuilder
                .buildIntrospectionRequest(userAccessToken.tokens.accessToken.toString())
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        Assert.assertTrue(response1.jsonPath().get("active").equals(true))

        automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
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

        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)

        def response2 = AURequestBuilder
                .buildIntrospectionRequest(userAccessToken.tokens.accessToken.toString())
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        Assert.assertTrue(response2.jsonPath().get("active").equals(true))
        Assert.assertTrue(response1.jsonPath().get("active").equals(true))
    }

    @Test (priority = 1)
    void "TC0203009_Status of the consent after revoking the access token bound to the consent"() {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

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

        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)

        def response1 = AURequestBuilder
                .buildIntrospectionRequest(userAccessToken.tokens.accessToken.toString())
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        Assert.assertTrue(response1.jsonPath().get("active").equals(true))

        // Revoke access Token
        def revokeResponse = AURequestBuilder
                .buildRevokeIntrospectionRequest(userAccessToken.tokens.accessToken.toString())
                .post("/oauth2/revoke")

        Assert.assertEquals(revokeResponse.statusCode(), AUConstants.STATUS_CODE_200)

        def response2 = AURequestBuilder
                .buildIntrospectionRequest(userAccessToken.tokens.accessToken.toString())
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        Assert.assertTrue(response2.jsonPath().get("active").equals(false))
    }

    @Test (priority = 2)
    void "OB-1142_Initiate authorisation consent flow only with scopes that require account selection"() {

        scopes = [
                AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
                AUConstants.SCOPES.BANK_ACCOUNT_DETAIL_READ,
                AUConstants.SCOPES.BANK_TRANSACTION_READ,
                AUConstants.SCOPES.BANK_REGULAR_PAYMENTS_READ
        ]

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

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

        Assert.assertNotNull(authorisationCode)

    }

    @Test (priority = 2)
    void "OB-1143_Initiate authorisation consent flow only with scopes that do not require account selection"() {

        scopes = [
                AUConstants.SCOPES.BANK_PAYEES_READ,
                AUConstants.SCOPES.BANK_CUSTOMER_BASIC_READ,
                AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ
        ]

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                }
                .addStep(new WaitForRedirectAutomationStep())
                .execute()

        // Get Code From URL
        authorisationCode = TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())

        Assert.assertNotNull(authorisationCode)

    }

    @Test (priority = 2)
    void "OB-1144_Initiate authorisation consent flow only with openid scope"() {

        scopes = []

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )
        String errorMessage = "No valid scopes found in the request"

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep { driver, context -> driver.navigate().to(authorisationBuilder.authoriseUrl)}.execute()

        String url = automation.currentUrl.get()
        String errorUrl

        if (AUConstants.SOLUTION_VERSION_150.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            errorUrl = url.split("oauthErrorMsg=")[1].split("&")[0].replaceAll("\\+", " ")
        } else {
            errorUrl = url.split("error_description=")[1].split("&")[0].replaceAll("\\+"," ")
        }
        Assert.assertEquals(errorUrl, errorMessage)

    }

    @Test (priority = 2)
    void "OB-1145_Initiate authorisation consent flow only with openid + scopes not applicable to CDR Data Retrieval"() {

        scopes = [
                AUConstants.SCOPES.ADMIN_METRICS_BASIC_READ,
                AUConstants.SCOPES.ADMIN_METADATA_UPDATE
        ]

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )
        String errorMessage = "No valid scopes found in the request"

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep { driver, context -> driver.navigate().to(authorisationBuilder.authoriseUrl)}.execute()

        String url = automation.currentUrl.get()
        String errorUrl

        if (AUConstants.SOLUTION_VERSION_150.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            errorUrl = url.split("oauthErrorMsg=")[1].split("&")[0].replaceAll("\\+", " ")
        } else {
            errorUrl = url.split("error_description=")[1].split("&")[0].replaceAll("\\+"," ")
        }
        Assert.assertEquals(errorUrl, errorMessage)

    }
}
