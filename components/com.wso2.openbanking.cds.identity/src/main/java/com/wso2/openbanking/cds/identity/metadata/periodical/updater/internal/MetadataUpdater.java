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

import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.identity.metadata.periodical.updater.service.dataholder.responsibility.DataHolderResponsibilitiesBulkExecutorJob;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Scheduled Task definition and trigger to perform primary cache-update job every n minutes.
 */
class MetadataUpdater {

    private static final Log LOG = LogFactory.getLog(MetadataUpdater.class);
    private static final String JOB_1 = "PeriodicalMetaDataUpdateJob";
    private static final String JOB_2 = "DataHolderResponsibilitiesBulkExecutorJob";
    private static final String TRIGGER_1 = "PeriodicalMetaDataUpdateTrigger";
    private static final String TRIGGER_2 = "DataHolderResponsibilitiesBulkExecutorTrigger";
    private static final String GROUP_1 = "group1";
    private static final String GROUP_2 = "group2";

    void run() {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();

            // configuring periodical meta data update job
            JobDetail periodicalMetaDataUpdateJob = newJob(PeriodicalMetaDataUpdateJob.class)
                    .withIdentity(JOB_1, GROUP_1)
                    .build();

            Trigger periodicalMetaDataUpdateTrigger = newTrigger()
                    .withIdentity(TRIGGER_1, GROUP_1)
                    .withPriority(1)
                    .startNow()
                    .withSchedule(simpleSchedule().withIntervalInMinutes(OpenBankingCDSConfigParser.getInstance()
                            .getMetaDataCacheUpdatePeriodInMinutes()).repeatForever())
                    .build();

            scheduler.scheduleJob(periodicalMetaDataUpdateJob, periodicalMetaDataUpdateTrigger);

            if (OpenBankingCDSConfigParser.getInstance().isBulkOperation()) {
                // configuring data holder responsibilities execution
                JobDetail responsibilitiesBulkExecutorJob = newJob(DataHolderResponsibilitiesBulkExecutorJob.class)
                        .withIdentity(JOB_2, GROUP_2)
                        .build();

                Trigger responsibilitiesBulkExecutorTrigger = newTrigger()
                        .withIdentity(TRIGGER_2, GROUP_2)
                        .withPriority(2)
                        .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(OpenBankingCDSConfigParser.getInstance()
                                .getBulkExecutionHour(), 0))
                        .build();

                scheduler.scheduleJob(responsibilitiesBulkExecutorJob, responsibilitiesBulkExecutorTrigger);
            }
        } catch (SchedulerException e) {
            LOG.error("Error while creating and starting Metadata Update Scheduled Task", e);
        }
    }
}
