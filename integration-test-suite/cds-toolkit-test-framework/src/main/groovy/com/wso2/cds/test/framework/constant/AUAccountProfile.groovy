/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.cds.test.framework.constant

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
