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
import com.wso2.openbanking.cds.common.metadata.periodical.updater.constants.MetadataConstants;
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
    private static final String CLIENT_ID_ACTIVE_REMOVED = "client-id-active-removed";
    private static final String CLIENT_ID_SUSPENDED_ACTIVE = "client-id-suspended-active";
    private static final String CLIENT_ID_SUSPENDED_INACTIVE = "client-id-suspended-inactive";
    private static final String CLIENT_ID_SUSPENDED_REMOVED = "client-id-suspended-removed";

    @BeforeMethod
    public void init() throws OpenBankingException {
        HashMap<String, String> adrStatusMap = new HashMap<>();
        adrStatusMap.put(CLIENT_ID_ACTIVE_ACTIVE, "ACTIVE");
        adrStatusMap.put(CLIENT_ID_ACTIVE_INACTIVE, "ACTIVE");
        adrStatusMap.put(CLIENT_ID_ACTIVE_REMOVED, "ACTIVE");
        adrStatusMap.put(CLIENT_ID_SUSPENDED_ACTIVE, "SUSPENDED");
        adrStatusMap.put(CLIENT_ID_SUSPENDED_INACTIVE, "SUSPENDED");
        adrStatusMap.put(CLIENT_ID_SUSPENDED_REMOVED, "SUSPENDED");

        HashMap<String, String> spStatusMap = new HashMap<>();
        spStatusMap.put(CLIENT_ID_ACTIVE_ACTIVE, "ACTIVE");
        spStatusMap.put(CLIENT_ID_ACTIVE_INACTIVE, "INACTIVE");
        spStatusMap.put(CLIENT_ID_ACTIVE_REMOVED, "REMOVED");
        spStatusMap.put(CLIENT_ID_SUSPENDED_ACTIVE, "ACTIVE");
        spStatusMap.put(CLIENT_ID_SUSPENDED_INACTIVE, "INACTIVE");
        spStatusMap.put(CLIENT_ID_SUSPENDED_REMOVED, "REMOVED");

        MetadataCache metadataCacheMock = Mockito.mock(MetadataCache.class);
        Mockito.when(metadataCacheMock.getFromCacheOrRetrieve(Mockito
                        .eq(MetadataCacheKey.from(MetadataConstants.MAP_DATA_RECIPIENTS)), Mockito.any()))
                .thenReturn(adrStatusMap);
        Mockito.when(metadataCacheMock.getFromCacheOrRetrieve(Mockito
                        .eq(MetadataCacheKey.from(MetadataConstants.MAP_SOFTWARE_PRODUCTS)), Mockito.any()))
                .thenReturn(spStatusMap);

        PowerMockito.mockStatic(MetadataCache.class);
        PowerMockito.when(MetadataCache.getInstance()).thenReturn(metadataCacheMock);
    }

    @Test
    public void testShouldDiscloseCDRData() {
        Assert.assertTrue(MetadataService.shouldDiscloseCDRData(CLIENT_ID_ACTIVE_ACTIVE).isValid());
        Assert.assertFalse(MetadataService.shouldDiscloseCDRData(CLIENT_ID_ACTIVE_INACTIVE).isValid());
        Assert.assertFalse(MetadataService.shouldDiscloseCDRData(CLIENT_ID_SUSPENDED_INACTIVE).isValid());
    }

    @Test
    public void testShouldFacilitateConsentAuthorisation() {
        Assert.assertTrue(MetadataService.shouldFacilitateConsentAuthorisation(CLIENT_ID_ACTIVE_ACTIVE).isValid());
        Assert.assertFalse(MetadataService.shouldFacilitateConsentAuthorisation(CLIENT_ID_ACTIVE_INACTIVE).isValid());
        Assert.assertFalse(MetadataService
                .shouldFacilitateConsentAuthorisation(CLIENT_ID_SUSPENDED_INACTIVE).isValid());
    }

    @Test
    public void testShouldFacilitateConsentWithdrawal() {
        Assert.assertTrue(MetadataService.shouldFacilitateConsentWithdrawal(CLIENT_ID_ACTIVE_ACTIVE).isValid());
        Assert.assertTrue(MetadataService.shouldFacilitateConsentWithdrawal(CLIENT_ID_ACTIVE_INACTIVE).isValid());
        Assert.assertTrue(MetadataService.shouldFacilitateConsentWithdrawal(CLIENT_ID_SUSPENDED_INACTIVE).isValid());
        Assert.assertFalse(MetadataService.shouldFacilitateConsentWithdrawal(CLIENT_ID_SUSPENDED_ACTIVE).isValid());
        Assert.assertFalse(MetadataService.shouldFacilitateConsentWithdrawal(CLIENT_ID_ACTIVE_REMOVED).isValid());
        Assert.assertFalse(MetadataService.shouldFacilitateConsentWithdrawal(CLIENT_ID_SUSPENDED_REMOVED).isValid());
    }
}
