package com.wso2.openbanking.cds.metrics.scheduler.internal;

import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.common.periodic.job.PeriodicJobScheduler;
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
 * Metrics API Service Component
 */
@Component(
        name = "com.wso2.openbanking.cds.metrics.scheduler.internal.MetricsApiServiceComponent",
        immediate = true
)
public class MetricsApiServiceComponent {
    private static Log log = LogFactory.getLog(MetricsApiServiceComponent.class);
    private OpenBankingCDSConfigParser configParser = OpenBankingCDSConfigParser.getInstance();

    @Activate
    protected void activate(ComponentContext context) {

        if (configParser.isMetricsPeriodicalJobEnabled()) {
            new PeriodicJobScheduler().run();
            log.debug("Periodic Task Manager bundle is activated");
        }

    }

    @Deactivate
    protected void deactivate(ComponentContext context) {

        log.debug("Periodic Task Manager bundle is deactivated");
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
        MetricsApiSchedulerDataHolder.getInstance().setRealmService(realmService);
    }

    protected void unsetRealmService(RealmService realmService) {

        log.debug("UnSetting the Realm Service");
        MetricsApiSchedulerDataHolder.getInstance().setRealmService(null);
    }

    @Reference(name = "api.manager.config.service",
            service = APIManagerConfigurationService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetAPIManagerConfigurationService"
    )
    protected void setAPIConfigurationService(APIManagerConfigurationService confService) {

        MetricsApiSchedulerDataHolder.getInstance().setApiManagerConfigurationService(confService);
        log.debug("API manager configuration service bound to the CDS Admin Mgt data holder");
    }

    protected void unsetAPIManagerConfigurationService(APIManagerConfigurationService amcService) {

        MetricsApiSchedulerDataHolder.getInstance().setApiManagerConfigurationService(null);
        log.debug("API manager configuration service unbound from the CDS Admin Mgt data holder");

    }

}
