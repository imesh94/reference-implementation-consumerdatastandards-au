/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.identity.dcr.utils;

import com.wso2.openbanking.accelerator.common.validator.OpenBankingValidator;
import com.wso2.openbanking.accelerator.identity.dcr.exception.DCRValidationException;
import com.wso2.openbanking.accelerator.identity.dcr.utils.ValidatorUtils;
import com.wso2.openbanking.accelerator.identity.dcr.validation.validationgroups.ValidationOrder;
import com.wso2.openbanking.cds.identity.dcr.constants.CDSValidationConstants;
import com.wso2.openbanking.cds.identity.dcr.model.CDSRegistrationRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Util class for validation logic implementation
 */
public class ValidationUtils {

    private static final Log log = LogFactory.getLog(ValidationUtils.class);

    public static void validateRequest(CDSRegistrationRequest cdsRegistrationRequest)
            throws DCRValidationException {

        //do SSA claim validations
        String error = OpenBankingValidator.getInstance()
                .getFirstViolation(cdsRegistrationRequest.getSoftwareStatementBody(), ValidationOrder.class);
        if (error != null) {
            String[] errors = error.split(":");
            throw new DCRValidationException(errors[1], errors[0]);
        }
        //do validations related to registration request
        ValidatorUtils.getValidationViolations(cdsRegistrationRequest);

        //remove the unsupported scopes in the software statement
        String requestedScopes = cdsRegistrationRequest.getSoftwareStatementBody().getScopes();
        String filteredScopes = ValidationUtils.filterOnlySupportedScopes(requestedScopes);
        cdsRegistrationRequest.getSoftwareStatementBody().setScopes(filteredScopes);
    }

    /**
     * Removes the unsupported scopes sent by data recipient.
     *
     * @param requestedScopes the scopes requested by the data recipient
     * @return filtered scopes string
     */
    public static String filterOnlySupportedScopes(String requestedScopes) {
        List<String> scopesList = Arrays.asList(requestedScopes.split(" "));
        List<String> filteredScopes = scopesList.stream().filter(CDSValidationConstants.VALID_SSA_SCOPES::contains)
                .collect(Collectors.toList());
        return String.join(" ", filteredScopes);
    }
}
