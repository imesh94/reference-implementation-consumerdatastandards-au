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
package com.wso2.openbanking.cds.identity.dcr.model;

import com.wso2.openbanking.accelerator.identity.dcr.model.RegistrationRequest;
import com.wso2.openbanking.accelerator.identity.dcr.model.SoftwareStatementBody;
import com.wso2.openbanking.accelerator.identity.dcr.validation.DCRCommonConstants;
import com.wso2.openbanking.accelerator.identity.dcr.validation.validationgroups.AttributeChecks;
import com.wso2.openbanking.accelerator.identity.dcr.validation.validationgroups.MandatoryChecks;
import com.wso2.openbanking.cds.identity.dcr.constants.CDSValidationConstants;
import com.wso2.openbanking.cds.identity.dcr.validation.annotation.ValidateCallbackUris;
import com.wso2.openbanking.cds.identity.dcr.validation.annotation.ValidateJTI;
import com.wso2.openbanking.cds.identity.dcr.validation.annotation.ValidateRedirectUriFormat;
import com.wso2.openbanking.cds.identity.dcr.validation.annotation.ValidateUriConnection;
import com.wso2.openbanking.cds.identity.dcr.validation.annotation.ValidateUriHostnames;

import java.util.List;

/**
 * Model class for CDS dcr registration request
 */
@ValidateCallbackUris(registrationRequestProperty = "registrationRequest", callbackUrisProperty = "redirectUris",
        ssa = "softwareStatement", message = "Invalid callback uris:" + DCRCommonConstants.INVALID_META_DATA,
        groups = AttributeChecks.class)
@ValidateRedirectUriFormat(registrationRequestProperty = "registrationRequest", ssa = "softwareStatement",
        message = "Invalid redirect_uris found in the SSA:" + CDSValidationConstants.INVALID_REDIRECT_URI,
        groups = MandatoryChecks.class)
@ValidateUriHostnames(registrationRequestProperty = "registrationRequest", ssa = "softwareStatement",
        message = "Host names of logo_uri/tos_uri/policy_uri/client_uri does not match with the redirect_uris:"
                + CDSValidationConstants.INVALID_REDIRECT_URI, groups = AttributeChecks.class)
@ValidateUriConnection(registrationRequestProperty = "registrationRequest", ssa = "softwareStatement",
        message = "Provided logo_uri/client_uri/policy_uri/tos_uri in the request does not resolve" +
                " to a valid web page:" + CDSValidationConstants.INVALID_REDIRECT_URI, groups = AttributeChecks.class)
@ValidateJTI(registrationRequestProperty = "registrationRequest", ssa = "softwareStatement",
        message = CDSValidationConstants.JTI_REPLAYED + ":" + DCRCommonConstants.INVALID_META_DATA,
        groups = AttributeChecks.class)
public class CDSRegistrationRequest extends RegistrationRequest {

    private RegistrationRequest registrationRequest;

    public CDSRegistrationRequest(RegistrationRequest registrationRequest) {

        this.registrationRequest = registrationRequest;
    }

    public RegistrationRequest getRegistrationRequest() {

        return registrationRequest;
    }

    @Override
    public String getTokenEndPointAuthentication() {

        return registrationRequest.getTokenEndPointAuthentication();
    }

    @Override
    public List<String> getGrantTypes() {

        return registrationRequest.getGrantTypes();
    }

    @Override
    public List<String> getCallbackUris() {

        return registrationRequest.getCallbackUris();
    }

    @Override
    public List<String> getResponseTypes() {

        return registrationRequest.getResponseTypes();
    }

    @Override
    public String getApplicationType() {

        return registrationRequest.getApplicationType();
    }

    @Override
    public String getIssuer() {

        return registrationRequest.getIssuer();
    }

    @Override
    public String getAudience() {

        return registrationRequest.getAudience();
    }

    @Override
    public String getSoftwareStatement() {

        return registrationRequest.getSoftwareStatement();
    }

    @Override
    public String getIdTokenSignedResponseAlg() {

        return registrationRequest.getIdTokenSignedResponseAlg();
    }

    @Override
    public String getTokenEndPointAuthSigningAlg() {

        return registrationRequest.getTokenEndPointAuthSigningAlg();
    }

    @Override
    public String getRequestObjectSigningAlg() {

        return registrationRequest.getRequestObjectSigningAlg();
    }

    @Override
    public String getIdTokenEncryptionResponseAlg() {

        return registrationRequest.getIdTokenEncryptionResponseAlg();
    }

    @Override
    public String getIdTokenEncryptionResponseEnc() {

        return registrationRequest.getIdTokenEncryptionResponseEnc();
    }

    @Override
    public SoftwareStatementBody getSoftwareStatementBody() {

        return registrationRequest.getSoftwareStatementBody();
    }
}
