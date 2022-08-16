/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.cds.integration.test

import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.data_provider.AccountsDataProviders
import org.testng.Assert
import org.testng.ITestContext
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import com.wso2.cds.test.framework.utility.AURestAsRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil

import java.nio.charset.Charset

/**
 * for testing account retrieval header
 */
class AccountsRetrievalRequestHeaderValidationTest extends AUTest {

    def clientHeader = "${Base64.encoder.encodeToString(getCDSClient().getBytes(Charset.defaultCharset()))}"

    @BeforeClass(alwaysRun = true)
    void "Get User Access Token"(ITestContext context) {
        getUserAccessToken(context)
    }

    @Test(dataProvider = "BankingApis", dataProviderClass = AccountsDataProviders.class)
    void "TC0301011_Retrieve accounts list with header x-min-v greater than the x-v"(resourcePath) {

        def response = AURestAsRequestBuilder.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_MIN_HEADER, 3)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS, AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS, clientHeader)
                .header(AUConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "links.self"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "links.first"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "links.prev"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "links.next"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "links.last"))
    }

}

