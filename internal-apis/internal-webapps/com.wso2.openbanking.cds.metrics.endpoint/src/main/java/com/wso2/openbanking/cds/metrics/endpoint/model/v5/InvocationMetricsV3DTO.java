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
 * Number of API calls in each performance tier over time
 **/
@ApiModel(description = "Number of API calls in each performance tier over time")
public class InvocationMetricsV3DTO {

    @ApiModelProperty(required = true, value = "")
    @Valid
    private InvocationMetricsV3UnauthenticatedDTO unauthenticated = null;

    @ApiModelProperty(required = true, value = "")
    @Valid
    private InvocationMetricsV3HighPriorityDTO highPriority = null;

    @ApiModelProperty(required = true, value = "")
    @Valid
    private InvocationMetricsV3LowPriorityDTO lowPriority = null;

    @ApiModelProperty(required = true, value = "")
    @Valid
    private InvocationMetricsV3UnattendedDTO unattended = null;

    @ApiModelProperty(required = true, value = "")
    @Valid
    private InvocationMetricsV3LargePayloadDTO largePayload = null;

    /**
     * Get unauthenticated
     *
     * @return unauthenticated
     **/
    @JsonProperty("unauthenticated")
    @NotNull
    public InvocationMetricsV3UnauthenticatedDTO getUnauthenticated() {
        return unauthenticated;
    }

    public void setUnauthenticated(InvocationMetricsV3UnauthenticatedDTO unauthenticated) {
        this.unauthenticated = unauthenticated;
    }

    public InvocationMetricsV3DTO unauthenticated(InvocationMetricsV3UnauthenticatedDTO unauthenticated) {
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
    public InvocationMetricsV3HighPriorityDTO getHighPriority() {
        return highPriority;
    }

    public void setHighPriority(InvocationMetricsV3HighPriorityDTO highPriority) {
        this.highPriority = highPriority;
    }

    public InvocationMetricsV3DTO highPriority(InvocationMetricsV3HighPriorityDTO highPriority) {
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
    public InvocationMetricsV3LowPriorityDTO getLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(InvocationMetricsV3LowPriorityDTO lowPriority) {
        this.lowPriority = lowPriority;
    }

    public InvocationMetricsV3DTO lowPriority(InvocationMetricsV3LowPriorityDTO lowPriority) {
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
    public InvocationMetricsV3UnattendedDTO getUnattended() {
        return unattended;
    }

    public void setUnattended(InvocationMetricsV3UnattendedDTO unattended) {
        this.unattended = unattended;
    }

    public InvocationMetricsV3DTO unattended(InvocationMetricsV3UnattendedDTO unattended) {
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
    public InvocationMetricsV3LargePayloadDTO getLargePayload() {
        return largePayload;
    }

    public void setLargePayload(InvocationMetricsV3LargePayloadDTO largePayload) {
        this.largePayload = largePayload;
    }

    public InvocationMetricsV3DTO largePayload(InvocationMetricsV3LargePayloadDTO largePayload) {
        this.largePayload = largePayload;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class InvocationMetricsV3DTO {\n");

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

