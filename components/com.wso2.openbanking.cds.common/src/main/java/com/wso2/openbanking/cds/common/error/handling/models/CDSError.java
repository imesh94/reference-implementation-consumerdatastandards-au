/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.common.error.handling.models;

/**
 * Builder class for building error in CDS.
 */
public class CDSError {

    private String code;
    private String title;
    private String detail;
    private CDSErrorMeta meta;

    public CDSError(Builder builder) {
        this.code = builder.code;
        this.title = builder.title;
        this.detail = builder.detail;
        this.meta = builder.meta;
    }

    public String getCode() {

        return code;
    }

    public String getTitle() {

        return title;
    }

    public String getDetail() {

        return detail;
    }

    public CDSErrorMeta getMeta() {

        return meta;
    }

    @Override
    public String toString() {
        return "AUErrorBuilder: code : " + this.code + ", title : " + this.title + ", detail : " + this.detail +
                ", meta :" + this.meta;
    }

    /**
     * Object builder class for AU Error.
     */
    public static final class Builder {

        private String code;
        private String title;
        private String detail;
        private CDSErrorMeta meta;

        public Builder withCode(String code) {
            this.code = code;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withDetail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder withMeta(CDSErrorMeta meta) {
            this.meta = meta;
            return this;
        }

        //Return the finally constructed Error object
        public CDSError build() {
            return new CDSError(this);
        }
    }
}
