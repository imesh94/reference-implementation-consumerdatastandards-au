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
 * Availability metrics
 **/
@ApiModel(description = "Availability metrics")
public class AvailabilityMetricsV2DTO {

    @ApiModelProperty(required = true, value = "")
    @Valid
    private AvailabilityMetricsV2AggregateDTO aggregate = null;

    @ApiModelProperty(required = true, value = "")
    @Valid
    private AvailabilityMetricsV2UnauthenticatedDTO unauthenticated = null;

    @ApiModelProperty(required = true, value = "")
    @Valid
    private AvailabilityMetricsV2AuthenticatedDTO authenticated = null;

    /**
     * Get aggregate
     *
     * @return aggregate
     **/
    @JsonProperty("aggregate")
    @NotNull
    public AvailabilityMetricsV2AggregateDTO getAggregate() {
        return aggregate;
    }

    public void setAggregate(AvailabilityMetricsV2AggregateDTO aggregate) {
        this.aggregate = aggregate;
    }

    public AvailabilityMetricsV2DTO aggregate(AvailabilityMetricsV2AggregateDTO aggregate) {
        this.aggregate = aggregate;
        return this;
    }

    /**
     * Get unauthenticated
     *
     * @return unauthenticated
     **/
    @JsonProperty("unauthenticated")
    @NotNull
    public AvailabilityMetricsV2UnauthenticatedDTO getUnauthenticated() {
        return unauthenticated;
    }

    public void setUnauthenticated(AvailabilityMetricsV2UnauthenticatedDTO unauthenticated) {
        this.unauthenticated = unauthenticated;
    }

    public AvailabilityMetricsV2DTO unauthenticated(AvailabilityMetricsV2UnauthenticatedDTO unauthenticated) {
        this.unauthenticated = unauthenticated;
        return this;
    }

    /**
     * Get authenticated
     *
     * @return authenticated
     **/
    @JsonProperty("authenticated")
    @NotNull
    public AvailabilityMetricsV2AuthenticatedDTO getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(AvailabilityMetricsV2AuthenticatedDTO authenticated) {
        this.authenticated = authenticated;
    }

    public AvailabilityMetricsV2DTO authenticated(AvailabilityMetricsV2AuthenticatedDTO authenticated) {
        this.authenticated = authenticated;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AvailabilityMetricsV2DTO {\n");

        sb.append("    aggregate: ").append(toIndentedString(aggregate)).append("\n");
        sb.append("    unauthenticated: ").append(toIndentedString(unauthenticated)).append("\n");
        sb.append("    authenticated: ").append(toIndentedString(authenticated)).append("\n");
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

