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

package com.wso2.openbanking.cds.identity.authenticator.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.common.util.Generated;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonConstants;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonHelper;
import com.wso2.openbanking.cds.identity.internal.CDSIdentityDataHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.core.util.KeyStoreManager;
import org.wso2.carbon.identity.application.common.model.ServiceProviderProperty;
import org.wso2.carbon.identity.oauth.common.OAuth2ErrorCodes;
import org.wso2.carbon.identity.oauth.common.exception.InvalidOAuthClientException;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth.dao.OAuthAppDO;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.client.authentication.OAuthClientAuthnException;
import org.wso2.carbon.identity.oauth2.token.handler.clientauth.jwt.cache.JWTCache;
import org.wso2.carbon.identity.oauth2.token.handler.clientauth.jwt.cache.JWTCacheEntry;
import org.wso2.carbon.identity.oauth2.token.handler.clientauth.jwt.dao.JWTEntry;
import org.wso2.carbon.identity.oauth2.token.handler.clientauth.jwt.dao.JWTStorageManager;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;
import org.wso2.carbon.identity.oauth2.validators.jwt.JWKSBasedJWTValidator;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * This class is used to validate the JWT which is coming along with the request.
 */
public class CDSJWTValidator {

    private static final Log log = LogFactory.getLog(CDSJWTValidator.class);
    public static final String FULLSTOP_DELIMITER = ".";
    public static final String DASH_DELIMITER = "-";
    public static final String KEYSTORE_FILE_EXTENSION = ".jks";
    public static final String RS = "RS";
    public static final String PS = "PS";
    private boolean preventTokenReuse;
    private List<String> validAudiences;
    private String validIssuer;
    private int rejectBeforeInMinutes;
    List<String> mandatoryClaims;
    private JWTCache jwtCache;
    private boolean enableJTICache;

    private JWTStorageManager jwtStorageManager;

    public CDSJWTValidator(boolean preventTokenReuse, List<String> validAudiences, int rejectBefore,
                           String validIssuer, List<String> mandatoryClaims, boolean enableJTICache) {

        this.preventTokenReuse = preventTokenReuse;
        this.validAudiences = validAudiences;
        this.validIssuer = validIssuer;
        this.jwtStorageManager = new JWTStorageManager();
        this.mandatoryClaims = mandatoryClaims;
        this.rejectBeforeInMinutes = rejectBefore;
        this.enableJTICache = enableJTICache;
        this.jwtCache = JWTCache.getInstance();
    }

    /**
     * To validate the JWT assertion.
     *
     * @param signedJWT Validate the token
     * @return true if the jwt is valid.
     * @throws IdentityOAuth2Exception
     */
    public boolean isValidAssertion(SignedJWT signedJWT) throws OAuthClientAuthnException {

        String errorMessage;

        if (signedJWT == null) {
            errorMessage = "No valid JWT assertion found for " + Constants.OAUTH_JWT_BEARER_GRANT_TYPE;
            return logAndThrowException(errorMessage);
        }
        try {
            JWTClaimsSet claimsSet = getClaimSet(signedJWT);

            if (claimsSet == null) {
                errorMessage = "Claim set is missing in the JWT assertion";
                throw new OAuthClientAuthnException(errorMessage, OAuth2ErrorCodes.INVALID_REQUEST);
            }

            String jwtIssuer = claimsSet.getIssuer();
            String jwtSubject = resolveSubject(claimsSet);
            List<String> audience = claimsSet.getAudience();
            Date expirationTime = claimsSet.getExpirationTime();
            String jti = claimsSet.getJWTID();
            Date nbf = claimsSet.getNotBeforeTime();
            Date issuedAtTime = claimsSet.getIssueTime();
            long currentTimeInMillis = System.currentTimeMillis();
            long timeStampSkewMillis = OAuthServerConfiguration.getInstance().getTimeStampSkewInSeconds() * 1000;
            OAuthAppDO oAuthAppDO = getOAuthAppDO(jwtSubject);
            String consumerKey = oAuthAppDO.getOauthConsumerKey();
            String tenantDomain = oAuthAppDO.getUser().getTenantDomain();
            if (!validateMandatoryFeilds(mandatoryClaims, claimsSet)) {
                return false;
            }

            //Validate issuer and subject.
            if (!validateIssuer(jwtIssuer, consumerKey) || !validateSubject(jwtSubject, consumerKey)) {
                return false;
            }

            long expTime = 0;
            long issuedTime = 0;
            if (expirationTime != null) {
                expTime = expirationTime.getTime();
            }
            if (issuedAtTime != null) {
                issuedTime = issuedAtTime.getTime();
            }

            //Validate signature validation, audience, nbf,exp time, jti.
            if (!validateJTI(signedJWT, jti, currentTimeInMillis, timeStampSkewMillis, expTime, issuedTime) ||
                    !validateAudience(validAudiences, audience) || !validateJWTWithExpTime(expirationTime,
                    currentTimeInMillis, timeStampSkewMillis) || !validateNotBeforeClaim(currentTimeInMillis,
                    timeStampSkewMillis, nbf) || !validateAgeOfTheToken(issuedAtTime, currentTimeInMillis,
                    timeStampSkewMillis) || !isValidSignature(consumerKey, signedJWT, tenantDomain, jwtSubject)) {
                return false;
            }

            return true;

        } catch (IdentityOAuth2Exception e) {
            return logAndThrowException(e.getMessage());
        }
    }

    private boolean validateMandatoryFeilds(List<String> mandatoryClaims, JWTClaimsSet claimsSet)
            throws OAuthClientAuthnException {

        for (String mandatoryClaim : mandatoryClaims) {
            if (claimsSet.getClaim(mandatoryClaim) == null) {
                String errorMessage = "Mandatory field :" + mandatoryClaim + " is missing in the JWT assertion.";
                return logAndThrowException(errorMessage);
            }
        }
        return true;
    }

    // "REQUIRED. sub. This MUST contain the client_id of the OAuth Client."
    public boolean validateSubject(String jwtSubject, String consumerKey) throws OAuthClientAuthnException {

        String errorMessage = String.format("Invalid Subject '%s' is found in the JWT. It should be equal to the '%s'",
                jwtSubject, consumerKey);
        if (!jwtSubject.trim().equals(consumerKey)) {
            if (log.isDebugEnabled()) {
                log.debug(errorMessage);
            }
            throw new OAuthClientAuthnException("Invalid Subject: " + jwtSubject + " is found in the JWT",
                    OAuth2ErrorCodes.INVALID_REQUEST);
        }
        return true;
    }

    // "REQUIRED. iss. This MUST contain the client_id of the OAuth Client." when a valid issuer is not specified in
    // the jwtValidator.
    private boolean validateIssuer(String issuer, String consumerKey) throws OAuthClientAuthnException {

        String errorMessage = String.format("Invalid issuer '%s' is found in the JWT. It should be equal to the '%s'"
                , issuer, consumerKey);
        String error = String.format("Invalid issuer '%s' is found in the JWT. ", issuer);
        //check whether the issuer is client_id
        if (isEmpty(validIssuer)) {
            if (!issuer.trim().equals(consumerKey)) {
                if (log.isDebugEnabled()) {
                    log.debug(errorMessage);
                }
                throw new OAuthClientAuthnException(error, OAuth2ErrorCodes.INVALID_REQUEST);
            }
            return true;
        } else if (!validIssuer.equals(issuer)) {
            if (log.isDebugEnabled()) {
                log.debug(errorMessage);
            }
            throw new OAuthClientAuthnException(error, OAuth2ErrorCodes.INVALID_REQUEST);
        }
        return true;
    }

    /**
     * Check if the sent audience claim is one of the expected audiences in the list
     *
     * @param expectedAudiences - Expected audiences list
     * @param audienceList      - Received audiences list
     * @return - boolean
     * @throws OAuthClientAuthnException - OAuthClientAuthnException
     */
    private boolean validateAudience(List<String> expectedAudiences, List<String> audienceList)
            throws OAuthClientAuthnException {

        for (String audience : audienceList) {
            if (expectedAudiences.contains(audience)) {
                return true;
            }
        }
        log.debug("None of the audience values matched with the allowed audience values");
        throw new OAuthClientAuthnException("Failed to match audience values.", OAuth2ErrorCodes.INVALID_REQUEST);
    }

    // "REQUIRED. JWT ID. A unique identifier for the token, which can be used to prevent reuse of the token.
    // These tokens MUST only be used once, unless conditions for reuse were negotiated between the parties; any such
    // negotiation is beyond the scope of this specification."
    private boolean validateJTI(SignedJWT signedJWT, String jti, long currentTimeInMillis,
                                long timeStampSkewMillis, long expTime, long issuedTime)
            throws OAuthClientAuthnException {

        if (enableJTICache) {
            JWTCacheEntry entry = jwtCache.getValueFromCache(jti);
            if (!validateJTIInCache(jti, signedJWT, entry, currentTimeInMillis, timeStampSkewMillis, this.jwtCache)) {
                return false;
            }
        }
        // Check JWT ID in DB
        if (!validateJWTInDataBase(jti, currentTimeInMillis, timeStampSkewMillis)) {
            return false;
        }
        persistJWTID(jti, expTime, issuedTime);
        return true;
    }

    private boolean validateJWTInDataBase(String jti, long currentTimeInMillis,
                                          long timeStampSkewMillis) throws OAuthClientAuthnException {

        JWTEntry jwtEntry = jwtStorageManager.getJwtFromDB(jti);
        if (jwtEntry == null) {
            if (log.isDebugEnabled()) {
                log.debug("JWT id: " + jti + " not found in the Storage the JWT has been validated successfully.");
            }
            return true;
        } else if (preventTokenReuse) {
            if (jwtStorageManager.isJTIExistsInDB(jti)) {
                String message = "JWT Token with JTI: " + jti + " has been replayed";
                return logAndThrowException(message);
            }
        } else {
            if (!checkJTIValidityPeriod(jti, jwtEntry.getExp(), currentTimeInMillis, timeStampSkewMillis)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkJTIValidityPeriod(String jti, long jwtExpiryTimeMillis, long currentTimeInMillis,
                                           long timeStampSkewMillis) throws OAuthClientAuthnException {

        if (currentTimeInMillis + timeStampSkewMillis > jwtExpiryTimeMillis) {
            if (log.isDebugEnabled()) {
                log.debug("JWT Token with jti: " + jti + "has been reused after the allowed expiry time: " +
                        jwtExpiryTimeMillis);
            }
            return true;
        } else {
            String message = "JWT Token with jti: " + jti + " has been replayed before the allowed expiry time: "
                    + jwtExpiryTimeMillis;
            return logAndThrowException(message);
        }
    }

    private void persistJWTID(final String jti, long expiryTime, long issuedTime) throws OAuthClientAuthnException {

        jwtStorageManager.persistJWTIdInDB(jti, expiryTime, issuedTime);
    }

    private OAuthAppDO getOAuthAppDO(String jwtSubject) throws OAuthClientAuthnException {

        OAuthAppDO oAuthAppDO = null;
        String message = String.format("Error while retrieving OAuth application with provided JWT information with " +
                "subject '%s' ", jwtSubject);
        try {
            oAuthAppDO = OAuth2Util.getAppInformationByClientId(jwtSubject);
            if (oAuthAppDO == null) {
                logAndThrowException(message);
            }
        } catch (InvalidOAuthClientException e) {
            logAndThrowException(message);
        } catch (IdentityOAuth2Exception e) {
            logAndThrowException(message);
        }
        return oAuthAppDO;
    }

    private boolean logAndThrowException(String detailedMessage) throws OAuthClientAuthnException {

        if (log.isDebugEnabled()) {
            log.debug(detailedMessage);
        }
        throw new OAuthClientAuthnException(detailedMessage, OAuth2ErrorCodes.INVALID_REQUEST);
    }

    private boolean validateJWTWithExpTime(Date expTime, long currentTimeInMillis, long timeStampSkewMillis)
            throws OAuthClientAuthnException {

        long expirationTime = expTime.getTime();
        if (currentTimeInMillis + timeStampSkewMillis > expirationTime) {
            String errorMessage = "JWT Token is expired. Expired Time: " + expTime;
            if (log.isDebugEnabled()) {
                log.debug(errorMessage);
            }
            throw new OAuthClientAuthnException(errorMessage, OAuth2ErrorCodes.INVALID_REQUEST);
        } else {
            return true;
        }
    }

    // "The JWT MAY contain an "nbf" (not before) claim that identifies
    // the time before which the token MUST NOT be accepted for
    // processing."
    private boolean validateNotBeforeClaim(long currentTimeInMillis, long timeStampSkewMillis, Date nbf)
            throws OAuthClientAuthnException {

        if (nbf != null) {

            if (currentTimeInMillis + timeStampSkewMillis - nbf.getTime() <= 0) {
                String message = "The token is used bfore the nbf claim value.";
                if (log.isDebugEnabled()) {
                    log.debug(message);
                }
                throw new OAuthClientAuthnException(message, OAuth2ErrorCodes.INVALID_REQUEST);
            }
        }
        return true;
    }

    private boolean isValidSignature(String clientId, SignedJWT signedJWT, String tenantDomain,
                                     String alias) throws OAuthClientAuthnException {

        X509Certificate cert = null;
        String jwksUri = "";
        boolean isValidSignature = false;
        String alg = signedJWT.getHeader().getAlgorithm().getName();

        try {
            cert = (X509Certificate) OAuth2Util.getX509CertOfOAuthApp(clientId, tenantDomain);
        } catch (IdentityOAuth2Exception e) {
            if (log.isDebugEnabled()) {
                String message = "Unable to retrieve the certificate for the service provider";
                log.debug(message, e);
            }
        }

        // Check if received signature algorithm matches the registered signature algorithm.
        if (!getRegisteredSigningAlgorithm(clientId).equals(alg)) {
            String errorMessage = "Signature algorithm validation failed. Registered algorithm does not match with " +
                    "the signed algorithm";
            throw new OAuthClientAuthnException(errorMessage, OAuth2ErrorCodes.INVALID_REQUEST);
        }

        // If cert is null check whether a jwks endpoint is configured for the service provider.
        if (cert == null) {
            try {
                ServiceProviderProperty[] spProperties = OAuth2Util.getServiceProvider(clientId).getSpProperties();
                for (ServiceProviderProperty spProperty : spProperties) {
                    if (Constants.JWKS_URI.equals(spProperty.getName())) {
                        jwksUri = spProperty.getValue();
                        break;
                    }
                }
                // Validate the signature of the assertion using the jwks end point.
                if (StringUtils.isNotBlank(jwksUri)) {
                    if (log.isDebugEnabled()) {
                        String message = "Found jwks end point for service provider " + jwksUri;
                        log.debug(message);
                    }
                    String jwtString = signedJWT.getParsedString();
                    Map<String, Object> options = new HashMap<String, Object>();
                    isValidSignature = new JWKSBasedJWTValidator().validateSignature(jwtString, jwksUri, alg, options);
                }
            } catch (IdentityOAuth2Exception e) {
                String errorMessage = "Error occurred while validating signature using jwks ";
                log.error(errorMessage, e);
                return false;
            }
        }
        // If certificate is not configured in service provider, it will throw an error.
        // For the existing clients need to handle that error and get from truststore.
        if (StringUtils.isBlank(jwksUri) && cert == null) {
            cert = getCertificate(tenantDomain, alias);
        }
        if (StringUtils.isBlank(jwksUri) && cert != null) {
            try {
                isValidSignature = validateSignature(signedJWT, cert);
            } catch (JOSEException e) {
                String message = "Error while validating the signature";
                throw new OAuthClientAuthnException(message, OAuth2ErrorCodes.INVALID_REQUEST, e);
            }
        }
        return isValidSignature;
    }

    /**
     * To retreive the processed JWT claimset.
     *
     * @param signedJWT signedJWT
     * @return JWT claim set
     * @throws IdentityOAuth2Exception
     */
    public JWTClaimsSet getClaimSet(SignedJWT signedJWT) throws OAuthClientAuthnException {

        JWTClaimsSet claimsSet;
        String errorMessage;
        if (signedJWT == null) {
            errorMessage = "No Valid Assertion was found for " + Constants.OAUTH_JWT_BEARER_GRANT_TYPE;
            throw new OAuthClientAuthnException(errorMessage, OAuth2ErrorCodes.INVALID_REQUEST);
        }
        try {
            claimsSet = signedJWT.getJWTClaimsSet();
            if (claimsSet == null) {
                errorMessage = "Claim values are empty in the given JSON Web Token.";
                throw new OAuthClientAuthnException(errorMessage, OAuth2ErrorCodes.INVALID_REQUEST);
            }
        } catch (ParseException e) {
            String errorMsg = "Error when trying to retrieve claimsSet from the JWT.";
            if (log.isDebugEnabled()) {
                log.debug(errorMsg);
            }
            throw new OAuthClientAuthnException(errorMsg, OAuth2ErrorCodes.INVALID_REQUEST);
        }
        return claimsSet;
    }

    /**
     * The default implementation which creates the subject from the 'sub' attribute.
     *
     * @param claimsSet all the JWT claims
     * @return The subject, to be used
     */
    public String resolveSubject(JWTClaimsSet claimsSet) {

        return claimsSet.getSubject();
    }

    private static X509Certificate getCertificate(String tenantDomain, String alias) throws OAuthClientAuthnException {

        int tenantId;
        try {
            tenantId = CDSIdentityDataHolder.getInstance().getRealmService().getTenantManager()
                    .getTenantId(tenantDomain);
        } catch (org.wso2.carbon.user.api.UserStoreException e) {
            String errorMsg = "Error getting the tenant ID for the tenant domain : " + tenantDomain;
            throw new OAuthClientAuthnException(errorMsg, OAuth2ErrorCodes.INVALID_REQUEST);
        }

        KeyStoreManager keyStoreManager;
        // get an instance of the corresponding Key Store Manager instance
        keyStoreManager = KeyStoreManager.getInstance(tenantId);
        KeyStore keyStore;
        try {
            if (tenantId != MultitenantConstants.SUPER_TENANT_ID) {
                // for tenants, load key from their generated key store
                keyStore = keyStoreManager.getKeyStore(generateKSNameFromDomainName(tenantDomain));
            } else {
                // for super tenant, load the default pub. cert using the config. in carbon.xml
                keyStore = keyStoreManager.getPrimaryKeyStore();
            }
            return (X509Certificate) keyStore.getCertificate(alias);

        } catch (KeyStoreException e) {
            String errorMsg = "Error instantiating an X509Certificate object for the certificate alias: " + alias +
                    " in tenant:" + tenantDomain;
            if (log.isDebugEnabled()) {
                log.debug(errorMsg);
            }
            throw new OAuthClientAuthnException(errorMsg, OAuth2ErrorCodes.INVALID_REQUEST);
        } catch (Exception e) {
            String message = "Unable to load key store manager for the tenant domain: " + tenantDomain;
            //keyStoreManager throws Exception
            if (log.isDebugEnabled()) {
                log.debug(message);
            }
            throw new OAuthClientAuthnException(message, OAuth2ErrorCodes.INVALID_REQUEST);
        }
    }

    private static String generateKSNameFromDomainName(String tenantDomain) {

        String ksName = tenantDomain.trim().replace(FULLSTOP_DELIMITER, DASH_DELIMITER);
        return ksName + KEYSTORE_FILE_EXTENSION;
    }

    private boolean validateSignature(SignedJWT signedJWT, X509Certificate x509Certificate)
            throws JOSEException, OAuthClientAuthnException {

        JWSVerifier verifier;
        JWSHeader header = signedJWT.getHeader();
        if (x509Certificate == null) {
            throw new OAuthClientAuthnException("Unable to locate certificate for JWT " + header.toString(),
                    OAuth2ErrorCodes.INVALID_REQUEST);
        }

        String alg = signedJWT.getHeader().getAlgorithm().getName();
        if (isEmpty(alg)) {
            throw new OAuthClientAuthnException("Signature validation failed. No algorithm is found in the " +
                    "JWT header.", OAuth2ErrorCodes.INVALID_REQUEST);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Signature Algorithm found in the JWT Header: " + alg);
            }
            if (alg.indexOf(RS) == 0 || alg.indexOf(PS) == 0) {
                // At this point 'x509Certificate' will never be null.
                PublicKey publicKey = x509Certificate.getPublicKey();
                if (publicKey instanceof RSAPublicKey) {
                    verifier = new RSASSAVerifier((RSAPublicKey) publicKey);
                } else {
                    throw new OAuthClientAuthnException("Signature validation failed. Public key is not an " +
                            "RSA public key.", OAuth2ErrorCodes.INVALID_REQUEST);
                }
            } else {
                throw new OAuthClientAuthnException("Signature Algorithm not supported : " + alg,
                        OAuth2ErrorCodes.INVALID_REQUEST);
            }
        }
        // At this point 'verifier' will never be null.
        return signedJWT.verify(verifier);
    }

    private boolean validateAgeOfTheToken(Date issuedAtTime, long currentTimeInMillis, long timeStampSkewMillis) throws
            OAuthClientAuthnException {

        if (issuedAtTime == null) {
            return true;
        }
        if (rejectBeforeInMinutes > 0) {
            long issuedAtTimeMillis = issuedAtTime.getTime();
            long rejectBeforeMillis = 1000L * 60 * rejectBeforeInMinutes;
            if (currentTimeInMillis + timeStampSkewMillis - issuedAtTimeMillis >
                    rejectBeforeMillis) {
                String logMsg = getTokenTooOldMessage(currentTimeInMillis, timeStampSkewMillis, issuedAtTimeMillis,
                        rejectBeforeMillis);
                if (log.isDebugEnabled()) {
                    log.debug(logMsg);
                }
                throw new OAuthClientAuthnException("The jwt is too old to use.", OAuth2ErrorCodes.INVALID_REQUEST);
            }
        }
        return true;
    }

    private String getTokenTooOldMessage(long currentTimeInMillis, long timeStampSkewMillis, long issuedAtTimeMillis,
                                         long rejectBeforeMillis) {

        StringBuilder tmp = new StringBuilder();
        tmp.append("JSON Web Token is issued before the allowed time.");
        tmp.append(" Issued At Time(ms) : ");
        tmp.append(issuedAtTimeMillis);
        tmp.append(", Reject before limit(ms) : ");
        tmp.append(rejectBeforeMillis);
        tmp.append(", TimeStamp Skew : ");
        tmp.append(timeStampSkewMillis);
        tmp.append(", Current Time : ");
        tmp.append(currentTimeInMillis);
        tmp.append(". JWT Rejected and validation terminated");
        return tmp.toString();
    }

    private boolean validateJTIInCache(String jti, SignedJWT signedJWT, JWTCacheEntry entry, long currentTimeInMillis,
                                       long timeStampSkewMillis, JWTCache jwtCache) throws OAuthClientAuthnException {

        if (entry == null) {
            // Update the cache with the new JWT for the same JTI.
            jwtCache.addToCache(jti, new JWTCacheEntry(signedJWT));
        } else if (preventTokenReuse) {
            throw new OAuthClientAuthnException("JWT Token with jti: " + jti + " has been replayed",
                    OAuth2ErrorCodes.INVALID_REQUEST);
        } else {
            try {
                SignedJWT cachedJWT = entry.getJwt();
                long cachedJWTExpiryTimeMillis = cachedJWT.getJWTClaimsSet().getExpirationTime().getTime();
                if (checkJTIValidityPeriod(jti, cachedJWTExpiryTimeMillis, currentTimeInMillis, timeStampSkewMillis)) {
                    // Update the cache with the new JWT for the same JTI.
                    jwtCache.addToCache(jti, new JWTCacheEntry(signedJWT));
                } else {
                    return false;
                }
            } catch (ParseException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Unable to parse the cached jwt assertion : " + entry.getEncodedJWt());
                }
                throw new OAuthClientAuthnException("JTI validation failed.", OAuth2ErrorCodes.INVALID_REQUEST);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("JWT id: " + jti + " not found in the cache and the JWT has been validated " +
                    "successfully in cache.");
        }
        return true;
    }

    @Generated(message = "Ignoring because it requires a service call")
    public String getRegisteredSigningAlgorithm(String clientId) throws OAuthClientAuthnException {

        try {
            return new IdentityCommonHelper().getAppPropertyFromSPMetaData(clientId,
                    IdentityCommonConstants.TOKEN_ENDPOINT_AUTH_SIGNING_ALG);
        } catch (OpenBankingException e) {
            throw new OAuthClientAuthnException("Unable to retrieve token signing algorithm. Token signing " +
                    "algorithm not registered", OAuth2ErrorCodes.INVALID_REQUEST, e);
        }
    }
}
