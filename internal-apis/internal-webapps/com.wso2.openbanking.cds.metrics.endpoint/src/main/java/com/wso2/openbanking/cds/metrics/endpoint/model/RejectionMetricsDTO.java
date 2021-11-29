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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;


/**
 * Number of calls rejected due to traffic thresholds over time.
 **/
@ApiModel(description = "Number of calls rejected due to traffic thresholds over time")
public class RejectionMetricsDTO {

    @ApiModelProperty(value = "")
    @Valid
    private RejectionMetricsAuthenticatedDTO authenticated = null;

    @ApiModelProperty(value = "")
    @Valid
    private RejectionMetricsUnauthenticatedDTO unauthenticated = null;
    /**
     * Get authenticated.
     * @return authenticated
     **/
    @JsonProperty("authenticated")
    public RejectionMetricsAuthenticatedDTO getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(RejectionMetricsAuthenticatedDTO authenticated) {
        this.authenticated = authenticated;
    }

    public RejectionMetricsDTO authenticated(RejectionMetricsAuthenticatedDTO authenticated) {
        this.authenticated = authenticated;
        return this;
    }

    /**
     * Get unauthenticated.
     * @return unauthenticated
     **/
    @JsonProperty("unauthenticated")
    public RejectionMetricsUnauthenticatedDTO getUnauthenticated() {
        return unauthenticated;
    }

    public void setUnauthenticated(RejectionMetricsUnauthenticatedDTO unauthenticated) {
        this.unauthenticated = unauthenticated;
    }

    public RejectionMetricsDTO unauthenticated(RejectionMetricsUnauthenticatedDTO unauthenticated) {
        this.unauthenticated = unauthenticated;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RejectionMetricsDTO {\n");

        sb.append("    authenticated: ").append(toIndentedString(authenticated)).append("\n");
        sb.append("    unauthenticated: ").append(toIndentedString(unauthenticated)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces.
     * (except the first line).
     */
    private static String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
