/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.gateway.executors.jwt.authentication.cache;

import com.wso2.openbanking.accelerator.common.caching.OpenBankingBaseCacheKey;

import java.io.Serializable;
import java.util.Objects;

/**
 * The definition of Cache Key to create JWT Auth JTI Cache.
 */
public class JwtJtiCacheKey extends OpenBankingBaseCacheKey implements Serializable {

    static final long serialVersionUID = 1382340369L;

    private String jtiValue;

    public JwtJtiCacheKey(String jtiCacheKey) {
        this.jtiValue = jtiCacheKey;
    }

    public static JwtJtiCacheKey of(String jtiCacheKey) {

        return new JwtJtiCacheKey(jtiCacheKey);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JwtJtiCacheKey that = (JwtJtiCacheKey) o;
        return Objects.equals(jtiValue, that.jtiValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jtiValue);
    }
}
