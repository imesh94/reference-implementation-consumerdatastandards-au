/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.identity.metadata.periodical.updater;

import com.wso2.openbanking.cds.identity.metadata.periodical.updater.internal.MetaDataUpdate;
import com.wso2.openbanking.cds.identity.metadata.periodical.updater.internal.PeriodicalMetaDataUpdateJob;

/**
 * Interface exposed to trigger metadata cache update externally
 */
public class ExternalMetadataUpdater {

    private static volatile ExternalMetadataUpdater instance;

    private ExternalMetadataUpdater() {

    }

    public static ExternalMetadataUpdater getInstance() {

        if (instance == null) {
            synchronized (ExternalMetadataUpdater.class) {
                if (instance == null) {
                    instance = new ExternalMetadataUpdater();
                }
            }
        }
        return instance;
    }

    public void updateMetadata() {
        MetaDataUpdate updateInterface = new PeriodicalMetaDataUpdateJob();
        updateInterface.updateMetaDataValues();
    }
}
