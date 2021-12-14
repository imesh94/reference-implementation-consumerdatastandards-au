/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.gateway.executors.idpermanence.utils;

import com.google.common.collect.ImmutableList;

/**
 * Constants required for IdPermanenceHandler and IdPermanenceUtils.
 */
public class IdPermanenceConstants {

    // resourceID keys
    public static final String ACCOUNT_ID = "accountId";
    public static final String ACCOUNT_IDS = "accountIds";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String SCHEDULED_PAYMENT_ID = "scheduledPaymentId";
    public static final String PAYEE_ID = "payeeId";

    // keys of response payload
    public static final String DATA = "data";
    public static final String LINKS = "links";
    public static final String SCHEDULED_PAYMENTS = "scheduledPayments";
    public static final String FROM = "from";
    public static final String PAYMENT_SET = "paymentSet";
    public static final String TO = "to";
    public static final String REQUEST_BODY = "request body";

    // Header Parameters
    public static final String DECRYPTED_SUB_REQUEST_PATH = "decrypted-sub-req-path";

    // response URLs
    public static final ImmutableList<String> RESOURCE_LIST_RES_URLS = ImmutableList.of(
            "/banking/accounts", "/banking/accounts/balances", "/banking/accounts/{accountId}/transactions",
            "/banking/accounts/{accountId}/direct-debits", "/banking/accounts/direct-debits", "/banking/payees");

    public static final ImmutableList<String> SINGLE_RESOURCE_RES_URLS = ImmutableList.of(
            "/banking/accounts/{accountId}/balance", "/banking/accounts/{accountId}",
            "/banking/accounts/{accountId}/transactions/{transactionId}", "/banking/payees/{payeeId}");

    public static final ImmutableList<String> SCHEDULED_PAYMENT_LIST_RES_URLS = ImmutableList.of(
            "/banking/accounts/{accountId}/payments/scheduled", "/banking/payments/scheduled");

    public static final ImmutableList<String> REQUEST_URLS_WITH_PATH_PARAMS = ImmutableList.of(
            "/banking/accounts/{accountId}/balance", "/banking/accounts/{accountId}",
            "/banking/accounts/{accountId}/transactions", "/banking/accounts/{accountId}/transactions/{transactionId}",
            "/banking/accounts/{accountId}/direct-debits", "/banking/accounts/{accountId}/payments/scheduled",
            "/banking/payees/{payeeId}");

    // regex patterns
    public static final String DECRYPTED_RESOURCE_ID_PATTERN = "^[^:]*:+[^:]*:[^:]*$";
    public static final String URL_TEMPLATE_PATH_PARAM_PATTERN = "(\\{.*?})";

}
