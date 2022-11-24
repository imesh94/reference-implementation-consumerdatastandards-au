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

package com.wso2.cds.integration.test.authflow

import com.wso2.cds.test.framework.automation.consent.AUBasicAuthAutomationStep
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUPageObjects
import com.wso2.cds.test.framework.request_builder.AUAuthorisationBuilder
import com.wso2.openbanking.test.framework.automation.NavigationAutomationStep
import com.wso2.openbanking.test.framework.configuration.OBConfigParser
import com.wso2.cds.test.framework.utility.AUMockCDRIntegrationUtil
import com.wso2.cds.test.framework.constant.ContextConstants
import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.utility.AURestAsRequestBuilder
import org.checkerframework.checker.units.qual.A
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.testng.Assert
import org.testng.ITestContext
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import com.wso2.cds.test.framework.request_builder.AURegistrationRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil

import java.util.concurrent.TimeUnit

/**
 * User Authentication Flow Validation Test
 */

class UserAuthenticationFlowValidationTest extends AUTest {
    List<AUAccountScope> scopes = [AUAccountScope.BANK_CUSTOMER_BASIC_READ]
    @Test
    void "TC0201001_Consumer redirects to the authorisation page upon valid user identification"() {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder()
        String authoriseUrl = authorisationBuilder.getAuthorizationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION, true).toURI()
        AUConfigurationService auConfigurationService=new AUConfigurationService()
        def automation =getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new NavigationAutomationStep(authoriseUrl,AUConstants.DEFAULT_DELAY))
                .addStep { driver, context ->

                    //User Login
                    WebElement username = driver.findElement(By.xpath(AUPageObjects.AU_USERNAME_FIELD_XPATH_200))
                    username.clear()
                    username.sendKeys(String.valueOf(auConfigurationService.getUserPSUName()))

                    driver.findElement(By.xpath(AUPageObjects.AU_AUTH_SIGNIN_XPATH_200)).click()
                    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)

                    //Identifier First Authentication
                    String otpCode = AUConstants.AU_OTP_CODE

                    Assert.assertTrue(driver.findElement(By.xpath(AUConstants.LBL_OTP_TIMEOUT)).isDisplayed())

                    driver.findElement(By.id(AUPageObjects.AU_TXT_OTP_CODE_ID)).sendKeys(otpCode)

                    Assert.assertTrue(driver.findElement(By.xpath(AUConstants.LBL_FOOTER_DESCRIPTION)).getText().trim()
                            .contains("Your Customer ID will not be shared with \"Mock Company Inc.,Mock Software 1\". " +
                                    "One time passwords are used to share banking data. You will never be asked to provide your real password to share banking data."))



                    driver.findElement(By.xpath(AUPageObjects.AU_BTN_AUTHENTICATE)).click()

                    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)

                    Assert.assertTrue(driver.findElement(By.xpath(AUConstants.ELE_CONSENT_PAGE)).isDisplayed())
                }
                .execute()
    }

}
