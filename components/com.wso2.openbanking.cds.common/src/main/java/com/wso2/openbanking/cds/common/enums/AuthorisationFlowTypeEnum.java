/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.common.enums;

/**
 * Specifies the authorisation flow types.
 */
public enum AuthorisationFlowTypeEnum {

    CONSENT_AUTHORISATION("consentAuthorisation"),
    CONSENT_AMENDMENT_AUTHORISATION("consentAmendmentAuthorisation"),
    UNCLASSIFIED("unclassified");

    private String value;

    AuthorisationFlowTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AuthorisationFlowTypeEnum fromValue(String value) {
        for (AuthorisationFlowTypeEnum type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
