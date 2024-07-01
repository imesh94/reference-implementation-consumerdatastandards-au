/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.endpoint.mapper;

import com.wso2.openbanking.cds.metrics.endpoint.model.v5.ResponseMetricsListV5DTO;
import com.wso2.openbanking.cds.metrics.model.MetricsResponseModel;

/**
 * Mapper to map MetricsResponseModel to Metrics API Response DTO.
 */
public interface MetricsMapper {

    /**
     * Map the MetricsResponseModel to ResponseMetricsListV5DTO.
     *
     * @param metricsListModel - Metrics model used in the service layer
     * @return - Specification supported DTO
     */
    ResponseMetricsListV5DTO getResponseMetricsListDTO(MetricsResponseModel metricsListModel, String period);

}
