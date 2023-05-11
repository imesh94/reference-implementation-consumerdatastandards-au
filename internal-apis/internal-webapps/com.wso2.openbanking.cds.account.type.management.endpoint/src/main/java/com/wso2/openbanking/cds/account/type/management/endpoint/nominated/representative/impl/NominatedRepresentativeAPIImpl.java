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
import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataServiceImpl;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.AuthorizationResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentMappingResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.DetailedConsentResource;
import com.wso2.openbanking.accelerator.consent.mgt.service.impl.ConsentCoreServiceImpl;
import com.wso2.openbanking.cds.account.type.management.endpoint.model.ErrorDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.model.ErrorStatusEnum;
import com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.api.NominatedRepresentativeAPI;
import com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model.AccountDataDeleteDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model.AccountDataUpdateDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model.AccountListDeleteDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model.AccountListUpdateDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model.BNRPermissionsEnum;
import com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model.NominatedRepresentativeDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model.NominatedRepresentativeResponseDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.util.ValidationUtil;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;

/**
 * Implementation of NominatedRepresentativeAPI.
 */
public class NominatedRepresentativeAPIImpl implements NominatedRepresentativeAPI {

    private static final Log log = LogFactory.getLog(NominatedRepresentativeAPIImpl.class);

    private static final String BNR_PERMISSION = "bnr-permission";

    // Permission status
    private static final String VIEW = "VIEW";
    private static final String AUTHORIZE = "AUTHORIZE";
    private static final String REVOKE = "REVOKE";

    // Consent constants
    private static final String PRIMARY_MEMBER_AUTH_TYPE = "primary_member";
    private static final String REVOKED_CONSENT_STATUS = "Revoked";

    // Error messages
    private static final String METADATA_SERVICE_ERROR = "\"Error occurred while persisting nominated " +
            "representative data using the account metadata service\"";

    private static final ConsentCoreServiceImpl consentCoreService = new ConsentCoreServiceImpl();


    /**
     * {@inheritDoc}
     */
    @Override
    public Response updateNominatedRepresentativePermissions(String requestBody) {

        ObjectMapper objectMapper = new ObjectMapper();
        AccountListUpdateDTO accountListUpdateDTO;

        try {
            accountListUpdateDTO = objectMapper.readValue(requestBody, AccountListUpdateDTO.class);
            // Validate the request body
            String validationError = ValidationUtil.getFirstViolationMessage(accountListUpdateDTO);
            if (validationError.isEmpty()) {
                // Proceed with persisting nominated representative data if there are no violations.
                boolean successfullyPersisted = persistUpdatedNominatedRepresentativeData
                        (accountListUpdateDTO);
                if (successfullyPersisted) {
                    return Response.status(Response.Status.OK).build();
                } else {
                    // Return internal server error if an error occurred in the accelerator account metadata service.
                    ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INTERNAL_SERVER_ERROR,
                            "Error occurred while " + "persisting updated nominated representative data");
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorDTO).build();
                }
            } else {
                ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INVALID_REQUEST, validationError);
                return Response.status(Response.Status.BAD_REQUEST).entity(errorDTO).build();
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INVALID_REQUEST,
                    "Error occurred while parsing " + "the request body");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorDTO).build();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response revokeNominatedRepresentativePermissions(String requestBody) {

        ObjectMapper objectMapper = new ObjectMapper();
        AccountListDeleteDTO accountListDeleteDTO;

        try {
            accountListDeleteDTO = objectMapper.readValue(requestBody, AccountListDeleteDTO.class);
            // Validate the request body
            String validationError = ValidationUtil.getFirstViolationMessage(accountListDeleteDTO);
            if (!validationError.isEmpty()) {
                ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INVALID_REQUEST, validationError);
                return Response.status(Response.Status.BAD_REQUEST).entity(errorDTO).build();
            }
            // Check if all user-ids in the request body are available in the database.
            List<String> unavailableUserIds = getUnavailableUserIdsFromAccountListDeleteDTO(accountListDeleteDTO);
            if (!unavailableUserIds.isEmpty()) {
                String unavailableUserIdsString = StringUtils.join(unavailableUserIds, ", ");
                ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.RESOURCE_NOT_FOUND, "User ID(s) " +
                        unavailableUserIdsString + " not found in the database.");
                return Response.status(Response.Status.NOT_FOUND).entity(errorDTO).build();
            }
            // Proceed with revoking nominated representative data.
            boolean successfullyPersisted = persistRevokedNominatedRepresentativeData(accountListDeleteDTO);
            if (successfullyPersisted) {
                return Response.status(Response.Status.OK).build();
            } else {
                // Return internal server error if an error occurred in the accelerator account metadata service.
                ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INTERNAL_SERVER_ERROR,
                        "Error occurred while " + "persisting revoked nominated representative data");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorDTO).build();
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INVALID_REQUEST,
                    "Error occurred while parsing " + "the request body");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorDTO).build();
        } catch (OpenBankingException e) {
            ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INTERNAL_SERVER_ERROR,
                    "Error occurred while validating " + "the user-Ids in the database");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorDTO).build();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response retrieveNominatedRepresentativePermissions(String accountId, String userId) {

        // Validate userId and accountId
        if (StringUtils.isBlank(accountId)) {
            ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INVALID_REQUEST,
                    "Account ID is required to proceed.");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorDTO).build();
        }
        if (StringUtils.isBlank(userId)) {
            ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INVALID_REQUEST,
                    "User ID is required to proceed.");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorDTO).build();
        }
        AccountMetadataServiceImpl accountMetadataService = AccountMetadataServiceImpl.getInstance();

        try {
            String permissionStatus = accountMetadataService.getAccountMetadataByKey(accountId, userId, BNR_PERMISSION);
            if (StringUtils.isNotBlank(permissionStatus)) {
                NominatedRepresentativeResponseDTO nominatedRepresentativeResponseDTO = new
                        NominatedRepresentativeResponseDTO(accountId, userId, permissionStatus);
                return Response.status(Response.Status.OK).entity(nominatedRepresentativeResponseDTO).build();
            } else {
                // Return not found if no nominated representative data is found for the given account ID and user ID.
                ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.RESOURCE_NOT_FOUND,
                        "No nominated representative data found for the given account ID and user ID");
                return Response.status(Response.Status.NOT_FOUND).entity(errorDTO).build();
            }
        } catch (OpenBankingException e) {
            log.error(e.getMessage());
            ErrorDTO errorDTO = new ErrorDTO(ErrorStatusEnum.INTERNAL_SERVER_ERROR,
                    "Error occurred while retrieving nominated representative permissions");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorDTO).build();
        }
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
     * Persist updated nominated representative data using the accelerator account metadata service.
     *
     * @param accountListUpdateDTO AccountListDTO object
     * @return true if the data is persisted successfully
     */
    protected static boolean persistUpdatedNominatedRepresentativeData(AccountListUpdateDTO accountListUpdateDTO) {

        AccountMetadataServiceImpl accountMetadataService = AccountMetadataServiceImpl.getInstance();
        Map<String, String> accountOwnerPermissionMap = Collections.singletonMap(BNR_PERMISSION,
                BNRPermissionsEnum.VIEW.toString());

        try {
            for (AccountDataUpdateDTO accountDataUpdateDTO : accountListUpdateDTO.getData()) {
                String accountID = accountDataUpdateDTO.getAccountID();
                // Persist account owners
                for (String accountOwner : accountDataUpdateDTO.getAccountOwners()) {
                    accountMetadataService.addOrUpdateAccountMetadata(accountID, accountOwner,
                            accountOwnerPermissionMap);
                }
                // Persist nominated representatives
                for (NominatedRepresentativeDTO nominatedRepresentative : accountDataUpdateDTO.
                        getNominatedRepresentatives()) {
                    Map<String, String> representativePermissionMap = Collections.singletonMap(BNR_PERMISSION,
                            nominatedRepresentative.getPermission());
                    String nominatedRepresentativeUserName = nominatedRepresentative.getName();
                    accountMetadataService.addOrUpdateAccountMetadata(accountID, nominatedRepresentativeUserName,
                            representativePermissionMap);
                }
            }
        } catch (OpenBankingException e) {
            log.error(METADATA_SERVICE_ERROR, e);
            return false;
        }
        return true;
    }

    /**
     * Persist revoked nominated representative data using the accelerator account metadata service.
     *
     * @param accountListDeleteDTO AccountListDeleteDTO object
     * @return true if the data is persisted successfully
     */
    protected static boolean persistRevokedNominatedRepresentativeData(AccountListDeleteDTO accountListDeleteDTO) {

        AccountMetadataServiceImpl accountMetadataService = AccountMetadataServiceImpl.getInstance();
        Map<String, String> revokePermissionMap = Collections.singletonMap(BNR_PERMISSION, BNRPermissionsEnum.
                REVOKE.toString());
        try {
            for (AccountDataDeleteDTO accountDataDeleteDTO : accountListDeleteDTO.getData()) {
                String accountID = accountDataDeleteDTO.getAccountID();
                // Get a list of account owners and nominated representatives for the account
                List<String> accountUsers = getUserIdListFromAccountDataDeleteDTO(accountDataDeleteDTO);
                ArrayList<String> consentIds = new ArrayList<>();

                // Persist account owners
                for (String accountUser : accountUsers) {
                    accountMetadataService.addOrUpdateAccountMetadata(accountID, accountUser,
                            revokePermissionMap);
                    /* Add consent ID to the consentIds list used for revocation if the user is primary member
                       of the consent and if the revocation config is enabled */
                    if (OpenBankingCDSConfigParser.getInstance().isBNRConsentRevocationEnabled()) {
                        List<AuthorizationResource> authResourcesForUser = consentCoreService.
                                searchAuthorizationsForUser(accountUser);
                        for (AuthorizationResource authResource : authResourcesForUser) {
                            if (PRIMARY_MEMBER_AUTH_TYPE.equals(authResource.getAuthorizationType())) {
                                consentIds.add(authResource.getConsentID());
                            }
                        }
                    }

                }
                /* Check if there are any other users who have AUTHORIZE permission for the account
                   If there are none, remove all bnr-permission records for the account*/
                Map<String, String> userIdAttributesMap = accountMetadataService.getUserAttributesForAccountIdAndKey(
                        accountID, BNR_PERMISSION);
                if (!userIdAttributesMap.containsValue(BNRPermissionsEnum.AUTHORIZE.toString())) {
                    accountMetadataService.removeAccountMetadataByKeyForAllUsers(accountID, BNR_PERMISSION);
                }
                // Revoke the consent where the user is the primary member of the consent
                if (consentIds.size() > 0) {
                    List<DetailedConsentResource> detailedConsentResources = consentCoreService.searchDetailedConsents(
                            consentIds, null, null, null, null, null, null, null, null, false);
                    log.info(detailedConsentResources.size());
                    for (DetailedConsentResource detailedConsentResource : detailedConsentResources) {
                        List<ConsentMappingResource> consentMappingResources = detailedConsentResource.
                                getConsentMappingResources();
                        for (ConsentMappingResource consentMappingResource : consentMappingResources) {
                            if (accountID.equals(consentMappingResource.getAccountID())) {
                                consentCoreService.revokeConsentWithReason(detailedConsentResource.getConsentID(),
                                        REVOKED_CONSENT_STATUS, null, true, "Revoked using BNR Dashboard");
                                break;
                            }
                        }
                    }
                }
            }
        } catch (OpenBankingException e) {
            log.error(METADATA_SERVICE_ERROR, e);
            return false;
        }
        return true;
    }

    /**
     * Get a list of user-ids from the AccountDataDeleteDTO object which are not present in the database.
     *
     * @param accountListDeleteDTO - AccountListDeleteDTO object
     * @return
     * @throws OpenBankingException - OpenBankingException
     */
    private static List<String> getUnavailableUserIdsFromAccountListDeleteDTO(
            AccountListDeleteDTO accountListDeleteDTO) throws OpenBankingException {

        List<String> unavailableUserIds = new ArrayList<>();
        for (AccountDataDeleteDTO accountDataDeleteDTO : accountListDeleteDTO.getData()) {
            String accountId = accountDataDeleteDTO.getAccountID();
            List<String> requestedUserIds = getUserIdListFromAccountDataDeleteDTO(accountDataDeleteDTO);
            List<String> unavailableUserIdsForAccount = checkForUnavailableUserIdsInDatabase(accountId,
                    requestedUserIds);
            if (!unavailableUserIdsForAccount.isEmpty()) {
                unavailableUserIds.addAll(unavailableUserIdsForAccount);
            }
        }
        return unavailableUserIds;
    }

    /**
     * Check if any of the user-id data is unavailable in the database for the given account-id using accelerator
     * account metadata service.
     *
     * @param accountId           Account ID
     * @param requestedUserIdList user-ids to be checked
     * @return list of unavailable user-ids
     */
    private static List<String> checkForUnavailableUserIdsInDatabase(String accountId, List<String>
            requestedUserIdList) throws OpenBankingException {

        AccountMetadataServiceImpl accountMetadataService = AccountMetadataServiceImpl.getInstance();
        Map<String, String> availableUserIdMap = accountMetadataService.getUserAttributesForAccountIdAndKey(accountId,
                BNR_PERMISSION);
        List<String> unavailableUserIds = new ArrayList<>();
        for (String requestedUserId : requestedUserIdList) {
            if (!availableUserIdMap.containsKey(requestedUserId)) {
                unavailableUserIds.add(requestedUserId);
            }
        }
        return unavailableUserIds;
    }

    /**
     * Get list of userIDs from AccountDataDeleteDTO.
     *
     * @param accountDataDeleteDTO accountDataDeleteDTO
     * @return List of account-ids in the given AccountDataDeleteDTO
     */
    private static List<String> getUserIdListFromAccountDataDeleteDTO(AccountDataDeleteDTO accountDataDeleteDTO) {
        List<String> userIdList = new ArrayList<>();
        userIdList.addAll(accountDataDeleteDTO.getAccountOwners());
        userIdList.addAll(accountDataDeleteDTO.getNominatedRepresentatives());
        return userIdList;
    }


}
