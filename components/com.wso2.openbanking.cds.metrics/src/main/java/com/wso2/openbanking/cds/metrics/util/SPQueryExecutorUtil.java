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
import org.apache.axis2.transport.http.HTTPConstants;
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

    private static Log log = LogFactory.getLog(SPQueryExecutorUtil.class);

    private static APIManagerAnalyticsConfiguration analyticsConfiguration = getAnalyticsConfiguration();
    private static final String spApiHost = analyticsConfiguration.getReporterProperties()
            .get(MetricsConstants.REST_API_URL_KEY);

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

        CloseableHttpClient httpClient = HTTPClientUtils.getHttpsClient();
        HttpPost httpPost = new HttpPost(spApiHost + MetricsConstants.SP_API_PATH);
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, getAuthHeader());
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

    /**
     * Executes the given request in SP.
     *
     * @param event - event object.
     * @param url - url of the SP.
     */
    public static void executeRequestOnStreamProcessor(JSONObject event, String url) {

        JSONObject params = new JSONObject();
        params.put("event", event);
        log.debug("Executing requests on Stream Processor with url " + url);

        try {
            CloseableHttpClient httpClient = HTTPClientUtils.getHttpsClient();
            HttpPost request = new HttpPost(url);
            request.setEntity(new StringEntity(params.toString(), ContentType.APPLICATION_JSON));

            request.addHeader(HTTPConstants.HEADER_AUTHORIZATION, getAuthHeader());

            if (log.isDebugEnabled()) {
                log.debug("Publishing event to Stream Processor on url:" + url + ", data:" + params);
            }

            HttpResponse response = httpClient.execute(request);

            if (log.isDebugEnabled()) {
                log.debug("Response from Stream Processor:" + response);
            }

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                String error = String.format("Error while invoking SP rest api : %s %s",
                        response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
                log.error(error);
            }
            log.debug("Returning response after executing requests on Stream Processor with url " + url);

        } catch (IOException e) {
            log.error("Exception occurred while publishing/receiving API stats: " + e.getMessage(), e);
        } catch (OpenBankingException e) {
            log.error("Exception occurred while getting Http client: " + e.getMessage(), e);
        }
    }

    public static APIManagerAnalyticsConfiguration getAnalyticsConfiguration() {

        Bundle bundle = FrameworkUtil.getBundle(APIManagerConfigurationService.class);
        BundleContext context = bundle.getBundleContext();
        ServiceReference<APIManagerConfigurationService> reference =
                context.getServiceReference(APIManagerConfigurationService.class);
        APIManagerConfigurationService service = context.getService(reference);
        return service.getAPIAnalyticsConfiguration();
    }

    public static String getAuthHeader() {
        String spUserName = analyticsConfiguration.getReporterProperties().get(MetricsConstants.SP_USERNAME_KEY);
        String spPassword = analyticsConfiguration.getReporterProperties().get(MetricsConstants.SP_PASSWORD_KEY);

        byte[] encodedAuth = Base64.getEncoder()
                .encode((spUserName + ":" + spPassword).getBytes(StandardCharsets.ISO_8859_1));
        return "Basic " + new String(encodedAuth, StandardCharsets.UTF_8);
    }
}
