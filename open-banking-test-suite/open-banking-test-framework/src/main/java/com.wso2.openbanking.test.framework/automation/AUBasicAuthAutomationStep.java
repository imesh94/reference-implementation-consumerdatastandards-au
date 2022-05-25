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

package com.wso2.openbanking.test.framework.automation;

import com.wso2.openbanking.test.framework.util.ConfigParser;
import com.wso2.openbanking.test.framework.util.TestConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

/**
 * Basic Authentication Automation.
 */
public class AUBasicAuthAutomationStep implements BrowserAutomationStep {

    public String authorizeUrl;
    private static final Log log = LogFactory.getLog(AUBasicAuthAutomationStep.class);

    /**
     * Initialize Basic Auth Flow.
     *
     * @param authorizeUrl authorise URL.
     */
    public AUBasicAuthAutomationStep(String authorizeUrl) {

        this.authorizeUrl = authorizeUrl;
    }

    /**
     * Execute automation using driver.
     *
     * @param webDriver driver object.
     * @param context   automation context.
     */
    @SuppressWarnings("checkstyle:WhitespaceAround")
    @Override
    public void execute(RemoteWebDriver webDriver, BrowserAutomation.AutomationContext context) {

        webDriver.navigate().to(authorizeUrl);
        WebElement username;

        if (TestConstants.SOLUTION_VERSION_300.equals(ConfigParser.getInstance().getSolutionVersion())) {
            username = webDriver.findElement(By.xpath(TestConstants.USERNAME_FIELD_XPATH_AU_200));

        } else {
            username = webDriver.findElement(By.id(TestConstants.USERNAME_FIELD_ID));
        }

        username.clear();
        username.sendKeys(ConfigParser.getInstance().getPsu());

        if (!TestConstants.SOLUTION_VERSION_300.equals(ConfigParser.getInstance().getSolutionVersion())) {
            WebElement password = webDriver.findElement(By.id(TestConstants.PASSWORD_FIELD_ID));
            password.clear();
            password.sendKeys(ConfigParser.getInstance().getPsuPassword());
        }

        if (TestConstants.SOLUTION_VERSION_300.equals(ConfigParser.getInstance().getSolutionVersion())) {
                webDriver.findElement(By.xpath(TestConstants.AUTH_SIGNIN_XPATH_AU_200)).click();

        } else {
            webDriver.findElement(By.xpath(TestConstants.AUTH_SIGNIN_XPATH)).submit();
        }

        webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        //Second Factor Authentication Step
        try {
            if (webDriver.findElement(By.xpath(TestConstants.LBL_SMSOTP_AUTHENTICATOR)).isDisplayed()) {

                String otpCode = TestConstants.OTP_CODE;
                webDriver.findElement(By.id(TestConstants.TXT_OTP_CODE)).sendKeys(otpCode);
                webDriver.findElement(By.xpath(TestConstants.BTN_AUTHENTICATE)).click();

                webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

            }
        } catch (NoSuchElementException e) {
                log.info("Second Factor Authentication Step is not configured");
        }
    }
}
