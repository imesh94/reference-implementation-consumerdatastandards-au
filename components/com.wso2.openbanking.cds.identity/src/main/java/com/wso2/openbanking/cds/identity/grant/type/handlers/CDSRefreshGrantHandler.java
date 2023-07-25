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

import com.wso2.openbanking.accelerator.identity.grant.type.handlers.OBRefreshGrantHandler;
import com.wso2.openbanking.cds.common.data.publisher.CDSDataPublishingService;
import com.wso2.openbanking.cds.identity.grant.type.handlers.utils.CDSGrantHandlerUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;

import java.util.HashMap;
import java.util.Map;

/**
 * CDS specific refresh grant handler.
 */
public class CDSRefreshGrantHandler extends OBRefreshGrantHandler {

    private static Log log = LogFactory.getLog(CDSRefreshGrantHandler.class);
    private CDSDataPublishingService dataPublishingService = CDSDataPublishingService.getCDSDataPublishingService();

    /**
     * Add cdr_arrangement_id.
     *
     * @param oAuth2AccessTokenRespDTO
     * @param tokReqMsgCtx
     */
    @Override
    public void executeInitialStep(OAuth2AccessTokenRespDTO oAuth2AccessTokenRespDTO,
                                   OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {

        CDSGrantHandlerUtil.populateCDRArrangementID(oAuth2AccessTokenRespDTO, tokReqMsgCtx.getScope());
    }

    /**
     * Publish refresh token related data.
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

}
