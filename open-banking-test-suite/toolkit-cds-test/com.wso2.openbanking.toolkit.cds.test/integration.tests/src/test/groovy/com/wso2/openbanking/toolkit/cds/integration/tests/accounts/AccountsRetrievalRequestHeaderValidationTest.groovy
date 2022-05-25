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

package com.wso2.openbanking.toolkit.cds.integration.tests.accounts

import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import com.wso2.openbanking.toolkit.cds.test.common.utils.AccountsDataProviders
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import java.nio.charset.Charset

/**
 * Accounts Retrieval Tests.
 */
class AccountsRetrievalRequestHeaderValidationTest extends AbstractAUTests {

    static final String CDS_PATH = AUConstants.CDS_PATH
    def clientHeader = "${Base64.encoder.encodeToString(cdsClient.getBytes(Charset.defaultCharset()))}"


    @BeforeClass (alwaysRun = true)
    void "Get User Access Token"() {
        doConsentAuthorisation()
        generateUserAccessToken()
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301004_Retrieve accounts list without x-v Header"(resourcePath) {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_MIN_HEADER , AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                AUConstants.ERROR_X_V_MISSING)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_MISSING_HEADER)

            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .MISSING_HEADER)
        }
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301005_Retrieve accounts list without x-min-v Header"(resourcePath) {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301006_Retrieve accounts list with negative x-v Header"(resourcePath) {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, -1)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${resourcePath}")

        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_VERSION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_VERSION)
        } else {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                    AUConstants.ERROR_X_MIN_V_INVALID)
        }
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301007_Retrieve accounts list with decimal x-v Header"(resourcePath) {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 1.2)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${resourcePath}")

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

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301008_Retrieve accounts list with zero value as x-v Header"(resourcePath) {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 0)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${resourcePath}")

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

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301009_Retrieve accounts list with negative x-min-v Header"(resourcePath) {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 2)
                .header(AUConstants.X_MIN_HEADER, -1)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${resourcePath}")

        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_VERSION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_VERSION)
        } else {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL), AUConstants
                    .ERROR_X_MIN_V_INVALID)
        }

    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301010_Retrieve accounts list with decimal x-min-v Header"(resourcePath) {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 2)
                .header(AUConstants.X_MIN_HEADER, 1.0)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${resourcePath}")

        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_VERSION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_VERSION)
        } else {
            Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL), AUConstants
                    .ERROR_X_MIN_V_INVALID)
        }
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301011_Retrieve accounts list with header x-min-v greater than the x-v"(resourcePath) {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_MIN_HEADER, 3)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301012_Retrieve accounts list with header x-min-v equals to the x-v"(resourcePath) {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_MIN_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301013_Retrieve accounts list with header x-min-v less than the x-v"(resourcePath) {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 3)
                .header(AUConstants.X_MIN_HEADER, 1)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301014_Retrieve accounts with unsupported endpoint version"(resourcePath) {

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 5)
                .header(AUConstants.X_MIN_HEADER , 4)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_UNSUPPORTED_VERSION)
            Assert.assertTrue(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL)
                    .contains(AUConstants.ERROR_ENDPOINT_VERSION4))
            //Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    //.PARAM_X_V)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .UNSUPPORTED_VERSION)
        }
    }

    //before executing need to configure  <HolderIdentifier>ABC-Bank</HolderIdentifier> in open-banking xml file
    //of OB_KM to the value set in {holderID} below
    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301015_Retrieve accounts with unsupported endpoint version with holder identifier header"(resourcePath) {

        def holderID = "ABC-Bank"

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, 5)
                .header("x-${holderID}-v", 4)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_406)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_UNSUPPORTED_VERSION)
            Assert.assertTrue(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL)
                    .contains(AUConstants.ERROR_ENDPOINT_VERSION5))
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .UNSUPPORTED_VERSION)
        }
    }

    @Test (dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301016_Retrieve accounts with supported endpoint version with holder identifier header"(resourcePath) {

        def holderID = "ABC-Bank"

        def response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header("x-${holderID}-v", 1)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
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
    void "TC0301017_Retrieve accounts list with optional headers"() {

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_MIN_HEADER, 1)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(AUConstants.X_FAPI_INTERACTION_ID, UUID.randomUUID())
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.accounts.accountId[0]"))

        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.accounts.accountId[1]"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
    }

    //Validate X_FAPI_INTERACTION_ID,X_FAPI_AUTH_DATE,X_FAPI_CUSTOMER_IP_ADDRESS & X_CDS_CLIENT_HEADER optional headers
    @Test (dataProvider = "AccountsRetrievalFlow", dataProviderClass = AccountsDataProviders.class)
    void "TC0301028_Retrieve account list with optional-headers"(resource) {
        def cdsClient = "${AppConfigReader.getClientId()}:${AppConfigReader.getClientSecret()}"
        def clientHeader = "${Base64.encoder.encodeToString(cdsClient.getBytes(Charset.defaultCharset()))}"
        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_INTERACTION_ID , AUConstants.UUID)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}$resource")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
    }

    @Test
    //Validate X_FAPI_INTERACTION_ID,X_FAPI_AUTH_DATE,X_FAPI_CUSTOMER_IP_ADDRESS & X_CDS_CLIENT_HEADER optional headers
    void "TC0304011_Retrieve banking products with optional-headers"(){
        def cdsClient = "${AppConfigReader.getClientId()}:${AppConfigReader.getClientSecret()}"
        def clientHeader = "${Base64.encoder.encodeToString(cdsClient.getBytes(Charset.defaultCharset()))}"
        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_PRODUCTS)
                .header(AUConstants.X_FAPI_INTERACTION_ID , AUConstants.UUID)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
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
    void "TC0301019_Retrieve account list with invalid product-category value"() {

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .queryParam("product-category", "TRANS")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_FIELD)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    .PARAM_PRODUCT_CATEGORY)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_FIELD)
        }
    }

    @Test
    void "TC0301021_Retrieve account list with open status"() {

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .queryParam("open-status", "OPEN")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(TestUtil.parseResponseBody(response, "data.accounts.openStatus[0]"),
                "OPEN")
    }

    //TODO: Git issue: https://github.com/wso2-enterprise/financial-open-banking/issues/5557
//    @Test
    void "TC0301022_Retrieve account list with close status"() {

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .queryParam("open-status", "CLOSED")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(TestUtil.parseResponseBody(response, "data.accounts.openStatus[0]"),
                "CLOSED")
    }

    //TODO: Git issue: https://github.com/wso2-enterprise/financial-open-banking/issues/5562
    @Test
    void "TC0301029_Retrieve account list with invalid x-fapi-interaction-id"() {

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .header(TestConstants.X_FAPI_INTERACTION_ID, "qaz")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_HEADER)
            //Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    //.PARAM_FAPI_INTERACTION_ID)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_HEADER)
        }
    }

    @Test
    void "TC0301032_Retrieve account list with invalid x-cds-client-headers"() {

        def cdsClient = "${AppConfigReader.getClientId()}:${AppConfigReader.getClientSecret()}"

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .header(AUConstants.X_CDS_CLIENT_HEADERS , cdsClient)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_HEADER)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    .PARAM_CDS_CLIENT_HEADER)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_HEADER)
        }
    }

    @Test
    void "TC0301037_Retrieve account list with invalid access token"() {

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer asd")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_500)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_UNAUTHORIZED)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    .PARAM_AUTHORIZATION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_AUTHORISATION)
        } else {
            Assert.assertTrue(TestUtil.parseResponseBody(response, "fault.description").contains(
                    "Invalid Credentials. Make sure you have given the correct access token") ||
                    TestUtil.parseResponseBody(response, "fault.description").contains(
                            "Invalid Credentials. Make sure you have provided the correct security credentials")
            )
        }
    }

    @Test
    void "TC0301039_Retrieve account list without access token"() {

        def response = TestSuite.buildRequest()
                .accept(AUConstants.ACCEPT)
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_401)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_UNAUTHORIZED)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_SOURCE_PARAMETER), AUConstants
                    .PARAM_AUTHORIZATION)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_AUTHORISATION)
        } else {
            Assert.assertTrue(TestUtil.parseResponseBody(response, "fault.description").contains(
                    "Missing Credentials. Make sure your API request provides required credentials") ||
                    TestUtil.parseResponseBody(response, "fault.description").contains(
                            "Invalid Credentials. Make sure your API invocation call has a header: 'Authorization : Bearer " +
                                    "ACCESS_TOKEN' or 'Authorization : Basic ACCESS_TOKEN' or 'apikey: API_KEY"))
        }
    }
}
