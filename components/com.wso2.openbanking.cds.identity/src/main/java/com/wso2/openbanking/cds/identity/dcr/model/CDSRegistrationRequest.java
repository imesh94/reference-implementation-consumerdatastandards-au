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
package com.wso2.openbanking.cds.identity.dcr.model;

import com.wso2.openbanking.accelerator.identity.dcr.model.RegistrationRequest;
import com.wso2.openbanking.accelerator.identity.dcr.model.SoftwareStatementBody;
import com.wso2.openbanking.accelerator.identity.dcr.validation.DCRCommonConstants;
import com.wso2.openbanking.accelerator.identity.dcr.validation.validationgroups.AttributeChecks;
import com.wso2.openbanking.cds.identity.dcr.validation.annotation.ValidateCallbackUris;

import java.util.List;


/**
 * Model class for CDS dcr registration request
 */
@ValidateCallbackUris(registrationRequestProperty = "registrationRequest", callbackUrisProperty = "redirectUris",
        ssa = "softwareStatement", message = "Invalid callback uris:" + DCRCommonConstants.INVALID_META_DATA,
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
