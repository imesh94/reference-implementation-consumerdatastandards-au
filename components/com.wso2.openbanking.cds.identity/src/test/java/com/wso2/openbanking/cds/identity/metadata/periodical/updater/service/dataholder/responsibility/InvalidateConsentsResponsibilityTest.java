/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 *
 */

package com.wso2.openbanking.cds.identity.metadata.periodical.updater.service.dataholder.responsibility;

import com.wso2.openbanking.accelerator.common.exception.ConsentManagementException;
import com.wso2.openbanking.accelerator.consent.mgt.service.ConsentCoreService;
import com.wso2.openbanking.cds.common.metadata.periodical.updater.utils.DataRecipientStatusEnum;
import com.wso2.openbanking.cds.common.metadata.periodical.updater.utils.SoftwareProductStatusEnum;
import com.wso2.openbanking.cds.identity.metadata.periodical.updater.internal.ServiceHolder;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.application.common.model.InboundAuthenticationConfig;
import org.wso2.carbon.identity.application.common.model.InboundAuthenticationRequestConfig;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.application.common.model.User;

/**
 * Test class for InvalidateConsentsResponsibility
 */
@PrepareForTest({ServiceHolder.class})
public class InvalidateConsentsResponsibilityTest extends PowerMockTestCase {

    private InvalidateConsentsResponsibility uut;
    private ServiceProvider serviceProviderMock;

    @BeforeClass
    public void init() {
        mockServiceProvider();
        this.uut = new InvalidateConsentsResponsibility(DataRecipientStatusEnum.REVOKED.toString(),
                SoftwareProductStatusEnum.REMOVED.toString(), this.serviceProviderMock);
    }

    @Test
    public void testShouldPerform() {
        Assert.assertTrue(this.uut.shouldPerform());

        InvalidateConsentsResponsibility responsibility = new InvalidateConsentsResponsibility("",
                SoftwareProductStatusEnum.ACTIVE.toString(), null);
        Assert.assertFalse(responsibility.shouldPerform());
    }

    @Test
    public void testPerform() throws ConsentManagementException {
        ConsentCoreService consentCoreServiceMock = Mockito.mock(ConsentCoreService.class);
        Mockito.doReturn(true)
                .doThrow(ConsentManagementException.class)
                .when(consentCoreServiceMock).revokeExistingApplicableConsents(Mockito.anyString(), Mockito.anyString(),
                        Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        mockServiceHolder(consentCoreServiceMock);

        this.uut.perform();
        // test exception flow, should do nothing
        this.uut.perform();
    }

    private void mockServiceProvider() {
        InboundAuthenticationRequestConfig reqConfigMock = Mockito.mock(InboundAuthenticationRequestConfig.class);
        Mockito.when(reqConfigMock.getInboundAuthKey()).thenReturn("s3qziY_X47dyXfsNjFyevKaJYkUa");

        InboundAuthenticationConfig configMock = Mockito.mock(InboundAuthenticationConfig.class);
        Mockito.when(configMock.getInboundAuthenticationRequestConfigs())
                .thenReturn(new InboundAuthenticationRequestConfig[]{reqConfigMock});

        User ownerMock = Mockito.mock(User.class);
        Mockito.when(ownerMock.getUserName()).thenReturn("admin@wso2.com");
        Mockito.when(ownerMock.getTenantDomain()).thenReturn("carbon.super");

        serviceProviderMock = Mockito.mock(ServiceProvider.class);
        Mockito.when(serviceProviderMock.getOwner()).thenReturn(ownerMock);
        Mockito.when(serviceProviderMock.getInboundAuthenticationConfig()).thenReturn(configMock);
    }

    private void mockServiceHolder(ConsentCoreService consentCoreServiceMock) {
        ServiceHolder serviceHolderMock = Mockito.mock(ServiceHolder.class);
        Mockito.when(serviceHolderMock.getConsentCoreService()).thenReturn(consentCoreServiceMock);

        PowerMockito.mockStatic(ServiceHolder.class);
        PowerMockito.when(ServiceHolder.getInstance()).thenReturn(serviceHolderMock);
    }
}
