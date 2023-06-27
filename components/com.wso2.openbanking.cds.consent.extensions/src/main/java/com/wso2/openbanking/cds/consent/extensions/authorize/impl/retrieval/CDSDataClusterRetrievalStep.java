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
package com.wso2.openbanking.cds.consent.extensions.authorize.impl.retrieval;

import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentRetrievalStep;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.CDSConsentCommonUtil;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.PermissionsEnum;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data cluster retrieval step CDS implementation to get human readable scopes.
 */
public class CDSDataClusterRetrievalStep implements ConsentRetrievalStep {

    private static final Log log = LogFactory.getLog(CDSDataClusterRetrievalStep.class);

    @Override
    public void execute(ConsentData consentData, JSONObject jsonObject) throws ConsentException {

        if (consentData.isRegulatory()) {
            if (!consentData.getMetaDataMap().containsKey(CDSConsentExtensionConstants.PERMISSIONS)) {
                log.error("Error: Scopes are not found in consent data.");
                return;
            }
            JSONArray scopes = new JSONArray();
            scopes.addAll((ArrayList) consentData.getMetaDataMap().get(CDSConsentExtensionConstants.PERMISSIONS));
            String customerType = CDSConsentCommonUtil.getCustomerType(consentData);
            // Consent amendment flow
            if (jsonObject.containsKey(CDSConsentExtensionConstants.IS_CONSENT_AMENDMENT) &&
                    (boolean) jsonObject.get(CDSConsentExtensionConstants.IS_CONSENT_AMENDMENT)) {
                //get existing scopes
                JSONArray existingScopes = new JSONArray();
                JSONArray commonScopes = new JSONArray(); //scopes that are in both old and new request objects
                JSONArray newScopes = new JSONArray();
                if (jsonObject.containsKey(CDSConsentExtensionConstants.EXISTING_PERMISSIONS)) {
                    JSONArray existingPermissions = (JSONArray) jsonObject.get(CDSConsentExtensionConstants.
                            EXISTING_PERMISSIONS);
                    for (Object permission : existingPermissions) {
                        existingScopes.add(PermissionsEnum.valueOf(permission.toString()).toString());
                    }
                    for (Object scope : scopes) {
                        if (existingScopes.contains(scope.toString())) {
                            commonScopes.add(scope);
                        } else {
                            newScopes.add(scope);
                        }
                    }
                    JSONArray dataCluster = getDataClusterFromScopes(commonScopes, customerType);
                    JSONArray newDataCluster = getDataClusterFromScopes(newScopes, customerType);
                    JSONArray businessDataCluster = getDataClusterFromScopes(commonScopes,
                            CDSConsentExtensionConstants.ORGANISATION);
                    JSONArray newBusinessDataCluster = getDataClusterFromScopes(newScopes,
                            CDSConsentExtensionConstants.ORGANISATION);
                    jsonObject.put(CDSConsentExtensionConstants.DATA_REQUESTED, dataCluster);
                    jsonObject.put(CDSConsentExtensionConstants.NEW_DATA_REQUESTED, newDataCluster);
                    jsonObject.put(CDSConsentExtensionConstants.BUSINESS_DATA_CLUSTER, businessDataCluster);
                    jsonObject.put(CDSConsentExtensionConstants.NEW_BUSINESS_DATA_CLUSTER, newBusinessDataCluster);
                } else {
                    log.error("Permissions not found for the given consent");
                    throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                            "Permissions not found for the given consent");
                }
            } else {
                JSONArray dataCluster = getDataClusterFromScopes(scopes, customerType);
                JSONArray businessDataCluster = getDataClusterFromScopes(scopes,
                        CDSConsentExtensionConstants.ORGANISATION);
                jsonObject.put(CDSConsentExtensionConstants.DATA_REQUESTED, dataCluster);
                jsonObject.put(CDSConsentExtensionConstants.BUSINESS_DATA_CLUSTER, businessDataCluster);
            }
        }
    }

    /**
     * Get data clusters mapping to the given scopes.
     *
     * @param scopes       cds scopes
     * @param customerType customer type
     * @return data cluster
     */
    private static JSONArray getDataClusterFromScopes(JSONArray scopes, String customerType) {

        JSONArray dataCluster = new JSONArray();
        for (Object scopeEnum : scopes) {
            JSONObject dataClusterItem = new JSONObject();
            String scope = scopeEnum.toString();
            if (CDSConsentExtensionConstants.COMMON_CUSTOMER_BASIC_READ_SCOPE.equalsIgnoreCase(scope) &&
                    scopes.contains(CDSConsentExtensionConstants.COMMON_CUSTOMER_DETAIL_READ_SCOPE)) {
                continue;
            } else if (CDSConsentExtensionConstants.COMMON_ACCOUNTS_BASIC_READ_SCOPE.equalsIgnoreCase(scope) &&
                    scopes.contains(CDSConsentExtensionConstants.COMMON_ACCOUNTS_DETAIL_READ_SCOPE)) {
                continue;
            }
            Map<String, List<String>> cluster;
            if (scope.contains(CDSConsentExtensionConstants.COMMON_SUBSTRING) &&
                    CDSConsentExtensionConstants.ORGANISATION.equalsIgnoreCase(customerType)) {
                cluster = CDSConsentExtensionConstants.BUSINESS_CDS_DATA_CLUSTER.get(scope);
            } else if (scope.contains(CDSConsentExtensionConstants.COMMON_SUBSTRING)) {
                cluster = CDSConsentExtensionConstants.INDIVIDUAL_CDS_DATA_CLUSTER.get(scope);
            } else if (scope.equals(CDSConsentExtensionConstants.PROFILE_SCOPE)) {
                cluster = CDSConsentExtensionConstants.PROFILE_DATA_CLUSTER.get(scope);
            } else {
                cluster = CDSConsentExtensionConstants.CDS_DATA_CLUSTER.get(scope);
            }
            if (cluster == null) {
                log.warn(String.format("No data found for scope: %s requested.", scope));
                continue;
            }
            for (Map.Entry<String, List<String>> entry : cluster.entrySet()) {
                dataClusterItem.put(CDSConsentExtensionConstants.TITLE, entry.getKey());
                JSONArray requestedData = new JSONArray();
                requestedData.addAll(entry.getValue());
                dataClusterItem.put(CDSConsentExtensionConstants.DATA, requestedData);
            }
            dataCluster.add(dataClusterItem);
        }
        return dataCluster;
    }
}
