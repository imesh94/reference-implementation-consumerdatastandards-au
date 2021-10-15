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

package com.wso2.openbanking.cds.gateway.executors.core;

import com.wso2.openbanking.accelerator.gateway.executor.core.AbstractRequestRouter;
import com.wso2.openbanking.accelerator.gateway.executor.core.OpenBankingGatewayExecutor;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIRequestContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIResponseContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Request Router for CDS API.
 */
public class CDSAPIRequestRouter extends AbstractRequestRouter {

    private static final List<OpenBankingGatewayExecutor> EMPTY_LIST = new ArrayList<>();

    /**
     * Get request executors
     *
     * @param requestContext - OBAPIRequestContext
     * @return Executor list
     */
    public List<OpenBankingGatewayExecutor> getExecutorsForRequest(OBAPIRequestContext requestContext) {

        if (RequestRouterConstants.API_TYPE_NON_REGULATORY
                .equals(requestContext.getOpenAPI().getExtensions().get(RequestRouterConstants.API_TYPE_CUSTOM_PROP))) {
            requestContext.addContextProperty(RequestRouterConstants.API_TYPE_CUSTOM_PROP,
                    RequestRouterConstants.API_TYPE_NON_REGULATORY);
            return EMPTY_LIST;
        } else if (RequestRouterConstants.API_TYPE_CONSENT
                .equals(requestContext.getOpenAPI().getExtensions().get(RequestRouterConstants.API_TYPE_CUSTOM_PROP))) {
            // Add support for consent management portal APIs
            requestContext.addContextProperty(RequestRouterConstants.API_TYPE_CUSTOM_PROP,
                    RequestRouterConstants.API_TYPE_CONSENT);
            return this.getExecutorMap().get(RequestRouterConstants.CONSENT);
        } else if (RequestRouterConstants.DCR_API_NAME.equals(requestContext.getOpenAPI().getInfo().getTitle())) {
            requestContext.addContextProperty(RequestRouterConstants.API_TYPE_CUSTOM_PROP,
                    RequestRouterConstants.API_TYPE_DCR);
            return this.getExecutorMap().get(RequestRouterConstants.DCR);
        } else if (RequestRouterConstants.CDS_API_NAME.equals(requestContext.getOpenAPI().getInfo().getTitle())) {
            requestContext.addContextProperty(RequestRouterConstants.API_TYPE_CUSTOM_PROP,
                    RequestRouterConstants.API_TYPE_CDS);
            return this.getExecutorMap().get(RequestRouterConstants.CDS);
        } else {
            return this.getExecutorMap().get(RequestRouterConstants.DEFAULT);
        }
    }

    /**
     * Get response executors
     *
     * @param responseContext - OBAPIResponseContext
     * @return Executor list
     */
    public List<OpenBankingGatewayExecutor> getExecutorsForResponse(OBAPIResponseContext responseContext) {

        List<OpenBankingGatewayExecutor> executorList = EMPTY_LIST;

        // Check for  x-wso2-api-type property
        if (responseContext.getContextProps().containsKey(RequestRouterConstants.API_TYPE_CUSTOM_PROP)) {
            switch (responseContext.getContextProps().get(RequestRouterConstants.API_TYPE_CUSTOM_PROP)) {
                case RequestRouterConstants.API_TYPE_NON_REGULATORY:
                    executorList = EMPTY_LIST;
                    break;
                case RequestRouterConstants.API_TYPE_CONSENT:
                    executorList = this.getExecutorMap().get(RequestRouterConstants.CONSENT);
                    break;
                case RequestRouterConstants.API_TYPE_DCR:
                    executorList = this.getExecutorMap().get(RequestRouterConstants.DCR);
                    break;
                case RequestRouterConstants.API_TYPE_CDS:
                    executorList = this.getExecutorMap().get(RequestRouterConstants.CDS);
                    break;
                default:
                    executorList = this.getExecutorMap().get(RequestRouterConstants.DEFAULT);
            }
        }

        return executorList;
    }

}