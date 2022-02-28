/*
 *  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 *  This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material contained
 *  herein is strictly forbidden, unless permitted by WSO2 in accordance with
 *  the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 *  language governing the permissions and limitations under this license,
 *  please see the license as well as any agreement you’ve entered into with
 *  WSO2 governing the purchase of this software and any associated services.
 *
 */

package com.wso2.openbanking.scp.webapp.service;

import com.wso2.openbanking.scp.webapp.exception.TokenGenerationException;
import com.wso2.openbanking.scp.webapp.util.Constants;
import com.wso2.openbanking.scp.webapp.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONObject;
import org.wso2.carbon.databridge.commons.exception.SessionTimeoutException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * APIMService
 * <p>
 * Contains methods to process requests that are forwarding to APIM
 */
public class APIMService implements Serializable {
    private static final long serialVersionUID = -1968486857447834419L;
    private static final Log LOG = LogFactory.getLog(APIMService.class);

    public Optional<String> constructAccessTokenFromCookies(HttpServletRequest req) {
        Optional<Cookie> cookieAccessTokenPart2 = Utils
                .getCookieFromRequest(req, Constants.ACCESS_TOKEN_COOKIE_NAME + "_P2");

        if (cookieAccessTokenPart2.isPresent()) {
            // access token part 2 is present as a cookie
            final String accessTokenPart1 = req.getHeader(HttpHeaders.AUTHORIZATION);


            if (StringUtils.isNotEmpty(accessTokenPart1)) {
                // access token part 1 is present in the request
                String reqAccessTokenPart1 = accessTokenPart1.replace("Bearer ", "");

                Optional<Cookie> cookieAccessTokenPart1 = Utils
                        .getCookieFromRequest(req, Constants.ACCESS_TOKEN_COOKIE_NAME + "_P1");

                if (cookieAccessTokenPart1.isPresent()
                        && cookieAccessTokenPart1.get().getValue().equals(reqAccessTokenPart1)) {
                    // access token part 1 is present in the cookie and its equal to request access token part 1
                    return Optional.of(reqAccessTokenPart1 + cookieAccessTokenPart2.get().getValue());
                }
            }
        }
        return Optional.empty();
    }

    public Optional<String> constructRefreshTokenFromCookies(HttpServletRequest req) {
        Optional<Cookie> refreshTokenPart1 = Utils
                .getCookieFromRequest(req, Constants.REFRESH_TOKEN_COOKIE_NAME + "_P1");

        Optional<Cookie> refreshTokenPart2 = Utils
                .getCookieFromRequest(req, Constants.REFRESH_TOKEN_COOKIE_NAME + "_P2");

        if (refreshTokenPart1.isPresent() && refreshTokenPart2.isPresent()) {
            return Optional.of(refreshTokenPart1.get().getValue() + refreshTokenPart2.get().getValue());
        }
        return Optional.empty();
    }

    public void forwardRequest(HttpServletResponse resp, HttpUriRequest httpRequest, Map<String, String> headers)
            throws TokenGenerationException {
        // adding headers to request
        headers.forEach(httpRequest::addHeader);

        JSONObject responseJson = Utils.sendRequest(httpRequest);
        int statusCode = responseJson.optInt("res_status_code", 200);
        responseJson.remove("res_status_code");

        // returning  response
        Utils.returnResponse(resp, statusCode, responseJson);

    }

    public boolean isAccessTokenExpired(HttpServletRequest req) throws SessionTimeoutException {
        Optional<Cookie> optValidityCookie = Utils
                .getCookieFromRequest(req, Constants.TOKEN_VALIDITY_COOKIE_NAME);
        try {
            if (optValidityCookie.isPresent()) {
                Cookie validityCookie = optValidityCookie.get();
                LocalDateTime tokenExpiry = Utils.parseEncodedStringToDate(validityCookie.getValue());

                return LocalDateTime.now().isAfter(tokenExpiry);
            }
            // cookie not found in the request
            LOG.debug(String.format("Invalid request received. %s cookie is missing",
                    Constants.TOKEN_VALIDITY_COOKIE_NAME));
        } catch (IllegalArgumentException e) {
            LOG.error(String.format("Invalid request received. %s cookie is invalid",
                    Constants.TOKEN_VALIDITY_COOKIE_NAME));
        }
        throw new SessionTimeoutException(String.format
                ("Invalid request received. %s cookie is missing", Constants.TOKEN_VALIDITY_COOKIE_NAME));
    }
}
