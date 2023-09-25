/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.cache;

import com.wso2.openbanking.accelerator.common.caching.OpenBankingBaseCacheKey;
import java.io.Serializable;

/**
 * Metrics cache key implementation.
 */
public class MetricsCacheKey extends OpenBankingBaseCacheKey implements Serializable {
    private static final long serialVersionUID = -8083228768863423682L;

    /**
     * public constructor for OpenBankingBaseCacheKey.
     *
     * @param cacheKey String cache key.
     */
    public MetricsCacheKey(String cacheKey) {

        super(cacheKey);
    }
}
