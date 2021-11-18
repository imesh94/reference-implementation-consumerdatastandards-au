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

package com.wso2.openbanking.cds.common.error.handling.util;

import com.google.gson.Gson;
import com.wso2.openbanking.cds.common.error.handling.models.CDSError;
import com.wso2.openbanking.cds.common.error.handling.models.CDSErrorMeta;
import com.wso2.openbanking.cds.common.error.handling.models.CDSErrorResponse;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Util class for Error Handling
 */
public class ErrorUtil {

    private static final Log log = LogFactory.getLog(ErrorUtil.class);

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
     *
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

    /**
     * Method to get the error json with multiple error objects for AU
     *
     * @param errors Array with multiple error details
     * @return
     */
    public static String getErrorJson(JSONArray errors) {
        Gson gson = new Gson();
        String errorJson = gson.toJson(constructErrorObject(errors));
        return errorJson;
    }

    /**
     * Method to construct object for AU to pass to the Error Generation Library.
     *
     * @param error        Relevant Error enum from the AUErrorConstants.AUError enum
     * @param errorMessage Custom error message
     * @return
     */
    public static JSONObject getErrorObject(ErrorConstants.AUErrorEnum error, String errorMessage,
                                            CDSErrorMeta metaData) {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put(ErrorConstants.ERROR_ENUM, error);
        jsonObject.put(ErrorConstants.DETAIL, errorMessage);
        jsonObject.put(ErrorConstants.METADATA, metaData);


        return jsonObject;
    }

    /**
     * Method to construct final error object for AU
     * JSONArray errorData is an array of JSONObjects which has the following structure
     * {
     * "auErrorEnum":"...",
     * "detail":"...",
     * "metadata":"..."
     * }
     * <p>
     * detail element in the above object is optional if it is not available default message will be returned in
     * the error.
     * To construct the JSONObjects getErrorArray() method in AUErrorHandlingUtils class can be used
     *
     * @param errorData
     * @return
     */
    public static CDSErrorResponse constructErrorObject(JSONArray errorData) {

        List<CDSError> errorArray = new ArrayList<>();

        for (int errorIndex = 0; errorIndex < errorData.length(); errorIndex++) {

            JSONObject errorElement = errorData.getJSONObject(errorIndex);

            //Get the enum for respective error
            ErrorConstants.AUErrorEnum auError = ErrorConstants.AUErrorEnum
                    .fromValue(errorElement.get(ErrorConstants.ERROR_ENUM).toString());

            //Setting the error details
            String errorMessage;
            String metaUrnError = StringUtils.EMPTY;
            if (errorElement.has(ErrorConstants.DETAIL)) {
                //Error detail is available in the object
                try {
                    Object errorObject = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(errorElement.
                            getString(ErrorConstants.DETAIL));
                    //Check errorObject instance to capture and convert string error message to JSON format
                    if (errorObject instanceof net.minidev.json.JSONObject) {
                        net.minidev.json.JSONObject errorJSON = (net.minidev.json.JSONObject) errorObject;
                        errorMessage = errorJSON.getAsString(ErrorConstants.DETAIL);
                        //Check for availability of urn in the error JSON
                        if (errorJSON.getAsString(ErrorConstants.META_URN) != null) {
                            metaUrnError = errorJSON.getAsString(ErrorConstants.META_URN);
                        }
                    } else {
                        errorMessage = errorObject.toString();
                    }
                } catch (ParseException e) {
                    log.error("Unexpected error while parsing string", e);
                    errorMessage = "Unexpected error while parsing string";
                }
            } else {
                //Sending the default message
                errorMessage = auError.getDetail();
            }
            //Construct the error object to CDS error standard
            CDSError error;
            if (StringUtils.isNotBlank(metaUrnError)) {
                //Adding urn to error body if meta urn is available
                CDSErrorMeta metaObject = (CDSErrorMeta) errorElement.get(ErrorConstants.METADATA);
                metaObject.setUrn(metaUrnError);
                error = new CDSError.Builder()
                        .withCode(auError.getCode())
                        .withTitle(auError.getTitle())
                        .withDetail(errorMessage)
                        .withMeta(metaObject)
                        .build();
            } else {
                error = new CDSError.Builder()
                        .withCode(auError.getCode())
                        .withTitle(auError.getTitle())
                        .withDetail(errorMessage)
                        .build();
            }
            //Add the constructed object to the error array
            errorArray.add(error);
        }

        CDSErrorResponse auErrorResponse = new CDSErrorResponse();
        auErrorResponse.setErrors(errorArray);
        return auErrorResponse;
    }

}
