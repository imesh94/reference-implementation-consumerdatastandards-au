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
 * Model class for AuthorisationMetricDay.
 */
public class AuthorisationMetricDay {

    private LocalDate date;
    private AuthorisationMetric newAuthorisationMetric;
    private AuthorisationMetric revokedAuthorisationMetric;
    private AuthorisationMetric amendedAuthorisationMetric;
    private AuthorisationMetric expiredAuthorisationMetric;

    public AuthorisationMetricDay() {
        this.newAuthorisationMetric = new AuthorisationMetric();
        this.revokedAuthorisationMetric = new AuthorisationMetric();
        this.amendedAuthorisationMetric = new AuthorisationMetric();
        this.expiredAuthorisationMetric = new AuthorisationMetric();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public AuthorisationMetric getNewAuthorisationMetric() {
        return newAuthorisationMetric;
    }

    public void setNewAuthorisationMetric(AuthorisationMetric newAuthorisationMetric) {
        this.newAuthorisationMetric = newAuthorisationMetric;
    }

    public AuthorisationMetric getRevokedAuthorisationMetric() {
        return revokedAuthorisationMetric;
    }

    public void setRevokedAuthorisationMetric(AuthorisationMetric revokedAuthorisationMetric) {
        this.revokedAuthorisationMetric = revokedAuthorisationMetric;
    }

    public AuthorisationMetric getAmendedAuthorisationMetric() {
        return amendedAuthorisationMetric;
    }

    public void setAmendedAuthorisationMetric(AuthorisationMetric amendedAuthorisationMetric) {
        this.amendedAuthorisationMetric = amendedAuthorisationMetric;
    }

    public AuthorisationMetric getExpiredAuthorisationMetric() {
        return expiredAuthorisationMetric;
    }

    public void setExpiredAuthorisationMetric(AuthorisationMetric expiredAuthorisationMetric) {
        this.expiredAuthorisationMetric = expiredAuthorisationMetric;
    }
}
