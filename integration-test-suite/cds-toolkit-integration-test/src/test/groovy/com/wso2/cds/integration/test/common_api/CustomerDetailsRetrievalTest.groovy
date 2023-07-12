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
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.testng.asserts.SoftAssert

import java.nio.charset.Charset

/**
 * Class contains Customer Details Retrieval Validation Tests.
 */
class CustomerDetailsRetrievalTest extends AUTest {

    def clientHeader = "${Base64.encoder.encodeToString(getCDSClient().getBytes(Charset.defaultCharset()))}"

    @BeforeClass(alwaysRun = true)
    void "Get User Access Token"() {
        doConsentAuthorisation()
        generateUserAccessToken()
    }

    @Test (groups = "SmokeTest")
    void "TC0601001_Get Customer Request"() {

        Response response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                AUConstants.X_V_HEADER_CUSTOMER, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.BULK_CUSTOMER}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_CUSTOMERUTYPE))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_PERSON))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_ORGANIZATION))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
    }

    @Test (groups = "SmokeTest")
    void "TC0601002_Get Customer Detail"() {

        Response response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                AUConstants.X_V_HEADER_CUSTOMER_DETAIL, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CUSTOMER_DETAILS}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER_DETAIL)

        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_CUSTOMERUTYPE))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_PERSON))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_ORGANIZATION))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
    }

    @Test (priority = 1)
    void "TC0601021_Retrieve Customer info with a consent without common customer basic read scope"() {

        scopes = [
                AUAccountScope.BANK_ACCOUNT_BASIC_READ
        ]

        doConsentAuthorisation()
        generateUserAccessToken()

        def response = AURequestBuilder.buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_CUSTOMER)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.BULK_CUSTOMER}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)
        softAssertion.assertAll()
    }

    @Test (priority = 1)
    void "TC0601022_Retrieve Customer Details with a consent without common customer detail read scope"() {

        scopes = [
                AUAccountScope.BANK_ACCOUNT_BASIC_READ
        ]

        doConsentAuthorisation()
        generateUserAccessToken()

        def response = AURequestBuilder.buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_CUSTOMER)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CUSTOMER_DETAILS}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)
        softAssertion.assertAll()
    }

    @Test (priority = 1)
    void "TC0601023_Retrieve Customer info with a consent with only common customer detail read scope"() {

        scopes = [
                AUAccountScope.BANK_CUSTOMER_DETAIL_READ
        ]

        doConsentAuthorisationWithoutAccountSelection()
        generateUserAccessToken()

        def response = AURequestBuilder.buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_CUSTOMER)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.BULK_CUSTOMER}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)
        softAssertion.assertAll()
    }

    @Test (priority = 1)
    void "TC0601024_Retrieve Customer info with a consent with only common customer basic read scope"() {

        scopes = [
                AUAccountScope.BANK_CUSTOMER_BASIC_READ
        ]

        doConsentAuthorisationWithoutAccountSelection()
        generateUserAccessToken()

        def response = AURequestBuilder.buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_CUSTOMER_DETAIL)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CUSTOMER))
                .get("${AUConstants.CUSTOMER_DETAILS}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER_DETAIL)
        softAssertion.assertAll()
    }

    @Test (groups = "SmokeTest")
    void "TC0602001_Obtain a health check status"() {

        def response = AURequestBuilder.buildBasicRequestWithoutAuthorisationHeader(AUConstants.X_V_HEADER_STATUS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DISCOVERY))
                .get("${AUConstants.DISCOVERY_STATUS}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_STATUS)

        Assert.assertEquals(AUTestUtil.parseResponseBody(response, "data.status"), "OK")
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.explanation"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.expectedResolutionTime"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
    }

    @Test (groups = "SmokeTest")
    void "TC0603001_Obtain a list of scheduled outages"() {

        Response response = AURequestBuilder.buildBasicRequestWithoutAuthorisationHeader(AUConstants.X_V_HEADER_OUTAGES)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DISCOVERY))
                .get("${AUConstants.DISCOVERY_OUTAGES}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_CUSTOMER)

        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.outages.outageTime"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.outages.duration"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.outages.isPartial"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
    }
}
