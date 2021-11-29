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
 *
 */

package com.wso2.openbanking.cds.metrics.endpoint.api;

import com.wso2.openbanking.cds.metrics.endpoint.model.ResponseMetricsListDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 *  Metrics API
 */
@Path("/admin")
public interface MetricsApi {

    /**
     * Get Metrics.
     * This end point allows the ACCC to obtain operational statistics from the Data Holder on the operation of their
     * CDR compliant implementation. The statistics obtainable from this end point are determined by the non-functional
     * requirements for the CDR regime.
     */
    @GET
    @Path("/metrics")
    @Produces({"application/json"})
    @ApiOperation(value = "Get Metrics", tags = {"Admin", "Metrics"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = ResponseMetricsListDTO.class)})
    Response getMetrics(@HeaderParam("x-v") @NotNull String xV, @QueryParam("period")
    @DefaultValue("ALL") String period, @HeaderParam("x-min-v") String xMinV);

}
