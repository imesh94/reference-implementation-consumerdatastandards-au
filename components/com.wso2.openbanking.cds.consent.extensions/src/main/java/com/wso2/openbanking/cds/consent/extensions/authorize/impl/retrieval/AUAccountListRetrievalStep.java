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
package com.wso2.openbanking.cds.consent.extensions.authorize.impl.retrieval;

import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentRetrievalStep;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.consent.extensions.authorize.impl.utils.AUDataRetrievalUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Account List retrieval step CDS implementation.
 */
public class AUAccountListRetrievalStep implements ConsentRetrievalStep {

    private static final Log log = LogFactory.getLog(AUAccountListRetrievalStep.class);
    private static final String USER_ID_KEY_NAME = "userID";

    @Override
    public void execute(ConsentData consentData, JSONObject jsonObject) throws ConsentException {

        String accountsURL = (String) OpenBankingCDSConfigParser.getInstance().getConfiguration()
                .get("ConsentManagement.SharableAccountsRetrieveEndpoint");

        if (StringUtils.isNotBlank(accountsURL)) {

            Map<String, String> parameters = new HashMap<>();
            parameters.put(USER_ID_KEY_NAME, consentData.getUserId());
            String accountData = AUDataRetrievalUtil.getAccountsFromEndpoint(accountsURL, parameters, new HashMap<>());

            if (accountData == null) {
                log.error("Unable to load accounts data for the user: " + consentData.getUserId());
                throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                        "Exception occurred while getting accounts data");
            }
            JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
            try {
                JSONObject jsonAccountData = (JSONObject) parser.parse(accountData);
                JSONArray accountsJSON = (JSONArray) jsonAccountData.get("data");
                jsonObject.appendField("accounts", accountsJSON);
            } catch (ParseException e) {
                throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                        "Exception occurred while getting accounts data");
            }
        }
    }
}
