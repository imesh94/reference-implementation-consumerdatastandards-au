/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Model class for ErrorMetricDay.
 */
public class ErrorMetricDay {

    private LocalDate date;
    private Map<String, Integer> authenticatedErrorMap;
    private Map<String, Integer> unauthenticatedErrorMap;

    public ErrorMetricDay() {
        this.authenticatedErrorMap = new HashMap<>();
        this.unauthenticatedErrorMap = new HashMap<>();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<String, Integer> getAuthenticatedErrorMap() {
        return authenticatedErrorMap;
    }

    public void setAuthenticatedErrorMap(Map<String, Integer> authenticatedErrorMap) {
        this.authenticatedErrorMap = authenticatedErrorMap;
    }

    public Map<String, Integer> getUnauthenticatedErrorMap() {
        return unauthenticatedErrorMap;
    }

    public void setUnauthenticatedErrorMap(Map<String, Integer> unauthenticatedErrorMap) {
        this.unauthenticatedErrorMap = unauthenticatedErrorMap;
    }
}
