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

package com.wso2.openbanking.cds.gateway.executors.reporting;

import com.wso2.openbanking.accelerator.gateway.executor.core.OpenBankingGatewayExecutor;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIRequestContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIResponseContext;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.common.utils.CDSCommonUtils;
import com.wso2.openbanking.cds.gateway.util.CDSDataPublishingConstants;

import java.util.Map;

/**
 * CDS common data reporting executor to populate additional data publishing elements.
 */
public class CDSCommonDataReportingExecutor implements OpenBankingGatewayExecutor {

    @Override
    public void preProcessRequest(OBAPIRequestContext obapiRequestContext) {

        // Add customer status to the analytics data by the presence of x-fapi-customer-ip-address header.
        // Customer status is undefined for infosec endpoints.
        Map<String, String> headers = obapiRequestContext.getMsgInfo().getHeaders();
        Object xFapiCustomerIpAddress = headers.get(CDSDataPublishingConstants.X_FAPI_CUSTOMER_IP_ADDRESS);
        String electedResource = obapiRequestContext.getMsgInfo().getElectedResource();
        String restApiContext = obapiRequestContext.getApiRequestInfo().getContext();
        String customerStatus;
        if (CDSDataPublishingConstants.INFOSEC_ENDPOINTS.contains(restApiContext) ||
                CDSDataPublishingConstants.INFOSEC_ENDPOINTS.contains(electedResource)) {
            customerStatus = CDSDataPublishingConstants.UNDEFINED;
        } else if (xFapiCustomerIpAddress == null) {
            customerStatus = CDSDataPublishingConstants.UNATTENDED;
        } else {
            customerStatus = CDSDataPublishingConstants.CUSTOMER_PRESENT;
        }

        // Add data publishing elements
        Map<String, Object> analyticsData = obapiRequestContext.getAnalyticsData();
        analyticsData.put(CDSDataPublishingConstants.CUSTOMER_STATUS, customerStatus);
        obapiRequestContext.setAnalyticsData(analyticsData);

    }

    @Override
    public void postProcessRequest(OBAPIRequestContext obapiRequestContext) {

        // Add access token to the analytics data.
        Map<String, String> headers = obapiRequestContext.getMsgInfo().getHeaders();
        String authorizationHeader = headers.get(CDSDataPublishingConstants.AUTHORIZATION);
        String accessToken = (authorizationHeader != null && authorizationHeader.split(" ").length > 1) ?
                authorizationHeader.split(" ")[1] : null;

        // Encrypt access token if configured.
        if (accessToken != null && OpenBankingCDSConfigParser.getInstance().isTokenEncryptionEnabled()) {
            accessToken = CDSCommonUtils.encryptAccessToken(accessToken);
        }
        // Add data publishing elements
        Map<String, Object> analyticsData = obapiRequestContext.getAnalyticsData();
        analyticsData.put(CDSDataPublishingConstants.ACCESS_TOKEN_ID, accessToken);
        obapiRequestContext.setAnalyticsData(analyticsData);
    }

    @Override
    public void preProcessResponse(OBAPIResponseContext obapiResponseContext) {

    }

    @Override
    public void postProcessResponse(OBAPIResponseContext obapiResponseContext) {

    }
}
