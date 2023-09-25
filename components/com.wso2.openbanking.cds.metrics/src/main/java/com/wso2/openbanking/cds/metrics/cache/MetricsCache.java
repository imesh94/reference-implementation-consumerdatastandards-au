/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.cache;

import com.wso2.openbanking.accelerator.common.caching.OpenBankingBaseCache;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Cache definition to store Metrics aggregated data.
 */
public class MetricsCache extends OpenBankingBaseCache<MetricsCacheKey, List<BigDecimal>> {

    private static final String CACHE_NAME = "CDS_METRICS_CACHE";
    private static volatile MetricsCache instance;
    private final Integer accessExpiryMinutes;
    private final Integer modifiedExpiryMinutes;
    private static MetricsCacheKey peakTPSCacheKey = new MetricsCacheKey("AggregatedPeakTPSData");
    private static MetricsCacheKey availabilityCacheKey = new MetricsCacheKey("AggregatedAvailabilityData");

    /**
     * Initialize with unique cache name.
     */
    private MetricsCache() {

        super(CACHE_NAME);

        accessExpiryMinutes = OpenBankingCDSConfigParser.getInstance().getCacheExpiryInMinutes();
        modifiedExpiryMinutes = OpenBankingCDSConfigParser.getInstance().getCacheExpiryInMinutes();
    }

    public static MetricsCache getInstance() {

        if (instance == null) {
            synchronized (MetricsCache.class) {
                if (instance == null) {
                    instance = new MetricsCache();
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

    public static MetricsCacheKey getPeakTPSCacheKey() {

        return peakTPSCacheKey;
    }

    public static MetricsCacheKey getAvailabilityCacheKey() {

        return availabilityCacheKey;
    }
}
