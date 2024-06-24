/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.util;

import com.wso2.openbanking.cds.metrics.constants.MetricsConstants;
import com.wso2.openbanking.cds.metrics.model.MetricsResponseModel;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Util methods used in Metrics Service.
 */
public class MetricsServiceUtil {

    /**
     * Append historic metrics to current day metrics.
     *
     * @param currentDayMetrics - current day metrics
     * @param historicMetrics   - historic metrics
     */
    public static void appendHistoricMetricsToCurrentDayMetrics(
            MetricsResponseModel currentDayMetrics, MetricsResponseModel historicMetrics) {

        currentDayMetrics.getAvailability().addAll(historicMetrics.getAvailability());
        currentDayMetrics.getPerformance().addAll(historicMetrics.getPerformance());
        currentDayMetrics.getSessionCount().addAll(historicMetrics.getSessionCount());
        currentDayMetrics.getAverageTPS().addAll(historicMetrics.getAverageTPS());
        currentDayMetrics.getPeakTPS().addAll(historicMetrics.getPeakTPS());
        currentDayMetrics.getErrors().addAll(historicMetrics.getErrors());
        appendInvocationMetrics(currentDayMetrics, historicMetrics);
        appendAverageResponseMetrics(currentDayMetrics, historicMetrics);
        appendRejectionMetrics(currentDayMetrics, historicMetrics);
    }

    /**
     * Append historic invocation metrics to current day invocation metrics.
     *
     * @param currentDayMetrics - current day metrics
     * @param historicMetrics   - historic metrics
     */
    private static void appendInvocationMetrics(
            MetricsResponseModel currentDayMetrics, MetricsResponseModel historicMetrics) {

        currentDayMetrics.getInvocationHighPriority().addAll(historicMetrics.getInvocationHighPriority());
        currentDayMetrics.getInvocationLowPriority().addAll(historicMetrics.getInvocationLowPriority());
        currentDayMetrics.getInvocationUnattended().addAll(historicMetrics.getInvocationUnattended());
        currentDayMetrics.getInvocationUnauthenticated().addAll(historicMetrics.getInvocationUnauthenticated());
        currentDayMetrics.getInvocationLargePayload().addAll(historicMetrics.getInvocationLargePayload());
    }

    /**
     * Append historic average response metrics to current day average response metrics.
     *
     * @param currentDayMetrics - current day metrics
     * @param historicMetrics   - historic metrics
     */
    private static void appendAverageResponseMetrics(
            MetricsResponseModel currentDayMetrics, MetricsResponseModel historicMetrics) {

        currentDayMetrics.getAverageResponseUnauthenticated().addAll(historicMetrics.
                getAverageResponseUnauthenticated());
        currentDayMetrics.getAverageResponseHighPriority().addAll(historicMetrics.getAverageResponseHighPriority());
        currentDayMetrics.getAverageResponseLowPriority().addAll(historicMetrics.getAverageResponseLowPriority());
        currentDayMetrics.getAverageResponseUnattended().addAll(historicMetrics.getAverageResponseUnattended());
        currentDayMetrics.getAverageResponseLargePayload().addAll(historicMetrics.getAverageResponseLargePayload());
    }

    /**
     * Append historic rejection metrics to current day rejection metrics.
     *
     * @param currentDayMetrics - current day metrics
     * @param historicMetrics   - historic metrics
     */
    private static void appendRejectionMetrics(
            MetricsResponseModel currentDayMetrics, MetricsResponseModel historicMetrics) {

        currentDayMetrics.getAuthenticatedEndpointRejections().addAll(historicMetrics.
                getAuthenticatedEndpointRejections());
        currentDayMetrics.getUnauthenticatedEndpointRejections().addAll(historicMetrics.
                getUnauthenticatedEndpointRejections());
    }

    /**
     * Check whether the response model is expired using the requestTime parameter.
     *
     * @param metricsResponseModel - MetricsResponseModel
     * @return boolean - whether the response model is expired
     */
    public static boolean isResponseModelExpired(MetricsResponseModel metricsResponseModel) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(MetricsConstants.REQUEST_TIMESTAMP_PATTERN);
        LocalDate requestDate = ZonedDateTime.parse(metricsResponseModel.getRequestTime(), formatter).toLocalDate();
        return requestDate.isBefore(LocalDate.now());
    }
}