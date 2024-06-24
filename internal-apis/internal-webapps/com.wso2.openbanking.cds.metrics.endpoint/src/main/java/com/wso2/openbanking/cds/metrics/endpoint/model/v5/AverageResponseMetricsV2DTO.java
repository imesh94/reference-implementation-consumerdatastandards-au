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
 * Average response time in seconds, at millisecond resolution, within each performance tier
 **/
@ApiModel(description = "Average response time in seconds, at millisecond resolution, within each performance tier")
public class AverageResponseMetricsV2DTO {

    @ApiModelProperty(required = true, value = "")
    @Valid
    private AverageResponseMetricsV2UnauthenticatedDTO unauthenticated = null;

    @ApiModelProperty(required = true, value = "")
    @Valid
    private AverageResponseMetricsV2HighPriorityDTO highPriority = null;

    @ApiModelProperty(required = true, value = "")
    @Valid
    private AverageResponseMetricsV2LowPriorityDTO lowPriority = null;

    @ApiModelProperty(required = true, value = "")
    @Valid
    private AverageResponseMetricsV2UnattendedDTO unattended = null;

    @ApiModelProperty(required = true, value = "")
    @Valid
    private AverageResponseMetricsV2LargePayloadDTO largePayload = null;

    /**
     * Get unauthenticated
     *
     * @return unauthenticated
     **/
    @JsonProperty("unauthenticated")
    @NotNull
    public AverageResponseMetricsV2UnauthenticatedDTO getUnauthenticated() {
        return unauthenticated;
    }

    public void setUnauthenticated(AverageResponseMetricsV2UnauthenticatedDTO unauthenticated) {
        this.unauthenticated = unauthenticated;
    }

    public AverageResponseMetricsV2DTO unauthenticated(AverageResponseMetricsV2UnauthenticatedDTO unauthenticated) {
        this.unauthenticated = unauthenticated;
        return this;
    }

    /**
     * Get highPriority
     *
     * @return highPriority
     **/
    @JsonProperty("highPriority")
    @NotNull
    public AverageResponseMetricsV2HighPriorityDTO getHighPriority() {
        return highPriority;
    }

    public void setHighPriority(AverageResponseMetricsV2HighPriorityDTO highPriority) {
        this.highPriority = highPriority;
    }

    public AverageResponseMetricsV2DTO highPriority(AverageResponseMetricsV2HighPriorityDTO highPriority) {
        this.highPriority = highPriority;
        return this;
    }

    /**
     * Get lowPriority
     *
     * @return lowPriority
     **/
    @JsonProperty("lowPriority")
    @NotNull
    public AverageResponseMetricsV2LowPriorityDTO getLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(AverageResponseMetricsV2LowPriorityDTO lowPriority) {
        this.lowPriority = lowPriority;
    }

    public AverageResponseMetricsV2DTO lowPriority(AverageResponseMetricsV2LowPriorityDTO lowPriority) {
        this.lowPriority = lowPriority;
        return this;
    }

    /**
     * Get unattended
     *
     * @return unattended
     **/
    @JsonProperty("unattended")
    @NotNull
    public AverageResponseMetricsV2UnattendedDTO getUnattended() {
        return unattended;
    }

    public void setUnattended(AverageResponseMetricsV2UnattendedDTO unattended) {
        this.unattended = unattended;
    }

    public AverageResponseMetricsV2DTO unattended(AverageResponseMetricsV2UnattendedDTO unattended) {
        this.unattended = unattended;
        return this;
    }

    /**
     * Get largePayload
     *
     * @return largePayload
     **/
    @JsonProperty("largePayload")
    @NotNull
    public AverageResponseMetricsV2LargePayloadDTO getLargePayload() {
        return largePayload;
    }

    public void setLargePayload(AverageResponseMetricsV2LargePayloadDTO largePayload) {
        this.largePayload = largePayload;
    }

    public AverageResponseMetricsV2DTO largePayload(AverageResponseMetricsV2LargePayloadDTO largePayload) {
        this.largePayload = largePayload;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AverageResponseMetricsV2DTO {\n");

        sb.append("    unauthenticated: ").append(toIndentedString(unauthenticated)).append("\n");
        sb.append("    highPriority: ").append(toIndentedString(highPriority)).append("\n");
        sb.append("    lowPriority: ").append(toIndentedString(lowPriority)).append("\n");
        sb.append("    unattended: ").append(toIndentedString(unattended)).append("\n");
        sb.append("    largePayload: ").append(toIndentedString(largePayload)).append("\n");
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
