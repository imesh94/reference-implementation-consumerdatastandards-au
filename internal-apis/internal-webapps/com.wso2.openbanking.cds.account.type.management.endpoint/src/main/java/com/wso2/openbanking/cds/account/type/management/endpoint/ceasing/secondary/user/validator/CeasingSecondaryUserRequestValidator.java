/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.validator;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Ceasing Secondary User - Validator
 */
public class CeasingSecondaryUserRequestValidator {

    private static final Log log = LogFactory.getLog(com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.
            secondary.user.validator.CeasingSecondaryUserRequestValidator.class);

    public static JSONArray ceasingSecondaryUserRequestValidator(String requestBody) throws
            OpenBankingException, Exception {

        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        JSONObject requestBodyJSON = (JSONObject) parser.parse(requestBody);
        JSONArray requestBodyJSONData = (JSONArray) requestBodyJSON.get("data");

        //Checking the presence of "data" field in the request object
        if (requestBodyJSONData == null) {
            throw new OpenBankingException("Bad request : expected field not present for the field \"data\"");
        }

        for (Object requestBodyJSONDataItem : requestBodyJSONData) {
            String accountId = ((JSONObject) requestBodyJSONDataItem).getAsString("accountId");
            String secondaryUserId = ((JSONObject) requestBodyJSONDataItem).getAsString("secondaryUserId");
            String legalEntityId = ((JSONObject) requestBodyJSONDataItem).getAsString("legalEntityId");

            //Checking the validity of the "accountId" field in the request object
            if (accountId == null) {
                throw new OpenBankingException
                        ("Bad request : expected field not present for the field \"accountId\" ");
            }

            //Checking the validity of the "accountId" field in the request object
            if (secondaryUserId == null) {
                throw new OpenBankingException
                        ("Bad request : expected field not present for the field \"secondaryUserId\" ");
            }

            //Checking the validity of the "accountId" field in the request object
            if (legalEntityId == null) {
                throw new OpenBankingException
                        ("Bad request : expected field not present for the field \"legalEntityId\" ");
            }

            //Checking the validity of the value in the "accountId" field in the request object
            if (!accountId.matches("^[a-zA-Z0-9]+$") || accountId.isEmpty()) {
                throw new OpenBankingException
                        ("Bad request : invalid value present for the field \"accountId\" ");
            }

            //Checking the validity of the value in the "accountId" field in the request object
            if (secondaryUserId.isEmpty()) {
                throw new OpenBankingException
                        ("Bad request : invalid value present for the field \"secondaryUserId\" ");
            }

            //Checking the validity of the value in the "accountId" field in the request object
            if (legalEntityId.isEmpty()) {
                throw new OpenBankingException
                        ("Bad request : invalid value present for the field \"legalEntityId\" ");
            }
        }

        return requestBodyJSONData;
    }
}
