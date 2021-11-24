/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.gateway.executors.header.validation;

import com.google.common.net.InetAddresses;
import com.wso2.openbanking.accelerator.common.util.Generated;
import com.wso2.openbanking.accelerator.gateway.executor.core.OpenBankingGatewayExecutor;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIRequestContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIResponseContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OpenBankingExecutorError;
import com.wso2.openbanking.cds.common.error.handling.util.ErrorConstants.AUErrorEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * CDSHeaderValidationExecutor.
 * <p>
 * Validates the HTTP request headers as per the Consumer Data Standards.
 *
 * @see <a href="https://consumerdatastandardsaustralia.github.io/standards/#http-headers">HTTP Headers</a>
 */
public class CDSHeaderValidationExecutor implements OpenBankingGatewayExecutor {
    public static final String X_FAPI_CUSTOMER_IP_ADDRESS = "x-fapi-customer-ip-address";
    public static final String X_CDS_CLIENT_HEADERS = "x-cds-client-headers";
    public static final String X_FAPI_AUTH_DATE = "x-fapi-auth-date";
    public static final String X_FAPI_INTERACTION_ID = "x-fapi-interaction-id";
    private static final Log LOG = LogFactory.getLog(CDSHeaderValidationExecutor.class);
    private static final Pattern UUID_REGEX_PATTERN =
            Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");
    private static final List<String> ACCEPTABLE_PATTERNS = Arrays
            .asList("EEE, dd MMM uuuu HH:mm:ss 'GMT'", "EEEE, dd-MMM-uu HH:mm:ss 'GMT'", "EE MMM dd HH:mm:ss uuuu");

    @Override
    public void preProcessRequest(OBAPIRequestContext obapiRequestContext) {
        // Skip the executor if previous executors failed.
        if (obapiRequestContext.isError()) {
            return;
        }

        Map<String, String> headers = obapiRequestContext.getMsgInfo().getHeaders();
        if (!headers.isEmpty()) {
            final String customerIpAddress = headers.get(X_FAPI_CUSTOMER_IP_ADDRESS);
            if (StringUtils.isNotBlank(customerIpAddress)) {
                // x-fapi-customer-ip-address is present, the API is being called in a customer present context
                if (InetAddresses.isInetAddress(customerIpAddress)) {
                    if (StringUtils.isBlank(headers.get(X_CDS_CLIENT_HEADERS))) {
                        setError(obapiRequestContext, AUErrorEnum.HEADER_MISSING, X_CDS_CLIENT_HEADERS);
                    }
                } else {
                    setError(obapiRequestContext, AUErrorEnum.INVALID_HEADER, X_FAPI_CUSTOMER_IP_ADDRESS);
                }
            }

            final String authDate = headers.get(X_FAPI_AUTH_DATE);
            if (StringUtils.isBlank(authDate)) {
                // x-fapi-auth-date is empty
                if (StringUtils.isNotBlank(headers.get(HttpHeaders.AUTHORIZATION))) {
                    // Since authorization header is present, x-fapi-auth-date is required
                    setError(obapiRequestContext, AUErrorEnum.HEADER_MISSING, X_FAPI_AUTH_DATE);
                }
            } else {
                if (!isValidHttpDate(authDate)) {
                    setError(obapiRequestContext, AUErrorEnum.INVALID_HEADER, X_FAPI_AUTH_DATE);
                }
            }

            final String interactionId = headers.get(X_FAPI_INTERACTION_ID);
            if (StringUtils.isNotBlank(interactionId) && !isValidUUID(interactionId)) {
                setError(obapiRequestContext, AUErrorEnum.INVALID_HEADER, X_FAPI_INTERACTION_ID);
            }
        }
    }

    @Generated(message = "Ignoring since empty")
    @Override
    public void postProcessRequest(OBAPIRequestContext obapiRequestContext) {
        // Do not need to handle
    }

    @Generated(message = "Ignoring since empty")
    @Override
    public void preProcessResponse(OBAPIResponseContext obapiResponseContext) {
        // Do not need to handle
    }

    @Generated(message = "Ignoring since empty")
    @Override
    public void postProcessResponse(OBAPIResponseContext obapiResponseContext) {
        // Do not need to handle
    }

    private void setError(OBAPIRequestContext obapiRequestContext, AUErrorEnum errorEnum, String invalidHeaderName) {
        ArrayList<OpenBankingExecutorError> executorErrors = obapiRequestContext.getErrors();
        executorErrors.add(new OpenBankingExecutorError(errorEnum.getCode(), errorEnum.getTitle(),
                String.format(errorEnum.getDetail(), invalidHeaderName), String.valueOf(errorEnum.getHttpCode())));

        obapiRequestContext.setErrors(executorErrors);
        obapiRequestContext.setError(true);
    }

    /**
     * Validates time when the customer last logged in to the Data Recipient Software Product as described in [FAPI-R].
     * As in section 7.1.1.1 of [RFC7231], this method validates IMF-fixdate, rfc850-date, and asctime-date
     * <p><code><br>
     * 1. IMF-fixdate = short-day-name "," SP day SP month SP year SP 24-hour ":" minute ":" second SP GMT
     * <br>e.g. Sun, 06 Nov 1994 08:49:37 GMT
     * <p>
     * 2. rfc850-date = day-name "," SP day "-" month "-" 2DIGIT SP hour ":" minute ":" second SP GMT Sunday,
     * <br>e.g. Sunday, 06-Nov-94 08:49:37 GMT
     * <p>
     * 3. asctime-date  = day-name SP month SP ( 2DIGIT / ( SP 1DIGIT )) SP hour ":" minute ":" second SP year
     * <br>e.g. Sun Nov  6 08:49:37 1994 / Sun Nov 16 08:49:37 1994
     * </code>
     *
     * @param httpDate date string received in request header
     * @return true if date is in an acceptable date format
     * @see <a href="https://openid.net/specs/openid-financial-api-part-1-ID2.html#client-provisions">FAPI-R</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-7.1.1.1">RFC7231#section-7.1.1.1</a>
     */
    protected boolean isValidHttpDate(String httpDate) {
        for (String acceptablePattern : ACCEPTABLE_PATTERNS) {
            SimpleDateFormat formatter = new SimpleDateFormat(acceptablePattern);
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

            try {
                formatter.parse(httpDate);
                return true;
            } catch (ParseException e) {
                // Will return false, if parsing failed for all acceptable patterns
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Invalid HTTP-Date received, accepts IMF-fixdates, rfc850-dates, and asctime-dates only. date: "
                    + httpDate);
        }
        return false;
    }

    private boolean isValidUUID(String uuid) {
        return UUID_REGEX_PATTERN.matcher(uuid).matches();
    }
}
