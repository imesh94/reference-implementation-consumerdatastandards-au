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
import com.wso2.openbanking.cds.metrics.util.AspectEnum;
import com.wso2.openbanking.cds.metrics.util.PeriodEnum;
import com.wso2.openbanking.cds.metrics.util.PriorityEnum;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.doReturn;

@PrepareForTest({OpenBankingCDSConfigParser.class})
@PowerMockIgnore({"javax.crypto.*", "jdk.internal.reflect.*"})
public class MetricsV5ProcessorImplTest extends PowerMockTestCase {

    private MetricsV5ProcessorImpl metricsV5Processor;
    private OpenBankingCDSConfigParser openBankingCDSConfigParserMock;
    private static final int TOTAL_DAYS = 8;
    private static final int TOTAL_MONTHS = 13;
    private static final int PRIORITY_TIERS = 5;

    @BeforeClass
    public void init() throws OpenBankingException {

        metricsV5Processor = new MetricsV5ProcessorImpl(PeriodEnum.ALL, new MockMetricsDataProvider(),
                ZoneId.of("GMT"));
    }

    @BeforeMethod
    public void setup() throws OpenBankingException {

        openBankingCDSConfigParserMock = PowerMockito.mock(OpenBankingCDSConfigParser.class);
        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        PowerMockito.when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        doReturn("GMT").when(openBankingCDSConfigParserMock).getMetricsTimeZone();
    }

    @Test
    public void testGetAvailabilityMetrics() throws Exception {

        Map<AspectEnum, List<BigDecimal>> availabilityMetrics = metricsV5Processor.getAvailabilityMetrics();

        List<BigDecimal> aggregateAvailability = availabilityMetrics.get(AspectEnum.ALL);
        List<BigDecimal> authenticatedAvailability = availabilityMetrics.get(AspectEnum.AUTHENTICATED);
        List<BigDecimal> unauthenticatedAvailability = availabilityMetrics.get(AspectEnum.UNAUTHENTICATED);

        Assert.assertNotNull(aggregateAvailability, "Aggregate availability metrics should not be null");
        Assert.assertNotNull(authenticatedAvailability, "Authenticated availability metrics should not be null");
        Assert.assertNotNull(unauthenticatedAvailability, "Unauthenticated availability metrics should not be null");

        Assert.assertEquals(aggregateAvailability.size(), TOTAL_MONTHS,
                "Aggregate availability metrics size should be equal to the number of months");
        Assert.assertEquals(authenticatedAvailability.size(), TOTAL_MONTHS,
                "Authenticated availability metrics size should be equal to the number of months");
        Assert.assertEquals(unauthenticatedAvailability.size(), TOTAL_MONTHS,
                "Unauthenticated availability metrics size should be equal to the number of months");
    }

    @Test
    public void testGetInvocationMetrics() throws Exception {

        Map<PriorityEnum, List<Integer>> invocationMetrics = metricsV5Processor.getInvocationMetrics();
        Assert.assertNotNull(invocationMetrics, "Invocation metrics should not be null");
        Assert.assertEquals(invocationMetrics.size(), PRIORITY_TIERS, "Invocation metrics size should be " +
                "equal to the number of priorities");
    }

    @Test
    public void testGetPerformanceMetrics() throws Exception {

        Map<PriorityEnum, List<Integer>> invocationMetrics = metricsV5Processor.getInvocationMetrics();
        List<BigDecimal> performanceMetrics = metricsV5Processor.getPerformanceMetrics(invocationMetrics);

        Assert.assertNotNull(performanceMetrics, "Performance metrics should not be null");
        Assert.assertEquals(performanceMetrics.size(), TOTAL_DAYS, "Performance metrics size should be " +
                "equal to the number of days");
    }

    @Test
    public void testGetAverageResponseTimeMetrics() throws Exception {

        Map<PriorityEnum, List<Integer>> invocationMetrics = metricsV5Processor.getInvocationMetrics();
        Map<PriorityEnum, List<BigDecimal>> averageResponseTimeMetrics = metricsV5Processor.
                getAverageResponseTimeMetrics(invocationMetrics);

        Assert.assertNotNull(averageResponseTimeMetrics, "Average response time metrics should not be null");
        Assert.assertEquals(averageResponseTimeMetrics.size(), PRIORITY_TIERS, "Average response time " +
                "metrics size should be equal to the number of priorities");
    }

    @Test
    public void testGetSessionCountMetrics() throws Exception {

        List<Integer> sessionCountMetrics = metricsV5Processor.getSessionCountMetrics();
        Assert.assertNotNull(sessionCountMetrics, "Session count metrics should not be null");
        Assert.assertEquals(sessionCountMetrics.size(), TOTAL_DAYS, "Session count metrics size should be " +
                "equal to the number of days");
    }

    @Test
    public void testGetAverageTPSMetrics() throws Exception {

        Map<AspectEnum, List<BigDecimal>> averageTPSMetrics = metricsV5Processor.getAverageTPSMetrics();

        List<BigDecimal> aggregateAverageTPSMetrics = averageTPSMetrics.get(AspectEnum.ALL);
        List<BigDecimal> authenticatedAverageTPSMetrics = averageTPSMetrics.get(AspectEnum.AUTHENTICATED);
        List<BigDecimal> unauthenticatedAverageTPSMetrics = averageTPSMetrics.get(AspectEnum.UNAUTHENTICATED);

        Assert.assertNotNull(aggregateAverageTPSMetrics,
                "Aggregate average TPS metrics should not be null");
        Assert.assertNotNull(authenticatedAverageTPSMetrics,
                "Authenticated average TPS metrics should not be null");
        Assert.assertNotNull(unauthenticatedAverageTPSMetrics,
                "Unauthenticated average TPS metrics should not be null");

        Assert.assertEquals(aggregateAverageTPSMetrics.size(), TOTAL_DAYS,
                "Aggregate average TPS metrics size should be equal to the number of days");
        Assert.assertEquals(authenticatedAverageTPSMetrics.size(), TOTAL_DAYS,
                "Authenticated average TPS metrics size should be equal to the number of days");
        Assert.assertEquals(unauthenticatedAverageTPSMetrics.size(), TOTAL_DAYS,
                "Unauthenticated average TPS metrics size should be equal to the number of days");
    }

    @Test
    public void testGetPeakTPSMetrics() throws Exception {

        List<BigDecimal> peakTPSMetrics = metricsV5Processor.getPeakTPSMetrics();
        Assert.assertNotNull(peakTPSMetrics, "Peak TPS metrics should not be null");
        Assert.assertEquals(peakTPSMetrics.size(), TOTAL_DAYS, "Peak TPS metrics size should be equal to " +
                "the number of days");
    }

    @Test
    public void testGetErrorMetrics() throws Exception {
        List<Integer> errorMetrics = metricsV5Processor.getErrorMetrics();

        Assert.assertNotNull(errorMetrics, "Error metrics should not be null");
        Assert.assertEquals(errorMetrics.size(), TOTAL_DAYS, "Error metrics size should be equal to the " +
                "number of days");
    }

    @Test
    public void testGetRejectionMetrics() throws Exception {

        Map<AspectEnum, List<Integer>> rejectionMetrics = metricsV5Processor.getRejectionMetrics();
        Assert.assertNotNull(rejectionMetrics, "Rejection metrics should not be null");
        Assert.assertEquals(rejectionMetrics.size(), 2, "Rejection metrics map size should be " +
                "equal to the number of aspects");
    }

    @Test
    public void testGetRecipientCountMetrics() throws Exception {
        int recipientCountMetrics = metricsV5Processor.getRecipientCountMetrics();

        Assert.assertNotNull(recipientCountMetrics, "Recipient count metrics should not be null");
        Assert.assertEquals(recipientCountMetrics, 1, "Recipient count metrics should be " +
                "equal to the provided value");
    }

    @Test
    public void testGetCustomerCountMetrics() throws Exception {
        int recipientCountMetrics = metricsV5Processor.getCustomerCountMetrics();

        Assert.assertNotNull(recipientCountMetrics, "Customer count metrics should not be null");
        Assert.assertEquals(recipientCountMetrics, 1, "Customer count metrics should be " +
                "equal to the provided value");
    }
}
