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
package com.wso2.openbanking.dynamic.client.registration.identity.mgt.validation.impl;

import com.wso2.finance.openbanking.accelerator.identity.dcr.model.RegistrationRequest;
import com.wso2.finance.openbanking.accelerator.identity.dcr.utils.ValidatorUtils;
import com.wso2.openbanking.dynamic.client.registration.identity.mgt.constants.CDSValidationConstants;
import com.wso2.openbanking.dynamic.client.registration.identity.mgt.validation.annotation.ValidateCallbackUris;
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
 * Validator class for validating the callback URIs of the registration request
 */
public class CallbackUrisValidator implements ConstraintValidator<ValidateCallbackUris, Object> {

    private static final Log log = LogFactory.getLog(CallbackUrisValidator.class);

    private String registrationRequestPath;
    private String redirectUriPath;
    private String ssaPath;

    @Override
    public void initialize(ValidateCallbackUris validateCallbackUris) {

        this.registrationRequestPath = validateCallbackUris.registrationRequestProperty();
        this.redirectUriPath = validateCallbackUris.callbackUrisProperty();
        this.ssaPath = validateCallbackUris.ssa();
    }

    @Override
    public boolean isValid(Object cdsRegistrationRequest, ConstraintValidatorContext constraintValidatorContext) {

        try {

            RegistrationRequest registrationRequest = (RegistrationRequest) new PropertyUtilsBean()
                    .getProperty(cdsRegistrationRequest, registrationRequestPath);
            String softwareStatement = BeanUtils.getProperty(cdsRegistrationRequest, ssaPath);
            List<String> callbackUris =  registrationRequest.getCallbackUris();
            if (callbackUris != null && !callbackUris.isEmpty()) {
                final Object ssaCallbackUris = ValidatorUtils.decodeRequestJWT(softwareStatement, "body")
                        .get(CDSValidationConstants.SSA_REDIRECT_URIS);

                return matchRedirectURI(callbackUris, ssaCallbackUris);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Error while resolving validation fields", e);
            return false;
        } catch (ParseException e) {
            log.error("Error while parsing the softwareStatement", e);
            return false;
        }
        return true;
    }

    /**
     * check whether the redirect uris in the request are a subset of the redirect uris in the software statement
     * assertion
     */
    private boolean matchRedirectURI(List<String> callbackUrisRequest, Object callbackUrisSoftwareStatement) {

        int matchedURis = 0;
        if (callbackUrisSoftwareStatement instanceof List) {
            List callbackUrisSoftwareStatementValues = (List) callbackUrisSoftwareStatement;
            for (String requestURI : callbackUrisRequest) {
                for (Object callbackUrisSoftwareStatementObject : callbackUrisSoftwareStatementValues) {
                    String softwareStatementURI = (String) callbackUrisSoftwareStatementObject;
                    if (requestURI.equals(softwareStatementURI)) {
                        matchedURis = matchedURis + 1;
                    }
                }
            }
        }
        return matchedURis == callbackUrisRequest.size();
    }
}
