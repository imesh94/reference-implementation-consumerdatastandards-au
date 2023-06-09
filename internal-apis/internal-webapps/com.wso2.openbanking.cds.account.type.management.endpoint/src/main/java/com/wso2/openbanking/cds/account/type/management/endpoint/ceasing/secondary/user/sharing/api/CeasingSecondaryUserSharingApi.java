/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.sharing.api;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * API Class for Ceasing Secondary User Sharing
 */
@Path("/account-type-management")
public interface CeasingSecondaryUserSharingApi {

    /**
     * ----- Update the sharing status for a legal entity -----
     * An endpoint should be designed to allow an account holder to update a legal entity sharing status.
     *
     * @param requestBody - List of legal entities to be blocked/unblocked
     * @return success or error message when updating the sharing status of legal entities.
     */
    @PUT
    @Path("/legal-entity")
    @Produces({"application/json"})
    @ApiOperation(value = "This API is used to block/unblock the sharing status for a legal entity",
            tags = {"Legal Entity"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success!, updated the sharing status for legal entity"),
            @ApiResponse(code = 400, message = "Error!, failed to update the sharing status for legal entity")})
    Response updateLegalEntitySharingStatus(@ApiParam(value = "Update the sharing status for a legal entity",
            required = true) String requestBody) throws OpenBankingException;


    /**
     * ----- Get users, accounts, legal entities and their sharing status -----
     * An endpoint should be designed to get all accounts, secondary users, legal entities and their sharing status
     * bound to the account holder in the consent manager dashboard.
     *
     * @param userID - The userID of the account holder
     * @return All accounts, secondary users, legal entities and their sharing status
     * bound to the account holder in the consent manager dashboard.
     */
    @GET
    @Path("/legal-entity-list/{userID}")
    @Produces({"application/json"})
    @ApiOperation(value = "This API is used to get accounts, secondary users, legal entities and their sharing status",
            tags = {"Legal Entity"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success!, retrieved accounts, secondary users, legal entities " +
                    "and their sharing status"),
            @ApiResponse(code = 400, message = "Error!, failed to retrieve accounts, secondary users, legal entities "
                    + "and their sharing status")})
    Response getUsersAccountsLegalEntities(@ApiParam(value = "Get accounts, secondary users, legal entities and their "
            + "sharing status",
            required = true) @PathParam("userID") String userID);
}
