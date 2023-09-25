/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.scheduler.util;

import com.wso2.openbanking.cds.metrics.scheduler.internal.MetricsApiSchedulerDataHolder;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;

/**
 * Admin API Scheduler Constants
 */
public class MetricsApiSchedulerConstants {

    // get configurations from api-manager.xml
    public static APIManagerConfiguration config;

    static {
        try {
            //get the configurations in OB-APIM env
            config = MetricsApiSchedulerDataHolder.getInstance().
                    getApiManagerConfigurationService().getAPIManagerConfiguration();
        } catch (NullPointerException e) {
            //get the configurations in Vanilla-APIM env
            Bundle bundle = FrameworkUtil.getBundle(APIManagerConfigurationService.class);
            BundleContext context = bundle.getBundleContext();
            ServiceReference reference = context.getServiceReference(APIManagerConfigurationService.class);
            APIManagerConfigurationService service = (APIManagerConfigurationService) context.getService(reference);
            config = service.getAPIManagerConfiguration();
        }
    }

    public static final String SP_API_HOST = config.getFirstProperty(APIConstants.API_USAGE_DAS_REST_API_URL);
    public static final String SP_USERNAME = config.getFirstProperty(APIConstants.API_USAGE_DAS_REST_API_USER);
    public static final String SP_PASSWORD = config.getFirstProperty(APIConstants.API_USAGE_DAS_REST_API_PASSWORD);
    public static final String SP_API_PATH = "/stores/query";
    // Json Attribute
    public static final String RECORDS = "records";
    public static final String CDS_INVOCATION_METRICS_APP = "CDSInvocationMetricsApp";
    public static final String CDS_AVAILABILITY_METRICS_APP = "CDSAvailabilityMetricsApp";
    public static final String PEAK_TPS_AGG_INPUT_STREAM = "MaxTPSMetricsAggregationInputStream";
    public static final String AVAILABILITY_AGG_INPUT_STREAM = "AvailabilityMetricsAggregationInputStream";
    public static final String SCHEDULED_OUTAGE = "scheduled";
    public static final String INCIDENT_OUTAGE = "incident";
    public static final String TIME_ZONE = "GMT";
    public static final String SP_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String AVAILABILITY_DATA = "availability_data";
    public static final String AVAILABILITY_AGG_DATA = "availability_agg_data";
    public static final String AGG_MONTH = "agg_month";
    public static final String DATE_AGGREGATED = "date_aggregated";
    public static final String AGG_DATE = "agg_date";
    public static final String MAX_TPS = "max_tps";
    public static final String TPS_AGG_DATA = "aggregated_tps_data";
}
