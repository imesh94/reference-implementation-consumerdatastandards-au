/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.identity.listener;

import org.wso2.carbon.identity.oauth.event.AbstractOAuthEventInterceptor;
import org.wso2.carbon.identity.oauth2.dto.OAuth2IntrospectionResponseDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2TokenValidationRequestDTO;

import java.util.Map;

/**
 * Event listener for token instrospection.
 */
public class CDSTokenIntrospectionListener extends AbstractOAuthEventInterceptor {
    private static final String REFRESH = "Refresh";
    /**
     * Allow token introspection for only the refresh token.
     *
     * @param oAuth2TokenValidationRequestDTO
     * @param oAuth2IntrospectionResponseDTO
     * @param params
     */
    @Override
    public void onPostTokenValidation(OAuth2TokenValidationRequestDTO oAuth2TokenValidationRequestDTO,
                                      OAuth2IntrospectionResponseDTO oAuth2IntrospectionResponseDTO,
                                      Map<String, Object> params) {


        if (oAuth2IntrospectionResponseDTO.isActive() &&
                !REFRESH.equalsIgnoreCase(oAuth2IntrospectionResponseDTO.getTokenType())) {
            // CDS specified only to support refresh token introspection.
            // As in rfc7662 section-2.2 : returning as inactive, for the tokens that are not allowed to introspect.
            oAuth2IntrospectionResponseDTO.setActive(false);
            oAuth2IntrospectionResponseDTO.setError("Introspection is supported only for refresh tokens.");
            return;
        }
    }
}
