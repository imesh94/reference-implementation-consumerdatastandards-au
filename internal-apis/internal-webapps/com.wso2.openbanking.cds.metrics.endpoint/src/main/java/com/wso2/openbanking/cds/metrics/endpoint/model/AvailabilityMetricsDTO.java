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
 * Percentage availability of the CDR platform over time.
 **/
@ApiModel(description = "Percentage availability of the CDR platform over time")
public class AvailabilityMetricsDTO {

    @ApiModelProperty(value = "Percentage availability of the CDR platform so far for the current calendar month. " +
            "0.0 means 0%. 1.0 means 100%.")
    @Valid
    /**
     * Percentage availability of the CDR platform so far for the current calendar month. 0.0 means 0%. 1.0 means 100%.
     **/
    private BigDecimal currentMonth;

    @ApiModelProperty(value = "Percentage availability of the CDR platform for previous calendar months. The first " +
            "element indicates the last month and so on. A maximum of twelve entries is required if available. " +
            "0.0 means 0%. 1.0 means 100%.")
    @Valid
    /**
     * Percentage availability of the CDR platform for previous calendar months. The first element indicates the last
     * month and so on. A maximum of twelve entries is required if available. 0.0 means 0%. 1.0 means 100%.
     **/
    private List<BigDecimal> previousMonths = null;

    /**
     * Percentage availability of the CDR platform so far for the current calendar month. 0.0 means 0%. 1.0 means 100%.
     *
     * @return currentMonth
     **/
    @JsonProperty("currentMonth")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public BigDecimal getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(BigDecimal currentMonth) {
        this.currentMonth = currentMonth;
    }

    public AvailabilityMetricsDTO currentMonth(BigDecimal currentMonth) {
        this.currentMonth = currentMonth;
        return this;
    }

    /**
     * Percentage availability of the CDR platform for previous calendar months. The first element indicates the last
     * month and so on. A maximum of twelve entries is required if available. 0.0 means 0%. 1.0 means 100%.
     *
     * @return previousMonths
     **/
    @JsonProperty("previousMonths")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<BigDecimal> getPreviousMonths() {
        return previousMonths;
    }

    public void setPreviousMonths(List<BigDecimal> previousMonths) {
        this.previousMonths = previousMonths;
    }

    public AvailabilityMetricsDTO previousMonths(List<BigDecimal> previousMonths) {
        this.previousMonths = previousMonths;
        return this;
    }

    public AvailabilityMetricsDTO addPreviousMonthsItem(BigDecimal previousMonthsItem) {
        this.previousMonths.add(previousMonthsItem);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AvailabilityMetricsDTO {\n");

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

