/*
 * Copyright (c) 2022-2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.cds.test.framework.automation.consent

import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.AUPageObjects
import com.wso2.openbanking.test.framework.automation.AutomationMethod
import com.wso2.openbanking.test.framework.automation.BrowserAutomationStep
import com.wso2.openbanking.test.framework.automation.OBBrowserAutomation
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

/**
 *  AU authorization automation step
 */
class AUBasicAuthAutomationStep implements BrowserAutomationStep {

    private String authorizeUrl
    private AUConfigurationService auConfiguration
    private static final Log log = LogFactory.getLog(AUBasicAuthAutomationStep.class);

    /**
     * Initialize Basic Auth Flow.
     *
     * @param authorizeUrl authorise URL.
     */
    AUBasicAuthAutomationStep(String authorizeUrl) {
        this.authorizeUrl = authorizeUrl
        this.auConfiguration = new AUConfigurationService()
    }

    @Override
    void execute(RemoteWebDriver webDriver, OBBrowserAutomation.AutomationContext context) {
        WebDriverWait wait = new WebDriverWait(webDriver, 30)
        AutomationMethod driver = new AutomationMethod(webDriver)
        webDriver.navigate().to(authorizeUrl)
        //Enter User Name
        driver.executeTextField(AUPageObjects.AU_USERNAME_FIELD_ID, auConfiguration.getUserPSUName())

        //Click on SignIn Button
        driver.submitButtonXpath(AUPageObjects.AU_AUTH_SIGNIN_XPATH)
        driver.waitTimeRange(30)

        //Second Factor Authentication Step
        try{
            if (driver.isElementDisplayed(AUPageObjects.AU_BTN_AUTHENTICATE)) {
                driver.executeSMSOTP(AUPageObjects.AU_LBL_SMSOTP_AUTHENTICATOR, AUPageObjects.AU_TXT_OTP_CODE_ID, AUConstants.AU_OTP_CODE)
                driver.clickButtonXpath(AUPageObjects.AU_BTN_AUTHENTICATE)
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(AUPageObjects.AU_BTN_AUTHENTICATE)))
            }
        } catch (NoSuchElementException e) {
            log.info("Second Factor Authentication Step is not required")
        }
    }
}

