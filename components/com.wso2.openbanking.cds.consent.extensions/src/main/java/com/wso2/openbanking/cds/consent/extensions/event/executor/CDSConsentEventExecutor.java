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

package com.wso2.openbanking.cds.consent.extensions.event.executor;

import com.wso2.openbanking.accelerator.common.event.executor.OBEventExecutor;
import com.wso2.openbanking.accelerator.common.event.executor.model.OBEvent;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.common.util.Generated;
import com.wso2.openbanking.accelerator.consent.mgt.service.constants.ConsentCoreServiceConstants;
import com.wso2.openbanking.accelerator.identity.util.HTTPClientUtils;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonHelper;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.common.utils.CommonConstants;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import com.wso2.openbanking.cds.identity.utils.CDSIdentityUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.minidev.json.JSONObject;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * CDS event executor implementation to execute consent state change related events.
 */
public class CDSConsentEventExecutor implements OBEventExecutor {

    private static final Log log = LogFactory.getLog(CDSConsentEventExecutor.class);
    private static final String DATA_RECIPIENT_CDR_ARRANGEMENT_REVOCATION_PATH = "/arrangements/revoke";
    private static final String REVOKED_STATE = "revoked";
    private static final String REASON = "Reason";
    private static final String CLIENT_ID = "ClientId";
    private static final String CONSENT_ID = "ConsentId";

    @Override
    public void processEvent(OBEvent obEvent) {

        Map<String, Object> eventData = obEvent.getEventData();

        if (Boolean.parseBoolean(OpenBankingCDSConfigParser.getInstance().getConfiguration()
                .get(CDSConsentExtensionConstants.ENABLE_RECIPIENT_CONSENT_REVOCATION).toString())
                && ConsentCoreServiceConstants.CONSENT_REVOKE_FROM_DASHBOARD_REASON.equals(eventData.get(REASON))
                && REVOKED_STATE.equalsIgnoreCase(obEvent.getEventType())) {

            // call DR's arrangement revocation endpoint
            try {
                if (eventData.get(CLIENT_ID) != null && eventData.get(CONSENT_ID) != null) {
                    sendArrangementRevocationRequestToADR(eventData.get(CLIENT_ID).toString(),
                            eventData.get(CONSENT_ID).toString(), OpenBankingCDSConfigParser.getInstance()
                                    .getConfiguration().get(CDSConsentExtensionConstants.DATA_HOLDER_ID).toString());
                } else {
                    log.error("Consent ID/Client ID cannot be null");
                }
            } catch (OpenBankingException e) {
                log.error("Something went wrong when sending the arrangement revocation request to ADR", e);
            }
        }

    }

    /**
     * CDS Data Holder initiated CDR Arrangement Revocation:
     *      to notify the Data Recipient of the consent withdrawn by a Customer via the Data Holder’s consent dashboard
     * @param clientId client ID
     * @param consentId  revoked sharing arrangement (consent) ID
     * @param dataHolderId ID of the Data Holder obtained from the CDR Register
     */
    protected void sendArrangementRevocationRequestToADR(String clientId, String consentId, String dataHolderId)
            throws OpenBankingException {

        String recipientBaseUri = getRecipientBaseUri(clientId);
        if (StringUtils.isBlank(recipientBaseUri)) {
            String errorMessage = "DH initiated CDR Arrangement Revocation for cdr_arrangement_id " + consentId +
                    " failed due to unavailability of recipient_base_uri. " +
                    "Please update the DR's client registration with DH with an SSA including recipient_base_uri.";
            log.error(errorMessage);
            throw new OpenBankingException(errorMessage);
        }

        String consentRevocationEndpoint = recipientBaseUri + DATA_RECIPIENT_CDR_ARRANGEMENT_REVOCATION_PATH;

        try (CloseableHttpClient httpclient = HTTPClientUtils.getHttpsClient()) {

            HttpPost httpPost = generateHttpPost(consentRevocationEndpoint, dataHolderId, recipientBaseUri, consentId);

            // POST request sent to ADR is logged here for further inspections
            log.info("DH initiated consent revocation - request: " +
                    "\n" + httpPost.getRequestLine() +
                    "\n" + (Arrays.toString(httpPost.getAllHeaders()))
                    .replaceAll("\\[|\\]", "").replaceAll(",", "\n") +
                    "\n" + EntityUtils.toString(httpPost.getEntity()));

            CloseableHttpResponse responseBody = httpclient.execute(httpPost);

            if (responseBody.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (log.isDebugEnabled()) {
                    log.debug("DH successfully called DR's CDR Arrangement Revocation endpoint for " +
                            "cdr_arrangement_id " + consentId + ".");
                }
            } else {
                String error  = "DH initiated CDR Arrangement Revocation for cdr_arrangement_id " + consentId +
                        " returned non OK response.";
                log.error(error);
                throw new OpenBankingException(error);
            }

        } catch (IOException e) {
            log.error("Error occurred while calling DR's CDR arrangement revocation endpoint", e);
            throw new OpenBankingException("Error occurred while calling DR's CDR arrangement revocation endpoint", e);
        }
    }

    /**
     * Method to get the issued from the current time
     *
     * @param currentTime Current time in milliseconds (unix timestamp format)
     * @return issued time in seconds (unix timestamp format)
     */
    protected static long getIatFromCurrentTime(long currentTime) {

        return currentTime / 1000;
    }

    /**
     * Method to get the expiry time when the current time is given
     *
     * @param currentTime Current time in milliseconds (unix timestamp format)
     * @return expiry time in seconds (unix timestamp format)
     */
    protected static long getExpFromCurrentTime(long currentTime) {
        // (current time + 5 minutes) is the expiry time.
        return (currentTime / 1000) + 300;
    }

    /**
     * Method to generate signed JWT
     *
     * @param payload payload as a string
     * @param alg signature algorithm
     *
     * @return JWT as a string.
     */
    protected String generateJWT(String payload, SignatureAlgorithm alg) throws OpenBankingException {

        return Jwts.builder()
                .setPayload(payload)
                .signWith(alg, CDSIdentityUtil.getJWTSigningKey())
                .compact();
    }

    @Generated(message = "Excluding from code coverage since it requires a service call")
    protected String getRecipientBaseUri(String clientId) throws OpenBankingException {

        return new IdentityCommonHelper().getAppPropertyFromSPMetaData(clientId,
                CDSConsentExtensionConstants.RECIPIENT_BASE_URI);
    }

    /**
     * Method to generate http post request
     *
     * @param consentRevocationEndpoint consent revocation endpoint url
     * @param dataHolderId data holder id
     * @param recipientBaseUri recipient base uri
     * @param consentId consent id
     *
     * @return HttpPost
     */
    protected HttpPost generateHttpPost(String consentRevocationEndpoint, String dataHolderId,
                                        String recipientBaseUri, String consentId)
            throws UnsupportedEncodingException, OpenBankingException {

        HttpPost httpPost = new HttpPost(consentRevocationEndpoint);

        JSONObject jwtPayload = generateJWTPayload(dataHolderId, recipientBaseUri);

        httpPost.setHeader(HTTPConstants.CONTENT_TYPE, HTTPConstants.MEDIA_TYPE_X_WWW_FORM);
        httpPost.setHeader(HTTPConstants.HEADER_AUTHORIZATION,
                "Bearer " + generateJWT(jwtPayload.toString(), SignatureAlgorithm.PS256));
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(CDSConsentExtensionConstants.CDR_ARRANGEMENT_ID, consentId));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        return httpPost;
    }

    /**
     * Method to generate http post request
     *
     *@param dataHolderId data holder id
     * @param recipientBaseUri recipient base uri
     *
     * @return JSONObject jwt payload
     */
    protected JSONObject generateJWTPayload(String dataHolderId, String recipientBaseUri) {

        long currentTime = System.currentTimeMillis();

        //Adding registered claims [https://tools.ietf.org/html/rfc7519]
        JSONObject jwtPayload = new JSONObject();
        jwtPayload.put(CommonConstants.ISSURE_CLAIM, dataHolderId);
        jwtPayload.put(CommonConstants.SUBJECT_CLAIM, dataHolderId);
        jwtPayload.put(CommonConstants.AUDIENCE_CLAIM, recipientBaseUri);
        jwtPayload.put(CommonConstants.IAT_CLAIM, getIatFromCurrentTime(currentTime));
        jwtPayload.put(CommonConstants.EXP_CLAIM, getExpFromCurrentTime(currentTime));
        jwtPayload.put(CommonConstants.JTI_CLAIM, currentTime);

        return jwtPayload;
    }


}