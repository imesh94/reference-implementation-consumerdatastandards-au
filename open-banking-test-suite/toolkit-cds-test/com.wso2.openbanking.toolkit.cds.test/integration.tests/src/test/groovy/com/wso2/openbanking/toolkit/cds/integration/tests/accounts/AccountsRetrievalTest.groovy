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

package com.wso2.openbanking.toolkit.cds.integration.tests.accounts

import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.testng.asserts.SoftAssert

import java.nio.charset.Charset

/**
 * Accounts Retrieval Tests.
 */
class AccountsRetrievalTest extends AbstractAUTests {

    static final String CDS_PATH = AUConstants.CDS_PATH
    def clientHeader = "${Base64.encoder.encodeToString(cdsClient.getBytes(Charset.defaultCharset()))}"

    @BeforeClass(alwaysRun = true)
    void "Get User Access Token"() {
        doConsentAuthorisation()
        generateUserAccessToken()
    }

    @Test(groups = "SmokeTest")
    void "TC0301001_Retrieve bulk accounts list"() {

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        softAssertion.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.accounts.accountId[0]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.accounts.accountId[1]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
        softAssertion.assertAll()
    }

    @Test
    void "TC0301002_Retrieve consented single accounts"() {

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${AUConstants.accountID}")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.accountId"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        softAssertion.assertAll()
    }

    // Need enable the account id validation in IAM
    @Test
    void "TC0301003_Retrieve invalid single accounts"() {

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_INTERACTION_ID, AUConstants.UUID)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/11059970")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)
        softAssertion.assertNotNull(response.getHeader(AUConstants.X_V_HEADER))
        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_INVALID_BANK_ACC)
    }

    @Test
    void "TC0302001_Retrieve valid bulk balances"() {

        String requestBody = """
            {
              "data": {
                "accountIds": [
                  "${AUConstants.accountID}", "${AUConstants.accountID}"
                ]
              },
              "meta": {}
            }
        """.stripIndent()

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)

                .contentType(ContentType.JSON)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_BALANCES))
                .post("${CDS_PATH}${AUConstants.BULK_BALANCES_PATH}")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.balances.accountId[0]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.balances.accountId[1]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
        softAssertion.assertAll()
    }

    @Test
    void "TC0302002_Retrieve invalid bulk balances"() {

        String requestBody = """
            {
              "data": {
                "accountIds": [
                  "${AUConstants.accountID}", "1234"
                ]
              },
              "meta": {}
            }
        """.stripIndent()

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_BALANCES))
                .post("${CDS_PATH}${AUConstants.BULK_BALANCES_PATH}")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_422)
        softAssertion.assertNotNull(response.getHeader(AUConstants.X_V_HEADER))

        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_BANK_ACC)
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_BANK_ACC)
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL), AUConstants
                    .ACCOUNT_ID_NOT_FOUND)
        } else {
            softAssertion.assertEquals(response.jsonPath().get("errors[0].code"), "0001 – Account not able to be found")
        }

        softAssertion.assertAll()
    }

    @Test(groups = "SmokeTest")
    void "TC0302003_Get bulk balances"() {

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_BALANCES))
                .get("${CDS_PATH}${AUConstants.BULK_BALANCES_PATH}")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.balances.accountId[0]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.balances.accountId[1]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
        softAssertion.assertAll()
    }

    @Test
    void "TC0302004_Retrieve balances for specific account"() {

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_BALANCES))
                    .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${AUConstants.accountID}/balance")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.accountId"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        softAssertion.assertAll()
    }

    @Test(groups = "SmokeTest", priority = 1)
    void "TC0303001_Retrieve transaction for specific account"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_TRANSACTIONS))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${AUConstants.accountID}/transactions")

        transactionId = TestUtil.parseResponseBody(response, "data.transactions.transactionId[0]")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.transactions.accountId"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
        softAssertion.assertAll()
    }

    @Test(dependsOnMethods = "TC0303001_Retrieve transaction for specific account", priority = 1)
    void "TC0303002_Retrieve transaction details for specific account"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_TRANSACTIONS))
                .get("${CDS_PATH}${AUConstants.getBULK_ACCOUNT_PATH()}/${AUConstants.accountID}/transactions/$transactionId")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.accountId"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        softAssertion.assertAll()
    }

    @Test
    void "TC0302001_Retrieve Direct Debits For Specific Accounts"() {

        String requestBody = """
            {
              "data": {
                "accountIds": [
                  "${AUConstants.accountID}", "${AUConstants.accountID2}"
                ]
              },
              "meta": {}
            }
        """.stripIndent()

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DIRECT_DEBIT))
                .post("${CDS_PATH}${AUConstants.BULK_DIRECT_DEBITS_PATH}")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.accountId"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.authorisedEntity"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.lastDebitDateTime"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.lastDebitAmount"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
        softAssertion.assertAll()
    }

    @Test(groups = "SmokeTest")
    void "TC0401002_Retrieve bulk direct debits"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DIRECT_DEBIT))
                .get("${CDS_PATH}${AUConstants.BULK_DIRECT_DEBITS_PATH}")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.accountId[0]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.authorisedEntity[0]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.lastDebitDateTime[0]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.lastDebitAmount[0]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.accountId[1]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.authorisedEntity[1]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.lastDebitDateTime[1]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.lastDebitAmount[1]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
        softAssertion.assertAll()
    }

    @Test
    void "TC0401003_Get Direct Debits For Account"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DIRECT_DEBIT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${AUConstants.accountID}/direct-debits")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.accountId"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.authorisedEntity"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.lastDebitDateTime"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.lastDebitAmount"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
        softAssertion.assertAll()
    }

    @Test
    void "TC0402001_Retrieve Scheduled Payments For Specific Accounts"() {

        String requestBody = """
            {
              "data": {
                "accountIds": [
                    "${AUConstants.accountID}", "${AUConstants.accountID2}"
                ]
              },
              "meta": {}
            }
        """.stripIndent()

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_SCHEDULED_PAYMENT))
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .post("${CDS_PATH}${AUConstants.BULK_SCHEDULE_PAYMENTS_PATH}")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.scheduledPaymentId"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.status"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.paymentSet"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.recurrence"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "meta"))
        softAssertion.assertAll()
    }

    @Test(groups = "SmokeTest")
    void "TC0402002_Retrieve bulk Scheduled Payments"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_SCHEDULED_PAYMENT))
                .get("${CDS_PATH}${AUConstants.BULK_SCHEDULE_PAYMENTS_PATH}")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.scheduledPaymentId[0]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.payerReference[0]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.status[0]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.paymentSet[0]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.recurrence[0]"))

        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.scheduledPaymentId[1]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.payerReference[1]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.status[1]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.paymentSet[1]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.recurrence[1]"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "meta"))
        softAssertion.assertAll()
    }

    @Test
    void "TC0402003_Get Scheduled Payments For Account"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_SCHEDULED_PAYMENT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${AUConstants.accountID}/payments/scheduled")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.scheduledPaymentId"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.payerReference"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.status"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.paymentSet"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.scheduledPayments.recurrence"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "meta"))
        softAssertion.assertAll()
    }

    @Test(groups = "SmokeTest", priority = 2)
    void "TC0503001_Get Payees"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PAYEES))
                .get("${CDS_PATH}${AUConstants.BULK_PAYEES}")

        payeeId = TestUtil.parseResponseBody(response, "data.payees.payeeId[0]")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.payees.payeeId"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.payees.type"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.payees.creationDate"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "meta"))
        softAssertion.assertAll()
    }

    @Test(dependsOnMethods = "TC0503001_Get Payees", priority = 2)
    void "TC0503002_Get Payee Detail"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PAYEES))
                .get("${CDS_PATH}${AUConstants.BULK_PAYEES}/${payeeId}")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.payeeId"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.type"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.payeeUType"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.domestic"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.biller"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.international"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        softAssertion.assertAll()
    }

    @Test(priority = 3)
    void "OB-1146_Get basic accounts data without bank accounts basic read permissions"() {

        scopes = [
                AUConstants.SCOPES.BANK_ACCOUNT_DETAIL_READ,
                AUConstants.SCOPES.BANK_TRANSACTION_READ,
                AUConstants.SCOPES.BANK_PAYEES_READ,
                AUConstants.SCOPES.BANK_REGULAR_PAYMENTS_READ,
                AUConstants.SCOPES.BANK_CUSTOMER_BASIC_READ,
                AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ
        ]
        doConsentAuthorisation()
        generateUserAccessToken()

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BULK_ACCOUNT_PATH))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_RESOURCE_FORBIDDEN)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
            "The access token does not allow you to access the requested resource")
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .RESOURCE_FORBIDDEN)
        } else {
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, "fault.message"),
                    "The access token does not allow you to access the requested resource")
        }
        softAssertion.assertAll()
    }

    @Test(priority = 3)
    void "OB-1147_Get detailed account data without bank accounts detail read permissions"() {

        scopes = [
                AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
                AUConstants.SCOPES.BANK_TRANSACTION_READ,
                AUConstants.SCOPES.BANK_PAYEES_READ,
                AUConstants.SCOPES.BANK_REGULAR_PAYMENTS_READ,
                AUConstants.SCOPES.BANK_CUSTOMER_BASIC_READ,
                AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ
        ]
        doConsentAuthorisation()
        generateUserAccessToken()

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.SINGLE_ACCOUNT_PATH))
                .get("${CDS_PATH}${AUConstants.SINGLE_ACCOUNT_PATH}")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_RESOURCE_FORBIDDEN)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                    "The access token does not allow you to access the requested resource")
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .RESOURCE_FORBIDDEN)
        } else {
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, "fault.message"),
                    "The access token does not allow you to access the requested resource")
        }
        softAssertion.assertAll()
    }

    @Test(priority = 3)
    void "OB-1148_Get transactions data without bank transactions read permission"() {

        scopes = [
                AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
                AUConstants.SCOPES.BANK_ACCOUNT_DETAIL_READ,
                AUConstants.SCOPES.BANK_PAYEES_READ,
                AUConstants.SCOPES.BANK_REGULAR_PAYMENTS_READ,
                AUConstants.SCOPES.BANK_CUSTOMER_BASIC_READ,
                AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ
        ]

        doConsentAuthorisation()
        generateUserAccessToken()

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.SINGLE_ACCOUNT_PATH))
                .get("${CDS_PATH}${AUConstants.GET_TRANSACTIONS}")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        if (TestConstants.SOLUTION_VERSION_300.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_RESOURCE_FORBIDDEN)
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
            "The access token does not allow you to access the requested resource")
            Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .RESOURCE_FORBIDDEN)
        } else {
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, "fault.message"),
                    "The access token does not allow you to access the requested resource")
        }
        softAssertion.assertAll()
    }

    @Test(priority = 3)
    void "OB-1149_Get transactions data with bank transactions read and without bank accounts basic read permission"() {

        scopes = [
                AUConstants.SCOPES.BANK_ACCOUNT_DETAIL_READ,
                AUConstants.SCOPES.BANK_TRANSACTION_READ,
                AUConstants.SCOPES.BANK_PAYEES_READ,
                AUConstants.SCOPES.BANK_REGULAR_PAYMENTS_READ,
                AUConstants.SCOPES.BANK_CUSTOMER_BASIC_READ,
                AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ
        ]

        doConsentAuthorisation()
        generateUserAccessToken()

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(AUConstants.X_FAPI_CUSTOMER_IP_ADDRESS , AUConstants.IP)
                .header(AUConstants.X_CDS_CLIENT_HEADERS , clientHeader)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.SINGLE_ACCOUNT_PATH))
                .get("${CDS_PATH}${AUConstants.GET_TRANSACTIONS}")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        softAssertion.assertTrue(response.getHeader(AUConstants.CONTENT_TYPE).contains(AUConstants.ACCEPT))

        softAssertion.assertNotNull(response.getHeader(AUConstants.X_FAPI_INTERACTION_ID))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "data.transactions.accountId"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.self"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.first"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.prev"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.next"))
        softAssertion.assertNotNull(TestUtil.parseResponseBody(response, "links.last"))
        softAssertion.assertAll()
    }

    @Test(priority = 3)
    void "TC0401004_Get Direct Debits without regular_payments read permissions"() {

        scopes = [
                AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
                AUConstants.SCOPES.BANK_ACCOUNT_DETAIL_READ,
                AUConstants.SCOPES.BANK_TRANSACTION_READ
        ]

        doConsentAuthorisation()
        generateUserAccessToken()

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DIRECT_DEBIT))
                .get("${CDS_PATH}${AUConstants.BULK_DIRECT_DEBITS_PATH}")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_RESOURCE_FORBIDDEN)
        Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                .RESOURCE_FORBIDDEN)
        Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
        "The access token does not allow you to access the requested resource")
        softAssertion.assertAll()
    }

    @Test(priority = 3)
    void "TC0402004_Get Scheduled Payments without regular_payments read permissions"() {

        scopes = [
                AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
                AUConstants.SCOPES.BANK_ACCOUNT_DETAIL_READ,
                AUConstants.SCOPES.BANK_TRANSACTION_READ
        ]

        doConsentAuthorisation()
        generateUserAccessToken()

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_SCHEDULED_PAYMENT))
                .get("${CDS_PATH}${AUConstants.BULK_SCHEDULE_PAYMENTS_PATH}")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)

        Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_RESOURCE_FORBIDDEN)
        Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                .RESOURCE_FORBIDDEN)
        Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                "The access token does not allow you to access the requested resource")
    }

    @Test(priority = 3)
    void "TC0503003_Get Payee without payees read permissions"() {

        scopes = [
                AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
                AUConstants.SCOPES.BANK_ACCOUNT_DETAIL_READ,
                AUConstants.SCOPES.BANK_TRANSACTION_READ
        ]

        doConsentAuthorisation()

        generateUserAccessToken()

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PAYEES))
                .get("${CDS_PATH}${AUConstants.BULK_PAYEES}")

        SoftAssert softAssertion = new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_403)
        softAssertion.assertEquals(response.getHeader(AUConstants.X_V_HEADER).toInteger(), AUConstants.X_V_HEADER_ACCOUNTS)
        Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                AUConstants.ERROR_CODE_RESOURCE_FORBIDDEN)
        Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                .RESOURCE_FORBIDDEN)
        Assert.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_DETAIL),
                "The access token does not allow you to access the requested resource")
        softAssertion.assertAll()
    }
}
