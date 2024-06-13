/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.endpoint.model.v5;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Availability metrics for the authenticated aspects of the CDR regime
 **/
@ApiModel(description = "Availability metrics for the authenticated aspects of the CDR regime")
public class AvailabilityMetricsV2AuthenticatedDTO {

    @ApiModelProperty(value = "Percentage availability of the CDR platform so far for the current calendar month. 0.0" +
            " means 0%. 1.0 means 100%. Must be a positive value or zero")
    /**
     * Percentage availability of the CDR platform so far for the current calendar month. 0.0 means 0%. 1.0 means
     * 100%. Must be a positive value or zero
     **/
    private String currentMonth;

    @ApiModelProperty(value = "Percentage availability of the CDR platform for previous calendar months. The first " +
            "element indicates the last month and so on. A maximum of twelve entries is required if available. 0.0 " +
            "means 0%. 1.0 means 100%. Values must be a positive or zero")
    /**
     * Percentage availability of the CDR platform for previous calendar months. The first element indicates the last
     * month and so on. A maximum of twelve entries is required if available. 0.0 means 0%. 1.0 means 100%. Values
     * must be a positive or zero
     **/
    private List<String> previousMonths = null;

    /**
     * Percentage availability of the CDR platform so far for the current calendar month. 0.0 means 0%. 1.0 means
     * 100%. Must be a positive value or zero
     *
     * @return currentMonth
     **/
    @JsonProperty("currentMonth")
    public String getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(String currentMonth) {
        this.currentMonth = currentMonth;
    }

    public AvailabilityMetricsV2AuthenticatedDTO currentMonth(String currentMonth) {
        this.currentMonth = currentMonth;
        return this;
    }

    /**
     * Percentage availability of the CDR platform for previous calendar months. The first element indicates the last
     * month and so on. A maximum of twelve entries is required if available. 0.0 means 0%. 1.0 means 100%. Values
     * must be a positive or zero
     *
     * @return previousMonths
     **/
    @JsonProperty("previousMonths")
    public List<String> getPreviousMonths() {
        return previousMonths;
    }

    public void setPreviousMonths(List<String> previousMonths) {
        this.previousMonths = previousMonths;
    }

    public AvailabilityMetricsV2AuthenticatedDTO previousMonths(List<String> previousMonths) {
        this.previousMonths = previousMonths;
        return this;
    }

    public AvailabilityMetricsV2AuthenticatedDTO addPreviousMonthsItem(String previousMonthsItem) {
        this.previousMonths.add(previousMonthsItem);
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AvailabilityMetricsV2AuthenticatedDTO {\n");

        sb.append("    currentMonth: ").append(toIndentedString(currentMonth)).append("\n");
        sb.append("    previousMonths: ").append(toIndentedString(previousMonths)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private static String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

