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

    //siddhi-app constants
    public static final String CDS_INVOCATION_METRICS_APP = "CDSInvocationMetricsApp";
    public static final String CDS_AVAILABILITY_METRICS_APP = "CDSAvailabilityMetricsApp";
    public static final String CDS_SESSION_METRICS_APP = "CDSSessionMetricsApp";
    public static final String CDS_CUSTOMER_RECIPIENT_METRICS_APP = "CDSCustomerRecipientMetricsApp";
    public static final String REGISTRATION_DATA_SUBMISSION_APP = "AppRegistrationDataSubmissionApp";
    public static final String API_RAW_DATA_SUBMISSION_APP = "APIRawDataSubmissionApp";

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
}
