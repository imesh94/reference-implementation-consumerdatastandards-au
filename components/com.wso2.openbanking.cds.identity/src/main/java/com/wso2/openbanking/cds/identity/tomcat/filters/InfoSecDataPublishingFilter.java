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

package com.wso2.openbanking.cds.identity.tomcat.filters;

import com.nimbusds.jwt.SignedJWT;
import com.wso2.openbanking.accelerator.common.config.OpenBankingConfigParser;
import com.wso2.openbanking.accelerator.data.publisher.common.constants.DataPublishingConstants;
import com.wso2.openbanking.accelerator.identity.token.util.TokenFilterException;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonConstants;
import com.wso2.openbanking.cds.common.data.publisher.CDSDataPublishingService;
import com.wso2.openbanking.cds.identity.tomcat.filters.constants.InfoSecDataPublishingConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Tomcat filter to publish data related to infoSec endpoints
 * This filter should be added as the first filter of the filter chain
 * as the invocation latency data are calculated within this filter logic
 */
public class InfoSecDataPublishingFilter implements Filter {

    public static final Log LOG = LogFactory.getLog(InfoSecDataPublishingFilter.class);
    public static final String REQUEST_IN_TIME = "REQUEST_IN_TIME";
    public static final String UNDEFINED = "undefined";

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {

            ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) response);
            // Record the request-in time to be used when calculating response latency for APILatency data publishing
            request.setAttribute(REQUEST_IN_TIME, System.currentTimeMillis());

            chain.doFilter(request, response);
            // Publish the reporting data before returning the response
            publishReportingData((HttpServletRequest) request, responseWrapper);
        }
    }

    /**
     * Response wrapper class
     */
    public static class ResponseWrapper extends HttpServletResponseWrapper {

        public int contentLength;

        public ResponseWrapper(HttpServletResponse response) {

            super(response);
        }

        public int getContentLength() {

            return contentLength;
        }

        public void setContentLength(int length) {

            this.contentLength = length;
            super.setContentLength(length);
        }
    }

    /**
     * Publish reporting data related to infoSec endpoints
     *
     * @param request HttpServletRequest
     * @param responseWrapper ResponseWrapper
     */
    public void publishReportingData(HttpServletRequest request, ResponseWrapper responseWrapper) {

        if (Boolean.parseBoolean((String) OpenBankingConfigParser.getInstance().getConfiguration()
                .get(DataPublishingConstants.DATA_PUBLISHING_ENABLED))) {

            String messageId = UUID.randomUUID().toString();

            // publish api endpoint invocation data
            Map<String, Object> requestData = generateInvocationDataMap(request, responseWrapper, messageId);
            CDSDataPublishingService.getCDSDataPublishingService().publishApiInvocationData(requestData);

            // publish api endpoint latency data
            Map<String, Object> latencyData = generateLatencyDataMap(request, responseWrapper, messageId);
            CDSDataPublishingService.getCDSDataPublishingService().publishApiLatencyData(latencyData);
        }
    }

    /**
     * Create the APIInvocation data map
     *
     * @param request HttpServletRequest
     * @param responseWrapper ResponseWrapper
     * @param messageId Unique Id for the request
     * @return requestData Map
     */
    public Map<String, Object> generateInvocationDataMap(HttpServletRequest request, ResponseWrapper responseWrapper,
                                            String messageId) {

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("consentId", null);
        // consumerId is not required for metrics calculations, hence set as null
        requestData.put("consumerId", null);
        //need to check
        requestData.put("clientId", request.getParameter(IdentityCommonConstants.CLIENT_ID));
        requestData.put("userAgent", null);
        //need to check
        requestData.put("statusCode", responseWrapper.getStatus());
        requestData.put("httpMethod", request.getMethod());
        //need to check
        requestData.put("responsePayloadSize",
                Long.parseLong(String.valueOf(responseWrapper.getContentLength())));

        String[] apiData = getApiData(request.getRequestURI());
        requestData.put("electedResource", apiData[0]);
        requestData.put("apiName", apiData[1]);

        // apiSpecVersion is not applicable to infoSec endpoints, hence publishing as null
        requestData.put("apiSpecVersion", null);
        requestData.put("timestamp", Instant.now().getEpochSecond());
        requestData.put("messageId", messageId);
        requestData.put("customerStatus", UNDEFINED);
        requestData.put("accessToken", null);
        return requestData;
    }

    /**
     * Create the APIInvocation Latency data map
     *
     * @param request HttpServletRequest
     * @param responseWrapper ResponseWrapper
     * @param messageId Unique Id for the request
     * @return latencyData Map
     */
    public Map<String, Object> generateLatencyDataMap(HttpServletRequest request, ResponseWrapper responseWrapper,
                                                          String messageId) {

        Map<String, Object> latencyData = new HashMap<>();
        long requestInTime = (long) request.getAttribute(REQUEST_IN_TIME);
        long requestLatency = System.currentTimeMillis() - requestInTime;

        latencyData.put("correlationId", messageId);
        latencyData.put("requestTimestamp", String.valueOf(Instant.now().getEpochSecond()));
        latencyData.put("backendLatency", 0L);
        latencyData.put("requestMediationLatency", 0L);
        latencyData.put("responseLatency", requestLatency >= 0 ? requestLatency : 0L);
        latencyData.put("responseLatency", requestLatency);
        latencyData.put("responseMediationLatency", 0L);
        return latencyData;

    }

    private String[] getApiData(String requestUri) {

        String[] apiData = new String[2];
        String apiName;
        String electedResource;
        switch(StringUtils.lowerCase(requestUri)) {
            case InfoSecDataPublishingConstants.TOKEN_REQUEST_URI:
                electedResource = InfoSecDataPublishingConstants.TOKEN_ENDPOINT;
                apiName = InfoSecDataPublishingConstants.TOKEN_API;
                break;
            case InfoSecDataPublishingConstants.AUTHORIZE_REQUEST_URI:
               /* clientId = (String) messageContext.getProperty(AUTHORIZE_CONSUMER_KEY);
                oAuthConsumerAppDTO = OBIdentityUtil.getOAuthConsumerAppDTO(clientId);
                if (oAuthConsumerAppDTO != null) {
                    tppId = oAuthConsumerAppDTO.getUsername();
                }*/
                electedResource = InfoSecDataPublishingConstants.AUTHORIZE_ENDPOINT;
                apiName = InfoSecDataPublishingConstants.AUTHORIZE_API;
                break;
            case InfoSecDataPublishingConstants.USERINFO_REQUEST_URI:
                electedResource = InfoSecDataPublishingConstants.USERINFO_ENDPOINT;
                apiName = InfoSecDataPublishingConstants.USERINFO_API;
                break;
            case InfoSecDataPublishingConstants.INTROSPECTION_REQUEST_URI:
                electedResource = InfoSecDataPublishingConstants.INTROSPECTION_ENDPOINT;
                apiName = InfoSecDataPublishingConstants.INTROSPECT_API;
                break;
            case InfoSecDataPublishingConstants.JWKS_REQUEST_URI:
                electedResource = InfoSecDataPublishingConstants.JWKS_ENDPOINT;
                apiName = InfoSecDataPublishingConstants.JWKS_API;
                break;
            case InfoSecDataPublishingConstants.REVOKE_REQUEST_URI:
                // todo: check if this logic has any impact from arrangement revoke
                electedResource = InfoSecDataPublishingConstants.REVOKE_ENDPOINT;
                apiName = InfoSecDataPublishingConstants.TOKEN_REVOCATION_API;
                break;
            case InfoSecDataPublishingConstants.WELL_KNOWN_REQUEST_URI:
                electedResource = InfoSecDataPublishingConstants.WELL_KNOWN_ENDPOINT;
                apiName = InfoSecDataPublishingConstants.WELL_KNOWN_API;
                break;
            case InfoSecDataPublishingConstants.PAR_REQUEST_URI:
                electedResource = InfoSecDataPublishingConstants.PAR_ENDPOINT;
                apiName = InfoSecDataPublishingConstants.PAR_API;
                break;
            default:
                apiName = StringUtils.EMPTY;
                electedResource = requestUri;
        }
        apiData[0] = electedResource;
        apiData[1] = apiName;
        return apiData;
    }

    /**
     * Extracts the client id from the request parameter or from the assertion.
     *
     * @param request servlet request containing the request data
     * @return clientId
     * @throws ParseException
     */
    private String extractClientId(ServletRequest request) throws TokenFilterException {

        try {
            Optional<String> signedObject =
                    Optional.ofNullable(request.getParameter(IdentityCommonConstants.OAUTH_JWT_ASSERTION));
            Optional<String> clientIdAsReqParam =
                    Optional.ofNullable(request.getParameter(IdentityCommonConstants.CLIENT_ID));
            if (signedObject.isPresent()) {
                SignedJWT signedJWT = SignedJWT.parse(signedObject.get());
                return signedJWT.getJWTClaimsSet().getIssuer();
            } else if (clientIdAsReqParam.isPresent()) {
                return clientIdAsReqParam.get();
            } else {
                throw new TokenFilterException(HttpServletResponse.SC_BAD_REQUEST, "Client ID not retrieved",
                        "Unable to find client id in the request");
            }
        } catch (ParseException e) {
            throw new TokenFilterException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid assertion", "Error " +
                    "occurred while parsing the signed assertion", e);
        }
    }

    @Override
    public void destroy() {
    }

}
