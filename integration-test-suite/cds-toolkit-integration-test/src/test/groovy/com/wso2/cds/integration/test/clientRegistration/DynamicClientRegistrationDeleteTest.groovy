/**
 * Copyright (c) 2022, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.clientRegistration

import com.wso2.cds.test.framework.constant.ContextConstants
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.AUTest
import org.testng.Assert
import org.testng.ITestContext
import org.testng.annotations.Test
import com.wso2.cds.test.framework.request_builder.AURegistrationRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil

/**
 * Test cases to validate DCR delete request
 */
class DynamicClientRegistrationDeleteTest extends AUTest {

    private String accessToken
    private String invalidClientId = "invalidclientid"

    @Test(groups = "SmokeTest")
    void "TC0101009_Verify Get Application Access Token"(ITestContext context){

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
        AUTestUtil.writeXMLContent(auConfiguration.getOBXMLFile().toString(), "Application",
                "ClientID", clientId, auConfiguration.getTppNumber())
        accessToken = getApplicationAccessToken(context.getAttribute(ContextConstants.CLIENT_ID).toString())
        Assert.assertNotNull(accessToken)
    }

    @Test(dependsOnMethods = "TC0101009_Verify Get Application Access Token")
    void "TC0104001_Delete application with invalid client id"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .delete(AUConstants.DCR_REGISTRATION_ENDPOINT + invalidClientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
    }

    @Test(groups = "SmokeTest", dependsOnMethods = "Verify Get Application Access Token", priority = 1)
    void "TC0104002_Delete application"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .delete(AUConstants.DCR_REGISTRATION_ENDPOINT + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_204)
    }
}
