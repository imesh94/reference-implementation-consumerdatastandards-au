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

package com.wso2.openbanking.toolkit.cds.integration.tests.cx.ccportal

import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.test.framework.automation.CCProtalWithdrawalAutomationStep
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.openqa.selenium.support.ui.Select

/**
 * CCPortal Withdrawal Flow Tests.
 */
class WithdrawalFlowValidationTest extends AbstractAUTests {

    @BeforeClass(alwaysRun = true)
    void "Get User Access Token"() {
        doConsentAuthorisation()
        generateUserAccessToken()
    }

    @Test
    void "TC1101001_Verify consent can be searched by User ID"() {

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)

                .addStep(new CCProtalWithdrawalAutomationStep(AUConstants.CCPORTAL_URL))
                .addStep { driver, context ->

                    //Enter User ID
                    WebElement textbox = driver.findElement(By.xpath(AUConstants.LBL_USER_ID_PATH))
                    textbox.sendKeys(AUConstants.LBL_USER_ID)

                    // explicit wait - to wait for the button to be click-able
                    WebDriverWait wait = new WebDriverWait(driver, 10)
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AUConstants.LBL_SEARCH_BUTTON_PATH)))

                    //Click on Search button
                    WebElement searchBtn = driver.findElement(By.xpath(AUConstants.LBL_SEARCH_BUTTON_PATH))
                    JavascriptExecutor executor = (JavascriptExecutor)driver
                    executor.executeScript("arguments[0].click();",searchBtn)

                    //Click on consent account
                    driver.findElement(By.xpath(AUConstants.LBL_CONSENT0_PATH)).click()

                    JavascriptExecutor js = (JavascriptExecutor) driver
                    js.executeScript("window.scrollBy(0,500)", "")

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_USER_ID_VERIFY_PATH)).getText(),
                            AUConstants.LBL_USER_ID+AUConstants.LBL_CARBON)
                }
                .execute()
    }

    @Test
    void "TC1101002_Verify consent can be searched by All Application"() {

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)

                .addStep(new CCProtalWithdrawalAutomationStep(AUConstants.CCPORTAL_URL))
                .addStep { driver, context ->

                    // explicit wait - to wait for the tab to be click-able
                    WebDriverWait wait = new WebDriverWait(driver, 10)
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AUConstants.LBL_APPLICATION_MENU_PATH)))

                    //Click on Application tab
                    driver.findElement(By.xpath(AUConstants.LBL_APPLICATION_MENU_PATH)).click()

                    //Select All Application option
                    WebElement testDropDown = driver.findElement(By.id(AUConstants.LBL_APPLICATION_ID))
                    Select dropdown = new Select(testDropDown)
                    dropdown.selectByVisibleText(AUConstants.LBL_ALL_APPLICATION)

                    //Click on Search button
                    driver.findElement(By.xpath(AUConstants.LBL_SEARCH_BUTTON_PATH)).click()

                    JavascriptExecutor js = (JavascriptExecutor) driver
                    js.executeScript("window.scrollBy(0,500)", "")

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_CONSENT0_STATUS_PATH)).getText(),
                            AUConstants.LBL_AUTHORISED)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_CONSENT2_STATUS_PATH)).getText(),
                            AUConstants.LBL_REVOKED)
                }
                .execute()
    }

    @Test
    void "TC1101003_Verify consent can be searched by Authorised status"() {

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)

                .addStep(new CCProtalWithdrawalAutomationStep(AUConstants.CCPORTAL_URL))
                .addStep { driver, context ->

                    //explicit wait - to wait for the tab to be click-able
                    WebDriverWait wait = new WebDriverWait(driver, 10)
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AUConstants.LBL_STATUS_PATH)))

                    //Enter status
                    WebElement textbox = driver.findElement(By.xpath(AUConstants.LBL_STATUS_PATH))
                    textbox.sendKeys(AUConstants.LBL_AUTHORISED)

                    //Click on Search button
                    driver.findElement(By.xpath(AUConstants.LBL_SEARCH_BUTTON_PATH)).click()

                    JavascriptExecutor js = (JavascriptExecutor) driver
                    js.executeScript("window.scrollBy(0,500)", "")

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_CONSENT0_STATUS_PATH)).getText(),
                            AUConstants.LBL_AUTHORISED)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_CONSENT1_STATUS_PATH)).getText(),
                            AUConstants.LBL_AUTHORISED)
                }
                .execute()
    }

    @Test
    void "TC1101004_Verify consent can be searched by Revoked status"() {

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)

                .addStep(new CCProtalWithdrawalAutomationStep(AUConstants.CCPORTAL_URL))
                .addStep { driver, context ->

                    //explicit wait - to wait for the tab to be click-able
                    WebDriverWait wait = new WebDriverWait(driver, 10)
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AUConstants.LBL_STATUS_PATH)))

                    //Enter status
                    WebElement textbox = driver.findElement(By.xpath(AUConstants.LBL_STATUS_PATH))
                    textbox.sendKeys(AUConstants.LBL_REVOKED)

                    //Click on Search button
                    driver.findElement(By.xpath(AUConstants.LBL_SEARCH_BUTTON_PATH)).click()

                    JavascriptExecutor js = (JavascriptExecutor) driver
                    js.executeScript("window.scrollBy(0,500)", "")

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_CONSENT0_STATUS_PATH)).getText(),
                            AUConstants.LBL_REVOKED)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_CONSENT1_STATUS_PATH)).getText(),
                            AUConstants.LBL_REVOKED)
                }
                .execute()
    }

    @Test
    void "TC1101005_Verify consent can be searched by date range"() {

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)

                .addStep(new CCProtalWithdrawalAutomationStep(AUConstants.CCPORTAL_URL))
                .addStep { driver, context ->

                    //explicit wait - to wait for the tab to be click-able
                    WebDriverWait wait = new WebDriverWait(driver, 30)
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AUConstants.LBL_DATE_RANGE_PATH)))

                    //Click on Date range tab
                    driver.findElement(By.xpath(AUConstants.LBL_DATE_RANGE_PATH)).click()

                    //Enter Date range
                    WebElement textbox = driver.findElement(By.xpath(AUConstants.LBL_DATE_RANGE_PATH))
                    textbox.clear()
                    textbox.sendKeys(AUConstants.DATE_FORMAT + "-" + AUConstants.DATE_FORMAT)

                    //Click on apply button
                    driver.findElement(By.xpath(AUConstants.LBL_APPLY_BUTTON_PATH)).click()

                    //Click on Search button
                    driver.findElement(By.xpath(AUConstants.LBL_SEARCH_BUTTON_PATH)).click()

                    JavascriptExecutor js = (JavascriptExecutor) driver
                    js.executeScript("window.scrollBy(0,500)", "")

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_CONSENT0_CREATED_DATE)).getText(),
                            AUConstants.LBL_CREATED_ON+AUConstants.DATE)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_CONSENT1_CREATED_DATE)).getText(),
                            AUConstants.LBL_CREATED_ON+AUConstants.DATE)
                }
                .execute()
    }

    @Test
    void "TC1102001_verify consent can be revoked"() {

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)

                .addStep(new CCProtalWithdrawalAutomationStep(AUConstants.CCPORTAL_URL))
                .addStep { driver, context ->

                    //explicit wait - to wait for the button to be click-able
                    WebDriverWait wait = new WebDriverWait(driver, 30)
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AUConstants.LBL_SEARCH_BUTTON_PATH)))

                    //Click on Search button
                    WebElement searchBtn = driver.findElement(By.xpath(AUConstants.LBL_SEARCH_BUTTON_PATH))
                    JavascriptExecutor executor = (JavascriptExecutor)driver
                    executor.executeScript("arguments[0].click();",searchBtn)

                    //Click on consent account
                    driver.findElement(By.xpath(AUConstants.LBL_CONSENT0_PATH)).click()

                    //Click on Revoke button
                    driver.findElement(By.xpath(AUConstants.LBL_REVOKE_BUTTON_PATH)).click()

                    //Enter reason to revoke consent
                    WebElement textbox = driver.findElement(By.xpath(AUConstants.LBL_TEXT_AREA_OF_REVOKE_REASON))
                    textbox.sendKeys("consent no longer required")

                    //Click on Confirm Revoke button
                    driver.findElement(By.xpath(AUConstants.LBL_CONFIRM_REVOKE_BUTTON_PATH)).click()

                    JavascriptExecutor js2 = (JavascriptExecutor) driver
                    js2.executeScript("window.scrollBy(0,500)", "")

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_CONSENT2_STATUS_PATH)).getText(),
                            AUConstants.LBL_REVOKED)
                }
                .execute()
    }
}
