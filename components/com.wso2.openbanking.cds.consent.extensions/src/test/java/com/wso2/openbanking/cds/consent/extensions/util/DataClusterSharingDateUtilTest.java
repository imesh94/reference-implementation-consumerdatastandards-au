/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.consent.extensions.util;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.common.utils.CDSStreamProcessorUtils;
import com.wso2.openbanking.cds.consent.extensions.model.DataClusterSharingDateModel;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Test class for Data Cluster Sharing Date Utils.
 */
@PrepareForTest({DataClusterSharingDateUtil.class, CDSStreamProcessorUtils.class})
@PowerMockIgnore("jdk.internal.reflect.*")

public class DataClusterSharingDateUtilTest extends PowerMockTestCase {
    private static final String consentId = "test_consent_id";
    private static final String dataCluster = "account_basic_read";

    @Test
    public void testGetSharingDateMap() throws OpenBankingException, IOException, ParseException {

        JSONObject spQueryResponse = new JSONObject();
        JSONArray records = new JSONArray();
        JSONArray recordOnj = new JSONArray();
        recordOnj.add(0, consentId);
        recordOnj.add(1, dataCluster);
        recordOnj.add(2, 111111);
        recordOnj.add(3, 222222);
        records.add(recordOnj);
        spQueryResponse.put("records", records);
        mockStatic(CDSStreamProcessorUtils.class);
        when(CDSStreamProcessorUtils.executeQueryOnStreamProcessor(anyString(), anyString()))
                .thenReturn(spQueryResponse);

        Map<String, DataClusterSharingDateModel> sharingDateDataMap =
                DataClusterSharingDateUtil.getSharingDateMap(consentId);

        DataClusterSharingDateModel sharingDateModel = sharingDateDataMap.get(dataCluster);
        Assert.assertNotNull(sharingDateModel);
        Assert.assertNotNull(sharingDateModel.getDataCluster());
        Assert.assertNotNull(sharingDateModel.getSharingStartDate());
        Assert.assertNotNull(sharingDateModel.getSharedLastDate());
    }
}
