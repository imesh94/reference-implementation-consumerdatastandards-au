/*
  * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
  *
  * This software is the property of WSO2 LLC. and its suppliers, if any.
  * Dissemination of any information or reproduction of any material contained
  * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
  * You may not alter or remove any copyright or other notice from copies of this content.
  */

package com.wso2.cds.keymanager.test.secondaryUser

import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.automation.consent.AUBasicAuthAutomationStep
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.AUPageObjects
import com.wso2.cds.test.framework.request_builder.AURegistrationRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import com.wso2.openbanking.test.framework.automation.AutomationMethod
import io.restassured.response.Response
import org.openqa.selenium.By
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Secondary User Instructions Test.
 */
class SecondaryUserInstructionsAuthorisationTest extends AUTest {

    def shareableElements

    @BeforeClass(alwaysRun = true)
    void "Provide User Permissions"() {

        auConfiguration.setPsuNumber(1)
        clientId = auConfiguration.getAppInfoClientID()
        //Get Sharable Account List and Secondary User with Authorize Permission
        shareableElements = AUTestUtil.getSecondaryUserDetails(getSharableBankAccounts())

        String accountID =  shareableElements[AUConstants.PARAM_ACCOUNT_ID]
        String userId = auConfiguration.getUserPSUName()

        def updateResponse = updateSecondaryUserInstructionPermission(accountID, userId, AUConstants.ACTIVE)
        Assert.assertEquals(updateResponse.statusCode(), AUConstants.OK)
    }

    @Test(groups = "SmokeTest")
    void "CDS-411_Verify Secondary accounts selection Authorization Flow"() {

        //Send Push Authorisation Request
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //Select Secondary Account during authorisation
        doSecondaryAccountSelection(scopes, requestUri.toURI())
        Assert.assertNotNull(authorisationCode)

        //Get User Access Token
        generateUserAccessToken()

        //Account Retrieval
        Response accountResponse = doAccountRetrieval(userAccessToken)
        Assert.assertEquals(accountResponse.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(AUTestUtil.parseResponseBody(accountResponse,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[0]"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(accountResponse,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[1]"))
    }

    @Test
    void "CDS-412_Verify consent authorization no account scopes in the consent"() {

        List<AUAccountScope> no_account_scopes = [

                AUAccountScope.BANK_PAYEES_READ
        ]

        response = auAuthorisationBuilder.doPushAuthorisationRequest(no_account_scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        doSecondaryAccountSelection(no_account_scopes, requestUri.toURI(), clientId)
        Assert.assertNotNull(authorisationCode)
    }

    @Test
    void "CDS-413_Verify the account list should display the accounts based criteria"() {

        // Data recipient name extracted from DCR GET call
        accessToken = getApplicationAccessToken(auConfiguration.getAppInfoClientID())
        Assert.assertNotNull(accessToken)

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .get(AUConstants.DCR_REGISTRATION_ENDPOINT + auConfiguration.getAppInfoClientID())

        String adrName = registrationResponse.jsonPath().get("org_name") + ", " + registrationResponse.jsonPath().get("client_name")

        //Send Authorisation Request via PAR
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), clientId)
                .toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    Assert.assertTrue(authWebDriver.getAttributeText(AUPageObjects.ADR_NAME_HEADER_XPATH).contains(adrName))

                    //Verify Account List
                    Assert.assertTrue(authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath()))
                    Assert.assertTrue(authWebDriver.isElementDisplayed(AUTestUtil.getAltSingleAccountXPath()))
                    Assert.assertTrue(authWebDriver.isElementDisplayed(AUTestUtil.getSecondaryAccount1XPath()))
                    Assert.assertTrue(authWebDriver.isElementDisplayed(AUTestUtil.getSecondaryAccount2XPath()))

                    //Select Secondary Account
                    selectSecondaryAccount(authWebDriver, false)

                    //Click Submit/Next Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_SUBMIT_XPATH)

                    //Click Confirm Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)
                }
                .execute()

        authorisationCode = AUTestUtil.getHybridCodeFromUrl(automation.currentUrl.get())
    }

    @Test
    void "CDS-416_Verify displaying secondary tag along with the account number of the secondary user account"() {

        //Send Authorisation Request via PAR
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), clientId)
                .toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Verify Account List display tag along with the account number of the secondary user account
                    Assert.assertTrue(authWebDriver.getAttributeText(AUPageObjects.LBL_SECONDARY_ACCOUNT_1)
                            .contains("secondary"))
                    Assert.assertTrue(authWebDriver.getAttributeText(AUPageObjects.LBL_SECONDARY_ACCOUNT_2)
                            .contains("secondary"))
                }
                .execute()
    }

    @Test
    void "CDS-417_Verify displaying secondary_joint tag along with the account number of the secondary user joint account"() {

        //Send Authorisation Request via PAR
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), clientId)
                .toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Verify Account List display both secondary and joint tags along with the account number
                    Assert.assertTrue(authWebDriver.getAttributeText(AUPageObjects.LBL_SECONDARY_JOINT_ACCOUNT_1)
                            .contains("secondary_joint"))
                }
                .execute()
    }

    //TODO: Issue: https://github.com/wso2-enterprise/financial-open-banking/issues/8291
    @Test
    void "CDS-547_Verify the account selection page when there are no unavailable accounts"() {

        //Send Authorisation Request via PAR
        authorisationCode = null
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), clientId)
                .toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->

                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Verify The page does not contain unavailable accounts
                    Assert.assertFalse(authWebDriver.isElementDisplayed(AUPageObjects.LBL_ACCOUNTS_UNAVAILABLE_TO_SHARE))
                }
                .addStep(getWaitForRedirectAutomationStep())
                .execute()
    }

    @Test
    void "CDS-546_Verify selecting all secondary user accounts in authorisation"() {

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //Select Secondary Account during authorisation
        doSecondaryAccountSelection(scopes, requestUri.toURI(), auConfiguration.getAppInfoClientID(), true)
        Assert.assertNotNull(authorisationCode)
    }

    @Test
    void "CDS-549_Verify cancellation of authorisation process in account selection page without selecting accounts"() {

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), clientId)
                .toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Click Submit/Next Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CANCEL_XPATH)
                    driver.findElement(By.xpath(AUPageObjects.CONFIRM_CONSENT_DENY_XPATH)).click()
                }
                .execute()

        def authUrl = automation.currentUrl.get()
        Assert.assertTrue(AUTestUtil.getDecodedUrl(authUrl).contains("User skip the consent flow"))
        def stateParam = authUrl.split("state=")[1]
        Assert.assertEquals(auAuthorisationBuilder.state, stateParam)
    }

    @Test
    void "CDS-550_Verify cancellation of authorisation process in account selection page by selecting an accounts"() {

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), clientId)
                .toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Secondary Account
                    selectSecondaryAccount(authWebDriver, false)

                    //Click Submit/Next Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CANCEL_XPATH)
                    driver.findElement(By.xpath(AUPageObjects.CONFIRM_CONSENT_DENY_XPATH)).click()
                }
                .execute()

        def authUrl = automation.currentUrl.get()
        Assert.assertTrue(AUTestUtil.getDecodedUrl(authUrl).contains("User skip the consent flow"))
        def stateParam = authUrl.split("state=")[1]
        Assert.assertEquals(auAuthorisationBuilder.state, stateParam)
    }

    @Test
    void "CDS-627_Verify user nominated for both individual and joint accounts"() {

        //Provide secondary user instruction permissions for joint account
        shareableElements = AUTestUtil.getSecondaryUserDetails(getSharableBankAccounts(), false)
        String accountID =  shareableElements[AUConstants.PARAM_ACCOUNT_ID]
        String userId = auConfiguration.getUserPSUName()

        def updateResponse = updateSecondaryUserInstructionPermission(accountID, userId, AUConstants.ACTIVE)
        Assert.assertEquals(updateResponse.statusCode(), AUConstants.OK)

        //Consent Authorisation
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), clientId)
                .toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Secondary Accounts - Individual and Joint Accounts
                    consentedAccount = authWebDriver.getElementAttribute(AUTestUtil.getSecondaryAccount1XPath(),
                            AUPageObjects.VALUE)
                    authWebDriver.clickButtonXpath(AUTestUtil.getSecondaryAccount1XPath())

                    consentedAccount = authWebDriver.getElementAttribute(AUPageObjects.SECONDARY_JOINT_ACCOUNT,
                            AUPageObjects.VALUE)
                    authWebDriver.clickButtonXpath(AUPageObjects.SECONDARY_JOINT_ACCOUNT)

                    //Click Submit/Next Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_SUBMIT_XPATH)

                    //Click Confirm Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)
                }
                .execute()

        // Get Code From URL
        authorisationCode = AUTestUtil.getHybridCodeFromUrl(automation.currentUrl.get())

        //Get User Access Token
        generateUserAccessToken()

        //Account Retrieval
        Response accountResponse = doAccountRetrieval(userAccessToken)
        Assert.assertEquals(accountResponse.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(AUTestUtil.parseResponseBody(accountResponse,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[0]"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(accountResponse,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[1]"))
    }

    @Test
    void "CDS-628_Verify an account owner of the secondary account has restricted a particular secondary user from sharing accounts"() {

        //Inactive secondary user instruction permissions for joint account
        shareableElements = AUTestUtil.getSecondaryUserDetails(getSharableBankAccounts(), true)
        String accountID =  shareableElements[AUConstants.PARAM_ACCOUNT_ID]
        String userId = auConfiguration.getUserPSUName()

        def updateResponse = updateSecondaryUserInstructionPermission(accountID, userId, AUConstants.INACTIVE)
        Assert.assertEquals(updateResponse.statusCode(), AUConstants.OK)

        //Consent Authorisation
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), clientId)
                .toURI().toString()

        //User unable to select the secondary Account
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Secondary Accounts - Individual and Joint Accounts
                    Assert.assertFalse(authWebDriver.isElementEnabled(AUTestUtil.getSecondaryAccount1XPath()))
                }
                .execute()
    }

    @Test
    void "CDS-629_Verify an account owner of the secondary joint account has restricted a particular secondary user from sharing accounts"() {

        //Inactive secondary user instruction permissions for joint account
        shareableElements = AUTestUtil.getSecondaryUserDetails(getSharableBankAccounts(), false)
        String accountID =  shareableElements[AUConstants.PARAM_ACCOUNT_ID]
        String userId = auConfiguration.getUserPSUName()

        def updateResponse = updateSecondaryUserInstructionPermission(accountID, userId, AUConstants.INACTIVE)
        Assert.assertEquals(updateResponse.statusCode(), AUConstants.OK)

        //Consent Authorisation
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), clientId)
                .toURI().toString()

        //User unable to select the secondary Account
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Secondary Accounts - Individual and Joint Accounts
                    Assert.assertFalse(authWebDriver.isElementEnabled(AUPageObjects.SECONDARY_JOINT_ACCOUNT))
                }
                .execute()
    }

    @Test
    void "CDS-438_Verify notification to indicate the reason for pausing the data sharing from that account"() {

        //Send Authorisation Request via PAR
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), clientId)
                .toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Verify Notification to indicate the reason for pausing the data sharing
                    //TODO: Modify after fixing the issue: https://github.com/wso2-enterprise/financial-open-banking/issues/8291
                    //                    Assert.assertTrue(authWebDriver.getAttributeText(AUPageObjects.LBL_SECONDARY_JOINT_ACCOUNT_1)
                    //                            .contains(""))
                }
                .execute()
    }
}
