/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.consent.extensions.util;

import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataService;
import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataServiceImpl;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import com.wso2.openbanking.cds.consent.extensions.validate.utils.CDSConsentValidatorUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * Util class for CDSConsentExtensions.
 */
public class CDSConsentExtensionsUtil {

    private static final Log log = LogFactory.getLog(CDSConsentValidatorUtil.class);
    private static AccountMetadataService accountMetadataService = AccountMetadataServiceImpl.getInstance();

    /**
     * Get secondary user instruction data
     * User is eligible for data sharing from the secondary account
     * only if secondary user instruction is in active state
     *
     * @param accountId
     * @param userId
     * @throws ConsentException
     */
    public static Boolean isUserEligibleForSecondaryAccountDataSharing(String accountId, String userId)
            throws ConsentException {

        try {
            Map<String, String> accountMetadata = accountMetadataService.getAccountMetadataMap(accountId, userId);
            if (!accountMetadata.isEmpty()) {
                return CDSConsentExtensionConstants.ACTIVE_STATUS
                        .equalsIgnoreCase(accountMetadata.get(CDSConsentExtensionConstants.INSTRUCTION_STATUS));
            } else {
                return false;
            }
        } catch (OpenBankingException e) {
            log.error("Error occurred while retrieving account metadata for account id : " + accountId, e);
            throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public static boolean isDOMSStatusEligibleForDataSharing(String accountID) throws OpenBankingException {

        Map<String, String> accountMetadata = accountMetadataService.getAccountMetadataMap(accountID);

        if (!accountMetadata.isEmpty()) {
            String status = accountMetadata.get(CDSConsentExtensionConstants.DOMS_STATUS);
            return status.equals(CDSConsentExtensionConstants.DOMS_STATUS_PRE_APPROVAL);
        } else {
            return true;
        }
    }
}
