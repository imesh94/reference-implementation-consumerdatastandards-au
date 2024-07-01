/*
 *  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 *  This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material contained
 *  herein is strictly forbidden, unless permitted by WSO2 in accordance with
 *  the WSO2 Commercial License available at http://wso2.com/licenses.
 *  For specific language governing the permissions and limitations under this
 *  license, please see the license as well as any agreement you’ve entered into
 *  with WSO2 governing the purchase of this software and any associated services.
 *
 */

package com.wso2.openbanking.cds.metrics.model;

import com.wso2.openbanking.cds.metrics.util.AspectEnum;

import java.util.Objects;

/**
 * Model class for server outages data for availability calculations.
 */
public class ServerOutageDataModel {

    private String outageId;
    private long timestamp;
    private String type;
    private long timeFrom;
    private long timeTo;
    private AspectEnum aspect;

    public ServerOutageDataModel(String outageId, long timestamp, String type, long timeFrom, long timeTo,
                                 AspectEnum aspect) {

        this.outageId = outageId;
        this.timestamp = timestamp;
        this.type = type;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.aspect = aspect;
    }

    public String getOutageId() {

        return outageId;
    }

    public void setOutageId(String outageId) {

        this.outageId = outageId;
    }

    public long getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(long timestamp) {

        this.timestamp = timestamp;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public long getTimeFrom() {

        return timeFrom;
    }

    public void setTimeFrom(long timeFrom) {

        this.timeFrom = timeFrom;
    }

    public long getTimeTo() {

        return timeTo;
    }

    public void setTimeTo(long timeTo) {

        this.timeTo = timeTo;
    }

    public AspectEnum getAspect() {
        return aspect;
    }

    public void setAspect(AspectEnum aspect) {
        this.aspect = aspect;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServerOutageDataModel dataModel = (ServerOutageDataModel) o;
        return timeFrom == dataModel.timeFrom && timeTo == dataModel.timeTo && Objects.equals(type, dataModel.type);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, timeFrom, timeTo);
    }
}
