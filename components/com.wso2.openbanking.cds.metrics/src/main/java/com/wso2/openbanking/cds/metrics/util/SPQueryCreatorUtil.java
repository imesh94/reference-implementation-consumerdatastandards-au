/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.metrics.util;

import com.wso2.openbanking.cds.metrics.constants.MetricsConstants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Util class form executing queries on the stream processor.
 */
public class SPQueryCreatorUtil {

    private SPQueryCreatorUtil() {
    }

    /**
     * Return query for retrieving historic api invocation data.
     *
     * @param priority - priority tier
     * @return - query string
     */
    static String getHistoricInvocationsQuery(PriorityEnum priority) {

        String[] timeRangeArray = getTimeGapForDays(7, TimeFormatEnum.STANDARD);

        return String.format("from CDSMetricsAgg within '%s', '%s' per 'days' " +
                        "select totalReqCount, AGG_TIMESTAMP having priorityTier == '%s';",
                timeRangeArray[0], timeRangeArray[1], priority.toString());
    }

    /**
     * Return query for retrieving current api invocation data.
     *
     * @param priority - priority tier
     * @return - query string
     */
    static String getCurrentInvocationsQuery(PriorityEnum priority) {

        String[] timeRangeArray = getTimeGapForDays(0, TimeFormatEnum.STANDARD);

        return String.format("from CDSMetricsAgg within '%s', '%s' per 'minutes' " +
                        "select totalReqCount having priorityTier == '%s';",
                timeRangeArray[0], timeRangeArray[1], priority.toString());
    }

    /**
     * Return query for retrieving historic total response time data.
     *
     * @param priority - priority tier
     * @return - query string
     */
    static String getHistoricTotalResponseQuery(PriorityEnum priority) {

        String[] timeRangeArray = getTimeGapForDays(7, TimeFormatEnum.STANDARD);

        return String.format("from CDSMetricsAgg within '%s', '%s' per 'days' " +
                        "select totalRespTime, AGG_TIMESTAMP having priorityTier == '%s';",
                timeRangeArray[0], timeRangeArray[1], priority.toString());
    }

    /**
     * Return query for retrieving current total response time data.
     *
     * @param priority - priority tier
     * @return - query string
     */
    static String getCurrentTotalResponseQuery(PriorityEnum priority) {

        String[] timeRangeArray = getTimeGapForDays(0, TimeFormatEnum.STANDARD);

        return String.format("from CDSMetricsAgg within '%s', '%s' per 'minutes' " +
                        "select totalRespTime having priorityTier == '%s';",
                timeRangeArray[0], timeRangeArray[1], priority.toString());
    }

    /**
     * Return query for retrieving historic average response time data.
     *
     * @return - query string
     */
    static String getHistoricSuccessInvocationsQuery() {

        String[] timeRangeArray = getTimeGapForDays(7, TimeFormatEnum.STANDARD);

        return String.format("from CDSMetricsPerfAgg within '%s', '%s' per 'days' select totalReqCount, " +
                "AGG_TIMESTAMP having withinThreshold == true;", timeRangeArray[0], timeRangeArray[1]);
    }

    /**
     * Return query for retrieving current day average response time data.
     *
     * @return - query string
     */
    static String getCurrentSuccessInvocationsQuery() {

        String[] timeRangeArray = getTimeGapForDays(0, TimeFormatEnum.STANDARD);

        return String.format("from CDSMetricsPerfAgg within '%s', '%s' per 'minutes' select totalReqCount " +
                "having withinThreshold == true;", timeRangeArray[0], timeRangeArray[1]);
    }

    /**
     * Return query for retrieving historic error invocations data.
     * Only internal server errors are considered (http 5xx)
     *
     * @return - query string
     */
    static String getHistoricErrorInvocationsQuery() {

        String[] timeRangeArray = getTimeGapForDays(7, TimeFormatEnum.STANDARD);

        return String.format("from CDSMetricsStatusAgg within '%s', '%s' " +
                        "per 'days' select totalReqCount, AGG_TIMESTAMP having statusCode >= 500 and " +
                        "statusCode < 600;",
                timeRangeArray[0], timeRangeArray[1]);
    }

    /**
     * Return query for retrieving current day error invocations data.
     * Only internal server errors are considered (http 5xx)
     *
     * @return - query string
     */
    static String getCurrentErrorInvocationsQuery() {

        String[] timeRangeArray = getTimeGapForDays(0, TimeFormatEnum.STANDARD);

        return String.format("from CDSMetricsStatusAgg within '%s', '%s' " +
                        "per 'minutes' select totalReqCount as reqCount having statusCode >= 500 and " +
                        "statusCode < 600;",
                timeRangeArray[0], timeRangeArray[1]);
    }

    /**
     * Return query for retrieving historic error invocations data.
     * Checks whether authenticated or unauthenticated based on the username
     *
     * @return - query string
     */
    static String getHistoricRejectionsQuery() {

        String[] timeRangeArray = getTimeGapForDays(7, TimeFormatEnum.EPOCH);

        return String.format("from API_INVOCATION_RAW_DATA on str:contains(API_NAME, 'ConsumerDataStandards') " +
                        "select count(STATUS_CODE) as throttledOutCount, TIMESTAMP, CONSUMER_ID group by TIMESTAMP, " +
                        "CONSUMER_ID having STATUS_CODE == 429 and TIMESTAMP > %s and TIMESTAMP < %s;",
                timeRangeArray[0], timeRangeArray[1]);
    }

    /**
     * Return query for authenticated error invocations data for the current week
     * Checks whether authenticated or unauthenticated based on the username
     *
     * @return - query string
     */
    static String getCurrentRejectionsQuery() {

        String[] timeRangeArray = getTimeGapForDays(0, TimeFormatEnum.EPOCH);
        // Rejected invocations for Consumer data standards will only be considered.
        return String.format("from API_INVOCATION_RAW_DATA on str:contains(API_NAME, 'ConsumerDataStandards') " +
                        "select count(STATUS_CODE) as throttleOutCount, CONSUMER_ID group by TIMESTAMP, CONSUMER_ID " +
                        "having STATUS_CODE == 429 and TIMESTAMP > %s and TIMESTAMP < %s;",
                timeRangeArray[0], timeRangeArray[1]);
    }

    /**
     * Return query for retrieving TPS data
     * (grouped according to the seconds timestamp).
     *
     * @return - query string
     */
    static String getCurrentTPSQuery() {

        String[] timeRangeArray = getTimeGapForDays(0, TimeFormatEnum.EPOCH);

        return String.format("from API_INVOCATION_RAW_DATA select count(ID) as idCounts group by TIMESTAMP having " +
                "TIMESTAMP > %s and TIMESTAMP < %s;", timeRangeArray[0], timeRangeArray[1]);
    }

    /**
     * Return query for retrieving Max TPS data.
     *
     * @return - query string
     */
    static String getHistoricPeakTPSQuery() {

        String[] timeRangeArray = getTimeGapForDays(7, TimeFormatEnum.EPOCH);

        return String.format("from CDS_METRICS_TPS select MAX_TPS, TIMESTAMP*1000 as TIMESTAMP group by TIMESTAMP, " +
                "MAX_TPS having TIMESTAMP > %s and TIMESTAMP < %s;", timeRangeArray[0], timeRangeArray[1]);
    }

    /**
     * Return query for retrieving historic session count data.
     *
     * @return - query string
     */
    static String getHistoricSessionCountQuery() {

        String[] timeRangeArray = getTimeGapForDays(7, TimeFormatEnum.STANDARD);

        return String.format("from CDSMetricsSessionAgg within '%s', '%s' per 'days' select sessionCount, " +
                "AGG_TIMESTAMP;", timeRangeArray[0], timeRangeArray[1]);
    }

    /**
     * Return query for retrieving current day session count data.
     *
     * @return - query string
     */
    static String getCurrentSessionCountQuery() {

        String[] timeRangeArray = getTimeGapForDays(0, TimeFormatEnum.STANDARD);

        return String.format("from CDSMetricsSessionAgg within '%s', '%s' " +
                        "per 'minutes' select sessionCount;", timeRangeArray[0],
                timeRangeArray[1]);
    }

    /**
     * Return query for retrieving recipient count data.
     *
     * @return - query string
     */
    static String getRecipientCountQuery() {

        return "from CDSMetricsCustomerRecipientSummary on ACTIVE_AUTH_COUNT>0 " +
                "select distinctCount(CLIENT_ID) as recipientCount ;";
    }

    /**
     * Return query for retrieving customer count.
     *
     * @return - query string
     */
    static String getCustomerCountQuery() {

        return "from CDSMetricsCustomerRecipientSummary on ACTIVE_AUTH_COUNT>0 " +
                "select distinctCount(USER_ID) as customerCount ;";
    }

    /**
     * Return query for retrieving unique consentId groups.
     *
     * @return - query string
     */
    static String getUserIdGroupsQuery() {

        return ("from UK_ACCOUNT_CONSENT_BINDING select count(USER_ID) as count group by USER_ID;");
    }

    /**
     * Return query for retrieving availability records between given time period.
     *
     * @return - query string
     */
    static String getAvailabilityRecordsByTimePeriod(Long fromTimestamp, Long toTimestamp) {

        return String.format("from SERVER_OUTAGES_RAW_DATA select OUTAGE_ID, TIMESTAMP, TYPE, TIME_FROM, TIME_TO " +
                        "group by OUTAGE_ID, TIMESTAMP, TYPE, TIME_FROM, TIME_TO having " +
                        "TIME_FROM >= %s AND TIME_TO < %s ;",
                fromTimestamp.toString(), toTimestamp.toString());
    }

    /**
     * Return query for retrieving oldest records of server outages.
     *
     * @return - query string
     */
    static String getOldestServerOutageRecord() {

        return ("from SERVER_OUTAGES_RAW_DATA select OUTAGE_ID, TIMESTAMP, TYPE, TIME_FROM, TIME_TO " +
                "order by TIME_FROM asc limit 1;");
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
        SimpleDateFormat formatter = new SimpleDateFormat(MetricsConstants.SP_TIMESTAMP_PATTERN);
        formatter.setTimeZone(TimeZone.getTimeZone(MetricsConstants.TIME_ZONE));

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
}
