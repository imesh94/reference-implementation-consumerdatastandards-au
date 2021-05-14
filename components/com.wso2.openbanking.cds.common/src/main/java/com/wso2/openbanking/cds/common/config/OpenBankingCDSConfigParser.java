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
import javax.xml.namespace.QName;
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
    private static final Map<Integer, String> revocationValidators = new HashMap<>();
    private static final Map<String, Map<String, String>> dcrConfigs = new HashMap<>();
    private static final Map<String, String> consentMgtConfigs = new HashMap<>();

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
            buildDCRConfigs();
            buildConsentManagementConfigs();
        } catch (IOException | XMLStreamException | OMException e) {
            throw new OpenBankingRuntimeException("Error occurred while building configuration from open-banking.xml",
                    e);
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

    private void buildDCRConfigs() {

        OMElement dcrElement = rootElement.getFirstChildWithName(
                new QName(CommonConstants.OB_CDS_CONFIG_QNAME, CommonConstants.DCR_CONFIG_TAG));
        Map<String, String> dcrSubConfigValues = new HashMap<>();
        if (dcrElement != null) {
            //obtaining each parameter type element under DCR tag
            Iterator parameterTypeElement = dcrElement.getChildElements();
            while (parameterTypeElement.hasNext()) {
                OMElement parameterType = (OMElement) parameterTypeElement.next();
                String configName = parameterType.getLocalName();
                //obtaining each step under each consent type
                Iterator<OMElement> methodType = parameterType.getChildrenWithName(
                        new QName(CommonConstants.OB_CDS_CONFIG_QNAME, CommonConstants.METHOD_CONFIG_TAG));
                if (methodType != null) {
                    while (methodType.hasNext()) {
                        OMElement executorElement = methodType.next();
                        String methodName = executorElement.getText();
                        dcrSubConfigValues.put(methodName, methodName);
                    }
                }
                dcrConfigs.put(configName, dcrSubConfigValues);
            }
        }

    }

    private void buildConsentManagementConfigs() {

        OMElement consentMgtElement = rootElement.getFirstChildWithName(
                new QName(CommonConstants.OB_CDS_CONFIG_QNAME, CommonConstants.CONSENT_MGT_CONFIG_TAG));

        if (consentMgtElement != null) {
            //obtaining each parameter type element under ConsentManagement tag
            Iterator parameterTypeElement = consentMgtElement.getChildElements();
            while (parameterTypeElement.hasNext()) {
                OMElement parameterType = (OMElement) parameterTypeElement.next();
                String parameterTypeName = parameterType.getLocalName();
                String parameterValues = parameterType.getText();

                consentMgtConfigs.put(parameterTypeName, parameterValues);
            }
        }
    }

    /**
     * Returns the revocation validators map.
     * <p>
     * The revocation validator map contains revocation type (OCSP/CRL) and its executing priority.
     * The default priority value has set as 1 for OCSP type, as OCSP validation is faster than the CRL validation
     *
     * @return certificate revocation validators map
     */
    public Map<Integer, String> getCertificateRevocationValidators() {

        return revocationValidators;
    }

    public Map<String, Map<String, String>> getDcrConfigs() {

        return dcrConfigs;
    }

    public Map<String, String> getConsentMgtConfigs() {

        return consentMgtConfigs;
    }
}
