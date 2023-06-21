/*
 * Copyright (c) 2021-2022, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.consent.extensions.validate;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.consent.extensions.validate.model.ConsentValidateData;
import com.wso2.openbanking.accelerator.consent.extensions.validate.model.ConsentValidationResult;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentMappingResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.DetailedConsentResource;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.common.error.handling.util.ErrorConstants;
import com.wso2.openbanking.cds.common.metadata.domain.MetadataValidationResponse;
import com.wso2.openbanking.cds.common.metadata.status.validator.service.MetadataService;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import com.wso2.openbanking.cds.consent.extensions.util.CDSConsentValidateTestConstants;
import com.wso2.openbanking.cds.consent.extensions.utils.CDSConsentExtensionUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for CDS consent Validator
 */
@PrepareForTest({OpenBankingCDSConfigParser.class, MetadataService.class, CDSConsentExtensionUtils.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class CDSConsentValidatorTest extends PowerMockTestCase {

    CDSConsentValidator cdsConsentValidator;
    @Mock
    ConsentValidateData consentValidateDataMock;
    @Mock
    DetailedConsentResource detailedConsentResourceMock;
    @Mock
    ConsentMappingResource consentMappingResourceMock;
    @Mock
    OpenBankingCDSConfigParser openBankingCDSConfigParserMock;
    Map<String, Object> configs = new HashMap<>();
    Map<String, String> resourceParams = new HashMap<>();

    @BeforeClass
    public void initClass() {
        cdsConsentValidator = new CDSConsentValidator();
        consentValidateDataMock = mock(ConsentValidateData.class);
        detailedConsentResourceMock = mock(DetailedConsentResource.class);
        consentMappingResourceMock = mock(ConsentMappingResource.class);
        openBankingCDSConfigParserMock = mock(OpenBankingCDSConfigParser.class);
        configs.put("ConsentManagement.ValidateAccountIdOnRetrieval", "true");
        resourceParams.put("ResourcePath", CDSConsentValidateTestConstants.ACCOUNT_PATH + "/123456");
    }

    @Test
    public void testValidateAccountRetrieval() {

        doReturn(detailedConsentResourceMock).when(consentValidateDataMock).getComprehensiveConsent();
        doReturn(CDSConsentValidateTestConstants
                .getDetailedConsentResource(CDSConsentValidateTestConstants.VALID_RECEIPT, "123456")
                .getReceipt()).when(detailedConsentResourceMock).getReceipt();
        doReturn(CDSConsentExtensionConstants.AUTHORIZED_STATUS).when(detailedConsentResourceMock).getCurrentStatus();
        doReturn(CDSConsentValidateTestConstants.ACCOUNT_PATH).when(consentValidateDataMock).getRequestPath();
        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        when(openBankingCDSConfigParserMock.getConfiguration()).thenReturn(configs);

        ConsentValidationResult consentValidationResult = new ConsentValidationResult();
        cdsConsentValidator.validate(consentValidateDataMock, consentValidationResult);

        Assert.assertTrue(consentValidationResult.isValid());
    }

    @Test
    public void testValidateAccountRetrievalWithValidAccountId() {

        doReturn(detailedConsentResourceMock).when(consentValidateDataMock).getComprehensiveConsent();
        doReturn(CDSConsentValidateTestConstants
                .getDetailedConsentResource(CDSConsentValidateTestConstants.VALID_RECEIPT, "123456")
                .getReceipt()).when(detailedConsentResourceMock).getReceipt();
        doReturn(CDSConsentValidateTestConstants
                .getDetailedConsentResource(CDSConsentValidateTestConstants.VALID_RECEIPT, "123456")
                .getConsentMappingResources()).when(detailedConsentResourceMock).getConsentMappingResources();
        doReturn(CDSConsentExtensionConstants.AUTHORIZED_STATUS).when(detailedConsentResourceMock).getCurrentStatus();
        doReturn(CDSConsentValidateTestConstants.ACCOUNT_PATH + "/{accountId}")
                .when(consentValidateDataMock).getRequestPath();
        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        when(openBankingCDSConfigParserMock.getConfiguration()).thenReturn(configs);
        doReturn(resourceParams).when(consentValidateDataMock).getResourceParams();

        ConsentValidationResult consentValidationResult = new ConsentValidationResult();
        cdsConsentValidator.validate(consentValidateDataMock, consentValidationResult);

        Assert.assertTrue(consentValidationResult.isValid());
    }

    @Test
    public void testValidateAccountRetrievalWithInvalidAccountId() {

        doReturn(detailedConsentResourceMock).when(consentValidateDataMock).getComprehensiveConsent();
        doReturn(CDSConsentValidateTestConstants
                .getDetailedConsentResource(CDSConsentValidateTestConstants.VALID_RECEIPT, "123455")
                .getReceipt()).when(detailedConsentResourceMock).getReceipt();
        doReturn(CDSConsentValidateTestConstants
                .getDetailedConsentResource(CDSConsentValidateTestConstants.VALID_RECEIPT, "123455")
                .getConsentMappingResources()).when(detailedConsentResourceMock).getConsentMappingResources();
        doReturn(CDSConsentExtensionConstants.AUTHORIZED_STATUS).when(detailedConsentResourceMock).getCurrentStatus();
        doReturn(CDSConsentValidateTestConstants.ACCOUNT_PATH + "/{accountId}")
                .when(consentValidateDataMock).getRequestPath();
        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        when(openBankingCDSConfigParserMock.getConfiguration()).thenReturn(configs);
        doReturn(resourceParams).when(consentValidateDataMock).getResourceParams();

        ConsentValidationResult consentValidationResult = new ConsentValidationResult();
        cdsConsentValidator.validate(consentValidateDataMock, consentValidationResult);

        Assert.assertFalse(consentValidationResult.isValid());
    }

    @Test
    public void testValidateAccountRetrievalWithInvalidStatus() {

        doReturn(detailedConsentResourceMock).when(consentValidateDataMock).getComprehensiveConsent();
        doReturn(CDSConsentValidateTestConstants
                .getDetailedConsentResource(CDSConsentValidateTestConstants.VALID_RECEIPT, "123456")
                .getReceipt()).when(detailedConsentResourceMock).getReceipt();
        doReturn("Revoked").when(detailedConsentResourceMock).getCurrentStatus();
        doReturn(CDSConsentValidateTestConstants.ACCOUNT_PATH).when(consentValidateDataMock).getRequestPath();

        ConsentValidationResult consentValidationResult = new ConsentValidationResult();
        cdsConsentValidator.validate(consentValidateDataMock, consentValidationResult);

        Assert.assertFalse(consentValidationResult.isValid());
        Assert.assertEquals(consentValidationResult.getErrorCode(),
                "urn:au-cds:error:cds-all:Authorisation/RevokedConsent");
        Assert.assertEquals(consentValidationResult.getHttpCode(), 403);

    }

    @Test
    public void testValidateAccountRetrievalWithExpiredConsent() {

        doReturn(detailedConsentResourceMock).when(consentValidateDataMock).getComprehensiveConsent();
        doReturn(CDSConsentValidateTestConstants
                .getDetailedConsentResource(CDSConsentValidateTestConstants.EXPIRED_CONSENT_RECEIPT, "123456")
                .getReceipt()).when(detailedConsentResourceMock).getReceipt();
        doReturn(CDSConsentExtensionConstants.AUTHORIZED_STATUS).when(detailedConsentResourceMock).getCurrentStatus();
        doReturn(CDSConsentValidateTestConstants.ACCOUNT_PATH).when(consentValidateDataMock).getRequestPath();

        ConsentValidationResult consentValidationResult = new ConsentValidationResult();
        cdsConsentValidator.validate(consentValidateDataMock, consentValidationResult);

        Assert.assertFalse(consentValidationResult.isValid());
        Assert.assertEquals(consentValidationResult.getErrorCode(),
                "urn:au-cds:error:cds-all:Authorisation/InvalidConsent");
        Assert.assertEquals(consentValidationResult.getHttpCode(), 403);
    }

    @Test(priority = 1)
    public void testValidateAccountRetrievalForValidPOSTRequests() throws ParseException {

        doReturn(detailedConsentResourceMock).when(consentValidateDataMock).getComprehensiveConsent();
        doReturn(CDSConsentValidateTestConstants
                .getDetailedConsentResource(CDSConsentValidateTestConstants.VALID_RECEIPT, "123456")
                .getReceipt()).when(detailedConsentResourceMock).getReceipt();
        doReturn(CDSConsentValidateTestConstants
                .getDetailedConsentResource(CDSConsentValidateTestConstants.VALID_RECEIPT, "123456")
                .getConsentMappingResources()).when(detailedConsentResourceMock).getConsentMappingResources();
        JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
        JSONObject payload = (JSONObject) parser
                .parse(CDSConsentValidateTestConstants.PAYLOAD);
        doReturn(payload).when(consentValidateDataMock).getPayload();
        doReturn(CDSConsentExtensionConstants.AUTHORIZED_STATUS).when(detailedConsentResourceMock).getCurrentStatus();
        doReturn(CDSConsentValidateTestConstants.ACCOUNT_PATH)
                .when(consentValidateDataMock).getRequestPath();
        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        when(openBankingCDSConfigParserMock.getConfiguration()).thenReturn(configs);
        resourceParams.put("httpMethod", "POST");
        doReturn(resourceParams).when(consentValidateDataMock).getResourceParams();

        ConsentValidationResult consentValidationResult = new ConsentValidationResult();
        cdsConsentValidator.validate(consentValidateDataMock, consentValidationResult);

        Assert.assertTrue(consentValidationResult.isValid());
    }

    @Test(dependsOnMethods = "testValidateAccountRetrievalForValidPOSTRequests", priority = 1)
    public void testValidateAccountRetrievalForInvalidPOSTRequests() throws ParseException {

        doReturn(detailedConsentResourceMock).when(consentValidateDataMock).getComprehensiveConsent();
        doReturn(CDSConsentValidateTestConstants
                .getDetailedConsentResource(CDSConsentValidateTestConstants.VALID_RECEIPT, "1234567")
                .getReceipt()).when(detailedConsentResourceMock).getReceipt();
        doReturn(CDSConsentValidateTestConstants
                .getDetailedConsentResource(CDSConsentValidateTestConstants.VALID_RECEIPT, "1234567")
                .getConsentMappingResources()).when(detailedConsentResourceMock).getConsentMappingResources();
        JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
        JSONObject payload = (JSONObject) parser
                .parse(CDSConsentValidateTestConstants.PAYLOAD);
        doReturn(payload).when(consentValidateDataMock).getPayload();
        doReturn(CDSConsentExtensionConstants.AUTHORIZED_STATUS).when(detailedConsentResourceMock).getCurrentStatus();
        doReturn(CDSConsentValidateTestConstants.ACCOUNT_PATH)
                .when(consentValidateDataMock).getRequestPath();
        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        when(openBankingCDSConfigParserMock.getConfiguration()).thenReturn(configs);
        doReturn(resourceParams).when(consentValidateDataMock).getResourceParams();

        ConsentValidationResult consentValidationResult = new ConsentValidationResult();
        cdsConsentValidator.validate(consentValidateDataMock, consentValidationResult);

        Assert.assertFalse(consentValidationResult.isValid());
        Assert.assertEquals(consentValidationResult.getErrorCode(),
                "urn:au-cds:error:cds-banking:Authorisation/InvalidBankingAccount");
        Assert.assertEquals(consentValidationResult.getHttpCode(), 422);
    }

    @Test
    public void testValidateAccountRetrievalWithInvalidMetadataCache() {
        doReturn(detailedConsentResourceMock).when(consentValidateDataMock).getComprehensiveConsent();
        doReturn(CDSConsentValidateTestConstants
                .getDetailedConsentResource(CDSConsentValidateTestConstants.VALID_RECEIPT, "123456")
                .getReceipt()).when(detailedConsentResourceMock).getReceipt();
        doReturn(CDSConsentExtensionConstants.AUTHORIZED_STATUS).when(detailedConsentResourceMock).getCurrentStatus();
        doReturn(CDSConsentValidateTestConstants.ACCOUNT_PATH).when(consentValidateDataMock).getRequestPath();
        doReturn("client-id").when(consentValidateDataMock).getClientId();

        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        when(openBankingCDSConfigParserMock.getConfiguration()).thenReturn(configs);
        when(openBankingCDSConfigParserMock.isMetadataCacheEnabled()).thenReturn(true);

        PowerMockito.mockStatic(MetadataService.class);
        PowerMockito.when(MetadataService.shouldDiscloseCDRData(Mockito.anyString()))
                .thenReturn(new MetadataValidationResponse(ErrorConstants.AUErrorEnum.INVALID_ADR_STATUS.getDetail()));

        ConsentValidationResult consentValidationResult = new ConsentValidationResult();
        cdsConsentValidator.validate(consentValidateDataMock, consentValidationResult);

        Assert.assertFalse(consentValidationResult.isValid());
        Assert.assertEquals(consentValidationResult.getErrorCode(), ErrorConstants.AUErrorEnum
                .INVALID_ADR_STATUS.getCode());
        Assert.assertEquals(consentValidationResult.getHttpCode(), ErrorConstants.AUErrorEnum.INVALID_ADR_STATUS
                .getHttpCode());
    }

    @Test
    public void testRemoveInactiveSecondaryUserAccountConsentMappingsForBlockedLegalEntityExists()
            throws OpenBankingException {

        doReturn(detailedConsentResourceMock).when(consentValidateDataMock).getComprehensiveConsent();
        doReturn(CDSConsentValidateTestConstants
                .getDetailedConsentResource(CDSConsentValidateTestConstants.VALID_RECEIPT, "123456")
                .getReceipt()).when(detailedConsentResourceMock).getReceipt();
        doReturn(CDSConsentExtensionConstants.AUTHORIZED_STATUS).when(detailedConsentResourceMock).getCurrentStatus();
        doReturn(CDSConsentValidateTestConstants.ACCOUNT_PATH).when(consentValidateDataMock).getRequestPath();
        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        when(openBankingCDSConfigParserMock.getConfiguration()).thenReturn(configs);

        when(openBankingCDSConfigParserMock.isCeasingSecondaryUserSharingEnabled()).thenReturn(true);

        ArrayList<ConsentMappingResource> consentMappingResourceList = new ArrayList<>();
        consentMappingResourceList.add(consentMappingResourceMock);
        when(consentValidateDataMock.getComprehensiveConsent().getConsentMappingResources()).
                thenReturn(consentMappingResourceList);

        PowerMockito.mockStatic(CDSConsentExtensionUtils.class);
        when(CDSConsentExtensionUtils.isLegalEntityBlockedForAccountAndUser
                (Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        ConsentValidationResult consentValidationResult = new ConsentValidationResult();
        cdsConsentValidator.validate(consentValidateDataMock, consentValidationResult);

        Assert.assertTrue(consentValidationResult.isValid());
    }

    @Test
    public void testRemoveInactiveSecondaryUserAccountConsentMappingsForBlockedLegalEntityNotExists()
            throws OpenBankingException {

        doReturn(detailedConsentResourceMock).when(consentValidateDataMock).getComprehensiveConsent();
        doReturn(CDSConsentValidateTestConstants
                .getDetailedConsentResource(CDSConsentValidateTestConstants.VALID_RECEIPT, "123456")
                .getReceipt()).when(detailedConsentResourceMock).getReceipt();
        doReturn(CDSConsentExtensionConstants.AUTHORIZED_STATUS).when(detailedConsentResourceMock).getCurrentStatus();
        doReturn(CDSConsentValidateTestConstants.ACCOUNT_PATH).when(consentValidateDataMock).getRequestPath();
        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        when(openBankingCDSConfigParserMock.getConfiguration()).thenReturn(configs);

        when(openBankingCDSConfigParserMock.isCeasingSecondaryUserSharingEnabled()).thenReturn(true);

        ArrayList<ConsentMappingResource> consentMappingResourceList = new ArrayList<>();
        consentMappingResourceList.add(consentMappingResourceMock);
        when(consentValidateDataMock.getComprehensiveConsent().getConsentMappingResources()).
                thenReturn(consentMappingResourceList);

        PowerMockito.mockStatic(CDSConsentExtensionUtils.class);
        when(CDSConsentExtensionUtils.isLegalEntityBlockedForAccountAndUser
                (Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(false);

        ConsentValidationResult consentValidationResult = new ConsentValidationResult();
        cdsConsentValidator.validate(consentValidateDataMock, consentValidationResult);

        Assert.assertTrue(consentValidationResult.isValid());
    }
}
