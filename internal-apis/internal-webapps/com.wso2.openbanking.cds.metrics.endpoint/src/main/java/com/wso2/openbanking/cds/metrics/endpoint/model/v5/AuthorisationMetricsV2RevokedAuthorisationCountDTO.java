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

import javax.validation.Valid;

/**
 * The number of revoked authorisations
 **/
@ApiModel(description = "The number of revoked authorisations")
public class AuthorisationMetricsV2RevokedAuthorisationCountDTO {

    @ApiModelProperty(value = "")
    @Valid
    private AuthorisationMetricsV2AuthorisationCountDayDTO currentDay = null;

    @ApiModelProperty(value = "Number of revoked authorisations for previous days. The first element indicates " +
            "yesterday and so on. A maximum of seven entries is required if available")
    @Valid
    /**
     * Number of revoked authorisations for previous days. The first element indicates yesterday and so on. A maximum
     * of seven entries is required if available
     **/
    private List<AuthorisationMetricsV2AuthorisationCountDayDTO> previousDays = null;

    /**
     * Get currentDay
     *
     * @return currentDay
     **/
    @JsonProperty("currentDay")
    public AuthorisationMetricsV2AuthorisationCountDayDTO getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(AuthorisationMetricsV2AuthorisationCountDayDTO currentDay) {
        this.currentDay = currentDay;
    }

    public AuthorisationMetricsV2RevokedAuthorisationCountDTO currentDay(
            AuthorisationMetricsV2AuthorisationCountDayDTO currentDay) {
        this.currentDay = currentDay;
        return this;
    }

    /**
     * Number of revoked authorisations for previous days. The first element indicates yesterday and so on. A maximum
     * of seven entries is required if available
     *
     * @return previousDays
     **/
    @JsonProperty("previousDays")
    public List<AuthorisationMetricsV2AuthorisationCountDayDTO> getPreviousDays() {
        return previousDays;
    }

    public void setPreviousDays(List<AuthorisationMetricsV2AuthorisationCountDayDTO> previousDays) {
        this.previousDays = previousDays;
    }

    public AuthorisationMetricsV2RevokedAuthorisationCountDTO previousDays(
            List<AuthorisationMetricsV2AuthorisationCountDayDTO> previousDays) {
        this.previousDays = previousDays;
        return this;
    }

    public AuthorisationMetricsV2RevokedAuthorisationCountDTO addPreviousDaysItem(
            AuthorisationMetricsV2AuthorisationCountDayDTO previousDaysItem) {
        this.previousDays.add(previousDaysItem);
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AuthorisationMetricsV2RevokedAuthorisationCountDTO {\n");

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

