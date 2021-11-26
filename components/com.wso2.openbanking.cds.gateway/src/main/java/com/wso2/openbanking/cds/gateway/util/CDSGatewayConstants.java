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

package com.wso2.openbanking.cds.gateway.util;

import java.util.regex.Pattern;

/**
 * CDS gateway common constants.
 */
public class CDSGatewayConstants {

    // Constants related to executors
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";

    // CDSHeaderValidationExecutor constants
    public static final String X_FAPI_CUSTOMER_IP_ADDRESS = "x-fapi-customer-ip-address";
    public static final String X_CDS_CLIENT_HEADERS = "x-cds-client-headers";
    public static final String X_FAPI_AUTH_DATE = "x-fapi-auth-date";
    public static final String X_FAPI_INTERACTION_ID = "x-fapi-interaction-id";
    public static final String MAX_REQUESTED_ENDPOINT_VERSION = "x-v";
    public static final String MIN_REQUESTED_ENDPOINT_VERSION = "x-min-v";
    public static final String X_VERSION = "x-version";
    public static final String IMF_FIX_DATE_PATTERN = "EEE, dd MMM uuuu HH:mm:ss 'GMT'";
    public static final String RFC850_DATE_PATTERN = "EEEE, dd-MMM-uu HH:mm:ss 'GMT'";
    public static final String ASC_TIME_DATE_PATTERN = "EE MMM dd HH:mm:ss uuuu";
    public static final Pattern UUID_REGEX_PATTERN =
            Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    private CDSGatewayConstants() {
    }
}
