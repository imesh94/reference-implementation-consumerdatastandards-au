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

package com.wso2.openbanking.cds.consent.extensions.validate;

import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataService;
import com.wso2.openbanking.accelerator.account.metadata.service.service.AccountMetadataServiceImpl;
import com.wso2.openbanking.accelerator.common.identity.retriever.sp.CommonServiceProviderRetriever;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.accelerator.consent.extensions.validate.model.ConsentValidateData;
import com.wso2.openbanking.accelerator.consent.extensions.validate.model.ConsentValidationResult;
import com.wso2.openbanking.accelerator.consent.extensions.validate.model.ConsentValidator;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentMappingResource;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.common.error.handling.util.ErrorConstants;
import com.wso2.openbanking.cds.common.metadata.domain.MetadataValidationResponse;
import com.wso2.openbanking.cds.common.metadata.status.validator.service.MetadataService;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import com.wso2.openbanking.cds.consent.extensions.validate.utils.CDSConsentValidatorUtil;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.wso2.openbanking.accelerator.consent.mgt.service.constants.ConsentCoreServiceConstants.INACTIVE_MAPPING_STATUS;

/**
 * Consent validator CDS implementation.
 */
public class CDSConsentValidator implements ConsentValidator {

    private static final Log log = LogFactory.getLog(CDSConsentValidator.class);
    AccountMetadataService accountMetadataService = AccountMetadataServiceImpl.getInstance();

    @Override
    public void validate(ConsentValidateData consentValidateData, ConsentValidationResult consentValidationResult)
            throws ConsentException {

        JSONObject receiptJSON;
        try {
            receiptJSON = (JSONObject) (new JSONParser(JSONParser.MODE_PERMISSIVE)).
                    parse(consentValidateData.getComprehensiveConsent().getReceipt());
        } catch (ParseException e) {
            throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                    "Error occurred while parsing consent data");
        }

        // consent status validation
        if (!CDSConsentExtensionConstants.AUTHORIZED_STATUS
                .equalsIgnoreCase(consentValidateData.getComprehensiveConsent().getCurrentStatus())) {
            consentValidationResult.setErrorMessage(generateErrorPayload("Consent Is Revoked",
                    "The consumer's consent is revoked", null, null));
            consentValidationResult.setErrorCode(ErrorConstants.REVOKED_CONSENT_STATUS);
            consentValidationResult.setHttpCode(HttpStatus.SC_FORBIDDEN);
            return;
        }

        // consent expiry validation
        if (CDSConsentValidatorUtil
                .isConsentExpired(((JSONObject) receiptJSON.get(CDSConsentExtensionConstants.ACCOUNT_DATA))
                        .getAsString(CDSConsentExtensionConstants.EXPIRATION_DATE_TIME))) {
            String description = "The associated consent for resource is not in a status " +
                    "that would allow the resource to be executed";
            consentValidationResult.setErrorMessage(generateErrorPayload("Consent Is Invalid", description,
                    null, null));
            consentValidationResult.setErrorCode(ErrorConstants.INVALID_CONSENT_STATUS);
            consentValidationResult.setHttpCode(HttpStatus.SC_FORBIDDEN);
            return;
        }

        // account ID Validation
        String isAccountIdValidationEnabled = OpenBankingCDSConfigParser.getInstance().getConfiguration()
                .get(CDSConsentExtensionConstants.ENABLE_ACCOUNT_ID_VALIDATION_ON_RETRIEVAL).toString();

        if (Boolean.parseBoolean(isAccountIdValidationEnabled) &&
                !CDSConsentValidatorUtil.isAccountIdValid(consentValidateData)) {

            ArrayList<String> requestPathResources = new ArrayList<>(Arrays.asList(consentValidateData.
                    getRequestPath().split("/")));
            int indexOfAccountID = requestPathResources.indexOf("{accountId}");
            String accountId = new ArrayList<>(Arrays.asList(consentValidateData.getResourceParams()
                    .get("ResourcePath").split("/"))).get(indexOfAccountID);

            consentValidationResult.setErrorMessage(generateErrorPayload("Invalid Banking Account",
                    "ID of the account not found or invalid", null, accountId));
            consentValidationResult.setErrorCode(ErrorConstants.RESOURCE_INVALID_BANKING_ACCOUNT);
            consentValidationResult.setHttpCode(HttpStatus.SC_NOT_FOUND);
            return;
        }

        // validate requested account ids for POST calls
        String httpMethod = consentValidateData.getResourceParams().get(CDSConsentExtensionConstants.HTTP_METHOD);
        if (CDSConsentExtensionConstants.POST_METHOD.equals(httpMethod)) {

            String validationResult = CDSConsentValidatorUtil.validAccountIdsInPostRequest(consentValidateData);
            if (!ErrorConstants.SUCCESS.equals(validationResult)) {
                consentValidationResult.setErrorMessage(generateErrorPayload("Invalid Banking Account",
                        "ID of the account not found or invalid", null, validationResult));
                consentValidationResult.setErrorCode(ErrorConstants.RESOURCE_INVALID_BANKING_ACCOUNT);
                consentValidationResult.setHttpCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
                return;
            }
        }

        // validate metadata status
        if (OpenBankingCDSConfigParser.getInstance().isMetadataCacheEnabled()) {
            MetadataValidationResponse metadataValidationResp =
                    MetadataService.shouldDiscloseCDRData(consentValidateData.getClientId());
            if (!metadataValidationResp.isValid()) {
                consentValidationResult.setErrorMessage(generateErrorPayload("ADR Status Is Not Active",
                        metadataValidationResp.getErrorMessage(), null, null));
                consentValidationResult.setErrorCode(ErrorConstants.AUErrorEnum.INVALID_ADR_STATUS.getCode());
                consentValidationResult.setHttpCode(ErrorConstants.AUErrorEnum.INVALID_ADR_STATUS.getHttpCode());
                return;
            }
        }

        // remove inactive and duplicate consent mappings
        removeInactiveAndDuplicateConsentMappings(consentValidateData);

        consentValidationResult.setValid(true);
    }

    /**
     * Method to remove inactive and duplicate consent mappings from consentValidateData.
     *
     * @param consentValidateData consentValidateData
     */
    private void removeInactiveAndDuplicateConsentMappings(ConsentValidateData consentValidateData) {
        ArrayList<ConsentMappingResource> distinctMappingResources = new ArrayList<>();
        List<String> duplicateAccountIds = new ArrayList<>();

        consentValidateData.getComprehensiveConsent().getConsentMappingResources().stream()
                .filter(mapping -> !INACTIVE_MAPPING_STATUS.equals(mapping.getMappingStatus()))
                .filter(mapping -> !duplicateAccountIds.contains(mapping.getAccountID()))
                .forEach(distinctMapping -> {
                    duplicateAccountIds.add(distinctMapping.getAccountID());
                    distinctMappingResources.add(distinctMapping);
                });

        // Filter accounts based on the sharing status of legal entity
        try {
            CommonServiceProviderRetriever commonServiceProviderRetriever = new CommonServiceProviderRetriever();


            String secondaryUserId = consentValidateData.getUserId().replace("@carbon.super", "");
            String legalEntityId = commonServiceProviderRetriever.
                    getAppPropertyFromSPMetaData(consentValidateData.getClientId(), "legal_entity_id");


            for (ConsentMappingResource consentMappingResource : consentValidateData.
                    getComprehensiveConsent().getConsentMappingResources()) {
                log.info(consentMappingResource.getAccountID());
            }


//            String responseLegalEntities = accountMetadataService.getAccountMetadataByKey
//                    (accountId, secondaryUserId, "BLOCKED_LEGAL_ENTITIES");


            log.info("----- PAUSE -----");
        } catch (Exception e) {
            log.info(e.getMessage());
        }


        consentValidateData.getComprehensiveConsent().setConsentMappingResources(distinctMappingResources);
    }

    private String generateErrorPayload(String title, String detail, String metaURN, String accountId) {

        JSONObject errorPayload = new JSONObject();
        errorPayload.put(ErrorConstants.DETAIL, detail);
        errorPayload.put(ErrorConstants.TITLE, title);

        if (StringUtils.isNotBlank(metaURN)) {
            errorPayload.put(ErrorConstants.META_URN, metaURN);
        }
        if (StringUtils.isNotBlank(accountId)) {
            errorPayload.put(ErrorConstants.ACCOUNT_ID, accountId);
        }
        return errorPayload.toString();
    }
}
