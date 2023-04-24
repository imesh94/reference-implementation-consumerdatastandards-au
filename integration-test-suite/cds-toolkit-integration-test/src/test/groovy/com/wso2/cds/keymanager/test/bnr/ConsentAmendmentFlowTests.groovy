/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.cds.keymanager.test.bnr

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
import org.openqa.selenium.By
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
    }

    @Test
    void "CDS-482_Verify skipping profile selection step in consent amendment flow"() {

        //Consent Authorisation
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        doConsentAuthorisationViaRequestUriSingleAccount(scopes, requestUri.toURI(), null, AUAccountProfile.BUSINESS)
        userAccessToken = getUserAccessTokenResponse(clientId).tokens.accessToken
        cdrArrangementId = userAccessToken.get.get("cdr_arrangement_id")
        Assert.assertNotNull(cdrArrangementId)

        //Consent Amendment
        scopes.remove(AUAccountScope.BANK_TRANSACTION_READ)

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri)
                .toURI().toString()

        //Consent Authorisation UI Flow -  Profile selection is not present
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Accounts
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Verify Account Selection Page
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getBusinessAccount1CheckBox())
                        authWebDriver.clickButtonXpath(AUTestUtil.getBusinessAccount1CheckBox())

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
        doConsentAuthorisationViaRequestUriSingleAccount(scopes, requestUri.toURI(), null, AUAccountProfile.BUSINESS)
        userAccessToken = getUserAccessTokenResponse(clientId).tokens.accessToken
        cdrArrangementId = userAccessToken.get.get("cdr_arrangement_id")
        Assert.assertNotNull(cdrArrangementId)

        //Consent Amendment
        scopes.remove(AUAccountScope.BANK_TRANSACTION_READ)

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri)
                .toURI().toString()

        //Consent Authorisation UI Flow -  Profile selection is not present
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Accounts
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Verify Account Selection Page
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getBusinessAccount1CheckBox())
                        authWebDriver.clickButtonXpath(AUTestUtil.getBusinessAccount1CheckBox())

                        //Click on deny
                        authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_DENY_XPATH)
                        driver.findElement(By.xpath(AUPageObjects.CONFIRM_CONSENT_DENY_XPATH)).click()

                    } else {
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath())
                        log.info("Profile Selection is Disabled")
                    }
                }
                .execute()
    }

    @Test (groups = "SmokeTest")
    void "CDS-514_Verify a Consent Amendment flow with a Business user account"() {

        //Consent Authorisation
        doConsentAuthorisation(null, AUAccountProfile.BUSINESS)
        generateUserAccessToken()

        //Get Account Transaction Details
        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.GET_TRANSACTIONS}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        //Consent Amendment
        scopes.remove(AUAccountScope.BANK_TRANSACTION_READ)

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri)
                .toURI().toString()

        //Consent Authorisation UI Flow -  Profile selection is not present
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Accounts
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Verify Account Selection Page
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getBusinessAccount1CheckBox())
                        authWebDriver.clickButtonXpath(AUTestUtil.getBusinessAccount1CheckBox())

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
        authorisationCode = AUTestUtil.getHybridCodeFromUrl(automation.currentUrl.get())

        //Generate Token
        generateUserAccessToken()

        //Get Account Transaction Details
        def responseAfterAmendment = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.GET_TRANSACTIONS}")

        Assert.assertEquals(responseAfterAmendment.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(responseAfterAmendment.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
    }
}
