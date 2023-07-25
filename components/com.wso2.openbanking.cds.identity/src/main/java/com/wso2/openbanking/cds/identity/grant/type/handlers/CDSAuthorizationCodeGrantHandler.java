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

package com.wso2.openbanking.cds.identity.grant.type.handlers;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.common.util.Generated;
import com.wso2.openbanking.accelerator.identity.grant.type.handlers.OBAuthorizationCodeGrantHandler;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonUtil;
import com.wso2.openbanking.cds.common.data.publisher.CDSDataPublishingService;
import com.wso2.openbanking.cds.identity.grant.type.handlers.utils.CDSGrantHandlerUtil;
import com.wso2.openbanking.cds.identity.utils.CDSIdentityUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;

import java.util.HashMap;
import java.util.Map;

/**
 * CDS specific authorization code grant handler.
 */
public class CDSAuthorizationCodeGrantHandler extends OBAuthorizationCodeGrantHandler {

    private static Log log = LogFactory.getLog(CDSAuthorizationCodeGrantHandler.class);
    private CDSDataPublishingService dataPublishingService = CDSDataPublishingService.getCDSDataPublishingService();

    /**
     * Set refresh token validity period and add cdr_arrangement_id.
     *
     * @param oAuth2AccessTokenRespDTO
     * @param tokReqMsgCtx
     */
    @Override
    public void executeInitialStep(OAuth2AccessTokenRespDTO oAuth2AccessTokenRespDTO,
                                   OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {

        // add cdr_arrangement_id to the token response dto
        CDSGrantHandlerUtil.populateCDRArrangementID(oAuth2AccessTokenRespDTO, tokReqMsgCtx.getScope());
    }

    /**
     * Publish token related data.
     *
     * @param oAuth2AccessTokenRespDTO
     */
    @Override
    public void publishUserAccessTokenData(OAuth2AccessTokenRespDTO oAuth2AccessTokenRespDTO)
            throws IdentityOAuth2Exception {

        log.debug("Publishing user access token data for metrics.");
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("accessTokenID", CDSGrantHandlerUtil.retrieveAccessToken(oAuth2AccessTokenRespDTO));
        dataPublishingService.publishUserAccessTokenData(tokenData);
    }

    /**
     * Extend this method to perform any actions related when issuing refresh token.
     *
     * @return
     */
    @Override
    public boolean issueRefreshToken() throws IdentityOAuth2Exception {

        OAuthTokenReqMessageContext tokenReqMessageContext = getTokenMessageContext();

        if (isRegulatory(tokenReqMessageContext)) {
            long sharingDuration;
            String[] scopes = tokenReqMessageContext.getScope();
            String consentId = CDSIdentityUtil.getConsentId(scopes);
            sharingDuration = CDSIdentityUtil.getRefreshTokenValidityPeriod(consentId);
            // set refresh token validity period for token request message context
            tokenReqMessageContext.setRefreshTokenvalidityPeriod(sharingDuration);
            if (log.isDebugEnabled()) {
                log.debug("Refresh token validity period is set to: " + sharingDuration);
            }
            // do not issue refresh token if sharing duration value equals to zero
            if (sharingDuration == 0) {
                return false;
            }
        }
        return true;
    }

    @Generated(message = "Excluding from code coverage since it requires a service call")
    protected OAuthTokenReqMessageContext getTokenMessageContext() {

        return OAuth2Util.getTokenRequestContext();
    }

    @Generated(message = "Excluding from code coverage since it requires a service call")
    protected boolean isRegulatory(OAuthTokenReqMessageContext tokenReqMessageContext) throws IdentityOAuth2Exception {

        try {
            return IdentityCommonUtil.getRegulatoryFromSPMetaData(tokenReqMessageContext.getOauth2AccessTokenReqDTO()
                    .getClientId());
        } catch (OpenBankingException e) {
            throw new IdentityOAuth2Exception("Error occurred while getting sp property from sp meta data");
        }

    }
}
