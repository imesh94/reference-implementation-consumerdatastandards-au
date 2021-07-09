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

package com.wso2.openbanking.cds.common.utils;

/**
 * Error Constant Class
 */
public class ErrorConstants {

    //Error Response Structure constants
    public static final String ERROR = "error";
    public static final String ERROR_DESCRIPTION = "error_description";

    //HTTP Error Codes
    public static final String HTTP_BAD_REQUEST = "400";
    public static final String HTTP_UNAUTHORIZED = "401";
    public static final String HTTP_FORBIDDEN = "403";
    public static final String HTTP_NOT_FOUND = "404";
    public static final String HTTP_NOT_ALLOWED = "405";
    public static final String HTTP_NOT_ACCEPTABLE = "406";
    public static final String HTTP_TOO_MANY_REQUESTS = "429";
    public static final String HTTP_UNPROCESSABLE_ENTITY = "422";
    public static final String HTTP_SERVER_ERROR = "500";
    public static final String HTTP_CONFLICT = "409";
    public static final String HTTP_UNSUPPORTED_MEDIA_TYPE = "415";

    // High level textual error code, to help categorize the errors.
    public static final String BAD_REQUEST_CODE = "400 Bad Request";
    public static final String UNAUTHORIZED_CODE = "401 Unauthorized";
    public static final String FORBIDDEN_CODE = "403 Forbidden";
    public static final String NOT_FOUND_CODE = "404 Not Found";
    public static final String NOT_ALLOWED_CODE = "405 Method Not Allowed";
    public static final String NOT_ACCEPTABLE_CODE = "406 Not Acceptable";
    public static final String TOO_MANY_REQUESTS_CODE = "429 Too Many Requests";
    public static final String SERVER_ERROR_CODE = "500 Internal Server Error";
    public static final String CONFLICT_CODE = "409 Conflict";
    public static final String UNSUPPORTED_MEDIA_TYPE_CODE = "415 Unsupported Media Type";

    //Low level textual error code
    public static final String RESOURCE_INVALID_BANKING_ACCOUNT = "AU.CDR.Resource.InvalidBankingAccount";
    public static final String RESOURCE_INVALID = "AU.CDR.Resource.Invalid";
    public static final String INVALID_CONSENT_STATUS = "AU.CDR.Entitlements.InvalidConsentStatus";
    public static final String REVOKED_CONSENT_STATUS = "AU.CDR.Entitlements.ConsentIsRevoked";

}
