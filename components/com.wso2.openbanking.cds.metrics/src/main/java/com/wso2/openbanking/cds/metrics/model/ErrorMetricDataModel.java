/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.model;

import net.minidev.json.JSONArray;

/**
 * Model class for ErrorMetricDataModel.
 */
public class ErrorMetricDataModel {

    private long timestamp;
    private String statusCode;
    private String aspect;
    private int count;

    public ErrorMetricDataModel() {
    }

    public ErrorMetricDataModel(JSONArray jsonArray) {
        this.timestamp = (long) jsonArray.get(0);
        this.statusCode = jsonArray.get(1).toString();
        this.aspect = (String) jsonArray.get(2);
        this.count = (int) jsonArray.get(3);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getAspect() {
        return aspect;
    }

    public void setAspect(String aspect) {
        this.aspect = aspect;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
