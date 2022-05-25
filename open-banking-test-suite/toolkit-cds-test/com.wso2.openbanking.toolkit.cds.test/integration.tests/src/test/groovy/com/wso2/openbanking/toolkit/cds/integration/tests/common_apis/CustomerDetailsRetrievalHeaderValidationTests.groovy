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
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.test.framework.util.AppConfigReader
import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.nio.charset.Charset

/**
 * Accounts Retrieval Tests.
 */
class CustomerDetailsRetrievalHeaderValidationTests extends AbstractAUTests {

    static final String CDS_PATH = AUConstants.CDS_PATH
    def clientHeader = "${Base64.encoder.encodeToString(cdsClient.getBytes(Charset.defaultCharset()))}"

    @BeforeClass (alwaysRun = true)
    void "Get User Access Token"() {
        doConsentAuthorisation()
        generateUserAccessToken()
    }

    @Test
    void "TC0601003_Retrieve Customer info without x-v Header"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_MIN_HEADER , AUConstants.X_V_HEADER_CUSTOMER)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                AUConstants.ERROR_X_V_MISSING)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_MISSING_HEADER)
           // Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                  //  .PARAM_X_V)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .MISSING_HEADER)
        }
    }

    @Test
    void "TC0601004_Retrieve Customer info without x-min-v Header"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_CUSTOMER)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.customerUType"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.person"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.organisation"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
    }

    @Test
    void "TC0601005_Retrieve Customer info with negative x-v Header"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, -1)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_VERSION)
           //Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    //.PARAM_X_V)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_VERSION)
        } else {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                    AUConstants.ERROR_X_V_INVALID)
        }
    }

    @Test
    void "TC0601006_Retrieve Customer info with decimal x-v Header"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 1.2)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_VERSION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_VERSION)
        } else {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                    AUConstants.ERROR_X_V_INVALID)
        }
    }

    @Test
    void "TC0601007_Retrieve Customer info with zero value as x-v Header"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 0)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_VERSION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_VERSION)
        } else {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                    AUConstants.ERROR_X_V_INVALID)
        }
    }

    @Test
    void "TC0601008_Retrieve Customer info with negative x-min-v Header"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_CUSTOMER)
                .header(AUConstants.X_MIN_HEADER, -1)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_VERSION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_VERSION)
        } else {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                    AUConstants.ERROR_X_V_INVALID)
        }
    }

    @Test
    void "TC0601009_Retrieve Customer info with decimal x-min-v Header"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_CUSTOMER)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(AUConstants.X_MIN_HEADER, 1.2)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_VERSION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_VERSION)
        } else {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                    AUConstants.ERROR_X_V_INVALID)
        }
    }

    @Test
    void "TC0601010_Retrieve Customer info with header x-min-v greater than the x-v"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_CUSTOMER)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(AUConstants.X_MIN_HEADER, 3)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.customerUType"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.person"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.organisation"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
    }

    @Test
    void "TC0601011_Retrieve Customer info with header x-min-v equals to the x-v"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_CUSTOMER)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(AUConstants.X_MIN_HEADER, AUConstants.X_V_HEADER_CUSTOMER)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.customerUType"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.person"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.organisation"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
    }

    @Test
    void "TC0601012_Retrieve Customer info with header x-min-v less than the x-v"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 3)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(AUConstants.X_MIN_HEADER, 1)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.customerUType"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.person"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.organisation"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
    }

    @Test
    void "TC0601013_Retrieve Customer info with unsupported endpoint version"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 5)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(AUConstants.X_MIN_HEADER, 4)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_UNSUPPORTED_VERSION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .UNSUPPORTED_VERSION)
        }
    }

    @Test
    void "TC0601014_Retrieve Customer info with unsupported endpoint version with holder identifier header"() {

        def holderID = "ABC-Bank"

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 5)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header("x-${holderID}-v", 4)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_UNSUPPORTED_VERSION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .UNSUPPORTED_VERSION)
        }
    }

    // need to configure the holder_identifier in AM
    @Test
    void "TC0601015_Retrieve Customer info with supported endpoint version with holder identifier header"() {

        def holderID = "ABC-Bank"

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 3)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header("x-${holderID}-v", 1)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.customerUType"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.person"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.organisation"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
    }

    @Test
    void "TC0601016_Retrieve Customer info with optional headers"() {

        def cdsClient = "${AppConfigReader.getClientId()}:${AppConfigReader.getClientSecret()}"
        def clientHeader = "${Base64.encoder.encodeToString(cdsClient.getBytes(Charset.defaultCharset()))}"

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, 3)
                .header(AUConstants.X_MIN_HEADER, 1)
                .header(AUConstants.X_FAPI_INTERACTION_ID, AUConstants.UUID)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")


        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.customerUType"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.person"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.organisation"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
    }

    @Test
    void "TC0601017_Retrieve Customer info with invalid x-fapi-interaction-id"() {

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_CUSTOMER)
                .header(AUConstants.X_FAPI_INTERACTION_ID, "obc")
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_HEADER)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    .PARAM_FAPI_INTERACTION_ID)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_HEADER)
        }
    }

    // Need to update the swagger with regex pattern for x-fapi-auth-date
    @Test
    void "TC0601018_Retrieve Customer info with invalid x-fapi-auth-date"() {

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_CUSTOMER)
                .header(AUConstants.X_FAPI_AUTH_DATE, "Sep 14")
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_HEADER)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_HEADER)
        }
    }

    // Need to update the swagger with regex pattern for x-fapi-customer-ip-address
    @Test
    void "TC0601019_Retrieve Customer info with invalid x-fapi-customer-ip-address"() {

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_CUSTOMER)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS, "123")
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_HEADER)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_HEADER)
        }
    }

    @Test
    void "TC0601020_Retrieve Customer info with invalid x-cds-client-headers"() {

        def cdsClient = "${AppConfigReader.getClientId()}:${AppConfigReader.getClientSecret()}"

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_CUSTOMER)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , cdsClient)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_HEADER)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_HEADER)
        }
    }

    @Test
    void "TC0602002_Get status with authorisation header"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_CUSTOMER)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DISCOVERY))
                .get("${CDS_PATH}${AUConstants.DISCOVERY_STATUS}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertEquals(TestUtil.parseResponseBody(response, "data.status"), "OK")
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.explanation"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.expectedResolutionTime"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
    }

    @Test
    void "TC0603002_Get Outages with authorisation header"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_CUSTOMER)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
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
