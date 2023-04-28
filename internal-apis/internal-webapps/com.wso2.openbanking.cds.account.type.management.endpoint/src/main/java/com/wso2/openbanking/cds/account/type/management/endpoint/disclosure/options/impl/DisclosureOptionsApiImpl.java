/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.account.type.management.endpoint.disclosure.options.impl;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.account.type.management.endpoint.disclosure.options.api.DisclosureOptionsApi;
import com.wso2.openbanking.cds.account.type.management.endpoint.disclosure.options.handler.DisclosureOptionsApiHandler;
import com.wso2.openbanking.cds.account.type.management.endpoint.disclosure.options.validation.
        DomsOptionsStatusValidator;

import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.Response;

/**
 * Disclosure Options API
 */
public class DisclosureOptionsApiImpl implements DisclosureOptionsApi {

    private static final Log log = LogFactory.getLog(DisclosureOptionsApiImpl.class);
    private static final String X_VERSION = "2";

    DisclosureOptionsApiHandler disclosureOptionsApiHandler = new DisclosureOptionsApiHandler();

    /**
     * Disclosure Options API
     */
    public Response cdsUpdateAccountDisclosureOptions(String requestBody) {
        try {
            JSONObject validatedRequestBody = new DomsOptionsStatusValidator().validateDomsOptionsStatusRequest
                    (requestBody);
            // proceed with processing the validated request body
            disclosureOptionsApiHandler.cdsUpdateAccountDisclosureOptions(String.valueOf(validatedRequestBody));
            String successMessage = "Account Disclosure Options successfully updated!";
            return Response.ok().entity(successMessage).build();
        } catch (OpenBankingException e) {
            // catch OpenBankingException thrown by the validator and return a BAD_REQUEST response
            log.error("Bad Request. Request body validation failed", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            // catch any other exception thrown and return a BAD_REQUEST response
            log.error("Bad Request. Request body validation failed", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}

