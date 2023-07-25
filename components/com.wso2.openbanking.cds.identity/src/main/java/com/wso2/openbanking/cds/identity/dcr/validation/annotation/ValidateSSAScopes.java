/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.identity.dcr.validation.annotation;

import com.wso2.openbanking.cds.identity.dcr.validation.impl.SSAScopesValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation class for SSA software roles validation.
 */
@Target(ElementType.METHOD)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {SSAScopesValidator.class})
public @interface ValidateSSAScopes {

    String message() default "Mandatory scopes are not given in the software statement";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
