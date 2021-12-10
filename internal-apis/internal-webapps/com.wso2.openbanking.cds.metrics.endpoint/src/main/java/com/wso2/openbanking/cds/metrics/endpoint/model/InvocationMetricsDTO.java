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

import javax.validation.Valid;

/**
 * Number of API calls in each performance tier over time.
 **/
@ApiModel(description = "Number of API calls in each performance tier over time")
public class InvocationMetricsDTO {

    @ApiModelProperty(value = "")
    @Valid
    private InvocationMetricsUnauthenticatedDTO unauthenticated = null;

    @ApiModelProperty(value = "")
    @Valid
    private InvocationMetricsHighPriorityDTO highPriority = null;

    @ApiModelProperty(value = "")
    @Valid
    private InvocationMetricsLowPriorityDTO lowPriority = null;

    @ApiModelProperty(value = "")
    @Valid
    private InvocationMetricsUnattendedDTO unattended = null;

    @ApiModelProperty(value = "")
    @Valid
    private InvocationMetricsLargePayloadDTO largePayload = null;

    /**
     * Get unauthenticated.
     *
     * @return unauthenticated
     **/
    @JsonProperty("unauthenticated")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public InvocationMetricsUnauthenticatedDTO getUnauthenticated() {
        return unauthenticated;
    }

    public void setUnauthenticated(InvocationMetricsUnauthenticatedDTO unauthenticated) {
        this.unauthenticated = unauthenticated;
    }

    public InvocationMetricsDTO unauthenticated(InvocationMetricsUnauthenticatedDTO unauthenticated) {
        this.unauthenticated = unauthenticated;
        return this;
    }

    /**
     * Get highPriority.
     *
     * @return highPriority
     **/
    @JsonProperty("highPriority")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public InvocationMetricsHighPriorityDTO getHighPriority() {
        return highPriority;
    }

    public void setHighPriority(InvocationMetricsHighPriorityDTO highPriority) {
        this.highPriority = highPriority;
    }

    public InvocationMetricsDTO highPriority(InvocationMetricsHighPriorityDTO highPriority) {
        this.highPriority = highPriority;
        return this;
    }

    /**
     * Get lowPriority.
     *
     * @return lowPriority
     **/
    @JsonProperty("lowPriority")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public InvocationMetricsLowPriorityDTO getLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(InvocationMetricsLowPriorityDTO lowPriority) {
        this.lowPriority = lowPriority;
    }

    public InvocationMetricsDTO lowPriority(InvocationMetricsLowPriorityDTO lowPriority) {
        this.lowPriority = lowPriority;
        return this;
    }

    /**
     * Get unattended.
     *
     * @return unattended
     **/
    @JsonProperty("unattended")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public InvocationMetricsUnattendedDTO getUnattended() {
        return unattended;
    }

    public void setUnattended(InvocationMetricsUnattendedDTO unattended) {
        this.unattended = unattended;
    }

    public InvocationMetricsDTO unattended(InvocationMetricsUnattendedDTO unattended) {
        this.unattended = unattended;
        return this;
    }

    /**
     * Get largePayload.
     *
     * @return largePayload
     **/
    @JsonProperty("largePayload")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public InvocationMetricsLargePayloadDTO getLargePayload() {
        return largePayload;
    }

    public void setLargePayload(InvocationMetricsLargePayloadDTO largePayload) {
        this.largePayload = largePayload;
    }

    public InvocationMetricsDTO largePayload(InvocationMetricsLargePayloadDTO largePayload) {
        this.largePayload = largePayload;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class InvocationMetricsDTO {\n");

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

