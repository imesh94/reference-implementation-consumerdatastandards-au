/*
 * Copyright (c) 2021 - 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.toolkit.cds.test.common.utils

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.RSASSASigner
import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.test.framework.automation.AUBasicAuthAutomationStep
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.test.framework.automation.WaitForRedirectAutomationStep
import com.wso2.openbanking.test.framework.model.AccessTokenJwtDto
import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import io.restassured.response.Response
import org.json.JSONObject
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.testng.Assert
import org.testng.annotations.BeforeClass

import java.nio.charset.Charset
import java.security.Key
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.Certificate

/**
 * Base Test For Accounts.
 */
class AbstractAUTests {

    public List<AUConstants.SCOPES> scopes = [
            AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ,
            AUConstants.SCOPES.BANK_ACCOUNT_DETAIL_READ,
            AUConstants.SCOPES.BANK_TRANSACTION_READ,
            AUConstants.SCOPES.BANK_PAYEES_READ,
            AUConstants.SCOPES.BANK_REGULAR_PAYMENTS_READ,
            AUConstants.SCOPES.BANK_CUSTOMER_BASIC_READ,
            AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ
    ]

    public String cdsClient = "${AppConfigReader.getClientId()}:${AppConfigReader.getClientSecret()}"

    public String authorisationCode
    public String userAccessToken
    public String consentedAccount
    public String secondConsentedAccount
    public String transactionId
    public String productId
    public String payeeId
    public Response parResponse
    public Response revocationResponse
    def response
    public String requestUri

    @BeforeClass(alwaysRun = true)
    void "Initialize Test Suite"() {

        TestSuite.init()
    }

    void doConsentAuthorisation(String clientId = null, AUAccountProfile profiles = AUAccountProfile.INDIVIDUAL) {

        if (clientId == null) {
            response = doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                    true, "")
            requestUri = TestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
            doConsentAuthorisationViaRequestUri(scopes, requestUri.toURI(), null, profiles)
        } else {
            response = doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                    true, "", clientId)
            requestUri = TestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
            doConsentAuthorisationViaRequestUri(scopes, requestUri.toURI(), clientId, profiles)
        }
    }

    void doConsentAuthorisationWithoutAccountSelection() {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(
                scopes, AUConstants.DEFAULT_SHARING_DURATION, true
        )

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()

                }
                .addStep(new WaitForRedirectAutomationStep())
                .execute()

        // Get Code From URL
        authorisationCode = TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())

        Assert.assertNotNull(authorisationCode)

    }

    void generateUserAccessToken() {

        userAccessToken = AURequestBuilder.getUserToken(authorisationCode, AURequestBuilder.getCodeVerifier()).tokens.accessToken
        Assert.assertNotNull(userAccessToken)
    }


    /**
     * Push Authorisation Request with private_key_jwt authentication method.
     * @param scopes
     * @param sharingDuration
     * @param sendSharingDuration
     * @param cdrArrangementId
     * @param clientId
     * @return
     */
    Response doPushAuthorisationRequest(List<AUConstants.SCOPES> scopes, Long sharingDuration,
                                        boolean sendSharingDuration, String cdrArrangementId = "",
                                        String clientId = AppConfigReader.getClientId(),
                                        String redirectUrl = AppConfigReader.getRedirectURL()) {

        String scopeString = "openid ${String.join(" ", scopes.collect({ it.scopeString }))}"

        String assertionString = getAssertionString(clientId)

        def bodyContent = [
                (TestConstants.CLIENT_ID_KEY)            : (clientId),
                (TestConstants.CLIENT_ASSERTION_TYPE_KEY): (TestConstants.CLIENT_ASSERTION_TYPE),
                (TestConstants.CLIENT_ASSERTION_KEY)     : assertionString,
        ]

        parResponse = TestSuite.buildRequest()
                .contentType(TestConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .formParams(bodyContent)
                .formParams(TestConstants.REQUEST_KEY, AUAuthorisationBuilder.getSignedRequestObject(scopeString,
                        sharingDuration, sendSharingDuration, cdrArrangementId, redirectUrl, clientId).serialize())
                .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                .post(AUConstants.PAR_ENDPOINT)

        return parResponse
    }
    /**
     * create assertion for the PAR endpoint
     */

    static String getAssertionString(clientId) {

        if (ConfigParser.getInstance().mockCDRRegisterEnabled) {

            KeyStore keyStore = TestUtil.getMockADRApplicationKeyStore()
            Certificate certificate = TestUtil.getCertificateFromMockADRKeyStore()
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.parse(ConfigParser.instance.signingAlgorithm)).
                    keyID(TestUtil.getJwkThumbPrintForSHA256(certificate)).build()

            Key signingKey = keyStore.getKey(ConfigParser.getInstance().getMockADRSigningKeystoreAlias(),
                    ConfigParser.getInstance().getMockADRSigningKeystorePassword().toCharArray())

            long currentTimeInMilliseconds = System.currentTimeMillis();
            long currentTimeInSeconds = System.currentTimeMillis() / 1000;
            //expire time is read from configs and converted to milli seconds
            long expireTime = currentTimeInSeconds + (long)(ConfigParser.getInstance().getAccessTokenExpireTime() * 1000);
            String aud = ConfigParser.getInstance().getAudienceValue();
            //String exp = expireTime;
            long iat = currentTimeInSeconds;
            String jti = String.valueOf(currentTimeInMilliseconds);

            JSONObject payload = new JSONObject();
            payload.put(TestConstants.ISSUER_KEY, clientId);
            payload.put(TestConstants.SUBJECT_KEY, clientId);
            payload.put(TestConstants.AUDIENCE_KEY, aud);
            payload.put(TestConstants.EXPIRE_DATE_KEY, expireTime);
            payload.put(TestConstants.ISSUED_AT_KEY, iat);
            payload.put(TestConstants.JTI_KEY, jti);

            JWSSigner signer = new RSASSASigner((PrivateKey) signingKey);

            JWSObject jwsObject = new JWSObject(header, new Payload(payload.toString()));
            jwsObject.sign(signer);

            return jwsObject.serialize();

        } else {
            return new AccessTokenJwtDto().getJwt(clientId, ConfigParser.getInstance().getAudienceValue())
        }

    }

    void doConsentAuthorisationViaRequestUri(List<AUConstants.SCOPES> scopes, URI requestUri,
                                             String clientId = null, AUAccountProfile profiles = null) {

        AUAuthorisationBuilder authorisationBuilder

        if (clientId) {
            authorisationBuilder = new AUAuthorisationBuilder(
                    scopes, AUConstants.DEFAULT_SHARING_DURATION, true, "", clientId)
        } else {
            authorisationBuilder = new AUAuthorisationBuilder(
                    scopes, AUConstants.DEFAULT_SHARING_DURATION, true)
        }

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
                    // Consent First Account
                    WebElement accElement = driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath()))
                    consentedAccount = accElement.getAttribute("value")
                    accElement.click()
                    // Consent Second Account
                    accElement = driver.findElement(By.xpath(AUTestUtil.getAltSingleAccountXPath()))
                    secondConsentedAccount = accElement.getAttribute("value")
                    accElement.click()
                    // Submit consent
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()

                    // Extra step for OB-2.0 AU Authentication flow.
                    if (TestConstants.SOLUTION_VERSION_300.equals(ConfigParser.getInstance().getSolutionVersion())) {
                        driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                    }
                }
                .addStep(new WaitForRedirectAutomationStep())
                .execute()

        // Get Code From URL
        authorisationCode = TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())

        Assert.assertNotNull(authorisationCode)
    }

    void verifyScopes(String scopesString, List<AUConstants.SCOPES> scopes) {
        for (AUConstants.SCOPES scope : scopes) {
            Assert.assertTrue(scopesString.contains(scope.getScopeString()))
        }
    }

    Response getAccountRetrieval(String userAccessToken) {
        //Account Retrieval request
        Response response = TestSuite.buildRequest()
                .header(AUConstants.X_V_HEADER, AUConstants.X_V_HEADER_ACCOUNTS)
                .header(AUConstants.X_FAPI_AUTH_DATE, AUConstants.DATE)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer " + userAccessToken)
                .baseUri(ConfigParser.instance.baseUrl)
                .get("${AUConstants.CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}/")
        return response
    }

    Response getConsentStatus(String headerString, String consentId) {
        return TestSuite.buildRequest()
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Basic " + Base64.encoder.encodeToString(
                        headerString.getBytes(Charset.forName("UTF-8"))))
                .baseUri(ConfigParser.instance.authorisationServerUrl)
                .get("${AUConstants.CONSENT_STATUS_ENDPOINT}${AUConstants.STATUS_PATH}?${consentId}")
    }

    Response doRevokeConsent(String clientId, String cdrArrangementId) {

        String assertionString = getAssertionString(clientId)

        def bodyContent = [(TestConstants.CLIENT_ID_KEY)            : (clientId),
                           (TestConstants.CLIENT_ASSERTION_TYPE_KEY): (TestConstants.CLIENT_ASSERTION_TYPE),
                           (TestConstants.CLIENT_ASSERTION_KEY)     : assertionString,
                           "cdr_arrangement_id"                     : cdrArrangementId]

        revocationResponse = TestSuite.buildRequest()
                .contentType(TestConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .formParams(bodyContent)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CDR_ARRANGEMENT))
                .post("${AUConstants.CDR_ARRANGEMENT_ENDPOINT}${AUConstants.REVOKE_PATH}")

        return revocationResponse
    }

    String doAuthorization(List<AUConstants.SCOPES> scopes, long sharingDuration, boolean sendSharingDuration) {

        AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(scopes, sharingDuration,
                sendSharingDuration)

        def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authorisationBuilder.authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_SUBMIT_XPATH)).click()
                    driver.findElement(By.xpath(AUConstants.CONSENT_CONFIRM_XPATH)).click()
                }
                .addStep(new WaitForRedirectAutomationStep())
                .execute()

        return TestUtil.getHybridCodeFromUrl(automation.currentUrl.get())
    }

    void deleteApplicationIfExists(List<String> scopes, String clientId = AppConfigReader.getClientId()) {
        if (clientId) {
            String token = AURequestBuilder.getApplicationToken(scopes, clientId)

            if (token) {
                def deletionResponse = AURegistrationRequestBuilder.buildBasicRequestWithContentTypeJson(token)
                        .when()
                        .delete(AUDCRConstants.REGISTRATION_ENDPOINT + clientId)
                Assert.assertEquals(deletionResponse.statusCode(), AUConstants.STATUS_CODE_204)
            }
        }
    }

    /**
     * Push Authorisation Request with private_key_jwt authentication method.
     * @param scopes
     * @param sharingDuration
     * @param sendSharingDuration
     * @param cdrArrangementId
     * @param clientId
     * @return
     */
    Response doPushAuthorisationRequestWithPkjwt(List<AUConstants.SCOPES> scopes, long sharingDuration,
                                                 boolean sendSharingDuration, String cdrArrangementId,
                                                 String clientId = AppConfigReader.getClientId()) {

        String scopeString = "openid ${String.join(" ", scopes.collect({ it.scopeString }))}"

        String assertionString = new AccessTokenJwtDto().getJwt(AppConfigReader.getClientId(),
                ConfigParser.getInstance().getAudienceValue())

        def bodyContent = [(TestConstants.CLIENT_ID_KEY)            : (AppConfigReader.getClientId()),
                           (TestConstants.CLIENT_ASSERTION_TYPE_KEY): (TestConstants.CLIENT_ASSERTION_TYPE),
                           (TestConstants.CLIENT_ASSERTION_KEY)     : assertionString,
                           "cdr_arrangement_id"                     : cdrArrangementId]

        parResponse = TestSuite.buildRequest()
                .contentType(TestConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .formParams(bodyContent)
                .formParams(TestConstants.REQUEST_KEY, AUAuthorisationBuilder.getSignedRequestObject(scopeString,
                        sharingDuration, sendSharingDuration, cdrArrangementId, AppConfigReader.getRedirectURL(), clientId).serialize())
                .baseUri(AUConstants.PUSHED_AUTHORISATION_BASE_PATH)
                .post(AUConstants.PAR_ENDPOINT)

        return parResponse
    }

    /**
     * Sharing Arrangement Revocation with private_key_jwt authentication method.
     * @param applicationAccessToken
     * @param cdrArrangementId
     * @param clientId
     * @return
     */
    Response doArrangementRevocationWithPkjwt(String applicationAccessToken, String cdrArrangementId,
                                              String clientId = AppConfigReader.getClientId()) {

        String assertionString = new AccessTokenJwtDto().getJwt(AppConfigReader.getClientId(),
                ConfigParser.getInstance().getAudienceValue())

        def bodyContent = [(TestConstants.CLIENT_ID_KEY)            : (clientId),
                           (TestConstants.CLIENT_ASSERTION_TYPE_KEY): (TestConstants.CLIENT_ASSERTION_TYPE),
                           (TestConstants.CLIENT_ASSERTION_KEY)     : assertionString,
                           "cdr_arrangement_id"                     : cdrArrangementId]

        revocationResponse = TestSuite.buildRequest()
                .contentType(TestConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .header(AUConstants.X_V_HEADER, AUConstants.CDR_ENDPOINT_VERSION)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${applicationAccessToken}")
                .formParams(bodyContent)
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_CDR_ARRANGEMENT))
                .delete("${AUConstants.CDR_ARRANGEMENT_ENDPOINT}/${cdrArrangementId}")

        return revocationResponse
    }

    /**
     * Basic TPP Registration Method.
     * @return response.
     */
    static Response tppRegistration() {

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaims())
                .when()
                .post(AUDCRConstants.REGISTRATION_ENDPOINT)

        return registrationResponse
    }

    /**
     * Basic TPP Deletion Method.
     * @param clientId
     * @param accessToken
     * @return response.
     */
    static Response tppDeletion(String clientId, String accessToken) {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequestWithContentTypeJson(accessToken)
                .when()
                .delete(AUDCRConstants.REGISTRATION_ENDPOINT + clientId)

        return registrationResponse
    }
}
