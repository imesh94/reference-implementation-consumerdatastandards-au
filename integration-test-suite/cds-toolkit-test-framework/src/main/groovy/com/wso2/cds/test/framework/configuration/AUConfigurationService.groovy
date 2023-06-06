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

package com.wso2.cds.test.framework.configuration

import com.wso2.cds.test.framework.constant.AUConfigConstants
import com.wso2.openbanking.test.framework.configuration.OBConfigurationService
import com.wso2.openbanking.test.framework.constant.OBConfigConstants

/**
 * Class for provide configuration data to the AU layers and AU tests
 * This class provide OB configuration and AU configuration.
 */
class AUConfigurationService extends OBConfigurationService {

    /**
     * Get Mock CDR Register enabled
     */
    boolean getMockCDREnabled() {
        if (configuration.get(AUConfigConstants.MOCK_CDR_REGISTER + "." + AUConfigConstants.MOCK_CDR_REG_ENABLED).equals("true")) {
            return true
        }
        return false
    }

    /**
     * Get Mock CDR Hostname
     */
    String getMockCDRHostname() {
        return configuration.get(AUConfigConstants.MOCK_CDR_REGISTER + "." + AUConfigConstants.MOCK_CDR_REG_HOST_NAME)
    }

    /**
     * Get Mock CDR Register Meta data file location
     */
    String getMockCDRMetaDataFileLoc() {
        return configuration.get(AUConfigConstants.MOCK_CDR_REGISTER + "." + AUConfigConstants.MOCK_CDR_REG_META_DATA_FILE_LOC)
    }

    /**
     * Get Mock CDR Register Transport OBKeyStore Location
     */
    String getMockCDRTransKeystoreLoc() {
        return configuration.get(AUConfigConstants.MOCK_CDR_REGISTER + "." + AUConfigConstants.MOCK_CDR_REG_TRANSPORT
                + "." + AUConfigConstants.MOCK_CDR_REG_TRANS_KEYSTORE + "." + AUConfigConstants.MOCK_CDR_REG_TRANS_KEYSTORE_LOC)
    }

    /**
     * Get Mock CDR Register Transport OBKeyStore Type
     */
    String getMockCDRTransKeystoreType() {
        return configuration.get(AUConfigConstants.MOCK_CDR_REGISTER + "." + AUConfigConstants.MOCK_CDR_REG_TRANSPORT
                + "." + AUConfigConstants.MOCK_CDR_REG_TRANS_KEYSTORE + "." + AUConfigConstants.MOCK_CDR_REG_TRANS_KEYSTORE_TYPE)
    }

    /**
     * Get Mock CDR Register Transport OBKeyStore Password
     */
    String getMockCDRTransKeystorePWD() {
        return configuration.get(AUConfigConstants.MOCK_CDR_REGISTER + "." + AUConfigConstants.MOCK_CDR_REG_TRANSPORT
                + "." + AUConfigConstants.MOCK_CDR_REG_TRANS_KEYSTORE + "." + AUConfigConstants.MOCK_CDR_REG_TRANS_KEYSTORE_PWD)
    }

    /**
     * Get Mock CDR Register Transport Truststore Location
     */
    String getMockCDRTransTruststoreLoc() {
        return configuration.get(AUConfigConstants.MOCK_CDR_REGISTER + "." + AUConfigConstants.MOCK_CDR_REG_TRANSPORT
                + "." + AUConfigConstants.MOCK_CDR_REG_TRANS_TRUSTSTORE + "." + AUConfigConstants.MOCK_CDR_REG_TRANS_TRUSTSTORE_LOC)
    }

    /**
     * Get Mock CDR Register Transport Truststore Type
     */
    String getMockCDRTransTruststoreType() {
        return configuration.get(AUConfigConstants.MOCK_CDR_REGISTER + "." + AUConfigConstants.MOCK_CDR_REG_TRANSPORT
                + "." + AUConfigConstants.MOCK_CDR_REG_TRANS_TRUSTSTORE + "." + AUConfigConstants.MOCK_CDR_REG_TRANS_TRUSTSTORE_TYPE)
    }

    /**
     * Get Mock CDR Register Transport Truststore Password
     */
    String getMockCDRTransTruststorePWD() {
        return configuration.get(AUConfigConstants.MOCK_CDR_REGISTER + "." + AUConfigConstants.MOCK_CDR_REG_TRANSPORT
                + "." + AUConfigConstants.MOCK_CDR_REG_TRANS_TRUSTSTORE + "." + AUConfigConstants.MOCK_CDR_REG_TRANS_TRUSTSTORE_PWD)
    }

    /**
     * Get Mock CDR Register Transport OBKeyStore Location
     */
    String getMockCDRAppKeystoreLoc() {
        return configuration.get(AUConfigConstants.MOCK_CDR_REGISTER + "." + AUConfigConstants.MOCK_CDR_REG_APP
                + "." + AUConfigConstants.MOCK_CDR_REG_APP_KEY + "." + AUConfigConstants.MOCK_CDR_REG_APP_KEY_LOC)
    }

    /**
     * Get Mock CDR Register Transport OBKeyStore Alias
     */
    String getMockCDRAppKeystoreAlias() {
        return configuration.get(AUConfigConstants.MOCK_CDR_REGISTER + "." + AUConfigConstants.MOCK_CDR_REG_APP
                + "." + AUConfigConstants.MOCK_CDR_REG_APP_KEY + "." + AUConfigConstants.MOCK_CDR_REG_APP_KEY_ALIAS)
    }

    /**
     * Get Mock CDR Register Transport OBKeyStore Password
     */
    String getMockCDRAppKeystorePWD() {
        return configuration.get(AUConfigConstants.MOCK_CDR_REGISTER + "." + AUConfigConstants.MOCK_CDR_REG_APP
                + "." + AUConfigConstants.MOCK_CDR_REG_APP_KEY + "." + AUConfigConstants.MOCK_CDR_REG_APP_KEY_PWD)
    }

    /**
     * Get Rest API DCR Access token
     */
    String getRestAPIDCRAccessToken() {
        return configuration.get(AUConfigConstants.REST_API + "." + AUConfigConstants.REST_API_DCR_ACCESS_TOKEN)
    }

    /**
     * Get Rest API ID
     */
    String getRestAPIID() {
        return configuration.get(AUConfigConstants.REST_API + "." + AUConfigConstants.REST_API_API_ID)
    }

    /**
     * Get Rest API ID
     */
    String getIDPermanence() {
        return configuration.get(AUConfigConstants.ID_PERMANENCE + "." + AUConfigConstants.ID_PERMANENCE_SECRET_KEY)
    }

    /**
     * Get Micro-Gateway Enabled
     */
    boolean getMicroGatewayEnabled() {
        if (configuration.get(AUConfigConstants.MICRO_GATEWAY + "." + AUConfigConstants.MICRO_GATEWAY_ENABLED).equals("true")) {
            return true
        } else {
            return false
        }
    }

    /**
     * Get Micro-Gateway DCR URL
     */
    String getMicroGatewayDCRUrl() {
        return configuration.get(AUConfigConstants.MICRO_GATEWAY + "." + AUConfigConstants.MICRO_GATEWAY_DCR_URL)
    }

    /**
     * Get Micro-Gateway CDS Accounts URL
     */
    String getMicroGatewayAccountsUrl() {
        return configuration.get(AUConfigConstants.MICRO_GATEWAY + "." + AUConfigConstants.MICRO_GATEWAY_CDS_ACCOUNTS_URL)
    }

    /**
     * Get Micro-Gateway CDS Balances URL
     */
    String getMicroGatewayBalancesUrl() {
        return configuration.get(AUConfigConstants.MICRO_GATEWAY + "." + AUConfigConstants.MICRO_GATEWAY_CDS_BALANCES_URL)
    }

    /**
     * Get Micro-Gateway Cds Au Transaction URL
     */
    String getMicroGatewayTransactionURL() {
        return configuration.get(AUConfigConstants.MICRO_GATEWAY + "." + AUConfigConstants.MICRO_GATEWAY_CDS_TRANSACTIONS_URL)
    }

    /**
     * Get Micro-Gateway Cds Au Direct-Debit URL
     */
    String getMicroGatewayDirectDebitURL() {
        return configuration.get(AUConfigConstants.MICRO_GATEWAY + "." + AUConfigConstants.MICRO_GATEWAY_CDS_DIRECT_DEBIT_URL)
    }

    /**
     * Get Micro-Gateway Cds Au Schedule-Payment URL
     */
    String getMicroGatewaySchedulePayURL() {
        return configuration.get(AUConfigConstants.MICRO_GATEWAY + "." + AUConfigConstants.MICRO_GATEWAY_CDS_SCHEDULED_PAY_URL)
    }

    /**
     * Get Micro-Gateway Cds Au Payee-URL
     */
    String getMicroGatewayPayeeURL() {
        return configuration.get(AUConfigConstants.MICRO_GATEWAY + "." + AUConfigConstants.MICRO_GATEWAY_CDS_PAYEE_URL)
    }

    /**
     * Get Micro-Gateway Cds Au ProductURL
     */
    String getMicroGatewayProductURL() {
        return configuration.get(AUConfigConstants.MICRO_GATEWAY + "." + AUConfigConstants.MICRO_GATEWAY_CDS_PRODUCT_URL)
    }

    /**
     * Get Micro-Gateway Cds Customer URL
     */
    String getMicroGatewayCustomerURL() {
        return configuration.get(AUConfigConstants.MICRO_GATEWAY + "." + AUConfigConstants.MICRO_GATEWAY_CDS_CUSTOMER_URL)
    }

    /**
     * Get Micro-Gateway Cds Discovery URL
     */
    String getMicroGatewayDiscoveryURL() {
        return configuration.get(AUConfigConstants.MICRO_GATEWAY + "." + AUConfigConstants.MICRO_GATEWAY_CDS_DISCOVERY_URL)
    }

    /**
     * Get Micro-Gateway Cdr Arrangement URL
     */
    String getMicroGatewayArrangementURL() {
        return configuration.get(AUConfigConstants.MICRO_GATEWAY + "." + AUConfigConstants.MICRO_GATEWAY_CDS_ARRANGEMENT_URL)
    }

    /**
     * Get Micro-Gateway Cds Admin URL
     */
    String getMicroGatewayAdminURL() {
        return configuration.get(AUConfigConstants.MICRO_GATEWAY + "." + AUConfigConstants.MICRO_GATEWAY_CDS_ADMIN_URL)
    }

    /**
     * Get Profile Selection Configurations
     */
    String getProfileSelectionEnabled() {
        return configuration.get(AUConfigConstants.PROFILE_SELECTION + "." + AUConfigConstants.PROFILE_SELECTION_ENABLED)
    }

    /**
     * Get Sharable Account Url
     */
    String getSharableAccountUrl() {
        return configuration.get(OBConfigConstants.SERVER + "." + AUConfigConstants.SHARABLE_ACCOUNT_URL)
    }
}

