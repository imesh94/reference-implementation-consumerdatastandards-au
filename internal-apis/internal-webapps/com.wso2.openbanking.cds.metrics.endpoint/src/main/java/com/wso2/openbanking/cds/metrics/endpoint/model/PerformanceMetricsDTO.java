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

package com.wso2.openbanking.cds.metrics.endpoint.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;

/**
 * Percentage of calls within the performance thresholds.
 **/
@ApiModel(description = "Percentage of calls within the performance thresholds")
public class PerformanceMetricsDTO {

    @ApiModelProperty(value = "Percentage of calls within the performance threshold for the current day. 0.0 means 0%" +
            ". 1.0 means 100%")
    @Valid
    /**
     * Percentage of calls within the performance threshold for the current day. 0.0 means 0%. 1.0 means 100%
     **/
    private BigDecimal currentDay;

    @ApiModelProperty(value = "Percentage of calls within the performance threshold for previous days. The first " +
            "element indicates yesterday and so on. A maximum of seven entries is required if available. 0.0 means 0%" +
            ". 1.0 means 100%")
    @Valid
    /**
     * Percentage of calls within the performance threshold for previous days. The first element indicates yesterday
     * and so on. A maximum of seven entries is required if available. 0.0 means 0%. 1.0 means 100%
     **/
    private List<BigDecimal> previousDays = null;

    /**
     * Percentage of calls within the performance threshold for the current day. 0.0 means 0%. 1.0 means 100%
     *
     * @return currentDay
     **/
    @JsonProperty("currentDay")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public BigDecimal getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(BigDecimal currentDay) {
        this.currentDay = currentDay;
    }

    public PerformanceMetricsDTO currentDay(BigDecimal currentDay) {
        this.currentDay = currentDay;
        return this;
    }

    /**
     * Percentage of calls within the performance threshold for previous days. The first element indicates yesterday and
     * so on. A maximum of seven entries is required if available. 0.0 means 0%. 1.0 means 100%
     *
     * @return previousDays
     **/
    @JsonProperty("previousDays")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<BigDecimal> getPreviousDays() {
        return previousDays;
    }

    public void setPreviousDays(List<BigDecimal> previousDays) {
        this.previousDays = previousDays;
    }

    public PerformanceMetricsDTO previousDays(List<BigDecimal> previousDays) {
        this.previousDays = previousDays;
        return this;
    }

    public PerformanceMetricsDTO addPreviousDaysItem(BigDecimal previousDaysItem) {
        this.previousDays.add(previousDaysItem);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PerformanceMetricsDTO {\n");

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

