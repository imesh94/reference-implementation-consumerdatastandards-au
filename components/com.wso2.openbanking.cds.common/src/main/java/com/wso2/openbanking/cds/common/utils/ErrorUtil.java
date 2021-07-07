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

import org.apache.http.HttpStatus;

import java.util.HashSet;

/**
 * Util class for Error Handling
 */
public class ErrorUtil {

    /**
     * Method to check whether error status code list have any client errors
     *
     * @param statusCodes
     * @return
     */
    public static boolean isAnyClientErrors(HashSet<String> statusCodes) {

        for (String statusCode : statusCodes) {
            if (statusCode.startsWith("4")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to get the HTTP Status code
     * @param statusCodes
     * @return
     */
    public static int getHTTPErrorCode(HashSet<String> statusCodes) {

        int statusCode;

        if (statusCodes.contains(ErrorConstants.HTTP_UNAUTHORIZED)) {
            statusCode = HttpStatus.SC_UNAUTHORIZED;
        } else if (statusCodes.contains(ErrorConstants.HTTP_FORBIDDEN)) {
            statusCode = HttpStatus.SC_FORBIDDEN;
        } else if (statusCodes.contains(ErrorConstants.HTTP_NOT_FOUND)) {
            statusCode = HttpStatus.SC_NOT_FOUND;
        } else if (statusCodes.contains(ErrorConstants.HTTP_NOT_ACCEPTABLE)) {
            statusCode = HttpStatus.SC_NOT_ACCEPTABLE;
        } else if (statusCodes.contains(ErrorConstants.HTTP_UNSUPPORTED_MEDIA_TYPE)) {
            statusCode = HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE;
        } else if (statusCodes.contains(ErrorConstants.HTTP_UNPROCESSABLE_ENTITY)) {
            statusCode = HttpStatus.SC_UNPROCESSABLE_ENTITY;
        } else {
            statusCode = HttpStatus.SC_BAD_REQUEST;
        }
        return statusCode;
    }
}
