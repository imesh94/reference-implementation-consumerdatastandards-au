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
 * Number of calls rejected due to traffic thresholds over time
 **/
@ApiModel(description = "Number of calls rejected due to traffic thresholds over time")
public class RejectionMetricsV3DTO {

    @ApiModelProperty(required = true, value = "")
    @Valid
    private RejectionMetricsV3AuthenticatedDTO authenticated = null;

    @ApiModelProperty(required = true, value = "")
    @Valid
    private RejectionMetricsV3UnauthenticatedDTO unauthenticated = null;

    /**
     * Get authenticated
     *
     * @return authenticated
     **/
    @JsonProperty("authenticated")
    @NotNull
    public RejectionMetricsV3AuthenticatedDTO getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(RejectionMetricsV3AuthenticatedDTO authenticated) {
        this.authenticated = authenticated;
    }

    public RejectionMetricsV3DTO authenticated(RejectionMetricsV3AuthenticatedDTO authenticated) {
        this.authenticated = authenticated;
        return this;
    }

    /**
     * Get unauthenticated
     *
     * @return unauthenticated
     **/
    @JsonProperty("unauthenticated")
    @NotNull
    public RejectionMetricsV3UnauthenticatedDTO getUnauthenticated() {
        return unauthenticated;
    }

    public void setUnauthenticated(RejectionMetricsV3UnauthenticatedDTO unauthenticated) {
        this.unauthenticated = unauthenticated;
    }

    public RejectionMetricsV3DTO unauthenticated(RejectionMetricsV3UnauthenticatedDTO unauthenticated) {
        this.unauthenticated = unauthenticated;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RejectionMetricsV3DTO {\n");

        sb.append("    authenticated: ").append(toIndentedString(authenticated)).append("\n");
        sb.append("    unauthenticated: ").append(toIndentedString(unauthenticated)).append("\n");
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

