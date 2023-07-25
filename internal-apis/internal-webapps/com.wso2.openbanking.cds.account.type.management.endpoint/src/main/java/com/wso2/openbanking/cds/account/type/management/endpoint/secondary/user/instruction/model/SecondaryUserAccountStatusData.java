/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.account.type.management.endpoint.secondary.user.instruction.model;

import com.wso2.openbanking.cds.account.type.management.endpoint.constants.AccountTypeManagementConstants;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Model class to carry attributes related to AU secondary user account status.
 */
public class SecondaryUserAccountStatusData {

    @NotNull(message = "secondaryAccountId field cannot be empty")
    private String secondaryAccountId;
    @NotNull(message = "secondaryUserId field cannot be empty")
    private String secondaryUserId;
    @NotNull(message = "otherAccountsAvailability field cannot be empty")
    @Pattern(regexp = "true|false", message = "Invalid Other account availability value. Must be 'true' or 'false'")
    private String otherAccountsAvailability;
    @NotNull(message = "secondaryAccountInstructionStatus field cannot be empty")
    @Pattern(regexp = "^(active|inactive)$", message = "Invalid secondary account instruction status value. " +
            "Must be inactive or active.")
    private String secondaryAccountInstructionStatus;

    public String getSecondaryAccountID() {

        return secondaryAccountId;
    }

    public void setSecondaryAccountID(String accountId) {
        // set tenant domain if not available
        if (!secondaryUserId.toLowerCase(Locale.ENGLISH).endsWith(
                AccountTypeManagementConstants.CARBON_TENANT_DOMAIN)) {
            secondaryUserId = secondaryUserId + AccountTypeManagementConstants.CARBON_TENANT_DOMAIN;
        }

        this.secondaryAccountId = accountId;
    }

    public String getSecondaryUserID() {

        return secondaryUserId;
    }

    public void setSecondaryUserID(String secondaryUserId) {

        this.secondaryUserId = secondaryUserId;
    }

    public Boolean getOtherAccountsAvailability() {

        return Boolean.parseBoolean(otherAccountsAvailability);
    }

    public void setOtherAccountsAvailability(String otherAccountsAvailability) {

        this.otherAccountsAvailability = otherAccountsAvailability;
    }

    public String getSecondaryAccountInstructionStatus() {

        return secondaryAccountInstructionStatus;
    }

    public void setSecondaryAccountInstructionStatus(String secondaryAccountInstructionStatus) {

        this.secondaryAccountInstructionStatus = secondaryAccountInstructionStatus;
    }

    public Map<String, String> getAccountMetadataMap() {
        Map<String, String> accountMetadataMap = new HashMap<>();
        accountMetadataMap.put("otherAccountsAvailability", this.otherAccountsAvailability);
        accountMetadataMap.put("secondaryAccountInstructionStatus", this.secondaryAccountInstructionStatus);

        return accountMetadataMap;
    }

}
