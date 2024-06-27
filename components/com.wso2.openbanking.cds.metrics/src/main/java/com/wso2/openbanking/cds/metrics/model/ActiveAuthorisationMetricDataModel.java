/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.model;

import com.wso2.openbanking.cds.common.enums.ConsentDurationTypeEnum;
import com.wso2.openbanking.cds.common.enums.ConsentStatusEnum;
import net.minidev.json.JSONArray;

/**
 * Model class for ActiveAuthorisationMetricDataModel.
 */
public class ActiveAuthorisationMetricDataModel {

    private String consentId;
    private ConsentStatusEnum consentStatus;
    private String customerProfile;
    private ConsentDurationTypeEnum consentDurationType;
    private long timestamp;

    public ActiveAuthorisationMetricDataModel() {
    }

    public ActiveAuthorisationMetricDataModel(JSONArray jsonArray) {
        this.consentId = (String) jsonArray.get(0);
        this.consentStatus = ConsentStatusEnum.fromValue((String) jsonArray.get(1));
        this.customerProfile = (String) jsonArray.get(2);
        this.consentDurationType = ConsentDurationTypeEnum.fromValue((String) jsonArray.get(3));
        this.timestamp = (long) jsonArray.get(4);
    }

    public String getConsentId() {
        return consentId;
    }

    public void setConsentId(String consentId) {
        this.consentId = consentId;
    }

    public ConsentStatusEnum getConsentStatus() {
        return consentStatus;
    }

    public void setConsentStatus(ConsentStatusEnum consentStatus) {
        this.consentStatus = consentStatus;
    }

    public String getCustomerProfile() {
        return customerProfile;
    }

    public void setCustomerProfile(String customerProfile) {
        this.customerProfile = customerProfile;
    }

    public ConsentDurationTypeEnum getConsentDurationType() {
        return consentDurationType;
    }

    public void setConsentDurationType(ConsentDurationTypeEnum consentDurationType) {
        this.consentDurationType = consentDurationType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
