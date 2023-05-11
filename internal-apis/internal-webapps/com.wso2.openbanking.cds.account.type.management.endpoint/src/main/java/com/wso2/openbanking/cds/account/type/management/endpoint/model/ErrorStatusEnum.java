package com.wso2.openbanking.cds.account.type.management.endpoint.model;

/**
 * ENUM of Error response Status.
 */
public enum ErrorStatusEnum {
    BAD_REQUEST("Bad Request"),
    INTERNAL_SERVER_ERROR("Internal Server Error"),
    INVALID_REQUEST("invalid request"),
    RESOURCE_NOT_FOUND("resource not found");

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
