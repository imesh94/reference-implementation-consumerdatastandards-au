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

package com.wso2.openbanking.cds.consent.extensions.admin.impl;

import com.wso2.openbanking.accelerator.common.exception.ConsentManagementException;
import com.wso2.openbanking.accelerator.consent.extensions.admin.impl.DefaultConsentAdminHandler;
import com.wso2.openbanking.accelerator.consent.extensions.admin.model.ConsentAdminData;
import com.wso2.openbanking.accelerator.consent.extensions.admin.model.ConsentAdminHandler;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.AuthorizationResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentMappingResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.DetailedConsentResource;
import com.wso2.openbanking.accelerator.consent.mgt.service.ConsentCoreService;
import com.wso2.openbanking.accelerator.consent.mgt.service.constants.ConsentCoreServiceConstants;
import com.wso2.openbanking.accelerator.consent.mgt.service.impl.ConsentCoreServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Map;

import static com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants.AUTH_RESOURCE_TYPE_PRIMARY;
import static com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants.CONSENT_STATUS_REVOKED;

/**
 * Consent admin handler CDS implementation.
 */
public class CDSConsentAdminHandler implements ConsentAdminHandler {
    protected static final String CONSENT_ID = "consentID";
    protected static final String USER_ID = "userID";
    private static final Log log = LogFactory.getLog(CDSConsentAdminHandler.class);
    private final ConsentCoreService consentCoreService;
    private final ConsentAdminHandler defaultConsentAdminHandler;

    public CDSConsentAdminHandler() {
        this.consentCoreService = new ConsentCoreServiceImpl();
        this.defaultConsentAdminHandler = new DefaultConsentAdminHandler();
    }

    public CDSConsentAdminHandler(ConsentCoreService consentCoreService, ConsentAdminHandler consentAdminHandler) {
        this.consentCoreService = consentCoreService;
        this.defaultConsentAdminHandler = consentAdminHandler;
    }

    @Override
    public void handleSearch(ConsentAdminData consentAdminData) throws ConsentException {
        this.defaultConsentAdminHandler.handleSearch(consentAdminData);
    }

    @Override
    public void handleRevoke(ConsentAdminData consentAdminData) throws ConsentException {
        try {
            Map queryParams = consentAdminData.getQueryParams();
            final String consentID = validateAndGetQueryParam(queryParams, CONSENT_ID);

            if (StringUtils.isBlank(consentID)) {
                throw new ConsentException(ResponseStatus.BAD_REQUEST, "Mandatory parameter consent ID not available");
            } else {
                final String userID = validateAndGetQueryParam(queryParams, USER_ID);
                DetailedConsentResource detailedConsentResource = this.consentCoreService.getDetailedConsent(consentID);
                if (detailedConsentResource != null) {
                    if (StringUtils.isNotBlank(userID) && !isPrimaryUserRevoking(detailedConsentResource, userID)) {
                        // Deactivate consent mappings as secondary consent holder
                        deactivateAccountMappings(detailedConsentResource, userID);
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Revoke consent for consentID " + consentID);
                        }
                        // Revoke consent as primary consent holder
                        revokeConsentAsPrimaryUser(consentID);
                    }
                }
            }
            consentAdminData.setResponseStatus(ResponseStatus.NO_CONTENT);
        } catch (ConsentManagementException e) {
            throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                    "Exception occurred while revoking consents");
        }
    }

    private String validateAndGetQueryParam(Map queryParams, String key) {
        if (queryParams.containsKey(key) && (((ArrayList) queryParams.get(key)).get(0) instanceof String)) {
            return (String) ((ArrayList) queryParams.get(key)).get(0);
        }
        return null;
    }

    /**
     * Method to check requested user's authorization resource type is primary.
     *
     * @param detailedConsentResource detailedConsentResource
     * @param userID                  user id
     * @return true if user's authorization resource type is primary
     */
    private boolean isPrimaryUserRevoking(DetailedConsentResource detailedConsentResource, String userID) {
        for (AuthorizationResource authorizationResource : detailedConsentResource.getAuthorizationResources()) {
            if (userID.equals(authorizationResource.getUserID())) {
                return AUTH_RESOURCE_TYPE_PRIMARY.equals(authorizationResource.getAuthorizationType());
            }
        }
        return true;
    }

    /**
     * Method to deactivate account mapping for secondary consent holders.
     *
     * @param detailedConsentResource detailedConsentResource
     * @param secondaryUserID         id of the secondary consent holder
     * @throws ConsentManagementException when deactivate account mappings fail
     */
    private void deactivateAccountMappings(DetailedConsentResource detailedConsentResource, String secondaryUserID)
            throws ConsentManagementException {
        ArrayList<String> mappingIds = new ArrayList<>();
        ArrayList<AuthorizationResource> authorizationResources = detailedConsentResource.getAuthorizationResources();
        ArrayList<ConsentMappingResource> consentMappings = detailedConsentResource.getConsentMappingResources();

        authorizationResources.stream()
                .filter(consentAuthResource -> secondaryUserID.equals(consentAuthResource.getUserID()))
                .findFirst()
                .ifPresent(auth ->
                        // an Auth Resource found for secondary consent holder
                        consentMappings.stream()
                                .filter(mapping -> mapping.getAuthorizationID().equals(auth.getAuthorizationID()))
                                .map(ConsentMappingResource::getAccountID)
                                .forEach(secondaryAccountID ->
                                        // add every mapping resource that has secondary user's accountID to mappingIds
                                        consentMappings.stream()
                                                .filter(resource -> secondaryAccountID.equals(resource.getAccountID()))
                                                .map(ConsentMappingResource::getMappingID)
                                                .forEach(mappingIds::add)
                                )
                );

        if (!mappingIds.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Deactivate consent mapping accounts for consentID %s and userID %s",
                        detailedConsentResource.getConsentID(), secondaryUserID));
            }

            this.consentCoreService.deactivateAccountMappings(mappingIds);
        }
    }

    private void revokeConsentAsPrimaryUser(String consentID) throws ConsentManagementException {
        this.consentCoreService.revokeConsentWithReason(consentID, CONSENT_STATUS_REVOKED, null,
                ConsentCoreServiceConstants.CONSENT_REVOKE_FROM_DASHBOARD_REASON);
    }
}
