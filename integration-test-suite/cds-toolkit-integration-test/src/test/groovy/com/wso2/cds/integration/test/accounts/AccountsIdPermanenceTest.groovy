/*
<<<<<<< HEAD
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
=======
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
>>>>>>> main
 */

package com.wso2.cds.integration.test.accounts

import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.constant.AUAccountProfile
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AUIdEncryptorDecryptor
import com.wso2.cds.test.framework.utility.AUTestUtil
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.testng.asserts.SoftAssert

import java.nio.charset.Charset

/**
 * Test class for Accounts Id Permanence.
 */
class AccountsIdPermanenceTest extends AUTest {

    def clientHeader = "${Base64.encoder.encodeToString(getCDSClient().getBytes(Charset.defaultCharset()))}"
    String encryptedAccount1Id
    String encryptedAccount2Id
    String encryptedTransactionId
    String encryptedPayeeId
    private String secretKey = auConfiguration.getIDPermanence()
    private String userId = auConfiguration.getUserKeyManagerAdminName() + "@" + auConfiguration.getCommonTenantDomain()

    @BeforeClass(alwaysRun = true)
    void "Get User Access Token"() {
        doConsentAuthorisation()
        generateUserAccessToken()
    }

    @Test(priority = 1)
    void "TC1201001_Get Accounts"() {

        String bulkAccountRequestUrl = "${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}"

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get(bulkAccountRequestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        encryptedAccount1Id = AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[1]")
        encryptedAccount2Id = AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_BULK_ACCOUNTID_LIST}[0]")

        softAssertion.assertEquals(consentedAccount,
                AUIdEncryptorDecryptor.decrypt(encryptedAccount1Id, secretKey).split(":")[2])
        softAssertion.assertEquals(secondConsentedAccount,
                AUIdEncryptorDecryptor.decrypt(encryptedAccount2Id, secretKey).split(":")[2])

        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF)
                .contains(bulkAccountRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST)
                .contains(bulkAccountRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV)
                .contains(bulkAccountRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT)
                .contains(bulkAccountRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST)
                .contains(bulkAccountRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1203001_Get Balances For Specific Accounts"() {

        String requestBody = """
            {
              "data": {
                "accountIds": [
                  "${encryptedAccount1Id}", "${encryptedAccount2Id}"
                ]
              },
              "meta": {}
            }
        """.stripIndent()

    String bulkBalanceRequestUrl = "${AUConstants.CDS_PATH}${AUConstants.BULK_BALANCES_PATH}"

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_BALANCES))
                .post(bulkBalanceRequestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_BULK_BALANCE_LIST}[0]"), secretKey).
                split(":")[2])
        softAssertion.assertEquals(secondConsentedAccount, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_BULK_BALANCE_LIST}[1]"), secretKey).
                split(":")[2])

        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF)
                .contains(bulkBalanceRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST)
                .contains(bulkBalanceRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV)
                .contains(bulkBalanceRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT)
                .contains(bulkBalanceRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST)
                .contains(bulkBalanceRequestUrl.split(AUConstants.CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1204001_Get Account Balance"() {

        String accBalanceRequestUrl = "${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${encryptedAccount1Id}/balance"

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_BALANCES))
                .get(accBalanceRequestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_SINGLE_ACCOUNTID), secretKey).
                split(":")[2])
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF)
                .contains(accBalanceRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1205001_Get Account Detail"() {

        String accountRequestUrl = "${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${encryptedAccount1Id}"

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get(accountRequestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "data.accountId"), secretKey).
                split(":")[2])

        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF)
                .contains(accountRequestUrl.split(AUConstants.CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1205003_Get Not Consented Account Details"() {

        clientId = auConfiguration.getAppInfoClientID()

        //Authorise consent by selecting single account
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        doConsentAuthorisationViaRequestUriSingleAccount(scopes, requestUri.toURI(), clientId, AUAccountProfile.INDIVIDUAL)
        def userAccessToken2 = getUserAccessTokenResponse(clientId).tokens.accessToken

        SoftAssert softAssertion= new SoftAssert()

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${encryptedAccount2Id}")

        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)

        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_BANK_ACC)
        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.INVALID_BANK_ACC)
        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1207002_Get Invalid Transaction Detail"() {

        Response response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_TRANSACTIONS))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${encryptedAccount1Id}/" +
                        "transactions/204987583920")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)

       softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
               AUConstants.ERROR_CODE_INVALID_RESOURCE)
        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.INVALID_RESOURCE)
        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1208001_Get Direct Debits For Account"() {

        String directDebitRequestUrl = "${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${encryptedAccount1Id}/direct-debits"

        Response response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DIRECT_DEBIT))
                .get(directDebitRequestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "data.directDebitAuthorisations.accountId[0]"), secretKey).
                split(":")[2])

        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF)
                .contains(directDebitRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST)
                .contains(directDebitRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV)
                .contains(directDebitRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT)
                .contains(directDebitRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST)
                .contains(directDebitRequestUrl.split(AUConstants.CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1208002_Get Direct Debits For Invalid Account"() {

        Response response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DIRECT_DEBIT))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/2349679635270/direct-debits")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)

        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_BANK_ACC)
        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.INVALID_BANK_ACC)

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1210001_Get Direct Debits For Specific Accounts"() {

        String requestBody = """
            {
              "data": {
                "accountIds": [
                  "${encryptedAccount1Id}", "${encryptedAccount2Id}"
                ]
              },
              "meta": {}
            }
        """.stripIndent()

        String directDebitRequestUrl = "${AUConstants.CDS_PATH}${AUConstants.BULK_DIRECT_DEBITS_PATH}"

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DIRECT_DEBIT))
                .post(directDebitRequestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_DIRECT_DEBIT_AUTH}.accountId[0]"), secretKey).
                split(":")[2])

        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF)
                .contains(directDebitRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST)
                .contains(directDebitRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV)
                .contains(directDebitRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT)
                .contains(directDebitRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST)
                .contains(directDebitRequestUrl.split(AUConstants.CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1211001_Get Scheduled Payments for Account"() {

        String schedulePaymentRequestUrl = "${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${encryptedAccount1Id}/payments/scheduled"

        Response response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_SCHEDULED_PAYMENT))
                .get(schedulePaymentRequestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response,
                        "${AUConstants.RESPONSE_DATA_SCHEDULE_PAY}.scheduledPaymentId[0]"), secretKey)
                .split(":")[0])
        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response,
                        "${AUConstants.RESPONSE_DATA_SCHEDULE_PAY}.from.accountId[0]"), secretKey)
                .split(":")[2])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response,
                        "${AUConstants.RESPONSE_DATA_SCHEDULE_PAY}.paymentSet[0].to.accountId[0]"), secretKey)
                .split(":")[0])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response,
                        "${AUConstants.RESPONSE_DATA_SCHEDULE_PAY}.paymentSet[0].to.payeeId[0]"), secretKey)
                .split(":")[0])

        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF)
                .contains(schedulePaymentRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST)
                .contains(schedulePaymentRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV)
                .contains(schedulePaymentRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT)
                .contains(schedulePaymentRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST)
                .contains(schedulePaymentRequestUrl.split(AUConstants.CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1213001_Get Scheduled Payments For Specific Accounts"() {

        String requestBody = """
            {
              "data": {
                "accountIds": [
                  "${encryptedAccount1Id}", "${encryptedAccount2Id}"
                ]
              },
              "meta": {}
            }
        """.stripIndent()

        String schedulePaymentRequestUrl = "${AUConstants.CDS_PATH}${AUConstants.BULK_SCHEDULE_PAYMENTS_PATH}"

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_SCHEDULED_PAYMENT))
                .post(schedulePaymentRequestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response,
                        "${AUConstants.RESPONSE_DATA_SCHEDULE_PAY}.scheduledPaymentId[0]"), secretKey)
                .split(":")[0])
        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response,
                        "${AUConstants.RESPONSE_DATA_SCHEDULE_PAY}.from.accountId[0]"), secretKey)
                .split(":")[2])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response,
                        "${AUConstants.RESPONSE_DATA_SCHEDULE_PAY}.paymentSet[0].to.accountId[0]"), secretKey)
                .split(":")[0])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response,
                        "${AUConstants.RESPONSE_DATA_SCHEDULE_PAY}.paymentSet[0].to.payeeId[0]"), secretKey)
                .split(":")[0])

        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF)
                .contains(schedulePaymentRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST)
                .contains(schedulePaymentRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV)
                .contains(schedulePaymentRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT)
                .contains(schedulePaymentRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST)
                .contains(schedulePaymentRequestUrl.split(AUConstants.CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1206001_Get Transactions For Account"() {

        String transactionRequestUrl = "${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${encryptedAccount1Id}/transactions"

        Response response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_TRANSACTIONS))
                .get(transactionRequestUrl)

        encryptedTransactionId = AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_TRANSACTION_LIST}.transactionId[0]")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_TRANSACTION_LIST}[0]"), secretKey).
                split(":")[2])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(encryptedTransactionId, secretKey).
                split(":")[0])

        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF)
                .contains(transactionRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST)
                .contains(transactionRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV)
                .contains(transactionRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT)
                .contains(transactionRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST)
                .contains(transactionRequestUrl.split(AUConstants.CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1206001_Get Transactions For Account", priority = 1)
    void "TC1207001_Get Transaction Detail"() {

        String transactionRequestUrl = "${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${encryptedAccount1Id}/" +
                "transactions/$encryptedTransactionId"

        Response response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_TRANSACTIONS))
                .get(transactionRequestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_SINGLE_ACCOUNTID), secretKey).
                split(":")[2])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, AUConstants.RESPONSE_DATA_TRANSACTIONID), secretKey).
                split(":")[0])

        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF)
                .contains(transactionRequestUrl.split(AUConstants.CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test
    void "TC1202001_Get Bulk Balances"() {

        String balanceRequestUrl = "${AUConstants.CDS_PATH}${AUConstants.BULK_BALANCES_PATH}"

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_BALANCES))
                .get(balanceRequestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_BULK_BALANCE_LIST}.accountId[1]"), secretKey).
                split(":")[2])
        softAssertion.assertEquals(secondConsentedAccount, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_BULK_BALANCE_LIST}.accountId[0]"), secretKey).
                split(":")[2])

        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF)
                .contains(balanceRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST)
                .contains(balanceRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV)
                .contains(balanceRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT)
                .contains(balanceRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST)
                .contains(balanceRequestUrl.split(AUConstants.CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test
    void "TC1203002_Get Balances For Specific Invalid Accounts"() {

        String requestBody = """
            {
              "data": {
                "accountIds": [
                  "eryvsy35278feegyegyse", "yvwylyg89"
                ]
              },
              "meta": {}
            }
        """.stripIndent()

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_BALANCES))
                .post("${AUConstants.CDS_PATH}${AUConstants.BULK_BALANCES_PATH}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_422)

        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_BANK_ACC)
        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.INVALID_BANK_ACC)

        softAssertion.assertAll()
    }

    @Test
    void "TC1204002_Get Invalid Account Balance"() {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_BALANCES))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/32125763242/balance")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)

        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_BANK_ACC)
        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.INVALID_BANK_ACC)
        softAssertion.assertAll()
    }

    @Test
    void "TC1205002_Get Invalid Account Detail"() {

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/342678987")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)

        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_BANK_ACC)
        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.INVALID_BANK_ACC)
        softAssertion.assertAll()
    }

    @Test
    void "TC1206002_Get Transactions For Invalid Account"() {

        Response response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_TRANSACTIONS))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/34867635209/transactions")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)

        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_BANK_ACC)
        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.INVALID_BANK_ACC)
        softAssertion.assertAll()
    }

    @Test
    void "TC1209001_Get Bulk Direct Debits"() {

        String directDebitRequestUrl = "${AUConstants.CDS_PATH}${AUConstants.BULK_DIRECT_DEBITS_PATH}"

        Response response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DIRECT_DEBIT))
                .get(directDebitRequestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_DIRECT_DEBIT_AUTH}.accountId[1]"), secretKey).
                split(":")[2])
        softAssertion.assertEquals(secondConsentedAccount, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_DIRECT_DEBIT_AUTH}.accountId[0]"), secretKey).
                split(":")[2])

        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF)
                .contains(directDebitRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST)
                .contains(directDebitRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV)
                .contains(directDebitRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT)
                .contains(directDebitRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST)
                .contains(directDebitRequestUrl.split(AUConstants.CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test
    void "TC1210002_Get Direct Debits For Specific Invalid Accounts"() {

        String requestBody = """
            {
              "data": {
                "accountIds": [
                  "4327823409", "455325897"
                ]
              },
              "meta": {}
            }
        """.stripIndent()

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DIRECT_DEBIT))
                .post("${AUConstants.CDS_PATH}${AUConstants.BULK_DIRECT_DEBITS_PATH}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_422)

        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_BANK_ACC)
        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.INVALID_BANK_ACC)
        softAssertion.assertAll()
    }

    @Test
    void "TC1212001_Get Scheduled Payments Bulk"() {

        String schedulePaymentRequestUrl = "${AUConstants.CDS_PATH}${AUConstants.BULK_SCHEDULE_PAYMENTS_PATH}"

        Response response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_SCHEDULED_PAYMENT))
                .get(schedulePaymentRequestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_SCHEDULE_PAY}.scheduledPaymentId[0]"),
                secretKey).split(":")[0])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_SCHEDULE_PAY}.from.accountId[0]"),
                secretKey).split(":")[0])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_SCHEDULE_PAY}.paymentSet[0].to.accountId[0]"),
                secretKey).split(":")[0])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_SCHEDULE_PAY}.paymentSet[0].to.payeeId[0]"),
                secretKey).split(":")[0])


        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_SCHEDULE_PAY}.scheduledPaymentId[1]"),
                secretKey).split(":")[0])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_SCHEDULE_PAY}.from.accountId[1]"),
                secretKey).split(":")[0])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_SCHEDULE_PAY}.paymentSet[1].to.accountId[0]"),
                secretKey).split(":")[0])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_SCHEDULE_PAY}.paymentSet[1].to.payeeId[0]"),
                secretKey).split(":")[0])

        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF)
                .contains(schedulePaymentRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST)
                .contains(schedulePaymentRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV)
                .contains(schedulePaymentRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT)
                .contains(schedulePaymentRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST)
                .contains(schedulePaymentRequestUrl.split(AUConstants.CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test
    void "TC1213002_Get Scheduled Payments For Specific Invalid Accounts"() {

        String requestBody = """
            {
              "data": {
                "accountIds": [
                  "4327823409", "455325897"
                ]
              },
              "meta": {}
            }
        """.stripIndent()

        def response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_SCHEDULED_PAYMENT))
                .post("${AUConstants.CDS_PATH}${AUConstants.BULK_SCHEDULE_PAYMENTS_PATH}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_422)

        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_BANK_ACC)
        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.INVALID_BANK_ACC)

        softAssertion.assertAll()
    }

    @Test
    void "TC1215002_Get Invalid Payee Detail"() {

        Response response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PAYEES))
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_PAYEES}/1426558421")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)

        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_RESOURCE)
        softAssertion.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                AUConstants.INVALID_RESOURCE)

        softAssertion.assertAll()
    }

    @Test (priority = 2)
    void "TC1214001_Get Payees"() {

        String payeeRequestUrl = "${AUConstants.CDS_PATH}${AUConstants.BULK_PAYEES}"

        Response response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PAYEES))
                .get(payeeRequestUrl)

        encryptedPayeeId = AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_PAYEE}.payeeId[0]")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(encryptedPayeeId,
                secretKey).split(":")[0])

        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF)
                .contains(payeeRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_FIRST)
                .contains(payeeRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_PREV)
                .contains(payeeRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_NEXT)
                .contains(payeeRequestUrl.split(AUConstants.CDS_PATH)[1]))
        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_LAST)
                .contains(payeeRequestUrl.split(AUConstants.CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1214001_Get Payees", priority = 2)
    void "TC1215001_Get Payee Detail"() {

        String payeeRequestUrl = "${AUConstants.CDS_PATH}${AUConstants.BULK_PAYEES}/${encryptedPayeeId}"

        Response response = AURequestBuilder.buildBasicRequestWithCustomHeaders(userAccessToken,
                AUConstants.X_V_HEADER_ACCOUNTS, clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PAYEES))
                .get(payeeRequestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                AUTestUtil.parseResponseBody(response, "${AUConstants.RESPONSE_DATA_PAYEEID}"),
                secretKey).split(":")[0])

        softAssertion.assertTrue(AUTestUtil.parseResponseBody(response, AUConstants.LINKS_SELF)
                .contains(payeeRequestUrl.split(AUConstants.CDS_PATH)[1]))

        softAssertion.assertAll()
    }
}
