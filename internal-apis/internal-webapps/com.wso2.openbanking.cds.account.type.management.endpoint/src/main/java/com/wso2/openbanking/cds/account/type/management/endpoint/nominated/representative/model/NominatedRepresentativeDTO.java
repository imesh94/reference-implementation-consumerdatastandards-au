package com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * NominatedRepresentativeDTO
 */
public class NominatedRepresentativeDTO {

    @NotEmpty(message = "Permission field cannot be empty")
    @Pattern(regexp = "^(AUTHORIZE|VIEW|REVOKE)$", message = "Invalid permission value. " +
            "Must be AUTHORIZE, VIEW, or REVOKE.")
    private String permission;

    @NotEmpty(message = "Name field cannot be empty")
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
