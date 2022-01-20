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

/**
 * CCPortal Withdrawal Flow UI Tests.
 */
class WithdrawalFlowUIValidationTest extends AbstractAUTests {

    @BeforeClass(alwaysRun = true)
    void "Get User Access Token"() {
        doConsentAuthorisation()
        generateUserAccessToken()
    }

    //TODO:Need to change Created on phrase to Consent granted phrase.Need to enable Test true after issue fix.
    // git issue:https://github.com/wso2-enterprise/financial-open-banking/issues/5610
    @Test(enabled = true)
    void "TC0303001_Validate DH use the term ‘consent’ instead of ‘authorisation’"() {

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)

                .addStep(new CCProtalWithdrawalAutomationStep(AUConstants.CCPORTAL_URL))
                .addStep { driver, context ->

                    // explicit wait - to wait for the button to be click-able
                    WebDriverWait wait = new WebDriverWait(driver, 10)
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AUConstants.LBL_SEARCH_BUTTON_PATH)))

                    //Click on Search button
                    WebElement searchBtn = driver.findElement(By.xpath(AUConstants.LBL_SEARCH_BUTTON_PATH))
                    JavascriptExecutor executor = (JavascriptExecutor)driver
                    executor.executeScript("arguments[0].click();",searchBtn)

                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_CONSENT0_CREATED_DATE)).getText(),
                            AUConstants.LBL_CONSENT_GRANTED + AUConstants.DATE)
                }
                .execute()
    }

    //TODO:Need to enable Test true after issue fix.
    // git issue:https://github.com/wso2-enterprise/financial-open-banking/issues/5610
    @Test(enabled = true)
    void "TC0303002_Validate consumer dashboard show details of the CDR data that has been authorised to be disclosed"() {

       def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new CCProtalWithdrawalAutomationStep(AUConstants.CCPORTAL_URL))
                .addStep { driver, context ->

                    // explicit wait - to wait for the button to be click-able
                    WebDriverWait wait = new WebDriverWait(driver, 10)
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AUConstants.LBL_SEARCH_BUTTON_PATH)))

                    //Click on Search button
                    WebElement searchBtn = driver.findElement(By.xpath(AUConstants.LBL_SEARCH_BUTTON_PATH))
                    JavascriptExecutor executor = (JavascriptExecutor)driver
                    executor.executeScript("arguments[0].click();",searchBtn)

                    //Click on consent account
                    driver.findElement(By.xpath(AUConstants.LBL_CONSENT0_PATH)).click()

                    //TODO:Permission list headings need to change
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_ACCOUNT_BASIC_READ_PATH)).getText(),
                           AUConstants.BANK_ACCOUNT_BASIC_READ)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_ACCOUNT_DETAIL_READ_PATH)).getText(),
                            AUConstants.BANK_ACCOUNT_DETAIL_READ)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_PAYEES_READ_PATH)).getText(),
                            AUConstants.BANK_PAYEES_READ)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_TRANSACTION_READ_PATH)).getText(),
                            AUConstants.BANK_TRANSACTION_READ)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_CUSTOMER_DETAIL_READ_PATH)).getText(),
                            AUConstants.BANK_CUSTOMER_DETAIL_READ )
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_CUSTOMER_BASIC_READ_PATH)).getText(),
                            AUConstants.BANK_CUSTOMER_BASIC_READ)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_REGULAR_PAYMENTS_READ_PATH)).getText(),
                            AUConstants.BANK_REGULAR_PAYMENTS_READ)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_CUSTOMER_BASIC_READ_INDIVIDUAL_PATH)).getText(),
                            AUConstants.BANK_CUSTOMER_BASIC_DETAIL_INDIVIDUAL)
                    Assert.assertEquals(driver.findElement(By.xpath(AUConstants.LBL_BANK_CUSTOMER_DETAIL_READ_INDIVIDUAL_PATH)).getText(),
                            AUConstants.BANK_CUSTOMER_BASIC_READ_INDIVIDUAL)

                    //Click on Account name type and balance details
                    //TODO:Need to verify Name of account,Type of account,Account balance(detail read include basic read permission list also)
                    driver.findElement(By.xpath(AUConstants.LBL_BANK_ACCOUNT_BASIC_READ_PATH)).click()
                    List<WebElement> listofElementsOfBankAccountBasic = driver.findElements(By.xpath(AUConstants.
                            LBL_CCPORTAL_ACCOUNT_NAME_TYPE_TAB))

                    Assert.assertEquals(listofElementsOfBankAccountBasic.get(0).getText(), AUConstants.LBL_NAME_OF_ACCOUNT)
                    Assert.assertEquals(listofElementsOfBankAccountBasic.get(1).getText(), AUConstants.LBL_TYPE_OF_ACCOUNT)
                    Assert.assertEquals(listofElementsOfBankAccountBasic.get(2).getText(), AUConstants.LBL_ACCOUNT_BALANCE)

                   //Click on Account numbers and features details
                    driver.findElement(By.xpath(AUConstants.LBL_BANK_ACCOUNT_DETAIL_READ_PATH)).click()
                    List<WebElement> listofElementsOfBankAccountDetail = driver.findElements(By.xpath(AUConstants.
                            LBL_CCPORTAL_ACCOUNT_NUMBER_AND_FEATURES_TAB))

                    Assert.assertEquals(listofElementsOfBankAccountDetail.get(0).getText(), AUConstants.LBL_ACCOUNT_NUMBER)
                    Assert.assertEquals(listofElementsOfBankAccountDetail.get(1).getText(), AUConstants.LBL_ACCOUNT_MAIL_ADDRESS)
                    Assert.assertEquals(listofElementsOfBankAccountDetail.get(2).getText(), AUConstants.LBL_INTEREST_RATES)
                    Assert.assertEquals(listofElementsOfBankAccountDetail.get(3).getText(), AUConstants.LBL_FEES)
                    Assert.assertEquals(listofElementsOfBankAccountDetail.get(4).getText(), AUConstants.LBL_DISCOUNTS)
                    Assert.assertEquals(listofElementsOfBankAccountDetail.get(5).getText(), AUConstants.LBL_ACCOUNT_TERMS)

                    //Click on Saved payees details
                    //TODO:Permission list details need to change
                    driver.findElement(By.xpath(AUConstants.LBL_BANK_PAYEES_READ_PATH)).click()
                    List<WebElement> listofElementsOfBankPayees = driver.findElements(By.xpath(AUConstants.LBL_CCPORTAL_SAVED_PAYEES_TAB))

                    Assert.assertEquals(listofElementsOfBankPayees.get(0).getText(), AUConstants.LBL_DETAILS_OF_SAVED_ACCOUNTS )

                    //Click on Transaction details
                    //TODO:Permission list details need to change
                    driver.findElement(By.xpath(AUConstants.LBL_BANK_TRANSACTION_READ_PATH)).click()
                    List<WebElement> listofElementsOfBankTransaction = driver.findElements(By.xpath(AUConstants.
                            LBL_CCPORTAL_TRANSACTIONS_DETAIL_TAB))

                    Assert.assertEquals(listofElementsOfBankTransaction.get(0).getText(), AUConstants.LBL_INCOMING_AND_OUTGOING_TRANSACTIONS)
                    Assert.assertEquals(listofElementsOfBankTransaction.get(1).getText(), AUConstants.LBL_AMOUNTS)
                    Assert.assertEquals(listofElementsOfBankTransaction.get(2).getText(), AUConstants.LBL_DATES)
                    Assert.assertEquals(listofElementsOfBankTransaction.get(3).getText(), AUConstants.LBL_DESCRIPTION_OF_TRANSACTION)
                    Assert.assertEquals(listofElementsOfBankTransaction.get(4).getText(), AUConstants.LBL_NAME_OF_MONEY_RECIPIENT)

                    //Click on Contact details
                    //TODO:Need to verify Name,Occupation(detail read include basic read permission list also)
                    driver.findElement(By.xpath(AUConstants.LBL_BANK_CUSTOMER_BASIC_READ_INDIVIDUAL_PATH)).click()
                    List<WebElement> listofElementsOfBankCustomerIndividual = driver.findElements(By.xpath(AUConstants.
                            LBL_CCPORTAL_CONTACT_DETAILS_TAB))

                    Assert.assertEquals(listofElementsOfBankCustomerIndividual.get(0).getText(), AUConstants.LBL_PHONE)
                    Assert.assertEquals(listofElementsOfBankCustomerIndividual.get(1).getText(), AUConstants.LBL_EMAIL_ADDRESS)
                    Assert.assertEquals(listofElementsOfBankCustomerIndividual.get(2).getText(), AUConstants.LBL_MAIL_ADDRESS)
                    Assert.assertEquals(listofElementsOfBankCustomerIndividual.get(3).getText(), AUConstants.LBL_RESIDENTIAL_ADDRESS)

                    //Click on Organisation contact details
                    //TODO:Need to verify Agent name and role,Organisation name,Organisation numbers (ABN or ACN)†,Charity status,
                    // Establishment date,Industry,Organisation type,Country of registration(detail read include basic read permission list also)
                    driver.findElement(By.xpath(AUConstants.LBL_BANK_CUSTOMER_DETAIL_READ_PATH)).click()
                    List<WebElement> listofElementsOfBankCustomerDetail = driver.findElements(By.xpath(AUConstants.
                            LBL_CCPORTAL_ORGANISATION_CONTACT_DETAILS_TAB))

                    Assert.assertEquals(listofElementsOfBankCustomerDetail.get(0).getText(), AUConstants.LBL_ORGANISATION_ADDRESS)
                    Assert.assertEquals(listofElementsOfBankCustomerDetail.get(1).getText(), AUConstants.LBL_MAIL_ADDRESS)
                    Assert.assertEquals(listofElementsOfBankCustomerDetail.get(2).getText(), AUConstants.LBL_PHONE_NUMBER)

                    //Click on Name and occupation details
                    driver.findElement(By.xpath(AUConstants.LBL_BANK_CUSTOMER_DETAIL_READ_INDIVIDUAL_PATH)).click()
                    List<WebElement> listofElementsOfBankCustomerDetailIndividual = driver.findElements(By.xpath(AUConstants.
                            LBL_CCPORTAL_NAME_AND_OCCUPATION_TAB))

                    Assert.assertEquals(listofElementsOfBankCustomerDetailIndividual.get(0).getText(), AUConstants.LBL_NAME)
                    Assert.assertEquals(listofElementsOfBankCustomerDetailIndividual.get(1).getText(), AUConstants.LBL_OCCUPATION)

                    //Click on Organisation profile details
                    //TODO:Permission list details need to change
                    driver.findElement(By.xpath(AUConstants.LBL_BANK_CUSTOMER_BASIC_READ_PATH)).click()
                    List<WebElement> listofElementsOfBankCustomerBasic = driver.findElements(By.xpath(AUConstants.
                            LBL_CCPORTAL_ORGANISATION_PROFILE_TAB))

                    Assert.assertEquals(listofElementsOfBankCustomerBasic.get(0).getText(), AUConstants.LBL_AGENT_NAME_AND_ROLE)
                    Assert.assertEquals(listofElementsOfBankCustomerBasic.get(1).getText(), AUConstants.LBL_ORGANISATION_NAME)
                    Assert.assertEquals(listofElementsOfBankCustomerBasic.get(2).getText(), AUConstants.LBL_ORGANISATION_NUMBER)
                    Assert.assertEquals(listofElementsOfBankCustomerBasic.get(3).getText(), AUConstants.LBL_CHARITY_STATUS)
                    Assert.assertEquals(listofElementsOfBankCustomerBasic.get(4).getText(), AUConstants.LBL_ESTABLISHMENT_DATE)
                    Assert.assertEquals(listofElementsOfBankCustomerBasic.get(5).getText(), AUConstants.LBL_INDUSTRY)
                    Assert.assertEquals(listofElementsOfBankCustomerBasic.get(6).getText(), AUConstants.LBL_ORGANISATION_TYPE)
                    Assert.assertEquals(listofElementsOfBankCustomerBasic.get(7).getText(), AUConstants.LBL_COUNTRY_OF_REGISTRATION)

                    //Click on Direct debits and scheduled payments details
                    //TODO:Permission list details need to change
                    driver.findElement(By.xpath(AUConstants.LBL_BANK_REGULAR_PAYMENTS_READ_PATH)).click()
                    List<WebElement> listofElementsOfBankRegularPayments = driver.findElements(By.xpath(AUConstants.
                            LBL_CCPORTAL_DIRECT_DEBITS_TAB))

                    Assert.assertEquals(listofElementsOfBankRegularPayments.get(0).getText(), AUConstants.LBL_DIRECT_DEBITS)
                    Assert.assertEquals(listofElementsOfBankRegularPayments.get(1).getText(), AUConstants.LBL_SCHEDULE_PAYMENTS)
                }
                .execute()
    }
}
