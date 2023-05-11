/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.account.type.management.endpoint.secondary.user.instruction.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *  Secondary User API
 */
@Path("/account-type-management")
public interface SecondaryUserInstructionApi {

    /**
     * Update Secondary User Instructions.
     * This end point allows Data Holder to update OB solution on secondary user instruction changes.
     */
    @PUT
    @Path("/secondary-accounts")
    @Consumes({"application/json; charset=utf-8"})
    @Produces({"application/json; charset=utf-8"})
    @ApiOperation(value = "Update Secondary Accounts Instruction Status\n",
            notes = "This API is used to update the CDS Secondary Accounts Instruction and Privilege Status.\n")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = " Secondary Accounts Instruction Status successfully updated\n"),
            @ApiResponse(code = 400, message = "Bad Request.\nRequest body validation failed.\n")})
    Response updateSecondaryAccountStatus(
            @ApiParam(value = "Secondary Accounts Instruction and Privilege Status details.\n", required = true)
            String requestBody);
}
