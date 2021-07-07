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

package com.wso2.openbanking.toolkit.cds.integration.tests.accounts

import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.test.framework.automation.AUBasicAuthAutomationStep
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.test.framework.automation.WaitForRedirectAutomationStep
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUAuthorisationBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.testng.Assert
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

/**
 * Base Test For Accounts.
 */
class DuplicateCommonAuthIdTest {

    public List<AUConstants.SCOPES> scopes = [
            AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
            AUConstants.SCOPES.BANK_ACCOUNT_DETAIL_READ,
            AUConstants.SCOPES.BANK_TRANSACTION_READ,
    ]

    private String authorisationCode
    public String userAccessToken
    public String consentedAccount
    public String secondConsentedAccount

    @BeforeTest (alwaysRun = true)
    void "Initialize Test Suite"() {

        TestSuite.init()
    }

    @Test
    void "TC0202006_Initiate two authorisation consent flows on same browser session"() {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION,true
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY, false)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
            // Consent First Account
            WebElement accElement = driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath()))
            consentedAccount = accElement.getAttribute("value")
            accElement.click()
            // Consent Second Account
            accElement = driver.findElement(By.xpath(AUTestUtil.getAltSingleAccountXPath()))
            secondConsentedAccount = accElement.getAttribute("value")
            accElement.click()
            // Submit consent
            driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()

            // Extra step for OB-2.0 AU Authentication flow.
            if (TestConstants.SOLUTION_VERSION_200.equals(ConfigParser.getInstance().getSolutionVersion())) {
                driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
            }
        }
        .addStep(new WaitForRedirectAutomationStep())
                .execute(false)

        // Get Code From URL
        authorisationCode = TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())
        Assert.assertNotNull(authorisationCode)


        def secondAuthorisation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY, true)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
            // Consent First Account
            WebElement accElement = driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath()))
            consentedAccount = accElement.getAttribute("value")
            accElement.click()
            // Consent Second Account
            accElement = driver.findElement(By.xpath(AUTestUtil.getAltSingleAccountXPath()))
            secondConsentedAccount = accElement.getAttribute("value")
            accElement.click()
            // Submit consent
            driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()

            // Extra step for OB-2.0 AU Authentication flow.
            if (TestConstants.SOLUTION_VERSION_200.equals(ConfigParser.getInstance().getSolutionVersion())) {
                driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
            }
        }
        .addStep(new WaitForRedirectAutomationStep())
                .execute(true)

        // Get Code From URL
        authorisationCode = TestUtil.getHybridCodeFromUrl(secondAuthorisation.currentUrl.get())
        Assert.assertNotNull(authorisationCode)
    }

    @Test(dependsOnMethods = "TC0202006_Initiate two authorisation consent flows on same browser session")
    void "TC0203005_Exchange authorisation code for access token"() {

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode).tokens.accessToken
        Assert.assertNotNull(userAccessToken)
    }

    @Test(dependsOnMethods = "TC0203005_Exchange authorisation code for access token")
    void "TC0401007_Retrieve bulk accounts list"() {

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.CDR_ENDPOINT_VERSION)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
    }
}
