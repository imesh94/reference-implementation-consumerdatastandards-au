/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.demo.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RequestAccountIdsData.
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen",
        date = "2019-08-16T11:28:45.176Z")
public class RequestAccountIdsData {

    @JsonProperty("accountIds")
    private List<String> accountIds = new ArrayList<String>();

    public RequestAccountIdsData accountIds(List<String> accountIds) {

        this.accountIds = accountIds;
        return this;
    }

    public RequestAccountIdsData addAccountIdsItem(String accountIdsItem) {

        this.accountIds.add(accountIdsItem);
        return this;
    }

    /**
     * Get accountIds
     *
     * @return accountIds
     **/
    @JsonProperty("accountIds")
    @ApiModelProperty(required = true, value = "")

    public List<String> getAccountIds() {

        return accountIds;
    }

    public void setAccountIds(List<String> accountIds) {

        this.accountIds = accountIds;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestAccountIdsData requestAccountIdsData = (RequestAccountIdsData) o;
        return Objects.equals(this.accountIds, requestAccountIdsData.accountIds);
    }

    @Override
    public int hashCode() {

        return Objects.hash(accountIds);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        sb.append("    accountIds: ").append(toIndentedString(accountIds)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {

        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

