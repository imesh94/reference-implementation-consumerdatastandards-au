/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.model;

import com.wso2.openbanking.cds.common.enums.AuthorisationFlowTypeEnum;
import com.wso2.openbanking.cds.common.enums.ConsentDurationTypeEnum;
import com.wso2.openbanking.cds.common.enums.ConsentStatusEnum;
import net.minidev.json.JSONArray;

/**
 * Model class for AuthorisationMetricDataModel.
 */
public class AuthorisationMetricDataModel {

    private long timestamp;
    private ConsentStatusEnum consentStatus;
    private AuthorisationFlowTypeEnum authFlowType;
    private String customerProfile;
    private ConsentDurationTypeEnum consentDurationType;
    private int count;

    public AuthorisationMetricDataModel() {
    }

    public AuthorisationMetricDataModel(JSONArray jsonArray) {
        this.timestamp = (long) jsonArray.get(0);
        this.consentStatus = ConsentStatusEnum.fromValue((String) jsonArray.get(1));
        this.authFlowType = AuthorisationFlowTypeEnum.fromValue((String) jsonArray.get(2));
        this.customerProfile = (String) jsonArray.get(3);
        this.consentDurationType = ConsentDurationTypeEnum.fromValue((String) jsonArray.get(4));
        this.count = (jsonArray.get(5) != null) ? (Integer) jsonArray.get(5) : 0;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ConsentStatusEnum getConsentStatus() {
        return consentStatus;
    }

    public void setConsentStatus(ConsentStatusEnum consentStatus) {
        this.consentStatus = consentStatus;
    }

    public AuthorisationFlowTypeEnum getAuthFlowType() {
        return authFlowType;
    }

    public void setAuthFlowType(AuthorisationFlowTypeEnum authFlowType) {
        this.authFlowType = authFlowType;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
