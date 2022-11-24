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

package com.wso2.cds.integration.test.clientRegistration

import com.wso2.cds.test.framework.constant.ContextConstants
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.AUTest
import org.testng.Assert
import org.testng.ITestContext
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import com.wso2.cds.test.framework.request_builder.AURegistrationRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil

/**
 * for testing DCR DELETE function
 */

class DynamicClientRegistrationDeleteTest extends AUTest {


    private String accessToken
    private String clientId
//    private String registrationPath = AUConstants.DCR_REGISTRATION_ENDPOINT
    private String invalidClientId = "invalidclientid"



    @Test(groups = "SmokeTest")
    void "Verify Get Application Access Token"(ITestContext context){
        // retrieve from context using key
        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        AUConfigurationService auConfiguration = new AUConfigurationService()
        def  registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getAURegularClaims())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        clientId = parseResponseBody(registrationResponse, "client_id")
        context.setAttribute(ContextConstants.CLIENT_ID,clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
        AUTestUtil.writeXMLContent(auConfiguration.getOBXMLFile().toString(), "Application", "ClientID", clientId, auConfiguration.getTppNumber())
        accessToken = getApplicationAccessToken(context.getAttribute(ContextConstants.CLIENT_ID).toString())
        Assert.assertNotNull(accessToken)
    }

    @Test(dependsOnMethods = "Verify Get Application Access Token")
    void "Delete application with invalid client id"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .delete(AUConstants.DCR_REGISTRATION_ENDPOINT + invalidClientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
    }

    @Test(groups = "SmokeTest", dependsOnMethods = "Verify Get Application Access Token", priority = 1)
    void "Delete application"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .delete(AUConstants.DCR_REGISTRATION_ENDPOINT + clientId)
        //deleteApplicationIfExists(scopes, clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_204)
    }

//    @AfterClass(alwaysRun = true)
//    void tearDown() {
//        deleteApplicationIfExists(scopes, clientId)
//    }
}
