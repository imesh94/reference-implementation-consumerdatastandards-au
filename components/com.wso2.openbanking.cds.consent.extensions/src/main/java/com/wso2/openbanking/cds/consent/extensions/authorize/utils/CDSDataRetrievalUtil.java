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
package com.wso2.openbanking.cds.consent.extensions.authorize.utils;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.accelerator.identity.push.auth.extension.request.validator.exception.PushAuthRequestValidatorException;
import com.wso2.openbanking.accelerator.identity.push.auth.extension.request.validator.util.PushAuthRequestValidatorUtils;
import com.wso2.openbanking.accelerator.identity.util.HTTPClientUtils;
import com.wso2.openbanking.cds.consent.extensions.authorize.impl.model.AccountConsentRequest;
import com.wso2.openbanking.cds.consent.extensions.authorize.impl.model.AccountData;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.wso2.carbon.identity.oauth.cache.SessionDataCache;
import org.wso2.carbon.identity.oauth.cache.SessionDataCacheEntry;
import org.wso2.carbon.identity.oauth.cache.SessionDataCacheKey;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Util class for Consent authorize CDS implementation.
 */
public class CDSDataRetrievalUtil {

    private static final Log log = LogFactory.getLog(CDSDataRetrievalUtil.class);
    private static final String JWT_PART_DELIMITER = "\\.";
    private static final int NUMBER_OF_PARTS_IN_JWE = 5;

    public static String getAccountsFromEndpoint(String sharableAccountsRetrieveUrl, Map<String, String> parameters,
                                                 Map<String, String> headers) {

        String retrieveUrl = "";
        if (!sharableAccountsRetrieveUrl.endsWith(CDSConsentExtensionConstants.SERVICE_URL_SLASH)) {
            retrieveUrl = sharableAccountsRetrieveUrl + CDSConsentExtensionConstants.SERVICE_URL_SLASH;
        } else {
            retrieveUrl = sharableAccountsRetrieveUrl;
        }
        if (!parameters.isEmpty()) {
            retrieveUrl = buildRequestURL(retrieveUrl, parameters);
        }

        if (log.isDebugEnabled()) {
            log.debug("Sharable accounts retrieve endpoint : " + retrieveUrl);
        }

        try (CloseableHttpClient client = HTTPClientUtils.getHttpsClient()) {
            HttpGet request = new HttpGet(retrieveUrl);
            request.addHeader(CDSConsentExtensionConstants.ACCEPT_HEADER_NAME,
                    CDSConsentExtensionConstants.ACCEPT_HEADER_VALUE);
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
                InputStream in = response.getEntity().getContent();
                return IOUtils.toString(in, String.valueOf(StandardCharsets.UTF_8));
            }
        } catch (IOException | OpenBankingException e) {
            log.error("Exception occurred while retrieving sharable accounts", e);
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
    public static String buildRequestURL(String baseURL, Map<String, String> parameters) {

        List<NameValuePair> pairs = new ArrayList<>();

        for (Map.Entry<String, String> key : parameters.entrySet()) {
            if (key.getKey() != null && key.getValue() != null) {
                pairs.add(new BasicNameValuePair(key.getKey(), key.getValue()));
            }
        }
        String queries = URLEncodedUtils.format(pairs, CDSConsentExtensionConstants.CHAR_SET);
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
            // Remove "openid", "profile" and "cdr:registration" from the scope list to display.
            List<String> openIdScopes = Stream.of(scopeString.split(" "))
                    .filter(x -> (!StringUtils.equalsIgnoreCase(x, CDSConsentExtensionConstants.OPENID_SCOPE)
                            && !StringUtils.equalsIgnoreCase(x, CDSConsentExtensionConstants.PROFILE_SCOPE)
                            && !StringUtils.equalsIgnoreCase(x, CDSConsentExtensionConstants.CDR_REGISTRATION_SCOPE)))
                    .collect(Collectors.toList());
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
    public static String extractRequestObject(String spQueryParams) throws ConsentException {
        if (spQueryParams != null && !spQueryParams.trim().isEmpty()) {
            String requestObject = null;
            String[] spQueries = spQueryParams.split("&");
            String clientId = null;
            for (String param : spQueries) {

                if (param.contains("client_id=")) {
                    clientId = param.split("client_id=")[1];
                }
                if (param.contains("request=")) {
                    requestObject = (param.substring("request=".length())).replaceAll(
                            "\\r\\n|\\r|\\n|\\%20", "");
                } else if (param.contains("request_uri=")) {
                    log.debug("Resolving request URI during Steps execution");
                    String[] requestUri = (param.substring("request_uri=".length())).replaceAll(
                            "\\%3A", ":").split(":");
                    // session key will be obtained splitting the request uri with ":" and getting the last index
                    // sample request_uri - urn:<substring>:<sessionKey>
                    String sessionKey = requestUri[(requestUri.length - 1)];
                    SessionDataCacheKey cacheKey = new SessionDataCacheKey(sessionKey);
                    SessionDataCacheEntry sessionDataCacheEntry = SessionDataCache.
                            getInstance().getValueFromCache(cacheKey);
                    if (sessionDataCacheEntry != null) {
                        // essential claims - <request object JWT>:<request object JWT expiry time>
                        String requestObjectFromCache = sessionDataCacheEntry.getoAuth2Parameters().
                                getEssentialClaims().split(":")[0];
                        // check whether request object is encrypted
                        if (requestObjectFromCache.split(JWT_PART_DELIMITER).length == NUMBER_OF_PARTS_IN_JWE) {
                            try {
                                // decrypt request object assuming it was signed before encrypting therefore,
                                // return value is a singed JWT
                                requestObject = PushAuthRequestValidatorUtils.decrypt(requestObjectFromCache, clientId);
                            } catch (PushAuthRequestValidatorException e) {
                                log.error("Error occurred while decrypting", e);
                                throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                                        "Request object cannot be extracted");
                            }
                        } else {
                            // cached request object should be a signed JWT in this scenario
                            requestObject = requestObjectFromCache;
                        }
                        log.debug("Removing request_URI entry from cache");
                        SessionDataCache.getInstance().clearCacheEntry(cacheKey);
                    } else {
                        log.error("Could not find cache entry with request URI");
                        throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                                "Request object cannot be extracted");
                    }
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
    public static String getRedirectURL(String spQueryParams) throws ConsentException {
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
        accountConsentRequest.setAccountData(accountData);
        accountConsentRequest.setRequestId(consentData.getConsentId());
        return accountConsentRequest;
    }
}
