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

package com.wso2.openbanking.cds.common.metadata.status.validator.cache;


import com.wso2.openbanking.accelerator.common.caching.OpenBankingBaseCache;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;

import java.util.Map;

/**
 * Cache definition to store ADR and SP statuses as String - String key value pair Maps.
 */
public class MetadataCache extends OpenBankingBaseCache<MetadataCacheKey, Map<String, String>> {

    private static final String CACHE_NAME = "CDS_METADATA_RESPONSE";
    private static volatile MetadataCache instance;
    private final Integer accessExpiryMinutes;
    private final Integer modifiedExpiryMinutes;

    /**
     * Initialize with unique cache name.
     */
    private MetadataCache() {

        super(CACHE_NAME);

        accessExpiryMinutes = OpenBankingCDSConfigParser.getInstance().getCacheExpiryInMinutes();
        modifiedExpiryMinutes = OpenBankingCDSConfigParser.getInstance().getCacheExpiryInMinutes();
    }

    public static MetadataCache getInstance() {

        if (instance == null) {
            synchronized (MetadataCache.class) {
                if (instance == null) {
                    instance = new MetadataCache();
                }
            }
        }
        return instance;
    }

    @Override
    public int getCacheAccessExpiryMinutes() {
        return accessExpiryMinutes;
    }

    @Override
    public int getCacheModifiedExpiryMinutes() {
        return modifiedExpiryMinutes;
    }
}
