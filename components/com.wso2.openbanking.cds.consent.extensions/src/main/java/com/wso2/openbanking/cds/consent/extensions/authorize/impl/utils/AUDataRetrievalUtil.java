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
package com.wso2.openbanking.cds.consent.extensions.authorize.impl.utils;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.accelerator.identity.util.HTTPClientUtils;
import com.wso2.openbanking.cds.consent.extensions.authorize.impl.model.AccountConsentRequest;
import com.wso2.openbanking.cds.consent.extensions.authorize.impl.model.AccountData;
import com.wso2.openbanking.cds.consent.extensions.authorize.impl.model.AccountRisk;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Util class for Consent authorize CDS implementation.
 */
public class AUDataRetrievalUtil {

    private static final Log log = LogFactory.getLog(AUDataRetrievalUtil.class);
    public static final String ACCEPT_HEADER_NAME = "Accept";
    public static final String ACCEPT_HEADER_VALUE = "application/json";
    public static final String CHAR_SET = "UTF-8";
    public static final String SERVICE_URL_SLASH = "/";

    public static String getAccountsFromEndpoint(String sharableAccountsRetrieveUrl, Map<String, String> parameters,
                                                 Map<String, String> headers) {

        String retrieveUrl = "";
        if (!sharableAccountsRetrieveUrl.endsWith(SERVICE_URL_SLASH)) {
            retrieveUrl = sharableAccountsRetrieveUrl + SERVICE_URL_SLASH;
        } else {
            retrieveUrl = sharableAccountsRetrieveUrl;
        }
        if (!parameters.isEmpty()) {
            retrieveUrl = buildRequestURL(retrieveUrl, parameters);
        }

        if (log.isDebugEnabled()) {
            log.debug("Sharable accounts retrieve endpoint : " + retrieveUrl);
        }

        BufferedReader reader = null;
        try (CloseableHttpClient client = HTTPClientUtils.getHttpsClient()) {
            HttpGet request = new HttpGet(retrieveUrl);
            request.addHeader(ACCEPT_HEADER_NAME, ACCEPT_HEADER_VALUE);
            if (!headers.isEmpty()) {
                for (Map.Entry<String, String> key : headers.entrySet()) {
                    if (key.getKey() != null && key.getValue() != null) {
                        request.addHeader(key.getKey(), key.getValue());
                    }
                }
            }
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
                log.error("Retrieving sharable accounts failed");
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

    /**
     * Build the complete URL with query parameters sent in the map
     *
     * @param baseURL    the base URL
     * @param parameters map of parameters
     * @return the output URL
     */
    private static String buildRequestURL(String baseURL, Map<String, String> parameters) {

        List<NameValuePair> pairs = new ArrayList<>();

        for (Map.Entry<String, String> key : parameters.entrySet()) {
            if (key.getKey() != null && key.getValue() != null) {
                pairs.add(new BasicNameValuePair(key.getKey(), key.getValue()));
            }
        }
        String queries = URLEncodedUtils.format(pairs, "UTF-8");
        return baseURL + "?" + queries;
    }

    /**
     * convert the scope string to permission enum list.
     *
     * @param scopeString string containing the requested scopes
     * @return list of permission enums to be stored
     */
    public static List<PermissionsEnum> getPermissionList(String scopeString) {

        ArrayList<PermissionsEnum> permissionList = new ArrayList<>();
        if (StringUtils.isNotBlank(scopeString)) {
            // Remove "openid" from the scope list to display.
            List<String> openIdScopes = Stream.of(scopeString.split(" "))
                    .filter(x -> !StringUtils.equalsIgnoreCase(x, "openid")).collect(Collectors.toList());
            for (String scope : openIdScopes) {
                PermissionsEnum permissionsEnum = PermissionsEnum.fromValue(scope);
                permissionList.add(permissionsEnum);
            }
        }
        return permissionList;
    }

    /**
     * Method to extract request object from query params
     * @param spQueryParams
     * @return
     */
    public static String extractRequestObject(String spQueryParams) {
        if (spQueryParams != null && !spQueryParams.trim().isEmpty()) {
            String requestObject = null;
            String[] spQueries = spQueryParams.split("&");
            for (String param : spQueries) {

                if (param.contains("request=")) {
                    requestObject = (param.substring("request=".length())).replaceAll(
                            "\\r\\n|\\r|\\n|\\%20", "");
                }
            }
            if (requestObject != null) {
                return requestObject;
            }
        }
        throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR, "Request object cannot be extracted");
    }

    /**
     * Method to extract redirect url from query params
     * @param spQueryParams
     * @return
     */
    public static String getRedirectURL(String spQueryParams) {
        if (spQueryParams != null && !spQueryParams.trim().isEmpty()) {
            String redirectURL = null;
            String[] spQueryParamList = spQueryParams.split("&");
            for (String param : spQueryParamList) {
                if (param.startsWith("redirect_uri=")) {
                    redirectURL = param.substring("redirect_uri=".length());
                }
            }
            if (redirectURL != null) {
                return redirectURL;
            }
        }
        throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR, "Redirect URL cannot be extracted");
    }

    /**
     * Maps data to AccountConsentRequest model.
     *
     * @param consentData consent data
     * @param date expirationDate
     * @param permissionsList permissions list
     * @return an AccountConsentRequest model
     */
    public static AccountConsentRequest getAccountConsent(ConsentData consentData, String date,
                                                          List<PermissionsEnum> permissionsList) {
        AccountConsentRequest accountConsentRequest = new AccountConsentRequest();
        AccountData accountData = new AccountData();
        accountData.setPermissions(permissionsList);
        accountData.setExpirationDateTime(date);
        AccountRisk risk = new AccountRisk();
        accountConsentRequest.setAccountData(accountData);
        accountConsentRequest.setRequestId(consentData.getConsentId());
        accountConsentRequest.setRisk(risk);
        return accountConsentRequest;
    }
}
