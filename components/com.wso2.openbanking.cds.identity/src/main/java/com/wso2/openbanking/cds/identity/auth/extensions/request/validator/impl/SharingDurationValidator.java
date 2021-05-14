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
package com.wso2.openbanking.cds.identity.auth.extensions.request.validator.impl;

import com.wso2.openbanking.cds.identity.auth.extensions.request.validator.annotation.ValidateSharingDuration;
import com.wso2.openbanking.cds.identity.auth.extensions.request.validator.model.CDSRequestObject;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator class for validating the sharing duration claim in the request object
 */
public class SharingDurationValidator implements ConstraintValidator<ValidateSharingDuration, Object> {

    private static Log log = LogFactory.getLog(SharingDurationValidator.class);

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        
        String sharingDurationString = StringUtils.EMPTY;
        JSONObject claims = (JSONObject) ((CDSRequestObject) object).getClaim("claims");
        if (claims != null && claims.containsKey("sharing_duration")) {
            sharingDurationString = claims.get("sharing_duration").toString();
        }
        int sharingDuration = sharingDurationString.isEmpty() ? 0 : Integer.parseInt(sharingDurationString);
        //If the sharing_duration value is negative then the authorisation should fail.
        return sharingDuration >= 0;
    }

}
