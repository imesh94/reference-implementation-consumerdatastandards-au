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
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.metrics.constants.MetricsConstants;
import com.wso2.openbanking.cds.metrics.service.MetricsQueryCreator;
import com.wso2.openbanking.cds.metrics.util.SPQueryExecutorUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

import static com.wso2.openbanking.cds.metrics.constants.MetricsConstants.RETRIEVAL_ERROR;

/**
 * Implementation of MetricsDataProvider interface.
 * This class provides data required to calculate metrics by interacting with WSO2 Streaming Integrator.
 * This class is excluded from code coverage since it requires an external dependency to function.
 */
public class MetricsV5DataProvider implements MetricsDataProvider {

    MetricsQueryCreator metricsV5QueryCreator;
    private static final OpenBankingCDSConfigParser configParser = OpenBankingCDSConfigParser.getInstance();
    private static final String tpsDataRetrievalUrl = configParser.getMetricsTPSDataRetrievalUrl();
    private static final Log log = LogFactory.getLog(MetricsV5DataProvider.class);

    public MetricsV5DataProvider(MetricsQueryCreator metricsV5QueryCreator) {
        this.metricsV5QueryCreator = metricsV5QueryCreator;
    }

    @Override
    public JSONObject getAvailabilityMetricsData() throws OpenBankingException {

        JSONObject availabilityMetricsJsonObject;
        String spQuery = metricsV5QueryCreator.getAvailabilityMetricsQuery();
        try {
            availabilityMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                    MetricsConstants.CDS_AVAILABILITY_METRICS_APP, spQuery);
        } catch (ParseException | IOException e) {
            String errorMessage = String.format(RETRIEVAL_ERROR, MetricsConstants.AVAILABILITY);
            log.error(errorMessage, e);
            throw new OpenBankingException(errorMessage, e);
        }
        return availabilityMetricsJsonObject;
    }

    @Override
    public JSONObject getInvocationMetricsData() throws OpenBankingException {

        JSONObject invocationMetricsJsonObject;
        String spQuery = metricsV5QueryCreator.getInvocationMetricsQuery();

        try {
            invocationMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                    MetricsConstants.CDS_INVOCATION_METRICS_APP, spQuery);
        } catch (ParseException | IOException e) {
            String errorMessage = String.format(RETRIEVAL_ERROR, MetricsConstants.INVOCATION);
            log.error(errorMessage, e);
            throw new OpenBankingException(errorMessage, e);
        }
        return invocationMetricsJsonObject;
    }

    @Override
    public JSONObject getSessionCountMetricsData() throws OpenBankingException {

        JSONObject sessionCountMetricsJsonObject;
        String spQuery = metricsV5QueryCreator.getSessionCountMetricsQuery();

        try {
            sessionCountMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                    MetricsConstants.CDS_SESSION_METRICS_APP, spQuery);
        } catch (ParseException | IOException e) {
            String errorMessage = String.format(RETRIEVAL_ERROR, MetricsConstants.SESSION_COUNT);
            log.error(errorMessage, e);
            throw new OpenBankingException(errorMessage, e);
        }
        return sessionCountMetricsJsonObject;
    }

    @Override
    public JSONArray getPeakTPSMetricsData() throws ParseException {

        JSONObject peakTPSEvent = metricsV5QueryCreator.getPeakTPSMetricsEvent();
        String responseStr = SPQueryExecutorUtil.executeRequestOnStreamProcessor(peakTPSEvent, tpsDataRetrievalUrl);
        //ToDO: Address vulnerable usage of JSONParser
        Object jsonResponse = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(responseStr);

        if (jsonResponse instanceof JSONArray) {
            return (JSONArray) jsonResponse;
        } else if (jsonResponse instanceof JSONObject) {
            JSONObject jsonResponseObt = (JSONObject) jsonResponse;
            JSONArray responseArray = new JSONArray();
            responseArray.add(jsonResponseObt);
            return responseArray;
        } else {
            return new JSONArray();
        }
    }

    @Override
    public JSONObject getErrorMetricsData() throws OpenBankingException {

        JSONObject errorMetricsJsonObject;
        String spQuery = metricsV5QueryCreator.getErrorMetricsQuery();

        try {
            errorMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                    MetricsConstants.CDS_INVOCATION_METRICS_APP, spQuery);
        } catch (ParseException | IOException e) {
            String errorMessage = String.format(RETRIEVAL_ERROR, MetricsConstants.ERROR);
            log.error(errorMessage, e);
            throw new OpenBankingException(errorMessage, e);
        }
        return errorMetricsJsonObject;
    }

    @Override
    public JSONObject getRejectionMetricsData() throws OpenBankingException {

        JSONObject rejectionMetricsJsonObject;
        String spQuery = metricsV5QueryCreator.getRejectionMetricsQuery();

        try {
            rejectionMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                    MetricsConstants.API_RAW_DATA_SUBMISSION_APP, spQuery);
        } catch (ParseException | IOException e) {
            String errorMessage = String.format(RETRIEVAL_ERROR, MetricsConstants.REJECTION);
            log.error(errorMessage, e);
            throw new OpenBankingException(errorMessage, e);
        }
        return rejectionMetricsJsonObject;
    }

    @Override
    public JSONObject getRecipientCountMetricsData() throws OpenBankingException {

        JSONObject recipientCountMetricsJsonObject;
        String spQuery = metricsV5QueryCreator.getRecipientCountMetricsQuery();

        try {
            recipientCountMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                    MetricsConstants.CDS_CUSTOMER_RECIPIENT_METRICS_APP, spQuery);
        } catch (ParseException | IOException e) {
            String errorMessage = String.format(RETRIEVAL_ERROR, MetricsConstants.RECIPIENT_COUNT);
            log.error(errorMessage, e);
            throw new OpenBankingException(errorMessage, e);
        }
        return recipientCountMetricsJsonObject;
    }

    @Override
    public JSONObject getCustomerCountMetricsData() throws OpenBankingException {

        JSONObject customerCountMetricsJsonObject;
        String spQuery = metricsV5QueryCreator.getCustomerCountMetricsQuery();

        try {
            customerCountMetricsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                    MetricsConstants.CDS_CUSTOMER_RECIPIENT_METRICS_APP, spQuery);
        } catch (ParseException | IOException e) {
            String errorMessage = String.format(RETRIEVAL_ERROR, MetricsConstants.CUSTOMER_COUNT);
            log.error(errorMessage, e);
            throw new OpenBankingException(errorMessage, e);
        }
        return customerCountMetricsJsonObject;
    }

    @Override
    public JSONObject getTotalResponseTimeMetricsData() throws OpenBankingException {

        JSONObject totalResponseTimeJsonObject;
        String spQuery = metricsV5QueryCreator.getTotalResponseTimeQuery();

        try {
            totalResponseTimeJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                    MetricsConstants.CDS_INVOCATION_METRICS_APP, spQuery);
        } catch (ParseException | IOException e) {
            String errorMessage = String.format(RETRIEVAL_ERROR, MetricsConstants.TOTAL_RESPONSE_TIME);
            log.error(errorMessage, e);
            throw new OpenBankingException(errorMessage, e);
        }
        return totalResponseTimeJsonObject;
    }

    @Override
    public JSONObject getSuccessfulInvocationMetricsData() throws OpenBankingException {

        JSONObject successInvocationsJsonObject;
        String spQuery = metricsV5QueryCreator.getSuccessfulInvocationsQuery();

        try {
            successInvocationsJsonObject = SPQueryExecutorUtil.executeQueryOnStreamProcessor(
                    MetricsConstants.CDS_INVOCATION_METRICS_APP, spQuery);
        } catch (ParseException | IOException e) {
            String errorMessage = String.format(RETRIEVAL_ERROR, MetricsConstants.SUCCESSFUL_INVOCATIONS);
            log.error(errorMessage, e);
            throw new OpenBankingException(errorMessage, e);
        }
        return successInvocationsJsonObject;
    }
}
