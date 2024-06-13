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
 * Model class for CustomerTypeCount.
 */
public class CustomerTypeCount {

    private int individual;
    private int nonIndividual;

    public CustomerTypeCount() {
        this.individual = 0;
        this.nonIndividual = 0;
    }

    public CustomerTypeCount(int individual, int nonIndividual) {
        this.individual = individual;
        this.nonIndividual = nonIndividual;
    }

    public int getIndividual() {
        return individual;
    }

    public void setIndividual(int individual) {
        this.individual = individual;
    }

    public int getNonIndividual() {
        return nonIndividual;
    }

    public void setNonIndividual(int nonIndividual) {
        this.nonIndividual = nonIndividual;
    }
}
