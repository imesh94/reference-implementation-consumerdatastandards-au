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

import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentPersistData;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.CDSDataRetrievalUtil;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for CDSJointAccountConsentPersistenceStep
 */
@PrepareForTest({OpenBankingCDSConfigParser.class, CDSDataRetrievalUtil.class})
public class CDSJointAccountConsentPersistenceStepTest extends PowerMockTestCase {
    private static final String TEST_ACCOUNT_DATA_JSON = "{" +
            "    \"data\": [" +
            "        {" +
            "            \"account_id\": \"regular-account-id\"," +
            "            \"display_name\": \"account_2\"," +
            "            \"accountName\": \"account_2\"," +
            "            \"authorizationMethod\": \"single\"," +
            "            \"nickName\": \"not-working\"," +
            "            \"customerAccountType\": \"Individual\"," +
            "            \"type\": \"TRANS_AND_SAVINGS_ACCOUNTS\"," +
            "            \"isEligible\": true," +
            "            \"isJointAccount\": false," +
            "            \"jointAccountConsentElectionStatus\": false" +
            "        }," +
            "        {" +
            "            \"account_id\": \"joint-account-id\"," +
            "            \"display_name\": \"joint_account_1\"," +
            "            \"accountName\": \"Joint Account 1\"," +
            "            \"authorizationMethod\": \"single\"," +
            "            \"nickName\": \"joint-account-1\"," +
            "            \"customerAccountType\": \"Individual\"," +
            "            \"type\": \"TRANS_AND_SAVINGS_ACCOUNTS\"," +
            "            \"isEligible\": true," +
            "            \"isJointAccount\": true," +
            "            \"jointAccountConsentElectionStatus\": \"ELECTED\"," +
            "            \"jointAccountinfo\": {" +
            "                \"linkedMember\": [" +
            "                    {" +
            "                        \"memberId\": \"john@wso2.com@carbon.super\"," +
            "                        \"meta\": {}" +
            "                    }," +
            "                    {" +
            "                        \"memberId\": \"amy@wso2.com@carbon.super\"," +
            "                        \"meta\": {}" +
            "                    }" +
            "                ]" +
            "            }," +
            "            \"meta\": {}" +
            "        }" +
            "    ]" +
            "}";
    private ConsentPersistData consentPersistDataMock;

    @BeforeClass
    public void setUp() {
        OpenBankingCDSConfigParser openBankingCDSConfigParser = Mockito.mock(OpenBankingCDSConfigParser.class);

        Map<String, Object> configMap = new HashMap<>();
        configMap.put(CDSConsentExtensionConstants.SHARABLE_ACCOUNTS_ENDPOINT, "https://test-server/sharable");
        Mockito.when(openBankingCDSConfigParser.getConfiguration())
                .thenReturn(configMap);

        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        PowerMockito.when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParser);

        PowerMockito.mockStatic(CDSDataRetrievalUtil.class);
        PowerMockito.when(CDSDataRetrievalUtil.getAccountsFromEndpoint(anyString(),
                        anyMapOf(String.class, String.class), anyMapOf(String.class, String.class)))
                .thenReturn(TEST_ACCOUNT_DATA_JSON);

        this.consentPersistDataMock = mock(ConsentPersistData.class);
        JSONArray accounts = new JSONArray();
        accounts.add("regular-account-id");
        accounts.add("joint-account-id");

        JSONObject payload = new JSONObject();
        payload.put(CDSConsentExtensionConstants.ACCOUNT_IDS, accounts);

        ConsentData consentDataMock = mock(ConsentData.class);
        when(consentDataMock.getUserId()).thenReturn("test-joint-account-user-id");

        when(this.consentPersistDataMock.getConsentData()).thenReturn(consentDataMock);
        when(this.consentPersistDataMock.getPayload()).thenReturn(payload);
        when(this.consentPersistDataMock.getApproval()).thenReturn(true);
    }

    @Test
    public void testExecute() {
        CDSJointAccountConsentPersistenceStep uut = new CDSJointAccountConsentPersistenceStep();
        uut.execute(this.consentPersistDataMock);

        ArgumentCaptor<Object> jointAccountIdWithUsersCaptor = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<Object> usersWithMultipleJointAccountsCaptor = ArgumentCaptor.forClass(Object.class);

        verify(consentPersistDataMock).addMetadata(eq(CDSConsentExtensionConstants.MAP_JOINT_ACCOUNTS_ID_WITH_USERS),
                jointAccountIdWithUsersCaptor.capture());
        Map<String, List<String>> jointAccountIdWithUsersCaptorValue =
                (Map<String, List<String>>) jointAccountIdWithUsersCaptor.getValue();

        verify(consentPersistDataMock).addMetadata(eq(CDSConsentExtensionConstants.MAP_USER_ID_WITH_JOINT_ACCOUNTS),
                usersWithMultipleJointAccountsCaptor.capture());
        Map<String, List<String>> usersWithMultipleJointAccountsCaptorValue =
                (Map<String, List<String>>) usersWithMultipleJointAccountsCaptor.getValue();

        assertTrue(jointAccountIdWithUsersCaptorValue.containsKey("joint-account-id"));
        assertTrue(jointAccountIdWithUsersCaptorValue.get("joint-account-id").contains("john@wso2.com@carbon.super"));
        assertTrue(jointAccountIdWithUsersCaptorValue.get("joint-account-id").contains("amy@wso2.com@carbon.super"));

        assertTrue(usersWithMultipleJointAccountsCaptorValue.containsKey("john@wso2.com@carbon.super"));
        assertTrue(usersWithMultipleJointAccountsCaptorValue.containsKey("amy@wso2.com@carbon.super"));
        assertTrue(usersWithMultipleJointAccountsCaptorValue.get("john@wso2.com@carbon.super")
                .contains("joint-account-id"));
        assertTrue(usersWithMultipleJointAccountsCaptorValue.get("amy@wso2.com@carbon.super")
                .contains("joint-account-id"));
    }
}
