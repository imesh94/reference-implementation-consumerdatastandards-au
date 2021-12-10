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

package com.wso2.openbanking.cds.metrics.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Model class for CDS metrics data.
 */
public class ResponseMetricsListModel {

    private String requestTime;

    // recipient count
    private int recipientCount;

    // customer count
    private int customerCount;

    // invocations
    private List<BigDecimal> invocationUnauthenticated;
    private List<BigDecimal> invocationHighPriority;
    private List<BigDecimal> invocationLowPriority;
    private List<BigDecimal> invocationUnattended;
    private List<BigDecimal> invocationLargePayload;

    // average response
    private List<BigDecimal> averageResponseUnauthenticated;
    private List<BigDecimal> averageResponseHighPriority;
    private List<BigDecimal> averageResponseLowPriority;
    private List<BigDecimal> averageResponseUnattended;
    private List<BigDecimal> averageResponseLargePayload;

    // average TPS
    private List<BigDecimal> averageTPS;

    // max TPS
    private List<BigDecimal> peakTPS;

    // errors
    private List<BigDecimal> errors;

    // rejections
    private List<BigDecimal> authenticatedEndpointRejections;
    private List<BigDecimal> unauthenticatedEndpointRejectons;

    // performance
    private List<BigDecimal> performance;

    // sessionCount
    private List<BigDecimal> sessionCount;

    // availabilityMetrics
    private List<BigDecimal> availability;

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

    public List<BigDecimal> getUnauthenticatedEndpointRejectons() {
        return unauthenticatedEndpointRejectons;
    }

    public void setUnauthenticatedEndpointRejectons(List<BigDecimal> unauthenticatedEndpointRejectons) {
        this.unauthenticatedEndpointRejectons = unauthenticatedEndpointRejectons;
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
