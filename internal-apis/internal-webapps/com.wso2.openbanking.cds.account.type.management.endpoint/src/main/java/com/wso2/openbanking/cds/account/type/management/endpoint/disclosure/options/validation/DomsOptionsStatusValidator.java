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


package com.wso2.openbanking.cds.account.type.management.endpoint.disclosure.options.validation;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * Validator class to validate the request body of the disclosure options API
 */

public class DomsOptionsStatusValidator {
    JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    public JSONObject validateDomsOptionsStatusRequest(String requestBody) throws OpenBankingException, Exception {

            JSONObject requestBodyJSON = (JSONObject) parser.parse(requestBody);
            JSONArray requestBodyJSONData = (JSONArray) requestBodyJSON.get("data");

            // Check if "data" field is present in the request body
            if (!requestBodyJSON.containsKey("data")) {
                throw new OpenBankingException ("Bad request: expected field not present for the field \"data\"");
            }

            for (Object requestBodyJSONDataItem : requestBodyJSONData) {

            String accountId = ((JSONObject) requestBodyJSONDataItem).getAsString("accountId");
            String disclosureOption = ((JSONObject) requestBodyJSONDataItem).getAsString("disclosureOption");


            // Check if "accountId" field is present in the request body data
            if (!((JSONObject) requestBodyJSONDataItem).containsKey("accountId")) {
                throw new OpenBankingException ("Bad request: expected field not present for the field \"accountId\"");
            }

            // Check if "disclosureOption" field is present in the request body data
            if (!((JSONObject) requestBodyJSONDataItem).containsKey("disclosureOption")) {
                throw new OpenBankingException("Bad request: expected field not present for the " +
                        "field \"disclosureOption\"");
            }

            // Check if "accountId" field is not empty
            if (accountId.isEmpty()) {
                throw new OpenBankingException("Bad request: 'accountId' field is empty");
            }

            // Check if "disclosureOption" field is not empty
            if (disclosureOption.isEmpty()) {
                throw new OpenBankingException("Bad request: 'disclosureOption' field is empty");
            }

            // Check if accountId is a valid format
            if (!accountId.matches("^[a-zA-Z0-9]+$")) {
                throw new OpenBankingException("Bad request: Invalid format for accountId field");
            }

            // Check if disclosureOption is a valid format
            if (!disclosureOption.matches("^[a-zA-Z0-9-]+$")) {
                throw new OpenBankingException("Bad request: Invalid format for disclosureOption field");
            }
        }
       return requestBodyJSON;
    }
}
