/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * NominatedRepresentativeDTO
 */
public class NominatedRepresentativeDTO {

    @NotEmpty(message = "permission field cannot be empty")
    @Pattern(regexp = "^(AUTHORIZE|VIEW|REVOKE)$", message = "Invalid permission value. " +
            "Must be AUTHORIZE, VIEW, or REVOKE.")
    private String permission;

    @NotEmpty(message = "name field cannot be empty")
    private String name;

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
