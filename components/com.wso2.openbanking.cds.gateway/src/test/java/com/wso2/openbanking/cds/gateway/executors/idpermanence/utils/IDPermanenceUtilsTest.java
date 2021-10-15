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

package com.wso2.openbanking.cds.gateway.executors.idpermanence.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.openbanking.cds.common.idpermanence.IdEncryptorDecryptor;
import com.wso2.openbanking.cds.gateway.executors.idpermanence.model.IdPermanenceValidationResponse;
import org.apache.commons.io.FileUtils;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


/**
 * Test class for IDPermanenceUtils
 */
@PrepareForTest(IdEncryptorDecryptor.class)
public class IDPermanenceUtilsTest extends PowerMockTestCase {

    private static final String ACCOUNTS_URL = "/banking/accounts";
    private static final String ACCOUNT_DETAILS_URL = "/banking/accounts/{accountId}";
    private static final String SCHEDULED_PAYMENTS_URL = "/banking/accounts/{accountId}/payments/scheduled";
    private static final String MEMBER_ID = "mark@gold.com@carbon.super@carbon.super";
    private static final String APP_ID = "7";
    private static final String ENC_KEY = "wso2";
    private static final String DATA = "data";
    private static final String ACCOUNTS = "accounts";
    private static final String SCHEDULED_PAYMENTS = "scheduledPayments";
    private static final String ACCOUNT_ID = "accountId";
    private static final String ACCOUNT_IDS = "accountIds";
    private static final String SCHEDULED_PAYMENT_ID = "scheduledPaymentId";
    private static final String DEC_ACCOUNT_ID = "30080012343456";
    private static final String ENC_ACCOUNT_ID_JSON = "{\"accountId\":encrypted-account-id}";
    private static final String ENC_ACCOUNT_IDS_JSON = "{\"data\":{\"accountIds\":[\"encrypted-account-id\"," +
            "\"encrypted-account-id\"]},\"meta\":\"\"}";
    private static final String ENCRYPTED_ID = "encrypted-account-id";
    private static final String DECRYPTED_ACCOUNT_STRING = "mark@gold.com@carbon.super@carbon.super:7:30080012343456";
    private static final String DECRYPTED_SCHEDULED_PAYMENT_STRING = "mark@gold.com@carbon.super@carbon.super:7:0123";

    @BeforeClass
    public void initClass() {

    }

    @Test
    public void testMaskResponseWithResourceList() throws IOException {

        File file = new File("src/test/resources/test-account-response.json");

        byte[] crlBytes = FileUtils.readFileToString(file, String.valueOf(StandardCharsets.UTF_8))
                .getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(crlBytes);
        JsonParser jsonParser = new JsonParser();
        JsonObject accountsResponse = (JsonObject) jsonParser.parse(new InputStreamReader(inputStream));
        PowerMockito.mockStatic(IdEncryptorDecryptor.class);
        Mockito.when(IdEncryptorDecryptor.encrypt(DECRYPTED_ACCOUNT_STRING, ENC_KEY)).thenReturn(ENCRYPTED_ID);

        JsonObject encAccountsResponse = IdPermanenceUtils.maskResponseIDs(accountsResponse, ACCOUNTS_URL,
                MEMBER_ID, APP_ID, ENC_KEY);
        // Get encrypted accountIds from json response
        String encAccountId = encAccountsResponse.get(DATA).getAsJsonObject().get(ACCOUNTS).getAsJsonArray().
                get(0).getAsJsonObject().get(ACCOUNT_ID).toString();
        // Remove inverted commas if there are any.
        encAccountId = encAccountId.replaceAll("^\"|\"$", "");

        Assert.assertEquals(encAccountId, ENCRYPTED_ID);
    }

    @Test
    public void testMaskResponseWithSingleResource() throws IOException {

        File file = new File("src/test/resources/test-account-details-response.json");
        byte[] crlBytes = FileUtils.readFileToString(file, String.valueOf(StandardCharsets.UTF_8))
                .getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(crlBytes);
        JsonParser jsonParser = new JsonParser();
        JsonObject accountsResponse = (JsonObject) jsonParser.parse(new InputStreamReader(inputStream));

        PowerMockito.mockStatic(IdEncryptorDecryptor.class);
        Mockito.when(IdEncryptorDecryptor.encrypt(DECRYPTED_ACCOUNT_STRING, ENC_KEY)).thenReturn(ENCRYPTED_ID);

        JsonObject encAccountsResponse = IdPermanenceUtils.maskResponseIDs(accountsResponse, ACCOUNT_DETAILS_URL,
                MEMBER_ID, APP_ID, ENC_KEY);
        // Get encrypted accountIds from json response
        String encAccountId = encAccountsResponse.getAsJsonObject(DATA).getAsJsonObject().get(ACCOUNT_ID).toString();
        // Remove inverted commas if there are any.
        encAccountId = encAccountId.replaceAll("^\"|\"$", "");

        Assert.assertEquals(encAccountId, ENCRYPTED_ID);
    }

    @Test
    public void testMaskScheduledPaymentsResponse() throws IOException {

        File file = new File("src/test/resources/test-scheduled-payments-response.json");
        byte[] crlBytes = FileUtils.readFileToString(file, String.valueOf(StandardCharsets.UTF_8))
                .getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(crlBytes);
        JsonParser jsonParser = new JsonParser();
        JsonObject accountsResponse = (JsonObject) jsonParser.parse(new InputStreamReader(inputStream));

        PowerMockito.mockStatic(IdEncryptorDecryptor.class);
        Mockito.when(IdEncryptorDecryptor.encrypt(DECRYPTED_SCHEDULED_PAYMENT_STRING, ENC_KEY)).
                thenReturn(ENCRYPTED_ID);
        Mockito.when(IdEncryptorDecryptor.encrypt(DECRYPTED_ACCOUNT_STRING, ENC_KEY)).
                thenReturn(ENCRYPTED_ID);

        JsonObject encAccountsResponse = IdPermanenceUtils.maskResponseIDs(accountsResponse, SCHEDULED_PAYMENTS_URL,
                MEMBER_ID, APP_ID, ENC_KEY);
        // Get encrypted accountIds from json response
        String encAccountId = encAccountsResponse.getAsJsonObject(DATA).getAsJsonArray(SCHEDULED_PAYMENTS).get(0).
                getAsJsonObject().get(SCHEDULED_PAYMENT_ID).toString();
        // Remove inverted commas if there are any.
        encAccountId = encAccountId.replaceAll("^\"|\"$", "");

        Assert.assertEquals(encAccountId, ENCRYPTED_ID);
    }

    @Test
    public void testUnmaskRequestPathIDs() {

        JsonParser parser = new JsonParser();
        JsonObject encryptedIds = parser.parse(ENC_ACCOUNT_ID_JSON).getAsJsonObject();

        PowerMockito.mockStatic(IdEncryptorDecryptor.class);
        Mockito.when(IdEncryptorDecryptor.decrypt(ENCRYPTED_ID, ENC_KEY)).thenReturn(DECRYPTED_ACCOUNT_STRING);

        // Unmask Ids
        IdPermanenceValidationResponse validationResponse = IdPermanenceUtils.unmaskRequestPathIDs(
                encryptedIds, ENC_KEY);
        String decryptedId = validationResponse.getDecryptedResourceIds().get(ACCOUNT_ID).toString();
        decryptedId = decryptedId.replaceAll("^\"|\"$", "");

        Assert.assertEquals(decryptedId, DEC_ACCOUNT_ID);
    }

    @Test
    public void testUnmaskRequestPathIDsWithBankDecryptedString() {

        JsonParser parser = new JsonParser();
        JsonObject encryptedIds = parser.parse(ENC_ACCOUNT_ID_JSON).getAsJsonObject();

        PowerMockito.mockStatic(IdEncryptorDecryptor.class);
        Mockito.when(IdEncryptorDecryptor.decrypt(ENCRYPTED_ID, ENC_KEY)).thenReturn("");

        // Unmask Ids
        IdPermanenceValidationResponse validationResponse = IdPermanenceUtils.unmaskRequestPathIDs(
                encryptedIds, ENC_KEY);

        Assert.assertEquals(validationResponse.isValid(), false);
    }

    @Test
    public void testUnmaskRequestBodyIDs() {

        JsonParser parser = new JsonParser();
        JsonObject encryptedIds = parser.parse(ENC_ACCOUNT_IDS_JSON).getAsJsonObject();

        PowerMockito.mockStatic(IdEncryptorDecryptor.class);
        Mockito.when(IdEncryptorDecryptor.decrypt(ENCRYPTED_ID, ENC_KEY)).thenReturn(DECRYPTED_ACCOUNT_STRING);

        // Unmask Ids
        IdPermanenceValidationResponse validationResponse = IdPermanenceUtils.unmaskRequestBodyAccountIDs(
                encryptedIds, ENC_KEY);
        String decryptedId1 = validationResponse.getDecryptedResourceIds().getAsJsonObject(DATA).
                getAsJsonArray(ACCOUNT_IDS).get(0).toString();
        String decryptedId2 = validationResponse.getDecryptedResourceIds().getAsJsonObject(DATA).
                getAsJsonArray(ACCOUNT_IDS).get(1).toString();
        decryptedId1 = decryptedId1.replaceAll("^\"|\"$", "");
        decryptedId2 = decryptedId2.replaceAll("^\"|\"$", "");

        Assert.assertEquals(decryptedId1, DEC_ACCOUNT_ID);
        Assert.assertEquals(decryptedId2, DEC_ACCOUNT_ID);
    }
}