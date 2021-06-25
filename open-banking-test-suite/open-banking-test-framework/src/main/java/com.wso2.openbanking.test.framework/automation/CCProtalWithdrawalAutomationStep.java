/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.test.framework.automation;

import com.wso2.openbanking.test.framework.util.ConfigParser;
import com.wso2.openbanking.test.framework.util.TestConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.concurrent.TimeUnit;

/**
 * CCPortal Automation.
 */
public class CCProtalWithdrawalAutomationStep implements BrowserAutomationStep {

    public String CCPortalUrl;

    public CCProtalWithdrawalAutomationStep(String CCPortalUrl) {

        this.CCPortalUrl = CCPortalUrl;

    }
    /**
     * Execute automation using driver
     *
     * @param webDriver driver object.
     * @param context   automation context.
     */
    public void execute(RemoteWebDriver webDriver, BrowserAutomation.AutomationContext context) {

        webDriver.navigate().to(CCPortalUrl);

        WebElement username = webDriver.findElement(By.id(TestConstants.USERNAME_FIELD_ID));
        username.clear();
        username.sendKeys(ConfigParser.getInstance().getCCPortal());

        WebElement password = webDriver.findElement(By.id(TestConstants.PASSWORD_FIELD_ID));
        password.clear();
        password.sendKeys(ConfigParser.getInstance().getCCPortalPassword());

        webDriver.findElement(By.xpath(TestConstants.CCPORTAL_SIGNIN_XPATH)).submit();

        webDriver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
    }
}
