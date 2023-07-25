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
package com.wso2.openbanking.cds.identity.authenticator;

import com.wso2.openbanking.cds.identity.authenticator.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth2.bean.OAuthClientAuthnContext;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * CDSPARPrivateKeyJWTClientAuthenticator for authenticating CDS par requests.
 */
public class CDSPARPrivateKeyJWTClientAuthenticator extends CDSBasePrivateKeyJWTClientAuthenticator {

    private static final Log log = LogFactory.getLog(CDSPARPrivateKeyJWTClientAuthenticator.class);

    @Override
    public boolean canAuthenticate(HttpServletRequest httpServletRequest, Map<String, List> bodyParameters,
                                   OAuthClientAuthnContext oAuthClientAuthnContext) {

        if (httpServletRequest.getRequestURI().contains(Constants.PAR)) {
            log.debug("Request can be handled by CDSPARPrivateKeyJWTClientAuthenticator");
            return canSuperAuthenticate(httpServletRequest, bodyParameters, oAuthClientAuthnContext);
        }
        log.debug("CDSPARPrivateKeyJWTClientAuthenticator cannot handle the request.");

        return false;
    }

    /**
     * Check if base private key jwt authenticator can authenticate the client.
     *
     * @param httpServletRequest
     * @param bodyParameters
     * @param oAuthClientAuthnContext
     * @return boolean
     */
    private boolean canSuperAuthenticate(HttpServletRequest httpServletRequest, Map<String, List> bodyParameters,
                                         OAuthClientAuthnContext oAuthClientAuthnContext) {
        return super.canAuthenticate(httpServletRequest, bodyParameters, oAuthClientAuthnContext);
    }

}
