/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.throttling

import com.nimbusds.oauth2.sdk.Scope
import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import java.util.concurrent.atomic.AtomicInteger

/**
 * Throttling policy test class.
 */
class ThrottlingPolicyTest extends AUTest{

    AtomicInteger sequence = new AtomicInteger(0)

    @BeforeClass(alwaysRun = true)
    void "Get User Access Token"() {
        doConsentAuthorisation()
        generateUserAccessToken()
    }

    /**
     * It may be required to increase the interval of the throttling policy upto 20 seconds to pass
     * the test depending on the performance
     */
    @Test(invocationCount = 600, threadPoolSize = 100, enabled = true)
    void "TC0306001_Throttle requests by AllConsumers policy - Unattended"() {

        def response = AURequestBuilder.buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        int currentCount =  sequence.addAndGet(1)

        if(currentCount > 500) {
            Assert.assertEquals(response.statusCode(), 429)
            Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_GENERAL_EXPECTED_ERROR)
            Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                    AUConstants.ERROR_TITLE_GENERAL_EXPECTED_ERROR)
        } else {
            Assert.assertEquals(response.statusCode(), 200)
        }
    }

    /**
     * It may be required to increase the interval of the throttling policy upto 20 seconds to pass
     * the test depending on the performance
     */
    @Test(invocationCount = 600, threadPoolSize = 100, enabled = false)
    void "TC0306002_Throttle requests by AllConsumers policy - CustomerPresent"() {

        def response = AURequestBuilder.buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        int currentCount =  sequence.addAndGet(1)

        if(currentCount > 500) {
            Assert.assertEquals(response.statusCode(), 429)
            Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_GENERAL_EXPECTED_ERROR)
            Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                    AUConstants.ERROR_TITLE_GENERAL_EXPECTED_ERROR)
        } else {
            Assert.assertEquals(response.statusCode(), 200)
        }
    }

    @Test(invocationCount = 50, threadPoolSize = 5,enabled = false)
    void "TC0306003_Throttle requests by CustomerPresent-Customer"() {

        def response = AURequestBuilder.buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        int currentCount =  sequence.addAndGet(1)

        if(currentCount > 40) {
            Assert.assertEquals(response.statusCode(), 429)
            Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_GENERAL_EXPECTED_ERROR)
            Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                    AUConstants.ERROR_TITLE_GENERAL_EXPECTED_ERROR)
        } else {
            Assert.assertEquals(response.statusCode(), 200)
        }
    }

    @Test(invocationCount = 200, threadPoolSize = 20, enabled = false)
    void "TC0306004_Throttle requests by DataRecipients policy - Unattended"() {

        def response = AURequestBuilder.buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        int currentCount =  sequence.addAndGet(1)

        if(currentCount > 150) {
            Assert.assertEquals(response.statusCode(), 429)
            Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_GENERAL_EXPECTED_ERROR)
            Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                    AUConstants.ERROR_TITLE_GENERAL_EXPECTED_ERROR)
        } else {
            Assert.assertEquals(response.statusCode(), 200)
        }
    }

    @Test(invocationCount = 200, threadPoolSize = 20, enabled = false)
    void "TC0306005_Throttle requests by DataRecipients policy - CustomerPresent"() {

        def response = AURequestBuilder.buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        int currentCount =  sequence.addAndGet(1)

        if(currentCount > 150) {
            Assert.assertEquals(response.statusCode(), 429)
            Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_GENERAL_EXPECTED_ERROR)
            Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                    AUConstants.ERROR_TITLE_GENERAL_EXPECTED_ERROR)
        } else {
            Assert.assertEquals(response.statusCode(), 200)
        }
    }

    @Test(invocationCount = 200, threadPoolSize = 5, enabled = false)
    void "TC0306006_Throttle requests by Unattended-CallsPerSession policy"() {

        def response = AURequestBuilder.buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        int currentCount =  sequence.addAndGet(1)

        if(currentCount > 150) {
            Assert.assertEquals(response.statusCode(), 429)
            Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_GENERAL_EXPECTED_ERROR)
            Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                    AUConstants.ERROR_TITLE_GENERAL_EXPECTED_ERROR)
        } else {
            Assert.assertEquals(response.statusCode(), 200)
        }
    }

    @Test(invocationCount = 40, threadPoolSize = 2, enabled = false)
    void "TC0306008_Throttle requests by Unattended-SessionCount policy"() {

        String scopeString = "openid ${String.join(" ", scopes.collect({ it.scopeString }))}"
        Scope scopeList = new Scope(scopeString)

        def response = AURequestBuilder.getTokenResponse(scopeList.toStringList(), clientId)
        int currentCount =  sequence.addAndGet(1)

        if(currentCount > 30) {
            Assert.assertEquals(response.statusCode(), 429)
            Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_GENERAL_EXPECTED_ERROR)
            Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                    AUConstants.ERROR_TITLE_GENERAL_EXPECTED_ERROR)
        } else {
            Assert.assertEquals(response.statusCode(), 200)
        }
    }

    @Test(invocationCount = 100, threadPoolSize = 10, enabled = false)
    void "TC0306007_Throttle requests by Unattended-SessionTPS policy"() {

        def response = AURequestBuilder.buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        int currentCount =  sequence.addAndGet(1)

        if(currentCount > 40) {
            Assert.assertEquals(response.statusCode(), 429)
            Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_GENERAL_EXPECTED_ERROR)
            Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                    AUConstants.ERROR_TITLE_GENERAL_EXPECTED_ERROR)
        }else {
            Assert.assertEquals(response.statusCode(), 200)
        }
    }
}
