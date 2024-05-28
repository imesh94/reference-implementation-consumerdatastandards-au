/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.consent.extensions.util;

/**
 * PermissionsEnum enumeration
 */
public enum PermissionsEnum {

    READCUSTOMERDETAILSBASIC("READCUSTOMERDETAILSBASIC"),

    READCUSTOMERDETAILS("READCUSTOMERDETAILS"),

    CDRREADACCOUNTSBASIC("CDRREADACCOUNTSBASIC"),

    CDRREADACCOUNTSDETAILS("CDRREADACCOUNTSDETAILS"),

    CDRREADTRANSACTION("CDRREADTRANSACTION"),

    CDRREADPAYMENTS("CDRREADPAYMENTS"),

    CDRREADPAYEES("CDRREADPAYEES"),

    PROFILE("PROFILE"),

    NAME("NAME"),

    GIVENNAME("GIVENNAME"),

    FAMILYNAME("FAMILYNAME"),

    UPDATEDAT("UPDATEDAT"),

    EMAIL("EMAIL"),

    EMAILVERIFIED("EMAILVERIFIED"),

    PHONENUMBER("PHONENUMBER"),

    PHONENUMBERVERIFIED("PHONENUMBERVERIFIED"),

    ADDRESS("ADDRESS");

    private String value;

    PermissionsEnum(String value) {

        this.value = value;
    }

    public String toString() {

        return String.valueOf(value);
    }

    public static PermissionsEnum fromValue(String text) {

        for (PermissionsEnum enumValue : PermissionsEnum.values()) {
            if (String.valueOf(enumValue.value).equals(text)) {
                return enumValue;
            }
        }
        return null;
    }

    public static String fromName(String text) {

        for (PermissionsEnum enumValue : PermissionsEnum.values()) {
            if (enumValue.name().equals(text)) {
                return enumValue.value;
            }
        }
        return null;
    }
}
