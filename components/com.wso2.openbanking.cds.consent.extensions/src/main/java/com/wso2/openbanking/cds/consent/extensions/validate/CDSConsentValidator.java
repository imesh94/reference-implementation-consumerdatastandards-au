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

import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.accelerator.consent.extensions.validate.model.ConsentValidateData;
import com.wso2.openbanking.accelerator.consent.extensions.validate.model.ConsentValidationResult;
import com.wso2.openbanking.accelerator.consent.extensions.validate.model.ConsentValidator;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentMappingResource;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import com.wso2.openbanking.cds.consent.extensions.validate.utils.CDSConsentValidatorUtil;
import edu.emory.mathcs.backport.java.util.Arrays;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Consent validator CDS implementation.
 */
public class CDSConsentValidator implements ConsentValidator {

    private static final Log log = LogFactory.getLog(CDSConsentValidator.class);

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

        // perform URI validation.
        String uri = consentValidateData.getRequestPath();
        if (StringUtils.isBlank(uri) && !CDSConsentValidatorUtil.isAccountURIValid(uri)) {
            consentValidationResult.setErrorMessage("Path requested is invalid.");
            consentValidationResult.setErrorCode("0013");
            consentValidationResult.setHttpCode(HttpStatus.SC_UNAUTHORIZED);
            return;
        }

        // consent status validation
        if (CDSConsentExtensionConstants.AUTHORIZED_STATUS
                .equalsIgnoreCase(consentValidateData.getComprehensiveConsent().getCurrentStatus())) {
            consentValidationResult.setErrorMessage("Account validation failed due to invalid consent state.");
            consentValidationResult.setErrorCode("00012");
            consentValidationResult.setHttpCode(HttpStatus.SC_UNAUTHORIZED);
            return;
        }

        // consent expiry validation
        if (CDSConsentValidatorUtil
                .isConsentExpired(((JSONObject) receiptJSON.get(CDSConsentExtensionConstants.ACCOUNT_DATA))
                        .getAsString(CDSConsentExtensionConstants.EXPIRATION_DATE_TIME))) {
            consentValidationResult.setErrorMessage("Account validation failed due to expired consent.");
            consentValidationResult.setErrorCode("00011");
            consentValidationResult.setHttpCode(HttpStatus.SC_BAD_REQUEST);
            return;
        }

        // validate requested account ids for POST calls
        if (consentValidateData.getPayload() != null) {

            List<String> requestedAccountsList = Arrays.asList(((JSONArray)(((JSONObject) consentValidateData
                    .getPayload().get("data"))).get("accountIds")).toArray());

            if (!requestedAccountsList.isEmpty()) {
                List<String> consentedAccountsList = new ArrayList<>();
                List<ConsentMappingResource> consentMappingResources = consentValidateData
                        .getComprehensiveConsent().getConsentMappingResources();

                for (ConsentMappingResource resource : consentMappingResources) {
                    consentedAccountsList.add(resource.getAccountID());
                }
                if (!consentedAccountsList.containsAll(requestedAccountsList)) {
                    consentValidationResult.setErrorMessage("Invalid Banking Account");
                    consentValidationResult.setErrorCode("AU.CDR.Resource.InvalidBankingAccount");
                    consentValidationResult.setHttpCode(422);
                    return;
                }
            }
        }
        consentValidationResult.setValid(true);
    }
}
