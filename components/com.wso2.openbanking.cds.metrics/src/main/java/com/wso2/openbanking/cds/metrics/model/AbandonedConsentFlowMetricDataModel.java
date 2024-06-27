/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.model;

import com.wso2.openbanking.cds.common.enums.AuthorisationStageEnum;
import net.minidev.json.JSONArray;

/**
 * Model class for AbandonedConsentFlowMetricDataModel.
 */
public class AbandonedConsentFlowMetricDataModel {

    private String requestUriKey;
    private AuthorisationStageEnum stage;
    private long timestamp;

    public AbandonedConsentFlowMetricDataModel() {
    }

    public AbandonedConsentFlowMetricDataModel(JSONArray jsonArray) {
        this.requestUriKey = (String) jsonArray.get(0);
        this.stage = AuthorisationStageEnum.fromValue((String) jsonArray.get(1));
        this.timestamp = (long) jsonArray.get(2);
    }

    public String getRequestUriKey() {
        return requestUriKey;
    }

    public void setRequestUriKey(String requestUriKey) {
        this.requestUriKey = requestUriKey;
    }

    public AuthorisationStageEnum getStage() {
        return stage;
    }

    public void setStage(AuthorisationStageEnum stage) {
        this.stage = stage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
