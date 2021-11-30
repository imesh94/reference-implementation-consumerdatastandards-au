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
 */

package com.wso2.openbanking.cds.metrics.util;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Util methods for manipulating time.
 */
public class DateTimeUtil {

    private DateTimeUtil() {
    }

    /**
     * Return number of days passed since the given timestamp.
     *
     * @param timestamp - timestamp in epoch.
     * @return
     */
    public static int getDaysAgo(long timestamp) {

        ZonedDateTime now = ZonedDateTime.now();
        Instant timeInstance = Instant.ofEpochSecond(timestamp);
        ZonedDateTime oldDate = ZonedDateTime.ofInstant(timeInstance, ZoneOffset.UTC);
        Duration duration = Duration.between(oldDate, now);
        return (int) duration.toDays();
    }
}
