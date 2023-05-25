/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.account.type.management.endpoint.disclosure.options.handler;

import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataService;
import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataServiceImpl;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import javax.ws.rs.core.Response;

/**
 * Handler class for handling CDS Account Disclosure Options requests.
 */

public class DisclosureOptionsApiHandler {
    private static final Log log = LogFactory.getLog(DisclosureOptionsApiHandler.class);
    AccountMetadataService accountMetadataService = AccountMetadataServiceImpl.getInstance();

    public Response cdsUpdateAccountDisclosureOptions(String requestBody) {

        log.debug("Update Account Disclosure Options request received");
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);

        try {
            JSONObject requestBodyJSON = (JSONObject) parser.parse(requestBody);
            JSONArray requestBodyJSONData = (JSONArray) requestBodyJSON.get("data");

            for (Object requestBodyJSONDataItem : requestBodyJSONData) {

                String accountId = ((JSONObject) requestBodyJSONDataItem).getAsString("accountID");
                String disclosureOption = ((JSONObject) requestBodyJSONDataItem).getAsString("disclosureOption");

                // Add the disclosureOption value to a HashMap
                HashMap<String, String> disclosureOptionsMap = new HashMap<String, String>();
                disclosureOptionsMap.put("DISCLOSURE_OPTIONS_STATUS", disclosureOption);

                // Call the addOrUpdateGlobalAccountMetadata method from the AccountMetadataService class
                accountMetadataService.addOrUpdateGlobalAccountMetadata(accountId, disclosureOptionsMap);
            }
            return Response.ok().entity("Account disclosure options successfully updated").build();
        } catch (OpenBankingException e) {
            log.error(e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Bad Request. Request body validation failed", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}



