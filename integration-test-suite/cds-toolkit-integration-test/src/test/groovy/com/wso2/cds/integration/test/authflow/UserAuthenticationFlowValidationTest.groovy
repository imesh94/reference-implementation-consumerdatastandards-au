/**
 * Copyright (c) 2022, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.authflow

import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.AUPageObjects
import com.wso2.cds.test.framework.request_builder.AUAuthorisationBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import com.wso2.openbanking.test.framework.automation.AutomationMethod
import com.wso2.openbanking.test.framework.automation.NavigationAutomationStep
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

import java.util.concurrent.TimeUnit
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.testng.Assert
import org.testng.annotations.Test


/**
 * User Authentication Flow Validation Test.
 */
class UserAuthenticationFlowValidationTest extends AUTest {

    List<AUAccountScope> scopes = [AUAccountScope.BANK_CUSTOMER_BASIC_READ]

    @Test
    void "TC0201001_Consumer redirects to the authorisation page upon valid user identification"() {

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(requestUri.toURI(), auConfiguration.getAppInfoClientID())
                .toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new NavigationAutomationStep(authoriseUrl,AUConstants.DEFAULT_DELAY))
                .addStep { driver, context ->

                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //User Login
                    authWebDriver.executeTextField(AUPageObjects.AU_USERNAME_FIELD_ID, auConfiguration.getUserPSUName())

                    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)

                    //Click on SignIn Button
                    authWebDriver.clickButtonXpath(AUPageObjects.AU_AUTH_SIGNIN_XPATH)

                    //Identifier First Authentication
                    Assert.assertTrue(authWebDriver.isElementDisplayed(AUConstants.LBL_OTP_TIMEOUT))
                    authWebDriver.executeSMSOTP(AUPageObjects.AU_LBL_SMSOTP_AUTHENTICATOR, AUPageObjects.AU_TXT_OTP_CODE_ID,
                            AUConstants.AU_OTP_CODE)
                    Assert.assertTrue(driver.findElement(By.xpath(AUConstants.LBL_FOOTER_DESCRIPTION)).getText().trim()
                            .contains("Your Customer ID will not be shared with \"Mock Company Inc.,Mock Software 1\". " +
                                    "One time passwords are used to share banking data. You will never be asked to provide " +
                                    "your real password to share banking data."))

                    authWebDriver.clickButtonXpath(AUPageObjects.AU_BTN_AUTHENTICATE)
                    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)
                    Assert.assertTrue(authWebDriver.isElementDisplayed(AUPageObjects.ORGANIZATION_A_PROFILE_SELECTION))
                }
        automation.execute()
    }

    @Test
    void "TC0201002_Consumer unable to proceed consent flow upon invalid user identification" () {

        String invalidUserName = "abc@gmail.com"

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(requestUri.toURI(), auConfiguration.getAppInfoClientID())
                .toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new NavigationAutomationStep(authoriseUrl,AUConstants.DEFAULT_DELAY))
                .addStep { driver, context ->

                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //User Login
                    authWebDriver.executeTextField(AUPageObjects.AU_USERNAME_FIELD_ID, invalidUserName)

                    authWebDriver.clickButtonXpath(AUPageObjects.AU_AUTH_SIGNIN_XPATH)

                    WebDriverWait wait = new WebDriverWait(driver, 60)
                    wait.until( ExpectedConditions.visibilityOfElementLocated(By.xpath(AUPageObjects.LBL_ERROR_TRY_AGAIN)))

                    Assert.assertTrue(authWebDriver.getAttributeText(AUPageObjects.LBL_ERROR_TRY_AGAIN)
                            .contains("These Details are incorrect. Please try again."))
                }
                .execute()
    }

    @Test
    void "TC0201003_Consumer unable to proceed consent flow upon invalid second factor authentication" () {

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(requestUri.toURI(), auConfiguration.getAppInfoClientID())
                .toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new NavigationAutomationStep(authoriseUrl,AUConstants.DEFAULT_DELAY))
                .addStep { driver, context ->

                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //User Login
                    authWebDriver.executeTextField(AUPageObjects.AU_USERNAME_FIELD_ID, auConfiguration.getUserPSUName())

                    authWebDriver.clickButtonXpath(AUPageObjects.AU_AUTH_SIGNIN_XPATH)
                    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)

                    //Identifier First Authentication
                    String otpCode = "987654"

                    Assert.assertTrue(authWebDriver.isElementDisplayed(AUConstants.LBL_OTP_TIMEOUT))

                    authWebDriver.executeSMSOTP(AUPageObjects.AU_LBL_SMSOTP_AUTHENTICATOR, AUPageObjects.AU_TXT_OTP_CODE_ID,
                            otpCode)
                    authWebDriver.clickButtonXpath(AUPageObjects.BTN_AUTHENTICATE)

                    WebDriverWait wait = new WebDriverWait(driver, 60)
                    wait.until( ExpectedConditions.visibilityOfElementLocated(By.xpath(AUConstants.LBL_AUTHENTICATION_FAILURE)))

                    Assert.assertTrue(authWebDriver.getAttributeText(AUConstants.LBL_AUTHENTICATION_FAILURE)
                            .contains("Authentication Failed! Please Retry"))

                }
                .execute()
    }

    @Test
    void "TC0103003_Consumer unable to redirect to Authorisation page without OTP" () {

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(requestUri.toURI(), auConfiguration.getAppInfoClientID())
                .toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new NavigationAutomationStep(authoriseUrl,AUConstants.DEFAULT_DELAY))
                .addStep { driver, context ->

                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //User Login
                    authWebDriver.executeTextField(AUPageObjects.AU_USERNAME_FIELD_ID, auConfiguration.getUserPSUName())

                    authWebDriver.clickButtonXpath(AUPageObjects.AU_AUTH_SIGNIN_XPATH)
                    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)

                    //Identifier First Authentication
                    String otpCode = ""

                    Assert.assertTrue(authWebDriver.isElementDisplayed(AUConstants.LBL_OTP_TIMEOUT))

                    authWebDriver.executeSMSOTP(AUPageObjects.AU_LBL_SMSOTP_AUTHENTICATOR, AUPageObjects.AU_TXT_OTP_CODE_ID,
                            otpCode)
                    authWebDriver.clickButtonXpath(AUPageObjects.BTN_AUTHENTICATE)

                    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)

                    //User not redirect to the Authorisation Page
                    Assert.assertTrue(authWebDriver.isElementDisplayed(AUConstants.LBL_OTP_TIMEOUT))
                }
                .execute()
    }

    @Test
    void "TC0103006_Consumer unable to redirect to Authorisation page with expired OTP" () {

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(requestUri.toURI(), auConfiguration.getAppInfoClientID())
                .toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new NavigationAutomationStep(authoriseUrl,AUConstants.DEFAULT_DELAY))
                .addStep { driver, context ->

                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //User Login
                    authWebDriver.executeTextField(AUPageObjects.AU_USERNAME_FIELD_ID, auConfiguration.getUserPSUName())

                    authWebDriver.clickButtonXpath(AUPageObjects.AU_AUTH_SIGNIN_XPATH)
                    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)

                    //Identifier First Authentication
                    Assert.assertTrue(driver.findElement(By.xpath(AUConstants.LBL_OTP_TIMEOUT)).isDisplayed())

                    authWebDriver.executeSMSOTP(AUPageObjects.AU_LBL_SMSOTP_AUTHENTICATOR, AUPageObjects.AU_TXT_OTP_CODE_ID,
                            AUConstants.OTP_CODE)

                    sleep(80000)

                    authWebDriver.clickButtonXpath(AUPageObjects.BTN_AUTHENTICATE)

                    WebDriverWait wait = new WebDriverWait(driver, 60)
                    wait.until( ExpectedConditions.visibilityOfElementLocated(By.xpath(AUConstants.LBL_AUTHENTICATION_FAILURE)))

                    Assert.assertTrue(authWebDriver.getAttributeText(AUConstants.LBL_AUTHENTICATION_FAILURE)
                            .contains("The code entered is expired. Click Resend Code to continue."))
                }
                .execute()
    }
}
