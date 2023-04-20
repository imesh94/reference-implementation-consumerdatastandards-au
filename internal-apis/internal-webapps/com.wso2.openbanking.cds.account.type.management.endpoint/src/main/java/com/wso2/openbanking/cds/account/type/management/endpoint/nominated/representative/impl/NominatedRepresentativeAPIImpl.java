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
import com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model.BusinessStakeholderListDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model.ErrorDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.Response;

/**
 * Implementation of NominatedRepresentativeAPI.
 */
public class NominatedRepresentativeAPIImpl implements NominatedRepresentativeAPI {

    private static final Log log = LogFactory.getLog(NominatedRepresentativeAPIImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Response updateNominatedRepresentativePermissions(String requestBody) {

        ObjectMapper objectMapper = new ObjectMapper();
        BusinessStakeholderListDTO businessNominatedUserPermissions;

        try {
            businessNominatedUserPermissions = objectMapper.readValue(requestBody, BusinessStakeholderListDTO.class);
            String validationError = validateBusinessStakeholderListDTO(businessNominatedUserPermissions);
            if (validationError.isEmpty()) {
                return Response.ok().entity("Success").build();
            } else {
                ErrorDTO errorDTO = new ErrorDTO("invalid_request", validationError);
                return Response.status(Response.Status.BAD_REQUEST).entity(errorDTO).build();
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            ErrorDTO errorDTO = new ErrorDTO("invalid_request", "Error occurred while parsing " +
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

    /**
     * Validate the BusinessStakeholderListDTO object and return the first violation message.
     *
     * @param businessStakeholderListDTO BusinessStakeholderListDTO object
     * @return first violation message
     */
    private String validateBusinessStakeholderListDTO(BusinessStakeholderListDTO businessStakeholderListDTO) {

        String firstViolationMessage = "";
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<BusinessStakeholderListDTO>> violations = validator.validate(
                businessStakeholderListDTO);
        if (!violations.isEmpty()) {
            ConstraintViolation<BusinessStakeholderListDTO> firstViolation = violations.iterator().next();
            firstViolationMessage = firstViolation.getMessage().replaceAll("\\.$", "") +
                    ". Error path :" + firstViolation.getPropertyPath();
        }
        return firstViolationMessage;
    }

}
