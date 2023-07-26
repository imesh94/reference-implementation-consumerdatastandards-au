/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.account.type.management.endpoint.disclosure.options.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.openbanking.cds.account.type.management.endpoint.disclosure.options.api.DisclosureOptionsApi;
import com.wso2.openbanking.cds.account.type.management.endpoint.disclosure.options.handler.DisclosureOptionsApiHandler;
import com.wso2.openbanking.cds.account.type.management.endpoint.disclosure.options.model.DOMSStatusUpdateListDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.model.ErrorDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.model.ErrorStatusEnum;
import com.wso2.openbanking.cds.account.type.management.endpoint.util.ValidationUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.Response;

/**
 * Disclosure Options API Implementation.
 */
public class DisclosureOptionsApiImpl implements DisclosureOptionsApi {

    private static final Log log = LogFactory.getLog(DisclosureOptionsApiImpl.class);
    DisclosureOptionsApiHandler disclosureOptionsApiHandler = new DisclosureOptionsApiHandler();

    /**
     * The following method updates the Disclosure Options.
     */
    public Response updateCDSAccountDisclosureOptions(String requestBody) {

        ObjectMapper objectMapper = new ObjectMapper();
        DOMSStatusUpdateListDTO domsStatusUpdateListDTO;

        try {
            domsStatusUpdateListDTO = objectMapper.readValue(requestBody, DOMSStatusUpdateListDTO.class);

            String validationError = ValidationUtil.getFirstViolationMessage(domsStatusUpdateListDTO);

            if (validationError.isEmpty()) {
                disclosureOptionsApiHandler.updateCDSAccountDisclosureOptions(requestBody);
                return Response.ok().entity("").build();
            } else {
                ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INVALID_REQUEST, validationError);
                return Response.status(Response.Status.BAD_REQUEST).entity(errorDTO).build();
            }

        } catch (JsonProcessingException e) {
            ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INVALID_REQUEST, "Error occurred while " +
                    "parsing the request body");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorDTO).build();
        }
    }
}

