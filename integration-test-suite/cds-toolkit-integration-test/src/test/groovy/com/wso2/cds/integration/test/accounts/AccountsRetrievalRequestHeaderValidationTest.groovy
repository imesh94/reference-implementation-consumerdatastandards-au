/*
 * Copyright (c) 2022-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.accounts

import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.data_provider.AccountsDataProviders
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import io.restassured.http.ContentType
import org.testng.Assert
import org.testng.ITestContext
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import com.wso2.cds.test.framework.utility.AURestAsRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import org.testng.asserts.SoftAssert

import java.nio.charset.Charset

/**
 * Testcases for Account Retrieval Header Validations.
 */
class AccountsRetrievalRequestHeaderValidationTest extends AUTest {

    def clientHeader = "${Base64.encoder.encodeToString(getCDSClient().getBytes(Charset.defaultCharset()))}"

    @BeforeClass(alwaysRun = true)
    void "Get User Access Token"() {
        doConsentAuthorisation()
        generateUserAccessToken()
    }

    @Test(dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301011_Retrieve accounts list with header x-min-v greater than the x-v"(resourcePath) {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .header(AUConstants.X_MIN_HEADER, 3)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.DATA))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST))
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301004_Retrieve accounts list without x-v Header"(resourcePath) {

        def response = AURestAsRequestBuilder.buildRequest()
                .header(AUConstants.X_MIN_HEADER , AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(AUConstants.AUTHORIZATION_HEADER_KEY, "${AUConstants.AUTHORIZATION_BEARER_TAG}${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                AUConstants.ERROR_X_V_MISSING)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_MISSING_HEADER)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.MISSING_HEADER)
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301005_Retrieve accounts list without x-min-v Header"(resourcePath) {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.DATA))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response,  AUConstants.LINKS_SELF))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST))
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301006_Retrieve accounts list with negative x-v Header"(resourcePath) {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken, -1, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_VERSION)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants.INVALID_VERSION)
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301007_Retrieve accounts list with decimal x-v Header"(resourcePath) {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken, 1.2, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_VERSION)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.INVALID_VERSION)
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301008_Retrieve accounts list with zero value as x-v Header"(resourcePath) {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken, 0, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_VERSION)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.INVALID_VERSION)
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301009_Retrieve accounts list with negative x-min-v Header"(resourcePath) {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .header(AUConstants.X_MIN_HEADER, -1)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${resourcePath}")
        
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_VERSION)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.INVALID_VERSION)

    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301010_Retrieve accounts list with decimal x-min-v Header"(resourcePath) {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .header(AUConstants.X_MIN_HEADER, 1.0)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_VERSION)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.INVALID_VERSION)
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301012_Retrieve accounts list with header x-min-v equals to the x-v"(resourcePath) {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .header(AUConstants.X_MIN_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.DATA))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST))
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301013_Retrieve accounts list with header x-min-v less than the x-v"(resourcePath) {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .header(AUConstants.X_MIN_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.DATA))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST))
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301014_Retrieve accounts with unsupported endpoint version"(resourcePath) {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.UNSUPPORTED_X_V_VERSION, clientHeader)
                .header(AUConstants.X_MIN_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_UNSUPPORTED_VERSION)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL)
                    .contains(AUConstants.ERROR_ENDPOINT_VERSION4))
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.UNSUPPORTED_VERSION)
    }

    //Before executing need to configure the [open_banking_cds.headers]holder_identifier=ABC-Bank in deployment.toml file
    //in API Manager pack.
    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301015_Retrieve accounts with unsupported endpoint version with holder identifier header"(resourcePath) {

        def holderID = "ABC-Bank"

        def response = AURestAsRequestBuilder.buildRequest()
                .header("x-${holderID}-v", AUConstants.UNSUPPORTED_X_V_VERSION)
                .header(AUConstants.AUTHORIZATION_HEADER_KEY, "${AUConstants.AUTHORIZATION_BEARER_TAG}${userAccessToken}")
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_UNSUPPORTED_VERSION)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL)
                .contains(AUConstants.ERROR_ENDPOINT_VERSION5))
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.UNSUPPORTED_VERSION)
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301016_Retrieve accounts with supported endpoint version with holder identifier header"(resourcePath) {

        def holderID = "ABC-Bank"

        def response = AURestAsRequestBuilder.buildRequest()
                .header("x-${holderID}-v", AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.AUTHORIZATION_HEADER_KEY, "${AUConstants.AUTHORIZATION_BEARER_TAG}${userAccessToken}")
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.DATA))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST))
    }

    @Test (dataProvider = "AccountsRetrievalFlow", dataProviderClass = AccountsDataProviders.class)
    void "TC0301017_Retrieve accounts list with optional headers"(resourcePath) {

        def response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .header(AUConstants.X_MIN_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.DATA))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST))
    }

    @Test
    void "TC0304011_Retrieve banking products with optional-headers"(){

        def response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                AUConstants.X_V_HEADER_PRODUCTS, clientHeader)
                .accept(AUConstants.ACCEPT)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.BANKING_PRODUCT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_PRODUCTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST))
    }

    @Test
    void "TC0301019_Retrieve account list with invalid product-category value"() {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_PRODUCTS, clientHeader)
                .queryParam(AUConstants.PARAM_PRODUCT_CATEGORY, "TRANS")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_FIELD)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                .INVALID_FIELD)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL).contains(
                "Schema validation failed in the Request: Instance value (\"TRANS\") not found in enum"))
    }

    @Test
    void "TC0301021_Retrieve account list with open status"() {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .accept(AUConstants.ACCEPT)
                .queryParam(AUConstants.OPEN_STATUS_PARAM, AUConstants.STATUS_OPEN)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_OPENSTATUS),
                AUConstants.STATUS_OPEN)
    }

    //TODO: Git issue: https://github.com/wso2-enterprise/financial-open-banking/issues/5557
    //@Test
    void "TC0301022_Retrieve account list with close status"() {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .accept(AUConstants.ACCEPT)
                .queryParam(AUConstants.OPEN_STATUS_PARAM, AUConstants.STATUS_CLOSED)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_OPENSTATUS),
                AUConstants.STATUS_CLOSED)
    }

    @Test
    void "TC0301029_Retrieve account list with invalid x-fapi-interaction-id"() {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_FAPI_INTERACTION_ID, "qaz")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_HEADER)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.INVALID_HEADER)
    }

    @Test
    void "TC0301032_Retrieve account list with invalid x-cds-client-headers"() {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, getCDSClient())
                .accept(AUConstants.ACCEPT)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_HEADER)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER),
                AUConstants.PARAM_CDS_CLIENT_HEADER)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.INVALID_HEADER)
    }

    @Test
    void "TC0301037_Retrieve account list with invalid access token"() {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders("asd",
                AUConstants.X_V_HEADER_ACCOUNTS, getCDSClient())
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_401)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_UNAUTHORIZED)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.INVALID_AUTHORISATION)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                "Invalid Credentials. Make sure you have provided the correct security credentials")
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_META_URN),
                AUConstants.ERROR_CODE_GENERAL_ERROR_UNEXPECTED)
    }

    @Test
    void "TC0301039_Retrieve account list without access token"() {

        def response = AURequestBuilder.buildBasicRequestWithoutAuthorisationHeader(AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_401)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR),
                AUConstants.INVALID_CLIENT)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION),
                AUConstants.MISSING_CREDENTIALS)
    }

    @Test (enabled = true, dataProvider = "AccountsRetrievalFlow", dataProviderClass = AccountsDataProviders.class)
    void "OB-1186_Retrieve account list with invalid X_FAPI_AUTH_DATE"(resource) {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader, AUConstants.CONSENT_EXPIRE_DATE)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}$resource")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_HEADER)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                AUConstants.UNSUPPORTED_X_FAPI_AUTH_DATE)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                .INVALID_HEADER)
    }

    @Test (dataProvider = "AccountsRetrievalFlow", dataProviderClass = AccountsDataProviders.class)
    void "OB-1187_Retrieve account list with invalid X_FAPI_CUSTOMER_IP_ADDRESS"(resource) {

        def response = AURequestBuilder.buildBasicRequestWithOptionalHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader, AUConstants.DATE, "23.3.4")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}$resource")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_HEADER)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                AUConstants.UNSUPPORTED_X_FAPI_IP_ADDRESS)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                .INVALID_HEADER)
    }

    //TODO: Issue: https://github.com/wso2-enterprise/financial-open-banking/issues/7316 (Check with latest swagger)
    @Test
    void "OB-1190_Retrieve transaction list with invalid oldest-time"() {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .header(AUConstants.OLDEST_TIME, "aaa")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.GET_TRANSACTIONS}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_FIELD)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                AUConstants.OLDEST_TIME)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                .INVALID_FIELD)
    }

    //TODO: Issue: https://github.com/wso2-enterprise/financial-open-banking/issues/7316 (Check with latest swagger)
    @Test
    void "OB-1191_Retrieve transaction list with invalid newest-time"() {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .header(AUConstants.NEWEST_TIME, "2021-05-01")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.GET_TRANSACTIONS}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_FIELD)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                AUConstants.NEWEST_TIME)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                .INVALID_FIELD)
    }

    @Test
    void "OB-1316_Retrieve Direct Debits For Specific Account with invalid content type"() {

        String requestBody = """
            {
              "data": {
                "accountIds": [
                  "${AUConstants.accountID}", "${secondConsentedAccount}"
                ]
              },
              "meta": {}
            }
        """.stripIndent()

        def response = AURequestBuilder.buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .contentType(ContentType.XML)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DIRECT_DEBIT))
                .post("${AUConstants.CDS_PATH}${AUConstants.BULK_DIRECT_DEBITS_PATH}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_415)

        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.ERROR_CODE_GENERAL_EXPECTED_ERROR)
        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                AUConstants.INVALID_CONTENT_TYPE)

        softAssertion.assertAll()
    }

    @Test
    void "OB-1317_Retrieve Direct Debits For Specific Account with invalid accept header"() {

        String requestBody = """
            {
              "data": {
                "accountIds": [
                  "${AUConstants.accountID}"
                ]
              },
              "meta": {}
            }
        """.stripIndent()

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .accept(ContentType.XML)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DIRECT_DEBIT))
                .post("${AUConstants.CDS_PATH}${AUConstants.BULK_DIRECT_DEBITS_PATH}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)

        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_GENERAL_EXPECTED_ERROR)
        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                AUConstants.INVALID_ACCEPT_HEADER)
        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.ERROR_TITLE_GENERAL_EXPECTED_ERROR)

        softAssertion.assertAll()
    }
}
