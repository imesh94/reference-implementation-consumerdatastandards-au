/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.metrics.endpoint.util;

import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
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
import com.wso2.openbanking.cds.metrics.model.ResponseMetricsListModel;
import com.wso2.openbanking.cds.metrics.util.PeriodEnum;

import java.math.BigDecimal;
import java.util.List;

/**
 * Class containing utility methods to map ResponseMetricsListModel to API Response DTO.
 */
public class MappingUtil {

    /**
     * private constructor.
     */
    private MappingUtil() {
    }

    /**
     * Map the ResponseMetricsListModel to ResponseMetricsListDTO.
     *
     * @param metricsListModel - Metrics model used in the service layer
     * @return - Specification supported DTO
     */
    public static ResponseMetricsListDTO getResponseMetricsListDTO(ResponseMetricsListModel metricsListModel,
                                                                   String period) {

        ResponseMetricsListDTO metricsListDTO = new ResponseMetricsListDTO();
        LinksDTO linksDTO = new LinksDTO();
        PeriodEnum periodEnum = PeriodEnum.fromString(period);

        ResponseMetricsListDataDTO metricsListDataDTO = getResponseMetricsListDataDTO(metricsListModel, periodEnum);
        metricsListDTO.setData(metricsListDataDTO);
        linksDTO.setSelf(getCDSAdminSelfLink(period));
        metricsListDTO.setLinks(linksDTO);
        return metricsListDTO;
    }

    private static ResponseMetricsListDataDTO getResponseMetricsListDataDTO(ResponseMetricsListModel metricsListModel
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
     * Get availability metrics DTO from availability list and period enum
     *
     * @param availability
     * @param period
     * @return
     */
    private static AvailabilityMetricsDTO getAvailabilityMetricsDTO(List<BigDecimal> availability, PeriodEnum period) {

        AvailabilityMetricsDTO availabilityMetricsDTO = new AvailabilityMetricsDTO();

        if (!availability.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            availabilityMetricsDTO.setCurrentMonth(availability.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            availabilityMetricsDTO.setPreviousMonths(availability);
        }
        return availabilityMetricsDTO;
    }

    private static InvocationMetricsDTO getInvocationMetricsDTO(ResponseMetricsListModel metricsListModel,
                                                                PeriodEnum period) {

        InvocationMetricsDTO invocationMetricsDTO = new InvocationMetricsDTO();

        //invocation metrics
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

    private static AverageResponseMetricsDTO getAverageResponseMetricsDTO(ResponseMetricsListModel metricsListModel,
                                                                          PeriodEnum period) {

        AverageResponseMetricsDTO averageResponseMetricsDTO = new AverageResponseMetricsDTO();

        //average response metrics
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

    private static InvocationMetricsUnauthenticatedDTO getInvocationMetricsUnauthenticatedDTO(
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

    private static InvocationMetricsHighPriorityDTO getInvocationMetricsHighPriorityDTO(
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

    private static InvocationMetricsLowPriorityDTO getInvocationMetricsLowPriorityDTO(
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

    private static InvocationMetricsUnattendedDTO getInvocationMetricsUnattendedDTO(
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

    private static InvocationMetricsLargePayloadDTO getInvocationMetricsLargePayloadDTO(
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

    private static AverageResponseMetricsUnauthenticatedDTO getAverageResponseMetricsUnauthenticatedDTO(
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

    private static AverageResponseMetricsHighPriorityDTO getAverageResponseMetricsHighPriorityDTO(
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

    private static AverageResponseMetricsLowPriorityDTO getAverageResponseMetricsLowPriorityDTO(
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

    private static AverageResponseMetricsUnattendedDTO getAverageResponseMetricsUnattendedDTO(
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

    private static AverageResponseMetricsLargePayloadDTO getAverageResponseMetricsLargePayloadDTO(
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

    private static AverageTPSMetricsDTO getaverageTPSMetricsDTO(List<BigDecimal> averageTPSList, PeriodEnum
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

    private static PeakTPSMetricsDTO getPeakTPSMetricsDTO(List<BigDecimal> peakTPSList, PeriodEnum period) {

        PeakTPSMetricsDTO peakTPSMetricsDTO = new PeakTPSMetricsDTO();
        if (!peakTPSList.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            peakTPSMetricsDTO.setCurrentDay(peakTPSList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            peakTPSMetricsDTO.setPreviousDays(peakTPSList);
        }
        return peakTPSMetricsDTO;
    }

    private static ErrorMetricsDTO getErrorsDTO(List<BigDecimal> errorList, PeriodEnum period) {

        ErrorMetricsDTO errorMetricsDTO = new ErrorMetricsDTO();
        if (!errorList.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            errorMetricsDTO.setCurrentDay(errorList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            errorMetricsDTO.setPreviousDays(errorList);
        }
        return errorMetricsDTO;
    }

    private static RejectionMetricsDTO getRejectionsDTO(ResponseMetricsListModel metricsListModel,
                                                        PeriodEnum period) {

        RejectionMetricsDTO rejectionMetricsDTO = new RejectionMetricsDTO();

        //rejection metrics
        rejectionMetricsDTO.setAuthenticated(getRejectionMetricsAuthenticatedDTO(
                metricsListModel.getAuthenticatedEndpointRejections(), period));
        rejectionMetricsDTO.setUnauthenticated(getRejectionMetricsUnauthenticatedDTO(
                metricsListModel.getUnauthenticatedEndpointRejectons(), period));

        return rejectionMetricsDTO;
    }

    private static RejectionMetricsAuthenticatedDTO getRejectionMetricsAuthenticatedDTO(
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

    private static RejectionMetricsUnauthenticatedDTO getRejectionMetricsUnauthenticatedDTO(
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

    private static PerformanceMetricsDTO getPerformanceDTO(List<BigDecimal> performanceList, PeriodEnum period) {

        PerformanceMetricsDTO performanceMetricsDTO = new PerformanceMetricsDTO();
        if (!performanceList.isEmpty() && (PeriodEnum.ALL == period || PeriodEnum.CURRENT == period)) {
            performanceMetricsDTO.setCurrentDay(performanceList.remove(0));
        }
        if (PeriodEnum.ALL == period || PeriodEnum.HISTORIC == period) {
            performanceMetricsDTO.setPreviousDays(performanceList);
        }
        return performanceMetricsDTO;
    }

    private static SessionCountMetricsDTO getSessionCountDTO(List<BigDecimal> sessionCountList, PeriodEnum
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

    /**
     * Get Admin API Self URL.
     *
     * @param period - period (ALL, CURRENT, HISTORIC)
     * @return - self-url string
     */
    private static String getCDSAdminSelfLink(String period) {

        String adminAPIBaseURL = OpenBankingCDSConfigParser.getInstance().getAdminAPISelfLink();
        return String.format("%smetrics?period=%s", adminAPIBaseURL, period);
    }

}
