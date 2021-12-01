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
import com.wso2.openbanking.accelerator.common.exception.OpenBankingRuntimeException;
import com.wso2.openbanking.accelerator.common.util.Generated;
import com.wso2.openbanking.accelerator.gateway.executor.core.OpenBankingGatewayExecutor;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIRequestContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIResponseContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OpenBankingExecutorError;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.common.error.handling.util.ErrorConstants.AUErrorEnum;
import io.swagger.v3.oas.models.PathItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.wso2.openbanking.cds.gateway.utils.GatewayConstants.ASC_TIME_DATE_PATTERN;
import static com.wso2.openbanking.cds.gateway.utils.GatewayConstants.HTTP_GET;
import static com.wso2.openbanking.cds.gateway.utils.GatewayConstants.HTTP_POST;
import static com.wso2.openbanking.cds.gateway.utils.GatewayConstants.IMF_FIX_DATE_PATTERN;
import static com.wso2.openbanking.cds.gateway.utils.GatewayConstants.MAX_REQUESTED_ENDPOINT_VERSION;
import static com.wso2.openbanking.cds.gateway.utils.GatewayConstants.MIN_REQUESTED_ENDPOINT_VERSION;
import static com.wso2.openbanking.cds.gateway.utils.GatewayConstants.RFC850_DATE_PATTERN;
import static com.wso2.openbanking.cds.gateway.utils.GatewayConstants.UUID_REGEX_PATTERN;
import static com.wso2.openbanking.cds.gateway.utils.GatewayConstants.X_CDS_CLIENT_HEADERS;
import static com.wso2.openbanking.cds.gateway.utils.GatewayConstants.X_FAPI_AUTH_DATE;
import static com.wso2.openbanking.cds.gateway.utils.GatewayConstants.X_FAPI_CUSTOMER_IP_ADDRESS;
import static com.wso2.openbanking.cds.gateway.utils.GatewayConstants.X_FAPI_INTERACTION_ID;
import static com.wso2.openbanking.cds.gateway.utils.GatewayConstants.X_VERSION;

/**
 * CDSHeaderValidationExecutor.
 * <p>
 * Validates the HTTP request headers as per the Consumer Data Standards.
 *
 * @see <a href="https://consumerdatastandardsaustralia.github.io/standards/#http-headers">HTTP Headers</a>
 */
public class CDSHeaderValidationExecutor implements OpenBankingGatewayExecutor {
    private static final Log LOG = LogFactory.getLog(CDSHeaderValidationExecutor.class);

    private static final List<String> ACCEPTABLE_HTTP_DATE_PATTERNS =
            Arrays.asList(IMF_FIX_DATE_PATTERN, RFC850_DATE_PATTERN, ASC_TIME_DATE_PATTERN);
    private static final String ERROR_HEADER_MISSING = "Header validation failed. %s is missing";
    private static final String ERROR_HEADER_INVALID = "Header validation failed. %s is invalid";

    @Generated(message = "Ignoring since empty")
    @Override
    public void preProcessRequest(OBAPIRequestContext obapiRequestContext) {
        // Do not need to handle
    }

    @Override
    public void postProcessRequest(OBAPIRequestContext obapiRequestContext) {
        // Skip the executor if previous executors failed.
        if (obapiRequestContext.isError()) {
            return;
        }

        Map<String, String> headers = obapiRequestContext.getMsgInfo().getHeaders();
        if (!headers.isEmpty()) {
            if (!isValidConditionalHeaders(obapiRequestContext, headers)) {
                return;
            }
            if (!isValidOptionalHeaders(obapiRequestContext, headers)) {
                return;
            }
            if (!isValidRequestedVersions(obapiRequestContext, headers)) {
                return;
            }
            LOG.debug("CDS HTTP header validation success");
        }
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
     * <code><br>
     *      1. IMF-fixdate = short-day-name "," SP day SP month SP year SP 24-hour ":" minute ":" second SP GMT
     *      e.g. Sun, 06 Nov 1994 08:49:37 GMT
     *      <br>
     *      2. rfc850-date = day-name "," SP day "-" month "-" 2DIGIT SP hour ":" minute ":" second SP GMT Sunday,
     *      e.g. Sunday, 06-Nov-94 08:49:37 GMT
     *      <br>
     *      3. asctime-date  = day-name SP month SP ( 2DIGIT / ( SP 1DIGIT )) SP hour ":" minute ":" second SP year
     *      e.g. Sun Nov  6 08:49:37 1994 / Sun Nov 16 08:49:37 1994
     * </code>
     *
     * @param httpDate date string received in request header
     * @return true if date is in an acceptable date format
     * @see <a href="https://openid.net/specs/openid-financial-api-part-1-ID2.html#client-provisions">FAPI-R</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-7.1.1.1">RFC7231#section-7.1.1.1</a>
     */
    protected boolean isValidHttpDate(String httpDate) {
        for (String acceptablePattern : ACCEPTABLE_HTTP_DATE_PATTERNS) {
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

    private boolean isPositiveInteger(String value) {
        if (StringUtils.isNumeric(value)) {
            return Integer.parseInt(value) > 0;
        }
        return false;
    }

    private List<Integer> extractSupportedVersionsFromSwagger(OBAPIRequestContext obapiRequestContext) {
        // extracting supported versions from api swagger
        final PathItem electedPath = obapiRequestContext.getOpenAPI().getPaths()
                .get(obapiRequestContext.getMsgInfo().getElectedResource());
        final String httpMethod = obapiRequestContext.getMsgInfo().getHttpMethod();
        String supportedVersions = StringUtils.EMPTY;
        if (HTTP_GET.equalsIgnoreCase(httpMethod) && electedPath.getGet().getExtensions().containsKey(X_VERSION)) {
            supportedVersions = electedPath.getGet().getExtensions().get(X_VERSION).toString();
        } else if (HTTP_POST.equalsIgnoreCase(httpMethod)
                && electedPath.getPost().getExtensions().containsKey(X_VERSION)) {
            supportedVersions = electedPath.getPost().getExtensions().get(X_VERSION).toString();
        }

        List<Integer> applicableVersionList = new ArrayList<>();
        if (StringUtils.isNotBlank(supportedVersions)) {
            if (supportedVersions.contains(",")) {
                Arrays.stream(supportedVersions.split(","))
                        .filter(StringUtils::isNumeric)
                        .map(Integer::parseInt)
                        .forEach(applicableVersionList::add);
            } else {
                applicableVersionList.add(Integer.parseInt(supportedVersions));
            }
        }
        return applicableVersionList;
    }

    private boolean isValidConditionalHeaders(OBAPIRequestContext obapiRequestContext, Map<String, String> headers) {
        final String customerIpAddress = headers.get(X_FAPI_CUSTOMER_IP_ADDRESS);
        if (StringUtils.isNotBlank(customerIpAddress)) {
            // x-fapi-customer-ip-address is present, the API is being called in a customer present context
            if (InetAddresses.isInetAddress(customerIpAddress)) {
                if (StringUtils.isBlank(headers.get(X_CDS_CLIENT_HEADERS))) {
                    LOG.error(String.format(ERROR_HEADER_MISSING, X_CDS_CLIENT_HEADERS));
                    setError(obapiRequestContext, AUErrorEnum.HEADER_MISSING, X_CDS_CLIENT_HEADERS);
                    return false;
                }
            } else {
                LOG.error(String.format(ERROR_HEADER_INVALID, X_FAPI_CUSTOMER_IP_ADDRESS));
                setError(obapiRequestContext, AUErrorEnum.INVALID_HEADER, X_FAPI_CUSTOMER_IP_ADDRESS);
                return false;
            }
        }

        final String authDate = headers.get(X_FAPI_AUTH_DATE);
        if (StringUtils.isBlank(authDate)) {
            // x-fapi-auth-date is empty
            if (StringUtils.isNotBlank(headers.get(HttpHeaders.AUTHORIZATION))) {
                // Since authorization header is present, x-fapi-auth-date is required
                LOG.error(String.format(ERROR_HEADER_MISSING, X_FAPI_AUTH_DATE));
                setError(obapiRequestContext, AUErrorEnum.HEADER_MISSING, X_FAPI_AUTH_DATE);
                return false;
            }
        } else {
            if (!isValidHttpDate(authDate)) {
                LOG.error(String.format(ERROR_HEADER_INVALID, X_FAPI_AUTH_DATE));
                setError(obapiRequestContext, AUErrorEnum.INVALID_HEADER, X_FAPI_AUTH_DATE);
                return false;
            }
        }
        return true;
    }

    private boolean isValidOptionalHeaders(OBAPIRequestContext obapiRequestContext, Map<String, String> headers) {
        final String interactionId = headers.get(X_FAPI_INTERACTION_ID);
        if (StringUtils.isNotBlank(interactionId) && !isValidUUID(interactionId)) {
            setError(obapiRequestContext, AUErrorEnum.INVALID_HEADER, X_FAPI_INTERACTION_ID);
            return false;
        }
        return true;
    }

    private boolean isValidRequestedVersions(OBAPIRequestContext obapiRequestContext, Map<String, String> headers) {
        int maxRequestedVersion;
        int minRequestedVersion = 0;
        boolean isMinRequestedVersionPresent = true;

        final String xv = headers.get(MAX_REQUESTED_ENDPOINT_VERSION);
        if (StringUtils.isBlank(xv)) {
            setError(obapiRequestContext, AUErrorEnum.HEADER_MISSING, MAX_REQUESTED_ENDPOINT_VERSION);
            return false;
        } else if (!isPositiveInteger(xv)) {
            setError(obapiRequestContext, AUErrorEnum.INVALID_VERSION, MAX_REQUESTED_ENDPOINT_VERSION);
            return false;
        } else {
            // x-v header is valid
            maxRequestedVersion = Integer.parseInt(xv);

            try {
                minRequestedVersion = getMinRequestedVersion(obapiRequestContext, headers);
            } catch (OpenBankingRuntimeException e) {
                LOG.debug("Both x-min-v and x-<HID>-v headers are missing.");
                isMinRequestedVersionPresent = false;
            }

            if (isMinRequestedVersionPresent && minRequestedVersion < 0) {
                return false;
            }
        }

        List<Integer> applicableVersionList = extractSupportedVersionsFromSwagger(obapiRequestContext);
        if (!applicableVersionList.isEmpty()) {
            final int dataHolderMaxVersion = Collections.max(applicableVersionList);
            final int dataHolderMinVersion = Collections.min(applicableVersionList);

            if (maxRequestedVersion > dataHolderMaxVersion) {
                setError(obapiRequestContext, AUErrorEnum.UNSUPPORTED_VERSION, String.valueOf(maxRequestedVersion));
                return false;
            } else if (isMinRequestedVersionPresent && minRequestedVersion < dataHolderMinVersion) {
                setError(obapiRequestContext, AUErrorEnum.UNSUPPORTED_VERSION, String.valueOf(minRequestedVersion));
                return false;
            } else {
                LOG.debug("version validation is success");
            }
        }
        return true;
    }

    /**
     * Method to generate minimum required version from headers.
     *
     * @param obapiRequestContext request context
     * @param headers             http cds headers
     * @return If versions are invalid return -1 <br> If versions are valid return requested version
     * @throws OpenBankingRuntimeException when both x-min-v and x-<HID>-v headers are missing
     */
    private int getMinRequestedVersion(OBAPIRequestContext obapiRequestContext, Map<String, String> headers)
            throws OpenBankingRuntimeException {
        // if holder specific endpoint version is there, should not include x-min-v header
        final String holderSpecificIdentifier = getHolderSpecificIdentifier();
        final String xHidV = headers.get(holderSpecificIdentifier);
        if (StringUtils.isBlank(holderSpecificIdentifier) || StringUtils.isBlank(xHidV)) {
            final String xMinV = headers.get(MIN_REQUESTED_ENDPOINT_VERSION);
            if (StringUtils.isNotBlank(xMinV)) {
                if (isPositiveInteger(xMinV)) {
                    return Integer.parseInt(xMinV);
                } else {
                    // invalid x-min-v
                    setError(obapiRequestContext, AUErrorEnum.INVALID_VERSION, MIN_REQUESTED_ENDPOINT_VERSION);
                    return -1;
                }
            } else {
                throw new OpenBankingRuntimeException("Both x-min-v and x-<HID>-v headers are missing.");
            }
        } else if (isPositiveInteger(xHidV)) {
            return Integer.parseInt(xHidV);
        } else {
            // invalid x-<HID>-v
            setError(obapiRequestContext, AUErrorEnum.INVALID_VERSION, holderSpecificIdentifier);
            return -1;
        }
    }

    private String getHolderSpecificIdentifier() {
        String holderId = OpenBankingCDSConfigParser.getInstance().getHolderSpecificIdentifier();
        if (StringUtils.isNotBlank(holderId) && !(holderId.startsWith("x-") && holderId.endsWith("-v"))) {
            return "x-" + holderId + "-v";
        } else {
            return holderId;
        }
    }
}
