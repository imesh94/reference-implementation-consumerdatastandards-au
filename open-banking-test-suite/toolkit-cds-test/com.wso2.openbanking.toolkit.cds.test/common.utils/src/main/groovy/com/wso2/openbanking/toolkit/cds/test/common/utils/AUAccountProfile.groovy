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
