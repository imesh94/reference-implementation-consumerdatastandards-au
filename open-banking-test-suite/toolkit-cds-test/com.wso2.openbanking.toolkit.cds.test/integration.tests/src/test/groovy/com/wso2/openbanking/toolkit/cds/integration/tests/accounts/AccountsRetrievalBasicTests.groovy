/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.toolkit.cds.integration.tests.accounts

import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import io.restassured.http.ContentType
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Accounts Retrieval basic test scenarios to validate errors.
 */
class AccountsRetrievalBasicTests extends AbstractAUTests {

    static final String CDS_PATH = AUConstants.CDS_PATH

    @BeforeClass (alwaysRun = true)
    void "Get User Access Token"() {
        doConsentAuthorisation()
        generateUserAccessToken()
    }

    @Test
    void "OB-1258_Retrieve account list with invalid base path"() {

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer asd")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("/cds-au/v0${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)
        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_RESOURCE_NOTFOUND)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_POINTER),
                    "/cds-au/v0${AUConstants.BULK_ACCOUNT_PATH}")
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .RESOURCE_NOT_FOUND)
        } else {
            Assert.assertTrue(TestUtil.parseResponseBody(response, "fault.description").contains(
                    "No matching resource found for given API Request"))
        }
    }

    @Test
    void "OB-1259_Retrieve account list with invalid sub path"() {

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}/banking/accountz")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)
        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_RESOURCE_NOTFOUND )
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .RESOURCE_NOT_FOUND)
        } else {
            Assert.assertTrue(TestUtil.parseResponseBody(response, "fault.description").contains(
                    "No matching resource found for given API Request"))
        }
    }

    @Test
    void "OB-1260_Retrieve account list with invalid http method"() {

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .put("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_405)
    }

    @Test
    void "OB-1261_Retrieve account list without mtls client certificate"() {

        def response = TestSuite.buildBasicRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_401)
        Assert.assertTrue(TestUtil.parseResponseBody(response, "error").contains(
                    "invalid_client"))
        Assert.assertTrue(TestUtil.parseResponseBody(response, "error_description").contains(
                "Invalid mutual TLS request. Client certificate is missing"))
    }

    //Todo: enable after fixing issue https://github.com/wso2-enterprise/financial-open-banking/issues/6640
    //@Test
    void "OB-1162_Invoke bulk balances POST without request body"() {

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .contentType(ContentType.JSON)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_BALANCES))
                .post("${CDS_PATH}${AUConstants.BULK_BALANCES_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_FIELD)
        Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_POINTER),
                "/banking/accounts/balances")
        Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                .INVALID_FIELD)

    }

    //Todo: enable after fixing issue https://github.com/wso2-enterprise/financial-open-banking/issues/6639
    //@Test
    void "OB-1263_Invoke bulk balances POST with invalid request body"() {

        // sending 'accountIds' as a string instead of the mandated String array format
        String requestBody = """
            {
              "data": {
                "accountIds": "${consentedAccount}"
              },
              "meta": {}
            }
        """.stripIndent()

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_BALANCES))
                .post("${CDS_PATH}${AUConstants.BULK_BALANCES_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_FIELD)
        Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_POINTER),
                "/banking/accounts/balances")
        Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                .INVALID_FIELD)
    }
}
