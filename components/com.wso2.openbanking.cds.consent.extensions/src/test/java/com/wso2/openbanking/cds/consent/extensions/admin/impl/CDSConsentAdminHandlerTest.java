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

package com.wso2.openbanking.cds.consent.extensions.admin.impl;

import com.wso2.openbanking.accelerator.common.exception.ConsentManagementException;
import com.wso2.openbanking.accelerator.consent.extensions.admin.model.ConsentAdminData;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.AuthorizationResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentMappingResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.DetailedConsentResource;
import com.wso2.openbanking.accelerator.consent.mgt.service.ConsentCoreService;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants.AUTH_RESOURCE_TYPE_PRIMARY;
import static com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants.AUTH_RESOURCE_TYPE_SECONDARY;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for CDSConsentAdminHandler
 */
public class CDSConsentAdminHandlerTest {

    public static final String USER_ID_SECONDARY = "test-secondary-user-id";
    public static final String USER_ID_PRIMARY = "test-primary-user-id";
    public static final String AUTH_ID_PRIMARY = "test-primary-auth-id";
    public static final String AUTH_ID_SECONDARY = "test-secondary-auth-id";
    public static final String JOINT_ACCOUNT_ID = "test-joint-account-id";
    public static final String MAPPING_ID_1 = "test-mapping-id-1";
    public static final String MAPPING_ID_2 = "test-mapping-id-2";
    public static final String MAPPING_ID_3 = "test-mapping-id-3";

    private CDSConsentAdminHandler uut;
    private ConsentCoreService consentCoreServiceMock;

    @BeforeClass
    public void setUp() throws ConsentManagementException {
        //mock
        consentCoreServiceMock = mock(ConsentCoreService.class);

        //when
        AuthorizationResource authResource1 = new AuthorizationResource();
        authResource1.setAuthorizationID(AUTH_ID_PRIMARY);
        authResource1.setUserID(USER_ID_PRIMARY);
        authResource1.setAuthorizationType(AUTH_RESOURCE_TYPE_PRIMARY);

        AuthorizationResource authResource2 = new AuthorizationResource();
        authResource2.setAuthorizationID(AUTH_ID_SECONDARY);
        authResource2.setUserID(USER_ID_SECONDARY);
        authResource2.setAuthorizationType(AUTH_RESOURCE_TYPE_SECONDARY);

        ConsentMappingResource mapping1 = new ConsentMappingResource();
        mapping1.setMappingID(MAPPING_ID_1);
        mapping1.setAccountID(JOINT_ACCOUNT_ID);
        mapping1.setAuthorizationID(AUTH_ID_PRIMARY);

        ConsentMappingResource mapping2 = new ConsentMappingResource();
        mapping2.setMappingID(MAPPING_ID_2);
        mapping2.setAccountID("test-regular-account-id");
        mapping2.setAuthorizationID(AUTH_ID_PRIMARY);

        ConsentMappingResource mapping3 = new ConsentMappingResource();
        mapping3.setMappingID(MAPPING_ID_3);
        mapping3.setAccountID(JOINT_ACCOUNT_ID);
        mapping3.setAuthorizationID(AUTH_ID_SECONDARY);

        DetailedConsentResource detailedConsentResource = new DetailedConsentResource();
        detailedConsentResource.setAuthorizationResources(new ArrayList<>(Arrays.asList(authResource1, authResource2)));
        detailedConsentResource
                .setConsentMappingResources(new ArrayList<>(Arrays.asList(mapping1, mapping2, mapping3)));

        doReturn(true).when(consentCoreServiceMock).deactivateAccountMappings(any(ArrayList.class));
        doReturn(detailedConsentResource).when(consentCoreServiceMock).getDetailedConsent(anyString());
        doReturn(true).when(consentCoreServiceMock)
                .revokeConsent(anyString(), anyString(), anyString(), anyString());

        this.uut = new CDSConsentAdminHandler(consentCoreServiceMock, null);
    }

    @Test
    public void testHandleRevoke() throws ConsentManagementException {
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put(CDSConsentAdminHandler.CONSENT_ID,
                new ArrayList<>(Collections.singletonList("test-consent-id")));
        queryParams.put(CDSConsentAdminHandler.USER_ID, new ArrayList<>(Collections.singletonList(USER_ID_SECONDARY)));

        ConsentAdminData consentAdminDataMock = mock(ConsentAdminData.class);
        when(consentAdminDataMock.getQueryParams()).thenReturn(queryParams);

        uut.handleRevoke(consentAdminDataMock);

        ArgumentCaptor<ArrayList> argumentCaptor = ArgumentCaptor.forClass(ArrayList.class);
        verify(consentCoreServiceMock).deactivateAccountMappings(argumentCaptor.capture());
        ArrayList capturedArgument = argumentCaptor.getValue();

        assertTrue(capturedArgument.contains(MAPPING_ID_1));
        assertTrue(capturedArgument.contains(MAPPING_ID_3));
        verify(consentAdminDataMock).setResponseStatus(ResponseStatus.NO_CONTENT);
    }

    @Test
    public void testHandleRevokeForPrimaryUser() {
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put(CDSConsentAdminHandler.CONSENT_ID,
                new ArrayList<>(Collections.singletonList("test-consent-id")));
        queryParams.put(CDSConsentAdminHandler.USER_ID, new ArrayList<>(Collections.singletonList(USER_ID_PRIMARY)));

        ConsentAdminData consentAdminDataMock = mock(ConsentAdminData.class);
        when(consentAdminDataMock.getQueryParams()).thenReturn(queryParams);

        uut.handleRevoke(consentAdminDataMock);

        verify(consentAdminDataMock).setResponseStatus(ResponseStatus.NO_CONTENT);
    }

    @Test(description = "if consent id is missing in query params, should throw ConsentException",
            expectedExceptions = ConsentException.class)
    public void testHandleRevokeForConsentException() {
        ConsentAdminData consentAdminDataMock = mock(ConsentAdminData.class);
        when(consentAdminDataMock.getQueryParams()).thenReturn(Collections.EMPTY_MAP);

        uut.handleRevoke(consentAdminDataMock);
    }
}
