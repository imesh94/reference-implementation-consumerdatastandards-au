/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.account.type.management.endpoint.constants;

/**
 * Constant Class for Account Type Management
 */
public class AccountTypeManagementConstants {

    // Common
    public static final String CARBON_TENANT_DOMAIN = "@carbon.super";

    // Secondary User Instructions
    public static final String PRIMARY_MEMBER_AUTH_TYPE = "primary_member";
    public static final String ACTIVE_STATUS = "active";

    // Ceasing Secondary User Sharing
    public static final String METADATA_KEY_BLOCKED_LEGAL_ENTITIES = "BLOCKED_LEGAL_ENTITIES";
    public static final String LEGAL_ENTITY_ID = "legal_entity_id";
    public static final String LEGAL_ENTITY_NAME = "legal_entity_name";

    // Disclosure Options Management
    public static final String DATA = "data";
    public static final String ACCOUNT_ID = "accountID";
    public static final String DISCLOSURE_OPTION = "disclosureOption";
    public static final String DISCLOSURE_OPTION_STATUS = "DISCLOSURE_OPTIONS_STATUS";

    // Business Nominated Representative
    public static final String BNR_PERMISSION = "bnr-permission";
    public static final String NOMINATED_REPRESENTATIVE_AUTH_TYPE = "nominated_representative";
    public static final String REVOKED_CONSENT_STATUS = "Revoked";
    public static final String METADATA_SERVICE_ERROR = "\"Error occurred while persisting nominated " +
            "representative data using the account metadata service\"";

}
