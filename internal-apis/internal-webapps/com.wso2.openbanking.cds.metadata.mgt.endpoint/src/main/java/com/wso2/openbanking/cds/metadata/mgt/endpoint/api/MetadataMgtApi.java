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

package com.wso2.openbanking.cds.metadata.mgt.endpoint.api;

import com.wso2.openbanking.cds.metadata.mgt.endpoint.model.MetadataUpdateRequestDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import javax.ws.rs.core.Response;

/**
 *  Metadata Management API
 */
@Path("/admin")
public interface MetadataMgtApi {

    /**
     * Metadata Update.
     * Indicate that a critical update to the metadata for Accredited Data Recipients has been made and
     * should be obtained.
     */
    @POST
    @Path("/register/metadata")
    @Consumes({"application/json"})
    @ApiOperation(value = "Metadata Update", tags = {"Admin", "Register"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success")})
    Response updateMetaData(@HeaderParam("x-v") @NotNull String xV, @Valid MetadataUpdateRequestDTO action,
                                   @HeaderParam("x-min-v") String xMinV);

}
