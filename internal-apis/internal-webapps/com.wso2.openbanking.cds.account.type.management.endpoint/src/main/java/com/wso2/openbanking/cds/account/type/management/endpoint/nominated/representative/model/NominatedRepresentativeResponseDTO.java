/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * NominatedRepresentativeResponseDTO.
 */
public class NominatedRepresentativeResponseDTO {

    @JsonProperty("userID")
    private String userID;

    @JsonProperty("permissionStatus")
    private Map<String, String> permissionStatus;

    public NominatedRepresentativeResponseDTO(String userID, String accountID, String status) {
        this.userID = userID;
        this.permissionStatus = new HashMap<>();
        this.permissionStatus.put(accountID, status);
    }

    public String getUserID() {
        return userID;
    }

    public Map<String, String> getPermissionStatus() {
        return permissionStatus;
    }
}
