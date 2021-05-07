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
package com.wso2.openbanking.cds.identity.dcr.utils;

import com.wso2.openbanking.accelerator.common.validator.OpenBankingValidator;
import com.wso2.openbanking.accelerator.identity.dcr.exception.DCRValidationException;
import com.wso2.openbanking.accelerator.identity.dcr.utils.ValidatorUtils;
import com.wso2.openbanking.accelerator.identity.dcr.validation.validationgroups.ValidationOrder;
import com.wso2.openbanking.cds.identity.dcr.model.CDSRegistrationRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    }
}
