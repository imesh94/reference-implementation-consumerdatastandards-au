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

package com.wso2.openbanking.toolkit.cds.integration.tests.banking_products

import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.Test

import java.nio.charset.Charset

/**
 * Accounts Retrieval Tests.
 */
class ProductRetrievalHeaderValidationTest extends AbstractAUTests {

    static final String CDS_PATH = AUConstants.CDS_PATH

    @Test
    void "TC1101004_Retrieve banking products with unsupported x-v header"() {

        Response response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 3)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        //RG: 1 check in MG
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_UNSUPPORTED_VERSION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    .PARAM_X_V)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .UNSUPPORTED_VERSION)
        }
    }

    @Test
    void "TC1101005_Retrieve banking products with authentication"() {

        doConsentAuthorisation()
        generateUserAccessToken()

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_PRODUCTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.products.productId"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.products.additionalInformation"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
    }

    @Test
    void "TC1101018_Retrieve banking products with supported endpoint version with holder identifier header"() {

        def holderID = "ABCBank"

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_PRODUCTS)
                .header("x-${holderID}-v", 1)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.products.productId[0]"))

        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
    }

    @Test
    //Validate X_FAPI_INTERACTION_ID,X_FAPI_AUTH_DATE,X_FAPI_CUSTOMER_IP_ADDRESS & X_CDS_CLIENT_HEADER optional headers
    void "TC0701026_Retrieve banking products with optional-headers"(){

        def cdsClient = "${AppConfigReader.getClientId()}:${AppConfigReader.getClientSecret()}"
        def clientHeader = "${Base64.encoder.encodeToString(cdsClient.getBytes(Charset.defaultCharset()))}"
        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_PRODUCTS)
                .header(AUConstants.X_FAPI_INTERACTION_ID , AUConstants.UUID)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
    }

    @Test
    void "TC1101006_Retrieve Product list without x-v Header"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_MIN_HEADER , AUConstants.X_V_HEADER_PRODUCTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_MISSING_HEADER)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    .PARAM_X_V)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .MISSING_HEADER)
        }
    }

    @Test
    void "TC1101007_Retrieve Product list without x-min-v Header"() {

       def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_PRODUCTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
    }

    @Test
    void "TC1101008_Retrieve Product list with negative x-v Header"() {

       def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, -2)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_422)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_VERSION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    .PARAM_X_V)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_VERSION)
        } else {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        }
    }

    @Test
    void "TC1101009_Retrieve Product list with decimal x-v Header"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 1.2)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_422)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_VERSION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    .PARAM_X_V)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_VERSION)
        } else {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        }
    }

    @Test
    void "TC1101010_Retrieve Product list with zero value as x-v Header"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 0)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_422)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_VERSION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    .PARAM_X_V)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_VERSION)
        } else {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        }
    }

    @Test
    void "TC1101011_Retrieve Product list with negative x-min-v Header"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_PRODUCTS)
                .header(AUConstants.X_MIN_HEADER, -1)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_422)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_VERSION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    .PARAM_X_MIN_V)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_VERSION)
        } else {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        }
    }

    @Test
    void "TC1101012_Retrieve Product list with decimal x-min-v Header"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_PRODUCTS)
                .header(AUConstants.X_MIN_HEADER, 1.0)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_422)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_VERSION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    .PARAM_X_MIN_V)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_VERSION)
        } else {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        }
    }

    @Test
    void "TC1101013_Retrieve Product list with header x-min-v greater than the x-v"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_PRODUCTS)
                .header(AUConstants.X_MIN_HEADER, 4)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
    }

    @Test
    void "TC1101014_Retrieve Product list with header x-min-v equals to the x-v"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_PRODUCTS)
                .header(AUConstants.X_MIN_HEADER, AUConstants.X_V_HEADER_PRODUCTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
    }

    @Test
    void "TC1101015_Retrieve Product list with header x-min-v less than the x-v"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_PRODUCTS)
                .header(AUConstants.X_MIN_HEADER, 1)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
    }

    @Test
    void "TC1101016_Retrieve Product with unsupported endpoint version"() {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 5)
                .header(AUConstants.X_MIN_HEADER , 4)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_UNSUPPORTED_VERSION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    .PARAM_X_V)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .UNSUPPORTED_VERSION)
        }
    }

    //before executing need to configure  <HolderIdentifier>ABC-Bank</HolderIdentifier> in open-banking xml file
    //of OB_KM to the value set in {holderID} below
    @Test
    void "TC1101017_Retrieve Product with unsupported endpoint version with holder identifier header"() {

        def holderID = "ABC-Bank"

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 5)
                .header("x-${holderID}-v", 4)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_UNSUPPORTED_VERSION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    .PARAM_X_V)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .UNSUPPORTED_VERSION)
        }
    }
}
