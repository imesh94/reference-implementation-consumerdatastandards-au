/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */
package com.wso2.openbanking.toolkit.cds.integration.tests.metadata_update

import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURegistrationRequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUMockCDRIntegrationUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUDCRConstants
import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Test Related to Meta Data Update - Batch Tasks
 */
class MetaDataUpdateBatchTaskTests extends AbstractAUTests {

    private String accessToken
    private String clientId
    private AccessTokenResponse userAccessToken = null
    private String registrationPath = AUDCRConstants.REGISTRATION_ENDPOINT
    private String headerString


    @BeforeClass(alwaysRun = true)
    void "Setup"() {
        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        AURegistrationRequestBuilder.retrieveADRInfo()
        headerString = ConfigParser.instance.getBasicAuthUser() + ":" + ConfigParser.instance.getBasicAuthUserPassword()

        String jti = String.valueOf(System.currentTimeMillis())

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaimsWithGivenJti(jti))
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)

        clientId = TestUtil.parseResponseBody(registrationResponse, "client_id")
    }

    //TODO: Enable the tests case after implementing the feature
    @Test(enabled = false)
    void "TC10030001_Verify the Consent Status when the SP Active and ADR Active"() {

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        // Test for Validating Consent Status
        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        Response response = getConsentStatus(headerString, userAccessToken.getCustomParameters().get("cdr_arrangement_id").toString())

        //Assert the consent status status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.jsonPath().get("List[0].Status"), "Authorised")
    }

    //TODO: Enable the tests case after implementing the feature
    @Test(enabled = false)
    void "TC10030002_Verify the Consent Status when the ADR Active and SP removed"() {

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        // Test for Validating Consent Status
        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 3, 1)
        sleep(81000)

        Response response = getConsentStatus(headerString, userAccessToken.getCustomParameters().get("cdr_arrangement_id").toString())

        //Assert the consent status status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.jsonPath().get("List[0].Status"), "Authorised")
    }

    //TODO: Enable the tests case after implementing the feature
    @Test(enabled = false)
    void "TC10030003_Verify the Consent Status when the ADR Suspended and SP removed"() {

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        // Test for Validating Consent Status
        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 3, 3)
        sleep(81000)

        Response response = getConsentStatus(headerString, userAccessToken.getCustomParameters().get("cdr_arrangement_id").toString())

        //Assert the consent status status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.jsonPath().get("List[0].Status"), "Authorised")
    }

    //TODO: Enable the tests case after implementing the feature
    @Test(enabled = false)
    void "TC10030004_Verify the Consent Status when the ADR Surrendered and SP removed"() {

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)
        // Test for Validating Consent Status
        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 3, 5)
        sleep(81000)

        Response response = getConsentStatus(headerString, userAccessToken.getCustomParameters().get("cdr_arrangement_id").toString())

        //Assert the consent status status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.jsonPath().get("List[0].Status"), "Authorised")
    }

    //TODO: Enable the tests case after implementing the feature
    @Test(enabled = false)
    void "TC10030005_Verify the Consent Status when the ADR Revoked and SP removed"() {

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)
        // Test for Validating Consent Status
        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 3, 4)
        sleep(81000)

        Response response = getConsentStatus(headerString, userAccessToken.getCustomParameters().get("cdr_arrangement_id").toString())

        //Assert the consent status status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.jsonPath().get("List[0].Status"), "Authorised")
    }

    //TODO: Enable the tests case after implementing the feature
    @Test(enabled = false)
    void "TC10030006_Verify the Registration Status when the SP Active and ADR Active"() {

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        // Tests for Validating Registration Status
        doConsentAuthorisation(clientId)

        accessToken = AURequestBuilder.getApplicationToken(scopes.collect({ it.scopeString }), clientId)
        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .get(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_200)
    }

    //TODO: Enable the tests case after implementing the feature
    @Test(enabled = false)
    void "TC10030007_Verify the Registration Status when the ADR Suspended and SP Removed"() {

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        // Tests for Validating Registration Status
        doConsentAuthorisation(clientId)

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 3, 3)
        sleep(81000)

        accessToken = AURequestBuilder.getApplicationToken(scopes.collect({ it.scopeString }), clientId)
        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .get(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_200)
    }

    //TODO: Enable the tests case after implementing the feature
    @Test(enabled = false)
    void "TC10030008_Verify the Registration Status when the ADR Revoked and SP Removed"() {

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        // Tests for Validating Registration Status
        doConsentAuthorisation(clientId)

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 3, 4)
        sleep(81000)

        accessToken = AURequestBuilder.getApplicationToken(scopes.collect({ it.scopeString }), clientId)
        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .get(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_200)
    }

    //TODO: Enable the tests case after implementing the feature
    @Test(enabled = false)
    void "TC10030009_Verify the Registration Status when the ADR Surrendered and SP Removed"() {

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        // Tests for Validating Registration Status
        doConsentAuthorisation(clientId)

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 3, 5)
        sleep(81000)

        accessToken = AURequestBuilder.getApplicationToken(scopes.collect({ it.scopeString }), clientId)
        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .get(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_200)
    }

    @AfterClass(alwaysRun = true)
    void tearDown() {
        accessToken = AURequestBuilder.getApplicationToken(scopes.collect({ it.scopeString }), clientId)
        def deletionResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .delete(registrationPath + clientId)

        Assert.assertEquals(deletionResponse.statusCode(), AUConstants.STATUS_CODE_204)
    }
}
