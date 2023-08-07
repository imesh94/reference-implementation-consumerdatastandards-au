/**
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.tpponboarding

import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.ContextConstants
import com.wso2.cds.test.framework.request_builder.AUJWTGenerator
import com.wso2.cds.test.framework.request_builder.AURegistrationRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import org.testng.Assert
import org.testng.ITestContext
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * This is a prerequisite which need to run the rest of the test scenarios.
 */
class CommonTppOnboardingStep extends AUTest{

    AUJWTGenerator generator = new AUJWTGenerator()
    String clientId

    @BeforeClass
    void "Delete Application if exists"() {
        deleteApplicationIfExists()
    }

    @Test
    void "Tpp Onboarding Step"(ITestContext context){

        jtiVal = String.valueOf(System.currentTimeMillis())
        AURegistrationRequestBuilder registrationRequestBuilder = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(registrationRequestBuilder.getAURegularClaims())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        clientId = AUTestUtil.parseResponseBody(registrationResponse, AUConstants.CLIENT_ID)

        // add to context using key value pair
        context.setAttribute(ContextConstants.CLIENT_ID,clientId)
        AUTestUtil.writeToConfigFile(clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertEquals(parseResponseBody(registrationResponse, "software_statement"),
                registrationRequestBuilder.getSSA())
    }
}
