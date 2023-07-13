/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.accounts

import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.automation.consent.AUBasicAuthAutomationStep
import com.wso2.cds.test.framework.constant.AUAccountProfile
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.AUPageObjects
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import com.wso2.openbanking.test.framework.automation.AutomationMethod
import org.testng.Assert
import org.testng.annotations.Test

import java.nio.charset.Charset

/**
 * Duplicate Common Auth Id Test.
 */
class DuplicateCommonAuthIdTest extends AUTest {

    def clientHeader = "${Base64.encoder.encodeToString(getCDSClient().getBytes(Charset.defaultCharset()))}"

    @Test
    void "TC0202006_Initiate two authorisation consent flows on same browser session"() {

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(requestUri.toURI(),
                auConfiguration.getAppInfoClientID()).toURI().toString()

        automationResponse = getBrowserAutomation(AUConstants.DEFAULT_DELAY, false)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Profile and Accounts
                    selectProfileAndAccount(authWebDriver, AUAccountProfile.INDIVIDUAL, true)

                    //Click Confirm Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)

                    //Click Authorise Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)
                }
                .execute(false)

        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)

        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(requestUri.toURI(),
                auConfiguration.getAppInfoClientID()).toURI().toString()

        automationResponse = getBrowserAutomation(AUConstants.DEFAULT_DELAY, true)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Profile and Accounts
                    selectProfileAndAccount(authWebDriver, AUAccountProfile.INDIVIDUAL, true)

                    //Click Confirm Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)

                    //Click Authorise Button
                    authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_CONFIRM_XPATH)
                }
                .execute(true)

        authorisationCode = AUTestUtil.getCodeFromJwtResponse(automationResponse.currentUrl.get())
        Assert.assertNotNull(authorisationCode)
    }

    @Test(dependsOnMethods = "TC0202006_Initiate two authorisation consent flows on same browser session")
    void "TC0203005_Exchange authorisation code for access token"() {

        AccessTokenResponse accessTokenResponse = getUserAccessTokenResponse(auConfiguration.getAppInfoClientID())
        userAccessToken = accessTokenResponse.tokens.accessToken
        Assert.assertNotNull(userAccessToken)
    }

    @Test(dependsOnMethods = "TC0203005_Exchange authorisation code for access token")
    void "TC0401007_Retrieve bulk accounts list"() {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))
    }
}
