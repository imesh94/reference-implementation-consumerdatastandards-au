/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */
package com.wso2.openbanking.toolkit.cds.integration.tests.metadata_update

import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.wso2.openbanking.test.framework.automation.BasicAuthErrorStep
import com.wso2.openbanking.test.framework.automation.AUBasicAuthAutomationStep
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.test.framework.automation.WaitForRedirectAutomationStep
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
 * Test Related to Meta Data Update
 */
class MetaDataConsentAuthorization extends AbstractAUTests {

    private String accessToken
    private String clientId
    private AccessTokenResponse userAccessToken
    private String registrationPath = AUDCRConstants.REGISTRATION_ENDPOINT

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
    void "TC001_Verify the Consent Authorisation when the SP and ADR both active"() {

        //Test case for Consent Authorization scenario
        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        //Wait time below can be updated as per cache update time in the Packs
        sleep(81000)
        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true, "", clientId
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_CONFIRM_XPATH)).click()
                }
                .addStep(new WaitForRedirectAutomationStep())
                .execute()

        // Get Code From URL
        authorisationCode = TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())
        Assert.assertNotNull(authorisationCode)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)

    }

    @Test
    void "TC002_Verify the Consent Authorisation when the SP Removed and ADR active"() {

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 3, 1)
        // The wait time for Meta Data Update Cache period on pack which can be changed
        sleep(81000)
        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true, "", clientId
        )

        validateErrorDescriptionInAuthoriseUrl(authorisationBuilder.authoriseUrl, AUConstants.ERROR_INVALID_SOFTWARE_PRODUCT)
    }

    @Test
    void "TC003_Verify the Consent Authorisation when the SP Inactive and ADR  active"() {

        //Test case for Consent Authorization scenario by changing ADR and SP Status
        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 2, 1)
        sleep(81000)
        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true, "", clientId
        )

        validateErrorDescriptionInAuthoriseUrl(authorisationBuilder.authoriseUrl, AUConstants.ERROR_INVALID_SOFTWARE_PRODUCT)
    }

    @Test
    void "TC004_Verify the Consent Authorisation when the SP Inactive and ADR  Suspended"() {

        //Test case for Consent Authorization scenario by changing ADR and SP Status
        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 2, 3)
        sleep(81000)
        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true, "", clientId
        )

        validateErrorDescriptionInAuthoriseUrl(authorisationBuilder.authoriseUrl, AUConstants.ERROR_INVALID_SOFTWARE_PRODUCT)
    }

    @Test
    void "TC005_Verify the Consent Authorisation when the SP Removed and ADR  Suspended"() {

        //Test case for Consent Authorization scenario by changing ADR and SP Status
        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 3, 3)
        sleep(81000)
        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true, "", clientId
        )

        validateErrorDescriptionInAuthoriseUrl(authorisationBuilder.authoriseUrl, AUConstants.ERROR_INVALID_SOFTWARE_PRODUCT)
    }

    @Test
    void "TC006_Verify the Consent Authorisation when the SP Removed and ADR  Surrendered"() {

        //Test case for Consent Authorization scenario by changing ADR and SP Status
        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 3, 5)
        sleep(81000)
        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true, "", clientId
        )

        validateErrorDescriptionInAuthoriseUrl(authorisationBuilder.authoriseUrl, AUConstants.ERROR_INVALID_SOFTWARE_PRODUCT)
    }

    @Test
    void "TC007_Verify the Consent Authorisation when the SP Removed and ADR  Revoked"() {

        //Test case for Consent Authorization scenario by changing ADR and SP Status
        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 3, 4)
        sleep(81000)
        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true, "", clientId
        )

        validateErrorDescriptionInAuthoriseUrl(authorisationBuilder.authoriseUrl, AUConstants.ERROR_INVALID_SOFTWARE_PRODUCT)
    }

    private void validateErrorDescriptionInAuthoriseUrl(String authoriseUrl, String errorDescription) {
        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new BasicAuthErrorStep(authoriseUrl))
                .addStep(new WaitForRedirectAutomationStep())
                .execute()

        Assert.assertTrue(TestUtil.getErrorDescriptionFromUrl(automation.currentUrl.get()).contains(errorDescription))
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
