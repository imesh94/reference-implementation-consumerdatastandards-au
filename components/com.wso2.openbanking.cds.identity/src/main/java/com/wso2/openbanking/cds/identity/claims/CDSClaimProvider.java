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
package com.wso2.openbanking.cds.identity.claims;

import com.wso2.openbanking.accelerator.identity.claims.OBClaimProvider;
import com.wso2.openbanking.cds.identity.claims.utils.CDSClaimProviderUtils;
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

public class CDSClaimProvider extends OBClaimProvider {

    private static Log log = LogFactory.getLog(CDSClaimProvider.class);
    private static final String S_HASH = "s_hash";
    private static final String SHARING_EXPIRES_AT = "sharing_expires_at";
    private static final String REFRESH_TOKEN_EXPIRES_AT = "refresh_token_expires_at";
    private static final String CDR_ARRANGEMENT_ID = "cdr_arrangement_id";

    /**
     * Method to add AU Specific claims for Authorization response
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


        HashMap<String, Object> auClaims = new HashMap<>();
        String sessionDataKey = authAuthzReqMessageContext.getAuthorizationReqDTO().getSessionDataKey();
        String stateValue = SessionDataCache.getInstance().getValueFromCache(new SessionDataCacheKey(sessionDataKey))
                .getoAuth2Parameters().getState();

        if (stateValue != null) {
            auClaims.put(S_HASH, CDSClaimProviderUtils.getHashValue(stateValue, null));
            if (log.isDebugEnabled()) {
                log.debug("S_HASH value created using given algorithm for state value:" + stateValue);
            }
        }

        //auth_time claim indicates the time when authentication occurs
        auClaims.put("auth_time", authAuthzReqMessageContext.getCodeIssuedTime());
        //nbf claim indicates the time when access token validity starts
        auClaims.put("nbf", authAuthzReqMessageContext.getAccessTokenIssuedTime());

        long sharingDuration = authAuthzReqMessageContext.getRefreshTokenvalidityPeriod() / 1000;

        if (sharingDuration == 0) {
            auClaims.put(REFRESH_TOKEN_EXPIRES_AT, 0);
            auClaims.put(SHARING_EXPIRES_AT, 0);
        } else {
            sharingDuration = CDSClaimProviderUtils.getEpochDateTime(sharingDuration);
            auClaims.put(REFRESH_TOKEN_EXPIRES_AT, sharingDuration);
            auClaims.put(SHARING_EXPIRES_AT, sharingDuration);
        }

        return auClaims;
    }

    /**
     * Method to add AU Specific claims for Token response
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

        HashMap<String, Object> auClaims = new HashMap<>();

        long sharingDuration = oAuthTokenReqMessageContext.getRefreshTokenvalidityPeriod() / 1000;

        if (sharingDuration == 0) {
            auClaims.put(REFRESH_TOKEN_EXPIRES_AT, 0);
            auClaims.put(SHARING_EXPIRES_AT, 0);
        } else {
            sharingDuration = CDSClaimProviderUtils.getEpochDateTime(sharingDuration);
            auClaims.put(REFRESH_TOKEN_EXPIRES_AT, sharingDuration);
            auClaims.put(SHARING_EXPIRES_AT, sharingDuration);
        }

        String consentId = oAuth2AccessTokenRespDTO.getParameter(CDR_ARRANGEMENT_ID);
        auClaims.put(CDR_ARRANGEMENT_ID, consentId);

        return auClaims;
    }
}
