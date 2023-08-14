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
import com.wso2.cds.test.framework.request_builder.AURegistrationRequestBuilder
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import com.wso2.openbanking.test.framework.automation.AutomationMethod
import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.nio.charset.Charset

/**
 * Ceasing Secondary User Consent Authorisation and Account Retrieval Test.
 */
class CeasingSecondaryUserConsentFlowTest extends AUTest {

    def shareableElements, clientHeader
    String accountID, userId, legalEntityId, altLegalEntityId
    Response response
    List <String> legalEntityList

    @BeforeClass
    void "Pre Execution Step"() {

        auConfiguration.setPsuNumber(1)
        clientId = auConfiguration.getAppInfoClientID()
        //Get Sharable Account List and Secondary User with Authorize Permission
        shareableElements = AUTestUtil.getSecondaryUserDetails(getSharableBankAccounts())

        accountID =  shareableElements[AUConstants.PARAM_ACCOUNT_ID]
        userId = auConfiguration.getUserPSUName()
        clientHeader = "${Base64.encoder.encodeToString(getCDSClient().getBytes(Charset.defaultCharset()))}"

        def updateResponse = updateSecondaryUserInstructionPermission(accountID, userId, AUConstants.ACTIVE)
        Assert.assertEquals(updateResponse.statusCode(), AUConstants.OK)

        //Get Legal Entity ID of the client
        accessToken = getApplicationAccessToken(auConfiguration.getAppInfoClientID())
        Assert.assertNotNull(accessToken)

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .get(AUConstants.DCR_REGISTRATION_ENDPOINT + auConfiguration.getAppInfoClientID())

        legalEntityId = registrationResponse.jsonPath().get(AUConstants.DCR_CLAIM_LEGAL_ENTITY_ID)
    }

    @Test
    void "CDS-643_Verify account is listed under unavailable accounts once the legal entity is restricted by account owner"() {

        //Block the sharing status
        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId, AUConstants.BLOCK_ENTITY)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //Send Push Authorisation Request
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //Get Authorisation URL
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(requestUri.toURI(), clientId)
                .toURI().toString()

        //Consent Authorisation UI Flow Validations
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Account Should be listed under unavailable accounts list and not displayed under selectable list
                    Assert.assertTrue(authWebDriver.isElementPresent(AUTestUtil.getUnavailableAccountsXPath(accountID)))
                    Assert.assertFalse(authWebDriver.isElementPresent(AUTestUtil.getSecondaryAccount1XPath()))

                    //TODO: Verify notification to indicate the reason for pausing the data sharing from that account
                }
                .execute()
    }

    @Test (priority = 1)
    void "CDS-644_Verify account is not listed under unavailable accounts once the legal entity is active by account owner"() {

        //Active the sharing status
        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId, AUConstants.ACTIVE)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //Send Push Authorisation Request
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //Get Authorisation URL
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(requestUri.toURI(), clientId)
                .toURI().toString()

        //Consent Authorisation UI Flow Validations
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Secondary Account
                    Assert.assertTrue(authWebDriver.isElementEnabled(AUTestUtil.getSecondaryAccount1XPath()))
                    selectSecondaryAccount(authWebDriver, false)

                    //Click Submit/Next Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_SUBMIT_XPATH)

                    //Click Confirm Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)
                }
                .execute()

        // Get Code From URL
        authorisationCode = AUTestUtil.getCodeFromJwtResponse(automation.currentUrl.get())
    }

    @Test (priority = 1,
            dependsOnMethods = "CDS-644_Verify account is not listed under unavailable accounts once the legal entity is active by account owner")
    void "CDS-645_Retrieve accounts after blocking the data sharing for legal entity"() {

        //Get User Access Token
        generateUserAccessToken()

        //Block the sharing status
        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId, AUConstants.BLOCK_ENTITY)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //Account Retrieval
        Response accountResponse = doAccountRetrieval(userAccessToken)
        Assert.assertEquals(accountResponse.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNull(AUTestUtil.parseResponseBody(accountResponse,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[0]"))
    }

    @Test (priority = 1,
            dependsOnMethods = "CDS-645_Retrieve accounts after blocking the data sharing for legal entity")
    void "CDS-646_Retrieve accounts after activating the data sharing for legal entity"() {

        //Block the sharing status
        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId, AUConstants.ACTIVE)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //Account Retrieval
        Response accountResponse = doAccountRetrieval(userAccessToken)
        Assert.assertEquals(accountResponse.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(AUTestUtil.parseResponseBody(accountResponse,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[0]"))
    }

    @Test
    void "CDS-647_Retrieve accounts after blocking the data sharing for one legal entity when the consent is given for multiple accounts"() {

        //Active the sharing status for secondary account 1
        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId, AUConstants.ACTIVE)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //Active the sharing status for secondary account 1
        def altAccountID = AUConstants.AlternateAccountId
        def updateResponse = updateSecondaryUserInstructionPermission(altAccountID, userId, AUConstants.ACTIVE)
        Assert.assertEquals(updateResponse.statusCode(), AUConstants.OK)

        response = updateLegalEntityStatus(clientHeader, altAccountID, userId, legalEntityId, AUConstants.ACTIVE)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //Send Push Authorisation Request
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //Select Secondary Account during authorisation
        doSecondaryAccountSelection(scopes, requestUri.toURI(), clientId, true)
        Assert.assertNotNull(authorisationCode)

        //Get User Access Token
        generateUserAccessToken()

        //Account Retrieval before blocking the sharing status
        Response accountResponse1 = doAccountRetrieval(userAccessToken)
        Assert.assertEquals(accountResponse1.statusCode(), AUConstants.STATUS_CODE_200)

        def consentedAccId = AUTestUtil.parseResponseBody(accountResponse1, "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[0]")
        def consentedAltAccId = AUTestUtil.parseResponseBody(accountResponse1, "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[1]")
        Assert.assertNotNull(consentedAccId)
        Assert.assertNotNull(consentedAltAccId)

        //Block the sharing status of one account
        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId, AUConstants.BLOCK_ENTITY)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //Account Retrieval after blocking the sharing status
        Response accountResponse2 = doAccountRetrieval(userAccessToken)
        Assert.assertEquals(accountResponse2.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(AUTestUtil.parseResponseBody(accountResponse2,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[0]"))
        Assert.assertNull(AUTestUtil.parseResponseBody(accountResponse2,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[1]"))

        //Single Account Retrieval after blocking the sharing status
        Response accountResponse = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNT, clientHeader)
                .baseUri(auConfiguration.getServerBaseURL())
                .get("${AUConstants.BULK_ACCOUNT_PATH}/${consentedAccId}")

        Assert.assertEquals(accountResponse.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNull(AUTestUtil.parseResponseBody(accountResponse,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}"))
    }

    @Test
    void "CDS-647_Consent amendment after ceasing the secondary user sharing"() {

        //Active the sharing status for secondary account 1
        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId, AUConstants.ACTIVE)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

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
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(requestUri.toURI()).toURI().toString()

        //Consent Authorisation UI Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Click Submit/Next Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_SUBMIT_XPATH)

                    //Click Confirm Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)
                }
                .execute()

        //Block the sharing status of one account
        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId, AUConstants.BLOCK_ENTITY)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //Account Retrieval after blocking the sharing status
        Response accountResponse2 = doAccountRetrieval(userAccessToken)
        Assert.assertEquals(accountResponse2.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNull(AUTestUtil.parseResponseBody(accountResponse2,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[0]"))
    }

    @Test
    void "CDS-649_Verify account is listed under unavailable accounts once the legal entity is restricted in consent amendment flow"() {

        //Active the sharing status for secondary account 1
        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId, AUConstants.ACTIVE)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

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

        //Block the sharing status of one account
        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId, AUConstants.BLOCK_ENTITY)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //Consent Amendment
        scopes.remove(AUAccountScope.BANK_TRANSACTION_READ)

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(requestUri.toURI()).toURI().toString()

        //Consent Authorisation UI Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Account Should be listed under unavailable accounts list and not displayed under selectable list
                    Assert.assertTrue(authWebDriver.isElementPresent(AUTestUtil.getUnavailableAccountsXPath(accountID)))
                    Assert.assertFalse(authWebDriver.isElementPresent(AUTestUtil.getSecondaryAccount1XPath()))

                    //Click Submit/Next Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_SUBMIT_XPATH)

                    //Click Confirm Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)
                }
                .execute()
    }
}
