/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.sharing.handler;

import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataService;
import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataServiceImpl;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.common.identity.retriever.sp.CommonServiceProviderRetriever;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.AuthorizationResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentMappingResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.DetailedConsentResource;
import com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.sharing.models.
        LegalEntityItemDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.sharing.models.
        LegalEntityListDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.sharing.models.UsersAccountsLegalEntitiesDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Ceasing Secondary User - Handler
 */
public class CeasingSecondaryUserHandler {

    private final String metadataKey = "BLOCKED_LEGAL_ENTITIES";
    private static final Log log = LogFactory.getLog(CeasingSecondaryUserHandler.class);
    AccountMetadataService accountMetadataService = AccountMetadataServiceImpl.getInstance();

    /**
     * ----- Block the sharing status for a legal entity -----
     * This endpoint is designed to allow an account holder to block a legal entity in order to cease the
     * disclosure initiated by a secondary user for a particular account to that legal entity.
     */
    public void blockLegalEntitySharingStatus(LegalEntityListDTO legalEntityListDTO) {

        log.debug("Blocking the legal entity sharing status");

        try {
            for (LegalEntityItemDTO legalEntityListItemDTO : legalEntityListDTO.getData()) {

                // Extracting fields in the legalEntityListItemDTO
                String accountID = legalEntityListItemDTO.getAccountID();
                String secondaryUserID = legalEntityListItemDTO.getSecondaryUserID();
                String legalEntityID = legalEntityListItemDTO.getLegalEntityID();

                // Generating the hashmap
                HashMap<String, String> blockedLegalEntityMap = new HashMap<String, String>();

                // Checking the existence of a record in the OB_ACCOUNT_METADATA table for the corresponding
                // to a particular accountID, secondaryUserID and metadataKey
                String responseLegalEntities = accountMetadataService.getAccountMetadataByKey
                        (accountID, secondaryUserID, metadataKey);

                if (responseLegalEntities == null) {
                    // Legal entities does not exist for corresponding accountID and secondaryUserID
                    blockedLegalEntityMap.put(metadataKey, legalEntityID);
                    accountMetadataService.
                            addOrUpdateAccountMetadata(accountID, secondaryUserID, blockedLegalEntityMap);
                    log.info("Legal Entity: " + legalEntityID + ", has been successfully blocked!");
                } else {
                    // Legal entities exist for corresponding accountID and secondaryUserID
                    StringBuilder legalEntitiesMetaDataValue = new StringBuilder(responseLegalEntities);
                    log.info(legalEntitiesMetaDataValue);

                    // Appending legalEntityID to the existing legalEntityIDs for corresponding accountID and
                    // secondaryUserID
                    if (legalEntitiesMetaDataValue.indexOf(legalEntityID) != -1) {
                        log.info("Legal Entity: " + legalEntityID + ", has been already blocked!");
                    } else {
                        legalEntitiesMetaDataValue.append(",");
                        legalEntitiesMetaDataValue.append(legalEntityID);
                        blockedLegalEntityMap.put(metadataKey, legalEntitiesMetaDataValue.toString());
                        accountMetadataService.addOrUpdateAccountMetadata
                                (accountID, secondaryUserID, blockedLegalEntityMap);
                        log.info("Legal Entity: " + legalEntityID + ", has been successfully blocked!");
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
     */
    public void unblockLegalEntitySharingStatus(LegalEntityListDTO legalEntityListDTO) {

        log.debug("Unblocking the legal entity sharing status");

        try {
            for (LegalEntityItemDTO legalEntityListItemDTO : legalEntityListDTO.getData()) {

                // Extracting fields in the legalEntityListItemDTO
                String accountID = legalEntityListItemDTO.getAccountID();
                String secondaryUserID = legalEntityListItemDTO.getSecondaryUserID();
                String legalEntityID = legalEntityListItemDTO.getLegalEntityID();

                // Checking the existence of a record in the OB_ACCOUNT_METADATA table for the corresponding
                // to a particular accountID, secondaryUserID and metadataKey
                String responseLegalEntities = accountMetadataService.getAccountMetadataByKey
                        (accountID, secondaryUserID, metadataKey);

                if (responseLegalEntities == null) {
                    //Legal entities does not exist for corresponding accountID and secondaryUserID
                    log.info("Legal Entity : " + legalEntityID + ", has not been blocked!");
                } else {
                    // Legal entities exist for corresponding accountID and secondaryUserID
                    StringBuilder legalEntitiesMetaDataValue = new StringBuilder(responseLegalEntities);

                    // Removing legalEntityID to the existing legalEntityIDs for corresponding accountID and
                    // secondaryUserID
                    if (legalEntitiesMetaDataValue.indexOf(legalEntityID) == -1) {
                        log.info("Legal Entity : " + legalEntityID + ", has not been blocked!");
                    } else {
                        String[] blockedLegalEntities = responseLegalEntities.split(",");
                        String[] newBlockedLegalEntities = new String[blockedLegalEntities.length - 1];

                        for (int i = 0, j = 0; i < blockedLegalEntities.length; i++) {
                            if (blockedLegalEntities[i].equals(legalEntityID)) {
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
                                (accountID, secondaryUserID, newBlockedLegalEntityMap);

                        if (accountMetadataService.getAccountMetadataByKey
                                (accountID, secondaryUserID, metadataKey).isEmpty()) {
                            accountMetadataService.removeAccountMetadataByKey(accountID, secondaryUserID, metadataKey);
                        }
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
     */
    public UsersAccountsLegalEntitiesDTO getUsersAccountsLegalEntities
    (ArrayList<DetailedConsentResource> responseDetailedConsents,
     UsersAccountsLegalEntitiesDTO responseUsersAccountsLegalEntitiesDTO) {

        log.debug("Getting accounts, secondary users, legal entities and their sharing status.");


        try {
            CommonServiceProviderRetriever commonServiceProviderRetriever = new CommonServiceProviderRetriever();

            // Updating - Secondary Users
            // Consent
            for (DetailedConsentResource detailedConsent : responseDetailedConsents) {

                // Authorization Resource
                for (AuthorizationResource authorizationResource : detailedConsent.getAuthorizationResources()) {

                    if (authorizationResource.getAuthorizationType().equals("primary_member")) {

                        UsersAccountsLegalEntitiesDTO.SecondaryUser uniqueSecondaryUser = new
                                UsersAccountsLegalEntitiesDTO.
                                        SecondaryUser(authorizationResource.getUserID(), null);

                        if (responseUsersAccountsLegalEntitiesDTO.getSecondaryUsers() == null) {
                            responseUsersAccountsLegalEntitiesDTO.addSecondaryUser(uniqueSecondaryUser);
                        } else {
                            for (UsersAccountsLegalEntitiesDTO.SecondaryUser secondaryUser :
                                    responseUsersAccountsLegalEntitiesDTO.getSecondaryUsers()) {
                                if (!secondaryUser.getSecondaryUserID().
                                        equals(uniqueSecondaryUser.getSecondaryUserID())) {
                                    responseUsersAccountsLegalEntitiesDTO.addSecondaryUser(uniqueSecondaryUser);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            // Updating - Accounts
            for (UsersAccountsLegalEntitiesDTO.SecondaryUser secondaryUser :
                    responseUsersAccountsLegalEntitiesDTO.getSecondaryUsers()) {

                // Consent
                for (DetailedConsentResource detailedConsent : responseDetailedConsents) {

                    // Authorization Resource
                    for (AuthorizationResource authorizationResource :
                            detailedConsent.getAuthorizationResources()) {
                        String authorizationID = authorizationResource.getAuthorizationID();
                        // Consent Mapping Resource
                        for (ConsentMappingResource consentMappingResource : detailedConsent.
                                getConsentMappingResources()) {

                            if (consentMappingResource.getAuthorizationID().equals(authorizationID) &&
                                    authorizationResource.getUserID().equals(secondaryUser.getSecondaryUserID())) {

                                UsersAccountsLegalEntitiesDTO.Account uniqueAccount =
                                        new UsersAccountsLegalEntitiesDTO.
                                                Account(consentMappingResource.getAccountID(), null);

                                if (secondaryUser.getAccounts() == null) {
                                    secondaryUser.addAccount(uniqueAccount);
                                } else {
                                    for (UsersAccountsLegalEntitiesDTO.Account account :
                                            secondaryUser.getAccounts()) {
                                        if (!account.getAccountID().equals(uniqueAccount.getAccountID())) {
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
            for (UsersAccountsLegalEntitiesDTO.SecondaryUser secondaryUser :
                    responseUsersAccountsLegalEntitiesDTO.getSecondaryUsers()) {

                for (UsersAccountsLegalEntitiesDTO.Account account : secondaryUser.getAccounts()) {
                    String clientID;
                    String secondaryUserID = secondaryUser.getSecondaryUserID();
                    String accountID = account.getAccountID();
                    String legalEntityID = null;
                    String legalEntitySharingStatus = null;

                    // Consent
                    for (DetailedConsentResource detailedConsent : responseDetailedConsents) {

                        // Authorization Resource
                        for (AuthorizationResource authorizationResource :
                                detailedConsent.getAuthorizationResources()) {

                            // Consent Mapping Resource
                            for (ConsentMappingResource consentMappingResource :
                                    detailedConsent.getConsentMappingResources()) {

                                if (authorizationResource.getUserID().equals(secondaryUserID) &&
                                        consentMappingResource.getAccountID().equals(accountID)) {
                                    clientID = detailedConsent.getClientID();

                                    legalEntityID = commonServiceProviderRetriever.
                                            getAppPropertyFromSPMetaData(clientID, "legal_entity_id");


                                    String responseLegalEntities = accountMetadataService.
                                            getAccountMetadataByKey
                                                    (accountID, secondaryUserID, "BLOCKED_LEGAL_ENTITIES");

                                    if (responseLegalEntities != null) {
                                        String[] blockedLegalEntities = responseLegalEntities.split(",");

                                        for (String blockedLegalEntity : blockedLegalEntities) {
                                            if (legalEntityID.equals(blockedLegalEntity)) {
                                                legalEntitySharingStatus = "blocked";
                                                break;
                                            } else {
                                                legalEntitySharingStatus = "active";
                                            }
                                        }
                                    } else {
                                        legalEntitySharingStatus = "active";
                                    }

                                    UsersAccountsLegalEntitiesDTO.LegalEntity uniqueLegalEntity =
                                            new UsersAccountsLegalEntitiesDTO.
                                                    LegalEntity(legalEntityID, legalEntitySharingStatus);

                                    if (account.getLegalEntities() == null) {
                                        account.addLegalEntity(uniqueLegalEntity);
                                    } else {
                                        for (UsersAccountsLegalEntitiesDTO.LegalEntity legalEntity :
                                                account.getLegalEntities()) {
                                            if (!legalEntity.getLegalEntityID().
                                                    equals(uniqueLegalEntity.getLegalEntityID())) {
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
            return responseUsersAccountsLegalEntitiesDTO;

        } catch (RuntimeException e) {
            throw e;
        } catch (OpenBankingException e) {
            log.warn(e.getMessage());
            return null;
        }
    }
}
