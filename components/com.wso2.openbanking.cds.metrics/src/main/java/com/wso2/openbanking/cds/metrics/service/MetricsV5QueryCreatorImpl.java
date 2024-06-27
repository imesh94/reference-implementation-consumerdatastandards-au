/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.service;

import com.wso2.openbanking.cds.metrics.util.DateTimeUtil;
import com.wso2.openbanking.cds.metrics.util.PeriodEnum;
import com.wso2.openbanking.cds.metrics.util.TimeGranularityEnum;
import net.minidev.json.JSONObject;

/**
 * Implementation of Metrics Query Creator for CDS Metrics V5.
 * This class will initialize a query creator with timestamps relevant to the given period.
 */
public class MetricsV5QueryCreatorImpl implements MetricsQueryCreator {

    private final String fromTimestamp;
    private final String toTimestamp;
    private final long fromTimestampEpochSecond;
    private final long toTimestampEpochSecond;
    private final long availabilityFromTimestamp;
    private final long availabilityToTimestamp;
    private final String timeGranularity;

    public MetricsV5QueryCreatorImpl(PeriodEnum period) {

        String[] timeRangeArray = DateTimeUtil.getTimeRange(period);
        String[] availabilityTimeRangeArray = DateTimeUtil.getAvailabilityMetricsTimeRange(period);
        this.timeGranularity = TimeGranularityEnum.DAYS.toString();
        this.fromTimestamp = timeRangeArray[0];
        this.toTimestamp = timeRangeArray[1];
        this.fromTimestampEpochSecond = DateTimeUtil.getEpochTimestamp(fromTimestamp);
        this.toTimestampEpochSecond = DateTimeUtil.getEpochTimestamp(toTimestamp);
        this.availabilityFromTimestamp = DateTimeUtil.getEpochTimestamp(availabilityTimeRangeArray[0]);
        this.availabilityToTimestamp = DateTimeUtil.getEpochTimestamp(availabilityTimeRangeArray[1]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAvailabilityMetricsQuery() {

        return "from SERVER_OUTAGES_RAW_DATA select OUTAGE_ID, TIMESTAMP, TYPE, TIME_FROM, TIME_TO, ASPECT group by " +
                "OUTAGE_ID, TIMESTAMP, TYPE, TIME_FROM, TIME_TO, ASPECT having TIME_FROM >= "
                + availabilityFromTimestamp + " AND TIME_TO <= " + availabilityToTimestamp + ";";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInvocationMetricsQuery() {

        return "from CDSMetricsAgg within '" + fromTimestamp + "', '" + toTimestamp + "' per '" + timeGranularity +
                "' select priorityTier, totalReqCount, AGG_TIMESTAMP group by priorityTier, AGG_TIMESTAMP order by " +
                "AGG_TIMESTAMP desc;";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInvocationByAspectMetricsQuery() {

        return "from CDSMetricsAspectAgg within '" + fromTimestamp + "', '" + toTimestamp + "' per '" +
                timeGranularity + "' select aspect, totalReqCount, AGG_TIMESTAMP group by aspect, AGG_TIMESTAMP " +
                "having aspect != 'uncategorized' order by AGG_TIMESTAMP desc;";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHourlyPerformanceByPriorityMetricsQuery() {

        return "from CDSMetricsPerfPriorityAgg within '" + fromTimestamp + "', '" + toTimestamp + "' per '" +
                timeGranularity + "' select priorityTier, AGG_TIMESTAMP, " +
                "withinThresholdCount/totalReqCount as performance group by priorityTier, AGG_TIMESTAMP " +
                "order by AGG_TIMESTAMP asc;";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSessionCountMetricsQuery() {

        return "from CDSMetricsSessionAgg within '" + fromTimestamp + "', '" + toTimestamp + "' per '" +
                timeGranularity + "' select sessionCount, AGG_TIMESTAMP;";
    }

    /**
     * {@inheritDoc}
     * This method is not implemented for CDS Metrics V3 since we are getting PeakTPS from a siddhi app.
     */
    @Override
    public String getPeakTPSMetricsQuery() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSONObject getPeakTPSMetricsEvent() {

        JSONObject event = new JSONObject();
        event.put("start_time", fromTimestampEpochSecond);
        event.put("end_time", toTimestampEpochSecond);
        return event;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getErrorMetricsQuery() {

        return "from CDSMetricsStatusAgg within '" + fromTimestamp + "', '" + toTimestamp + "' per '" +
                timeGranularity + "' select totalReqCount, AGG_TIMESTAMP as reqCount having statusCode >= 500 and " +
                "statusCode < 600;";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRejectionMetricsQuery() {

        return "from API_INVOCATION_RAW_DATA on str:contains(API_NAME, 'ConsumerDataStandards') " +
                "select count(STATUS_CODE) as throttleOutCount, TIMESTAMP, CONSUMER_ID group by TIMESTAMP, " +
                "CONSUMER_ID having STATUS_CODE == 429 and TIMESTAMP > " + fromTimestampEpochSecond + " and " +
                "TIMESTAMP < " + toTimestampEpochSecond + ";";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRecipientCountMetricsQuery() {

        return "from CDSMetricsCustomerRecipientSummary on ACTIVE_AUTH_COUNT>0 select distinctCount(CLIENT_ID) " +
                "as recipientCount;";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustomerCountMetricsQuery() {

        return "from CDSMetricsCustomerRecipientSummary on ACTIVE_AUTH_COUNT > 0 select distinctCount(USER_ID) as " +
                "customerCount;";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSuccessfulInvocationsQuery() {

        return "from CDSMetricsPerfAgg within '" + fromTimestamp + "', '" + toTimestamp + "' per '" + timeGranularity +
                "' select totalReqCount, AGG_TIMESTAMP having withinThreshold == true;";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTotalResponseTimeQuery() {

        return "from CDSMetricsAgg within '" + fromTimestamp + "', '" + toTimestamp + "' per '" + timeGranularity +
                "' select priorityTier, (totalRespTime / 1000.0) as totalRespTime, AGG_TIMESTAMP " +
                "group by priorityTier, AGG_TIMESTAMP order by AGG_TIMESTAMP desc;";
    }

}
