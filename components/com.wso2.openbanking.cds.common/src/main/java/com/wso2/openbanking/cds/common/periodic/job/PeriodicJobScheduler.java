/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.common.periodic.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.wso2.carbon.utils.CarbonUtils;

/**
 * Periodic job scheduler class.
 * This class initialize the scheduler on osgi activation and schedule configured jobs and triggers.
 */
public class PeriodicJobScheduler {

    private static Log log = LogFactory.getLog(PeriodicJobScheduler.class);
    private static final String QUARTZ_PROPERTY_FILE = "quartz.properties";

    public void run() {

        try {
            // Load properties file if exists. Else scheduler factory will be initialized with default configs. Add
            // the properties file to set up clustering and to configure the job configuration file.
            // The jobs and triggers will be configured from the xml file.
            StdSchedulerFactory sf = new StdSchedulerFactory(
                    CarbonUtils.getCarbonConfigDirPath() + "/" + QUARTZ_PROPERTY_FILE);
            Scheduler scheduler = sf.getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            log.error("Exception while scheduling the job.", e);
        }
    }
}
