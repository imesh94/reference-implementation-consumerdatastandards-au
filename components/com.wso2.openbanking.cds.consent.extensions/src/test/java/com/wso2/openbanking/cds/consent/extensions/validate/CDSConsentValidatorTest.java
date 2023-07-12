/*
 * Copyright (c) 2021-2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.consent.extensions.validate;

import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataServiceImpl;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.consent.extensions.validate.model.ConsentValidateData;
import com.wso2.openbanking.accelerator.consent.extensions.validate.model.ConsentValidationResult;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.AuthorizationResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentMappingResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.DetailedConsentResource;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.common.error.handling.util.ErrorConstants;
import com.wso2.openbanking.cds.common.metadata.domain.MetadataValidationResponse;
import com.wso2.openbanking.cds.common.metadata.status.validator.service.MetadataService;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import com.wso2.openbanking.cds.consent.extensions.util.CDSConsentExtensionsUtil;
import com.wso2.openbanking.cds.consent.extensions.util.CDSConsentValidateTestConstants;
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
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Test class for CDS consent Validator
 */
@PrepareForTest({OpenBankingCDSConfigParser.class, MetadataService.class, CDSConsentExtensionsUtil.class,
        AccountMetadataServiceImpl.class})
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
    @Mock
    AccountMetadataServiceImpl accountMetadataServiceMock;
    Map<String, Object> configs = new HashMap<>();
    Map<String, String> resourceParams = new HashMap<>();

    @BeforeClass
    public void initClass() {
        cdsConsentValidator = new CDSConsentValidator();
        consentValidateDataMock = mock(ConsentValidateData.class);
        detailedConsentResourceMock = mock(DetailedConsentResource.class);
        consentMappingResourceMock = mock(ConsentMappingResource.class);
        openBankingCDSConfigParserMock = mock(OpenBankingCDSConfigParser.class);
        accountMetadataServiceMock = mock(AccountMetadataServiceImpl.class);
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
        mockStatic(OpenBankingCDSConfigParser.class);
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
        mockStatic(OpenBankingCDSConfigParser.class);
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
        mockStatic(OpenBankingCDSConfigParser.class);
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

        mockStatic(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        when(openBankingCDSConfigParserMock.getConfiguration()).thenReturn(configs);

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

        mockStatic(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        when(openBankingCDSConfigParserMock.getConfiguration()).thenReturn(configs);

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
        mockStatic(OpenBankingCDSConfigParser.class);
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
        mockStatic(OpenBankingCDSConfigParser.class);
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

        mockStatic(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        when(openBankingCDSConfigParserMock.getConfiguration()).thenReturn(configs);
        when(openBankingCDSConfigParserMock.isMetadataCacheEnabled()).thenReturn(true);

        mockStatic(MetadataService.class);
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
        mockStatic(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        when(openBankingCDSConfigParserMock.getConfiguration()).thenReturn(configs);

        when(openBankingCDSConfigParserMock.isCeasingSecondaryUserSharingEnabled()).thenReturn(true);

        ArrayList<ConsentMappingResource> consentMappingResourceList = new ArrayList<>();
        consentMappingResourceList.add(consentMappingResourceMock);
        when(consentValidateDataMock.getComprehensiveConsent().getConsentMappingResources()).
                thenReturn(consentMappingResourceList);

        mockStatic(CDSConsentExtensionsUtil.class);
        when(CDSConsentExtensionsUtil.isLegalEntityBlockedForAccountAndUser
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
        mockStatic(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        when(openBankingCDSConfigParserMock.getConfiguration()).thenReturn(configs);

        when(openBankingCDSConfigParserMock.isCeasingSecondaryUserSharingEnabled()).thenReturn(true);

        ArrayList<ConsentMappingResource> consentMappingResourceList = new ArrayList<>();
        consentMappingResourceList.add(consentMappingResourceMock);
        when(consentValidateDataMock.getComprehensiveConsent().getConsentMappingResources()).
                thenReturn(consentMappingResourceList);

        mockStatic(CDSConsentExtensionsUtil.class);
        when(CDSConsentExtensionsUtil.isLegalEntityBlockedForAccountAndUser
                (Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(false);

        ConsentValidationResult consentValidationResult = new ConsentValidationResult();
        cdsConsentValidator.validate(consentValidateDataMock, consentValidationResult);

        Assert.assertTrue(consentValidationResult.isValid());
    }

    @Test
    public void testRemoveInactiveDOMSAccountConsentMappingsForSharableJointAccounts() throws OpenBankingException {
        String testAccountId = "111-222";
        doReturn(detailedConsentResourceMock).when(consentValidateDataMock).getComprehensiveConsent();
        doReturn(CDSConsentValidateTestConstants
                .getDetailedConsentResource(CDSConsentValidateTestConstants.VALID_RECEIPT, testAccountId)
                .getReceipt()).when(detailedConsentResourceMock).getReceipt();
        doReturn(CDSConsentExtensionConstants.AUTHORIZED_STATUS).when(detailedConsentResourceMock).getCurrentStatus();
        doReturn(CDSConsentValidateTestConstants.ACCOUNT_PATH).when(consentValidateDataMock).getRequestPath();
        mockStatic(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        when(openBankingCDSConfigParserMock.getConfiguration()).thenReturn(configs);
        when(openBankingCDSConfigParserMock.getDOMSEnabled()).thenReturn(true);

        // Adding a ConsentMappingResource for the specified account ID and mocking its
        // retrieval from ComprehensiveConsent.
        ArrayList<ConsentMappingResource> consentMappingResourceList = new ArrayList<>();
        consentMappingResourceList.add(CDSConsentValidateTestConstants.getConsentMappingResourceForDOMS(
                CDSConsentExtensionConstants.ACCOUNT_ID));
        when(detailedConsentResourceMock.getConsentMappingResources()).thenReturn(consentMappingResourceList);

        // Creating and mocking retrieval of a list of AuthorizationResources.
        ArrayList<AuthorizationResource> authorizationResourceList = new ArrayList<>();
        authorizationResourceList.add(CDSConsentValidateTestConstants.getAuthorizationResourceForDOMS());
        when(detailedConsentResourceMock.getAuthorizationResources()).thenReturn(authorizationResourceList);
        mockStatic(AccountMetadataServiceImpl.class);
        when(AccountMetadataServiceImpl.getInstance()).thenReturn(accountMetadataServiceMock);
        Map<String, String> accountMetadataMap = new HashMap<>();
        when(accountMetadataServiceMock.getAccountMetadataMap(testAccountId)).thenReturn(accountMetadataMap);

        // Checking if the DOMS status for the specified account ID is eligible for data sharing and storing the
        // result in the boolean variable isJointAccountSharable.
        boolean isJointAccountSharable = CDSConsentExtensionsUtil.isDOMSStatusEligibleForDataSharing(testAccountId);
        ConsentValidationResult consentValidationResult = new ConsentValidationResult();
        cdsConsentValidator.validate(consentValidateDataMock, consentValidationResult);

        Assert.assertTrue(isJointAccountSharable);
        Assert.assertTrue(consentValidationResult.isValid());
    }

    @Test
    public void testRemoveInactiveDOMSAccountConsentMappingsForNonSharableJointAccounts() throws OpenBankingException {
        String testAccountId = "123-456";
        doReturn(detailedConsentResourceMock).when(consentValidateDataMock).getComprehensiveConsent();
        doReturn(CDSConsentValidateTestConstants
                .getDetailedConsentResource(CDSConsentValidateTestConstants.VALID_RECEIPT, testAccountId)
                .getReceipt()).when(detailedConsentResourceMock).getReceipt();
        doReturn(CDSConsentExtensionConstants.AUTHORIZED_STATUS).when(detailedConsentResourceMock).getCurrentStatus();
        doReturn(CDSConsentValidateTestConstants.ACCOUNT_PATH).when(consentValidateDataMock).getRequestPath();
        mockStatic(OpenBankingCDSConfigParser.class);
        when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        when(openBankingCDSConfigParserMock.getConfiguration()).thenReturn(configs);
        when(openBankingCDSConfigParserMock.getDOMSEnabled()).thenReturn(true);

        // Adding a ConsentMappingResource for the specified account ID and mocking its
        // retrieval from ComprehensiveConsent.
        ArrayList<ConsentMappingResource> consentMappingResourceList = new ArrayList<>();
        consentMappingResourceList.add(CDSConsentValidateTestConstants.
                getConsentMappingResourceForDOMS(CDSConsentExtensionConstants.ACCOUNT_ID));
        when(detailedConsentResourceMock.getConsentMappingResources()).thenReturn(consentMappingResourceList);

        // Creating and mocking retrieval of a list of AuthorizationResources.
        ArrayList<AuthorizationResource> authorizationResourceList = new ArrayList<>();
        authorizationResourceList.add(CDSConsentValidateTestConstants.getAuthorizationResourceForDOMS());
        when(detailedConsentResourceMock.getAuthorizationResources()).thenReturn(authorizationResourceList);
        mockStatic(AccountMetadataServiceImpl.class);
        when(AccountMetadataServiceImpl.getInstance()).thenReturn(accountMetadataServiceMock);
        Map<String, String> accountMetadataMap = new HashMap<>();
        accountMetadataMap.put("DISCLOSURE_OPTIONS_STATUS", "no-sharing");
        when(accountMetadataServiceMock.getAccountMetadataMap(testAccountId)).thenReturn(accountMetadataMap);

        // Checking if the DOMS status for the specified account ID is eligible for data sharing and storing the
        // result in the boolean variable isJointAccountSharable.
        boolean isJointAccountSharable = CDSConsentExtensionsUtil.isDOMSStatusEligibleForDataSharing(testAccountId);
        ConsentValidationResult consentValidationResult = new ConsentValidationResult();
        cdsConsentValidator.validate(consentValidateDataMock, consentValidationResult);

        Assert.assertFalse(isJointAccountSharable);
        Assert.assertTrue(consentValidationResult.isValid());
    }
}
