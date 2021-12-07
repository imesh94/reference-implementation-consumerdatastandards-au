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

package com.wso2.openbanking.cds.identity.tomcat.filters.constants;

/**
 * Field names used for data publishing related to InfoSec endpoints.
 */
public class InfoSecDataPublishingConstants {

    public static final String TOKEN_ENDPOINT = "/token";
    public static final String AUTHORIZE_ENDPOINT = "/authorize";
    public static final String JWKS_ENDPOINT = "/jwks";
    public static final String USERINFO_ENDPOINT = "/userinfo";
    public static final String REVOKE_ENDPOINT = "/revoke";
    public static final String TOKEN_INTROSPECTION_ENDPOINT = "/token/introspect";
    public static final String PAR_ENDPOINT = "/par";
    public static final String WELL_KNOWN_ENDPOINT = "/.well-known/openid-configuration";

    public static final String REST_API_CONTEXT = "REST_API_CONTEXT";
    public static final String CLIENT_ASSSERTION = "client_assertion";
    public static final String CLIENT_CREDENTIALS = "client_credentials";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String TRANSPORT_HEADERS = "TRANSPORT_HEADERS";
    public static final String AUTHORIZATION = "Authorization";

    public static final String TOKEN_API = "TokenAPI";
    public static final String AUTHORIZE_API = "AuthorizeAPI";
    public static final String USERINFO_API = "UserInfoAPI";
    public static final String INTROSPECT_API = "IntrospectAPI";
    public static final String JWKS_API = "JwksAPI";
    public static final String TOKEN_REVOCATION_API = "TokenRevocationAPI";
    public static final String WELL_KNOWN_API = "WellKnownAPI";
    public static final String PAR_API = "PAR";
    public static final String API_SPEC_VERSION = "SYNAPSE_REST_API_VERSION";
    public static final String CLIENT_USER_AGENT = "User-Agent";
    public static final String CUSTOMER_STATUS = "CustomerStatus";
    public static final String IS_TOKEN_REQUEST_FROM_DCR = "IsTokenRequestFromDCR";
    public static final String ARRANGEMENT = "arrangement";

}
