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

package com.wso2.openbanking.toolkit.cds.integration.tests.throttling

import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import com.wso2.openbanking.test.framework.util.TestUtil
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.util.concurrent.atomic.AtomicInteger

/**
 * CDS Throttling Policy Tests.
 *
 * To ensure proper functioning of each throttling policy, deploy one policy at one time and run
 * the tests relevant to the deployed throttling policy.
 */
class ThrottlingPolicyTest extends AbstractAUTests {

    static final String CDS_PATH = AUConstants.CDS_PATH
    static final String clientId = AppConfigReader.getClientId()
    AtomicInteger sequence = new AtomicInteger(0)
    private List<String> scopes = [
            AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ.getScopeString(),
            AUConstants.SCOPES.BANK_TRANSACTION_READ.getScopeString(),
            AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ.getScopeString()
    ]

    @BeforeClass()
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


        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        int currentCount =  sequence.addAndGet(1)

        if(currentCount > 500) {
            Assert.assertEquals(response.statusCode(), 429)
            if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
                Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                        AUConstants.ERROR_CODE_TOO_MANY_REQUESTS)
                Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                        .MESSAGE_THROTTLED_OUT)
            } else {
                Assert.assertTrue(TestUtil.parseResponseBody(response, "fault.description").contains(
                        "You have exceeded your quota"))
            }
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

        def response = AURequestBuilder
                .buildBasicRequestWithCustomerIP(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        int currentCount =  sequence.addAndGet(1)

        if(currentCount > 500) {
            Assert.assertEquals(response.statusCode(), 429)
            Assert.assertEquals(response.statusCode(), 429)
            if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
                Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                        AUConstants.ERROR_CODE_TOO_MANY_REQUESTS)
                Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                        .MESSAGE_THROTTLED_OUT)
            } else {
                Assert.assertTrue(TestUtil.parseResponseBody(response, "fault.description").contains(
                        "You have exceeded your quota"))
            }
        } else {
            Assert.assertEquals(response.statusCode(), 200)
        }
    }

    @Test(invocationCount = 50, threadPoolSize = 5,enabled = false)
    void "TC0306003_Throttle requests by CustomerPresent-Customer"() {

        def response = AURequestBuilder
                .buildBasicRequestWithCustomerIP(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        int currentCount =  sequence.addAndGet(1)

        if(currentCount > 40) {
            Assert.assertEquals(response.statusCode(), 429)
            Assert.assertEquals(response.statusCode(), 429)
            if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
                Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                        AUConstants.ERROR_CODE_TOO_MANY_REQUESTS)
                Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                        .MESSAGE_THROTTLED_OUT)
            } else {
                Assert.assertTrue(TestUtil.parseResponseBody(response, "fault.description").contains(
                        "You have exceeded your quota"))
            }
        } else {
            Assert.assertEquals(response.statusCode(), 200)
        }
    }

    @Test(invocationCount = 200, threadPoolSize = 20, enabled = false)
    void "TC0306004_Throttle requests by DataRecipients policy - Unattended"() {

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        int currentCount =  sequence.addAndGet(1)

        if(currentCount > 150) {
            Assert.assertEquals(response.statusCode(), 429)
            Assert.assertEquals(response.statusCode(), 429)
            if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
                Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                        AUConstants.ERROR_CODE_TOO_MANY_REQUESTS)
                Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                        .MESSAGE_THROTTLED_OUT)
            } else {
                Assert.assertTrue(TestUtil.parseResponseBody(response, "fault.description").contains(
                        "You have exceeded your quota"))
            }
        } else {
            Assert.assertEquals(response.statusCode(), 200)
        }
    }

    @Test(invocationCount = 200, threadPoolSize = 20, enabled = false)
    void "TC0306005_Throttle requests by DataRecipients policy - CustomerPresent"() {

        def response = AURequestBuilder
                .buildBasicRequestWithCustomerIP(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        int currentCount =  sequence.addAndGet(1)
        if(currentCount > 150) {
            Assert.assertEquals(response.statusCode(), 429)
            Assert.assertEquals(response.statusCode(), 429)
            if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
                Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                        AUConstants.ERROR_CODE_TOO_MANY_REQUESTS)
                Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                        .MESSAGE_THROTTLED_OUT)
            } else {
                Assert.assertTrue(TestUtil.parseResponseBody(response, "fault.description").contains(
                        "You have exceeded your quota"))
            }
        } else {
            Assert.assertEquals(response.statusCode(), 200)
        }
    }

    @Test(invocationCount = 200, threadPoolSize = 5, enabled = false)
    void "TC0306006_Throttle requests by Unattended-CallsPerSession policy"() {

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        int currentCount =  sequence.addAndGet(1)

        if(currentCount > 150) {
            Assert.assertEquals(response.statusCode(), 429)
            Assert.assertEquals(response.statusCode(), 429)
            if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
                Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                        AUConstants.ERROR_CODE_TOO_MANY_REQUESTS)
                Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                        .MESSAGE_THROTTLED_OUT)
            } else {
                Assert.assertTrue(TestUtil.parseResponseBody(response, "fault.description").contains(
                        "You have exceeded your quota"))
            }
        } else {
            Assert.assertEquals(response.statusCode(), 200)
        }
    }

    @Test(invocationCount = 40, threadPoolSize = 2, enabled = false)
    void "TC0306008_Throttle requests by Unattended-SessionCount policy"() {

        def response = AURequestBuilder.getTokenResponse(scopes, clientId)
        int currentCount =  sequence.addAndGet(1)
        if(currentCount > 30) {
            Assert.assertEquals(response.statusCode(), 429)
            Assert.assertEquals(response.statusCode(), 429)
            if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
                Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                        AUConstants.ERROR_CODE_TOO_MANY_REQUESTS)
                Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                        .MESSAGE_THROTTLED_OUT)
            } else {
                Assert.assertTrue(TestUtil.parseResponseBody(response, "fault.description").contains(
                        "You have exceeded your quota"))
            }
        } else {
            Assert.assertEquals(response.statusCode(), 200)
        }
    }

    @Test(invocationCount = 100, threadPoolSize = 10, enabled = false)
    void "TC0306007_Throttle requests by Unattended-SessionTPS policy"() {

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        int currentCount =  sequence.addAndGet(1)

        if(currentCount > 40) {
            Assert.assertEquals(response.statusCode(), 429)
            Assert.assertEquals(response.statusCode(), 429)
            if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
                Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                        AUConstants.ERROR_CODE_TOO_MANY_REQUESTS)
                Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                        .MESSAGE_THROTTLED_OUT)
            } else {
                Assert.assertTrue(TestUtil.parseResponseBody(response, "fault.description").contains(
                        "You have exceeded your quota"))
            }
        }else {
            Assert.assertEquals(response.statusCode(), 200)
        }
    }
}
