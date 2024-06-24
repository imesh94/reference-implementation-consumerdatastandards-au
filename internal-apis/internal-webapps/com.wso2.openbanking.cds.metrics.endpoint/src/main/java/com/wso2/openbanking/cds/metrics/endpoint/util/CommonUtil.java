/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.endpoint.util;

import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for common metrics utility functions.
 */
public class CommonUtil {

    private static final Log log = LogFactory.getLog(CommonUtil.class);

    /**
     * Get Admin API Self URL.
     *
     * @param period - period (ALL, CURRENT, HISTORIC)
     * @return self-url string
     */
    public static String getCDSAdminSelfLink(String period) {

        String adminAPIBaseURL = OpenBankingCDSConfigParser.getInstance().getAdminAPISelfLink();
        return String.format("%smetrics?period=%s", adminAPIBaseURL, period);
    }

    /**
     * Adds missing months to the provided list of BigDecimal values.
     * If the list has fewer months than the calculated months from the availability start date to the current date,
     * this method will add default values of BigDecimal.ONE for each missing month.
     *
     * @param list - the list of BigDecimal values representing months
     * @return - the updated list of BigDecimal values with missing months added
     */
    public static List<BigDecimal> addMissingMonths(List<BigDecimal> list) {
        try {
            OpenBankingCDSConfigParser configParser = OpenBankingCDSConfigParser.getInstance();
            ZoneId timeZone = ZoneId.of(configParser.getMetricsTimeZone());
            String availabilityStartDateString = configParser.getAvailabilityStartDate();

            if (availabilityStartDateString == null || availabilityStartDateString.isEmpty()) {
                return list;
            }

            // Parse the availability start date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            ZonedDateTime availabilityStartDate = LocalDate.parse(availabilityStartDateString, formatter)
                    .atStartOfDay(timeZone);

            // Get the current date and time
            ZonedDateTime currentDateTime = LocalDateTime.now().atZone(timeZone);

            // Calculate the number of months in the list
            int monthsCount = list.size();

            // Calculate the number of months from the availability start date to the current date
            int monthsFromStart = (int) ChronoUnit.MONTHS.between(
                    availabilityStartDate.withDayOfMonth(1),
                    currentDateTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
            );

            // Ensure we do not consider more than 12 months
            monthsFromStart = Math.min(12, monthsFromStart);

            // If the list has fewer months than the calculated months, add the missing months
            if (monthsCount < monthsFromStart) {
                int missingMonths = monthsFromStart - monthsCount;
                for (int i = 0; i < missingMonths; i++) {
                    list.add(BigDecimal.ONE); // Add a default value of BigDecimal.ONE for each missing month
                }
            }
        } catch (DateTimeParseException e) {
            log.error("Error while adding missing months. Proceeding with available data", e);
        }

        return list;
    }

    /**
     * Converts a list of BigDecimal values to a list of Strings with a scale of 3,
     * rounding half up.
     *
     * @param list - the list of BigDecimal values
     * @return - the list of String representations of BigDecimal values with scale of 3
     */
    public static List<String> convertToStringListWithScale(List<BigDecimal> list) {

        return list.stream().map((BigDecimal decimal) -> decimal.setScale(3, RoundingMode.HALF_UP).toString())
                .collect(Collectors.toList());
    }

    /**
     * Converts a nested list of BigDecimal values to a nested list of Strings.
     *
     * @param nestedList - the nested list of BigDecimal values
     * @return - the nested list of String representations of BigDecimal values
     */
    public static List<List<String>> convertToNestedStringList(List<List<BigDecimal>> nestedList) {

        return nestedList.stream()
                .map(CommonUtil::convertToStringList)
                .collect(Collectors.toList());
    }

    /**
     * Convert BigDecimal list to String list.
     *
     * @param list - BigDecimal list
     * @return - String list
     */
    public static List<String> convertToStringList(List<BigDecimal> list) {

        return list.stream()
                .map(BigDecimal::toString)
                .collect(Collectors.toList());
    }

}
