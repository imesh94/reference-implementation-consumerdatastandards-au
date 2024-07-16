/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.gateway.handlers;

import com.wso2.openbanking.cds.common.data.publisher.CDSDataPublishingService;
import com.wso2.openbanking.cds.gateway.utils.GatewayConstants;
import org.apache.axis2.context.MessageContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.rest.AbstractHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handler to publish data related to infoSec endpoints.
 */
public class InfoSecDataPublishingHandler extends AbstractHandler {

    private static final Log LOG = LogFactory.getLog(InfoSecDataPublishingHandler.class);
    private static final String REQUEST_IN_TIME = "REQUEST_IN_TIME";

    @Override
    public boolean handleRequest(org.apache.synapse.MessageContext messageContext) {

        // Record the request-in time to be used when calculating response latency for APILatency data publishing
        messageContext.setProperty(REQUEST_IN_TIME, System.currentTimeMillis());

        return true;
    }

    @Override
    public boolean handleResponse(org.apache.synapse.MessageContext messageContext) {

        String messageId = UUID.randomUUID().toString();

        // publish api endpoint invocation data
        Map<String, Object> requestData = generateInvocationDataMap(messageContext, messageId);
        CDSDataPublishingService.getCDSDataPublishingService().publishApiInvocationData(requestData);

        // publish api endpoint latency data
        Map<String, Object> latencyData = generateLatencyDataMap(messageContext, messageId);
        CDSDataPublishingService.getCDSDataPublishingService().publishApiLatencyData(latencyData);

        return true;
    }

    /**
     * Create the APIInvocation data map.
     *
     * @param messageContext - Message context
     * @param messageId      - Unique Id for the request
     * @return requestData Map
     */
    protected Map<String, Object> generateInvocationDataMap(org.apache.synapse.MessageContext messageContext,
                                                          String messageId) {

        Map<String, Object> requestData = new HashMap<>();

        MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
        Map headers = (Map) axis2MessageContext.getProperty(MessageContext.TRANSPORT_HEADERS);
        String contentLength = (String) headers.get(GatewayConstants.CONTENT_LENGTH);

        // consumerId is not required for metrics calculations, hence publishing as null
        requestData.put("consumerId", null);
        requestData.put("userAgent", getUserAgent(messageContext));
        requestData.put("statusCode", axis2MessageContext.getProperty(GatewayConstants.HTTP_SC));
        requestData.put("httpMethod", messageContext.getProperty(GatewayConstants.REST_METHOD));
        requestData.put("responsePayloadSize", contentLength != null ? Long.parseLong(contentLength) : 0);
        String[] apiData = getApiData((String) messageContext.getProperty(GatewayConstants.REST_API_CONTEXT));
        requestData.put("electedResource", apiData[0]);
        requestData.put("apiName", apiData[1]);
        // apiSpecVersion is not applicable to infoSec endpoints
        requestData.put("apiSpecVersion", null);
        requestData.put("timestamp", Instant.now().getEpochSecond());
        requestData.put("messageId", messageId);
        requestData.put("customerStatus", GatewayConstants.UNDEFINED);
        requestData.put("accessToken", null);
        return requestData;
    }

    /**
     * Create the APIInvocation Latency data map.
     *
     * @param messageContext - Message context
     * @param messageId      - Unique Id for the request
     * @return latencyData Map
     */
    protected Map<String, Object> generateLatencyDataMap(org.apache.synapse.MessageContext messageContext,
                                                       String messageId) {

        Map<String, Object> latencyData = new HashMap<>();
        long requestInTime = (long) messageContext.getProperty(REQUEST_IN_TIME);
        long requestLatency = System.currentTimeMillis() - requestInTime;

        latencyData.put("correlationId", messageId);
        latencyData.put("requestTimestamp", String.valueOf(Instant.now().getEpochSecond()));
        latencyData.put("backendLatency", 0L);
        latencyData.put("requestMediationLatency", 0L);
        latencyData.put("responseLatency", requestLatency >= 0 ? requestLatency : 0L);
        latencyData.put("responseMediationLatency", 0L);
        return latencyData;

    }

    private String[] getApiData(String context) {

        String[] apiData = new String[2];
        String apiName;
        switch (StringUtils.lowerCase(context)) {
            case GatewayConstants.TOKEN_ENDPOINT:
                apiName = GatewayConstants.TOKEN_API;
                break;
            case GatewayConstants.AUTHORIZE_ENDPOINT:
                apiName = GatewayConstants.AUTHORIZE_API;
                break;
            case GatewayConstants.USERINFO_ENDPOINT:
                apiName = GatewayConstants.USERINFO_API;
                break;
            case GatewayConstants.INTROSPECTION_ENDPOINT:
                apiName = GatewayConstants.INTROSPECT_API;
                break;
            case GatewayConstants.JWKS_ENDPOINT:
                apiName = GatewayConstants.JWKS_API;
                break;
            case GatewayConstants.REVOKE_ENDPOINT:
                apiName = GatewayConstants.TOKEN_REVOCATION_API;
                break;
            case GatewayConstants.WELL_KNOWN_ENDPOINT:
                apiName = GatewayConstants.WELL_KNOWN_API;
                break;
            case GatewayConstants.PAR_ENDPOINT:
                apiName = GatewayConstants.PAR_API;
                break;
            default:
                apiName = StringUtils.EMPTY;
        }
        apiData[0] = context;
        apiData[1] = apiName;
        return apiData;
    }

    /**
     * Extracts the user agent from the message context.
     *
     * @param messageContext - Message context
     * @return clientId
     */
    private String getUserAgent(org.apache.synapse.MessageContext messageContext) {

        MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
        Map headers = (Map) axis2MessageContext.getProperty(MessageContext.TRANSPORT_HEADERS);

        String userAgent;
        if (messageContext.getProperty(GatewayConstants.CLIENT_USER_AGENT) != null) {
            userAgent = (String) messageContext.getProperty(GatewayConstants.CLIENT_USER_AGENT);
        } else if (headers.get(GatewayConstants.CLIENT_USER_AGENT) != null) {
            userAgent = (String) headers.get(GatewayConstants.CLIENT_USER_AGENT);
        } else {
            userAgent = GatewayConstants.UNKNOWN;
        }

        return userAgent;
    }
}
