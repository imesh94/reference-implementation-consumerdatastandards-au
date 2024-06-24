/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.service;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.metrics.constants.MetricsConstants;
import com.wso2.openbanking.cds.metrics.data.MetricsDataProvider;
import com.wso2.openbanking.cds.metrics.model.ServerOutageDataModel;
import com.wso2.openbanking.cds.metrics.util.AspectEnum;
import com.wso2.openbanking.cds.metrics.util.MetricsProcessorUtil;
import com.wso2.openbanking.cds.metrics.util.PeriodEnum;
import com.wso2.openbanking.cds.metrics.util.PriorityEnum;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wso2.openbanking.cds.metrics.constants.MetricsConstants.NO_DATA_ERROR;

/**
 * A general metrics processor implementation that can be used to calculate metrics for any given period of time.
 */
public class MetricsV3ProcessorImpl implements MetricsProcessor {

    MetricsDataProvider metricsDataProvider;
    int numberOfDays;
    int numberOfMonths;
    long metricsCountLastDateEpoch;
    ZonedDateTime availabilityMetricsLastDate;

    private static final Log log = LogFactory.getLog(MetricsV3ProcessorImpl.class);

    /**
     * Constructor for MetricsV3ProcessorImpl.
     *
     * @param period - period (Current, Historic, All).
     */
    public MetricsV3ProcessorImpl(PeriodEnum period, MetricsDataProvider metricsDataProvider, ZoneId timeZone)
            throws OpenBankingException {

        ZonedDateTime currentDateEnd = ZonedDateTime.now(timeZone).with(LocalTime.MAX);
        switch (period) {
            case CURRENT:
                numberOfDays = 1;
                numberOfMonths = 1;
                metricsCountLastDateEpoch = currentDateEnd.toEpochSecond();
                availabilityMetricsLastDate = currentDateEnd;
                break;
            case HISTORIC:
                numberOfDays = 7;
                numberOfMonths = 12;
                metricsCountLastDateEpoch = currentDateEnd.minusDays(1).toEpochSecond();
                availabilityMetricsLastDate = currentDateEnd.withDayOfMonth(1).minusDays(1);
                break;
            case ALL:
                numberOfDays = 8;
                numberOfMonths = 13;
                metricsCountLastDateEpoch = currentDateEnd.toEpochSecond();
                availabilityMetricsLastDate = currentDateEnd;
                break;
            default:
                throw new OpenBankingException("Invalid period value. Only CURRENT and HISTORIC and ALL periods are" +
                        " accepted at this level.");
        }
        this.metricsDataProvider = metricsDataProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BigDecimal> getAvailabilityMetrics() throws OpenBankingException {

        log.debug("Starting availability metrics calculation.");
        JSONObject availabilityMetricsJsonObject = metricsDataProvider.getAvailabilityMetricsData();
        if (availabilityMetricsJsonObject != null) {
            List<ServerOutageDataModel> serverOutageData = MetricsProcessorUtil.getServerOutageDataFromJson(
                    availabilityMetricsJsonObject);
            List<BigDecimal> availabilityList = MetricsProcessorUtil.getAvailabilityFromServerOutages(serverOutageData,
                    numberOfMonths, availabilityMetricsLastDate);
            log.debug("Finished availability metrics calculation successfully.");
            return availabilityList;
        } else {
            throw new OpenBankingException(String.format(NO_DATA_ERROR, MetricsConstants.AVAILABILITY));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<PriorityEnum, List<BigDecimal>> getInvocationMetrics() throws OpenBankingException {

        log.debug("Starting invocation metrics calculation.");
        JSONObject invocationMetricsJsonObject = metricsDataProvider.getInvocationMetricsData();
        if (invocationMetricsJsonObject != null) {
            Map<PriorityEnum, List<BigDecimal>> invocationMetricsMap = MetricsProcessorUtil.
                getPopulatedInvocationMetricsMap(invocationMetricsJsonObject, numberOfDays, metricsCountLastDateEpoch);
            log.debug("Finished invocation metrics calculation successfully.");
            return invocationMetricsMap;
        } else {
            throw new OpenBankingException(String.format(NO_DATA_ERROR, MetricsConstants.INVOCATION));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BigDecimal> getPerformanceMetrics(Map<PriorityEnum, List<BigDecimal>> invocationMetricsMap)
            throws OpenBankingException {

        log.debug("Starting performance metrics calculation.");
        List<BigDecimal> totalInvocationList = MetricsProcessorUtil.getTotalInvocationsForEachDay(invocationMetricsMap);
        List<BigDecimal> successInvocationList = getSuccessfulInvocations();
        List<BigDecimal> performanceMetricsList = MetricsProcessorUtil.divideList(successInvocationList,
                totalInvocationList);
        log.debug("Finished performance metrics calculation successfully.");
        return performanceMetricsList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<PriorityEnum, List<BigDecimal>> getAverageResponseTimeMetrics(
            Map<PriorityEnum, List<BigDecimal>> invocationMetricsMap) throws OpenBankingException {

        log.debug("Starting average response metrics calculation.");
        Map<PriorityEnum, List<BigDecimal>> totalResponseTimeMetricsMap = getTotalResponseTimeMap();
        Map<PriorityEnum, List<BigDecimal>> averageResponseMetricsMap = new HashMap<>();

        for (PriorityEnum priority : PriorityEnum.values()) {
            List<BigDecimal> responseTimeList = totalResponseTimeMetricsMap.get(priority);
            List<BigDecimal> invocationCountList = invocationMetricsMap.get(priority);
            List<BigDecimal> tempAverageList = MetricsProcessorUtil.divideList(responseTimeList, invocationCountList);
            averageResponseMetricsMap.put(priority, tempAverageList);
        }
        log.debug("Finished average response metrics calculation successfully.");
        return averageResponseMetricsMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BigDecimal> getSessionCountMetrics() throws OpenBankingException {

        log.debug("Starting session count metrics calculation.");
        JSONObject sessionCountMetricsJsonObject = metricsDataProvider.getSessionCountMetricsData();
        if (sessionCountMetricsJsonObject != null) {
            List<BigDecimal> sessionCountMetricsList = MetricsProcessorUtil.getPopulatedMetricsList(
                    sessionCountMetricsJsonObject, numberOfDays, metricsCountLastDateEpoch);
            log.debug("Finished session count metrics calculation successfully.");
            return sessionCountMetricsList;
        } else {
            throw new OpenBankingException(String.format(NO_DATA_ERROR, MetricsConstants.SESSION_COUNT));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BigDecimal> getAverageTPSMetrics(Map<PriorityEnum, List<BigDecimal>> invocationMetricsMap) {

        log.debug("Starting average TPS metrics calculation.");
        List<BigDecimal> averageTPSList = new ArrayList<>();
        List<BigDecimal> totalInvocationList = MetricsProcessorUtil.getTotalInvocationsForEachDay(invocationMetricsMap);
        for (BigDecimal transactionCount : totalInvocationList) {
            BigDecimal avgTPS = transactionCount.divide(MetricsConstants.SECONDS_IN_DAY, 3, RoundingMode.HALF_UP);
            if (avgTPS.compareTo(BigDecimal.ZERO) == 0) {
                averageTPSList.add(BigDecimal.valueOf(0).setScale(3, RoundingMode.HALF_UP));
            } else {
                averageTPSList.add(avgTPS);
            }
        }
        log.debug("Finished average TPS metrics calculation successfully.");
        return averageTPSList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BigDecimal> getPeakTPSMetrics() throws OpenBankingException {

        try {
            log.debug("Starting peak TPS metrics calculation.");
            List<BigDecimal> peakTPSList;
            JSONArray peakTPSData = metricsDataProvider.getPeakTPSMetricsData();
            Map<AspectEnum, List<BigDecimal>> peakTPSDataMap = MetricsProcessorUtil.getPeakTPSMapFromJsonArray(
                    peakTPSData, numberOfDays, metricsCountLastDateEpoch);
            peakTPSList = peakTPSDataMap.get(AspectEnum.ALL);
            log.debug("Finished peak TPS metrics calculation successfully.");
            return peakTPSList;
        } catch (ParseException e) {
            throw new OpenBankingException("Error occurred while parsing peak TPS Json Array", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BigDecimal> getErrorMetrics() throws OpenBankingException {

        log.debug("Starting error metrics calculation.");
        JSONObject errorMetricsJsonObject = metricsDataProvider.getErrorMetricsData();
        if (errorMetricsJsonObject != null) {
            List<BigDecimal> errorMetricsList = MetricsProcessorUtil.getPopulatedMetricsList(errorMetricsJsonObject,
                    numberOfDays, metricsCountLastDateEpoch);
            log.debug("Finished error metrics calculation successfully.");
            return errorMetricsList;
        } else {
            throw new OpenBankingException(String.format(NO_DATA_ERROR, MetricsConstants.ERROR));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<AspectEnum, List<BigDecimal>> getRejectionMetrics() throws OpenBankingException {

        log.debug("Starting rejection metrics calculation.");
        JSONObject rejectionMetricsJsonObject = metricsDataProvider.getRejectionMetricsData();
        Map<AspectEnum, List<BigDecimal>> rejectionMetricsMap = new HashMap<>();
        ArrayList<ArrayList<BigDecimal>> rejectedInvocationMetricsList = new ArrayList<ArrayList<BigDecimal>>(2);

        if (rejectionMetricsJsonObject != null) {
            rejectedInvocationMetricsList.addAll(MetricsProcessorUtil.getListFromRejectionsJson(
                    rejectionMetricsJsonObject, numberOfDays, metricsCountLastDateEpoch));
            rejectionMetricsMap.put(AspectEnum.AUTHENTICATED, rejectedInvocationMetricsList.get(0));
            rejectionMetricsMap.put(AspectEnum.UNAUTHENTICATED, rejectedInvocationMetricsList.get(1));
        } else {
            throw new OpenBankingException(String.format(NO_DATA_ERROR, MetricsConstants.REJECTION));
        }
        log.debug("Finished rejection metrics calculation successfully.");
        return rejectionMetricsMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRecipientCountMetrics() throws OpenBankingException {

        log.debug("Starting recipient count metrics calculation.");
        JSONObject recipientCountMetricsJsonObject = metricsDataProvider.getRecipientCountMetricsData();
        if (recipientCountMetricsJsonObject != null) {
            int recipientCount = MetricsProcessorUtil.getLastElementValueFromJsonObject(
                    recipientCountMetricsJsonObject);
            log.debug("Finished recipient count metrics calculation successfully.");
            return recipientCount;
        } else {
            throw new OpenBankingException(String.format(NO_DATA_ERROR, MetricsConstants.RECIPIENT_COUNT));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCustomerCountMetrics() throws OpenBankingException {

        log.debug("Starting customer count metrics calculation.");
        JSONObject customerCountMetricsJsonObject = metricsDataProvider.getCustomerCountMetricsData();
        if (customerCountMetricsJsonObject != null) {
            int customerCount = MetricsProcessorUtil.getLastElementValueFromJsonObject(customerCountMetricsJsonObject);
            log.debug("Finished customer count metrics calculation successfully.");
            return customerCount;
        } else {
            throw new OpenBankingException(String.format(NO_DATA_ERROR, MetricsConstants.CUSTOMER_COUNT));
        }
    }

    /**
     * Get map of total response times by priority.
     * Used to calculate average response time metrics.
     *
     * @return map of total response times by priority
     * @throws OpenBankingException - OpenBankingException
     */
    private Map<PriorityEnum, List<BigDecimal>> getTotalResponseTimeMap() throws OpenBankingException {

        log.debug("Starting total response time calculation.");
        JSONObject totalResponseTimeJsonObject = metricsDataProvider.getTotalResponseTimeMetricsData();
        if (totalResponseTimeJsonObject != null) {
            Map<PriorityEnum, List<BigDecimal>> totalResponseTimeMap = MetricsProcessorUtil.
                    getPopulatedTotalResponseTimeMetricsMap(totalResponseTimeJsonObject, numberOfDays,
                            metricsCountLastDateEpoch);
            log.debug("Finished total response time calculation successfully.");
            return totalResponseTimeMap;
        } else {
            throw new OpenBankingException(String.format(NO_DATA_ERROR, MetricsConstants.TOTAL_RESPONSE_TIME));
        }
    }

    /**
     * Get list of successful invocations.
     * Used to calculate performance metrics.
     *
     * @return list of successful invocations
     * @throws OpenBankingException - OpenBankingException
     */
    public List<BigDecimal> getSuccessfulInvocations() throws OpenBankingException {

        log.debug("Starting successful invocations calculation.");
        JSONObject successInvocationsJsonObject = metricsDataProvider.getSuccessfulInvocationMetricsData();
        if (successInvocationsJsonObject != null) {
            List<BigDecimal> successInvocationsList = MetricsProcessorUtil.getPopulatedMetricsList(
                    successInvocationsJsonObject, numberOfDays, metricsCountLastDateEpoch);
            log.debug("Finished total successful invocation calculation successfully.");
            return successInvocationsList;
        } else {
            throw new OpenBankingException(String.format(NO_DATA_ERROR, MetricsConstants.SUCCESSFUL_INVOCATIONS));
        }
    }

}