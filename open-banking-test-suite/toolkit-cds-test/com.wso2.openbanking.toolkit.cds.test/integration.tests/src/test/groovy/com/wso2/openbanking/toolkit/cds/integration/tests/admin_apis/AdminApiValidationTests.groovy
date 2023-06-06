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

package com.wso2.openbanking.toolkit.cds.integration.tests.admin_apis

import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.test.framework.model.AccessTokenJwtDto
import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Test Related to Admin Api
 */
class AdminApiValidationTests extends AbstractAUTests {

    static final String CDS_PATH = "/cds-au/v2"
    private String accessToken
    private String clientId
    private AccessTokenJwtDto accessTokenJWTDTO = new AccessTokenJwtDto()

    List<String> scopes = [
            AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ.getScopeString(),
            AUConstants.SCOPES.BANK_TRANSACTION_READ.getScopeString(),
            AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ.getScopeString()
    ]

    @BeforeClass(alwaysRun = true)
    void "Generate Access Token"() {
        clientId = AppConfigReader.getClientId()
    }

    @Test
    void "TC1001001_Retrieve critical update to the metadata for Accredited Data Recipients"() {

        accessToken = accessTokenJWTDTO.getJwt(AUConstants.ADMIN_API_ISSUER, AUConstants.ADMIN_API_AUDIENCE)

        String requestBody = """
            {
                "data": {
                "action": "REFRESH"
            },
                "meta": {}
            }
        """.stripIndent()

        def response = TestSuite.buildRequest()
                .header(AUConstants.CONTENT_TYPE, "application/json")
                .header(AUConstants.X_V_HEADER, 1)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${accessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ADMIN))
                .body(requestBody)
                .post("${CDS_PATH}${AUConstants.GET_META}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER), AUConstants.X_V_HEADER_METRICS)
    }

    @Test
    void "TC1002001_Retrieve operational statistics from the Data Holder"() {

        accessToken = accessTokenJWTDTO.getJwt(AUConstants.ADMIN_API_ISSUER, AUConstants.ADMIN_API_AUDIENCE)

        def response = TestSuite.buildRequest()
                .header(AUConstants.CONTENT_TYPE, "application/json")
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_METRICS)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${accessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ADMIN))
                .get("${CDS_PATH}${AUConstants.GET_STAT}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_METRICS)
    }

    //TODO: Enable the tests case after implementing the feature
    @Test(enabled = false)
    void "Meta Data"() {

        accessToken = accessTokenJWTDTO.getJwt(AUConstants.ADMIN_API_ISSUER, AUConstants.ADMIN_API_AUDIENCE)

        String requestBody = """
            {
                "data": {
                "action": "REFRESH"
            },
                "meta": {}
            }
         """.stripIndent()

        def response = TestSuite.buildRequest()
                .header(AUConstants.CONTENT_TYPE, "application/json")
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_METRICS)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${accessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ADMIN))
                .body(requestBody)
                .post("${CDS_PATH}/admin/register/metadata")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
    }

    @Test
    void "Meta Data Update with authorisation code type access token"() {

        doConsentAuthorisation()
        generateUserAccessToken()

        String requestBody = """
            {
                "data": {
                "action": "REFRESH"
            },
                "meta": {}
            }
         """.stripIndent()

        def response = TestSuite.buildRequest()
                .header(AUConstants.CONTENT_TYPE, "application/json")
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_METRICS)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${userAccessToken}")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ADMIN))
                .body(requestBody)
                .post("${CDS_PATH}/admin/register/metadata")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
    }

    @Test
    void "TC1002002_Metrics Data Current"() {

        accessToken = accessTokenJWTDTO.getJwt(AUConstants.ADMIN_API_ISSUER, AUConstants.ADMIN_API_AUDIENCE)

        def response = TestSuite.buildRequest()
                .header(AUConstants.CONTENT_TYPE, "application/json")
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_METRICS)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${accessToken}")
                .queryParam("period", "CURRENT")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ADMIN))
                .get("${CDS_PATH}/admin/metrics")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_METRICS)
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.requestTime"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.performance.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.unauthenticated.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.highPriority.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.lowPriority.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.unattended.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.largePayload.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.unauthenticated.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.highPriority.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.lowPriority.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.unattended.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.largePayload.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.sessionCount.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageTps.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.peakTps.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.errors.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.customerCount"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.recipientCount"))

        if (AUConstants.API_VERSION.equalsIgnoreCase("1.2.0")) {
            Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.rejections.currentDay"))
        } else {
            Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.rejections.authenticated.currentDay"))
            Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.rejections.unauthenticated.currentDay"))
        }
    }

    @Test
    void "TC1002003_Metrics Data Historic"() {

        accessToken = accessTokenJWTDTO.getJwt(AUConstants.ADMIN_API_ISSUER, AUConstants.ADMIN_API_AUDIENCE)

        def response = TestSuite.buildRequest()
                .header(AUConstants.CONTENT_TYPE, "application/json")
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_METRICS)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${accessToken}")
                .queryParam("period", "HISTORIC")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ADMIN))
                .get("${CDS_PATH}/admin/metrics")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_METRICS)
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.requestTime"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.performance.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.unauthenticated.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.highPriority.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.lowPriority.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.unattended.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.largePayload.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.unauthenticated.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.highPriority.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.lowPriority.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.unattended.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.largePayload.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.sessionCount.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageTps.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.peakTps.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.errors.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.customerCount"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.recipientCount"))

        if (AUConstants.API_VERSION.equalsIgnoreCase("1.2.0")) {
            Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.rejections.previousDays"))
        } else {
            Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.rejections.authenticated.previousDays"))
            Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.rejections.unauthenticated.previousDays"))
        }
    }

    @Test(groups = "SmokeTest")
    void "TC1002004_Metrics Data All"() {

        accessToken = accessTokenJWTDTO.getJwt(AUConstants.ADMIN_API_ISSUER, AUConstants.ADMIN_API_AUDIENCE)

        def response = TestSuite.buildRequest()
                .header(AUConstants.CONTENT_TYPE, "application/json")
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_METRICS)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${accessToken}")
                .queryParam("period", "ALL")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ADMIN))
                .get("${CDS_PATH}/admin/metrics")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_METRICS)
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.requestTime"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.performance.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.unauthenticated.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.highPriority.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.lowPriority.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.unattended.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.largePayload.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.unauthenticated.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.highPriority.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.lowPriority.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.unattended.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.largePayload.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.sessionCount.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageTps.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.peakTps.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.errors.currentDay"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.customerCount"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.recipientCount"))

        if (AUConstants.API_VERSION.equalsIgnoreCase("1.2.0")) {
            Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.rejections.currentDay"))
        } else {
            Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.rejections.authenticated.currentDay"))
            Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.rejections.authenticated.previousDays"))
        }

        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.performance.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.unauthenticated.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.highPriority.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.lowPriority.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.unattended.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.invocations.largePayload.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.unauthenticated.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.highPriority.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.lowPriority.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.unattended.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageResponse.largePayload.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.sessionCount.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.averageTps.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.peakTps.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.errors.previousDays"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.customerCount"))
        Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.recipientCount"))

        if (AUConstants.API_VERSION.equalsIgnoreCase("1.2.0")) {
            Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.rejections.previousDays"))
        } else {
            Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.rejections.authenticated.previousDays"))
            Assert.assertNotNull(TestUtil.parseResponseBody(response, "data.rejections.unauthenticated.previousDays"))
        }
    }
}
