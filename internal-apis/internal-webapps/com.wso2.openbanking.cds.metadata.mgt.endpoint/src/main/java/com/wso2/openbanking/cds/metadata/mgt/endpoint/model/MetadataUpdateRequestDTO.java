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

package com.wso2.openbanking.cds.metadata.mgt.endpoint.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 *  MetaData Update Request DTO
 */
public class MetadataUpdateRequestDTO {
    @ApiModelProperty(required = true, value = "")
    @Valid
    private MetadataUpdateDataDTO data;

    @ApiModelProperty(value = "")
    @Valid
    private Object meta;

    /**
     * Get data.
     *
     * @return data
     **/
    @JsonProperty("data")
    @NotNull
    public MetadataUpdateDataDTO getData() {
        return data;
    }

    public void setData(MetadataUpdateDataDTO data) {
        this.data = data;
    }

    public MetadataUpdateRequestDTO data(MetadataUpdateDataDTO data) {
        this.data = data;
        return this;
    }

    /**
     * Get meta.
     *
     * @return meta
     **/
    @JsonProperty("meta")
    public Object getMeta() {
        return meta;
    }

    public void setMeta(Object meta) {
        this.meta = meta;
    }

    public MetadataUpdateRequestDTO meta(Object meta) {
        this.meta = meta;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RequestMetaDataUpdateDTO {\n");

        sb.append("    data: ").append(toIndentedString(data)).append("\n");
        sb.append("    meta: ").append(toIndentedString(meta)).append("\n");
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
