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

import com.wso2.cds.test.framework.constant.AUAccountProfile
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.bfsi.test.framework.exception.TestFrameworkException
import com.wso2.cds.test.framework.constant.AUPageObjects
import com.wso2.openbanking.test.framework.utility.OBTestUtil
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import org.apache.http.conn.ssl.SSLSocketFactory
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.testng.Assert
import io.restassured.response.Response
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
}

