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

import com.wso2.openbanking.accelerator.common.util.Generated;
import com.wso2.openbanking.cds.metrics.constants.MetricsConstants;
import com.wso2.openbanking.cds.metrics.model.ServerOutageDataModel;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Util class containing methods to process the JSON object returned form stream processor.
 */
public class SPJsonProcessorUtil {

    private static final Log log = LogFactory.getLog(SPJsonProcessorUtil.class);

    private SPJsonProcessorUtil() {

    }

    // Json Attribute
    private static final String RECORDS = "records";

    /**
     * Get sum of the elements in the JSON object.
     *
     * @param jsonObject - JSON object
     * @return - total
     */
    static BigDecimal getSumFromJsonObject(JSONObject jsonObject) {

        JSONArray recordsArray = (JSONArray) jsonObject.get(RECORDS);
        JSONArray countArray;
        int totalCount = 0;
        for (Object object : recordsArray) {
            countArray = (JSONArray) object;
            totalCount += Long.parseLong(countArray.get(0).toString());
        }
        return BigDecimal.valueOf(totalCount);
    }

    /**
     * Get Authenticated and Unauthenticated API rejection counts
     *
     * @param jsonObject - JSON object
     * @return - total counts for each type
     */
    static List<BigDecimal> getSumFromJsonObjectRejection(JSONObject jsonObject) {

        JSONArray recordsArray = (JSONArray) jsonObject.get(RECORDS);
        JSONArray countArray;
        String validity;
        ArrayList<BigDecimal> elementList = new ArrayList<>(Arrays.asList(new BigDecimal[2]));
        Collections.fill(elementList, BigDecimal.valueOf(0));
        int totalCountAuthenticated = 0;
        int totalCountUnauthenticated = 0;

        for (Object object : recordsArray) {
            countArray = (JSONArray) object;
            validity = (String) countArray.get(1);
            if (!MetricsConstants.CDS_REJECTION_METRICS_APP_VALIDITY.equals(validity)) { //authenticated user
                totalCountAuthenticated += Long.parseLong(countArray.get(0).toString());
            }
            if (MetricsConstants.CDS_REJECTION_METRICS_APP_VALIDITY.equals(validity)) { //unauthenticated user
                totalCountUnauthenticated += Long.parseLong(countArray.get(0).toString());
            }
        }

        elementList.set(0, BigDecimal.valueOf(totalCountAuthenticated));
        elementList.set(1, BigDecimal.valueOf(totalCountUnauthenticated));
        return elementList;
    }

    /**
     * Get maximum value from the elements in the JSON object.
     *
     * @param jsonObject - JSON object
     * @return - maximum value
     */
    static BigDecimal getMaxFromJsonObject(JSONObject jsonObject) {

        JSONArray recordsArray = (JSONArray) jsonObject.get(RECORDS);
        JSONArray countArray;
        long maxValue = 0;
        long currentValue;
        for (Object object : recordsArray) {
            countArray = (JSONArray) object;
            currentValue = Long.parseLong(countArray.get(0).toString());;
            maxValue = currentValue > maxValue ? currentValue : maxValue;
        }
        return BigDecimal.valueOf(maxValue);
    }

    /**
     * Convert JSON object returned from SP to a list.
     *
     * @param jsonObject - JSON object
     * @return - total
     */
    static List<BigDecimal> getListFromJsonObject(JSONObject jsonObject) {

        JSONArray recordsArray = (JSONArray) jsonObject.get(RECORDS);
        ArrayList<BigDecimal> elementList = new ArrayList<>(Arrays.asList(new BigDecimal[7]));
        Collections.fill(elementList, BigDecimal.valueOf(0));

        JSONArray countArray;
        Long currentElement;
        int currentDay;
        for (Object object : recordsArray) {
            countArray = (JSONArray) object;
            currentElement = Long.parseLong (countArray.get(0).toString());
            long currentTimestamp = (Long.parseLong (countArray.get(1).toString())) / 1000;
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
     * Convert JSON object returned from SP to a list and check whether request is authenticated or unauthenticated.
     *
     * @param jsonObject - JSON object
     * @return - total
     * New method for historic count
     */
    @Generated(message = "Excluded from code coverage")
    static List<ArrayList<BigDecimal>> getListFromJsonObjectRejection(JSONObject jsonObject) {

        JSONArray recordsArray = (JSONArray) jsonObject.get(RECORDS);
        ArrayList<BigDecimal> elementListAuthenticated = new ArrayList<>(Arrays.asList(new BigDecimal[7]));
        ArrayList<BigDecimal> elementListUnauthenticated = new ArrayList<>(Arrays.asList(new BigDecimal[7]));
        ArrayList<ArrayList<BigDecimal>> elementList = new ArrayList<>(2);
        Collections.fill(elementListAuthenticated, BigDecimal.valueOf(0));
        Collections.fill(elementListUnauthenticated, BigDecimal.valueOf(0));

        JSONArray countArray;

        Long currentElement;
        int currentDay;
        String validity;
        for (Object object : recordsArray) {
            countArray = (JSONArray) object;
            currentElement = Long.parseLong(countArray.get(0).toString());
            long currentTimestamp = (Long.parseLong (countArray.get(1).toString())) / 1000;
            validity = (String) countArray.get(2);
            currentDay = DateTimeUtil.getDaysAgo(currentTimestamp);
            if (!MetricsConstants.CDS_REJECTION_METRICS_APP_VALIDITY.equals(validity)) { //authenticated user
                if (currentDay > 0 && currentDay <= 7) { //allowed range of days
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Adding metrics data for day %s", currentDay));
                    }
                    elementListAuthenticated.set(currentDay - 1, BigDecimal.valueOf(currentElement));
                }
            }
            if (MetricsConstants.CDS_REJECTION_METRICS_APP_VALIDITY.equals(validity)) { //unauthenticated user
                if (currentDay > 0 && currentDay <= 7) { //allowed range of days
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Adding metrics data for day %s", currentDay));
                    }
                    elementListUnauthenticated.set(currentDay - 1, BigDecimal.valueOf(currentElement));
                }
            }
        }

        elementList.add(0, elementListAuthenticated);
        elementList.add(1, elementListUnauthenticated);

        return elementList;
    }

    /**
     * Get latest value from the elements in the JSON object.
     *
     * @param jsonObject - JSON object
     * @return - latest value
     */
    static Long getLastElementValueFromJsonObject(JSONObject jsonObject) {

        JSONArray recordsArray = (JSONArray) jsonObject.get(RECORDS);
        if (recordsArray.isEmpty()) {
            return 0L;
        }
        JSONArray countArray = (JSONArray) recordsArray.get(recordsArray.size() - 1);
        return Long.parseLong(countArray.get(0).toString());
    }

    /**
     * Get server availability between given time period from the list of ServerOutages
     *
     * @param serverOutageDataList
     * @param from
     * @param to
     * @return
     */
    public static BigDecimal getAvailabilityFromServerOutages(List<ServerOutageDataModel> serverOutageDataList,
                                                              long from, long to) {

        long timeDurationOfReportingPeriod = to - from;
        long totalScheduledOutages = 0L;
        long totalIncidentOutages = 0L;

        List<ServerOutageDataModel> scheduledOutages = new ArrayList<>();
        List<ServerOutageDataModel> incidentOutages = new ArrayList<>();

        // filter the outages. scheduled vs incidents
        for (ServerOutageDataModel dataModel : serverOutageDataList) {
            if (dataModel.getTimeFrom() >= from && dataModel.getTimeFrom() < to) {
                if (MetricsConstants.SCHEDULED_OUTAGE.equals(dataModel.getType())) {
                    scheduledOutages.add(dataModel);
                } else {
                    incidentOutages.add(dataModel);
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
     * Calculate total server outage time from ServerOutageDataModel
     *
     * @param serverOutages
     * @return
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
                    // not an overlap
                    totalTime += serverOutage.getTimeTo() - serverOutage.getTimeFrom();
                    currentEndTime = serverOutage.getTimeTo();
                } else if (serverOutage.getTimeTo() <= currentEndTime) {
                    // complete overlap = ignore

                } else if (serverOutage.getTimeTo() > currentEndTime) {
                    // overlap
                    totalTime += serverOutage.getTimeTo() - currentEndTime;
                    currentEndTime = serverOutage.getTimeTo();
                }
            }
        }
        return totalTime;
    }
}
