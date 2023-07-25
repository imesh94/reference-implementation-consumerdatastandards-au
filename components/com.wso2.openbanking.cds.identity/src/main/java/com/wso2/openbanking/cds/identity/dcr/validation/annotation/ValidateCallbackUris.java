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
package com.wso2.openbanking.cds.identity.dcr.validation.annotation;

import com.wso2.openbanking.cds.identity.dcr.validation.impl.CallbackUrisValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation class for grant types validation.
 */
@Target(ElementType.TYPE)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {CallbackUrisValidator.class})
public @interface ValidateCallbackUris {

    String message() default "Invalid callback uris";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String registrationRequestProperty() default "registrationRequestProperty";

    String callbackUrisProperty() default "callbackUrisProperty";

    String ssa() default "ssa";
}
