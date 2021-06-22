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

package com.wso2.openbanking.test.framework.model;

import com.wso2.openbanking.test.framework.exception.TestFrameworkException;
import com.wso2.openbanking.test.framework.util.ConfigParser;
import com.wso2.openbanking.test.framework.util.TestConstants;
import com.wso2.openbanking.test.framework.util.TestUtil;

import java.util.ArrayList;
import java.util.List;

import static com.wso2.openbanking.test.framework.util.TestConstants.CLIENT_ASSERTION_TYPE;
import static com.wso2.openbanking.test.framework.util.TestConstants.CLIENT_ASSERTION_TYPE_KEY;

/**
 * Model class for Refresh Token Grant Access Token Request.
 */
public class RefreshTokenDto {

	private String grantType;
	private String refreshToken;
	private List<String> scopes;
	private String clientAssertionType;
	private String clientAssertion;
	private String redirectUrl;
	private String codeVerifier;
	private AccessTokenJwtDto accessTokenJwtDto;
	private String clientId;

	public AccessTokenJwtDto getAccessTokenJwtDto() {

		return accessTokenJwtDto;
	}

	public void setAccessTokenJwtDto(AccessTokenJwtDto accessTokenJwtDto) {

		this.accessTokenJwtDto = accessTokenJwtDto;
	}

	public String getGrantType() {

		return grantType;
	}

	public void setGrantType(String grantType) {

		this.grantType = grantType;
	}

	public String getRefreshToken() {

		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {

		this.refreshToken = refreshToken;
	}

	public List<String> getScopes() {

		return scopes;
	}

	public void setScopes(List<String> scopes) {

		this.scopes = scopes;
	}

	public String getClientAssertionType() {

		return clientAssertionType;
	}

	public void setClientAssertionType(String clientAssertionType) {

		this.clientAssertionType = clientAssertionType;
	}

	public String getClientAssertion() {

		return clientAssertion;
	}

	public void setClientAssertion(String clientAssertion) {

		this.clientAssertion = clientAssertion;
	}

	public String getRedirectUrl() {

		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {

		this.redirectUrl = redirectUrl;
	}

	public String getClientId() {

		return clientId;
	}

	public void setClientId(String clientId) {

		this.clientId = clientId;
	}

	/**
	 * Method to generate Refresh Token Grant Access token Payload based on Auth Method.
	 *
	 * @param authMethodType authMethodType
	 * @return payload
	 * @throws TestFrameworkException exception
	 */
	@SuppressWarnings("checkstyle:LineLength")
	public String getPayload(String authMethodType) throws TestFrameworkException {

		if (grantType == null) {
			setGrantType(TestConstants.REFRESH_TOKEN);
		}

		if (redirectUrl == null) {
			setRedirectUrl(ConfigParser.getInstance().getRedirectUrl());
		}

		if (scopes == null) {
			List<String> scopes = new ArrayList<>();
			scopes.add("accounts");
			setScopes(scopes);
		}

		if (refreshToken == null) {
			setRefreshToken("");
		}

		if (clientId == null) {
			setClientId(ConfigParser.getInstance().getClientId());
		}

		String payload = "";
		String delimiter = "&";

		if (authMethodType == TestConstants.TLS_AUTH_METHOD) {
			payload = payload.concat(TestConstants.GRANT_TYPE_KEY + "=" + getGrantType() + delimiter)
							.concat(TestConstants.REFRESH_TOKEN + "=" + getRefreshToken() + delimiter)
							.concat(TestConstants.SCOPE_KEY + "="
											+ TestUtil.getParamListAsString(getScopes(), ' ') + delimiter)
							.concat(TestConstants.REDIRECT_URI_KEY + "=" + getRedirectUrl() + delimiter)
							.concat(TestConstants.CLIENT_ID + "=" + getClientId());
		} else {

			if (clientAssertionType == null) {
				setClientAssertionType(CLIENT_ASSERTION_TYPE);
			}

			if (clientAssertion == null) {
				if (getAccessTokenJwtDto() == null) {
					setAccessTokenJwtDto(new AccessTokenJwtDto());
				}
				setClientAssertion(accessTokenJwtDto.getJwt());
			}

			payload = payload.concat(TestConstants.GRANT_TYPE_KEY + "=" + getGrantType() + delimiter)
							.concat(TestConstants.REFRESH_TOKEN + "=" + getRefreshToken() + delimiter)
							.concat(TestConstants.SCOPE_KEY + "="
											+ TestUtil.getParamListAsString(getScopes(), ' ') + delimiter)
							.concat(CLIENT_ASSERTION_TYPE_KEY + "=" + getClientAssertionType() + delimiter)
							.concat(TestConstants.CLIENT_ASSERTION_KEY + "=" + getClientAssertion() + delimiter)
							.concat(TestConstants.REDIRECT_URI_KEY + "=" + getRedirectUrl() + delimiter)
							.concat(TestConstants.CLIENT_ID + "=" + getClientId());
		}

		return payload;
	}

	/**
	 * Method to generate Refresh Token Grant Access token Payload for Non Regulatory Client.
	 *
	 * @return payload
	 */
	public String getPayloadForNonRegulatoryClient(){

		if (grantType == null) {
			setGrantType(TestConstants.REFRESH_TOKEN);
		}

		if (redirectUrl == null) {
			setRedirectUrl(ConfigParser.getInstance().getRedirectUrl());
		}

		if (scopes == null) {
			List<String> scopes = new ArrayList<>();
			scopes.add("accounts");
			setScopes(scopes);
		}

		if (refreshToken == null) {
			setRefreshToken("");
		}

		if (clientId == null) {
			setClientId(ConfigParser.getInstance().getClientId());
		}

		String payload = "";
		String delimiter = "&";

		payload = payload.concat(TestConstants.GRANT_TYPE_KEY + "=" + getGrantType() + delimiter)
						.concat(TestConstants.REFRESH_TOKEN + "=" + getRefreshToken() + delimiter)
						.concat(TestConstants.SCOPE_KEY + "="
										+ TestUtil.getParamListAsString(getScopes(), ' ') + delimiter)
						.concat(TestConstants.REDIRECT_URI_KEY + "=" + getRedirectUrl() + delimiter)
						.concat(TestConstants.CLIENT_ID + "=" + getClientId());

		return payload;
	}
}
