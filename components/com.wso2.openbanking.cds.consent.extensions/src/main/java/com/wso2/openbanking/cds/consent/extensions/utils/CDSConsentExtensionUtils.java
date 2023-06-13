/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.consent.extensions.utils;

import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataService;
import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataServiceImpl;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.common.identity.retriever.sp.CommonServiceProviderRetriever;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import com.wso2.openbanking.cds.consent.extensions.validate.CDSConsentValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility Class for CDS Consent Extensions
 */
public class CDSConsentExtensionUtils {
    private static final Log log = LogFactory.getLog(CDSConsentValidator.class);

    AccountMetadataService accountMetadataService = AccountMetadataServiceImpl.getInstance();

    /**
     * Method to retrieve the sharing status of a legal entity for an accountID and secondaryUserID and legalEntityID
     *
     * @param accountID
     * @param userID
     * @param clientID
     * @return true/false based on the sharing status of a legal entity for an accountID and secondaryUserID and
     * legalEntityID
     */
    public boolean isLegalEntityBlockedForAccountAndUser(String accountID, String userID, String clientID)
            throws OpenBankingException {

        try {
            CommonServiceProviderRetriever commonServiceProviderRetriever = new CommonServiceProviderRetriever();

            String legalEntityID = commonServiceProviderRetriever.
                    getAppPropertyFromSPMetaData(clientID,
                            CDSConsentExtensionConstants.LEGAL_ENTITY_ID);
            String blockedLegalEntities = accountMetadataService.getAccountMetadataByKey
                    (accountID, userID, CDSConsentExtensionConstants.METADATA_KEY_BLOCKED_LEGAL_ENTITIES);

            if (blockedLegalEntities != null) {
                String[] blockedLegalEntityArray = blockedLegalEntities.split(",");
                boolean isLegalEntitySharingStatusBlocked = false;
                for (String blockedLegalEntity : blockedLegalEntityArray) {
                    if (legalEntityID.equals(blockedLegalEntity)) {
                        isLegalEntitySharingStatusBlocked = true;
                        break;
                    }
                }
                return isLegalEntitySharingStatusBlocked;
            } else {
                return false;
            }
        } catch (OpenBankingException e) {
            log.error("Error occurred while retrieving account metadata");
            throw new OpenBankingException("Error occurred while retrieving account metadata");
        }
    }
}
