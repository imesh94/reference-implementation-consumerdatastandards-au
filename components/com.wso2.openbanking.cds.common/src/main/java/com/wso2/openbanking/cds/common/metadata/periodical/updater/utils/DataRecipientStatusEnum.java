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

package com.wso2.openbanking.cds.common.metadata.periodical.updater.utils;

/**
 * ENUM of Data Recipient and Software Product statuses.
 */
public enum DataRecipientStatusEnum {

    ACTIVE("Active"),

    SUSPENDED("Suspended"),

    REVOKED("Revoked"),

    SURRENDEED("Surrendered");

    private String value;

    DataRecipientStatusEnum(String value) {

        this.value = value;
    }

    public static DataRecipientStatusEnum fromValue(String text) {

        for (DataRecipientStatusEnum b : DataRecipientStatusEnum.values()) {
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
