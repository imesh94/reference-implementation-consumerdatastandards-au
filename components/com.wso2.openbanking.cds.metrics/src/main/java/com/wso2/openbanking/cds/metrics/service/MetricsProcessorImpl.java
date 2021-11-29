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

package com.wso2.openbanking.cds.metrics.service;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.metrics.constants.MetricsConstants;
import com.wso2.openbanking.cds.metrics.model.ResponseMetricsListModel;
import com.wso2.openbanking.cds.metrics.util.MetricsProcessorUtil;
import com.wso2.openbanking.cds.metrics.util.PeriodEnum;
import com.wso2.openbanking.cds.metrics.util.PriorityEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Implementation of MetricsProcessor.
 * Contains methods required for formatting the retrieved data from Stream Processor.
 */
public class MetricsProcessorImpl implements MetricsProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMetricsListModel getResponseMetricsListModel(String requestTime, String period)
            throws OpenBankingException {

        ResponseMetricsListModel responseMetricsListModel = new ResponseMetricsListModel();
        responseMetricsListModel.setRequestTime(requestTime);
        PeriodEnum periodEnum = PeriodEnum.fromString(period);

        // get invocation metrics from sp
        Map<PriorityEnum, List<BigDecimal>> invocationMetricsMap =
                MetricsProcessorUtil.getInvocationMetrics(periodEnum);

        // get total transactions for each day
        List<BigDecimal> totalTransactionsList =
                MetricsProcessorUtil.getTotalInvocationsForEachDay(invocationMetricsMap);

        //get response time metrics from sp
        Map<PriorityEnum, List<BigDecimal>> averageResponseMetricsMap = MetricsProcessorUtil.getAverageResponseMetrics(
                invocationMetricsMap, periodEnum);
        // get recipient count metrics from sp
        responseMetricsListModel.setRecipientCount(MetricsProcessorUtil.getRecipientCountMetrics());
        // get customer count metrics from sp
        responseMetricsListModel.setCustomerCount(MetricsProcessorUtil.getCustomerCountMetrics());

        // set invocation metrics
        responseMetricsListModel.setInvocationUnauthenticated(invocationMetricsMap.get(
                PriorityEnum.UNAUTHENTICATED));
        responseMetricsListModel.setInvocationHighPriority(invocationMetricsMap.get(
                PriorityEnum.HIGH_PRIORITY));
        responseMetricsListModel.setInvocationLowPriority(invocationMetricsMap.get(
                PriorityEnum.LOW_PRIORITY));
        responseMetricsListModel.setInvocationUnattended(invocationMetricsMap.get(
                PriorityEnum.UNATTENDED));
        responseMetricsListModel.setInvocationLargePayload(invocationMetricsMap.get(
                PriorityEnum.LARGE_PAYLOAD));

        // set average response metrics
        responseMetricsListModel.setAverageResponseUnauthenticated(averageResponseMetricsMap.get(
                PriorityEnum.UNAUTHENTICATED));
        responseMetricsListModel.setAverageResponseHighPriority(averageResponseMetricsMap.get(
                PriorityEnum.HIGH_PRIORITY));
        responseMetricsListModel.setAverageResponseLowPriority(averageResponseMetricsMap.get(
                PriorityEnum.LOW_PRIORITY));
        responseMetricsListModel.setAverageResponseUnattended(averageResponseMetricsMap.get(
                PriorityEnum.UNATTENDED));
        responseMetricsListModel.setAverageResponseLargePayload(averageResponseMetricsMap.get(
                PriorityEnum.LARGE_PAYLOAD));

        // get average TPS metrics from sp
        responseMetricsListModel.setAverageTPS(MetricsProcessorUtil.getAverageTPSMetrics(totalTransactionsList));

        // get peak TPS metrics from sp
        responseMetricsListModel.setPeakTPS(MetricsProcessorUtil.getPeakTPSMetrics(periodEnum));

        // get session count metrics from sp
        responseMetricsListModel.setSessionCount(MetricsProcessorUtil.getSessionCountMetrics(periodEnum));

        //get error metrics from sp
        responseMetricsListModel.setErrors(MetricsProcessorUtil.getErrorInvocationMetrics(periodEnum));

        //get authenticated rejection metrics from sp
        responseMetricsListModel.setAuthenticatedEndpointRejections(MetricsProcessorUtil.
                getRejectedInvocationMetrics(periodEnum, MetricsConstants.CDS_REJECTION_METRICS_APP_AUTHENTICATED));

        //get unauthenticated rejection metrics from sp
        responseMetricsListModel.setUnauthenticatedEndpointRejectons(MetricsProcessorUtil.
                getRejectedInvocationMetrics(periodEnum, MetricsConstants.CDS_REJECTION_METRICS_APP_UNAUTHENTICATED));

        //get performance metrics from sp
        responseMetricsListModel.setPerformance(MetricsProcessorUtil.getPerformanceMetrics(periodEnum,
                totalTransactionsList));

        //get availability metrics from sp
        responseMetricsListModel.setAvailability(MetricsProcessorUtil.getAvailabilityMetrics(periodEnum));

        return responseMetricsListModel;
    }

}
