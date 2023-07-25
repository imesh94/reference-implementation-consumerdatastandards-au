/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.identity.push.auth.extension.request.validator;

import com.wso2.openbanking.accelerator.common.util.JWTUtils;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.DetailedConsentResource;
import com.wso2.openbanking.accelerator.consent.mgt.service.impl.ConsentCoreServiceImpl;
import com.wso2.openbanking.accelerator.identity.push.auth.extension.request.validator.constants.PushAuthRequestConstants;
import com.wso2.openbanking.accelerator.identity.push.auth.extension.request.validator.exception.PushAuthRequestValidatorException;
import com.wso2.openbanking.cds.identity.push.auth.extension.request.validator.util.CDSPushAuthRequestValidatorTestData;
import com.wso2.openbanking.cds.identity.utils.CDSIdentityConstants;
import net.minidev.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Test class for CDS Push Authorization Request Validator.
 */
public class CDSPushAuthRequestValidatorTest {

    private CDSPushAuthRequestValidator cdsPushAuthRequestValidator;
    private Map<String, Object> parameters;
    private static ConsentCoreServiceImpl consentCoreServiceMock;

    @BeforeClass
    public void initTest() {
        consentCoreServiceMock = mock(ConsentCoreServiceImpl.class);
    }

    @Test
    public void validateValidAdditionalParams() throws Exception {

        // mocking required variables
        DetailedConsentResource detailedConsentResource = new DetailedConsentResource();
        detailedConsentResource.setCurrentStatus(CDSIdentityConstants.AUTHORIZED);
        detailedConsentResource.setClientID("wHKH6jd5YRJtG_CXSLVfcStMfOAa");

        long nowInEpochSeconds = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond();
        detailedConsentResource.setValidityPeriod(nowInEpochSeconds + 1000);

        doReturn(detailedConsentResource).when(consentCoreServiceMock).getDetailedConsent(anyString());

        JSONObject decodedRequestBody = JWTUtils.decodeRequestJWT(CDSPushAuthRequestValidatorTestData.VALID_SIGNED_JWT,
                "body");
        parameters = new HashMap<>();
        parameters.put(PushAuthRequestConstants.DECODED_JWT_BODY, decodedRequestBody);
        cdsPushAuthRequestValidator = new CDSPushAuthRequestValidator(consentCoreServiceMock);

        try {
            cdsPushAuthRequestValidator.validateAdditionalParams(parameters);
        } catch (PushAuthRequestValidatorException e) {
            Assert.fail("should not throw exception");
        }
    }

    @Test(expectedExceptions = PushAuthRequestValidatorException.class)
    public void expiredConsent() throws Exception {

        // mocking required variables
        DetailedConsentResource detailedConsentResource = new DetailedConsentResource();
        detailedConsentResource.setCurrentStatus(CDSIdentityConstants.AUTHORIZED);
        detailedConsentResource.setClientID("wHKH6jd5YRJtG_CXSLVfcStMfOAa");

        long nowInEpochSeconds = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond();
        detailedConsentResource.setValidityPeriod(nowInEpochSeconds - 1000);

        doReturn(detailedConsentResource).when(consentCoreServiceMock).getDetailedConsent(anyString());

        JSONObject decodedRequestBody = JWTUtils.decodeRequestJWT(CDSPushAuthRequestValidatorTestData.VALID_SIGNED_JWT,
                "body");
        parameters = new HashMap<>();
        parameters.put(PushAuthRequestConstants.DECODED_JWT_BODY, decodedRequestBody);
        cdsPushAuthRequestValidator = new CDSPushAuthRequestValidator(consentCoreServiceMock);
        cdsPushAuthRequestValidator.validateAdditionalParams(parameters);
    }

    @Test(expectedExceptions = PushAuthRequestValidatorException.class)
    public void invalidConsentStatus() throws Exception {

        // mocking required variables
        DetailedConsentResource detailedConsentResource = new DetailedConsentResource();
        detailedConsentResource.setClientID("wHKH6jd5YRJtG_CXSLVfcStMfOAa");

        doReturn(detailedConsentResource).when(consentCoreServiceMock).getDetailedConsent(anyString());

        JSONObject decodedRequestBody = JWTUtils.decodeRequestJWT(CDSPushAuthRequestValidatorTestData.VALID_SIGNED_JWT,
                "body");
        parameters = new HashMap<>();
        parameters.put(PushAuthRequestConstants.DECODED_JWT_BODY, decodedRequestBody);
        cdsPushAuthRequestValidator = new CDSPushAuthRequestValidator(consentCoreServiceMock);
        cdsPushAuthRequestValidator.validateAdditionalParams(parameters);
    }

    @Test(expectedExceptions = PushAuthRequestValidatorException.class)
    public void invalidClientIdForCDRArrangementId() throws Exception {

        // mocking required variables
        DetailedConsentResource detailedConsentResource = new DetailedConsentResource();
        detailedConsentResource.setClientID("abc");

        doReturn(detailedConsentResource).when(consentCoreServiceMock).getDetailedConsent(anyString());

        JSONObject decodedRequestBody = JWTUtils.decodeRequestJWT(CDSPushAuthRequestValidatorTestData.VALID_SIGNED_JWT,
                "body");
        parameters = new HashMap<>();
        parameters.put(PushAuthRequestConstants.DECODED_JWT_BODY, decodedRequestBody);
        cdsPushAuthRequestValidator = new CDSPushAuthRequestValidator(consentCoreServiceMock);
        cdsPushAuthRequestValidator.validateAdditionalParams(parameters);
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
