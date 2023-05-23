/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.handler;

import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataService;
import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataServiceImpl;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.common.identity.retriever.sp.CommonServiceProviderRetriever;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.AuthorizationResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentMappingResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.DetailedConsentResource;
import com.wso2.openbanking.accelerator.consent.mgt.service.ConsentCoreService;
import com.wso2.openbanking.accelerator.consent.mgt.service.impl.ConsentCoreServiceImpl;
import com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.models.
        UsersAccountsLegalEntitiesResource;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Ceasing Secondary User - Handler
 */
public class CeasingSecondaryUserHandler {

    private final String metadataKey = "BLOCKED_LEGAL_ENTITIES";
    private static final Log log = LogFactory.getLog(com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.
            secondary.user.handler.CeasingSecondaryUserHandler.class);
    AccountMetadataService accountMetadataService = AccountMetadataServiceImpl.getInstance();

    /**
     * ----- Block the sharing status for a legal entity -----
     * This endpoint is designed to allow an account holder to block a legal entity in order to cease the
     * disclosure initiated by a secondary user for a particular account to that legal entity.
     *
     * @param JSONArray - ValidatedRequestBody
     * @return void
     * @throws OpenBankingException
     */
    public void blockLegalEntitySharingStatus(JSONArray validatedRequestBody) {

        log.debug("Blocking the legal entity sharing status");

        try {
            for (Object validatedRequestBodyItem : validatedRequestBody) {
                String accountId = ((JSONObject) validatedRequestBodyItem).getAsString("accountId");
                String secondaryUserId = ((JSONObject) validatedRequestBodyItem).getAsString("secondaryUserId");
                String legalEntityId = ((JSONObject) validatedRequestBodyItem).getAsString("legalEntityId");

                // Generating the hashmap
                HashMap<String, String> blockedLegalEntityMap = new HashMap<String, String>();

                // Checking the existence of a record in the OB_ACCOUNT_METADATA table for the corresponding
                // to a particular accountId, secondaryUserId and metadataKey
                String responseLegalEntities = accountMetadataService.getAccountMetadataByKey
                        (accountId, secondaryUserId, metadataKey);

                if (responseLegalEntities == null) {
                    // Legal entities does not exist for corresponding accountId and secondaryUserId
                    blockedLegalEntityMap.put(metadataKey, legalEntityId);
                    accountMetadataService.
                            addOrUpdateAccountMetadata(accountId, secondaryUserId, blockedLegalEntityMap);
                    log.info("Legal Entity: " + legalEntityId + ", has been successfully blocked!");
                } else {
                    // Legal entities exist for corresponding accountId and secondaryUserId
                    StringBuilder legalEntitiesMetaDataValue = new StringBuilder(responseLegalEntities);
                    log.info(legalEntitiesMetaDataValue);

                    // Appending legalEntityId to the existing legalEntityIds for corresponding accountId and
                    // secondaryUserId
                    if (legalEntitiesMetaDataValue.indexOf(legalEntityId) != -1) {
                        log.info("Legal Entity: " + legalEntityId + ", has been already blocked!");
                    } else {
                        legalEntitiesMetaDataValue.append(",");
                        legalEntitiesMetaDataValue.append(legalEntityId);
                        blockedLegalEntityMap.put(metadataKey, legalEntitiesMetaDataValue.toString());
                        accountMetadataService.addOrUpdateAccountMetadata
                                (accountId, secondaryUserId, blockedLegalEntityMap);
                        log.info("Legal Entity: " + legalEntityId + ", has been successfully blocked!");
                    }
                }
            }

        } catch (OpenBankingException e) {
            log.warn(e.getMessage());
        }

    }

    /**
     * ----- Unblock the sharing status for a legal entity -----
     * This endpoint is designed to allow an account holder to block a legal entity in order to cease the
     * disclosure initiated by a secondary user for a particular account to that legal entity.
     *
     * @param JSONArray - ValidatedRequestBody
     * @return void
     * @throws OpenBankingException
     */
    public void unblockLegalEntitySharingStatus(JSONArray validatedRequestBody) {

        log.debug("Unblocking the legal entity sharing status");

        try {
            for (Object validatedRequestBodyItem : validatedRequestBody) {
                String accountId = ((JSONObject) validatedRequestBodyItem).getAsString("accountId");
                String secondaryUserId = ((JSONObject) validatedRequestBodyItem).getAsString("secondaryUserId");
                String legalEntityId = ((JSONObject) validatedRequestBodyItem).getAsString("legalEntityId");

                // Checking the existence of a record in the OB_ACCOUNT_METADATA table for the corresponding
                // to a particular accountId, secondaryUserId and metadataKey
                String responseLegalEntities = accountMetadataService.getAccountMetadataByKey
                        (accountId, secondaryUserId, metadataKey);

                if (responseLegalEntities == null) {
                    //Legal entities does not exist for corresponding accountId and secondaryUserId
                    log.info("Legal Entity : " + legalEntityId + ", has not been blocked!");
                } else {
                    // Legal entities exist for corresponding accountId and secondaryUserId
                    StringBuilder legalEntitiesMetaDataValue = new StringBuilder(responseLegalEntities);

                    // Removing legalEntityId to the existing legalEntityIds for corresponding accountId and
                    // secondaryUserId
                    if (legalEntitiesMetaDataValue.indexOf(legalEntityId) == -1) {
                        log.info("Legal Entity : " + legalEntityId + ", has not been blocked!");
                    } else {
                        String[] blockedLegalEntities = responseLegalEntities.split(",");
                        String[] newBlockedLegalEntities = new String[blockedLegalEntities.length - 1];

                        for (int i = 0, j = 0; i < blockedLegalEntities.length; i++) {
                            if (blockedLegalEntities[i].equals(legalEntityId)) {
                                continue;
                            }
                            newBlockedLegalEntities[j++] = blockedLegalEntities[i];
                        }

                        HashMap<String, String> newBlockedLegalEntityMap = new HashMap<String, String>();
                        StringBuilder newBlockedLegalEntitiesSB = new StringBuilder();

                        for (int i = 0; i < newBlockedLegalEntities.length; i++) {
                            newBlockedLegalEntitiesSB.append(newBlockedLegalEntities[i]);
                            if (i != newBlockedLegalEntities.length - 1) {
                                newBlockedLegalEntitiesSB.append(",");
                            }
                        }
                        newBlockedLegalEntityMap.put(metadataKey, newBlockedLegalEntitiesSB.toString());
                        accountMetadataService.addOrUpdateAccountMetadata
                                (accountId, secondaryUserId, newBlockedLegalEntityMap);

                        if (accountMetadataService.getAccountMetadataByKey
                                (accountId, secondaryUserId, metadataKey).isEmpty()) {
                            accountMetadataService.removeAccountMetadataByKey(accountId, secondaryUserId, metadataKey);
                        }
                        log.info("Legal Entity : " + legalEntityId + ", has been unblocked!");
                    }
                }
            }
        } catch (OpenBankingException e) {
            log.warn(e.getMessage());
        }
    }


    /**
     * ----- Get accounts, secondary users, legal entities and their sharing status -----
     * This endpoint is designed to get all accounts, secondary users, legal entities and their sharing status
     * bound to the account holder in the consent manager dashboard.
     *
     * @param String - UserId
     * @return UsersAccountsLegalEntitiesResource
     * @throws RuntimeException
     */
    public UsersAccountsLegalEntitiesResource getUsersAccountsLegalEntities(String userId) {

        log.debug("Getting accounts, secondary users, legal entities and their sharing status.");

        try {
            ConsentCoreService consentCoreService = new ConsentCoreServiceImpl();
            CommonServiceProviderRetriever commonServiceProviderRetriever = new CommonServiceProviderRetriever();

            // Creating an array list to append the userId
            ArrayList<String> userIdAL = new ArrayList<>();
            userIdAL.add(userId);

            UsersAccountsLegalEntitiesResource responseUsersAccountsLegalEntities =
                    new UsersAccountsLegalEntitiesResource(userId);

            ArrayList<DetailedConsentResource> responseDetailedConsents = consentCoreService.searchDetailedConsents
                    (null, null, null, null, userIdAL, null, null,
                            null, null, false);


            // Updating - Secondary Users
            // Consent
            for (DetailedConsentResource detailedConsent : responseDetailedConsents) {

                // Authorization Resource
                for (AuthorizationResource authorizationResource : detailedConsent.getAuthorizationResources()) {

                    if (authorizationResource.getAuthorizationType().equals("primary_member")) {

                        UsersAccountsLegalEntitiesResource.SecondaryUser uniqueSecondaryUser = new
                                UsersAccountsLegalEntitiesResource.
                                        SecondaryUser(authorizationResource.getUserID(), null);

                        if (responseUsersAccountsLegalEntities.getSecondaryUsers() == null) {
                            responseUsersAccountsLegalEntities.addSecondaryUser(uniqueSecondaryUser);
                        } else {
                            for (UsersAccountsLegalEntitiesResource.SecondaryUser secondaryUser :
                                    responseUsersAccountsLegalEntities.getSecondaryUsers()) {
                                if (!secondaryUser.getSecondaryUserId().
                                        equals(uniqueSecondaryUser.getSecondaryUserId())) {
                                    responseUsersAccountsLegalEntities.addSecondaryUser(uniqueSecondaryUser);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            // Updating - Accounts
            for (UsersAccountsLegalEntitiesResource.SecondaryUser secondaryUser :
                    responseUsersAccountsLegalEntities.getSecondaryUsers()) {

                // Consent
                for (DetailedConsentResource detailedConsent : responseDetailedConsents) {

                    // Authorization Resource
                    for (AuthorizationResource authorizationResource : detailedConsent.getAuthorizationResources()) {
                        String authorizationId = authorizationResource.getAuthorizationID();
                        // Consent Mapping Resource
                        for (ConsentMappingResource consentMappingResource : detailedConsent.
                                getConsentMappingResources()) {

                            if (consentMappingResource.getAuthorizationID().equals(authorizationId) &&
                                    authorizationResource.getUserID().equals(secondaryUser.getSecondaryUserId())) {

                                UsersAccountsLegalEntitiesResource.Account uniqueAccount =
                                        new UsersAccountsLegalEntitiesResource.
                                                Account(consentMappingResource.getAccountID(), null);

                                if (secondaryUser.getAccounts() == null) {
                                    secondaryUser.addAccount(uniqueAccount);
                                } else {
                                    for (UsersAccountsLegalEntitiesResource.Account account :
                                            secondaryUser.getAccounts()) {
                                        if (!account.getAccountId().equals(uniqueAccount.getAccountId())) {
                                            secondaryUser.addAccount(uniqueAccount);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            /* Updating - Legal Entities */
            for (UsersAccountsLegalEntitiesResource.SecondaryUser secondaryUser :
                    responseUsersAccountsLegalEntities.getSecondaryUsers()) {

                for (UsersAccountsLegalEntitiesResource.Account account : secondaryUser.getAccounts()) {
                    String clientId;
                    String secondaryUserId = secondaryUser.getSecondaryUserId();
                    String accountId = account.getAccountId();
                    String legalEntityId = null;
                    String legalEntitySharingStatus = null;

                    // Consent
                    for (DetailedConsentResource detailedConsent : responseDetailedConsents) {

                        // Authorization Resource
                        for (AuthorizationResource authorizationResource :
                                detailedConsent.getAuthorizationResources()) {

                            // Consent Mapping Resource
                            for (ConsentMappingResource consentMappingResource :
                                    detailedConsent.getConsentMappingResources()) {

                                if (authorizationResource.getUserID().equals(secondaryUserId) &&
                                        consentMappingResource.getAccountID().equals(accountId)) {
                                    clientId = detailedConsent.getClientID();

                                    legalEntityId = commonServiceProviderRetriever.
                                            getAppPropertyFromSPMetaData(clientId, "legal_entity_id");


                                    String responseLegalEntities = accountMetadataService.
                                            getAccountMetadataByKey
                                                    (accountId, secondaryUserId, "BLOCKED_LEGAL_ENTITIES");

                                    if (responseLegalEntities != null) {
                                        String[] blockedLegalEntities = responseLegalEntities.split(",");

                                        for (String blockedLegalEntity : blockedLegalEntities) {
                                            if (legalEntityId.equals(blockedLegalEntity)) {
                                                legalEntitySharingStatus = "blocked";
                                                break;
                                            } else {
                                                legalEntitySharingStatus = "active";
                                            }
                                        }
                                    } else {
                                        legalEntitySharingStatus = "active";
                                    }

                                    UsersAccountsLegalEntitiesResource.LegalEntity uniqueLegalEntity =
                                            new UsersAccountsLegalEntitiesResource.
                                                    LegalEntity(legalEntityId, legalEntitySharingStatus);

                                    if (account.getLegalEntities() == null) {
                                        account.addLegalEntity(uniqueLegalEntity);
                                    } else {
                                        for (UsersAccountsLegalEntitiesResource.LegalEntity legalEntity :
                                                account.getLegalEntities()) {
                                            if (!legalEntity.getLegalEntityId().
                                                    equals(uniqueLegalEntity.getLegalEntityId())) {
                                                account.addLegalEntity(uniqueLegalEntity);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
            return responseUsersAccountsLegalEntities;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }
}
