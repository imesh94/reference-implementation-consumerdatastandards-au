/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.consent.extensions.common;

/**
 * ENUM of Secondary Account Owner Types.
 */
public enum SecondaryAccountOwnerTypeEnum {
    JOINT("secondary_joint_account_owner"),
    INDIVIDUAL("secondary_individual_account_owner");

    private final String value;

    SecondaryAccountOwnerTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isValidOwnerType(String value) {
        for (SecondaryAccountOwnerTypeEnum type : SecondaryAccountOwnerTypeEnum.values()) {
            if (type.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
