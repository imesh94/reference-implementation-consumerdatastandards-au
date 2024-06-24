/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 *
 */

package com.wso2.openbanking.cds.common.enums;

/**
 * Specifies the type of authorisation stage.
 */
public enum AuthorisationStageEnum {

    STARTED("started"),
    USER_IDENTIFIED("userIdentified"),
    USER_AUTHENTICATED("userAuthenticated"),
    ACCOUNT_SELECTED("accountSelected"),
    CONSENT_APPROVED("consentApproved"),
    CONSENT_REJECTED("consentRejected"),
    TOKEN_EXCHANGE_FAILED("tokenExchangeFailed"),
    COMPLETED("completed");

    private String value;

    AuthorisationStageEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AuthorisationStageEnum fromValue(String value) {
        for (AuthorisationStageEnum stage : values()) {
            if (stage.value.equals(value)) {
                return stage;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
