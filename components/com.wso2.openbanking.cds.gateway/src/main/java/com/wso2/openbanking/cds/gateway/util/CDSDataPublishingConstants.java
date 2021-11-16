/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.gateway.util;

import java.util.Arrays;
import java.util.List;

/**
 * CDS data publishing constants.
 */
public class CDSDataPublishingConstants {

    public static final String X_FAPI_CUSTOMER_IP_ADDRESS = "x-fapi-customer-ip-address";
    public static final String CUSTOMER_STATUS = "customerStatus";
    public static final String AUTHORIZATION = "Authorization";
    public static final String ACCESS_TOKEN_ID = "accessTokenID";

    //Customer status types
    public static final String UNATTENDED = "unattended";
    public static final String CUSTOMER_PRESENT = "customer-present";
    public static final String UNDEFINED = "undefined";

    //Infosec endpoints
    public static final String TOKEN_ENDPOINT = "/token";
    public static final String AUTHORIZE_ENDPOINT = "/authorize";
    public static final String JWKS_ENDPOINT = "/jwks";
    public static final String USERINFO_ENDPOINT = "/userinfo";
    public static final String REVOKE_ENDPOINT = "/revoke";
    public static final String TOKEN_INTROSPECT_ENDPOINT = "/token/introspect";
    public static final String PAR_ENDPOINT = "/par";
    public static final String REGISTER_ENDPOINT = "/register";
    public static final String REGISTER_CLIENT_ID_ENDPOINT = "/register/{ClientId}";
    public static final String WELL_KNOWN_ENDPOINT = "/.well-known/openid-configuration";
    public static final String CDR_ARRANGEMENT_ENDPOINT = "/{cdrArrangementId}";
    public static final String DISCOVERY_OUTAGES_ENDPOINT = "/discovery/outages";
    public static final String DISCOVERY_STATUS_ENDPOINT = "/discovery/status";

    public static final List<String> INFOSEC_ENDPOINTS = Arrays.asList(AUTHORIZE_ENDPOINT, TOKEN_ENDPOINT,
            USERINFO_ENDPOINT, PAR_ENDPOINT, TOKEN_INTROSPECT_ENDPOINT, JWKS_ENDPOINT, REVOKE_ENDPOINT,
            REGISTER_ENDPOINT, REGISTER_CLIENT_ID_ENDPOINT, WELL_KNOWN_ENDPOINT, CDR_ARRANGEMENT_ENDPOINT,
            DISCOVERY_OUTAGES_ENDPOINT, DISCOVERY_STATUS_ENDPOINT);

    public static final String UNKNOWN = "Unknown";

    public static final String CLIENT_USER_AGENT = "User-Agent";
    public static final String USER_NAME = "api.ut.userName";
    public static final String CONSUMER_KEY = "api.ut.consumerKey";
    public static final String HTTP_METHOD = "HTTP_METHOD";
    public static final String API_NAME = "api.ut.api";
    public static final String API_SPEC_VERSION = "SYNAPSE_REST_API_VERSION";
    public static final String SYNAPSE_REST_API = "SYNAPSE_REST_API";
    public static final String CORRELATION_ID = "correlation_id";
    public static final String REST_API_CONTEXT = "REST_API_CONTEXT";
}
