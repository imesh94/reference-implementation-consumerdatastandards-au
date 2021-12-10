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
    public static final String INTROSPECTION_ENDPOINT = "/introspect";
    public static final String PAR_ENDPOINT = "/par";
    public static final String WELL_KNOWN_ENDPOINT = "/.well-known/openid-configuration";

    public static final String TOKEN_REQUEST_URI = "/oauth2/token";
    public static final String AUTHORIZE_REQUEST_URI = "/oauth2/authorize";
    public static final String JWKS_REQUEST_URI = "/oauth2/jwks";
    public static final String USERINFO_REQUEST_URI = "/oauth2/userinfo";
    public static final String REVOKE_REQUEST_URI = "/oauth2/revoke";
    public static final String INTROSPECTION_REQUEST_URI = "/oauth2/introspect";
    public static final String PAR_REQUEST_URI = "/api/openbanking/push-authorization/par";
    public static final String WELL_KNOWN_REQUEST_URI = "/oauth2/token/.well-known/openid-configuration";

    public static final String TOKEN_API = "TokenAPI";
    public static final String AUTHORIZE_API = "AuthorizeAPI";
    public static final String USERINFO_API = "UserInfoAPI";
    public static final String INTROSPECT_API = "IntrospectAPI";
    public static final String JWKS_API = "JwksAPI";
    public static final String TOKEN_REVOCATION_API = "TokenRevocationAPI";
    public static final String WELL_KNOWN_API = "WellKnownAPI";
    public static final String PAR_API = "PAR";

    public static final String REQUEST_IN_TIME = "REQUEST_IN_TIME";
    public static final String UNDEFINED = "undefined";
    public static final String CONTENT_LENGTH = "Content-Length";

}
