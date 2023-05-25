/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.identity.push.auth.extension.request.validator;

import com.wso2.openbanking.accelerator.identity.push.auth.extension.request.validator.PushAuthRequestValidator;
import com.wso2.openbanking.accelerator.identity.push.auth.extension.request.validator.constants.PushAuthRequestConstants;
import com.wso2.openbanking.accelerator.identity.push.auth.extension.request.validator.exception.PushAuthRequestValidatorException;
import com.wso2.openbanking.cds.identity.utils.CDSIdentityConstants;
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
    private static final String CDR_ARRANGEMENT_ID = "cdr_arrangement_id";

    @Override
    public void validateAdditionalParams(Map<String, Object> parameters) throws PushAuthRequestValidatorException {

        JSONObject requestObjectJsonBody;
        if (parameters.containsKey(PushAuthRequestConstants.DECODED_JWT_BODY) &&
                parameters.get(PushAuthRequestConstants.DECODED_JWT_BODY) instanceof JSONObject) {

            requestObjectJsonBody = (JSONObject) parameters.get(PushAuthRequestConstants.DECODED_JWT_BODY);
        } else {
            log.error(CDSIdentityConstants.INVALID_PUSH_AUTH_REQUEST);
            throw new PushAuthRequestValidatorException(HttpStatus.SC_BAD_REQUEST,
                    PushAuthRequestConstants.INVALID_REQUEST, CDSIdentityConstants.INVALID_PUSH_AUTH_REQUEST);
        }

        if (!isValidSharingDuration(requestObjectJsonBody)) {
            log.error(CDSIdentityConstants.INVALID_SHARING_DURATION);
            throw new PushAuthRequestValidatorException(HttpStatus.SC_BAD_REQUEST,
                    PushAuthRequestConstants.INVALID_REQUEST,
                    CDSIdentityConstants.INVALID_SHARING_DURATION);
        }

        // Sending an error for empty cdr_arrangement_id(A null cdr_arrangement_id should be ignored)
        if (isCDRArrangementIdEmpty(requestObjectJsonBody)) {
            log.error(CDSIdentityConstants.EMPTY_CDR_ARRANGEMENT_ID);
            throw new PushAuthRequestValidatorException(HttpStatus.SC_BAD_REQUEST,
                    PushAuthRequestConstants.INVALID_REQUEST,
                    CDSIdentityConstants.EMPTY_CDR_ARRANGEMENT_ID);
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

        int sharingDuration;
        try {
            sharingDuration = StringUtils.isEmpty(sharingDurationString) ? 0 : Integer.parseInt(sharingDurationString);
        } catch (NumberFormatException e) {
            return false;
        }

        //If the sharing_duration value is negative then the authorisation should fail.
        return sharingDuration >= 0;
    }

    private boolean isCDRArrangementIdEmpty(JSONObject requestObjectJsonBody) {

        JSONObject claims = requestObjectJsonBody.get(CLAIMS) != null ?
                (JSONObject) requestObjectJsonBody.get(CLAIMS) : null;
        return claims != null
                && claims.containsKey(CDR_ARRANGEMENT_ID)
                && claims.get(CDR_ARRANGEMENT_ID) != null
                && StringUtils.isBlank(claims.getAsString(CDR_ARRANGEMENT_ID));

    }
}
