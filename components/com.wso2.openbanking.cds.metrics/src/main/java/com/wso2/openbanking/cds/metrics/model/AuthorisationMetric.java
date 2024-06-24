/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.model;

/**
 * Model class for AuthorisationMetric.
 */
public class AuthorisationMetric {

    // Short-lived consents(consents with sharing duration less than 24 hours)
    private CustomerTypeCount onceOff;

    // Long-lived consents(consents with sharing duration more than 24 hours)
    private CustomerTypeCount ongoing;

    public AuthorisationMetric() {
        this.onceOff = new CustomerTypeCount();
        this.ongoing = new CustomerTypeCount();
    }

    public CustomerTypeCount getOnceOff() {
        return onceOff;
    }

    public void setOnceOff(CustomerTypeCount onceOff) {
        this.onceOff = onceOff;
    }

    public CustomerTypeCount getOngoing() {
        return ongoing;
    }

    public void setOngoing(CustomerTypeCount ongoing) {
        this.ongoing = ongoing;
    }
}
