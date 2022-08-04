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

import com.wso2.bfsi.test.framework.exception.TestFrameworkException
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import org.apache.http.conn.ssl.SSLSocketFactory

import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException

/**
 * Class for create Socket factory
 */
class AUSSLSocketFactoryCreator {

    AUConfigurationService auConfiguration

    AUSSLSocketFactoryCreator() {
        auConfiguration = new AUConfigurationService()
    }

    /**
     * Create SSL socket factory to invoke the AU mock CDR Register
     *
     * @return an SSLSocketFactory implementation
     * @throws TestFrameworkException when an error occurs while loading the keystore and truststore
     */
    SSLSocketFactory createForMockCDRRegister() throws TestFrameworkException {
        try {

            FileInputStream keystoreLocation = new FileInputStream(new File(auConfiguration.getMockCDRTransKeystoreLoc()))
            FileInputStream truststoreLocation = new FileInputStream(new File(auConfiguration.getMockCDRTransTruststoreLoc()))

            KeyStore keyStore = KeyStore.getInstance(auConfiguration.getMockCDRTransKeystoreType());
            keyStore.load(keystoreLocation, auConfiguration.getMockCDRTransKeystorePWD().toCharArray());
            KeyStore trustStore = KeyStore.getInstance(auConfiguration.getMockCDRTransTruststoreType());
            trustStore.load(truststoreLocation, auConfiguration.getMockCDRTransTruststorePWD().toCharArray());

            return new SSLSocketFactory(keyStore, auConfiguration.getMockCDRTransKeystorePWD(), trustStore)

        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | KeyManagementException | UnrecoverableKeyException | IOException e) {
            throw new TestFrameworkException("Unable to load the transport keystore and truststore", e);
        }
    }
}
