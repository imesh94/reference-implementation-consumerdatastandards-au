/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.sharing.models;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * Ceasing Secondary User - BlockedLegalEntityItemDTO
 */
public class LegalEntityItemUpdateDTO {
    @NotEmpty(message = "Invalid value for \"secondaryUserID\"")
    private String secondaryUserID;

    @NotEmpty(message = "Invalid value for \"accountID\"")
    private String accountID;

    @NotEmpty(message = "Invalid value for \"legalEntityID\"")
    private String legalEntityID;

    @NotEmpty(message = "Invalid value for  \"legalEntitySharingStatus\". " +
            "Must be active or blocked")
    @Pattern(regexp = "^(active|blocked)?$", message = "Invalid value for " +
            "\"legalEntitySharingStatus\". Must be active or blocked")
    private String legalEntitySharingStatus;

    public String getSecondaryUserID() {
        return secondaryUserID;
    }

    public void setSecondaryUserID(String secondaryUserID) {
        this.secondaryUserID = secondaryUserID;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getLegalEntityID() {
        return legalEntityID;
    }

    public void setLegalEntityID(String legalEntityID) {
        this.legalEntityID = legalEntityID;
    }

    public String getLegalEntitySharingStatus() {
        return legalEntitySharingStatus;
    }

    public void setLegalEntitySharingStatus(String legalEntitySharingStatus) {
        this.legalEntitySharingStatus = legalEntitySharingStatus;
    }
}

