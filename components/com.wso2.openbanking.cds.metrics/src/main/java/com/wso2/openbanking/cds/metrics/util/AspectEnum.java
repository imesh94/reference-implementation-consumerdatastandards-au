/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.util;

/**
 * Enum for Authenticated and Unauthenticated aspects.
 */
public enum AspectEnum {

    AUTHENTICATED("authenticated"),
    UNAUTHENTICATED("unauthenticated"),
    ALL("all");

    private String value;

    AspectEnum(String value) {
        this.value = value;
    }

    public static AspectEnum fromValue(String text) {

        for (AspectEnum b : AspectEnum.values()) {
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
