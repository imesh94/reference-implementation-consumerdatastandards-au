/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses. For specific
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
     * validate the scopes of software statement with CDS specification allowed values
     *
     * @param scopes    scopes included in the software statement
     * @return true if the scopes are validated
     */
    private boolean validateScopes(Object scopes) {

        List<String> validScopes = new ArrayList<>(Arrays.asList(CDSValidationConstants.SSA_SCOPES.split(" ")));
        if (scopes instanceof String) {
            String scopeString = (String) scopes;
            for (String scope : scopeString.split(" ")) {
                if (!validScopes.contains(scope)) {
                    return false;
                }
            }
        }
        return true;
    }
}
