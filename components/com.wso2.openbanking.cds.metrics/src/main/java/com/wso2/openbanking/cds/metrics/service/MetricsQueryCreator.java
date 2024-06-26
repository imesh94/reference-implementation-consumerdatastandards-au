/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.service;

import net.minidev.json.JSONObject;

/**
 * Interface of Metrics Query Creator.
 */
public interface MetricsQueryCreator {

    /**
     * Returns the query for getting availability metrics data.
     *
     * @return Query as a String
     */
    String getAvailabilityMetricsQuery();

    /**
     * Returns the query for getting api invocation data.
     *
     * @return Query as a String
     */
    String getInvocationMetricsQuery();

    /**
     * Returns the query for getting api invocation data by aspect.
     *
     * @return Query as a String
     */
    String getInvocationByAspectMetricsQuery();

    /**
     * Returns the query for getting session count.
     *
     * @return Query as a String
     */
    String getSessionCountMetricsQuery();

    /**
     * Returns the query for getting peak tps.
     *
     * @return Query as a String
     */
    String getPeakTPSMetricsQuery();

    /**
     * Returns the event for getting peak tps from siddhi app.
     *
     * @return Query as a String
     */
    JSONObject getPeakTPSMetricsEvent();


    /**
     * Return the query for getting error data.
     *
     * @return Query as a String
     */
    String getErrorMetricsQuery();

    /**
     * Return the query for getting rejection data (throttled out requests).
     *
     * @return Query as a String
     */
    String getRejectionMetricsQuery();

    /**
     * Returns the query for getting data recipient count.
     *
     * @return Query as a String
     */
    String getRecipientCountMetricsQuery();

    /**
     * Returns the query for getting customer count with active authorizations.
     *
     * @return Query as a String
     */
    String getCustomerCountMetricsQuery();

    /**
     * Returns the query for getting successful invocations count.
     *
     * @return Query as a String
     */
    String getSuccessfulInvocationsQuery();

    /**
     * Returns the query for getting response time data.
     *
     * @return Query as a String
     */
    String getTotalResponseTimeQuery();

}
