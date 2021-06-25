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
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.test.framework.automation.WaitForRedirectAutomationStep
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUAuthorisationBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import org.openqa.selenium.By

import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Sharing duration value Testing.
 */
class SharingDurationValidationTest {

    private List<AUConstants.SCOPES> scopes = [
            AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
            AUConstants.SCOPES.BANK_TRANSACTION_READ,
            AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ
    ]
    private String authorisationCode
    private AccessTokenResponse userAccessToken

    private String doAuthorization(long sharingDuration, boolean sendSharingDuration, String assertionValue) {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(scopes, sharingDuration,
                                             sendSharingDuration)

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                    Assert.assertTrue(driver.findElement(By.xpath(AUConstants.CONSENT_EXPIRY_XPATH)).getText()
                            .contains(assertionValue))
                    driver.findElement(By.xpath(AUConstants.CONSENT_CONFIRM_XPATH)).click()
                }
                .addStep(new WaitForRedirectAutomationStep())
                .execute()

        // Get Code From URL
        authorisationCode = TestUtil.getHybridCodeFromURL(automation.currentUrl.get())
        return authorisationCode
    }


    @BeforeClass(alwaysRun = true)
    void "Initialize Test Suite"() {

        TestSuite.init()
    }

    @Test (priority = 1)
    void "TC0202002_Initiate authorisation consent flow with no sharing duration"() {

        authorisationCode = doAuthorization(AUConstants.SINGLE_ACCESS_CONSENT, false, "Single use consent")
        Assert.assertNotNull(authorisationCode)

    }

    @Test(dependsOnMethods = "TC0202002_Initiate authorisation consent flow with no sharing duration", priority = 1)
    void "TC0203002_Check no refresh token when no sharing duration is given"() {

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        Assert.assertNull(userAccessToken.tokens.refreshToken)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
    }


    @Test (priority = 2)
    void "TC0202003_Initiate authorisation consent flow with sharing duration zero"() {

        authorisationCode = doAuthorization(AUConstants.SINGLE_ACCESS_CONSENT, true, "Single use consent")
        Assert.assertNotNull(authorisationCode)
    }

    @Test(dependsOnMethods = "TC0202003_Initiate authorisation consent flow with sharing duration zero", priority = 2)
    void "TC0203003_Check no refresh token for sharing duration zero"() {

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        Assert.assertNull(userAccessToken.tokens.refreshToken)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
    }

    @Test (groups = "SmokeTest", priority = 3)
    void "TC0202004_Initiate authorisation consent flow with sharing duration greater than one year"() {

        OffsetDateTime currentTime = OffsetDateTime.now(ZoneOffset.UTC)
        String consentExpiry = currentTime.plusSeconds(AUConstants.ONE_YEAR_DURATION).getYear().toString()

        authorisationCode = doAuthorization(AUConstants.ONE_YEAR_DURATION, true, consentExpiry)
        Assert.assertNotNull(authorisationCode)
    }

    @Test(groups = "SmokeTest",
            dependsOnMethods = "TC0202004_Initiate authorisation consent flow with sharing duration greater than one year",
            priority = 3)
    void "TC0203004_Check refresh token issued for sharing duration greater than one year"() {

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
        Assert.assertNotNull(userAccessToken.tokens.refreshToken)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
    }

    @Test (priority = 4)
    void "TC0202005_Initiate authorisation consent flow with negative sharing duration"() {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.NEGATIVE_DURATION, true)
        String errorMessage = "Negative sharing_duration"

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
