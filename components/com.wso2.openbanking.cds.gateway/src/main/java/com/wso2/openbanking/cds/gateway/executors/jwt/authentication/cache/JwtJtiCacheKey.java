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

package com.wso2.openbanking.cds.gateway.executors.jwt.authentication.cache;

import com.wso2.openbanking.accelerator.common.caching.OpenBankingBaseCacheKey;

import java.io.Serializable;
import java.util.Objects;

/**
 * The definition of Cache Key to create JWT Auth JTI Cache.
 */
public class JwtJtiCacheKey extends OpenBankingBaseCacheKey implements Serializable {

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
