/*
 * Copyright (c) 2021-2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.service;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.metrics.util.AspectEnum;
import com.wso2.openbanking.cds.metrics.util.PriorityEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Class containing methods for processing metrics data.
 */
public interface MetricsProcessor {

    /**
     * Get availability metrics.
     *
     * @return List of availability metrics
     * @throws OpenBankingException - OpenBankingException
     */
    List<BigDecimal> getAvailabilityMetrics() throws OpenBankingException;

    /**
     * Get invocation metrics.
     *
     * @return Map of invocation metrics with priority tiers
     * @throws OpenBankingException - OpenBankingException
     */
    Map<PriorityEnum, List<BigDecimal>> getInvocationMetrics() throws OpenBankingException;

    /**
     * Get performance metrics.
     *
     * @param invocationMetricsMap - Map of invocation metrics
     * @return List of performance metrics
     * @throws OpenBankingException - OpenBankingException
     */
    List<BigDecimal> getPerformanceMetrics(Map<PriorityEnum, List<BigDecimal>> invocationMetricsMap)
            throws OpenBankingException;

    /**
     * Get average response time metrics.
     *
     * @param invocationMetricsMap - Map of invocation metrics
     * @return Map of average response time metrics with priority tiers
     * @throws OpenBankingException - OpenBankingException
     */
    Map<PriorityEnum, List<BigDecimal>> getAverageResponseTimeMetrics(
            Map<PriorityEnum, List<BigDecimal>> invocationMetricsMap) throws OpenBankingException;

    /**
     * Get session count metrics.
     *
     * @return List of session count metrics
     * @throws OpenBankingException - OpenBankingException
     */
    List<BigDecimal> getSessionCountMetrics() throws OpenBankingException;

    /**
     * Calculate average TPS for a day.
     * Used formula: averageTPS = (total no. of transactions for a day / no. of seconds in a day)
     *
     * @param invocationMetricsMap - Map of invocation metrics
     * @return list of average TPS
     */
    List<BigDecimal> getAverageTPSMetrics(Map<PriorityEnum, List<BigDecimal>> invocationMetricsMap);

    /**
     * Get peak TPS metrics.
     *
     * @return List of peak TPS metrics
     * @throws OpenBankingException - OpenBankingException
     */
    List<BigDecimal> getPeakTPSMetrics() throws OpenBankingException;

    /**
     * Get error metrics.
     *
     * @return List of error metrics
     * @throws OpenBankingException - OpenBankingException
     */
    List<BigDecimal> getErrorMetrics() throws OpenBankingException;

    /**
     * Get rejection metrics.
     *
     * @return Map of rejection metrics with aspect tiers
     * @throws OpenBankingException - OpenBankingException
     */
    Map<AspectEnum, List<BigDecimal>> getRejectionMetrics() throws OpenBankingException;

    /**
     * Get recipient count metrics.
     *
     * @return count of data recipients
     * @throws OpenBankingException - OpenBankingException
     */
    int getRecipientCountMetrics() throws OpenBankingException;

    /**
     * Get customer count metrics.
     *
     * @return count of customers with active authorizations
     * @throws OpenBankingException - OpenBankingException
     */
    int getCustomerCountMetrics() throws OpenBankingException;

}
