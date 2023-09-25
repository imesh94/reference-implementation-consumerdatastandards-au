/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.scheduler.util;

import com.wso2.openbanking.cds.metrics.util.DateTimeUtil;
import com.wso2.openbanking.cds.metrics.util.TimeFormatEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * Metrics API Scheduler Util class
 */
public class MetricsApiSchedulerUtil {

    private static Log log = LogFactory.getLog(MetricsApiSchedulerUtil.class);

    /**
     * Executes the given query in SP.
     *
     * @param appName Name of the siddhi app.
     * @param query   Name of the query
     * @return - JSON object with result
     * @throws IOException    IO Exception.
     * @throws ParseException Parse Exception.
     */
    public static JSONObject executeQueryOnStreamProcessor(String appName, String query)
            throws IOException, ParseException {

        byte[] encodedAuth = Base64.getEncoder()
                .encode((MetricsApiSchedulerConstants.SP_USERNAME + ":" + MetricsApiSchedulerConstants.SP_PASSWORD)
                        .getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth, StandardCharsets.UTF_8.toString());

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(MetricsApiSchedulerConstants.SP_API_HOST +
                MetricsApiSchedulerConstants.SP_API_PATH);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appName", appName);
        jsonObject.put("query", query);
        StringEntity requestEntity = new StringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(requestEntity);
        HttpResponse response;
        try {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Executing query %s on SP", query));
            }
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                String error = String.format("Error while invoking SP rest api : %s %s",
                        response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
                log.error(error);
                return null;
            }
            String responseStr = EntityUtils.toString(entity);
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(responseStr);
        } finally {
            httpPost.releaseConnection();
        }
    }

    /**
     * Return query for retrieving Max TPS data.
     *
     * @return - query string
     */
    public static String getHistoricPeakTPSQuery() {

        String[] timeRangeArray = getTimeGapForDays(7, TimeFormatEnum.EPOCH);

        return String.format("from CDS_METRICS_TPS select MAX_TPS, TIMESTAMP*1000 as TIMESTAMP group by TIMESTAMP, " +
                "MAX_TPS having TIMESTAMP > %s and TIMESTAMP < %s;", timeRangeArray[0], timeRangeArray[1]);
    }

    /**
     * Return query for retrieving oldest records of server outages.
     *
     * @return - query string
     */
    public static String getOldestServerOutageRecord() {

        return ("from SERVER_OUTAGES_RAW_DATA select OUTAGE_ID, TIMESTAMP, TYPE, TIME_FROM, TIME_TO " +
                "order by TIME_FROM asc limit 1;");
    }

    /**
     * Return query for retrieving availability records between given time period.
     *
     * @return - query string
     */
    public static String getAvailabilityRecordsByTimePeriod(Long fromTimestamp, Long toTimestamp) {

        return String.format("from SERVER_OUTAGES_RAW_DATA select OUTAGE_ID, TIMESTAMP, TYPE, TIME_FROM, TIME_TO " +
                        "group by OUTAGE_ID, TIMESTAMP, TYPE, TIME_FROM, TIME_TO having " +
                        "TIME_FROM >= %s AND TIME_TO < %s ;",
                fromTimestamp.toString(), toTimestamp.toString());
    }

    /**
     * Get start and end timestamps according to the given number of days.
     *
     * @param days       - number of days to deduct
     * @param timeFormat - 'standard' or 'epoch' time
     * @return - String array [startTime, endTime]
     */
    private static String[] getTimeGapForDays(int days, TimeFormatEnum timeFormat) {

        String[] timestamps = new String[2];
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(MetricsApiSchedulerConstants.SP_TIMESTAMP_PATTERN);
        formatter.setTimeZone(TimeZone.getTimeZone(MetricsApiSchedulerConstants.TIME_ZONE));

        // reset hour, minutes, seconds and millis
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (days == 0) {
            calendar.add(Calendar.DATE, 1);
        }
        // get current date
        timestamps[1] = TimeFormatEnum.EPOCH.equals(timeFormat) ?
                String.valueOf(calendar.getTimeInMillis() / 1000) : formatter.format(calendar.getTime());

        // set proper day count for current day
        if (days == 0) {
            days = 1;
        }
        // deduct given number of days
        calendar.add(Calendar.DATE, -days);
        timestamps[0] = TimeFormatEnum.EPOCH.equals(timeFormat) ?
                String.valueOf(calendar.getTimeInMillis() / 1000) : formatter.format(calendar.getTime());
        return timestamps;
    }

    /**
     * Convert JSON object returned from SP to a list.
     *
     * @param jsonObject - JSON object
     * @return - total
     */
    public static List<BigDecimal> getListFromJsonObject(JSONObject jsonObject) {

        JSONArray recordsArray = (JSONArray) jsonObject.get(MetricsApiSchedulerConstants.RECORDS);
        ArrayList<BigDecimal> elementList = new ArrayList<>(Arrays.asList(new BigDecimal[7]));
        Collections.fill(elementList, BigDecimal.valueOf(0));

        JSONArray countArray;
        Long currentElement;
        int currentDay;
        for (Object object : recordsArray) {
            countArray = (JSONArray) object;
            currentElement = (Long) (countArray.get(0));
            long currentTimestamp = ((Long) (countArray.get(1))) / 1000;
            currentDay = DateTimeUtil.getDaysAgo(currentTimestamp);
            if (currentDay > 0 && currentDay <= 7) { //allowed range of days
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Adding metrics data for day %s", currentDay));
                }
                elementList.set(currentDay - 1, BigDecimal.valueOf(currentElement)); // elementIndex = day - 1
            }
        }
        return elementList;
    }

    /**
     * Get server availability between given time period from the list of ServerOutages
     *
     * @param serverOutageDataList
     * @param from
     * @param to
     * @return
     */
    public static BigDecimal getAvailabilityFromServerOutages(JSONObject serverOutageDataList, long from, long to) {

        long timeDurationOfReportingPeriod = to - from;
        long totalScheduledOutages = 0L;
        long totalIncidentOutages = 0L;

        JSONArray records = (JSONArray) serverOutageDataList.get("records");

        List<JSONArray> scheduledOutages = new ArrayList<>();
        List<JSONArray> incidentOutages = new ArrayList<>();

        // filter the outages. scheduled vs incidents
        for (Object record : records) {
            JSONArray recordObj = (JSONArray) record;
            long timeFrom = (long) recordObj.get(3);
            String type = (String) recordObj.get(2);
            if (timeFrom >= from && timeFrom < to) {
                if (MetricsApiSchedulerConstants.SCHEDULED_OUTAGE.equals(type)) {
                    scheduledOutages.add(recordObj);
                } else {
                    incidentOutages.add(recordObj);
                }
            }
        }

        // calculate the summation of total time
        totalScheduledOutages = calculateServerOutageTime(scheduledOutages);
        totalIncidentOutages = calculateServerOutageTime(incidentOutages);

        // calculate the availability from total time
        double availability = ((double) timeDurationOfReportingPeriod - (double) totalScheduledOutages
                - (double) totalIncidentOutages) /
                ((double) timeDurationOfReportingPeriod - (double) totalScheduledOutages);

        return BigDecimal.valueOf(availability).setScale(2, RoundingMode.HALF_UP);

    }

    /**
     * Calculate total server outage time from ServerOutageData Object
     *
     * @param serverOutages
     * @return
     */
    private static long calculateServerOutageTime(List<JSONArray> serverOutages) {

        long totalTime = 0;

        List<JSONArray> filteredServerOutages = serverOutages.stream()
                .filter(outage -> (long) outage.get(4) >= (long) outage.get(3))
                .distinct()
                .sorted(Comparator.comparingLong(outage -> (long) outage.get(3)))
                .collect(Collectors.toList());

        if (!filteredServerOutages.isEmpty()) {

            long currentEndTime = 0;

            for (int outageIndex = 0; outageIndex < filteredServerOutages.size(); outageIndex++) {

                JSONArray serverOutage = filteredServerOutages.get(outageIndex);
                long timeFrom = (long) serverOutage.get(3);
                long timeTo = (long) serverOutage.get(4);
                if (timeFrom >= currentEndTime) {
                    // not an overlap
                    totalTime += timeTo - timeFrom;
                    currentEndTime = timeTo;
                } else if (timeTo <= currentEndTime) {
                    // complete overlap = ignore

                } else if (timeTo > currentEndTime) {
                    // overlap
                    totalTime += timeTo - currentEndTime;
                    currentEndTime = timeTo;
                }
            }
        }
        return totalTime;
    }
}