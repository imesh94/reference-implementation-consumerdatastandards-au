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

import com.wso2.openbanking.accelerator.common.caching.OpenBankingBaseCacheKey;

import java.io.Serializable;
import java.util.Objects;

/**
 * The definition of Cache Key to create Metadata Cache.
 */
public class MetadataCacheKey extends OpenBankingBaseCacheKey implements Serializable {

    static final long serialVersionUID = 1382340305L;
    private String status;

    public static MetadataCacheKey from(String statusType) {

        MetadataCacheKey metadataCacheKey = new MetadataCacheKey();
        metadataCacheKey.setStatus(statusType); // Metadata element is either DR map or SP map.
        return metadataCacheKey;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MetadataCacheKey that = (MetadataCacheKey) o;
        return Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }
}
