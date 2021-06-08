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
package com.wso2.openbanking.cds.consent.extensions.authorize.impl.retrieval;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.identity.util.HTTPClientUtils;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.consent.extensions.authorize.impl.retrieval.CDSDataClusterRetrievalStep;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import com.wso2.openbanking.cds.consent.extensions.util.CDSConsentAuthorizeTestConstants;
import graphql.Assert;
import net.minidev.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for CDS Data Cluster Retrieval
 */
@PrepareForTest({OpenBankingCDSConfigParser.class, HTTPClientUtils.class})
public class CDSDataClusterRetrievalStepTests extends PowerMockTestCase {

    @Mock
    OpenBankingCDSConfigParser openBankingCDSConfigParserMock;
    @Mock
    ConsentData consentDataMock;
    private static Map<String, String> configMap;
    private static Map<String, Object> consentDataMap;
    private static CDSDataClusterRetrievalStep cdsDataClusterRetrievalStep;

    @BeforeClass
    public void initClass() {
        configMap = new HashMap<>();
        consentDataMap = new HashMap<>();
        configMap.put(CDSConsentExtensionConstants.ENABLE_CUSTOMER_DETAILS,
                "true");
        configMap.put(CDSConsentExtensionConstants.CUSTOMER_DETAILS_RETRIEVE_ENDPOINT,
                "http://localhost:9763/api/openbanking/customer/detail/{userId}");
        consentDataMap.put("permissions",
                new ArrayList<>(Arrays.asList(CDSConsentAuthorizeTestConstants.PERMISSION_SCOPES.split(" "))));
        cdsDataClusterRetrievalStep = new CDSDataClusterRetrievalStep();
        consentDataMock = mock(ConsentData.class);
    }

    @Test
    public void testAccountDataRetrievalSuccessScenario() throws IOException, OpenBankingException {

        openBankingCDSConfigParserMock = mock(OpenBankingCDSConfigParser.class);
        doReturn(configMap).when(openBankingCDSConfigParserMock).getConfiguration();

        PowerMockito.mockStatic(OpenBankingCDSConfigParser.class);
        PowerMockito.when(OpenBankingCDSConfigParser.getInstance()).thenReturn(openBankingCDSConfigParserMock);

        StatusLine statusLineMock = Mockito.mock(StatusLine.class);
        Mockito.doReturn(HttpStatus.SC_OK).when(statusLineMock).getStatusCode();

        File file = new File("src/test/resources/test-customer-details.json");
        byte[] crlBytes = FileUtils.readFileToString(file, String.valueOf(StandardCharsets.UTF_8))
                .getBytes(StandardCharsets.UTF_8);
        InputStream inStream = new ByteArrayInputStream(crlBytes);

        HttpEntity httpEntityMock = Mockito.mock(HttpEntity.class);
        Mockito.doReturn(inStream).when(httpEntityMock).getContent();

        CloseableHttpResponse httpResponseMock = Mockito.mock(CloseableHttpResponse.class);
        Mockito.doReturn(statusLineMock).when(httpResponseMock).getStatusLine();
        Mockito.doReturn(httpEntityMock).when(httpResponseMock).getEntity();

        CloseableHttpClient closeableHttpClientMock = Mockito.mock(CloseableHttpClient.class);
        Mockito.doReturn(httpResponseMock).when(closeableHttpClientMock).execute(Mockito.any(HttpGet.class));

        PowerMockito.mockStatic(HTTPClientUtils.class);
        when(HTTPClientUtils.getHttpsClient()).thenReturn(closeableHttpClientMock);

        JSONObject jsonObject = new JSONObject();
        doReturn(consentDataMap).when(consentDataMock).getMetaDataMap();
        doReturn("admin@wso2.com@carbon.super").when(consentDataMock).getUserId();
        cdsDataClusterRetrievalStep.execute(consentDataMock, jsonObject);

        Assert.assertNotNull(jsonObject.get("data_requested"));
    }
}
