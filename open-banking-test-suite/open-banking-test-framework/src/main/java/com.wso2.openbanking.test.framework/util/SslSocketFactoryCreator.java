/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.test.framework.util;

import com.wso2.openbanking.test.framework.exception.TestFrameworkException;
import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;


/**
 * Creates an SSL socket factory for MTLS requests.
 */
public class SslSocketFactoryCreator {

  /**
   * Create SSL socket factory.
   *
   * @return an SSLSocketFactory implementation
   * @throws TestFrameworkException when an error occurs while loading the keystore and truststore
   */
  public SSLSocketFactory create() throws TestFrameworkException {
    try (FileInputStream keystoreLocation =
                 new FileInputStream(new File(ConfigParser.getInstance()
                         .getTransportKeystoreLocation()));
         FileInputStream truststoreLocation =
                 new FileInputStream(new File(ConfigParser.getInstance()
                         .getTransportTruststoreLocation()))) {

      KeyStore keyStore = KeyStore.getInstance(ConfigParser.getInstance()
              .getTransportKeystoreType());
      keyStore.load(keystoreLocation, ConfigParser.getInstance()
              .getTransportKeystorePassword().toCharArray());
      KeyStore trustStore = KeyStore.getInstance(ConfigParser.getInstance()
              .getTransportTruststoreType());
      trustStore.load(truststoreLocation, ConfigParser.getInstance()
              .getTransportTruststorePassword().toCharArray());

      // Manually create a new socketfactory and pass in the required values.
      return new SSLSocketFactory(keyStore, ConfigParser.getInstance()
              .getTransportKeystorePassword(), trustStore);
    } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException
            | KeyManagementException | UnrecoverableKeyException | IOException e) {
      throw new TestFrameworkException("Unable to load the transport keystore and truststore", e);
    }
  }

  /**
   * Create SSL socket factory.
   * @param keystoreFilePath keystore file path.
   * @param keystorePassword keystore password.
   * @return an SSLSocketFactory implementation.
   * @throws TestFrameworkException when an error occurs while loading the keystore and truststore.
   */
  public SSLSocketFactory create(String keystoreFilePath, String keystorePassword) throws TestFrameworkException {
    try (FileInputStream keystoreLocation =
                 new FileInputStream(new File(keystoreFilePath));
         FileInputStream truststoreLocation =
                 new FileInputStream(new File(ConfigParser.getInstance()
                         .getTransportTruststoreLocation()))) {

      KeyStore keyStore = KeyStore.getInstance(ConfigParser.getInstance()
              .getTransportKeystoreType());
      keyStore.load(keystoreLocation, keystorePassword.toCharArray());
      KeyStore trustStore = KeyStore.getInstance(ConfigParser.getInstance()
              .getTransportTruststoreType());
      trustStore.load(truststoreLocation, ConfigParser.getInstance()
              .getTransportTruststorePassword().toCharArray());

      // Manually create a new socket factory and pass in the required values.
      return new SSLSocketFactory(keyStore, keystorePassword, trustStore);
    } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException
            | KeyManagementException | UnrecoverableKeyException | IOException e) {
      throw new TestFrameworkException("Unable to load the transport keystore and truststore", e);
    }
  }
}
