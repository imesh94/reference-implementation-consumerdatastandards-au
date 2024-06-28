/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.identity.listener;

import com.wso2.openbanking.accelerator.common.config.OpenBankingConfigParser;
import com.wso2.openbanking.accelerator.identity.interceptor.OBIntrospectionDataProvider;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonConstants;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonUtil;
import com.wso2.openbanking.cds.common.utils.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuth2IntrospectionResponseDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2TokenValidationRequestDTO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * CDS Introspection data provider.
 */
public class CDSIntrospectionDataProvider extends OBIntrospectionDataProvider {

    @Override
    public Map<String, Object> getIntrospectionData(OAuth2TokenValidationRequestDTO oAuth2TokenValidationRequestDTO,
                                                    OAuth2IntrospectionResponseDTO oAuth2IntrospectionResponseDTO)
            throws IdentityOAuth2Exception {

        /*
           No need to check whether the request is for a refresh token introspection because it is validated before
           this point in CDSTokenIntrospectionListener.
         */
        Map<String, Object> additionalIntrospectionData = new HashMap<>();
        String consentIdClaim = OpenBankingConfigParser.getInstance().getConfiguration()
                .get(IdentityCommonConstants.CONSENT_ID_CLAIM_NAME).toString();
        String scopes = oAuth2IntrospectionResponseDTO.getScope();
        String cdrArrangementIdWithPrefix = Arrays.stream(scopes.split(IdentityCommonConstants.SPACE_SEPARATOR))
                .filter(word -> word.startsWith(consentIdClaim))
                .findFirst()
                .orElse(null);

        /* Todo: Remove duplicated logic from accelerator OBIntrospectionDataProvider class after fixing
            https://github.com/wso2/financial-open-banking/issues/75. */
        String[] nonInternalScopes = IdentityCommonUtil.removeInternalScopes(scopes
                .split(IdentityCommonConstants.SPACE_SEPARATOR));
        oAuth2IntrospectionResponseDTO.setScope(StringUtils.join(nonInternalScopes,
                IdentityCommonConstants.SPACE_SEPARATOR));

        additionalIntrospectionData.put(IdentityCommonConstants.SCOPE, StringUtils.join(nonInternalScopes,
                IdentityCommonConstants.SPACE_SEPARATOR));
        if (StringUtils.isNotBlank(cdrArrangementIdWithPrefix)) {
            String cdrArrangementId = StringUtils.removeStart(cdrArrangementIdWithPrefix, consentIdClaim);
            additionalIntrospectionData.put(CommonConstants.CDR_ARRANGEMENT_ID, cdrArrangementId);
        }
        return additionalIntrospectionData;
    }
}
