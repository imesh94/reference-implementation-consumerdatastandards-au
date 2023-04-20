/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.api.NominatedRepresentativeAPI;
import com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model.AccountListDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model.ErrorDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.Response;

/**
 * Implementation of NominatedRepresentativeAPI.
 */
public class NominatedRepresentativeAPIImpl implements NominatedRepresentativeAPI {

    private static final Log log = LogFactory.getLog(NominatedRepresentativeAPIImpl.class);
    private static final String INVALID_REQUEST = "invalid_request";
    private static final String INTERNAL_SERVER_ERROR = "internal_server_error";

    /**
     * {@inheritDoc}
     */
    @Override
    public Response updateNominatedRepresentativePermissions(String requestBody) {

        ObjectMapper objectMapper = new ObjectMapper();
        AccountListDTO accountListDTO;

        try {
            accountListDTO = objectMapper.readValue(requestBody, AccountListDTO.class);
            // Validate the request body
            String validationError = NominatedRepresentativeUtil.validateAccountListDTO(accountListDTO);
            if (validationError.isEmpty()) {
                // Proceed with persisting nominated representative data if there are no violations.
                boolean successfullyPersisted = NominatedRepresentativeUtil.persistNominatedRepresentativeData
                        (accountListDTO);
                if (successfullyPersisted) {
                    return Response.status(Response.Status.OK).build();
                } else {
                    // Return internal server error if an error occurred in the accelerator account metadata service.
                    ErrorDTO errorDTO = new ErrorDTO(INTERNAL_SERVER_ERROR, "Error occurred while " +
                            "persisting nominated representative data");
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorDTO).build();
                }
            } else {
                ErrorDTO errorDTO = new ErrorDTO(INVALID_REQUEST, validationError);
                return Response.status(Response.Status.BAD_REQUEST).entity(errorDTO).build();
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            ErrorDTO errorDTO = new ErrorDTO(INVALID_REQUEST, "Error occurred while parsing " +
                    "the request body");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorDTO).build();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response revokeNominatedRepresentativePermissions(String requestBody) {

        //ToDo: Implement this method
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response retrieveNominatedRepresentativePermissions(String userId, String accountIds) {

        //ToDo: Implement this method
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response retrieveNominatedRepresentativeProfiles(String userId) {

        //ToDo: Implement this method
        return null;
    }

}
