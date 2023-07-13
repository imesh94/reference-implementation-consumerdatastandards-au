/**
 * Copyright (c) 2022, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.integration.test.tokenEndpoint

import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil
import java.nio.charset.Charset
import org.testng.Assert
import org.testng.annotations.Test
import org.testng.asserts.SoftAssert


/**
 * for testing User access token with the test context.
 * new User access token will be generated if there is no already generated user access token.
 */
class AccessTokenTest extends AUTest {

    private List<AUAccountScope> scopeArrayList = [
            AUAccountScope.BANK_ACCOUNT_BASIC_READ,
            AUAccountScope.BANK_TRANSACTION_READ,
            AUAccountScope.BANK_CUSTOMER_DETAIL_READ
    ]

    private final String ACCOUNTS_BASIC_OPENID_SCOPE_LIST = "bank:accounts.basic:read bank:accounts.detail:" +
            "read openid"
    private final String ACCOUNTS_BASIC_ACCOUNT_DETAIL_OPENID_SCOPE_LIST = "bank:accounts.basic:read bank:" +
            "accounts.detail:read openid"
    private AccessTokenResponse userAccessToken

    @Test
    void "OB-1264-Invoke token endpoint for user access token without private-key JWT client authentication"() {

        doConsentAuthorisation( auConfiguration.getAppInfoClientID())
        Assert.assertNotNull(authorisationCode)

        def errorObject = AURequestBuilder.getUserTokenErrorResponse(authorisationCode,
                auConfiguration.getAppInfoRedirectURL(),auConfiguration.getAppInfoClientID(),false)
        Assert.assertEquals(errorObject.toJSONObject().get(AUConstants.ERROR_DESCRIPTION), "Request does not follow the" +
                " registered token endpoint auth method private_key_jwt")
    }

    @Test
    void "OB-1265-Invoke token endpoint for user access token without MTLS transport security"() {

        doConsentAuthorisation( auConfiguration.getAppInfoClientID())
        Assert.assertNotNull(authorisationCode)

        def errorObject = AURequestBuilder.getUserTokenErrorResponse(authorisationCode,
                auConfiguration.getAppInfoRedirectURL(), auConfiguration.getAppInfoClientID(), true,
                false)
        Assert.assertEquals(errorObject.toJSONObject().get(AUConstants.ERROR_DESCRIPTION), "Transport certificate" +
                " not found in the request")
    }

    @Test
    void "OB-1266_Invoke token endpoint for user access token with a different redirect uri"() {

        doConsentAuthorisation( auConfiguration.getAppInfoClientID())
        Assert.assertNotNull(authorisationCode)

        def errorObject = AURequestBuilder.getUserTokenErrorResponse(authorisationCode,
                AUConstants.DCR_ALTERNATE_REDIRECT_URI)
        Assert.assertEquals(errorObject.toJSONObject().get(AUConstants.ERROR_DESCRIPTION), "Callback url mismatch")

    }

    @Test
    void "OB-1272_Invoke token endpoint for user access token with 'RS256' as the signature algorithm"() {

        doConsentAuthorisation( auConfiguration.getAppInfoClientID())
        Assert.assertNotNull(authorisationCode)

        def errorObject = AURequestBuilder.getUserTokenErrorResponse(authorisationCode,
                auConfiguration.getAppInfoRedirectURL(), auConfiguration.getAppInfoClientID(), true,
                true,"RS256")
        Assert.assertEquals(errorObject.toJSONObject().get(AUConstants.ERROR_DESCRIPTION), "Registered algorithm " +
                "does not match with the token signed algorithm")

    }

    @Test
    void "OB-1273_Invoke token endpoint for user access token with 'PS512' as the signature algorithm"() {

        doConsentAuthorisation( auConfiguration.getAppInfoClientID())
        Assert.assertNotNull(authorisationCode)

        def errorObject = AURequestBuilder.getUserTokenErrorResponse(authorisationCode,
                auConfiguration.getAppInfoRedirectURL(), auConfiguration.getAppInfoClientID(), true,
                true,"PS512")
        Assert.assertEquals(errorObject.toJSONObject().get(AUConstants.ERROR_DESCRIPTION), "Registered algorithm " +
                "does not match with the token signed algorithm")

    }

    @Test (priority = 1)
    void "OB-1267_Invoke token endpoint for user access token with a subset of authorized scopes"() {

        // scopes authorized for the consent
        scopes = [
                AUAccountScope.BANK_ACCOUNT_BASIC_READ,
                AUAccountScope.BANK_ACCOUNT_DETAIL_READ,
        ]
        doConsentAuthorisation(auConfiguration.getAppInfoClientID())
        Assert.assertNotNull(authorisationCode)

        //scopes requested for the user access token
        scopes = [
                AUAccountScope.BANK_ACCOUNT_BASIC_READ,
        ]
        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, scopes,
                AUConstants.CODE_VERIFIER)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
        Assert.assertNotNull(userAccessToken.tokens.refreshToken)
        Assert.assertEquals(userAccessToken.toJSONObject().get("scope"), ACCOUNTS_BASIC_OPENID_SCOPE_LIST)
    }

    @Test(priority = 1, dependsOnMethods = "OB-1267_Invoke token endpoint for user access token with a subset of authorized scopes")
    void "OB-1268_Invoke accounts retrieval with access token only bound to bank account basic read scopes"() {

        def cdsClient = "${auConfiguration.getAppInfoClientID()}:${auConfiguration.getAppInfoClientSecret()}"
        def clientHeader = "${Base64.encoder.encodeToString(cdsClient.getBytes(Charset.defaultCharset()))}"

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken.tokens.accessToken.toString(), AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.BULK_ACCOUNT_PATH}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertAll()
    }

    @Test
    void "OB-1270_Invoke token endpoint for user access token with a unauthorized scope"() {

        scopes = [
                AUAccountScope.BANK_ACCOUNT_BASIC_READ
        ]

        doConsentAuthorisation( auConfiguration.getAppInfoClientID())
        Assert.assertNotNull(authorisationCode)

        scopes = [
                AUAccountScope.BANK_REGULAR_PAYMENTS_READ
        ]

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, scopes, AUConstants.CODE_VERIFIER)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
        Assert.assertNotNull(userAccessToken.tokens.refreshToken)
        Assert.assertEquals(userAccessToken.toJSONObject().get("scope"), "bank:accounts.basic:read openid")
    }

    @Test
    void "OB-1271_Invoke token endpoint for user access token with a set of authorized and unauthorized scopes"() {

        scopes = [
                AUAccountScope.BANK_ACCOUNT_BASIC_READ,
                AUAccountScope.BANK_ACCOUNT_DETAIL_READ
        ]

        doConsentAuthorisation( auConfiguration.getAppInfoClientID())
        Assert.assertNotNull(authorisationCode)

        //Profile scope is additionally requested
        scopes = [
                AUAccountScope.PROFILE,
                AUAccountScope.BANK_ACCOUNT_BASIC_READ,
                AUAccountScope.BANK_ACCOUNT_DETAIL_READ
        ]

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, scopes, AUConstants.CODE_VERIFIER)
        Assert.assertNotNull(userAccessToken.tokens.accessToken)
        Assert.assertNotNull(userAccessToken.tokens.refreshToken)
        Assert.assertNotNull(userAccessToken.getCustomParameters().get("cdr_arrangement_id"))
        Assert.assertEquals(userAccessToken.toJSONObject().get("scope"),ACCOUNTS_BASIC_ACCOUNT_DETAIL_OPENID_SCOPE_LIST)
    }

}
