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

package com.wso2.openbanking.cds.identity.authenticator.util;

/**
 * Constants are listed here
 */
public class Constants {

    public static final int DEFAULT_VALIDITY_PERIOD_IN_MINUTES = 300;
    public static final boolean DEFAULT_ENABLE_JTI_CACHE = true;
    public static final String OAUTH_JWT_BEARER_GRANT_TYPE = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
    public static final String OAUTH_JWT_ASSERTION = "client_assertion";
    public static final String OAUTH_JWT_ASSERTION_TYPE = "client_assertion_type";
    public static final String DEFAULT_AUDIENCE = "";
    public static final String ENDPOINT_ALIAS = "EndpointAlias";
    public static final String PREVENT_TOKEN_REUSE = "PreventTokenReuse";
    public static final String REJECT_BEFORE_IN_MINUTES = "RejectBeforeInMinutes";
    public static final String JWT_ID_CLAIM = "jti";
    public static final String EXPIRATION_TIME_CLAIM = "exp";
    public static final String AUDIENCE_CLAIM = "aud";
    public static final String SUBJECT_CLAIM = "sub";
    public static final String ISSUER_CLAIM = "iss";
    public static final String PRIVATE_KEY_JWT = "signedJWT";
    public static final String JWKS_URI = "jwksURI";

    //Endpoint identifiers
    public static final String ARRANGEMENTS = "arrangements";
    public static final String TOKEN = "token";
    public static final String REVOKE = "revoke";
    public static final String PAR = "par";


}
