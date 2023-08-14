/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.metadata_update

import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.utility.AUTestUtil
import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Test Related to Meta Data Update - Account Retrieval
 */
class MetaDataAccountRetrieval extends AUTest{

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

        // Retrieve the user access token by auth code
        userAccessToken = getUserAccessTokenResponse(clientId).tokens.accessToken
    }

    @Test(enabled = true)
    void "TC008 Verify the Account Retrieval when the SP and ADR both active"() {

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = getUserAccessTokenResponse(clientId).tokens.accessToken

        //Account Retrieval request
        Response response = doAccountRetrieval(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)

        //Asserting account retrieval response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(response.jsonPath().get("${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}"))
    }

    @Test(enabled = true)
    void "TC009 _Verify the Account Retrieval when the SP Removed and ADR active"() {

        //TODO: Change the Status
        sleep(100000)

        //Account Retrieval request
        Response response = doAccountRetrieval(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)

        //Asserting account retrieval response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE)
                .contains(AUConstants.ERROR_CODE_ADR_STATUS_NOT_ACTIVE))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE)
                .contains(AUConstants.ADR_STATUS_NOT_ACTIVE))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL)
                .contains("The software product of ADR is not in an active state in the CDR Register. Current status is REMOVED"))
    }

    //Enable the test case when running the test case. Disabled due to the mock authenticator is not available now.
    @Test(enabled = false)
    void "TC010_Verify the Account Retrieval when the SP Inactive and ADR active"() {

        //TODO: Change the Status
        sleep(100000)

        //Account Retrieval request
        Response response = doAccountRetrieval(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)

        //Asserting account retrieval error response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE)
                .contains(AUConstants.ERROR_CODE_ADR_STATUS_NOT_ACTIVE))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE)
                .contains(AUConstants.ADR_STATUS_NOT_ACTIVE))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL)
                .contains("The software product of ADR is not in an active state in the CDR Register. Current status is INACTIVE"))
    }

    //Enable the test case when running the test case. Disabled due to the mock authenticator is not available now.
    @Test(enabled = false)
    void "TC011_Verify the Account Retrieval when the SP Inactive and ADR Suspended"() {

        //TODO: Change the Status
        sleep(100000)

        //Account Retrieval request
        Response response = doAccountRetrieval(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)

        //Asserting account retrieval error response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE)
                .contains(AUConstants.ERROR_CODE_ADR_STATUS_NOT_ACTIVE))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE)
                .contains(AUConstants.ADR_STATUS_NOT_ACTIVE))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL)
                .contains("The ADR is not in an active state in the CDR Register. Current status is SUSPENDED"))
    }

    //Enable the test case when running the test case. Disabled due to the mock authenticator is not available now.
    @Test(enabled = false)
    void "TC012_Verify the Account Retrieval when the SP Removed and ADR Suspended"() {

        //TODO: Change the Status
        sleep(100000)

        //Account Retrieval request
        Response response = doAccountRetrieval(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)

        //Asserting account retrieval error response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE)
                .contains(AUConstants.ERROR_CODE_ADR_STATUS_NOT_ACTIVE))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE)
                .contains(AUConstants.ADR_STATUS_NOT_ACTIVE))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL)
                .contains("The ADR is not in an active state in the CDR Register. Current status is SUSPENDED"))
    }

    //Enable the test case when running the test case. Disabled due to the mock authenticator is not available now.
    @Test(enabled = false)
    void "TC013_Verify the Account Retrieval when the SP Removed and ADR Revoked"() {

        //TODO: Change the Status
        sleep(100000)

        //Account Retrieval request
        Response response = doAccountRetrieval(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)

        //Asserting account retrieval error response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE)
                .contains(AUConstants.ERROR_CODE_ADR_STATUS_NOT_ACTIVE))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE)
                .contains(AUConstants.ADR_STATUS_NOT_ACTIVE))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL)
                .contains("The ADR is not in an active state in the CDR Register. Current status is REVOKED"))
    }

    //Enable the test case when running the test case. Disabled due to the mock authenticator is not available now.
    @Test(enabled = false)
    void "TC014_Verify the Account Retrieval when the SP Removed and ADR Surrendered"() {

        //TODO: Change the Status
        sleep(100000)

        //Account Retrieval request
        Response response = doAccountRetrieval(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)

        //Asserting account retrieval error response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE)
                .contains(AUConstants.ERROR_CODE_ADR_STATUS_NOT_ACTIVE))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE)
                .contains(AUConstants.ADR_STATUS_NOT_ACTIVE))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL)
                .contains("The ADR is not in an active state in the CDR Register. Current status is SURRENDERED"))
    }
}
