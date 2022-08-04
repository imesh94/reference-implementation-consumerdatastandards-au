/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.cds.integration.test

import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.AUTest
import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import com.wso2.cds.test.framework.request_builder.AURegistrationRequestBuilder
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AUMockCDRIntegrationUtil
import com.wso2.cds.test.framework.utility.AUTestUtil

/**
 * Test Meta data withdrawal
 */
class MetaDataConsentWithdrawal extends AUTest{

    private String clientId

    @BeforeClass(alwaysRun = true)
    void "Setup"() {

        AURegistrationRequestBuilder requestBuilder = new AURegistrationRequestBuilder()

        //Load Meta Data to CDR Register
        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        String jti = String.valueOf(System.currentTimeMillis())

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(requestBuilder.getRegularClaimsWithGivenJti(jti))
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_201)
        clientId = AUTestUtil.parseResponseBody(registrationResponse, "client_id")
    }

    @Test
    void "TC016_Verify the Consent Withdrawal when the SP Inactive and ADR Active"() {

        // Tests for Validating Consent Withdrawal
        AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
        sleep(81000)

        doConsentAuthorisation(clientId)

        // Use method "generateCDRArrangementId()" to get CDRArrangementId for given Client ID
        generateCDRArrangementId(clientId)

        AUMockCDRIntegrationUtil.updateMetaDataOfCDRRegister(true, false, 2)
        sleep(81000)

        //Revoke the Consent
        Response response = AURequestBuilder.doRevokeConsent(clientId, cdrArrangementId)

        //Assert the consent revoke status code
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_204)
    }

}
