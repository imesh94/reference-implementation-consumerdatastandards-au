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
import com.wso2.openbanking.accelerator.common.util.Generated;
import com.wso2.openbanking.cds.metrics.constants.MetricsConstants;
import com.wso2.openbanking.cds.metrics.model.ResponseMetricsListModel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implementation of CDS Admin metrics service.
 */
public class CDSMetricsServiceImpl implements CDSMetricsService {

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMetricsListModel getMetrics(String xV, String period, String xMinV) throws OpenBankingException {

        ResponseMetricsListModel responseMetricsListModel;
        MetricsProcessor metricsProcessor = getMetricsProcessor();
        String requestTime = new SimpleDateFormat(MetricsConstants.REQUEST_TIMESTAMP_PATTERN).format(new Date());
        responseMetricsListModel = metricsProcessor.getResponseMetricsListModel(requestTime, period);
        return responseMetricsListModel;
    }

    @Generated(message = "Excluding from code coverage")
    protected MetricsProcessor getMetricsProcessor() {

        return new MetricsProcessorImpl();
    }

}
