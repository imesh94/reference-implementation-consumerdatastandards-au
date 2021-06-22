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

package com.wso2.openbanking.toolkit.cds.integration.tests.cx.consentmgt

import com.wso2.openbanking.test.framework.automation.BasicWithdrawalAutomationStep
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import io.restassured.path.json.JsonPath
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.WebDriverWait
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.openqa.selenium.support.ui.ExpectedConditions

/**
 * Withdrawal Flow Tests.
 */
class WithdrawalFlowValidationTest extends AbstractAUTests {

    @BeforeClass(alwaysRun = true)
    void "Get User Access Token"() {
        doConsentAuthorisation()
        generateUserAccessToken()
    }

    public String consentedAccount
    public String consentedAccountDetails

    @Test
    void "TC0901001_Verify the Account Consent List"() {

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)

                .addStep(new BasicWithdrawalAutomationStep(AUConstants.CONSENT_MANAGER_URL))
                .addStep { driver, context ->

                    //Verify consent status display
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_AUTHORISED_STATUS)).getText(),
                            AUConstants.LBL_AUTHORISED)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_REVOKED_STATUS)).getText(),
                            AUConstants.LBL_REVOKED)

                    //Click on consent account
                    WebElement accElement = driver.findElement(By.xpath(AUConstants.LBL_CONSENT_ACCOUNT_1))
                    consentedAccount = accElement.getAttribute("class")
                    accElement.click()

                    //Click on consent account details
                    accElement = driver.findElement(By.xpath(AUConstants.LBL_CONSENT_ACCOUNT_DETAILS_LIST_ITEM_1))
                    consentedAccountDetails = accElement.getAttribute("value")
                    accElement.click()

                    //Verify Stop Sharing button is visible
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_STOP_SHARING_BUTTON_OF_ACCOUNT_1)
                    ).getText(), AUConstants.LBL_STOP_SHARING)
                }
                .execute()
    }

    @Test
    void "TC0901002_Verify old consent status"() {

        //consent status display as Authorised
        def automation2 = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)

                .addStep(new BasicWithdrawalAutomationStep(AUConstants.CONSENT_MANAGER_URL))
                .addStep { driver, context ->

                    //Click on consent account
                    WebElement accElement = driver.findElement(By.xpath(AUConstants.LBL_CONSENT_ACCOUNT_1))
                    consentedAccount = accElement.getAttribute("class")
                    accElement.click()

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_AUTHORISED_STATUS)).getText(),
                            AUConstants.LBL_AUTHORISED)
                }
                .execute()

        //Again authorised consent
        doConsentAuthorisation()

        //Previous consent in Revoked status
        def automation4 = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)

                .addStep(new BasicWithdrawalAutomationStep(AUConstants.CONSENT_MANAGER_URL))
                .addStep { driver, context ->

                    //Click on consent account
                    WebElement accElement = driver.findElement(By.xpath(AUConstants.LBL_CONSENT_ACCOUNT_1))
                    consentedAccount = accElement.getAttribute("class")
                    accElement.click()

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_AUTHORISED_STATUS)).getText(),
                            AUConstants.LBL_AUTHORISED)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_REVOKED_STATUS)).getText(),
                            AUConstants.LBL_REVOKED)
                }
                .execute()
    }

    @Test
    void "TC0901003_Revoke Account Consent"() {

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)

                .addStep(new BasicWithdrawalAutomationStep(AUConstants.CONSENT_MANAGER_URL))
                .addStep { driver, context ->

                    // explicit wait - to wait for the button to be click-able
                    WebDriverWait wait = new WebDriverWait(driver, 30)
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AUConstants.LBL_CONSENT_ACCOUNT_1)))

                    //Click on consent account
                    WebElement accElement = driver.findElement(By.xpath(AUConstants.LBL_CONSENT_ACCOUNT_1))
                    consentedAccount = accElement.getAttribute("class")
                    accElement.click()

                    //Click on Stop sharing button
                    WebDriverWait wait1 = new WebDriverWait(driver, 30)
                    wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AUConstants.LBL_STOP_SHARING_BUTTON_OF_ACCOUNT_1)))
                    driver.findElement(By.xpath(AUConstants.LBL_STOP_SHARING_BUTTON_OF_ACCOUNT_1)).click()

                    //Enter reason to revoke consent
                    WebElement textbox = driver.findElement(By.xpath(AUConstants.LBL_TEXT_AREA_OF_REVOKE_REASON))
                    textbox.sendKeys("consent no longer required")

                    //Click on Revoke button
                    driver.findElement(By.xpath(AUConstants.LBL_REVOKE_BUTTON)).click()
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_REVOKED_STATUS)).getText(), AUConstants.LBL_REVOKED)

                }
                .execute()
    }

    @Test
    void "TC0901004_Verify the status of the access token of a Revoked Consent"() {

        //Authorised consent
        doConsentAuthorisation()

        //Revoke consent
        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)

                .addStep(new BasicWithdrawalAutomationStep(AUConstants.CONSENT_MANAGER_URL))
                .addStep { driver, context ->

                    // explicit wait - to wait for the button to be click-able
                    WebDriverWait wait = new WebDriverWait(driver, 30)
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AUConstants.LBL_CONSENT_ACCOUNT_1)))

                    //Click on consent account
                    WebElement accElement = driver.findElement(By.xpath(AUConstants.LBL_CONSENT_ACCOUNT_1))
                    consentedAccount = accElement.getAttribute("class")
                    accElement.click()

                    //Click on Stop sharing button
                    WebDriverWait wait1 = new WebDriverWait(driver, 30)
                    wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AUConstants.LBL_STOP_SHARING_BUTTON_OF_ACCOUNT_1)))
                    driver.findElement(By.xpath(AUConstants.LBL_STOP_SHARING_BUTTON_OF_ACCOUNT_1)).click()

                    //Enter reason to revoke consent
                    WebElement textbox = driver.findElement(By.xpath(AUConstants.LBL_TEXT_AREA_OF_REVOKE_REASON))
                    textbox.sendKeys("consent no longer required")

                    //Click on Revoke button
                    driver.findElement(By.xpath(AUConstants.LBL_REVOKE_BUTTON)).click()
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_REVOKED_STATUS)).getText(), AUConstants.LBL_REVOKED)

                    //Verify the status of the access token of Revoked Consent
                    def response =given().relaxedHTTPSValidation().
                             auth()
                            .preemptive()
                            .basic(AUConstants.USERNAME, AUConstants.PASSWORD)
                            .header(AUConstants.CONTENT_TYPE,AUConstants.CONTENT)
                            .param(AUConstants.ACCESS_TOKEN,userAccessToken)
                            .when()
                            .post(AUConstants.OAUTH2_INTROSPECT_URL)
                            .then().
                            statusCode(200).extract()

                    JsonPath jsonPathEvaluator = response.jsonPath()
                    boolean active = jsonPathEvaluator.get("active")
                    Assert.assertEquals(active,false,"Token not active")
                }
                .execute()
    }

}
