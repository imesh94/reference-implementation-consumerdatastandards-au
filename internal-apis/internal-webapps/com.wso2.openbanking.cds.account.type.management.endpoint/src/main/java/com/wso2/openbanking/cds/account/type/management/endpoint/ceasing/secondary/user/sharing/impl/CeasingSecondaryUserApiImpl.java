/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.sharing.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.AuthorizationResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.DetailedConsentResource;
import com.wso2.openbanking.accelerator.consent.mgt.service.ConsentCoreService;
import com.wso2.openbanking.accelerator.consent.mgt.service.impl.ConsentCoreServiceImpl;
import com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.sharing.api.
        CeasingSecondaryUserApi;
import com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.sharing.handler.
        CeasingSecondaryUserHandler;
import com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.sharing.models.
        LegalEntityListDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.sharing.models.
        UsersAccountsLegalEntitiesDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.model.ErrorDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.model.ErrorStatusEnum;
import com.wso2.openbanking.cds.account.type.management.endpoint.util.ValidationUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import javax.ws.rs.core.Response;

/**
 * Ceasing Secondary User - Impl
 */
public class CeasingSecondaryUserApiImpl implements CeasingSecondaryUserApi {


    private static final Log log = LogFactory.getLog(CeasingSecondaryUserApiImpl.class);
    CeasingSecondaryUserHandler ceasingSecondaryUserHandler = new CeasingSecondaryUserHandler();

    /**
     * {@inheritDoc}
     */
    public Response blockLegalEntitySharingStatus(String requestBody) {

        ObjectMapper objectMapper = new ObjectMapper();
        LegalEntityListDTO legalEntityListDTO;

        try {
            legalEntityListDTO = objectMapper.readValue(requestBody, LegalEntityListDTO.class);

            //Validating the requestBody
            String validationError = ValidationUtil.getFirstViolationMessage(legalEntityListDTO);

            if (validationError.isEmpty()) {
                ceasingSecondaryUserHandler.blockLegalEntitySharingStatus(legalEntityListDTO);

                log.info("Success!, the sharing status for legal entity/entities has been blocked.");
                return Response.ok().
                        entity("Success!, the sharing status for legal entity/entities has been blocked.").build();
            } else {
                log.error("Error occurred while blocking the sharing status for a legal entity/entities.");
                ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INVALID_REQUEST,
                        "Error occurred while blocking the sharing status for a legal entity/entities.");
                return Response.status(Response.Status.BAD_REQUEST).entity(errorDTO).build();
            }

        } catch (JsonProcessingException e) {
            log.error("Error occurred while processing the JSON object.", e);
            ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INVALID_REQUEST,
                    "Error occurred while processing the JSON object.");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO).build();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Response unblockLegalEntitySharingStatus(String requestBody) {


        ObjectMapper objectMapper = new ObjectMapper();
        LegalEntityListDTO legalEntityListDTO;

        try {
            legalEntityListDTO = objectMapper.readValue(requestBody, LegalEntityListDTO.class);

            //Validating the requestBody
            String validationError = ValidationUtil.getFirstViolationMessage(legalEntityListDTO);

            if (validationError.isEmpty()) {
                ceasingSecondaryUserHandler.unblockLegalEntitySharingStatus(legalEntityListDTO);

                log.info("Success!, the sharing status for legal entity/entities has been unblocked.");
                return Response.ok().
                        entity("Success!, the sharing status for legal entity/entities has been unblocked.").build();
            } else {
                log.error("Error occurred while unblocking the sharing status for a legal entity/entities.");
                ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INVALID_REQUEST,
                        "Error occurred while unblocking the sharing status for a legal entity/entities.");
                return Response.status(Response.Status.BAD_REQUEST).entity(errorDTO).build();
            }

        } catch (JsonProcessingException e) {
            log.error("Error occurred while processing the JSON object.", e);
            ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INVALID_REQUEST,
                    "Error occurred while processing the JSON object.");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorDTO).build();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Response getUsersAccountsLegalEntities(String userID) {

        String userIDError = null;

        try {
            ConsentCoreService consentCoreService = new ConsentCoreServiceImpl();

            // Creating an array list to append the userID
            ArrayList<String> userIDAL = new ArrayList<>();
            userIDAL.add(userID);

            UsersAccountsLegalEntitiesDTO responseUsersAccountsLegalEntitiesDTO =
                    new UsersAccountsLegalEntitiesDTO(userID);

            ArrayList<DetailedConsentResource> responseDetailedConsents = consentCoreService.searchDetailedConsents
                    (null, null, null, null, userIDAL, null, null,
                            null, null, false);

            // Checking the validity of the userID
            if (responseDetailedConsents.size() == 0) {
                userIDError = "Error!, user not found with userID: " + userID;
            }

            if (userIDError != null) {
                log.error(userIDError);
                ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INVALID_REQUEST,
                        userIDError);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO).build();
            }

            // Checking if the user is a secondary account owner
            boolean isSecondaryAccountOwner = false;

            // Consent
            for (DetailedConsentResource detailedConsent : responseDetailedConsents) {

                // Authorization Resource
                for (AuthorizationResource authorizationResource : detailedConsent.getAuthorizationResources()) {
                    if (authorizationResource.getUserID().equals(userID) &&
                            authorizationResource.getAuthorizationType().equals("secondary_account_owner")) {
                        isSecondaryAccountOwner = true;
                        break;
                    }
                }
            }

            if (!isSecondaryAccountOwner) {
                userIDError = "Error, UserID: " + userID + " is not a secondary account owner";
            }

            if (userIDError == null) {
                UsersAccountsLegalEntitiesDTO responseUsersAccountsLegalEntities = ceasingSecondaryUserHandler.
                        getUsersAccountsLegalEntities(responseDetailedConsents, responseUsersAccountsLegalEntitiesDTO);
                return Response.ok().entity(responseUsersAccountsLegalEntities).build();
            } else {
                log.error(userIDError);
                ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INVALID_REQUEST,
                        userIDError);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorDTO).build();
            }

        } catch (Exception e) {
            log.error("Error occurred while retrieving users,accounts and legal entities.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }
}

