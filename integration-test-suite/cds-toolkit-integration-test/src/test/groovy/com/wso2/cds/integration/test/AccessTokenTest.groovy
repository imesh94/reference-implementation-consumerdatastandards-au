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

import com.wso2.cds.test.framework.AUTest
import org.testng.ITestContext
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.request_builder.AUAuthorisationBuilder
import com.wso2.cds.test.framework.utility.AUMockCDRIntegrationUtil
import com.wso2.cds.test.framework.constant.ContextConstants
import org.checkerframework.checker.units.qual.A
import org.testng.Assert
import org.testng.annotations.AfterClass
import com.wso2.cds.test.framework.request_builder.AURegistrationRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil

/**
 * for testing User access token with the test context
 * new User access token will be generated if there is no already generated user access token
 */
class AccessTokenTest extends AUTest {

    @BeforeClass(alwaysRun = true)
    void "Initialize Test Suite"(ITestContext context) {
        getUserAccessToken(context)
    }

    @Test(priority = 1, groups = "consent")
    void "Account ID Second"() {
        System.out.println("New Test Case")
        System.out.println(userAccessToken)
    }

}

