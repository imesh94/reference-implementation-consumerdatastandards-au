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
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUDCRConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURegistrationRequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUMockCDRIntegrationUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import org.testng.Assert
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Dynamic client registration flow tests.
 */
class DynamicClientRegistrationCreateTest extends AbstractAUTests {

    private List<String> scopes = [
            AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ.getScopeString(),
            AUConstants.SCOPES.BANK_TRANSACTION_READ.getScopeString(),
            AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ.getScopeString(),
            AUConstants.SCOPES.CDR_REGISTRATION.getScopeString()
    ]

    private String accessToken
    private String clientId
    private String registrationPath = AUDCRConstants.REGISTRATION_ENDPOINT

    File xmlFile = new File(System.getProperty("user.dir").toString()
            .concat("/../../resources/test-config.xml"))
    AppConfigReader appConfigReader = new AppConfigReader()

    @BeforeClass(alwaysRun = true)
    void "Initialize Test Suite"() {

        TestSuite.init()
        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        AURegistrationRequestBuilder.retrieveADRInfo()
        deleteApplicationIfExists(scopes, clientId)
        appConfigReader.setTppNumber(0)
    }

    @Test(groups = "SmokeTest")
    void "TC0101008_Create application"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaims())
                .when()
                .post(registrationPath)

        clientId = TestUtil.parseResponseBody(registrationResponse, "client_id")

        TestUtil.writeXMLContent(xmlFile.toString(), "Application", "ClientID", clientId,
                appConfigReader.tppNumber)
        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, "software_statement"),
                AUDCRConstants.SSA)
    }

    @Test(priority = 1, groups = "SmokeTest", dependsOnMethods = "TC0101008_Create application")
    void "TC0101009_Get access token"() {

        accessToken = AURequestBuilder.getApplicationToken(scopes, clientId)
        Assert.assertNotNull(accessToken)
    }

    @Test(priority = 2, dependsOnMethods = "TC0101008_Create application")
    void "TC0101011_Create application with already available SSA"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaims())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, "error"),
                "invalid_client_metadata")

        Assert.assertTrue(TestUtil.parseResponseBody(registrationResponse, "error_description").contains(
                "Application with the name " + AUDCRConstants.SOFTWARE_PRODUCT_ID + " already exist in the system"))
    }

    @Test(priority = 4)
    void "TC0101001_Create application without Aud"() {

        deleteApplicationIfExists(scopes, clientId)
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

        deleteApplicationIfExists(scopes, clientId)
        String jti = String.valueOf(System.currentTimeMillis())

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaimsWithGivenJti(jti))
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)

        clientId = TestUtil.parseResponseBody(registrationResponse, "client_id")
        TestUtil.writeXMLContent(xmlFile.toString(), "Application", "ClientID", clientId,
                appConfigReader.tppNumber)
        deleteApplicationIfExists(scopes, clientId)

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

    @Test(priority = 3)
    void "OB-1160_Create application with unsupported TokenEndpointAuthMethod"() {

        deleteApplicationIfExists(scopes, clientId)
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getClaimsWithUnsupportedTokenEndpointAuthMethod())
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)

        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR),
                AUDCRConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR_DESCRIPTION),
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
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR_DESCRIPTION),
                "Invalid grantTypes provided")
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
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR_DESCRIPTION),
                "Invalid responseTypes provided")
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
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR_DESCRIPTION),
                "Invalid applicationType provided")
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
        Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse, TestConstants.ERROR_DESCRIPTION),
                "Malformed request JWT")
    }

    @Test(priority = 4)
    void "OB-1165_Create application without request_object_signing_alg"() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaimsWithoutRequestObjectSigningAlg())
                .when()
                .post(registrationPath)

        clientId = TestUtil.parseResponseBody(registrationResponse, "client_id")
        TestUtil.writeXMLContent(xmlFile.toString(), "Application", "ClientID", clientId,
                appConfigReader.tppNumber)
        deleteApplicationIfExists(scopes, clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertNotNull(TestUtil.parseResponseBody(registrationResponse, "request_object_signing_alg"))
    }

    @Test(priority = 4)
    void "OB-1166_Create application without redirect_uris"() {

        deleteApplicationIfExists(scopes, clientId)
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaimsWithoutRedirectUris())
                .when()
                .post(registrationPath)

        clientId = TestUtil.parseResponseBody(registrationResponse, "client_id")
        TestUtil.writeXMLContent(xmlFile.toString(), "Application", "ClientID", clientId,
                appConfigReader.tppNumber)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertNotNull(TestUtil.parseResponseBody(registrationResponse, "redirect_uris"))
    }

    @AfterClass(alwaysRun = true)
    void tearDown() {
      //  deleteApplicationIfExists(scopes, clientId)
    }
}
