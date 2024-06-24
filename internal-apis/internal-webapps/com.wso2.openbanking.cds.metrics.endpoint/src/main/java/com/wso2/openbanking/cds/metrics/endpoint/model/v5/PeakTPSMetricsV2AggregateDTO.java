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

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

/**
 * Aggregate peak transactions per second over time for all endpoints
 **/
@ApiModel(description = "Aggregate peak transactions per second over time for all endpoints")
public class PeakTPSMetricsV2AggregateDTO {

    @ApiModelProperty(value = "Peak TPS for current day. Must be a positive value or zero")
    @Valid
    /**
     * Peak TPS for current day. Must be a positive value or zero
     **/
    private BigDecimal currentDay;

    @ApiModelProperty(value = "Peak TPS for previous days. The first element indicates yesterday and so on. A maximum" +
            " of seven entries is required if available. Values must be a positive or zero")
    @Valid
    /**
     * Peak TPS for previous days. The first element indicates yesterday and so on. A maximum of seven entries is
     * required if available. Values must be a positive or zero
     **/
    private List<BigDecimal> previousDays = null;

    /**
     * Peak TPS for current day. Must be a positive value or zero
     *
     * @return currentDay
     **/
    @JsonProperty("currentDay")
    public BigDecimal getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(BigDecimal currentDay) {
        this.currentDay = currentDay;
    }

    public PeakTPSMetricsV2AggregateDTO currentDay(BigDecimal currentDay) {
        this.currentDay = currentDay;
        return this;
    }

    /**
     * Peak TPS for previous days. The first element indicates yesterday and so on. A maximum of seven entries is
     * required if available. Values must be a positive or zero
     *
     * @return previousDays
     **/
    @JsonProperty("previousDays")
    public List<BigDecimal> getPreviousDays() {
        return previousDays;
    }

    public void setPreviousDays(List<BigDecimal> previousDays) {
        this.previousDays = previousDays;
    }

    public PeakTPSMetricsV2AggregateDTO previousDays(List<BigDecimal> previousDays) {
        this.previousDays = previousDays;
        return this;
    }

    public PeakTPSMetricsV2AggregateDTO addPreviousDaysItem(BigDecimal previousDaysItem) {
        this.previousDays.add(previousDaysItem);
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PeakTPSMetricsV2AggregateDTO {\n");

        sb.append("    currentDay: ").append(toIndentedString(currentDay)).append("\n");
        sb.append("    previousDays: ").append(toIndentedString(previousDays)).append("\n");
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

