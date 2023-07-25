/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.identity.dcr.validation.impl;

import com.wso2.openbanking.cds.identity.dcr.constants.CDSValidationConstants;
import com.wso2.openbanking.cds.identity.dcr.validation.annotation.ValidateSSAScopes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Validator class for validating the scopes in software statement.
 */
public class SSAScopesValidator implements ConstraintValidator<ValidateSSAScopes, Object> {

    private static final Log log = LogFactory.getLog(SSAScopesValidator.class);

    @Override
    public boolean isValid(Object scopes, ConstraintValidatorContext constraintValidatorContext) {

        return validateScopes(scopes);
    }

    /**
     * Checks if the scopes contain the mandatory 'cdr:register' and 'openid' scopes.
     *
     * @param scopes    scopes included in the software statement
     */
    private boolean validateScopes(Object scopes) {

        boolean containsRegistrationScope = false;
        boolean containsOpenIdScope = false;
        if (scopes instanceof String) {
            String scopeString = (String) scopes;
            for (String scope : scopeString.split(" ")) {
                if (CDSValidationConstants.CDR_REGISTRATION_SCOPE.equals(scope)) {
                    containsRegistrationScope = true;
                } else if (CDSValidationConstants.OPENID.equals(scope)) {
                    containsOpenIdScope = true;
                }
            }
        }
        return containsRegistrationScope && containsOpenIdScope;
    }
}
