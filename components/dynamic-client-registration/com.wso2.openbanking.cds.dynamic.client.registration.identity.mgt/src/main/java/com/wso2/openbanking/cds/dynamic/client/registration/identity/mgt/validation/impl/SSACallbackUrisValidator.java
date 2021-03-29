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
package com.wso2.openbanking.cds.dynamic.client.registration.identity.mgt.validation.impl;

import com.wso2.openbanking.cds.dynamic.client.registration.identity.mgt.validation.annotation.ValidateSSACallbackUris;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator class for validating the redirect URIs in software statement
 */
public class SSACallbackUrisValidator implements ConstraintValidator<ValidateSSACallbackUris, Object> {

    private static final Log log = LogFactory.getLog(SSACallbackUrisValidator.class);

    @Override
    public boolean isValid(Object callbackUris, ConstraintValidatorContext constraintValidatorContext) {

        return validateRedirectURIHostNames(callbackUris);
    }

    /**
     * check the hostnames of redirect uris and other uris
     *
     * @param callbackUrisSoftwareStatement    callback uris included in the software statement
     * @return true if the uris are validated
     */
    private boolean validateRedirectURIHostNames(Object callbackUrisSoftwareStatement) {

        try {
            List<String> hostNameList = new ArrayList<>();
            if (callbackUrisSoftwareStatement instanceof List) {
                List callbackUrisSoftwareStatementValues = (List) callbackUrisSoftwareStatement;
                for (Object redirectURIObject : callbackUrisSoftwareStatementValues) {
                    hostNameList.add(new URI((String) redirectURIObject).getHost());
                }
            }
            //if all the redirect uris contain the same hostname, size of the set will be 1
            if ((new HashSet<>(hostNameList)).size() != 1) {
                return false;
            }
        } catch (URISyntaxException e) {
            log.error("Malformed redirect uri", e);
            return false;
        }
        return true;
    }
}
