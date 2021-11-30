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

package com.wso2.openbanking.cds.metrics.service;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.metrics.model.ResponseMetricsListModel;

/**
 * Service class for CDS Admin metrics.
 */
public interface CDSMetricsService {

    /**
     * Return CDS operational statistics model.
     *
     * @param xV - expected endpoint version
     * @param period - requested time period [CURRENT, HISTORIC, ALL]
     * @param xMinV - minimum version
     * @return
     */
     ResponseMetricsListModel getMetrics(String xV, String period, String xMinV) throws OpenBankingException;

}
