/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.consent.extensions.authorize.utils;

/**
 * PermissionsEnum enumeration.
 */
public enum CustomerTypeSelectionMethodEnum {

    PROFILE_SELECTION("profile_selection"),
    CUSTOMER_UTYPE("customer_utype"),
    COOKIE_DATA("cookie_data");

    private final String value;

    CustomerTypeSelectionMethodEnum(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(value);
    }

}
