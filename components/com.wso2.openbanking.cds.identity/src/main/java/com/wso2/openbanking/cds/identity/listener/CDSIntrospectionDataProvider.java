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

        /* No need to check whether the request is for a refresh token introspection because it is validated before
        this point in CDSTokenIntrospectionListener. */
        String consentIdClaim = OpenBankingConfigParser.getInstance().getConfiguration()
                .get(IdentityCommonConstants.CONSENT_ID_CLAIM_NAME).toString();
        return getAdditionalDataForIntrospectResponse(oAuth2IntrospectionResponseDTO,
                consentIdClaim);
    }

    /**
     * Method to set additional data to the introspection response.
     *
     * @param oAuth2IntrospectionResponseDTO introspection response DTO
     * @param consentIdClaimName the name of the consent ID claim
     * @return the additional data map
     */
    private Map<String, Object> getAdditionalDataForIntrospectResponse(OAuth2IntrospectionResponseDTO
                                                                               oAuth2IntrospectionResponseDTO,
                                                                       String consentIdClaimName) {

        Map<String, Object> additionalIntrospectionData = new HashMap<>();
        String scopes = oAuth2IntrospectionResponseDTO.getScope();
        String cdrArrangementIdWithPrefix = Arrays.stream(scopes.split(IdentityCommonConstants.SPACE_SEPARATOR))
                .filter(word -> word.startsWith(consentIdClaimName))
                .findFirst()
                .orElse(null);

        if (StringUtils.isNotBlank(cdrArrangementIdWithPrefix)) {
            String cdrArrangementId = StringUtils.removeStart(cdrArrangementIdWithPrefix, consentIdClaimName);
            additionalIntrospectionData.put(CommonConstants.CDR_ARRANGEMENT_ID, cdrArrangementId);
        }
        return additionalIntrospectionData;
    }
}
