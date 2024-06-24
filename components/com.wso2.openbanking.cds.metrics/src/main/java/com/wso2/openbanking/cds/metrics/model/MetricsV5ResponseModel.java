/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Model class for CDS Metrics V5 data.
 * In this model, List<BigDecimal> is used to represent metrics that require representation of data for multiple days.
 * <p>
 * For the CURRENT period, each list will contain a single value representing the metric for the current day.
 * For the HISTORIC period, each list will contain seven elements representing the metrics for the last seven days.
 * For the ALL period, the first element in the list represents the current day and the subsequent elements
 * represent the metrics for the previous 7 days starting from yesterday.
 */
public class MetricsV5ResponseModel {

    private String requestTime;
    private int customerCount;
    private int recipientCount;
    private List<Integer> sessionCount;

    // Availability
    private List<BigDecimal> availability;
    private List<BigDecimal> authenticatedAvailability;
    private List<BigDecimal> unauthenticatedAvailability;

    // Performance
    private List<BigDecimal> performance;
    private List<List<BigDecimal>> performanceUnauthenticated;
    private List<List<BigDecimal>> performanceHighPriority;
    private List<List<BigDecimal>> performanceLowPriority;
    private List<List<BigDecimal>> performanceUnattended;
    private List<List<BigDecimal>> performanceLargePayload;

    // Invocations
    private List<Integer> invocationUnauthenticated;
    private List<Integer> invocationHighPriority;
    private List<Integer> invocationLowPriority;
    private List<Integer> invocationUnattended;
    private List<Integer> invocationLargePayload;

    // Average response
    private List<BigDecimal> averageResponseUnauthenticated;
    private List<BigDecimal> averageResponseHighPriority;
    private List<BigDecimal> averageResponseLowPriority;
    private List<BigDecimal> averageResponseUnattended;
    private List<BigDecimal> averageResponseLargePayload;

    // Average TPS
    private List<BigDecimal> averageTPS;
    private List<BigDecimal> authenticatedAverageTPS;
    private List<BigDecimal> unauthenticatedAverageTPS;

    // Peak TPS
    private List<BigDecimal> peakTPS;
    private List<BigDecimal> authenticatedPeakTPS;
    private List<BigDecimal> unauthenticatedPeakTPS;

    // Errors
    private List<Integer> errors;
    private List<Map<String, Integer>> authenticatedErrors;
    private List<Map<String, Integer>> unauthenticatedErrors;

    // Rejections
    private List<Integer> authenticatedEndpointRejections;
    private List<Integer> unauthenticatedEndpointRejections;

    // Authorisations
    private int activeIndividualAuthorisationCount;
    private int activeNonIndividualAuthorisationCount;
    private List<AuthorisationMetric> newAuthorisationCount;
    private List<CustomerTypeCount> revokedAuthorisationCount;
    private List<CustomerTypeCount> amendedAuthorisationCount;
    private List<CustomerTypeCount> expiredAuthorisationCount;
    private List<Integer> abandonedConsentFlowCount;

    // Abandonment by stage
    private List<Integer> preIdentificationAbandonedConsentFlowCount;
    private List<Integer> preAuthenticationAbandonedConsentFlowCount;
    private List<Integer> preAccountSelectionAbandonedConsentFlowCount;
    private List<Integer> preAuthorisationAbandonedConsentFlowCount;
    private List<Integer> rejectedAbandonedConsentFlowCount;
    private List<Integer> failedTokenExchangeAbandonedConsentFlowCount;

    public MetricsV5ResponseModel(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public int getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(int customerCount) {
        this.customerCount = customerCount;
    }

    public int getRecipientCount() {
        return recipientCount;
    }

    public void setRecipientCount(int recipientCount) {
        this.recipientCount = recipientCount;
    }

    public List<Integer> getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(List<Integer> sessionCount) {
        this.sessionCount = sessionCount;
    }

    public List<BigDecimal> getAvailability() {
        return availability;
    }

    public void setAvailability(List<BigDecimal> availability) {
        this.availability = availability;
    }

    public List<BigDecimal> getAuthenticatedAvailability() {
        return authenticatedAvailability;
    }

    public void setAuthenticatedAvailability(List<BigDecimal> authenticatedAvailability) {
        this.authenticatedAvailability = authenticatedAvailability;
    }

    public List<BigDecimal> getUnauthenticatedAvailability() {
        return unauthenticatedAvailability;
    }

    public void setUnauthenticatedAvailability(List<BigDecimal> unauthenticatedAvailability) {
        this.unauthenticatedAvailability = unauthenticatedAvailability;
    }

    public List<BigDecimal> getPerformance() {
        return performance;
    }

    public void setPerformance(List<BigDecimal> performance) {
        this.performance = performance;
    }

    public List<List<BigDecimal>> getPerformanceUnauthenticated() {
        return performanceUnauthenticated;
    }

    public void setPerformanceUnauthenticated(List<List<BigDecimal>> performanceUnauthenticated) {
        this.performanceUnauthenticated = performanceUnauthenticated;
    }

    public List<List<BigDecimal>> getPerformanceHighPriority() {
        return performanceHighPriority;
    }

    public void setPerformanceHighPriority(List<List<BigDecimal>> performanceHighPriority) {
        this.performanceHighPriority = performanceHighPriority;
    }

    public List<List<BigDecimal>> getPerformanceLowPriority() {
        return performanceLowPriority;
    }

    public void setPerformanceLowPriority(List<List<BigDecimal>> performanceLowPriority) {
        this.performanceLowPriority = performanceLowPriority;
    }

    public List<List<BigDecimal>> getPerformanceUnattended() {
        return performanceUnattended;
    }

    public void setPerformanceUnattended(List<List<BigDecimal>> performanceUnattended) {
        this.performanceUnattended = performanceUnattended;
    }

    public List<List<BigDecimal>> getPerformanceLargePayload() {
        return performanceLargePayload;
    }

    public void setPerformanceLargePayload(List<List<BigDecimal>> performanceLargePayload) {
        this.performanceLargePayload = performanceLargePayload;
    }

    public List<Integer> getInvocationUnauthenticated() {
        return invocationUnauthenticated;
    }

    public void setInvocationUnauthenticated(List<Integer> invocationUnauthenticated) {
        this.invocationUnauthenticated = invocationUnauthenticated;
    }

    public List<Integer> getInvocationHighPriority() {
        return invocationHighPriority;
    }

    public void setInvocationHighPriority(List<Integer> invocationHighPriority) {
        this.invocationHighPriority = invocationHighPriority;
    }

    public List<Integer> getInvocationLowPriority() {
        return invocationLowPriority;
    }

    public void setInvocationLowPriority(List<Integer> invocationLowPriority) {
        this.invocationLowPriority = invocationLowPriority;
    }

    public List<Integer> getInvocationUnattended() {
        return invocationUnattended;
    }

    public void setInvocationUnattended(List<Integer> invocationUnattended) {
        this.invocationUnattended = invocationUnattended;
    }

    public List<Integer> getInvocationLargePayload() {
        return invocationLargePayload;
    }

    public void setInvocationLargePayload(List<Integer> invocationLargePayload) {
        this.invocationLargePayload = invocationLargePayload;
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

    public List<BigDecimal> getAverageTPS() {
        return averageTPS;
    }

    public void setAverageTPS(List<BigDecimal> averageTPS) {
        this.averageTPS = averageTPS;
    }

    public List<BigDecimal> getAuthenticatedAverageTPS() {
        return authenticatedAverageTPS;
    }

    public void setAuthenticatedAverageTPS(List<BigDecimal> authenticatedAverageTPS) {
        this.authenticatedAverageTPS = authenticatedAverageTPS;
    }

    public List<BigDecimal> getUnauthenticatedAverageTPS() {
        return unauthenticatedAverageTPS;
    }

    public void setUnauthenticatedAverageTPS(List<BigDecimal> unauthenticatedAverageTPS) {
        this.unauthenticatedAverageTPS = unauthenticatedAverageTPS;
    }

    public List<BigDecimal> getPeakTPS() {
        return peakTPS;
    }

    public void setPeakTPS(List<BigDecimal> peakTPS) {
        this.peakTPS = peakTPS;
    }

    public List<BigDecimal> getAuthenticatedPeakTPS() {
        return authenticatedPeakTPS;
    }

    public void setAuthenticatedPeakTPS(List<BigDecimal> authenticatedPeakTPS) {
        this.authenticatedPeakTPS = authenticatedPeakTPS;
    }

    public List<BigDecimal> getUnauthenticatedPeakTPS() {
        return unauthenticatedPeakTPS;
    }

    public void setUnauthenticatedPeakTPS(List<BigDecimal> unauthenticatedPeakTPS) {
        this.unauthenticatedPeakTPS = unauthenticatedPeakTPS;
    }

    public List<Integer> getErrors() {
        return errors;
    }

    public void setErrors(List<Integer> errors) {
        this.errors = errors;
    }

    public List<Map<String, Integer>> getAuthenticatedErrors() {
        return authenticatedErrors;
    }

    public void setAuthenticatedErrors(List<Map<String, Integer>> authenticatedErrors) {
        this.authenticatedErrors = authenticatedErrors;
    }

    public List<Map<String, Integer>> getUnauthenticatedErrors() {
        return unauthenticatedErrors;
    }

    public void setUnauthenticatedErrors(List<Map<String, Integer>> unauthenticatedErrors) {
        this.unauthenticatedErrors = unauthenticatedErrors;
    }

    public List<Integer> getAuthenticatedEndpointRejections() {
        return authenticatedEndpointRejections;
    }

    public void setAuthenticatedEndpointRejections(List<Integer> authenticatedEndpointRejections) {
        this.authenticatedEndpointRejections = authenticatedEndpointRejections;
    }

    public List<Integer> getUnauthenticatedEndpointRejections() {
        return unauthenticatedEndpointRejections;
    }

    public void setUnauthenticatedEndpointRejections(List<Integer> unauthenticatedEndpointRejections) {
        this.unauthenticatedEndpointRejections = unauthenticatedEndpointRejections;
    }

    public int getActiveIndividualAuthorisationCount() {
        return activeIndividualAuthorisationCount;
    }

    public void setActiveIndividualAuthorisationCount(int activeIndividualAuthorisationCount) {
        this.activeIndividualAuthorisationCount = activeIndividualAuthorisationCount;
    }

    public int getActiveNonIndividualAuthorisationCount() {
        return activeNonIndividualAuthorisationCount;
    }

    public void setActiveNonIndividualAuthorisationCount(int activeNonIndividualAuthorisationCount) {
        this.activeNonIndividualAuthorisationCount = activeNonIndividualAuthorisationCount;
    }

    public List<AuthorisationMetric> getNewAuthorisationCount() {
        return newAuthorisationCount;
    }

    public void setNewAuthorisationCount(List<AuthorisationMetric> newAuthorisationCount) {
        this.newAuthorisationCount = newAuthorisationCount;
    }

    public List<CustomerTypeCount> getRevokedAuthorisationCount() {
        return revokedAuthorisationCount;
    }

    public void setRevokedAuthorisationCount(List<CustomerTypeCount> revokedAuthorisationCount) {
        this.revokedAuthorisationCount = revokedAuthorisationCount;
    }

    public List<CustomerTypeCount> getAmendedAuthorisationCount() {
        return amendedAuthorisationCount;
    }

    public void setAmendedAuthorisationCount(List<CustomerTypeCount> amendedAuthorisationCount) {
        this.amendedAuthorisationCount = amendedAuthorisationCount;
    }

    public List<CustomerTypeCount> getExpiredAuthorisationCount() {
        return expiredAuthorisationCount;
    }

    public void setExpiredAuthorisationCount(List<CustomerTypeCount> expiredAuthorisationCount) {
        this.expiredAuthorisationCount = expiredAuthorisationCount;
    }

    public List<Integer> getAbandonedConsentFlowCount() {
        return abandonedConsentFlowCount;
    }

    public void setAbandonedConsentFlowCount(List<Integer> abandonedConsentFlowCount) {
        this.abandonedConsentFlowCount = abandonedConsentFlowCount;
    }

    public List<Integer> getPreIdentificationAbandonedConsentFlowCount() {
        return preIdentificationAbandonedConsentFlowCount;
    }

    public void setPreIdentificationAbandonedConsentFlowCount(
            List<Integer> preIdentificationAbandonedConsentFlowCount) {
        this.preIdentificationAbandonedConsentFlowCount = preIdentificationAbandonedConsentFlowCount;
    }

    public List<Integer> getPreAuthenticationAbandonedConsentFlowCount() {
        return preAuthenticationAbandonedConsentFlowCount;
    }

    public void setPreAuthenticationAbandonedConsentFlowCount(
            List<Integer> preAuthenticationAbandonedConsentFlowCount) {
        this.preAuthenticationAbandonedConsentFlowCount = preAuthenticationAbandonedConsentFlowCount;
    }

    public List<Integer> getPreAccountSelectionAbandonedConsentFlowCount() {
        return preAccountSelectionAbandonedConsentFlowCount;
    }

    public void setPreAccountSelectionAbandonedConsentFlowCount(
            List<Integer> preAccountSelectionAbandonedConsentFlowCount) {
        this.preAccountSelectionAbandonedConsentFlowCount = preAccountSelectionAbandonedConsentFlowCount;
    }

    public List<Integer> getPreAuthorisationAbandonedConsentFlowCount() {
        return preAuthorisationAbandonedConsentFlowCount;
    }

    public void setPreAuthorisationAbandonedConsentFlowCount(List<Integer> preAuthorisationAbandonedConsentFlowCount) {
        this.preAuthorisationAbandonedConsentFlowCount = preAuthorisationAbandonedConsentFlowCount;
    }

    public List<Integer> getRejectedAbandonedConsentFlowCount() {
        return rejectedAbandonedConsentFlowCount;
    }

    public void setRejectedAbandonedConsentFlowCount(List<Integer> rejectedAbandonedConsentFlowCount) {
        this.rejectedAbandonedConsentFlowCount = rejectedAbandonedConsentFlowCount;
    }

    public List<Integer> getFailedTokenExchangeAbandonedConsentFlowCount() {
        return failedTokenExchangeAbandonedConsentFlowCount;
    }

    public void setFailedTokenExchangeAbandonedConsentFlowCount(
            List<Integer> failedTokenExchangeAbandonedConsentFlowCount) {
        this.failedTokenExchangeAbandonedConsentFlowCount = failedTokenExchangeAbandonedConsentFlowCount;
    }
}
