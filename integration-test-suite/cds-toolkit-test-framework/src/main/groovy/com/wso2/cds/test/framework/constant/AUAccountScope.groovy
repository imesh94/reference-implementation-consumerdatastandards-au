/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
 * Enum class for keeping account scopes
 */
enum AUAccountScope {

    BANK_ACCOUNT_BASIC_READ("bank:accounts.basic:read"),
    BANK_ACCOUNT_DETAIL_READ("bank:accounts.detail:read"),
    BANK_TRANSACTION_READ("bank:transactions:read"),
    BANK_PAYEES_READ("bank:payees:read"),
    BANK_REGULAR_PAYMENTS_READ("bank:regular_payments:read"),
    BANK_CUSTOMER_BASIC_READ("common:customer.basic:read"),
    BANK_CUSTOMER_DETAIL_READ("common:customer.detail:read"),
    CDR_REGISTRATION("cdr:registration"),
    ADMIN_METRICS_BASIC_READ("admin:metrics.basic:read"),
    ADMIN_METADATA_UPDATE("admin:metadata:update"),
    PROFILE("profile"),

    private String value

    AUAccountScope(String value) {
        this.value = value
    }

    String getScopeString() {
        return this.value
    }

}

