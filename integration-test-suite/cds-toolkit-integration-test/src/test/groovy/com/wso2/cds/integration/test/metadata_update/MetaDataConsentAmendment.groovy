/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.metadata_update

import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.automation.consent.AUBasicAuthAutomationStep
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.AUPageObjects
import com.wso2.cds.test.framework.request_builder.AURegistrationRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import com.wso2.openbanking.test.framework.automation.AutomationMethod
import org.openqa.selenium.By
import org.testng.Assert
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Test Related to Meta Data Update - Consent Amendment
 */
class MetaDataConsentAmendment extends AUTest{

    @BeforeClass(alwaysRun = true)
    void setup() {
        auConfiguration.setTppNumber(1)

        //Register Second TPP.
        def registrationResponse = tppRegistration()
        clientId = AUTestUtil.parseResponseBody(registrationResponse, "client_id")
        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.CREATED)

        //Write Client Id of TPP2 to config file.
        AUTestUtil.writeXMLContent(auConfiguration.getOBXMLFile().toString(), "Application",
                "ClientID", clientId, auConfiguration.getTppNumber())

        doConsentAuthorisation(clientId)

        auConfiguration.setTppNumber(0)
        accessToken = getApplicationAccessToken(clientId)
        Assert.assertNotNull(accessToken)
    }

    @Test
    void "TC022_Verify the Consent Amendment when the SP Active and ADR Active"() {

        //Remove Payee Scope and do authorisation
        scopes.remove(AUAccountScope.BANK_PAYEES_READ)
        doConsentAuthorisation(clientId)

        //Get Access Token
        AccessTokenResponse responseBody = getUserAccessTokenResponse(clientId)
        userAccessToken = responseBody.tokens.accessToken
        cdrArrangementId = responseBody.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId)

        //remove an existing scope and add a new scope to amend the consent
        scopes.remove(AUAccountScope.BANK_TRANSACTION_READ)
        scopes.add(AUAccountScope.BANK_PAYEES_READ)

        //Retrieve and assert the request URI from Push Authorization request
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.AMENDED_SHARING_DURATION,
                true, cdrArrangementId)
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI()).toURI().toString()

        //Consent Authorisation UI Flow -  Profile selection is not present
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Verify if Profile Selection Enabled
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Verify Account Selection Page
                        Assert.assertTrue(authWebDriver.isElementDisplayed(AUTestUtil.getAltSingleAccountXPath()))
                        authWebDriver.clickButtonXpath(AUTestUtil.getAltSingleAccountXPath())

                        //Click Confirm Button
                        authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)

                        // Below assertion is to verify the labels of amendment are visible on review page or not
                        Assert.assertTrue(driver.findElement(By.xpath(AUConstants.LBL_NEW_PAYEES_INDICATOR_XPATH))
                                .isDisplayed())

                        //Click Authorise Button
                        authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)

                    } else {
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath())
                        log.info("Profile Selection is Disabled")
                    }
                }
                .execute()

        //Retrieve the second authorization code
        authorisationCode = AUTestUtil.getHybridCodeFromUrl(automation.currentUrl.get())
        Assert.assertNotNull(authorisationCode)

        //Retrieve the second user access token and assert the CDR arrangement ID is the same.
        // Retrieve the user access token by auth code
        responseBody = getUserAccessTokenResponse(clientId)
        userAccessToken = responseBody.tokens.accessToken
        verifyScopes(responseBody.toJSONObject().get("scope").toString())
        Assert.assertEquals(cdrArrangementId, responseBody.getCustomParameters().get("cdr_arrangement_id"),
                "Amended CDR id is not original CDR id ")
    }

    @AfterClass(alwaysRun = true)
    void tearDown() {
        List<String> scopes = new ArrayList<>();
        scopes.add(AUAccountScope.CDR_REGISTRATION.getScopeString())
        accessToken = getApplicationAccessToken(clientId)
        def deletionResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .delete(AUConstants.DCR_REGISTRATION_ENDPOINT + clientId)

        Assert.assertEquals(deletionResponse.statusCode(), AUConstants.STATUS_CODE_204)
    }
}
