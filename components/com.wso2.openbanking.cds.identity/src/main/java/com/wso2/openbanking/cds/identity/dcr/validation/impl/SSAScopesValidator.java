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
package com.wso2.openbanking.cds.identity.dcr.validation.impl;

import com.wso2.openbanking.cds.identity.dcr.constants.CDSValidationConstants;
import com.wso2.openbanking.cds.identity.dcr.validation.annotation.ValidateSSAScopes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Validator class for validating the scopes in software statement
 */
public class SSAScopesValidator implements ConstraintValidator<ValidateSSAScopes, Object> {

    private static final Log log = LogFactory.getLog(SSAScopesValidator.class);

    @Override
    public boolean isValid(Object scopes, ConstraintValidatorContext constraintValidatorContext) {

        return validateScopes(scopes);
    }

    /**
     * Validate the scope requested by data recipient is allowed and checks if the scopes contain
     * the mandatory 'cdr:register' scope.
     *
     * @param scopes    scopes included in the software statement
     */
    private boolean validateScopes(Object scopes) {

        List<String> validScopes = new ArrayList<>(Arrays.asList(CDSValidationConstants.SSA_SCOPES.split(" ")));
        boolean containsRegistrationScope = false;
        boolean allScopesValid = true;
        if (scopes instanceof String) {
            String scopeString = (String) scopes;
            for (String scope : scopeString.split(" ")) {
                if (CDSValidationConstants.CDR_REGISTRATION_SCOPE.equals(scope)) {
                    containsRegistrationScope = true;
                }
                if (!validScopes.contains(scope)) {
                    allScopesValid = false;
                    break;
                }
            }
        }
        return allScopesValid && containsRegistrationScope;
    }
}
