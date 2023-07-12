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
import org.apache.commons.lang.StringUtils;
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

        if (StringUtils.isNotBlank(request.getParameter(IdentityCommonConstants.REQUEST_URI))) {

            this.requiredParams = new ArrayList(Arrays.asList(OAuth.OAUTH_CLIENT_ID,
                    IdentityCommonConstants.REQUEST_URI));
            this.notAllowedParams.add(IdentityCommonConstants.REQUEST);
        }
    }
}
