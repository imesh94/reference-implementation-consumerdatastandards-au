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

import com.wso2.cds.test.framework.constant.AUPageObjects
import com.wso2.openbanking.test.framework.automation.AutomationMethod
import com.wso2.openbanking.test.framework.automation.BrowserAutomationStep
import com.wso2.openbanking.test.framework.automation.OBBrowserAutomation
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import org.openqa.selenium.remote.RemoteWebDriver
import com.wso2.cds.test.framework.utility.AUTestUtil

/**
 * AU Account section automation step
 */
class AUAccountSelectionStep implements BrowserAutomationStep {

    private AUConfigurationService auConfiguration
    /**
     * Initialize Basic Auth Flow.
     *
     * @param authorizeUrl authorise URL.
     */
    AUAccountSelectionStep() {
        this.auConfiguration = new AUConfigurationService()
    }

    @Override
    void execute(RemoteWebDriver webDriver, OBBrowserAutomation.AutomationContext context) {
        AutomationMethod driver = new AutomationMethod(webDriver)
        driver.clickButtonXpath(AUTestUtil.getSingleAccountXPath())
        driver.clickButtonXpath(AUPageObjects.CONSENT_SUBMIT_XPATH)
        driver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)
    }
}

