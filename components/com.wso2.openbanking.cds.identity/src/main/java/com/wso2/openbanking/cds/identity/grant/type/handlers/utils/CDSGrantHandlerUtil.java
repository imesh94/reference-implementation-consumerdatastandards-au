/*
 * Copyright (c) 2021-2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 *
 */

package com.wso2.openbanking.cds.identity.grant.type.handlers.utils;

import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.common.utils.CDSCommonUtils;
import com.wso2.openbanking.cds.common.utils.CommonConstants;
import com.wso2.openbanking.cds.identity.utils.CDSIdentityUtil;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;

/**
 * Utility class for grant handlers.
 */
public class CDSGrantHandlerUtil {

    /**
     * Populates cdr_arrangement_id parameter in the token response.
     *
     * @param oAuth2AccessTokenRespDTO oAuth2AccessTokenResponseDTO
     * @param scopes                   token scopes
     */
    public static void populateCDRArrangementID(OAuth2AccessTokenRespDTO oAuth2AccessTokenRespDTO, String[] scopes) {

            String consentId = CDSIdentityUtil.getConsentId(scopes);
            oAuth2AccessTokenRespDTO.addParameter(CommonConstants.CDR_ARRANGEMENT_ID, consentId);
    }

    /**
     * Retrieve access token from the oAuth2AccessTokenRespDTO and encrypt if required.
     *
     * @param oAuth2AccessTokenRespDTO oAuth2AccessTokenRespDTO
     * @return access token string
     */
    public static String retrieveAccessToken(OAuth2AccessTokenRespDTO oAuth2AccessTokenRespDTO) {

        String accessToken = oAuth2AccessTokenRespDTO.getAccessToken();
        // Encrypt access token
        if (accessToken != null && OpenBankingCDSConfigParser.getInstance().isTokenEncryptionEnabled()) {
            accessToken = CDSCommonUtils.encryptAccessToken(accessToken);
        }
        return accessToken;
    }

}
