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
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUDCRConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURegistrationRequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
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
            AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ.getScopeString()
    ]

    private String accessToken
    private String clientId
    private String registrationPath = AUDCRConstants.REGISTRATION_ENDPOINT
    private String invalidClientId = "invalidclientid"
    File clientIdFile = new File('clientId.txt')
    File accessTokenFile = new File('accessToken.txt')

    @BeforeClass (alwaysRun = true)
    void "Initialize Test Suite"() {
        TestSuite.init()
        AURequestBuilder.getApplicationToken(scopes, null) //to prevent 'connection refused' error
    }

    @Test
    void "TC0101001_Create application without Aud"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithoutAud())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
    }

    @Test
    void "TC0101002_Create application with non matching redirect uris"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder
                .getRegularClaimsWithNonMatchingRedirectUri())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
    }

    @Test
    void "TC0101003_Create application without TokenEndpointAuthSigningAlg"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithoutTokenEndpointAuthSigningAlg())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
    }

    @Test
    void "TC0101004_Create application without TokenEndpointAuthMethod"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithoutTokenEndpointAuthMethod())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
    }

    @Test
    void "TC0101005_Create application without GrantTypes"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithoutGrantTypes())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
    }

    @Test
    void "TC0101006_Create application without ResponseTypes"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithoutResponseTypes())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
    }

    @Test
    void "TC0101007_Create application without SSA"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithoutSSA())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
    }

    @Test
    void "TC0101012_Create application without ID Token Encrypted Response Algorithm"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithoutIdTokenAlg())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, AUConstants.CODE),
                AUConstants.INVALID_CLIENT_METADATA)
    }

    @Test
    void "TC0101013_Create application without ID Token Encrypted Response Encryption Method"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithoutIdTokenEnc())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, AUConstants.CODE),
                AUConstants.INVALID_CLIENT_METADATA)
    }

    @Test
    void "TC0101014_Create application with invalid ID Token Encrypted Response Algorithm"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithInvalidIdTokenAlg())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, AUConstants.CODE),
                AUConstants.INVALID_CLIENT_METADATA)
    }

    @Test
    void "TC0101015_Create application with invalid ID Token Encrypted Response Encryption Method"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithInvalidIdTokenEnc())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, AUConstants.CODE),
                AUConstants.INVALID_CLIENT_METADATA)
    }

    @Test
    void "TC0101016_Create application with different values for software ID in SSA and ISS in request JWT"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithNonMatchingSoftwareIDandISS())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, AUConstants.CODE),
                AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                "Request JWT Issuer does not match with the software ID of the SSA")
    }

    @Test
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
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, AUConstants.CODE),
                AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                "JTI value of the registration request has been replayed")
    }

    @Test (priority = 1, groups = "SmokeTest")
    void "TC0101008_Create application"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaims())
                .when()
                .post(registrationPath)

        clientId = TestUtil.parseResponseBody(registrationResponse, "client_id")
        clientIdFile.write(clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
    }

    @Test (priority = 1, groups = "SmokeTest", dependsOnMethods = "TC0101008_Create application")
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

    @Test (priority = 1, dependsOnMethods = "TC0101008_Create application")
    void "TC0101011_Create application with already available SSA"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaims())
                .when()
                .post(registrationPath)

        switch (AUTestUtil.solutionVersion) {
            case AUConstants.SOLUTION_VERSION_150:
                Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_409)
                break

            default:
                Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
                break
        }

        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse,"code"), "resource_already_exists")
        Assert.assertTrue(TestUtil.parseResponseBody(registrationResponse,"error_description").contains(
                "An application created with the given SSA is already available."))
    }
}
