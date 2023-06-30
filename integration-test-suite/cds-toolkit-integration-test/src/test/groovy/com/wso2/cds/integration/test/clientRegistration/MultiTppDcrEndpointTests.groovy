/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.clientRegistration

import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.request_builder.AUJWTGenerator
import com.wso2.cds.test.framework.request_builder.AURegistrationRequestBuilder
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AUMockCDRIntegrationUtil
import com.wso2.cds.test.framework.utility.AUTestUtil
import org.testng.Assert
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Test class contains Multi TPP related DCR Tests.
 */
class MultiTppDcrEndpointTests extends AUTest {

    public List<AUAccountScope> scopes = [
            AUAccountScope.CDR_REGISTRATION
    ]

    @BeforeClass(alwaysRun = true)
    void setup() {
        auConfiguration.setTppNumber(1)

        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()

        //Register Second TPP.
        def registrationResponse = tppRegistration()
        clientId = AUTestUtil.parseResponseBody(registrationResponse, "client_id")
        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.CREATED)

        //Write Client Id of TPP2 to config file.
        AUTestUtil.writeXMLContent(auConfiguration.getOBXMLFile().toString(), "Application",
                "ClientID", clientId, auConfiguration.getTppNumber())

        doConsentAuthorisation(clientId)

        auConfiguration.setTppNumber(0)
        accessToken = AURequestBuilder.getApplicationAccessToken(scopes, auConfiguration.getAppInfoClientID())
        Assert.assertNotNull(accessToken)
    }

    @Test
    void "OB-1308_Retrieve registration details with access token bound to a different client"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .get(AUConstants.DCR_REGISTRATION_ENDPOINT + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.UNAUTHORIZED)
        Assert.assertEquals(AUTestUtil.parseResponseBody(registrationResponse,AUConstants.ERROR),
                com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(AUTestUtil.parseResponseBody(registrationResponse,AUConstants.ERROR_DESCRIPTION),
                "Request failed due to unknown or invalid Client")
    }

    @Test
    void "OB-1309_Update Application with access token bound to a different client"() {

        AUJWTGenerator aujwtGenerator = new AUJWTGenerator()
        AURegistrationRequestBuilder auRegistrationRequestBuilder = new AURegistrationRequestBuilder()
        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .body(aujwtGenerator.getSignedRequestObject(auRegistrationRequestBuilder
                        .getRegularClaimsWithNewRedirectUri()))
                .when()
                .put(AUConstants.DCR_REGISTRATION_ENDPOINT + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.UNAUTHORIZED)
        Assert.assertEquals(AUTestUtil.parseResponseBody(registrationResponse,AUConstants.ERROR),
                com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(AUTestUtil.parseResponseBody(registrationResponse,AUConstants.ERROR_DESCRIPTION),
                "Request failed due to unknown or invalid Client")
    }

    @Test
    void "OB-1310_Delete application with access token bound to a different client"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .delete(AUConstants.DCR_REGISTRATION_ENDPOINT + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.UNAUTHORIZED)
        Assert.assertEquals(AUTestUtil.parseResponseBody(registrationResponse,AUConstants.ERROR),
                com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants.INVALID_CLIENT_METADATA)
        Assert.assertEquals(AUTestUtil.parseResponseBody(registrationResponse,AUConstants.ERROR_DESCRIPTION),
                "Request failed due to unknown or invalid Client")
    }

    @AfterClass (alwaysRun = true)
    void tearDown() {

        List<String> scopes = [
                AUAccountScope.CDR_REGISTRATION
        ]

        //Delete TPP2.
        auConfiguration.setTppNumber(1)
        deleteApplicationIfExists(scopes)
        Assert.assertEquals(deletionResponse.statusCode(), AUConstants.STATUS_CODE_204)
    }
}
