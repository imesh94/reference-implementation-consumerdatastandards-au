/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.demo.backend.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Account Information Object.
 */
public class AccountInfo implements Serializable {

    private static final long serialVersionUID = -5675538442439191042L;

    @SerializedName("CustomerId")
    private String customerId;
    @SerializedName("AccountIds")
    private String[] accountIds;
    @SerializedName("Permissions")
    private String[] permissions;

    public String getCustomerId() {

        return customerId;
    }

    public void setCustomerId(String customerId) {

        this.customerId = customerId;
    }

    public String[] getAccountIds() {

        if (accountIds == null) {
            return new String[]{};
        }
        return Arrays.copyOf(accountIds, accountIds.length);
    }

    public void setAccountIds(String[] accountIds) {

        this.accountIds = Arrays.copyOf(accountIds, accountIds.length);
    }

    public String[] getPermissions() {

        if (permissions == null) {
            return new String[]{};
        }
        return Arrays.copyOf(permissions, permissions.length);
    }

    public void setPermissions(String[] permissions) {

        this.permissions = Arrays.copyOf(permissions, permissions.length);
    }
}
