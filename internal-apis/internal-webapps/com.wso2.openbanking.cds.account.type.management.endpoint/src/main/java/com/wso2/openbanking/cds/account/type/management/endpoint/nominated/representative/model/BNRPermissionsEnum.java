/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model;

/**
 * ENUM of Nominated Representative permissions.
 */
public enum BNRPermissionsEnum {

    VIEW("VIEW"),

    AUTHORIZE("AUTHORIZE"),

    REVOKE("REVOKE");

    private String value;

    BNRPermissionsEnum(String value) {

        this.value = value;
    }

    public static BNRPermissionsEnum fromValue(String text) {

        for (BNRPermissionsEnum b : BNRPermissionsEnum.values()) {
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
