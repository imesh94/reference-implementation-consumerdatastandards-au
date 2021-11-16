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

package com.wso2.openbanking.cds.gateway.executors.error.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wso2.openbanking.accelerator.gateway.executor.core.OpenBankingGatewayExecutor;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIRequestContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIResponseContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OpenBankingExecutorError;
import com.wso2.openbanking.accelerator.gateway.util.GatewayConstants;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.common.error.handling.util.ErrorConstants;
import com.wso2.openbanking.cds.common.error.handling.util.ErrorUtil;
import com.wso2.openbanking.cds.common.idpermanence.IdEncryptorDecryptor;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import net.sf.saxon.Err;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

/**
 * Executor to handle gateway errors in CDS format.
 */
public class CDSErrorHandler implements OpenBankingGatewayExecutor {

    private static Log log = LogFactory.getLog(CDSErrorHandler.class);
    private static final String SECRET_KEY = OpenBankingCDSConfigParser.getInstance().getIdPermanenceSecretKey();

    /**
     * Method to handle pre request
     *
     * @param obapiRequestContext OB request context object
     */
    @Override
    public void preProcessRequest(OBAPIRequestContext obapiRequestContext) {

        handleRequestError(obapiRequestContext);

    }

    /**
     * Method to handle post request
     *
     * @param obapiRequestContext OB request context object
     */
    @Override
    public void postProcessRequest(OBAPIRequestContext obapiRequestContext) {

        handleRequestError(obapiRequestContext);
    }

    /**
     * Method to handle pre response
     *
     * @param obapiResponseContext OB response context object
     */
    @Override
    public void preProcessResponse(OBAPIResponseContext obapiResponseContext) {

        handleResponseError(obapiResponseContext);
    }

    /**
     * Method to handle post response
     *
     * @param obapiResponseContext OB response context object
     */
    @Override
    public void postProcessResponse(OBAPIResponseContext obapiResponseContext) {

        handleResponseError(obapiResponseContext);
    }


    protected void handleRequestError(OBAPIRequestContext obapiRequestContext) {

        if (!obapiRequestContext.isError()) {
            return;
        }

        ArrayList<OpenBankingExecutorError> errors = obapiRequestContext.getErrors();
        HashSet<String> statusCodes = new HashSet<>();

        for (OpenBankingExecutorError error : errors) {
            statusCodes.add(error.getHttpStatusCode());
        }

        if (obapiRequestContext.getMsgInfo().getResource().contains("/register")) {
            JSONArray dcrErrorPayload = getDCRErrorJSON(errors);
            obapiRequestContext.setModifiedPayload(dcrErrorPayload.toString());
        } else {
            String memberId = obapiRequestContext.getApiRequestInfo().getUsername();
            String appId = obapiRequestContext.getApiRequestInfo().getConsumerKey();
            JsonObject errorPayload = getErrorJson(errors, memberId, appId);
            obapiRequestContext.setModifiedPayload(errorPayload.toString());
        }
        Map<String, String> addedHeaders = obapiRequestContext.getAddedHeaders();
        addedHeaders.put(GatewayConstants.CONTENT_TYPE_TAG, GatewayConstants.JSON_CONTENT_TYPE);
        obapiRequestContext.setAddedHeaders(addedHeaders);

        int statusCode;
        if (ErrorUtil.isAnyClientErrors(statusCodes)) {
            statusCode = ErrorUtil.getHTTPErrorCode(statusCodes);
        } else {
            if (obapiRequestContext.getContextProperty(GatewayConstants.ERROR_STATUS_PROP) != null) {
                statusCode = Integer.parseInt(obapiRequestContext
                        .getContextProperty(GatewayConstants.ERROR_STATUS_PROP));
            } else {
                statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            }
        }
        obapiRequestContext.addContextProperty(GatewayConstants.ERROR_STATUS_PROP, String.valueOf(statusCode));

    }

    protected void handleResponseError(OBAPIResponseContext obapiResponseContext) {

        if (!obapiResponseContext.isError()) {
            return;
        }
        ArrayList<OpenBankingExecutorError> errors = obapiResponseContext.getErrors();
        HashSet<String> statusCodes = new HashSet<>();

        for (OpenBankingExecutorError error : errors) {
            statusCodes.add(error.getHttpStatusCode());
        }

        if (obapiResponseContext.getMsgInfo().getResource().contains("/register")) {
            JSONArray dcrErrorPayload = getDCRErrorJSON(errors);
            obapiResponseContext.setModifiedPayload(dcrErrorPayload.toString());
        } else {
            String memberId = obapiResponseContext.getApiRequestInfo().getUsername();
            String appId = obapiResponseContext.getApiRequestInfo().getConsumerKey();
            JsonObject errorPayload = getErrorJson(errors, memberId, appId);
            obapiResponseContext.setModifiedPayload(errorPayload.toString());
        }
        Map<String, String> addedHeaders = obapiResponseContext.getAddedHeaders();
        addedHeaders.put(GatewayConstants.CONTENT_TYPE_TAG, GatewayConstants.JSON_CONTENT_TYPE);
        obapiResponseContext.setAddedHeaders(addedHeaders);

        int statusCode;
        if (ErrorUtil.isAnyClientErrors(statusCodes)) {
            statusCode = ErrorUtil.getHTTPErrorCode(statusCodes);
        } else {
            if (obapiResponseContext.getContextProperty(GatewayConstants.ERROR_STATUS_PROP) != null) {
                statusCode = Integer.parseInt(obapiResponseContext
                        .getContextProperty(GatewayConstants.ERROR_STATUS_PROP));
            } else {
                statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            }
        }

        obapiResponseContext.addContextProperty(GatewayConstants.ERROR_STATUS_PROP, String.valueOf(statusCode));
    }

    public static JSONArray getDCRErrorJSON(ArrayList<OpenBankingExecutorError> errors) {

        JSONArray errorList = new JSONArray();

        for (OpenBankingExecutorError error : errors) {
            JSONObject errorObj = new JSONObject();
            errorObj.put(ErrorConstants.ERROR, error.getTitle());
            errorObj.put(ErrorConstants.ERROR_DESCRIPTION, error.getMessage());
            errorList.add(errorObj);
        }
        return errorList;
    }

    public static JsonObject getErrorJson(ArrayList<OpenBankingExecutorError> errors, String memberId, String appId) {

        JsonArray errorList = new JsonArray();
        JsonObject parentObject = new JsonObject();

        for (OpenBankingExecutorError error : errors) {
            JsonObject errorObj = new JsonObject();

            try {
                Object errorPayload = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(error.getMessage());

//                if ("Consent Enforcement Error".equals(error.getTitle())) {
//                    JSONObject errorJSON = (JSONObject) errorPayload;
//                    errorObj.addProperty(ErrorConstants.TITLE, errorJSON.getAsString(ErrorConstants.TITLE));
//
//                    if (errorJSON.get(ErrorConstants.ACCOUNT_ID) != null) {
//                        String stringToEncrypt = memberId + ":" + appId + ":" +
//                                errorJSON.get(ErrorConstants.ACCOUNT_ID).toString();
//                        String encryptedId = IdEncryptorDecryptor.encrypt(stringToEncrypt, SECRET_KEY);
//                        errorObj.addProperty(ErrorConstants.DETAIL, encryptedId);
//                    } else {
//                        errorObj.addProperty(ErrorConstants.DETAIL, errorJSON.getAsString(ErrorConstants.DETAIL));
//                    }
//
//                    if (errorJSON.getAsString(ErrorConstants.META_URN) != null) {
//                        JsonObject meta = new JsonObject();
//                        meta.addProperty("urn", errorJSON.getAsString(ErrorConstants.META_URN));
//                        errorObj.add("meta", meta);
//                    }
//                } else {
//                    if (errorPayload instanceof JSONObject) {
//                        JSONObject errorJSON = (JSONObject) errorPayload;
//                        errorObj.addProperty(ErrorConstants.DETAIL, errorJSON.getAsString(ErrorConstants.DETAIL));
//
//                        if (errorJSON.getAsString(ErrorConstants.META_URN) != null) {
//                            JsonObject meta = new JsonObject();
//                            meta.addProperty("urn", errorJSON.getAsString(ErrorConstants.META_URN));
//                            errorObj.add("meta", meta);
//                        }
//                    } else {
//                        errorObj.addProperty(ErrorConstants.DETAIL, errorPayload.toString());
//                    }
//                    errorObj.addProperty(ErrorConstants.TITLE, error.getTitle());
//                }

                //////////////////////////////////////////////////////////////////

                if (errorPayload instanceof JSONObject) {

                    JSONObject errorJSON = (JSONObject) errorPayload;

                    if ("Consent Enforcement Error".equals(error.getTitle())) {
                        errorObj.addProperty(ErrorConstants.TITLE, errorJSON.getAsString(ErrorConstants.TITLE));
                        if (errorJSON.get(ErrorConstants.ACCOUNT_ID) != null) {
                            String stringToEncrypt = memberId + ":" + appId + ":" +
                                    errorJSON.get(ErrorConstants.ACCOUNT_ID).toString();
                            String encryptedId = IdEncryptorDecryptor.encrypt(stringToEncrypt, SECRET_KEY);
                            errorObj.addProperty(ErrorConstants.DETAIL, encryptedId);
                        } else {
                            errorObj.addProperty(ErrorConstants.DETAIL, errorJSON.getAsString(ErrorConstants.DETAIL));
                        }

                    } else {
                        errorObj.addProperty(ErrorConstants.TITLE, error.getTitle());
                        errorObj.addProperty(ErrorConstants.DETAIL, errorJSON.getAsString(ErrorConstants.DETAIL));
                    }

                    if (errorJSON.getAsString(ErrorConstants.META_URN) != null) {
                        JsonObject meta = new JsonObject();
                        meta.addProperty("urn", errorJSON.getAsString(ErrorConstants.META_URN));
                        errorObj.add("meta", meta);
                    }
                } else {
                    errorObj.addProperty(ErrorConstants.DETAIL, errorPayload.toString());
                    errorObj.addProperty(ErrorConstants.TITLE, error.getTitle());
                }

            } catch (ParseException e) {
                log.error("Unexpected Error", e);
            }
            errorObj.addProperty(ErrorConstants.CODE, error.getCode());
            errorList.add(errorObj);
        }
        parentObject.add(ErrorConstants.ERRORS, errorList);
        return parentObject;
    }
}
