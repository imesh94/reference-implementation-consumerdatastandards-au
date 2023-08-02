/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.metadata_update

import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.utility.AUTestUtil
import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Test Related to Meta Data Update - Consent Withdrawal
 */
class MetaDataConsentWithdrawal extends AUTest{

    @BeforeClass(alwaysRun = true)
    void setup() {
        auConfiguration.setTppNumber(1)

        //Register Second TPP.
        deleteApplicationIfExists(auConfiguration.getAppInfoClientID())
        def registrationResponse = tppRegistration()
        clientId = AUTestUtil.parseResponseBody(registrationResponse, "client_id")
        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.CREATED)

        //Write Client Id of TPP2 to config file.
        AUTestUtil.writeXMLContent(AUTestUtil.getTestConfigurationFilePath(), "Application",
                "ClientID", clientId, auConfiguration.getTppNumber())

        doConsentAuthorisation(clientId)

        accessToken = getApplicationAccessToken(clientId)
        Assert.assertNotNull(accessToken)
    }

    @Test(enabled = true)
    void "TC015_Verify the Consent Withdrawal when the SP Active and ADR Active"() {

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        AccessTokenResponse responseBody = getUserAccessTokenResponse(clientId)
        userAccessToken = responseBody.tokens.accessToken
        cdrArrangementId = responseBody.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId)

        //Revoke the Consent
        Response response = doRevokeCdrArrangement(clientId, cdrArrangementId)

        //Assert the consent revoke status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_204)
    }

    @Test(enabled = true)
    void "TC016_Verify the Consent Withdrawal when the SP Inactive and ADR Active"() {

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        AccessTokenResponse responseBody = getUserAccessTokenResponse(clientId)
        userAccessToken = responseBody.tokens.accessToken
        cdrArrangementId = responseBody.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId)

        //TODO: Change Status
        sleep(81000)

        //revoke sharing arrangement
        def response = doRevokeCdrArrangement(auConfiguration.getAppInfoClientID(), cdrArrangementId)

        //Assert the consent revoke status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_204)
    }

    //Enable the test case when running the test case. Disabled due to the mock authenticator is not available now.
    @Test(enabled = false)
    void "TC017_Verify the Consent Withdrawal when the SP Inactive and ADR Suspended"() {

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        AccessTokenResponse responseBody = getUserAccessTokenResponse(clientId)
        userAccessToken = responseBody.tokens.accessToken
        cdrArrangementId = responseBody.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId)

        //TODO: Change Status
        sleep(81000)

        //Revoke the Consent
        def response = doRevokeCdrArrangement(auConfiguration.getAppInfoClientID(), cdrArrangementId)

        //Assert the consent revoke status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_204)
    }

    //Enable the test case when running the test case. Disabled due to the mock authenticator is not available now.
    @Test(enabled = false)
    void "TC018_Verify the Consent Withdrawal when the SP Removed and ADR Active"() {

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        AccessTokenResponse responseBody = getUserAccessTokenResponse(clientId)
        userAccessToken = responseBody.tokens.accessToken
        cdrArrangementId = responseBody.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId)

        //TODO: Change Status
        sleep(81000)

        //Revoke the Consent
        def response = doRevokeCdrArrangement(auConfiguration.getAppInfoClientID(), cdrArrangementId)

        //Assert the consent revoke status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE)
                .contains(AUConstants.ERROR_CODE_ADR_STATUS_NOT_ACTIVE))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE)
                .contains(AUConstants.ERROR_TITLE_INVALID_SP_STATUS))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL)
                .contains("The software product of ADR is not in an active state in the CDR Register. Current status is REMOVED"))
    }

    //Enable the test case when running the test case. Disabled due to the mock authenticator is not available now.
    @Test(enabled = false)
    void "TC019_Verify the Consent Withdrawal when the SP Removed and ADR Suspended"() {

        setup()

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        AccessTokenResponse responseBody = getUserAccessTokenResponse(clientId)
        userAccessToken = responseBody.tokens.accessToken
        cdrArrangementId = responseBody.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId)

        //TODO: Change Status
        sleep(81000)

        //Revoke the Consent
        def response = doRevokeCdrArrangement(auConfiguration.getAppInfoClientID(), cdrArrangementId)

        //Assert the consent revoke status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE)
                .contains(AUConstants.ERROR_CODE_ADR_STATUS_NOT_ACTIVE))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE)
                .contains(AUConstants.ERROR_TITLE_INVALID_SP_STATUS))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL)
                .contains("The software product of ADR is not in an active state in the CDR Register. Current status is REMOVED"))
    }

    //Enable the test case when running the test case. Disabled due to the mock authenticator is not available now.
    @Test(enabled = false)
    void "TC020_Verify the Consent Withdrawal when the SP Removed and ADR Revoked"() {

        setup()

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        AccessTokenResponse responseBody = getUserAccessTokenResponse(clientId)
        userAccessToken = responseBody.tokens.accessToken
        cdrArrangementId = responseBody.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId)

        //TODO: Change Status
        sleep(81000)

        //Revoke the Consent
        def response = doRevokeCdrArrangement(auConfiguration.getAppInfoClientID(), cdrArrangementId)

        //Assert the consent revoke status code
        //Assert the consent revoke status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE)
                .contains(AUConstants.ERROR_CODE_ADR_STATUS_NOT_ACTIVE))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE)
                .contains(AUConstants.ERROR_TITLE_INVALID_SP_STATUS))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL)
                .contains("The ADR is not in an active state in the CDR Register. Current status is REVOKED"))
    }

    //Enable the test case when running the test case. Disabled due to the mock authenticator is not available now.
    @Test(enabled = false)
    void "TC021_Verify the Consent Withdrawal when the SP Removed and ADR Surrendered"() {

        setup()

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        AccessTokenResponse responseBody = getUserAccessTokenResponse(clientId)
        userAccessToken = responseBody.tokens.accessToken
        cdrArrangementId = responseBody.getCustomParameters().get(AUConstants.CDR_ARRANGEMENT_ID)
        Assert.assertNotNull(cdrArrangementId)

        //TODO: Change Status
        sleep(81000)

        //Revoke the Consent
        def response = doRevokeCdrArrangement(auConfiguration.getAppInfoClientID(), cdrArrangementId)

        //Assert the consent revoke status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE)
                .contains(AUConstants.ERROR_CODE_ADR_STATUS_NOT_ACTIVE))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE)
                .contains(AUConstants.ERROR_TITLE_INVALID_SP_STATUS))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL)
                .contains("The ADR is not in an active state in the CDR Register. Current status is SURRENDERED"))
    }
}
