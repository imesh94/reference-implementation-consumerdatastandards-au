/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.periodic.job;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.metrics.cache.MetricsCache;
import com.wso2.openbanking.cds.metrics.constants.MetricsConstants;
import com.wso2.openbanking.cds.metrics.internal.MetricsDataHolder;
import com.wso2.openbanking.cds.metrics.periodic.util.MetricsApiSchedulerUtil;
import com.wso2.openbanking.cds.metrics.util.SPQueryExecutorUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.user.api.UserStoreException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin Api Metrics Aggregation Job
 */
@DisallowConcurrentExecution
public class MetricsAggregationJob implements Job {

    private static final Log log = LogFactory.getLog(MetricsAggregationJob.class);

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDateTime currentDateInLocalTime = LocalDateTime.now();
    private String currentDate = dtf.format(currentDateInLocalTime);
    private OpenBankingCDSConfigParser openBankingCDSConfigParser = OpenBankingCDSConfigParser.getInstance();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        log.info("Executing Admin Api Metrics Aggregation Scheduler Job");

        try {
            // Set tenant domain if already not defined.
            final String tenantDomain = MetricsDataHolder.getInstance()
                    .getRealmService().getTenantManager().getSuperTenantDomain();
            final int tenantId = MetricsDataHolder.getInstance()
                    .getRealmService().getTenantManager().getTenantId(tenantDomain);

            if (CarbonContext.getThreadLocalCarbonContext().getTenantDomain() == null) {
                PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomain);
                PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(tenantId);
            }
            //Aggregate Peak TPS data and store in cache
            aggregatePeakTpsData();

            //Aggregate Availability data and store in cache
            aggregateAvailabilityData();
            log.info("Executing Admin Api Metrics Aggregation Scheduler Job successful!");
        } catch (OpenBankingException e) {
            log.error("Exception while running the Admin Api Metrics Aggregation Scheduler Job", e);
        } catch (IOException | ParseException e) {
            log.error("Exception while executing the queries on Stream Processor", e);
        } catch (UserStoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to retrieve and aggregate Peak TPS data
     *
     * @throws IOException - IOException
     * @throws ParseException - ParseException
     * @throws OpenBankingException - OpenBankingException
     */
    private void aggregatePeakTpsData() throws IOException, ParseException, OpenBankingException {
        String spQuery = MetricsApiSchedulerUtil.getHistoricPeakTPSQuery();
        JSONObject tpsMetricsJsonObject = MetricsApiSchedulerUtil.executeQueryOnStreamProcessor(
                MetricsConstants.CDS_INVOCATION_METRICS_APP, spQuery);
        List<BigDecimal> tpsMetricsList;
        if (tpsMetricsJsonObject != null) {
            tpsMetricsList = MetricsApiSchedulerUtil.getListFromJsonObject(tpsMetricsJsonObject);
        } else {
            log.error(String.format("Error occurred while retrieving peak TPS for the 7 past days using " +
                    "the query: %s", spQuery));
            throw new OpenBankingException("Null value returned after executing query for retrieving" +
                    "availability data for past months on Stream Processor");
        }

        // Add data to cache
        MetricsCache metricsCache = MetricsCache.getInstance();
        metricsCache.addToCache(MetricsCache.getPeakTPSCacheKey(), tpsMetricsList);

        //Store aggregated data in database
        SPQueryExecutorUtil.executeRequestOnStreamProcessor(
                constructMaxTPSEvent(currentDate, tpsMetricsList),
                openBankingCDSConfigParser.getMetricsMaxTpsRetrievalUrl());

    }

    /**
     * Retrieve and aggregate availability data
     *
     * @throws IOException - IOException
     * @throws ParseException - ParseException
     * @throws OpenBankingException - OpenBankingException
     */
    private void aggregateAvailabilityData() throws OpenBankingException, IOException, ParseException {

        List<BigDecimal> availabilityMetricsList = new ArrayList<>();
        String spQuery;
        JSONObject availabilityMetricsJsonObject;
        ZonedDateTime currentDateTime = currentDateInLocalTime.atZone(ZoneOffset.UTC);
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
            spQuery = MetricsApiSchedulerUtil.getAvailabilityRecordsByTimePeriod(
                    timestamps[noOfMonthsForHistory - 1][0], timestamps[0][1]);
            availabilityMetricsJsonObject = MetricsApiSchedulerUtil.executeQueryOnStreamProcessor(
                    MetricsConstants.CDS_AVAILABILITY_METRICS_APP, spQuery);
            // calculate availability for past months
            if (availabilityMetricsJsonObject != null) {
                for (int month = 0; month < noOfMonthsForHistory; month++) {
                    BigDecimal availabilityValues = MetricsApiSchedulerUtil.getAvailabilityFromServerOutages(
                            availabilityMetricsJsonObject, timestamps[month][0], timestamps[month][1]);
                    availabilityMetricsList.add(availabilityValues);
                }
            } else {
                log.error(String.format("Error occurred while retrieving invocation metrics " +
                        "for the past months using the query: %s", spQuery));
                throw new OpenBankingException("Null value returned after executing query for retrieving" +
                        "availability data for past months on Stream Processor");
            }
        }

        // Add data to cache
        MetricsCache metricsCache = MetricsCache.getInstance();
        metricsCache.addToCache(MetricsCache.getAvailabilityCacheKey(), availabilityMetricsList);

        //Store aggregated data in database
        SPQueryExecutorUtil.executeRequestOnStreamProcessor(constructAvailabilityEvent(currentDate,
                availabilityMetricsList), openBankingCDSConfigParser.getMetricsAvailabilityRetrievalUrl());
    }

    /**
     * Get no of months to calculate the availability metrics based on the oldest record from server outages data
     *
     * @param currentDateTime - Current DateTime
     * @throws IOException -    IOException
     * @throws ParseException - ParseException
     * @return months count
     */
    private static int getMonthsCountForAvailabilityHistoricMetrics(ZonedDateTime currentDateTime)
            throws IOException, ParseException, OpenBankingException {

        String spQuery;
        JSONObject serverOutageJsonObject;
        int noOfMonthsCount = 0;

        spQuery = MetricsApiSchedulerUtil.getOldestServerOutageRecord();
        serverOutageJsonObject = MetricsApiSchedulerUtil.executeQueryOnStreamProcessor(
                MetricsConstants.CDS_AVAILABILITY_METRICS_APP, spQuery);
        JSONArray records = (JSONArray) serverOutageJsonObject.get("records");

        if (records != null && records.size() == 1) {
            try {
                JSONArray serverOutageDateJsonObject = (JSONArray) records.get(0);
                LocalDateTime oldestRecordTime = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond((Long) serverOutageDateJsonObject.get(3)), ZoneOffset.UTC);
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
     * Method to construct max Tps data as a JSONObject
     *
     * @param currentDate    - Current Date
     * @param tpsMetricsList - List of TPS metrics
     * @return JSONObject
     */
    private static net.minidev.json.JSONObject constructMaxTPSEvent(
            String currentDate, List<BigDecimal> tpsMetricsList) {
        JSONArray eventsArray = new JSONArray();
        for (int aggregateDate = 0; aggregateDate < tpsMetricsList.size(); aggregateDate++) {
            JSONObject eventObj = new JSONObject();
            eventObj.put(MetricsConstants.DATE_AGGREGATED , currentDate);
            eventObj.put(MetricsConstants.AGG_DATE , aggregateDate);
            eventObj.put(MetricsConstants.MAX_TPS , tpsMetricsList.get(aggregateDate).toString());

            eventsArray.add(eventObj);
        }
        net.minidev.json.JSONObject event = new net.minidev.json.JSONObject();
        event.put(MetricsConstants.TPS_AGG_DATA, eventsArray);
        return event;
    }

    /**
     * Method to construct Availability data as a JSONObject
     *
     * @param currentDate             - Current Date
     * @param availabilityMetricsList - List of availability metrics
     * @return JSONObject
     */
    private static net.minidev.json.JSONObject constructAvailabilityEvent(
            String currentDate, List<BigDecimal> availabilityMetricsList) {
        JSONArray eventsArray = new JSONArray();
        for (int aggregateDate = 0; aggregateDate < availabilityMetricsList.size(); aggregateDate++) {
            JSONObject eventObj = new JSONObject();
            eventObj.put(MetricsConstants.DATE_AGGREGATED, currentDate);
            eventObj.put(MetricsConstants.AGG_MONTH, aggregateDate);
            eventObj.put(MetricsConstants.AVAILABILITY_AGG_DATA,
                    availabilityMetricsList.get(aggregateDate).toString());

            eventsArray.add(eventObj);
        }
        net.minidev.json.JSONObject event = new net.minidev.json.JSONObject();
        event.put(MetricsConstants.AVAILABILITY_DATA, eventsArray);
        return event;
    }
}
