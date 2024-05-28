/*
 * Copyright (c) 2021-2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.endpoint.impl;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.common.error.handling.models.CDSErrorMeta;
import com.wso2.openbanking.cds.common.error.handling.util.ErrorConstants;
import com.wso2.openbanking.cds.common.error.handling.util.ErrorUtil;
import com.wso2.openbanking.cds.metrics.endpoint.api.MetricsApi;
import com.wso2.openbanking.cds.metrics.endpoint.mapper.MetricsMapper;
import com.wso2.openbanking.cds.metrics.endpoint.mapper.MetricsV3MapperImpl;
import com.wso2.openbanking.cds.metrics.endpoint.model.ResponseMetricsListDTO;
import com.wso2.openbanking.cds.metrics.model.MetricsResponseModel;
import com.wso2.openbanking.cds.metrics.service.CDSMetricsService;
import com.wso2.openbanking.cds.metrics.service.CDSMetricsServiceImpl;
import com.wso2.openbanking.cds.metrics.util.PeriodEnum;
import net.minidev.json.JSONArray;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import javax.ws.rs.core.Response;

/**
 * Implementation of the Metrics API
 */
public class MetricsApiImpl implements MetricsApi {

    private static final Log log = LogFactory.getLog(MetricsApiImpl.class);
    CDSMetricsService cdsMetricsServiceImpl = new CDSMetricsServiceImpl();
    MetricsMapper metricsMapper = new MetricsV3MapperImpl();
    private static final String XV_HEADER = "x-v";
    private static final String[] SUPPORTED_X_VERSIONS = {"3"};

    /**
     * {@inheritDoc}
     */
    public Response getMetrics(String xV, String period, String xMinV) {

        if (!Arrays.asList(SUPPORTED_X_VERSIONS).contains(xV)) {
            log.error("Error occurred due to request API version mismatch.");
            JSONArray errorList = new JSONArray();
            errorList.add(ErrorUtil.getErrorObject(ErrorConstants.AUErrorEnum.UNSUPPORTED_VERSION,
                    "Requested x-v version is not supported", new CDSErrorMeta()));
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(ErrorUtil.getErrorJson(errorList)).build();
        }
        PeriodEnum periodEnum = PeriodEnum.fromString(period);

        try {
            MetricsResponseModel metricsListModel = cdsMetricsServiceImpl.getMetrics(xV, periodEnum);
            ResponseMetricsListDTO metricsListDTO = metricsMapper.getResponseMetricsListDTO(metricsListModel, period);
            return Response.ok().entity(metricsListDTO).header(XV_HEADER, xV).build();
        } catch (OpenBankingException e) {
            log.error("Error occurred while computing metrics.", e);
            JSONArray errorList = new JSONArray();
            errorList.add(ErrorUtil.getErrorObject(ErrorConstants.AUErrorEnum.UNEXPECTED_ERROR,
                    "Unexpected error occurred while calculating metrics", new CDSErrorMeta()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ErrorUtil.getErrorJson(errorList))
                    .header(XV_HEADER, xV).build();
        }
    }

}

