/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.keymanager.test.bnr

import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.automation.consent.AUBasicAuthAutomationStep
import com.wso2.cds.test.framework.constant.AUAccountProfile
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUBusinessUserPermission
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.AUPageObjects
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import com.wso2.openbanking.test.framework.automation.AutomationMethod
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.nio.charset.Charset

/**
 * Consent Amendment Flow in BNR Flow.
 */
class ConsentAmendmentFlowTests extends AUTest {

    def clientHeader
    String accountID
    String accountOwnerUserID
    String nominatedRepUserID
    String nominatedRepUserID2

    @BeforeClass(alwaysRun = true)
    void "Nominate Business User Representative"() {
        auConfiguration.setPsuNumber(2)
        clientHeader = "${Base64.encoder.encodeToString(getCDSClient().getBytes(Charset.defaultCharset()))}"

        //Get Sharable Account List and Nominate Business Representative with Authorize and View Permissions
        def shareableElements = AUTestUtil.getSharableAccountsList(getSharableBankAccounts())

        accountID =  shareableElements[AUConstants.PARAM_ACCOUNT_ID]
        accountOwnerUserID = shareableElements[AUConstants.ACCOUNT_OWNER_USER_ID]
        nominatedRepUserID = shareableElements[AUConstants.NOMINATED_REP_USER_ID]
        nominatedRepUserID2 = shareableElements[AUConstants.NOMINATED_REP_USER_ID2]

        def updateResponse = updateMultiBusinessUserPermission(clientHeader, accountID, accountOwnerUserID,
                nominatedRepUserID, AUBusinessUserPermission.AUTHORIZE.getPermissionString(), nominatedRepUserID2,
                AUBusinessUserPermission.VIEW.getPermissionString())
        Assert.assertEquals(updateResponse.statusCode(), AUConstants.OK)

        def businessAccount3 = "586-522-B0025"
        def updateSecondAccPermissionResponse = updateMultiBusinessUserPermission(clientHeader, businessAccount3, accountOwnerUserID,
                nominatedRepUserID, AUBusinessUserPermission.AUTHORIZE.getPermissionString(), nominatedRepUserID2,
                AUBusinessUserPermission.VIEW.getPermissionString())
        Assert.assertEquals(updateSecondAccPermissionResponse.statusCode(), AUConstants.OK)
    }

    @Test
    void "CDS-482_Verify skipping profile selection step in consent amendment flow"() {

        //Consent Authorisation
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        doConsentAuthorisationViaRequestUriSingleAccount(scopes, requestUri.toURI(), null, AUAccountProfile.ORGANIZATION_B)

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

        //Consent Authorisation UI Flow -  Profile selection is not present
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Verify if Profile Selection Enabled
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Verify Account Selection Page
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getBusinessAccount3CheckBox())
                        authWebDriver.clickButtonXpath(AUTestUtil.getBusinessAccount3CheckBox())

                    } else {
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath())
                        log.info("Profile Selection is Disabled")
                    }
                }
                .execute()
    }

    @Test
    void "CDS-485_Verify a Consent denial flow with Business Account selection in consent amendment"() {

        //Consent Authorisation
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        doConsentAuthorisationViaRequestUriSingleAccount(scopes, requestUri.toURI(), null, AUAccountProfile.ORGANIZATION_A)

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

        //Consent Authorisation UI Flow -  Profile selection is not present
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Verify if Profile Selection Enabled
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Click Confirm Button
                        authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)

                        //Click on deny
                        authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_DENY_XPATH)

                    } else {
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath())
                        log.info("Profile Selection is Disabled")
                    }
                }
                .execute()

        String url = automation.currentUrl.get()
        def errorMessage = url.split("error_description=")[1].split("&")[0].replaceAll("\\+"," ")
        Assert.assertEquals(errorMessage, AUConstants.USER_DENIED_THE_CONSENT)
    }

    @Test (groups = "SmokeTest")
    void "CDS-514_Verify a Consent Amendment flow with a Business user account"() {

        //Consent Authorisation
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        doConsentAuthorisationViaRequestUriSingleAccount(scopes, requestUri.toURI(), null, AUAccountProfile.ORGANIZATION_B)

        //Get Access Token
        AccessTokenResponse responseBody = getUserAccessTokenResponse(clientId)
        userAccessToken = responseBody.tokens.accessToken
        cdrArrangementId = responseBody.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId)

        //Get Account Details
        response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[0]"))
        Assert.assertNull(AUTestUtil.parseResponseBody(response,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[1]"))

        //Consent Amendment
        scopes.remove(AUAccountScope.BANK_ACCOUNT_DETAIL_READ)

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(requestUri.toURI()).toURI().toString()

        //Consent Authorisation UI Flow -  Profile selection is not present
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Verify if Profile Selection Enabled
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Verify Account Selection Page
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getBusinessAccount3CheckBox())
                        authWebDriver.clickButtonXpath(AUTestUtil.getBusinessAccount3CheckBox())

                        //Click Confirm Button
                        authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)

                        //Click Authorise Button
                        authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)

                    } else {
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath())
                        log.info("Profile Selection is Disabled")
                    }
                }
                .execute()

        // Get Code From URL
        authorisationCode = AUTestUtil.getCodeFromJwtResponse(automation.currentUrl.get())

        //Generate Token
        generateUserAccessToken()

        //Get Account Transaction Details
        def responseAfterAmendment = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(responseAfterAmendment.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(responseAfterAmendment.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertNotNull(AUTestUtil.parseResponseBody(responseAfterAmendment,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[0]"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(responseAfterAmendment,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[1]"))
    }

    @Test
    void "CDS-617_Verify consent amendment from non primary member"() {

        //Consent Authorisation
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        doConsentAuthorisationViaRequestUriSingleAccount(scopes, requestUri.toURI(), null, AUAccountProfile.ORGANIZATION_B)

        //Get Access Token
        AccessTokenResponse responseBody = getUserAccessTokenResponse(clientId)
        userAccessToken = responseBody.tokens.accessToken
        cdrArrangementId = responseBody.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId)

        //Get Account Details
        response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[0]"))
        Assert.assertNull(AUTestUtil.parseResponseBody(response,
                "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[1]"))

        //Consent Amendment
        scopes.remove(AUAccountScope.BANK_ACCOUNT_DETAIL_READ)

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(requestUri.toURI()).toURI().toString()

        auConfiguration.setPsuNumber(3)
        //Consent Authorisation UI Flow -  Profile selection is not present
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .execute()

        def errorUrl = automation.currentUrl.get().split("statusMsg=")[1].replace("%20", " ")
        Assert.assertTrue(errorUrl.contains("Retrieving consent data failed"))

    }
}
