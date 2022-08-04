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
import com.wso2.cds.test.framework.constant.ContextConstants
import com.wso2.cds.test.framework.AUTest
import org.testng.Assert
import org.testng.ITestContext
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import com.wso2.cds.test.framework.request_builder.AURegistrationRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil

/**
 * Test DCR function
 */
class DCRTest extends AUTest{

    @BeforeClass
    void "Delete Application if exists"() {
        deleteApplicationIfExists()
    }

    @Test(groups = "DCRTest")
    void "Dynamic client registration test"(ITestContext context){

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getAURegularClaims())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        String clientId = AUTestUtil.parseResponseBody(registrationResponse, "client_id")
        // add to context using key value pair
        context.setAttribute(ContextConstants.CLIENT_ID,clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertEquals(parseResponseBody(registrationResponse, "software_statement"), dcr.getSSA())
    }

    @Test(priority = 1,groups = "DCRTest",dependsOnMethods = "Dynamic client registration test")
    void "Get Application Access Token"(ITestContext context){
        // retrieve from context using key
        String token = getApplicationAccessToken(context.getAttribute(ContextConstants.CLIENT_ID).toString())
        Assert.assertNotNull(token)
    }

}

