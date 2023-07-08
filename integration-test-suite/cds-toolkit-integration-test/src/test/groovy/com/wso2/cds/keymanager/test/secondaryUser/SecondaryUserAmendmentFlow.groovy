/*
  * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
  *
  * This software is the property of WSO2 LLC. and its suppliers, if any.
  * Dissemination of any information or reproduction of any material contained
  * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
  * You may not alter or remove any copyright or other notice from copies of this content.
  */

package com.wso2.cds.keymanager.test.secondaryUser

import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.automation.consent.AUBasicAuthAutomationStep
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.AUPageObjects
import com.wso2.cds.test.framework.utility.AUTestUtil
import com.wso2.openbanking.test.framework.automation.AutomationMethod
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.nio.charset.Charset

/**
 * Secondary User Amendment Flow.
 */
class SecondaryUserAmendmentFlow extends AUTest {

    def shareableElements, clientHeader
    String accountID, userId

    @BeforeClass(alwaysRun = true)
    void "Provide User Permissions"() {

        auConfiguration.setPsuNumber(1)
        clientId = auConfiguration.getAppInfoClientID()
        clientHeader = "${Base64.encoder.encodeToString(getCDSClient().getBytes(Charset.defaultCharset()))}"

        //Get Sharable Account List and Secondary User with Authorize Permission
        shareableElements = AUTestUtil.getSecondaryUserDetails(getSharableBankAccounts())

        accountID =  shareableElements[AUConstants.PARAM_ACCOUNT_ID]
        userId = auConfiguration.getUserPSUName()

        def updateResponse = updateSecondaryUserInstructionPermission(accountID, userId, AUConstants.ACTIVE,
                true)
        Assert.assertEquals(updateResponse.statusCode(), AUConstants.OK)

        //Get Secondary Joint Account
        shareableElements = AUTestUtil.getSecondaryUserDetails(getSharableBankAccounts(), false)
        accountID =  shareableElements[AUConstants.PARAM_ACCOUNT_ID]

        def updateResponseJointAccount = updateSecondaryUserInstructionPermission(accountID, userId, AUConstants.ACTIVE)
        Assert.assertEquals(updateResponseJointAccount.statusCode(), AUConstants.OK)
    }

    @Test
    void "CDS-423_Verify  selecting secondary user accounts for an Consent Amendment"() {

        //Send Push Authorisation Request
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //Select Secondary Account during authorisation
        doSecondaryAccountSelection(scopes, requestUri.toURI())
        Assert.assertNotNull(authorisationCode)

        //Get Access Token
        AccessTokenResponse responseBody = getUserAccessTokenResponse(clientId)
        userAccessToken = responseBody.tokens.accessToken
        cdrArrangementId = responseBody.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId)

        //Consent Amendment
        scopes.remove(AUAccountScope.BANK_TRANSACTION_READ)

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI()).toURI().toString()

        //Consent Authorisation UI Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Secondary Account
                    consentedAccount = authWebDriver.getElementAttribute(AUTestUtil.getSecondaryJointAccount1XPath(),
                            AUPageObjects.VALUE)
                    authWebDriver.clickButtonXpath(AUTestUtil.getSecondaryJointAccount1XPath())

                    //Click Submit/Next Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_SUBMIT_XPATH)

                    //Click Confirm Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)
                }
                .execute()
    }

    @Test
    void "CDS-427_Verify Consent Amendment if consumer nominated as secondary user for account by providing secondary user instruction"() {

        //Consent Authorisation by selecting individual accounts
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //Select Individual Account during authorisation
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), clientId)
                .toURI().toString()

        def automation1 = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Secondary Account
                    consentedAccount = authWebDriver.getElementAttribute(AUTestUtil.getSingleAccountXPath(),
                            AUPageObjects.VALUE)
                    authWebDriver.clickButtonXpath(AUTestUtil.getSingleAccountXPath())

                    //Click Submit/Next Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_SUBMIT_XPATH)

                    //Click Confirm Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)
                }
                .execute()

        authorisationCode = AUTestUtil.getCodeFromJwtResponse(automation1.currentUrl.get())

        //Get Access Token
        AccessTokenResponse responseBody = getUserAccessTokenResponse(clientId)
        userAccessToken = responseBody.tokens.accessToken
        cdrArrangementId = responseBody.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId)

        //Get Account Transaction Details
        def responseBeforeAmendment = doAccountRetrieval(userAccessToken)

        Assert.assertEquals(responseBeforeAmendment.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(responseBeforeAmendment.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertNotNull(AUTestUtil.parseResponseBody(responseBeforeAmendment,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[0]"))
        Assert.assertNull(AUTestUtil.parseResponseBody(responseBeforeAmendment,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[1]"))

        //Consent Amendment
        scopes.remove(AUAccountScope.BANK_TRANSACTION_READ)

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI()).toURI().toString()

        //Consent Authorisation UI Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Secondary Account
                    consentedAccount = authWebDriver.getElementAttribute(AUTestUtil.getSecondaryAccount1XPath(),
                            AUPageObjects.VALUE)
                    authWebDriver.clickButtonXpath(AUTestUtil.getSecondaryAccount1XPath())

                    //Click Submit/Next Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_SUBMIT_XPATH)

                    //Click Confirm Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)
                }
                .execute()

        // Get Code From URL
        authorisationCode = AUTestUtil.getCodeFromJwtResponse(automation.currentUrl.get())

        //Generate Token
        generateUserAccessToken()

        //Get Account Transaction Details
        def responseAfterAmendment = doAccountRetrieval(userAccessToken)

        Assert.assertEquals(responseAfterAmendment.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(responseAfterAmendment.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertNotNull(AUTestUtil.parseResponseBody(responseAfterAmendment,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[0]"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(responseAfterAmendment,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[1]"))
    }

    @Test (priority = 1)
    void "CDS-431_Verify if account owner of secondary account has restricted a particular secondary user from sharing accounts"() {

        //Send Push Authorisation Request
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //Select Secondary Account during authorisation
        doSecondaryAccountSelection(scopes, requestUri.toURI())
        Assert.assertNotNull(authorisationCode)

        //Get Access Token
        AccessTokenResponse responseBody = getUserAccessTokenResponse(clientId)
        userAccessToken = responseBody.tokens.accessToken
        cdrArrangementId = responseBody.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId)

        //Restrict Secondary User Instruction
        shareableElements = AUTestUtil.getSecondaryUserDetails(getSharableBankAccounts())
        accountID =  shareableElements[AUConstants.PARAM_ACCOUNT_ID]
        def updateResponse = updateSecondaryUserInstructionPermission(accountID, userId, AUConstants.INACTIVE,
                true)
        Assert.assertEquals(updateResponse.statusCode(), AUConstants.OK)

        //Consent Amendment
        scopes.remove(AUAccountScope.BANK_TRANSACTION_READ)

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI()).toURI().toString()

        //Consent Authorisation UI Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Secondary Accounts - Individual and Joint Accounts
                    Assert.assertFalse(authWebDriver.isElementPresent(AUPageObjects.SECONDARY_ACCOUNT_1))
                }
                .execute()
    }

    @Test
    void "CDS-551_Verify the pre-selected accounts in account selection page"() {

        //Send Push Authorisation Request
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //Select Secondary Account during authorisation
        doSecondaryAccountSelection(scopes, requestUri.toURI())
        Assert.assertNotNull(authorisationCode)

        //Get Access Token
        AccessTokenResponse responseBody = getUserAccessTokenResponse(clientId)
        userAccessToken = responseBody.tokens.accessToken
        cdrArrangementId = responseBody.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId)

        //Consent Amendment
        scopes.remove(AUAccountScope.BANK_TRANSACTION_READ)

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI()).toURI().toString()

        //Consent Authorisation UI Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Verify Secondary Account in Unavailable List
                    Assert.assertTrue(authWebDriver.isElementSelected(AUTestUtil.getSecondaryAccount1XPath()))
                }
                .execute()
    }

    @Test
    void "CDS-630_Verify restricting secondary user instruction from previously selected account"() {

        //Send Push Authorisation Request
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //Select Secondary Account during authorisation
        doSecondaryAccountSelection(scopes, requestUri.toURI())
        Assert.assertNotNull(authorisationCode)

        //Get Access Token
        AccessTokenResponse responseBody = getUserAccessTokenResponse(clientId)
        userAccessToken = responseBody.tokens.accessToken
        cdrArrangementId = responseBody.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId)

        //Restrict Secondary User Instruction
        def updateResponse = updateSecondaryUserInstructionPermission(accountID, userId, AUConstants.INACTIVE)
        Assert.assertEquals(updateResponse.statusCode(), AUConstants.OK)

        //Consent Amendment
        scopes.remove(AUAccountScope.BANK_TRANSACTION_READ)

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI()).toURI().toString()

        //Consent Authorisation UI Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Verify Secondary Account in Unavailable List
                    Assert.assertTrue(authWebDriver.isElementEnabled(AUTestUtil.getSecondaryAccount1XPath()))
                }
                .execute()
    }
}
