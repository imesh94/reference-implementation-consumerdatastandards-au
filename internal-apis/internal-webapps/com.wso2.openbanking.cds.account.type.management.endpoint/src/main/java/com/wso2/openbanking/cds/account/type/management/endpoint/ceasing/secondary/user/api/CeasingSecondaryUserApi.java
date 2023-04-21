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

package com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Ceasing Secondary User - API
 */
@Path("/account-type-management")
public interface CeasingSecondaryUserApi {

    /**
     * ----- Block the sharing status for a legal entity -----
     * An endpoint should be designed to allow an account holder to block a legal entity in order to cease the
     * disclosure initiated by a secondary user for a particular account to that legal entity.
     */
    @PUT
    @Path("/block-legalentitiy")
    @Produces({"application/json"})
    @ApiOperation(value = "This API is used to block the sharing status for a legal entity", tags = {"Legal Entity"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success!, blocked the legal entity"),
            @ApiResponse(code = 400, message = "Error!, failed to block the legal entity")})
    Response blockLegalEntitySharingStatus(@ApiParam(value = "Block the sharing status for a legal entity",
            required = true) String requestBody);

    /**
     * ----- Unblock the sharing status for a legal entity -----
     * An endpoint should be designed to allow an account holder to block a legal entity in order to cease the
     * disclosure initiated by a secondary user for a particular account to that legal entity.
     */
    @PUT
    @Path("/unblock-legalentitiy")
    @Produces({"application/json"})
    @ApiOperation(value = "This API is used to unblock the sharing status for a legal entity", tags = {"Legal Entity"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success!, unblocked the legal entity"),
            @ApiResponse(code = 400, message = "Error!, failed to unblock the legal entity")})
    Response unblockLegalEntitySharingStatus(@ApiParam(value = "Unblock the sharing status for a legal entity",
            required = true) String requestBody);


    /**
     * ----- Get accounts, secondary users, legal entities and their sharing status -----
     * An endpoint should be designed to get all accounts, secondary users, legal entities and their sharing status
     * bound to the account holder in the consent manager dashboard.
     */
    @GET
    @Path("/get-accounts-users-legalentities-details")
    @Produces({"application/json"})
    @ApiOperation(value = "This API is used to get accounts, secondary users, legal entities and their sharing status",
            tags = {"Legal Entity"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success!, retrieved accounts, secondary users, legal entities " +
                    "and their sharing status"),
            @ApiResponse(code = 400, message = "Error!, failed to retrieve accounts, secondary users, legal entities "
                    + "and their sharing status")})
    Response getAccountsUsersLegalEntities(@ApiParam(value = "Get accounts, secondary users, legal entities and their "
            + "sharing status",
            required = true) @QueryParam("userId") String userId);

}
