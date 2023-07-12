/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.identity.auth.extensions.response.validator;

import com.wso2.openbanking.accelerator.identity.util.IdentityCommonConstants;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonUtil;
import com.wso2.openbanking.cds.identity.utils.CDSIdentityConstants;
import org.apache.oltu.oauth2.as.validator.TokenValidator;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

/**
 * Validator for code flow token requests.
 */
public class CDSCodeResponseTypeValidator extends TokenValidator {

    @Override
    public void validateRequiredParameters(HttpServletRequest request) throws OAuthProblemException {

        this.requiredParams = new ArrayList(Arrays.asList(OAuth.OAUTH_CLIENT_ID, IdentityCommonConstants.REQUEST_URI));
        this.notAllowedParams.add(IdentityCommonConstants.REQUEST);

        String responseType = IdentityCommonUtil
                .decodeRequestObjectAndGetKey(request, CDSIdentityConstants.RESPONSE_TYPE);
        String responseMode = IdentityCommonUtil
                .decodeRequestObjectAndGetKey(request, CDSIdentityConstants.RESPONSE_MODE);
        String state = IdentityCommonUtil
                .decodeRequestObjectAndGetKey(request, CDSIdentityConstants.STATE);

        //If the response type is "code", only the "jwt" response mode can be used.
        if (CDSIdentityConstants.CODE_RESPONSE_TYPE.equalsIgnoreCase(responseType) &&
                !CDSIdentityConstants.JWT_RESPONSE_MODE.equalsIgnoreCase(responseMode)) {
            throw OAuthProblemException.error(CDSIdentityConstants.UNSUPPORTED_RESPONSE_TYPE_ERROR)
                    .description("Unsupported Response Mode")
                    .state(state);
        }
    }
}
