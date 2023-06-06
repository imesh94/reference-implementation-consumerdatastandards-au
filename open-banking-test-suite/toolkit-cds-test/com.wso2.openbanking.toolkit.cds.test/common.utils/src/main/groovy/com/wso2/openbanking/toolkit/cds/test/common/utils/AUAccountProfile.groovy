/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.toolkit.cds.test.common.utils

/**
 * Enum class for keeping account Profiles Eg: Business and Individual.
 */
enum AUAccountProfile {

    ORGANIZATION_A("Organization A"),
    ORGANIZATION_B("Organization B"),
    INDIVIDUAL("Individual"),

    private String value

    AUAccountProfile(String value) {
        this.value = value
    }

    String getProfileString() {
        return this.value
    }
}
