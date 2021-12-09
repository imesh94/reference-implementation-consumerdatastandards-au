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

package com.wso2.openbanking.cds.common.config;

import com.wso2.openbanking.accelerator.common.constant.OpenBankingConstants;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingRuntimeException;
import com.wso2.openbanking.accelerator.common.util.CarbonUtils;
import com.wso2.openbanking.cds.common.utils.CommonConstants;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.securevault.SecretResolver;
import org.wso2.securevault.SecretResolverFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import javax.xml.stream.XMLStreamException;

/**
 * Config parser for open-banking-cds.xml
 */
public class OpenBankingCDSConfigParser {

    // To enable attempted thread-safety using double-check locking
    private static final Object lock = new Object();
    private static final Log log = LogFactory.getLog(OpenBankingCDSConfigParser.class);

    private static OpenBankingCDSConfigParser parser;
    private static String configFilePath;
    private static SecretResolver secretResolver;
    private OMElement rootElement;

    private static final Map<String, Object> configuration = new HashMap<>();

    /**
     * Private Constructor of config parser.
     */
    private OpenBankingCDSConfigParser() {

        buildConfiguration();
    }

    /**
     * Singleton getInstance method to create only one object.
     *
     * @return OpenBankingConfigParser object
     */
    public static OpenBankingCDSConfigParser getInstance() {

        if (parser == null) {
            synchronized (lock) {
                if (parser == null) {
                    parser = new OpenBankingCDSConfigParser();
                }
            }
        }
        return parser;
    }

    /**
     * Method to get an instance of ConfigParser when custom file path is provided.
     *
     * @param filePath Custom file path
     * @return OpenBankingConfigParser object
     */
    public static OpenBankingCDSConfigParser getInstance(String filePath) {

        configFilePath = filePath;
        return getInstance();
    }

    /**
     * Method to read the configuration (in a recursive manner) as a model and put them in the configuration map.
     */
    private void buildConfiguration() {

        InputStream inStream = null;

        try {
            if (configFilePath != null) {
                File openBankingConfigXml = new File(configFilePath);
                if (openBankingConfigXml.exists()) {
                    inStream = new FileInputStream(openBankingConfigXml);
                }
            } else {
                File openBankingConfigXml = new File(CarbonUtils.getCarbonConfigDirPath(),
                        CommonConstants.OB_CONFIG_FILE);
                if (openBankingConfigXml.exists()) {
                    inStream = new FileInputStream(openBankingConfigXml);
                }
            }
            if (inStream == null) {
                String message = "open-banking configuration not found at: " + configFilePath + " . Cause - ";
                if (log.isDebugEnabled()) {
                    log.debug(message);
                }
                throw new FileNotFoundException(message);
            }
            StAXOMBuilder builder = new StAXOMBuilder(inStream);
            builder.setDoDebug(false);
            rootElement = builder.getDocumentElement();
            Stack<String> nameStack = new Stack<>();
            secretResolver = SecretResolverFactory.create(rootElement, true);
            readChildElements(rootElement, nameStack);
        } catch (IOException | XMLStreamException | OMException e) {
            throw new OpenBankingRuntimeException("Error occurred while building configuration from " +
                    "open-banking-cds.xml", e);
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                log.error("Error closing the input stream for open-banking-cds.xml", e);
            }
        }
    }

    /**
     * Method to obtain map of configs
     *
     * @return Config map
     */
    public Map<String, Object> getConfiguration() {

        return configuration;
    }

    /**
     * Method to read text configs from xml when root element is given
     *
     * @param serverConfig XML root element object
     * @param nameStack    stack of config names
     */
    private void readChildElements(OMElement serverConfig, Stack<String> nameStack) {

        for (Iterator childElements = serverConfig.getChildElements(); childElements.hasNext(); ) {
            OMElement element = (OMElement) childElements.next();
            nameStack.push(element.getLocalName());
            if (elementHasText(element)) {
                String key = getKey(nameStack);
                Object currentObject = configuration.get(key);
                String value = replaceSystemProperty(element.getText());
                if (secretResolver != null && secretResolver.isInitialized() &&
                        secretResolver.isTokenProtected(key)) {
                    value = secretResolver.resolve(key);
                }
                if (currentObject == null) {
                    configuration.put(key, value);
                } else if (currentObject instanceof ArrayList) {
                    ArrayList list = (ArrayList) currentObject;
                    if (!list.contains(value)) {
                        list.add(value);
                        configuration.put(key, list);
                    }
                } else {
                    if (!value.equals(currentObject)) {
                        ArrayList<Object> arrayList = new ArrayList<>(2);
                        arrayList.add(currentObject);
                        arrayList.add(value);
                        configuration.put(key, arrayList);
                    }
                }
            }
            readChildElements(element, nameStack);
            nameStack.pop();
        }
    }

    /**
     * Method to check whether config element has text value.
     *
     * @param element root element as a object
     * @return availability of text in the config
     */
    private boolean elementHasText(OMElement element) {

        String text = element.getText();
        return text != null && text.trim().length() != 0;
    }

    /**
     * Method to obtain config key from stack
     *
     * @param nameStack Stack of strings with names
     * @return key as a String
     */
    private String getKey(Stack<String> nameStack) {

        StringBuilder key = new StringBuilder();
        for (int index = 0; index < nameStack.size(); index++) {
            String name = nameStack.elementAt(index);
            key.append(name).append(".");
        }
        key.deleteCharAt(key.lastIndexOf("."));
        return key.toString();
    }

    /**
     * Method to replace system properties in configs.
     *
     * @param text String that may require modification
     * @return modified string
     */
    private String replaceSystemProperty(String text) {

        int indexOfStartingChars = -1;
        int indexOfClosingBrace;

        // The following condition deals with properties.
        // Properties are specified as ${system.property},
        // and are assumed to be System properties
        StringBuilder textBuilder = new StringBuilder(text);
        while (indexOfStartingChars < textBuilder.indexOf("${")
                && (indexOfStartingChars = textBuilder.indexOf("${")) != -1
                && (indexOfClosingBrace = textBuilder.indexOf("}")) != -1) { // Is a property used?
            String sysProp = textBuilder.substring(indexOfStartingChars + 2, indexOfClosingBrace);
            String propValue = System.getProperty(sysProp);
            if (propValue != null) {
                textBuilder = new StringBuilder(textBuilder.substring(0, indexOfStartingChars) + propValue
                        + textBuilder.substring(indexOfClosingBrace + 1));
            }
            if (sysProp.equals(OpenBankingConstants.CARBON_HOME) &&
                    System.getProperty(OpenBankingConstants.CARBON_HOME).equals(".")) {
                textBuilder.insert(0, new File(".").getAbsolutePath() + File.separator);
            }
        }
        return textBuilder.toString();
    }

    //check account masking is enabled
    public boolean isAccountMaskingEnabled() {
        return Boolean.parseBoolean((String) configuration.get(CommonConstants.ACCOUNT_MASKING));
    }

    public String getIdPermanenceSecretKey() {
        String value = (String) configuration.get(CommonConstants.ID_PERMANENCE_SECRET_KEY);
        return value == null ? "" : value;
    }

    /**
     * Returns the element with the provided key
     *
     * @param key local part name
     * @return Corresponding value for key
     */
    protected Object getConfigElementFromKey(String key) {

        return configuration.get(key);
    }

    /**
     * Check metadata cache is enabled from config
     *
     * @return configured boolean value, default value is false
     */
    public boolean isMetadataCacheEnabled() {
        Object config = getConfigElementFromKey(CommonConstants.METADATA_CACHE_ENABLED);
        if (config != null) {
            return Boolean.parseBoolean((String) config);
        } else {
            return false;
        }
    }

    /**
     * Read metadata cache update period in minutes from config
     *
     * @return configured time in minutes, default value is 5
     */
    public int getMetaDataCacheUpdatePeriodInMinutes() {
        Object config = getConfigElementFromKey(CommonConstants.METADATA_CACHE_UPDATE_TIME);
        if (config != null && 0 < Integer.parseInt((String) config)) {
            // configured value is a positive number
            return Integer.parseInt((String) config);
        }
        return 5;
    }

    /**
     * Read data recipients discovery url from config
     *
     * @return configured url, default value is "https://api.cdr.gov.au/cdr-register/v1/banking/data-recipients"
     */
    public String getDataRecipientsDiscoveryUrl() {

        Object config = getConfigElementFromKey(CommonConstants.METADATA_CACHE_DATA_RECIPIENTS_URL);
        if (config != null) {
            return (String) config;
        }
        return "https://api.cdr.gov.au/cdr-register/v1/banking/data-recipients";
    }

    /**
     * Read APIM DCR register url from config
     * @return configured url
     */
    public String getDcrInternalUrl() {

        Object config = getConfigElementFromKey(CommonConstants.METADATA_CACHE_DCR_INTERNAL_URL);
        return (config != null) ? (String) config : "";
    }

    /**
     * Read APIM application search url from config
     *
     * @return configured url
     * @see <a href="https://apim.docs.wso2.com/en/latest/reference/product-apis/admin-apis/admin-v2/admin-v2/
     * #tag/Applications/paths/~1applications/get">APIM Search Applications Doc</a>
     */
    public String getApimApplicationsSearchUrl() {

        Object config = getConfigElementFromKey(CommonConstants.METADATA_CACHE_APPLICATION_SEARCH_URL);
        return (config != null) ? (String) config : "";
    }

    /**
     * Read retry count from config
     * @return retry count, default value is 2
     */
    public int getRetryCount() {

        Object config = getConfigElementFromKey(CommonConstants.METADATA_CACHE_RETRY_COUNT);
        if (config != null && 0 < Integer.parseInt((String) config)) {
            // configured value is a positive number
            return Integer.parseInt((String) config);
        }
        return 2;
    }

    /**
     * Read metadata cache expiry time in minutes from config
     * @return expiry time, default value is 2 min
     */
    public int getCacheExpiryInMinutes() {

        Object config = getConfigElementFromKey(CommonConstants.METADATA_CACHE_EXPIRY);
        if (config != null && 0 < Integer.parseInt((String) config)) {
            // configured value is a positive number
            return Integer.parseInt((String) config);
        }
        return 2;
    }

    /**
     * Check if data holder responsibilities can execute as bulk operations
     * @return configured boolean value, default value is true
     */
    public boolean isBulkOperation() {
        Object config = getConfigElementFromKey(CommonConstants.METADATA_CACHE_BULK_EXECUTE);
        if (config != null) {
            return Boolean.parseBoolean((String) config);
        } else {
            return true;
        }
    }

    /**
     * Read bulk execution hour from config
     * @return execution hour, default value is 2 AM
     */
    public int getBulkExecutionHour() {

        Object config = getConfigElementFromKey(CommonConstants.METADATA_CACHE_BULK_EXECUTE_HOUR);
        if (config != null) {
            final int hour = Integer.parseInt((String) config);
            if (0 <= hour && 24 > hour) {
                // configured value is a valid hour
                return hour;
            }
        }
        return 2;
    }

    /**
     * Check if access token encryption is enabled
     * @return configured boolean value, default value is true
     */
    public boolean isTokenEncryptionEnabled() {
        Object config = getConfigElementFromKey(CommonConstants.TOKEN_ENCRYPTION_ENABLED);
        if (config != null) {
            return Boolean.parseBoolean((String) config);
        } else {
            return true;
        }
    }

    /**
     * Read token encryption secret from config
     * @return token encryption secret, default value is "wso2"
     */
    public String getTokenEncryptionSecretKey() {
        return getConfigElementFromKey(CommonConstants.TOKEN_ENCRYPTION_SECRETKEY) == null ? "wso2" :
                ((String) getConfigElementFromKey(CommonConstants.TOKEN_ENCRYPTION_SECRETKEY)).trim();
    }

    /**
     * Read the Admin API self link Url
     * @return configured url
     */
    public String getAdminAPISelfLink () {
        String value = (String) getConfigElementFromKey(CommonConstants.ADMIN_API_SELF_LINK);
        return value == null ? "" : value;
    }

    /**
     * Read the holder specific id (HID) from config.
     *
     * @return configured value of the x-<HID>-v
     */
    public String getHolderSpecificIdentifier() {
        Object config = getConfigElementFromKey(CommonConstants.HOLDER_SPECIFIC_IDENTIFIER);
        return (config != null) ? (String) config : "";
    }

    /**
     * Get jwt authentication enabled status.
     *
     * @return boolean
     */
    public boolean getJWTAuthEnabled() {

        Object config = getConfigElementFromKey(CommonConstants.JWT_AUTH_ENABLED);
        if (config != null) {
            return Boolean.parseBoolean((String) config);
        } else {
            return false;
        }
    }

    /**
     * Get issuer claim for jwt authentication.
     *
     * @return issuer claim
     */
    public String getJWTAuthIssuer() {

        String value = (String) getConfigElementFromKey(CommonConstants.JWT_AUTH_ISS);
        return value == null ? "" : value;
    }

    /**
     * Get sub claim for jwt authentication.
     *
     * @return sub claim
     */
    public String getJWTAuthSubject() {

        String value = (String) getConfigElementFromKey(CommonConstants.JWT_AUTH_SUB);
        return value == null ? "" : value;
    }

    /**
     * Get aud claim for jwt authentication.
     *
     * @return sub claim
     */
    public String getJWTAuthAudience() {

        String value = (String) getConfigElementFromKey(CommonConstants.JWT_AUTH_AUD);
        return value == null ? "" : value;
    }

    /**
     * Get JWKS Url for jwt signature verification.
     *
     * @return aud claim
     */
    public String getJWTAuthJWKSUrl() {

        String value = (String) getConfigElementFromKey(CommonConstants.JWT_AUTH_JWKS_URL);
        return value == null ? "" : value;
    }

}
