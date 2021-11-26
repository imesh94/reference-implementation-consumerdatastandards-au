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

package com.wso2.openbanking.cds.metrics.util;

/**
 * Enum for CDS defined priority tiers.
 */
public enum PriorityEnum {

    UNAUTHENTICATED("Unauthenticated"),
    HIGH_PRIORITY("HighPriority"),
    LOW_PRIORITY("LowPriority"),
    UNATTENDED("Unattended"),
    LARGE_PAYLOAD("LargePayload");

    private String value;

    PriorityEnum(String value) {

        this.value = value;
    }

    public static PriorityEnum fromValue(String text) {

        for (PriorityEnum b : PriorityEnum.values()) {
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
