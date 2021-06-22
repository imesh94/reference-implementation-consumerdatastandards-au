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

package com.wso2.openbanking.toolkit.cds.test.common.utils

import com.fasterxml.uuid.Generators
import com.wso2.openbanking.test.framework.util.ConfigParser
import com.wso2.openbanking.test.framework.util.TestConstants

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AUTestUtil {

    static String solutionVersion = ConfigParser.getInstance().getSolutionVersion()

    /**
     * Get ISO_8601 Standard date time
     * Eg: 2019-09-30T04:44:05.271Z
     *
     * @return String value of the current date time
     */
    static String getDateAndTime() {

        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
    /**
     * Generate X_FAPI_INTERACTION_ID
     */
    static String generateUUID() {
        UUID uuid = Generators.timeBasedGenerator().generate()
        return uuid.toString()
    }

    /**
     * Get ISO_8601 Standard date time
     * @return String value of the date time after 11.50hours
     */
    static String getTommorowDateAndTime() {
        long n = 11.10;
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.plusHours(n).format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    /**
     * Get date in MM/dd/yyyy format
     * @return String value of the current date
     */
    static String getDate() {
        String pattern = "MM/dd/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern)
        String date = simpleDateFormat.format(new Date())
        return date;
    }

    /**
     * Get SingleAccountXPath based on OB Version
     * @return SingleAccountXPath
     */
    static String getSingleAccountXPath() {
        if (TestConstants.SOLUTION_VERSION_200.equals(ConfigParser.getInstance().getSolutionVersion())) {
            return AUConstants.SINGLE_ACCOUNT_XPATH_200
        } else {
            return AUConstants.SINGLE_ACCOUNT_XPATH
        }
    }

    /**
     * Get AltSingleAccountXPath based on OB Version
     * @return AltSingleAccountXPath
     */
    static String getAltSingleAccountXPath() {
        if (TestConstants.SOLUTION_VERSION_200.equals(ConfigParser.getInstance().getSolutionVersion())) {
            return AUConstants.ALT_SINGLE_ACCOUNT_XPATH_200
        } else {
            return AUConstants.ALT_SINGLE_ACCOUNT_XPATH
        }
    }

    /**
     * Get the base URl based on the Gateway Configuration
     * @param resourceType
     * @return base url
     */
    static String getBaseUrl(String basePathType) {

        String baseUrl

        if (ConfigParser.getInstance().getMicroGatewayEnabled()) {

            switch (basePathType) {
                case "DCR":
                    baseUrl = ConfigParser.getInstance().getDcrUrl()
                    break
                case "Accounts":
                    baseUrl = ConfigParser.getInstance().getCdsAuAccountsUrl()
                    break
                case "Balances":
                    baseUrl = ConfigParser.getInstance().getCdsAuBalancesUrl()
                    break
                case "Transactions":
                    baseUrl = ConfigParser.getInstance().getCdsAuTransactionUrl()
                    break
                case "Direct-Debit":
                    baseUrl = ConfigParser.getInstance().getCdsAuDirectDebitUrl()
                    break
                case "Scheduled-Payment":
                    baseUrl = ConfigParser.getInstance().getCdsAuSchedulePaymentUrl()
                    break
                case "Payees":
                    baseUrl = ConfigParser.getInstance().getCdsAuPayeeUrl()
                    break
                case "Product":
                    baseUrl = ConfigParser.getInstance().getCdsAuProductUrl()
                    break
                case "Customer":
                    baseUrl = ConfigParser.getInstance().getCdsCustomerUrl()
                    break
                case "Discovery":
                    baseUrl = ConfigParser.getInstance().getCdsDiscoveryUrl()
                    break
                case "CDR-Arrangement":
                    baseUrl = ConfigParser.getInstance().getCdrArrangementUrl()
                    break
                case "Admin":
                    baseUrl = ConfigParser.getInstance().getCdsAdminUrl()
                    break
            }
        } else {
            baseUrl = ConfigParser.getInstance().getBaseUrl()
        }

        return baseUrl
    }
}
