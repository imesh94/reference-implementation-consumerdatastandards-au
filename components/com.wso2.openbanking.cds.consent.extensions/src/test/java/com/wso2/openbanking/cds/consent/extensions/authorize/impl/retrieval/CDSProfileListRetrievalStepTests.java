/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.consent.extensions.authorize.impl.retrieval;

import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.identity.util.HTTPClientUtils;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Test class for CDS Account Retrieval
 */
@PrepareForTest({OpenBankingCDSConfigParser.class, HTTPClientUtils.class})
@PowerMockIgnore({"com.wso2.openbanking.accelerator.consent.extensions.common.*", "jdk.internal.reflect.*"})
public class CDSProfileListRetrievalStepTests extends PowerMockTestCase {

    private static final String TEST_ACCOUNT_DATA_JSON = "{\n" +
            "  \"accounts\": [\n" +
            "    {\n" +
            "      \"accountId\": \"30080012343456\",\n" +
            "      \"authorizationMethod\": \"single\",\n" +
            "      \"displayName\": \"account_1\",\n" +
            "      \"nickName\": \"not-working\",\n" +
            "      \"customerAccountType\": \"Individual\",\n" +
            "      \"isEligible\": true,\n" +
            "      \"jointAccountConsentElectionStatus\": false,\n" +
            "      \"type\": \"TRANS_AND_SAVINGS_ACCOUNTS\",\n" +
            "      \"isJointAccount\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"profileName\": \"Organization A\",\n" +
            "      \"authorizationMethod\": \"single\",\n" +
            "      \"isSecondaryAccount\": false,\n" +
            "      \"businessAccountInfo\": {\n" +
            "        \"AccountOwners\": [\n" +
            "          {\n" +
            "            \"meta\": {},\n" +
            "            \"memberId\": \"user1@wso2.com@carbon.super\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"meta\": {},\n" +
            "            \"memberId\": \"user2@wso2.com@carbon.super\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"NominatedRepresentatives\": [\n" +
            "          {\n" +
            "            \"meta\": {},\n" +
            "            \"memberId\": \"nominatedUser1@wso2.com@carbon.super\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"meta\": {},\n" +
            "            \"memberId\": \"nominatedUser2@wso2.com@carbon.super\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"meta\": {},\n" +
            "            \"memberId\": \"admin@wso2.com@carbon.super\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"displayName\": \"business_account_1\",\n" +
            "      \"nickName\": \"not-working\",\n" +
            "      \"customerAccountType\": \"Business\",\n" +
            "      \"jointAccountConsentElectionStatus\": false,\n" +
            "      \"type\": \"TRANS_AND_SAVINGS_ACCOUNTS\",\n" +
            "      \"accountId\": \"143-000-B1234\",\n" +
            "      \"profileId\": \"00001\",\n" +
            "      \"isEligible\": true,\n" +
            "      \"isJointAccount\": false\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Mock
    OpenBankingCDSConfigParser openBankingCDSConfigParserMock;
    @Mock
    ConsentData consentDataMock;
    private static CDSProfileListRetrievalStep cdsProfileListRetrievalStep;

    @BeforeClass
    public void initClass() {
        cdsProfileListRetrievalStep = new CDSProfileListRetrievalStep();
        consentDataMock = mock(ConsentData.class);
    }

    @Test
    public void testProfileDataRetrievalSuccessScenario() throws ParseException {

        openBankingCDSConfigParserMock = mock(OpenBankingCDSConfigParser.class);
        doReturn(true).when(openBankingCDSConfigParserMock).
                isBNRPrioritizeSharableAccountsResponseEnabled();

        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        PowerMockito.when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);

        JSONObject jsonObject = (JSONObject) new JSONParser(JSONParser.MODE_PERMISSIVE).parse(TEST_ACCOUNT_DATA_JSON);
        doReturn(CDSConsentExtensionConstants.ACCOUNTS).when(consentDataMock).getType();

        doReturn(true).when(consentDataMock).isRegulatory();
        doReturn("user1@wso2.com@carbon.super").when(consentDataMock).getUserId();

        cdsProfileListRetrievalStep.execute(consentDataMock, jsonObject);

        Assert.assertNotNull(jsonObject.get(CDSConsentExtensionConstants.CUSTOMER_PROFILES_ATTRIBUTE));
    }
}