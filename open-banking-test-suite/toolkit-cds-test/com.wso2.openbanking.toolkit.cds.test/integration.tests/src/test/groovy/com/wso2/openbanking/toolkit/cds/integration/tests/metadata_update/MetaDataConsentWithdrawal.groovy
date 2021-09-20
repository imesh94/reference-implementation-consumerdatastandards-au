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
 * Test Related to Meta Data Update - Consent Withdrawal
 */
class MetaDataConsentWithdrawal extends AbstractAUTests {

    private String accessToken
    private String clientId
    private AccessTokenResponse userAccessToken = null
    private String cdrArrangementId = ""
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
    void "TC015_Verify the Consent Withdrawal when the SP Active and ADR Active"() {

        // Tests for Validating Consent Withdrawal
        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        //Revoke the Consent
        Response response = doRevokeConsent(clientId, cdrArrangementId)

        //Assert the consent revoke status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_204)
    }

    @Test
    void "TC016_Verify the Consent Withdrawal when the SP Inactive and ADR Active"() {

        // Tests for Validating Consent Withdrawal
        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, false, 2)
        sleep(81000)

        //Revoke the Consent
        Response response = doRevokeConsent(clientId, cdrArrangementId)

        //Assert the consent revoke status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_204)
    }

    @Test
    void "TC017_Verify the Consent Withdrawal when the SP Inactive and ADR Suspended"() {

        // Tests for Validating Consent Withdrawal
        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 2, 3)
        sleep(81000)

        //Revoke the Consent
        Response response = doRevokeConsent(clientId, cdrArrangementId)

        //Assert the consent revoke status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_204)
    }

    @Test
    void "TC018_Verify the Consent Withdrawal when the SP Removed and ADR Active"() {

        // Tests for Validating Consent Withdrawal
        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, false, 3)
        sleep(81000)

        //Revoke the Consent
        Response response = doRevokeConsent(clientId, cdrArrangementId)

        //Assert the consent revoke status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(response.jsonPath().get("errors.code").toString()
                .contains(AUConstants.ERROR_CODE_INVALID_SP_STATUS))
        Assert.assertTrue(response.jsonPath().get("errors.title").toString().contains(AUConstants.ERROR_TITLE_INVALID_SP_STATUS))
    }

    @Test
    void "TC019_Verify the Consent Withdrawal when the SP Removed and ADR Suspended"() {

        // Tests for Validating Consent Withdrawal
        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 3, 3)
        sleep(81000)

        //Revoke the Consent
        Response response = doRevokeConsent(clientId, cdrArrangementId)

        //Assert the consent revoke status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(response.jsonPath().get("errors.code").toString()
                .contains(AUConstants.ERROR_CODE_INVALID_SP_STATUS))
        Assert.assertTrue(response.jsonPath().get("errors.title").toString().contains(AUConstants.ERROR_TITLE_INVALID_SP_STATUS))
    }

    @Test
    void "TC020_Verify the Consent Withdrawal when the SP Removed and ADR Revoked"() {

        // Tests for Validating Consent Withdrawal
        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, false, 3, 4)
        sleep(81000)

        //Revoke the Consent
        Response response = doRevokeConsent(clientId, cdrArrangementId)

        //Assert the consent revoke status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(response.jsonPath().get("errors.code").toString()
                .contains(AUConstants.ERROR_CODE_INVALID_SP_STATUS))
        Assert.assertTrue(response.jsonPath().get("errors.title").toString().contains(AUConstants.ERROR_TITLE_INVALID_SP_STATUS))
    }

    @Test
    void "TC021_Verify the Consent Withdrawal when the SP Removed and ADR Surrendered"() {

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, clientId)
        cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, true, 3, 5)
        sleep(81000)

        //Revoke the Consent
        Response response = doRevokeConsent(clientId, cdrArrangementId)

        //Assert the consent revoke status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(response.jsonPath().get("errors.code").toString()
                .contains(AUConstants.ERROR_CODE_INVALID_SP_STATUS))
        Assert.assertTrue(response.jsonPath().get("errors.title").toString().contains(AUConstants.ERROR_TITLE_INVALID_SP_STATUS))
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
