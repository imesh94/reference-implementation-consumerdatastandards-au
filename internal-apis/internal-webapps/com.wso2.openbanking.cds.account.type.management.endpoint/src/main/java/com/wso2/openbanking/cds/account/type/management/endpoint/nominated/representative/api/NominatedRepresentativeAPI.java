/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Nominated Representative API.
 * Used to manage the permissions of business nominated representatives.
 */
@Path("/account-type-management")
public interface NominatedRepresentativeAPI {

    /**
     * Update the permissions of business nominated representatives.
     *
     * @param requestBody - Business nominated representative details
     * @return - Success or failure response
     */
    @PUT
    @Path("/business-stakeholders")
    @Consumes({"application/json; charset=utf-8"})
    @Produces({"application/json; charset=utf-8"})
    @ApiOperation(value = "Update Business Nominated Representative Permissions\n",
            notes = "This API is used to update the CDS Business Nominated Representative Permissions.\n")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Business nominated representative permissions successfully updated\n"),
            @ApiResponse(code = 400, message = "Bad Request.\nRequest body validation failed.\n")})
    Response updateNominatedRepresentativePermissions(
            @ApiParam(value = "Business nominated representative permissions.\n", required = true) String requestBody
    );

    /**
     * Revoke the permissions of business nominated representatives.
     *
     * @param requestBody - Business nominated representative details
     * @return - Success or failure response
     */
    @DELETE
    @Path("/business-stakeholders")
    @Consumes({"application/json; charset=utf-8"})
    @Produces({"application/json; charset=utf-8"})
    @ApiOperation(value = "Revoke the permissions of business nominated representatives\n",
            notes = "This API is used to revoke the permissions of business nominated representatives.\n")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Business nominated representative permissions successfully revoked\n"),
            @ApiResponse(code = 400, message = "Bad Request.\nRequest body validation failed.\n")})
    Response revokeNominatedRepresentativePermissions(
            @ApiParam(value = "Business nominated representative permissions.\n", required = true) String requestBody
    );

    /**
     * Retrieve the permissions of business nominated representatives.
     *
     * @return - Business nominated representative permission details
     */
    @GET
    @Path("/business-stakeholders/permission")
    @Consumes({"application/json; charset=utf-8"})
    @ApiOperation(value = "Retrieve the permission status of the business nominated representatives. \n",
            notes = "This API is used to retrieve the permission status of the business nominated representatives.\n")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Permission status successfully retrieved\n"),
            @ApiResponse(code = 400, message = "Bad Request.\nRequest body validation failed.\n")})
    Response retrieveNominatedRepresentativePermissions(
            @ApiParam(value = "Account ID of the subject.\n", required = true)
            @QueryParam("accountId") String accountId,
            @ApiParam(value = "User identifier of the subject.\n", required = true)
            @QueryParam("userId") String userId
    );

    @GET
    @Path("/business-stakeholders/profiles")
    @Consumes({"application/json; charset=utf-8"})
    @ApiOperation(value = "Retrieve the available user profiles available for a user. \n",
            notes = "This API is used to retrieve the available user profiles of users.\n")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Permission status successfully retrieved\n"),
            @ApiResponse(code = 400, message = "Bad Request.\nRequest body validation failed.\n")})
    Response retrieveNominatedRepresentativeProfiles(
            @ApiParam(value = "User identifier of the subject.\n", required = true)
            @QueryParam("userId") String userId
    );

}
