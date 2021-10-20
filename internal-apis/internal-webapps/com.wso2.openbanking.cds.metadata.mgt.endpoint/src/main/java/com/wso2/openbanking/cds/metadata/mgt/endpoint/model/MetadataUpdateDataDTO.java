/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 *
 */

package com.wso2.openbanking.cds.metadata.mgt.endpoint.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * MetaData Update Request Data DTO
 */
public class MetadataUpdateDataDTO {
    @ApiModelProperty(required = true, value = "The action to take for the meta data. At the moment the only option " +
            "is REFRESH which requires the data holder to call the ACCC to refresh meta data as soon as practicable")
    /**
     * The action to take for the meta data. At the moment the only option is REFRESH which requires the data holder
     * to call the ACCC to refresh meta data as soon as practicable
     **/
    private ActionEnum action = ActionEnum.REFRESH;

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

    /**
     * The action to take for the meta data. At the moment the only option is REFRESH which requires the data holder to
     * call the ACCC to refresh meta data as soon as practicable
     *
     * @return action
     **/
    @JsonProperty("action")
    public String getAction() {
        if (action == null) {
            return null;
        }
        return action.value();
    }

    public void setAction(ActionEnum action) {
        this.action = action;
    }

    public MetadataUpdateDataDTO action(ActionEnum action) {
        this.action = action;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RequestMetaDataUpdateDataDTO {\n");

        sb.append("    action: ").append(toIndentedString(action)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Action Enum.
     */
    @XmlType(name = "ActionEnum")
    @XmlEnum(String.class)
    public enum ActionEnum {

        @XmlEnumValue("REFRESH") REFRESH("REFRESH");

        private final String value;

        ActionEnum(String v) {
            value = v;
        }

        public static ActionEnum fromValue(String value) {
            for (ActionEnum b : ActionEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}
