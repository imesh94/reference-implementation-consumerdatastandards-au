/**
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.openbanking.cds.metrics.endpoint.model.v5;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

/**
 * Peak transactions per second over time for authenticated endpoints
 **/
@ApiModel(description = "Peak transactions per second over time for authenticated endpoints")
public class PeakTPSMetricsV2AuthenticatedDTO {

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

    public PeakTPSMetricsV2AuthenticatedDTO currentDay(BigDecimal currentDay) {
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

    public PeakTPSMetricsV2AuthenticatedDTO previousDays(List<BigDecimal> previousDays) {
        this.previousDays = previousDays;
        return this;
    }

    public PeakTPSMetricsV2AuthenticatedDTO addPreviousDaysItem(BigDecimal previousDaysItem) {
        this.previousDays.add(previousDaysItem);
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PeakTPSMetricsV2AuthenticatedDTO {\n");

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

