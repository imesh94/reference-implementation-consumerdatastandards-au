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

package com.wso2.openbanking.toolkit.cds.integration.tests.cx.authflow

import com.wso2.openbanking.test.framework.automation.AuthorizationFlowNavigationAutomationStep
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUAuthorisationBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.testng.Assert
import org.testng.annotations.Test

import java.util.concurrent.TimeUnit

/**
 * User Authentication Flow Validation Test
 */
class UserAuthenticationFlowValidationTest {

    List<AUConstants.SCOPES> scopes = [AUConstants.SCOPES.BANK_CUSTOMER_BASIC_READ]

    @Test
    void "TC0201001_Consumer redirects to the authorisation page upon valid user identification"() {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AuthorizationFlowNavigationAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->

            //User Login
            WebElement username = driver.findElement(By.xpath(TestConstants.USERNAME_FIELD_XPATH_AU_200))
            username.clear()
            username.sendKeys(ConfigParser.getInstance().getPsu())

            driver.findElement(By.xpath(TestConstants.AUTH_SIGNIN_XPATH_AU_200)).click()
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)

            //Identifier First Authentication
            String otpCode = TestConstants.OTP_CODE

            Assert.assertTrue(driver.findElement(By.xpath(TestConstants.LBL_OTP_TIMEOUT)).isDisplayed())

            driver.findElement(By.id(TestConstants.TXT_OTP_CODE)).sendKeys(otpCode)

            Assert.assertTrue(driver.findElement(By.xpath(TestConstants.LBL_FOOTER_DESCRIPTION)).getText().trim()
                    .contains("Your Customer ID will not be shared with \"Mock Company Inc.,Mock Software\". One time " +
                            "passwords are used to share banking data. You will never be asked to provide your real " +
                            "password to share banking data."))



            driver.findElement(By.xpath(TestConstants.BTN_AUTHENTICATE)).click()

            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)

            Assert.assertTrue(driver.findElement(By.xpath(TestConstants.ELE_CONSENT_PAGE)).isDisplayed())
        }
        .execute()
    }

    @Test
    void "TC0201002_Consumer unable to proceed consent flow upon invalid user identification" () {

        String invalidUserName = "abc@gmail.com"

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AuthorizationFlowNavigationAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->

            //User Login
            WebElement username = driver.findElement(By.xpath(TestConstants.USERNAME_FIELD_XPATH_AU_200))
            username.clear()
            username.sendKeys(invalidUserName)

            driver.findElement(By.xpath(TestConstants.AUTH_SIGNIN_XPATH_AU_200)).click()
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)

            Assert.assertTrue(driver.findElement(By.xpath(TestConstants.LBL_INCORRECT_USERNAME)).getText()
                    .contains("These Details are incorrect. Please try again."))
        }
        .execute()
    }

    @Test
    void "TC0201003_Consumer unable to proceed consent flow upon invalid second factor authentication" () {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AuthorizationFlowNavigationAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->

            //User Login
            WebElement username = driver.findElement(By.xpath(TestConstants.USERNAME_FIELD_XPATH_AU_200))
            username.clear()
            username.sendKeys(ConfigParser.getInstance().getPsu())

            driver.findElement(By.xpath(TestConstants.AUTH_SIGNIN_XPATH_AU_200)).click()
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)

            //Identifier First Authentication
            String otpCode = "987654"

            Assert.assertTrue(driver.findElement(By.xpath(TestConstants.LBL_OTP_TIMEOUT)).isDisplayed())

            driver.findElement(By.id(TestConstants.TXT_OTP_CODE)).sendKeys(otpCode)
            driver.findElement(By.xpath(TestConstants.BTN_AUTHENTICATE)).click()

            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)

            Assert.assertTrue(driver.findElement(By.xpath(TestConstants.LBL_AUTHENTICATION_FAILURE)).getText()
                    .contains("Authentication Failed! Please Retry"))
        }
        .execute()
    }

    @Test
    void "TC0103003_Consumer unable to redirect to Authorisation page without OTP" () {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AuthorizationFlowNavigationAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->

            //User Login
            WebElement username = driver.findElement(By.xpath(TestConstants.USERNAME_FIELD_XPATH_AU_200))
            username.clear()
            username.sendKeys(ConfigParser.getInstance().getPsu())

            driver.findElement(By.xpath(TestConstants.AUTH_SIGNIN_XPATH_AU_200)).click()
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)

            //Identifier First Authentication
            String otpCode = ""

            Assert.assertTrue(driver.findElement(By.xpath(TestConstants.LBL_OTP_TIMEOUT)).isDisplayed())

            driver.findElement(By.id(TestConstants.TXT_OTP_CODE)).sendKeys(otpCode)
            driver.findElement(By.xpath(TestConstants.BTN_AUTHENTICATE)).click()

            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)

            //User not redirect to the Authorisation Page
            Assert.assertTrue(driver.findElement(By.xpath(TestConstants.LBL_OTP_TIMEOUT)).isDisplayed())
        }
        .execute()
    }

    @Test
    void "TC0103006_Consumer unable to redirect to Authorisation page with expired OTP" () {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AuthorizationFlowNavigationAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->

            //User Login
            WebElement username = driver.findElement(By.xpath(TestConstants.USERNAME_FIELD_XPATH_AU_200))
            username.clear()
            username.sendKeys(ConfigParser.getInstance().getPsu())

            driver.findElement(By.xpath(TestConstants.AUTH_SIGNIN_XPATH_AU_200)).click()
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)

            //Identifier First Authentication
            String otpCode = TestConstants.OTP_CODE

            Assert.assertTrue(driver.findElement(By.xpath(TestConstants.LBL_OTP_TIMEOUT)).isDisplayed())

            driver.findElement(By.id(TestConstants.TXT_OTP_CODE)).sendKeys(otpCode)

            sleep(80000)

            driver.findElement(By.xpath(TestConstants.BTN_AUTHENTICATE)).click()

            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)

            Assert.assertTrue(driver.findElement(By.xpath(TestConstants.LBL_AUTHENTICATION_FAILURE)).getText()
                    .contains("The code entered is expired. Click Resend Code to continue."))
        }
        .execute()
    }
}
