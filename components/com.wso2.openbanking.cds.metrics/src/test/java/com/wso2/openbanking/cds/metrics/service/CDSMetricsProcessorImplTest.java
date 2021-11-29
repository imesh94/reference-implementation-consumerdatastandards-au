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

package com.wso2.openbanking.cds.metrics.service;

import com.wso2.openbanking.cds.metrics.model.ResponseMetricsListModel;
import com.wso2.openbanking.cds.metrics.util.MetricsProcessorUtil;
import com.wso2.openbanking.cds.metrics.util.SPQueryExecutorUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({MetricsProcessorUtil.class, SPQueryExecutorUtil.class, FrameworkUtil.class})
public class CDSMetricsProcessorImplTest extends PowerMockTestCase {

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