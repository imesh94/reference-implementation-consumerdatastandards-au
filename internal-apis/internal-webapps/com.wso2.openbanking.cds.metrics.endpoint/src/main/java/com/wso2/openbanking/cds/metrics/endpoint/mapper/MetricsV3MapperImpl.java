/*
 * Copyright (c) 2021-2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.endpoint.mapper;

import com.wso2.openbanking.cds.metrics.endpoint.model.AvailabilityMetricsDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.AverageResponseMetricsDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.AverageResponseMetricsHighPriorityDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.AverageResponseMetricsLargePayloadDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.AverageResponseMetricsLowPriorityDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.AverageResponseMetricsUnattendedDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.AverageResponseMetricsUnauthenticatedDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.AverageTPSMetricsDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.ErrorMetricsDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.InvocationMetricsDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.InvocationMetricsHighPriorityDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.InvocationMetricsLargePayloadDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.InvocationMetricsLowPriorityDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.InvocationMetricsUnattendedDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.InvocationMetricsUnauthenticatedDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.LinksDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.PeakTPSMetricsDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.PerformanceMetricsDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.RejectionMetricsAuthenticatedDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.RejectionMetricsDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.RejectionMetricsUnauthenticatedDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.ResponseMetricsListDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.ResponseMetricsListDataDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.SessionCountMetricsDTO;
import com.wso2.openbanking.cds.metrics.endpoint.model.v5.ResponseMetricsListV5DTO;
import com.wso2.openbanking.cds.metrics.endpoint.util.CommonUtil;
import com.wso2.openbanking.cds.metrics.model.MetricsResponseModel;
import com.wso2.openbanking.cds.metrics.model.MetricsV5ResponseModel;
import com.wso2.openbanking.cds.metrics.util.PeriodEnum;

import java.math.BigDecimal;
import java.util.List;

/**
 * Class containing methods to map MetricsResponseModel to Metrics V3 API Response DTO.
 */
public class MetricsV3MapperImpl implements MetricsMapper {

    /**
     * {@inheritDoc}
     */
    public ResponseMetricsListV5DTO getResponseMetricsListV5DTO(MetricsV5ResponseModel metricsListModel,
                                                                String period) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ResponseMetricsListDTO getResponseMetricsListDTO(MetricsResponseModel metricsListModel, String period) {

        ResponseMetricsListDTO metricsListDTO = new ResponseMetricsListDTO();
        LinksDTO linksDTO = new LinksDTO();
        PeriodEnum periodEnum = PeriodEnum.fromString(period);

        ResponseMetricsListDataDTO metricsListDataDTO = getResponseMetricsListDataDTO(metricsListModel, periodEnum);
        metricsListDTO.setData(metricsListDataDTO);
        linksDTO.setSelf(CommonUtil.getCDSAdminSelfLink(period));
        metricsListDTO.setLinks(linksDTO);

        return metricsListDTO;
    }

    /**
     * Get ResponseMetricsListDataDTO from MetricsResponseModel and period enum.
     *
     * @param metricsListModel - Metrics model
     * @param period           - period enum
     * @return ResponseMetricsListDataDTO
     */
    private ResponseMetricsListDataDTO getResponseMetricsListDataDTO(MetricsResponseModel metricsListModel
            , PeriodEnum period) {

        ResponseMetricsListDataDTO responseMetricsListDataDTO = new ResponseMetricsListDataDTO();
        responseMetricsListDataDTO.setRequestTime(metricsListModel.getRequestTime());
        responseMetricsListDataDTO.setRecipientCount(metricsListModel.getRecipientCount());
        responseMetricsListDataDTO.setCustomerCount(metricsListModel.getCustomerCount());
        responseMetricsListDataDTO.setInvocations(getInvocationMetricsDTO(metricsListModel, period));
        responseMetricsListDataDTO.setAverageResponse(getAverageResponseMetricsDTO(metricsListModel, period));
        responseMetricsListDataDTO.setAverageTps(getaverageTPSMetricsDTO(metricsListModel.getAverageTPS(), period));
        responseMetricsListDataDTO.setPeakTps(getPeakTPSMetricsDTO(metricsListModel.getPeakTPS(), period));
        responseMetricsListDataDTO.setErrors(getErrorsDTO(metricsListModel.getErrors(), period));
        responseMetricsListDataDTO.setRejections(getRejectionsDTO(metricsListModel, period));
        responseMetricsListDataDTO.setPerformance(getPerformanceDTO(metricsListModel.getPerformance(), period));
        responseMetricsListDataDTO.setSessionCount(getSessionCountDTO(metricsListModel.getSessionCount(), period));
        responseMetricsListDataDTO.setAvailability(
                getAvailabilityMetricsDTO(metricsListModel.getAvailability(), period));

        return responseMetricsListDataDTO;
    }

    /**
     * Get availability metrics DTO from availability list and period enum.
     *
     * @param availability - list of availability data
     * @param period       - period enum
     * @return AvailabilityMetricsDTO
     */
    private AvailabilityMetricsDTO getAvailabilityMetricsDTO(List<BigDecimal> availability, PeriodEnum period) {

        AvailabilityMetricsDTO availabilityMetricsDTO = new AvailabilityMetricsDTO();

        if (!availability.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            availabilityMetricsDTO.setCurrentMonth(availability.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            availabilityMetricsDTO.setPreviousMonths(availability);
        }
        return availabilityMetricsDTO;
    }

    /**
     * Get invocation metrics DTO from metrics list model and period enum.
     *
     * @param metricsListModel - Metrics model
     * @param period           - period enum
     * @return InvocationMetricsDTO
     */
    private InvocationMetricsDTO getInvocationMetricsDTO(MetricsResponseModel metricsListModel, PeriodEnum period) {

        InvocationMetricsDTO invocationMetricsDTO = new InvocationMetricsDTO();
        invocationMetricsDTO.setUnauthenticated(getInvocationMetricsUnauthenticatedDTO(
                metricsListModel.getInvocationUnauthenticated(), period));
        invocationMetricsDTO.setHighPriority(getInvocationMetricsHighPriorityDTO(
                metricsListModel.getInvocationHighPriority(), period));
        invocationMetricsDTO.setLowPriority(getInvocationMetricsLowPriorityDTO(
                metricsListModel.getInvocationLowPriority(), period));
        invocationMetricsDTO.setUnattended(getInvocationMetricsUnattendedDTO(
                metricsListModel.getInvocationUnattended(), period));
        invocationMetricsDTO.setLargePayload(getInvocationMetricsLargePayloadDTO(
                metricsListModel.getInvocationLargePayload(), period));

        return invocationMetricsDTO;
    }

    /**
     * Get Average response metrics DTO from metrics list model and period enum.
     *
     * @param metricsListModel - Metrics model
     * @param period           - period enum
     * @return AverageResponseMetricsDTO
     */
    private AverageResponseMetricsDTO getAverageResponseMetricsDTO(
            MetricsResponseModel metricsListModel, PeriodEnum period) {

        AverageResponseMetricsDTO averageResponseMetricsDTO = new AverageResponseMetricsDTO();
        averageResponseMetricsDTO.setUnauthenticated(getAverageResponseMetricsUnauthenticatedDTO(
                metricsListModel.getAverageResponseUnauthenticated(), period));
        averageResponseMetricsDTO.setHighPriority(getAverageResponseMetricsHighPriorityDTO(
                metricsListModel.getAverageResponseHighPriority(), period));
        averageResponseMetricsDTO.setLowPriority(getAverageResponseMetricsLowPriorityDTO(
                metricsListModel.getAverageResponseLowPriority(), period));
        averageResponseMetricsDTO.setUnattended(getAverageResponseMetricsUnattendedDTO(
                metricsListModel.getAverageResponseUnattended(), period));
        averageResponseMetricsDTO.setLargePayload(getAverageResponseMetricsLargePayloadDTO(
                metricsListModel.getAverageResponseLargePayload(), period));

        return averageResponseMetricsDTO;
    }

    /**
     * Get InvocationMetrics Unauthenticated DTO from invocation unauthenticated List model and period enum.
     *
     * @param invocationUnauthenticatedList - invocation unauthenticated list
     * @param period                        - period enum
     * @return InvocationMetricsUnauthenticatedDTO
     */
    private InvocationMetricsUnauthenticatedDTO getInvocationMetricsUnauthenticatedDTO(
            List<BigDecimal> invocationUnauthenticatedList, PeriodEnum period) {

        InvocationMetricsUnauthenticatedDTO invocationMetricsUnauthenticatedDTO =
                new InvocationMetricsUnauthenticatedDTO();
        if (!invocationUnauthenticatedList.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            invocationMetricsUnauthenticatedDTO.setCurrentDay(invocationUnauthenticatedList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            invocationMetricsUnauthenticatedDTO.setPreviousDays(invocationUnauthenticatedList);
        }
        return invocationMetricsUnauthenticatedDTO;
    }

    /**
     * Get InvocationMetrics HighPriority DTO from invocation high priority List model and period enum.
     *
     * @param invocationHighPriorityList - invocation high priority list
     * @param period                     - period enum
     * @return InvocationMetricsHighPriorityDTO
     */
    private InvocationMetricsHighPriorityDTO getInvocationMetricsHighPriorityDTO(
            List<BigDecimal> invocationHighPriorityList, PeriodEnum period) {

        InvocationMetricsHighPriorityDTO invocationMetricsHighPriorityDTO = new InvocationMetricsHighPriorityDTO();
        if (!invocationHighPriorityList.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            invocationMetricsHighPriorityDTO.setCurrentDay(invocationHighPriorityList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            invocationMetricsHighPriorityDTO.setPreviousDays(invocationHighPriorityList);
        }
        return invocationMetricsHighPriorityDTO;
    }

    /**
     * Get InvocationMetrics LowPriority DTO  from invocation high priority List model and period enum.
     *
     * @param invocationLowPriorityList - invocation low priority list
     * @param period                    - period enum
     * @return InvocationMetricsLowPriorityDTO
     */
    private InvocationMetricsLowPriorityDTO getInvocationMetricsLowPriorityDTO(
            List<BigDecimal> invocationLowPriorityList, PeriodEnum period) {

        InvocationMetricsLowPriorityDTO invocationMetricsLowPriorityDTO = new InvocationMetricsLowPriorityDTO();
        if (!invocationLowPriorityList.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            invocationMetricsLowPriorityDTO.setCurrentDay(invocationLowPriorityList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            invocationMetricsLowPriorityDTO.setPreviousDays(invocationLowPriorityList);
        }
        return invocationMetricsLowPriorityDTO;
    }

    /**
     * Get InvocationMetrics Unattended DTO from invocation unattended List model and period enum.
     *
     * @param invocationUnattendedList - invocation unattended list
     * @param period                   - period enum
     * @return InvocationMetricsUnattendedDTO
     */
    private InvocationMetricsUnattendedDTO getInvocationMetricsUnattendedDTO(
            List<BigDecimal> invocationUnattendedList, PeriodEnum period) {

        InvocationMetricsUnattendedDTO invocationMetricsUnattendedDTO =
                new InvocationMetricsUnattendedDTO();
        if (!invocationUnattendedList.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            invocationMetricsUnattendedDTO.setCurrentDay(invocationUnattendedList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            invocationMetricsUnattendedDTO.setPreviousDays(invocationUnattendedList);
        }
        return invocationMetricsUnattendedDTO;
    }

    /**
     * Get InvocationMetrics LargePayload DTO from invocation large payload List model and period enum.
     *
     * @param invocationLargePayloadList - invocation large payload list
     * @param period                     - period enum
     * @return InvocationMetricsLargePayloadDTO
     */
    private InvocationMetricsLargePayloadDTO getInvocationMetricsLargePayloadDTO(
            List<BigDecimal> invocationLargePayloadList, PeriodEnum period) {

        InvocationMetricsLargePayloadDTO invocationMetricsLargePayloadDTO =
                new InvocationMetricsLargePayloadDTO();
        if (!invocationLargePayloadList.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            invocationMetricsLargePayloadDTO.setCurrentDay(invocationLargePayloadList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            invocationMetricsLargePayloadDTO.setPreviousDays(invocationLargePayloadList);
        }
        return invocationMetricsLargePayloadDTO;
    }

    /**
     * Get AverageResponseMetricsUnauthenticatedDTO from average response unauthenticated List model and period enum.
     *
     * @param averageResponseUnauthenticatedList - average response unauthenticated list
     * @param period                             - period enum
     * @return AverageResponseMetricsUnauthenticatedDTO
     */
    private AverageResponseMetricsUnauthenticatedDTO getAverageResponseMetricsUnauthenticatedDTO(
            List<BigDecimal> averageResponseUnauthenticatedList, PeriodEnum period) {

        AverageResponseMetricsUnauthenticatedDTO averageResponseMetricsUnauthenticatedDTO =
                new AverageResponseMetricsUnauthenticatedDTO();
        if (!averageResponseUnauthenticatedList.isEmpty() && (PeriodEnum.ALL == period ||
                PeriodEnum.CURRENT == period)) {
            averageResponseMetricsUnauthenticatedDTO.setCurrentDay(averageResponseUnauthenticatedList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            averageResponseMetricsUnauthenticatedDTO.setPreviousDays(averageResponseUnauthenticatedList);
        }
        return averageResponseMetricsUnauthenticatedDTO;
    }

    /**
     * Get AverageResponseMetricsHighPriorityDTO from average response high priority List model and period enum.
     *
     * @param averageResponseHighPriorityList - average response high priority list
     * @param period                          - period enum
     * @return AverageResponseMetricsHighPriorityDTO
     */
    private AverageResponseMetricsHighPriorityDTO getAverageResponseMetricsHighPriorityDTO(
            List<BigDecimal> averageResponseHighPriorityList, PeriodEnum period) {

        AverageResponseMetricsHighPriorityDTO averageResponseMetricsHighPriorityDTO =
                new AverageResponseMetricsHighPriorityDTO();
        if (!averageResponseHighPriorityList.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            averageResponseMetricsHighPriorityDTO.setCurrentDay(averageResponseHighPriorityList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            averageResponseMetricsHighPriorityDTO.setPreviousDays(averageResponseHighPriorityList);
        }
        return averageResponseMetricsHighPriorityDTO;
    }

    /**
     * Get AverageResponseMetricsLowPriorityDTO from average response low priority List model and period enum.
     *
     * @param averageResponseLowPriorityList - average response low priority list
     * @param period                         - period enum
     * @return AverageResponseMetricsLowPriorityDTO
     */
    private AverageResponseMetricsLowPriorityDTO getAverageResponseMetricsLowPriorityDTO(
            List<BigDecimal> averageResponseLowPriorityList, PeriodEnum period) {

        AverageResponseMetricsLowPriorityDTO averageResponseMetricsLowPriorityDTO =
                new AverageResponseMetricsLowPriorityDTO();
        if (!averageResponseLowPriorityList.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            averageResponseMetricsLowPriorityDTO.setCurrentDay(averageResponseLowPriorityList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            averageResponseMetricsLowPriorityDTO.setPreviousDays(averageResponseLowPriorityList);
        }
        return averageResponseMetricsLowPriorityDTO;
    }

    /**
     * Get AverageResponseMetricsUnattendedDTO from average response unattended List model and period enum.
     *
     * @param averageResponseUnattendedList - average response unattended list
     * @param period                        - period enum
     * @return AverageResponseMetricsUnattendedDTO
     */
    private AverageResponseMetricsUnattendedDTO getAverageResponseMetricsUnattendedDTO(
            List<BigDecimal> averageResponseUnattendedList, PeriodEnum period) {

        AverageResponseMetricsUnattendedDTO averageResponseMetricsUnattendedDTO =
                new AverageResponseMetricsUnattendedDTO();
        if (!averageResponseUnattendedList.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            averageResponseMetricsUnattendedDTO.setCurrentDay(averageResponseUnattendedList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            averageResponseMetricsUnattendedDTO.setPreviousDays(averageResponseUnattendedList);
        }
        return averageResponseMetricsUnattendedDTO;
    }

    /**
     * Get AverageResponseMetricsLargePayloadDTO from average response large payload List model and period enum.
     *
     * @param averageResponseLargePayloadList - average response large payload list
     * @param period                          - period enum
     * @return AverageResponseMetricsLargePayloadDTO
     */
    private AverageResponseMetricsLargePayloadDTO getAverageResponseMetricsLargePayloadDTO(
            List<BigDecimal> averageResponseLargePayloadList, PeriodEnum period) {

        AverageResponseMetricsLargePayloadDTO averageResponseMetricsLargePayloadDTO =
                new AverageResponseMetricsLargePayloadDTO();
        if (!averageResponseLargePayloadList.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            averageResponseMetricsLargePayloadDTO.setCurrentDay(averageResponseLargePayloadList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            averageResponseMetricsLargePayloadDTO.setPreviousDays(averageResponseLargePayloadList);
        }
        return averageResponseMetricsLargePayloadDTO;
    }

    /**
     * Get Average TPS metrics DTO from average TPS list and period enum.
     *
     * @param averageTPSList - list of average TPS data
     * @param period         - period enum
     * @return AverageTPSMetricsDTO
     */
    private AverageTPSMetricsDTO getaverageTPSMetricsDTO(List<BigDecimal> averageTPSList, PeriodEnum
            period) {

        AverageTPSMetricsDTO averageTPSMetricsDTO = new AverageTPSMetricsDTO();
        if (!averageTPSList.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            averageTPSMetricsDTO.setCurrentDay(averageTPSList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            averageTPSMetricsDTO.setPreviousDays(averageTPSList);
        }
        return averageTPSMetricsDTO;
    }

    /**
     * Get Peak TPS metrics DTO from peak TPS list and period enum.
     *
     * @param peakTPSList - list of peak TPS data
     * @param period      - period enum
     * @return PeakTPSMetricsDTO
     */
    private PeakTPSMetricsDTO getPeakTPSMetricsDTO(List<BigDecimal> peakTPSList, PeriodEnum period) {

        PeakTPSMetricsDTO peakTPSMetricsDTO = new PeakTPSMetricsDTO();
        if (!peakTPSList.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            peakTPSMetricsDTO.setCurrentDay(peakTPSList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            peakTPSMetricsDTO.setPreviousDays(peakTPSList);
        }
        return peakTPSMetricsDTO;
    }

    /**
     * Get Error metrics DTO from errors list and period enum.
     *
     * @param errorList - list of error data
     * @param period    - period enum
     * @return ErrorMetricsDTO
     */
    private ErrorMetricsDTO getErrorsDTO(List<BigDecimal> errorList, PeriodEnum period) {

        ErrorMetricsDTO errorMetricsDTO = new ErrorMetricsDTO();
        if (!errorList.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            errorMetricsDTO.setCurrentDay(errorList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            errorMetricsDTO.setPreviousDays(errorList);
        }
        return errorMetricsDTO;
    }

    /**
     * Get Rejection metrics DTO from metrics list model and period enum.
     *
     * @param metricsListModel - Metrics model
     * @param period           - period enum
     * @return RejectionMetricsDTO
     */
    private RejectionMetricsDTO getRejectionsDTO(MetricsResponseModel metricsListModel,
                                                 PeriodEnum period) {

        RejectionMetricsDTO rejectionMetricsDTO = new RejectionMetricsDTO();

        //rejection metrics
        rejectionMetricsDTO.setAuthenticated(getRejectionMetricsAuthenticatedDTO(
                metricsListModel.getAuthenticatedEndpointRejections(), period));
        rejectionMetricsDTO.setUnauthenticated(getRejectionMetricsUnauthenticatedDTO(
                metricsListModel.getUnauthenticatedEndpointRejections(), period));

        return rejectionMetricsDTO;
    }

    /**
     * Get Rejection metrics DTO from metrics list model and period enum.
     *
     * @param rejectionAuthenticatedList - rejection authenticated list
     * @param period                     - period enum
     * @return RejectionMetricsAuthenticatedDTO
     */
    private RejectionMetricsAuthenticatedDTO getRejectionMetricsAuthenticatedDTO(
            List<BigDecimal> rejectionAuthenticatedList, PeriodEnum period) {

        RejectionMetricsAuthenticatedDTO rejectionMetricsAuthenticatedDTO =
                new RejectionMetricsAuthenticatedDTO();
        if (!rejectionAuthenticatedList.isEmpty() && (PeriodEnum.ALL == period ||
                PeriodEnum.CURRENT == period)) {
            rejectionMetricsAuthenticatedDTO.setCurrentDay(rejectionAuthenticatedList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            rejectionMetricsAuthenticatedDTO.setPreviousDays(rejectionAuthenticatedList);
        }
        return rejectionMetricsAuthenticatedDTO;
    }

    /**
     * Get Rejection metrics DTO from metrics list model and period enum.
     *
     * @param rejectionUnauthenticatedList - rejection unauthenticated list
     * @param period                       - period enum
     * @return RejectionMetricsUnauthenticatedDTO
     */
    private RejectionMetricsUnauthenticatedDTO getRejectionMetricsUnauthenticatedDTO(
            List<BigDecimal> rejectionUnauthenticatedList, PeriodEnum period) {

        RejectionMetricsUnauthenticatedDTO rejectionMetricsUnauthenticatedDTO =
                new RejectionMetricsUnauthenticatedDTO();
        if (!rejectionUnauthenticatedList.isEmpty() && (PeriodEnum.ALL == period ||
                PeriodEnum.CURRENT == period)) {
            rejectionMetricsUnauthenticatedDTO.setCurrentDay(rejectionUnauthenticatedList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            rejectionMetricsUnauthenticatedDTO.setPreviousDays(rejectionUnauthenticatedList);
        }
        return rejectionMetricsUnauthenticatedDTO;
    }

    /**
     * Get Performance metrics DTO from performance list and period enum.
     *
     * @param performanceList - list of performance data
     * @param period          - period enum
     * @return PerformanceMetricsDTO
     */
    private PerformanceMetricsDTO getPerformanceDTO(List<BigDecimal> performanceList, PeriodEnum period) {

        PerformanceMetricsDTO performanceMetricsDTO = new PerformanceMetricsDTO();
        if (!performanceList.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            performanceMetricsDTO.setCurrentDay(performanceList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            performanceMetricsDTO.setPreviousDays(performanceList);
        }
        return performanceMetricsDTO;
    }

    /**
     * Get Session count metrics DTO from session count list and period enum.
     *
     * @param sessionCountList - list of session count data
     * @param period           - period enum
     * @return SessionCountMetricsDTO
     */
    private SessionCountMetricsDTO getSessionCountDTO(List<BigDecimal> sessionCountList, PeriodEnum
            period) {

        SessionCountMetricsDTO sessionCountMetricsDTO = new SessionCountMetricsDTO();
        if (!sessionCountList.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            sessionCountMetricsDTO.setCurrentDay(sessionCountList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            sessionCountMetricsDTO.setPreviousDays(sessionCountList);
        }
        return sessionCountMetricsDTO;
    }

}
