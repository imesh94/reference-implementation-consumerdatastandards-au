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

package com.wso2.openbanking.toolkit.cds.integration.tests.cx.authflow

import com.wso2.openbanking.test.framework.automation.AUBasicAuthAutomationStep
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUAuthorisationBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import org.openqa.selenium.By
import org.testng.Assert
import org.testng.annotations.Test

/**
 * Authorisation Flow CX guidelines Tests.
 */
class AuthorisationFlowUIValidationTest {


    @Test
    void "TC0203003_Verify the permissions of a consent with common:customer.basic:read scope"() {
        List<AUConstants.SCOPES> scopes = [AUConstants.SCOPES.BANK_CUSTOMER_BASIC_READ]

      AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )
        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->

                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()

                    def lbl_permission_header = driver.findElement(By.xpath(AUConstants.LBL_PERMISSION_HEADER_ORG_PROFILE))
                    Assert.assertTrue(lbl_permission_header.isDisplayed())
                    lbl_permission_header.click()
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_1)).getText(),
                            AUConstants.LBL_AGENT_NAME_AND_ROLE)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_2)).getText(),
                            AUConstants.LBL_ORGANISATION_NAME)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_3)).getText(),
                            AUConstants.LBL_ORGANISATION_NUMBER)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_4)).getText(),
                            AUConstants.LBL_CHARITY_STATUS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_5)).getText(),
                            AUConstants.LBL_ESTABLISHMENT_DATE)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_6)).getText(),
                            AUConstants.LBL_INDUSTRY)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_7)).getText(),
                            AUConstants.LBL_ORGANISATION_TYPE)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_8)).getText(),
                            AUConstants.LBL_COUNTRY_OF_REGISTRATION)
                    // Submit consent
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                }
                .execute()
    }


    @Test
    void "TC0203004_Verify the permissions of a consent with common:customer.detail:read scope"() {
        List<AUConstants.SCOPES> scopes = [AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ]

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )
        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->

                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()

                    def lbl_permission_header = driver.findElement(By.xpath(AUConstants.LBL_PERMISSION_HEADER_ORG_PROFILE))
                    Assert.assertTrue(lbl_permission_header.isDisplayed())
                    lbl_permission_header.click()
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_1)).getText(),
                            AUConstants.LBL_AGENT_NAME_AND_ROLE)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_2)).getText(),
                            AUConstants.LBL_ORGANISATION_NAME)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_3)).getText(),
                            AUConstants.LBL_ORGANISATION_NUMBER)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_4)).getText(),
                            AUConstants.LBL_CHARITY_STATUS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_5)).getText(),
                            AUConstants.LBL_ESTABLISHMENT_DATE)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_6)).getText(),
                            AUConstants.LBL_INDUSTRY)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_7)).getText(),
                            AUConstants.LBL_ORGANISATION_TYPE)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_8)).getText(),
                            AUConstants.LBL_COUNTRY_OF_REGISTRATION)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_9)).getText(),
                            AUConstants.LBL_ORGANISATION_ADDRESS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_10)).getText(),
                            AUConstants.LBL_MAIL_ADDRESS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_11)).getText(),
                            AUConstants.LBL_PHONE_NUMBER)
                    // Submit consent
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                }
                .execute()
    }

    @Test
    void "TC0203005_Verify the permissions of a consent with bank:accounts.basic:read scope"() {

        List<AUConstants.SCOPES> scopes = [AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ]

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )
        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->

                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()

                    def lbl_permission_header = driver.findElement(By.xpath(AUConstants.LBL_PERMISSION_HEADER_ACC_NAME))
                    Assert.assertTrue(lbl_permission_header.isDisplayed())
                    lbl_permission_header.click()
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_1)).getText(),
                            AUConstants.LBL_NAME_OF_ACCOUNT)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_2)).getText(),
                            AUConstants.LBL_TYPE_OF_ACCOUNT)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_3)).getText(),
                            AUConstants.LBL_ACCOUNT_BALANCE)
                    // Submit consent
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                }
                .execute()
    }

    @Test
    void "TC0203006_Verify the permissions of a consent with bank:accounts.detail:read scope"() {

        List<AUConstants.SCOPES> scopes = [AUConstants.SCOPES.BANK_ACCOUNT_DETAIL_READ]

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->

                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()

                    def lbl_permission_header = driver.findElement(By.xpath(AUConstants.LBL_PERMISSION_HEADER_ACC_BAL))
                    Assert.assertTrue(lbl_permission_header.isDisplayed())
                    lbl_permission_header.click()
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_1)).getText(),
                            AUConstants.LBL_NAME_OF_ACCOUNT)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_2)).getText(),
                            AUConstants.LBL_TYPE_OF_ACCOUNT)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_3)).getText(),
                            AUConstants.LBL_ACCOUNT_BALANCE)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_4)).getText(),
                            AUConstants.LBL_ACCOUNT_NUMBER)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_5)).getText(),
                            AUConstants.LBL_INTEREST_RATES)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_6)).getText(),
                            AUConstants.LBL_FEES)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_7)).getText(),
                            AUConstants.LBL_DISCOUNTS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_8)).getText(),
                            AUConstants.LBL_ACCOUNT_TERMS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_9)).getText(),
                            AUConstants.LBL_ACCOUNT_MAIL_ADDRESS)
                    // Submit consent
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                }
                .execute()
    }

    @Test
    void "TC0203007_Verify the permissions of a consent with bank:transactions:read scope"() {

        List<AUConstants.SCOPES> scopes = [AUConstants.SCOPES.BANK_TRANSACTION_READ]

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->

                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()

                    def lbl_permission_header = driver.findElement(By.xpath(AUConstants.LBL_PERMISSION_HEADER_TRA_DETAILS))
                    Assert.assertTrue(lbl_permission_header.isDisplayed())
                    lbl_permission_header.click()
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_1)).getText(),
                            AUConstants.LBL_INCOMING_AND_OUTGOING_TRANSACTIONS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_2)).getText(),
                            AUConstants.LBL_AMOUNTS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_3)).getText(),
                            AUConstants.LBL_DATES)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_4)).getText(),
                            AUConstants.LBL_DESCRIPTION_OF_TRANSACTION)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_5)).getText(),
                            AUConstants.LBL_NAME_OF_MONEY_RECIPIENT)
                    // Submit consent
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                }
                .execute()
    }

    @Test
    void "TC0203008_Verify the permissions of a consent with bank:regular_payments:read scope"() {

        List<AUConstants.SCOPES> scopes = [AUConstants.SCOPES.BANK_REGULAR_PAYMENTS_READ]

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->

                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()

                    def lbl_permission_header = driver.findElement(By.xpath(AUConstants.LBL_PERMISSION_HEADER_PAYMENT_READ))
                    Assert.assertTrue(lbl_permission_header.isDisplayed())
                    lbl_permission_header.click()
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_1)).getText(),
                            AUConstants.LBL_DIRECT_DEBITS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_2)).getText(),
                            AUConstants.LBL_SCHEDULE_PAYMENTS)
                    // Submit consent
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                }
                .execute()
    }

    @Test
    void "TC0203009_Verify the permissions of a consent with bank:payees:read scope"() {

        List<AUConstants.SCOPES> scopes = [AUConstants.SCOPES.BANK_PAYEES_READ]

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->

                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()


                    def lbl_permission_header = driver.findElement(By.xpath(AUConstants.LBL_PERMISSION_HEADER_PAYEES))
                    Assert.assertTrue(lbl_permission_header.isDisplayed())
                    lbl_permission_header.click()
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUConstants.LBL_PERMISSION_LIST_ITEM_1)).getText(),
                            AUConstants.LBL_DETAILS_OF_SAVED_ACCOUNTS)
                    // Submit consent
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                }
                .execute()
    }
}
