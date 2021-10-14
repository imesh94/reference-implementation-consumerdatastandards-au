/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.common.metadata.status.validator.service;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.common.metadata.periodical.updater.MetadataHolder;
import com.wso2.openbanking.cds.common.metadata.periodical.updater.constants.MetadataConstants;
import com.wso2.openbanking.cds.common.metadata.periodical.updater.utils.DataRecipientStatusEnum;
import com.wso2.openbanking.cds.common.metadata.periodical.updater.utils.SoftwareProductStatusEnum;
import com.wso2.openbanking.cds.common.metadata.status.validator.cache.MetadataCache;
import com.wso2.openbanking.cds.common.metadata.status.validator.cache.MetadataCacheKey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * The validation service to check DR and SP statuses.
 */
public class MetadataService {

    private static final Log LOG = LogFactory.getLog(MetadataService.class);

    private MetadataService() {}

    /**
     * To check disclose CDR data DataHolderResponsibility
     *
     * @param clientId client id
     * @return true when conditions are valid
     * @see <a href="https://cdr-register.github.io/register/#data-holder-responsibilities">
     *     Data Holder Responsibilities</a>
     */
    public static boolean shouldDiscloseCDRData(String clientId) {
        final String dataRecipientStatus = getDataRecipientStatus(clientId);
        final String softwareProductStatus = getSoftwareProductStatus(clientId);

        final boolean result = DataRecipientStatusEnum.ACTIVE.toString().equalsIgnoreCase(dataRecipientStatus) &&
                SoftwareProductStatusEnum.ACTIVE.toString().equalsIgnoreCase(softwareProductStatus);

        if (!result && LOG.isDebugEnabled()) {
            LOG.debug(String.format("Cannot disclose CDR data. DataRecipientStatus: %s, SoftwareProductStatus: %s",
                    dataRecipientStatus, softwareProductStatus));
        }
        return result;
    }

    /**
     * To check facilitate consent authorisation DataHolderResponsibility
     *
     * @param clientId client id
     * @return true when conditions are valid
     * @see <a href="https://cdr-register.github.io/register/#data-holder-responsibilities">
     *     Data Holder Responsibilities</a>
     */
    public static boolean shouldFacilitateConsentAuthorisation(String clientId) {
        final String dataRecipientStatus = getDataRecipientStatus(clientId);
        final String softwareProductStatus = getSoftwareProductStatus(clientId);

        final boolean result = DataRecipientStatusEnum.ACTIVE.toString().equalsIgnoreCase(dataRecipientStatus) &&
                SoftwareProductStatusEnum.ACTIVE.toString().equalsIgnoreCase(softwareProductStatus);

        if (!result && LOG.isDebugEnabled()) {
            LOG.debug(String.format("Cannot facilitate consent authorisation. DataRecipientStatus: %s, " +
                            "SoftwareProductStatus: %s", dataRecipientStatus, softwareProductStatus));
        }
        return result;
    }

    /**
     * To check facilitate consent withdrawal DataHolderResponsibility
     *
     * @param clientId client id
     * @return true when conditions are valid
     * @see <a href="https://cdr-register.github.io/register/#data-holder-responsibilities">
     *     Data Holder Responsibilities</a>
     */
    public static boolean shouldFacilitateConsentWithdrawal(String clientId) {
        final String dataRecipientStatus = getDataRecipientStatus(clientId);
        final String softwareProductStatus = getSoftwareProductStatus(clientId);

        final boolean condition1 = DataRecipientStatusEnum.ACTIVE.toString().equalsIgnoreCase(dataRecipientStatus) &&
                SoftwareProductStatusEnum.ACTIVE.toString().equalsIgnoreCase(softwareProductStatus);
        final boolean condition2 = DataRecipientStatusEnum.ACTIVE.toString().equalsIgnoreCase(dataRecipientStatus) &&
                SoftwareProductStatusEnum.INACTIVE.toString().equalsIgnoreCase(softwareProductStatus);
        final boolean condition3 = DataRecipientStatusEnum.SUSPENDED.toString().equalsIgnoreCase(dataRecipientStatus) &&
                SoftwareProductStatusEnum.INACTIVE.toString().equalsIgnoreCase(softwareProductStatus);

        final boolean result = condition1 || condition2 || condition3;
        if (!result && LOG.isDebugEnabled()) {
            LOG.debug(String.format("Cannot facilitate consent withdrawal. DataRecipientStatus: %s, " +
                    "SoftwareProductStatus: %s", dataRecipientStatus, softwareProductStatus));
        }
        return result;
    }

    /**
     * Get ClientID - Status check
     *
     * @param clientId client id
     * @return software product status
     */
    public static String getSoftwareProductStatus(String clientId) {
        try {
            Map<String, String> cache = getMetadataFromCache(MetadataConstants.MAP_SOFTWARE_PRODUCTS);
            if (cache == null) {
                return null;
            }
            return cache.get(clientId);
        } catch (OpenBankingException e) {
            LOG.error("Unable to perform validation of SP. Returning special Code", e);
        }
        return null;
    }

    /**
     * Get ClientID - Status check
     *
     * @param clientId client id
     * @return data recipient status
     */
    public static String getDataRecipientStatus(String clientId) {
        try {
            Map<String, String> cache = getMetadataFromCache(MetadataConstants.MAP_DATA_RECIPIENTS);
            if (cache == null) {
                return null;
            }
            return cache.get(clientId);
        } catch (OpenBankingException e) {
            LOG.error("Unable to perform validation of DR. Returning special Code", e);
        }
        return null;
    }

    private static Map<String, String> getMetadataFromCache(String type) throws OpenBankingException {
        MetadataCacheKey cacheKey = MetadataCacheKey.from(type);

        return MetadataCache.getInstance().getFromCacheOrRetrieve(cacheKey, () -> {

            LOG.debug("Retrieving from primary cache as secondary cache is expired.");

            MetadataHolder metadataHolder = MetadataHolder.getInstance();

            if (metadataHolder == null || metadataHolder.getSoftwareProduct() == null ||
                    metadataHolder.getDataRecipient() == null) {
                throw new OpenBankingException("Periodical Updater DataHolder is null");
            }

            if (MetadataConstants.MAP_SOFTWARE_PRODUCTS.equals(type)) {
                return metadataHolder.getSoftwareProduct();
            } else if (MetadataConstants.MAP_DATA_RECIPIENTS.equals(type)) {
                return metadataHolder.getDataRecipient();
            } else {
                return null;
            }
        });
    }
}
