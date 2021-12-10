/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.metrics.endpoint.impl;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.metrics.endpoint.api.MetricsApi;
import com.wso2.openbanking.cds.metrics.endpoint.model.ResponseMetricsListDTO;
import com.wso2.openbanking.cds.metrics.endpoint.util.MappingUtil;
import com.wso2.openbanking.cds.metrics.model.ResponseMetricsListModel;
import com.wso2.openbanking.cds.metrics.service.CDSMetricsService;
import com.wso2.openbanking.cds.metrics.service.CDSMetricsServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.Response;

/**
 * Get Metrics
 *
 * This end point allows the ACCC to obtain operational statistics from the Data Holder on the operation of their
 * CDR compliant implementation. The statistics obtainable from this end point are determined by the non-functional
 * requirements for the CDR regime.
 */
public class MetricsApiImpl implements MetricsApi {

    private static final Log log = LogFactory.getLog(MetricsApiImpl.class);
    CDSMetricsService cdsMetricsServiceImpl = new CDSMetricsServiceImpl();
    private static final String XV_HEADER = "x-v";
    private static final String X_VERSION = "2";

    /**
     * Get Metrics
     * <p>
     * This end point allows the ACCC to obtain operational statistics from the Data Holder on the operation of their
     * CDR compliant implementation. The statistics obtainable from this end point are determined by the non-functional
     * requirements for the CDR regime.
     */
    public Response getMetrics(String xV, String period, String xMinV) {

        try {
            ResponseMetricsListModel metricsListModel = cdsMetricsServiceImpl.getMetrics(xV, period, xMinV);
            ResponseMetricsListDTO metricsListDTO = MappingUtil.getResponseMetricsListDTO(metricsListModel, period);
            return Response.ok().entity(metricsListDTO).header(XV_HEADER, X_VERSION).build();
        } catch (OpenBankingException e) {
            log.error("Error occurred while computing metrics.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error occurred while computing " +
                    "metrics").header(XV_HEADER, X_VERSION).build();
        }

    }

}

