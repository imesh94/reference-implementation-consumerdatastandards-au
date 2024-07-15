/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.consent.extensions.authorize.impl.retrieval;

import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

/**
 * Test class for CDSAccountMaskingRetrievalStep.
 */
@PrepareForTest({OpenBankingCDSConfigParser.class})
@PowerMockIgnore({"jdk.internal.reflect.*"})
public class CDSAccountMaskingRetrievalStepTest extends PowerMockTestCase {

    @Mock
    OpenBankingCDSConfigParser openBankingCDSConfigParserMock;
    private static CDSAccountMaskingRetrievalStep cdsAccountMaskingRetrievalStep;
    private JSONObject accountJson;
    private String testJson;

    @BeforeClass
    public void init() throws ParseException {

        cdsAccountMaskingRetrievalStep = new CDSAccountMaskingRetrievalStep();
        testJson = "{\n" +
                "    \"accounts\": [\n" +
                "        {\n" +
                "            \"accountId\": \"30080012343456\",\n" +
                "            \"authorizationMethod\": \"single\",\n" +
                "            \"isSecondaryAccount\": false,\n" +
                "            \"isPreSelectedAccount\": \"true\",\n" +
                "            \"displayName\": \"account_1\",\n" +
                "            \"nickName\": \"not-working\",\n" +
                "            \"customerAccountType\": \"Individual\",\n" +
                "            \"isEligible\": true,\n" +
                "            \"jointAccountConsentElectionStatus\": false,\n" +
                "            \"type\": \"TRANS_AND_SAVINGS_ACCOUNTS\",\n" +
                "            \"isJointAccount\": false\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        accountJson = (JSONObject) new JSONParser(JSONParser.MODE_PERMISSIVE).parse(testJson);
    }

    @Test
    public void testGetDisplayableAccountNumber() {

        String displayableAccountNumber = cdsAccountMaskingRetrievalStep
                .getDisplayableAccountNumber("30080012343456");
        Assert.assertEquals(displayableAccountNumber, "3008*******456");
    }

    @Test
    public void testAccountMaskingEnabled() {


        openBankingCDSConfigParserMock = mock(OpenBankingCDSConfigParser.class);
        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        PowerMockito.when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        PowerMockito.when(openBankingCDSConfigParserMock.isAccountMaskingEnabled()).thenReturn(true);

        JSONObject testAccountsJson = new JSONObject();
        testAccountsJson.put(CDSConsentExtensionConstants.ACCOUNTS, testJson);
        cdsAccountMaskingRetrievalStep.execute(mock(ConsentData.class), accountJson);
        Assert.assertNotNull(accountJson.get(CDSConsentExtensionConstants.ACCOUNTS));
        JSONArray returnedAccountsJsonArray = (JSONArray) accountJson.get(CDSConsentExtensionConstants.ACCOUNTS);
        JSONObject returnedAccountJson = (JSONObject) returnedAccountsJsonArray.get(0);
        Assert.assertNotNull(returnedAccountJson.get(CDSConsentExtensionConstants.ACCOUNT_ID_DISPLAYABLE));
        Assert.assertEquals("3008*******456", returnedAccountJson
                .get(CDSConsentExtensionConstants.ACCOUNT_ID_DISPLAYABLE));
    }

    @Test
    public void testAccountMaskingDisabled() {

        openBankingCDSConfigParserMock = mock(OpenBankingCDSConfigParser.class);
        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        PowerMockito.when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);
        PowerMockito.when(openBankingCDSConfigParserMock.isAccountMaskingEnabled()).thenReturn(false);

        JSONObject testAccountsJson = new JSONObject();
        testAccountsJson.put(CDSConsentExtensionConstants.ACCOUNTS, testJson);
        cdsAccountMaskingRetrievalStep.execute(mock(ConsentData.class), accountJson);
        Assert.assertNotNull(accountJson.get(CDSConsentExtensionConstants.ACCOUNTS));
        JSONArray returnedAccountsJsonArray = (JSONArray) accountJson.get(CDSConsentExtensionConstants.ACCOUNTS);
        JSONObject returnedAccountJson = (JSONObject) returnedAccountsJsonArray.get(0);
        Assert.assertNull(returnedAccountJson.get(CDSConsentExtensionConstants.ACCOUNT_ID_DISPLAYABLE));
    }
}
