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

package com.wso2.openbanking.cds.identity.dcr.validation.annotation;

import com.wso2.openbanking.cds.identity.dcr.validation.impl.UriHostnameValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation class for validate whether the redirect URIs and other uris have same hostname
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {UriHostnameValidator.class})
public @interface ValidateUriHostnames {

    String message() default "Host names of logo_uri/tos_uri/policy_uri/client_uri does not match with the " +
            "redirect_uris";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String registrationRequestProperty() default "registrationRequestProperty";

    String ssa() default "ssa";

}
