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

package com.wso2.openbanking.toolkit.cds.integration.tests.common_apis

import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.testng.asserts.SoftAssert

import java.nio.charset.Charset

/**
 * Accounts Retrieval Tests.
 */
class CustomerDetailsRetrievalTest extends AbstractAUTests {

    static final String CDS_PATH = AUConstants.CDS_PATH
    def clientHeader = "${Base64.encoder.encodeToString(cdsClient.getBytes(Charset.defaultCharset()))}"

    @BeforeClass (alwaysRun = true)
    void "Get User Access Token"() {
        doConsentAuthorisation()
        generateUserAccessToken()
    }

    @Test (groups = "SmokeTest")
    void "TC0601001_Get Customer Request"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_CUSTOMER)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.customerUType"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.person"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.organisation"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
    }

    @Test (groups = "SmokeTest")
    void "TC0601002_Get Customer Detail"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_CUSTOMER)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.CUSTOMER_DETAILS}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.customerUType"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.person"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.organisation"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
    }

    @Test (priority = 1)
    void "TC0601021_Retrieve Customer info with a consent without common:customer.basic:read scope"() {

        scopes = [
                AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ
        ]

        doConsentAuthorisation()
        generateUserAccessToken()

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER , AUConstants.X_V_HEADER_CUSTOMER)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)
        softAssertion.assertAll()
    }

    @Test (priority = 1)
    void "TC0601022_Retrieve Customer Details with a consent without common:customer.detail:read scope"() {

        scopes = [
                AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ
        ]

        doConsentAuthorisation()
        generateUserAccessToken()

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER , AUConstants.X_V_HEADER_CUSTOMER)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.CUSTOMER_DETAILS}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)
        softAssertion.assertAll()
    }

    @Test (priority = 1)
    void "TC0601023_Retrieve Customer info with a consent with only common:customer.detail:read scope"() {

        scopes = [
                AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ
        ]

        doConsentAuthorisation()
        generateUserAccessToken()

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER , AUConstants.X_V_HEADER_CUSTOMER)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)
        softAssertion.assertAll()
    }

    @Test (priority = 1)
    void "TC0601024_Retrieve Customer info with a consent with only common:customer.basic:read scope"() {

        scopes = [
                AUConstants.SCOPES.BANK_CUSTOMER_BASIC_READ
        ]

        doConsentAuthorisation()
        generateUserAccessToken()

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER , AUConstants.X_V_HEADER_CUSTOMER)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.CUSTOMER_DETAILS}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)
        softAssertion.assertAll()
    }

    @Test (groups = "SmokeTest")
    void "TC0602001_Obtain a health check status"() {

        Response response = AURequestBuilder
                .buildBasicRequestWithoutAuthorisationHeader(AUConstants.X_V_HEADER_CUSTOMER)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DISCOVERY))
                .get("${CDS_PATH}${AUConstants.DISCOVERY_STATUS}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertEquals(TestUtil.parseResponseBody(response, "data.status"), "OK")
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.explanation"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.expectedResolutionTime"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
    }

    @Test (groups = "SmokeTest")
    void "TC0603001_Obtain a list of scheduled outages"() {

        Response response = AURequestBuilder
                .buildBasicRequestWithoutAuthorisationHeader(AUConstants.X_V_HEADER_CUSTOMER)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DISCOVERY))
                .get("${CDS_PATH}${AUConstants.DISCOVERY_OUTAGES}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.outages.outageTime"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.outages.duration"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.outages.isPartial"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
    }
}
