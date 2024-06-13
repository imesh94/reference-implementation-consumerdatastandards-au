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
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * ResponseMetricsListV5 DTO class.
 */
public class ResponseMetricsListV5DTO {

    @ApiModelProperty(required = true, value = "")
    @Valid
    private ResponseMetricsListV5DataDTO data = null;

    @ApiModelProperty(required = true, value = "")
    @Valid
    private LinksDTO links = null;

    @ApiModelProperty(value = "")
    @Valid
    private Object meta = null;

    /**
     * Get data
     *
     * @return data
     **/
    @JsonProperty("data")
    @NotNull
    public ResponseMetricsListV5DataDTO getData() {
        return data;
    }

    public void setData(ResponseMetricsListV5DataDTO data) {
        this.data = data;
    }

    public ResponseMetricsListV5DTO data(ResponseMetricsListV5DataDTO data) {
        this.data = data;
        return this;
    }

    /**
     * Get links
     *
     * @return links
     **/
    @JsonProperty("links")
    @NotNull
    public LinksDTO getLinks() {
        return links;
    }

    public void setLinks(LinksDTO links) {
        this.links = links;
    }

    public ResponseMetricsListV5DTO links(LinksDTO links) {
        this.links = links;
        return this;
    }

    /**
     * Get meta
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

    public ResponseMetricsListV5DTO meta(Object meta) {
        this.meta = meta;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ResponseMetricsListV5DTO {\n");

        sb.append("    data: ").append(toIndentedString(data)).append("\n");
        sb.append("    links: ").append(toIndentedString(links)).append("\n");
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

