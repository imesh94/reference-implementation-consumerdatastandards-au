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

import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.metrics.util.DateTimeUtil;
import com.wso2.openbanking.cds.metrics.util.PeriodEnum;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

@PrepareForTest({DateTimeUtil.class, OpenBankingCDSConfigParser.class})
@PowerMockIgnore({"javax.crypto.*", "jdk.internal.reflect.*"})
public class MetricsV5QueryCreatorImplTest extends PowerMockTestCase {

    private MetricsV5QueryCreatorImpl queryCreator;
    OpenBankingCDSConfigParser configParserMock;


    @BeforeClass
    public void setUp() {

        configParserMock = PowerMockito.mock(OpenBankingCDSConfigParser.class);
        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        PowerMockito.when(OpenBankingCDSConfigParser.getInstance()).thenReturn(configParserMock);
        doReturn("GMT").when(configParserMock).getMetricsTimeZone();

        PowerMockito.mockStatic(DateTimeUtil.class);
        String[] timeRange = {"2024-01-01T00:00:00Z", "2024-01-01T23:59:59Z"};
        String[] availabilityTimeRange = {"2024-01-01T00:00:00Z", "2024-01-31T23:59:59Z"};

        // Ensure that the correct PeriodEnum value is used
        when(DateTimeUtil.getTimeRange(PeriodEnum.CURRENT)).thenReturn(timeRange);
        when(DateTimeUtil.getAvailabilityMetricsTimeRange(PeriodEnum.CURRENT)).thenReturn(availabilityTimeRange);
        when(DateTimeUtil.getEpochTimestamp("2024-01-01T00:00:00Z")).thenReturn(1704067200L);
        when(DateTimeUtil.getEpochTimestamp("2024-01-01T23:59:59Z")).thenReturn(1706745599L);
        when(DateTimeUtil.getEpochTimestamp("2024-01-31T23:59:59Z")).thenReturn(1706745599L);

        queryCreator = new MetricsV5QueryCreatorImpl(PeriodEnum.CURRENT);
    }

    @Test
    public void testGetAvailabilityMetricsQuery() {
        // Expected SQL query construction using mocked timestamps
        String expectedQuery = "from SERVER_OUTAGES_RAW_DATA select OUTAGE_ID, TIMESTAMP, TYPE, TIME_FROM, TIME_TO, " +
                "ASPECT group by OUTAGE_ID, TIMESTAMP, TYPE, TIME_FROM, TIME_TO, ASPECT having TIME_FROM >= " +
                1704067200 + " AND TIME_TO <= " + 1706745599 + ";";

        // Execute the method
        String actualQuery = queryCreator.getAvailabilityMetricsQuery();

        // Assert that the expected and actual SQL queries match
        assertEquals(actualQuery, expectedQuery, "The generated availability metrics query does not " +
                "match the expected result.");
    }

    @Test
    public void testGetInvocationMetricsQuery() {
        String expectedQuery = "from CDSMetricsAgg within '2024-01-01T00:00:00Z', '2024-01-01T23:59:59Z' per " +
                "'days' select priorityTier, totalReqCount, AGG_TIMESTAMP group by priorityTier, AGG_TIMESTAMP " +
                "order by AGG_TIMESTAMP desc;";
        String actualQuery = queryCreator.getInvocationMetricsQuery();
        assertEquals(actualQuery, expectedQuery);
    }

    @Test
    public void testGetSessionCountMetricsQuery() {
        String expectedQuery = "from CDSMetricsSessionAgg within '2024-01-01T00:00:00Z', '2024-01-01T23:59:59Z' " +
                "per 'days' select sessionCount, AGG_TIMESTAMP;";
        String actualQuery = queryCreator.getSessionCountMetricsQuery();
        assertEquals(actualQuery, expectedQuery);
    }

    @Test
    public void testGetErrorMetricsQuery() {
        String expectedQuery = "from CDSMetricsStatusAgg within '2024-01-01T00:00:00Z', '2024-01-01T23:59:59Z' " +
                "per 'days' select totalReqCount, AGG_TIMESTAMP having statusCode >= 500 and statusCode " +
                "< 600;";
        String actualQuery = queryCreator.getErrorMetricsQuery();
        assertEquals(actualQuery, expectedQuery);
    }

    @Test
    public void testGetRejectionMetricsQuery() {
        String expectedQuery = "from API_INVOCATION_RAW_DATA on str:contains(API_NAME, 'ConsumerDataStandards') " +
                "select count(STATUS_CODE) as throttleOutCount, TIMESTAMP, CONSUMER_ID group by TIMESTAMP, " +
                "CONSUMER_ID having STATUS_CODE == 429 and TIMESTAMP > 1704067200 and TIMESTAMP < 1706745599;";
        String actualQuery = queryCreator.getRejectionMetricsQuery();
        assertEquals(actualQuery, expectedQuery);
    }

    @Test
    public void testGetRecipientCountMetricsQuery() {
        String expectedQuery = "from CDSMetricsCustomerRecipientSummary on ACTIVE_AUTH_COUNT>0 select " +
                "distinctCount(CLIENT_ID) as recipientCount;";
        String actualQuery = queryCreator.getRecipientCountMetricsQuery();
        assertEquals(actualQuery, expectedQuery);
    }

    @Test
    public void testGetCustomerCountMetricsQuery() {
        String expectedQuery = "from CDSMetricsCustomerRecipientSummary on ACTIVE_AUTH_COUNT > 0 select " +
                "distinctCount(USER_ID) as customerCount;";
        String actualQuery = queryCreator.getCustomerCountMetricsQuery();
        assertEquals(actualQuery, expectedQuery);
    }

    @Test
    public void testGetSuccessfulInvocationsQuery() {
        String expectedQuery = "from CDSMetricsPerfAgg within '2024-01-01T00:00:00Z', '2024-01-01T23:59:59Z' " +
                "per 'days' select totalReqCount, AGG_TIMESTAMP having withinThreshold == true;";
        String actualQuery = queryCreator.getSuccessfulInvocationsQuery();
        assertEquals(actualQuery, expectedQuery);
    }

    @Test
    public void testGetTotalResponseTimeQuery() {
        String expectedQuery = "from CDSMetricsAgg within '2024-01-01T00:00:00Z', '2024-01-01T23:59:59Z' " +
                "per 'days' select priorityTier, (totalRespTime / 1000.0) as totalRespTime, " +
                "AGG_TIMESTAMP group by priorityTier, AGG_TIMESTAMP order by AGG_TIMESTAMP desc;";
        String actualQuery = queryCreator.getTotalResponseTimeQuery();
        assertEquals(actualQuery, expectedQuery);
    }
}
