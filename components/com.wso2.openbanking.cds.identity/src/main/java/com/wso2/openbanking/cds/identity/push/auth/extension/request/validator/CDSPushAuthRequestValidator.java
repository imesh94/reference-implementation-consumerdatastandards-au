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

package com.wso2.openbanking.cds.identity.push.auth.extension.request.validator;

import com.wso2.openbanking.accelerator.identity.push.auth.extension.request.validator.PushAuthRequestValidator;
import com.wso2.openbanking.accelerator.identity.push.auth.extension.request.validator.constants.PushAuthRequestConstants;
import com.wso2.openbanking.accelerator.identity.push.auth.extension.request.validator.exception.PushAuthRequestValidatorException;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;

import java.util.Map;

/**
 * The extension class for enforcing CDS Push Auth Request Validations.
 */
public class CDSPushAuthRequestValidator  extends PushAuthRequestValidator {

    private static final Log log = LogFactory.getLog(CDSPushAuthRequestValidator.class);
    private static final String CLAIMS = "claims";
    private static final String SHARING_DURATION = "sharing_duration";

    @Override
    public void validateAdditionalParams(Map<String, Object> parameters) throws PushAuthRequestValidatorException {

        JSONObject requestObjectJsonBody;
        if (parameters.containsKey(PushAuthRequestConstants.DECODED_JWT_BODY) &&
                parameters.get(PushAuthRequestConstants.DECODED_JWT_BODY) instanceof JSONObject) {

            requestObjectJsonBody = (JSONObject) parameters.get(PushAuthRequestConstants.DECODED_JWT_BODY);
        } else {
            log.error("Invalid push authorisation request");
            throw new PushAuthRequestValidatorException(HttpStatus.SC_BAD_REQUEST,
                    PushAuthRequestConstants.INVALID_REQUEST, "Invalid push authorisation request");
        }

        if (!isValidSharingDuration(requestObjectJsonBody)) {
            log.error("Invalid sharing_duration value");
            throw new PushAuthRequestValidatorException(HttpStatus.SC_BAD_REQUEST,
                    PushAuthRequestConstants.INVALID_REQUEST,
                    "Invalid sharing_duration value");
        }
    }

    private boolean isValidSharingDuration(JSONObject requestObjectJsonBody) {

        String sharingDurationString = StringUtils.EMPTY;
        JSONObject claims = requestObjectJsonBody.get(CLAIMS) != null ?
                (JSONObject) requestObjectJsonBody.get(CLAIMS) : null;
        if (claims != null && claims.containsKey(SHARING_DURATION)
                && StringUtils.isNotBlank(claims.getAsString(SHARING_DURATION))) {
            sharingDurationString = claims.getAsString(SHARING_DURATION);
        }
        int sharingDuration = sharingDurationString.isEmpty() ? 0 : Integer.parseInt(sharingDurationString);
        //If the sharing_duration value is negative then the authorisation should fail.
        return sharingDuration >= 0;
    }
}
