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

import com.wso2.openbanking.accelerator.common.util.JWTUtils;
import com.wso2.openbanking.accelerator.identity.dcr.model.RegistrationRequest;
import com.wso2.openbanking.cds.identity.dcr.constants.CDSValidationConstants;
import com.wso2.openbanking.cds.identity.dcr.validation.annotation.ValidateRedirectUriFormat;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator class to validate Redirect Urls are valid Urls
 */
public class RedirectUriFormatValidator implements ConstraintValidator<ValidateRedirectUriFormat, Object> {

    private static final Log log = LogFactory.getLog(RedirectUriFormatValidator.class);
    private String registrationRequestPath;
    private String ssaPath;

    @Override
    public void initialize(ValidateRedirectUriFormat validateRedirectUriFormat) {
        this.registrationRequestPath = validateRedirectUriFormat.registrationRequestProperty();
        this.ssaPath = validateRedirectUriFormat.ssa();
    }

    @Override
    public boolean isValid(Object cdsRegistrationRequest, ConstraintValidatorContext constraintValidatorContext) {

        try {
            RegistrationRequest registrationRequest = (RegistrationRequest) new PropertyUtilsBean()
                    .getProperty(cdsRegistrationRequest, registrationRequestPath);
            String softwareStatement = BeanUtils.getProperty(registrationRequest, ssaPath);

            List<String> redirectURIsFromSSA = (List<String>) JWTUtils
                    .decodeRequestJWT(softwareStatement, "body").get(CDSValidationConstants.SSA_REDIRECT_URIS);

            return validateRedirectURIs(redirectURIsFromSSA);

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Error while resolving validation fields", e);
        } catch (ParseException e) {
        log.error("Error while parsing the softwareStatement", e);
    }
        return false;
    }

    /**
     * Check validity and connection of redirect uris.
     *
     * @param redirectURIs redirect uris included in the software statement
     * @return true if the uris are validated
     */
    public static boolean validateRedirectURIs(List<String> redirectURIs) {

        for (String redirectURI : redirectURIs) {
            if (!(redirectURI != null && redirectURI.contains("https") && !redirectURI.contains("localhost"))) {
                return false;
            }
        }
        return true;
    }
}
