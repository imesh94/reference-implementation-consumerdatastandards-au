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
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.CDSDataRetrievalUtil;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang3.StringUtils;
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

        if (consentData.isRegulatory()) {
            String requestObject = CDSDataRetrievalUtil.extractRequestObject(consentData.getSpQueryParams());
            Map<String, Object> requiredData = extractRequiredDataFromRequestObject(requestObject);

            JSONArray permissions = new JSONArray();
            permissions.addAll(CDSDataRetrievalUtil.getPermissionList(consentData.getScopeString()));
            JSONArray consentDataJSON = new JSONArray();

            JSONObject jsonElementPermissions = new JSONObject();
            jsonElementPermissions.appendField(CDSConsentExtensionConstants.TITLE,
                    CDSConsentExtensionConstants.PERMISSION_TITLE);
            jsonElementPermissions.appendField(CDSConsentExtensionConstants.DATA, permissions);

            consentDataJSON.add(jsonElementPermissions);
            String expiry =  requiredData.get(CDSConsentExtensionConstants.EXPIRATION_DATE_TIME).toString();
            JSONArray expiryArray = new JSONArray();
            expiryArray.add(expiry);

            JSONObject jsonElementExpiry = new JSONObject();
            jsonElementExpiry.appendField(CDSConsentExtensionConstants.TITLE,
                    CDSConsentExtensionConstants.EXPIRATION_DATE_TITLE);
            jsonElementExpiry.appendField(CDSConsentExtensionConstants.DATA, expiryArray);

            consentDataJSON.add(jsonElementExpiry);

            jsonObject.appendField(CDSConsentExtensionConstants.CONSENT_DATA, consentDataJSON);
            consentData.addData(CDSConsentExtensionConstants.PERMISSIONS,
                    CDSDataRetrievalUtil.getPermissionList(consentData.getScopeString()));
            consentData.addData(CDSConsentExtensionConstants.EXPIRATION_DATE_TIME,
                    requiredData.get(CDSConsentExtensionConstants.EXPIRATION_DATE_TIME));
            consentData.addData(CDSConsentExtensionConstants.SHARING_DURATION_VALUE,
                    requiredData.get(CDSConsentExtensionConstants.SHARING_DURATION_VALUE));
            consentData.addData(CDSConsentExtensionConstants.CDR_ARRANGEMENT_ID,
                    requiredData.get(CDSConsentExtensionConstants.CDR_ARRANGEMENT_ID));

            // consent type is hard coded since CDS only support accounts type for the moment
            // scopes will be used to determine consent type if any other types required in future
            consentData.setType(CDSConsentExtensionConstants.CDR_ACCOUNTS);

            // appending redirect URL
            jsonObject.appendField(CDSConsentExtensionConstants.REDIRECT_URL, CDSDataRetrievalUtil
                    .getRedirectURL(consentData.getSpQueryParams()));

            // appending openid_scopes to be retrieved in authentication webapp
            jsonObject.appendField(CDSConsentExtensionConstants.OPENID_SCOPES, permissions);

            // append consent expiry date
            jsonObject.appendField(CDSConsentExtensionConstants.CONSENT_EXPIRY, expiry);

            // append service provider full name
            if (StringUtils.isNotBlank(consentData.getClientId())) {
                try {
                    jsonObject.appendField(CDSConsentExtensionConstants.SP_FULL_NAME,
                            CDSDataRetrievalUtil.getServiceProviderFullName(consentData.getClientId()));
                } catch (OpenBankingException e) {
                    log.error(String.format("Error occurred while building service provider full name. %s",
                            e.getMessage()));
                    throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                            "Error occurred while building service provider full name");
                }
            } else {
                log.error("Error occurred while building service provider full name. Client-id is not found in " +
                        "consent data.");
                throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                        "Error occurred while building service provider full name. Client-id not found.");
            }
        }
    }

    /**
     * Method to extract required data from request object
     *
     * @param requestObject
     * @return
     */
    private Map<String, Object> extractRequiredDataFromRequestObject(String requestObject) throws ConsentException {

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
            clientID = jsonObject.getAsString(CDSConsentExtensionConstants.CLIENT_ID);

            if (StringUtils.isBlank(clientID)) {
                log.error("client_id not found in request object");
                dataMap.put(CDSConsentExtensionConstants.IS_ERROR, "client_id not found in request object");
                return dataMap;
            }
            dataMap.put(CDSConsentExtensionConstants.CLIENT_ID, clientID);

            if (jsonObject.containsKey(CDSConsentExtensionConstants.CLAIMS)) {
                JSONObject claims = (JSONObject) jsonObject.get(CDSConsentExtensionConstants.CLAIMS);
                if (claims.containsKey(CDSConsentExtensionConstants.SHARING_DURATION)) {
                    sharingDuration = Long.parseLong(claims
                            .get(CDSConsentExtensionConstants.SHARING_DURATION).toString());

                    if (sharingDuration > secondsInYear) {
                        sharingDuration = secondsInYear;
                        if (log.isDebugEnabled()) {
                            log.debug("Requested sharing_duration is greater than a year,therefore one year duration"
                                    + " is set as consent expiration for request object of client: "
                                    + dataMap.get(CDSConsentExtensionConstants.CLIENT_ID));
                        }
                    }
                    dataMap.put(CDSConsentExtensionConstants.EXPIRATION_DATE_TIME,
                            getConsentExpiryDateTime(sharingDuration));
                }
                if (sharingDuration == 0) {
                    if (log.isDebugEnabled()) {
                        log.debug("sharing_duration not found in the request object of client: " + clientID);
                    }
                    dataMap.put(CDSConsentExtensionConstants.EXPIRATION_DATE_TIME, 0);
                }
                // adding original sharing_duration_value to data map
                dataMap.put(CDSConsentExtensionConstants.SHARING_DURATION_VALUE, sharingDuration);
                if (claims.containsKey(CDSConsentExtensionConstants.CDR_ARRANGEMENT_ID)) {
                    dataMap.put(CDSConsentExtensionConstants.CDR_ARRANGEMENT_ID,
                            claims.get(CDSConsentExtensionConstants.CDR_ARRANGEMENT_ID).toString());
                }
            }
        } catch (ParseException e) {
            log.error("Error while parsing the request object", e);
            throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR, "Error while parsing the request object ");
        }
        return dataMap;
    }

    private OffsetDateTime getConsentExpiryDateTime(long sharingDuration) {

        OffsetDateTime currentTime = OffsetDateTime.now(ZoneOffset.UTC);
        return currentTime.plusSeconds(sharingDuration);
    }
}
