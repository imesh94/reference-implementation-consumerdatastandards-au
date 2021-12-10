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

package com.wso2.openbanking.cds.identity.metadata.periodical.updater.utils;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.identity.util.HTTPClientUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Utility functions to perform tasks.
 */
public class Utils {

    private static final Log LOG = LogFactory.getLog(Utils.class);

    private Utils() {
    }

    @Nullable
    public static JSONObject readJsonFromUrl(String url) throws IOException, OpenBankingException {

        if (!StringUtils.isEmpty(url)) {
            try (CloseableHttpClient httpclient = HTTPClientUtils.getHttpsClient()) {

                HttpGet httpGet = new HttpGet(url);
                CloseableHttpResponse response = httpclient.execute(httpGet);

                return getResponseJson(response);
            } catch (NullPointerException e) {
                LOG.debug("Unable to retrieve status from Directory. " +
                        "Possible because Common HttpPool is not initialized yet.");
            }
        }
        return null;
    }

    private static JSONObject getResponseJson(HttpResponse response) throws IOException, OpenBankingException {

        HttpEntity responseEntity = response.getEntity();
        if (responseEntity != null) {
            final String responseStr = EntityUtils.toString(responseEntity);

            int statusCode = response.getStatusLine().getStatusCode();

            if (HttpStatus.SC_OK == statusCode) {
                LOG.debug("Received success(200) metadata response from ACCC");
                return new JSONObject(responseStr);
            } else if (HttpStatus.SC_BAD_REQUEST == statusCode || HttpStatus.SC_NOT_ACCEPTABLE == statusCode) {
                LOG.error("Received error( " + statusCode + ") metadata response from ACCC, response=" + responseStr);
                return null;
            }
            LOG.error("Received unexpected error( " + statusCode + ") metadata response from ACCC, response=" +
                    responseStr);
            throw new OpenBankingException("Metadata cache update was not performed. " +
                    "Unexpected response received from ACCC");
        } else {
            throw new OpenBankingException("Metadata cache update was not performed. " +
                    "Empty response received from ACCC");
        }

    }
}
