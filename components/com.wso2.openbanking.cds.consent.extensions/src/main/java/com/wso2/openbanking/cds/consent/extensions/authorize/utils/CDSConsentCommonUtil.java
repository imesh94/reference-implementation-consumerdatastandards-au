/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.consent.extensions.authorize.utils;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.identity.util.HTTPClientUtils;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.wso2.carbon.context.PrivilegedCarbonContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

/**
 * This class contains the common utility methods used for CDS Consent steps.
 */
public class CDSConsentCommonUtil {

    private static final Log log = LogFactory.getLog(CDSConsentCommonUtil.class);


    /**
     * Method to get the userId with tenant domain.
     *
     * @param userId
     * @return
     */
    public static String getUserIdWithTenantDomain(String userId) {

        String tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
        if (userId.endsWith(tenantDomain)) {
            return userId;
        } else {
            return userId + "@" + tenantDomain;
        }
    }

    /**
     * Method to get the customer type from the customer endpoint.
     *
     * @param consentData Consent data
     * @return Customer type
     */
    public static String getCustomerType(ConsentData consentData) {

        if (CDSConsentExtensionConstants.TRUE.equalsIgnoreCase(OpenBankingCDSConfigParser.getInstance()
                .getConfiguration().get(CDSConsentExtensionConstants.ENABLE_CUSTOMER_DETAILS).toString())) {

            String customerEPURL = OpenBankingCDSConfigParser.getInstance().getConfiguration()
                    .get(CDSConsentExtensionConstants.CUSTOMER_DETAILS_RETRIEVE_ENDPOINT).toString();

            if (StringUtils.isNotBlank(customerEPURL)) {
                String customerDetails = getCustomerFromEndpoint(customerEPURL, consentData.getUserId());
                if (StringUtils.isNotBlank(customerDetails)) {
                    try {
                        JSONObject customerDetailsJson = (JSONObject) new JSONParser(JSONParser.MODE_PERMISSIVE)
                                .parse(customerDetails);
                        return customerDetailsJson.get(CDSConsentExtensionConstants.CUSTOMER_TYPE).toString();
                    } catch (ParseException e) {
                        if (log.isDebugEnabled()) {
                            log.debug("Unable to load customer data for the customer: " + consentData.getUserId());
                        }
                        return CDSConsentExtensionConstants.ORGANISATION;
                    }
                }
            }
        }
        return CDSConsentExtensionConstants.ORGANISATION;
    }

    /**
     *  Method to get the customer details from the customer endpoint.
     *
     * @param customerDetailsUrl Customer details endpoint
     * @param user User
     * @return Customer details
     */
    public static String getCustomerFromEndpoint(String customerDetailsUrl, String user) {
        user = user.substring(0, user.lastIndexOf("@"));
        String url = customerDetailsUrl.replace("{userId}", user);
        if (log.isDebugEnabled()) {
            log.debug("Customer Details endpoint : " + url);
        }

        try (CloseableHttpClient client = HTTPClientUtils.getHttpsClient()) {
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
                log.error("Retrieving customer details failed");
                return null;
            } else {
                InputStream in = response.getEntity().getContent();
                return IOUtils.toString(in, String.valueOf(StandardCharsets.UTF_8));
            }
        } catch (IOException | OpenBankingException e) {
            log.error("Exception occurred while retrieving sharable accounts", e);
        }
        return null;
    }

}
