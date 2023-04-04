/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.account.metadata.endpoint.nominated.representative.api;

import io.swagger.annotations.ApiParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * NominatedRepresentative API
 */
@Path("/account-metadata")
public interface NominatedRepresentativeAPI {

    @PUT
    @Path("/business-stakeholders")
    @Consumes({"application/json; charset=utf-8"})
    @io.swagger.annotations.ApiOperation(value = "Update Nominated Representative And Account Owner Details\n",
            notes = "This API is used to update the CDS Nominated Representative and Account Owner Details.\n")
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = " Business Stakeholder details successfully " +
                    "updated\n"),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request.\nRequest body validation " +
                    "failed.\n")})
    Response auUpdateBusinessStakeholderDetails(@ApiParam(value = "Business Stakeholder details.\n", required = true)
                                                String requestBody);

    @DELETE
    @Path("/business-stakeholders")
    @Consumes({"application/json; charset=utf-8"})
    @io.swagger.annotations.ApiOperation(value = "Remove Nominated Representatives And Account Owners\n",
            notes = "This API is used to remove the CDS Nominated Representatives And Account Owners.\n")
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = " Business Stakeholders successfully removed.\n"),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request.\nRequest body validation " +
                    "failed.\n")})
    Response auRemoveBusinessStakeholder(@ApiParam(value = "Business Stakeholder details.\n", required = true)
                                         String requestBody);

    @GET
    @Path("/business-stakeholders/permission")
    @Consumes({"application/json; charset=utf-8"})
    @io.swagger.annotations.ApiOperation(value = "Retrieve the permission status of the business users. \n",
            notes = "This API is used to retrieve the permission status of the business users.\n")
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Permission status successfully retrieved\n"),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request.\nRequest body validation " +
                    "failed.\n")})
    Response auBusinessStakeholderPermissionGet(@ApiParam(value = "User identifier of the subject.\n", required = true)
                                                @QueryParam("userId") String userId,
                                                @ApiParam(value = "Account ID of the subject.\n", required = true)
                                                @QueryParam("accountId") String accountIds);

    @GET
    @Path("/business-stakeholders/profiles")
    @Consumes({"application/json; charset=utf-8"})
    @io.swagger.annotations.ApiOperation(value = "Retrieve the available user profiles available for a user. \n",
            notes = "This API is used to retrieve the available user profiles of users.\n")
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Permission status successfully retrieved\n"),
            @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request.\nRequest body validation " +
                    "failed.\n")})
    Response auBusinessStakeholderPermissionGet(@ApiParam(value = "User identifier of the subject.\n", required = true)
                                                @QueryParam("userId") String userId);
}
