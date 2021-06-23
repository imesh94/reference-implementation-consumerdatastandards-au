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
 * Common Service.
 */
@Path("/")
public class CommonService {

    private static final String XV_HEADER = "x-v";
    private static JsonParser jsonParser = new JsonParser();

    @GET
    @Path("/customer")
    @Produces("application/json")
    public Response getCustomer(@HeaderParam(XV_HEADER) String apiVersion) {

        JsonObject customer = getSampleCustomer();
        String response = getResponse(customer, getSampleLinks("/customer"), new JsonObject());

        return Response.status(200).entity(response).header(XV_HEADER, apiVersion).build();
    }

    @GET
    @Path("/customer/detail")
    @Produces("application/json")
    public Response getCustomerDetail(@HeaderParam(XV_HEADER) String apiVersion) {

        JsonObject customer = getSampleCustomerDetail();
        String response = getResponse(customer, getSampleLinks("/customer/detail"), new JsonObject());

        return Response.status(200).entity(response).header(XV_HEADER, apiVersion).build();
    }

    private JsonObject getSampleCustomer() {

        String customerJson = "{\n" +
                "    \"customerUType\": \"person\",\n" +
                "    \"person\": {\n" +
                "      \"lastUpdateTime\": \"string\",\n" +
                "      \"firstName\": \"string\",\n" +
                "      \"lastName\": \"string\",\n" +
                "      \"middleNames\": [\n" +
                "        \"string\"\n" +
                "      ],\n" +
                "      \"prefix\": \"string\",\n" +
                "      \"suffix\": \"string\",\n" +
                "      \"occupationCode\": \"string\"\n" +
                "    },\n" +
                "    \"organisation\": {\n" +
                "      \"lastUpdateTime\": \"string\",\n" +
                "      \"agentFirstName\": \"string\",\n" +
                "      \"agentLastName\": \"string\",\n" +
                "      \"agentRole\": \"string\",\n" +
                "      \"businessName\": \"string\",\n" +
                "      \"legalName\": \"string\",\n" +
                "      \"shortName\": \"string\",\n" +
                "      \"abn\": \"string\",\n" +
                "      \"acn\": \"string\",\n" +
                "      \"isACNCRegistered\": true,\n" +
                "      \"industryCode\": \"string\",\n" +
                "      \"organisationType\": \"SOLE_TRADER\",\n" +
                "      \"registeredCountry\": \"string\",\n" +
                "      \"establishmentDate\": \"string\"\n" +
                "    }\n" +
                "  }";
        return jsonParser.parse(customerJson).getAsJsonObject();
    }

    private JsonObject getSampleCustomerDetail() {

        String customerJson = "{\n" +
                "    \"customerUType\": \"person\",\n" +
                "    \"person\": {\n" +
                "      \"lastUpdateTime\": \"string\",\n" +
                "      \"firstName\": \"string\",\n" +
                "      \"lastName\": \"string\",\n" +
                "      \"middleNames\": [\n" +
                "        \"string\"\n" +
                "      ],\n" +
                "      \"prefix\": \"string\",\n" +
                "      \"suffix\": \"string\",\n" +
                "      \"occupationCode\": \"string\",\n" +
                "      \"phoneNumbers\": [\n" +
                "        {\n" +
                "          \"isPreferred\": true,\n" +
                "          \"purpose\": \"MOBILE\",\n" +
                "          \"countryCode\": \"string\",\n" +
                "          \"areaCode\": \"string\",\n" +
                "          \"number\": \"string\",\n" +
                "          \"extension\": \"string\",\n" +
                "          \"fullNumber\": \"string\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"emailAddresses\": [\n" +
                "        {\n" +
                "          \"isPreferred\": true,\n" +
                "          \"purpose\": \"WORK\",\n" +
                "          \"address\": \"string\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"physicalAddresses\": [\n" +
                "        {\n" +
                "          \"addressUType\": \"simple\",\n" +
                "          \"simple\": {\n" +
                "            \"mailingName\": \"string\",\n" +
                "            \"addressLine1\": \"string\",\n" +
                "            \"addressLine2\": \"string\",\n" +
                "            \"addressLine3\": \"string\",\n" +
                "            \"postcode\": \"string\",\n" +
                "            \"city\": \"string\",\n" +
                "            \"state\": \"string\",\n" +
                "            \"country\": \"string\"\n" +
                "          },\n" +
                "          \"paf\": {\n" +
                "            \"dpid\": \"string\",\n" +
                "            \"thoroughfareNumber1\": 0,\n" +
                "            \"thoroughfareNumber1Suffix\": \"string\",\n" +
                "            \"thoroughfareNumber2\": 0,\n" +
                "            \"thoroughfareNumber2Suffix\": \"string\",\n" +
                "            \"flatUnitType\": \"string\",\n" +
                "            \"flatUnitNumber\": \"string\",\n" +
                "            \"floorLevelType\": \"string\",\n" +
                "            \"floorLevelNumber\": \"string\",\n" +
                "            \"lotNumber\": 0,\n" +
                "            \"buildingName1\": \"string\",\n" +
                "            \"buildingName2\": \"string\",\n" +
                "            \"streetName\": \"string\",\n" +
                "            \"streetType\": \"string\",\n" +
                "            \"streetSuffix\": \"string\",\n" +
                "            \"postalDeliveryType\": \"string\",\n" +
                "            \"postalDeliveryNumber\": 0,\n" +
                "            \"postalDeliveryNumberPrefix\": \"string\",\n" +
                "            \"postalDeliveryNumberSuffix\": \"string\",\n" +
                "            \"localityName\": \"string\",\n" +
                "            \"postcode\": \"string\",\n" +
                "            \"state\": \"string\"\n" +
                "          },\n" +
                "          \"purpose\": \"REGISTERED\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    \"organisation\": {\n" +
                "      \"lastUpdateTime\": \"string\",\n" +
                "      \"agentFirstName\": \"string\",\n" +
                "      \"agentLastName\": \"string\",\n" +
                "      \"agentRole\": \"string\",\n" +
                "      \"businessName\": \"string\",\n" +
                "      \"legalName\": \"string\",\n" +
                "      \"shortName\": \"string\",\n" +
                "      \"abn\": \"string\",\n" +
                "      \"acn\": \"string\",\n" +
                "      \"isACNCRegistered\": true,\n" +
                "      \"industryCode\": \"string\",\n" +
                "      \"organisationType\": \"SOLE_TRADER\",\n" +
                "      \"registeredCountry\": \"string\",\n" +
                "      \"establishmentDate\": \"string\",\n" +
                "      \"physicalAddresses\": [\n" +
                "        {\n" +
                "          \"addressUType\": \"simple\",\n" +
                "          \"simple\": {\n" +
                "            \"mailingName\": \"string\",\n" +
                "            \"addressLine1\": \"string\",\n" +
                "            \"addressLine2\": \"string\",\n" +
                "            \"addressLine3\": \"string\",\n" +
                "            \"postcode\": \"string\",\n" +
                "            \"city\": \"string\",\n" +
                "            \"state\": \"string\",\n" +
                "            \"country\": \"string\"\n" +
                "          },\n" +
                "          \"paf\": {\n" +
                "            \"dpid\": \"string\",\n" +
                "            \"thoroughfareNumber1\": 0,\n" +
                "            \"thoroughfareNumber1Suffix\": \"string\",\n" +
                "            \"thoroughfareNumber2\": 0,\n" +
                "            \"thoroughfareNumber2Suffix\": \"string\",\n" +
                "            \"flatUnitType\": \"string\",\n" +
                "            \"flatUnitNumber\": \"string\",\n" +
                "            \"floorLevelType\": \"string\",\n" +
                "            \"floorLevelNumber\": \"string\",\n" +
                "            \"lotNumber\": 0,\n" +
                "            \"buildingName1\": \"string\",\n" +
                "            \"buildingName2\": \"string\",\n" +
                "            \"streetName\": \"string\",\n" +
                "            \"streetType\": \"string\",\n" +
                "            \"streetSuffix\": \"string\",\n" +
                "            \"postalDeliveryType\": \"string\",\n" +
                "            \"postalDeliveryNumber\": 0,\n" +
                "            \"postalDeliveryNumberPrefix\": \"string\",\n" +
                "            \"postalDeliveryNumberSuffix\": \"string\",\n" +
                "            \"localityName\": \"string\",\n" +
                "            \"postcode\": \"string\",\n" +
                "            \"state\": \"string\"\n" +
                "          },\n" +
                "          \"purpose\": \"REGISTERED\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  }";
        return jsonParser.parse(customerJson).getAsJsonObject();
    }
}
