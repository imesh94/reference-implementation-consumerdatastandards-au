/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.consent.extensions.authorize.impl.persist;

import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentPersistData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentPersistStep;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.CDSConsentCommonUtil;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.CDSConsentPersistUtil;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import com.wso2.openbanking.cds.consent.extensions.validate.utils.CDSConsentValidatorUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Secondary accounts consent persistence step for CDS.
 */
public class CDSSecondaryAccountConsentPersistenceStep implements ConsentPersistStep {

    private static final Log log = LogFactory.getLog(CDSSecondaryAccountConsentPersistenceStep.class);

    @Override
    public void execute(ConsentPersistData consentPersistData) throws ConsentException {
        if (consentPersistData.getApproval()) {

            log.debug("Executing CDSSecondaryAccountConsentPersistenceStep");

            Map<String, Map<String, String>> secondaryAccountIdWithOwners = new HashMap<>();
            Map<String, ArrayList<String>> secondaryAccountIDsMapWithPermissions = new HashMap<>();
            String userId = CDSConsentCommonUtil.getUserIdWithTenantDomain(
                    consentPersistData.getConsentData().getUserId());
            consentPersistData.getConsentData().setUserId(userId);

            // Get details of consented accounts
            ArrayList<String> consentedAccountIdList =
                    CDSConsentPersistUtil.getConsentedAccountIdList(consentPersistData.getPayload());
            JSONArray consentedAccountsJsonArray = CDSConsentPersistUtil.getRequestedAccounts(
                    consentPersistData.getConsentData(), consentedAccountIdList);

            for (Object accountDetails : consentedAccountsJsonArray) {
                if (accountDetails instanceof JSONObject) {
                    JSONObject account = (JSONObject) accountDetails;
                    final String consentedAccountId = account.getAsString(CDSConsentExtensionConstants.ACCOUNT_ID);

                    try {
                        if (isValidSecondaryAccount(userId, account, consentedAccountIdList)) {
                            secondaryAccountIdWithOwners.put(consentedAccountId, getOwnersOfSecondaryAccount(account));

                            // update consent mapping permissions for secondary accounts
                            ArrayList<String> accountMappingPermissions = new ArrayList<>();
                            accountMappingPermissions.add(CDSConsentExtensionConstants.SECONDARY_ACCOUNT_USER);
                            secondaryAccountIDsMapWithPermissions.put(consentedAccountId, accountMappingPermissions);
                        }
                    } catch (ConsentException e) {
                        log.error("Error occurred while validating secondary account: " + consentedAccountId, e);
                        throw new ConsentException(e.getStatus(), e.getMessage());
                    }
                }
            }

            // Add secondary account data to consentPersistData, used in CDSConsentPersistStep.class
            CDSConsentPersistUtil.
                    addNonPrimaryAccountDataToPersistData(secondaryAccountIdWithOwners,
                            secondaryAccountIDsMapWithPermissions, consentPersistData);
        }
    }

    /**
     * Check whether secondary account is sharable.
     *
     * @param userId:                secondary user id
     * @param consentedAccountIdList: consented account id list
     * @param account:                account received from bank backend
     * @return true if user has secondaryAccountPrivilege and secondaryAccountInstruction
     * statuses are in an active state for account
     */
    private boolean isValidSecondaryAccount(String userId, JSONObject account, List<String> consentedAccountIdList)
            throws ConsentException {

        final boolean isSecondaryAccount = Boolean.parseBoolean(account
                .getAsString(CDSConsentExtensionConstants.IS_SECONDARY_ACCOUNT_RESPONSE)) &&
                CDSConsentExtensionConstants.SECONDARY_ACCOUNT_TYPE.equals(account.getAsString(
                CDSConsentExtensionConstants.CUSTOMER_ACCOUNT_TYPE));

        if (isSecondaryAccount) {
            final String accountId = account.getAsString(CDSConsentExtensionConstants.ACCOUNT_ID);

            /* secondaryAccountPrivilegeStatus depicts whether the user has granted permission to share data from
             the secondary account */
            Boolean secondaryAccountPrivilegeStatus = Boolean.valueOf(String
                    .valueOf(account.get(CDSConsentExtensionConstants.SECONDARY_ACCOUNT_PRIVILEGE_STATUS)));

            // secondaryAccountInstructionStatus depicts whether the account is enabled for secondary user data sharing
            Boolean secondaryAccountInstructionStatus =
                    CDSConsentValidatorUtil.isUserEligibleForSecondaryAccountDataSharing(
                    account.getAsString(CDSConsentExtensionConstants.ACCOUNT_ID), userId);

            Boolean isShareableAccount = secondaryAccountInstructionStatus && secondaryAccountPrivilegeStatus;

            if (!isShareableAccount) {
                log.error("Secondary account instruction is not granted for account: " + accountId);
                throw new ConsentException(ResponseStatus.PRECONDITION_FAILED,
                                "Secondary account instruction is not granted for account: " + accountId);
            }

            // validate secondary user joint accounts
            if (Boolean.parseBoolean(account
                    .getAsString(CDSConsentExtensionConstants.IS_JOINT_ACCOUNT_RESPONSE))) {
                final String consentElectionStatus = account
                        .getAsString(CDSConsentExtensionConstants.JOINT_ACCOUNT_CONSENT_ELECTION_STATUS);

                final boolean isPreApproved = CDSConsentExtensionConstants.JOINT_ACCOUNT_PRE_APPROVAL
                        .equalsIgnoreCase(consentElectionStatus);

                return (isPreApproved && isShareableAccount) && consentedAccountIdList.contains(accountId);
            }

            return isShareableAccount && consentedAccountIdList.contains(accountId);
        }
        return false;
    }

    /**
     * Get secondary account owner against auth type map.
     *
     * @param secondaryAccount: consented secondary account
     * @return list of user ids
     */
    private Map<String, String> getOwnersOfSecondaryAccount(JSONObject secondaryAccount) {
        Map<String, String> userIdPrivilegeMap = new HashMap<>();
        Object secondaryAccountInfo = secondaryAccount.get(CDSConsentExtensionConstants.SECONDARY_ACCOUNT_INFO);
        if (secondaryAccountInfo instanceof JSONObject) {
            Object accountOwners = ((JSONObject) secondaryAccountInfo)
                    .get(CDSConsentExtensionConstants.SECONDARY_ACCOUNT_OWNER_LIST);
            if (accountOwners instanceof JSONArray) {
                for (Object accountOwner : ((JSONArray) accountOwners)) {
                    if (accountOwner instanceof JSONObject) {
                        String accountOwnerId = ((JSONObject) accountOwner)
                                .getAsString(CDSConsentExtensionConstants.LINKED_MEMBER_ID);
                        userIdPrivilegeMap.put(accountOwnerId, CDSConsentExtensionConstants.SECONDARY_ACCOUNT_OWNER);
                        if (log.isDebugEnabled()) {
                            log.debug("Added secondary account owner:" + accountOwnerId + " to the list of users " +
                                    "to be persisted");
                        }
                    }
                }
            }
        }
        return userIdPrivilegeMap;
    }
}
