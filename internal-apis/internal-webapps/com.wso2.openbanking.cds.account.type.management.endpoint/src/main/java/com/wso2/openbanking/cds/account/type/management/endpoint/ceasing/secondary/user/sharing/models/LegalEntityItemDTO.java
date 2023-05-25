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

/**
 * Ceasing Secondary User - BlockedLegalEntityItemDTO
 */
public class LegalEntityItemDTO {
    @NotEmpty(message = "Bad request : expected field not present for the field \"secondaryUserID\" ")
    private String secondaryUserID;

    @NotEmpty(message = "Bad request : expected field not present for the field \"accountID\" ")
    private String accountID;

    @NotEmpty(message = "Bad request : expected field not present for the field \"legalEntityID\" ")
    private String legalEntityID;

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
}

