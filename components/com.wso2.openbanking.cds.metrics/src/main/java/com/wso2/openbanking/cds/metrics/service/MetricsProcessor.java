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
 * Class containing methods for processing metrics data.
 */
public interface MetricsProcessor {

    /**
     * @param requestTime - time of request
     * @param period      - requested time period [CURRENT, HISTORIC, ALL]
     * @return - model containing metrics data
     * @throws OpenBankingException - OpenBankingException
     */
    ResponseMetricsListModel getResponseMetricsListModel(String requestTime, String period)
            throws OpenBankingException;

}
