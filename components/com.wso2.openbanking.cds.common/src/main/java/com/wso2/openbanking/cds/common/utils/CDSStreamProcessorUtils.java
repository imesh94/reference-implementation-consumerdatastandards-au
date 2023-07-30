/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.common.utils;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.common.util.HTTPClientUtils;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * CDS stream processor utils
 */
public class CDSStreamProcessorUtils {
    private static Log log = LogFactory.getLog(CDSStreamProcessorUtils.class);

    /**
     * Executes the given query in stream processor.
     *
     * @param appName Name of the siddhi app.
     * @param query   Name of the query
     * @return - JSON object with result
     * @throws IOException    IO Exception.
     * @throws ParseException Parse Exception.
     */
    public static JSONObject executeQueryOnStreamProcessor(String appName, String query)
            throws IOException, ParseException, OpenBankingException {

        OpenBankingCDSConfigParser configParser = OpenBankingCDSConfigParser.getInstance();
        String spApiHost = configParser.getConfiguration().get(CommonConstants.SP_SERVER_URL).toString();
        String spUserName = configParser.getConfiguration().get(CommonConstants.SP_USERNAME).toString();
        String spPassword = configParser.getConfiguration().get(CommonConstants.SP_PASSWORD).toString();

        byte[] encodedAuth = Base64.getEncoder()
                .encode((spUserName + ":" + spPassword).getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth, StandardCharsets.UTF_8.toString());

        CloseableHttpClient httpClient = HTTPClientUtils.getHttpsClient();
        HttpPost httpPost = new HttpPost(spApiHost + CommonConstants.SP_API_PATH);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CommonConstants.APP_NAME, appName);
        jsonObject.put(CommonConstants.QUERY, query);
        StringEntity requestEntity = new StringEntity(jsonObject.toJSONString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(requestEntity);
        HttpResponse response;

        if (log.isDebugEnabled()) {
            log.debug(String.format("Executing query %s on SP", query));
        }
        response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            String error = String.format("Error while invoking SP rest api : %s %s",
                    response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
            log.error(error);
            return null;
        }
        String responseStr = EntityUtils.toString(entity);
        JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
        return (JSONObject) parser.parse(responseStr);
    }
}
