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

import org.mockito.Mockito;
import org.powermock.modules.testng.PowerMockTestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.UUID;
import javax.servlet.FilterChain;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Test class for CDS Infosec Data Publishing Filter.
 */
public class InfoSecDataPublishingFilterTests extends PowerMockTestCase {

    MockHttpServletRequest request;
    MockHttpServletResponse response;
    FilterChain filterChain;

    @BeforeMethod
    public void beforeMethod() {

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = Mockito.spy(FilterChain.class);
    }

    @Test(description = "Test the attributes in the latency data map")
    public void latencyDataMapAttributesTest() {

        InfoSecDataPublishingFilter filter = Mockito.spy(InfoSecDataPublishingFilter.class);
        String messageId = UUID.randomUUID().toString();
        request.setAttribute("REQUEST_IN_TIME", System.currentTimeMillis());
        Map<String, Object> latencyData = filter.generateLatencyDataMap(request, messageId);
        assertEquals(latencyData.get("correlationId"), messageId);
        assertNotNull(latencyData.get("requestTimestamp"));
        assertNotNull(latencyData.get("backendLatency"));
        assertNotNull(latencyData.get("requestMediationLatency"));
        assertNotNull(latencyData.get("responseLatency"));
        assertNotNull(latencyData.get("responseMediationLatency"));
    }

    @Test(description = "Test the ResponseLatency attribute in the latency data map")
    public void latencyDataMapNegativeResponseLatencyTest() {

        InfoSecDataPublishingFilter filter = Mockito.spy(InfoSecDataPublishingFilter.class);
        String messageId = UUID.randomUUID().toString();
        request.setAttribute("REQUEST_IN_TIME", System.currentTimeMillis() + (60 * 1000));
        Map<String, Object> latencyData = filter.generateLatencyDataMap(request, messageId);
        assertEquals(latencyData.get("responseLatency"), 0L);
    }
}
