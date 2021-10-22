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

import com.wso2.openbanking.cds.identity.authenticator.util.CDSJWTValidator;
import com.wso2.openbanking.cds.identity.authenticator.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth2.token.handler.clientauth.jwt.PrivateKeyJWTClientAuthenticator;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * CDS Customized Client Authentication handler to implement oidc private_key_jwt client authentication
 * Supports validating multiple audience claim values according to the CDS Specification
 * http://openid.net/specs/openid-connect-core-1_0.html#ClientAuthentication.
 */
public class CDSBasePrivateKeyJWTClientAuthenticator extends PrivateKeyJWTClientAuthenticator {

    private static final Log LOG = LogFactory.getLog(CDSBasePrivateKeyJWTClientAuthenticator.class);
    private CDSJWTValidator jwtValidator;

    public CDSBasePrivateKeyJWTClientAuthenticator() {

        int rejectBeforePeriod = Constants.DEFAULT_VALIDITY_PERIOD_IN_MINUTES;
        boolean preventTokenReuse = true;
        String endpointAlias = Constants.DEFAULT_AUDIENCE;
        try {
            if (isNotEmpty(properties.getProperty(Constants.ENDPOINT_ALIAS))) {
                endpointAlias = properties.getProperty(Constants.ENDPOINT_ALIAS);
            }
            if (isNotEmpty(properties.getProperty(Constants.PREVENT_TOKEN_REUSE))) {
                preventTokenReuse = Boolean.parseBoolean(properties.getProperty(Constants.PREVENT_TOKEN_REUSE));
            }
            if (isNotEmpty(properties.getProperty(Constants.REJECT_BEFORE_IN_MINUTES))) {
                rejectBeforePeriod = Integer.parseInt(properties.getProperty(Constants.REJECT_BEFORE_IN_MINUTES));
            }
            jwtValidator = createJWTValidator(endpointAlias, preventTokenReuse, rejectBeforePeriod);
        } catch (NumberFormatException e) {
            LOG.warn("Invalid PrivateKeyJWT Validity period found in the configuration. Using default value: " +
                    rejectBeforePeriod);
        }
    }

    private CDSJWTValidator createJWTValidator(String accessedEndpoint, boolean preventTokenReuse, int rejectBefore) {

        // Adding accepted audience value as per CDS Specification
        String tokenEndpoint = OAuth2Util.OAuthURL.getOAuth2TokenEPUrl();
        String issuer = OAuth2Util.getIDTokenIssuer();

        List<String> acceptedAudienceList = new ArrayList<>();
        acceptedAudienceList.add(accessedEndpoint);
        acceptedAudienceList.add(tokenEndpoint);
        acceptedAudienceList.add(issuer);

        return new CDSJWTValidator(preventTokenReuse, acceptedAudienceList, rejectBefore, null,
                populateMandatoryClaims(), Constants.DEFAULT_ENABLE_JTI_CACHE);
    }

    private List<String> populateMandatoryClaims() {

        List<String> mandatoryClaims = new ArrayList<>();
        mandatoryClaims.add(Constants.ISSUER_CLAIM);
        mandatoryClaims.add(Constants.SUBJECT_CLAIM);
        mandatoryClaims.add(Constants.AUDIENCE_CLAIM);
        mandatoryClaims.add(Constants.EXPIRATION_TIME_CLAIM);
        mandatoryClaims.add(Constants.JWT_ID_CLAIM);
        return mandatoryClaims;
    }
}
