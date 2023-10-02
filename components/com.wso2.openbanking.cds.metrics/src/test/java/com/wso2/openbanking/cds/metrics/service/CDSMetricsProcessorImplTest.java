/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 *
 */
 
package com.wso2.openbanking.cds.metrics.service;

import com.wso2.openbanking.cds.metrics.cache.MetricsCache;
import com.wso2.openbanking.cds.metrics.model.ResponseMetricsListModel;
import com.wso2.openbanking.cds.metrics.util.MetricsProcessorUtil;
import com.wso2.openbanking.cds.metrics.util.SPQueryExecutorUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.apimgt.impl.APIManagerAnalyticsConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;

import java.util.HashMap;
import java.util.Map;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test class for CDSMetricsProcessorImpl.
 */
@PrepareForTest({MetricsProcessorUtil.class, SPQueryExecutorUtil.class, FrameworkUtil.class, MetricsCache.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class CDSMetricsProcessorImplTest extends PowerMockTestCase {

    @Mock
    MetricsCache metricsCacheMock;

    @Test
    public void testGetResponseMetricsModel() throws Exception {

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
        APIManagerAnalyticsConfiguration apiManagerAnalyticsConfigurationMock =
                mock(APIManagerAnalyticsConfiguration.class);
        when(apiManagerConfigurationServiceMock.getAPIAnalyticsConfiguration())
                .thenReturn(apiManagerAnalyticsConfigurationMock);
        Map<String, String> reporterProperties = new HashMap<>();
        reporterProperties.put("stream.processor.rest.api.url", "https://localhost:7444");
        when(apiManagerAnalyticsConfigurationMock.getReporterProperties()).thenReturn(reporterProperties);
        mockStatic(MetricsCache.class);
        metricsCacheMock = mock(MetricsCache.class);
        when(MetricsCache.getInstance()).thenReturn(metricsCacheMock);

        mockStatic(SPQueryExecutorUtil.class);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("records", jsonArray);
        when(SPQueryExecutorUtil.executeQueryOnStreamProcessor(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(jsonObject);

        MetricsProcessorImpl metricsProcessor = new MetricsProcessorImpl();

        ResponseMetricsListModel responseMetricsListModel = metricsProcessor
                .getResponseMetricsListModel(Mockito.anyString(), Mockito.anyString());

        Assert.assertNotNull(responseMetricsListModel);

    }
}
