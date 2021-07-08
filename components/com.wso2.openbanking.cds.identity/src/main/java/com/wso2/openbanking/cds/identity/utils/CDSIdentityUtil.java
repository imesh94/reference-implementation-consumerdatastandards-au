/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.identity.utils;

import com.wso2.openbanking.accelerator.common.exception.ConsentManagementException;
import com.wso2.openbanking.accelerator.common.util.Generated;
import com.wso2.openbanking.accelerator.consent.mgt.service.impl.ConsentCoreServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth2.authz.OAuthAuthzReqMessageContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.Cookie;

/**
 * Util class to provide services identity module
 */
public class CDSIdentityUtil {

    private static Log log = LogFactory.getLog(CDSIdentityUtil.class);
    private static final String COMMON_AUTH_ID = "commonAuthId";
    private static final String SHARING_DURATION_VALUE = "sharing_duration_value";
    private static final String ZERO_SHARING_DURATION = "0";
    private static final String OB_CONSENT_ID_PREFIX = "OB_CONSENT_ID_";

    /**
     * method to retrieve the commonAuthId from the oauth message context.
     *
     * @param oAuthAuthzReqMessageContext
     * @return commonAuthId
     */
    public static String getCommonAuthId(OAuthAuthzReqMessageContext oAuthAuthzReqMessageContext) {

        Cookie[] cookies = oAuthAuthzReqMessageContext.getAuthorizationReqDTO().getCookie();
        String commonAuthId = StringUtils.EMPTY;
        ArrayList<Cookie> cookieList = new ArrayList<>(Arrays.asList(cookies));
        for (Cookie cookie : cookieList) {
            if (COMMON_AUTH_ID.equals(cookie.getName())) {
                commonAuthId = cookie.getValue();
                break;
            }
        }
        return commonAuthId;
    }

    /**
     * method to get the refresh token validity period.
     *
     * @param consentId consent Id
     * @return validity period for the refresh token
     */
    @Generated(message = "Excluding from code coverage since it requires a service call")
    public static long getRefreshTokenValidityPeriod(String consentId) {

        long sharingDuration = 0;
        if (StringUtils.isNotBlank(consentId)) {
            try {
                String sharingDurationValue = new ConsentCoreServiceImpl().getConsentAttributes(consentId)
                        .getConsentAttributes().get(SHARING_DURATION_VALUE);

                if (!ZERO_SHARING_DURATION.equals(sharingDurationValue)
                        && StringUtils.isNotBlank(sharingDurationValue)) {
                    sharingDuration = Long.parseLong(sharingDurationValue);
                }
            } catch (ConsentManagementException e) {
                log.error("Error while retrieving sharing duration. ", e);
            }
        }
        return sharingDuration;
    }

    /**
     * retrieve the consent id from the scopes.
     *
     * @param scopes array of scopes bound to the token
     * @return consent Id
     */
    public static String getConsentId(String[] scopes) {

        if (scopes != null && scopes.length > 0) {
            List<String> scopesList = new LinkedList<>(Arrays.asList(scopes));
            for (String scope : scopesList) {
                if (scope.startsWith(OB_CONSENT_ID_PREFIX)) {
                    return scope.split(OB_CONSENT_ID_PREFIX)[1];
                }
            }
        }
        return StringUtils.EMPTY;
    }

    public static String getConsentIdWithCommonAuthId(String commonAuthId) {
        String consentId;
        try {
            consentId = new ConsentCoreServiceImpl()
                    .getConsentIdByConsentAttributeNameAndValue(COMMON_AUTH_ID, commonAuthId).get(0);
        } catch (ConsentManagementException e) {
            log.error("Error occurred while retrieving consent id");
            return StringUtils.EMPTY;
        }
        return consentId;
    }
}
