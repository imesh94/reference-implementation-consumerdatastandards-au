/*
 * Copyright (c) 2023-2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.internal;

import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.metrics.periodic.job.HistoricMetricsCacheJob;
import com.wso2.openbanking.cds.metrics.periodic.scheduler.MetricsPeriodicJobScheduler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * Metrics Service Component
 */
@Component(
        name = "com.wso2.openbanking.cds.metrics.internal.MetricsServiceComponent",
        immediate = true
)
public class MetricsServiceComponent {
    private static final Log log = LogFactory.getLog(MetricsServiceComponent.class);
    private final OpenBankingCDSConfigParser configParser = OpenBankingCDSConfigParser.getInstance();

    @Activate
    protected void activate(ComponentContext context) {

        if (configParser.isMetricsPeriodicalJobEnabled()) {
            MetricsPeriodicJobScheduler.getInstance().initScheduler();
            log.debug("CDS Metrics periodic scheduler is initialized");

            // Cache historic metrics at server startup
            HistoricMetricsCacheJob job = new HistoricMetricsCacheJob();
            job.execute(null);
            log.debug("HistoricMetricsCacheJob executed at server startup");
        }
        log.debug("CDS Metrics bundle is activated");

    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        log.debug("CDS Metrics bundle is deactivated");
    }

    public static RealmService getRealmService() {
        return (RealmService) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                .getOSGiService(RealmService.class, null);
    }

    @Reference(
            name = "realm.service",
            service = RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRealmService"
    )
    protected void setRealmService(RealmService realmService) {

        log.debug("Setting the Realm Service");
        MetricsDataHolder.getInstance().setRealmService(realmService);
    }

    protected void unsetRealmService(RealmService realmService) {

        log.debug("UnSetting the Realm Service");
        MetricsDataHolder.getInstance().setRealmService(null);
    }

    @Reference(name = "api.manager.config.service",
            service = APIManagerConfigurationService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetAPIManagerConfigurationService"
    )
    protected void setAPIConfigurationService(APIManagerConfigurationService confService) {

        MetricsDataHolder.getInstance().setApiManagerConfigurationService(confService);
        log.debug("API manager configuration service bound to the CDS Metrics data holder");
    }

    protected void unsetAPIManagerConfigurationService(APIManagerConfigurationService amcService) {

        MetricsDataHolder.getInstance().setApiManagerConfigurationService(null);
        log.debug("API manager configuration service unbound from the CDS Metrics data holder");

    }

}
