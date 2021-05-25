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

import com.google.gson.Gson;
import com.wso2.openbanking.accelerator.common.exception.ConsentManagementException;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentRetrievalStep;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.DetailedConsentResource;
import com.wso2.openbanking.accelerator.consent.mgt.service.impl.ConsentCoreServiceImpl;
import com.wso2.openbanking.cds.consent.extensions.authorize.impl.model.AccountConsentRequest;
import com.wso2.openbanking.cds.consent.extensions.authorize.impl.model.AccountData;
import com.wso2.openbanking.cds.consent.extensions.authorize.impl.model.AccountRisk;
import com.wso2.openbanking.cds.consent.extensions.authorize.impl.utils.PermissionsEnum;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Consent retrieval step CDS implementation.
 */
public class AUConsentRetrievalStep implements ConsentRetrievalStep {

    private static final Log log = LogFactory.getLog(AUConsentRetrievalStep.class);
    private static final ConsentCoreServiceImpl consentCoreService = new ConsentCoreServiceImpl();
    private static final int secondsInYear = (int) TimeUnit.SECONDS.convert(365, TimeUnit.DAYS);

    @Override
    public void execute(ConsentData consentData, JSONObject jsonObject) throws ConsentException {

        String requestObject = extractRequestObject(consentData.getSpQueryParams());
        Map<String, Object> requiredData = validateRequestObjectAndExtractRequiredData(requestObject);

        //get the consent object
        AccountConsentRequest accountConsentRequest = getAccountConsent(consentData,
                requiredData.get("sharing_duration").toString(), getPermissionList(consentData.getScopeString()));

        Gson gson = new Gson();
        String requestString = gson.toJson(accountConsentRequest);

        ConsentResource requestedConsent = new ConsentResource(requiredData.get("client_id").toString(),
                requestString, "CDR_ACCOUNTS", "awaitingAuthorization");

        DetailedConsentResource createdConsent = null;
        try {
            createdConsent = consentCoreService.createAuthorizableConsent(requestedConsent,
                    null, "created", "awaitingAuthorization", true);
        } catch (ConsentManagementException e) {
            log.error(e.getMessage());
        }
        consentData.setConsentId(createdConsent.getConsentID());
    }

    /**
     * Method to extract request object from query params
     * @param spQueryParams
     * @return
     */
    private String extractRequestObject(String spQueryParams) {
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
     * Method to validate the request object and extract required data
     *
     * @param requestObject
     * @return
     */
    private Map<String, Object> validateRequestObjectAndExtractRequiredData(String requestObject) {

        String clientID;
        Map<String, Object> dataMap = new HashMap<>();
        try {

            // validate request object and get the payload
            String requestObjectPayload;
            String[] jwtTokenValues = requestObject.split("\\.");
            if (jwtTokenValues.length == 3) {
                requestObjectPayload = new String(Base64.getUrlDecoder().decode(jwtTokenValues[1]),
                        StandardCharsets.UTF_8);
            } else {
                log.error("request object is not signed JWT");
                throw new ConsentException(ResponseStatus.BAD_REQUEST, "request object is not signed JWT");
            }
            Object payload = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(requestObjectPayload);
            if (!(payload instanceof JSONObject)) {
                throw new ConsentException(ResponseStatus.BAD_REQUEST, "Payload is not a JSON object");
            }
            JSONObject jsonObject = (JSONObject) payload;

            long sharingDuration = 0;
            clientID = jsonObject.getAsString("client_id");

            if (clientID == null) {
                log.error("client_id not found in request object");
                dataMap.put("isError", "client_id not found in request object");
                return dataMap;
            }
            dataMap.put("client_id", clientID);

            if (jsonObject.containsKey("claims")) {
                JSONObject claims = (JSONObject) jsonObject.get("claims");
                if (claims.containsKey("sharing_duration")) {
                    sharingDuration = Long.parseLong(claims.get("sharing_duration").toString());

                    if (sharingDuration > secondsInYear) {
                        sharingDuration = secondsInYear;
                        if (log.isDebugEnabled()) {
                            log.debug("Requested sharing_duration is greater than a year,therefore one year duration"
                                    + " is set as consent expiration for request object of client: "
                                    + dataMap.get("client_id"));
                        }
                    }
                    dataMap.put("sharing_duration", getConsentExpiryDateTime(sharingDuration, clientID));
                }
                if (sharingDuration == 0) {
                    if (log.isDebugEnabled()) {
                        log.debug("sharing_duration not found in the request object of client: " + clientID);
                    }
                    dataMap.put("sharing_duration", "0");
                }
                // adding original sharing_duration_value to data map
                dataMap.put("sharing_duration_value", sharingDuration);
                if (claims.containsKey("cdr_arrangement_id")) {
                    dataMap.put("cdr_arrangement_id",
                            claims.get("cdr_arrangement_id").toString());
                }
            }
        } catch (ParseException e) {
            log.error("Error while parsing the request object", e);
            throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR, "Error while parsing the request object ");
        }
        return dataMap;
    }

    private String getConsentExpiryDateTime(long sharingDuration, String clientId) {

        if (sharingDuration > secondsInYear) {
            sharingDuration = secondsInYear;
            if (log.isDebugEnabled()) {
                log.debug("Requested sharing_duration is greater than a year, therefore one year duration is" +
                        " set as consent expiration for request object of client: " + clientId);
            }
        }
        OffsetDateTime currentTime = OffsetDateTime.now(ZoneOffset.UTC);
        return currentTime.plusSeconds(sharingDuration).toString();
    }

    /**
     * Maps data to AccountConsentRequest model.
     *
     * @param consentData consent data
     * @param date expirationDate
     * @param permissionsList permissions list
     * @return an AccountConesntRequest model
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

    /**
     * convert the scope string to permission enum list.
     *
     * @param scopeString string containing the requested scopes
     * @return list of permission enums to be stored
     */
    private List<PermissionsEnum> getPermissionList(String scopeString) {

        ArrayList<PermissionsEnum> permissionList = new ArrayList<>();
        if (StringUtils.isNotEmpty(scopeString)) {
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
}
