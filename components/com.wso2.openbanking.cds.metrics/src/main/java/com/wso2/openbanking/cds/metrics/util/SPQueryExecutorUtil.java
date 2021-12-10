/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.metrics.util;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.common.util.HTTPClientUtils;
import com.wso2.openbanking.cds.metrics.constants.MetricsConstants;
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
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.wso2.carbon.apimgt.impl.APIManagerAnalyticsConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Util class to handle communications with stream processor.
 */
public class SPQueryExecutorUtil {

    private static final String REST_API_URL_KEY = "stream.processor.rest.api.url";
    private static final String SP_USERNAME_KEY = "stream.processor.rest.api.username";
    private static final String SP_PASSWORD_KEY = "stream.processor.rest.api.password";
    private static Log log = LogFactory.getLog(SPQueryExecutorUtil.class);

    private static APIManagerAnalyticsConfiguration analyticsConfiguration = getAnalyticsConfiguration();

    /**
     * Executes the given query in SP.
     *
     * @param appName Name of the siddhi app.
     * @param query   Name of the query
     * @return - JSON object with result
     * @throws IOException    IO Exception.
     * @throws ParseException Parse Exception.
     */
    public static JSONObject executeQueryOnStreamProcessor(String appName, String query)
            throws IOException, ParseException, OpenBankingException {

        String spApiHost = analyticsConfiguration.getReporterProperties().get(REST_API_URL_KEY);
        String spUserName = analyticsConfiguration.getReporterProperties().get(SP_USERNAME_KEY);
        String spPassword = analyticsConfiguration.getReporterProperties().get(SP_PASSWORD_KEY);

        byte[] encodedAuth = Base64.getEncoder()
                .encode((spUserName + ":" + spPassword).getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth, StandardCharsets.UTF_8.toString());

        CloseableHttpClient httpClient = HTTPClientUtils.getHttpsClient();;
        HttpPost httpPost = new HttpPost(spApiHost + MetricsConstants.SP_API_PATH);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appName", appName);
        jsonObject.put("query", query);
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

    protected static APIManagerAnalyticsConfiguration getAnalyticsConfiguration() {

        Bundle bundle = FrameworkUtil.getBundle(APIManagerConfigurationService.class);
        BundleContext context = bundle.getBundleContext();
        ServiceReference<APIManagerConfigurationService> reference =
                context.getServiceReference(APIManagerConfigurationService.class);
        APIManagerConfigurationService service = context.getService(reference);
        return service.getAPIAnalyticsConfiguration();
    }
}
