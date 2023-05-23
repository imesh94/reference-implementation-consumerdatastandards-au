/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model;

import java.util.List;
import javax.validation.constraints.NotEmpty;

/**
 * AccountDataDeleteDTO
 */
public class AccountDataDeleteDTO {

    @NotEmpty(message = "accountID field cannot be empty")
    private String accountID;

    private List<String> accountOwners;

    private List<String> nominatedRepresentatives;

    // Default constructor for deserialization
    public AccountDataDeleteDTO() {
    }

    // Getters and setters for private fields
    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public List<String> getAccountOwners() {
        return accountOwners;
    }

    public void setAccountOwners(List<String> accountOwners) {
        this.accountOwners = accountOwners;
    }

    public List<String> getNominatedRepresentatives() {
        return nominatedRepresentatives;
    }

    public void setNominatedRepresentatives(List<String> nominatedRepresentatives) {
        this.nominatedRepresentatives = nominatedRepresentatives;
    }
}
