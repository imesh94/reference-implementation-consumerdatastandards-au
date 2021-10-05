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

package com.wso2.openbanking.cds.common.metadata.status.validator.service;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.common.metadata.status.validator.cache.MetadataCache;
import com.wso2.openbanking.cds.common.metadata.status.validator.cache.MetadataCacheKey;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Test class for MetadataService
 */
@PrepareForTest({MetadataCache.class})
public class MetadataServiceTest extends PowerMockTestCase {
    private static final String CLIENT_ID_ACTIVE_ACTIVE = "client-id-active-active";
    private static final String CLIENT_ID_ACTIVE_INACTIVE = "client-id-active-inactive";
    private static final String CLIENT_ID_SUSPENDED_ACTIVE = "client-id-suspended-active";

    @BeforeMethod
    public void init() throws OpenBankingException {
        HashMap<String, String> testStatusMap = new HashMap<>();
        testStatusMap.put(CLIENT_ID_ACTIVE_ACTIVE, "ACTIVE");
        testStatusMap.put(CLIENT_ID_ACTIVE_INACTIVE, "INACTIVE");
        testStatusMap.put(CLIENT_ID_SUSPENDED_ACTIVE, "SUSPENDED");

        MetadataCache metadataCacheMock = Mockito.mock(MetadataCache.class);
        Mockito.when(metadataCacheMock.getFromCacheOrRetrieve(Mockito.any(MetadataCacheKey.class), Mockito.any()))
                .thenReturn(testStatusMap);

        PowerMockito.mockStatic(MetadataCache.class);
        PowerMockito.when(MetadataCache.getInstance()).thenReturn(metadataCacheMock);
    }

    @Test
    public void testShouldDiscloseCDRData() {
        Assert.assertTrue(MetadataService.shouldDiscloseCDRData(CLIENT_ID_ACTIVE_ACTIVE));
        Assert.assertFalse(MetadataService.shouldDiscloseCDRData(CLIENT_ID_ACTIVE_INACTIVE));
    }

    @Test
    public void testShouldFacilitateConsentAuthorisation() {
        Assert.assertTrue(MetadataService.shouldFacilitateConsentAuthorisation(CLIENT_ID_ACTIVE_ACTIVE));
        Assert.assertFalse(MetadataService.shouldFacilitateConsentAuthorisation(CLIENT_ID_ACTIVE_INACTIVE));
    }

    @Test
    public void testShouldFacilitateConsentWithdrawal() {
        Assert.assertTrue(MetadataService.shouldFacilitateConsentWithdrawal(CLIENT_ID_ACTIVE_ACTIVE));
        Assert.assertFalse(MetadataService.shouldFacilitateConsentWithdrawal(CLIENT_ID_ACTIVE_INACTIVE));
        Assert.assertFalse(MetadataService.shouldFacilitateConsentWithdrawal(CLIENT_ID_SUSPENDED_ACTIVE));
    }
}
