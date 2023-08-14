/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.keymanager.test.bnr

import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import com.wso2.cds.test.framework.constant.AUAccountProfile
import com.wso2.cds.test.framework.constant.AUBusinessUserPermission
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.data_provider.ConsentDataProviders
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.nio.charset.Charset

/**
 * Business Nominated Representative End To End Flow Test.
 */
class BnrEndToEndTest extends AUTest{

    def clientHeader
    AUConfigurationService auConfiguration = new AUConfigurationService()

    @BeforeClass(alwaysRun = true)
    void "Nominate Business User Representative"() {
        auConfiguration.setPsuNumber(2)
        clientHeader = "${Base64.encoder.encodeToString(getCDSClient().getBytes(Charset.defaultCharset()))}"

        //Get Sharable Account List and Nominate Business Representative with Authorize Permission
        def shareableElements = AUTestUtil.getSharableAccountsList(getSharableBankAccounts())

        String accountID =  shareableElements[AUConstants.PARAM_ACCOUNT_ID]
        String accountOwnerUserID = shareableElements[AUConstants.ACCOUNT_OWNER_USER_ID]
        String nominatedRepUserID = shareableElements[AUConstants.NOMINATED_REP_USER_ID]

        def updateResponse = updateSingleBusinessUserPermission(clientHeader, accountID, accountOwnerUserID,
                nominatedRepUserID, AUBusinessUserPermission.AUTHORIZE.getPermissionString())
        Assert.assertEquals(updateResponse.statusCode(), AUConstants.OK)
    }

    @Test (groups = "SmokeTest", dataProvider = "BankingApisBusinessProfile", dataProviderClass = ConsentDataProviders.class)
    void "CDS-486_Verify an accounts retrieval call after business profile selection and business accounts consented"(resourcePath) {

        //Consent Authorisation
        doConsentAuthorisation(null, AUAccountProfile.ORGANIZATION_B)
        generateUserAccessToken()

        int x_v_header = AUTestUtil.getBankingApiEndpointVersion(resourcePath.toString())

        //Get Accounts
        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                x_v_header, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${resourcePath}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), x_v_header)
        Assert.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        Assert.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.DATA))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST))
    }
}
