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

package com.wso2.cds.test.framework

import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConfigConstants
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.AUPageObjects
import com.wso2.cds.test.framework.constant.ContextConstants
import com.wso2.cds.test.framework.automation.consent.AUAccountSelectionStep
import com.wso2.cds.test.framework.automation.consent.AUBasicAuthAutomationStep
import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.wso2.openbanking.test.framework.OBTest
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import com.wso2.openbanking.test.framework.configuration.OBConfigParser
import io.restassured.response.Response
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.testng.ITestContext
import org.testng.annotations.BeforeClass
import com.wso2.cds.test.framework.request_builder.AUAuthorisationBuilder
import com.wso2.cds.test.framework.request_builder.AURegistrationRequestBuilder
import com.wso2.cds.test.framework.request_builder.AURequestBuilder
import com.wso2.cds.test.framework.utility.AURestAsRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil

/**
 * Class for defining common methods that needed in test classes.
 * Every test class in Test layer should extended from this.
 * Execute test framework initialization process
 */
class AUTest extends OBTest {


    AUConfigurationService auConfiguration
    protected static Logger log = LogManager.getLogger(AUTest.class.getName());

    @BeforeClass(alwaysRun = true)
    void "Initialize Test Suite"() {
        OBConfigParser.getInstance(AUConfigConstants.CONFIG_FILE_LOCATION)
        AURestAsRequestBuilder.init()
        auConfiguration = new AUConfigurationService()
    }

    public List<AUAccountScope> scopes = [
            AUAccountScope.BANK_ACCOUNT_BASIC_READ,
            AUAccountScope.BANK_ACCOUNT_DETAIL_READ,
            AUAccountScope.BANK_TRANSACTION_READ,
            AUAccountScope.BANK_PAYEES_READ,
            AUAccountScope.BANK_REGULAR_PAYMENTS_READ,
            AUAccountScope.BANK_CUSTOMER_BASIC_READ,
            AUAccountScope.BANK_CUSTOMER_DETAIL_READ
    ]

    private List<String> DCRScopes
    public String redirectURL
    public String userAccessToken
    public String authorisationCode
    public String consentedAccount
    public String secondConsentedAccount
    public String cdrArrangementId = ""
    public String jtiVal
    public String clientId
    public String accessToken

    /**
     * Set Scopes of application
     * can be used in any testcase
     * @param scopeList
     */
    void setApplicationScope(List<String> scopeList) {
        this.DCRScopes = scopeList
    }

    /**
     * Set redirect URL of application
     * can be used in any testcase
     * @param url
     */
    void setRedirectURL(String url) {
        this.redirectURL = url
    }

    /**
     * Get Scopes
     * @return
     */
    List<String> getApplicationScope() {
        if (this.DCRScopes == null) {
            this.DCRScopes = [
                    AUAccountScope.BANK_ACCOUNT_BASIC_READ.getScopeString(),
                    AUAccountScope.BANK_TRANSACTION_READ.getScopeString(),
                    AUAccountScope.BANK_CUSTOMER_DETAIL_READ.getScopeString(),
                    AUAccountScope.CDR_REGISTRATION.getScopeString()
            ]
        }
        return this.DCRScopes
    }

    String getRedirectURL() {
        if (this.redirectURL == null) {
            this.redirectURL = auConfiguration.getAppInfoRedirectURL()
        }
        return this.redirectURL
    }

    /**
     * Consent Authorization method
     * @param clientId
     */
    void doConsentAuthorisation(String clientId = null) {
        AUAuthorisationBuilder auAuthorisationBuilder = new AUAuthorisationBuilder()
        String authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION, true)
                .toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
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
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_SUBMIT_XPATH)).click()
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_SUBMIT_XPATH)).click()

                }
                .addStep(getWaitForRedirectAutomationStep())
                .execute()
        // Get Code From URL
        authorisationCode = AUTestUtil.getHybridCodeFromUrl(automation.currentUrl.get())
    }

    /**
     * Consent authorization method with Request URI
     * @param scopes
     * @param requestUri
     */
    void doConsentAuthorisationViaRequestUri(List<AUAccountScope> scopes, URI requestUri) {
        AUAuthorisationBuilder auAuthorisationBuilder = new AUAuthorisationBuilder()
        String authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri)
                .toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    driver.findElement(By.xpath(AUTestUtil.getSingleAccountXPath())).click()
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_SUBMIT_XPATH)).click()
                    driver.findElement(By.xpath(AUPageObjects.CONSENT_CONFIRM_XPATH)).click()

                }
                .addStep(getWaitForRedirectAutomationStep())
                .execute()
        // Get Code From URL
        authorisationCode = AUTestUtil.getHybridCodeFromUrl(automation.currentUrl.get())

    }

    /**
     *  Authorization method
     * @param sharingDuration
     * @param sendSharingDuration
     * @return
     */
    String doAuthorization(long sharingDuration, boolean sendSharingDuration) {
        AUAuthorisationBuilder auAuthorisationBuilder = new AUAuthorisationBuilder()
        String authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, sharingDuration, sendSharingDuration)
                .toURI().toString()

        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep(new AUAccountSelectionStep())
                .addStep(getWaitForRedirectAutomationStep())
                .execute()
        // Get Code From URL
        String authCode = AUTestUtil.getHybridCodeFromUrl(automation.currentUrl.get())
        return authCode
    }

    /**
     * Method for get user access token response
     * @return
     */
    AccessTokenResponse getUserAccessTokenResponse(String clientId = null) {
        try {
            return AURequestBuilder.getUserToken(authorisationCode, clientId)
        }
        catch (Exception e) {
            log.error(e)
        }
    }

    /**
     * Method for get new User access token
     */
    void generateUserAccessToken(String clientId = null) {
        userAccessToken = getUserAccessTokenResponse(clientId).tokens.accessToken
    }

    /**
     * Method for get CDR Arrangement ID
     * @param clientId
     */
    void generateCDRArrangementId(String clientId = null) {
        cdrArrangementId = getUserAccessToken(clientId).getCustomParameters().get("cdr_arrangement_id")
    }

    /**
     * Get existing User access token if already generated.
     * otherwise new user access token will be generated
     */
    void getUserAccessToken(ITestContext context) {
        userAccessToken = context.getAttribute(ContextConstants.USER_ACCESS_TKN) as String
        if (userAccessToken == null) {
            System.out.println("Generate new user access token")
            doConsentAuthorisation()
            generateUserAccessToken()
            context.setAttribute(ContextConstants.USER_ACCESS_TKN, userAccessToken)
        }
    }


    /**
     * Method for delete application
     * @param scopes
     * @param clientId
     */
    void deleteApplicationIfExists(List<String> scopes, String clientId = auConfiguration.getAppInfoClientID()) {
        if (clientId) {
            String token = AURequestBuilder.getApplicationAccessToken(scopes, clientId)

            if (token) {
                def deletionResponse = AURegistrationRequestBuilder.buildBasicRequest(token)
                        .when()
                        .delete(AUConstants.DCR_REGISTRATION_ENDPOINT + clientId)
            }
        }
    }

    /**
     * Method for delete application
     * @param scopes
     * @param clientId
     */
    void deleteApplicationIfExists(String clientId = auConfiguration.getAppInfoClientID()) {
        if (clientId) {
            String token = AURequestBuilder.getApplicationAccessToken(getApplicationScope(), clientId)

            if (token) {
                def deletionResponse = AURegistrationRequestBuilder.buildBasicRequest(token)
                        .when()
                        .delete(AUConstants.DCR_REGISTRATION_ENDPOINT + clientId)

            }
        }
    }

    /**
     * Get Application access token
     * @param clientId
     */
    String getApplicationAccessToken(String clientId = auConfiguration.getAppInfoClientID()) {
        String token = AURequestBuilder.getApplicationAccessToken(getApplicationScope(), clientId)
        if (token != null) {
            addToContext(ContextConstants.APP_ACCESS_TKN, token)
        } else {
            log.error("Application access Token Cannot be generated")
        }
        return token
    }

    /**
     * Basic TPP Registration Method.
     * @return response.
     */
    Response tppRegistration() {

        AURegistrationRequestBuilder reg = new AURegistrationRequestBuilder()

        def registrationResponse = AURegistrationRequestBuilder
                .buildRegistrationRequest(reg.getAURegularClaims())
                .when()
                .post(AUConstants.DCR_REGISTRATION_ENDPOINT)

        return registrationResponse
    }

    /**
     * Basic TPP Deletion Method.
     * @param clientId
     * @param accessToken
     * @return response.
     */
    Response tppDeletion(String clientId, String accessToken) {

        def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
                .when()
                .delete(AUConstants.DCR_REGISTRATION_ENDPOINT + clientId)

        return registrationResponse
    }

    String getCDSClient() {
        return "${auConfiguration.getAppInfoClientID()}:${auConfiguration.getAppInfoClientSecret()}"
    }

}

