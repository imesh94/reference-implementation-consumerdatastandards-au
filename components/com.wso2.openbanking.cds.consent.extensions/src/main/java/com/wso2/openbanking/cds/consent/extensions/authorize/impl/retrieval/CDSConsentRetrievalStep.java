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

import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentRetrievalStep;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.CDSDataRetrievalUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Consent retrieval step CDS implementation.
 */
public class CDSConsentRetrievalStep implements ConsentRetrievalStep {

    private static final Log log = LogFactory.getLog(CDSConsentRetrievalStep.class);
    private static final int secondsInYear = (int) TimeUnit.SECONDS.convert(365, TimeUnit.DAYS);

    @Override
    public void execute(ConsentData consentData, JSONObject jsonObject) throws ConsentException {

        String requestObject = CDSDataRetrievalUtil.extractRequestObject(consentData.getSpQueryParams());
        Map<String, Object> requiredData = extractRequiredDataFromRequestObject(requestObject);

        JSONArray permissions = new JSONArray();
        permissions.addAll(CDSDataRetrievalUtil.getPermissionList(consentData.getScopeString()));
        JSONArray consentDataJSON = new JSONArray();

        JSONObject jsonElementPermissions = new JSONObject();
        jsonElementPermissions.appendField("title", "Permissions");
        jsonElementPermissions.appendField("data", permissions);

        consentDataJSON.add(jsonElementPermissions);
        String expiry =  requiredData.get("expirationDateTime").toString();
        JSONArray expiryArray = new JSONArray();
        expiryArray.add(expiry);

        JSONObject jsonElementExpiry = new JSONObject();
        jsonElementExpiry.appendField("title", "Expiration Date Time");
        jsonElementExpiry.appendField("data", expiryArray);

        consentDataJSON.add(jsonElementExpiry);

        jsonObject.appendField("consentData", consentDataJSON);
        consentData.addData("permissions", CDSDataRetrievalUtil.getPermissionList(consentData.getScopeString()));
        consentData.addData("expirationDateTime", requiredData.get("expirationDateTime"));
        consentData.addData("sharing_duration_value", requiredData.get("sharing_duration_value"));

        // consent type is hard coded since CDS only support accounts type for the moment
        // scopes will be used to determine consent type if any other types required in future
        consentData.setType("CDR_ACCOUNTS");

        // appending redirect URL
        jsonObject.appendField("redirectURL", CDSDataRetrievalUtil
                .getRedirectURL(consentData.getSpQueryParams()));

        // appending openid_scopes to be retrieved in authentication webapp
        jsonObject.appendField("openid_scopes", permissions);
    }

    /**
     * Method to extract required data from request object
     *
     * @param requestObject
     * @return
     */
    private Map<String, Object> extractRequiredDataFromRequestObject(String requestObject) {

        String clientID;
        Map<String, Object> dataMap = new HashMap<>();
        try {

            // request object validation is carried out in request object validator
            String[] jwtTokenValues = requestObject.split("\\.");
            String requestObjectPayload = new String(Base64.getUrlDecoder().decode(jwtTokenValues[1]),
                        StandardCharsets.UTF_8);

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
                    dataMap.put("expirationDateTime", getConsentExpiryDateTime(sharingDuration, clientID));
                }
                if (sharingDuration == 0) {
                    if (log.isDebugEnabled()) {
                        log.debug("sharing_duration not found in the request object of client: " + clientID);
                    }
                    dataMap.put("expirationDateTime", 0);
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

    private OffsetDateTime getConsentExpiryDateTime(long sharingDuration, String clientId) {

        if (sharingDuration > secondsInYear) {
            sharingDuration = secondsInYear;
            if (log.isDebugEnabled()) {
                log.debug("Requested sharing_duration is greater than a year, therefore one year duration is" +
                        " set as consent expiration for request object of client: " + clientId);
            }
        }
        OffsetDateTime currentTime = OffsetDateTime.now(ZoneOffset.UTC);
        return currentTime.plusSeconds(sharingDuration);
    }
}
