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

package com.wso2.openbanking.cds.gateway.mediators;

import com.wso2.openbanking.cds.common.error.handling.models.CDSErrorMeta;
import com.wso2.openbanking.cds.common.error.handling.util.ErrorConstants;
import com.wso2.openbanking.cds.common.error.handling.util.ErrorUtil;
import com.wso2.openbanking.cds.gateway.utils.GatewayConstants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.transport.nhttp.NhttpConstants;

/**
 * CDS Gateway error mediator.
 * This mediator is used for gateway error mediation and data publishing.
 */
public class GatewayErrorMediator extends AbstractMediator {

    private static final Log log = LogFactory.getLog(GatewayErrorMediator.class);

    @Override
    public boolean mediate(MessageContext messageContext) {

        if ((messageContext.getProperty(GatewayConstants.ERROR_CODE)) != null) {

            int errorCode = (int) messageContext.getProperty(GatewayConstants.ERROR_CODE);
            String errorMessage = (String) messageContext.getProperty(GatewayConstants.ERROR_MSG);
            String errorDetail = (String) messageContext.getProperty(GatewayConstants.ERROR_DETAIL);

            JSONObject errorData;

            if (Integer.toString(errorCode).startsWith("9")) {
                errorData = getAuthFailureResponse(errorCode, errorMessage);
            } else {
                return true;
            }

            String errorResponse = errorData.get(GatewayConstants.ERROR_RESPONSE).toString();
            int status = (int) errorData.get(GatewayConstants.STATUS_CODE);
            setFaultPayload(messageContext, errorResponse, status);

        }
        return true;
    }

    /**
     * set the error message to the jsonPayload to be sent back
     *
     * @param messageContext the messageContext sent back to the user
     * @param errorData      the details of the error for validation failure
     */
    private static void setFaultPayload(MessageContext messageContext, String errorData, int status) {

        org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext)
                .getAxis2MessageContext();
        axis2MessageContext.setProperty(GatewayConstants.MESSAGE_TYPE, GatewayConstants.JSON_CONTENT_TYPE);
        axis2MessageContext.setProperty(NhttpConstants.HTTP_SC, status);
        try {
            //setting the payload as the message payload
            JsonUtil.getNewJsonPayload(axis2MessageContext, errorData, true,
                    true);
            messageContext.setResponse(true);
            messageContext.setProperty(GatewayConstants.RESPONSE_CAPS, GatewayConstants.TRUE);
            messageContext.setTo(null);
            axis2MessageContext.removeProperty(GatewayConstants.NO_ENTITY_BODY);
        } catch (AxisFault axisFault) {
            log.error(GatewayConstants.PAYLOAD_SETTING_ERROR, axisFault);
        }
    }

    /**
     * Method to get the error response for auth failures
     *
     * @param errorCode
     * @param errorMessage
     * @return
     */
    private static JSONObject getAuthFailureResponse(int errorCode, String errorMessage) {

        JSONObject errorData = new JSONObject();
        JSONArray errorList = new JSONArray();
        int status;
        String errorResponse;

        if (errorCode == GatewayConstants.API_AUTH_GENERAL_ERROR) {
            status = ErrorConstants.AUErrorEnum.UNEXPECTED_ERROR.getHttpCode();
            errorList.add(ErrorUtil.getErrorObject(ErrorConstants.AUErrorEnum.UNEXPECTED_ERROR, errorMessage,
                    new CDSErrorMeta()));
            errorResponse = ErrorUtil.getErrorJson(errorList);
        } else if (errorCode == GatewayConstants.API_AUTH_INCORRECT_API_RESOURCE ||
                errorCode == GatewayConstants.API_AUTH_FORBIDDEN ||
                errorCode == GatewayConstants.INVALID_SCOPE) {
            status = ErrorConstants.AUErrorEnum.RESOURCE_FORBIDDEN.getHttpCode();
            errorList.add(ErrorUtil.getErrorObject(ErrorConstants.AUErrorEnum.RESOURCE_FORBIDDEN_TEMP, errorMessage,
                    new CDSErrorMeta()));
            errorResponse = ErrorUtil.getErrorJson(errorList);
        } else if (errorCode == GatewayConstants.API_AUTH_MISSING_CREDENTIALS ||
                errorCode == GatewayConstants.API_AUTH_INVALID_CREDENTIALS) {
            status = ErrorConstants.AUErrorEnum.CLIENT_AUTH_FAILED.getHttpCode();
            errorList.add(ErrorUtil.getErrorObject(ErrorConstants.AUErrorEnum.CLIENT_AUTH_FAILED, errorMessage,
                    new CDSErrorMeta()));
            errorResponse = ErrorUtil.getErrorJson(errorList);
        } else {
            status = ErrorConstants.AUErrorEnum.CLIENT_AUTH_FAILED.getHttpCode();
            errorList.add(ErrorUtil.getErrorObject(ErrorConstants.AUErrorEnum.CLIENT_AUTH_FAILED, errorMessage,
                    new CDSErrorMeta()));
            errorResponse = ErrorUtil.getErrorJson(errorList);
        }

        errorData.put(GatewayConstants.STATUS_CODE, status);
        errorData.put(GatewayConstants.ERROR_RESPONSE, errorResponse);

        return errorData;

    }
}



