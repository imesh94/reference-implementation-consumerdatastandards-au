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
import org.testng.Assert
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.testng.ITestContext

/**
 *Test cases to validate DCR create request.
 */
class DynamicClientRegistrationCreateTest extends AUTest{

    @BeforeClass
    void "Delete Application if exists"() {
        deleteApplicationIfExists()
    }

    @Test(priority = 2,dependsOnMethods = "TC0101008_Verify Dynamic client registration test")
    void "TC0101009_Verify Get Application Access Token"(ITestContext context){

        // retrieve from context using key
        accessToken = getApplicationAccessToken(context.getAttribute(ContextConstants.CLIENT_ID).toString())
        Assert.assertNotNull(accessToken)
    }

    @Test(priority = 1)
    void "TC0101008_Verify Dynamic client registration test"(ITestContext context){

        jtiVal = String.valueOf(System.currentTimeMillis())
        AURegistrationRequestBuilder registrationRequestBuilder = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(registrationRequestBuilder.getAURegularClaims())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        clientId = AUTestUtil.parseResponseBody(registrationResponse, "client_id")
        // add to context using key value pair
        context.setAttribute(ContextConstants.CLIENT_ID,clientId)
        AUTestUtil.writeXMLContent(auConfiguration.getOBXMLFile().toString(), "Application",
                "ClientID", clientId, auConfiguration.getTppNumber())

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertEquals(parseResponseBody(registrationResponse, "software_statement"),
                registrationRequestBuilder.getSSA())

    }

    @Test(priority = 3, dependsOnMethods = "TC0101008_Verify Dynamic client registration test")
    void "TC0101011_Create application with already available SSA"() {

        AURegistrationRequestBuilder registrationRequestBuilder = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(registrationRequestBuilder.getAURegularClaims())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR),
                AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertTrue(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION).
                contains("Application with the name " + AUConstants.DCR_SOFTWARE_PRODUCT_ID +
                        " already exist in the system"))
    }

    @Test(priority = 5)
    void "TC0101001_Create application without Aud"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getClaimsWithoutAud())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR),
                AUConstants.INVALID_CLIENT_METADATA)
    }

    @Test(priority = 5)
    void "TC0101002_Create application with non matching redirect uris"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getRegularClaimsWithNonMatchingRedirectUri())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                AUConstants.DCR_INVALID_REDIRECT_DESCRIPTION)
    }

    @Test(priority = 5)
    void "TC0101003_Create application without TokenEndpointAuthSigningAlg"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getClaimsWithoutTokenEndpointAuthSigningAlg())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR),
                AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                AUConstants.DCR_WITHOUT_TOKEN_ENDPOINT_SIGNINGALGO)
    }

    @Test(priority = 5)
    void "TC0101004_Create application without TokenEndpointAuthMethod"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getClaimsWithoutTokenEndpointAuthMethod())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                AUConstants.DCR_WITHOUT_TOKEN_ENDPOINT_AUTHMETHOD)
    }

    @Test(priority = 5)
    void "TC0101005_Create application without GrantTypes"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getClaimsWithoutGrantTypes())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                AUConstants.DCR_GRANT_TYPES_NULL)
    }

    @Test(priority = 5)
    void "TC0101006_Create application without ResponseTypes"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getClaimsWithoutResponseTypes())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR),
                AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                AUConstants.DCR_WITHOUT_RESPONSE_TYPES)
    }

    @Test(priority = 5)
    void "TC0101007_Create application without SSA"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getClaimsWithoutSSA())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                AUConstants.DCR_WITHOUT_SSA)
    }

    @Test(priority = 5)
    void "TC0101012_Create application without ID Token Encrypted Response Algorithm"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getClaimsWithoutIdTokenAlg())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR),
                AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                AUConstants.DCR_WITHOUT_ID_TOKEN_RESPONSE_ALGO)
    }

    @Test(priority = 5)
    void "TC0101013_Create application without ID Token Encrypted Response Encryption Method"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getClaimsWithoutIdTokenEnc())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR),
                AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                AUConstants.DCR_WITHOUT_ID_TOKEN_ENCRYPTION_METHOD)
    }

    @Test(priority = 5)
    void "TC0101014_Create application with invalid ID Token Encrypted Response Algorithm"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getClaimsWithInvalidIdTokenAlg())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR),
                AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                AUConstants.DCR_INVALID_ID_TOKEN_ENCRYPTION_ALGO)
    }

    @Test(priority = 5)
    void "TC0101015_Create application with invalid ID Token Encrypted Response Encryption Method"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getClaimsWithInvalidIdTokenEnc())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR),
                AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                AUConstants.DCR_INVALID_ID_TOKEN_ENCRYPTION_METHOD)
    }

    @Test(priority = 5)
    void "TC0101016_Create application with different values for software ID in SSA and ISS in request JWT"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getClaimsWithNonMatchingSoftwareIDandISS())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR),
                AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                "Invalid issuer")
    }

    @Test(priority = 5)
    void "TC0101017_Create application with a replayed JTI value in JWT request"() {

        AUConfigurationService auConfiguration = new AUConfigurationService()
        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()

        deleteApplicationIfExists(clientId)
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getRegularClaimsWithGivenJti(jtiVal))
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR),
                AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                "JTI value of the registration request has been replayed")


        registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getAURegularClaims())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
        clientId = parseResponseBody(registrationResponse, "client_id")

        AUTestUtil.writeXMLContent(auConfiguration.getOBXMLFile().toString(), "Application",
                "ClientID", clientId, auConfiguration.getTppNumber())

    }

    @Test(priority = 4)
    void "OB-1160_Create application with unsupported TokenEndpointAuthMethod"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getClaimsWithUnsupportedTokenEndpointAuthMethod())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR),
                AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                "Invalid tokenEndPointAuthentication provided")
    }

    @Test(priority = 5)
    void "OB-1161_Create application with unsupported GrantTypes"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getRegularClaimsWithInvalidGrantTypes())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR),
                AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                "Invalid grantTypes provided")
    }

    @Test(priority = 5)
    void "OB-1162_Create application with unsupported ResponseTypes"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getRegularClaimsWithInvalidResponseTypes())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR),
                AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                "Invalid responseTypes provided")
    }

    @Test(priority = 5)
    void "OB-1163_Create application with unsupported ApplicationType"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getRegularClaimsWithUnsupportedApplicationType())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR),
                AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                "Invalid applicationType provided")
    }


    @Test(priority = 5)
    void "OB-1164_Create application with malformed SSA"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getRegularClaimsWithMalformedSSA())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR),
                AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR_DESCRIPTION),
                "Malformed request JWT")
    }

    @Test(priority = 5)
    void "OB-1165_Create application without request_object_signing_alg"() {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        deleteApplicationIfExists(clientId)
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getRegularClaimsWithoutRequestObjectSigningAlg())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertNotNull(parseResponseBody(registrationResponse, "request_object_signing_alg"))

        clientId = parseResponseBody(registrationResponse, "client_id")
        deleteApplicationIfExists(clientId)

        registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getAURegularClaims())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
        clientId = parseResponseBody(registrationResponse, "client_id")

        AUTestUtil.writeXMLContent(auConfiguration.getOBXMLFile().toString(), "Application",
                "ClientID", clientId, auConfiguration.getTppNumber())
    }

    @Test(priority = 5)
    void "OB-1166_Create application without redirect_uris"() {

        deleteApplicationIfExists(clientId)
        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getRegularClaimsWithoutRedirectUris())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        clientId = parseResponseBody(registrationResponse, "client_id")
        AUTestUtil.writeXMLContent(auConfiguration.getOBXMLFile().toString(), "Application",
                "ClientID", clientId, auConfiguration.getTppNumber())

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertNotNull(parseResponseBody(registrationResponse, "redirect_uris"))
    }

//    @AfterClass(alwaysRun = true)
    void tearDown() {
        deleteApplicationIfExists(clientId)
    }

}

