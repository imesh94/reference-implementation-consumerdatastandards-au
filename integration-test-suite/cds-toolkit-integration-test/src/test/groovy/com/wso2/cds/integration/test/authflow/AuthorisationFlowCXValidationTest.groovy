/**
 * Copyright (c) 2022, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.authflow

import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.automation.consent.AUBasicAuthAutomationStep
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.AUPageObjects
import com.wso2.cds.test.framework.request_builder.AUAuthorisationBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import org.openqa.selenium.By
import org.testng.Assert
import org.testng.annotations.Test


/**
 * Authorisation Flow CX guidelines Tests.
 */
class AuthorisationFlowCXValidationTest extends AUTest {

    @Test(groups = "SmokeTest")
    void "TC0203003_Verify the permissions of a consent with common customer basic read scope"() {
        List<AUAccountScope> scopes = [AUAccountScope.BANK_CUSTOMER_BASIC_READ]

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder()
        String authoriseUrl = authorisationBuilder.getAuthorizationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION, true).toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->

                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_SUBMIT_XPATH)).click()

                    def lbl_permission_header = driver.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_HEADER_ORG_PROFILE))
                    Assert.assertTrue(lbl_permission_header.isDisplayed())
                    lbl_permission_header.click()
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_1)).getText(),
                            AUConstants.LBL_AGENT_NAME_AND_ROLE)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_2)).getText(),
                            AUConstants.LBL_ORGANISATION_NAME)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_3)).getText(),
                            AUConstants.LBL_ORGANISATION_NUMBER)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_4)).getText(),
                            AUConstants.LBL_CHARITY_STATUS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_5)).getText(),
                            AUConstants.LBL_ESTABLISHMENT_DATE)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_6)).getText(),
                            AUConstants.LBL_INDUSTRY)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_7)).getText(),
                            AUConstants.LBL_ORGANISATION_TYPE)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_8)).getText(),
                            AUConstants.LBL_COUNTRY_OF_REGISTRATION)
                    // Submit consent
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_SUBMIT_XPATH)).click()
                }
                .execute()
    }

    @Test
    void "TC0203004_Verify the permissions of a consent with common customer detail read scope"() {
        List<AUAccountScope> scopes = [AUAccountScope.BANK_CUSTOMER_DETAIL_READ]

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder()
        String authoriseUrl = authorisationBuilder.getAuthorizationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION, true)
                .toURI().toString()
        def automation =getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->

                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_SUBMIT_XPATH)).click()

                    def lbl_permission_header = driver.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_HEADER_ORG_PROFILE))
                    Assert.assertTrue(lbl_permission_header.isDisplayed())
                    lbl_permission_header.click()
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_1)).getText(),
                            AUConstants.LBL_AGENT_NAME_AND_ROLE)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_2)).getText(),
                            AUConstants.LBL_ORGANISATION_NAME)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_3)).getText(),
                            AUConstants.LBL_ORGANISATION_NUMBER)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_4)).getText(),
                            AUConstants.LBL_CHARITY_STATUS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_5)).getText(),
                            AUConstants.LBL_ESTABLISHMENT_DATE)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_6)).getText(),
                            AUConstants.LBL_INDUSTRY)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_7)).getText(),
                            AUConstants.LBL_ORGANISATION_TYPE)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_8)).getText(),
                            AUConstants.LBL_COUNTRY_OF_REGISTRATION)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_9)).getText(),
                            AUConstants.LBL_ORGANISATION_ADDRESS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_10)).getText(),
                            AUConstants.LBL_MAIL_ADDRESS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_11)).getText(),
                            AUConstants.LBL_PHONE_NUMBER)
                    // Submit consent
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_SUBMIT_XPATH)).click()
                }
                .execute()
    }

    @Test
    void "TC0203005_Verify the permissions of a consent with bank accounts basic read scope"() {

        List<AUAccountScope> scopes = [AUAccountScope.BANK_ACCOUNT_BASIC_READ]

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder()
        String authoriseUrl = authorisationBuilder.getAuthorizationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION, true)
                .toURI().toString()
        def automation =getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->

                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_SUBMIT_XPATH)).click()

                    def lbl_permission_header = driver.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_HEADER_ACC_NAME))
                    Assert.assertTrue(lbl_permission_header.isDisplayed())
                    lbl_permission_header.click()
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_1)).getText(),
                            AUConstants.LBL_NAME_OF_ACCOUNT)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_2)).getText(),
                            AUConstants.LBL_TYPE_OF_ACCOUNT)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_3)).getText(),
                            AUConstants.LBL_ACCOUNT_BALANCE)
                    // Submit consent
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_SUBMIT_XPATH)).click()
                }
                .execute()
    }

    @Test
    void "TC0203006_Verify the permissions of a consent with bank accounts detail read scope"() {

        List<AUAccountScope> scopes = [AUAccountScope.BANK_ACCOUNT_DETAIL_READ]

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder()
        String authoriseUrl = authorisationBuilder.getAuthorizationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION, true)
                .toURI().toString()
        def automation =getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->

                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_SUBMIT_XPATH)).click()

                    def lbl_permission_header = driver.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_HEADER_ACC_BAL))
                    Assert.assertTrue(lbl_permission_header.isDisplayed())
                    lbl_permission_header.click()
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_1)).getText(),
                            AUConstants.LBL_NAME_OF_ACCOUNT)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_2)).getText(),
                            AUConstants.LBL_TYPE_OF_ACCOUNT)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_3)).getText(),
                            AUConstants.LBL_ACCOUNT_BALANCE)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_4)).getText(),
                            AUConstants.LBL_ACCOUNT_NUMBER)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_5)).getText(),
                            AUConstants.LBL_INTEREST_RATES)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_6)).getText(),
                            AUConstants.LBL_FEES)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_7)).getText(),
                            AUConstants.LBL_DISCOUNTS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_8)).getText(),
                            AUConstants.LBL_ACCOUNT_TERMS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_9)).getText(),
                            AUConstants.LBL_ACCOUNT_MAIL_ADDRESS)
                    // Submit consent
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_SUBMIT_XPATH)).click()
                }
                .execute()
    }

    @Test
    void "TC0203007_Verify the permissions of a consent with bank transactions read scope"() {

        List<AUAccountScope> scopes = [AUAccountScope.BANK_TRANSACTION_READ]

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder()
        String authoriseUrl = authorisationBuilder.getAuthorizationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION, true)
                .toURI().toString()
        def automation =getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->

                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_SUBMIT_XPATH)).click()

                    def lbl_permission_header = driver.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_HEADER_TRA_DETAILS))
                    Assert.assertTrue(lbl_permission_header.isDisplayed())
                    lbl_permission_header.click()
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_1)).getText(),
                            AUConstants.LBL_INCOMING_AND_OUTGOING_TRANSACTIONS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_2)).getText(),
                            AUConstants.LBL_AMOUNTS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_3)).getText(),
                            AUConstants.LBL_DATES)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_4)).getText(),
                            AUConstants.LBL_DESCRIPTION_OF_TRANSACTION)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_5)).getText(),
                            AUConstants.LBL_NAME_OF_MONEY_RECIPIENT)
                    // Submit consent
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_SUBMIT_XPATH)).click()
                }
                .execute()
    }


    @Test
    void "TC0203008_Verify the permissions of a consent with bank regular_payments read scope"() {

        List<AUAccountScope> scopes = [AUAccountScope.BANK_REGULAR_PAYMENTS_READ]

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder()
        String authoriseUrl = authorisationBuilder.getAuthorizationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION, true)
                .toURI().toString()
        def automation =getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->

                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_SUBMIT_XPATH)).click()

                    def lbl_permission_header = driver.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_HEADER_PAYMENT_READ))
                    Assert.assertTrue(lbl_permission_header.isDisplayed())
                    lbl_permission_header.click()
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_1)).getText(),
                            AUConstants.LBL_DIRECT_DEBITS)
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_2)).getText(),
                            AUConstants.LBL_SCHEDULE_PAYMENTS)
                    // Submit consent
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_SUBMIT_XPATH)).click()
                }
                .execute()
    }

    @Test
    void "TC0203009_Verify the permissions of a consent with bank payees read scope"() {

        List<AUAccountScope> scopes = [AUAccountScope.BANK_PAYEES_READ]

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder()
        String authoriseUrl = authorisationBuilder.getAuthorizationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION, true)
                .toURI().toString()
        def automation =getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->

                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_SUBMIT_XPATH)).click()

                    def lbl_permission_header = driver.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_HEADER_PAYEES))
                    Assert.assertTrue(lbl_permission_header.isDisplayed())
                    lbl_permission_header.click()
                    Assert.assertEquals(lbl_permission_header.findElement(By.xpath(AUPageObjects.LBL_PERMISSION_LIST_ITEM_1)).getText(),
                            AUConstants.LBL_DETAILS_OF_SAVED_ACCOUNTS)
                    // Submit consent
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_SUBMIT_XPATH)).click()
                }
                .execute()
    }

}
