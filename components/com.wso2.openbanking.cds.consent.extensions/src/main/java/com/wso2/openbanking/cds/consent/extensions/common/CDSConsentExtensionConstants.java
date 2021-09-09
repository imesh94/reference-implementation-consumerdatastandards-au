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
package com.wso2.openbanking.cds.consent.extensions.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CDS Consent Extension Constant class
 */
public class CDSConsentExtensionConstants {

    public static final String TITLE = "title";
    public static final String DATA = "data";
    public static final String CONSENT_DATA = "consentData";
    public static final String ACCOUNT_DATA = "accountData";
    public static final String ACCOUNTS_DATA = "accounts_data";
    public static final String ACCOUNT_ID = "account_id";
    public static final String DISPLAY_NAME = "display_name";
    public static final String REDIRECT_URL = "redirectURL";
    public static final String EXPIRATION_DATE_TITLE = "Expiration Date Time";
    public static final String PERMISSION_TITLE = "Permissions";
    public static final String EXPIRATION_DATE_TIME = "expirationDateTime";
    public static final String PERMISSIONS = "permissions";
    public static final String SHARING_DURATION_VALUE = "sharing_duration_value";
    public static final String CDR_ARRANGEMENT_ID = "cdr_arrangement_id";
    public static final String CDR_ACCOUNTS = "CDR_ACCOUNTS";
    public static final String OPENID_SCOPES = "openid_scopes";
    public static final String CLIENT_ID = "client_id";
    public static final String SP_FULL_NAME = "sp_full_name";
    public static final String CLAIMS = "claims";
    public static final String IS_ERROR = "isError";
    public static final String SHARING_DURATION = "sharing_duration";
    public static final String ACCOUNTS = "accounts";
    public static final String CREATED_STATUS = "created";
    public static final String REJECTED_STATUS = "rejected";
    public static final String AUTHORIZED_STATUS = "authorized";
    public static final String AWAITING_AUTH_STATUS = "awaitingAuthorization";
    public static final String COMMON_AUTH_ID = "commonAuthId";
    public static final String METADATA = "metadata";
    public static final String ACCOUNT_IDS = "accountIds";
    public static final String ZERO = "0";
    public static final String CONSENT_EXPIRY = "consent_expiration";
    public static final String ACCOUNT_MASKING_ENABLED = "account_masking_enabled";

    public static final String CHAR_SET = "UTF-8";
    public static final String CUSTOMER_TYPE = "customerUType";
    public static final String ORGANISATION = "Organisation";
    public static final String DATA_REQUESTED = "data_requested";
    public static final String COMMON_CUSTOMER_BASIC_READ_SCOPE = "common:customer.basic:read";
    public static final String COMMON_CUSTOMER_DETAIL_READ_SCOPE = "common:customer.detail:read";
    public static final String COMMON_ACCOUNTS_BASIC_READ_SCOPE = "bank:accounts.basic:read";
    public static final String COMMON_ACCOUNTS_DETAIL_READ_SCOPE = "bank:accounts.detail:read";
    public static final String COMMON_SUBSTRING = "common:";
    public static final Map<String, Map<String, List<String>>> CDS_DATA_CLUSTER;
    public static final Map<String, Map<String, List<String>>> BUSINESS_CDS_DATA_CLUSTER;
    public static final Map<String, Map<String, List<String>>> INDIVIDUAL_CDS_DATA_CLUSTER;

    public static final String ACCEPT_HEADER_NAME = "Accept";
    public static final String ACCEPT_HEADER_VALUE = "application/json";
    public static final String SERVICE_URL_SLASH = "/";

    public static final String TRUE = "true";
    public static final String ENABLE_CUSTOMER_DETAILS = "CustomerDetails.Enable";
    public static final String CUSTOMER_DETAILS_RETRIEVE_ENDPOINT = "CustomerDetails.CustomerDetailsRetrieveEndpoint";

    public static final String SHARABLE_ACCOUNTS_ENDPOINT = "ConsentManagement.SharableAccountsRetrieveEndpoint";

    public static final String OPENID_SCOPE = "openid";
    public static final String PROFILE_SCOPE = "profile";
    public static final String CDR_REGISTRATION_SCOPE = "cdr:registration";
    public static final String HTTP_METHOD = "HttpMethod";
    public static final String POST_METHOD = "POST";
    public static final String ENABLE_ACCOUNT_ID_VALIDATION_ON_RETRIEVAL = "ConsentManagement.Validate" +
            "AccountIdOnRetrieval";

    static {
        Map<String, Map<String, List<String>>> dataCluster = new HashMap<>();

        Map<String, List<String>> permissionLanguage = new LinkedHashMap<>();
        permissionLanguage.put("Account name, type, and balance", Arrays.asList("Name of account", "Type of account",
                "Account balance"));
        dataCluster.put("bank:accounts.basic:read", permissionLanguage);


        permissionLanguage = new LinkedHashMap<>();
        permissionLanguage.put("Account balance and details", Arrays.asList("Name of account", "Type of account",
                "Account balance", "Account number", "Interest rates", "Fees", "Discounts", "Account terms",
                "Account mail address"));
        dataCluster.put("bank:accounts.detail:read", permissionLanguage);


        permissionLanguage = new LinkedHashMap<>();
        permissionLanguage.put("Transaction details", Arrays.asList("Incoming and outgoing transactions", "Amounts",
                "Dates", "Descriptions of transactions", "Who you have sent money to and received " +
                        "money from(e.g.their name)"));
        dataCluster.put("bank:transactions:read", permissionLanguage);


        permissionLanguage = new LinkedHashMap<>();
        permissionLanguage.put("Direct debits and scheduled payments", Arrays.asList("Direct debits", "Scheduled " +
                "payments"));
        dataCluster.put("bank:regular_payments:read", permissionLanguage);


        permissionLanguage = new LinkedHashMap<>();
        permissionLanguage.put("Saved payees", Arrays.asList("Names and details of accounts you have saved; (e.g. " +
                "their BSB and Account Number, BPay CRN and Biller code, or NPP PayID)"));
        dataCluster.put("bank:payees:read", permissionLanguage);

        CDS_DATA_CLUSTER = Collections.unmodifiableMap(dataCluster);
    }

    static {
        Map<String, Map<String, List<String>>> dataCluster = new HashMap<>();


        Map<String, List<String>> permissionLanguage = new LinkedHashMap<>();
        permissionLanguage.put("Organisation profile", Arrays.asList("Agent name and role", "Organisation name",
                "Organisation numbers (ABN or ACN)", "Charity status", "Establishment date", "Industry",
                "Organisation type", "Country of registration"));
        dataCluster.put("common:customer.basic:read", permissionLanguage);

        permissionLanguage = new LinkedHashMap<>();
        permissionLanguage.put("Organisation profile and contact details", Arrays.asList("Agent name and role",
                "Organisation name", "Organisation numbers (ABN or ACN)", "Charity status", "Establishment date",
                "Industry", "Organisation type", "Country of registration", "Organisation address",
                "Mail address", "Phone number"));
        dataCluster.put("common:customer.detail:read", permissionLanguage);

        BUSINESS_CDS_DATA_CLUSTER = Collections.unmodifiableMap(dataCluster);
    }

    static {
        Map<String, Map<String, List<String>>> dataCluster = new HashMap<>();

        Map<String, List<String>> permissionLanguage = new LinkedHashMap<>();
        permissionLanguage.put("Name and occupation", Arrays.asList("Name", "Occupation"));
        dataCluster.put("common:customer.basic:read", permissionLanguage);

        permissionLanguage = new LinkedHashMap<>();
        permissionLanguage.put("Name, occupation, contact details", Arrays.asList("Name", "Occupation", "Phone",
                "Email address", "Mail address", "Residential address"));
        dataCluster.put("common:customer.detail:read", permissionLanguage);

        INDIVIDUAL_CDS_DATA_CLUSTER = Collections.unmodifiableMap(dataCluster);
    }
}
