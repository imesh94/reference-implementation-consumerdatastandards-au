/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.consent.extensions.authorize.utils;

import org.wso2.carbon.context.PrivilegedCarbonContext;

/**
 * This class contains the common utility methods used for CDS Consent steps.
 */
public class CDSConsentCommonUtil {

    /**
     * Method to get the userId with tenant domain.
     *
     * @param userId
     * @return
     */
    public static String getUserIdWithTenantDomain(String userId) {

        String tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
        if (userId.endsWith(tenantDomain)) {
            return userId;
        } else {
            return userId + "@" + tenantDomain;
        }
    }

}
