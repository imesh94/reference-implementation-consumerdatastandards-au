/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.consent.extensions.authorize.impl.retrieval;

import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentRetrievalStep;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
public class CDSAccountMaskingRetrievalStep implements ConsentRetrievalStep {

    @Override
    public void execute(ConsentData consentData, JSONObject jsonObject) throws ConsentException {

        boolean isAccountMaskingEnabled = OpenBankingCDSConfigParser.getInstance().isAccountMaskingEnabled();

        if (isAccountMaskingEnabled) {
            JSONArray accountsJSON = (JSONArray) jsonObject.get(CDSConsentExtensionConstants.ACCOUNTS);
            JSONArray updatedAccountsJSON = new JSONArray();

            for (Object accountElement : accountsJSON) {
                JSONObject account = (JSONObject) accountElement;
                String accountId = (String) account.get(CDSConsentExtensionConstants.ACCOUNT_ID);
                String accountNumberDisplay = getDisplayableAccountNumber(accountId);
                account.put(CDSConsentExtensionConstants.ACCOUNT_ID_DISPLAYABLE, accountNumberDisplay);
                updatedAccountsJSON.add(account);

            }
            jsonObject.put(CDSConsentExtensionConstants.ACCOUNTS, updatedAccountsJSON);
        }
    }

    /**
     * Account number masking is performed in this method.
     *
     * @param accountId plain account id.
     * @return account number in the displayable masked format.
     */
    protected String getDisplayableAccountNumber(String accountId) {

        String accountNumberDisplay;
        String patternRegex = ".(?=.{3})";
        String lastDigits = accountId.substring(4);
        lastDigits = lastDigits.replaceAll(patternRegex, "*");
        accountNumberDisplay = accountId.substring(0, 4) + lastDigits;
        return accountNumberDisplay;
    }
}
