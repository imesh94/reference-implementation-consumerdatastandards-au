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

package com.wso2.openbanking.cds.demo.backend.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Identify the customer type in the consent flow.
 * CustomerDetailsService class
 */
@Path("/")
public class CustomerDetails {

    //customerUType can be 'Person' or 'Organisation'
    private static final String customerOrg = "{\n" +
            "  \"customerUType\": \"organisation\" " +
            "}";

    private static final String customerPer = "{\n" +
            "  \"customerUType\": \"person\" " +
            "}";

    @GET
    @Path("/details/{userId}")
    @Produces("application/json; charset=utf-8")
    public Response getCustomerDetails(@PathParam("userId") String userId) {

        //Try different user to check person profile
        if ("admin@wso2.com".equals(userId)) {
            return Response.status(200).entity(customerOrg).build();
        } else {
            return Response.status(200).entity(customerPer).build();
        }
    }

}
