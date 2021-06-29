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

package com.wso2.openbanking.test.framework.util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class to contain common constants used for Test Framework.
 */
public class TestConstants {
	private static final ConfigParser config = ConfigParser.getInstance();

	public static final String CLIENT_CREDENTIALS = "client_credentials";
	public static final String AUTH_CODE = "authorization_code";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String PASSWORD_GRANT = "password";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String TOKEN = "token";
	public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
	public static final String AUTH_RESPONSE_TYPE = "code id_token";
	public static final String FIREFOX_DRIVER_NAME = "webdriver.gecko.driver";
	public static final String CLIENT_ASSERTION_TYPE =
					"urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
	public static final String X_FAPI_FINANCIAL_ID_KEY = "x-fapi-financial-id";
	public static final String X_WSO2_CLIENT_ID_KEY = "x-wso2-client-id";
	public static final String ACCESS_TOKEN_CONTENT_TYPE = "application/x-www-form-urlencoded";
	public static final String SCA_CLAIM = "urn:openbanking:psd2:sca";
	public static final String CA_CLAIM = "urn:openbanking:psd2:ca";
	public static final String TIME_ZONE = "Asia/Colombo";
	public static final ArrayList<String> ACCOUNTS_DEFAULT_SCOPES =
					new ArrayList<>(Arrays.asList("accounts", "openid"));
	public static final String AUTHORIZATION_HEADER_KEY = "Authorization";
	public static final String AUTHORIZATION_BEARER_TAG = "Bearer ";
	public static final String AUTHORIZATION_BASIC_TAG = "Basic";
	public static final String X_FAPI_CUSTOMER_LAST_LOGGED_TIME = "x-fapi-customer-last-logged-time";
	public static final String X_FAPI_CUSTOMER_IP_ADDRESS = "x-fapi-customer-ip-address";
	public static final String X_FAPI_INTERACTION_ID = "x-fapi-interaction-id";
	public static final String MTLS_CERTIFICATE_HEADER = "x-wso2-mutual-auth-cert";
	public static final String CONTENT_TYPE_APPLICATION_JWT = "application/jwt";

	//Invalid keystore details
	public static final String PATH_TO_INVALID_KEYSTORE = "./../../../../../test-artifacts/"
					+ "tpp3-invalid-info/certs/signing/tpp3-invalid-signing.jks";
	public static final String INVALID_KEYSTORE_PASSWORD = "wso2carbon";
	public static final String INVALID_KEYSTORE_ALIAS = "tpp3-invalid";

	//Endpoints
	public static final String TOKEN_ENDPOINT = "/token";
	public static final String AUTHORIZE_ENDPOINT = "/authorize/?";
	public static final String INTROSPECTION_ENDPOINT = "/oauth2/introspect";

	//JWT Claim keys
	public static final String ISSUER_KEY = "iss";
	public static final String SUBJECT_KEY = "sub";
	public static final String AUDIENCE_KEY = "aud";
	public static final String EXPIRE_DATE_KEY = "exp";
	public static final String ISSUED_AT_KEY = "iat";
	public static final String JTI_KEY = "jti";
	public static final String CLIENT_SECRET = "client_secret";
	public static final String INTEGRATION_DATE = "integrationDate";

	//payload constants
	public static final String CODE_VERIFIER_KEY = "code_verifier";
	public static final String GRANT_TYPE_KEY = "grant_type";
	public static final String SCOPE_KEY = "scope";
	public static final String CLIENT_ASSERTION_TYPE_KEY = "client_assertion_type";
	public static final String CLIENT_ASSERTION_KEY = "client_assertion";
	public static final String REDIRECT_URI_KEY = "redirect_uri";
	public static final String CODE_KEY = "code";
	public static final String CLIENT_ID = "client_id";
	public static final String USER_NAME = "username";
	public static final String PASSWORD = "password";
	public static final String TLS_AUTH_METHOD = "tls_client_auth";

	//Selenium constants
	public static final String USERNAME_FIELD_ID = "usernameUserInput";
	public static final String USERNAME_FIELD_XPATH_AU_200 = "//form[@id=\"identifierForm\"]/div//input[@id" +
					"=\"usernameUserInput\"]";
	public static final String PASSWORD_FIELD_ID = "password";
	public static final String HEADLESS_TAG = "--headless";
	public static final String ACCOUNT_SELECT_DROPDOWN_XPATH = "//*[@id=\"accselect\"]";
	public static final String AUTH_SIGNIN_XPATH = "//button[contains(text(),'Sign In')]";
	public static final String AUTH_SIGNIN_XPATH_AU_200 = "//input[@value=\"Next\"]";
	public static final String CONSENT_DENY_XPATH = "//input[@value='Deny']";
	public static final String CONSENT_APPROVE_SUBMIT_ID = "approve";
	public static final String IS_USERNAME_ID = "txtUserName";
	public static final String IS_PASSWORD_ID = "txtPassword";
	public static final String BTN_IS_SIGNING = "//input[@value='Sign-in']";
	public static final String BTN_DEVPORTAL_SIGNIN = "//span[contains(text(),'Sign-in')]";
	public static final String BTN_CONTINUE = "//button[contains(text(),'Continue')]";
	public static final String TAB_APPLICATIONS = "//span[contains(text(),'Applications')]";
	public static final String TBL_ROWS = "//tbody/tr";
	public static final String TAB_SUBSCRIPTIONS = "//p[text()='Subscriptions']";

	public static final String X_REQUEST_ID = "X-Request-ID";
	public static final String PSU_IP_ADDRESS = "PSU-IP-Address";
	public static final String DATE = "DATE";
	public static final String PSU_ID = "PSU-ID";
	public static final String PSU_TYPE = "PSU-ID-Type";
	public static final String EXPLICIT_AUTH_PREFFERED = "TPP-Explicit-Authorisation-Preferred";
	public static final String TPP_SIGNATURE_CERTIFICATE = "TPP-Signature-Certificate";
	public static final String DIGEST = "Digest";
	public static final String SIGNATURE = "Signature";
	public static final String PSU_CORPORATE_ID_HEADER = "PSU-Corporate-ID";
	public static final String TPP_REDIRECT_URI_HEADER = "TPP-Redirect-URI";
	public static final String API_CONTEXT = "xs2a";
	public static final String LOGIN_LINK = "//*[@id=\"btn-login\"]";
	public static final String SP_MENU_NAME =
					"/html/body/table/tbody/tr[2]/td[2]/table/tbody/tr[1]/td/div/ul/li[3]/ul/li[8]/ul/li[2]/a";
	public static final String SP_EDIT_XPATH =
					"/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]/td/div/div/table[2]/tbody/tr/td/table/tbody/tr[1]/td[3]/a[1]";
	public static final String INBOUND_MENU = "//*[@id=\"app_authentication_head\"]";
	public static final String DROPDOWN_KEY = "//*[@id=\"oauth.config.head\"]";
	public static final String EDIT_LINK =
					"/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]/td/div/div[1]/form/div[5]/div[2]/table/tbody/tr/td/table/tbody/tr/td[3]/a[1]";
	public static final String SELECT_IMPLICIT_GRANT_XPATH = "//*[@id=\"grant_implicit\"]";
	public static final String SELECT_CLIENT_CREDENTIALS_GRANT =
					"//*[@id=\"grant_client_credentials\"]";
	public static final String SELECT_AUTH_CODE_GRANT = "//*[@id=\"grant_authorization_code\"]";
	public static final String SELECT_REFRESH_TOKEN_GRANT =
					"//*[@id=\"grant_refresh_token\"]";
	public static final String SELECT_SAML2_GRANT =
					"//*[@id=\"grant_urn:ietf:params:oauth:grant-type:saml2-bearer\"]";
	public static final String UPDATE_BUTTON_XPATH =
					"/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]/td/div/div/form/table/tbody/tr[2]/td/input[1]";
	public static final String LOGIN_BUTTON =
					"/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]/td/div/table/tbody/tr/td[2]/div/form/table/tbody/tr[4]/td[2]/input";
	public static final String USERNAME_XPATH = "//*[@id=\"txtUserName\"]";
	public static final String PASSWORD_XPATH = "//*[@id=\"txtPassword\"]";
	public static final int TIMEOUT = 10000;
	public static final String WINDOWS_SCROLL_100 = "window.scrollBy(0, 1000)";
	public static final String WINDOWS_SCROLL_20 = "window.scrollBy(0, 200)";
	public static final String LBL_OTP_TIMEOUT = "//div[@id='otpTimeout']";
	public static final String ELE_CONSENT_PAGE = "//form[@id='oauth2_authz_consent']";
	public static final String LBL_AUTHENTICATION_FAILURE = "//div[contains(text(),'Authentication Error')]/../p";
	public static final String LBL_FOOTER_DESCRIPTION = "//div[@class='ui segment']/div/form/div/div";

	//Non-Regulatory
	public static final ArrayList<String> SCOPES_OPEN_ID = new ArrayList<>(Arrays.asList("openid"));
	public static final String CLIENTID_NON_REGULATORY_APP = config.getNonRegulatoryClientId();
	public static final String CCPORTAL_SIGNIN_XPATH = "//button[contains(text(),'Sign in')]";

	//Second Factor Authenticator
	public static final String OTP_CODE = "123456";
	public static final String LBL_SMSOTP_AUTHENTICATOR = "//h2[text()='Authenticating with SMSOTP']";
	public static final String TXT_OTP_CODE = "OTPcode";
	public static final String BTN_AUTHENTICATE = "//input[@id='authenticate']";

	public static final String SOLUTION_VERSION_200 = "2.0.0";

}
