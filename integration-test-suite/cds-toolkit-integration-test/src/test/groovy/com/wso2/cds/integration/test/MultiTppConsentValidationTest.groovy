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
import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import com.wso2.cds.test.framework.request_builder.AUAuthorisationBuilder
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil

/**
 * Test Multiple tpp consents
 */
class MultiTppConsentValidationTest extends AUTest{

    AUConfigurationService auConfiguration = new AUConfigurationService()
    String clientID

    @BeforeClass(alwaysRun = true)
    void setup() {
        auConfiguration.setTppNumber(1)

        deleteApplicationIfExists()
        //Register Second TPP.
        def registrationResponse = tppRegistration()
        Assert.assertEquals(registrationResponse.statusCode(), AUConstants.CREATED)

        clientID = AUTestUtil.parseResponseBody(registrationResponse, "client_id")
        List<String> redirectURI = AUTestUtil.parseResponseBodyList(registrationResponse, "redirect_uris")

        //Write Client Id of TPP2 to config file.
        AUTestUtil.writeXMLContent(auConfiguration.getOBXMLFile().toString(), "Application",
                "ClientID", clientID, auConfiguration.getTppNumber())
    }

    @Test
    void "OB-1313_Revoke sharing arrangement bound to different Tpp"(){
        auConfiguration.setTppNumber(0)

        doConsentAuthorisation()
        Assert.assertNotNull(authorisationCode)

        //obtain cdr_arrangement_id from token response
        AccessTokenResponse userAccessTokenRes = getUserAccessTokenResponse()
        String cdrArrangementId = userAccessTokenRes.getCustomParameters().get("cdr_arrangement_id")
        Assert.assertNotNull(cdrArrangementId)

        //retrieve consumer data successfully
        Response response = AURequestBuilder
                .buildBasicRequest(userAccessTokenRes.tokens.accessToken.toString(), AUConstants.CDR_ENDPOINT_VERSION)
                .header(AUConstants.PARAM_FAPI_AUTH_DATE,AUConstants.VALUE_FAPI_AUTH_DATE)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        auConfiguration.setTppNumber(1)

        //Get application access token for TPP2
        setApplicationScope(["openid"])
        String secondAppAccessToken = getApplicationAccessToken(clientID)
        Assert.assertNotNull(secondAppAccessToken)

        auConfiguration.setTppNumber(0)

        AUAuthorisationBuilder authBuilder = new AUAuthorisationBuilder()
        //revoke sharing arrangement using token of TPP2 and cdrArrangementId of TPP1
        Response revocationResponse= authBuilder.doArrangementRevocationWithPkjwt(secondAppAccessToken,cdrArrangementId)
        Assert.assertEquals(revocationResponse.statusCode(), AUConstants.STATUS_CODE_404)

    }
}

