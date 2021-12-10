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

import com.wso2.openbanking.accelerator.common.caching.OpenBankingBaseCache;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.common.utils.CommonConstants;

/**.
 * Cache definition to store JTI values
 */
public class JwtJtiCache extends OpenBankingBaseCache<JwtJtiCacheKey, String> {

    private static final String cacheName = "JWT_AUTH_JTI_CACHE";

    private Integer accessExpiryMinutes;
    private Integer modifiedExpiryMinutes;
    private static JwtJtiCache jwtJtiCache;

    /**
     * Initialize With unique cache name.
     */
    private JwtJtiCache() {

        super(cacheName);
        this.accessExpiryMinutes = setAccessExpiryMinutes();
        this.modifiedExpiryMinutes = setModifiedExpiryMinutes();
    }

    /**
     * Singleton getInstance method to create only one object.
     *
     * @return JwtJtiCache object
     */
    public static synchronized JwtJtiCache getInstance() {
        if (jwtJtiCache == null) {
            jwtJtiCache = new JwtJtiCache();
        }
        return jwtJtiCache;
    }

    @Override
    public int getCacheAccessExpiryMinutes() {

        return accessExpiryMinutes;
    }

    @Override
    public int getCacheModifiedExpiryMinutes() {

        return modifiedExpiryMinutes;
    }

    public int setAccessExpiryMinutes() {

        String cacheAccessExpiry = (String) OpenBankingCDSConfigParser.getInstance().getConfiguration()
                .get(CommonConstants.JWT_AUTH_CACHE_EXPIRY_TIME);

        return cacheAccessExpiry == null ? 3600 : Integer.parseInt(cacheAccessExpiry);
    }

    public int setModifiedExpiryMinutes() {

        String cacheAccessExpiry = (String) OpenBankingCDSConfigParser.getInstance().getConfiguration()
                .get(CommonConstants.JWT_AUTH_CACHE_MODIFY_EXPIRY);

        return cacheAccessExpiry == null ? 3600 : Integer.parseInt(cacheAccessExpiry);
    }
}
