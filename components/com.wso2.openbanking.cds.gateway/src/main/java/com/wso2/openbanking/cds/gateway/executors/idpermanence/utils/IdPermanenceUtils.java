/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.gateway.executors.idpermanence.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wso2.openbanking.accelerator.gateway.executor.model.OpenBankingExecutorError;
import com.wso2.openbanking.cds.common.idpermanence.IdEncryptorDecryptor;
import com.wso2.openbanking.cds.common.utils.ErrorConstants;
import com.wso2.openbanking.cds.gateway.executors.idpermanence.model.IdPermanenceValidationResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Util methods required for id permanence requirement of AU spec
 */
public class IdPermanenceUtils {

    private static final Log log = LogFactory.getLog(IdPermanenceUtils.class);

    /**
     * Mask resourceIds in response payload
     *
     * @param responseJsonPayload response payload
     * @param url                 requested Url
     * @param memberId            user Id
     * @param appId               application id
     * @param key                 encryption key
     * @return processed Json payload with masked resource Ids
     */
    public static JsonObject maskResponseIDs(JsonObject responseJsonPayload, String url,
                                             String memberId, String appId, String key) {

        JsonObject data = (JsonObject) responseJsonPayload.get(IdPermanenceConstants.DATA);
        responseJsonPayload.add(IdPermanenceConstants.DATA, maskResourceIDsInData(data, url, memberId, appId, key));

        JsonObject links = (JsonObject) responseJsonPayload.get(IdPermanenceConstants.LINKS);
        responseJsonPayload.add(IdPermanenceConstants.LINKS, maskResourceIDsInLinks(links, url, memberId, appId, key));

        return responseJsonPayload;
    }

    /**
     * Mask resourceIds in response payload data object
     *
     * @param data     data object of response payload
     * @param url      requested Url
     * @param memberId user Id
     * @param appId    software product Id
     * @param key      encryption key
     * @return processed data Json payload with masked resource Ids
     */
    public static JsonObject maskResourceIDsInData(JsonObject data, String url,
                                                   String memberId, String appId, String key) {
        if (isResourceListResponse(url)) {
            // handle responses with a resource list
            List<String> keys = getJsonObjectMembers(data);
            JsonArray resourceList = (JsonArray) data.get(keys.get(0));
            int resourceArrayLength = resourceList.size();

            if (resourceArrayLength > 0) {
                List<String> availableResourceIdKeys =
                        getListOfAvailableResourceIdKeysInResponse(resourceList.get(0).getAsJsonObject());

                for (int resourceIndex = 0; resourceIndex < resourceArrayLength; resourceIndex++) {
                    JsonObject resourceNew = resourceList.get(resourceIndex).getAsJsonObject();
                    encryptResourceIdsInJsonObject(availableResourceIdKeys, resourceNew, memberId, appId, key);
                }
            }
        } else if (isSingleResourceResponse(url)) {
            // handle response with a single resource
            List<String> availableResourceIdKeys =
                    getListOfAvailableResourceIdKeysInResponse(data);
            encryptResourceIdsInJsonObject(availableResourceIdKeys, data, memberId, appId, key);
        } else if (isSchedulePaymentListResponse(url)) {
            // handle scheduled payment list retrieval requests
            JsonArray resourceList = (JsonArray) data.get(IdPermanenceConstants.SCHEDULED_PAYMENTS);
            int resourceArrayLength = resourceList.size();
            for (int resourceIndex = 0; resourceIndex < resourceArrayLength; resourceIndex++) {
                JsonObject resourceNew = resourceList.get(resourceIndex).getAsJsonObject();
                encryptResourceIdsInJsonObject(Arrays.asList(IdPermanenceConstants.SCHEDULED_PAYMENT_ID), resourceNew,
                        memberId, appId, key);

                // update resource ids in nested objects of the response
                JsonObject fromNew = (JsonObject) resourceNew.get(IdPermanenceConstants.FROM);
                encryptResourceIdsInJsonObject(Arrays.asList(IdPermanenceConstants.ACCOUNT_ID), fromNew, memberId,
                        appId, key);

                JsonArray paymentSet = (JsonArray) resourceNew.get(IdPermanenceConstants.PAYMENT_SET);
                int paymentSetLength = paymentSet.size();
                for (int paymentNumber = 0; paymentNumber < paymentSetLength; paymentNumber++) {
                    JsonObject toNew = ((JsonObject) paymentSet.get(paymentNumber)).get(IdPermanenceConstants.TO).
                            getAsJsonObject();
                    encryptResourceIdsInJsonObject(getListOfAvailableResourceIdKeysInResponse(toNew),
                            toNew, memberId, appId, key);
                }

            }
        }
        return data;
    }

    /**
     * Mask resourceIds in response payload
     *
     * @param links    links object of response payload
     * @param url      requested Url
     * @param memberId user Id
     * @param appId    software product Id
     * @param key      encryption key
     * @return processed data Json payload with masked resource Ids
     */
    public static JsonObject maskResourceIDsInLinks(JsonObject links, String url,
                                                    String memberId, String appId, String key) {

        if (IdPermanenceConstants.REQUEST_URLS_WITH_PATH_PARAMS.contains(url)) {
            for (String keyLink : getJsonObjectMembers(links)) {
                String link = links.get(keyLink).getAsString();
                JsonObject pathParams = extractUrlParams(url, link);
                if (pathParams.size() > 0) {
                    encryptResourceIdsInJsonObject(getJsonObjectMembers(pathParams), pathParams, memberId, appId, key);
                    links.addProperty(keyLink, processNewUri(url, link, pathParams));
                } else {
                    log.error(keyLink + " link is not in correct url format.");
                    links.addProperty(keyLink, "incorrect link format");
                }
            }
        }
        return links;
    }

    /**
     * Unmask masked resource Ids sent in the request path
     *
     * @param idSet set of resource ids to be encrypted as <resourceIdKey>:<maskedResourceID> pairs
     * @param key   decryption key
     * @return set of decrypted resource ids as <resourceIdKey>:<maskedResourceID> pairs
     */
    public static IdPermanenceValidationResponse unmaskRequestPathIDs(JsonObject idSet, String key) {
        List<String> resourceKeys = getJsonObjectMembers(idSet);
        JsonObject decryptedResourceIdSet = new JsonObject();
        IdPermanenceValidationResponse idPermanenceValidationResponse = new IdPermanenceValidationResponse();

        for (String resourceKey : resourceKeys) {
            String encryptedId = String.valueOf(idSet.get(resourceKey));
            encryptedId = encryptedId.replaceAll("^\"|\"$", ""); // Remove any inverted commas
            String decryptedString;
            try {
                decryptedString = IdEncryptorDecryptor.decrypt(encryptedId, key);
            } catch (IllegalArgumentException e) {
                log.debug("Error while decrypting", e);
                idPermanenceValidationResponse.setValid(false);
                if (resourceKey.equals(IdPermanenceConstants.ACCOUNT_ID)) {
                    idPermanenceValidationResponse.setError(new OpenBankingExecutorError(
                            ErrorConstants.AUErrorEnum.INVALID_BANK_ACCOUNT_PATH.getCode(),
                            ErrorConstants.AUErrorEnum.INVALID_BANK_ACCOUNT_PATH.getTitle(),
                            ErrorConstants.AUErrorEnum.INVALID_BANK_ACCOUNT_PATH.getDetail(),
                            String.valueOf(ErrorConstants.AUErrorEnum.INVALID_BANK_ACCOUNT_PATH.getHttpCode())
                    ));
                } else {
                    idPermanenceValidationResponse.setError(new OpenBankingExecutorError(
                            ErrorConstants.AUErrorEnum.INVALID_RESOURCE_PATH.getCode(),
                            ErrorConstants.AUErrorEnum.INVALID_RESOURCE_PATH.getTitle(),
                            ErrorConstants.AUErrorEnum.INVALID_RESOURCE_PATH.getDetail(),
                            String.valueOf(ErrorConstants.AUErrorEnum.INVALID_RESOURCE_PATH.getHttpCode())
                    ));
                }
                return idPermanenceValidationResponse;
            }
            if (StringUtils.isBlank(decryptedString) ||
                    !decryptedString.matches(IdPermanenceConstants.DECRYPTED_RESOURCE_ID_PATTERN)) {
                idPermanenceValidationResponse.setValid(false);
                if (resourceKey.equals(IdPermanenceConstants.ACCOUNT_ID)) {
                    idPermanenceValidationResponse.setError(new OpenBankingExecutorError(
                            ErrorConstants.AUErrorEnum.INVALID_BANK_ACCOUNT_PATH.getCode(),
                            ErrorConstants.AUErrorEnum.INVALID_BANK_ACCOUNT_PATH.getTitle(),
                            ErrorConstants.AUErrorEnum.INVALID_BANK_ACCOUNT_PATH.getDetail(),
                            String.valueOf(ErrorConstants.AUErrorEnum.INVALID_BANK_ACCOUNT_PATH.getHttpCode())
                    ));
                    idPermanenceValidationResponse.setHttpStatus(
                            ErrorConstants.AUErrorEnum.INVALID_BANK_ACCOUNT_PATH.getHttpCode());
                } else {
                    idPermanenceValidationResponse.setError(new OpenBankingExecutorError(
                            ErrorConstants.AUErrorEnum.INVALID_RESOURCE_PATH.getCode(),
                            ErrorConstants.AUErrorEnum.INVALID_RESOURCE_PATH.getTitle(),
                            ErrorConstants.AUErrorEnum.INVALID_RESOURCE_PATH.getDetail(),
                            String.valueOf(ErrorConstants.AUErrorEnum.INVALID_RESOURCE_PATH.getHttpCode())
                    ));
                }
                return idPermanenceValidationResponse;
            }
            String realResourceId = decryptedString.split(":")[2];
            decryptedResourceIdSet.addProperty(resourceKey, realResourceId);
        }

        JsonObject sanitizedDecryptedResourceIdSet = removeEmptyStrings(decryptedResourceIdSet);
        idPermanenceValidationResponse.setValid(true);
        idPermanenceValidationResponse.setDecryptedResourceIds(sanitizedDecryptedResourceIdSet);
        return idPermanenceValidationResponse;
    }

    /**
     * Unmask masked accountIds sent in the request body
     *
     * @param requestJsonPayload request body
     * @param key                decryption key
     * @return request Json Payload with decrypted(unmasked) accountIds
     */
    public static IdPermanenceValidationResponse unmaskRequestBodyAccountIDs
    (JsonObject requestJsonPayload, String key) {

        JsonObject data = (JsonObject) requestJsonPayload.get(IdPermanenceConstants.DATA);
        JsonArray accountIdList = (JsonArray) data.get(IdPermanenceConstants.ACCOUNT_IDS);
        JsonArray decryptedAccountIdList = new JsonArray();

        IdPermanenceValidationResponse idPermanenceValidationResponse = new IdPermanenceValidationResponse();
        for (int accountIndex = 0; accountIndex < accountIdList.size(); accountIndex++) {
            String encryptedId = String.valueOf(accountIdList.get(accountIndex).getAsString());
            String decryptedString;
            try {
                // decrypted string is expected to be in the format userId:appId:resourceId
                decryptedString = IdEncryptorDecryptor.decrypt(encryptedId, key);
            } catch (IllegalArgumentException e) {
                log.error("Error occurred while decrypting");
                log.debug("Error: ", e);
                idPermanenceValidationResponse.setValid(false);
                idPermanenceValidationResponse.setError(new OpenBankingExecutorError(
                        ErrorConstants.AUErrorEnum.INVALID_BANK_ACCOUNT_BODY.getCode(),
                        ErrorConstants.AUErrorEnum.INVALID_BANK_ACCOUNT_BODY.getTitle(),
                        ErrorConstants.AUErrorEnum.INVALID_BANK_ACCOUNT_BODY.getDetail(),
                        String.valueOf(ErrorConstants.AUErrorEnum.INVALID_BANK_ACCOUNT_BODY.getHttpCode()))
                );
                return idPermanenceValidationResponse;
            }
            if (StringUtils.isBlank(decryptedString) ||
                    !decryptedString.matches(IdPermanenceConstants.DECRYPTED_RESOURCE_ID_PATTERN)) {
                idPermanenceValidationResponse.setValid(false);
                idPermanenceValidationResponse.setError(new OpenBankingExecutorError(
                        ErrorConstants.AUErrorEnum.INVALID_BANK_ACCOUNT_BODY.getCode(),
                        ErrorConstants.AUErrorEnum.INVALID_BANK_ACCOUNT_BODY.getTitle(),
                        ErrorConstants.AUErrorEnum.INVALID_BANK_ACCOUNT_BODY.getDetail(),
                        String.valueOf(ErrorConstants.AUErrorEnum.INVALID_BANK_ACCOUNT_BODY.getHttpCode()))
                );
                return idPermanenceValidationResponse;
            }
            String realResourceId = decryptedString.split(":")[2];
            decryptedAccountIdList.add(realResourceId);
        }

        data.add(IdPermanenceConstants.ACCOUNT_IDS, decryptedAccountIdList);
        idPermanenceValidationResponse.setValid(true);
        JsonObject sanitizedRequestJsonPayload = removeEmptyStrings(requestJsonPayload);
        idPermanenceValidationResponse.setDecryptedResourceIds(sanitizedRequestJsonPayload);
        return idPermanenceValidationResponse;

    }

    /**
     * Check for resource list response.
     *
     * @param url url
     * @return boolean
     */
    private static boolean isResourceListResponse(String url) {
        return IdPermanenceConstants.RESOURCE_LIST_RES_URLS.contains(url);
    }

    /**
     * Check for single resource response.
     *
     * @param url url
     * @return boolean
     */
    private static boolean isSingleResourceResponse(String url) {
        return IdPermanenceConstants.SINGLE_RESOURCE_RES_URLS.contains(url);
    }

    /**
     * Check for scheduled payment list response.
     *
     * @param url url
     * @return boolean
     */
    private static boolean isSchedulePaymentListResponse(String url) {
        return IdPermanenceConstants.SCHEDULED_PAYMENT_LIST_RES_URLS.contains(url);
    }

    /**
     * Retrieve list of resourceId keys (subjected to id permanence) available in the input Json Object.
     *
     * @param resourceJson Json Payload
     * @return list of resourceId keys (subjected to id permanence) available in the input Json Object
     */
    private static List<String> getListOfAvailableResourceIdKeysInResponse(JsonObject resourceJson) {
        List<String> allKeys = getJsonObjectMembers(resourceJson);
        List<String> resourceIdKeys = Arrays.asList(IdPermanenceConstants.ACCOUNT_ID,
                IdPermanenceConstants.TRANSACTION_ID, IdPermanenceConstants.SCHEDULED_PAYMENT_ID,
                IdPermanenceConstants.PAYEE_ID);

        return allKeys.stream()
                .filter(resourceIdKeys::contains)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve available key set of a Json Object.
     *
     * @param jsonObject json Object
     * @return key set of the Json Object as a list
     */
    public static List<String> getJsonObjectMembers(JsonObject jsonObject) {
        return jsonObject.entrySet()
                .stream()
                .map(i -> i.getKey())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Encrypt resource ids subjected to id permanence, in the input resource.
     *
     * @param availableResourceIdKeys list of resourceId keys available in a Json payload
     * @param resource                Json resource
     * @param memberId                user ID
     * @param appId                   application ID
     * @param key                     encryption key
     */
    private static void encryptResourceIdsInJsonObject(List<String> availableResourceIdKeys, JsonObject resource,
                                                       String memberId, String appId, String key) {
        for (String resourceIdKey : availableResourceIdKeys) {
            String resourceId = String.valueOf(resource.get(resourceIdKey));
            resourceId = resourceId.replaceAll("^\"|\"$", ""); // Remove inverted commas if there are any.
            String stringToEncrypt = memberId + ":" + appId + ":" + resourceId;
            String encryptedId = IdEncryptorDecryptor.encrypt(stringToEncrypt, key);
            resource.addProperty(resourceIdKey, encryptedId);
        }
    }

    /**
     * Process new request URI with new resource Ids.
     *
     * @param uriTemplate matching resource url template
     * @param rawUrl      raw url to be modified
     * @param newIdSet    set of new resource Ids in raw url as
     *                    "RESOURCE_KEY": "NEW_RESOURCE_ID" key-value pairs
     * @return new url with new resource Ids
     */
    public static String processNewUri(String uriTemplate, String rawUrl, JsonObject newIdSet) {
        String processedUri = rawUrl;
        List<String> resourceKeys = getJsonObjectMembers(newIdSet);

        String urlTemplateRegex = "(" +
                uriTemplate.replace("{", "(?<").replace("}", ">[^\\/?]*)") + ")";

        Pattern urlPattern = Pattern.compile(urlTemplateRegex);
        Matcher urlMatcher = urlPattern.matcher(rawUrl);
        if (urlMatcher.find()) {
            for (String resourceKey : resourceKeys) {
                processedUri = processedUri.replaceAll(urlMatcher.group(resourceKey),
                        (newIdSet.get(resourceKey).getAsString()).replaceAll("^[\"']+|[\"']+$", ""));
            }
        }

        return processedUri;
    }

    /**
     * Extract Url Params from the Url, when uri template is provided.
     *
     * @param uriTemplate uri template
     * @param rawUrl      the url, from which path parameters need to be extracted
     * @return set of path parameters in rawUrl as
     * "PARAM_PLACEHOLDER": "PARAM_VALUE" key-value pairs
     */
    public static JsonObject extractUrlParams(String uriTemplate, String rawUrl) {

        JsonObject pathParams = new JsonObject();
        List<String> resourceKeys = new ArrayList<>();

        String urlTemplateRegex = "(" +
                uriTemplate.replace("{", "(?<").replace("}", ">[^\\/?]*)") + ")";

        Pattern resourceKeyPattern = Pattern.compile(IdPermanenceConstants.URL_TEMPLATE_PATH_PARAM_PATTERN);
        Matcher resourceKeyMatcher = resourceKeyPattern.matcher(uriTemplate);
        while (resourceKeyMatcher.find()) {
            resourceKeys.add(resourceKeyMatcher.group(0).replaceAll("\\{|}", ""));
        }

        Pattern urlPattern = Pattern.compile(urlTemplateRegex);
        Matcher urlMatcher = urlPattern.matcher(rawUrl);
        if (urlMatcher.find()) {
            for (String resourceKey : resourceKeys) {
                pathParams.addProperty(resourceKey, urlMatcher.group(resourceKey));
            }
        }

        return pathParams;
    }

    /**
     * Remove empty strings from the json object and set them as null
     *
     * @param jsonObject jsonObject
     * @return jsonObject
     */
    public static JsonObject removeEmptyStrings(JsonObject jsonObject) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            if (!entry.getValue().isJsonObject() && entry.getValue().getAsString().length() == 0) {
                entry.setValue(null);
            }
        }
        return jsonObject;
    }

}
