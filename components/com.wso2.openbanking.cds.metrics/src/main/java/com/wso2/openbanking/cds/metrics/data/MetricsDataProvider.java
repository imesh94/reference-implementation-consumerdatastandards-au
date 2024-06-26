/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.data;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;

/**
 * Interface for providing metrics data.
 * The expected JSON structure for each data type can be found in MockMetricsDataProvider class.
 * This interface should be implemented if there is a requirement to support a new analytics engine.
 */
public interface MetricsDataProvider {

    JSONObject getAvailabilityMetricsData() throws OpenBankingException;

    JSONObject getInvocationMetricsData() throws OpenBankingException;

    JSONObject getInvocationByAspectMetricsData() throws OpenBankingException;

    JSONObject getSessionCountMetricsData() throws OpenBankingException;

    JSONArray getPeakTPSMetricsData() throws ParseException;

    JSONObject getErrorMetricsData() throws OpenBankingException;

    JSONObject getRejectionMetricsData() throws OpenBankingException;

    JSONObject getRecipientCountMetricsData() throws OpenBankingException;

    JSONObject getCustomerCountMetricsData() throws OpenBankingException;

    JSONObject getTotalResponseTimeMetricsData() throws OpenBankingException;

    JSONObject getSuccessfulInvocationMetricsData() throws OpenBankingException;
}
