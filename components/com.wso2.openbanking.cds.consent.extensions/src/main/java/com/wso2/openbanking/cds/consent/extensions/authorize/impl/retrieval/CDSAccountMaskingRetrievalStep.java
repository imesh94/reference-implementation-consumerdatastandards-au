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
import org.apache.commons.lang3.StringUtils;

/**
 * CDS account ID masking step.
 */
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
     * Account number masking is performed in this method. Logic is executed when the account ID length is 2 or higher.
     * The logic is handled like this because the specification doesn't mention the exact length of an account ID.
     *
     * If the account ID length is less than 4, mask all but the last character.
     * If the account ID length is exactly 4, mask all but the last two characters.
     * If the length is greater than 4, mask all but the last 4 characters.
     *
     * @param accountId plain account id.
     * @return account number in the displayable masked format.
     */
    protected String getDisplayableAccountNumber(String accountId) {

        int accountIdLength = accountId.length();

        if (accountIdLength > 1) {
            if (accountIdLength < 4) {
                // If the length is less than 4, mask all but the last character
                String maskedPart = StringUtils.repeat('*', accountIdLength - 1);
                String visiblePart = StringUtils.right(accountId, 1);
                return maskedPart + visiblePart;
            } else if (accountIdLength == 4) {
                // If the length is exactly 4, mask all but the last two characters
                return "**" + StringUtils.right(accountId, 2);
            } else {
                // If the length is greater than 4, mask all but the last 4 characters
                String maskedPart = StringUtils.repeat('*', accountIdLength - 4);
                String visiblePart = StringUtils.right(accountId, 4);
                return maskedPart + visiblePart;
            }
        }
        return accountId;
    }
}
