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

package com.wso2.openbanking.test.framework.util;

import com.wso2.openbanking.test.framework.exception.TestFrameworkException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.*;

/**
 * Configuration parser to read configurations from test-config.xml.
 */
public class ConfigParser {

    private static final Object lock = new Object();
    private static final Log log = LogFactory.getLog(ConfigParser.class);
    private static String configFilePath;
    private static final Map<String, Object> configuration = new HashMap<>();
    private static final Map<String, String> consentProcessorConfig = new HashMap<>();
    private static volatile ConfigParser parser = null;
    private OMElement rootElement;
    private String clientId;

    protected ConfigParser() throws TestFrameworkException {

        buildGlobalConfiguration();
    }

    /**
     * Maintain single instance of Config parser through out the implementations.
     *
     * @return ConfigParser object
     */
    public static ConfigParser getInstance() {

        if (parser == null) {
            synchronized (lock) {
                if (parser == null) {
                    try {
                        parser = new ConfigParser();
                    } catch (TestFrameworkException e) {
                        log.error("Failed to initiate config parser", e);
                        parser = null;
                    }
                }
            }
        }
        return parser;
    }

    public Map<String, Object> getConfiguration() {

        return configuration;
    }

    /**
     * Build global configurations from test-config.xml.
     */
    void buildGlobalConfiguration() throws TestFrameworkException {

        InputStream inStream = null;
        StAXOMBuilder builder;

        String warningMessage = "";
        try {
            if (configFilePath != null) {
                File openBankingConfig = new File(configFilePath);
                if (openBankingConfig.exists()) {
                    inStream = new FileInputStream(openBankingConfig);
                } else {
                    log.warn("No file found in the specified path: " + configFilePath
                            + ". Proceeding with default location.");
                }
            } else {
                File configXml = new File(this.getClass().getClassLoader()
                        .getResource("test-config.xml").getFile());
                if (configXml.exists()) {
                    inStream = new FileInputStream(configXml);
                }
            }

            if (inStream == null) {
                String message = "Test Framework configuration not found. Cause - " + warningMessage;
                if (log.isDebugEnabled()) {
                    log.debug(message);
                }
                throw new FileNotFoundException(message);
            }

            builder = new StAXOMBuilder(inStream);
            rootElement = builder.getDocumentElement();
            Deque<String> elementNames = new ArrayDeque<>();
            readChildElements(rootElement, elementNames);

        } catch (IOException | XMLStreamException e) {
            throw new TestFrameworkException("Error occurred while building configuration "
                    + "from test-config.xml", e);
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                log.error("Error closing the input stream for test-config.xml", e);
            }
        }
    }

    /**
     * Read element recursively and put in the configuration map.
     *
     * @param serverConfig OM Element
     * @param elementNames Deque of element names
     */
    private void readChildElements(OMElement serverConfig, Deque<String> elementNames) {

        for (Iterator childElements = serverConfig.getChildElements(); childElements.hasNext(); ) {
            OMElement element = (OMElement) childElements.next();
            elementNames.push(element.getLocalName());
            if (elementHasText(element)) {
                String key = getKey(elementNames);
                Object currentObject = configuration.get(key);
                String value = replaceSystemProperty(element.getText());
                if (currentObject == null) {
                    configuration.put(key, value);
                } else if (currentObject instanceof ArrayList) {
                    List<String> list = (ArrayList) currentObject;
                    if (!list.contains(value)) {
                        list.add(value);
                        configuration.put(key, list);
                    }
                } else {
                    if (!value.equals(currentObject)) {
                        List<Object> arrayList = new ArrayList<>(2);
                        arrayList.add(currentObject);
                        arrayList.add(value);
                        configuration.put(key, arrayList);
                    }
                }
            }
            readChildElements(element, elementNames);
            elementNames.pop();
        }
    }

    private boolean elementHasText(OMElement element) {

        String text = element.getText();
        return text != null && text.trim().length() != 0;
    }

    /**
     * Converts the hierarchical element name to key.
     *
     * @param elementNames hierarchical element name
     * @return key name
     */
    private String getKey(Deque<String> elementNames) {

        StringBuilder key = new StringBuilder();
        for (Iterator itr = elementNames.descendingIterator(); itr.hasNext(); ) {
            key.append(itr.next()).append(".");
        }
        key.deleteCharAt(key.lastIndexOf("."));
        return key.toString();
    }

    private String replaceSystemProperty(String propertyName) {

        int indexOfStartingChars = -1;
        int indexOfClosingBrace;

        StringBuilder nameBuilder = new StringBuilder(propertyName);
        while (indexOfStartingChars < nameBuilder.indexOf("${")
                && (indexOfStartingChars = nameBuilder.indexOf("${")) != -1
                && (indexOfClosingBrace = nameBuilder.toString().indexOf('}')) != -1) {
            // Is a property used?
            String sysProp = nameBuilder.substring(indexOfStartingChars + 2, indexOfClosingBrace);
            String propValue = System.getProperty(sysProp);
            if (propValue != null) {
                nameBuilder = new StringBuilder(nameBuilder.substring(0, indexOfStartingChars) + propValue
                        + nameBuilder.substring(indexOfClosingBrace + 1));
            }
        }
        propertyName = nameBuilder.toString();
        return propertyName;
    }

    /**
     * Check the Config of Browser Headless Mode.
     *
     * @return boolean
     */
    public boolean isHeadless() {

        return Boolean.parseBoolean(String.valueOf(ConfigParser.getInstance()
                .getConfiguration().get("BrowserAutomation.HeadlessEnabled")));
    }

    /**
     * Read Client Id.
     *
     * @return clientId
     */
    public Object getClientId() {

        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.Application.ClientID");
    }

    /**
     * Read Client Secret.
     *
     * @return client Secret
     */
    public Object getClientSecret() {

        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.Application.ClientSecret");
    }

    /**
     * Read PSU UserName.
     *
     * @return psu userName
     */
    public String getPsu() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("PSUInfo.Psu"));
    }

    /**
     * Read PSU Password.
     *
     * @return psu password
     */
    public String getPsuPassword() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("PSUInfo.PsuPassword"));
    }

    /**
     * Read Base Url Config.
     *
     * @return base url
     */
    public String getBaseUrl() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("Server.BaseUrl"));
    }

    /**
     * Read IAM Server Url Config.
     *
     * @return iam server url
     */
    public String getISServerUrl() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("Server.ISServerUrl"));
    }

    /**
     * Read APIM Server Url Config.
     *
     * @return apim server url
     */
    public String getApimServerUrl() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("Server.APIMServerUrl"));
    }

    /**
     * Read Redirect Url Config.
     *
     * @return redirect url
     */
    public Object getRedirectURL() {

        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.Application.RedirectURL");
    }

    /**
     * Read Gecko Driver Location.
     *
     * @return location of the geckoDriver
     */
    public String getFirefoxDriverLocation() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("BrowserAutomation.FirefoxDriverLocation"));
    }

    /**
     * Read Application Keystore Location.
     *
     * @return application keystore location
     */
    public Object getApplicationKeystoreLocation() {

        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.Application.KeyStore.Location");
    }

    /**
     * Read Application Keystore Alias.
     *
     * @return alias
     */
    public Object getApplicationKeystoreAlias() {

        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.Application.KeyStore.Alias");
    }

    /**
     * Read Application Keystore Password.
     *
     * @return keystore password
     */
    public Object getApplicationKeystorePassword() {

        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.Application.KeyStore.Password");
    }

    /**
     * Read Application Certificate Kid.
     *
     * @return kid
     */
    public String getApplicationCertificateKid() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("Application.Certificate.KID"));
    }

    /**
     * Read Is MTLS Enabled.
     *
     * @return boolean
     */
    public Object isMTLSEnabled() {

        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.Transport.MTLSEnabled");
    }

    /**
     * Read Transport Keystore Location.
     *
     * @return transport keystore location
     */
    public Object getTransportKeystoreLocation() {

        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.Transport.KeyStore.Location");
    }

    /**
     * Read Transport Keystore Password.
     *
     * @return keystore password
     */
    public Object getTransportKeystorePassword() {

        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.Transport.KeyStore.Password");
    }

    /**
     * Get Transport Keystore Alias.
     *
     * @return alias
     */
    public String getTransportKeystoreAlias() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("Transport.KeyStore.Alias"));
    }

    /**
     * Read Transport Keystore Type.
     *
     * @return keystore type
     */
    public Object getTransportKeystoreType() {

        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.Transport.KeyStore.Type");
    }

    /**
     * Read Transport Truststore Location.
     *
     * @return truststore location
     */
    public String getTransportTruststoreLocation() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("Transport.Truststore.Location"));
    }

    /**
     * Read Transport Truststore Password.
     *
     * @return truststore password
     */
    public String getTransportTruststorePassword() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("Transport.Truststore.Password"));
    }

    /**
     * Read Transport Truststore Type.
     *
     * @return truststore type
     */
    public String getTransportTruststoreType() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("Transport.Truststore.Type"));
    }

    /**
     * Read Access Token Expire Time.
     *
     * @return expiry time
     */
    public int getAccessTokenExpireTime() {

        return Integer.parseInt(String.valueOf(ConfigParser.getInstance()
                .getConfiguration().get("Common.AccessTokenExpireTime")));
    }

    /**
     * Read Tenant Domain.
     *
     * @return tenant domain
     */
    public String getTenantDomain() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("Common.TenantDomain"));
    }

    /**
     * Read Application Keystore Domain.
     *
     * @return Keystore Domain
     */
    public Object getApplicationKeystoreDomain() {
        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.Application.KeyStore.DomainName");
    }

    /**
     * Read Signing Algorithm.
     *
     * @return signing algorithm
     */
    public String getSigningAlgorithm() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("Common.SigningAlgorithm"));
    }

    /**
     * Read KeyManager Admin Username.
     *
     * @return admin username
     */
    public String getKeyManagerAdminUsername() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("Common.KeyManager.Admin.Username"));
    }

    /**
     * Read KeyManager Admin Password.
     *
     * @return admin password
     */
    public String getKeyManagerAdminPassword() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("Common.KeyManager.Admin.Password"));
    }

    /**
     * Read Publisher UserName.
     *
     * @return username
     */
    public String getPublisherAdminUsername() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("PublisherInfo.Publisher"));
    }

    /**
     * Read Publisher Password.
     *
     * @return password
     */
    public String getPublisherAdminPassword() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("PublisherInfo.PublisherPassword"));
    }

    /**
     * Read Provisioning Enable Config.
     *
     * @return boolean
     */
    public boolean isProvisioning() {

        return Boolean.parseBoolean(String.valueOf(ConfigParser.getInstance()
                .getConfiguration().get("Provisioning.Enabled")));
    }

    /**
     * Read Provision File Path.
     *
     * @return file path
     */
    public String getProvisionFilePath() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("Provisioning.ProvisionFilePath"));
    }

    /**
     * Read Non-Regulatory App Client Id.
     *
     * @return client id
     */
    public String getNonRegulatoryClientId() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("NonRegulatoryApplication.ClientID"));
    }

    /**
     * Read Non-Regulatory App Client Secret.
     *
     * @return client secret
     */
    public String getNonRegulatoryClientSecret() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("NonRegulatoryApplication.ClientSecret"));
    }

    /**
     * Read Non-Regulatory App Redirect Url.
     *
     * @return redirect url
     */
    public String getNonRegulatoryRedirectUrl() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("NonRegulatoryApplication.RedirectURL"));
    }

    /**
     * Read SSA File Path.
     *
     * @return ssa file path
     */
    public Object getSSAFilePath() {

        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.DCR.SSAPath");
    }

    /**
     * Read Self Signed SSA FilePath.
     *
     * @return file path
     */
    public Object getSelfSignedSSAFilePath() {

        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.DCR.SelfSignedSSAPath");
    }

    /**
     * Read the Key Id for SSA header.
     *
     * @return keyid
     */
    public String getSsaKeyId() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("DCR.SSAKeyId"));
    }

    /**
     * Read Software Id.
     *
     * @return software id
     */
    public Object getSoftwareId() {

        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.DCR.SoftwareId");
    }

    /**
     * Read Redirect Url.
     *
     * @return redirect url
     */
    public String getRedirectUri() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("DCR.RedirectUri"));
    }

    /**
     * Read Alternate Redirect Url.
     *
     * @return alternate redirect url
     */
    public Object getAlternateRedirectUri() {

        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.DCR.AlternateRedirectUri");
    }

    /**
     * Read Audience Value.
     *
     * @return audience value
     */
    public String getAudienceValue() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("ConsentApi.AudienceValue"));
    }

    /**
     * Read TPP UserName.
     *
     * @return tpp userName
     */
    public String getTppUserName() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("TPPInfo.Tpp"));
    }

    /**
     * Read TPP Password.
     *
     * @return tpp password
     */
    public String getTppPassword() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("TPPInfo.TppPassword"));
    }

    /**
     * Read Test Artifact Location.
     *
     * @return test artifact folder location
     */
    public String getTestArtifactLocation() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("Common.TestArtifactLocation"));
    }

    /**
     * Get OB solution version.
     *
     * @return OB Version
     */
    public String getSolutionVersion() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("SolutionVersion"));
    }

    /**
     * Get ID Permanence Secret Key.
     *
     * @return Id Permanence Secret Key
     */
    public String getIdPermanenceSecretKey() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("IdPermanence.SecretKey"));
    }

    /**
     * Get Authorisation Server URL.
     *
     * @return Authorisation Server Url
     */
    public String getAuthorisationServerUrl() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("Server.AuthorisationServerURL"));
    }

    /**
     * Get Micro Gateway Enabled.
     *
     * @return Micro Gateway Enabled
     */
    public boolean getMicroGatewayEnabled() {

        return Boolean.parseBoolean(String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("Server.MicroGateway.MicroGatewayEnabled")));
    }

    /**
     * Get DCR URL.
     *
     * @return dcr url
     */
    public String getDcrUrl() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("Server.MicroGateway.AU.DcrURL"));
    }

    /**
     * Get Cds Accounts URL.
     *
     * @return Cds Accounts URL
     */
    public String getCdsAuAccountsUrl() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("Server.MicroGateway.AU.CdsAuAccountsURL"));
    }

    /**
     * Get Cdr Arrangement URL.
     *
     * @return Cdr Arrangement URL
     */
    public String getCdrArrangementUrl() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("Server.MicroGateway.AU.CdrArrangementURL"));
    }

    /**
     * Get Cds Admin URL.
     *
     * @return Cds Admin URL
     */
    public String getCdsAdminUrl() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("Server.MicroGateway.AU.CdsAdminURL"));
    }

    /**
     * Get Cds Customer URL.
     *
     * @return Cds Customer URL
     */
    public String getCdsCustomerUrl() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("Server.MicroGateway.AU.CdsCustomerURL"));
    }

    /**
     * Get Cds Discovery URL.
     *
     * @return Cds Discovery URL
     */
    public String getCdsDiscoveryUrl() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("Server.MicroGateway.AU.CdsDiscoveryURL"));
    }

    /**
     * Get Cds Balances URL.
     *
     * @return Cds Balances URL
     */
    public String getCdsAuBalancesUrl() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("Server.MicroGateway.AU.CdsAuBalancesURL"));
    }

    /**
     * Get Cds Transaction URL.
     *
     * @return Cds Transaction URL
     */
    public String getCdsAuTransactionUrl() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("Server.MicroGateway.AU.CdsAuTransactionURL"));
    }

    /**
     * Get Cds Product URL.
     *
     * @return Cds Product URL
     */
    public String getCdsAuProductUrl() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("Server.MicroGateway.AU.CdsAuProductURL"));
    }

    /**
     * Get Cds Payee URL.
     *
     * @return Cds Payee URL
     */
    public String getCdsAuPayeeUrl() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("Server.MicroGateway.AU.CdsAuPayeeURL"));
    }

    /**
     * Get Cds Direct Debit URL.
     *
     * @return Cds Direct Debit URL
     */
    public String getCdsAuDirectDebitUrl() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("Server.MicroGateway.AU.CdsAuDirectDebitURL"));
    }

    /**
     * Get Cds Schedule Payment URL.
     *
     * @return Cds Schedule Payment URL
     */
    public String getCdsAuSchedulePaymentUrl() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("Server.MicroGateway.AU.CdsAuSchedulePaymentURL"));
    }

    /**
     * Get Customer Care Officer UserName.
     *
     * @return Customer Care Officer UserName
     */
    public String getCCPortal() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("CustomerCareInfo.CustomerCareUser"));
    }

    /**
     * Get Customer Care Officer Password.
     *
     * @return Customer Care Officer Password
     */
    public String getCCPortalPassword() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("CustomerCareInfo.CustomerCareUserPassword"));
    }

    public String getApiVersion() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("ApiVersion"));
    }

    public static String getRESTApiDCRAccessToken() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("RESTApi.DCRAccessToken"));
    }

    public static String getRESTApiApiId() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("RESTApi.ApiId"));
    }

    public boolean getMockCDRRegisterEnabled() {
        return Boolean.parseBoolean(String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("AUMockCDRRegister.Enabled")));
    }

    public String getMetaDataFileLocationForMockCDRRegister() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("AUMockCDRRegister.MetaDataFileLocation"));
    }

    public String getTransportKeystoreLocationForMockCDRRegister() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("AUMockCDRRegister.Transport.KeyStore.Location"));
    }

    public String getTransportTruststoreLocationForMockCDRRegister() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("AUMockCDRRegister.Transport.Truststore.Location"));
    }

    public String getTransportKeystoreTypeForMockCDRRegister() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("AUMockCDRRegister.Transport.KeyStore.Type"));
    }

    public String getTransportTruststorePasswordForMockCDRRegister() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("AUMockCDRRegister.Transport.Truststore.Password"));
    }

    public String getTransportTruststoreTypeForMockCDRRegister() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("AUMockCDRRegister.Transport.Truststore.Type"));
    }

    public String getTransportKeystorePasswordForMockCDRRegister() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("AUMockCDRRegister.Transport.KeyStore.Password"));
    }

    public String getMockADRSigningKeystoreLocation() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("AUMockCDRRegister.Application.KeyStore.Location"));
    }

    public String getMockADRSigningKeystorePassword() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("AUMockCDRRegister.Application.KeyStore.Password"));
    }

    public String getMockADRSigningKeystoreAlias() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("AUMockCDRRegister.Application.KeyStore.Alias"));
    }

    public Object getSigningCertificateKid() {
        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.Application.KeyStore.SigningKid");
    }

    public Object getDcrRedirectUri() {
        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.DCR.RedirectUri");
    }

    public Object getDCRAPIVersion() {
        return ConfigParser.getInstance().getConfiguration()
                .get("ApplicationConfigList.AppConfig.DCR.DCRAPIVersion");
    }

    public String getBasicAuthUser() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("BasicAuthInfo.BasicAuthUser"));
    }

    public String getBasicAuthUserPassword() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("BasicAuthInfo.BasicAuthUserPassword"));
    }

    public String getRevocationAudienceValue() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("ConsentApi.RevocationAudienceValue"));
    }

    public String getMockCDRRegisterHostName() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("AUMockCDRRegister.HostName"));
    }

    public String browserPreference() {
        return String.valueOf(ConfigParser.getInstance().getConfiguration().get("BrowserAutomation.BrowserPreference"));
    }

    public String getDriverLocation() {

        return String.valueOf(ConfigParser.getInstance().getConfiguration()
                .get("BrowserAutomation.WebDriverLocation"));
    }

}
