/*
 * Copyright (c) 2021-2022, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.metrics.util;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({SPQueryExecutorUtil.class, FrameworkUtil.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class MetricsProcessorUtilTest extends PowerMockTestCase {

    JSONObject jsonObject = new JSONObject();

    @BeforeMethod
    public void beforeMethod() throws OpenBankingException, IOException, ParseException {

        mockStatic(FrameworkUtil.class);
        Bundle bundleMock = mock(Bundle.class);
        BundleContext bundleContextMock = mock(BundleContext.class);
        ServiceReference<APIManagerConfigurationService> serviceReferenceMock = mock(ServiceReference.class);
        APIManagerConfigurationService apiManagerConfigurationServiceMock = mock(APIManagerConfigurationService.class);
        when(FrameworkUtil.getBundle(Mockito.any())).thenReturn(bundleMock);
        when(bundleMock.getBundleContext()).thenReturn(bundleContextMock);
        when(bundleContextMock.getServiceReference(APIManagerConfigurationService.class))
                .thenReturn(serviceReferenceMock);
        when(bundleContextMock.getService(Mockito.any())).thenReturn(apiManagerConfigurationServiceMock);

        mockStatic(SPQueryExecutorUtil.class);
        JSONArray jsonArray = new JSONArray();
        jsonObject.put("records", jsonArray);
        when(SPQueryExecutorUtil.executeQueryOnStreamProcessor(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(jsonObject);
    }

    @Test
    public void testGetInvocationMetrics() throws Exception {

        Assert.assertFalse(MetricsProcessorUtil
                .getInvocationMetrics(PeriodEnum.ALL).isEmpty());
    }

    @Test
    public void testGetAverageResponseMetrics() throws Exception {

        Map<PriorityEnum, List<BigDecimal>> invocationMetricsMap = new HashMap<>();
        List<BigDecimal> list = new ArrayList<>(Arrays.asList(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE));
        invocationMetricsMap.put(PriorityEnum.UNAUTHENTICATED, list);
        invocationMetricsMap.put(PriorityEnum.HIGH_PRIORITY, list);
        invocationMetricsMap.put(PriorityEnum.LOW_PRIORITY, list);
        invocationMetricsMap.put(PriorityEnum.LARGE_PAYLOAD, list);
        invocationMetricsMap.put(PriorityEnum.UNATTENDED, list);

        Assert.assertFalse(MetricsProcessorUtil
                .getAverageResponseMetrics(invocationMetricsMap, PeriodEnum.ALL).isEmpty());
    }

    @Test
    public void testGetPerformanceMetrics() throws OpenBankingException {

        List<BigDecimal> totalInvocationList = new ArrayList<>(Arrays.asList(BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE));
        Assert.assertNotNull(MetricsProcessorUtil.getPerformanceMetrics(PeriodEnum.ALL, totalInvocationList));
    }

    @Test
    public void testGetErrorInvocationMetrics() throws OpenBankingException {

        List<BigDecimal> result = MetricsProcessorUtil.getErrorInvocationMetrics(PeriodEnum.ALL);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetAllRejectedInvocationMetrics() throws OpenBankingException {

        Assert.assertNotNull(MetricsProcessorUtil.getRejectedInvocationMetrics(PeriodEnum.ALL,
                "authenticated"));
    }

    @Test
    public void testGetHistoricRejectedInvocationMetrics() throws OpenBankingException {

        Assert.assertNotNull(MetricsProcessorUtil.getRejectedInvocationMetrics(PeriodEnum.HISTORIC,
                "authenticated"));
    }

    @Test
    public void testGetAverageTPSMetrics() {

        List<BigDecimal> totalInvocationList = new ArrayList<>();
        totalInvocationList.add(BigDecimal.ONE);
        Assert.assertNotNull(MetricsProcessorUtil.getAverageTPSMetrics(totalInvocationList));
    }

    @Test
    public void testGetSessionCountMetrics() throws OpenBankingException {

        Assert.assertNotNull(MetricsProcessorUtil.getSessionCountMetrics(PeriodEnum.ALL));
    }

    @Test
    public void testGetPeakTPSMetrics() throws OpenBankingException {

        Assert.assertNotNull(MetricsProcessorUtil.getPeakTPSMetrics(PeriodEnum.ALL));
    }


    @Test
    public void testGetRecipientCountMetrics() throws OpenBankingException {

        Assert.assertEquals(0, MetricsProcessorUtil.getRecipientCountMetrics());
    }

    @Test
    public void testGetCustomerCountMetrics() throws OpenBankingException {

        Assert.assertEquals(0, MetricsProcessorUtil.getCustomerCountMetrics());
    }

    @Test
    public void testGetTotalInvocationsForEachDay() {

        Map<PriorityEnum, List<BigDecimal>> invocationMetricsMap = new HashMap<>();
        List<BigDecimal> list = new ArrayList<>(Collections.singletonList(BigDecimal.ONE));
        invocationMetricsMap.put(PriorityEnum.UNAUTHENTICATED, list);
        invocationMetricsMap.put(PriorityEnum.HIGH_PRIORITY, list);
        invocationMetricsMap.put(PriorityEnum.LOW_PRIORITY, list);
        invocationMetricsMap.put(PriorityEnum.LARGE_PAYLOAD, list);
        invocationMetricsMap.put(PriorityEnum.UNATTENDED, list);
        Assert.assertNotNull(MetricsProcessorUtil.getTotalInvocationsForEachDay(invocationMetricsMap));
    }

    @Test(priority = 1)
    public void testGetAvailabilityMetrics() throws OpenBankingException {

        JSONArray jsonArray = new JSONArray();
        JSONArray jsonElement = new JSONArray();
        jsonElement.add("anonymous");
        jsonElement.add(Long.parseLong("1"));
        jsonElement.add("anonymous");
        jsonElement.add(Long.parseLong("2"));
        jsonElement.add(Long.parseLong("3"));
        jsonArray.add(jsonElement);

        jsonObject.put("records", jsonArray);
        Assert.assertNotNull(MetricsProcessorUtil.getAvailabilityMetrics(PeriodEnum.ALL));
    }

}
