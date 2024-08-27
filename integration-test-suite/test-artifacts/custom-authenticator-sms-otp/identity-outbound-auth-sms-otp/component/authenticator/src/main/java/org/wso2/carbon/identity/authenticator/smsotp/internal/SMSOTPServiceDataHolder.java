/**
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.authenticator.smsotp.internal;

import org.osgi.framework.BundleContext;
import org.wso2.carbon.identity.event.services.IdentityEventService;
import org.wso2.carbon.identity.governance.IdentityGovernanceService;
import org.wso2.carbon.identity.handler.event.account.lock.service.AccountLockService;
import org.wso2.carbon.user.core.service.RealmService;

public class SMSOTPServiceDataHolder {

    private static volatile SMSOTPServiceDataHolder smsOTPServiceDataHolder = new SMSOTPServiceDataHolder();

    private BundleContext bundleContext;
    private IdentityEventService identityEventService;
    private RealmService realmService;
    private AccountLockService accountLockService;
    private IdentityGovernanceService identityGovernanceService;

    private SMSOTPServiceDataHolder(){

    }

    public static SMSOTPServiceDataHolder getInstance() {
        return smsOTPServiceDataHolder;
    }


    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public IdentityEventService getIdentityEventService() {
        return identityEventService;
    }

    public void setIdentityEventService(IdentityEventService identityEventService) {
        this.identityEventService = identityEventService;
    }

    public RealmService getRealmService() {
        return realmService;
    }

    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }

    public IdentityGovernanceService getIdentityGovernanceService() {

        if (identityGovernanceService == null) {
            throw new RuntimeException("IdentityGovernanceService not available. Component is not started properly.");
        }
        return identityGovernanceService;
    }

    public void setIdentityGovernanceService(IdentityGovernanceService identityGovernanceService) {

        this.identityGovernanceService = identityGovernanceService;
    }

    public AccountLockService getAccountLockService() {

        return accountLockService;
    }

    public void setAccountLockService(AccountLockService accountLockService) {

        this.accountLockService = accountLockService;
    }
}
