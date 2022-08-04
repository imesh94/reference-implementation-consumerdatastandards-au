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

import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.AUTest
import org.testng.Assert
import org.testng.annotations.Test
import com.wso2.cds.test.framework.request_builder.AUAuthorisationBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil

/**
 * Test Pushed authorization flow for given scope
 */
class PushedAuthorisationFlowTest extends AUTest{

    private String headerString = auConfiguration.getUserBasicAuthName()+ ":" +auConfiguration.getUserBasicAuthPWD()
    AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder()
    String requestUri
    private List<AUAccountScope> scopeArrayList = [
            AUAccountScope.BANK_ACCOUNT_BASIC_READ,
            AUAccountScope.BANK_TRANSACTION_READ,
            AUAccountScope.BANK_CUSTOMER_DETAIL_READ
    ]

    @Test(priority = 1)
    void "TC0205001_Data Recipients Initiate authorisation request using PAR"() {

        def response = authorisationBuilder.doPushAuthorisationRequest(headerString, scopeArrayList, AUConstants.DEFAULT_SHARING_DURATION,
                true, cdrArrangementId)

        requestUri = AUTestUtil.parseResponseBody(response, "requestUri")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_201)
        Assert.assertNotNull(requestUri)
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "expiresIn"))
    }

    @Test(priority = 1, dependsOnMethods = "TC0205001_Data Recipients Initiate authorisation request using PAR")
    void "TC0205002_Initiate consent authorisation flow with pushed authorisation request uri"() {
        doConsentAuthorisationViaRequestUri(scopes, requestUri.toURI())
        Assert.assertNotNull(authorisationCode)
    }

}

