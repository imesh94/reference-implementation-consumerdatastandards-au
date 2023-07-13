/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.admin_api

import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.request_builder.AUJWTGenerator
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import org.testng.Assert
import org.testng.annotations.Test

/**
 * Admin API Validation Tests.
 */
class AdminApiValidationTests extends AUTest {

    AUJWTGenerator generator = new AUJWTGenerator()

    @Test
    void "TC1001001_Retrieve critical update to the metadata for Accredited Data Recipients"() {

        String assertionString = generator.getClientAssertionJwt(AUConstants.ADMIN_API_ISSUER, AUConstants.ADMIN_API_AUDIENCE)

        String requestBody = """
            {
                "data": {
                "action": "REFRESH"
            },
                "meta": {}
            }
        """.stripIndent()

        def response = AURequestBuilder.buildBasicRequest(assertionString, AUConstants.X_V_HEADER_METADATA)
                .header(AUConstants.CONTENT_TYPE, "application/json")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ADMIN))
                .body(requestBody)
                .post("${AUConstants.CDS_ADMIN_PATH}${AUConstants.GET_META}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER), AUConstants.X_V_HEADER_METRICS)
    }

    @Test
    void "TC1002001_Retrieve operational statistics from the Data Holder"() {

        String assertionString = generator.getClientAssertionJwt(AUConstants.ADMIN_API_ISSUER, AUConstants.ADMIN_API_AUDIENCE)

        def response = AURequestBuilder.buildBasicRequest(assertionString, AUConstants.X_V_HEADER_METRICS)
                .header(AUConstants.CONTENT_TYPE, "application/json")
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ADMIN))
                .get("${AUConstants.CDS_ADMIN_PATH}${AUConstants.GET_STAT}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_METRICS)
    }

    @Test
    void "Meta Data"() {

        String assertionString = generator.getClientAssertionJwt(AUConstants.ADMIN_API_ISSUER, AUConstants.ADMIN_API_AUDIENCE)

        String requestBody = """
            {
                "data": {
                "action": "REFRESH"
            },
                "meta": {}
            }
         """.stripIndent()

        def response = AURequestBuilder.buildBasicRequest(assertionString, AUConstants.X_V_HEADER_METADATA)
                .header(AUConstants.CONTENT_TYPE, "application/json")
                .header(AUConstants.X_MIN_HEADER, AUConstants.X_V_MIN_HEADER_METRICS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ADMIN))
                .body(requestBody)
                .post("${AUConstants.CDS_ADMIN_PATH}${AUConstants.GET_META}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
    }

    @Test
    void "Meta Data Update with authorisation code type access token"() {

        doConsentAuthorisation()
        String assertionString = generator.getClientAssertionJwt(AUConstants.ADMIN_API_ISSUER, AUConstants.ADMIN_API_AUDIENCE)

        String requestBody = """
            {
                "data": {
                "action": "REFRESH"
            },
                "meta": {}
            }
         """.stripIndent()

        def response = AURequestBuilder.buildBasicRequest(assertionString, AUConstants.X_V_HEADER_METADATA)
                .header(AUConstants.CONTENT_TYPE, "application/json")
                .header(AUConstants.X_MIN_HEADER, AUConstants.X_V_MIN_HEADER_METRICS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ADMIN))
                .body(requestBody)
                .post("${AUConstants.CDS_ADMIN_PATH}${AUConstants.GET_META}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
    }

    @Test
    void "TC1002002_Metrics Data Current"() {

        String assertionString = generator.getClientAssertionJwt(AUConstants.ADMIN_API_ISSUER, AUConstants.ADMIN_API_AUDIENCE)

        def response = AURequestBuilder.buildBasicRequest(assertionString, AUConstants.X_V_HEADER_METRICS)
                .header(AUConstants.CONTENT_TYPE, "application/json")
                .header(AUConstants.X_MIN_HEADER, AUConstants.X_V_MIN_HEADER_METRICS)
                .queryParam(AUConstants.PERIOD, AUConstants.CURRENT)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ADMIN))
                .get("${AUConstants.CDS_ADMIN_PATH}${AUConstants.ADMIN_METRICS}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_METRICS)
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.requestTime"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.performance.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.unauthenticated.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.highPriority.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.lowPriority.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.unattended.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.largePayload.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.unauthenticated.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.highPriority.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.lowPriority.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.unattended.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.largePayload.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.sessionCount.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageTps.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.peakTps.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.errors.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.customerCount"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.recipientCount"))

        if (AUConstants.API_VERSION.equalsIgnoreCase("1.2.0")) {
            Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.rejections.currentDay"))
        } else {
            Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.rejections.authenticated.currentDay"))
            Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.rejections.unauthenticated.currentDay"))
        }
    }

    @Test
    void "TC1002003_Metrics Data Historic"() {

        String assertionString = generator.getClientAssertionJwt(AUConstants.ADMIN_API_ISSUER, AUConstants.ADMIN_API_AUDIENCE)

        def response = AURequestBuilder.buildBasicRequest(assertionString, AUConstants.X_V_HEADER_METRICS)
                .header(AUConstants.CONTENT_TYPE, "application/json")
                .header(AUConstants.X_MIN_HEADER, AUConstants.X_V_MIN_HEADER_METRICS)
                .queryParam(AUConstants.PERIOD, AUConstants.HISTORIC)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ADMIN))
                .get("${AUConstants.CDS_ADMIN_PATH}${AUConstants.ADMIN_METRICS}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_METRICS)
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.requestTime"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.performance.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.unauthenticated.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.highPriority.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.lowPriority.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.unattended.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.largePayload.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.unauthenticated.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.highPriority.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.lowPriority.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.unattended.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.largePayload.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.sessionCount.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageTps.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.peakTps.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.errors.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.customerCount"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.recipientCount"))

        if (AUConstants.API_VERSION.equalsIgnoreCase("1.2.0")) {
            Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.rejections.previousDays"))
        } else {
            Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.rejections.authenticated.previousDays"))
            Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.rejections.unauthenticated.previousDays"))
        }
    }

    @Test (groups = "SmokeTest")
    void "TC1002004_Metrics Data All"() {

        String assertionString = generator.getClientAssertionJwt(AUConstants.ADMIN_API_ISSUER, AUConstants.ADMIN_API_AUDIENCE)

        def response = AURequestBuilder.buildBasicRequest(assertionString, AUConstants.X_V_HEADER_METRICS)
                .header(AUConstants.CONTENT_TYPE, "application/json")
                .header(AUConstants.X_MIN_HEADER, AUConstants.X_V_MIN_HEADER_METRICS)
                .queryParam(AUConstants.PERIOD, AUConstants.ALL)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ADMIN))
                .get("${AUConstants.CDS_ADMIN_PATH}${AUConstants.ADMIN_METRICS}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_METRICS)
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.requestTime"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.performance.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.unauthenticated.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.highPriority.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.lowPriority.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.unattended.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.largePayload.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.unauthenticated.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.highPriority.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.lowPriority.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.unattended.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.largePayload.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.sessionCount.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageTps.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.peakTps.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.errors.currentDay"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.customerCount"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.recipientCount"))

        if (AUConstants.API_VERSION.equalsIgnoreCase("1.2.0")) {
            Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.rejections.currentDay"))
        } else {
            Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.rejections.authenticated.currentDay"))
            Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.rejections.authenticated.previousDays"))
        }

        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.performance.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.unauthenticated.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.highPriority.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.lowPriority.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.unattended.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.invocations.largePayload.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.unauthenticated.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.highPriority.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.lowPriority.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.unattended.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageResponse.largePayload.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.sessionCount.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.averageTps.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.peakTps.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.errors.previousDays"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.customerCount"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.recipientCount"))

        if (AUConstants.API_VERSION.equalsIgnoreCase("1.2.0")) {
            Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.rejections.previousDays"))
        } else {
            Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.rejections.authenticated.previousDays"))
            Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "data.rejections.unauthenticated.previousDays"))
        }
    }
}
