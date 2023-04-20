package com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model;

/**
 * ErrorDTO
 */
public class ErrorDTO {

    private String error;
    private String errorDescription;

    public ErrorDTO(String error, String errorDescription) {
        this.error = error;
        this.errorDescription = errorDescription;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
