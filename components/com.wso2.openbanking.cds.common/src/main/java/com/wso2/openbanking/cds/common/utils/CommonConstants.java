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

package com.wso2.openbanking.cds.common.utils;

/**
 * Common Constant Class
 */
public class CommonConstants {


    private CommonConstants() {}

    public static final String OB_CONSENT_ID_PREFIX = "OB_CONSENT_ID_";
    public static final String CDS_API_CONTEXT_PREFIX = "cds-au";

    // Config related constants
    public static final String OB_CONFIG_FILE = "open-banking-cds.xml";
    public static final String ACCOUNT_MASKING = "ConsentManagement.EnableAccountMasking";
    public static final String ID_PERMANENCE_SECRET_KEY = "IdPermanence.SecretKey";

    // Http related constants
    public static final String POST_METHOD = "POST";

    // metadata config constants
    public static final String METADATA_CACHE = "MetaDataCache";
    public static final String METADATA_CACHE_ENABLED = METADATA_CACHE + ".EnableMetaDataCache";
    public static final String METADATA_CACHE_UPDATE_TIME = METADATA_CACHE + ".MetaDataCacheUpdatePeriod";
    public static final String METADATA_CACHE_DATA_RECIPIENTS_URL = METADATA_CACHE + ".DataRecipientsDiscoveryURL";
    public static final String METADATA_CACHE_DCR_INTERNAL_URL = METADATA_CACHE + ".DCRInternalURL";
    public static final String METADATA_CACHE_APPLICATION_SEARCH_URL = METADATA_CACHE + ".APIMApplicationSearchURL";
    public static final String METADATA_CACHE_RETRY_COUNT = METADATA_CACHE + ".RetryCount";
    public static final String METADATA_CACHE_EXPIRY = METADATA_CACHE + ".Expiry";
    public static final String METADATA_CACHE_BULK_EXECUTE = METADATA_CACHE +
            ".DataHolderResponsibilities.BulkExecution";
    public static final String METADATA_CACHE_BULK_EXECUTE_HOUR = METADATA_CACHE +
            ".DataHolderResponsibilities.BulkExecutionHour";
}
