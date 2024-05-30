/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 *
 */

package com.wso2.openbanking.cds.metrics.util;

import com.wso2.openbanking.cds.metrics.model.MetricsResponseModel;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class MetricsServiceUtilTest {

    private MetricsResponseModel currentDayMetrics;
    private MetricsResponseModel historicMetrics;
    private static final int TOTAL_DAYS = 8;

    @BeforeMethod
    public void setUp() {
        currentDayMetrics = createMetricsResponseModelWithValues(1);
        historicMetrics = createMetricsResponseModelWithValues(7);
    }

    private MetricsResponseModel createMetricsResponseModelWithValues(int numberOfEntries) {

        String currentDate = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        MetricsResponseModel model = new MetricsResponseModel(currentDate);
        model.setAvailability(createBigDecimalList(numberOfEntries));
        model.setErrors(createBigDecimalList(numberOfEntries));
        model.setPeakTPS(createBigDecimalList(numberOfEntries));
        model.setAverageTPS(createBigDecimalList(numberOfEntries));
        model.setPerformance(createBigDecimalList(numberOfEntries));
        model.setSessionCount(createBigDecimalList(numberOfEntries));

        model.setInvocationUnauthenticated(createBigDecimalList(numberOfEntries));
        model.setInvocationHighPriority(createBigDecimalList(numberOfEntries));
        model.setInvocationLowPriority(createBigDecimalList(numberOfEntries));
        model.setInvocationUnattended(createBigDecimalList(numberOfEntries));
        model.setInvocationLargePayload(createBigDecimalList(numberOfEntries));

        model.setAverageResponseUnauthenticated(createBigDecimalList(numberOfEntries));
        model.setAverageResponseHighPriority(createBigDecimalList(numberOfEntries));
        model.setAverageResponseLowPriority(createBigDecimalList(numberOfEntries));
        model.setAverageResponseUnattended(createBigDecimalList(numberOfEntries));
        model.setAverageResponseLargePayload(createBigDecimalList(numberOfEntries));

        model.setAuthenticatedEndpointRejections(createBigDecimalList(numberOfEntries));
        model.setUnauthenticatedEndpointRejections(createBigDecimalList(numberOfEntries));

        return model;
    }

    private List<BigDecimal> createBigDecimalList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> BigDecimal.valueOf(Math.random() * 10))
                .collect(Collectors.toList());
    }

    @Test
    public void testAppendHistoricMetricsToCurrentDayMetrics() {

        MetricsServiceUtil.appendHistoricMetricsToCurrentDayMetrics(currentDayMetrics, historicMetrics);
        // Test if all metrics lists have been appended correctly
        assertEquals(currentDayMetrics.getAvailability().size(), TOTAL_DAYS);
        assertEquals(currentDayMetrics.getErrors().size(), TOTAL_DAYS);
        assertEquals(currentDayMetrics.getPeakTPS().size(), TOTAL_DAYS);
        assertEquals(currentDayMetrics.getAverageTPS().size(), TOTAL_DAYS);
        assertEquals(currentDayMetrics.getPerformance().size(), TOTAL_DAYS);
        assertEquals(currentDayMetrics.getSessionCount().size(), TOTAL_DAYS);

        assertEquals(currentDayMetrics.getInvocationUnauthenticated().size(), TOTAL_DAYS);
        assertEquals(currentDayMetrics.getInvocationHighPriority().size(), TOTAL_DAYS);
        assertEquals(currentDayMetrics.getInvocationLowPriority().size(), TOTAL_DAYS);
        assertEquals(currentDayMetrics.getInvocationUnattended().size(), TOTAL_DAYS);
        assertEquals(currentDayMetrics.getInvocationLargePayload().size(), TOTAL_DAYS);

        assertEquals(currentDayMetrics.getAverageResponseUnauthenticated().size(), TOTAL_DAYS);
        assertEquals(currentDayMetrics.getAverageResponseHighPriority().size(), TOTAL_DAYS);
        assertEquals(currentDayMetrics.getAverageResponseLowPriority().size(), TOTAL_DAYS);
        assertEquals(currentDayMetrics.getAverageResponseUnattended().size(), TOTAL_DAYS);
        assertEquals(currentDayMetrics.getAverageResponseLargePayload().size(), TOTAL_DAYS);

        assertEquals(currentDayMetrics.getAuthenticatedEndpointRejections().size(), TOTAL_DAYS);
        assertEquals(currentDayMetrics.getUnauthenticatedEndpointRejections().size(), TOTAL_DAYS);
    }

    @Test
    public void testIsResponseModelExpired() {
        assertFalse(MetricsServiceUtil.isResponseModelExpired(currentDayMetrics));
        String pastDate = ZonedDateTime.now().minusDays(2).format(DateTimeFormatter.
                ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        MetricsResponseModel expiredModel = new MetricsResponseModel(pastDate);
        assertTrue(MetricsServiceUtil.isResponseModelExpired(expiredModel));
    }
}
