/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.common_api

import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AURestAsRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.nio.charset.Charset

/**
 * Class contains Customer Details Retrieval Header Validation Tests.
 */
class CustomerDetailsRetrievalHeaderValidationTests extends AUTest {

    def clientHeader = "${Base64.encoder.encodeToString(getCDSClient().getBytes(Charset.defaultCharset()))}"

    @BeforeClass(alwaysRun = true)
    void "Get User Access Token"() {
        doConsentAuthorisation()
        generateUserAccessToken()
    }

    @Test
    void "TC0601003_Retrieve Customer info without x-v Header"() {

        def response = AURestAsRequestBuilder.buildRequest()
                .header(AUConstants.X_MIN_HEADER , AUConstants.X_V_HEADER_CUSTOMER)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(AUConstants.AUTHORIZATION_HEADER_KEY, "${AUConstants.AUTHORIZATION_BEARER_TAG} ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                AUConstants.ERROR_X_V_MISSING)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_MISSING_HEADER)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants.MISSING_HEADER)
    }

    @Test
    void "TC0601004_Retrieve Customer info without x-min-v Header"() {

        def response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                AUConstants.X_V_HEADER_CUSTOMER, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_CUSTOMERUTYPE))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_PERSON))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_ORGANIZATION))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
    }

    @Test
    void "TC0601005_Retrieve Customer info with negative x-v Header"() {

        def response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                -1, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_VERSION)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants.INVALID_VERSION)
    }

    @Test
    void "TC0601006_Retrieve Customer info with decimal x-v Header"() {

        def response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                1.2, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_VERSION)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants.INVALID_VERSION)
    }

    @Test
    void "TC0601007_Retrieve Customer info with zero value as x-v Header"() {

        def response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                0, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_VERSION)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants.INVALID_VERSION)
    }

    @Test
    void "TC0601008_Retrieve Customer info with negative x-min-v Header"() {

        def response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                AUConstants.X_V_HEADER_CUSTOMER, clientHeader)
                .header(AUConstants.X_MIN_HEADER, -1)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_VERSION)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants.INVALID_VERSION)
    }

    @Test
    void "TC0601009_Retrieve Customer info with decimal x-min-v Header"() {

        def response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                AUConstants.X_V_HEADER_CUSTOMER, clientHeader)
                .header(AUConstants.X_MIN_HEADER, 1.2)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_VERSION)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants.INVALID_VERSION)
    }

    @Test
    void "TC0601010_Retrieve Customer info with header x-min-v greater than the x-v"() {

        def response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                AUConstants.X_V_HEADER_CUSTOMER, clientHeader)
                .header(AUConstants.X_MIN_HEADER, 3)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_CUSTOMERUTYPE))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_PERSON))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_ORGANIZATION))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
    }

    @Test
    void "TC0601011_Retrieve Customer info with header x-min-v equals to the x-v"() {

        def response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                AUConstants.X_V_HEADER_CUSTOMER, clientHeader)
                .header(AUConstants.X_MIN_HEADER, AUConstants.X_V_HEADER_CUSTOMER)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_CUSTOMERUTYPE))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_PERSON))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_ORGANIZATION))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
    }

    @Test
    void "TC0601012_Retrieve Customer info with header x-min-v less than the x-v"() {

        def response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                AUConstants.X_V_HEADER_CUSTOMER, clientHeader)
                .header(AUConstants.X_MIN_HEADER, 0)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_CUSTOMERUTYPE))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_PERSON))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_ORGANIZATION))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
    }

    @Test
    void "TC0601013_Retrieve Customer info with unsupported endpoint version"() {

        def response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                AUConstants.UNSUPPORTED_X_V_VERSION, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_UNSUPPORTED_VERSION)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants.UNSUPPORTED_VERSION)
    }

    @Test
    void "TC0601014_Retrieve Customer info with unsupported endpoint version with holder identifier header"() {

        def holderID = "ABC-Bank"

        def response = AURestAsRequestBuilder.buildRequest()
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header("x-${holderID}-v", 4)
                .header(AUConstants.AUTHORIZATION_HEADER_KEY, "${AUConstants.AUTHORIZATION_BEARER_TAG} ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_UNSUPPORTED_VERSION)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants.UNSUPPORTED_VERSION)
    }

    // need to configure the holder_identifier in AM deployment.toml file.
    @Test
    void "TC0601015_Retrieve Customer info with supported endpoint version with holder identifier header"() {

        def holderID = "ABC-Bank"

        def response = AURestAsRequestBuilder.buildRequest()
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header("x-${holderID}-v", AUConstants.X_V_HEADER_CUSTOMER)
                .header(AUConstants.AUTHORIZATION_HEADER_KEY,
                        "${AUConstants.AUTHORIZATION_BEARER_TAG} ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_CUSTOMERUTYPE))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_PERSON))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_ORGANIZATION))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
    }

    @Test
    void "TC0601016_Retrieve Customer info with optional headers"() {

        def response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                AUConstants.UNSUPPORTED_X_V_VERSION, clientHeader)
                .header(AUConstants.X_MIN_HEADER, AUConstants.X_V_HEADER_CUSTOMER)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")


        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_CUSTOMERUTYPE))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_PERSON))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_ORGANIZATION))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
    }

    @Test
    void "TC0601017_Retrieve Customer info with invalid x-fapi-interaction-id"() {

        def response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                AUConstants.UNSUPPORTED_X_V_VERSION, clientHeader)
                .header(AUConstants.X_FAPI_INTERACTION_ID, "obc")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_HEADER)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
        "Requested x-fapi-interaction-id header is not supported")
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants.INVALID_HEADER)
    }

    @Test
    void "TC0601018_Retrieve Customer info with invalid x-fapi-auth-date"() {

        def response = AURequestBuilder.buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_CUSTOMER)
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_FAPI_AUTH_DATE, "Sep 14")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_HEADER)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants.INVALID_HEADER)
    }

    @Test
    void "TC0601019_Retrieve Customer info with invalid x-fapi-customer-ip-address"() {

        def response = AURequestBuilder.buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_CUSTOMER)
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS, "123")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_HEADER)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants.INVALID_HEADER)
    }

    @Test
    void "TC0601020_Retrieve Customer info with invalid x-cds-client-headers"() {

        def cdsClient = "${auConfiguration.getAppInfoClientID()}:${auConfiguration.getAppInfoClientSecret()}"

        def response = AURequestBuilder.buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_CUSTOMER)
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , cdsClient)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_HEADER)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants.INVALID_HEADER)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL).contains("Schema validation " +
                "failed in the Request: ECMA 262 regex \"^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?\$\" " +
                "does not match input string"))
    }

    @Test
    void "TC0602002_Get status with authorisation header"() {

        Response response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                AUConstants.X_V_HEADER_CUSTOMER, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DISCOVERY))
                .get("${AUConstants.CDS_PATH}${AUConstants.DISCOVERY_STATUS}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertEquals(AUTestUtil.parseResponseBody(response, "data.status"), AUConstants.OK)
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.explanation"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.expectedResolutionTime"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
    }

    @Test
    void "TC0603002_Get Outages with authorisation header"() {

        Response response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                AUConstants.X_V_HEADER_CUSTOMER, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DISCOVERY))
                .get("${AUConstants.CDS_PATH}${AUConstants.DISCOVERY_OUTAGES}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.outages.outageTime"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.outages.duration"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.outages.isPartial"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.OK))
    }
}
