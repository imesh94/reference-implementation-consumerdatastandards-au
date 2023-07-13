/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.tokenEndpoint

import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AUMockCDRIntegrationUtil
import com.wso2.cds.test.framework.utility.AUTestUtil
import org.testng.Assert
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Test class contains Multi Tpp Token Flow Validation Tests.
 */
class MultiTppTokenFlowValidationTests extends AUTest {

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
    }

    @Test
    void "OB-1314_Get user access token from authorisation code bound to different Tpp" () {

        auConfiguration.setTppNumber(0)
        def userToken = AURequestBuilder.getUserTokenErrorResponse(authorisationCode,
                auConfiguration.getAppInfoRedirectURL(), auConfiguration.getAppInfoClientID())

        Assert.assertEquals(userToken.error.httpStatusCode, AUConstants.BAD_REQUEST)
        Assert.assertEquals(userToken.error.code, AUConstants.INVALID_GRANT)
        Assert.assertEquals(userToken.error.description, "Invalid authorization code received from token request")
    }

    @Test
    void "OB-1315_Get user access token with client_assertion does not bound to the requested client" () {

        auConfiguration.setTppNumber(0)
        doConsentAuthorisation(auConfiguration.getAppInfoClientID())
        def userToken = AURequestBuilder.getUserTokenErrorResponse(authorisationCode,
                auConfiguration.getAppInfoRedirectURL(), clientId)

        Assert.assertEquals(userToken.error.httpStatusCode, AUConstants.BAD_REQUEST)
        Assert.assertEquals(userToken.error.code, AUConstants.INVALID_GRANT)
        Assert.assertEquals(userToken.error.description, "Invalid authorization code received from token request")
    }

    @AfterClass(alwaysRun = true)
    void tearDown() {
        deleteApplicationIfExists(clientId)
    }
}
