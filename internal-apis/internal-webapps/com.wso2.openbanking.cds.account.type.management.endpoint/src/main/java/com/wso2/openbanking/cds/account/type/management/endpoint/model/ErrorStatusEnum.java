/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.account.type.management.endpoint.model;

/**
 * ENUM of Error response Status.
 */
public enum ErrorStatusEnum {
    BAD_REQUEST("bad_request"),
    INTERNAL_SERVER_ERROR("internal_server_error"),
    INVALID_REQUEST("invalid_request"),
    RESOURCE_NOT_FOUND("resource_not_found");

    private final String value;

    ErrorStatusEnum(String value) {
       this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ErrorStatusEnum fromValue(String text) {

        for (ErrorStatusEnum b : ErrorStatusEnum.values()) {
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
