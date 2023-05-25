/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.account.type.management.endpoint.util;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * This class contains the common validation methods used
 * in account type management endpoints.
 */
public class ValidationUtil {

    /**
     * Validate the passed DTO and return the first violation message.
     *
     * @param dto DTO to be validated
     * @return first violation message
     */
    public static String getFirstViolationMessage(Object dto) {

        String firstViolationMessage = "";
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            ConstraintViolation<?> firstViolation = violations.iterator().next();
            firstViolationMessage = firstViolation.getMessage().replaceAll("\\.$", "") +
                    ". Error path :" + firstViolation.getPropertyPath();
        }
        return firstViolationMessage;
    }
}
