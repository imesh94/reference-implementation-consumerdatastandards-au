/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.constants;

import java.math.BigDecimal;

/**
 * CDS Metrics management constants.
 */
public class MetricsConstants {

    private MetricsConstants() {
    }

    // sp api endpoint
    public static final String SP_API_PATH = "/stores/query";
    public static final String REST_API_URL_KEY = "stream.processor.rest.api.url";
    public static final String SP_USERNAME_KEY = "stream.processor.rest.api.username";
    public static final String SP_PASSWORD_KEY = "stream.processor.rest.api.password";

    //siddhi-app constants
    public static final String CDS_INVOCATION_METRICS_APP = "CDSInvocationMetricsApp";
    public static final String CDS_AVAILABILITY_METRICS_APP = "CDSAvailabilityMetricsApp";
    public static final String CDS_SESSION_METRICS_APP = "CDSSessionMetricsApp";
    public static final String CDS_CUSTOMER_RECIPIENT_METRICS_APP = "CDSCustomerRecipientMetricsApp";
    public static final String REGISTRATION_DATA_SUBMISSION_APP = "AppRegistrationDataSubmissionApp";
    public static final String API_RAW_DATA_SUBMISSION_APP = "APIRawDataSubmissionApp";
    public static final String CDS_PEAK_TPS_AGG_DATA_APP = "CDSPeakTPSMetricsAggregationApp";
    public static final String CDS_AVAILABILITY_AGG_DATA_APP = "CDSAvailabilityMetricsAggregationApp";
    public static final String PEAK_TPS_AGG_INPUT_STREAM = "MaxTPSMetricsAggregationInputStream";
    public static final String AVAILABILITY_AGG_INPUT_STREAM = "AvailabilityMetricsAggregationInputStream";

    //date-time constants
    public static final String TIME_ZONE = "GMT";
    public static final String SP_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String REQUEST_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static final BigDecimal SECONDS_IN_DAY = BigDecimal.valueOf(86400); // 60 * 60 * 24

    //rejection metrics constants
    public static final String CDS_REJECTION_METRICS_APP_AUTHENTICATED = "authenticated";
    public static final String CDS_REJECTION_METRICS_APP_UNAUTHENTICATED = "unauthenticated";
    public static final String CDS_REJECTION_METRICS_APP_VALIDITY = "anonymous";

    //availability constants
    public static final String SCHEDULED_OUTAGE = "scheduled";
    public static final String INCIDENT_OUTAGE = "incident";

    // metrics aggregation constants
    public static final String RECORDS = "records";
    public static final String AVAILABILITY_DATA = "availability_data";
    public static final String AVAILABILITY_AGG_DATA = "availability_agg_data";
    public static final String AGG_MONTH = "agg_month";
    public static final String DATE_AGGREGATED = "date_aggregated";
    public static final String AGG_DATE = "agg_date";
    public static final String MAX_TPS = "max_tps";
    public static final String TPS_AGG_DATA = "aggregated_tps_data";
}
