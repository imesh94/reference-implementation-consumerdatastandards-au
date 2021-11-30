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

package com.wso2.openbanking.cds.gateway.mediators;

import com.wso2.openbanking.accelerator.common.config.OpenBankingConfigParser;
import com.wso2.openbanking.accelerator.data.publisher.common.constants.DataPublishingConstants;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.common.data.publisher.CDSDataPublishingService;
import com.wso2.openbanking.cds.common.error.handling.models.CDSErrorMeta;
import com.wso2.openbanking.cds.common.error.handling.util.ErrorConstants;
import com.wso2.openbanking.cds.common.error.handling.util.ErrorUtil;
import com.wso2.openbanking.cds.common.utils.CDSCommonUtils;
import com.wso2.openbanking.cds.gateway.utils.GatewayConstants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.transport.nhttp.NhttpConstants;
import org.wso2.carbon.apimgt.impl.APIConstants;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


/**
 * CDS Gateway error mediator.
 * This mediator is used for gateway error mediation and data publishing.
 */
public class GatewayErrorMediator extends AbstractMediator {

    private static final Log log = LogFactory.getLog(GatewayErrorMediator.class);

    @Override
    public boolean mediate(MessageContext messageContext) {

        // Publish gateway error data.
        if (Boolean.parseBoolean((String) OpenBankingConfigParser.getInstance().getConfiguration()
                .get(DataPublishingConstants.DATA_PUBLISHING_ENABLED))) {

            log.debug("Publishing invocation error data from CDS error mediator.");
            if ((messageContext.getProperty(GatewayConstants.ERROR_CODE)) != null) {
                Map<String, Object> invocationErrorData = getApiInvocationErrorDataToPublish(messageContext);
                CDSDataPublishingService.getCDSDataPublishingService().publishApiInvocationData(invocationErrorData);
            }
        }
        // Error handling logic.
        if ((messageContext.getProperty(GatewayConstants.ERROR_CODE)) != null) {

            int errorCode = (int) messageContext.getProperty(GatewayConstants.ERROR_CODE);
            String errorMessage = (String) messageContext.getProperty(GatewayConstants.ERROR_MSG);
            String errorDetail = (String) messageContext.getProperty(GatewayConstants.ERROR_DETAIL);

            JSONObject errorData;
            if (Integer.toString(errorCode).startsWith("9")) {
                errorData = getAuthFailureResponse(errorCode, errorMessage);
            } else if (Integer.toString(errorCode).startsWith("4") && StringUtils.isEmpty(errorDetail)) {
                errorData = getResourceFailureResponse(errorCode, errorMessage);
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
     * Method to retrieve API Invocation Error Data to publish.
     *
     * @param messageContext message context
     * @return api input stream data map
     */
    private static Map<String, Object> getApiInvocationErrorDataToPublish(MessageContext messageContext) {

        Map<String, Object> requestData = new HashMap<>();

        org.apache.axis2.context.MessageContext axis2MessageContext =
                ((Axis2MessageContext) messageContext).getAxis2MessageContext();
        Map headers = (Map) axis2MessageContext.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);

        long unixTimestamp = Instant.now().getEpochSecond();
        String electedResource = (String) messageContext.getProperty(APIConstants.API_ELECTED_RESOURCE);

        String userAgent;
        if (messageContext.getProperty(GatewayConstants.CLIENT_USER_AGENT) != null) {
            userAgent = (String) messageContext.getProperty(GatewayConstants.CLIENT_USER_AGENT);
        } else if (headers.get(GatewayConstants.CLIENT_USER_AGENT) != null) {
            userAgent = (String) headers.get(GatewayConstants.CLIENT_USER_AGENT);
        } else {
            userAgent = GatewayConstants.UNKNOWN;
        }

        String restApiContext = (String) messageContext.getProperty(GatewayConstants.REST_API_CONTEXT);
        String customerStatus;
        if (GatewayConstants.INFOSEC_ENDPOINTS.contains(restApiContext) ||
                GatewayConstants.INFOSEC_ENDPOINTS.contains(electedResource)) {
            customerStatus = GatewayConstants.UNDEFINED;
        } else if (headers.get(GatewayConstants.X_FAPI_CUSTOMER_IP_ADDRESS) == null) {
            customerStatus = GatewayConstants.UNATTENDED;
        } else {
            customerStatus = GatewayConstants.CUSTOMER_PRESENT;
        }

        String consumerId = (String) axis2MessageContext.getProperty(GatewayConstants.USER_NAME);
        String clientId = (String) axis2MessageContext.getProperty(GatewayConstants.CONSUMER_KEY);
        String httpMethod = (String) axis2MessageContext.getProperty(GatewayConstants.HTTP_METHOD);
        String apiName = (String) axis2MessageContext.getProperty(GatewayConstants.API_NAME);
        // Get api name from SYNAPSE_REST_API if not available in axis2 message context.
        if (apiName == null && messageContext.getProperty(GatewayConstants.SYNAPSE_REST_API) != null) {
            apiName = (messageContext.getProperty(GatewayConstants.SYNAPSE_REST_API).toString())
                            .split(":")[0];
        }

        String apiSpecVersion = null;
        if (messageContext.getProperty(GatewayConstants.API_SPEC_VERSION) != null) {
            apiSpecVersion = (String) messageContext.getProperty(GatewayConstants.API_SPEC_VERSION);
        }

        int statusCode = (int) messageContext.getProperty(GatewayConstants.HTTP_RESPONSE_STATUS_CODE);
        String messageId = (String) messageContext.getProperty(GatewayConstants.CORRELATION_ID);

        String authorizationHeader = (String) headers.get(GatewayConstants.AUTHORIZATION);
        String accessToken = (authorizationHeader != null && authorizationHeader.split(" ").length > 1) ?
                authorizationHeader.split(" ")[1] : null;
        // Encrypt access token if configured.
        if (accessToken != null && OpenBankingCDSConfigParser.getInstance().isTokenEncryptionEnabled()) {
            accessToken = CDSCommonUtils.encryptAccessToken(accessToken);
        }

        // Get error payload size
        long payloadSize = 0;
        SOAPEnvelope env = messageContext.getEnvelope();
        if (env != null) {
            SOAPBody soapbody = env.getBody();
            if (soapbody != null) {
                byte[] size = soapbody.toString().getBytes(Charset.defaultCharset());
                payloadSize = size.length;
            }
        }

        requestData.put("consentId", null);
        requestData.put("consumerId", consumerId);
        requestData.put("clientId", clientId);
        requestData.put("userAgent", userAgent);
        requestData.put("statusCode", statusCode);
        requestData.put("httpMethod", httpMethod);
        requestData.put("responsePayloadSize", payloadSize);
        requestData.put("electedResource", electedResource);
        requestData.put("apiName", apiName);
        requestData.put("apiSpecVersion", apiSpecVersion);
        requestData.put("timestamp", unixTimestamp);
        requestData.put("messageId", messageId);
        requestData.put("customerStatus", customerStatus);
        requestData.put("accessToken", accessToken);

        return requestData;
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
            status = 403;
            errorList.add(ErrorUtil.getErrorObject(ErrorConstants.AUErrorEnum.EXPECTED_GENERAL_ERROR, errorMessage,
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

    private static JSONObject getResourceFailureResponse(int errorCode, String errorMessage) {

        JSONObject errorData = new JSONObject();
        JSONArray errorList = new JSONArray();
        String errorResponse;
        int status;

        if (errorCode == 404) {
            status = ErrorConstants.AUErrorEnum.RESOURCE_NOT_FOUND.getHttpCode();
            errorList.add(ErrorUtil.getErrorObject(ErrorConstants.AUErrorEnum.RESOURCE_NOT_FOUND, errorMessage,
                    new CDSErrorMeta()));
            errorResponse = ErrorUtil.getErrorJson(errorList);
        } else if (errorCode == 422) {
            status = ErrorConstants.AUErrorEnum.RESOURCE_UNAVAILABLE_BODY.getHttpCode();
            errorList.add(ErrorUtil.getErrorObject(ErrorConstants.AUErrorEnum.RESOURCE_UNAVAILABLE_BODY, errorMessage,
                    new CDSErrorMeta()));
            errorResponse = ErrorUtil.getErrorJson(errorList);
        } else {
            status = errorCode;
            errorList.add(ErrorUtil.getErrorObject(ErrorConstants.AUErrorEnum.EXPECTED_GENERAL_ERROR, errorMessage,
                    new CDSErrorMeta()));
            errorResponse = ErrorUtil.getErrorJson(errorList);
        }
        errorData.put(GatewayConstants.STATUS_CODE, status);
        errorData.put(GatewayConstants.ERROR_RESPONSE, errorResponse);

        return errorData;
    }
}



