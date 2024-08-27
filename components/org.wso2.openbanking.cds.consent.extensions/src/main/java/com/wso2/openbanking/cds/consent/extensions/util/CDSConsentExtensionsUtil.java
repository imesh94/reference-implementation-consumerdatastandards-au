/**
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.openbanking.cds.consent.extensions.util;

import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataService;
import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataServiceImpl;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.common.identity.retriever.sp.CommonServiceProviderRetriever;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import org.wso2.openbanking.cds.consent.extensions.validate.utils.CDSConsentValidatorUtil;

import java.util.Map;

/**
 * Util class for CDSConsentExtensions.
 */
public class CDSConsentExtensionsUtil {

    private static final Log log = LogFactory.getLog(CDSConsentValidatorUtil.class);
    private static AccountMetadataService accountMetadataService = AccountMetadataServiceImpl.getInstance();

    /**
     * Get secondary user instruction data.
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

    /**
     * Method to retrieve the sharing status of a legal entity for the given accountID, secondaryUserID and.
     * legalEntityID
     *
     * @param accountID
     * @param userID
     * @param clientID
     * @return true/false based on the sharing status of a legal entity
     */
    public static boolean isLegalEntityBlockedForAccountAndUser(String accountID, String userID, String clientID)
            throws ConsentException {

        try {
            CommonServiceProviderRetriever commonServiceProviderRetriever = new CommonServiceProviderRetriever();

            String legalEntityID = commonServiceProviderRetriever.
                    getAppPropertyFromSPMetaData(clientID,
                            CDSConsentExtensionConstants.LEGAL_ENTITY_ID);
            String blockedLegalEntities = accountMetadataService.getAccountMetadataByKey
                    (accountID, userID, CDSConsentExtensionConstants.METADATA_KEY_BLOCKED_LEGAL_ENTITIES);

            if (blockedLegalEntities != null) {
                String[] blockedLegalEntityArray = blockedLegalEntities.split(",");
                for (String blockedLegalEntity : blockedLegalEntityArray) {
                    if (legalEntityID.equals(blockedLegalEntity)) {
                        return true;
                    }
                }
                return false;
            } else {
                return false;
            }
        } catch (OpenBankingException e) {
            log.error("Error occurred while retrieving account metadata");
            throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                    "Error occurred while retrieving account metadata");
        }
    }
}
