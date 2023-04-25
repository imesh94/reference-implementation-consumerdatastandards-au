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
    private static final String XV_HEADER = "x-v";
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
            log.info(successMessage); // log the success message to the console
            return Response.ok().entity(successMessage).header(XV_HEADER, X_VERSION).build();
        } catch (OpenBankingException e) {
            // catch OpenBankingException thrown by the validator and return a BAD_REQUEST response
            log.error("Bad Request. Request body validation failed", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).
                    header(XV_HEADER, X_VERSION).build();
        } catch (Exception e) {
            // catch any other exception thrown and return a BAD_REQUEST response
            log.error("Bad Request. Request body validation failed", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).
                    header(XV_HEADER, X_VERSION).build();
        }
    }
}

