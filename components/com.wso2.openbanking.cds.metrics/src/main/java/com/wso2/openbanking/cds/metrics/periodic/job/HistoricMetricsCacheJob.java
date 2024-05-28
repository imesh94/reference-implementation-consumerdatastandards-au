/*
 * Copyright (c) 2023-2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.periodic.job;

import com.google.gson.Gson;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.metrics.cache.MetricsCache;
import com.wso2.openbanking.cds.metrics.constants.MetricsConstants;
import com.wso2.openbanking.cds.metrics.internal.MetricsDataHolder;
import com.wso2.openbanking.cds.metrics.model.MetricsResponseModel;
import com.wso2.openbanking.cds.metrics.service.CDSMetricsServiceImpl;
import com.wso2.openbanking.cds.metrics.util.PeriodEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.user.api.TenantManager;
import org.wso2.carbon.user.api.UserStoreException;

/**
 * Quartz job to cache historic metrics to improve Metrics API performance.
 * This job should be scheduled to run daily before the Metrics API is called by the CDR Register.
 */
@DisallowConcurrentExecution
public class HistoricMetricsCacheJob implements Job {

    private static final Log log = LogFactory.getLog(HistoricMetricsCacheJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {

        log.info("Executing historic metrics cache scheduler job");
        try {
            // Set tenant domain if already not defined.
            TenantManager tenantManager = MetricsDataHolder.getInstance().getRealmService().getTenantManager();
            final String tenantDomain = tenantManager.getSuperTenantDomain();
            final int tenantId = tenantManager.getTenantId(tenantDomain);

            if (CarbonContext.getThreadLocalCarbonContext().getTenantDomain() == null) {
                PrivilegedCarbonContext privilegedCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                privilegedCarbonContext.setTenantDomain(tenantDomain);
                privilegedCarbonContext.setTenantId(tenantId);
            }
            cacheHistoricMetrics();
            log.info("Historic metrics cache scheduler job execution complete");
        } catch (UserStoreException e) {
            log.error("Exception while retrieving the tenant domain and tenant ID from RealmService", e);
        }
    }

    /**
     * Method to cache historic metrics
     */
    public void cacheHistoricMetrics() {

        try {
            log.info("Caching historic metrics started");
            MetricsCache metricsCache = MetricsCache.getInstance();
            metricsCache.removeFromCache(MetricsCache.getHistoricMetricsCacheKey());
            CDSMetricsServiceImpl metricsService = new CDSMetricsServiceImpl();
            MetricsResponseModel metricsResponseModel = metricsService.getMetrics(MetricsConstants.METRICS_VERSION_3,
                    PeriodEnum.HISTORIC);
            Gson gson = new Gson();
            String responseMetricsV5ListModelJson = gson.toJson(metricsResponseModel);
            metricsCache.addToCache(MetricsCache.getHistoricMetricsCacheKey(), responseMetricsV5ListModelJson);
            log.info("Caching historic metrics completed successfully");
        } catch (OpenBankingException e) {
            log.error("Error occurred while caching historic metrics", e);
        }
    }
}
