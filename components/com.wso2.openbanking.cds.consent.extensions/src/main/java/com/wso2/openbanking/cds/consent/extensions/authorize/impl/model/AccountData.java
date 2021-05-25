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
package com.wso2.openbanking.cds.consent.extensions.authorize.impl.model;

import com.wso2.openbanking.cds.consent.extensions.authorize.impl.utils.PermissionsEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class Account data.
 */
public class AccountData {

    private String expirationDateTime = null;
    private List<PermissionsEnum> permissions = new ArrayList<>();

    public String getExpirationDateTime() {

        return expirationDateTime;
    }

    public void setExpirationDateTime(String expirationDateTime) {

        this.expirationDateTime = expirationDateTime;
    }

    public List<PermissionsEnum> getPermissions() {

        return permissions;
    }

    public void setPermissions(List<PermissionsEnum> permissions) {

        this.permissions = permissions;
    }
}
