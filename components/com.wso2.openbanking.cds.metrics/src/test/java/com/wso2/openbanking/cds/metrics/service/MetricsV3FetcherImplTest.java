/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 *
 */

package com.wso2.openbanking.cds.metrics.service;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.metrics.data.MockMetricsDataProvider;
import com.wso2.openbanking.cds.metrics.model.MetricsResponseModel;
import com.wso2.openbanking.cds.metrics.util.PeriodEnum;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.ZoneId;

import static org.mockito.Mockito.doReturn;

@PrepareForTest({OpenBankingCDSConfigParser.class})
@PowerMockIgnore({"javax.crypto.*", "jdk.internal.reflect.*"})
public class MetricsV3FetcherImplTest extends PowerMockTestCase {

    private MetricsV3FetcherImpl metricsFetcher;
    private MetricsProcessor metricsProcessor;
    private OpenBankingCDSConfigParser openBankingCDSConfigParserMock;

    @BeforeClass
    public void init() throws OpenBankingException {

        metricsProcessor = new MetricsV3ProcessorImpl(PeriodEnum.ALL, new MockMetricsDataProvider(),
                ZoneId.of("GMT"));
        metricsFetcher = new MetricsV3FetcherImpl(metricsProcessor);

    }

    @BeforeMethod
    public void setup() throws OpenBankingException {

        openBankingCDSConfigParserMock = PowerMockito.mock(OpenBankingCDSConfigParser.class);
        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        PowerMockito.when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        doReturn("GMT").when(openBankingCDSConfigParserMock).getMetricsTimeZone();
    }

    @Test
    public void testGetResponseMetricsListModel() throws Exception {

        MetricsResponseModel metricsResponseModel = metricsFetcher.getResponseMetricsListModel(
                "2024-05-30T01:56:28+05:30");
        Assert.assertNotNull(metricsResponseModel, "Metrics response model should not be null");
    }
}
