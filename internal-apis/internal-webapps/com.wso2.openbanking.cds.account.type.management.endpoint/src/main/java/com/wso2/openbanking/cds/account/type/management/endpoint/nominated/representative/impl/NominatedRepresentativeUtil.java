/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.impl;

import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataServiceImpl;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model.AccountDataDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model.AccountListDTO;
import com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model.NominatedRepresentativeDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Util class for Nominated Representative functions.
 */
public class NominatedRepresentativeUtil {

    private static final Log log = LogFactory.getLog(NominatedRepresentativeUtil.class);

    /**
     * Validate the AccountListDTO object and return the first violation message.
     *
     * @param accountListDTO AccountListDTO object
     * @return first violation message
     */
    protected static String validateAccountListDTO(AccountListDTO accountListDTO) {

        String firstViolationMessage = "";
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<AccountListDTO>> violations = validator.validate(
                accountListDTO);
        if (!violations.isEmpty()) {
            ConstraintViolation<AccountListDTO> firstViolation = violations.iterator().next();
            firstViolationMessage = firstViolation.getMessage().replaceAll("\\.$", "") +
                    ". Error path :" + firstViolation.getPropertyPath();
        }
        return firstViolationMessage;
    }


    /**
     * Persist nominated representative data using the accelerator account metadata service.
     *
     * @param accountListDTO AccountListDTO object
     * @return true if the data is persisted successfully
     */
    protected static boolean persistNominatedRepresentativeData(AccountListDTO accountListDTO) {

        AccountMetadataServiceImpl accountMetadataService = AccountMetadataServiceImpl.getInstance();
        Map<String, String> accountOwnerPermissionMap = Collections.singletonMap("bnr-permission", "VIEW");

        try {
            for (AccountDataDTO accountDataDTO : accountListDTO.getData()) {
                String accountID = accountDataDTO.getAccountID();
                // Persist account owners
                for (String accountOwner : accountDataDTO.getAccountOwners()) {
                    accountMetadataService.addOrUpdateAccountMetadata(accountID, accountOwner,
                            accountOwnerPermissionMap);
                }
                // Persist nominated representatives
                for (NominatedRepresentativeDTO nominatedRepresentative : accountDataDTO.
                        getNominatedRepresentatives()) {
                    Map<String, String> representativePermissionMap = Collections.singletonMap("bnr-permission",
                            nominatedRepresentative.getPermission());
                    String nominatedRepresentativeUserName = nominatedRepresentative.getName();
                    accountMetadataService.addOrUpdateAccountMetadata(accountID, nominatedRepresentativeUserName,
                            representativePermissionMap);
                }
            }
        } catch (OpenBankingException e) {
            log.error("Error occurred while persisting nominated representative data", e);
            return false;
        }
        return true;
    }

}
