/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.util;

/**
 * Enum for time formats.
 * Used
 */
public enum TimeGranularityEnum {

    SECONDS("seconds"),
    MINUTES("minutes"),
    HOURS("hours"),
    DAYS("days"),
    MONTHS("months"),
    YEARS("years");

    private final String text;

    TimeGranularityEnum(String value) {
        this.text = value;
    }

    public static TimeGranularityEnum fromString(String text) {

        for (TimeGranularityEnum durationEnum : TimeGranularityEnum.values()) {
            if (durationEnum.text.equalsIgnoreCase(text)) {
                return durationEnum;
            }
        }
        return null;
    }

    public String toString() {
        return String.valueOf(text);
    }

}
