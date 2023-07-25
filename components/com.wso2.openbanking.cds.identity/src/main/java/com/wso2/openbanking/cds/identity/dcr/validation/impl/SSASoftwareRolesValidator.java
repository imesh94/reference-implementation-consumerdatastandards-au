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
import com.wso2.openbanking.cds.identity.dcr.validation.annotation.ValidateSSASoftwareRoles;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator class for validating the software roles in software statement.
 */
public class SSASoftwareRolesValidator implements ConstraintValidator<ValidateSSASoftwareRoles, Object> {

    private static final Log log = LogFactory.getLog(SSASoftwareRolesValidator.class);

    @Override
    public boolean isValid(Object softwareRoles, ConstraintValidatorContext constraintValidatorContext) {

        return CDSValidationConstants.DATA_RECIPIENT_SOFTWARE_PRODUCT.equals(softwareRoles);
    }
}
