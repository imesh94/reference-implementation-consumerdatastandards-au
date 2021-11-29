/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.metrics.util;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.metrics.constants.MetricsConstants;
import com.wso2.openbanking.cds.metrics.model.ServerOutageDataModel;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains utility methods for calculating metrics.
 */
public class MetricsProcessorUtil {

    private static final Log log = LogFactory.getLog(MetricsProcessorUtil.class);

    private MetricsProcessorUtil() {

    }

    /**
     * Compose invocation metrics data per priority tier and day.
     *
     * @param period - PeriodEnum
     * @return - invocation metrics map
     * @throws OpenBankingException - OpenBankingException
     */
    public static Map<PriorityEnum, List<BigDecimal>> getInvocationMetrics(PeriodEnum period)
            throws OpenBankingException {

        Map<PriorityEnum, List<BigDecimal>> invocationMetricsMap;

        List<BigDecimal> unauthenticatedList = getInvocationMetricsByPriority(PriorityEnum.UNAUTHENTICATED, period);
        List<BigDecimal> highPriorityList = getInvocationMetricsByPriority(PriorityEnum.HIGH_PRIORITY, period);
        List<BigDecimal> lowPriorityList = getInvocationMetricsByPriority(PriorityEnum.LOW_PRIORITY, period);
        List<BigDecimal> unattendedList = getInvocationMetricsByPriority(PriorityEnum.UNATTENDED, period);
        List<BigDecimal> largePayloadList = getInvocationMetricsByPriority(PriorityEnum.LARGE_PAYLOAD, period);

        invocationMetricsMap = getMetricsMap(unauthenticatedList, highPriorityList, lowPriorityList, unattendedList,
                largePayloadList);

        return invocationMetricsMap;
    }

    /**
     * Compose average response time metrics data per priority tier and day.
     *
     * @param period - PeriodEnum
     * @return - average response time map
     * @throws OpenBankingException - OpenBankingException
     */
    public static Map<PriorityEnum, List<BigDecimal>> getAverageResponseMetrics(
            Map<PriorityEnum, List<BigDecimal>> invocationMetricsMap, PeriodEnum period)
            throws OpenBankingException {

        Map<PriorityEnum, List<BigDecimal>> totalResponseTimeMetricsMap = getTotalResponseTimeMetrics(period);
        Map<PriorityEnum, List<BigDecimal>> averageResponseMetricsMap = new HashMap<>();

        for (PriorityEnum priority : PriorityEnum.values()) {
            List<BigDecimal> responseTimeList = totalResponseTimeMetricsMap.get(priority);
            List<BigDecimal> invocationCountList = invocationMetricsMap.get(priority);
            List<BigDecimal> tempAverageList = divideList(responseTimeList, invocationCountList);
            averageResponseMetricsMap.put(priority, tempAverageList);
        }
        return averageResponseMetricsMap;
    }

    /**
     * Compose total response time metrics data per priority tier and day.
     *
     * @param period - PeriodEnum
     * @return - total response time map
     * @throws OpenBankingException - OpenBankingException
     */
    private static Map<PriorityEnum, List<BigDecimal>> getTotalResponseTimeMetrics(PeriodEnum period)
            throws OpenBankingException {

        Map<PriorityEnum, List<BigDecimal>> totalResponseTimeMetricsMap;

        List<BigDecimal> unauthenticatedList = getTotalResponseTimeMetricsByPriority(
                PriorityEnum.UNAUTHENTICATED, period);
        List<BigDecimal> highPriorityList = getTotalResponseTimeMetricsByPriority(PriorityEnum.HIGH_PRIORITY, period);
        List<BigDecimal> lowPriorityList = getTotalResponseTimeMetricsByPriority(PriorityEnum.LOW_PRIORITY, period);
        List<BigDecimal> unattendedList = getTotalResponseTimeMetricsByPriority(PriorityEnum.UNATTENDED, period);
        List<BigDecimal> largePayloadList = getTotalResponseTimeMetricsByPriority(PriorityEnum.LARGE_PAYLOAD, period);

        totalResponseTimeMetricsMap = getMetricsMap(unauthenticatedList, highPriorityList, lowPriorityList,
                unattendedList, largePayloadList);

        return totalResponseTimeMetricsMap;
    }

    /**
     * Get invocation metrics according to the given priority and day range.
     *
     * @param priority - priority tier
     * @param period   - PeriodEnum
     * @return - list of invocation metrics
     * @throws OpenBankingException - OpenBankingException
     */
    private static List<BigDecimal> getInvocationMetricsByPriority(PriorityEnum priority, PeriodEnum period)
            throws OpenBankingException {

        String spQuery;
        JSONObject invocationMetricsJsonObject;
        List<BigDecimal> invocationMetricsList = new ArrayList<>();

        // execute queries in the stream processor
        try {
            //current day metrics
            if (PeriodEnum.CURRENT == period || PeriodEnum.ALL == period) {
                log.debug("Retrieving invocation metrics for the current day.");
                spQuery = SPQueryCreatorUtil.getCurrentInvocationsQuery(priority);
                invocationMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                        MetricsConstants.CDS_INVOCATION_METRICS_APP, spQuery);
                if (invocationMetricsJsonObject != null) {
                    invocationMetricsList.add(SPJsonProcessorUtil.getSumFromJsonObject(invocationMetricsJsonObject));
                } else {
                    log.error(String.format("Error occurred while retrieving invocation metrics for the current day " +
                            "using the query: %s", spQuery));
                    throw new OpenBankingException("Null value returned after executing query for retrieving api" +
                            "invocation data for current day on Stream Processor");
                }
            }
            // historic metrics
            if (PeriodEnum.HISTORIC == period || PeriodEnum.ALL == period) {
                log.debug("Retrieving invocation metrics for past seven days.");
                spQuery = SPQueryCreatorUtil.getHistoricInvocationsQuery(priority);
                invocationMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                        MetricsConstants.CDS_INVOCATION_METRICS_APP, spQuery);
                if (invocationMetricsJsonObject != null) {
                    invocationMetricsList.addAll(SPJsonProcessorUtil.getListFromJsonObject(
                            invocationMetricsJsonObject));
                } else {
                    log.error(String.format("Error occurred while retrieving invocation metrics for the past 7 days " +
                            "using the query: %s", spQuery));
                    throw new OpenBankingException("Null value returned after executing query for retrieving api " +
                            "invocation data for the past 7 days on Stream Processor");
                }
            }
        } catch (ParseException | IOException e) {
            throw new OpenBankingException("Error occurred while retrieving api invocation data", e);
        }
        return invocationMetricsList;
    }

    /**
     * Get response time metrics according to the given priority and day range.
     *
     * @param priority - priority tier
     * @return - list of average response time metrics
     * @throws OpenBankingException - OpenBankingException
     */
    private static List<BigDecimal> getTotalResponseTimeMetricsByPriority(PriorityEnum priority, PeriodEnum period)
            throws OpenBankingException, OpenBankingException {

        String spQuery;
        JSONObject totalResponseMetricsJsonObject;
        List<BigDecimal> totalResponseMetricsList = new ArrayList<>();
        List<BigDecimal> totalResponseMetricsListSeconds = new ArrayList<>();
        BigDecimal responseTimeForDay;

        // execute queries in the stream processor
        try {
            //current day metrics
            if (PeriodEnum.CURRENT == period || PeriodEnum.ALL == period) {
                log.debug("Retrieving total response time metrics for the current day.");
                spQuery = SPQueryCreatorUtil.getCurrentTotalResponseQuery(priority);
                totalResponseMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                        MetricsConstants.CDS_INVOCATION_METRICS_APP, spQuery);
                if (totalResponseMetricsJsonObject != null) {
                    responseTimeForDay = SPJsonProcessorUtil.getSumFromJsonObject(totalResponseMetricsJsonObject);
                    // convert to seconds
                    totalResponseMetricsListSeconds.add(responseTimeForDay.divide(BigDecimal.valueOf(1000), 3,
                            RoundingMode.HALF_UP));
                } else {
                    log.error(String.format("Error occurred while retrieving total response time metrics for the " +
                            "current day " +
                            "using the query: %s", spQuery));
                    throw new OpenBankingException("Null value returned after executing query for retrieving response" +
                            " time data for current day on Stream Processor");
                }
            }
            // historic metrics
            if (PeriodEnum.HISTORIC == period || PeriodEnum.ALL == period) {
                log.debug("Retrieving total response time metrics for past seven days.");
                spQuery = SPQueryCreatorUtil.getHistoricTotalResponseQuery(priority);
                totalResponseMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                        MetricsConstants.CDS_INVOCATION_METRICS_APP, spQuery);
                if (totalResponseMetricsJsonObject != null) {
                    totalResponseMetricsList.addAll(SPJsonProcessorUtil.getListFromJsonObject(
                            totalResponseMetricsJsonObject));

                    // convert to seconds
                    BigDecimal responseTimeInSeconds;
                    for (BigDecimal responseTime : totalResponseMetricsList) {
                        responseTimeInSeconds = responseTime.divide(BigDecimal.valueOf(1000), 3,
                                RoundingMode.HALF_UP);
                        totalResponseMetricsListSeconds.add(responseTimeInSeconds);
                    }
                } else {
                    log.error(String.format("Error occurred while retrieving total response time metrics for the " +
                            "past 7 days using the query: %s", spQuery));
                    throw new OpenBankingException("Null value returned after executing query for retrieving response" +
                            " time data for past 7 days on Stream Processor");
                }
            }
        } catch (ParseException | IOException e) {
            throw new OpenBankingException("Error occurred while retrieving total response time data", e);
        }
        return totalResponseMetricsListSeconds;
    }

    /**
     * Get performance metrics.
     *
     * @param period               - Perion Enum
     * @param totalInvocationsList - List of total invocations for each
     * @return - performance metrics list
     * @throws OpenBankingException - OpenBankingException
     */
    public static List<BigDecimal> getPerformanceMetrics(PeriodEnum period, List<BigDecimal> totalInvocationsList)
            throws OpenBankingException {

        List<BigDecimal> successInvocationsList = getSuccessInvocationsList(period);
        return divideList(successInvocationsList, totalInvocationsList);
    }

    /**
     * Get total successful api invocations for each day.
     *
     * @return - list of success invocation metrics
     * @throws OpenBankingException - OpenBankingException
     */
    private static List<BigDecimal> getSuccessInvocationsList(PeriodEnum period)
            throws OpenBankingException {

        String spQuery;
        JSONObject successInvocationsJsonObject;
        List<BigDecimal> successInvocationsList = new ArrayList<>();

        // execute queries in the stream processor
        try {
            //current day metrics
            if (PeriodEnum.CURRENT == period || PeriodEnum.ALL == period) {
                log.debug("Retrieving successful invocations metrics for the current day.");
                spQuery = SPQueryCreatorUtil.getCurrentSuccessInvocationsQuery();
                successInvocationsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                        MetricsConstants.CDS_INVOCATION_METRICS_APP, spQuery);
                if (successInvocationsJsonObject != null) {
                    successInvocationsList.add(SPJsonProcessorUtil.getSumFromJsonObject(
                            successInvocationsJsonObject));
                } else {
                    log.error(String.format("Error occurred while retrieving successful api invocation metrics for " +
                            "the current day using the query: %s", spQuery));
                    throw new OpenBankingException("Null value returned after executing query for retrieving " +
                            "successful api invocation data for current day on Stream Processor");
                }
            }
            // historic metrics
            if (PeriodEnum.HISTORIC == period || PeriodEnum.ALL == period) {
                log.debug("Retrieving successful invocations metrics for past seven days.");
                spQuery = SPQueryCreatorUtil.getHistoricSuccessInvocationsQuery();
                successInvocationsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                        MetricsConstants.CDS_INVOCATION_METRICS_APP, spQuery);
                if (successInvocationsJsonObject != null) {
                    successInvocationsList.addAll(SPJsonProcessorUtil.getListFromJsonObject(
                            successInvocationsJsonObject));
                } else {
                    log.error(String.format("Error occurred while retrieving successful api invocation metrics for " +
                            "past 7 days using the query: %s", spQuery));
                    throw new OpenBankingException("Null value returned after executing query for retrieving " +
                            "successful api invocation data for past 7 days on Stream Processor");
                }
            }
        } catch (ParseException | IOException e) {
            throw new OpenBankingException("Error occurred while retrieving successful api invocation count", e);
        }
        return successInvocationsList;
    }

    /**
     * Get api invocation count with errors.
     *
     * @return - list of error invocation metrics
     * @throws OpenBankingException - OpenBankingException
     */
    public static List<BigDecimal> getErrorInvocationMetrics(PeriodEnum period)
            throws OpenBankingException {

        String spQuery;
        JSONObject errorInvocationMetricsJsonObject;
        List<BigDecimal> errorInvocationMetricsList = new ArrayList<>();

        // execute queries in the stream processor
        try {
            //current day metrics
            if (PeriodEnum.CURRENT == period || PeriodEnum.ALL == period) {
                log.debug("Retrieving error invocations metrics for the current day.");
                spQuery = SPQueryCreatorUtil.getCurrentErrorInvocationsQuery();
                errorInvocationMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                        MetricsConstants.CDS_INVOCATION_METRICS_APP, spQuery);
                if (errorInvocationMetricsJsonObject != null) {
                    errorInvocationMetricsList.add(SPJsonProcessorUtil.getSumFromJsonObject(
                            errorInvocationMetricsJsonObject));
                } else {
                    log.error(String.format("Error occurred while retrieving erroneous api invocation metrics for " +
                            "the current day using the query: %s", spQuery));
                    throw new OpenBankingException("Null value returned after executing query for retrieving " +
                            "erroneous api invocation data for current day on Stream Processor");
                }
            }
            // historic metrics
            if (PeriodEnum.HISTORIC == period || PeriodEnum.ALL == period) {
                log.debug("Retrieving error invocations metrics for past seven days.");
                spQuery = SPQueryCreatorUtil.getHistoricErrorInvocationsQuery();
                errorInvocationMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                        MetricsConstants.CDS_INVOCATION_METRICS_APP, spQuery);
                if (errorInvocationMetricsJsonObject != null) {
                    errorInvocationMetricsList.addAll(SPJsonProcessorUtil.getListFromJsonObject(
                            errorInvocationMetricsJsonObject));
                } else {
                    log.error(String.format("Error occurred while retrieving erroneous api invocation metrics for " +
                            "past 7 days using the query: %s", spQuery));
                    throw new OpenBankingException("Null value returned after executing query for retrieving " +
                            "erroneous api invocation data for past 7 days on Stream Processor");
                }
            }
        } catch (ParseException | IOException e) {
            throw new OpenBankingException("Error occurred while retrieving erroneous api invocation count", e);
        }
        return errorInvocationMetricsList;
    }

    /**
     * Get rejected api invocation count due to throttling out.
     *
     * @return - list of rejected invocation metrics
     * @throws OpenBankingException - OpenBankingException
     */
    public static List<BigDecimal> getRejectedInvocationMetrics(PeriodEnum period, String authentication)
            throws OpenBankingException {

        String spQuery;
        JSONObject rejectedInvocationMetricsJsonObject;
        List<BigDecimal> rejectedInvocationMetricsList = new ArrayList<>();
        List<BigDecimal> rejectedInvocationMetricsListCurrent = new ArrayList<>();
        ArrayList<ArrayList<BigDecimal>> rejectedInvocationMetricsListHistoric =
                new ArrayList<ArrayList<BigDecimal>>(2);

        // execute queries in the stream processor
        try {
            //current day metrics
            if (PeriodEnum.CURRENT == period || PeriodEnum.ALL == period) {
                log.debug("Retrieving rejected invocations metrics for the current day.");
                spQuery = SPQueryCreatorUtil.getCurrentRejectionsQuery();
                rejectedInvocationMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                        MetricsConstants.API_RAW_DATA_SUBMISSION_APP, spQuery);
                if (rejectedInvocationMetricsJsonObject != null) {
                    rejectedInvocationMetricsListCurrent.addAll(SPJsonProcessorUtil.getSumFromJsonObjectRejection(
                            rejectedInvocationMetricsJsonObject));
                } else {
                    log.error(String.format("Error occurred while retrieving rejected api invocation metrics for " +
                            "the current day using the query: %s", spQuery));
                    throw new OpenBankingException("Null value returned after executing query for retrieving " +
                            "rejected api invocation data for current day on Stream Processor");
                }

                //Authenticated user
                if (MetricsConstants.CDS_REJECTION_METRICS_APP_AUTHENTICATED.equals(authentication)) {
                    rejectedInvocationMetricsList.add(0, rejectedInvocationMetricsListCurrent.get(0));
                    //Unauthenticated user
                } else if (MetricsConstants.CDS_REJECTION_METRICS_APP_UNAUTHENTICATED.equals(authentication)) {
                    rejectedInvocationMetricsList.add(0, rejectedInvocationMetricsListCurrent.get(1));
                }
            }
            // historic metrics
            if (PeriodEnum.HISTORIC == period || PeriodEnum.ALL == period) {
                log.debug("Retrieving rejected invocations metrics for past seven days.");
                spQuery = SPQueryCreatorUtil.getHistoricRejectionsQuery();
                rejectedInvocationMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                        MetricsConstants.API_RAW_DATA_SUBMISSION_APP, spQuery);
                if (rejectedInvocationMetricsJsonObject != null) {
                    rejectedInvocationMetricsListHistoric.addAll(SPJsonProcessorUtil.getListFromJsonObjectRejection(
                            rejectedInvocationMetricsJsonObject));
                } else {
                    log.error(String.format("Error occurred while retrieving rejected api invocation metrics for " +
                            "past 7 days using the query: %s", spQuery));
                    throw new OpenBankingException("Null value returned after executing query for retrieving " +
                            "rejected api invocation data for past 7 days on Stream Processor");
                }

                if (PeriodEnum.HISTORIC == period) {
                    //Authenticated user
                    if (MetricsConstants.CDS_REJECTION_METRICS_APP_AUTHENTICATED.equals(authentication)) {
                        rejectedInvocationMetricsList.addAll(0, rejectedInvocationMetricsListHistoric.get(0));
                        //Unauthenticated user
                    } else if (MetricsConstants.CDS_REJECTION_METRICS_APP_UNAUTHENTICATED.equals(authentication)) {
                        rejectedInvocationMetricsList.addAll(0, rejectedInvocationMetricsListHistoric.get(1));
                    }
                } else if (PeriodEnum.ALL == period) {
                    if (MetricsConstants.CDS_REJECTION_METRICS_APP_AUTHENTICATED.equals(authentication)) {
                        rejectedInvocationMetricsList.addAll(1, rejectedInvocationMetricsListHistoric.get(0));
                        //Unauthenticated user
                    } else if (MetricsConstants.CDS_REJECTION_METRICS_APP_UNAUTHENTICATED.equals(authentication)) {
                        rejectedInvocationMetricsList.addAll(1, rejectedInvocationMetricsListHistoric.get(1));
                    }
                }
            }
        } catch (ParseException | IOException e) {
            throw new OpenBankingException("Error occurred while retrieving rejected api invocation count", e);
        }
        return rejectedInvocationMetricsList;
    }

    /**
     * Calculate average TPS for a day.
     * Used formula: averageTPS = (total no. of transactions for a day / no. of seconds in a day)
     *
     * @param totalTransactionsList - list of total transactions for each day.
     * @return - list of average TPS
     */
    public static List<BigDecimal> getAverageTPSMetrics(List<BigDecimal> totalTransactionsList) {

        List<BigDecimal> averageTPSList = new ArrayList<>();
        // Iterate each day
        for (BigDecimal transactionCount : totalTransactionsList) {
            BigDecimal avgTPS = transactionCount.divide(MetricsConstants.SECONDS_IN_DAY, 3, RoundingMode.HALF_UP);
            if (avgTPS.compareTo(BigDecimal.ZERO) == 0) {
                averageTPSList.add(BigDecimal.valueOf(0));
            } else {
                averageTPSList.add(avgTPS);
            }
        }
        return averageTPSList;
    }

    /**
     * Get session count metrics for the given range of days.
     *
     * @return - list of session counts
     * @throws OpenBankingException - OpenBankingException
     */
    public static List<BigDecimal> getSessionCountMetrics(PeriodEnum period) throws OpenBankingException {

        List<BigDecimal> sessionCountList = new ArrayList<>();
        JSONObject sessionCountMetricsJsonObject;
        String spQuery;

        // execute queries in the stream processor
        try {
            //current day metrics
            if (PeriodEnum.CURRENT == period || PeriodEnum.ALL == period) {
                log.debug("Retrieving session data for the current day.");
                spQuery = SPQueryCreatorUtil.getCurrentSessionCountQuery();
                sessionCountMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                        MetricsConstants.CDS_SESSION_METRICS_APP, spQuery);
                if (sessionCountMetricsJsonObject != null) {
                    sessionCountList.add(SPJsonProcessorUtil.getSumFromJsonObject(
                            sessionCountMetricsJsonObject));
                } else {
                    log.error(String.format("Error occurred while retrieving session count metrics for " +
                            "the current day using the query: %s", spQuery));
                    throw new OpenBankingException("Null value returned after executing query for retrieving " +
                            "session count data for current day on Stream Processor");
                }
            }
            // historic metrics
            if (PeriodEnum.HISTORIC == period || PeriodEnum.ALL == period) {
                log.debug("Retrieving session count metrics for past seven days.");
                spQuery = SPQueryCreatorUtil.getHistoricSessionCountQuery();
                sessionCountMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                        MetricsConstants.CDS_SESSION_METRICS_APP, spQuery);
                if (sessionCountMetricsJsonObject != null) {
                    sessionCountList.addAll(SPJsonProcessorUtil.getListFromJsonObject(
                            sessionCountMetricsJsonObject));
                } else {
                    log.error(String.format("Error occurred while retrieving session count metrics for " +
                            "the past 7 days using the query: %s", spQuery));
                    throw new OpenBankingException("Null value returned after executing query for retrieving " +
                            "session count data for past 7 days on Stream Processor");
                }
            }
        } catch (ParseException | IOException e) {
            throw new OpenBankingException("Error occurred while retrieving session count data", e);
        }
        return sessionCountList;
    }

    /**
     * Get peak TPS metrics for the given range of days.
     *
     * @return - list of peak TPS
     * @throws OpenBankingException - OpenBankingException
     */
    public static List<BigDecimal> getPeakTPSMetrics(PeriodEnum period) throws OpenBankingException {

        List<BigDecimal> peakTPSList = new ArrayList<>();
        JSONObject tpsMetricsJsonObject;
        String spQuery;

        // execute queries in the stream processor
        try {
            //current day metrics
            if (PeriodEnum.CURRENT == period || PeriodEnum.ALL == period) {
                log.debug("Retrieving TPS data for the current day.");
                spQuery = SPQueryCreatorUtil.getCurrentTPSQuery();
                tpsMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                        MetricsConstants.API_RAW_DATA_SUBMISSION_APP, spQuery);
                if (tpsMetricsJsonObject != null) {
                    peakTPSList.add(SPJsonProcessorUtil.getMaxFromJsonObject(
                            tpsMetricsJsonObject));
                } else {
                    log.error(String.format("Error occurred while retrieving peak TPS metrics for " +
                            "the current day using the query: %s", spQuery));
                    throw new OpenBankingException("Null value returned after executing query for retrieving " +
                            "peak TPS data for current day on Stream Processor");
                }
            }
            // historic metrics
            if (PeriodEnum.HISTORIC == period || PeriodEnum.ALL == period) {
                log.debug("Retrieving peak TPS metrics for past seven days.");
                spQuery = SPQueryCreatorUtil.getHistoricPeakTPSQuery();
                tpsMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                        MetricsConstants.CDS_INVOCATION_METRICS_APP, spQuery);
                if (tpsMetricsJsonObject != null) {
                    peakTPSList.addAll(SPJsonProcessorUtil.getListFromJsonObject(
                            tpsMetricsJsonObject));
                } else {
                    log.error(String.format("Error occurred while retrieving peak TPS metrics for " +
                            "the past 7 days using the query: %s", spQuery));
                    throw new OpenBankingException("Null value returned after executing query for retrieving " +
                            "peak TPS data for past 7 days on Stream Processor");
                }
            }
        } catch (ParseException | IOException e) {
            throw new OpenBankingException("Error occurred while retrieving peak TPS data", e);
        }
        return peakTPSList;
    }

    /**
     * Get recipient count metrics.
     *
     * @return - number of active data recipients
     * @throws OpenBankingException - OpenBankingException
     */
    public static int getRecipientCountMetrics() throws OpenBankingException {

        String spQuery = SPQueryCreatorUtil.getRecipientCountQuery();
        try {
            JSONObject recipientCountJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                    MetricsConstants.CDS_CUSTOMER_RECIPIENT_METRICS_APP, spQuery);
            if (recipientCountJsonObject != null) {
                return SPJsonProcessorUtil.getLastElementValueFromJsonObject(recipientCountJsonObject).intValue();
            } else {
                log.error(String.format("Error occurred while retrieving recipient count metrics using the query: %s"
                        , spQuery));
                throw new OpenBankingException("Null value returned after executing query for retrieving " +
                        "recipient count on Stream Processor");
            }
        } catch (ParseException | IOException e) {
            throw new OpenBankingException("Error occurred while retrieving recipient count", e);
        }
    }

    /**
     * Get customer count metrics.
     *
     * @return - number of consent ids
     * @throws OpenBankingException - OpenBankingException
     */
    public static int getCustomerCountMetrics() throws OpenBankingException {

        String spQuery = SPQueryCreatorUtil.getCustomerCountQuery();
        try {
            JSONObject customerCountJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                    MetricsConstants.CDS_CUSTOMER_RECIPIENT_METRICS_APP, spQuery);
            if (customerCountJsonObject != null) {
                return SPJsonProcessorUtil.getLastElementValueFromJsonObject(customerCountJsonObject).intValue();
            } else {
                log.error(String.format("Error occurred while retrieving customer count metrics using the query: %s",
                        spQuery));
                throw new OpenBankingException("Null value returned after executing query for retrieving " +
                        "customer count on Stream Processor");
            }
        } catch (ParseException | IOException e) {
            throw new OpenBankingException("Error occurred while retrieving customer count", e);

        }
    }

    /**
     * Get availability count metrics.
     *
     * @param periodEnum
     * @return
     */
    public static List<BigDecimal> getAvailabilityMetrics(PeriodEnum periodEnum) throws OpenBankingException {

        String spQuery;
        JSONObject availabilityMetricsJsonObject;
        List<BigDecimal> availabilityMetricsList = new ArrayList<>();
        ZonedDateTime currentDateTime = LocalDateTime.now().atZone(ZoneOffset.UTC);

        // execute queries in the stream processor
        try {
            //current month metrics
            if (PeriodEnum.CURRENT == periodEnum || PeriodEnum.ALL == periodEnum) {
                log.debug("Retrieving availability metrics for the current month.");
                long from = currentDateTime.withHour(0).withMinute(0).withSecond(0).withDayOfMonth(1)
                        .toInstant().toEpochMilli() / 1000;
                long to = currentDateTime.toInstant().toEpochMilli() / 1000;
                spQuery = SPQueryCreatorUtil.getAvailabilityRecordsByTimePeriod(from, to);
                availabilityMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                        MetricsConstants.CDS_AVAILABILITY_METRICS_APP, spQuery);

                if (availabilityMetricsJsonObject != null) {
                    BigDecimal availabilityForCurrentMonth = SPJsonProcessorUtil.getAvailabilityFromServerOutages(
                            mapToServerOutageDataList(availabilityMetricsJsonObject), from, to);
                    availabilityMetricsList.add(availabilityForCurrentMonth);
                } else {
                    log.error(String.format("Error occurred while retrieving availability metrics " +
                            "for the current month using the query: %s", spQuery));
                    throw new OpenBankingException("Null value returned after executing query for retrieving" +
                            "availability data for current month on Stream Processor");
                }
            }
            // historic metrics
            if (PeriodEnum.HISTORIC == periodEnum || PeriodEnum.ALL == periodEnum) {
                log.debug("Retrieving availability metrics for past 12 months.");
                int noOfMonthsForHistory = getMonthsCountForAvailabilityHistoricMetrics(currentDateTime);
                // declaring timestamps for no of historic months
                long[][] timestamps = new long[noOfMonthsForHistory][2];
                for (int month = 1; month < noOfMonthsForHistory + 1; month++) {
                    int lengthOfTheMonth = LocalDate.now().minusMonths(month).lengthOfMonth();
                    long startTimeOfMonth = currentDateTime.minusMonths(month).withHour(0).withMinute(0)
                            .withSecond(0).withDayOfMonth(1).toInstant().toEpochMilli() / 1000;
                    long endTimeOfMonth = currentDateTime.minusMonths(month).withHour(23).withMinute(59)
                            .withSecond(59).withDayOfMonth(lengthOfTheMonth).toInstant().toEpochMilli() / 1000;
                    timestamps[month - 1] = new long[]{startTimeOfMonth, endTimeOfMonth};
                }
                if (noOfMonthsForHistory > 0) {
                    spQuery = SPQueryCreatorUtil.getAvailabilityRecordsByTimePeriod(
                            timestamps[noOfMonthsForHistory - 1][0], timestamps[0][1]);
                    availabilityMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                            MetricsConstants.CDS_AVAILABILITY_METRICS_APP, spQuery);
                    // calculate availability for past months
                    if (availabilityMetricsJsonObject != null) {
                        for (int month = 0; month < noOfMonthsForHistory; month++) {
                            BigDecimal availabilityValues = SPJsonProcessorUtil.getAvailabilityFromServerOutages(
                                    mapToServerOutageDataList(availabilityMetricsJsonObject),
                                    timestamps[month][0], timestamps[month][1]);
                            availabilityMetricsList.add(availabilityValues);
                        }
                    } else {
                        log.error(String.format("Error occurred while retrieving invocation metrics " +
                                "for the past months using the query: %s", spQuery));
                        throw new OpenBankingException("Null value returned after executing query for retrieving" +
                                "availability data for past months on Stream Processor");
                    }
                }
            }
        } catch (ParseException | IOException e) {
            throw new OpenBankingException("Error occurred while retrieving availability data", e);
        }
        return availabilityMetricsList;
    }

    /**
     * Map server outages JSONObject from SP query to list of ServerOutageDataModels
     *
     * @param availabilityMetricsJsonObject
     * @return
     * @throws OpenBankingException
     */
    private static List<ServerOutageDataModel> mapToServerOutageDataList(JSONObject availabilityMetricsJsonObject)
            throws OpenBankingException {

        List<ServerOutageDataModel> serverOutageDataModelList = new ArrayList<>();
        JSONArray records = (JSONArray) availabilityMetricsJsonObject.get("records");

        if (records != null) {
            try {
                for (int recordIndex = 0; recordIndex < records.size(); recordIndex++) {
                    JSONArray serverOutageDateJsonObject = (JSONArray) records.get(recordIndex);
                    ServerOutageDataModel dataModel = getServerOutageDataModel(serverOutageDateJsonObject);
                    serverOutageDataModelList.add(dataModel);
                }
            } catch (RuntimeException e) {
                throw new OpenBankingException("Error occurred while mapping server outage data", e);
            }
        }
        return serverOutageDataModelList;
    }

    /**
     * Map server outage JSONObject to ServerOutageDataModel
     *
     * @param serverOutageDateJsonObject
     * @return
     */
    private static ServerOutageDataModel getServerOutageDataModel(JSONArray serverOutageDateJsonObject) {

        return new ServerOutageDataModel(
                serverOutageDateJsonObject.get(0).toString(),
                Long.parseLong(serverOutageDateJsonObject.get(1).toString()),
                serverOutageDateJsonObject.get(2).toString(),
                Long.parseLong(serverOutageDateJsonObject.get(3).toString()),
                Long.parseLong(serverOutageDateJsonObject.get(4).toString()));
    }

    /**
     * Get no of months to calculate the availability metrics based on the oldest record from server outages data
     *
     * @param currentDateTime
     * @return
     * @throws IOException
     * @throws ParseException
     * @throws OpenBankingException
     */
    private static int getMonthsCountForAvailabilityHistoricMetrics(ZonedDateTime currentDateTime)
            throws IOException, ParseException, OpenBankingException {

        String spQuery;
        JSONObject serverOutageJsonObject;
        int noOfMonthsCount = 0;
        ServerOutageDataModel dataModel;

        spQuery = SPQueryCreatorUtil.getOldestServerOutageRecord();
        serverOutageJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                MetricsConstants.CDS_AVAILABILITY_METRICS_APP, spQuery);
        JSONArray records = (JSONArray) serverOutageJsonObject.get("records");

        if (records != null && records.size() == 1) {
            try {
                JSONArray serverOutageDateJsonObject = (JSONArray) records.get(0);
                dataModel = getServerOutageDataModel(serverOutageDateJsonObject);
                LocalDateTime oldestRecordTime = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(dataModel.getTimeFrom()), ZoneOffset.UTC);
                noOfMonthsCount = (int) ChronoUnit.MONTHS.between(
                        oldestRecordTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0),
                        currentDateTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0));
                if (noOfMonthsCount > 12) {
                    noOfMonthsCount = 12;
                } else if (noOfMonthsCount < 0) {
                    noOfMonthsCount = 0;
                }
            } catch (DateTimeException e) {
                throw new OpenBankingException("Error occurred while temporal calculation on server outage data", e);
            } catch (RuntimeException e) {
                throw new OpenBankingException("Error occurred while mapping server outage data", e);
            }
        }

        return noOfMonthsCount;
    }

    /**
     * Get map of metrics when priority tier lists are given.
     *
     * @param unauthenticatedList - unauthenticated list
     * @param highPriorityList    - high priority list
     * @param lowPriorityList     - low priority list
     * @param unattendedList      - unattended list
     * @param largePayloadList    - large payload list
     * @return
     */
    private static Map<PriorityEnum, List<BigDecimal>> getMetricsMap(List<BigDecimal> unauthenticatedList,
                                                                     List<BigDecimal> highPriorityList,
                                                                     List<BigDecimal> lowPriorityList,
                                                                     List<BigDecimal> unattendedList,
                                                                     List<BigDecimal> largePayloadList) {

        Map<PriorityEnum, List<BigDecimal>> metricsMap = new HashMap<>();

        metricsMap.put(PriorityEnum.UNAUTHENTICATED, unauthenticatedList);
        metricsMap.put(PriorityEnum.HIGH_PRIORITY, highPriorityList);
        metricsMap.put(PriorityEnum.LOW_PRIORITY, lowPriorityList);
        metricsMap.put(PriorityEnum.UNATTENDED, unattendedList);
        metricsMap.put(PriorityEnum.LARGE_PAYLOAD, largePayloadList);

        return metricsMap;
    }

    public static List<BigDecimal> getTotalInvocationsForEachDay(Map<PriorityEnum,
            List<BigDecimal>> invocationMetricsMap) {

        List<BigDecimal> totalTransactionsList = new ArrayList<>();

        // get number of days by list size
        int dayCount = invocationMetricsMap.get(PriorityEnum.UNAUTHENTICATED).size();

        for (int day = 0; day < dayCount; day++) {
            BigDecimal totalTransactions = BigDecimal.valueOf(0);
            List<BigDecimal> currentPriorityList;
            for (PriorityEnum priority : PriorityEnum.values()) {
                currentPriorityList = invocationMetricsMap.get(priority);
                if (!currentPriorityList.isEmpty()) {
                    totalTransactions = totalTransactions.add(currentPriorityList.get(day));
                }
            }
            totalTransactionsList.add(totalTransactions);
        }
        return totalTransactionsList;
    }

    /**
     * Perform division between two lists.
     *
     * @param list1 - dividend
     * @param list2 - divisor
     * @return resulting list
     */
    private static List<BigDecimal> divideList(List<BigDecimal> list1, List<BigDecimal> list2)
            throws OpenBankingException {

        int listSize = list1.size();
        List<BigDecimal> resultList = new ArrayList<>();

        if (listSize != list2.size()) {
            throw new OpenBankingException("Cannot perform division between lists with different sizes");
        }
        BigDecimal currentResult;
        BigDecimal currentDivisor;
        for (int i = 0; i < listSize; i++) {
            currentDivisor = list2.get(i);
            if (!(BigDecimal.valueOf(0).equals(currentDivisor))) {
                currentResult = list1.get(i).divide(currentDivisor, 3, RoundingMode.HALF_UP);
                resultList.add(currentResult);
            } else {
                resultList.add(BigDecimal.valueOf(0));
            }
        }
        return resultList;
    }
}
