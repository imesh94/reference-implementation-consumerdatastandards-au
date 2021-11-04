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

package com.wso2.openbanking.cds.consent.extensions.authorize.impl.persist;

import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentPersistData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentPersistStep;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.CDSDataRetrievalUtil;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
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
            JSONObject payload = consentPersistData.getPayload();
            ArrayList<String> consentedAccountIdList = getConsentedAccountIdList(payload);
            Map<String, List<String>> jointAccountIdWithUsers = new HashMap<>();

            final String accountsUrl = (String) OpenBankingCDSConfigParser.getInstance().getConfiguration()
                    .get(CDSConsentExtensionConstants.SHARABLE_ACCOUNTS_ENDPOINT);
            ConsentData consentData = consentPersistData.getConsentData();
            JSONArray allAccounts = getAccountsJsonFromEndpoint(accountsUrl, consentData.getUserId());

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
            consentPersistData.addMetadata(CDSConsentExtensionConstants.MAP_JOINT_ACCOUNTS_ID_WITH_USERS,
                    jointAccountIdWithUsers);
            consentPersistData.addMetadata(CDSConsentExtensionConstants.MAP_USER_ID_WITH_JOINT_ACCOUNTS,
                    getUsersWithMultipleJointAccounts(jointAccountIdWithUsers));
        }
    }

    /**
     * Check whether joint account is sharable
     *
     * @param consentedAccountIdList: consented account id list
     * @param account:            account received from bank backend
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
     * Get user ids list from joint account
     *
     * @param jointAccount: consented joint account
     * @return list of user ids
     */
    private List<String> getUsersFromAccount(JSONObject jointAccount) {
        List<String> userIdList = new ArrayList<>();
        Object jointAccountInfo = jointAccount.get(CDSConsentExtensionConstants.JOINT_ACCOUNT_INFO);
        if (jointAccountInfo instanceof JSONObject) {
            Object linkedMembers = ((JSONObject) jointAccountInfo).get(CDSConsentExtensionConstants.LINKED_MEMBER);
            if (linkedMembers instanceof JSONArray) {
                for (Object linkedMember : ((JSONArray) linkedMembers)) {
                    if (linkedMember instanceof JSONObject) {
                        userIdList.add(((JSONObject) linkedMember)
                                .getAsString(CDSConsentExtensionConstants.LINKED_MEMBER_ID));
                    }
                }
            }
        }
        return userIdList;
    }

    /**
     * Get account list from payload data and check for validity
     *
     * @param payloadData payload data of retrieved from persist data
     * @return List of user consented accounts
     */
    private ArrayList<String> getConsentedAccountIdList(JSONObject payloadData) throws ConsentException {

        if (!(payloadData.get(CDSConsentExtensionConstants.ACCOUNT_IDS) instanceof JSONArray)) {
            log.error("Account IDs not available in persist request");
            throw new ConsentException(ResponseStatus.BAD_REQUEST,
                    "Account IDs not available in persist request");
        }

        ArrayList<String> accountIdsList = new ArrayList<>();
        JSONArray accountIds = (JSONArray) payloadData.get(CDSConsentExtensionConstants.ACCOUNT_IDS);
        for (Object account : accountIds) {
            if (!(account instanceof String)) {
                log.error("Account IDs format error in persist request");
                throw new ConsentException(ResponseStatus.BAD_REQUEST,
                        "Account IDs format error in persist request");
            }
            accountIdsList.add((String) account);
        }
        return accountIdsList;
    }

    /**
     * Get accounts data from bank backend
     *
     * @param accountsUrl: sharable accounts url
     * @param userId:      ID of the primary user
     * @return JSONArray of accounts data
     */
    private JSONArray getAccountsJsonFromEndpoint(String accountsUrl, String userId) {

        if (StringUtils.isNotBlank(accountsUrl)) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put(CDSConsentExtensionConstants.USER_ID_KEY_NAME, userId);

            final String accountData = CDSDataRetrievalUtil.getAccountsFromEndpoint(accountsUrl, parameters,
                    new HashMap<>());

            if (StringUtils.isBlank(accountData)) {
                log.error("Unable to load accounts data for the user: " + userId);
                throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                        "Exception occurred while getting accounts data");
            }

            JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
            try {
                JSONObject jsonAccountData = (JSONObject) parser.parse(accountData);
                return (JSONArray) jsonAccountData.get(CDSConsentExtensionConstants.DATA);
            } catch (ParseException e) {
                log.error("Exception occurred while parsing accounts data. Caused by, ", e);
                throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                        "Exception occurred while parsing accounts data");
            }
        } else {
            log.error("Sharable accounts endpoint is not configured properly");
            throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                    "Sharable accounts endpoint is not configured properly");
        }
    }

    /**
     * Generate a list of account ids for users who has multiple joint accounts
     *
     * @param jointAccountIdWithUsers: joint account id and linked members map
     * @return a map of user ids and joint account ids
     */
    private Map<String, List<String>> getUsersWithMultipleJointAccounts(Map<String, List<String>>
                                                                                jointAccountIdWithUsers) {
        Map<String, List<String>> userWithJointAccountIds = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : jointAccountIdWithUsers.entrySet()) {
            final String jointAccountId = entry.getKey();
            final List<String> userIdList = entry.getValue();
            for (String userId : userIdList) {
                if (userWithJointAccountIds.containsKey(userId)) {
                    userWithJointAccountIds.get(userId).add(jointAccountId);
                } else {
                    userWithJointAccountIds.put(userId, new ArrayList<>(Collections.singletonList(jointAccountId)));
                }
            }
        }
        return userWithJointAccountIds;
    }
}
