package com.wso2.cds.integration.test.clientRegistration

import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AURestAsRequestBuilder
import com.wso2.openbanking.test.framework.utility.RestAsRequestBuilder
import com.wso2.cds.test.framework.request_builder.AUJWTGenerator
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.request_builder.AUAuthorisationBuilder
import com.wso2.cds.test.framework.utility.AUMockCDRIntegrationUtil
import com.wso2.cds.test.framework.constant.ContextConstants
import com.wso2.cds.test.framework.AUTest
import org.checkerframework.checker.units.qual.A
import org.testng.Assert
import org.testng.ITestContext
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import com.wso2.cds.test.framework.request_builder.AURegistrationRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil

/**
 * Dynamic client registration flow tests.
 */

class DynamicClientRegistrationUpdateTest extends AUTest{
//    private List<String> scopes = [
//            AUAccountScope.BANK_ACCOUNT_BASIC_READ.getScopeString(),
//            AUAccountScope.BANK_TRANSACTION_READ.getScopeString(),
//            AUAccountScope.BANK_CUSTOMER_DETAIL_READ.getScopeString(),
//            AUAccountScope.CDR_REGISTRATION.getScopeString()
//    ]

    private String accessToken
    private String clientId
    private String registrationPath = AUConstants.DCR_REGISTRATION_ENDPOINT
    private String invalidClientId = "invalidclientid"

//    File xmlFile = new File(System.getProperty("user.dir").toString()
//            .concat("/../../resources/test-config.xml"))
//    AUAuthorisationBuilder appConfigReader = new AUAuthorisationBuilder()
//    AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder() //**

    @BeforeClass(alwaysRun = true)
    void "Initialize Test Suite"() {

//        AUTest."Initialize Test Suite"(); //*
//        AURestAsRequestBuilder.init()
//        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
//        AURegistrationRequestBuilder.retrieveADRInfo()  //**
//        deleteApplicationIfExists(scopes)

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getAURegularClaims())
                .when() //*
                .post(registrationPath)

        clientId = parseResponseBody(registrationResponse, "client_id")  //**
        context.setAttribute(ContextConstants.CLIENT_ID,clientId)


        AUTestUtil.writeXMLContent(auConfiguration.getOBXMLFile().toString(), "Application", "ClientID", clientId, auConfiguration.getTppNumber())
    }

    @Test(priority = 1, dependsOnMethods = "Get access token")
    void "Update registration details with invalid client id"() {

        AUJWTGenerator aujwtGenerator=new AUJWTGenerator()
        AURegistrationRequestBuilder auRegistrationRequestBuilder=new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .body(aujwtGenerator.getSignedRequestObject(auRegistrationRequestBuilder.getRegularClaimsWithNewRedirectUri()))
                .when()
                .put(registrationPath + invalidClientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
    }

    @Test(priority = 1, groups = "SmokeTest", dependsOnMethods = "Get access token")
    void "Update registration details"() {

        AUJWTGenerator aujwtGenerator=new AUJWTGenerator()
        AURegistrationRequestBuilder auRegistrationRequestBuilder=new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .body(aujwtGenerator.getSignedRequestObject(auRegistrationRequestBuilder.getRegularClaims()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_200)
    }

    @Test(priority = 2, groups = "SmokeTest")
    void "Get access token"() {

        accessToken = AURequestBuilder.getApplicationAccessToken(context.getAttribute(ContextConstants.CLIENT_ID).toString())
        Assert.assertNotNull(accessToken)
    }

    @Test(priority = 2, dependsOnMethods = "Get access token")
    void "Update registration details without SSA"() {

        AUJWTGenerator aujwtGenerator=new AUJWTGenerator()
        AURegistrationRequestBuilder auRegistrationRequestBuilder=new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .body(aujwtGenerator.getSignedRequestObject(auRegistrationRequestBuilder
                        .getClaimsWithoutSSA()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertEquals(parseResponseBody(registrationResponse, AUConstants.ERROR),
                AUConstants.INVALID_CLIENT_METADATA)
    }

    @Test(priority = 2, dependsOnMethods = "Get access token")
    void "Update registration details with fields not supported by data holder brand"() {

        AUJWTGenerator aujwtGenerator=new AUJWTGenerator()
        AURegistrationRequestBuilder auRegistrationRequestBuilder=new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .body(aujwtGenerator.getSignedRequestObject(auRegistrationRequestBuilder
                        .getRegularClaimsWithFieldsNotSupported()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_200)

        def retrievalResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .get(registrationPath + clientId)

        Assert.assertEquals(retrievalResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertNull(parseResponseBody(retrievalResponse, "adr_name"))
    }

    @Test(priority = 3)
    void "Update registration details with a access token bound only to CDR Authorization scopes"() {

        scopes = [
                AUAccountScope.BANK_ACCOUNT_BASIC_READ.getScopeString(),
                AUAccountScope.BANK_TRANSACTION_READ.getScopeString(),
                AUAccountScope.BANK_CUSTOMER_DETAIL_READ.getScopeString()
        ]

        accessToken = AURequestBuilder.getApplicationAccessToken(scopes, clientId)
        Assert.assertNotNull(accessToken)

        AUJWTGenerator aujwtGenerator=new AUJWTGenerator()
        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .body(aujwtGenerator.getSignedRequestObject(AURegistrationRequestBuilder.getRegularClaims()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_403)
    }

    @Test(priority = 3)
    void "OB-1170_Update registration details without access token"() {

        AUJWTGenerator aujwtGenerator=new AUJWTGenerator()
        AURegistrationRequestBuilder auRegistrationRequestBuilder=new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(null)
                .body(aujwtGenerator.getSignedRequestObject(auRegistrationRequestBuilder
                        .getRegularClaims()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
    }

    @Test(priority = 3)
    void "Update registration details with invalid access token"() {
        AUJWTGenerator aujwtGenerator=new AUJWTGenerator()
        AURegistrationRequestBuilder auRegistrationRequestBuilder=new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest("asd")
                .body(aujwtGenerator.getSignedRequestObject(auRegistrationRequestBuilder
                        .getRegularClaims()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
    }

//    @AfterClass(alwaysRun = true)
//    void tearDown() {
//        deleteApplicationIfExists(scopes, clientId)
//    }
}
