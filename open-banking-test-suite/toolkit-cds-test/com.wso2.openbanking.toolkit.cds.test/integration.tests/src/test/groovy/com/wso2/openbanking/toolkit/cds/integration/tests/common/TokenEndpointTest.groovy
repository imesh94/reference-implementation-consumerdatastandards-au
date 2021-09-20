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

package com.wso2.openbanking.toolkit.cds.integration.tests.common

import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.wso2.openbanking.test.framework.automation.AUBasicAuthAutomationStep
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.test.framework.automation.WaitForRedirectAutomationStep
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUAuthorisationBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import io.restassured.response.Response
import org.openqa.selenium.By
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.testng.asserts.SoftAssert

/**
 * Token Endpoint Testing.
 */
class TokenEndpointTest {

    private List<AUConstants.SCOPES> scopes = [
            AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
            AUConstants.SCOPES.BANK_TRANSACTION_READ,
            AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ
    ]

    private final String ACCOUNTS_BASIC_OPENID_SCOPE_LIST = "bank:accounts.basic:read openid"
    private final String ACCOUNTS_BASIC_ACCOUNT_DETAIL_OPENID_SCOPE_LIST = "bank:accounts.basic:read bank:accounts.detail:read openid"

    private String authorisationCode
    private AccessTokenResponse userAccessToken

    @BeforeClass (alwaysRun = true)
    void "Initialize Test Suite"() {

        TestSuite.init()
    }

    static String doConsentAuthorization(List<AUConstants.SCOPES> scopes) {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()

                    if (TestConstants.SOLUTION_VERSION_200.equals(ConfigParser.getInstance().getSolutionVersion())) {
                        driver.findElement(By.xpath(AUConstants.CONSENT_CONFIRM_XPATH)).click()
                    }
                }
                .addStep(new WaitForRedirectAutomationStep())
                .execute()

        // Get Code From URL
        return TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())

    }

    @Test
    void "OB-1264-Invoke token endpoint for user access token without private-key JWT client authentication"() {

        authorisationCode = doConsentAuthorization(scopes)
        Assert.assertNotNull(authorisationCode)

        def errorObject = AURequestBuilder.getUserTokenErrorResponse(authorisationCode,
                AppConfigReader.getRedirectURL(), false)
        Assert.assertEquals(errorObject.toJSONObject().get("error_description"), "Client Authentication failed.")
    }

    @Test
    void "OB-1265-Invoke token endpoint for user access token without MTLS transport security"() {

        authorisationCode = doConsentAuthorization(scopes)
        Assert.assertNotNull(authorisationCode)

        def errorObject = AURequestBuilder.getUserTokenErrorResponse(authorisationCode,
                AppConfigReader.getRedirectURL(), true, false)
        Assert.assertEquals(errorObject.toJSONObject().get("error_description"), "Certificate not found in the request")
    }

    @Test
    void "OB-1266_Invoke token endpoint for user access token with a different redirect uri"() {

        authorisationCode = doConsentAuthorization(scopes)
        Assert.assertNotNull(authorisationCode)

        def errorObject = AURequestBuilder.getUserTokenErrorResponse(authorisationCode, AppConfigReader.getAlternateRedirectUri())
        Assert.assertEquals(errorObject.toJSONObject().get("error_description"), "Callback url mismatch")

    }

    @Test
    void "OB-1272_Invoke token endpoint for user access token with 'RS256' as the signature algorithm"() {

        authorisationCode = doConsentAuthorization(scopes)
        Assert.assertNotNull(authorisationCode)

        def errorObject = AURequestBuilder.getUserTokenErrorResponse(authorisationCode, AppConfigReader.getRedirectURL(),
                true, true, "RS256")
        Assert.assertEquals(errorObject.toJSONObject().get("error_description"), "Signature Algorithm not supported : RS256")

    }

    @Test
    void "OB-1273_Invoke token endpoint for user access token with 'PS512' as the signature algorithm"() {

        authorisationCode = doConsentAuthorization(scopes)
        Assert.assertNotNull(authorisationCode)

        def errorObject = AURequestBuilder.getUserTokenErrorResponse(authorisationCode, AppConfigReader.getRedirectURL(),
                true, true, "PS512")
        Assert.assertEquals(errorObject.toJSONObject().get("error_description"), "Signature Algorithm not supported : PS512")

    }

    @Test (priority = 1)
    void "OB-1267_Invoke token endpoint for user access token with a subset of authorized scopes"() {

        // scopes authorized for the consent
        scopes = [
                AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
                AUConstants.SCOPES.BANK_REGULAR_PAYMENTS_READ,
        ]
        authorisationCode = doConsentAuthorization(scopes)
        Assert.assertNotNull(authorisationCode)

        //scopes requested for the user access token
        scopes = [
                AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
        ]
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, scopes)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
        Assert.assertNotNull(userAccessToken.tokens.refreshToken)
        //Todo : enable this validation after fixing issue https://github.com/wso2-enterprise/financial-open-banking/issues/6646
        //Assert.assertEquals(userAccessToken.toJSONObject().get("scope"), ACCOUNTS_BASIC_OPENID_SCOPE_LIST)
    }

    @Test(priority = 1, dependsOnMethods = "OB-1267_Invoke token endpoint for user access token with a subset of authorized scopes")
    void "OB-1268_Invoke accounts retrieval with access token only bound to bank account basic read scopes"() {

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken.tokens.accessToken.toString(), AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertAll()
    }

    //Todo: enable after fixing issue https://github.com/wso2-enterprise/financial-open-banking/issues/6646
    //@Test (priority = 1, dependsOnMethods = "OB-1267_Invoke token endpoint for user access token with a subset of authorized scopes")
    void "OB-1269_Get Scheduled Payments with access token only bound to bank account basic read scopes"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken.tokens.accessToken.toString(), AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_SCHEDULED_PAYMENT))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_SCHEDULE_PAYMENTS_PATH}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_RESOURCE_FORBIDDEN)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_POINTER), AUConstants
                    .BULK_SCHEDULE_PAYMENTS_PATH)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .RESOURCE_FORBIDDEN)
        } else {
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, "fault.message"),
                    "The access token does not allow you to access the requested resource")
            softAssertion.assertAll()
        }
    }

    //Todo: enable after fixing issue https://github.com/wso2-enterprise/financial-open-banking/issues/6646
    //@Test
    void "OB-1270_Invoke token endpoint for user access token with a unauthorized scope"() {

        scopes = [
                AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
        ]

        authorisationCode = doConsentAuthorization(scopes)
        Assert.assertNotNull(authorisationCode)

        scopes = [
                AUConstants.SCOPES.BANK_REGULAR_PAYMENTS_READ,
        ]

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, scopes)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
        Assert.assertNotNull(userAccessToken.tokens.refreshToken)
        Assert.assertEquals(userAccessToken.toJSONObject().get("scope"), "openid")
    }

    @Test
    void "OB-1271_Invoke token endpoint for user access token with a set of authorized and unauthorized scopes"() {

        scopes = [
                AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
                AUConstants.SCOPES.BANK_ACCOUNT_DETAIL_READ
        ]

        authorisationCode = doConsentAuthorization(scopes)
        Assert.assertNotNull(authorisationCode)

        //Profile scope is additionally requested
        scopes = [
                AUConstants.SCOPES.PROFILE,
                AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
                AUConstants.SCOPES.BANK_ACCOUNT_DETAIL_READ
        ]

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, scopes)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
        Assert.assertNotNull(userAccessToken.tokens.refreshToken)
        Assert.assertNotNull(userAccessToken.getCustomParameters().get("cdr_arrangement_id"))
        Assert.assertEquals(userAccessToken.toJSONObject().get("scope"),ACCOUNTS_BASIC_ACCOUNT_DETAIL_OPENID_SCOPE_LIST)
    }

}
