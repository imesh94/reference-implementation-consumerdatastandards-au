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

package com.wso2.openbanking.toolkit.cds.integration.tests.client_registration

import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.*
import org.testng.Assert
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Dynamic client registration flow tests.
 */
class DynamicClientRegistrationRetrievalTest extends AbstractAUTests {

    private List<String> scopes = [
            AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ.getScopeString(),
            AUConstants.SCOPES.BANK_TRANSACTION_READ.getScopeString(),
            AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ.getScopeString(),
            AUConstants.SCOPES.CDR_REGISTRATION.getScopeString()
    ]

    private String accessToken
    private String clientId
    private String applicationId
    private String registrationPath = AUDCRConstants.REGISTRATION_ENDPOINT
    File xmlFile = new File(System.getProperty("user.dir").toString()
            .concat("/../../resources/test-config.xml"))
    AppConfigReader appConfigReader = new AppConfigReader()
    String baseURL = TestConstants.REST_API_STORE_ENDPOINT;

    @BeforeClass(alwaysRun = true)
    void "Initialize Test Suite"() {
        TestSuite.init()
        AURequestBuilder.getApplicationToken(scopes, null) //to prevent 'connection refused' error
        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        AURegistrationRequestBuilder.retrieveADRInfo()

        deleteApplicationIfExists(scopes)
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaims())
                .when()
                .post(registrationPath)

        clientId = TestUtil.parseResponseBody(registrationResponse, "client_id")
        TestUtil.writeXMLContent(xmlFile.toString(), "Application", "ClientID", clientId,
                appConfigReader.tppNumber)
    }

    @Test(priority = 1, groups = "SmokeTest")
    void "TC0101018_Retrieve Application"() {
        URI devPortalEndpoint =
                new URI("${String.valueOf(ConfigParser.getInstance().getConfiguration().get("Server.GatewayURL"))}"
                        + baseURL + "applications");
        def response = TestSuite.buildRequest()
                .contentType(TestConstants.CONTENT_TYPE_APPLICATION_JSON)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, TestConstants.AUTHORIZATION_BEARER_TAG +
                        ConfigParser.getRESTApiDCRAccessToken())
                .get(devPortalEndpoint.toString())

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(TestUtil.parseResponseBody(response, "count"), "2")
        applicationId = TestUtil.parseResponseBody(response, "list[1].applicationId")
    }

    @Test(priority = 1, groups = "SmokeTest", dependsOnMethods = "TC0101018_Retrieve Application")
    void "TC0101019_Subscribe admin API"() {
        def apiID = ConfigParser.getRESTApiApiId()
        URI devPortalEndpoint =
                new URI("${String.valueOf(ConfigParser.getInstance().getConfiguration().get("Server.GatewayURL"))}"
                        + baseURL + "subscriptions");
        def response = TestSuite.buildRequest()
                .contentType(TestConstants.CONTENT_TYPE_APPLICATION_JSON)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, TestConstants.AUTHORIZATION_BEARER_TAG +
                        ConfigParser.getRESTApiDCRAccessToken())
                .body(getSubscriptionPayload(applicationId, apiID))
                .post(devPortalEndpoint.toString())

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_201)
    }

    @Test(priority = 1, dependsOnMethods = "TC0101009_Get access token")
    void "TC0102001_Get registration details with invalid client id"() {

        String invalidClientId = "invalidclientid"

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .get(registrationPath + invalidClientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
    }

    @Test(priority = 1, groups = "SmokeTest", dependsOnMethods = "TC0101009_Get access token")
    void "TC0102002_Get registration details"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .get(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_200)
    }

    @Test(priority = 2, groups = "SmokeTest")
    void "TC0101009_Get access token"() {

        accessToken = AURequestBuilder.getApplicationToken(scopes, clientId)
        Assert.assertNotNull(accessToken)
    }

    @AfterClass(alwaysRun = true)
    void tearDown() {
        deleteApplicationIfExists(scopes, clientId)
    }

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
