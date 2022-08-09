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

package org.wso2.carbon.identity.authenticator.smsotp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.extension.identity.helper.FederatedAuthenticatorUtil;
import org.wso2.carbon.extension.identity.helper.util.IdentityHelperUtil;
import org.wso2.carbon.identity.application.authentication.framework.AbstractApplicationAuthenticator;
import org.wso2.carbon.identity.application.authentication.framework.AuthenticatorFlowStatus;
import org.wso2.carbon.identity.application.authentication.framework.FederatedApplicationAuthenticator;
import org.wso2.carbon.identity.application.authentication.framework.LocalApplicationAuthenticator;
import org.wso2.carbon.identity.application.authentication.framework.config.ConfigurationFacade;
import org.wso2.carbon.identity.application.authentication.framework.config.model.StepConfig;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.exception.InvalidCredentialsException;
import org.wso2.carbon.identity.application.authentication.framework.exception.LogoutFailedException;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkConstants;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;
import org.wso2.carbon.identity.application.common.model.Property;
import org.wso2.carbon.identity.authenticator.smsotp.exception.SMSOTPException;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.wso2.carbon.identity.authenticator.smsotp.SMSOTPConstants.CHAR_SET_UTF_8;
import static java.util.Base64.getEncoder;

/**
 * Authenticator of SMS OTP
 */
public class SMSOTPAuthenticator extends AbstractApplicationAuthenticator implements FederatedApplicationAuthenticator {

    private static final Log log = LogFactory.getLog(SMSOTPAuthenticator.class);

    /**
     * Check whether the authentication or logout request can be handled by the authenticator
     */
    @Override
    public boolean canHandle(HttpServletRequest request) {

        if (log.isDebugEnabled()) {
            log.debug("Inside SMSOTPAuthenticator canHandle method and check the existence of mobile number and " +
                    "otp code");
        }
        return ((StringUtils.isNotEmpty(request.getParameter(SMSOTPConstants.RESEND))
                && StringUtils.isEmpty(request.getParameter(SMSOTPConstants.CODE)))
                || StringUtils.isNotEmpty(request.getParameter(SMSOTPConstants.CODE))
                || StringUtils.isNotEmpty(request.getParameter(SMSOTPConstants.MOBILE_NUMBER)));
    }

    @Override
    public AuthenticatorFlowStatus process(HttpServletRequest request,
                                           HttpServletResponse response,
                                           AuthenticationContext context)
            throws AuthenticationFailedException, LogoutFailedException {
        // if the logout request comes, then no need to go through and complete the flow.
        if (context.isLogoutRequest()) {
            return AuthenticatorFlowStatus.SUCCESS_COMPLETED;
        } else if (StringUtils.isNotEmpty(request.getParameter(SMSOTPConstants.MOBILE_NUMBER))) {
            // if the request comes with MOBILE_NUMBER, it will go through this flow.
            initiateAuthenticationRequest(request, response, context);
            return AuthenticatorFlowStatus.INCOMPLETE;
        } else if (StringUtils.isEmpty(request.getParameter(SMSOTPConstants.CODE))) {
            // if the request comes with code, it will go through this flow.
            initiateAuthenticationRequest(request, response, context);
            if (context.getProperty(SMSOTPConstants.AUTHENTICATION)
                    .equals(SMSOTPConstants.AUTHENTICATOR_NAME)) {
                // if the request comes with authentication is SMSOTP, it will go through this flow.
                return AuthenticatorFlowStatus.INCOMPLETE;
            } else {
                // if the request comes with authentication is basic, complete the flow.
                return AuthenticatorFlowStatus.SUCCESS_COMPLETED;
            }
        } else {
            return super.process(request, response, context);
        }
    }

    /**
     * Initiate the authentication request.
     */
    @Override
    protected void initiateAuthenticationRequest(HttpServletRequest request, HttpServletResponse response,
                                                 AuthenticationContext context) throws AuthenticationFailedException {

        try {
            String username;
            AuthenticatedUser authenticatedUser;
            String mobileNumber;
            String tenantDomain = context.getTenantDomain();
            context.setProperty(SMSOTPConstants.AUTHENTICATION, SMSOTPConstants.AUTHENTICATOR_NAME);
            if (!tenantDomain.equals(SMSOTPConstants.SUPER_TENANT)) {
                IdentityHelperUtil.loadApplicationAuthenticationXMLFromRegistry(context, getName(), tenantDomain);
            }
            FederatedAuthenticatorUtil.setUsernameFromFirstStep(context);
            username = String.valueOf(context.getProperty(SMSOTPConstants.USER_NAME));
            authenticatedUser = (AuthenticatedUser) context.getProperty(SMSOTPConstants.AUTHENTICATED_USER);
            // find the authenticated user.
            if (authenticatedUser == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Authentication failed: Could not find the authenticated user. ");
                }
                throw new AuthenticationFailedException
                        ("Authentication failed: Cannot proceed further without identifying the user. ");
            }
            boolean isSMSOTPMandatory = SMSOTPUtils.isSMSOTPMandatory(context);
            boolean isUserExists = FederatedAuthenticatorUtil.isUserExistInUserStore(username);
            String queryParams = FrameworkUtils.getQueryStringWithFrameworkContextId(context.getQueryParams(),
                    context.getCallerSessionKey(), context.getContextIdentifier());
            String errorPage = getErrorPage(context);
            // SMS OTP authentication is mandatory and user doesn't disable SMS OTP claim in user's profile.
            if (isSMSOTPMandatory) {
                if (log.isDebugEnabled()) {
                    log.debug("SMS OTP is mandatory. Hence processing in mandatory path");
                }
                processSMSOTPMandatoryCase(context, request, response, queryParams, username, isUserExists);
            } else if (isUserExists && !SMSOTPUtils.isSMSOTPDisableForLocalUser(username, context)) {
                if (context.isRetrying() && !Boolean.parseBoolean(request.getParameter(SMSOTPConstants.RESEND))) {
                    checkStatusCode(response, context, queryParams, errorPage);
                } else {
                    proceedWithOTP(response, context, errorPage, queryParams, username);

                }
            } else {
                processFirstStepOnly(authenticatedUser, context);
            }
        } catch (SMSOTPException e) {
            throw new AuthenticationFailedException("Failed to get the parameters from authentication xml file. ", e);
        } catch (UserStoreException e) {
            throw new AuthenticationFailedException("Failed to get the user from User Store. ", e);
        }
    }

    /**
     * Get the loginPage from authentication.xml file or use the login page from constant file.
     *
     * @param context the AuthenticationContext
     * @return the loginPage
     * @throws AuthenticationFailedException
     */
    private String getLoginPage(AuthenticationContext context) throws AuthenticationFailedException {

        String loginPage = SMSOTPUtils.getLoginPageFromXMLFile(context);
        if (StringUtils.isEmpty(loginPage)) {
            loginPage = ConfigurationFacade.getInstance().getAuthenticationEndpointURL()
                    .replace(SMSOTPConstants.LOGIN_PAGE, SMSOTPConstants.SMS_LOGIN_PAGE);
            if (log.isDebugEnabled()) {
                log.debug("Default authentication endpoint context is used");
            }
        }
        return loginPage;
    }

    /**
     * Get the errorPage from authentication.xml file or use the error page from constant file.
     *
     * @param context the AuthenticationContext
     * @return the errorPage
     * @throws AuthenticationFailedException
     */
    private String getErrorPage(AuthenticationContext context) throws AuthenticationFailedException {

        String errorPage = SMSOTPUtils.getErrorPageFromXMLFile(context);
        if (StringUtils.isEmpty(errorPage)) {
            errorPage = ConfigurationFacade.getInstance().getAuthenticationEndpointURL()
                    .replace(SMSOTPConstants.LOGIN_PAGE, SMSOTPConstants.ERROR_PAGE);
            if (log.isDebugEnabled()) {
                log.debug("Default authentication endpoint context is used");
            }
        }
        return errorPage;
    }

    /**
     * To get the redirection URL.
     *
     * @param baseURI     the base path
     * @param queryParams the queryParams
     * @return url
     */
    private String getURL(String baseURI, String queryParams) {

        String url;
        if (StringUtils.isNotEmpty(queryParams)) {
            url = baseURI + "?" + queryParams + "&" + SMSOTPConstants.NAME_OF_AUTHENTICATORS + getName();
        } else {
            url = baseURI + "?" + SMSOTPConstants.NAME_OF_AUTHENTICATORS + getName();
        }
        return url;
    }

    /**
     * Redirect to an error page.
     *
     * @param response    the HttpServletResponse
     * @param queryParams the queryParams
     * @throws AuthenticationFailedException
     */
    private void redirectToErrorPage(HttpServletResponse response, AuthenticationContext context, String queryParams,
                                     String retryParam)
            throws AuthenticationFailedException {
        // that Enable the SMS OTP in user's Profile. Cannot proceed further without SMS OTP authentication.
        try {
            String errorPage = getErrorPage(context);
            String url = getURL(errorPage, queryParams);
            response.sendRedirect(url + retryParam);
        } catch (IOException e) {
            throw new AuthenticationFailedException("Exception occurred while redirecting to errorPage. ", e);
        }
    }

    /**
     * In SMSOTP optional case proceed with first step only.It can be basic or federated.
     *
     * @param authenticatedUser the name of authenticatedUser
     * @param context           the AuthenticationContext
     */
    private void processFirstStepOnly(AuthenticatedUser authenticatedUser, AuthenticationContext context) {

        if (log.isDebugEnabled()) {
            log.debug("Processing First step only. Skipping SMSOTP");
        }
        //the authentication flow happens with basic authentication.
        StepConfig stepConfig = context.getSequenceConfig().getStepMap().get(context.getCurrentStep() - 1);
        if (stepConfig.getAuthenticatedAutenticator().getApplicationAuthenticator() instanceof
                LocalApplicationAuthenticator) {
            if (log.isDebugEnabled()) {
                log.debug("Found local authenticator in previous step. Hence setting a local user");
            }
            FederatedAuthenticatorUtil.updateLocalAuthenticatedUserInStepConfig(context, authenticatedUser);
            context.setProperty(SMSOTPConstants.AUTHENTICATION, SMSOTPConstants.BASIC);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Found federated authenticator in previous step. Hence setting a local user");
            }
            FederatedAuthenticatorUtil.updateAuthenticatedUserInStepConfig(context, authenticatedUser);
            context.setProperty(SMSOTPConstants.AUTHENTICATION, SMSOTPConstants.FEDERETOR);
        }
    }

    /**
     * Check with SMSOTP mandatory case with SMSOTP flow.
     *
     * @param context      the AuthenticationContext
     * @param request      the HttpServletRequest
     * @param response     the HttpServletResponse
     * @param queryParams  the queryParams
     * @param username     the Username
     * @param isUserExists check whether user exist or not
     * @throws AuthenticationFailedException
     * @throws SMSOTPException
     */
    private void processSMSOTPMandatoryCase(AuthenticationContext context, HttpServletRequest request,
                                            HttpServletResponse response, String queryParams, String username,
                                            boolean isUserExists) throws AuthenticationFailedException, SMSOTPException {
        //the authentication flow happens with sms otp authentication.
        String tenantDomain = context.getTenantDomain();
        String errorPage = getErrorPage(context);
        if (context.isRetrying() && !Boolean.parseBoolean(request.getParameter(SMSOTPConstants.RESEND))) {
            checkStatusCode(response, context, queryParams, errorPage);
        } else {
            processSMSOTPFlow(context, request, response, isUserExists, username, queryParams, tenantDomain,
                    errorPage);
        }
    }

    /**
     * Check with SMSOTP flow with user existence.
     *
     * @param context      the AuthenticationContext
     * @param request      the HttpServletRequest
     * @param response     the HttpServletResponse
     * @param isUserExists check whether user exist or not
     * @param username     the UserName
     * @param queryParams  the queryParams
     * @param tenantDomain the TenantDomain
     * @param errorPage    the errorPage
     * @throws AuthenticationFailedException
     * @throws SMSOTPException
     */
    private void processSMSOTPFlow(AuthenticationContext context, HttpServletRequest request,
                                   HttpServletResponse response, boolean isUserExists, String username,
                                   String queryParams, String tenantDomain, String errorPage)
            throws AuthenticationFailedException, SMSOTPException {

        if (isUserExists) {
            boolean isSMSOTPDisabledByUser = SMSOTPUtils.isSMSOTPDisableForLocalUser(username, context);
            if (log.isDebugEnabled()) {
                log.debug("Has user enabled SMS OTP : " + isSMSOTPDisabledByUser);
            }
            if (isSMSOTPDisabledByUser) {
                // that Enable the SMS OTP in user's Profile. Cannot proceed further without SMS OTP authentication.
                redirectToErrorPage(response, context, queryParams, SMSOTPConstants.ERROR_SMSOTP_DISABLE);
            }
        }
        proceedWithOTP(response, context, errorPage, queryParams, username);
    }

    /**
     * Proceed with One Time Password.
     *
     * @param response     the HttpServletResponse
     * @param context      the AuthenticationContext
     * @param errorPage    the errorPage
     * @param queryParams  the queryParams
     * @param username     the Username
     * @throws AuthenticationFailedException
     */
    private void proceedWithOTP(HttpServletResponse response, AuthenticationContext context, String errorPage,
                                String queryParams, String username)
            throws AuthenticationFailedException {

        String screenValue;
        Map<String, String> authenticatorProperties = context.getAuthenticatorProperties();
        boolean isEnableResendCode = SMSOTPUtils.isEnableResendCode(context);
        String loginPage = getLoginPage(context);
        String tenantDomain = MultitenantUtils.getTenantDomain(username);
        String tenantAwareUsername = MultitenantUtils.getTenantAwareUsername(username);
        UserRealm userRealm = SMSOTPUtils.getUserRealm(tenantDomain);

        try {
            // One time password is generated and stored in the context.

	        Random randomElement = new Random();

            String otpToken = SMSOTPConstants.SAMPLE_OTPS[randomElement.nextInt(SMSOTPConstants.SAMPLE_OTPS.length)];

	        if ((SMSOTPUtils.getTokenExpiryTime(context)) != null) {
		        long tokenExpiryTime = Integer.parseInt(SMSOTPUtils.getTokenExpiryTime(context));
		        context.setProperty(SMSOTPConstants.TOKEN_VALIDITY_TIME, tokenExpiryTime);
	        }

            context.setProperty(SMSOTPConstants.OTP_TOKEN, otpToken);
            if (log.isDebugEnabled()) {
                log.debug("Generated OTP successfully and set to the context.");
            }

            boolean connectionResult = true;

            if (!connectionResult) {
                String retryParam;
                if (context.getProperty(SMSOTPConstants.ERROR_CODE) != null) {
                    String errorCode = context.getProperty(SMSOTPConstants.ERROR_CODE).toString();
                    // If UseInternalErrorCodes is configured as true, then http response error codes will be mapped
                    // to local error codes and passed as query param value for authfailure msg.
                    if (SMSOTPUtils.useInternalErrorCodes(context)) {
                        String errorResponseCode = getHttpErrorResponseCode(errorCode);
                        if (StringUtils.isNotEmpty(errorResponseCode)) {
                            String internalErrorCode = SMSOTPConstants.ErrorMessage.
                                    getMappedInternalErrorCode(errorResponseCode).getCode();
                            errorCode = URLEncoder.encode(internalErrorCode, CHAR_SET_UTF_8);
                        }
                    }
                    retryParam = SMSOTPConstants.ERROR_MESSAGE + errorCode;
                    String errorInfo = context.getProperty(SMSOTPConstants.ERROR_INFO).toString();
                    if (Boolean.parseBoolean(authenticatorProperties.get(SMSOTPConstants.SHOW_ERROR_INFO)) &&
                            errorInfo != null) {
                        retryParam = retryParam + SMSOTPConstants.ERROR_MESSAGE_DETAILS + getEncoder().encodeToString
                                (errorInfo.getBytes());
                    }
                } else {
                    retryParam = SMSOTPConstants.ERROR_MESSAGE + SMSOTPConstants.UNABLE_SEND_CODE_VALUE;
                }
                String redirectUrl = getURL(errorPage, queryParams);
                response.sendRedirect(redirectUrl + SMSOTPConstants.RESEND_CODE + isEnableResendCode + retryParam);
            } else {
                long sentOTPTokenTime = System.currentTimeMillis();
                context.setProperty(SMSOTPConstants.SENT_OTP_TOKEN_TIME, sentOTPTokenTime);
                String url = getURL(loginPage, queryParams);
                boolean isUserExists = FederatedAuthenticatorUtil.isUserExistInUserStore(username);
                if (isUserExists) {
                    screenValue = getScreenAttribute(context, userRealm, tenantAwareUsername);
                    if (screenValue != null) {
                        url = url + SMSOTPConstants.SCREEN_VALUE + screenValue;
                    }
                }
                response.sendRedirect(url);
            }
        } catch (IOException e) {
            throw new AuthenticationFailedException("Error while sending the HTTP request. ", e);
        } catch (UserStoreException e) {
            throw new AuthenticationFailedException("Failed to get the user from user store. ", e);
        }
    }

    /**
     * Check the status codes when resend and retry enabled.
     *
     * @param response    the HttpServletResponse
     * @param context     the AuthenticationContext
     * @param queryParams the queryParams
     * @param errorPage   the errorPage
     * @throws AuthenticationFailedException
     */
    private void checkStatusCode(HttpServletResponse response, AuthenticationContext context,
                                 String queryParams, String errorPage) throws AuthenticationFailedException {

        boolean isRetryEnabled = SMSOTPUtils.isRetryEnabled(context);
        String loginPage = getLoginPage(context);
        AuthenticatedUser authenticatedUser =
                (AuthenticatedUser) context.getProperty(SMSOTPConstants.AUTHENTICATED_USER);
        String url = getURL(loginPage, queryParams);
        if (StringUtils.isNotEmpty(getScreenValue(context))) {
            url = url + SMSOTPConstants.SCREEN_VALUE + getScreenValue(context);
        }
        try {
            if (SMSOTPUtils.isLocalUser(context) && SMSOTPUtils.isAccountLocked(authenticatedUser)) {
                boolean showAuthFailureReason = SMSOTPUtils.isShowAuthFailureReason(context);
                String retryParam;
                if (showAuthFailureReason) {
                    long unlockTime = getUnlockTimeInMilliSeconds(authenticatedUser);
                    long timeToUnlock = unlockTime - System.currentTimeMillis();
                    if (timeToUnlock > 0) {
                        queryParams += "&unlockTime=" + Math.round((double) timeToUnlock / 1000 / 60);
                    }
                    retryParam = SMSOTPConstants.ERROR_USER_ACCOUNT_LOCKED;
                } else {
                    retryParam = SMSOTPConstants.RETRY_PARAMS;
                }
                redirectToErrorPage(response, context, queryParams, retryParam);
            } else if (isRetryEnabled) {
                if (StringUtils.isNotEmpty((String) context.getProperty(SMSOTPConstants.TOKEN_EXPIRED))) {
                    response.sendRedirect(url + SMSOTPConstants.RESEND_CODE
                            + SMSOTPUtils.isEnableResendCode(context) + SMSOTPConstants.ERROR_MESSAGE +
                            SMSOTPConstants.TOKEN_EXPIRED_VALUE);
                } else {
                    response.sendRedirect(url + SMSOTPConstants.RESEND_CODE
                            + SMSOTPUtils.isEnableResendCode(context) + SMSOTPConstants.RETRY_PARAMS);
                }
            } else {
                url = getURL(errorPage, queryParams);
                if (Boolean.parseBoolean(String.valueOf(context.getProperty(SMSOTPConstants.CODE_MISMATCH)))) {
                    response.sendRedirect(url + SMSOTPConstants.RESEND_CODE
                            + SMSOTPUtils.isEnableResendCode(context) + SMSOTPConstants.ERROR_MESSAGE
                            + SMSOTPConstants.ERROR_CODE_MISMATCH);
                } else if (StringUtils.isNotEmpty((String) context.getProperty(SMSOTPConstants.TOKEN_EXPIRED))) {
                    response.sendRedirect(url + SMSOTPConstants.RESEND_CODE
                            + SMSOTPUtils.isEnableResendCode(context) + SMSOTPConstants.ERROR_MESSAGE + SMSOTPConstants
                            .TOKEN_EXPIRED_VALUE);
                } else {
                    response.sendRedirect(url + SMSOTPConstants.RESEND_CODE
                            + SMSOTPUtils.isEnableResendCode(context) + SMSOTPConstants.RETRY_PARAMS);
                }
            }
        } catch (IOException e) {
            throw new AuthenticationFailedException("Authentication Failed: An IOException was caught. ", e);
        }
    }

    /**
     * Get the screen value for configured screen attribute.
     *
     * @param context the AuthenticationContext
     * @return screenValue
     * @throws AuthenticationFailedException
     */
    private String getScreenValue(AuthenticationContext context) throws AuthenticationFailedException {

        String screenValue;
        String username = String.valueOf(context.getProperty(SMSOTPConstants.USER_NAME));
        String tenantDomain = MultitenantUtils.getTenantDomain(username);
        String tenantAwareUsername = MultitenantUtils.getTenantAwareUsername(username);
        UserRealm userRealm = SMSOTPUtils.getUserRealm(tenantDomain);
        try {
            screenValue = getScreenAttribute(context, userRealm, tenantAwareUsername);
        } catch (UserStoreException e) {
            throw new AuthenticationFailedException("Failed to get the screen attribute for the user " +
                    tenantAwareUsername + " from user store. ", e);
        }
        return screenValue;
    }

    /**
     * Process the response of the SMSOTP end-point.
     *
     * @param request  the HttpServletRequest
     * @param response the HttpServletResponse
     * @param context  the AuthenticationContext
     * @throws AuthenticationFailedException
     */
    @Override
    protected void processAuthenticationResponse(HttpServletRequest request, HttpServletResponse response,
                                                 AuthenticationContext context) throws AuthenticationFailedException {

        AuthenticatedUser authenticatedUser =
                (AuthenticatedUser) context.getProperty(SMSOTPConstants.AUTHENTICATED_USER);
        boolean isLocalUser = SMSOTPUtils.isLocalUser(context);

        if (authenticatedUser != null && isLocalUser && SMSOTPUtils.isAccountLocked(authenticatedUser)) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Authentication failed since authenticated user: %s,  account is locked.",
                        authenticatedUser));
            }
            context.setProperty(SMSOTPConstants.ACCOUNT_LOCKED, true);
            throw new AuthenticationFailedException("User account is locked.");
        }

        String userToken = request.getParameter(SMSOTPConstants.CODE);

        if (StringUtils.isEmpty(request.getParameter(SMSOTPConstants.CODE))) {
            throw new InvalidCredentialsException("Code cannot not be null");
        }
        if (Boolean.parseBoolean(request.getParameter(SMSOTPConstants.RESEND))) {
            if (log.isDebugEnabled()) {
                log.debug("Retrying to resend the OTP");
            }
            throw new InvalidCredentialsException("Retrying to resend the OTP");
        }
        boolean succeededAttempt = false;
        if (Arrays.asList(SMSOTPConstants.SAMPLE_OTPS).contains(userToken)) {
            processValidUserToken(context, authenticatedUser);
            succeededAttempt = true;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Given otp code is a mismatch.");
            }
            context.setProperty(SMSOTPConstants.CODE_MISMATCH, true);
        }
        if (!succeededAttempt) {
            handleSmsOtpVerificationFail(context);
            throw new AuthenticationFailedException("Invalid code. Verification failed.");
        }
        // It reached here means the authentication was successful.
        resetSmsOtpFailedAttempts(context);
    }

    private void processValidUserToken(AuthenticationContext context, AuthenticatedUser authenticatedUser) throws
            AuthenticationFailedException {
        Optional<Object> tokenValidityTime = Optional.ofNullable(context.getProperty(SMSOTPConstants.
                TOKEN_VALIDITY_TIME));
        if (!tokenValidityTime.isPresent() || !NumberUtils.isNumber(tokenValidityTime.get().toString())) {
            log.error("TokenExpiryTime property is not configured in application-authentication.xml or SMS OTP " +
                    "Authenticator UI");
            context.setSubject(authenticatedUser);
            return;
        }

        Optional<Object> otpTokenSentTime = Optional.ofNullable(context.getProperty(SMSOTPConstants.
                SENT_OTP_TOKEN_TIME));
        if (!otpTokenSentTime.isPresent() || !NumberUtils.isNumber(otpTokenSentTime.get().toString())) {
            if (log.isDebugEnabled()) {
                log.debug("Could not find OTP sent time");
            }
            throw new AuthenticationFailedException("Internal Error Occurred");
        }

        long elapsedTokenTime = System.currentTimeMillis() - Long.parseLong(otpTokenSentTime.get().toString());

        if (elapsedTokenTime <= (Long.parseLong(tokenValidityTime.get().toString()) * 1000)) {
            context.setSubject(authenticatedUser);
        } else {
            context.setProperty(SMSOTPConstants.TOKEN_EXPIRED, SMSOTPConstants.TOKEN_EXPIRED_VALUE);
            handleSmsOtpVerificationFail(context);
            throw new AuthenticationFailedException("OTP code has expired");
        }
    }

    /**
     * Get the user realm of the logged in user.
     *
     * @param username the Username
     * @return the userRealm
     * @throws AuthenticationFailedException
     */
    private UserRealm getUserRealm(String username) throws AuthenticationFailedException {

        UserRealm userRealm = null;
        try {
            if (StringUtils.isNotEmpty(username)) {
                String tenantDomain = MultitenantUtils.getTenantDomain(username);
                int tenantId = IdentityTenantUtil.getTenantId(tenantDomain);
                RealmService realmService = IdentityTenantUtil.getRealmService();
                userRealm = realmService.getTenantUserRealm(tenantId);
            }
        } catch (UserStoreException e) {
            throw new AuthenticationFailedException("Cannot find the user realm. ", e);
        }
        return userRealm;
    }

    /**
     * Get the user realm of the logged in user.
     *
     * @param authenticatedUser Authenticated user.
     * @return The userRealm.
     * @throws AuthenticationFailedException Exception on authentication failure.
     */
    private UserRealm getUserRealm(AuthenticatedUser authenticatedUser) throws AuthenticationFailedException {

        UserRealm userRealm = null;
        try {
            if (authenticatedUser != null) {
                String tenantDomain = authenticatedUser.getTenantDomain();
                int tenantId = IdentityTenantUtil.getTenantId(tenantDomain);
                RealmService realmService = IdentityTenantUtil.getRealmService();
                userRealm = realmService.getTenantUserRealm(tenantId);
            }
        } catch (UserStoreException e) {
            throw new AuthenticationFailedException("Cannot find the user realm.", e);
        }
        return userRealm;
    }

    /**
     * Get the friendly name of the Authenticator
     */
    public String getFriendlyName() {

        return SMSOTPConstants.AUTHENTICATOR_FRIENDLY_NAME;
    }

    /**
     * Get the name of the Authenticator
     */
    public String getName() {

        return SMSOTPConstants.AUTHENTICATOR_NAME;
    }

    /**
     * Get the Context identifier sent with the request.
     */
    public String getContextIdentifier(HttpServletRequest httpServletRequest) {

        return httpServletRequest.getParameter(FrameworkConstants.SESSION_DATA_KEY);
    }

    @Override
    protected boolean retryAuthenticationEnabled() {

        return true;
    }

    /**
     * Get the configuration properties of UI
     */
    @Override
    public List<Property> getConfigurationProperties() {

        List<Property> configProperties = new ArrayList<Property>();

        Property smsUrl = new Property();
        smsUrl.setName(SMSOTPConstants.SMS_URL);
        smsUrl.setDisplayName("SMS URL");
        smsUrl.setRequired(false);
        smsUrl.setDescription("Enter client sms url value. If the phone number and text message are in URL, " +
                "specify them as $ctx.num and $ctx.msg");
        smsUrl.setDisplayOrder(0);
        configProperties.add(smsUrl);

        Property httpMethod = new Property();
        httpMethod.setName(SMSOTPConstants.HTTP_METHOD);
        httpMethod.setDisplayName("HTTP Method");
        httpMethod.setRequired(false);
        httpMethod.setDescription("Enter the HTTP Method used by the SMS API");
        httpMethod.setDisplayOrder(1);
        configProperties.add(httpMethod);

        Property headers = new Property();
        headers.setName(SMSOTPConstants.HEADERS);
        headers.setDisplayName("HTTP Headers");
        headers.setRequired(false);
        headers.setDescription("Enter the headers used by the API separated by comma, with the Header name and value " +
                "separated by \":\". If the phone number and text message are in Headers, specify them as $ctx.num and $ctx.msg");
        headers.setDisplayOrder(2);
        configProperties.add(headers);

        Property payload = new Property();
        payload.setName(SMSOTPConstants.PAYLOAD);
        payload.setDisplayName("HTTP Payload");
        payload.setRequired(false);
        payload.setDescription("Enter the HTTP Payload used by the SMS API. If the phone number and text message are " +
                "in Payload, specify them as $ctx.num and $ctx.msg");
        payload.setDisplayOrder(3);
        configProperties.add(payload);

        Property httpResponse = new Property();
        httpResponse.setName(SMSOTPConstants.HTTP_RESPONSE);
        httpResponse.setDisplayName("HTTP Response Code");
        httpResponse.setRequired(false);
        httpResponse.setDescription("Enter the HTTP response code the API sends upon successful call. Leave empty if unknown");
        httpResponse.setDisplayOrder(4);
        configProperties.add(httpResponse);

        Property showErrorInfo = new Property();
        showErrorInfo.setName(SMSOTPConstants.SHOW_ERROR_INFO);
        showErrorInfo.setDisplayName("Show Detailed Error Information");
        showErrorInfo.setRequired(false);
        showErrorInfo.setDescription("Enter \"true\" if detailed error information from SMS provider needs to be " +
                "displayed in the UI");
        showErrorInfo.setDisplayOrder(5);
        configProperties.add(showErrorInfo);

        Property valuesToBeMasked = new Property();
        valuesToBeMasked.setName(SMSOTPConstants.VALUES_TO_BE_MASKED_IN_ERROR_INFO);
        valuesToBeMasked.setDisplayName("Mask values in Error Info");
        valuesToBeMasked.setRequired(false);
        valuesToBeMasked.setDescription("Enter comma separated Values to be masked by * in the detailed error messages");
        valuesToBeMasked.setDisplayOrder(6);
        configProperties.add(valuesToBeMasked);

        return configProperties;
    }

    /**
     * Get a screen value from the user attributes. If you need to show n digits of mobile number or any other user
     * attribute value in the UI.
     *
     * @param userRealm the user Realm
     * @param username  the username
     * @return the screen attribute
     * @throws UserStoreException
     */
    public String getScreenAttribute(AuthenticationContext context, UserRealm userRealm, String username)
            throws UserStoreException, AuthenticationFailedException {

        String screenUserAttributeParam;
        String screenUserAttributeValue = null;
        String screenValue = null;
        int noOfDigits = 0;

        screenUserAttributeParam = SMSOTPUtils.getScreenUserAttribute(context);
        if (screenUserAttributeParam != null) {
            screenUserAttributeValue = userRealm.getUserStoreManager()
                    .getUserClaimValue(username, screenUserAttributeParam, null);
        }

        if (screenUserAttributeValue != null) {
            if ((SMSOTPUtils.getNoOfDigits(context)) != null) {
                noOfDigits = Integer.parseInt(SMSOTPUtils.getNoOfDigits(context));
            }
            screenValue = getMaskedValue(context, screenUserAttributeValue, noOfDigits);
        }
        return screenValue;
    }

    private String getMaskedValue(AuthenticationContext context, String screenUserAttributeValue, int noOfDigits) {

        String screenValue;
        String hiddenScreenValue;

        int screenAttributeLength = screenUserAttributeValue.length();
        if (SMSOTPConstants.BACKWARD.equals(SMSOTPUtils.getDigitsOrder(context))) {
            screenValue = screenUserAttributeValue.substring(screenAttributeLength - noOfDigits,
                    screenAttributeLength);
            hiddenScreenValue = screenUserAttributeValue.substring(0, screenAttributeLength - noOfDigits);
            for (int i = 0; i < hiddenScreenValue.length(); i++) {
                screenValue = ("*").concat(screenValue);
            }
        } else {
            screenValue = screenUserAttributeValue.substring(0, noOfDigits);
            hiddenScreenValue = screenUserAttributeValue.substring(noOfDigits, screenAttributeLength);
            for (int i = 0; i < hiddenScreenValue.length(); i++) {
                screenValue = screenValue.concat("*");
            }
        }
        return screenValue;
    }

    private String getHttpErrorResponseCode(String errorMsg) {

        String errorCode = errorMsg;
        if (StringUtils.contains(errorCode, ":")) {
            errorCode = errorCode.split(":")[0];
        }
        return StringUtils.trim(errorCode);
    }

    /**
     * Reset SMS OTP Failed Attempts count upon successful completion of the SMS OTP verification.
     *
     * @param context Authentication context.
     * @throws AuthenticationFailedException Exception on authentication failure.
     */
    private void resetSmsOtpFailedAttempts(AuthenticationContext context) throws AuthenticationFailedException {
        
        /*
        Check whether account locking enabled for SMS OTP to keep backward compatibility.
        Account locking is not done for federated flows.
         */
        if (!SMSOTPUtils.isLocalUser(context) || !SMSOTPUtils.isAccountLockingEnabledForSmsOtp(context)) {
            return;
        }
        AuthenticatedUser authenticatedUser =
                (AuthenticatedUser) context.getProperty(SMSOTPConstants.AUTHENTICATED_USER);
        Property[] connectorConfigs = SMSOTPUtils.getAccountLockConnectorConfigs(authenticatedUser.getTenantDomain());

        // Return if account lock handler is not enabled.
        for (Property connectorConfig : connectorConfigs) {
            if ((SMSOTPConstants.PROPERTY_ACCOUNT_LOCK_ON_FAILURE.equals(connectorConfig.getName())) &&
                    !Boolean.parseBoolean(connectorConfig.getValue())) {
                return;
            }
        }

        String usernameWithDomain = IdentityUtil.addDomainToName(authenticatedUser.getUserName(),
                authenticatedUser.getUserStoreDomain());
        try {
            UserRealm userRealm = getUserRealm(authenticatedUser);
            UserStoreManager userStoreManager = userRealm.getUserStoreManager();

            // Avoid updating the claims if they are already zero.
            String[] claimsToCheck = {SMSOTPConstants.SMS_OTP_FAILED_ATTEMPTS_CLAIM,
                    SMSOTPConstants.FAILED_LOGIN_LOCKOUT_COUNT_CLAIM};
            Map<String, String> userClaims = userStoreManager.getUserClaimValues(usernameWithDomain, claimsToCheck,
                    UserCoreConstants.DEFAULT_PROFILE);
            String failedSmsOtpAttempts = userClaims.get(SMSOTPConstants.SMS_OTP_FAILED_ATTEMPTS_CLAIM);
            String failedLoginLockoutCount = userClaims.get(SMSOTPConstants.FAILED_LOGIN_LOCKOUT_COUNT_CLAIM);

            if (NumberUtils.isNumber(failedSmsOtpAttempts) && Integer.parseInt(failedSmsOtpAttempts) > 0 ||
                    NumberUtils.isNumber(failedLoginLockoutCount) && Integer.parseInt(failedLoginLockoutCount) > 0) {
                Map<String, String> updatedClaims = new HashMap<>();
                updatedClaims.put(SMSOTPConstants.SMS_OTP_FAILED_ATTEMPTS_CLAIM, "0");
                updatedClaims.put(SMSOTPConstants.FAILED_LOGIN_LOCKOUT_COUNT_CLAIM, "0");
                userStoreManager
                        .setUserClaimValues(usernameWithDomain, updatedClaims, UserCoreConstants.DEFAULT_PROFILE);
            }
        } catch (UserStoreException e) {
            log.error("Error while resetting failed SMS OTP attempts", e);
            String errorMessage =
                    String.format("Failed to reset failed attempts count for user : %s.", authenticatedUser);
            throw new AuthenticationFailedException(errorMessage, e);
        }
    }

    /**
     * Execute account lock flow for OTP verification failures.
     *
     * @param context Authentication context.
     * @throws AuthenticationFailedException Exception on authentication failure.
     */
    private void handleSmsOtpVerificationFail(AuthenticationContext context) throws AuthenticationFailedException {

        AuthenticatedUser authenticatedUser =
                (AuthenticatedUser) context.getProperty(SMSOTPConstants.AUTHENTICATED_USER);

        /*
        Account locking is not done for federated flows.
        Check whether account locking enabled for SMS OTP to keep backward compatibility.
        No need to continue if the account is already locked.
         */
        if (!SMSOTPUtils.isLocalUser(context) || !SMSOTPUtils.isAccountLockingEnabledForSmsOtp(context) ||
                SMSOTPUtils.isAccountLocked(authenticatedUser)) {
            return;
        }
        int maxAttempts = 0;
        long unlockTimePropertyValue = 0;
        double unlockTimeRatio = 1;

        Property[] connectorConfigs = SMSOTPUtils.getAccountLockConnectorConfigs(authenticatedUser.getTenantDomain());
        for (Property connectorConfig : connectorConfigs) {
            switch (connectorConfig.getName()) {
                case SMSOTPConstants.PROPERTY_ACCOUNT_LOCK_ON_FAILURE:
                    if (!Boolean.parseBoolean(connectorConfig.getValue())) {
                        return;
                    }
                case SMSOTPConstants.PROPERTY_ACCOUNT_LOCK_ON_FAILURE_MAX:
                    if (NumberUtils.isNumber(connectorConfig.getValue())) {
                        maxAttempts = Integer.parseInt(connectorConfig.getValue());
                    }
                    break;
                case SMSOTPConstants.PROPERTY_ACCOUNT_LOCK_TIME:
                    if (NumberUtils.isNumber(connectorConfig.getValue())) {
                        unlockTimePropertyValue = Integer.parseInt(connectorConfig.getValue());
                    }
                    break;
                case SMSOTPConstants.PROPERTY_LOGIN_FAIL_TIMEOUT_RATIO:
                    if (NumberUtils.isNumber(connectorConfig.getValue())) {
                        double value = Double.parseDouble(connectorConfig.getValue());
                        if (value > 0) {
                            unlockTimeRatio = value;
                        }
                    }
                    break;
            }
        }
        Map<String, String> claimValues = getUserClaimValues(authenticatedUser);
        if (claimValues == null) {
            claimValues = new HashMap<>();
        }
        int currentAttempts = 0;
        if (NumberUtils.isNumber(claimValues.get(SMSOTPConstants.SMS_OTP_FAILED_ATTEMPTS_CLAIM))) {
            currentAttempts = Integer.parseInt(claimValues.get(SMSOTPConstants.SMS_OTP_FAILED_ATTEMPTS_CLAIM));
        }
        int failedLoginLockoutCountValue = 0;
        if (NumberUtils.isNumber(claimValues.get(SMSOTPConstants.FAILED_LOGIN_LOCKOUT_COUNT_CLAIM))) {
            failedLoginLockoutCountValue =
                    Integer.parseInt(claimValues.get(SMSOTPConstants.FAILED_LOGIN_LOCKOUT_COUNT_CLAIM));
        }

        Map<String, String> updatedClaims = new HashMap<>();
        if ((currentAttempts + 1) >= maxAttempts) {
            // Calculate the incremental unlock-time-interval in milli seconds.
            unlockTimePropertyValue = (long) (unlockTimePropertyValue * 1000 * 60 * Math.pow(unlockTimeRatio,
                    failedLoginLockoutCountValue));
            // Calculate unlock-time by adding current-time and unlock-time-interval in milli seconds.
            long unlockTime = System.currentTimeMillis() + unlockTimePropertyValue;
            updatedClaims.put(SMSOTPConstants.ACCOUNT_LOCKED_CLAIM, Boolean.TRUE.toString());
            updatedClaims.put(SMSOTPConstants.SMS_OTP_FAILED_ATTEMPTS_CLAIM, "0");
            updatedClaims.put(SMSOTPConstants.ACCOUNT_UNLOCK_TIME_CLAIM, String.valueOf(unlockTime));
            updatedClaims.put(SMSOTPConstants.FAILED_LOGIN_LOCKOUT_COUNT_CLAIM,
                    String.valueOf(failedLoginLockoutCountValue + 1));
            IdentityUtil.threadLocalProperties.get().put(SMSOTPConstants.ADMIN_INITIATED, false);
            setUserClaimValues(authenticatedUser, updatedClaims);
            String errorMessage = String.format("User account: %s is locked.", authenticatedUser.getUserName());
            throw new AuthenticationFailedException(errorMessage);
        } else {
            updatedClaims.put(SMSOTPConstants.SMS_OTP_FAILED_ATTEMPTS_CLAIM, String.valueOf(currentAttempts + 1));
            setUserClaimValues(authenticatedUser, updatedClaims);
        }
    }

    private Map<String, String> getUserClaimValues(AuthenticatedUser authenticatedUser)
            throws AuthenticationFailedException {

        Map<String, String> claimValues;
        try {
            UserRealm userRealm = getUserRealm(authenticatedUser);
            UserStoreManager userStoreManager = userRealm.getUserStoreManager();
            claimValues = userStoreManager.getUserClaimValues(IdentityUtil.addDomainToName(
                    authenticatedUser.getUserName(), authenticatedUser.getUserStoreDomain()), new String[]{
                            SMSOTPConstants.SMS_OTP_FAILED_ATTEMPTS_CLAIM,
                            SMSOTPConstants.FAILED_LOGIN_LOCKOUT_COUNT_CLAIM},
                    UserCoreConstants.DEFAULT_PROFILE);
        } catch (UserStoreException e) {
            log.error("Error while reading user claims", e);
            String errorMessage = String.format("Failed to read user claims for user : %s.", authenticatedUser);
            throw new AuthenticationFailedException(errorMessage, e);
        }
        return claimValues;
    }

    private void setUserClaimValues(AuthenticatedUser authenticatedUser, Map<String, String> updatedClaims)
            throws AuthenticationFailedException {

        try {
            UserRealm userRealm = getUserRealm(authenticatedUser);
            UserStoreManager userStoreManager = userRealm.getUserStoreManager();
            userStoreManager.setUserClaimValues(IdentityUtil.addDomainToName(authenticatedUser.getUserName(),
                    authenticatedUser.getUserStoreDomain()), updatedClaims, UserCoreConstants.DEFAULT_PROFILE);
        } catch (UserStoreException e) {
            log.error("Error while updating user claims", e);
            String errorMessage = String.format("Failed to update user claims for user : %s.", authenticatedUser);
            throw new AuthenticationFailedException(errorMessage, e);
        }
    }

    /**
     * Get user account unlock time in milli seconds. If no value configured for unlock time user claim, return 0.
     *
     * @param authenticatedUser The authenticated user.
     * @return User account unlock time in milli seconds. If no value is configured return 0.
     * @throws AuthenticationFailedException If an error occurred while getting the user unlock time.
     */
    private long getUnlockTimeInMilliSeconds(AuthenticatedUser authenticatedUser) throws AuthenticationFailedException {

        String username = authenticatedUser.toFullQualifiedUsername();
        String tenantAwareUsername = MultitenantUtils.getTenantAwareUsername(username);
        try {
            UserRealm userRealm = getUserRealm(username);
            if (userRealm == null) {
                throw new AuthenticationFailedException("UserRealm is null for user : " + username);
            }
            UserStoreManager userStoreManager = userRealm.getUserStoreManager();
            if (userStoreManager == null) {
                if (log.isDebugEnabled()) {
                    log.debug("userStoreManager is null for user: " + username);
                }
                throw new AuthenticationFailedException("userStoreManager is null for user: " + username);
            }
            Map<String, String> claimValues = userStoreManager
                    .getUserClaimValues(tenantAwareUsername, new String[]{SMSOTPConstants.ACCOUNT_UNLOCK_TIME_CLAIM},
                            null);
            if (claimValues.get(SMSOTPConstants.ACCOUNT_UNLOCK_TIME_CLAIM) == null) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("No value configured for claim: %s, of user: %s",
                            SMSOTPConstants.ACCOUNT_UNLOCK_TIME_CLAIM, username));
                }
                return 0;
            }
            return Long.parseLong(claimValues.get(SMSOTPConstants.ACCOUNT_UNLOCK_TIME_CLAIM));
        } catch (UserStoreException e) {
            throw new AuthenticationFailedException("Cannot find the user claim for unlock time for user : " +
                    username, e);
        }
    }
}
