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
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUDCRConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURegistrationRequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

class DeleteClientRegistration{

    private List<String> scopes = [
            AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ.getScopeString(),
            AUConstants.SCOPES.BANK_TRANSACTION_READ.getScopeString(),
            AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ.getScopeString()
    ]

    private String accessToken
    private String clientId
    private String registrationPath = AUDCRConstants.REGISTRATION_ENDPOINT
    private String invalidClientId = "invalidclientid"
    File clientIdFile = new File('clientId.txt')
    File accessTokenFile = new File('accessToken.txt')

    @BeforeClass (alwaysRun = true)
    void "Initialize Test Suite"() {
        TestSuite.init()
    }

    @Test (groups = "SmokeTest")
    void "TC0101009_Get access token"() {

        clientId = clientIdFile.text

        accessToken = AURequestBuilder.getApplicationToken(scopes, clientId)
        accessTokenFile.write(accessToken)

        Assert.assertNotNull(accessToken)
    }
    
    @Test (dependsOnMethods = "TC0101009_Get access token")
    void "TC0104001_Delete application with invalid client id"() {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .delete(registrationPath + invalidClientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_401)
    }

    @Test (groups = "SmokeTest", dependsOnMethods = "TC0101009_Get access token", priority = 1)
    void "TC0104002_Delete application"() {

        clientId = clientIdFile.text
        accessToken = accessTokenFile.text

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .delete(registrationPath + clientId)

        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_204)
    }
}