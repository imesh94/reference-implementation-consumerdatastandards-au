/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
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
 * Rejection counts for all authenticated end points.
 **/
@ApiModel(description = "Rejection counts for all authenticated end points")
public class RejectionMetricsAuthenticatedDTO {

    @ApiModelProperty(value = "Number of calls rejected for current day")
    @Valid
    @JsonInclude(JsonInclude.Include.NON_NULL)
    /**
     * Number of calls rejected for current day.
     **/
    private BigDecimal currentDay;

    @ApiModelProperty(value = "Number of calls rejected for previous days. The first element indicates yesterday and " +
            "so on. A maximum of seven entries is required if available.")
    @Valid
    @JsonInclude(JsonInclude.Include.NON_NULL)
    /**
     * Number of calls rejected for previous days. The first element indicates yesterday and so on. A maximum of seven
     * entries is required if available.
     **/
    private List<BigDecimal> previousDays = null;

    /**
     * Number of calls rejected for current day.
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

    public RejectionMetricsAuthenticatedDTO currentDay(BigDecimal currentDay) {

        this.currentDay = currentDay;
        return this;
    }

    /**
     * Number of calls rejected for previous days. The first element indicates yesterday and so on. A maximum of seven
     * entries is required if available.
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

    public RejectionMetricsAuthenticatedDTO previousDays(List<BigDecimal> previousDays) {

        this.previousDays = previousDays;
        return this;
    }

    public RejectionMetricsAuthenticatedDTO addPreviousDaysItem(BigDecimal previousDaysItem) {

        this.previousDays.add(previousDaysItem);
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class RejectionMetricsAuthenticatedDTO {\n");

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
