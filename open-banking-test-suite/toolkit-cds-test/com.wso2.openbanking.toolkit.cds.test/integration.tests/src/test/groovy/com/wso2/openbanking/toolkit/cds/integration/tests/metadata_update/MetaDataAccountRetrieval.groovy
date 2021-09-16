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
 * Test Related to Meta Data Update - Account Retrieval
 */
class MetaDataAccountRetrieval extends AbstractAUTests {

    private String accessToken
    private String clientId
    private AccessTokenResponse userAccessToken = null
    private String registrationPath = AUDCRConstants.REGISTRATION_ENDPOINT

    @BeforeClass(alwaysRun = true)
    void "Setup"() {

        //Load Meta Data to CDR Register
        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)
        AURegistrationRequestBuilder.retrieveADRInfo()

        String jti = String.valueOf(System.currentTimeMillis())

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaimsWithGivenJti(jti))
                .when()
                .post(registrationPath)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
        clientId = TestUtil.parseResponseBody(registrationResponse, "client_id")
    }

    @Test
    void "TC008 Verify the Account Retrieval when the SP and ADR both active"() {

        //Load Meta Data to Register and wait time
        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)

        //Account Retrieval request
        Response response = getAccountRetrieval(userAccessToken.tokens.accessToken.toString())

        //Asserting account retrieval response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(response.jsonPath().get("data.accounts[0].accountId"))
    }

    @Test
    void "TC009 _Verify the Account Retrieval when the SP Removed and ADR active"() {

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 3, 1)
        sleep(100000)

        //Account Retrieval request
        Response response = getAccountRetrieval(userAccessToken.tokens.accessToken.toString())

        //Asserting account retrieval response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(response.jsonPath().get("errors.code").toString()
                .contains(AUConstants.ERROR_CODE_INVALID_SP_STATUS))
        Assert.assertTrue(response.jsonPath().get("errors.title").toString().contains(AUConstants.ERROR_TITLE_INVALID_SP_STATUS))
    }

    @Test
    void "TC010_Verify the Account Retrieval when the SP Inactive and ADR active"() {

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, false, 2)
        sleep(100000)

        //Account Retrieval request
        Response response = getAccountRetrieval(userAccessToken.tokens.accessToken.toString())

        //Asserting account retrieval error response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(response.jsonPath().get("errors.code").toString()
                .contains(AUConstants.ERROR_CODE_INVALID_SP_STATUS))
        Assert.assertTrue(response.jsonPath().get("errors.title").toString().contains(AUConstants.ERROR_TITLE_INVALID_SP_STATUS))
    }

    @Test
    void "TC011_Verify the Account Retrieval when the SP Inactive and ADR Suspended"() {

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 2, 3)
        sleep(100000)

        //Account Retrieval request
        Response response = getAccountRetrieval(userAccessToken.tokens.accessToken.toString())

        //Asserting account retrieval error response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(response.jsonPath().get("errors.code").toString()
                .contains(AUConstants.ERROR_CODE_INVALID_ADR_STATUS))
        Assert.assertTrue(response.jsonPath().get("errors.title").toString().contains(AUConstants.ERROR_TITLE_INVALID_ADR_STATUS))
    }

    @Test
    void "TC012_Verify the Account Retrieval when the SP Removed and ADR Suspended"() {

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 3, 3)
        sleep(100000)

        //Account Retrieval request
        Response response = getAccountRetrieval(userAccessToken.tokens.accessToken.toString())

        //Asserting account retrieval error response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(response.jsonPath().get("errors.code").toString().contains(AUConstants.ERROR_CODE_INVALID_ADR_STATUS))
        Assert.assertTrue(response.jsonPath().get("errors.title").toString().contains(AUConstants.ERROR_TITLE_INVALID_ADR_STATUS))
    }

    @Test
    void "TC013_Verify the Account Retrieval when the SP Removed and ADR Revoked"() {

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 3, 4)
        sleep(100000)

        //Account Retrieval request
        Response response = getAccountRetrieval(userAccessToken.tokens.accessToken.toString())

        //Asserting account retrieval error response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(response.jsonPath().get("errors.code").toString().contains(AUConstants.ERROR_CODE_INVALID_ADR_STATUS))
        Assert.assertTrue(response.jsonPath().get("errors.title").toString().contains(AUConstants.ERROR_TITLE_INVALID_ADR_STATUS))
    }

    @Test
    void "TC014_Verify the Account Retrieval when the SP Removed and ADR Surrendered"() {

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(100000)

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 3, 5)
        sleep(100000)

        //Account Retrieval request
        Response response = getAccountRetrieval(userAccessToken.tokens.accessToken.toString())

        //Asserting account retrieval error response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(response.jsonPath().get("errors.code").toString().contains(AUConstants.ERROR_CODE_INVALID_ADR_STATUS))
        Assert.assertTrue(response.jsonPath().get("errors.title").toString().contains(AUConstants.ERROR_TITLE_INVALID_ADR_STATUS))
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
