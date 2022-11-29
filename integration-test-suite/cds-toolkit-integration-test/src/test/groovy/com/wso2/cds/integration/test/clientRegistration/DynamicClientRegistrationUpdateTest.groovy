/**
 * Copyright (c) 2022, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
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
 * Testcases for DCR Update request validation
 */

class DynamicClientRegistrationUpdateTest extends AUTest{

    private String accessToken
    private String registrationPath = AUConstants.DCR_REGISTRATION_ENDPOINT
    private String invalidClientId = "invalidclientid"

    @BeforeClass(alwaysRun = true)
    void "Initialize Test Suite"(ITestContext context) {

        AURegistrationRequestBuilder dcr = new AURegistrationRequestBuilder()

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(dcr.getAURegularClaims())
                .when()
                .post(registrationPath)

        clientId = parseResponseBody(registrationResponse, "client_id")
        context.setAttribute(ContextConstants.CLIENT_ID,clientId)


        AUTestUtil.writeXMLContent(auConfiguration.getOBXMLFile().toString(), "Application",
                "ClientID", clientId, auConfiguration.getTppNumber())
    }

    @Test(priority = 1, dependsOnMethods = "TC0101009_Get access token")
    void "TC0103001_Update registration details with invalid client id"() {

        AUJWTGenerator aujwtGenerator=new AUJWTGenerator()
        AURegistrationRequestBuilder auRegistrationRequestBuilder=new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .body(aujwtGenerator.getSignedRequestObject(auRegistrationRequestBuilder.getRegularClaimsWithNewRedirectUri()))
                .when()
                .put(registrationPath + invalidClientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
    }

    @Test(priority = 1, groups = "SmokeTest", dependsOnMethods = "TC0101009_Get access token")
    void "TC0103002_Update registration details"() {

        AUJWTGenerator aujwtGenerator=new AUJWTGenerator()
        AURegistrationRequestBuilder auRegistrationRequestBuilder=new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .body(aujwtGenerator.getSignedRequestObject(auRegistrationRequestBuilder.getRegularClaims()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_200)
    }

    @Test(priority = 2, groups = "SmokeTest")
    void "TC0101009_Get access token"() {

        accessToken = AURequestBuilder.getApplicationAccessToken(context.getAttribute(ContextConstants.CLIENT_ID).toString())
        Assert.assertNotNull(accessToken)
    }

    @Test(priority = 2, dependsOnMethods = "TC0101009_Get access token")
    void "OB-1167_Update registration details without SSA"() {

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

    @Test(priority = 2, dependsOnMethods = "TC0101009_Get access token")
    void "OB-1168_Update registration details with fields not supported by data holder brand"() {

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
    void "OB-1169_Update registration details with a access token bound only to CDR Authorization scopes"() {

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
    void "OB-1171_Update registration details with invalid access token"() {
        AUJWTGenerator aujwtGenerator=new AUJWTGenerator()
        AURegistrationRequestBuilder auRegistrationRequestBuilder=new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest("asd")
                .body(aujwtGenerator.getSignedRequestObject(auRegistrationRequestBuilder
                        .getRegularClaims()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
    }

}
