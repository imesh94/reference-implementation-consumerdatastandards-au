/*
 * Copyright (c) 2021-2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.util;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.common.util.Generated;
import com.wso2.openbanking.cds.metrics.constants.MetricsConstants;
import com.wso2.openbanking.cds.metrics.model.ServerOutageDataModel;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wso2.openbanking.cds.metrics.constants.MetricsConstants.RECORDS;

/**
 * Contains utility methods for calculating metrics.
 */
public class MetricsProcessorUtil {

    private MetricsProcessorUtil() {
    }

    /**
     * Perform division between two lists.
     *
     * @param list1 - dividend list
     * @param list2 - divisor list
     * @param <T1> - type of the first list elements
     * @param <T2> - type of the second list elements
     * @return resulting list of BigDecimal
     * @throws OpenBankingException if lists have different sizes or division by zero occurs
     */
    public static <T1, T2> List<BigDecimal> divideLists(List<T1> list1, List<T2> list2) throws OpenBankingException {
        int listSize = list1.size();
        List<BigDecimal> resultList = new ArrayList<>();

        if (listSize != list2.size()) {
            throw new OpenBankingException("Cannot perform division between lists with different sizes");
        }

        for (int i = 0; i < listSize; i++) {
            BigDecimal dividend = convertToBigDecimal(list1.get(i));
            BigDecimal divisor = convertToBigDecimal(list2.get(i));
            if (!divisor.equals(BigDecimal.ZERO)) {
                BigDecimal currentResult = dividend.divide(divisor, 3, RoundingMode.HALF_UP);
                resultList.add(currentResult);
            } else {
                resultList.add(BigDecimal.valueOf(0));
            }
        }
        return resultList;
    }

    /**
     * Convert an object to BigDecimal.
     *
     * @param value - the value to be converted
     * @param <T> - type of the value
     * @return the converted BigDecimal
     * @throws IllegalArgumentException if the value type is not supported
     */
    private static <T> BigDecimal convertToBigDecimal(T value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Integer) {
            return BigDecimal.valueOf((Integer) value);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
        }
    }

    /**
     * Initialize new priority-tier map for Metrics data with a given type.
     *
     * @param numberOfDays - Number of days to initialize
     * @param initialValue - Initial value for the list elements
     * @param <T> - Type of the list elements
     * @return - Priority-tier map initialized for given number of days
     */
    public static <T> Map<PriorityEnum, List<T>> initializeMap(int numberOfDays, T initialValue) {
        Map<PriorityEnum, List<T>> map = new HashMap<>();
        for (PriorityEnum priority : PriorityEnum.values()) {
            map.put(priority, initializeList(numberOfDays, initialValue));
        }
        return map;
    }

    /**
     * Initializes a list with a specified number of items, each set to an initial value.
     *
     * @param <T> The type of elements in the list.
     * @param numberOfItems The number of items to be included in the list.
     * @param initialValue The initial value for each item in the list.
     * @return An ArrayList initialized with the specified number of items, each set to the provided initial value.
     */
    public static <T> ArrayList<T> initializeList(int numberOfItems, T initialValue) {
        return new ArrayList<>(Collections.nCopies(numberOfItems, initialValue));
    }

    /**
     * Get last value from the elements in the JSON object.
     *
     * @param jsonObject - JSON object
     * @return latest value
     */
    public static int getLastElementValueFromJsonObject(JSONObject jsonObject) {

        JSONArray recordsArray = (JSONArray) jsonObject.get(RECORDS);
        if (recordsArray.isEmpty()) {
            return 0;
        }
        JSONArray countArray = (JSONArray) recordsArray.get(recordsArray.size() - 1);
        return Integer.parseInt(countArray.get(0).toString());
    }

    /**
     * Merge priority tiers together to get a single list of invocations for each day.
     *
     * @param invocationMetricsMap - Map of invocation metrics
     * @return - Merged list of invocations
     */
    public static List<Integer> getTotalInvocationsForEachDay(Map<PriorityEnum,
            List<Integer>> invocationMetricsMap) {

        List<Integer> totalTransactionsList = new ArrayList<>();

        // get number of days by list size
        int dayCount = invocationMetricsMap.get(PriorityEnum.UNAUTHENTICATED).size();

        for (int day = 0; day < dayCount; day++) {
            int totalTransactions = 0;
            List<Integer> currentPriorityList;
            for (PriorityEnum priority : PriorityEnum.values()) {
                currentPriorityList = invocationMetricsMap.get(priority);
                if (!currentPriorityList.isEmpty()) {
                    totalTransactions = totalTransactions + currentPriorityList.get(day);
                }
            }
            totalTransactionsList.add(totalTransactions);
        }
        return totalTransactionsList;
    }

    /**
     * Populate a map of invocation metrics data categorized to priority tiers.
     *
     * @param metricsJsonObject         - Json object with invocation metrics
     * @param numberOfDays              - Number of days to consider
     * @param metricsCountLastDateEpoch - Epoch timestamp of the last date that metrics are required
     * @return - populated map
     */
    public static Map<PriorityEnum, List<Integer>> getPopulatedInvocationMetricsMap(
            JSONObject metricsJsonObject, int numberOfDays, long metricsCountLastDateEpoch) {

        Map<PriorityEnum, List<Integer>> dataMap = initializeMap(numberOfDays, 0);
        JSONArray records = (JSONArray) metricsJsonObject.get(RECORDS);
        for (Object recordObj : records) {
            JSONArray record = (JSONArray) recordObj;
            PriorityEnum priority = PriorityEnum.fromValue((String) record.get(0));
            Integer count = (Integer) record.get(1);
            long recordTimestamp = (Long) record.get(2);
            int daysAgo = DateTimeUtil.getDayDifference(recordTimestamp / 1000, metricsCountLastDateEpoch);

            // Number of days ago can be used as the index to insert data to the list
            if (daysAgo >= 0 && daysAgo < numberOfDays) {
                dataMap.get(priority).set(daysAgo, dataMap.get(priority).get(daysAgo) + count);
            }
        }
        return dataMap;
    }

    /**
     * Populate a map of total response time data categorized to priority tiers.
     *
     * @param metricsJsonObject         - Json object with total response metrics
     * @param numberOfDays              - Number of days to consider
     * @param metricsCountLastDateEpoch - Epoch timestamp of the last date that metrics are required
     * @return - populated map
     */
    public static Map<PriorityEnum, List<BigDecimal>> getPopulatedTotalResponseTimeMetricsMap(
            JSONObject metricsJsonObject, int numberOfDays, long metricsCountLastDateEpoch) {

        Map<PriorityEnum, List<BigDecimal>> dataMap = initializeMap(numberOfDays, BigDecimal.ZERO);
        JSONArray records = (JSONArray) metricsJsonObject.get(RECORDS);
        for (Object recordObj : records) {
            JSONArray record = (JSONArray) recordObj;
            PriorityEnum priority = PriorityEnum.fromValue((String) record.get(0));
            BigDecimal count = BigDecimal.valueOf((Double) record.get(1));
            long recordTimestamp = (Long) record.get(2);
            int daysAgo = DateTimeUtil.getDayDifference(recordTimestamp / 1000, metricsCountLastDateEpoch);

            // Number of days ago can be used as the index to insert data to the list
            if (daysAgo >= 0 && daysAgo < numberOfDays) {
                dataMap.get(priority).set(
                        daysAgo, dataMap.get(priority).get(daysAgo).add(count));
            }
        }
        return dataMap;
    }

    /**
     * Populate a list of metrics data.
     * List elements are grouped by days.
     *
     * @param metricsJsonObject - Json object with metrics
     * @return - populated map
     */
    public static List<Integer> getPopulatedMetricsList(
            JSONObject metricsJsonObject, int numberOfDays, long metricsCountLastDateEpoch) {

        List<Integer> dataList = initializeList(numberOfDays, 0);
        JSONArray records = (JSONArray) metricsJsonObject.get(RECORDS);
        for (Object recordObj : records) {
            JSONArray record = (JSONArray) recordObj;
            Integer count = (Integer) record.get(0);
            long recordTimestamp = (Long) record.get(1);
            int daysAgo = DateTimeUtil.getDayDifference(recordTimestamp / 1000, metricsCountLastDateEpoch);

            // Number of days ago can be used as the index to insert data to the list
            if (daysAgo >= 0 && daysAgo < numberOfDays) {
                dataList.set(daysAgo, dataList.get(daysAgo) + count);
            }
        }
        return dataList;
    }

    /**
     * Get list of elements from the JSON object.
     * Groups elements to authenticated and unauthenticated lists.
     *
     * @param jsonObject       - JSON object
     * @param numberOfPastDays - Number of past days to consider
     * @return - List of elements
     */
    @Generated(message = "Excluded from code coverage")
    public static List<ArrayList<Integer>> getListFromRejectionsJson(
            JSONObject jsonObject, int numberOfPastDays, long metricsCountLastDateEpoch) {

        JSONArray recordsArray = (JSONArray) jsonObject.get(RECORDS);
        ArrayList<Integer> elementListAuthenticated = initializeList(numberOfPastDays, 0);
        ArrayList<Integer> elementListUnauthenticated = initializeList(numberOfPastDays, 0);
        populateRejectionLists(recordsArray, elementListAuthenticated, elementListUnauthenticated,
                metricsCountLastDateEpoch);

        // 2 lists for authenticated and unauthenticated elements
        List<ArrayList<Integer>> elementList = new ArrayList<>(2);
        elementList.add(elementListAuthenticated);
        elementList.add(elementListUnauthenticated);

        return elementList;
    }

    /**
     * Populate the lists with elements from the JSON array.
     * Used for rejection metrics where elements need to be grouped as authenticated and unauthenticated.
     *
     * @param recordsArray    - JSON array with elements
     * @param authenticated   - List of authenticated elements
     * @param unauthenticated - List of unauthenticated elements
     */
    private static void populateRejectionLists(JSONArray recordsArray, ArrayList<Integer> authenticated,
                                               ArrayList<Integer> unauthenticated, long metricsCountLastDateEpoch) {

        for (Object object : recordsArray) {
            JSONArray countArray = (JSONArray) object;
            long currentElement = Long.parseLong(countArray.get(0).toString());
            long recordTimestamp = Long.parseLong(countArray.get(1).toString());
            String validity = countArray.get(2).toString();
            int daysAgo = DateTimeUtil.getDayDifference(recordTimestamp, metricsCountLastDateEpoch);

            // Number of days ago can be used as the index to insert data to the list
            Integer currentCount = (int) currentElement;
            if (!MetricsConstants.CDS_REJECTION_METRICS_APP_VALIDITY.equals(validity)) {
                authenticated.set(daysAgo, authenticated.get(daysAgo) + currentCount);
            } else {
                unauthenticated.set(daysAgo, unauthenticated.get(daysAgo) + currentCount);
            }
        }
    }

    /**
     * Get availability from server outages for the given number of months.
     *
     * @param serverOutageDataList - Server Outage Data List
     * @param noOfMonths           - Number of months
     * @return Availability list
     */
    public static List<BigDecimal> getAvailabilityFromServerOutages(
            List<ServerOutageDataModel> serverOutageDataList, int noOfMonths, ZonedDateTime endOfMonth) {

        List<BigDecimal> availabilityList = initializeList(noOfMonths, BigDecimal.ONE);
        ZonedDateTime currentEndOfMonth = endOfMonth;
        ZonedDateTime currentStartOfMonth;

        // Iterate through the months in reverse order
        for (int monthIndex = 0; monthIndex < noOfMonths; monthIndex++) {
            currentStartOfMonth = currentEndOfMonth.withDayOfMonth(1);
            long startTimestamp = currentStartOfMonth.toEpochSecond();
            long endTimestamp = currentEndOfMonth.with(LocalTime.MAX).toEpochSecond();

            BigDecimal availability = getAvailabilityFromServerOutagesForTimeRange(serverOutageDataList, startTimestamp,
                    endTimestamp);
            availabilityList.set(monthIndex, availability); // Set availability for the month using current index
            // Get end date of previous month for the next iteration
            currentEndOfMonth = currentStartOfMonth.minusDays(1);
        }
        return availabilityList;
    }

    /**
     * Get server availability between given time period from the list of ServerOutages.
     *
     * @param serverOutageDataList - Server Outage Data List
     * @param fromTime             - From epoch timestamp
     * @param toTime               - To epoch timestamp
     * @return availability value
     */
    public static BigDecimal getAvailabilityFromServerOutagesForTimeRange(
            List<ServerOutageDataModel> serverOutageDataList, long fromTime, long toTime) {

        long timeDurationOfReportingPeriod = toTime - fromTime;
        long totalScheduledOutages;
        long totalIncidentOutages;

        List<ServerOutageDataModel> scheduledOutages = new ArrayList<>();
        List<ServerOutageDataModel> incidentOutages = new ArrayList<>();

        // filter the outages. scheduled vs incidents
        for (ServerOutageDataModel dataModel : serverOutageDataList) {
            if (dataModel.getTimeFrom() >= fromTime && dataModel.getTimeFrom() < toTime) {
                if (MetricsConstants.SCHEDULED_OUTAGE.equals(dataModel.getType())) {
                    scheduledOutages.add(dataModel);
                } else {
                    incidentOutages.add(dataModel);
                }
            }
        }
        // Calculate the summation of total time
        totalScheduledOutages = calculateServerOutageTime(scheduledOutages);
        totalIncidentOutages = calculateServerOutageTime(incidentOutages);

        // Formula to calculate the availability from total time
        double availability = ((double) timeDurationOfReportingPeriod - (double) totalScheduledOutages
                - (double) totalIncidentOutages) /
                ((double) timeDurationOfReportingPeriod - (double) totalScheduledOutages);

        return BigDecimal.valueOf(availability).setScale(3, RoundingMode.HALF_UP);
    }

    /**
     * Calculate total server outage time from ServerOutageDataModel.
     *
     * @param serverOutages - Server Outages
     * @return server outage time
     */
    @Generated(message = "Excluded from code coverage")
    private static long calculateServerOutageTime(List<ServerOutageDataModel> serverOutages) {

        long totalTime = 0;
        List<ServerOutageDataModel> filteredServerOutages = serverOutages.stream()
                .filter(outage -> outage.getTimeTo() >= outage.getTimeFrom())
                .distinct()
                .sorted(Comparator.comparingLong(ServerOutageDataModel::getTimeFrom))
                .collect(Collectors.toList());

        if (!filteredServerOutages.isEmpty()) {
            long currentEndTime = 0;
            for (int outageIndex = 0; outageIndex < filteredServerOutages.size(); outageIndex++) {
                ServerOutageDataModel serverOutage = filteredServerOutages.get(outageIndex);
                if (serverOutage.getTimeFrom() >= currentEndTime) {
                    // Not an overlap
                    totalTime += serverOutage.getTimeTo() - serverOutage.getTimeFrom();
                    currentEndTime = serverOutage.getTimeTo();
                } else if (serverOutage.getTimeTo() <= currentEndTime) {
                    // Complete overlap = ignore
                } else if (serverOutage.getTimeTo() > currentEndTime) {
                    // Overlap
                    totalTime += serverOutage.getTimeTo() - currentEndTime;
                    currentEndTime = serverOutage.getTimeTo();
                }
            }
        }
        return totalTime;
    }

    /**
     * Map server outages JSONObject to list of ServerOutageDataModels.
     *
     * @param availabilityMetricsJsonObject - Availability Metrics JsonObject
     * @return list of server outage data
     */
    public static List<ServerOutageDataModel> getServerOutageDataFromJson(JSONObject availabilityMetricsJsonObject) {

        List<ServerOutageDataModel> serverOutageDataModelList = new ArrayList<>();
        JSONArray records = (JSONArray) availabilityMetricsJsonObject.get(RECORDS);

        if (records != null) {
            for (Object record : records) {
                JSONArray serverOutageDateJsonObject = (JSONArray) record;
                ServerOutageDataModel dataModel = getServerOutageDataModel(serverOutageDateJsonObject);
                serverOutageDataModelList.add(dataModel);
            }
        }
        return serverOutageDataModelList;
    }

    /**
     * Map server outage JSONObject to ServerOutageDataModel.
     * <p>
     * [
     * "outageId",
     * timestamp (epoch seconds),
     * "type" (scheduled/incident),
     * time_from (epoch seconds),
     * time_to (epoch seconds)
     * ]
     *
     * @param serverOutageDateJsonObject - Server Outage Date JsonObject
     * @return ServerOutageDataModel
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
     * Get peak TPS map from the JSON array containing TPS data.
     *
     * @param tpsMetricsJsonArray - JSON array
     * @return - Peak TPS map with aspects and peak TPS for given number of days
     */
    public static Map<AspectEnum, List<BigDecimal>> getPeakTPSMapFromJsonArray(
            JSONArray tpsMetricsJsonArray, int numberOfDays, long metricsCountLastDateEpoch) {

        Map<AspectEnum, List<BigDecimal>> peakTpsMap = new HashMap<>();
        JSONArray authenticatedRecordsArray = getArrayByAspect(tpsMetricsJsonArray, AspectEnum.AUTHENTICATED);
        JSONArray unAuthenticatedRecordsArray = getArrayByAspect(tpsMetricsJsonArray, AspectEnum.UNAUTHENTICATED);

        peakTpsMap.put(AspectEnum.ALL, getPeakTpsList(tpsMetricsJsonArray, numberOfDays, metricsCountLastDateEpoch));
        peakTpsMap.put(AspectEnum.AUTHENTICATED, getPeakTpsList(authenticatedRecordsArray, numberOfDays,
                metricsCountLastDateEpoch));
        peakTpsMap.put(AspectEnum.UNAUTHENTICATED, getPeakTpsList(unAuthenticatedRecordsArray, numberOfDays,
                metricsCountLastDateEpoch));

        return peakTpsMap;
    }

    /**
     * Process TPS records array to get peak TPS list for given number of days.
     *
     * @param tpsRecordsArray - JSONArray
     * @return - List of peak TPS values
     */
    private static ArrayList<BigDecimal> getPeakTpsList(
            JSONArray tpsRecordsArray, int numberOfDays, long metricsCountLastDateEpoch) {

        ArrayList<BigDecimal> elementList = new ArrayList<>(Arrays.asList(new BigDecimal[numberOfDays]));
        Collections.fill(elementList, BigDecimal.valueOf(0));

        JSONObject eventObject;
        long currentElement;
        for (Object object : tpsRecordsArray) {
            JSONObject jsonObject = (JSONObject) object;
            eventObject = (JSONObject) jsonObject.get(MetricsConstants.EVENT);
            currentElement = ((Number) eventObject.get(MetricsConstants.TOTAL_COUNT)).longValue();
            long recordTimestamp = ((Number) eventObject.get(MetricsConstants.TIMESTAMP)).longValue();
            int daysAgo = DateTimeUtil.getDayDifference(recordTimestamp, metricsCountLastDateEpoch);

            if (daysAgo >= 0 && daysAgo < numberOfDays) {
                // Setting the max value to the aggregated peak TPS list
                BigDecimal newValue = BigDecimal.valueOf(currentElement);
                BigDecimal currentValueInList = elementList.get(daysAgo);
                int comparisonResult = newValue.compareTo(currentValueInList);
                if (comparisonResult > 0) {
                    elementList.set(daysAgo, newValue);
                }
            }
        }
        return elementList;
    }


    /**
     * Filter records by aspect and get an array for a single aspect
     *
     * @param recordsArray - records array
     * @param aspectEnum   - aspect (Authenticated / Unauthenticated)
     * @return - filtered array
     */
    private static JSONArray getArrayByAspect(JSONArray recordsArray, AspectEnum aspectEnum) {

        JSONArray filteredArray = new JSONArray();
        for (Object object : recordsArray) {
            JSONObject record = (JSONObject) object;
            JSONObject event = (JSONObject) record.get(MetricsConstants.EVENT);
            if (aspectEnum.toString().equals(event.get(MetricsConstants.ASPECT))) {
                filteredArray.add(record);
            }
        }
        return filteredArray;
    }


}
