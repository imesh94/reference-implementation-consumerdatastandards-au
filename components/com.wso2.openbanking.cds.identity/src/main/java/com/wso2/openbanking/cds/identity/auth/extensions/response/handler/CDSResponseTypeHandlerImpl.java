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
package com.wso2.openbanking.cds.identity.auth.extensions.response.handler;

import com.wso2.openbanking.accelerator.identity.auth.extensions.response.handler.OBResponseTypeHandler;
import com.wso2.openbanking.cds.common.utils.CommonConstants;
import com.wso2.openbanking.cds.identity.utils.CDSIdentityUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth2.authz.OAuthAuthzReqMessageContext;

import java.util.Arrays;

/**
 * ResponseType Handler implementation for CDS specification
 */
public class CDSResponseTypeHandlerImpl implements OBResponseTypeHandler {

    private static final Log log = LogFactory.getLog(CDSResponseTypeHandlerImpl.class);

    @Override
    public String[] updateApprovedScopes(OAuthAuthzReqMessageContext oAuthAuthzReqMessageContext) {

        if (oAuthAuthzReqMessageContext != null && oAuthAuthzReqMessageContext.getAuthorizationReqDTO() != null) {

            String[] scopes = oAuthAuthzReqMessageContext.getApprovedScope();
            String commonAuthId = CDSIdentityUtil.getCommonAuthId(oAuthAuthzReqMessageContext);
            if (!Arrays.asList(scopes).contains("consentmgt") && StringUtils.isNotBlank(commonAuthId)) {

                String consentId = "DummyConsentId";
                String consentScope = CommonConstants.OB_CONSENT_ID_PREFIX + consentId;
                String[] updatedScopes = (String[]) ArrayUtils.addAll(scopes, new String[]{consentScope});

                if (log.isDebugEnabled()) {
                    log.debug("Updated scopes: " + Arrays.toString(updatedScopes));
                }
                return updatedScopes;
            }
            if (StringUtils.isEmpty(commonAuthId)) {
                log.error("Failed to update scopes.");
            }
        } else {
            return new String[0];
        }
        return oAuthAuthzReqMessageContext.getApprovedScope();
    }

    @Override
    public long updateRefreshTokenValidityPeriod(OAuthAuthzReqMessageContext oAuthAuthzReqMessageContext) {

        String commonAuthId = CDSIdentityUtil.getCommonAuthId(oAuthAuthzReqMessageContext);
        if (StringUtils.isNotBlank(commonAuthId)) {
            String consentId = "DummyConsentId";
            long sharingDuration = CDSIdentityUtil.getRefreshTokenValidityPeriod(consentId);
            if (sharingDuration != 0) {
                return sharingDuration;
            }
        }
        return oAuthAuthzReqMessageContext.getRefreshTokenvalidityPeriod();
    }
}
