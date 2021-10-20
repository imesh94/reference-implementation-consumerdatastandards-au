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

import com.wso2.openbanking.accelerator.consent.mgt.service.ConsentCoreService;
import com.wso2.openbanking.accelerator.consent.mgt.service.impl.ConsentCoreServiceImpl;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonHelper;

/**
 * Simple holder to store Services and Service Stubs.
 */
public class ServiceHolder {
    private static volatile ServiceHolder instance;

    private ConsentCoreService consentCoreService;
    private IdentityCommonHelper identityCommonHelper;

    private ServiceHolder() {
    }

    public static ServiceHolder getInstance() {

        if (instance == null) {
            synchronized (ServiceHolder.class) {
                if (instance == null) {
                    instance = new ServiceHolder();
                }
            }
        }
        return instance;
    }

    public ConsentCoreService getConsentCoreService() {

        if (consentCoreService == null) {
            consentCoreService = new ConsentCoreServiceImpl();
        }
        return consentCoreService;
    }

    public IdentityCommonHelper getIdentityCommonHelper() {
        if (identityCommonHelper == null) {
            this.identityCommonHelper = new IdentityCommonHelper();
        }
        return identityCommonHelper;
    }
}
