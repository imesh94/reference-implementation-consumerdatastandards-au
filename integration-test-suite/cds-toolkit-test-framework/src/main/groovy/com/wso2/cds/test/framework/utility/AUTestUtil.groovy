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

package com.wso2.cds.test.framework.utility

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.wso2.cds.test.framework.constant.AUAccountProfile
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.bfsi.test.framework.exception.TestFrameworkException
import com.wso2.cds.test.framework.constant.AUPageObjects
import com.wso2.cds.test.framework.request_builder.AUJWTGenerator
import com.wso2.openbanking.test.framework.utility.OBTestUtil
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import org.apache.http.conn.ssl.SSLSocketFactory
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.RemoteWebDriver
import org.testng.Assert
import io.restassured.response.Response

import java.nio.charset.Charset;
import org.jsoup.Jsoup

/**
 * Domain specific AU layer Class to contain utility classes used for Test Framework.
 */
class AUTestUtil extends OBTestUtil {

    static SSLSocketFactory sslSocketFactoryForMockCDRRegister

    static AUConfigurationService auConfiguration = new AUConfigurationService()

    // Static initialize the SSL socket factory for MockCDRRegister
    static {

        AUSSLSocketFactoryCreator auSSLSocketFactoryCreator = new AUSSLSocketFactoryCreator()

        if (auConfiguration.getMockCDREnabled()) {
            try {
                sslSocketFactoryForMockCDRRegister = auSSLSocketFactoryCreator.createForMockCDRRegister();
                // Skip hostname verification.
                sslSocketFactoryForMockCDRRegister.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            } catch (TestFrameworkException e) {
                OBTestUtil.log.error("Unable to create the SSL socket factory", e);
            }
        }
    }

    public static SSLSocketFactory getSslSocketFactoryForMockCDRRegister() {
        return sslSocketFactoryForMockCDRRegister;
    }

    /**
     * Get SingleAccountXPath
     * @return SingleAccountXPath
     */
    static String getSingleAccountXPath() {
        return AUPageObjects.SINGLE_ACCOUNT_XPATH
    }

    /**
     * Get AltSingleAccountXPath
     * @return AltSingleAccountXPath
     */
    static String getAltSingleAccountXPath() {
        return AUPageObjects.ALT_SINGLE_ACCOUNT_XPATH
    }

    /**
     * Get the base URl based on the Gateway Configuration
     * @param resourceType
     * @return base url
     */
    static String getBaseUrl(String basePathType) {

        String baseUrl

        if (auConfiguration.getMicroGatewayEnabled()) {
            switch (basePathType) {
                case AUConstants.DCR_BASE_PATH_TYPE:
                    baseUrl = auConfiguration.getMicroGatewayDCRUrl()
                    break
                case AUConstants.BASE_PATH_TYPE_ACCOUNT:
                    baseUrl = auConfiguration.getMicroGatewayAccountsUrl()
                    break
                case AUConstants.BASE_PATH_TYPE_BALANCES:
                    baseUrl = auConfiguration.getMicroGatewayBalancesUrl()
                    break
                case AUConstants.BASE_PATH_TYPE_TRANSACTIONS:
                    baseUrl = auConfiguration.getMicroGatewayTransactionURL()
                    break
                case AUConstants.BASE_PATH_TYPE_DIRECT_DEBIT:
                    baseUrl = auConfiguration.getMicroGatewayDirectDebitURL()
                    break
                case AUConstants.BASE_PATH_TYPE_SCHEDULED_PAYMENT:
                    baseUrl = auConfiguration.getMicroGatewaySchedulePayURL()
                    break
                case AUConstants.BASE_PATH_TYPE_PAYEES:
                    baseUrl = auConfiguration.getMicroGatewayPayeeURL()
                    break
                case AUConstants.BASE_PATH_TYPE_PRODUCTS:
                    baseUrl = auConfiguration.getMicroGatewayProductURL()
                    break
                case AUConstants.BASE_PATH_TYPE_CUSTOMER:
                    baseUrl = auConfiguration.getMicroGatewayCustomerURL()
                    break
                case AUConstants.BASE_PATH_TYPE_DISCOVERY:
                    baseUrl = auConfiguration.getMicroGatewayDiscoveryURL()
                    break
                case AUConstants.BASE_PATH_TYPE_CDR_ARRANGEMENT:
                    baseUrl = auConfiguration.getMicroGatewayArrangementURL()
                    break
                case AUConstants.BASE_PATH_TYPE_ADMIN:
                    baseUrl = auConfiguration.getMicroGatewayAdminURL()
                    break
            }
        } else {
            baseUrl = auConfiguration.getServerBaseURL()
        }

        return baseUrl
    }

    //Parse response body

    public static String parseResponseBody(Response response, String jsonPath) {

        return response.jsonPath().getString(jsonPath);
    }


    /**
     * Check scope
     * @param scopesString
     * @param scopes
     */
    static void verifyScopes(String scopesString, List<AUAccountScope> scopes) {
        for (AUAccountScope scope : scopes) {
            Assert.assertTrue(scopesString.contains(scope.getScopeString()))
        }
    }

    /**
     * Get Business Account 1 XPath
     * @return SingleAccountXPath
     */
    static String getBusinessAccount1CheckBox() {
        return AUPageObjects.CHK_ORG_A_BUSINESS_ACCOUNT_1
    }

    /**
     * Get Business Account 2 XPath
     * @return SingleAccountXPath
     */
    static String getBusinessAccount2CheckBox() {
        return AUPageObjects.CHK_ORG_B_BUSINESS_ACCOUNT_1
    }

    /**
     * Get Business Account 3 XPath
     * @return SingleAccountXPath
     */
    static String getBusinessAccount3CheckBox() {
        return AUPageObjects.CHK_ORG_B_BUSINESS_ACCOUNT_2
    }

    /**
     * Get Secondary Account 1 XPath
     * @return SecondaryAccountXPath
     */
    static String getSecondaryAccount1XPath() {
        return AUPageObjects.SECONDARY_ACCOUNT_1
    }

    /**
     * Get Secondary Account 2 XPath
     * @return SecondaryAccount2XPath
     */
    static String getSecondaryAccount2XPath() {
        return AUPageObjects.SECONDARY_ACCOUNT_2
    }

    /**
     * Get Secondary Joint Account 1 XPath
     * @return SecondaryJointAccount1XPath
     */
    static String getSecondaryJointAccount1XPath() {
        return AUPageObjects.SECONDARY_JOINT_ACCOUNT
    }

    /**
     * Get Business Account 1 Label XPath
     * @return BusinessAccount1LabelXPath
     */
    static String getBusinessAccount1Label() {
        return AUPageObjects.LBL_BUSINESS_ACCOUNT_1
    }

    /**
     * Get Business Account 2 Label XPath
     * @return BusinessAccount2LabelXPath
     */
    static String getBusinessAccount2Label() {
        return AUPageObjects.LBL_BUSINESS_ACCOUNT_2
    }

    /**
     * Get Shareable Accounts List required Params based on Input Values
     * @param shareableAccountsResponse
     * @return ShareableAccountMap
     */
    static Map getSharableAccountsList(Response shareableAccountsResponse,
                                       String profile = AUAccountProfile.ORGANIZATION_A.getProperty(AUConstants.VALUE_KEY)) {

        //Get the response of the shareable endpoint and map the required values according to the profile selection.
        def sharableAccountList = shareableAccountsResponse.jsonPath().get(AUConstants.DATA)

        def  ShareableAccountMap = [:]

        for (sharableAccount in sharableAccountList) {
            if (sharableAccount[AUConstants.PARAM_PROFILE_NAME] == profile) {
                ShareableAccountMap [AUConstants.PARAM_ACCOUNT_ID] = sharableAccount[AUConstants.ACCOUNT_ID]
                ShareableAccountMap [AUConstants.ACCOUNT_OWNER_USER_ID] =
                        sharableAccount[AUConstants.BUSINESS_ACCOUNT_INFO][AUConstants.ACCOUNT_OWNERS][AUConstants.MEMBER_ID][0]
                ShareableAccountMap [AUConstants.NOMINATED_REP_USER_ID] =
                        sharableAccount[AUConstants.BUSINESS_ACCOUNT_INFO][AUConstants.NOMINATED_REPRESENTATIVES][AUConstants.MEMBER_ID][0]
                ShareableAccountMap [AUConstants.ACCOUNT_OWNER_USER_ID] =
                        sharableAccount[AUConstants.BUSINESS_ACCOUNT_INFO][AUConstants.ACCOUNT_OWNERS][AUConstants.MEMBER_ID][1]
                ShareableAccountMap [AUConstants.NOMINATED_REP_USER_ID2] =
                        sharableAccount[AUConstants.BUSINESS_ACCOUNT_INFO][AUConstants.NOMINATED_REPRESENTATIVES][AUConstants.MEMBER_ID][1]
                break
            }
        }
        return ShareableAccountMap
    }

    /**
     * Get Json Path of the response of get permission request.
     * @param accountId
     * @return Json Path
     */
    static String getPermissionForUser(String accountId) {
        return "$AUConstants.PARAM_PERMISSION_STATUS.$accountId"
    }

    /**
     * Retrieve the required Joint Account IDs from the shareable Accounts endpoint
     * @param shareableAccountsResponse - shareable Accounts endpoint response
     * @return accountIdList.
     */
    static List<String> getJointAccountIds(Response shareableAccountsResponse) {

        List<String> accountIdList = new ArrayList<>()
        def sharableAccountList = shareableAccountsResponse.jsonPath().get(AUConstants.DATA)

        for (sharableAccount in sharableAccountList) {
            if (sharableAccount["isJointAccount"]) {
                accountIdList.add(sharableAccount[AUConstants.ACCOUNT_ID].toString())
            }
        }
        return accountIdList
    }

    /**
     * Retrieve the required Normal Individual Account IDs from the shareable Accounts endpoint
     * @param shareableAccountsResponse - shareable Accounts endpoint response
     * @return accountIdList.
     */
    static List<String> getSingleAccountIds(Response shareableAccountsResponse) {

        List<String> normalAccountIdList = new ArrayList<>()
        def sharableAccountList = shareableAccountsResponse.jsonPath().get(AUConstants.DATA)

        for (sharableAccount in sharableAccountList) {
            normalAccountIdList.add(sharableAccount[AUConstants.PARAM_ACCOUNT_ID].toString())
        }

        return normalAccountIdList
    }

    /**
     * Get the required Secondary Account IDs from the shareable Accounts endpoint
     * @param shareableAccountsResponse - shareable Accounts endpoint response
     * @return ShareableAccountMap
     */
    static Map getSecondaryUserDetails(Response shareableAccountsResponse, boolean isSecondarySingleAccount = true) {

        //Asserting sharableBankAccountsResponse response and sec
        def sharableAccountList = shareableAccountsResponse.jsonPath().get(AUConstants.DATA)

        def  ShareableAccountMap = [:]

        for (sharableAccount in sharableAccountList) {
            if (sharableAccount[AUConstants.IS_SECONDARY_ACCOUNT]) {
                if (isSecondarySingleAccount && !sharableAccount[AUConstants.IS_JOINT_ACCOUNT]) {

                    ShareableAccountMap [AUConstants.PARAM_ACCOUNT_ID] = sharableAccount[AUConstants.ACCOUNT_ID]
                    break
                } else if (!isSecondarySingleAccount && sharableAccount[AUConstants.IS_JOINT_ACCOUNT]){
                    ShareableAccountMap[AUConstants.PARAM_ACCOUNT_ID] = sharableAccount[AUConstants.ACCOUNT_ID]
                    break
                }
            }
        }
        return ShareableAccountMap
    }

    /**
     * Get List of Legal Entity IDs.
     * @param legalEntityListResponse - Legal Entity List Response
     * @param userId - User ID
     * @param accountId - Account ID
     * @return List of Legal Entity IDs
     */
    static List<String> getLegalEntityIdList(Response legalEntityListResponse, String userId, String accountId) {

        // Parse the payload using Gson
        Gson gson = new Gson();
        JsonObject payloadObj = gson.fromJson(legalEntityListResponse.getBody(), JsonObject.class)

        // Get the secondary users array
        JsonArray secondaryUsersArray = payloadObj.getAsJsonArray(AUConstants.PAYLOAD_SECONDARY_USERS)

        // List to store legal entity IDs
        List<String> legalEntityIds = new ArrayList<>()

        // Iterate through the secondary users
        for (JsonElement secondaryUserElement : secondaryUsersArray) {
            JsonObject secondaryUserObj = secondaryUserElement.getAsJsonObject()

            // Check if the user ID matches
            if (secondaryUserObj.get(AUConstants.SECONDARY_USERS_USERID).getAsString().equals(userId)) {

                // Get the legal entity details array for the given user
                JsonArray legalEntityDetailsArray = secondaryUserObj.getAsJsonArray(AUConstants.LEGAL_ENTITY_DETAILS)

                // Iterate through the legal entity details
                for (JsonElement legalEntityDetailsElement : legalEntityDetailsArray) {
                    JsonObject legalEntityDetailsObj = legalEntityDetailsElement.getAsJsonObject()

                    // Check if the account ID matches
                    if (legalEntityDetailsObj.get(AUConstants.PAYLOAD_PARAM_ACCOUNT_ID).getAsString().equals(accountId)) {

                        // Get the legal entities array for the given account
                        JsonArray legalEntitiesArray = legalEntityDetailsObj.getAsJsonArray(AUConstants.LEGAL_ENTITIES)

                        // Iterate through the legal entities
                        for (JsonElement legalEntityElement : legalEntitiesArray) {
                            JsonObject legalEntityObj = legalEntityElement.getAsJsonObject()

                            // Get the legal entity ID
                            String legalEntityId = legalEntityObj.get(AUConstants.LEGAL_ENTITY_ID).getAsString()

                            // Add legal entity ID to the list
                            legalEntityIds.add(legalEntityId)
                        }
                        break
                    }
                }
                break
            }
        }
        return legalEntityIds
    }
  
    /**
     * Read Attributes from HTML Document
     * @param htmlDocumentBody
     * @param attribute
     * @return
     */
    static String readHtmlDocument(String htmlDocumentBody, String attribute) {

        Document doc = Jsoup.parse(htmlDocumentBody)
        Element element = doc.getElementsByAttribute(attribute)

        return element.toString()
    }

    /**
     * Get Code From JWT Response.
     * @param authResponseUrl - URL of the response
     * @return Code
     */
    static String getCodeFromJwtResponse(String authResponseUrl) {

        String responseJwt = authResponseUrl.split(AUConstants.HTML_RESPONSE_ATTR)[1]
        Assert.assertNotNull(responseJwt)
        return AUJWTGenerator.extractJwt(responseJwt).getClaim(AUConstants.CODE_KEY)
    }

    /**
     * Get Endpoint version of Banking API Endpoints.
     * @param endpoint - Banking API Endpoint
     * @return Banking API Endpoint version
     */
    static int getBankingApiEndpointVersion(String endpoint) {

        switch (endpoint) {
            case AUConstants.BULK_ACCOUNT_PATH:
                return AUConstants.X_V_HEADER_ACCOUNTS
            case AUConstants.SINGLE_ACCOUNT_PATH:
                return AUConstants.X_V_HEADER_ACCOUNT
            case AUConstants.BULK_BALANCES_PATH:
                return AUConstants.X_V_HEADER_BALANCES
            case AUConstants.ACCOUNT_BALANCE_PATH:
                return AUConstants.X_V_HEADER_BALANCE
            case AUConstants.GET_TRANSACTIONS:
                return AUConstants.X_V_HEADER_TRANSACTIONS
            case AUConstants.BULK_DIRECT_DEBITS_PATH:
                return AUConstants.X_V_HEADER_DIRECT_DEBITS
            case AUConstants.BULK_SCHEDULE_PAYMENTS_PATH:
                return AUConstants.X_V_HEADER_PAYMENT_SCHEDULED
            case AUConstants.BULK_PAYEES:
                return AUConstants.X_V_HEADER_PAYEES
            case AUConstants.GET_PRODUCTS:
                return AUConstants.X_V_HEADER_PRODUCTS
            default:
                return 1
        }
    }
}

