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
package com.wso2.openbanking.cds.request.object.validator;

import com.nimbusds.jwt.JWTClaimsSet;
import com.wso2.finance.openbanking.accelerator.common.validator.OpenBankingValidator;
import com.wso2.finance.openbanking.accelerator.identity.auth.extensions.request.validator.OBRequestObjectValidator;
import com.wso2.finance.openbanking.accelerator.identity.auth.extensions.request.validator.models.OBRequestObject;
import com.wso2.finance.openbanking.accelerator.identity.auth.extensions.request.validator.models.ValidationResponse;
import com.wso2.openbanking.cds.request.object.validator.model.CDSRequestObject;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth2.RequestObjectException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The extension class for enforcing CDS Request Object Validations.
 */
public class CDSRequestObjectValidator extends OBRequestObjectValidator {

    private static final Log log = LogFactory.getLog(CDSRequestObjectValidator.class);

    @Override
    public ValidationResponse validateOBConstraints(OBRequestObject obRequestObject, Map<String, Object> dataMap) {

        CDSRequestObject cdsRequestObject = new CDSRequestObject(obRequestObject);

        String violation = validateScope(obRequestObject, dataMap);

        violation = StringUtils.isEmpty(violation) ? OpenBankingValidator.getInstance()
                .getFirstViolation(cdsRequestObject) : violation;

        if (StringUtils.isEmpty(violation)) {
            return new ValidationResponse(true);
        } else {
            return new ValidationResponse(false, violation);
        }
    }

    private String validateScope(OBRequestObject obRequestObject, Map<String, Object> dataMap) {

        try {
            //remove scope claim
            JWTClaimsSet claimsSet = obRequestObject.getClaimsSet();
            JSONObject claimsSetJsonObject = claimsSet.toJSONObject();
            if (claimsSetJsonObject.containsKey("scope")) {
                String scopeClaimString = claimsSetJsonObject.remove("scope").toString();
                List allowedScopes = (List) dataMap.get("scope");
                List<String> requestedScopes = new ArrayList<>(Arrays.asList(scopeClaimString.split(" ")));
                StringBuilder stringBuilder = new StringBuilder();

                // iterate through requested scopes and remove if not allowed
                for (String scope : requestedScopes) {
                    if (allowedScopes.contains(scope)) {
                        stringBuilder.append(scope).append(" ");
                        if (log.isDebugEnabled()) {
                            log.debug(String.format("Removed scope %s from the request object", scope));
                        }
                    }
                }
                String modifiedScopeString = stringBuilder.toString().trim();
                //throw an error if no valid scopes found
                if (StringUtils.isBlank(modifiedScopeString)) {
                    throw new RequestObjectException("No valid scopes found in the request");
                }
                claimsSetJsonObject.put("scope", modifiedScopeString);
                //Set claims set to request object
                JWTClaimsSet validatedClaimsSet = JWTClaimsSet.parse(claimsSetJsonObject);
                obRequestObject.setClaimSet(validatedClaimsSet);
                log.debug("Successfully set the modified claims-set to the request object");
            }
        } catch (ParseException | RequestObjectException e) {
             return e.getMessage();
        }
        return StringUtils.EMPTY;
    }
}
