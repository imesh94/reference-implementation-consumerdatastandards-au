/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.model;

import java.time.LocalDate;

/**
 * Model class for AbandonedConsentFlowByStageMetricDay.
 */
public class AbandonedConsentFlowByStageMetricDay {

    private LocalDate date;
    private int abandonedByPreIdentificationStageCount;
    private int abandonedByPreAuthenticationStageCount;
    private int abandonedByPreAccountSelectionStageCount;
    private int abandonedByPreAuthorisationStageCount;
    private int abandonedByRejectedStageCount;
    private int abandonedByFailedTokenExchangeStageCount;

    public AbandonedConsentFlowByStageMetricDay() {
        this.abandonedByPreIdentificationStageCount = 0;
        this.abandonedByPreAuthenticationStageCount = 0;
        this.abandonedByPreAccountSelectionStageCount = 0;
        this.abandonedByPreAuthorisationStageCount = 0;
        this.abandonedByRejectedStageCount = 0;
        this.abandonedByFailedTokenExchangeStageCount = 0;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getAbandonedConsentFlowCount() {
        return abandonedByPreIdentificationStageCount + abandonedByPreAuthenticationStageCount +
                abandonedByPreAccountSelectionStageCount + abandonedByPreAuthorisationStageCount +
                abandonedByRejectedStageCount + abandonedByFailedTokenExchangeStageCount;
    }

    public int getAbandonedByPreIdentificationStageCount() {
        return abandonedByPreIdentificationStageCount;
    }

    public void setAbandonedByPreIdentificationStageCount(int abandonedByPreIdentificationStageCount) {
        this.abandonedByPreIdentificationStageCount = abandonedByPreIdentificationStageCount;
    }

    public int getAbandonedByPreAuthenticationStageCount() {
        return abandonedByPreAuthenticationStageCount;
    }

    public void setAbandonedByPreAuthenticationStageCount(int abandonedByPreAuthenticationStageCount) {
        this.abandonedByPreAuthenticationStageCount = abandonedByPreAuthenticationStageCount;
    }

    public int getAbandonedByPreAccountSelectionStageCount() {
        return abandonedByPreAccountSelectionStageCount;
    }

    public void setAbandonedByPreAccountSelectionStageCount(int abandonedByPreAccountSelectionStageCount) {
        this.abandonedByPreAccountSelectionStageCount = abandonedByPreAccountSelectionStageCount;
    }

    public int getAbandonedByPreAuthorisationStageCount() {
        return abandonedByPreAuthorisationStageCount;
    }

    public void setAbandonedByPreAuthorisationStageCount(int abandonedByPreAuthorisationStageCount) {
        this.abandonedByPreAuthorisationStageCount = abandonedByPreAuthorisationStageCount;
    }

    public int getAbandonedByRejectedStageCount() {
        return abandonedByRejectedStageCount;
    }

    public void setAbandonedByRejectedStageCount(int abandonedByRejectedStageCount) {
        this.abandonedByRejectedStageCount = abandonedByRejectedStageCount;
    }

    public int getAbandonedByFailedTokenExchangeStageCount() {
        return abandonedByFailedTokenExchangeStageCount;
    }

    public void setAbandonedByFailedTokenExchangeStageCount(int abandonedByFailedTokenExchangeStageCount) {
        this.abandonedByFailedTokenExchangeStageCount = abandonedByFailedTokenExchangeStageCount;
    }
}
