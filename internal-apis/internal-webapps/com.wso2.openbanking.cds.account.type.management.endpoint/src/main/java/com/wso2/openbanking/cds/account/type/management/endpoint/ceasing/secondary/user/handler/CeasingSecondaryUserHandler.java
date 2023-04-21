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

package com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.handler;

import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataService;
import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataServiceImpl;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.AuthorizationResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.DetailedConsentResource;
import com.wso2.openbanking.accelerator.consent.mgt.service.ConsentCoreService;
import com.wso2.openbanking.accelerator.consent.mgt.service.impl.ConsentCoreServiceImpl;
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
     * An endpoint should be designed to allow an account holder to block a legal entity in order to cease the
     * disclosure initiated by a secondary user for a particular account to that legal entity.
     */
    public void blockLegalEntitySharingStatus(JSONArray validatedRequestBody) {

        log.debug("Blocking the legal entity sharing status");

        try {
            //TODO: Handle the existing consents upon blocking a legal entity
            for (Object validatedRequestBodyItem : validatedRequestBody) {
                String accountId = ((JSONObject) validatedRequestBodyItem).getAsString("accountId");
                String secondaryUserId = ((JSONObject) validatedRequestBodyItem).getAsString("secondaryUserId");
                String legalEntityId = ((JSONObject) validatedRequestBodyItem).getAsString("legalEntityId");

                //Generating the hashmap
                HashMap<String, String> blockedLegalEntityMap = new HashMap<String, String>();

                //Checking the existence of a record in the OB_ACCOUNT_METADATA table for the corresponding
                //to a particular accountId, secondaryUserId and metadataKey
                String responseLegalEntities = accountMetadataService.getAccountMetadataByKey
                        (accountId, secondaryUserId, metadataKey);

                if (responseLegalEntities == null) {
                    /* --- Legal entities does not exist for corresponding accountId and secondaryUserId --- */
                    blockedLegalEntityMap.put(metadataKey, legalEntityId);
                    accountMetadataService.
                            addOrUpdateAccountMetadata(accountId, secondaryUserId, blockedLegalEntityMap);
                    log.info("Legal Entity: " + legalEntityId + ", has been successfully blocked!");
                } else {
                    /* --- Legal entities exist for corresponding accountId and secondaryUserId --- */
                    StringBuilder legalEntitiesMetaDataValue = new StringBuilder(responseLegalEntities);
                    log.info(legalEntitiesMetaDataValue);

                /* --- Appending legalEntityId to the existing legalEntityIds for corresponding accountId and
                       secondaryUserId --- */
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
     * An endpoint should be designed to allow an account holder to block a legal entity in order to cease the
     * disclosure initiated by a secondary user for a particular account to that legal entity.
     */
    public void unblockLegalEntitySharingStatus(JSONArray validatedRequestBody) {

        log.debug("Unblocking the legal entity sharing status");

        try {
            for (Object validatedRequestBodyItem : validatedRequestBody) {
                String accountId = ((JSONObject) validatedRequestBodyItem).getAsString("accountId");
                String secondaryUserId = ((JSONObject) validatedRequestBodyItem).getAsString("secondaryUserId");
                String legalEntityId = ((JSONObject) validatedRequestBodyItem).getAsString("legalEntityId");

                //Checking the existence of a record in the OB_ACCOUNT_METADATA table for the corresponding
                //to a particular accountId, secondaryUserId and metadataKey
                String responseLegalEntities = accountMetadataService.getAccountMetadataByKey
                        (accountId, secondaryUserId, metadataKey);

                if (responseLegalEntities == null) {
                    /* --- Legal entities does not exist for corresponding accountId and secondaryUserId --- */
                    log.info("Legal Entity : " + legalEntityId + ", has not been blocked!");
                } else {
                    /* --- Legal entities exist for corresponding accountId and secondaryUserId --- */
                    StringBuilder legalEntitiesMetaDataValue = new StringBuilder(responseLegalEntities);

                /* --- Removing legalEntityId to the existing legalEntityIds for corresponding accountId and
                       secondaryUserId --- */
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
     * An endpoint should be designed to get all accounts, secondary users, legal entities and their sharing status
     * bound to the account holder in the consent manager dashboard.
     */
    public void getAccountsUsersLegalEntities(String userId) {

        log.debug("Getting accounts, secondary users, legal entities and their sharing status.");

        try {
            ConsentCoreService consentCoreService = new ConsentCoreServiceImpl();

            // Creating an array list to append the userId
            ArrayList<String> userIdAL = new ArrayList<>();
            userIdAL.add(userId);

            ArrayList<DetailedConsentResource> responseDetailedConsents = consentCoreService.searchDetailedConsents
                    (null, null, null, null, userIdAL, null, null,
                            null, null, false);


            log.info(responseDetailedConsents);

//            for (DetailedConsentResource detailedConsent : responseDetailedConsents) {
//                log.info("\n\n");
//                log.info(detailedConsent.getAuthorizationResources());
//
//                for(AuthorizationResource ar:detailedConsent.getAuthorizationResources()){
//                    log.info(ar.getUserID());
//                    log.info("\n\n");
//
//                }
//            }


            log.info("----- PAUSE -----");
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}
