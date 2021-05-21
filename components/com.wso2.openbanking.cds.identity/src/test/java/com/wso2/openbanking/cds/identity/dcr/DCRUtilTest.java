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
package com.wso2.openbanking.cds.identity.dcr;

import com.google.gson.Gson;
import com.wso2.openbanking.accelerator.common.constant.OpenBankingConstants;
import com.wso2.openbanking.accelerator.common.util.JWTUtils;
import com.wso2.openbanking.accelerator.identity.dcr.exception.DCRValidationException;
import com.wso2.openbanking.accelerator.identity.dcr.model.RegistrationRequest;
import com.wso2.openbanking.accelerator.identity.dcr.validation.DCRCommonConstants;
import com.wso2.openbanking.accelerator.identity.dcr.validation.RegistrationValidator;
import com.wso2.openbanking.accelerator.identity.internal.IdentityExtensionsDataHolder;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.identity.dcr.model.CDSRegistrationRequest;
import com.wso2.openbanking.cds.identity.dcr.util.RegistrationTestConstants;
import com.wso2.openbanking.cds.identity.dcr.utils.ValidationUtils;
import com.wso2.openbanking.cds.identity.dcr.validation.CDSRegistrationValidatorImpl;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test class for DCR functionalities.
 */
@PowerMockIgnore({"javax.net.ssl.*"})
@PrepareForTest({JWTUtils.class, OpenBankingCDSConfigParser.class})
public class DCRUtilTest {

    private static final Log log = LogFactory.getLog(DCRUtilTest.class);

    private RegistrationValidator registrationValidator;
    private CDSRegistrationValidatorImpl extendedValidator = new CDSRegistrationValidatorImpl();
    private RegistrationRequest registrationRequest;
    private OpenBankingCDSConfigParser openBankingCDSConfigParser;
    private Map<String, Object> cdsConfigMap = new HashMap<>();
    private static final String NULL = "null";

    @BeforeClass
    public void beforeClass() {

        Map<String, Object> confMap = new HashMap<>();
        Map<String, Map<String, Object>> dcrRegistrationConfMap = new HashMap<>();
        List<String> registrationParams = Arrays.asList("Issuer:true:null",
                "TokenEndPointAuthentication:true:private_key_jwt", "ResponseTypes:true:code id_token",
                "GrantTypes:true:authorization_code,refresh_token", "ApplicationType:false:web",
                "IdTokenSignedResponseAlg:true:null", "SoftwareStatement:true:null", "Scope:false:accounts,payments");
        confMap.put(DCRCommonConstants.DCR_VALIDATOR, "com.wso2.openbanking.cds.identity.dcr.validation" +
                ".CDSRegistrationValidatorImpl");
        confMap.put("DCR.JwksUrlProduction",
                "https://keystore.openbankingtest.org.uk/0015800001HQQrZAAX/9b5usDpbNtmxDcTzs7GzKp.jwks");
        confMap.put("DCR.JwksUrlSandbox",
                "https://keystore.openbankingtest.org.uk/0015800001HQQrZAAX/9b5usDpbNtmxDcTzs7GzKp.jwks");
        cdsConfigMap.put("DCR.EnableHostNameValidation", "false");
        cdsConfigMap.put("DCR.EnableURIValidation", "false");
        List<String> validAlgorithms = new ArrayList<>();
        validAlgorithms.add("PS256");
        validAlgorithms.add("ES256");
        confMap.put(OpenBankingConstants.SIGNATURE_ALGORITHMS, validAlgorithms);
        IdentityExtensionsDataHolder.getInstance().setConfigurationMap(confMap);

        String dcrValidator = confMap.get(DCRCommonConstants.DCR_VALIDATOR).toString();
        registrationValidator = getDCRValidator(dcrValidator);
        registrationRequest = getRegistrationRequestObject(RegistrationTestConstants.registrationRequestJson);
        for (String param : registrationParams) {
            setParamConfig(param, dcrRegistrationConfMap);
        }
        IdentityExtensionsDataHolder.getInstance().setDcrRegistrationConfigMap(dcrRegistrationConfMap);
    }

    @Test
    public void testInvalidCallbackUris() throws Exception {

        registrationRequest.setCallbackUris(Arrays.asList("https://www.google.com"));

        initiateData();
        CDSRegistrationRequest cdsRegistrationRequest = new CDSRegistrationRequest(registrationRequest);
        try {
            ValidationUtils.validateRequest(cdsRegistrationRequest);
        } catch (DCRValidationException e) {
            Assert.assertTrue(e.getErrorDescription().contains("Invalid callback uris"));
        }
    }

    @Test(dependsOnMethods = "testInvalidCallbackUris")
    public void testInvalidUriHostnames() throws Exception {

        registrationRequest.setCallbackUris(Arrays.asList("https://www.google.com/redirects/redirect1",
                "https://www.google.com/redirects/redirect2"));
        cdsConfigMap.put("DCR.EnableHostNameValidation", "true");

        initiateData();
        CDSRegistrationRequest cdsRegistrationRequest = new CDSRegistrationRequest(registrationRequest);
        try {
            ValidationUtils.validateRequest(cdsRegistrationRequest);
        } catch (DCRValidationException e) {
            Assert.assertTrue(e.getErrorDescription().contains("Host names of logo_uri/tos_uri/policy_uri/client_uri " +
                    "does not match with the redirect_uris"));
        }
    }

    @Test(dependsOnMethods = "testInvalidUriHostnames")
    public void testInvalidUriConnection() throws Exception {

        cdsConfigMap.put("DCR.EnableHostNameValidation", "false");
        cdsConfigMap.put("DCR.EnableURIValidation", "true");

        initiateData();
        CDSRegistrationRequest cdsRegistrationRequest = new CDSRegistrationRequest(registrationRequest);
        try {
            ValidationUtils.validateRequest(cdsRegistrationRequest);
        } catch (DCRValidationException e) {
            Assert.assertTrue(e.getErrorDescription().contains("Provided logo_uri/client_uri/policy_uri/tos_uri " +
                    "in the request does not resolve to a valid web page"));
        }
    }

    @Test(dependsOnMethods = "testInvalidUriConnection")
    public void testValidUriConnection() throws Exception {

        mockStatic(JWTUtils.class);
        when(JWTUtils.validateJWTSignature(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(true);
        mockStatic(OpenBankingCDSConfigParser.class);
        openBankingCDSConfigParser = mock(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParser);
        when(openBankingCDSConfigParser.getConfiguration()).thenReturn(cdsConfigMap);

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(RegistrationTestConstants.ssaBodyJsonWithDummyWorkingURLs);

        when(JWTUtils.decodeRequestJWT(Mockito.anyString(), Mockito.anyString())).thenReturn(json);

        String decodedSSA = null;
        try {
            decodedSSA = JWTUtils
                    .decodeRequestJWT(registrationRequest.getSoftwareStatement(), "body").toJSONString();
        } catch (ParseException e) {
            log.error("Error while parsing the SSA", e);
        }
        registrationValidator.setSoftwareStatementPayload(registrationRequest, decodedSSA);
        CDSRegistrationRequest cdsRegistrationRequest = new CDSRegistrationRequest(registrationRequest);
        try {
            ValidationUtils.validateRequest(cdsRegistrationRequest);
        } catch (DCRValidationException e) {
            Assert.fail("should not throw exception");
        }
    }

    @Test
    public void testInvalidSSACallBackUrisSet() throws Exception {

        cdsConfigMap.put("DCR.EnableHostNameValidation", "false");
        cdsConfigMap.put("DCR.EnableURIValidation", "false");

        mockStatic(JWTUtils.class);
        when(JWTUtils.validateJWTSignature(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(true);
        mockStatic(OpenBankingCDSConfigParser.class);
        openBankingCDSConfigParser = mock(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParser);
        when(openBankingCDSConfigParser.getConfiguration()).thenReturn(cdsConfigMap);

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser
                .parse(RegistrationTestConstants.ssaBodyJsonWithDifferentRedirectUriHostnames);

        when(JWTUtils.decodeRequestJWT(Mockito.anyString(), Mockito.anyString())).thenReturn(json);

        String decodedSSA = null;
        try {
            decodedSSA = JWTUtils
                    .decodeRequestJWT(registrationRequest.getSoftwareStatement(), "body").toJSONString();
        } catch (ParseException e) {
            log.error("Error while parsing the SSA", e);
        }
        registrationValidator.setSoftwareStatementPayload(registrationRequest, decodedSSA);
        CDSRegistrationRequest cdsRegistrationRequest = new CDSRegistrationRequest(registrationRequest);
        try {
            ValidationUtils.validateRequest(cdsRegistrationRequest);
        } catch (DCRValidationException e) {
            Assert.assertTrue(e.getErrorDescription().contains("Redirect URIs do not contain the same hostname"));
        }
    }

    @Test(priority = 1)
    public void testExtendedValidatePostFailure() throws Exception {

        cdsConfigMap.put("DCR.EnableHostNameValidation", "false");
        cdsConfigMap.put("DCR.EnableURIValidation", "false");
        registrationRequest = getRegistrationRequestObject(RegistrationTestConstants.extendedRegistrationRequestJson);
        initiateData();
        try {
            extendedValidator.validatePost(registrationRequest);
        } catch (DCRValidationException e) {
            Assert.assertTrue(e.getErrorDescription().contains("Invalid issuer"));
        }
    }

    @Test(priority = 1)
    public void testExtendedValidateUpdateFailure() throws Exception {

        cdsConfigMap.put("DCR.EnableHostNameValidation", "false");
        cdsConfigMap.put("DCR.EnableURIValidation", "false");
        registrationRequest = getRegistrationRequestObject(RegistrationTestConstants.extendedRegistrationRequestJson);
        initiateData();
        try {
            extendedValidator.validateUpdate(registrationRequest);
        } catch (DCRValidationException e) {
            Assert.assertTrue(e.getErrorDescription().contains("Invalid issuer"));
        }
    }

    private void initiateData() throws Exception {

        mockStatic(JWTUtils.class);
        when(JWTUtils.validateJWTSignature(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(true);
        mockStatic(OpenBankingCDSConfigParser.class);
        openBankingCDSConfigParser = mock(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParser);
        when(openBankingCDSConfigParser.getConfiguration()).thenReturn(cdsConfigMap);

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(RegistrationTestConstants.ssaBodyJson);

        when(JWTUtils.decodeRequestJWT(Mockito.anyString(), Mockito.anyString())).thenReturn(json);

        String decodedSSA = null;
        try {
            decodedSSA = JWTUtils
                    .decodeRequestJWT(registrationRequest.getSoftwareStatement(), "body").toJSONString();
        } catch (ParseException e) {
            log.error("Error while parsing the SSA", e);
        }
        registrationValidator.setSoftwareStatementPayload(registrationRequest, decodedSSA);
    }

    private static RegistrationRequest getRegistrationRequestObject(String request) {

        Gson gson = new Gson();
        return gson.fromJson(request, RegistrationRequest.class);
    }

    public static RegistrationValidator getDCRValidator(String dcrValidator)  {

        if (StringUtils.isEmpty(dcrValidator)) {
            return new CDSRegistrationValidatorImpl();
        }
        try {
            return (RegistrationValidator) Class.forName(dcrValidator).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("Error instantiating " + dcrValidator, e);
            return new CDSRegistrationValidatorImpl();
        } catch (ClassNotFoundException e) {
            log.error("Cannot find class: " + dcrValidator, e);
            return new CDSRegistrationValidatorImpl();
        }
    }

    private void setParamConfig(String configParam, Map<String, Map<String, Object>> dcrRegistrationConfMap) {
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put(DCRCommonConstants.DCR_REGISTRATION_PARAM_REQUIRED, configParam.split(":")[1]);
        if (!NULL.equalsIgnoreCase(configParam.split(":")[2])) {
            List<String> allowedValues = new ArrayList<>();
            allowedValues.addAll(Arrays.asList(configParam.split(":")[2].split(",")));
            parameterValues.put(DCRCommonConstants.DCR_REGISTRATION_PARAM_ALLOWED_VALUES, allowedValues);
        }
        dcrRegistrationConfMap.put(configParam.split(":")[0], parameterValues);
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {

        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }
}
