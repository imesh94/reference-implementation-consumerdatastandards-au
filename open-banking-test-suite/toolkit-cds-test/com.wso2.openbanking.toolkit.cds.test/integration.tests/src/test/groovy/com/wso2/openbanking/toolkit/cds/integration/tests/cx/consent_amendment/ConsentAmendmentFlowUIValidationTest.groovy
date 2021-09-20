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

package com.wso2.openbanking.toolkit.cds.integration.tests.cx.consent_amendment

import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.test.framework.automation.AUBasicAuthAutomationStep
import com.wso2.openbanking.test.framework.automation.BasicAuthErrorStep
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.test.framework.automation.WaitForRedirectAutomationStep
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUAuthorisationBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURegistrationRequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUDCRConstants
import io.restassured.response.Response
import org.openqa.selenium.By
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.text.SimpleDateFormat

/**
 * Consent Amendment Flow CX guidelines Tests.
 */
class ConsentAmendmentFlowUIValidationTest extends AbstractAUTests{


    private List<AUConstants.SCOPES> scopes = [
            AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
            AUConstants.SCOPES.BANK_TRANSACTION_READ,
            AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ
    ]

    private String authorisationCode, secondAuthorisationCode = null
    private AccessTokenResponse userAccessToken, secondUserAccessToken = null
    private String cdrArrangementId = ""
    private String requestUri
    private String account1Id, account2Id

    @BeforeClass(alwaysRun = true)
    void "Initialize Test Suite"() {
        TestSuite.init()

        authorisationCode = doAuthorization(scopes, AUConstants.DEFAULT_SHARING_DURATION, true)
        Assert.assertNotNull(authorisationCode)

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")
        Assert.assertNotNull(cdrArrangementId)

        scopes.remove(AUConstants.SCOPES.BANK_TRANSACTION_READ)
        scopes.add(AUConstants.SCOPES.BANK_PAYEES_READ)
    }

    @Test()
    void "TC001 Verify the account selection page should show the pre selected account"() {

        //Retrieve Request URI via Push request
        requestUri = TestUtil.parseResponseBody(doPushAuthorisationRequest(scopes, AUConstants.AMENDED_SHARING_DURATION,
                true, cdrArrangementId), "request_uri")
        Assert.assertNotNull(requestUri)

        //Verification of the pre selected accounts
        new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(new AUAuthorisationBuilder(
                        scopes, requestUri.toURI()).authoriseUrl))
                .addStep { driver, context ->
                    Assert.assertTrue(driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).isSelected(),
                            "Preselected account not selected")
                }
                .execute()
    }

    @Test()
    void "TC002 Verify the consumer can view the name of the relevant accredited data recipient"() {

        // Data recipient name extracted from DCR GET call
        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(
                AURequestBuilder.getApplicationToken(scopes.collect({ it.scopeString }),
                        AppConfigReader.getClientId()))
                .when()
                .get(AUDCRConstants.REGISTRATION_ENDPOINT + AppConfigReader.getClientId())

        Assert.assertNotNull(registrationResponse)
        String adrName = registrationResponse.jsonPath().get("org_name") + "," + registrationResponse.jsonPath().get("client_name")

        //Retrieve Request URI via Push request
        requestUri = TestUtil.parseResponseBody(doPushAuthorisationRequest(scopes, AUConstants.AMENDED_SHARING_DURATION,
                true, cdrArrangementId), "request_uri")
        Assert.assertNotNull(requestUri)

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, requestUri.toURI())

        new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
                    Assert.assertTrue(driver.findElement(By.xpath(AUConstants.ADR_NAME_HEADER_XPATH)).getText().contains(adrName))
                }
                .execute()
    }

    @Test()
    void "TC003 Verify the System should display the review page to reflect the amended attributes"() {


        requestUri = TestUtil.parseResponseBody(doPushAuthorisationRequest(scopes, AUConstants.AMENDED_SHARING_DURATION,
                true, cdrArrangementId), "request_uri")
        Assert.assertNotNull(requestUri)

        String sharingPeriod = new SimpleDateFormat("YYYY-MM-dd").format(new Date()) + " - " +
                new SimpleDateFormat("YYYY-MM-dd").format(new Date() + 1)

        new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(new AUAuthorisationBuilder(
                        scopes, requestUri.toURI()).authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUTestUtil.getAltSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()

                    Assert.assertTrue(driver.findElement(By.xpath(AUConstants.LBL_NEW_PAYEES_INDICATOR_XPATH)).isDisplayed())
                    Assert.assertTrue(driver.findElement(By.xpath(AUConstants.LBL_NEW_SHARING_DURATION_XPATH)).isDisplayed())
                    Assert.assertTrue(driver.findElement(By.xpath(AUConstants.CONSENT_EXPIRY_XPATH)).getText().contains(sharingPeriod))
                }
                .execute()
    }

    @Test(dependsOnMethods = "TC003 Verify the System should display the review page to reflect the amended attributes")
    void "TC004 Verify the consent amendment of multiple accounts"() {

        requestUri = TestUtil.parseResponseBody(doPushAuthorisationRequest(scopes, AUConstants.AMENDED_SHARING_DURATION,
                true, cdrArrangementId), "request_uri")
        Assert.assertNotNull(requestUri)

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(new AUAuthorisationBuilder(
                        scopes, requestUri.toURI()).authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUTestUtil.getAltSingleAccountXPath())).click()
                    account1Id = driver.findElement(By.xpath(AUConstants.LBL_ACCOUNT_1_ID_XPATH)).getText()
                    account2Id = driver.findElement(By.xpath(AUConstants.LBL_ACCOUNT_2_ID_XPATH)).getText()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_CONFIRM_XPATH)).click()
                }
                .addStep(new WaitForRedirectAutomationStep())
                .execute()
        secondAuthorisationCode = TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())
        secondUserAccessToken = AURequestBuilder.getUserToken(secondAuthorisationCode)

        Response response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 1)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer " + secondUserAccessToken.tokens.accessToken.toString())
                .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                .get("${AUConstants.CDS_100_PATH}${AUConstants.BULK_ACCOUNT_PATH}/")

        Assert.assertEquals(response.jsonPath().getList("data.accounts").size(), 2)
        Assert.assertEquals(response.jsonPath().get("data.accounts[0].accountId"), account1Id)
        Assert.assertEquals(response.jsonPath().get("data.accounts[1].accountId"), account2Id)
    }

    @Test(dependsOnMethods = "TC003 Verify the System should display the review page to reflect the amended attributes")
    void "TC005 Verify the instruction on how to manage the data-sharing agreements"() {

        requestUri = TestUtil.parseResponseBody(doPushAuthorisationRequest(scopes, AUConstants.AMENDED_SHARING_DURATION,
                true, cdrArrangementId), "request_uri")
        Assert.assertNotNull(requestUri)

        new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(new AUAuthorisationBuilder(
                        scopes, requestUri.toURI()).authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUTestUtil.getAltSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                    Assert.assertTrue(driver.findElement(By.xpath(AUConstants.LBL_WHERE_TO_MANAGE_INSTRUCTION_XPATH))
                            .getText().contains(AUConstants.LBL_WHERE_TO_MANAGE_INSTRUCTION))
                }
                .execute()
    }

    @Test(priority = 1)
    void "TC006 Verify back button on the CDR policy page at consent amendment"() {

        requestUri = TestUtil.parseResponseBody(doPushAuthorisationRequest(scopes, AUConstants.AMENDED_SHARING_DURATION,
                true, cdrArrangementId), "request_uri")
        Assert.assertNotNull(requestUri)

        new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(new AUAuthorisationBuilder(
                        scopes, requestUri.toURI()).authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUTestUtil.getAltSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                    Assert.assertTrue(driver.findElement(By.xpath(AUConstants.CONSENT_AUTHORIZE_FLOW_BACK_XPATH)).isDisplayed())
                    driver.findElement(By.xpath(AUConstants.CONSENT_AUTHORIZE_FLOW_BACK_XPATH)).click()
                    Assert.assertTrue(driver.findElement(By.xpath(AUConstants.LBL_SELECT_THE_ACCOUNTS_XPATH)).isDisplayed())
                }
                .execute()
    }

    @Test(priority = 1)
    void "TC007 Verify deny flow for consent amendment  at consent amendment"() {

        requestUri = TestUtil.parseResponseBody(doPushAuthorisationRequest(scopes, AUConstants.AMENDED_SHARING_DURATION,
                true, cdrArrangementId), "request_uri")
        Assert.assertNotNull(requestUri)

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(new AUAuthorisationBuilder(
                        scopes, requestUri.toURI()).authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUTestUtil.getAltSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                    Assert.assertTrue(driver.findElement(By.xpath(AUConstants.CONSENT_CANCEL_XPATH)).isDisplayed())
                    driver.findElement(By.xpath(AUConstants.CONSENT_CANCEL_XPATH)).click()
                    driver.findElement(By.xpath(AUConstants.CONFIRM_CONSENT_DENY_XPATH)).click()
                }
                .execute()
        Assert.assertTrue(TestUtil.getDecodedUrl(automation.currentUrl.get()).contains("User skip the consent flow"))
    }

    @Test(priority = 1)
    void "TC008 Verify an initiate Authorization request for consent Amendment with a expired request_uri from PAR"() {

        requestUri = TestUtil.parseResponseBody(doPushAuthorisationRequest(scopes, AUConstants.AMENDED_SHARING_DURATION,
                true, cdrArrangementId), "request_uri")
        Assert.assertNotNull(requestUri)
        sleep(65000)

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new BasicAuthErrorStep(new AUAuthorisationBuilder(
                        scopes, requestUri.toURI()).authoriseUrl))
                .execute()

        Assert.assertTrue(automation.currentUrl.get().split("error_description=")[1].split("&")[0]
                .replaceAll("\\+", " ").contains("Expired request URI"))
    }
}
