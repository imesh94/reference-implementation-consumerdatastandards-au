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
class DynamicClientRegistrationUpdateTest extends AbstractAUTests {

    private List<String> scopes = [
            AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ.getScopeString(),
            AUConstants.SCOPES.BANK_TRANSACTION_READ.getScopeString(),
            AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ.getScopeString(),
            AUConstants.SCOPES.CDR_REGISTRATION.getScopeString()
    ]

    private String accessToken
    private String clientId
    private String registrationPath = AUDCRConstants.REGISTRATION_ENDPOINT
    private String invalidClientId = "invalidclientid"
    File xmlFile = new File(System.getProperty("user.dir").toString()
            .concat("/../../resources/test-config.xml"))
    AppConfigReader appConfigReader = new AppConfigReader()

    @BeforeClass(alwaysRun = true)
    void "Initialize Test Suite"() {
        TestSuite.init()
        //AURequestBuilder.getApplicationToken(scopes, null) //to prevent 'connection refused' error
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

    @Test(priority = 1, dependsOnMethods = "TC0101009_Get access token")
    void "TC0103001_Update registration details with invalid client id"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .body(AURegistrationRequestBuilder.getSignedRequestObject(AURegistrationRequestBuilder
                        .getRegularClaimsWithNewRedirectUri()))
                .when()
                .put(registrationPath + invalidClientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
    }

    @Test(priority = 1, groups = "SmokeTest", dependsOnMethods = "TC0101009_Get access token")
    void "TC0103002_Update registration details"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .body(AURegistrationRequestBuilder.getSignedRequestObject(AURegistrationRequestBuilder
                        .getRegularClaims()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_200)
    }

    @Test(priority = 2, groups = "SmokeTest")
    void "TC0101009_Get access token"() {

        accessToken = AURequestBuilder.getApplicationToken(scopes, clientId)
        Assert.assertNotNull(accessToken)
    }

    @Test(priority = 2, dependsOnMethods = "TC0101009_Get access token")
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

    @Test(priority = 2, dependsOnMethods = "TC0101009_Get access token")
    void "OB-1168_Update registration details with fields not supported by data holder brand"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .body(AURegistrationRequestBuilder.getSignedRequestObject(AURegistrationRequestBuilder
                        .getRegularClaimsWithFieldsNotSupported()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_200)

        def retrievalResponse = AURegistrationRequestBuilder.buildBasicRequestWithContentTypeJson(accessToken)
                .when()
                .get(registrationPath + clientId)

        Assert.assertEquals(retrievalResponse.statusCode(), AUConstants.STATUS_CODE_400)
        Assert.assertNull(TestUtil.parseResponseBody(retrievalResponse, "adr_name"))
    }

    @Test(priority = 3)
    void "OB-1169_Update registration details with a access token bound only to CDR Authorization scopes"() {

        scopes = [
                AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ.getScopeString(),
                AUConstants.SCOPES.BANK_TRANSACTION_READ.getScopeString(),
                AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ.getScopeString()
        ]

        accessToken = AURequestBuilder.getApplicationToken(scopes, clientId)
        Assert.assertNotNull(accessToken)

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .body(AURegistrationRequestBuilder.getSignedRequestObject(AURegistrationRequestBuilder
                        .getRegularClaims()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_403)
    }

    @Test(priority = 3)
    void "OB-1170_Update registration details without access token"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest("")
                .body(AURegistrationRequestBuilder.getSignedRequestObject(AURegistrationRequestBuilder
                        .getRegularClaims()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
    }

    @Test(priority = 3)
    void "OB-1171_Update registration details with invalid access token"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest("asd")
                .body(AURegistrationRequestBuilder.getSignedRequestObject(AURegistrationRequestBuilder
                        .getRegularClaims()))
                .when()
                .put(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
    }

    @AfterClass(alwaysRun = true)
    void tearDown() {
        deleteApplicationIfExists(scopes, clientId)
    }
}
