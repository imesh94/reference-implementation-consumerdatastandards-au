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

package com.wso2.openbanking.cds.consent.extensions.validate.utils;

import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.accelerator.consent.extensions.validate.model.ConsentValidateData;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentMappingResource;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Consent validate util class for CDS specification.
 */
public class CDSConsentValidatorUtil {

    private static final Log log = LogFactory.getLog(CDSConsentValidatorUtil.class);

    /**
     * Util method to validate the Account request URI
     * @param uri
     * @return
     */
    public static Boolean isAccountURIValid(String uri) {
        List<String> accountPaths = getAccountAPIPathRegexArray();

        for (String entry : accountPaths) {
            if (uri.matches(entry)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Method provides API resource paths applicable for UK Account API.
     *
     * @return map of API Resources.
     */
    public static List<String> getAccountAPIPathRegexArray() {

        List<String> requestUrls = Arrays.asList(CDSConsentExtensionConstants.ACCOUNT_REGEX,
                CDSConsentExtensionConstants.BULK_BALANCES_REGEX,
                CDSConsentExtensionConstants.ACCOUNT_BALANCE_REGEX,
                CDSConsentExtensionConstants.ACCOUNT_DETAIL_REGEX,
                CDSConsentExtensionConstants.TRANSACTIONS_FOR_ACCOUNT_REGEX,
                CDSConsentExtensionConstants.TRANSACTIONS_DETAIL_REGEX,
                CDSConsentExtensionConstants.DIRECT_DEBIT_FOR_ACCOUNT_REGEX,
                CDSConsentExtensionConstants.BULK_DIRECT_DEBIT_REGEX,
                CDSConsentExtensionConstants.SCHEDULED_PAYMENT_FOR_ACCOUNT_REGEX,
                CDSConsentExtensionConstants.BULK_SCHEDULED_PAYMENT_REGEX,
                CDSConsentExtensionConstants.PAYEES_REGEX,
                CDSConsentExtensionConstants.PAYEE_DETAIL_REGEX,
                CDSConsentExtensionConstants.PRODUCTS_REGEX,
                CDSConsentExtensionConstants.PRODUCT_DETAIL_REGEX,
                CDSConsentExtensionConstants.COMMON_CUSTOMER_REGEX,
                CDSConsentExtensionConstants.COMMON_CUSTOMER_DETAIL_REGEX,
                CDSConsentExtensionConstants.DISCOVERY_STATUS_REGEX,
                CDSConsentExtensionConstants.DISCOVERY_OUTAGES_REGEX);

        return requestUrls;
    }

    /**
     * Validate whether consent is expired
     * @param expDateVal
     * @return
     * @throws ConsentException
     */
    public static Boolean isConsentExpired(String expDateVal) throws ConsentException {

        if (expDateVal != null && !expDateVal.isEmpty()) {
            try {
                OffsetDateTime expDate = OffsetDateTime.parse(expDateVal);
                return OffsetDateTime.now().isAfter(expDate);
            } catch (DateTimeParseException e) {
                log.error("Error occurred while parsing the expiration date" + " : " + expDateVal);
                throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                        "Error occurred while parsing the expiration date");
            }
        } else {
            return false;
        }

    }

    /**
     * Method to validate account ids in post request body
     * @param consentValidateData
     * @return
     */
    public static Boolean validAccountIdsInPostRequest(ConsentValidateData consentValidateData) {

        List<String> requestedAccountsList = new ArrayList<>();
        for (Object element: (ArrayList) ((JSONObject) consentValidateData.getPayload()
                .get(CDSConsentExtensionConstants.DATA)).get(CDSConsentExtensionConstants.ACCOUNT_IDS)) {
            if (element != null) {
                requestedAccountsList.add(element.toString());
            } else {
                return false;
            }
        }
        if (!requestedAccountsList.isEmpty()) {
            List<String> consentedAccountsList = new ArrayList<>();

            for (ConsentMappingResource resource : consentValidateData.getComprehensiveConsent()
                    .getConsentMappingResources()) {
                consentedAccountsList.add(resource.getAccountID());
            }
            return consentedAccountsList.containsAll(requestedAccountsList);
        }
        return false;
    }

    /**
     * Method to validate whether account id is valid
     * @param consentValidateData
     * @return
     */
    public static Boolean isAccountIdValid(ConsentValidateData consentValidateData) {

        if (!consentValidateData.getRequestPath().contains("{accountId}")) {
            return true;
        }
        String resourcePath = consentValidateData.getResourceParams().get("ResourcePath");

        for (ConsentMappingResource resource : consentValidateData.getComprehensiveConsent()
                .getConsentMappingResources()) {
            if (resourcePath.contains(resource.getAccountID())) {
                return true;
            }
        }
        return false;
    }
}
