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

package com.wso2.openbanking.toolkit.cds.test.common.utils

import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.test.framework.automation.AUBasicAuthAutomationStep
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.test.framework.automation.WaitForRedirectAutomationStep
import com.wso2.openbanking.test.framework.model.AccessTokenJwtDto
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.testng.Assert
import org.testng.annotations.BeforeClass
import io.restassured.response.Response

import java.nio.charset.Charset

/**
 * Base Test For Accounts.
 */
class AbstractAUTests {

    public List<AUConstants.SCOPES> scopes = [
            AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
            AUConstants.SCOPES.BANK_ACCOUNT_DETAIL_READ,
            AUConstants.SCOPES.BANK_TRANSACTION_READ,
            AUConstants.SCOPES.BANK_PAYEES_READ,
            AUConstants.SCOPES.BANK_REGULAR_PAYMENTS_READ,
            AUConstants.SCOPES.BANK_CUSTOMER_BASIC_READ,
            AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ
    ]

    public String cdsClient = "${AppConfigReader.getClientId()}:${AppConfigReader.getClientSecret()}"

    public String authorisationCode
    public String userAccessToken
    public String consentedAccount
    public String secondConsentedAccount
    public String transactionId
    public String productId
    public String payeeId
    public Response parResponse

    @BeforeClass(alwaysRun = true)
    void "Initialize Test Suite"() {

        TestSuite.init()
    }

    void doConsentAuthorisation(String clientId = null) {

        AUAuthorisationBuilder authorisationBuilder

        if (clientId) {
            authorisationBuilder = new AUAuthorisationBuilder(
                    scopes, AUConstants.DEFAULT_SHARING_DURATION, true, "", clientId)
        } else {
            authorisationBuilder = new AUAuthorisationBuilder(
                    scopes, AUConstants.DEFAULT_SHARING_DURATION, true)
        }

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
                    // Consent First Account
                    WebElement accElement = driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath()))
                    consentedAccount = accElement.getAttribute("value")
                    accElement.click()
                    // Consent Second Account
                    accElement = driver.findElement(By.xpath(AUTestUtil.getAltSingleAccountXPath()))
                    secondConsentedAccount = accElement.getAttribute("value")
                    accElement.click()
                    // Submit consent
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()

                    // Extra step for OB-2.0 AU Authentication flow.
                    if (TestConstants.SOLUTION_VERSION_200.equals(ConfigParser.getInstance().getSolutionVersion())) {
                        driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                    }
                }
                .addStep(new WaitForRedirectAutomationStep())
                .execute()

        // Get Code From URL
        authorisationCode = TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())

        Assert.assertNotNull(authorisationCode)

    }

    void doConsentAuthorisationWithoutAccountSelection() {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()

                }
                .addStep(new WaitForRedirectAutomationStep())
                .execute()

        // Get Code From URL
        authorisationCode = TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())

        Assert.assertNotNull(authorisationCode)

    }

    void generateUserAccessToken() {

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode).tokens.accessToken
        Assert.assertNotNull(userAccessToken)
    }

    Response doPushAuthorisationRequest(String headerString, List<AUConstants.SCOPES> scopes, long sharingDuration,
                                        boolean sendSharingDuration, String cdrArrangementId,
                                        String clientId = AppConfigReader.getClientId()) {

        String scopeString = "openid ${String.join(" ", scopes.collect({ it.scopeString }))}"

        parResponse = TestSuite.buildRequest()
                .contentType(TestConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Basic " + Base64.encoder.encodeToString(
                        headerString.getBytes(Charset.forName("UTF-8"))))
                .formParams(TestConstants.REQUEST_KEY, AUAuthorisationBuilder.getSignedRequestObject(scopeString,
                        sharingDuration, sendSharingDuration, cdrArrangementId, AppConfigReader.getRedirectURL(), clientId).serialize())
                .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                .post(AUConstants.PAR_ENDPOINT)

        return parResponse
    }

    void doConsentAuthorisationViaRequestUri(List<AUConstants.SCOPES> scopes, URI requestUri) {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, requestUri)

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_CONFIRM_XPATH)).click()
                }
                .addStep(new WaitForRedirectAutomationStep())
                .execute()

        authorisationCode = TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())
    }

    void verifyScopes(String scopesString, List<AUConstants.SCOPES> scopes) {
        for (AUConstants.SCOPES scope : scopes) {
            Assert.assertTrue(scopesString.contains(scope.getScopeString()))
        }
    }

    Response getAccountRetrieval(String userAccessToken) {
        //Account Retrieval request
        Response response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 1)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer " + userAccessToken)
                .baseUri(ConfigParser.instance.baseUrl)
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/")
        return response
    }

    Response getConsentStatus(String headerString, String consentId) {
        return TestSuite.buildRequest()
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Basic " + Base64.encoder.encodeToString(
                        headerString.getBytes(Charset.forName("UTF-8"))))
                .baseUri(ConfigParser.instance.authorisationServerUrl)
                .get("${AUConstants.CONSENT_STATUS_ENDPOINT}${AUConstants.STATUS_PATH}?${consentId}")
    }

    Response doRevokeConsent(String clientId, String cdrArrangementId) {

        String assertionString = new AccessTokenJwtDto().getJwt(clientId)

        def bodyContent = [(TestConstants.CLIENT_ID_KEY)            : (clientId),
                           (TestConstants.CLIENT_ASSERTION_TYPE_KEY): (TestConstants.CLIENT_ASSERTION_TYPE),
                           (TestConstants.CLIENT_ASSERTION_KEY)     : assertionString,
                           "cdr_arrangement_id"                     : cdrArrangementId]

        def response = TestSuite.buildRequest()
                .contentType(TestConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .formParams(bodyContent)
                .baseUri(ConfigParser.instance.baseUrl)
                .post("${AUConstants.CDR_ARRANGEMENT_ENDPOINT}${AUConstants.REVOKE_PATH}")
        return response
    }

    String doAuthorization(List<AUConstants.SCOPES> scopes, long sharingDuration, boolean sendSharingDuration) {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(scopes, sharingDuration,
                sendSharingDuration)

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_CONFIRM_XPATH)).click()
                }
                .addStep(new WaitForRedirectAutomationStep())
                .execute()

        return TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())
    }

    void deleteApplicationIfExists(List<String> scopes, String clientId = AppConfigReader.getClientId()) {
        if (clientId) {
            String token = AURequestBuilder.getApplicationToken(scopes, clientId)

            if (token) {
                def deletionResponse = AURegistrationRequestBuilder.buildBasicRequest(token)
                        .when()
                        .delete(AUDCRConstants.REGISTRATION_ENDPOINT + clientId)
                Assert.assertEquals(deletionResponse.statusCode(), AUConstants.STATUS_CODE_204)
            }
        }
    }
}
