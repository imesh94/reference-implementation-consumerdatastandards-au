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

package com.wso2.openbanking.toolkit.cds.integration.tests.cdr_arrangement

import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.test.framework.automation.AUBasicAuthAutomationStep
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.test.framework.automation.WaitForRedirectAutomationStep
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUAuthorisationBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import io.restassured.response.Response
import org.openqa.selenium.By
import org.testng.Assert
import org.testng.annotations.Test

import java.nio.charset.Charset

class ConcurrentConsentTest extends AbstractAUTests {

    static final String CDS_PATH = AUConstants.CDS_PATH
    def clientHeader = "${Base64.encoder.encodeToString(cdsClient.getBytes(Charset.defaultCharset()))}"
    static final String CDR_ARRANGEMENT_ENDPOINT = AUConstants.CDR_ARRANGEMENT_ENDPOINT
    def authorisationCode = ""

    private AccessTokenResponse doAuthorizationWithAccountSelection( List<AUConstants.SCOPES> scopes, String cdrArrangementId = ""){

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true,
                cdrArrangementId
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

        authorisationCode = TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())

        return AURequestBuilder.getUserToken(authorisationCode)
    }

    private AccessTokenResponse doAuthorizationWithoutAccountSelection( List<AUConstants.SCOPES> scopes, String cdrArrangementId = ""){

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true,
                cdrArrangementId
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
        }
        .addStep(new WaitForRedirectAutomationStep())
        .execute()


        authorisationCode = TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())

        return AURequestBuilder.getUserToken(authorisationCode)
    }

    @Test
    void "TC0204001_Retrieve Consumer data using tokens obtained for multiple consents"() {

        List<AUConstants.SCOPES> scopes1 = [ AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ ]
        List<AUConstants.SCOPES> scopes2 = [ AUConstants.SCOPES.BANK_PAYEES_READ ]

        def userAccessToken1 = doAuthorizationWithAccountSelection(scopes1)
        def userAccessToken2 = doAuthorizationWithoutAccountSelection(scopes2)

        Response response1 = AURequestBuilder
                .buildBasicRequest(userAccessToken1.tokens.accessToken.toString(),
                 AUConstants.CDR_ENDPOINT_VERSION)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response1.statusCode(), AUConstants.STATUS_CODE_200)

        Response response2 = AURequestBuilder
                .buildBasicRequest(userAccessToken2.tokens.accessToken.toString(),
                 AUConstants.CDR_ENDPOINT_VERSION)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PAYEES))
                .get("${CDS_PATH}${AUConstants.BULK_PAYEES}")

        Assert.assertEquals(response2.statusCode(), AUConstants.STATUS_CODE_200)
    }

    @Test
    void "TC0204002_Retrieve Consumer data using invalid tokens obtained for multiple consents"() {

        List<AUConstants.SCOPES> scopes1 = [ AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ ]
        List<AUConstants.SCOPES> scopes2 = [ AUConstants.SCOPES.BANK_PAYEES_READ ]

        def userAccessToken1 = doAuthorizationWithAccountSelection(scopes1)
        def userAccessToken2 = doAuthorizationWithoutAccountSelection(scopes2)


        Response response1 = AURequestBuilder
                .buildBasicRequest(userAccessToken2.tokens.accessToken.toString(),
                AUConstants.CDR_ENDPOINT_VERSION)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response1.statusCode(), AUConstants.STATUS_CODE_403)

        Response response2 = AURequestBuilder
                .buildBasicRequest(userAccessToken1.tokens.accessToken.toString(),
                AUConstants.CDR_ENDPOINT_VERSION)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PAYEES))
                .get("${CDS_PATH}${AUConstants.BULK_PAYEES}")

        Assert.assertEquals(response2.statusCode(), AUConstants.STATUS_CODE_403)
    }

    @Test (groups = "SmokeTest")
    void "TC0902001_Revoke consent using cdr management endpoint"() {

        List<AUConstants.SCOPES> sharingScopes = [ AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ ]

        //authorise sharing arrangement
        def userAccessToken = doAuthorizationWithAccountSelection(sharingScopes)

        //obtain cdr_arrangement_id from token response
        String cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        Assert.assertNotNull(cdrArrangementId)

        //retrieve consumer data successfully
        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken.tokens.accessToken.toString(),
                AUConstants.CDR_ENDPOINT_VERSION)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //obtain application access token to invoke cdr management endpoint
        List<String> scopes = ["openid"]
        String applicationToken = AURequestBuilder.getApplicationToken(scopes, AppConfigReader.getClientId())

        Assert.assertNotNull(applicationToken)

        //revoke sharing arrangement
        def responsemg = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.CDR_ENDPOINT_VERSION)

                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${applicationToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CDR_ARRANGEMENT))
                .delete("${CDR_ARRANGEMENT_ENDPOINT}/${cdrArrangementId}")

        Assert.assertEquals(responsemg.statusCode(), AUConstants.STATUS_CODE_204)

        Thread.sleep(120000)

        //try to retrieve consumer data after revocation
        response = AURequestBuilder
                .buildBasicRequest(userAccessToken.tokens.accessToken.toString(),
                AUConstants.CDR_ENDPOINT_VERSION)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)

        //validate token
        response = AURequestBuilder
                    .buildIntrospectionRequest(userAccessToken.tokens.accessToken.toString())
                    .post(AUConstants.INTROSPECTION_ENDPOINT)

        Assert.assertTrue((response.jsonPath().get("active")).equals(false))
    }

    @Test
    void "TC0203010_Generate User access token by revoked consent"() {

        List<AUConstants.SCOPES> sharingScopes = [ AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ ]

        //authorise sharing arrangement
        def userAccessToken = doAuthorizationWithAccountSelection(sharingScopes)

        //obtain cdr_arrangement_id from token response
        String cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        Assert.assertNotNull(cdrArrangementId)

        //retrieve consumer data successfully
        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken.tokens.accessToken.toString(),
                AUConstants.CDR_ENDPOINT_VERSION)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //obtain application access token to invoke cdr arrangement endpoint
        List<String> scopes = ["openid"]
        String applicationToken = AURequestBuilder.getApplicationToken(scopes, AppConfigReader.getClientId())

        Assert.assertNotNull(applicationToken)

        //revoke sharing arrangement
        response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.CDR_ENDPOINT_VERSION)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${applicationToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CDR_ARRANGEMENT))
                .post("${CDR_ARRANGEMENT_ENDPOINT}${AUConstants.REVOKE_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_204)

        //generate user access token
        def errorObject = AURequestBuilder.getUserTokenErrorResponse(authorisationCode)

        Assert.assertEquals(errorObject.toJSONObject().get("error_description"), "Inactive authorization code received from token request")
    }

    //When cdr_arrangement_id is sent as a claim in the request object, the corresponding consent should get revoked
    //upon staging new consent
    @Test
    void "TC0204003_Revoke consent using upon staging of a new consent"() {
        List<AUConstants.SCOPES> sharingScopes1 = [ AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ ]
        List<AUConstants.SCOPES> sharingScopes2 = [ AUConstants.SCOPES.BANK_PAYEES_READ ]

        //authorise the first sharing arrangement
        def userAccessToken1 = doAuthorizationWithAccountSelection(sharingScopes1)

        //obtain cdr_arrangement_id from token response
        String cdrArrangementId1 = userAccessToken1.getCustomParameters().get("cdr_arrangement_id")
        Assert.assertNotNull(cdrArrangementId1)

        //authorize the second sharing arrangement
        def userAccessToken2 = doAuthorizationWithoutAccountSelection(sharingScopes2, cdrArrangementId1)
        String cdrArrangementId2 = userAccessToken2.getCustomParameters().get("cdr_arrangement_id")
        Assert.assertNotNull(cdrArrangementId2)

        Thread.sleep(2000)

        //validate first token
        def response = AURequestBuilder
                .buildIntrospectionRequest(userAccessToken1.tokens.accessToken.toString())
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        Assert.assertTrue(response.jsonPath().get("active").toString().contains("false"))

        //validate second token
        response = AURequestBuilder
                .buildIntrospectionRequest(userAccessToken2.tokens.accessToken.toString())
                .post(AUConstants.INTROSPECTION_ENDPOINT)

        Assert.assertTrue(response.jsonPath().get("active").toString().contains("true"))
        Assert.assertEquals(response.jsonPath().get("cdr_arrangement_id").toString(), cdrArrangementId2)
    }
}