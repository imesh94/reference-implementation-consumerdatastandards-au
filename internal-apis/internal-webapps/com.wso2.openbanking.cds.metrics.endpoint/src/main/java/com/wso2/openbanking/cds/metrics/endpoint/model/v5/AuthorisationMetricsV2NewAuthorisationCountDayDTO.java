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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Number of new authorisations for a day
 **/
@ApiModel(description = "Number of new authorisations for a day")
public class AuthorisationMetricsV2NewAuthorisationCountDayDTO {

    @ApiModelProperty(required = true, value = "")
    @Valid
    private AuthorisationMetricsV2AuthorisationCountDayDTO onceOff = null;

    @ApiModelProperty(required = true, value = "")
    @Valid
    private AuthorisationMetricsV2AuthorisationCountDayDTO ongoing = null;

    /**
     * Get onceOff
     *
     * @return onceOff
     **/
    @JsonProperty("onceOff")
    @NotNull
    public AuthorisationMetricsV2AuthorisationCountDayDTO getOnceOff() {
        return onceOff;
    }

    public void setOnceOff(AuthorisationMetricsV2AuthorisationCountDayDTO onceOff) {
        this.onceOff = onceOff;
    }

    public AuthorisationMetricsV2NewAuthorisationCountDayDTO onceOff(
            AuthorisationMetricsV2AuthorisationCountDayDTO onceOff) {
        this.onceOff = onceOff;
        return this;
    }

    /**
     * Get ongoing
     *
     * @return ongoing
     **/
    @JsonProperty("ongoing")
    @NotNull
    public AuthorisationMetricsV2AuthorisationCountDayDTO getOngoing() {
        return ongoing;
    }

    public void setOngoing(AuthorisationMetricsV2AuthorisationCountDayDTO ongoing) {
        this.ongoing = ongoing;
    }

    public AuthorisationMetricsV2NewAuthorisationCountDayDTO ongoing(
            AuthorisationMetricsV2AuthorisationCountDayDTO ongoing) {
        this.ongoing = ongoing;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AuthorisationMetricsV2NewAuthorisationCountDayDTO {\n");

        sb.append("    onceOff: ").append(toIndentedString(onceOff)).append("\n");
        sb.append("    ongoing: ").append(toIndentedString(ongoing)).append("\n");
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

