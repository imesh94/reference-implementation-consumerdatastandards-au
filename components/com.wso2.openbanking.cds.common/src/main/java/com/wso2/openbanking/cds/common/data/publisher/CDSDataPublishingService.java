/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.common.data.publisher;

import java.util.Map;

/**
 * Data Publishing Service for CDS.
 */
public interface CDSDataPublishingService {

    /**
     * Method to get the CDSDataPublishingService instance.
     *
     * @return An instance of CDSDataPublishingService
     */
    static CDSDataPublishingService getCDSDataPublishingService() {

        return CDSDataPublishingServiceImpl.getInstance();
    }

    /**
     * Method to publish API Invocation related data.
     *
     * @param apiInvocationData Map containing the data that needs to be published
     */
    void publishApiInvocationData(Map<String, Object> apiInvocationData);

    /**
     * Method to publish Access Token related data.
     *
     * @param accessTokenData Map containing the data that needs to be published
     */
    void publishUserAccessTokenData(Map<String, Object> accessTokenData);

    /**
     * Method to publish Consent related data.
     *
     * @param consentData Map containing the data that needs to be published
     */
    void publishConsentData(Map<String, Object> consentData);

    /**
     * Method to publish API latency related data.
     *
     * @param apiLatencyData Map containing the data that needs to be published
     */
    void publishApiLatencyData(Map<String, Object> apiLatencyData);

}
