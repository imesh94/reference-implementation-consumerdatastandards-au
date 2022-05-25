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
class ProductRetrievalValidationTest extends AbstractAUTests {

    static final String CDS_PATH = AUConstants.CDS_PATH
    def clientHeader = "${Base64.encoder.encodeToString(cdsClient.getBytes(Charset.defaultCharset()))}"

    @Test (priority = 1, groups = "SmokeTest")
    void "TC1101001_Retrieve banking products"() {

        Response response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_PRODUCTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        productId = TestUtil.parseResponseBody(response, "data.products.productId[0]").toString()

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.products.productId"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.products.lastUpdated"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.products.productCategory"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.products.name"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.products.description"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.products.brand"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.products.isTailored"))
        Assert.assertEquals(TestUtil.parseResponseBody(response, "data.products.effectiveFrom[0]"), "CURRENT")
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
    }

    @Test (priority = 1, dependsOnMethods = "TC1101001_Retrieve banking products")
    void "TC1101002_Retrieve specific banking product details"() {

        Response response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_PRODUCTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}/$productId")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertEquals(TestUtil.parseResponseBody(response, "data.productId"), productId)
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.bundles.name"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.bundles.description"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.features.features"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.constraints.constraintType"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.eligibility.eligibilityType"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.fees.name"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.fees.feeType"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.fees.discounts.description"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.fees.discounts.discountType"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.fees.discounts.eligibility.discountEligibilityType"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.depositRates.depositRateType"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.depositRates.rate"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.depositRates.tiers.name"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.depositRates.tiers.unitOfMeasure"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.depositRates.tiers.minimumValue"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
    }

    @Test
    void "TC1101003_Retrieve specific banking product details for invalid product id"() {

        Response response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_PRODUCTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}/12345")

        //TODO: Test is failing due to: https://github.com/wso2-enterprise/financial-open-banking/issues/5412
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)
        Assert.assertEquals(response.jsonPath().get("errors[0].code"), "0001 – Account not able to be found")
    }

    // Need to update the swaggers with maximum, minimum ranges for page-size
    @Test
    void "TC1101019_Retrieve banking products with page size greater than the maximum standard pagination"() {

        Response response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_PRODUCTS)
                .queryParam("page-size", 200000)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_422)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_PAGE_SIZE_TOO_LARGE)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    .PARAM_PAGE_SIZE)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .PAGE_SIZE_EXCEEDED)
        }
    }

    @Test
    void "TC1101020_Retrieve banking products with filters"() {

        String queryParams = "?effectiveFrom=CURRENT&effectiveTo=FUTURE&brand=TEST&product-category=TRANS_AND_SAVINGS_ACCOUNTS&page=2&" +
                "page-size=25&updated-since=2019-12-25T15:43:00-08:00"
        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_PRODUCTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}${queryParams}")

        productId = TestUtil.parseResponseBody(response, "data.products.productId[0]").toString()

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertEquals(TestUtil.parseResponseBody(response, "data.products.effectiveFrom[0]"), "CURRENT")
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.products.productId"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
    }

    //TODO: git issue https://github.com/wso2-enterprise/financial-open-banking/issues/5561
   // @Test
    void "TC1101021_Retrieve banking products with invalid updated-since value"() {

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_PRODUCTS)
                .queryParam("updated-since", "October")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)
    }

    //TODO: Git issue : https://github.com/wso2-enterprise/financial-open-banking/issues/5638
   // @Test
    void "TC1101024_Retrieve banking products with invalid brand value"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_PRODUCTS)
                .queryParam("brand", 123)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)
    }

    //TODO: Git issue https://github.com/wso2-enterprise/financial-open-banking/issues/5560
  //  @Test
    void "TC1101027_Retrieve Product list with undefined query parameter"() {

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_PRODUCTS)
                .queryParam("open-status", "OPEN")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)
    }

    //TODO: Git Issue: https://github.com/wso2-enterprise/financial-open-banking/issues/5557
//    @Test
    void "TC1101029_Retrieve banking products with effectiveFROM value and effectiveTO value equal to ALL"() {

        String queryParams = "?effectiveFrom=ALL&effectiveTo=ALL"

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_PRODUCTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}${queryParams}")

        productId = TestUtil.parseResponseBody(response, "data.products.productId[0]").toString()

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.products.productId"))
        Assert.assertEquals(TestUtil.parseResponseBody(response, "data.products.effectiveFrom[0]"), "ALL")
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
    }

    @Test
    void "TC1101031_Retrieve product list with invalid product-category value"() {

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_PRODUCTS)
                .queryParam("product-category", "TRANS")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PRODUCTS))
                .get("${CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_FIELD)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    .PARAM_PRODUCT_CATEGORY)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_FIELD)
        }
    }
}
