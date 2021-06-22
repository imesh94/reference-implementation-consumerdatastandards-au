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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static com.wso2.openbanking.cds.demo.backend.services.BankingService.getResponse;
import static com.wso2.openbanking.cds.demo.backend.services.BankingService.getSampleLinks;


/**
 * Discovery Service.
 */
@Path("/")
public class DiscoveryService {

    private static final String XV_HEADER = "x-v";
    private static JsonParser jsonParser = new JsonParser();

    @GET
    @Path("/status")
    @Produces("application/json")
    public Response getDiscoveryStatus(@HeaderParam(XV_HEADER) String apiVersion) {

        String statusJson = "{\n" +
                "    \"status\": \"OK\",\n" +
                "    \"explanation\": \"string\",\n" +
                "    \"detectionTime\": \"string\",\n" +
                "    \"expectedResolutionTime\": \"string\",\n" +
                "    \"updateTime\": \"string\"\n" +
                "  }";

        JsonObject status = jsonParser.parse(statusJson).getAsJsonObject();
        String response = getResponse(status, getSampleLinks("/status"), new JsonObject());

        return Response.status(200).entity(response).header(XV_HEADER, apiVersion).build();
    }

    @GET
    @Path("/outages")
    @Produces("application/json")
    public Response getDiscoveryOutages(@HeaderParam(XV_HEADER) String apiVersion) {

        String outageJson = "{\n" +
                "    \"outages\": [\n" +
                "      {\n" +
                "        \"outageTime\": \"string\",\n" +
                "        \"duration\": \"string\",\n" +
                "        \"isPartial\": true,\n" +
                "        \"explanation\": \"string\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";

        JsonObject outage = jsonParser.parse(outageJson).getAsJsonObject();
        String response = getResponse(outage, getSampleLinks("/outages"), new JsonObject());

        return Response.status(200).entity(response).header(XV_HEADER, apiVersion).build();
    }

}
