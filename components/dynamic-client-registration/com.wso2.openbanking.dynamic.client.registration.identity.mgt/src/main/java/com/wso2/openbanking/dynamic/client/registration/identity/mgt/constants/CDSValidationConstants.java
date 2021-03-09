/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */
package com.wso2.openbanking.dynamic.client.registration.identity.mgt.constants;

/**
 * Field names used for CDS specific validations.
 */
public class CDSValidationConstants {

    //ssa claims
    public static final String SSA_REDIRECT_URIS = "redirect_uris";
    public static final String CDR_REGISTER = "cdr-register";
    public static final String DATA_RECIPIENT_SOFTWARE_PRODUCT = "data-recipient-software-product";
    public static final String SSA_SCOPES = "openid bank:accounts.basic:read bank:accounts.detail:read " +
            "bank:transactions:read bank:payees:read bank:regular_payments:read common:customer.basic:read " +
            "common:customer.detail:read cdr:registration";
}
