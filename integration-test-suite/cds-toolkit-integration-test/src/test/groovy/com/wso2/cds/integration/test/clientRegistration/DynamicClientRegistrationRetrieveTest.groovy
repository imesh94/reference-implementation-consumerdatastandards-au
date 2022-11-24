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
import com.wso2.cds.test.framework.utility.AURestAsRequestBuilder
import com.wso2.openbanking.test.framework.configuration.OBConfigParser
import com.wso2.openbanking.test.framework.utility.RestAsRequestBuilder
import com.wso2.openbanking.test.framework.constant.OBConstants
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.request_builder.AUAuthorisationBuilder
import com.wso2.cds.test.framework.utility.AUMockCDRIntegrationUtil
import com.wso2.cds.test.framework.AUTest
import org.testng.Assert
import org.testng.ITestContext
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import com.wso2.cds.test.framework.request_builder.AURegistrationRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil


/**
 * for testing DCR GET function
 */
class DynamicClientRegistrationRetrieveTest extends AUTest{
//    private List<String> scopes = [
//            AUAccountScope.BANK_ACCOUNT_BASIC_READ.getScopeString(),
//            AUAccountScope.BANK_TRANSACTION_READ.getScopeString(),
//            AUAccountScope.BANK_CUSTOMER_DETAIL_READ.getScopeString(),
//            AUAccountScope.CDR_REGISTRATION.getScopeString()
//    ]

    private String accessToken
    private String clientId
    private String applicationId
   // private String registrationPath = AUConstants.DCR_REGISTRATION_ENDPOINT
//    File xmlFile = new File(System.getProperty("user.dir").toString()
//            .concat("/../../resources/test-config.xml"))
//    AUAuthorisationBuilder appConfigReader = new AUAuthorisationBuilder()
//    AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder() //**
    //String baseURL = AUConstants.REST_API_STORE_ENDPOINT;

//    @BeforeClass(alwaysRun = true)
//    void "Initialize Test Suite"() {
//        AURestAsRequestBuilder.init()
//        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
//        AURegistrationRequestBuilder.retrieveADRInfo()

       // deleteApplicationIfExists(scopes)

//        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
//
//        def registrationResponse = AURegistrationRequestBuilder
//                .buildRegistrationRequest(dcr.getAURegularClaims())
//                .when()
//                .post( AUConstants.DCR_REGISTRATION_ENDPOINT)
//
//        clientId = AUTestUtil.parseResponseBody(registrationResponse, "client_id")
//        //context.setAttribute(ContextConstants.CLIENT_ID,clientId)
//
//        AUTestUtil.writeXMLContent(auConfiguration.getOBXMLFile().toString(), "Application", "ClientID", clientId, auConfiguration.getTppNumber())

 //   }

    @SuppressWarnings('GroovyAccessibility')
    @Test(priority = 1, groups = "SmokeTest")
    void "Retrieve Application"(ITestContext context) {
        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()


        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getAURegularClaims())
                .when()
                .post( AUConstants.DCR_REGISTRATION_ENDPOINT)

        clientId = AUTestUtil.parseResponseBody(registrationResponse, "client_id")
        context.setAttribute(ContextConstants.CLIENT_ID,clientId)


        AUTestUtil.writeXMLContent(auConfiguration.getOBXMLFile().toString(), "Application", "ClientID", clientId, auConfiguration.getTppNumber())

        AUConfigurationService auConfigurationService=new AUConfigurationService()
        URI devPortalEndpoint =
                new URI("${String.valueOf(OBConfigParser.getInstance().getConfigurationMap().get("Server.GatewayURL"))}" + AUConstants.REST_API_STORE_ENDPOINT + "applications");
        def response = RestAsRequestBuilder.buildRequest()
                .contentType(OBConstants.CONTENT_TYPE_APPLICATION_JSON)
                .header(OBConstants.AUTHORIZATION_HEADER_KEY, "Bearer " + auConfigurationService.getRestAPIDCRAccessToken())
                .get(devPortalEndpoint.toString())

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(AUTestUtil.parseResponseBody(response, "count"), "2")
        applicationId = AUTestUtil.parseResponseBody(response, "list[1].applicationId")
    }

    @Test(priority = 1, groups = "SmokeTest", dependsOnMethods = "Retrieve Application")
    void "Subscribe admin API"() {
        def apiID = auConfiguration.getRestAPIID()
        URI devPortalEndpoint =
                new URI("${String.valueOf(OBConfigParser.getInstance().getConfigurationMap().get("Server.GatewayURL"))}" +  AUConstants.REST_API_STORE_ENDPOINT + "subscriptions");
        def response = RestAsRequestBuilder.buildRequest()
                .contentType(OBConstants.CONTENT_TYPE_APPLICATION_JSON)
                .header(OBConstants.AUTHORIZATION_HEADER_KEY,  "Bearer "+ auConfiguration.getRestAPIDCRAccessToken())
                .body(getSubscriptionPayload(applicationId, apiID))
                .post(devPortalEndpoint.toString())

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_201)
    }

    @Test(priority = 1, dependsOnMethods = "Get access token")
    void "Get registration details with invalid client id"() {

        String invalidClientId = "invalidclientid"

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .get(AUConstants.DCR_REGISTRATION_ENDPOINT + invalidClientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
    }

    @Test(priority = 1, groups = "SmokeTest", dependsOnMethods = "Get access token")
    void "Get registration details"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .get(AUConstants.DCR_REGISTRATION_ENDPOINT + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_200)
    }
//
    @Test(priority = 2, groups = "SmokeTest")
    void "Get access token"() {

        accessToken = getApplicationAccessToken(clientId)
        Assert.assertNotNull(accessToken)
    }
//
//    @AfterClass(alwaysRun = true)
//    void tearDown() {
//        deleteApplicationIfExists(scopes, clientId)
//    }
//
    static String getSubscriptionPayload(String applicationId, String apiId) {
        return """
            {
              "applicationId": "$applicationId",
              "apiId": "$apiId",
              "throttlingPolicy": "Unlimited"
            }
            """.stripIndent()
    }
}
