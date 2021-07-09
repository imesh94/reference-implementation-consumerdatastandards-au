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

import com.wso2.openbanking.accelerator.gateway.executor.core.OpenBankingGatewayExecutor;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIRequestContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIResponseContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OpenBankingExecutorError;
import com.wso2.openbanking.accelerator.gateway.util.GatewayConstants;
import com.wso2.openbanking.cds.common.utils.ErrorConstants;
import com.wso2.openbanking.cds.common.utils.ErrorUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
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
        }
        Map<String, String> addedHeaders = obapiRequestContext.getAddedHeaders();
        addedHeaders.put(GatewayConstants.CONTENT_TYPE_TAG, GatewayConstants.JSON_CONTENT_TYPE);
        obapiRequestContext.setAddedHeaders(addedHeaders);

        int statusCode;
        if (ErrorUtil.isAnyClientErrors(statusCodes)) {
            statusCode = ErrorUtil.getHTTPErrorCode(statusCodes);
        } else {
            if (obapiRequestContext.getContextProperty(GatewayConstants.ERROR_STATUS_PROP) != null) {
                statusCode =  Integer.parseInt(obapiRequestContext
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
        }
        Map<String, String> addedHeaders = obapiResponseContext.getAddedHeaders();
        addedHeaders.put(GatewayConstants.CONTENT_TYPE_TAG, GatewayConstants.JSON_CONTENT_TYPE);
        obapiResponseContext.setAddedHeaders(addedHeaders);

        int statusCode;
        if (ErrorUtil.isAnyClientErrors(statusCodes)) {
            statusCode = ErrorUtil.getHTTPErrorCode(statusCodes);
        } else {
            if (obapiResponseContext.getContextProperty(GatewayConstants.ERROR_STATUS_PROP) != null) {
                statusCode =  Integer.parseInt(obapiResponseContext
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
}
