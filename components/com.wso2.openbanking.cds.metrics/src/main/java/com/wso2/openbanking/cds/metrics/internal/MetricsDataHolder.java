/*
 * Copyright (c) 2023-2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.internal;

import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * Metrics Data Holder class
 */
public class MetricsDataHolder {
    private static final MetricsDataHolder instance = new MetricsDataHolder();
    private RealmService realmService;
    private APIManagerConfigurationService apiManagerConfigurationService;

    private MetricsDataHolder() {

    }

    public static MetricsDataHolder getInstance() {

        return instance;
    }

    public RealmService getRealmService() {

        if (realmService == null) {
            throw new RuntimeException("Realm Service is not available. Component did not start correctly.");
        }
        return realmService;
    }

    void setRealmService(RealmService realmService) {

        this.realmService = realmService;
    }

    /**
     * Return APIM configuration service.
     *
     * @return APIManagerConfigurationService
     */
    public APIManagerConfigurationService getApiManagerConfigurationService() {
        return apiManagerConfigurationService;
    }

    /**
     * Set APIM configuration service.
     */
    public void setApiManagerConfigurationService(APIManagerConfigurationService apiManagerConfigurationService) {
        this.apiManagerConfigurationService = apiManagerConfigurationService;
    }

}
