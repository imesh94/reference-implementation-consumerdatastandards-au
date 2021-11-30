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
package com.wso2.openbanking.cds.identity.dcr.constants;

import java.util.ArrayList;

/**
 * Field names used for CDS specific validations.
 */
public class CDSValidationConstants {

    // ssa claims
    public static final String SSA_REDIRECT_URIS = "redirect_uris";
    public static final String SSA_SECTOR_IDENTIFIER_URI = "sector_identifier_uri";
    public static final String CDR_REGISTER = "cdr-register";
    public static final String SSA_LOGO_URI = "logo_uri";
    public static final String SSA_POLICY_URI = "policy_uri";
    public static final String SSA_TOS_URI = "tos_uri";
    public static final String SSA_CLIENT_URI = "client_uri";
    public static final String DATA_RECIPIENT_SOFTWARE_PRODUCT = "data-recipient-software-product";
    public static final ArrayList<String> VALID_SSA_SCOPES = new ArrayList<String>() {
        {
            add("openid");
            add("profile");
            add("bank:accounts.basic:read");
            add("bank:accounts.detail:read");
            add("bank:transactions:read");
            add("bank:payees:read");
            add("bank:regular_payments:read");
            add("common:customer.basic:read");
            add("common:customer.detail:read");
            add("cdr:registration");
        }
    };
    public static final String CDR_REGISTRATION_SCOPE = "cdr:registration";

    // registration request params
    public static final String INVALID_REDIRECT_URI = "invalid_redirect_uri";
    public static final String ID_TOKEN_ENCRYPTION_RESPONSE_ALG = "id_token_encrypted_response_alg";
    public static final String ID_TOKEN_ENCRYPTION_RESPONSE_ENC = "id_token_encrypted_response_enc";

    // dcr config constants
    public static final String DCR_VALIDATE_REDIRECT_URI = "DCR.EnableURIValidation";
    public static final String DCR_VALIDATE_URI_HOSTNAME = "DCR.EnableHostNameValidation";
    public static final String DCR_VALIDATE_SECTOR_IDENTIFIER_URI = "DCR.EnableSectorIdentifierUriValidation";
    public static final String JTI = "jti";
    public static final String JTI_REPLAYED = "JTI value of the registration request has been replayed";
}
