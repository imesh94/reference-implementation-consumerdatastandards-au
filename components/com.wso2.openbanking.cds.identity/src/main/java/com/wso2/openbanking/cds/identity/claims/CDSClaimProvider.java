/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.identity.claims;

import com.wso2.openbanking.accelerator.identity.claims.OBClaimProvider;
import com.wso2.openbanking.cds.identity.claims.utils.CDSClaimProviderUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth.cache.SessionDataCache;
import org.wso2.carbon.identity.oauth.cache.SessionDataCacheKey;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.authz.OAuthAuthzReqMessageContext;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AuthorizeRespDTO;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;

import java.util.HashMap;
import java.util.Map;

/**
 * CDS specific claim provider
 */
public class CDSClaimProvider extends OBClaimProvider {

    private static Log log = LogFactory.getLog(CDSClaimProvider.class);
    private static final String S_HASH_CLAIM = "s_hash";
    private static final String SHARING_EXPIRES_AT_CLAIM = "sharing_expires_at";
    private static final String REFRESH_TOKEN_EXPIRES_AT_CLAIM = "refresh_token_expires_at";
    private static final String CDR_ARRANGEMENT_ID = "cdr_arrangement_id";
    private static final String NBF_CLAIM = "nbf";
    private static final String AUTH_TIME_CLAIM = "auth_time";

    /**
     * Method to add CDS Specific claims for Authorization response
     *
     * @param authAuthzReqMessageContext Authorization Request message context
     * @param authorizeRespDTO      Authorization Response
     * @return Map of additional claims
     * @throws IdentityOAuth2Exception when failed to obtain claims
     */
    @Override
    public Map<String, Object> getAdditionalClaims(OAuthAuthzReqMessageContext authAuthzReqMessageContext,
                                                   OAuth2AuthorizeRespDTO authorizeRespDTO)
            throws IdentityOAuth2Exception {


        Map<String, Object> cdsClaims = new HashMap<>();
        String sessionDataKey = authAuthzReqMessageContext.getAuthorizationReqDTO().getSessionDataKey();
        String stateValue = SessionDataCache.getInstance().getValueFromCache(new SessionDataCacheKey(sessionDataKey))
                .getoAuth2Parameters().getState();

        if (stateValue != null) {
            cdsClaims.put(S_HASH_CLAIM, CDSClaimProviderUtil.getHashValue(stateValue, null));
            if (log.isDebugEnabled()) {
                log.debug("S_HASH value created using given algorithm for state value:" + stateValue);
            }
        }

        //auth_time claim indicates the time when authentication occurs
        cdsClaims.put(AUTH_TIME_CLAIM, authAuthzReqMessageContext.getCodeIssuedTime());
        //nbf claim indicates the time when access token validity starts
        cdsClaims.put(NBF_CLAIM, authAuthzReqMessageContext.getAccessTokenIssuedTime());

        long sharingDuration = authAuthzReqMessageContext.getRefreshTokenvalidityPeriod() / 1000;
        // set claims related to sharing duration
        setSharingDurationRelatedClaims(sharingDuration, cdsClaims);
        return cdsClaims;
    }

    /**
     * Method to add CDS Specific claims for Token response
     *
     * @param oAuthTokenReqMessageContext token Request message context
     * @param oAuth2AccessTokenRespDTO    token Response DTO
     * @return Map of additional claims
     * @throws IdentityOAuth2Exception when failed to obtain claims
     */
    @Override
    public Map<String, Object> getAdditionalClaims(OAuthTokenReqMessageContext oAuthTokenReqMessageContext,
                                                   OAuth2AccessTokenRespDTO oAuth2AccessTokenRespDTO)
            throws IdentityOAuth2Exception {

        Map<String, Object> cdsClaims = new HashMap<>();

        long sharingDuration = oAuthTokenReqMessageContext.getRefreshTokenvalidityPeriod() / 1000;
        // set claims related to sharing duration
        setSharingDurationRelatedClaims(sharingDuration, cdsClaims);

        String consentId = oAuth2AccessTokenRespDTO.getParameter(CDR_ARRANGEMENT_ID);
        cdsClaims.put(CDR_ARRANGEMENT_ID, consentId);
        return cdsClaims;
    }

    /**
     * Method to add CDS Specific claims for Token response
     *
     * @param sharingDuration sharing duration value
     * @param cdsClaims cds related claims
     */
    private void setSharingDurationRelatedClaims(long sharingDuration, Map<String, Object> cdsClaims) {

        if (sharingDuration == 0) {
            cdsClaims.put(REFRESH_TOKEN_EXPIRES_AT_CLAIM, 0);
            cdsClaims.put(SHARING_EXPIRES_AT_CLAIM, 0);
        } else {
            sharingDuration = CDSClaimProviderUtil.getEpochDateTime(sharingDuration);
            cdsClaims.put(REFRESH_TOKEN_EXPIRES_AT_CLAIM, sharingDuration);
            cdsClaims.put(SHARING_EXPIRES_AT_CLAIM, sharingDuration);
        }
    }
}
