/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.model;

/**
 * Model class for PerformanceMetric.
 */
public class PerformanceMetric {

    private String priorityTier;
    private long timestamp;
    private double performanceValue;

    public String getPriorityTier() {
        return priorityTier;
    }

    public void setPriorityTier(String priorityTier) {
        this.priorityTier = priorityTier;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getPerformanceValue() {
        return performanceValue;
    }

    public void setPerformanceValue(double performanceValue) {
        this.performanceValue = performanceValue;
    }
}
