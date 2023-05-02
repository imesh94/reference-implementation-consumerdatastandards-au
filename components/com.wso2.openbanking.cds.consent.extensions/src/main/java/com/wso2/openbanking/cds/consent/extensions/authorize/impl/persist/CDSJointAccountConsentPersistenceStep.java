/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.consent.extensions.authorize.impl.persist;

import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentPersistData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentPersistStep;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.CDSConsentPersistUtil;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Joint accounts consent persistence step for CDS.
 */
public class CDSJointAccountConsentPersistenceStep implements ConsentPersistStep {

    private static final Log log = LogFactory.getLog(CDSJointAccountConsentPersistenceStep.class);

    @Override
    public void execute(ConsentPersistData consentPersistData) throws ConsentException {

        if (consentPersistData.getApproval()) {

            log.debug("Executing CDSJointAccountConsentPersistenceStep.");
            JSONObject payload = consentPersistData.getPayload();
            ArrayList<String> consentedAccountIdList = CDSConsentPersistUtil.getConsentedAccountIdList(payload);
            Map<String, Map<String, String>> jointAccountIdWithUsers = new HashMap<>();

            ConsentData consentData = consentPersistData.getConsentData();
            JSONArray allAccounts = getAllAccounts(consentData);

            for (Object accountDetails : allAccounts) {
                if (accountDetails instanceof JSONObject) {
                    JSONObject account = (JSONObject) accountDetails;
                    if (isValidJointAccount(account, consentedAccountIdList)) {
                        final String consentedAccountId = account.getAsString(CDSConsentExtensionConstants.ACCOUNT_ID);
                        jointAccountIdWithUsers.put(consentedAccountId, getUsersFromAccount(account));
                    }
                }
            }

            // Add joint account data to consentPersistData, used in CDSConsentPersistStep.class
            CDSConsentPersistUtil.addNonPrimaryAccountDataToPersistData(jointAccountIdWithUsers, consentPersistData);

        }
    }

    /**
     * Check whether joint account is sharable.
     *
     * @param consentedAccountIdList: consented account id list
     * @param account:                account received from bank backend
     * @return true if account is a pre-approved joint account
     */
    private boolean isValidJointAccount(JSONObject account, List<String> consentedAccountIdList) {

        final boolean isJointAccount = Boolean.parseBoolean(account
                .getAsString(CDSConsentExtensionConstants.IS_JOINT_ACCOUNT_RESPONSE));

        if (isJointAccount) {
            final String accountId = account.getAsString(CDSConsentExtensionConstants.ACCOUNT_ID);
            final String consentElectionStatus = account
                    .getAsString(CDSConsentExtensionConstants.JOINT_ACCOUNT_CONSENT_ELECTION_STATUS);

            final boolean isSelectableAccount = CDSConsentExtensionConstants.JOINT_ACCOUNT_PRE_APPROVAL
                    .equalsIgnoreCase(consentElectionStatus);

            return isSelectableAccount && consentedAccountIdList.contains(accountId);
        }
        return false;
    }

    /**
     * Get user ids list from joint account.
     *
     * @param jointAccount: consented joint account
     * @return list of user ids
     */
    private Map<String, String> getUsersFromAccount(JSONObject jointAccount) {
        Map<String, String> userIdList = new HashMap<>();
        Object jointAccountInfo = jointAccount.get(CDSConsentExtensionConstants.JOINT_ACCOUNT_INFO);
        if (jointAccountInfo instanceof JSONObject) {
            Object linkedMembers = ((JSONObject) jointAccountInfo).get(CDSConsentExtensionConstants.LINKED_MEMBER);
            if (linkedMembers instanceof JSONArray) {
                for (Object linkedMember : ((JSONArray) linkedMembers)) {
                    if (linkedMember instanceof JSONObject) {
                        userIdList.put(((JSONObject) linkedMember)
                                        .getAsString(CDSConsentExtensionConstants.LINKED_MEMBER_ID),
                                CDSConsentExtensionConstants.LINKED_MEMBER_AUTH_TYPE);
                    }
                }
            }
        }
        return userIdList;
    }

    /**
     * Get all accounts from consent data.
     *
     * @param consentData: consent data from consentPersistData
     * @return JSONArray of accounts data
     */
    private JSONArray getAllAccounts(ConsentData consentData) {

        Object accountsObj = consentData.getMetaDataMap().get(CDSConsentExtensionConstants.ACCOUNTS);
        if (accountsObj instanceof JSONArray) {
            return (JSONArray) accountsObj;
        } else {
            return new JSONArray();
        }
    }

}
