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
package com.wso2.openbanking.request.object.validator;

import com.wso2.finance.openbanking.accelerator.common.validator.OpenBankingValidator;
import com.wso2.finance.openbanking.accelerator.identity.auth.extensions.request.validator.OBRequestObjectValidator;
import com.wso2.finance.openbanking.accelerator.identity.auth.extensions.request.validator.models.OBRequestObject;
import com.wso2.finance.openbanking.accelerator.identity.auth.extensions.request.validator.models.ValidationResponse;
import com.wso2.openbanking.request.object.validator.model.CDSRequestObject;
import org.apache.commons.lang.StringUtils;

/**
 * The extension class for enforcing CDS Request Object Validations.
 */
public class CDSRequestObjectValidator extends OBRequestObjectValidator {

    public ValidationResponse validateOBConstraints(OBRequestObject obRequestObject) {

        CDSRequestObject cdsRequestObject = new CDSRequestObject(obRequestObject);

        String violation = OpenBankingValidator.getInstance().getFirstViolation(cdsRequestObject);

        if (StringUtils.isEmpty(violation)) {
            return new ValidationResponse(true);
        } else {
            return new ValidationResponse(false, violation);
        }


    }
}
