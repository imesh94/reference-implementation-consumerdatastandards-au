/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.openbanking.scp.webapp.servlet;

import com.wso2.openbanking.accelerator.common.util.Generated;
import com.wso2.openbanking.scp.webapp.model.SCPError;
import com.wso2.openbanking.scp.webapp.service.OAuthService;
import com.wso2.openbanking.scp.webapp.util.Constants;
import com.wso2.openbanking.scp.webapp.util.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The OAuthAuthorizationServlet is responsible for handling oauth2 authorization flow and token flow for
 * Self Care Portal app.
 */
@WebServlet(name = "OAuthAuthorizationServlet", urlPatterns = "/scp_oauth2_authorize")
public class OAuthAuthorizationServlet extends HttpServlet {

    private static final long serialVersionUID = 6935866958152624870L;
    private static final Log LOG = LogFactory.getLog(OAuthAuthorizationServlet.class);

    @Generated(message = "Ignoring since all cases are covered from other unit tests")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        LOG.debug("Authorization request received");

        try {
            final String iamBaseUrl = getServletContext().getInitParameter(Constants.SERVLET_CONTEXT_IAM_BASE_URL);
            final String clientKey = getServletContext().getInitParameter(Constants.SERVLET_CONTEXT_CLIENT_KEY);

            final String authUrl = OAuthService.getInstance().generateAuthorizationUrl(iamBaseUrl, clientKey);

            LOG.debug("Redirecting to: " + authUrl);
            resp.sendRedirect(authUrl);
        } catch (URISyntaxException | IOException e) {
            LOG.error("Exception occurred while redirecting to authorization url. caused by,", e);
            // sending error to frontend
            SCPError error = new SCPError("Authentication Failed!",
                    "Something went wrong during the authentication process. Please try signing in again.");
            final String iamBaseUrl = getServletContext()
                    .getInitParameter(Constants.SERVLET_CONTEXT_IAM_BASE_URL);
            final String errorUrlFormat = iamBaseUrl + "/consentmgr/error?message=%s&description=%s";
            Utils.sendErrorToFrontend(error, errorUrlFormat, resp);
        }
    }
}
