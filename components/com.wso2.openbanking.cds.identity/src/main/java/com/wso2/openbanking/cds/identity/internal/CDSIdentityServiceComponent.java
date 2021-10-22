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
 *
 */
package com.wso2.openbanking.cds.identity.internal;

import com.wso2.openbanking.cds.identity.authenticator.CDSArrangementPrivateKeyJWTClientAuthenticator;
import com.wso2.openbanking.cds.identity.authenticator.CDSTokenPrivateKeyJWTClientAuthenticator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.oauth2.client.authentication.OAuthClientAuthenticator;
import org.wso2.carbon.identity.oauth2.client.authentication.OAuthClientAuthnService;
import org.wso2.carbon.user.core.service.RealmService;


/**
 * Identity open banking common data holder
 */
@Component(
        name = "com.wso2.openbanking.cds.identity.internal.CDSIdentityServiceComponent",
        immediate = true
)
public class CDSIdentityServiceComponent {

    private static final Log log = LogFactory.getLog(CDSIdentityServiceComponent.class);

    @Activate
    protected void activate(ComponentContext context) {

        BundleContext bundleContext = context.getBundleContext();
        log.debug("Registering CDS related Identity services.");
        bundleContext.registerService(OAuthClientAuthenticator.class.getName(),
                new CDSArrangementPrivateKeyJWTClientAuthenticator(), null);
        bundleContext.registerService(OAuthClientAuthenticator.class.getName(),
                new CDSTokenPrivateKeyJWTClientAuthenticator(), null);
    }

    public static RealmService getRealmService() {
        return (RealmService) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                .getOSGiService(RealmService.class);
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
        CDSIdentityDataHolder.getInstance().setRealmService(realmService);
    }

    protected void unsetRealmService(RealmService realmService) {

        log.debug("UnSetting the Realm Service");
        CDSIdentityDataHolder.getInstance().setRealmService(null);
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {

        log.debug("Open banking CDS Identity Service Component is deactivated");
    }

    @Reference(name = "oauth.client.authn.service",
            service = OAuthClientAuthnService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetOAuthClientAuthnService"
    )
    protected void setOAuthClientAuthnService(OAuthClientAuthnService oAuthClientAuthnService) {
        CDSIdentityDataHolder.getInstance().setOAuthClientAuthnService(oAuthClientAuthnService);
    }

    protected void unsetOAuthClientAuthnService(OAuthClientAuthnService oAuthClientAuthnService) {
        CDSIdentityDataHolder.getInstance().setOAuthClientAuthnService(null);
    }
}
