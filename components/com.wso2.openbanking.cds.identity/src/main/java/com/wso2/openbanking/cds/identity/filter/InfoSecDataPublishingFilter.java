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

package com.wso2.openbanking.cds.identity.filter;

import com.nimbusds.jwt.SignedJWT;
import com.wso2.openbanking.accelerator.common.config.OpenBankingConfigParser;
import com.wso2.openbanking.accelerator.data.publisher.common.constants.DataPublishingConstants;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonConstants;
import com.wso2.openbanking.cds.common.data.publisher.CDSDataPublishingService;
import com.wso2.openbanking.cds.identity.filter.constants.CDSFilterConstants;
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

/**
 * Tomcat filter to publish data related to infoSec endpoints
 * This filter should be added as the first filter of the filter chain
 * as the invocation latency data are calculated within this filter logic
 */
public class InfoSecDataPublishingFilter implements Filter {

    private static final Log LOG = LogFactory.getLog(InfoSecDataPublishingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {

            // Record the request-in time to be used when calculating response latency for APILatency data publishing
            request.setAttribute(CDSFilterConstants.REQUEST_IN_TIME, System.currentTimeMillis());
            chain.doFilter(request, response);

            // Publish the reporting data before returning the response
            publishReportingData((HttpServletRequest) request, (HttpServletResponse) response);
        }
    }

    /**
     * Publish reporting data related to infoSec endpoints
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    public void publishReportingData(HttpServletRequest request, HttpServletResponse response) {

        if (Boolean.parseBoolean((String) OpenBankingConfigParser.getInstance().getConfiguration()
                .get(DataPublishingConstants.DATA_PUBLISHING_ENABLED))) {

            String messageId = UUID.randomUUID().toString();

            // publish api endpoint invocation data
            Map<String, Object> requestData = generateInvocationDataMap(request, response, messageId);
            CDSDataPublishingService.getCDSDataPublishingService().publishApiInvocationData(requestData);

            // publish api endpoint latency data
            Map<String, Object> latencyData = generateLatencyDataMap(request, messageId);
            CDSDataPublishingService.getCDSDataPublishingService().publishApiLatencyData(latencyData);
        }
    }

    /**
     * Create the APIInvocation data map
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param messageId Unique Id for the request
     * @return requestData Map
     */
    public Map<String, Object> generateInvocationDataMap(HttpServletRequest request, HttpServletResponse response,
                                                         String messageId) {

        Map<String, Object> requestData = new HashMap<>();
        String contentLength = response.getHeader(CDSFilterConstants.CONTENT_LENGTH);

        requestData.put("consentId", null);
        requestData.put("clientId", extractClientId(request));
        // consumerId is not required for metrics calculations, hence publishing as null
        requestData.put("consumerId", null);
        requestData.put("userAgent", null);
        requestData.put("statusCode", response.getStatus());
        requestData.put("httpMethod", request.getMethod());
        requestData.put("responsePayloadSize", contentLength != null ? Long.parseLong(contentLength) : 0);
        String[] apiData = getApiData(request.getRequestURI());
        requestData.put("electedResource", apiData[0]);
        requestData.put("apiName", apiData[1]);
        // apiSpecVersion is not applicable to infoSec endpoints, hence publishing as null
        requestData.put("apiSpecVersion", null);
        requestData.put("timestamp", Instant.now().getEpochSecond());
        requestData.put("messageId", messageId);
        requestData.put("customerStatus", CDSFilterConstants.UNDEFINED);
        requestData.put("accessToken", null);
        return requestData;
    }

    /**
     * Create the APIInvocation Latency data map
     *
     * @param request HttpServletRequest
     * @param messageId Unique Id for the request
     * @return latencyData Map
     */
    public Map<String, Object> generateLatencyDataMap(HttpServletRequest request, String messageId) {

        Map<String, Object> latencyData = new HashMap<>();
        long requestInTime = (long) request.getAttribute(CDSFilterConstants.REQUEST_IN_TIME);
        long requestLatency = System.currentTimeMillis() - requestInTime;

        latencyData.put("correlationId", messageId);
        latencyData.put("requestTimestamp", String.valueOf(Instant.now().getEpochSecond()));
        latencyData.put("backendLatency", 0L);
        latencyData.put("requestMediationLatency", 0L);
        latencyData.put("responseLatency", requestLatency >= 0 ? requestLatency : 0L);
        latencyData.put("responseMediationLatency", 0L);
        return latencyData;

    }

    private String[] getApiData(String requestUri) {

        String[] apiData = new String[2];
        String apiName;
        String electedResource;
        switch(StringUtils.lowerCase(requestUri)) {
            case CDSFilterConstants.TOKEN_REQUEST_URI:
                electedResource = CDSFilterConstants.TOKEN_ENDPOINT;
                apiName = CDSFilterConstants.TOKEN_API;
                break;
            case CDSFilterConstants.AUTHORIZE_REQUEST_URI:
                electedResource = CDSFilterConstants.AUTHORIZE_ENDPOINT;
                apiName = CDSFilterConstants.AUTHORIZE_API;
                break;
            case CDSFilterConstants.USERINFO_REQUEST_URI:
                electedResource = CDSFilterConstants.USERINFO_ENDPOINT;
                apiName = CDSFilterConstants.USERINFO_API;
                break;
            case CDSFilterConstants.INTROSPECTION_REQUEST_URI:
                electedResource = CDSFilterConstants.INTROSPECTION_ENDPOINT;
                apiName = CDSFilterConstants.INTROSPECT_API;
                break;
            case CDSFilterConstants.JWKS_REQUEST_URI:
                electedResource = CDSFilterConstants.JWKS_ENDPOINT;
                apiName = CDSFilterConstants.JWKS_API;
                break;
            case CDSFilterConstants.REVOKE_REQUEST_URI:
                electedResource = CDSFilterConstants.REVOKE_ENDPOINT;
                apiName = CDSFilterConstants.TOKEN_REVOCATION_API;
                break;
            case CDSFilterConstants.WELL_KNOWN_REQUEST_URI:
                electedResource = CDSFilterConstants.WELL_KNOWN_ENDPOINT;
                apiName = CDSFilterConstants.WELL_KNOWN_API;
                break;
            case CDSFilterConstants.PAR_REQUEST_URI:
                electedResource = CDSFilterConstants.PAR_ENDPOINT;
                apiName = CDSFilterConstants.PAR_API;
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
     * @param request HttpServlet request containing the request data
     * @return clientId
     */
    private String extractClientId(HttpServletRequest request) {

        Optional<String> signedObject = Optional.ofNullable(request
                .getParameter(IdentityCommonConstants.OAUTH_JWT_ASSERTION));
        Optional<String> clientIdAsReqParam = Optional.ofNullable(request
                .getParameter(IdentityCommonConstants.CLIENT_ID));
        if (signedObject.isPresent()) {
            SignedJWT signedJWT = null;
            try {
                signedJWT = SignedJWT.parse(signedObject.get());
                return signedJWT.getJWTClaimsSet().getIssuer();
            } catch (ParseException e) {
                LOG.error("Invalid assertion found in the request", e);
            }
        } else if (clientIdAsReqParam.isPresent()) {
            return clientIdAsReqParam.get();
        }
        return null;
    }

    @Override
    public void destroy() {
    }

}
