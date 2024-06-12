/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 *
 */

package com.wso2.openbanking.cds.consent.extensions.authorize.worker;

import com.wso2.openbanking.accelerator.common.config.OpenBankingConfigParser;
import com.wso2.openbanking.accelerator.data.publisher.common.constants.DataPublishingConstants;
import com.wso2.openbanking.accelerator.identity.auth.extensions.adaptive.function.OpenBankingAuthenticationWorker;
import com.wso2.openbanking.cds.common.data.publisher.CDSDataPublishingService;
import com.wso2.openbanking.cds.common.utils.CDSCommonUtils;
import com.wso2.openbanking.cds.common.utils.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsServletRequest;

import java.util.Map;

/**
 * Authentication Worker implementation to publish abandoned consent flow data from the common auth script.
 */
public class AbandonedConsentFlowDataPublisherWorker implements OpenBankingAuthenticationWorker {

    private static final Log log = LogFactory.getLog(AbandonedConsentFlowDataPublisherWorker.class);

    @Override
    public JSONObject handleRequest(JsAuthenticationContext context, Map<String, String> map) {

        if (Boolean.parseBoolean((String) OpenBankingConfigParser.getInstance().getConfiguration()
                .get(DataPublishingConstants.DATA_PUBLISHING_ENABLED))) {

            String requestUri = getRequestUri(context);
            String requestUriKey = CDSCommonUtils.getRequestUriKey(requestUri);

            if (StringUtils.isBlank(requestUriKey)) {
                log.error("Request URI key not found.");
                return new JSONObject();
            }

            Map<String, Object> abandonedConsentFlowDataMap = CDSCommonUtils
                    .generateAbandonedConsentFlowDataMap(requestUriKey, null, map.get(CommonConstants.STAGE));

            CDSDataPublishingService.getCDSDataPublishingService()
                    .publishAbandonedConsentFlowData(abandonedConsentFlowDataMap);
        }

        return new JSONObject();
    }

    private String getRequestUri(JsAuthenticationContext context) {

        JsServletRequest jsServletRequest = (JsServletRequest) context.getMember(CommonConstants.REQUEST);
        String requestUri = jsServletRequest.getWrapped().getWrapped().getParameter(CommonConstants.REQUEST_URI);

        if (StringUtils.isNotBlank(requestUri)) {
            // Setting the retrieved request URI to the context since it is not available after the first
            // retrieval from the request
            context.getWrapped().setProperty(CommonConstants.REQUEST_URI, requestUri);
            return requestUri;
        }

        return (String) context.getWrapped().getProperty(CommonConstants.REQUEST_URI);
    }
}
