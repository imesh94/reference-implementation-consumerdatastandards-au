/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.account.type.management.endpoint.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ErrorDTO model class.
 */
public class ErrorDTO {

    private String error;
    private String errorDescription;

    public ErrorDTO(ErrorStatusEnum error, String errorDescription) {
        this.error = error.getValue();
        this.errorDescription = errorDescription;
    }

    public String getError() {
        return error;
    }

    @JsonProperty("error_description")
    public String getErrorDescription() {
        return errorDescription;
    }
}
