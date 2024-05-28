/*
 * Copyright (c) 2021-2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.util;

import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.metrics.constants.MetricsConstants;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Util methods for manipulating time.
 */
public class DateTimeUtil {

    private static final ZoneId TIME_ZONE = ZoneId.of(OpenBankingCDSConfigParser.getInstance().getMetricsTimeZone());

    private DateTimeUtil() {
    }

    /**
     * Get the difference in days between two epoch timestamps.
     *
     * @param epochSecondsStart - start epoch timestamp in seconds.
     * @param epochSecondsEnd   - end epoch timestamp in seconds.
     * @return difference in days
     */
    public static int getDayDifference(long epochSecondsStart, long epochSecondsEnd) {

        ZonedDateTime startDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecondsStart), TIME_ZONE);
        ZonedDateTime endDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecondsEnd), TIME_ZONE);
        Duration duration = Duration.between(startDateTime, endDateTime);
        return (int) duration.toDays();
    }

    /**
     * Get time range for the given period.
     * CURRENT - From start of today to end of today
     * HISTORIC - From start of 7th past day to end of yesterday
     * ALL - From start of 7th past day to end of today
     *
     * @param period - PeriodEnum (CURRENT, HISTORIC)
     * @return - Array of two strings with start and end times
     */
    public static String[] getTimeRange(PeriodEnum period) {

        LocalDate today = LocalDate.now(TIME_ZONE);
        String[] timeRange = new String[2];
        ZonedDateTime startOfToday = today.atStartOfDay(TIME_ZONE);
        if (PeriodEnum.CURRENT == period) {
            ZonedDateTime endOfToday = startOfToday.plusDays(1).minus(1, ChronoUnit.MILLIS);
            timeRange[0] = formatZonedDateTime(startOfToday);
            timeRange[1] = formatZonedDateTime(endOfToday);
        } else if (PeriodEnum.HISTORIC == period) {
            ZonedDateTime startOf7thPastDay = today.atStartOfDay(TIME_ZONE).minus(7, ChronoUnit.DAYS);
            ZonedDateTime endOfYesterday = today.atStartOfDay(TIME_ZONE).minus(1, ChronoUnit.MILLIS);
            timeRange[0] = formatZonedDateTime(startOf7thPastDay);
            timeRange[1] = formatZonedDateTime(endOfYesterday);
        } else if (PeriodEnum.ALL == period) {
            ZonedDateTime startOf8thPastDay = today.atStartOfDay(TIME_ZONE).minus(7, ChronoUnit.DAYS);
            ZonedDateTime endOfToday = startOfToday.plusDays(1).minus(1, ChronoUnit.MILLIS);
            timeRange[0] = formatZonedDateTime(startOf8thPastDay);
            timeRange[1] = formatZonedDateTime(endOfToday);
        }
        return timeRange;
    }

    /**
     * Get time range for the given period.
     * CURRENT - From start of the current month to end of the current month
     * HISTORIC - From start of the previous 12th month to end of the last month
     *
     * @param period - PeriodEnum (CURRENT, HISTORIC)
     * @return - Array of two strings with start and end times
     */
    public static String[] getAvailabilityMetricsTimeRange(PeriodEnum period) {

        LocalDate today = LocalDate.now(TIME_ZONE);
        String[] timeRange = new String[2];
        ZonedDateTime startOfThisMonth = today.withDayOfMonth(1).atStartOfDay(TIME_ZONE);
        if (PeriodEnum.CURRENT == period) {
            ZonedDateTime endOfThisMonth = startOfThisMonth.plusMonths(1).minus(1, ChronoUnit.MILLIS);
            timeRange[0] = formatZonedDateTime(startOfThisMonth);
            timeRange[1] = formatZonedDateTime(endOfThisMonth);
        } else if (PeriodEnum.HISTORIC == period) {
            ZonedDateTime startOf12MonthsAgo = today.minusYears(1).withDayOfMonth(1).atStartOfDay(TIME_ZONE);
            ZonedDateTime endOfLastMonth = today.withDayOfMonth(1).atStartOfDay(TIME_ZONE).minus(1, ChronoUnit.MILLIS);
            timeRange[0] = formatZonedDateTime(startOf12MonthsAgo);
            timeRange[1] = formatZonedDateTime(endOfLastMonth);
        } else if (PeriodEnum.ALL == period) {
            ZonedDateTime startOf12MonthsAgo = today.minusYears(1).withDayOfMonth(1).atStartOfDay(TIME_ZONE);
            ZonedDateTime endOfThisMonth = startOfThisMonth.plusMonths(1).minus(1, ChronoUnit.MILLIS);
            timeRange[0] = formatZonedDateTime(startOf12MonthsAgo);
            timeRange[1] = formatZonedDateTime(endOfThisMonth);
        }
        return timeRange;
    }

    /**
     * Format ZonedDateTime to a string.
     *
     * @param dateTime - ZonedDateTime
     * @return - formatted string
     */
    public static String formatZonedDateTime(ZonedDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(MetricsConstants.SP_TIMESTAMP_PATTERN);
        String formattedDateTime = dateTime.format(formatter).replace("Z", "");
        return formattedDateTime;
    }

    /**
     * Get epoch timestamp from the given timestamp string.
     *
     * @param timestamp - timestamp string
     * @return - epoch timestamp
     */
    public static long getEpochTimestamp(String timestamp) {
        timestamp = timestamp.trim();
        if (!timestamp.endsWith("Z") && !timestamp.matches(".*[+-]\\d{2}:\\d{2}$")) {
            timestamp += " Z"; // Append 'Z' to denote GMT if no offset is provided
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(MetricsConstants.SP_TIMESTAMP_PATTERN);
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestamp, formatter);
        return zonedDateTime.toInstant().getEpochSecond();
    }

}
