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
package com.wso2.openbanking.toolkit.cds.integration.tests.metadata_update

import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.wso2.openbanking.test.framework.automation.AUBasicAuthAutomationStep
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.test.framework.automation.WaitForRedirectAutomationStep
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURegistrationRequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUMockCDRIntegrationUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUDCRConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUAuthorisationBuilder
import org.openqa.selenium.By
import org.testng.Assert
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Test Related to Meta Data Update - Consent Amendment
 */
class MetaDataConsentAmendment extends AbstractAUTests {

    private String accessToken
    private String clientId
    private String secondAuthorisationCode = null
    private AccessTokenResponse userAccessToken, secondUserAccessToken = null
    private String cdrArrangementId = ""
    private String requestUri
    private String registrationPath = AUDCRConstants.REGISTRATION_ENDPOINT
    private String headerString = ConfigParser.instance.getBasicAuthUser() + ":" + ConfigParser.instance.getBasicAuthUserPassword()

    private List<AUConstants.SCOPES> scopes = [
            AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
            AUConstants.SCOPES.BANK_PAYEES_READ,
            AUConstants.SCOPES.BANK_TRANSACTION_READ,
            AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ
    ]

    @BeforeClass(alwaysRun = true)
    void "Setup"() {

        //Load Meta Data to CDR Register
        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)
        AURegistrationRequestBuilder.retrieveADRInfo()

        String jti = String.valueOf(System.currentTimeMillis())

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaimsWithGivenJti(jti))
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
        clientId = TestUtil.parseResponseBody(registrationResponse, "client_id")
    }

    @Test
    void "TC022_Verify the Consent Amendment when the SP Active and ADR Active"() {

        // Tests for Consent Amendment scenarios
        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        scopes.remove(AUConstants.SCOPES.BANK_PAYEES_READ)
        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")
        Assert.assertNotNull(cdrArrangementId)

        //remove an existing scope and add a new scope to amend the consent
        scopes.remove(AUConstants.SCOPES.BANK_TRANSACTION_READ)
        scopes.add(AUConstants.SCOPES.BANK_PAYEES_READ)

        //Retrieve and assert the request URI from Push Authorization request
        requestUri = TestUtil.parseResponseBody(doPushAuthorisationRequest(scopes, AUConstants.AMENDED_SHARING_DURATION,
                true, cdrArrangementId, clientId), "requestUri")
        Assert.assertNotNull(requestUri)

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(new AUAuthorisationBuilder(scopes, requestUri.toURI(),
                        clientId).authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUTestUtil.getAltSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                    // Below assertion is to verify the labels of amendment are visible on review page or not
                    Assert.assertTrue(driver.findElement(By.xpath(AUConstants.LBL_NEW_PAYEES_INDICATOR_XPATH)).isDisplayed())
                    driver.findElement(By.xpath(AUConstants.CONSENT_CONFIRM_XPATH)).click()
                }
                .addStep(new WaitForRedirectAutomationStep())
                .execute()

        //Retrieve the second authorization code
        secondAuthorisationCode = TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())
        Assert.assertNotNull(secondAuthorisationCode)

        //Retrieve the second user access token and assert the CDR arrangement ID is the same.
        secondUserAccessToken = AURequestBuilder.getUserToken(secondAuthorisationCode, clientId)
        verifyScopes(secondUserAccessToken.toJSONObject().get("scope").toString(), scopes)
        Assert.assertEquals(cdrArrangementId, secondUserAccessToken.getCustomParameters().get("cdr_arrangement_id"),
                "Amended CDR id is not original CDR id ")
    }

    @AfterClass(alwaysRun = true)
    void tearDown() {
        accessToken = AURequestBuilder.getApplicationToken(scopes.collect({ it.scopeString }), clientId)
        def deletionResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .delete(registrationPath + clientId)

        Assert.assertEquals(deletionResponse.statusCode(), AUConstants.STATUS_CODE_204)
    }
}
