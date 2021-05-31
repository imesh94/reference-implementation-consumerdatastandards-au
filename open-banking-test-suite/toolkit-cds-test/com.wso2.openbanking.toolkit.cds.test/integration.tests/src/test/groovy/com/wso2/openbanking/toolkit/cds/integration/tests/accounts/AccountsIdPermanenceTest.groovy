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

import com.wso2.openbanking.test.framework.automation.AUBasicAuthAutomationStep
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUAuthorisationBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUIdEncryptorDecryptor
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.testng.asserts.SoftAssert

/**
 *  AU ID Permanence Tests
 */
class AccountsIdPermanenceTest extends AbstractAUTests{

    static final String CDS_PATH = AUConstants.CDS_PATH

    private static ConfigParser configuration = ConfigParser.getInstance()
    private String secretKey = configuration.getIdPermanenceSecretKey()
    private String encryptedAccount1Id
    private String encryptedAccount2Id
    private String encryptedTransactionId
    private String encryptedPayeeId
    private String userId = configuration.getKeyManagerAdminUsername() + "@" + configuration.getTenantDomain()

    @BeforeClass
    void "Get User Access Token"() {
        doConsentAuthorisation()
        generateUserAccessToken()
    }

    @Test(priority = 1)
    void "TC1201001_Get Accounts"() {

        String requestUrl = "${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}"

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get(requestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        encryptedAccount1Id = TestUtil.parseResponseBody(response, "data.accounts.accountId[0]")
        encryptedAccount2Id = TestUtil.parseResponseBody(response, "data.accounts.accountId[1]")

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.
                decrypt(encryptedAccount1Id, secretKey).split(":")[2])
        softAssertion.assertEquals(secondConsentedAccount, AUIdEncryptorDecryptor.
                decrypt(encryptedAccount2Id, secretKey).split(":")[2])

        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.self")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.first")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.prev")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.next")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.last")
                .contains(requestUrl.split(CDS_PATH)[1]))

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

        String requestUrl = "${CDS_PATH}${AUConstants.BULK_BALANCES_PATH}"

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_BALANCES))
                .post(requestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.balances.accountId[0]"), secretKey).
                split(":")[2])
        softAssertion.assertEquals(secondConsentedAccount, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.balances.accountId[1]"), secretKey).
                split(":")[2])

        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.self")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.first")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.prev")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.next")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.last")
                .contains(requestUrl.split(CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1204001_Get Account Balance"() {

        String requestUrl = "${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${encryptedAccount1Id}/balance"

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_BALANCES))
                .get(requestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.accountId"), secretKey).
                split(":")[2])

        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.self")
                .contains(requestUrl.split(CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1205001_Get Account Detail"() {

        String requestUrl = "${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${encryptedAccount1Id}"

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get(requestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.accountId"), secretKey).
                split(":")[2])

        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.self")
                .contains(requestUrl.split(CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1205003_Get Not Consented Account Details"() {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
                    // Consent First Account
                    WebElement accElement = driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath()))
                    accElement.click()
                    // Submit consent
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()

                    // Extra step for OB-2.0 AU Authentication flow.
                    if (TestConstants.SOLUTION_VERSION_200.equals(ConfigParser.getInstance().getSolutionVersion())) {
                        driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                    }
                }
                .execute()

        SoftAssert softAssertion= new SoftAssert()

        // Get Code From URL
        String authCode = TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())
        softAssertion.assertNotNull(authCode)

        String user2AccessToken = AURequestBuilder.getUserToken(authCode).tokens.accessToken
        softAssertion.assertNotNull(user2AccessToken)

        def response = AURequestBuilder
                .buildBasicRequest(user2AccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${encryptedAccount2Id}")

        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_BANK_ACC)
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_BANK_ACC)
        }

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1207002_Get Invalid Transaction Detail"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_TRANSACTIONS))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${encryptedAccount1Id}/" +
                        "transactions/204987583920")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_RESOURCE)
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_RESOURCE)
        }

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1208001_Get Direct Debits For Account"() {

        String requestUrl = "${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${encryptedAccount1Id}/direct-debits"

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DIRECT_DEBIT))
                .get(requestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.accountId[0]"), secretKey).
                split(":")[2])

        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.self")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.first")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.prev")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.next")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.last")
                .contains(requestUrl.split(CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1208002_Get Direct Debits For Invalid Account"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DIRECT_DEBIT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/2349679635270/direct-debits")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_BANK_ACC)
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE),
                    AUConstants.INVALID_BANK_ACC)
        }

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

        String requestUrl = "${CDS_PATH}${AUConstants.BULK_DIRECT_DEBITS_PATH}"

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DIRECT_DEBIT))
                .post(requestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.accountId[0]"), secretKey).
                split(":")[2])

        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.self")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.first")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.prev")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.next")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.last")
                .contains(requestUrl.split(CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1211001_Get Scheduled Payments for Account"() {

        String requestUrl = "${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${encryptedAccount1Id}/payments/scheduled"

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_SCHEDULED_PAYMENT))
                .get(requestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.scheduledPayments.scheduledPaymentId[0]"), secretKey)
                .split(":")[0])
        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.scheduledPayments.from.accountId[0]"), secretKey)
                .split(":")[2])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.scheduledPayments.paymentSet[0].to.accountId[0]"),
                secretKey).split(":")[0])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.scheduledPayments.paymentSet[0].to.payeeId[0]"),
                secretKey).split(":")[0])

        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.self")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.first")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.prev")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.next")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.last")
                .contains(requestUrl.split(CDS_PATH)[1]))

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

        String requestUrl = "${CDS_PATH}${AUConstants.BULK_SCHEDULE_PAYMENTS_PATH}"

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_SCHEDULED_PAYMENT))
                .post(requestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.scheduledPayments.scheduledPaymentId[0]"), secretKey)
                .split(":")[0])
        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.scheduledPayments.from.accountId[0]"), secretKey)
                .split(":")[2])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.scheduledPayments.paymentSet[0].to.accountId[0]"),
                secretKey).split(":")[0])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.scheduledPayments.paymentSet[0].to.payeeId[0]"),
                secretKey).split(":")[0])

        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.self")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.first")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.prev")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.next")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.last")
                .contains(requestUrl.split(CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1201001_Get Accounts", priority = 1)
    void "TC1206001_Get Transactions For Account"() {

        String requestUrl = "${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${encryptedAccount1Id}/transactions"

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_TRANSACTIONS))
                .get(requestUrl)

        encryptedTransactionId = TestUtil.parseResponseBody(response, "data.transactions.transactionId[0]")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.transactions.accountId[0]"), secretKey).
                split(":")[2])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(encryptedTransactionId, secretKey).
                split(":")[0])

        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.self")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.first")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.prev")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.next")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.last")
                .contains(requestUrl.split(CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1206001_Get Transactions For Account", priority = 1)
    void "TC1207001_Get Transaction Detail"() {

        String requestUrl = "${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/${encryptedAccount1Id}/" +
                "transactions/$encryptedTransactionId"

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_TRANSACTIONS))
                .get(requestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.accountId"), secretKey).
                split(":")[2])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.transactionId"), secretKey).
                split(":")[0])

        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.self")
                .contains(requestUrl.split(CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test
    void "TC1202001_Get Bulk Balances"() {

        String requestUrl = "${CDS_PATH}${AUConstants.BULK_BALANCES_PATH}"

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_BALANCES))
                .get(requestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.balances.accountId[0]"), secretKey).
                split(":")[2])
        softAssertion.assertEquals(secondConsentedAccount, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.balances.accountId[1]"), secretKey).
                split(":")[2])

        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.self")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.first")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.prev")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.next")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.last")
                .contains(requestUrl.split(CDS_PATH)[1]))

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

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_BALANCES))
                .post("${CDS_PATH}${AUConstants.BULK_BALANCES_PATH}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_422)

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_BANK_ACC)
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_BANK_ACC)
        } else {
            softAssertion.assertEquals(response.jsonPath().get("errors[0].code"),
                    "0001 – Account not able to be found")
        }

        softAssertion.assertAll()
    }

    @Test
    void "TC1204002_Get Invalid Account Balance"() {

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_BALANCES))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/32125763242/balance")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_BANK_ACC)
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_BANK_ACC)
        }

        softAssertion.assertAll()
    }

    @Test
    void "TC1205002_Get Invalid Account Detail"() {

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/342678987")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_BANK_ACC)
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_BANK_ACC)
        }

        softAssertion.assertAll()
    }

    @Test
    void "TC1206002_Get Transactions For Invalid Account"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_TRANSACTIONS))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/34867635209/transactions")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_BANK_ACC)
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_BANK_ACC)
        }

        softAssertion.assertAll()
    }

    @Test
    void "TC1209001_Get Bulk Direct Debits"() {

        String requestUrl = "${CDS_PATH}${AUConstants.BULK_DIRECT_DEBITS_PATH}"

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DIRECT_DEBIT))
                .get(requestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(consentedAccount, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.accountId[0]"), secretKey).
                split(":")[2])
        softAssertion.assertEquals(secondConsentedAccount, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.directDebitAuthorisations.accountId[1]"), secretKey).
                split(":")[2])

        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.self")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.first")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.prev")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.next")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.last")
                .contains(requestUrl.split(CDS_PATH)[1]))

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

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_DIRECT_DEBIT))
                .post("${CDS_PATH}${AUConstants.BULK_DIRECT_DEBITS_PATH}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_422)

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_BANK_ACC)
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_BANK_ACC)
        } else {
            softAssertion.assertEquals(response.jsonPath().get("errors[0].code"),
                    "0001 – Account not able to be found")
        }

        softAssertion.assertAll()
    }

    @Test
    void "TC1211002_Get Scheduled Payments for Invalid Account"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_SCHEDULED_PAYMENT))
                .get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/3167421098/payments/scheduled")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_BANK_ACC)
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_BANK_ACC)
        }

        softAssertion.assertAll()
    }

    @Test
    void "TC1212001_Get Scheduled Payments Bulk"() {

        String requestUrl = "${CDS_PATH}${AUConstants.BULK_SCHEDULE_PAYMENTS_PATH}"

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_SCHEDULED_PAYMENT))
                .get(requestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.scheduledPayments.scheduledPaymentId[0]"), secretKey)
                .split(":")[0])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.scheduledPayments.from.accountId[0]"), secretKey)
                .split(":")[0])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.scheduledPayments.paymentSet[0].to.accountId[0]"),
                secretKey).split(":")[0])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.scheduledPayments.paymentSet[0].to.payeeId[0]"),
                secretKey).split(":")[0])


        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.scheduledPayments.scheduledPaymentId[1]"), secretKey)
                .split(":")[0])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.scheduledPayments.from.accountId[1]"), secretKey)
                .split(":")[0])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.scheduledPayments.paymentSet[1].to.accountId[0]"),
                secretKey).split(":")[0])
        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.scheduledPayments.paymentSet[1].to.payeeId[0]"),
                secretKey).split(":")[0])

        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.self")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.first")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.prev")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.next")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.last")
                .contains(requestUrl.split(CDS_PATH)[1]))

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

        def response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_SCHEDULED_PAYMENT))
                .post("${CDS_PATH}${AUConstants.BULK_SCHEDULE_PAYMENTS_PATH}")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_422)

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_BANK_ACC)
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_BANK_ACC)
        } else {
            softAssertion.assertEquals(response.jsonPath().get("errors[0].code"),
                    "0001 – Account not able to be found")
        }

        softAssertion.assertAll()
    }

    @Test
    void "TC1215002_Get Invalid Payee Detail"() {

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PAYEES))
                .get("${CDS_PATH}${AUConstants.BULK_PAYEES}/1426558421")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_404)

        if (TestConstants.SOLUTION_VERSION_200.equalsIgnoreCase(AUTestUtil.solutionVersion)) {
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_CODE),
                    AUConstants.ERROR_CODE_INVALID_RESOURCE)
            softAssertion.assertEquals(TestUtil.parseResponseBody(response, AUConstants.ERROR_TITLE), AUConstants
                    .INVALID_RESOURCE)
        }

        softAssertion.assertAll()
    }

    @Test (priority = 2)
    void "TC1214001_Get Payees"() {

        String requestUrl = "${CDS_PATH}${AUConstants.BULK_PAYEES}"

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PAYEES))
                .get(requestUrl)

        encryptedPayeeId = TestUtil.parseResponseBody(response, "data.payees.payeeId[0]")

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(encryptedPayeeId,
                secretKey).split(":")[0])

        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.self")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.first")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.prev")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.next")
                .contains(requestUrl.split(CDS_PATH)[1]))
        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.last")
                .contains(requestUrl.split(CDS_PATH)[1]))

        softAssertion.assertAll()
    }

    @Test (dependsOnMethods = "TC1214001_Get Payees", priority = 2)
    void "TC1215001_Get Payee Detail"() {

        String requestUrl = "${CDS_PATH}${AUConstants.BULK_PAYEES}/${encryptedPayeeId}"

        Response response = AURequestBuilder
                .buildBasicRequest(userAccessToken, AUConstants.X_V_HEADER_ACCOUNTS)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_PAYEES))
                .get(requestUrl)

        SoftAssert softAssertion= new SoftAssert()
        softAssertion.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        softAssertion.assertEquals(userId, AUIdEncryptorDecryptor.decrypt(
                TestUtil.parseResponseBody(response, "data.payeeId"),
                secretKey).split(":")[0])

        softAssertion.assertTrue(TestUtil.parseResponseBody(response, "links.self")
                .contains(requestUrl.split(CDS_PATH)[1]))

        softAssertion.assertAll()
    }

}
