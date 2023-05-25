/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.account.type.management.endpoint.disclosure.options.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * Disclosure Options Management - DomsStatusUpdateDTOItem
 */
public class DomsStatusUpdateDTOItem {
    @NotEmpty(message = "Expected field is not present for the field \"accountID\" ")
    private String accountID;

    @NotEmpty(message = "Expected field is not present for the field \"disclosureOption\" ")
    @Pattern(regexp = "^(pre-approval|no-sharing)$", message = "Invalid Disclosure Option value. " +
            "Must be pre-approval or no-sharing.")
    private String disclosureOption;

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getDisclosureOption() {
        return disclosureOption;
    }

    public void setDisclosureOption(String disclosureOption) {
        this.disclosureOption = disclosureOption;
    }
}
