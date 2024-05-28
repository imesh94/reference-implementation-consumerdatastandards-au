/*
 * Copyright (c) 2021-2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.service;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.metrics.model.MetricsResponseModel;
import com.wso2.openbanking.cds.metrics.util.PeriodEnum;

/**
 * Service class for CDS Admin metrics.
 */
public interface CDSMetricsService {

    /**
     * Return CDS operational statistics model.
     *
     * @param xV     - expected endpoint version
     * @param period - requested time period [CURRENT, HISTORIC, ALL]
     * @return ResponseMetricsListModel - model containing metrics data
     */
    MetricsResponseModel getMetrics(String xV, PeriodEnum period) throws OpenBankingException;

}
