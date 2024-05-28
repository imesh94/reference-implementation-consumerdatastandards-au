/*
 * Copyright (c) 2021-2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.model;

import com.wso2.openbanking.cds.metrics.util.AspectEnum;
import com.wso2.openbanking.cds.metrics.util.PriorityEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Model class for CDS Metrics V3 data.
 * In this model, List<BigDecimal> is used to represent metrics that require representation of data for multiple days.
 * <p>
 * For the CURRENT period, each list will contain a single value representing the metric for the current day.
 * For the HISTORIC period, each list will contain seven elements representing the metrics for the last seven days.
 * For the ALL period, the first element in the list represents the current day and the subsequent elements
 * represent the metrics for the previous 7 days starting from yesterday.
 */
public class MetricsResponseModel {

    private String requestTime;
    private int recipientCount;
    private int customerCount;
    private List<BigDecimal> errors;
    private List<BigDecimal> peakTPS;
    private List<BigDecimal> averageTPS;
    private List<BigDecimal> performance;
    private List<BigDecimal> sessionCount;
    private List<BigDecimal> availability;

    // Invocations
    private List<BigDecimal> invocationUnauthenticated;
    private List<BigDecimal> invocationHighPriority;
    private List<BigDecimal> invocationLowPriority;
    private List<BigDecimal> invocationUnattended;
    private List<BigDecimal> invocationLargePayload;

    // Average response
    private List<BigDecimal> averageResponseUnauthenticated;
    private List<BigDecimal> averageResponseHighPriority;
    private List<BigDecimal> averageResponseLowPriority;
    private List<BigDecimal> averageResponseUnattended;
    private List<BigDecimal> averageResponseLargePayload;

    // Rejections
    private List<BigDecimal> authenticatedEndpointRejections;
    private List<BigDecimal> unauthenticatedEndpointRejections;


    public MetricsResponseModel(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public int getRecipientCount() {
        return recipientCount;
    }

    public void setRecipientCount(int recipientCount) {
        this.recipientCount = recipientCount;
    }

    public int getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(int customerCount) {
        this.customerCount = customerCount;
    }

    public List<BigDecimal> getInvocationUnauthenticated() {
        return invocationUnauthenticated;
    }

    public void setInvocationUnauthenticated(List<BigDecimal> invocationUnauthenticated) {
        this.invocationUnauthenticated = invocationUnauthenticated;
    }

    public List<BigDecimal> getInvocationHighPriority() {
        return invocationHighPriority;
    }

    public void setInvocationHighPriority(List<BigDecimal> invocationHighPriority) {
        this.invocationHighPriority = invocationHighPriority;
    }

    public List<BigDecimal> getInvocationLowPriority() {
        return invocationLowPriority;
    }

    public void setInvocationLowPriority(List<BigDecimal> invocationLowPriority) {
        this.invocationLowPriority = invocationLowPriority;
    }

    public List<BigDecimal> getInvocationUnattended() {
        return invocationUnattended;
    }

    public void setInvocationUnattended(List<BigDecimal> invocationUnattended) {
        this.invocationUnattended = invocationUnattended;
    }

    public List<BigDecimal> getInvocationLargePayload() {
        return invocationLargePayload;
    }

    public void setInvocationLargePayload(List<BigDecimal> invocationLargePayload) {
        this.invocationLargePayload = invocationLargePayload;
    }

    public void setInvocations(Map<PriorityEnum, List<BigDecimal>> invocationMap) {

        setInvocationUnauthenticated(invocationMap.get(PriorityEnum.UNAUTHENTICATED));
        setInvocationHighPriority(invocationMap.get(PriorityEnum.HIGH_PRIORITY));
        setInvocationLowPriority(invocationMap.get(PriorityEnum.LOW_PRIORITY));
        setInvocationUnattended(invocationMap.get(PriorityEnum.UNATTENDED));
        setInvocationLargePayload(invocationMap.get(PriorityEnum.LARGE_PAYLOAD));
    }

    public List<BigDecimal> getAverageResponseUnauthenticated() {
        return averageResponseUnauthenticated;
    }

    public void setAverageResponseUnauthenticated(List<BigDecimal> averageResponseUnauthenticated) {
        this.averageResponseUnauthenticated = averageResponseUnauthenticated;
    }

    public List<BigDecimal> getAverageResponseHighPriority() {
        return averageResponseHighPriority;
    }

    public void setAverageResponseHighPriority(List<BigDecimal> averageResponseHighPriority) {
        this.averageResponseHighPriority = averageResponseHighPriority;
    }

    public List<BigDecimal> getAverageResponseLowPriority() {
        return averageResponseLowPriority;
    }

    public void setAverageResponseLowPriority(List<BigDecimal> averageResponseLowPriority) {
        this.averageResponseLowPriority = averageResponseLowPriority;
    }

    public List<BigDecimal> getAverageResponseUnattended() {
        return averageResponseUnattended;
    }

    public void setAverageResponseUnattended(List<BigDecimal> averageResponseUnattended) {
        this.averageResponseUnattended = averageResponseUnattended;
    }

    public List<BigDecimal> getAverageResponseLargePayload() {
        return averageResponseLargePayload;
    }

    public void setAverageResponseLargePayload(List<BigDecimal> averageResponseLargePayload) {
        this.averageResponseLargePayload = averageResponseLargePayload;
    }

    public void setAverageResponseTime(Map<PriorityEnum, List<BigDecimal>> averageResponseMap) {

        setAverageResponseUnauthenticated(averageResponseMap.get(PriorityEnum.UNAUTHENTICATED));
        setAverageResponseHighPriority(averageResponseMap.get(PriorityEnum.HIGH_PRIORITY));
        setAverageResponseLowPriority(averageResponseMap.get(PriorityEnum.LOW_PRIORITY));
        setAverageResponseUnattended(averageResponseMap.get(PriorityEnum.UNATTENDED));
        setAverageResponseLargePayload(averageResponseMap.get(PriorityEnum.LARGE_PAYLOAD));
    }

    public List<BigDecimal> getAverageTPS() {
        return averageTPS;
    }

    public void setAverageTPS(List<BigDecimal> averageTPS) {
        this.averageTPS = averageTPS;
    }

    public List<BigDecimal> getPeakTPS() {
        return peakTPS;
    }

    public void setPeakTPS(List<BigDecimal> peakTPS) {
        this.peakTPS = peakTPS;
    }

    public List<BigDecimal> getErrors() {
        return errors;
    }

    public void setErrors(List<BigDecimal> errors) {
        this.errors = errors;
    }

    public List<BigDecimal> getAuthenticatedEndpointRejections() {
        return authenticatedEndpointRejections;
    }

    public void setAuthenticatedEndpointRejections(List<BigDecimal> authenticatedEndpointRejections) {
        this.authenticatedEndpointRejections = authenticatedEndpointRejections;
    }

    public List<BigDecimal> getUnauthenticatedEndpointRejections() {
        return unauthenticatedEndpointRejections;
    }

    public void setUnauthenticatedEndpointRejections(List<BigDecimal> unauthenticatedEndpointRejectons) {
        this.unauthenticatedEndpointRejections = unauthenticatedEndpointRejectons;
    }

    public void setRejections(Map<AspectEnum, List<BigDecimal>> rejectionsMap) {

        setAuthenticatedEndpointRejections(rejectionsMap.get(AspectEnum.AUTHENTICATED));
        setUnauthenticatedEndpointRejections(rejectionsMap.get(AspectEnum.UNAUTHENTICATED));
    }

    public List<BigDecimal> getPerformance() {
        return performance;
    }

    public void setPerformance(List<BigDecimal> performance) {
        this.performance = performance;
    }

    public List<BigDecimal> getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(List<BigDecimal> sessionCount) {
        this.sessionCount = sessionCount;
    }

    public List<BigDecimal> getAvailability() {
        return availability;
    }

    public void setAvailability(List<BigDecimal> availability) {
        this.availability = availability;
    }
}
