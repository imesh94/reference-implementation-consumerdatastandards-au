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

package com.wso2.openbanking.cds.identity.metadata.periodical.updater.internal;

import com.wso2.openbanking.accelerator.service.activator.OBServiceObserver;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * MetadataScheduledTaskObserver
 * <p>
 * Service to initiate Metadata Cache updater Scheduled Task.
 */
public class MetadataScheduledTaskObserver implements OBServiceObserver {

    private static final Log LOG = LogFactory.getLog(MetadataScheduledTaskObserver.class);

    @Override
    public void activate() {
        if (OpenBankingCDSConfigParser.getInstance().isMetadataCacheEnabled()) {
            new MetadataUpdater().run();
            LOG.debug("Periodical metadata updater is activated");
        }
    }
}