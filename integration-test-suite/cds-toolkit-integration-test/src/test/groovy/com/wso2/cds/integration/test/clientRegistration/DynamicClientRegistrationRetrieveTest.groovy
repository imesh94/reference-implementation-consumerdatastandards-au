/**
 * Copyright (c) 2022, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.clientRegistration

import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.ContextConstants
import com.wso2.cds.test.framework.request_builder.AURegistrationRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import com.wso2.openbanking.test.framework.configuration.OBConfigParser
import com.wso2.openbanking.test.framework.constant.OBConstants
import com.wso2.openbanking.test.framework.utility.RestAsRequestBuilder
import org.testng.Assert
import org.testng.annotations.AfterClass
import org.testng.annotations.Test
import org.testng.ITestContext

/**
 * Testcases for DCR retrieve request validation.
 */
class DynamicClientRegistrationRetrieveTest extends AUTest{

    private String applicationId

    @SuppressWarnings('GroovyAccessibility')
    @Test(priority = 1)
    void "TC0101018_Retrieve Application"(ITestContext context) {

        AURegistrationRequestBuilder registrationRequestBuilder = new AURegistrationRequestBuilder()

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(registrationRequestBuilder.getAURegularClaims())
                .when()
                .post( AUConstants.DCR_REGISTRATION_ENDPOINT)

        clientId = AUTestUtil.parseResponseBody(registrationResponse, "client_id")
        context.setAttribute(ContextConstants.CLIENT_ID,clientId)
        AUTestUtil.writeXMLContent(auConfiguration.getOBXMLFile().toString(), "Application",
                "ClientID", clientId, auConfiguration.getTppNumber())

        AUConfigurationService auConfigurationService = new AUConfigurationService()
        URI devPortalEndpoint =
                new URI("${String.valueOf(OBConfigParser.getInstance().getConfigurationMap().get("Server.GatewayURL"))}" +
                        AUConstants.REST_API_STORE_ENDPOINT + "applications")
        def response = RestAsRequestBuilder.buildRequest()
                .contentType(OBConstants.CONTENT_TYPE_APPLICATION_JSON)
                .header(OBConstants.AUTHORIZATION_HEADER_KEY, AUConstants.AUTHORIZATION_BEARER_TAG +
                        auConfigurationService.getRestAPIDCRAccessToken())
                .get(devPortalEndpoint.toString())

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, "count"), "2")
        applicationId = AUTestUtil.parseResponseBody(response, "list[1].applicationId")
    }

    @Test(priority = 1)
    void "TC0101009_Get access token"() {

        accessToken = getApplicationAccessToken(clientId)
        Assert.assertNotNull(accessToken)
    }

    @Test(priority = 2, dependsOnMethods = "TC0101018_Retrieve Application")
    void "TC0101019_Subscribe admin API"() {

        AURegistrationRequestBuilder registrationRequestBuilder = new AURegistrationRequestBuilder()

        def apiID = auConfiguration.getRestAPIID()
        URI devPortalEndpoint =
                new URI("${String.valueOf(OBConfigParser.getInstance().getConfigurationMap().get("Server.GatewayURL"))}" +
                        AUConstants.REST_API_STORE_ENDPOINT + "subscriptions")
        def response = RestAsRequestBuilder.buildRequest()
                .contentType(OBConstants.CONTENT_TYPE_APPLICATION_JSON)
                .header(OBConstants.AUTHORIZATION_HEADER_KEY,  AUConstants.AUTHORIZATION_BEARER_TAG+
                        auConfiguration.getRestAPIDCRAccessToken())
                .body(registrationRequestBuilder.getSubscriptionPayload(applicationId, apiID))
                .post(devPortalEndpoint.toString())

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_201)
    }

    @Test(priority = 2, dependsOnMethods = "TC0101009_Get access token")
    void "TC0102001_Get registration details with invalid client id"() {

        String invalidClientId = "invalidclientid"

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .get(AUConstants.DCR_REGISTRATION_ENDPOINT + invalidClientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
    }

    @Test(priority = 2, dependsOnMethods = "TC0101009_Get access token")
    void "TC0102002_Get registration details"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .get(AUConstants.DCR_REGISTRATION_ENDPOINT + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_200)
    }

    @AfterClass(alwaysRun = true)
    void tearDown() {
        deleteApplicationIfExists(clientId)
    }
}