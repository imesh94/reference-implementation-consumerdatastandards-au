/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.impl;

import com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.api.CeasingSecondaryUserApi;
import com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.handler.
        CeasingSecondaryUserHandler;
import com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.models.UsersAccountsLegalEntitiesResource;
import com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.validator.
        CeasingSecondaryUserRequestValidator;
import net.minidev.json.JSONArray;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.Response;

/**
 * Ceasing Secondary User - Impl
 */
public class CeasingSecondaryUserApiImpl implements CeasingSecondaryUserApi {


    private static final Log log = LogFactory.getLog(CeasingSecondaryUserApiImpl.class);

    CeasingSecondaryUserHandler ceasingSecondaryUserHandler = new CeasingSecondaryUserHandler();

    /**
     * ----- Block the sharing status for a legal entity -----
     * An endpoint should be designed to allow an account holder to block a legal entity in order to cease the
     * disclosure initiated by a secondary user for a particular account to that legal entity.
     */
    public Response blockLegalEntitySharingStatus(String requestBody) {

        try {
            //Validating the request object
            JSONArray validatedRequestBody = CeasingSecondaryUserRequestValidator.
                    ceasingSecondaryUserRequestValidator(requestBody);

            ceasingSecondaryUserHandler.blockLegalEntitySharingStatus(validatedRequestBody);

            log.info("Success!, the sharing status for legal entity/entities has been blocked.");
            return Response.ok().
                    entity("Success!, the sharing status for legal entity/entities has been blocked.").build();
        } catch (Exception e) {
            log.error("Error occurred while blocking the sharing status for a legal entity/entities.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }


    /**
     * ----- Unblock the sharing status for a legal entity -----
     * An endpoint should be designed to allow an account holder to block a legal entity in order to cease the
     * disclosure initiated by a secondary user for a particular account to that legal entity.
     */
    public Response unblockLegalEntitySharingStatus(String requestBody) {

        try {
            //Validating the request object
            JSONArray validatedRequestBody = CeasingSecondaryUserRequestValidator.
                    ceasingSecondaryUserRequestValidator(requestBody);

            ceasingSecondaryUserHandler.unblockLegalEntitySharingStatus(validatedRequestBody);

            log.info("Success!, the sharing status for legal entity/entities has been unblocked.");
            return Response.ok().
                    entity("Success!, the sharing status for legal entity/entities has been unblocked.").build();
        } catch (Exception e) {
            log.error("Error occurred while unblocking the sharing status for a legal entity/entities.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }

    /**
     * ----- Get accounts, secondary users, legal entities and their sharing status -----
     * An endpoint should be designed to get all accounts, secondary users, legal entities and their sharing status
     * bound to the account holder in the consent manager dashboard.
     */
    public Response getUsersAccountsLegalEntities(String userId) {

        try {
            UsersAccountsLegalEntitiesResource responseUsersAccountsLegalEntities = ceasingSecondaryUserHandler.
                    getUsersAccountsLegalEntities(userId);
            return Response.ok().entity(responseUsersAccountsLegalEntities).build();
        } catch (Exception e) {
            //TODO: Update the response message
            log.error("Error occurred while retrieving users,accounts and legal entities.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }
}

