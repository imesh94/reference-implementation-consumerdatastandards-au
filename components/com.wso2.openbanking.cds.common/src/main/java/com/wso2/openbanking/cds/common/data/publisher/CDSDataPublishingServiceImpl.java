/*
 * Copyright (c) 2021-2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 *
 */

package com.wso2.openbanking.cds.common.data.publisher;

import com.wso2.openbanking.accelerator.data.publisher.common.util.OBDataPublisherUtil;

import java.util.Map;

/**
 * CDS Data publishing service implementation.
 */
public class CDSDataPublishingServiceImpl implements CDSDataPublishingService {

    private static final String INPUT_STREAM_VERSION = "1.0.0";
    private static final String ACCESS_TOKEN_INPUT_STREAM = "AccessTokenInputStream";
    private static final String API_DATA_STREAM = "APIInputStream";
    private static final String CONSENT_INPUT_STREAM = "ConsentInputStream";
    private static final String API_LATENCY_INPUT_STREAM = "APILatencyInputStream";
    private static final String AUTHORISATION_METRICS_INPUT_STREAM = "AuthorisationMetricsInputStream";
    private static final String ABANDONED_CONSENT_FLOW_METRICS_INPUT_STREAM = "AbandonedConsentFlowMetricsInputStream";

    private static final CDSDataPublishingServiceImpl dataPublishingService = new CDSDataPublishingServiceImpl();

    public static CDSDataPublishingServiceImpl getInstance() {

        return dataPublishingService;

    }

    @Override
    public void publishApiInvocationData(Map<String, Object> apiInvocationData) {

        OBDataPublisherUtil.publishData(API_DATA_STREAM, INPUT_STREAM_VERSION, apiInvocationData);
    }

    @Override
    public void publishUserAccessTokenData(Map<String, Object> accessTokenData) {

        OBDataPublisherUtil.publishData(ACCESS_TOKEN_INPUT_STREAM, INPUT_STREAM_VERSION, accessTokenData);

    }

    @Override
    public void publishConsentData(Map<String, Object> consentData) {

        OBDataPublisherUtil.publishData(CONSENT_INPUT_STREAM, INPUT_STREAM_VERSION, consentData);

    }

    @Override
    public void publishApiLatencyData(Map<String, Object> apiLatencyData) {

        OBDataPublisherUtil.publishData(API_LATENCY_INPUT_STREAM, INPUT_STREAM_VERSION, apiLatencyData);
    }

    @Override
    public void publishAuthorisationData(Map<String, Object> authorisationData) {

        OBDataPublisherUtil.publishData(AUTHORISATION_METRICS_INPUT_STREAM, INPUT_STREAM_VERSION, authorisationData);
    }

    @Override
    public void publishAbandonedConsentFlowData(Map<String, Object> abandonedConsentFlowData) {

        OBDataPublisherUtil.publishData(ABANDONED_CONSENT_FLOW_METRICS_INPUT_STREAM, INPUT_STREAM_VERSION,
                abandonedConsentFlowData);
    }
}
