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
package com.wso2.openbanking.cds.identity.filter;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.identity.internal.IdentityExtensionsDataHolder;
import com.wso2.openbanking.accelerator.identity.token.validators.MTLSEnforcementValidator;
import com.wso2.openbanking.accelerator.identity.token.validators.OBIdentityFilterValidator;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonConstants;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonUtil;
import com.wso2.openbanking.cds.identity.filter.util.TestConstants;
import com.wso2.openbanking.cds.identity.filter.util.TestUtil;
import com.wso2.openbanking.cds.identity.filter.validator.CDSIntrospectionPrivateKeyJWTFilterValidator;
import org.apache.http.HttpStatus;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import static org.testng.Assert.assertEquals;

@PrepareForTest({IdentityCommonUtil.class})
public class CDSIntrospectionFilterTest extends PowerMockTestCase {

    MockHttpServletRequest request;
    MockHttpServletResponse response;
    FilterChain filterChain;

    @BeforeMethod
    public void beforeMethod() {

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = Mockito.spy(FilterChain.class);
    }

    @Test(description = "Test the validators are omitted in non regulatory scenario")
    public void nonRegulatoryTest() throws Exception {

        Map<String, Object> configMap = new HashMap<>();
        configMap.put(IdentityCommonConstants.CLIENT_CERTIFICATE_ENCODE, false);
        IdentityExtensionsDataHolder.getInstance().setConfigurationMap(configMap);

        CDSIntrospectionFilter filter = Mockito.spy(CDSIntrospectionFilter.class);
        PowerMockito.mockStatic(IdentityCommonUtil.class);
        request.setParameter(IdentityCommonConstants.CLIENT_ID, "test");
        PowerMockito.when(IdentityCommonUtil.getRegulatoryFromSPMetaData("test")).thenReturn(false);
        filter.doFilter(request, response, filterChain);
        assertEquals(response.getStatus(), HttpStatus.SC_OK);
    }

    @Test(description = "Test the certificate in context/header is mandated")
    public void noCertificateTest() throws IOException, OpenBankingException, ServletException {

        Map<String, Object> configMap = new HashMap<>();
        PowerMockito.mockStatic(IdentityCommonUtil.class);
        configMap.put(IdentityCommonConstants.ENABLE_TRANSPORT_CERT_AS_HEADER, true);
        IdentityExtensionsDataHolder.getInstance().setConfigurationMap(configMap);

        CDSIntrospectionFilter filter = Mockito.spy(CDSIntrospectionFilter.class);
        request.setParameter(IdentityCommonConstants.CLIENT_ID, "test");

        PowerMockito.when(IdentityCommonUtil.getRegulatoryFromSPMetaData("test")).thenReturn(true);
        PowerMockito.when(IdentityCommonUtil.getMTLSAuthHeader())
                .thenReturn(IdentityCommonConstants.CERTIFICATE_HEADER);
        filter.doFilter(request, response, filterChain);
        Map<String, String> responseMap = TestUtil.getResponse(response.getOutputStream());
        assertEquals(response.getStatus(), HttpStatus.SC_BAD_REQUEST);
        assertEquals(responseMap.get(IdentityCommonConstants.OAUTH_ERROR), "Transport certificate not found");
        assertEquals(responseMap.get(IdentityCommonConstants.OAUTH_ERROR_DESCRIPTION),
                "Transport certificate not found in the request");
    }

    @Test(description = "Test the certificate in attribute is present if config is disabled")
    public void certificateIsNotPresentInAttributeTest() throws IOException, OpenBankingException, ServletException {

        Map<String, Object> configMap = new HashMap<>();
        PowerMockito.mockStatic(IdentityCommonUtil.class);
        configMap.put(IdentityCommonConstants.ENABLE_TRANSPORT_CERT_AS_HEADER, false);
        configMap.put(IdentityCommonConstants.CLIENT_CERTIFICATE_ENCODE, false);
        IdentityExtensionsDataHolder.getInstance().setConfigurationMap(configMap);

        CDSIntrospectionFilter filter = Mockito.spy(CDSIntrospectionFilter.class);
        request.setParameter(IdentityCommonConstants.CLIENT_ID, "test");
        request.addHeader(TestConstants.CERTIFICATE_HEADER, TestConstants.CERTIFICATE_CONTENT);

        PowerMockito.when(IdentityCommonUtil.getRegulatoryFromSPMetaData("test")).thenReturn(true);
        PowerMockito.when(IdentityCommonUtil.getMTLSAuthHeader()).thenReturn(TestConstants.CERTIFICATE_HEADER);
        filter.doFilter(request, response, filterChain);
        Map<String, String> responseMap = TestUtil.getResponse(response.getOutputStream());
        assertEquals(response.getStatus(), HttpStatus.SC_BAD_REQUEST);
        assertEquals(responseMap.get(IdentityCommonConstants.OAUTH_ERROR), "Transport certificate not found");
        assertEquals(responseMap.get(IdentityCommonConstants.OAUTH_ERROR_DESCRIPTION),
                "Transport certificate not found in the request");

    }

    @Test(description = "Test the certificate in the attribute is overridden in header")
    public void certificateInAttributeOverriddenTest() throws IOException, OpenBankingException, ServletException {

        CDSIntrospectionFilter filter = Mockito.spy(CDSIntrospectionFilter.class);
        PowerMockito.mockStatic(IdentityCommonUtil.class);
        request.setParameter(IdentityCommonConstants.CLIENT_ID, "test");
        request.addHeader(TestConstants.CERTIFICATE_HEADER, "invalid");
        request.setAttribute(IdentityCommonConstants.JAVAX_SERVLET_REQUEST_CERTIFICATE,
                TestUtil.getCertificate(TestConstants.CERTIFICATE_CONTENT));

        Map<String, Object> configMap = new HashMap<>();
        configMap.put(IdentityCommonConstants.CLIENT_CERTIFICATE_ENCODE, false);
        IdentityExtensionsDataHolder.getInstance().setConfigurationMap(configMap);

        PowerMockito.when(IdentityCommonUtil.getMTLSAuthHeader()).thenReturn(TestConstants.CERTIFICATE_HEADER);
        PowerMockito.when(IdentityCommonUtil.getRegulatoryFromSPMetaData("test")).thenReturn(true);
        filter.doFilter(request, response, filterChain);
        assertEquals(response.getStatus(), HttpStatus.SC_OK);
    }

    @Test(description = "Test the validators are omitted if nothing is configured")
    public void noValidatorsConfiguredTest() throws Exception {

        Map<String, Object> configMap = new HashMap<>();
        configMap.put(IdentityCommonConstants.ENABLE_TRANSPORT_CERT_AS_HEADER, true);
        configMap.put(IdentityCommonConstants.CLIENT_CERTIFICATE_ENCODE, false);
        IdentityExtensionsDataHolder.getInstance().setConfigurationMap(configMap);

        CDSIntrospectionFilter filter = Mockito.spy(CDSIntrospectionFilter.class);
        PowerMockito.mockStatic(IdentityCommonUtil.class);
        request.setParameter(IdentityCommonConstants.CLIENT_ID, "test");
        request.addHeader(TestConstants.CERTIFICATE_HEADER, TestConstants.CERTIFICATE_CONTENT);
        PowerMockito.when(IdentityCommonUtil.getRegulatoryFromSPMetaData("test")).thenReturn(true);
        PowerMockito.when(IdentityCommonUtil.getMTLSAuthHeader()).thenReturn(TestConstants.CERTIFICATE_HEADER);
        filter.doFilter(request, response, filterChain);
        assertEquals(response.getStatus(), HttpStatus.SC_OK);
    }

    @Test(description = "Test the client ID is enforced")
    public void clientIdEnforcementTest() throws Exception {

        CDSIntrospectionFilter filter = Mockito.spy(CDSIntrospectionFilter.class);
        PowerMockito.mockStatic(IdentityCommonUtil.class);
        filter.doFilter(request, response, filterChain);

        PowerMockito.when(IdentityCommonUtil.getRegulatoryFromSPMetaData("test")).thenReturn(true);
        Map<String, String> responseMap = TestUtil.getResponse(response.getOutputStream());
        assertEquals(response.getStatus(), HttpStatus.SC_BAD_REQUEST);
        assertEquals(responseMap.get(IdentityCommonConstants.OAUTH_ERROR), "Client ID not retrieved");
        assertEquals(responseMap.get(IdentityCommonConstants.OAUTH_ERROR_DESCRIPTION),
                "Unable to find client id in the request");
    }

    @Test(description = "Test mtls enforcement validator engaged")
    public void mTLSEnforcementValidatorTest() throws Exception {

        CDSIntrospectionFilter filter = Mockito.spy(CDSIntrospectionFilter.class);
        PowerMockito.mockStatic(IdentityCommonUtil.class);
        MTLSEnforcementValidator mtlsEnforcementValidator =
                Mockito.spy(MTLSEnforcementValidator.class);

        request.setParameter(IdentityCommonConstants.OAUTH_JWT_ASSERTION_TYPE,
                IdentityCommonConstants.OAUTH_JWT_BEARER_GRANT_TYPE);
        request.setParameter(IdentityCommonConstants.OAUTH_JWT_ASSERTION, TestConstants.CLIENT_ASSERTION);
        request.addHeader(TestConstants.CERTIFICATE_HEADER, TestConstants.CERTIFICATE_CONTENT);
        PowerMockito.when(IdentityCommonUtil.getMTLSAuthHeader()).thenReturn(TestConstants.CERTIFICATE_HEADER);

        List<OBIdentityFilterValidator> validators = new ArrayList<>();
        validators.add(mtlsEnforcementValidator);

        filter.doFilter(request, response, filterChain);
        assertEquals(response.getStatus(), HttpStatus.SC_OK);
    }

    @Test(description = "Test pvt key jwt validator engaged")
    public void pvtKeyJWTValidatorTest() throws Exception {

        CDSIntrospectionFilter filter = Mockito.spy(CDSIntrospectionFilter.class);
        PowerMockito.mockStatic(IdentityCommonUtil.class);
        CDSIntrospectionPrivateKeyJWTFilterValidator pvtKeyJWTValidator =
                Mockito.spy(CDSIntrospectionPrivateKeyJWTFilterValidator.class);

        request.setParameter(IdentityCommonConstants.OAUTH_JWT_ASSERTION_TYPE,
                IdentityCommonConstants.OAUTH_JWT_BEARER_GRANT_TYPE);
        request.setParameter(IdentityCommonConstants.OAUTH_JWT_ASSERTION, TestConstants.CLIENT_ASSERTION);
        request.addHeader(TestConstants.CERTIFICATE_HEADER, TestConstants.CERTIFICATE_CONTENT);
        PowerMockito.when(IdentityCommonUtil.getMTLSAuthHeader()).thenReturn(TestConstants.CERTIFICATE_HEADER);

        List<OBIdentityFilterValidator> validators = new ArrayList<>();
        validators.add(pvtKeyJWTValidator);

        filter.doFilter(request, response, filterChain);
        assertEquals(response.getStatus(), HttpStatus.SC_OK);
    }
}