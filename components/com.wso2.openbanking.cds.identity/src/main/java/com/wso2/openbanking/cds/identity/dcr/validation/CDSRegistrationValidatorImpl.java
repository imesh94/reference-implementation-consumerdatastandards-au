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
package com.wso2.openbanking.cds.identity.dcr.validation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.wso2.openbanking.accelerator.identity.dcr.exception.DCRValidationException;
import com.wso2.openbanking.accelerator.identity.dcr.model.RegistrationRequest;
import com.wso2.openbanking.accelerator.identity.dcr.validation.DefaultRegistrationValidatorImpl;
import com.wso2.openbanking.cds.identity.dcr.model.CDSRegistrationRequest;
import com.wso2.openbanking.cds.identity.dcr.model.CDSRegistrationResponse;
import com.wso2.openbanking.cds.identity.dcr.model.CDSSoftwareStatementBody;
import com.wso2.openbanking.cds.identity.dcr.utils.ValidationUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * CDS specific registration validator class.
 */
public class CDSRegistrationValidatorImpl extends DefaultRegistrationValidatorImpl {

    @Override
    public void validatePost(RegistrationRequest registrationRequest) throws DCRValidationException {

        CDSRegistrationRequest cdsRegistrationRequest = new CDSRegistrationRequest(registrationRequest);
        ValidationUtils.validateRequest(cdsRegistrationRequest);
    }

    @Override
    public void validateUpdate(RegistrationRequest registrationRequest) throws DCRValidationException {

        CDSRegistrationRequest cdsRegistrationRequest = new CDSRegistrationRequest(registrationRequest);
        ValidationUtils.validateRequest(cdsRegistrationRequest);
    }

    @Override
    public void setSoftwareStatementPayload(RegistrationRequest registrationRequest, String decodedSSA) {

        CDSSoftwareStatementBody cdsSoftwareStatementBody =  new GsonBuilder().create()
                .fromJson(decodedSSA, CDSSoftwareStatementBody.class);
        registrationRequest.setSoftwareStatementBody(cdsSoftwareStatementBody);
    }

    @Override
    public String getRegistrationResponse(Map<String, Object> spMetaData) {

        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(spMetaData);
        CDSRegistrationResponse cdsRegistrationResponse =  gson.fromJson(jsonElement, CDSRegistrationResponse.class);
        if (StringUtils.isBlank(cdsRegistrationResponse.getRequestObjectSigningAlg())) {
            cdsRegistrationResponse.setRequestObjectSigningAlg("PS256");
        }
        return gson.toJson(cdsRegistrationResponse);
    }
}
