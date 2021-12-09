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

package com.wso2.openbanking.cds.gateway.executors.jwt.authentication;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.wso2.openbanking.accelerator.common.util.JWTUtils;
import com.wso2.openbanking.accelerator.gateway.executor.core.OpenBankingGatewayExecutor;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIRequestContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIResponseContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OpenBankingExecutorError;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.common.error.handling.util.ErrorConstants;
import com.wso2.openbanking.cds.gateway.executors.jwt.authentication.cache.JwtJtiCache;
import com.wso2.openbanking.cds.gateway.executors.jwt.authentication.cache.JwtJtiCacheKey;
import com.wso2.openbanking.cds.gateway.utils.GatewayConstants;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;

/**
 * CDS JWT Authentication executor to validate self signed JWT tokens sent in the Authorization header.
 */
public class JWTAuthenticationExecutor implements OpenBankingGatewayExecutor {

    private static final Log LOG = LogFactory.getLog(JWTAuthenticationExecutor.class);

    @Override
    public void preProcessRequest(OBAPIRequestContext obapiRequestContext) {

        // Skip the executor if previous executors failed or jwt authentication is disabled in config
        if (obapiRequestContext.isError() || !OpenBankingCDSConfigParser.getInstance().getJWTAuthEnabled()) {
            return;
        }

        Map<String, String> headers = obapiRequestContext.getMsgInfo().getHeaders();
        String authHeader = headers.get(HttpHeaders.AUTHORIZATION);

        // Check if Authorization header is null
        if (StringUtils.isBlank(authHeader)) {
            LOG.error("Authorization header is null");
            setAuthHeaderValidationError(obapiRequestContext,
                    ErrorConstants.AUErrorEnum.HEADER_MISSING, HttpHeaders.AUTHORIZATION);
            return;
        }

        String jwksUrl = OpenBankingCDSConfigParser.getInstance().getJWTAuthJWKSUrl();

        // Check if jwks url is configured
        if (StringUtils.isBlank(jwksUrl)) {
            LOG.error("JWT authentication jwks url is not configured.");
            setError(obapiRequestContext, ErrorConstants.AUErrorEnum.UNEXPECTED_ERROR,
                    "Failed to validate the JWT token");
            return;
        }

        String[] splitAuthHeader = authHeader.split(" ");

        if (splitAuthHeader.length == 2 && StringUtils.isNotBlank(splitAuthHeader[1])) {
            String jwtString = splitAuthHeader[1];
            validateJWTToken(obapiRequestContext, jwtString, jwksUrl);
        } else {
            LOG.error("Error occurred while trying to authenticate. The Authorization header values are " +
                    "not defined correctly.");
            setAuthHeaderValidationError(obapiRequestContext, ErrorConstants.AUErrorEnum.INVALID_HEADER,
                    HttpHeaders.AUTHORIZATION);
            return;
        }
    }

    @Override
    public void postProcessRequest(OBAPIRequestContext obapiRequestContext) {

    }

    @Override
    public void preProcessResponse(OBAPIResponseContext obapiResponseContext) {

    }

    @Override
    public void postProcessResponse(OBAPIResponseContext obapiResponseContext) {

    }

    /**
     * Validate the JWT token passed as the authorization header
     *
     * @param obapiRequestContext OBAPIRequestContext
     * @param jwtString jwtString of the token
     * @param configuredJwksUrl the configured jwks endpoint
     * @return
     */
    private boolean validateJWTToken (OBAPIRequestContext obapiRequestContext, String jwtString,
                                      String configuredJwksUrl) {

        LOG.debug("Decoding the JWT token found in Authorization header");
        try {
            JSONObject jwtHeader = JWTUtils.decodeRequestJWT(jwtString, "head");
            JSONObject jwtBody = JWTUtils.decodeRequestJWT(jwtString, "body");

            if (jwtHeader == null || jwtBody == null) {
                LOG.error("Unsupported JWT token format found");
                setOAuthError(obapiRequestContext, "invalid_token",
                        "Unsupported JWT token format found", ErrorConstants.HTTP_UNAUTHORIZED);
                return false;
            }

            // Validate JTI. Continue if jti is not present in cache
            String jtiValue = jwtBody.getAsString("jti");
            if (jtiValue == null) {
                LOG.error("jti claim is not found in the JWT token");
                setOAuthError(obapiRequestContext, "invalid_token",
                        "Mandatory claim 'jti' is missing from the jwt token", ErrorConstants.HTTP_UNAUTHORIZED);
                return false;
            }
            if (getJtiFromCache(jtiValue) != null) {
                LOG.error(String.format("Rejected replayed jti: %s", jtiValue));
                setOAuthError(obapiRequestContext, "invalid_token",
                        String.format("jti value %s has been replayed", jtiValue), ErrorConstants.HTTP_UNAUTHORIZED);
                return false;
            }

            // Add jti value to cache
            JwtJtiCacheKey jtiCacheKey = JwtJtiCacheKey.of(jtiValue);
            JwtJtiCache.getInstance().addToCache(jtiCacheKey, jtiValue);

            //Validate claims
            //if no error, claimValidationError will be blank.
            String claimValidationError = validateClaims(jwtBody);
            if (StringUtils.isNotBlank(claimValidationError)) {
                LOG.error(claimValidationError);
                setOAuthError(obapiRequestContext, "invalid_token", claimValidationError,
                        ErrorConstants.HTTP_UNAUTHORIZED);
                return false;
            }

            // Validate jwt signature
            if (!JWTUtils.validateJWTSignature(jwtString, configuredJwksUrl, jwtHeader.getAsString("alg"))) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format("Validating the JWT %s using the JWKS Url %s",
                            jwtString, configuredJwksUrl));
                }
                setOAuthError(obapiRequestContext, "invalid_token",
                        "JWT Signature validation failed", ErrorConstants.HTTP_UNAUTHORIZED);
                return false;
            }
        } catch (ParseException | BadJOSEException | JOSEException |
                MalformedURLException e) {
            LOG.error("Error occurred while validating JWT Token", e);
            setOAuthError(obapiRequestContext, "invalid_token", e.getMessage(),
                    ErrorConstants.HTTP_UNAUTHORIZED);
            return false;
        }
        return true;
    }

    /**
     * Validate claims in the jwt token body against the configured values.
     *
     * @param jwtTokenBody - jwt token body
     * @return - error message
     */
     private String validateClaims(JSONObject jwtTokenBody) {

         String issuer = OpenBankingCDSConfigParser.getInstance().getJWTAuthIssuer();
         String audience = OpenBankingCDSConfigParser.getInstance().getJWTAuthAudience();
         String subject = OpenBankingCDSConfigParser.getInstance().getJWTAuthSubject();

        // Validate exp claim
        if (StringUtils.isBlank(jwtTokenBody.getAsString(GatewayConstants.EXP_CLAIM))) {
            LOG.debug("exp claim not be present in the JWT token.");
            return "Mandatory claim 'exp' is missing from the jwt token";
        }
        // Validate iss claim
        if (StringUtils.isBlank(jwtTokenBody.getAsString(GatewayConstants.ISS_CLAIM))) {
            return "Mandatory claim 'iss' is missing from the jwt token";
        } else if (!isValidIssuer(jwtTokenBody.getAsString(GatewayConstants.ISS_CLAIM), issuer)) {
            return "JWT Token contains invalid issuer";
        }
        // Validate sub claim
        if (StringUtils.isBlank(jwtTokenBody.getAsString(GatewayConstants.SUB_CLAIM))) {
            return "Mandatory claim 'sub' is missing from the jwt token";
        } else if (!isValidSubject(jwtTokenBody.getAsString(GatewayConstants.SUB_CLAIM), subject)) {
            return "JWT Token contains invalid subject";

        }
        // Validate aud claim
        if (StringUtils.isBlank(jwtTokenBody.getAsString(GatewayConstants.AUD_CLAIM))) {
            return "Mandatory claim 'aud' is missing from the jwt token";
        } else if (!isValidAudience(jwtTokenBody.getAsString(GatewayConstants.AUD_CLAIM), audience)) {
            return "JWT Token contains invalid audience";
        }
        //return an empty string if no error.
        return "";
    }

    /**
     * Check if the issuer is valid.
     *
     * @param issuer - iss claim
     * @return boolean
     */
    private boolean isValidIssuer(String issuer, String issuerConfigured) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Validating the iss claim: %s", issuer));
        }
        return (issuerConfigured.equals(issuer));
    }

    /**
     * Check if the subject is valid.
     *
     * @param subject - sub claim
     * @return boolean
     */
    private boolean isValidSubject(String subject, String subjectConfigured) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Validating the sub claim: %s", subject));
        }
        return (subjectConfigured.equals(subject));
    }

    /**
     * Check if the audience is valid.
     *
     * @param audience - aud claim
     * @return boolean
     */
    private boolean isValidAudience(String audience, String audienceConfigured) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Validating the aud claim: %s", audience));
        }
        return (audienceConfigured.equals(audience));
    }

    private String getJtiFromCache(String jtiValue) {
        JwtJtiCacheKey jtiCacheKey = JwtJtiCacheKey.of(jtiValue);
        return JwtJtiCache.getInstance().getFromCache(jtiCacheKey);
    }

    private void setError(OBAPIRequestContext obapiRequestContext, ErrorConstants.AUErrorEnum errorEnum,
                          String errorDescription) {

        ArrayList<OpenBankingExecutorError> executorErrors = obapiRequestContext.getErrors();
        executorErrors.add(new OpenBankingExecutorError(errorEnum.getCode(), errorEnum.getTitle(),
                errorDescription, String.valueOf(errorEnum.getHttpCode())));

        obapiRequestContext.setErrors(executorErrors);
        obapiRequestContext.setError(true);
    }

    private void setAuthHeaderValidationError(OBAPIRequestContext obapiRequestContext,
                                              ErrorConstants.AUErrorEnum errorEnum, String invalidHeaderName) {

        ArrayList<OpenBankingExecutorError> executorErrors = obapiRequestContext.getErrors();
        executorErrors.add(new OpenBankingExecutorError(errorEnum.getCode(), errorEnum.getTitle(),
                String.format(errorEnum.getDetail(), invalidHeaderName), String.valueOf(errorEnum.getHttpCode())));

        obapiRequestContext.setErrors(executorErrors);
        obapiRequestContext.setError(true);
    }

    /**
     * Set the error as required by the OAuth Standard
     *
     * @param obapiRequestContext OBAPIRequestContext
     * @param errorTitle errorTitle
     * @param errorDescription errorDescription
     */
    private void setOAuthError(OBAPIRequestContext obapiRequestContext, String errorTitle,
                               String errorDescription, String httpStatusCode) {

        obapiRequestContext.setError(true);
        ArrayList<OpenBankingExecutorError> executorErrors = obapiRequestContext.getErrors();
        executorErrors.add(new OpenBankingExecutorError(errorTitle, errorTitle, errorDescription, httpStatusCode));
        obapiRequestContext.setErrors(executorErrors);
    }

}
