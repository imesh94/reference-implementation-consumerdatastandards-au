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

import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.bfsi.test.framework.exception.TestFrameworkException
import com.wso2.cds.test.framework.constant.AUPageObjects
import com.wso2.cds.test.framework.utility.AUSSLSocketFactoryCreator
import com.wso2.openbanking.test.framework.utility.OBTestUtil
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import org.apache.http.conn.ssl.SSLSocketFactory
import org.testng.Assert

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
     * Get SingleAccountXPath based on OB Version
     * @return SingleAccountXPath
     */
    static String getSingleAccountXPath() {
        if (!AUConstants.SOLUTION_VERSION_200.equals(auConfiguration.getCommonSolutionVersion())) {
            return AUPageObjects.SINGLE_ACCOUNT_XPATH_200
        } else {
            return AUPageObjects.SINGLE_ACCOUNT_XPATH
        }
    }

    /**
     * Get AltSingleAccountXPath based on OB Version
     * @return AltSingleAccountXPath
     */
    static String getAltSingleAccountXPath() {
        if (!AUConstants.SOLUTION_VERSION_200.equals(auConfiguration.getCommonSolutionVersion())) {
            return AUPageObjects.ALT_SINGLE_ACCOUNT_XPATH_200
        } else {
            return AUPageObjects.ALT_SINGLE_ACCOUNT_XPATH
        }
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

}

