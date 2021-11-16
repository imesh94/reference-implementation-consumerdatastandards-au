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
import com.wso2.openbanking.cds.common.utils.CDSCommonUtils;
import com.wso2.openbanking.cds.gateway.util.CDSDataPublishingConstants;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
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

    private static final String errorCode = "ERROR_CODE";
    private static final Log log = LogFactory.getLog(GatewayErrorMediator.class);

    @Override
    public boolean mediate(MessageContext messageContext) {

        // Publish gateway error data.
        if (Boolean.parseBoolean((String) OpenBankingConfigParser.getInstance().getConfiguration()
                .get(DataPublishingConstants.DATA_PUBLISHING_ENABLED))) {

            log.debug("Publishing invocation error data from CDS error mediator.");
            if ((messageContext.getProperty(errorCode)) != null) {
                Map<String, Object> invocationErrorData = getApiInvocationErrorDataToPublish(messageContext);
                CDSDataPublishingService.getCDSDataPublishingService().publishApiInvocationData(invocationErrorData);
            }
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
        if (messageContext.getProperty(CDSDataPublishingConstants.CLIENT_USER_AGENT) != null) {
            userAgent = (String) messageContext.getProperty(CDSDataPublishingConstants.CLIENT_USER_AGENT);
        } else if (headers.get(CDSDataPublishingConstants.CLIENT_USER_AGENT) != null) {
            userAgent = (String) headers.get(CDSDataPublishingConstants.CLIENT_USER_AGENT);
        } else {
            userAgent = CDSDataPublishingConstants.UNKNOWN;
        }

        String restApiContext = (String) messageContext.getProperty(CDSDataPublishingConstants.REST_API_CONTEXT);
        String customerStatus;
        if (CDSDataPublishingConstants.INFOSEC_ENDPOINTS.contains(restApiContext) ||
                CDSDataPublishingConstants.INFOSEC_ENDPOINTS.contains(electedResource)) {
            customerStatus = CDSDataPublishingConstants.UNDEFINED;
        } else if (headers.get(CDSDataPublishingConstants.X_FAPI_CUSTOMER_IP_ADDRESS) == null) {
            customerStatus = CDSDataPublishingConstants.UNATTENDED;
        } else {
            customerStatus = CDSDataPublishingConstants.CUSTOMER_PRESENT;
        }

        String consumerId = (String) axis2MessageContext.getProperty(CDSDataPublishingConstants.USER_NAME);
        String clientId = (String) axis2MessageContext.getProperty(CDSDataPublishingConstants.CONSUMER_KEY);
        String httpMethod = (String) axis2MessageContext.getProperty(CDSDataPublishingConstants.HTTP_METHOD);
        String apiName = (String) axis2MessageContext.getProperty(CDSDataPublishingConstants.API_NAME);
        // Get api name from SYNAPSE_REST_API if not available in axis2 message context.
        if (apiName == null && messageContext.getProperty(CDSDataPublishingConstants.SYNAPSE_REST_API) != null) {
            apiName = (messageContext.getProperty(CDSDataPublishingConstants.SYNAPSE_REST_API).toString())
                            .split(":")[0];
        }

        String apiSpecVersion = null;
        if (messageContext.getProperty(CDSDataPublishingConstants.API_SPEC_VERSION) != null) {
            apiSpecVersion = (String) messageContext.getProperty(CDSDataPublishingConstants.API_SPEC_VERSION);
        }

        int errorCode = (int) messageContext.getProperty(GatewayErrorMediator.errorCode);
        String messageId = (String) messageContext.getProperty(CDSDataPublishingConstants.CORRELATION_ID);

        String authorizationHeader = (String) headers.get(CDSDataPublishingConstants.AUTHORIZATION);
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
        requestData.put("statusCode", errorCode);
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
}
