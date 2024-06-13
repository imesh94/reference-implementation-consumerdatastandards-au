/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.endpoint.model.v5;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * The number of active ongoing authorisations
 **/
@ApiModel(description = "The number of active ongoing authorisations")
public class AuthorisationMetricsV2ActiveAuthorisationCountDTO {

    @ApiModelProperty(required = true, value = "Active ongoing authorisation count for individual customers")
    /**
     * Active ongoing authorisation count for individual customers
     **/
    private Integer individual;

    @ApiModelProperty(required = true, value = "Active ongoing authorisation count for non-individual customers")
    /**
     * Active ongoing authorisation count for non-individual customers
     **/
    private Integer nonIndividual;

    /**
     * Active ongoing authorisation count for individual customers
     *
     * @return individual
     **/
    @JsonProperty("individual")
    @NotNull
    public Integer getIndividual() {
        return individual;
    }

    public void setIndividual(Integer individual) {
        this.individual = individual;
    }

    public AuthorisationMetricsV2ActiveAuthorisationCountDTO individual(Integer individual) {
        this.individual = individual;
        return this;
    }

    /**
     * Active ongoing authorisation count for non-individual customers
     *
     * @return nonIndividual
     **/
    @JsonProperty("nonIndividual")
    @NotNull
    public Integer getNonIndividual() {
        return nonIndividual;
    }

    public void setNonIndividual(Integer nonIndividual) {
        this.nonIndividual = nonIndividual;
    }

    public AuthorisationMetricsV2ActiveAuthorisationCountDTO nonIndividual(Integer nonIndividual) {
        this.nonIndividual = nonIndividual;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AuthorisationMetricsV2ActiveAuthorisationCountDTO {\n");

        sb.append("    individual: ").append(toIndentedString(individual)).append("\n");
        sb.append("    nonIndividual: ").append(toIndentedString(nonIndividual)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private static String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

