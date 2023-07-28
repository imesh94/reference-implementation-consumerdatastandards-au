/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 *
 */
 
package com.wso2.openbanking.cds.metrics.util;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.common.util.HTTPClientUtils;
import net.minidev.json.parser.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.apimgt.impl.APIManagerAnalyticsConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test class for SPQueryExecutorUtil.
 */
@PrepareForTest({FrameworkUtil.class, HTTPClientUtils.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class SPQueryExecutorUtilTest extends PowerMockTestCase {

    @Test
    public void testGetAnalyticsConfiguration() {

        mockStatic(FrameworkUtil.class);
        Bundle bundleMock = mock(Bundle.class);
        BundleContext bundleContextMock = mock(BundleContext.class);
        ServiceReference<APIManagerConfigurationService> serviceReferenceMock = mock(ServiceReference.class);
        APIManagerConfigurationService apiManagerConfigurationServiceMock = mock(APIManagerConfigurationService.class);
        when(FrameworkUtil.getBundle(Mockito.any())).thenReturn(bundleMock);
        when(bundleMock.getBundleContext()).thenReturn(bundleContextMock);
        when(bundleContextMock.getServiceReference(APIManagerConfigurationService.class))
                .thenReturn(serviceReferenceMock);

        APIManagerAnalyticsConfiguration apiManagerAnalyticsConfigurationMock =
                mock(APIManagerAnalyticsConfiguration.class);
        when(bundleContextMock.getService(Mockito.any())).thenReturn(apiManagerConfigurationServiceMock);
        when(apiManagerConfigurationServiceMock.getAPIAnalyticsConfiguration())
                .thenReturn(apiManagerAnalyticsConfigurationMock);

        Assert.assertNotNull(SPQueryExecutorUtil.getAnalyticsConfiguration());
    }

    @Test
    public void testExecuteQueryOnStreamProcessor() throws OpenBankingException, IOException, ParseException {

        mockStatic(FrameworkUtil.class);
        Bundle bundleMock = mock(Bundle.class);
        BundleContext bundleContextMock = mock(BundleContext.class);
        ServiceReference<APIManagerConfigurationService> serviceReferenceMock = mock(ServiceReference.class);
        APIManagerConfigurationService apiManagerConfigurationServiceMock = mock(APIManagerConfigurationService.class);
        when(FrameworkUtil.getBundle(Mockito.any())).thenReturn(bundleMock);
        when(bundleMock.getBundleContext()).thenReturn(bundleContextMock);
        when(bundleContextMock.getServiceReference(APIManagerConfigurationService.class))
                .thenReturn(serviceReferenceMock);

        APIManagerAnalyticsConfiguration apiManagerAnalyticsConfigurationMock =
                mock(APIManagerAnalyticsConfiguration.class);
        when(bundleContextMock.getService(Mockito.any())).thenReturn(apiManagerConfigurationServiceMock);
        when(apiManagerConfigurationServiceMock.getAPIAnalyticsConfiguration())
                .thenReturn(apiManagerAnalyticsConfigurationMock);

        StatusLine statusLineMock = Mockito.mock(StatusLine.class);
        Mockito.doReturn(HttpStatus.SC_OK).when(statusLineMock).getStatusCode();

        File file = new File("src/test/resources/test-query-data.json");
        byte[] crlBytes = FileUtils.readFileToString(file, String.valueOf(StandardCharsets.UTF_8))
                .getBytes(StandardCharsets.UTF_8);
        InputStream inStream = new ByteArrayInputStream(crlBytes);

        HttpEntity httpEntityMock = Mockito.mock(HttpEntity.class);
        Mockito.doReturn(inStream).when(httpEntityMock).getContent();

        CloseableHttpResponse httpResponseMock = Mockito.mock(CloseableHttpResponse.class);
        Mockito.doReturn(statusLineMock).when(httpResponseMock).getStatusLine();
        Mockito.doReturn(httpEntityMock).when(httpResponseMock).getEntity();

        CloseableHttpClient closeableHttpClientMock = Mockito.mock(CloseableHttpClient.class);
        Mockito.doReturn(httpResponseMock).when(closeableHttpClientMock).execute(Mockito.any(HttpGet.class));

        PowerMockito.mockStatic(HTTPClientUtils.class);
        Mockito.when(HTTPClientUtils.getHttpsClient()).thenReturn(closeableHttpClientMock);

        Assert.assertNotNull(SPQueryExecutorUtil.executeQueryOnStreamProcessor("dummyAppName", "dummyQuery"));
    }
}
