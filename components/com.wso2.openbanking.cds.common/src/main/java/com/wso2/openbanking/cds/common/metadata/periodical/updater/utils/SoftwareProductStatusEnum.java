/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.common.metadata.periodical.updater.utils;

/**
 * ENUM of Software Product statuses.
 */
public enum SoftwareProductStatusEnum {

    ACTIVE("Active"),

    INACTIVE("Inactive"),

    REMOVED("Removed");

    private String value;

    SoftwareProductStatusEnum(String value) {

        this.value = value;
    }

    public static SoftwareProductStatusEnum fromValue(String text) {

        for (SoftwareProductStatusEnum b : SoftwareProductStatusEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    public String toString() {

        return String.valueOf(value);
    }
}
