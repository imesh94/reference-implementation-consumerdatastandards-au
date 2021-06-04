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
    public static final String DATA_REQUESTED = "data_requested";
    public static final Map<String, Map<String, List<String>>> CDS_DATA_CLUSTER;
    public static final Map<String, Map<String, List<String>>> BUSINESS_CDS_DATA_CLUSTER;
    public static final Map<String, Map<String, List<String>>> INDIVIDUAL_CDS_DATA_CLUSTER;

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
