/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
import org.openqa.selenium.remote.RemoteWebDriver

/**
 *  AU authorization automation step
 */
class AUBasicAuthAutomationStep implements BrowserAutomationStep {

    private String authorizeUrl
    private AUConfigurationService auConfiguration

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
        AutomationMethod driver = new AutomationMethod(webDriver)
        webDriver.navigate().to(authorizeUrl)
        if (!AUConstants.SOLUTION_VERSION_200.equals(auConfiguration.getCommonSolutionVersion())) {
            driver.executeTextFieldXpath(AUPageObjects.AU_USERNAME_FIELD_XPATH_200, auConfiguration.getUserPSUName())
            driver.clickButtonXpath(AUPageObjects.AU_AUTH_SIGNIN_XPATH_200)
        } else {
            driver.executeTextField(AUPageObjects.AU_USERNAME_FIELD_ID, auConfiguration.getUserPSUName())
            driver.executeTextField(AUPageObjects.AU_PASSWORD_FIELD_ID, auConfiguration.getUserPSUPWD())
            driver.submitButtonXpath(AUPageObjects.AU_AUTH_SIGNIN_XPATH)
        }
        driver.waitTimeRange(30)
        driver.executeSMSOTP(AUPageObjects.AU_LBL_SMSOTP_AUTHENTICATOR, AUPageObjects.AU_TXT_OTP_CODE_ID, AUConstants.AU_OTP_CODE)
        driver.clickButtonXpath(AUPageObjects.AU_BTN_AUTHENTICATE)
        driver.waitTimeRange(30)

    }
}

