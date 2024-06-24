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
package com.wso2.openbanking.cds.consent.extensions.authorize.impl.persist;

import com.wso2.openbanking.accelerator.common.exception.ConsentManagementException;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentPersistData;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.AuthorizationResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.DetailedConsentResource;
import com.wso2.openbanking.accelerator.consent.mgt.service.impl.ConsentCoreServiceImpl;
import com.wso2.openbanking.accelerator.data.publisher.common.util.OBDataPublisherUtil;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.CDSConsentCommonUtil;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import com.wso2.openbanking.cds.consent.extensions.util.CDSConsentAuthorizeTestConstants;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for CDS Consent Persistence.
 */
@PrepareForTest({CDSConsentCommonUtil.class, OBDataPublisherUtil.class})
@PowerMockIgnore({"com.wso2.openbanking.accelerator.consent.extensions.common.*", "jdk.internal.reflect.*"})
public class CDSConsentPersistStepTests extends PowerMockTestCase {

    private static CDSConsentPersistStep cdsConsentPersistStep;
    private static ConsentPersistData consentPersistDataMock;
    private static ConsentData consentDataMock;
    private static ConsentResource consentResourceMock;
    private static AuthorizationResource authorizationResourceMock;
    private static ConsentCoreServiceImpl consentCoreServiceMock;
    private static Map<String, Object> consentDataMap;
    private static Map<String, String> browserCookies;

    @BeforeClass
    public void initTest() {

        consentDataMap = new HashMap<>();
        browserCookies = new HashMap<>();
        browserCookies.put("commonAuthId", "DummyCommonAuthId");
        consentDataMap.put("permissions",
                new ArrayList<>(Arrays.asList(CDSConsentAuthorizeTestConstants.PERMISSION_SCOPES.split(" "))));
        consentDataMap.put("expirationDateTime", OffsetDateTime.now(ZoneOffset.UTC));
        consentDataMap.put("sharing_duration_value", (long) 7600000);
        consentDataMap.put("requestUriKey", "abc123");
        cdsConsentPersistStep = new CDSConsentPersistStep();
        consentPersistDataMock = mock(ConsentPersistData.class);
        consentDataMock = mock(ConsentData.class);
        consentResourceMock = mock(ConsentResource.class);
        authorizationResourceMock = mock(AuthorizationResource.class);
        consentCoreServiceMock = mock(ConsentCoreServiceImpl.class);
    }

    @BeforeMethod
    public void initMethod() throws Exception {
        PowerMockito.mockStatic(CDSConsentCommonUtil.class);
        when(CDSConsentCommonUtil.getUserIdWithTenantDomain(anyString())).thenReturn("user1@wso2.com@carbon.super");

        PowerMockito.mockStatic(OBDataPublisherUtil.class);
        PowerMockito.doNothing().when(OBDataPublisherUtil.class, "publishData", Mockito.anyString(),
                Mockito.anyString(), Mockito.anyObject());
    }

    @Test
    public void testConsentPersistWithApproval() throws Exception {

        ArrayList<AuthorizationResource> authorizationResources = new ArrayList<>();
        authorizationResources.add(authorizationResourceMock);
        doReturn(consentDataMock).when(consentPersistDataMock).getConsentData();
        doReturn(consentDataMap).when(consentDataMock).getMetaDataMap();
        doReturn(consentResourceMock).when(consentDataMock).getConsentResource();
        doReturn(authorizationResourceMock).when(consentDataMock).getAuthResource();
        JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
        JSONObject payload = (JSONObject) parser
                .parse(CDSConsentAuthorizeTestConstants.PAYLOAD);
        doReturn(payload).when(consentPersistDataMock).getPayload();
        doReturn(browserCookies).when(consentPersistDataMock).getBrowserCookies();
        doReturn(true).when(consentPersistDataMock).getApproval();

        MockCDSConsentPersistSuccess mockCDSConsentPersist = new MockCDSConsentPersistSuccess();

        try {
            mockCDSConsentPersist.execute(consentPersistDataMock);
        } catch (ConsentException e) {
            Assert.fail("should not throw exception");
        }
    }

    @Test(expectedExceptions = ConsentException.class)
    public void testConsentPersistErrorForConsentCreation() throws Exception {

        doReturn(consentDataMock).when(consentPersistDataMock).getConsentData();
        doReturn(consentDataMap).when(consentDataMock).getMetaDataMap();
        doReturn(consentResourceMock).when(consentDataMock).getConsentResource();
        doReturn(authorizationResourceMock).when(consentDataMock).getAuthResource();
        JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
        JSONObject payload = (JSONObject) parser
                .parse(CDSConsentAuthorizeTestConstants.PAYLOAD);
        doReturn(payload).when(consentPersistDataMock).getPayload();
        doReturn(browserCookies).when(consentPersistDataMock).getBrowserCookies();
        doReturn(true).when(consentPersistDataMock).getApproval();

        cdsConsentPersistStep.execute(consentPersistDataMock);
    }

    @Test(expectedExceptions = ConsentException.class)
    public void testConsentPersistErrorForNullAccountData() throws Exception {

        doReturn(consentDataMock).when(consentPersistDataMock).getConsentData();
        doReturn(consentDataMap).when(consentDataMock).getMetaDataMap();
        doReturn(consentResourceMock).when(consentDataMock).getConsentResource();
        doReturn(authorizationResourceMock).when(consentDataMock).getAuthResource();
        JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
        JSONObject payload = (JSONObject) parser
                .parse(CDSConsentAuthorizeTestConstants.PAYLOAD_WITHOUT_ACCOUNT_DATA);
        doReturn(payload).when(consentPersistDataMock).getPayload();
        doReturn(true).when(consentPersistDataMock).getApproval();

        MockCDSConsentPersistSuccess mockCDSConsentPersist = new MockCDSConsentPersistSuccess();
        mockCDSConsentPersist.execute(consentPersistDataMock);
    }

    @Test(expectedExceptions = ConsentException.class)
    public void testConsentPersistErrorForNonStringAccountData() throws Exception {

        doReturn(consentDataMock).when(consentPersistDataMock).getConsentData();
        doReturn(consentDataMap).when(consentDataMock).getMetaDataMap();
        doReturn(consentResourceMock).when(consentDataMock).getConsentResource();
        doReturn(authorizationResourceMock).when(consentDataMock).getAuthResource();
        JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
        JSONObject payload = (JSONObject) parser
                .parse(CDSConsentAuthorizeTestConstants.PAYLOAD_NON_STRING_ACCOUNT_DATA);
        doReturn(payload).when(consentPersistDataMock).getPayload();
        doReturn(true).when(consentPersistDataMock).getApproval();

        MockCDSConsentPersistSuccess mockCDSConsentPersist = new MockCDSConsentPersistSuccess();
        mockCDSConsentPersist.execute(consentPersistDataMock);
    }

    @Test(expectedExceptions = ConsentException.class)
    public void testConsentPersistErrorForNullAuthResource() throws Exception {

        doReturn(consentDataMock).when(consentPersistDataMock).getConsentData();
        doReturn(consentDataMap).when(consentDataMock).getMetaDataMap();
        doReturn(consentResourceMock).when(consentDataMock).getConsentResource();
        doReturn(authorizationResourceMock).when(consentDataMock).getAuthResource();
        JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
        JSONObject payload = (JSONObject) parser
                .parse(CDSConsentAuthorizeTestConstants.PAYLOAD_NON_STRING_ACCOUNT_DATA);
        doReturn(payload).when(consentPersistDataMock).getPayload();
        doReturn(true).when(consentPersistDataMock).getApproval();

        MockCDSConsentPersistError mockCDSConsentPersistError = new MockCDSConsentPersistError();
        mockCDSConsentPersistError.execute(consentPersistDataMock);
    }

    @Test
    public void testConsentPersistWithAmendment() throws Exception {

        doReturn(null).when(consentCoreServiceMock).amendDetailedConsent(anyString(), anyString(),
                anyLong(), anyString(), any(HashMap.class), anyString(), any(HashMap.class),
                anyString(), any(HashMap.class));

        doReturn(new DetailedConsentResource()).when(consentCoreServiceMock).getDetailedConsent(anyString());
        doNothing().when(consentCoreServiceMock).revokeTokens(any(DetailedConsentResource.class), anyString());
        doReturn(true).when(consentCoreServiceMock).updateAccountMappingStatus(any(ArrayList.class), anyString());

        Map<Object, Object> consentDataMapClone = new HashMap<>(consentDataMap);
        consentDataMapClone.put(CDSConsentExtensionConstants.IS_CONSENT_AMENDMENT, true);
        consentDataMapClone.put(CDSConsentExtensionConstants.CDR_ARRANGEMENT_ID, UUID.randomUUID().toString());
        consentDataMapClone.put(CDSConsentExtensionConstants.AUTH_RESOURCE_ID, UUID.randomUUID().toString());
        consentDataMapClone.put(CDSConsentExtensionConstants.AUTH_RESOURCE_STATUS, "Authorized");

        doReturn(consentDataMock).when(consentPersistDataMock).getConsentData();
        doReturn(consentDataMapClone).when(consentDataMock).getMetaDataMap();
        doReturn(consentResourceMock).when(consentDataMock).getConsentResource();
        doReturn(authorizationResourceMock).when(consentDataMock).getAuthResource();
        JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
        JSONObject payload = (JSONObject) parser
                .parse(CDSConsentAuthorizeTestConstants.PAYLOAD);
        doReturn(payload).when(consentPersistDataMock).getPayload();
        doReturn(browserCookies).when(consentPersistDataMock).getBrowserCookies();
        doReturn(true).when(consentPersistDataMock).getApproval();

        MockCDSConsentPersistSuccess mockCDSConsentPersist = new MockCDSConsentPersistSuccess(consentCoreServiceMock);

        try {
            mockCDSConsentPersist.execute(consentPersistDataMock);
        } catch (ConsentException e) {
            Assert.fail("should not throw exception");
        }
    }
}

class MockCDSConsentPersistSuccess extends CDSConsentPersistStep {

    public MockCDSConsentPersistSuccess() {
    }

    public MockCDSConsentPersistSuccess(ConsentCoreServiceImpl consentCoreService) {
        super(consentCoreService);
    }

    @Override
    protected DetailedConsentResource createConsent(ConsentCoreServiceImpl consentCoreService,
                                                    ConsentResource requestedConsent, ConsentData consentData)
            throws ConsentManagementException {

        return CDSConsentAuthorizeTestConstants.getDetailedConsentResource();
    }

    @Override
    protected boolean bindUserAccountsToConsent(ConsentCoreServiceImpl consentCoreService,
                                                ConsentResource consentResource, ConsentData consentData,
                                                ArrayList<String> accountIdsString)
            throws ConsentManagementException {

        return true;
    }
}

class MockCDSConsentPersistError extends CDSConsentPersistStep {

    @Override
    protected DetailedConsentResource createConsent(ConsentCoreServiceImpl consentCoreService,
                                                    ConsentResource requestedConsent, ConsentData consentData)
            throws ConsentManagementException {

        return CDSConsentAuthorizeTestConstants.getDetailedConsentResource();
    }
}
