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

import org.wso2.carbon.identity.oauth2.client.authentication.OAuthClientAuthnService;
import org.wso2.carbon.user.core.service.RealmService;


/**
 * Data Holder for Open Banking Common.
 */
public class CDSIdentityDataHolder {

    private static volatile CDSIdentityDataHolder instance = new CDSIdentityDataHolder();
    private RealmService realmService;
    private OAuthClientAuthnService oAuthClientAuthnService;

    private CDSIdentityDataHolder() {

    }

    public static CDSIdentityDataHolder getInstance() {

        if (instance == null) {
            synchronized (CDSIdentityDataHolder.class) {
                if (instance == null) {
                    instance = new CDSIdentityDataHolder();
                }
            }
        }
        return instance;
    }

    public RealmService getRealmService() {

        if (realmService == null) {
            throw new RuntimeException("Realm Service is not available. Component did not start correctly.");
        }
        return realmService;
    }

    void setRealmService(RealmService realmService) {

        this.realmService = realmService;
    }

    /**
     * Return OAuthClientAuthnService.
     *
     * @return OAuthClientAuthnService
     */
    public OAuthClientAuthnService getOAuthClientAuthnService() {
        return oAuthClientAuthnService;
    }

    /**
     * Set OAuthClientAuthnService.
     */
    public void setOAuthClientAuthnService(OAuthClientAuthnService oAuthClientAuthnService) {
        this.oAuthClientAuthnService = oAuthClientAuthnService;
    }
}
