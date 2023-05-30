/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.consent.extensions.authorize.impl.retrieval;

import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataServiceImpl;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentRetrievalStep;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.CDSConsentCommonUtil;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.CustomerTypeSelectionMethodEnum;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Account List retrieval step CDS implementation.
 */
public class CDSProfileListRetrievalStep implements ConsentRetrievalStep {

    private static final Log log = LogFactory.getLog(CDSProfileListRetrievalStep.class);
    AccountMetadataServiceImpl accountMetadataService = AccountMetadataServiceImpl.getInstance();

    @Override
    public void execute(ConsentData consentData, JSONObject jsonObject) throws ConsentException {

        if (!consentData.isRegulatory()) {
            return;
        }
        JSONArray accountsJSON = (JSONArray) jsonObject.get(CDSConsentExtensionConstants.ACCOUNTS);
        if (accountsJSON == null || accountsJSON.isEmpty()) {
            return;
        }
        log.info("Engaging CDS profile list retrieval step.");
        //Consent amendment flow. Get selected profile.
        if (jsonObject.containsKey(CDSConsentExtensionConstants.IS_CONSENT_AMENDMENT) &&
                (boolean) jsonObject.get(CDSConsentExtensionConstants.IS_CONSENT_AMENDMENT)) {
            executeConsentAmendmentFlow(accountsJSON, jsonObject);
        }

        // Get Customer Type Selection Method from config
        String customerTypeSelectionMethod = OpenBankingCDSConfigParser.getInstance().
                getBNRCustomerTypeSelectionMethod();
        String customerType = null;
        if (CustomerTypeSelectionMethodEnum.CUSTOMER_UTYPE.toString().equals(customerTypeSelectionMethod)) {
            customerType = CDSConsentCommonUtil.getCustomerType(consentData);
        } else if (CustomerTypeSelectionMethodEnum.COOKIE_DATA.toString().equals(customerTypeSelectionMethod)) {
            //ToDo: Implement cookie data based customer type selection.
            customerType = null;
        }

        Map<String, String> profileMap = new HashMap<>();
        Map<String, List<String>> profileIdAccountsMap = new HashMap<>();
        String userId = CDSConsentCommonUtil.getUserIdWithTenantDomain(consentData.getUserId());
        for (Object account : accountsJSON) {
            JSONObject accountJSON = (JSONObject) account;
            // Check if the current user has permission to authorize the account.
            boolean isUserEligible = isUserEligibleForConsentAuthorization(userId, accountJSON);

            // Process business accounts.
            if (isUserEligible && accountJSON.containsKey(CDSConsentExtensionConstants.
                    CUSTOMER_ACCOUNT_TYPE) && StringUtils.equals((String) accountJSON.get(
                    CDSConsentExtensionConstants.CUSTOMER_ACCOUNT_TYPE), CDSConsentExtensionConstants.
                    BUSINESS_PROFILE_TYPE)) {
                if (customerType != null && customerType.equalsIgnoreCase(
                        CDSConsentExtensionConstants.ORGANISATION)) {
                    jsonObject.put(CDSConsentExtensionConstants.PRE_SELECTED_PROFILE_ID,
                            CDSConsentExtensionConstants.ORGANISATION_PROFILE_ID);
                    profileIdAccountsMap = getProfileIdAccountsMapForGeneralBusinessAccounts(
                            profileIdAccountsMap, profileMap, accountJSON);
                } else {
                    profileIdAccountsMap = getProfileIdAccountsMapForProfiledAccounts(profileIdAccountsMap,
                            profileMap, accountJSON);
                }
            //Process individual accounts (non-business accounts are processed as individual).
            } else {
                if (customerType != null && customerType.equalsIgnoreCase(CDSConsentExtensionConstants.
                        PERSON)) {
                    jsonObject.put(CDSConsentExtensionConstants.PRE_SELECTED_PROFILE_ID,
                            CDSConsentExtensionConstants.INDIVIDUAL_PROFILE_ID);
                }
                profileIdAccountsMap = getProfileIdAccountsMapForIndividualAccounts(profileIdAccountsMap,
                        profileMap, accountJSON);
            }
        }

        /*If there is only one profile, set it as the pre-selected profile so the profile selection can
         be skipped.*/
        if (profileMap.size() == 1) {
            String profileId = profileMap.keySet().iterator().next();
            jsonObject.put(CDSConsentExtensionConstants.PRE_SELECTED_PROFILE_ID, profileId);
        }
        JSONArray customerProfilesJson = getCustomerProfilesAsJson(profileMap, profileIdAccountsMap);
        jsonObject.put(CDSConsentExtensionConstants.CUSTOMER_PROFILES_ATTRIBUTE, customerProfilesJson);

    }

    /**
     * Get customer profile data as JSON.
     *
     * @param profileMap           profile map
     * @param profileIdAccountsMap profile id accounts map
     * @return customer profile data as JSONArray
     */
    private JSONArray getCustomerProfilesAsJson(Map<String, String> profileMap,
                                                Map<String, List<String>> profileIdAccountsMap) {

        JSONArray profilesArray = new JSONArray();
        for (Map.Entry<String, String> entry : profileMap.entrySet()) {
            JSONObject profileJSON = new JSONObject();
            profileJSON.put(CDSConsentExtensionConstants.PROFILE_ID, entry.getKey());
            profileJSON.put(CDSConsentExtensionConstants.PROFILE_NAME, entry.getValue());

            List<String> accountIdList = profileIdAccountsMap.get(entry.getKey());
            JSONArray accountsArray = new JSONArray();
            accountsArray.addAll(accountIdList);
            profileJSON.put(CDSConsentExtensionConstants.ACCOUNT_IDS, accountsArray);

            profilesArray.add(profileJSON);
        }
        return profilesArray;
    }

    /**
     * Get nominated representatives from account response.
     *
     * @param accountJSON account response
     * @return nominated representatives
     */
    private List<String> getNominatedRepresentativesFromAccountResponse(JSONObject accountJSON) {
        List<String> nominatedRepresentatives = new ArrayList<>();
        if (accountJSON.containsKey(CDSConsentExtensionConstants.BUSINESS_ACCOUNT_INFO)) {
            JSONObject businessAccountInfoJson = (JSONObject) accountJSON.get(CDSConsentExtensionConstants.
                    BUSINESS_ACCOUNT_INFO);
            if (businessAccountInfoJson != null && businessAccountInfoJson.containsKey(CDSConsentExtensionConstants.
                    NOMINATED_REPRESENTATIVES)) {
                JSONArray nominatedRepresentativesArray = (JSONArray) businessAccountInfoJson.
                        get(CDSConsentExtensionConstants.NOMINATED_REPRESENTATIVES);
                for (Object nominatedRepresentative : nominatedRepresentativesArray) {
                    JSONObject nominatedRepresentativeJSON = (JSONObject) nominatedRepresentative;
                    if (nominatedRepresentativeJSON.containsKey(CDSConsentExtensionConstants.MEMBER_ID)) {
                        nominatedRepresentatives.add((String) nominatedRepresentativeJSON.get(
                                CDSConsentExtensionConstants.MEMBER_ID));
                    }
                }
            }

        }
        return nominatedRepresentatives;
    }

    /**
     * Check if the current user has permission to authorize the account.
     *
     * @param userId      - user id
     * @param accountJSON - account json
     * @return true if the user is eligible for consent authorization
     * @throws ConsentException - Consent Exception
     */
    private boolean isUserEligibleForConsentAuthorization(String userId, JSONObject accountJSON) throws
            ConsentException {

        boolean isEligible;
        // If the config is enabled, get eligibility from the sharable accounts response. Otherwise, get
        // eligibility from the Account_Metadata table.
        if (OpenBankingCDSConfigParser.getInstance().isBNRPrioritizeSharableAccountsResponseEnabled()) {
            isEligible = getNominatedRepresentativesFromAccountResponse(accountJSON).contains(userId);
        } else {
            String accountId = (String) accountJSON.get(CDSConsentExtensionConstants.ACCOUNT_ID);
            try {
                String permissionStatus = accountMetadataService.getAccountMetadataByKey(accountId, userId,
                        CDSConsentExtensionConstants.BNR_PERMISSION);
                isEligible = permissionStatus == null || CDSConsentExtensionConstants.BNR_AUTHORIZE_PERMISSION.equals(
                        permissionStatus);
            } catch (OpenBankingException e) {
                log.error("Error occurred while checking nominated representative permissions in the database", e);
                throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                        "Error occurred while checking nominated representative permissions in the database");
            }
        }
        return isEligible;
    }

    /**
     * Execute profile list retrieval consent amendment flow.
     * This method will get the pre-selected profile from the pre-selected accounts.
     *
     * @param accountsJSON - accountsJSON
     * @param jsonObject   - jsonObject
     */
    public void executeConsentAmendmentFlow(JSONArray accountsJSON, JSONObject jsonObject) {
        JSONArray preSelectedAccounts = (JSONArray) jsonObject.get(CDSConsentExtensionConstants.
                PRE_SELECTED_ACCOUNT_LIST);

        if (preSelectedAccounts != null) {
            for (Object account : accountsJSON) {
                JSONObject accountJson = (JSONObject) account;

                if (accountJson.containsKey(CDSConsentExtensionConstants.ACCOUNT_ID) &&
                        preSelectedAccounts.contains(accountJson.get(CDSConsentExtensionConstants.ACCOUNT_ID))) {
                    String customerAccountType = (String) accountJson.get(
                            CDSConsentExtensionConstants.CUSTOMER_ACCOUNT_TYPE);
                    String preSelectedProfileId = CDSConsentExtensionConstants.INDIVIDUAL_PROFILE_ID;

                    if (CDSConsentExtensionConstants.BUSINESS_PROFILE_TYPE.equals(customerAccountType)) {
                        preSelectedProfileId = (String) accountJson.getOrDefault(
                                CDSConsentExtensionConstants.PROFILE_ID, preSelectedProfileId);
                    }

                    jsonObject.put(CDSConsentExtensionConstants.PRE_SELECTED_PROFILE_ID, preSelectedProfileId);

                    if (log.isDebugEnabled()) {
                        log.debug("Profile id: " + preSelectedProfileId + " selected for the consent amendment " +
                                "flow.");
                    }
                    break;
                }
            }
        }
    }

    /**
     * Get profileId to accountIds map for business accounts with profiles.
     *
     * @param accountJSON account Json
     * @return profileId to accountIds map
     */
    public Map<String, List<String>> getProfileIdAccountsMapForProfiledAccounts(
            Map<String, List<String>> profileIdAccountsMap, Map<String, String> profileMap, JSONObject accountJSON) {
        // Create maps of profileId to profileName and profileId to accountIds for business accounts.
        String accountId = (String) accountJSON.get(CDSConsentExtensionConstants.ACCOUNT_ID);
        String profileId = (String) accountJSON.get(CDSConsentExtensionConstants.PROFILE_ID);
        String profileName = (String) accountJSON.get(CDSConsentExtensionConstants.PROFILE_NAME);
        // Add profiles to map
        if (!profileMap.containsKey(profileId)) {
            profileMap.put(profileId, profileName);
        }
        // Add accounts to map
        if (profileIdAccountsMap.containsKey(profileId)) {
            profileIdAccountsMap.get(profileId).add(accountId);
        } else {
            profileIdAccountsMap.put(profileId, new ArrayList<>(Collections.
                    singletonList(accountId)));
        }

        return profileIdAccountsMap;
    }

    /**
     * Get profileId to accountIds map for general business accounts with no profile.
     *
     * @param accountJSON account Json
     * @return profileId to accountIds map
     */
    public Map<String, List<String>> getProfileIdAccountsMapForGeneralBusinessAccounts(
            Map<String, List<String>> profileIdAccountsMap, Map<String, String> profileMap, JSONObject accountJSON) {
        // Create maps of profileId to profileName and profileId to accountIds for business accounts.
        String accountId = (String) accountJSON.get(CDSConsentExtensionConstants.ACCOUNT_ID);

        // Add 'Organization' as profile name and "organization" as profile id for general business accounts.
        if (!profileMap.containsKey(CDSConsentExtensionConstants.ORGANISATION_PROFILE_ID)) {
            profileMap.put(CDSConsentExtensionConstants.ORGANISATION_PROFILE_ID,
                    CDSConsentExtensionConstants.ORGANISATION);
        }
        // Add accounts to map
        if (profileIdAccountsMap.containsKey(CDSConsentExtensionConstants.ORGANISATION_PROFILE_ID)) {
            profileIdAccountsMap.get(CDSConsentExtensionConstants.ORGANISATION_PROFILE_ID).add(accountId);
        } else {
            profileIdAccountsMap.put(CDSConsentExtensionConstants.ORGANISATION_PROFILE_ID, new ArrayList<>(Collections.
                    singletonList(accountId)));
        }

        return profileIdAccountsMap;
    }

    /**
     * Get profileId to accountIds map for individual accounts.
     *
     * @param accountJSON account Json
     * @return profileId to accountIds map
     */
    public Map<String, List<String>> getProfileIdAccountsMapForIndividualAccounts(
            Map<String, List<String>> profileIdAccountsMap, Map<String, String> profileMap, JSONObject accountJSON) {
        // Create maps of profileId to profileName and profileId to accountIds for individual accounts.
        String accountId = (String) accountJSON.get(CDSConsentExtensionConstants.ACCOUNT_ID);

        if (!profileMap.containsKey(CDSConsentExtensionConstants.INDIVIDUAL_PROFILE_ID)) {
            profileMap.put(CDSConsentExtensionConstants.INDIVIDUAL_PROFILE_ID,
                    CDSConsentExtensionConstants.INDIVIDUAL_PROFILE_TYPE);
        }
        if (profileIdAccountsMap.containsKey(CDSConsentExtensionConstants.INDIVIDUAL_PROFILE_ID)) {
            profileIdAccountsMap.get(CDSConsentExtensionConstants.INDIVIDUAL_PROFILE_ID).add(accountId);
        } else {
            profileIdAccountsMap.put(CDSConsentExtensionConstants.INDIVIDUAL_PROFILE_ID,
                    new ArrayList<>(Collections.singletonList(accountId)));
        }
        return profileIdAccountsMap;
    }

}
