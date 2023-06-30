/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.metadata_update

import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.utility.AUTestUtil
import com.wso2.openbanking.test.framework.automation.NavigationAutomationStep
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Test Related to Meta Data Update - Consent Authorisation
 */
class MetaDataConsentAuthorization extends AUTest{

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

    @Test(priority = 1)
    void "TC001_Verify the Consent Authorisation when the SP and ADR both active"() {

        doConsentAuthorisation()
        generateUserAccessToken()
    }

    @Test(priority = 3)
    void "TC002_Verify the Consent Authorisation when the SP Removed and ADR active"() {

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //TODO: Update status
        sleep(81000)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), clientId)
                .toURI().toString()

        def automationResponse = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new NavigationAutomationStep(authoriseUrl, 10))
                .execute()

        String url = automationResponse.currentUrl.get()
        String errorUrl

        errorUrl = url.split("error_description=")[1].split("&")[0].replaceAll("\\+"," ")
        Assert.assertEquals(errorUrl, AUConstants.ERROR_INVALID_CLIENT_ID)
    }

    @Test(priority = 2)
    void "TC003_Verify the Consent Authorisation when the SP Inactive and ADR  active"() {

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //TODO: Update status
        sleep(81000)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), clientId)
                .toURI().toString()

        def automationResponse = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new NavigationAutomationStep(authoriseUrl, 10))
                .execute()

        String url = automationResponse.currentUrl.get()
        String errorUrl

        errorUrl = url.split("error_description=")[1].split("&")[0].replaceAll("\\+"," ")
        Assert.assertEquals(errorUrl, AUConstants.ERROR_INVALID_SOFTWARE_PRODUCT)
    }

    @Test(priority = 2)
    void "TC004_Verify the Consent Authorisation when the SP Inactive and ADR  Suspended"() {

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //TODO: Update status
        sleep(81000)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), clientId)
                .toURI().toString()

        def automationResponse = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new NavigationAutomationStep(authoriseUrl, 10))
                .execute()

        String url = automationResponse.currentUrl.get()
        String errorUrl

        errorUrl = url.split("error_description=")[1].split("&")[0].replaceAll("\\+"," ")
        Assert.assertEquals(errorUrl, AUConstants.ERROR_INVALID_SOFTWARE_PRODUCT)
    }

    @Test(priority = 4)
    void "TC005_Verify the Consent Authorisation when the SP Removed and ADR  Suspended"() {

        setup()

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //TODO: Update status
        sleep(81000)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), clientId)
                .toURI().toString()

        def automationResponse = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new NavigationAutomationStep(authoriseUrl, 10))
                .execute()

        String url = automationResponse.currentUrl.get()
        String errorUrl

        errorUrl = url.split("error_description=")[1].split("&")[0].replaceAll("\\+"," ")
        Assert.assertEquals(errorUrl, AUConstants.ERROR_INVALID_CLIENT_ID)
    }

    @Test(priority = 4)
    void "TC006_Verify the Consent Authorisation when the SP Removed and ADR  Surrendered"() {

        setup()

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //TODO: Update status
        sleep(81000)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), clientId)
                .toURI().toString()

        def automationResponse = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new NavigationAutomationStep(authoriseUrl, 10))
                .execute()

        String url = automationResponse.currentUrl.get()
        String errorUrl

        errorUrl = url.split("error_description=")[1].split("&")[0].replaceAll("\\+"," ")
        Assert.assertEquals(errorUrl, AUConstants.ERROR_INVALID_CLIENT_ID)
    }

    @Test(priority = 4)
    void "TC007_Verify the Consent Authorisation when the SP Removed and ADR  Revoked"() {

        setup()

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        //TODO: Update status
        sleep(81000)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri.toURI(), clientId)
                .toURI().toString()

        def automationResponse = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new NavigationAutomationStep(authoriseUrl, 10))
                .execute()

        String url = automationResponse.currentUrl.get()
        String errorUrl

        errorUrl = url.split("error_description=")[1].split("&")[0].replaceAll("\\+"," ")
        Assert.assertEquals(errorUrl, AUConstants.ERROR_INVALID_CLIENT_ID)
    }
}
