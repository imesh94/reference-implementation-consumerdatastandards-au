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

package com.wso2.cds.test.framework.keystore

import com.wso2.bfsi.test.framework.exception.TestFrameworkException
import com.wso2.openbanking.test.framework.keystore.OBKeyStore
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import java.security.Key
import java.security.KeyStore
import java.security.cert.Certificate

/**
 * Class for provide keystore functions for AU Layer
 */
class AUKeyStore extends OBKeyStore{

    private static AUConfigurationService auConfiguration = new AUConfigurationService()

    /**
     * Get Mock-CDR register application Keystore
     * @return
     * @throws TestFrameworkException
     */
    static KeyStore getMockCDRApplicationKeyStore() throws TestFrameworkException {
        return getKeyStore(auConfiguration.getMockCDRAppKeystoreLoc(),auConfiguration.getMockCDRAppKeystorePWD());
    }

    /**
     * Get Mock-CDR register application Keystore Certificate
     * @return
     * @throws TestFrameworkException
     */
    static Certificate getCertificateFromMockCDRKeyStore() throws TestFrameworkException {
        KeyStore keyStore = getKeyStore(auConfiguration.getMockCDRAppKeystoreLoc(),auConfiguration.getMockCDRAppKeystorePWD())
        return getCertificate(keyStore
                ,auConfiguration.getMockCDRAppKeystoreAlias(),auConfiguration.getMockCDRAppKeystorePWD())
    }

    /**
     * Get Mock-CDR register Signing key
     * @return
     * @throws TestFrameworkException
     */
    static Key getMockCDRSigningKey() throws TestFrameworkException {
        return getSigningKey(auConfiguration.getMockCDRAppKeystoreLoc(),auConfiguration.getMockCDRAppKeystorePWD()
                ,auConfiguration.getMockCDRAppKeystoreAlias())
    }

}

