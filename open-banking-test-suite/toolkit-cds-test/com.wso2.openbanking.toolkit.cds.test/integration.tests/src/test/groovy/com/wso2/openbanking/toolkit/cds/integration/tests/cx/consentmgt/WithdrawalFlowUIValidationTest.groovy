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
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.WebDriverWait
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.openqa.selenium.support.ui.ExpectedConditions

/**
 * Withdrawal Flow UI Tests.
 */
class WithdrawalFlowUIValidationTest extends AbstractAUTests {

    @BeforeClass(alwaysRun = true)
    void "Get User Access Token"() {
        doConsentAuthorisation()
        generateUserAccessToken()
    }

    public String consentedAccount
    public String consentedAccountDetails

    //TODO:Need to change Authorisation on phrase to Consent granted phrase.Need to enable Test true after issue fix.
    // git issue:https://github.com/wso2-enterprise/financial-open-banking/issues/5551
    @Test(enabled = false)
    void "TC0301003_Validate DH use the term consent instead of authorisation"() {
        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)

                .addStep(new BasicWithdrawalAutomationStep(AUConstants.CONSENT_MANAGER_URL))
                .addStep { driver, context ->
                    Assert.assertTrue(driver.findElement(By.xpath(AUConstants.LBL_CONSENT_GRANTED_DATE)).getText().trim()
                            .contains(AUConstants.LBL_CONSENT_GRANTED))
                }
                .execute()
    }

    @Test
    void "TC0301022_Verify consumer dashboard show the date of authorisation was given"() {
        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)

                .addStep(new BasicWithdrawalAutomationStep(AUConstants.CONSENT_MANAGER_URL))
                .addStep { driver, context ->

                    WebDriverWait wait = new WebDriverWait(driver, 30)
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AUConstants.LBL_CONSENT_ACCOUNT_1)))

                    //Click on consent account
                    WebElement accElement = driver.findElement(By.xpath(AUConstants.LBL_CONSENT_ACCOUNT_1))
                    consentedAccount = accElement.getAttribute("class")
                    accElement.click()

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_CONSENT_GAVE)).getText(),
                            AUConstants.LBL_WHEN_YOU_GAVE_CONSENT)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_CONSENT_GAVE_DATE)).getText(),
                            AUConstants.DATE)
                }
                .execute()
    }

    @Test
    void "TC0301023_Verify consumer dashboard show the date of authorisation is scheduled to expire"() {
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

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_CONSENT_EXPIRE)).getText(),
                            AUConstants.LBL_WHEN_YOUR_CONSENT_EXPIRE)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_CONSENT_EXPIRE_DATE)).getText(),
                            AUConstants.CONSENT_EXPIRE_DATE)
                }
                .execute()
    }

    @Test
    void "TC0301024_Verify consumer dashboard show the period in which authorisation was given"() {
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

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_OF_SHARING_PERIOD)).getText(),
                            AUConstants.LBL_SHARING_PERIOD)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_OF_SHARING_PERIOD_DATE)).getText(),
                            AUConstants.DATE + " to " + AUConstants.CONSENT_EXPIRE_DATE)
                }
                .execute()
    }
    //TODO:Need to enable Test true after issue fix.
    // git issue:https://github.com/wso2-enterprise/financial-open-banking/issues/5551
    @Test(enabled = false)
    void "TC0301015_Validate consumer dashboard show details of the CDR data that has been authorised to be disclosed"() {
        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)

                .addStep(new BasicWithdrawalAutomationStep(AUConstants.CONSENT_MANAGER_URL))
                .addStep { driver, context ->

                    JavascriptExecutor js = (JavascriptExecutor) driver
                    js.executeScript("window.scrollBy(200,800)", "")

                    // explicit wait - to wait for the button to be click-able
                    WebDriverWait wait = new WebDriverWait(driver, 30)
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AUConstants.LBL_CONSENT_ACCOUNT_1)))

                    //Click on consent account details
                    //TODO:Permission list headings need to change
                    WebElement accElement = driver.findElement(By.xpath(AUConstants.LBL_CONSENT_ACCOUNT_1))
                    consentedAccount = accElement.getAttribute("class")
                    accElement.click()

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_ACCOUNT_BASIC_READ)).getText(),
                            AUConstants.BANK_ACCOUNT_BASIC_READ)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_ACCOUNT_DETAIL_READ)).getText(),
                            AUConstants.BANK_ACCOUNT_DETAIL_READ)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_PAYEES_READ)).getText(),
                            AUConstants.BANK_PAYEES_READ)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_TRANSACTION_READ)).getText(),
                            AUConstants.BANK_TRANSACTION_READ)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_CUSTOMER_DETAIL_READ)).getText(),
                            AUConstants.BANK_CUSTOMER_DETAIL_READ)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_CUSTOMER_BASIC_READ)).getText(),
                            AUConstants.BANK_CUSTOMER_BASIC_READ)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_REGULAR_PAYMENTS_READ)).getText(),
                            AUConstants.BANK_REGULAR_PAYMENTS_READ)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_CUSTOMER_BASIC_READ_INDIVIDUAL)).getText(),
                            AUConstants.BANK_CUSTOMER_BASIC_DETAIL_INDIVIDUAL)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_CUSTOMER_DETAIL_READ_INDIVIDUAL)).getText(),
                            AUConstants.BANK_CUSTOMER_BASIC_READ_INDIVIDUAL)

                    //Click on Account name type and balance details
                    accElement = driver.findElement(By.xpath(AUConstants.LBL_BANK_ACCOUNT_BASIC_READ))
                    consentedAccountDetails = accElement.getAttribute("value")
                    accElement.click()

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_NAME_OF_ACCOUNT_PATH)).getText(),
                            AUConstants.LBL_NAME_OF_ACCOUNT)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_TYPE_OF_ACCOUNT_PATH)).getText(),
                            AUConstants.LBL_TYPE_OF_ACCOUNT)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_ACCOUNT_BALANCE_PATH)).getText(),
                            AUConstants.LBL_ACCOUNT_BALANCE)

                    //Click on Account numbers and features details
                    //TODO:Need to verify Name of account,Type of account,Account balance(detail read include basic read permission list also)
                    accElement = driver.findElement(By.xpath(AUConstants.LBL_BANK_ACCOUNT_DETAIL_READ))
                    consentedAccountDetails = accElement.getAttribute("value")
                    accElement.click()

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_ACCOUNT_NUMBER_PATH)).getText(),
                            AUConstants.LBL_ACCOUNT_NUMBER)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_ACCOUNT_MAIL_ADDRESS_PATH)).getText(),
                            AUConstants.LBL_ACCOUNT_MAIL_ADDRESS)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_RATES_FEES_DISCOUNT_PATH)).getText(),
                            AUConstants.LBL_RATES_FEES_DISCOUNT)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_ACCOUNT_TERMS_PATH)).getText(),
                            AUConstants.LBL_ACCOUNT_TERMS)

                    //Click on Saved payees details
                    //TODO:Permission list details need to change
                    accElement = driver.findElement(By.xpath(AUConstants.LBL_BANK_PAYEES_READ))
                    consentedAccountDetails = accElement.getAttribute("value")
                    accElement.click()

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_DETAILS_OF_SAVED_ACCOUNTS_PATH)).getText(),
                            AUConstants.LBL_DETAILS_OF_SAVED_ACCOUNTS)

                    //Click on Transaction details
                    //TODO:Permission list details need to change
                    accElement = driver.findElement(By.xpath(AUConstants.LBL_BANK_TRANSACTION_READ))
                    consentedAccountDetails = accElement.getAttribute("value")
                    accElement.click()

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_INCOMING_AND_OUTGOING_TRANSACTIONS_PATH)).getText(),
                            AUConstants.LBL_INCOMING_AND_OUTGOING_TRANSACTIONS)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_AMOUNTS_AND_DATES_PATH)).getText(),
                            AUConstants.LBL_AMOUNTS_AND_DATES)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_DESCRIPTION_OF_TRANSACTION_PATH)).getText(),
                            AUConstants.LBL_DESCRIPTION_OF_TRANSACTION)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_NAME_OF_MONEY_RECIPIENT_PATH)).getText(),
                            AUConstants.LBL_NAME_OF_MONEY_RECIPIENT)

                    //Click on Contact details
                    //TODO:Need to verify Name,Occupation(detail read include basic read permission list also)
                    accElement = driver.findElement(By.xpath(AUConstants.LBL_BANK_CUSTOMER_BASIC_READ_INDIVIDUAL))
                    consentedAccountDetails = accElement.getAttribute("value")
                    accElement.click()

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_PHONE_PATH)).getText(), AUConstants.LBL_PHONE)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_EMAIL_ADDRESS_PATH)).getText(),
                            AUConstants.LBL_EMAIL_ADDRESS)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_MAIL_ADDRESS_PATH)).getText(),
                            AUConstants.LBL_MAIL_ADDRESS)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_RESIDENTIAL_ADDRESS_PATH)).getText(),
                            AUConstants.LBL_RESIDENTIAL_ADDRESS)

                    //Click on Organisation contact details
                    //TODO:Need to verify Agent name and role,Organisation name,Organisation numbers (ABN or ACN)†,Charity status,
                    // Establishment date,Industry,Organisation type,Country of registration(detail read include basic read permission list also)
                    accElement = driver.findElement(By.xpath(AUConstants.LBL_BANK_CUSTOMER_DETAIL_READ))
                    consentedAccountDetails = accElement.getAttribute("value")
                    accElement.click()

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_ORGANISATION_ADDRESS_PATH)).getText(),
                            AUConstants.LBL_ORGANISATION_ADDRESS)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_ORGANISATION_MAIL_ADDRESS_PATH)).getText(),
                            AUConstants.LBL_MAIL_ADDRESS)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_PHONE_NUMBER_PATH)).getText(),
                            AUConstants.LBL_PHONE_NUMBER)

                    //Click on Name and occupation details
                    accElement = driver.findElement(By.xpath(AUConstants.LBL_BANK_CUSTOMER_DETAIL_READ_INDIVIDUAL))
                    consentedAccountDetails = accElement.getAttribute("value")
                    accElement.click()

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_NAME_PATH)).getText(), AUConstants.LBL_NAME)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_OCCUPATION_PATH)).getText(), AUConstants.LBL_OCCUPATION)

                    //Click on Organisation profile details
                    //TODO:Permission list details need to change
                    accElement = driver.findElement(By.xpath(AUConstants.LBL_BANK_CUSTOMER_BASIC_READ))
                    consentedAccountDetails = accElement.getAttribute("value")
                    accElement.click()

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_AGENT_NAME_ROLE_PATH)).getText(),
                            AUConstants.LBL_AGENT_NAME_AND_ROLE)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_ORGANISATION_NAME_PATH)).getText(),
                            AUConstants.LBL_ORGANISATION_NAME)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_ORGANISATION_NUMBER_PATH)).getText(),
                            AUConstants.LBL_ORGANISATION_NUMBER)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_CHARITY_STATUS_PATH)).getText(),
                            AUConstants.LBL_CHARITY_STATUS)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_ESTABLISHMENT_PATH)).getText(),
                            AUConstants.LBL_ESTABLISHMENT_DATE)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_INDUSTRY_PATH)).getText(),
                            AUConstants.LBL_INDUSTRY)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_ORGANISATION_TYPE_PATH)).getText(),
                            AUConstants.LBL_ORGANISATION_TYPE)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_COUNTRY_OF_REGISTRATION_PATH)).getText(),
                            AUConstants.LBL_COUNTRY_OF_REGISTRATION)

                    //Click on Direct debits and scheduled payments details
                    //TODO:Permission list details need to change
                    accElement = driver.findElement(By.xpath(AUConstants.LBL_BANK_REGULAR_PAYMENTS_READ))
                    consentedAccountDetails = accElement.getAttribute("value")
                    accElement.click()

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_DIRECT_DEBITS_PATH)).getText(),
                            AUConstants.LBL_DIRECT_DEBITS)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_SCHEDULED_PAYMENT_PATH)).getText(),
                            AUConstants.LBL_SCHEDULE_PAYMENTS)
                }
                .execute()
    }

}
