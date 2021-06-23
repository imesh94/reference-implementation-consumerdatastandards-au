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

package com.wso2.openbanking.cds.identity.grant.type.handlers.utils;

import com.wso2.openbanking.cds.identity.utils.CDSIdentityUtil;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;

/**
 * Utility class for grant handlers.
 */
public class CDSGrantHandlerUtil {

    private static final String CDR_ARRANGEMENT_ID = "cdr_arrangement_id";

    /**
     * Populates cdr_arrangement_id parameter in the token response
     *
     * @param oAuth2AccessTokenRespDTO oAuth2AccessTokenResponseDTO
     * @param scopes                   token scopes
     */
    public static void populateCDRArrangementID(OAuth2AccessTokenRespDTO oAuth2AccessTokenRespDTO, String[] scopes) {

            String consentId = CDSIdentityUtil.getConsentId(scopes);
            oAuth2AccessTokenRespDTO.addParameter(CDR_ARRANGEMENT_ID, consentId);
    }

}
