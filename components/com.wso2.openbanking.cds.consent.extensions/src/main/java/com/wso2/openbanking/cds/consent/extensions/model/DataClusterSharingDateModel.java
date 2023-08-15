/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.consent.extensions.model;

import java.sql.Timestamp;

/**
 * Model class for consent sharing date data.
 */
public class DataClusterSharingDateModel {
    private String dataCluster;
    private Timestamp sharingStartDate;
    private Timestamp lastSharedDate;

    public String getDataCluster() {
        return dataCluster;
    }

    public void setDataCluster(String dataCluster) {
        this.dataCluster = dataCluster;
    }

    public Timestamp getSharingStartDate() {
        if (sharingStartDate == null) {
            return null;
        } else {
            return (Timestamp) sharingStartDate.clone();
        }
    }

    public void setSharingStartDate(Timestamp sharingStartDate) {
        if (sharingStartDate == null) {
            this.sharingStartDate = null;
        } else {
            this.sharingStartDate = (Timestamp) sharingStartDate.clone();
        }
    }

    public Timestamp getLastSharedDate() {
        if (lastSharedDate == null) {
            return null;
        } else {
            return (Timestamp) lastSharedDate.clone();
        }
    }

    public void setLastSharedDate(Timestamp lastSharedDate) {
        if (lastSharedDate == null) {
            this.lastSharedDate = null;
        } else {
            this.lastSharedDate = (Timestamp) lastSharedDate.clone();
        }
    }
}
