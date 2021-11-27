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
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUDCRConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURegistrationRequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUMockCDRIntegrationUtil
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Dynamic client registration flow tests.
 */
class DynamicClientRegistrationFlowTest {

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
    private String invalidClientId = "invalidclientid"
    File clientIdFile = new File('clientId.txt')
    File accessTokenFile = new File('accessToken.txt')
    String baseURL = TestConstants.REST_API_STORE_ENDPOINT;

    @BeforeClass (alwaysRun = true)
    void "Initialize Test Suite"() {
        TestSuite.init()
        AURequestBuilder.getApplicationToken(scopes, null) //to prevent 'connection refused' error
        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        AURegistrationRequestBuilder.retrieveADRInfo()
    }

    @Test(priority = 4)
    void "TC0101001_Create application without Aud"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithoutAud())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR),
                AUDCRConstants.INVALID_CLIENT_METADATA)
    }

    @Test(priority = 4)
    void "TC0101002_Create application with non matching redirect uris"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder
                .getRegularClaimsWithNonMatchingRedirectUri())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR_DESCRIPTION),
                AUDCRConstants.INVALID_REDIRECT_DESCRIPTION)
    }

    @Test(priority = 4)
    void "TC0101003_Create application without TokenEndpointAuthSigningAlg"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithoutTokenEndpointAuthSigningAlg())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR),
                AUDCRConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR_DESCRIPTION),
                AUDCRConstants.WITHOUT_TOKEN_ENDPOINT_SIGNINGALGO)
    }

    @Test(priority = 4)
    void "TC0101004_Create application without TokenEndpointAuthMethod"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithoutTokenEndpointAuthMethod())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR_DESCRIPTION),
                AUDCRConstants.WITHOUT_TOKEN_ENDPOINT_AUTHMETHOD)
    }

    @Test(priority = 4)
    void "TC0101005_Create application without GrantTypes"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithoutGrantTypes())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR_DESCRIPTION),
                AUDCRConstants.GRANT_TYPES_NULL)
    }

    @Test(priority = 4)
    void "TC0101006_Create application without ResponseTypes"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithoutResponseTypes())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR),
                AUDCRConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR_DESCRIPTION),
               AUDCRConstants.WITHOUT_RESPONSE_TYPES)
    }

    @Test(priority = 4)
    void "TC0101007_Create application without SSA"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithoutSSA())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR_DESCRIPTION),
                AUDCRConstants.WITHOUT_SSA)
    }

    @Test(priority = 4)
    void "TC0101012_Create application without ID Token Encrypted Response Algorithm"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithoutIdTokenAlg())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR),
                AUDCRConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR_DESCRIPTION),
                AUDCRConstants.WITHOUT_ID_TOKEN_RESPONSE_ALGO)
    }

   @Test(priority = 4)
    void "TC0101013_Create application without ID Token Encrypted Response Encryption Method"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithoutIdTokenEnc())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR),
                AUDCRConstants.INVALID_CLIENT_METADATA)
       Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR_DESCRIPTION),
               AUDCRConstants.WITHOUT_ID_TOKEN_ENCRYPTION_METHOD)
    }

    @Test(priority = 4)
    void "TC0101014_Create application with invalid ID Token Encrypted Response Algorithm"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithInvalidIdTokenAlg())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR),
                AUDCRConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR_DESCRIPTION),
                AUDCRConstants.INVALID_ID_TOKEN_ENCRYPTION_ALGO)
    }

    @Test(priority = 4)
    void "TC0101015_Create application with invalid ID Token Encrypted Response Encryption Method"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithInvalidIdTokenEnc())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR),
                AUDCRConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR_DESCRIPTION),
                AUDCRConstants.INVALID_ID_TOKEN_ENCRYPTION_METHOD)
    }

    @Test(priority = 4)
    void "TC0101016_Create application with different values for software ID in SSA and ISS in request JWT"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithNonMatchingSoftwareIDandISS())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR),
                AUDCRConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR_DESCRIPTION),
                "Invalid issuer")
    }

    @Test(priority = 4)
    void "TC0101017_Create application with a replayed JTI value in JWT request"() {

        String jti = String.valueOf(System.currentTimeMillis())

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaimsWithGivenJti(jti))
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)

        clientId = TestUtil.parseResponseBody(registrationResponse, "client_id")
        accessToken = AURequestBuilder.getApplicationToken(scopes, clientId)
        AURegistrationRequestBuilder.buildBasicRequest(accessToken).when()
                .delete(registrationPath + clientId)

        registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaimsWithGivenJti(jti))
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR),
                AUDCRConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR_DESCRIPTION),
                "JTI value of the registration request has been replayed")
    }

    @Test(priority = 4)
    void "OB-1160_Create application with unsupported TokenEndpointAuthMethod"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithUnsupportedTokenEndpointAuthMethod())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)

        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR),
                AUDCRConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse,TestConstants.ERROR_DESCRIPTION),
                "Invalid tokenEndPointAuthentication provided")
    }

    @Test(priority = 4)
    void "OB-1161_Create application with unsupported GrantTypes"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaimsWithInvalidGrantTypes())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR),
                AUDCRConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse,TestConstants.ERROR_DESCRIPTION),
                "Invalid grant types found in the request")
    }

    @Test(priority = 4)
    void "OB-1162_Create application with unsupported ResponseTypes"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaimsWithInvalidResponseTypes())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR),
                AUDCRConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse,TestConstants.ERROR_DESCRIPTION),
                "Invalid response types found in the request")
    }

    @Test(priority = 4)
    void "OB-1163_Create application with unsupported ApplicationType"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaimsWithUnsupportedApplicationType())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR),
                AUDCRConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse,TestConstants.ERROR_DESCRIPTION),
                "Invalid application type found in the request")
    }

    @Test(priority = 4)
    void "OB-1164_Create application with malformed SSA"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaimsWithMalformedSSA())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR),
                AUDCRConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse,TestConstants.ERROR_DESCRIPTION),
                "Provided SSA is malformed or unsupported by the specification")
    }

    @Test(priority = 4)
    void "OB-1165_Create application without request_object_signing_alg"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaimsWithoutRequestObjectSigningAlg())
                .when()
                .post(registrationPath)

        clientId = TestUtil.parseResponseBody(registrationResponse, "client_id")

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertNotNull(TestUtil.parseResponseBody(registrationResponse, "request_object_signing_alg"))

        // delete the created application to facilitate next testcases
        accessToken = AURequestBuilder.getApplicationToken(scopes, clientId)
        def deletionResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .delete(registrationPath + clientId)

        Assert.assertEquals(deletionResponse.statusCode(), AUConstants.STATUS_CODE_204)
    }

    @Test(priority = 4)
    void "OB-1166_Create application without redirect_uris"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaimsWithoutRedirectUris())
                .when()
                .post(registrationPath)

        clientId = TestUtil.parseResponseBody(registrationResponse, "client_id")

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertNotNull(TestUtil.parseResponseBody(registrationResponse, "redirect_uris"))

        // delete the created application to facilitate next testcases
        accessToken = AURequestBuilder.getApplicationToken(scopes, clientId)
        def deletionResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .delete(registrationPath + clientId)

        Assert.assertEquals(deletionResponse.statusCode(), AUConstants.STATUS_CODE_204)
    }

    @Test (priority = 1, groups = "SmokeTest")
    void "TC0101008_Create application"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaims())
                .when()
                .post(registrationPath)

        clientId = TestUtil.parseResponseBody(registrationResponse, "client_id")
        clientIdFile.write(clientId)
        def newFile = new File("target/test.properties")
        newFile << "\nClientID=$clientId"

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, "software_statement"),
                AUDCRConstants.SSA)
    }

    @Test (priority = 1, groups = "SmokeTest", dependsOnMethods = "TC0101008_Create application")
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

    @Test (priority = 1, groups = "SmokeTest",  dependsOnMethods = "TC0101018_Retrieve Application")
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

     @Test (priority = 2, groups = "SmokeTest", dependsOnMethods = "TC0101008_Create application")
    void "TC0101009_Get access token"() {

        clientId = clientIdFile.text

        accessToken = AURequestBuilder.getApplicationToken(scopes, clientId)
        accessTokenFile.write(accessToken)

        Assert.assertNotNull(accessToken)
    }

    @Test (priority = 1, dependsOnMethods = "TC0101009_Get access token")
    void "TC0102001_Get registration details with invalid client id"() {

        String invalidClientId = "invalidclientid"

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .get(registrationPath + invalidClientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
    }

    @Test (priority = 1, groups = "SmokeTest", dependsOnMethods = "TC0101009_Get access token")
    void "TC0102002_Get registration details"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .get(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_200)
    }

   @Test (priority = 1, dependsOnMethods = "TC0101009_Get access token")
    void "TC0103001_Update registration details with invalid client id"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .body(AURegistrationRequestBuilder.getSignedRequestObject(AURegistrationRequestBuilder
                .getRegularClaimsWithNewRedirectUri()))
                .when()
                .put(registrationPath + invalidClientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
    }

    @Test (priority = 1, groups = "SmokeTest", dependsOnMethods = "TC0101009_Get access token")
    void "TC0103002_Update registration details"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .body(AURegistrationRequestBuilder.getSignedRequestObject(AURegistrationRequestBuilder
                .getRegularClaims()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_200)
    }

    @Test (priority = 2, dependsOnMethods = "TC0101008_Create application")
    void "TC0101011_Create application with already available SSA"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaims())
                .when()
                .post(registrationPath)

        switch (AUTestUtil.solutionVersion) {
            case AUConstants.SOLUTION_VERSION_150:
                Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_409)
                Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse,"code"),
                        "resource_already_exists")
                break

            default:
                Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
                Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse,"error"),
                        "invalid_client_metadata")
                break
        }

        Assert.assertTrue(TestUtil.parseResponseBody(registrationResponse,"error_description").contains(
                "Application with the name " +AUDCRConstants.SOFTWARE_PRODUCT_ID+ " already exist in the system"))
    }

    @Test (priority = 2, dependsOnMethods = "TC0101009_Get access token")
    void "OB-1167_Update registration details without SSA"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .body(AURegistrationRequestBuilder.getSignedRequestObject(AURegistrationRequestBuilder
                        .getClaimsWithoutSSA()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR),
                AUDCRConstants.INVALID_CLIENT_METADATA)
    }

   @Test (priority = 2, dependsOnMethods = "TC0101009_Get access token")
    void "OB-1168_Update registration details with fields not supported by data holder brand"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .body(AURegistrationRequestBuilder.getSignedRequestObject(AURegistrationRequestBuilder
                        .getRegularClaimsWithFieldsNotSupported()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_200)

        def retrievalResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .get(registrationPath + clientId)

        Assert.assertEquals(retrievalResponse.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNull(TestUtil.parseResponseBody(retrievalResponse, "adr_name"))
    }

    @Test (priority = 3)
    void "OB-1169_Update registration details with a access token bound only to CDR Authorization scopes"() {

        scopes = [
                AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ.getScopeString(),
                AUConstants.SCOPES.BANK_TRANSACTION_READ.getScopeString(),
                AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ.getScopeString()
        ]

        clientId = clientIdFile.text

        accessToken = AURequestBuilder.getApplicationToken(scopes, clientId)
        accessTokenFile.write(accessToken)

        Assert.assertNotNull(accessToken)

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .body(AURegistrationRequestBuilder.getSignedRequestObject(AURegistrationRequestBuilder
                        .getRegularClaims()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_403)

    }

    @Test (priority = 3)
    void "OB-1170_Update registration details without access token"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(null)
                .body(AURegistrationRequestBuilder.getSignedRequestObject(AURegistrationRequestBuilder
                        .getRegularClaims()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
    }

    @Test (priority = 3)
    void "OB-1171_Update registration details with invalid access token"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest("asd")
                .body(AURegistrationRequestBuilder.getSignedRequestObject(AURegistrationRequestBuilder
                        .getRegularClaims()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
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
