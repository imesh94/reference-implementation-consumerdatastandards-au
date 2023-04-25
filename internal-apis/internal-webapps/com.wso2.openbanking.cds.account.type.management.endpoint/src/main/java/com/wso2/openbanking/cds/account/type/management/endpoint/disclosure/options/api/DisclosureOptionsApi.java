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

package com.wso2.openbanking.cds.account.type.management.endpoint.disclosure.options.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *  Disclosure Options API
 */
@Path("/account-type-management")
public interface DisclosureOptionsApi {

    /**
     *  Disclosure Options API
     *  REST API endpoint that updates the disclosure options of the CDS account by
     *  sending a JSON payload in the request body.
     */
    @PUT
    @Path("/update-disclosure-options")
    @Produces({"application/json"})
    @ApiOperation(value = "Update CDS account disclosure options", notes = "This API is used to update the CDS " +
            "account disclosure status.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Account disclosure options successfully updated."),
            @ApiResponse(code = 400, message = "Bad Request. Request body validation failed.")
    })
    Response cdsUpdateAccountDisclosureOptions(@ApiParam(value = "Array of account disclosure option details.\n",
            required = true) String requestBody);

}
