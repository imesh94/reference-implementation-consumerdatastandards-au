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
 * Specifies the type of abandonment by stage for AU.
 */
public enum AbandonmentsByStageEnum {

    PRE_IDENTIFICATION("preIdentification"),
    PRE_AUTHENTICATION("preAuthentication"),
    PRE_ACCOUNT_SELECTION("preAccountSelection"),
    PRE_AUTHORISATION("preAuthorisation"),
    REJECTED("rejected"),
    FAILED_TOKEN_EXCHANGE("failedTokenExchange");

    private String value;

    AbandonmentsByStageEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
