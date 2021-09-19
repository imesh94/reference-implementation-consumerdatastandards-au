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

import com.wso2.openbanking.accelerator.common.util.JWTUtils;
import com.wso2.openbanking.accelerator.identity.push.auth.extension.request.validator.constants.PushAuthRequestConstants;
import com.wso2.openbanking.accelerator.identity.push.auth.extension.request.validator.exception.PushAuthRequestValidatorException;
import com.wso2.openbanking.cds.identity.push.auth.extension.request.validator.util.CDSPushAuthRequestValidatorTestData;
import net.minidev.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class CDSPushAuthRequestValidatorTest {

    private CDSPushAuthRequestValidator cdsPushAuthRequestValidator;
    private Map<String, Object> parameters;


    @Test
    public void validateSharingDurationSuccessScenario() throws Exception {

        JSONObject decodedRequestBody = JWTUtils.decodeRequestJWT(CDSPushAuthRequestValidatorTestData.VALID_SIGNED_JWT,
                "body");
        parameters = new HashMap<>();
        parameters.put(PushAuthRequestConstants.DECODED_JWT_BODY, decodedRequestBody);

        cdsPushAuthRequestValidator = new CDSPushAuthRequestValidator();

        try {
            cdsPushAuthRequestValidator.validateAdditionalParams(parameters);
        } catch (PushAuthRequestValidatorException e) {
            Assert.fail("should not throw exception");
        }
    }

    @Test(expectedExceptions = PushAuthRequestValidatorException.class)
    public void validateSharingDurationWithNegativeSharingValue() throws Exception {

        JSONObject decodedRequestBody = JWTUtils
                .decodeRequestJWT(CDSPushAuthRequestValidatorTestData.INVALID_SIGNED_JWT, "body");
        parameters = new HashMap<>();
        parameters.put(PushAuthRequestConstants.DECODED_JWT_BODY, decodedRequestBody);
        cdsPushAuthRequestValidator = new CDSPushAuthRequestValidator();
        cdsPushAuthRequestValidator.validateAdditionalParams(parameters);
    }
}
