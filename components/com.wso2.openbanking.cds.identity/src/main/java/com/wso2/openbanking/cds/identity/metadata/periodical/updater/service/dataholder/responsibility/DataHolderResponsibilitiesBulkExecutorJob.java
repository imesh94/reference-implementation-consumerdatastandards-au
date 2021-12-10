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
 *
 */

package com.wso2.openbanking.cds.identity.metadata.periodical.updater.service.dataholder.responsibility;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * DataHolderResponsibilitiesBulkExecutorJob
 * <p>
 * Invalidation of consents and cleanup of registrations can be executed as bulk operations.
 * This class is used to execute these as batch tasks performed overnight
 */
@DisallowConcurrentExecution
public class DataHolderResponsibilitiesBulkExecutorJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        DataHolderResponsibilitiesExecutor.getInstance().execute();
    }
}
