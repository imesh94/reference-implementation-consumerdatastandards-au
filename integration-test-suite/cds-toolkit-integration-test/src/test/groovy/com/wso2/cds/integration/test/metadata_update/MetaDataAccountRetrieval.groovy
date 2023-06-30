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
        def registrationResponse = tppRegistration()
        clientId = AUTestUtil.parseResponseBody(registrationResponse, "client_id")
        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.CREATED)

        //Write Client Id of TPP2 to config file.
        AUTestUtil.writeXMLContent(auConfiguration.getOBXMLFile().toString(), "Application",
                "ClientID", clientId, auConfiguration.getTppNumber())

        doConsentAuthorisation(clientId)

        auConfiguration.setTppNumber(0)
        accessToken = getApplicationAccessToken(clientId)
        Assert.assertNotNull(accessToken)
    }

    @Test(priority = 1)
    void "TC008 Verify the Account Retrieval when the SP and ADR both active"() {

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = getUserAccessTokenResponse(clientId).tokens.accessToken

        //Account Retrieval request
        Response response = doAccountRetrieval(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)

        //Asserting account retrieval response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(response.jsonPath().get("${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[0].accountId"))
    }

    @Test(priority = 2)
    void "TC009 _Verify the Account Retrieval when the SP Removed and ADR active"() {

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = getUserAccessTokenResponse(clientId).tokens.accessToken

        //TODO: Change the Status
        sleep(100000)

        //Account Retrieval request
        Response response = doAccountRetrieval(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)

        //Asserting account retrieval response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_401)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE)
                .contains(AUConstants.ERROR_CODE_UNAUTHORIZED))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE)
                .contains(AUConstants.INVALID_AUTHORISATION))
    }

    @Test(priority = 1)
    void "TC010_Verify the Account Retrieval when the SP Inactive and ADR active"() {

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = getUserAccessTokenResponse(clientId).tokens.accessToken

        //TODO: Change the Status
        sleep(100000)

        //Account Retrieval request
        Response response = doAccountRetrieval(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)

        //Asserting account retrieval error response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE)
                .contains(AUConstants.ERROR_CODE_INVALID_SP_STATUS))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE)
                .contains(AUConstants.ERROR_TITLE_INVALID_SP_STATUS))
    }

    @Test(priority = 1)
    void "TC011_Verify the Account Retrieval when the SP Inactive and ADR Suspended"() {

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = getUserAccessTokenResponse(clientId).tokens.accessToken

        //TODO: Change the Status
        sleep(100000)

        //Account Retrieval request
        Response response = doAccountRetrieval(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)

        //Asserting account retrieval error response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE)
                .contains(AUConstants.ERROR_CODE_INVALID_ADR_STATUS))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE)
                .contains(AUConstants.ERROR_TITLE_INVALID_ADR_STATUS))
    }

    @Test(priority = 3)
    void "TC012_Verify the Account Retrieval when the SP Removed and ADR Suspended"() {
        //creating application again since meta data state changes delete the application
        setup()

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = getUserAccessTokenResponse(clientId).tokens.accessToken

        //TODO: Change the Status
        sleep(100000)

        //Account Retrieval request
        Response response = doAccountRetrieval(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)

        //Asserting account retrieval error response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_401)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE)
                .contains(AUConstants.ERROR_CODE_UNAUTHORIZED))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE)
                .contains(AUConstants.INVALID_AUTHORISATION))
    }

    @Test(priority = 3)
    void "TC013_Verify the Account Retrieval when the SP Removed and ADR Revoked"() {

        //creating application again since meta data state changes delete the application
        setup()

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = getUserAccessTokenResponse(clientId).tokens.accessToken

        //TODO: Change the Status
        sleep(100000)

        //Account Retrieval request
        Response response = doAccountRetrieval(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)

        //Asserting account retrieval error response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_401)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE)
                .contains(AUConstants.ERROR_CODE_UNAUTHORIZED))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE)
                .contains(AUConstants.INVALID_AUTHORISATION))
    }

    @Test(priority = 3)
    void "TC014_Verify the Account Retrieval when the SP Removed and ADR Surrendered"() {

        //creating application again since meta data state changes delete the application
        setup()

        doConsentAuthorisation(clientId)

        // Retrieve the user access token by auth code
        userAccessToken = getUserAccessTokenResponse(clientId).tokens.accessToken

        //TODO: Change the Status
        sleep(100000)

        //Account Retrieval request
        Response response = doAccountRetrieval(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)

        //Asserting account retrieval error response and status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_401)
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE)
                .contains(AUConstants.ERROR_CODE_UNAUTHORIZED))
        Assert.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE)
                .contains(AUConstants.INVALID_AUTHORISATION))
    }
}
