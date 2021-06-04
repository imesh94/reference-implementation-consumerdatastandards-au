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
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentRetrievalStep;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.identity.util.HTTPClientUtils;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.simple.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data cluster retrieval step CDS implementation to get human readable scopes.
 */
public class CDSDataClusterRetrievalStep implements ConsentRetrievalStep {

    private static final Log log = LogFactory.getLog(CDSDataClusterRetrievalStep.class);
    private static final String CHAR_SET = "UTF-8";
    private static final String CUSTOMER_TYPE = "customerUType";
    private static final String ORGANISATION = "Organisation";

    @Override
    public void execute(ConsentData consentData, JSONObject jsonObject) throws ConsentException {

        if (!consentData.getMetaDataMap().containsKey("permissions")) {
            log.error("Error: Scopes are not found in consent data.");
            return;
        }
        JSONArray dataCluster = new JSONArray();
        JSONArray scopes = new JSONArray();
        scopes.addAll((ArrayList) consentData.getMetaDataMap().get("permissions"));
        String customerType = getCustomerType(consentData);

        for (Object scopeEnum: scopes) {
            JSONObject dataClusterItem = new JSONObject();
            String scope = scopeEnum.toString();
            if ("common:customer.basic:read".equalsIgnoreCase(scope) &&
                    scopes.contains("common:customer.detail:read")) {
                continue;
            } else if ("bank:accounts.basic:read".equalsIgnoreCase(scope) &&
                    scopes.contains("bank:accounts.detail:read")) {
                continue;
            }
            Map<String, List<String>> cluster;
            if (scope.contains("common:") && ORGANISATION.equalsIgnoreCase(customerType)) {
                cluster = CDSConsentExtensionConstants.BUSINESS_CDS_DATA_CLUSTER.get(scope);
            } else if (scope.contains("common:")) {
                cluster = CDSConsentExtensionConstants.INDIVIDUAL_CDS_DATA_CLUSTER.get(scope);
            } else {
                cluster = CDSConsentExtensionConstants.CDS_DATA_CLUSTER.get(scope);
            }
            if (cluster == null) {
                log.warn("No data found for scope: " + scope + " requested by " + consentData.getClientId());
                continue;
            }
            for (Map.Entry<String, List<String>> entry : cluster.entrySet()) {
                dataClusterItem.put(CDSConsentExtensionConstants.TITLE, entry.getKey());
                JSONArray requestedData = new JSONArray();
                requestedData.addAll(entry.getValue());
                dataClusterItem.put(CDSConsentExtensionConstants.DATA, requestedData);
            }
            dataCluster.add(dataClusterItem);
        }
        jsonObject.put(CDSConsentExtensionConstants.DATA_REQUESTED, dataCluster);
    }

    private static String getCustomerType(ConsentData consentData) {

        if ("true".equalsIgnoreCase(OpenBankingCDSConfigParser.getInstance().getConfiguration()
                .get("CustomerDetails.Enable").toString())) {

            String customerEPURL = OpenBankingCDSConfigParser.getInstance().getConfiguration()
                    .get("CustomerDetails.CustomerDetailsRetrieveEndpoint").toString();

            if (customerEPURL != null) {
                String customerDetails = getCustomerFromEndpoint(customerEPURL, consentData.getUserId());
                try {
                    JSONObject customerDetailsJson = (JSONObject) new JSONParser(JSONParser.MODE_PERMISSIVE)
                            .parse(customerDetails);
                    return customerDetailsJson.get(CUSTOMER_TYPE).toString();
                } catch (ParseException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("Unable to load customer data for the customer: " + consentData.getUserId());
                    }
                    return ORGANISATION;
                }
            }
        }
        return ORGANISATION;
    }

    private static String getCustomerFromEndpoint(String customerDetailsUrl, String user) {
//        user = user.substring(0, user.lastIndexOf("@"));
//        String url = customerDetailsUrl.replace("{userId}", user);
        String url = customerDetailsUrl;
        if (log.isDebugEnabled()) {
            log.debug("Customer Details endpoint : " + url);
        }

        BufferedReader reader = null;
        try (CloseableHttpClient client = HTTPClientUtils.getHttpsClient()) {
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
                log.error("Retrieving customer details failed");
                return null;
            } else {
                reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), CHAR_SET));
                String inputLine;
                StringBuffer buffer = new StringBuffer();
                while ((inputLine = reader.readLine()) != null) {
                    buffer.append(inputLine);
                }
                if (log.isDebugEnabled()) {
                    log.debug("Sharable accounts endpoints returned : " + buffer.toString());
                }
                return buffer.toString();
            }
        } catch (IOException | OpenBankingException e) {
            log.error("Exception occurred while retrieving sharable accounts", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("Error while closing buffered reader");
                }
            }
        }
        return null;
    }
}
