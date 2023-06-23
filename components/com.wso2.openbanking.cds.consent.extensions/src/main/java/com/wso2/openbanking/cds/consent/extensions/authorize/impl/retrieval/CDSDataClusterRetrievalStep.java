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

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentRetrievalStep;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ResponseStatus;
import com.wso2.openbanking.accelerator.identity.util.HTTPClientUtils;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.PermissionsEnum;
import com.wso2.openbanking.cds.consent.extensions.common.CDSConsentExtensionConstants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
            String customerType = getCustomerType(consentData);
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
                    JSONArray dataCluster = getDataClusterFromScopes(commonScopes,
                            CDSConsentExtensionConstants.INDIVIDUAL_PROFILE_TYPE);
                    JSONArray newDataCluster = getDataClusterFromScopes(newScopes,
                            CDSConsentExtensionConstants.INDIVIDUAL_PROFILE_TYPE);
                    JSONArray businessDataCluster = getDataClusterFromScopes(commonScopes,
                            CDSConsentExtensionConstants.ORGANISATION);
                    JSONArray newBusinessDataCluster = getDataClusterFromScopes(newScopes,
                            CDSConsentExtensionConstants.ORGANISATION);
                    // Add profile scope and standard claim data clusters
                    JSONObject profileDataJsonObject = processProfileDataClusters(dataCluster, newDataCluster,
                            businessDataCluster, newBusinessDataCluster, commonScopes, newScopes);
                    dataCluster = (JSONArray) profileDataJsonObject.get(CDSConsentExtensionConstants.DATA_CLUSTER);
                    newDataCluster = (JSONArray) profileDataJsonObject
                            .get(CDSConsentExtensionConstants.NEW_DATA_CLUSTER);
                    businessDataCluster = (JSONArray) profileDataJsonObject
                            .get(CDSConsentExtensionConstants.BUSINESS_DATA_CLUSTER);
                    newBusinessDataCluster = (JSONArray) profileDataJsonObject
                            .get(CDSConsentExtensionConstants.NEW_BUSINESS_DATA_CLUSTER);
                    jsonObject.put(CDSConsentExtensionConstants.DATA_REQUESTED, dataCluster);
                    jsonObject.put(CDSConsentExtensionConstants.NEW_DATA_REQUESTED, newDataCluster);
                    jsonObject.put(CDSConsentExtensionConstants.BUSINESS_DATA_CLUSTER, businessDataCluster);
                    jsonObject.put(CDSConsentExtensionConstants.NEW_BUSINESS_DATA_CLUSTER, newBusinessDataCluster);
                    jsonObject.put(CDSConsentExtensionConstants.NAME_CLAIMS,
                            profileDataJsonObject.get(CDSConsentExtensionConstants.NAME_CLAIMS));
                    jsonObject.put(CDSConsentExtensionConstants.CONTACT_CLAIMS,
                            profileDataJsonObject.get(CDSConsentExtensionConstants.CONTACT_CLAIMS));
                } else {
                    log.error("Permissions not found for the given consent");
                    throw new ConsentException(ResponseStatus.INTERNAL_SERVER_ERROR,
                            "Permissions not found for the given consent");
                }
            } else {
                JSONArray dataCluster = getDataClusterFromScopes(scopes,
                        CDSConsentExtensionConstants.INDIVIDUAL_PROFILE_TYPE);
                JSONArray businessDataCluster = getDataClusterFromScopes(scopes,
                        CDSConsentExtensionConstants.ORGANISATION);
                // Add profile scope and standard claim data clusters
                JSONObject processedDataClusters = processProfileDataClusters(new JSONArray(), dataCluster,
                        new JSONArray(), businessDataCluster, new JSONArray(), scopes);
                dataCluster = (JSONArray) processedDataClusters.get(CDSConsentExtensionConstants.NEW_DATA_CLUSTER);
                businessDataCluster = (JSONArray) processedDataClusters
                        .get(CDSConsentExtensionConstants.NEW_BUSINESS_DATA_CLUSTER);
                jsonObject.put(CDSConsentExtensionConstants.DATA_REQUESTED, dataCluster);
                jsonObject.put(CDSConsentExtensionConstants.BUSINESS_DATA_CLUSTER, businessDataCluster);
            }
        }
    }

    private static String getCustomerType(ConsentData consentData) {

        if (CDSConsentExtensionConstants.TRUE.equalsIgnoreCase(OpenBankingCDSConfigParser.getInstance()
                .getConfiguration().get(CDSConsentExtensionConstants.ENABLE_CUSTOMER_DETAILS).toString())) {

            String customerEPURL = OpenBankingCDSConfigParser.getInstance().getConfiguration()
                    .get(CDSConsentExtensionConstants.CUSTOMER_DETAILS_RETRIEVE_ENDPOINT).toString();

            if (StringUtils.isNotBlank(customerEPURL)) {
                String customerDetails = getCustomerFromEndpoint(customerEPURL, consentData.getUserId());
                if (StringUtils.isNotBlank(customerDetails)) {
                    try {
                        JSONObject customerDetailsJson = (JSONObject) new JSONParser(JSONParser.MODE_PERMISSIVE)
                                .parse(customerDetails);
                        return customerDetailsJson.get(CDSConsentExtensionConstants.CUSTOMER_TYPE).toString();
                    } catch (ParseException e) {
                        if (log.isDebugEnabled()) {
                            log.debug("Unable to load customer data for the customer: " + consentData.getUserId());
                        }
                        return CDSConsentExtensionConstants.ORGANISATION;
                    }
                }
            }
        }
        return CDSConsentExtensionConstants.ORGANISATION;
    }

    private static String getCustomerFromEndpoint(String customerDetailsUrl, String user) {
        user = user.substring(0, user.lastIndexOf("@"));
        String url = customerDetailsUrl.replace("{userId}", user);
        if (log.isDebugEnabled()) {
            log.debug("Customer Details endpoint : " + url);
        }

        try (CloseableHttpClient client = HTTPClientUtils.getHttpsClient()) {
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
                log.error("Retrieving customer details failed");
                return null;
            } else {
                InputStream in = response.getEntity().getContent();
                return IOUtils.toString(in, String.valueOf(StandardCharsets.UTF_8));
            }
        } catch (IOException | OpenBankingException e) {
            log.error("Exception occurred while retrieving sharable accounts", e);
        }
        return null;
    }

    /**
     * Get data clusters mapping to the given scopes.
     *
     * @param scopes cds scopes
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

    /**
     * Get profile data clusters for the given scopes.
     *
     * @param dataCluster            previous data cluster
     * @param newDataCluster         new data cluster
     * @param businessDataCluster    previous business data cluster
     * @param newBusinessDataCluster new business data cluster
     * @param previousScopes         previous scopes array
     * @param newScopes              new scopes array
     * @return modified data clusters with profile data
     */
    private static JSONObject processProfileDataClusters(JSONArray dataCluster, JSONArray
            newDataCluster, JSONArray businessDataCluster, JSONArray
                                                                 newBusinessDataCluster, JSONArray previousScopes,
                                                         JSONArray newScopes) {

        ArrayList<String> newlyRequestedProfileClaims = new ArrayList<>();
        ArrayList<String> previouslyRequestedProfileClaims = new ArrayList<>();
        ArrayList<String> nameClaims = new ArrayList<>();
        ArrayList<String> contactClaims = new ArrayList<>();

        // Processing name cluster related scopes and claims.
        for (String claim : CDSConsentExtensionConstants.NAME_CLUSTER_PERMISSIONS) {
            if (newScopes.contains(PermissionsEnum.fromValue(claim))) {
                newlyRequestedProfileClaims.add(CDSConsentExtensionConstants.NAME_CLUSTER);
                // Remove previous name data clusters if exists.
                previouslyRequestedProfileClaims.remove(CDSConsentExtensionConstants.NAME_CLUSTER);
                break;
            } else if (!previouslyRequestedProfileClaims.contains(CDSConsentExtensionConstants.NAME_CLUSTER) &&
                    previousScopes.contains(PermissionsEnum.fromValue(claim))) {
                previouslyRequestedProfileClaims.add(CDSConsentExtensionConstants.NAME_CLUSTER);
            }
        }
        //Add all requested claims related to name cluster for consent amendment UI display
        for (String claim : CDSConsentExtensionConstants.NAME_CLUSTER_PERMISSIONS) {
            // Add all requested claims related to name cluster for consent amendment UI display
            if (previousScopes.contains(PermissionsEnum.fromValue(claim)) ||
                    newScopes.contains(PermissionsEnum.fromValue(claim))) {
                nameClaims.add(claim);
            }
        }

        // Processing contact cluster related claims.
        ArrayList<String> newContactPermissions = new ArrayList<>();
        ArrayList<String> previousContactPermissions = new ArrayList<>();
        for (Map.Entry<String, String[]> cluster : CDSConsentExtensionConstants.CONTACT_CLUSTER_CLAIMS.entrySet()) {
            String clusterName = cluster.getKey();
            String[] clusterClaims = cluster.getValue();
            for (String claim : clusterClaims) {
                if (newScopes.contains(PermissionsEnum.fromValue(claim))) {
                    newContactPermissions.add(clusterName);
                    break;
                } else if (previousScopes.contains(PermissionsEnum.fromValue(claim))) {
                    previousContactPermissions.add(clusterName);
                }
            }
            // Add all requested claims related to contact cluster for consent amendment UI display
            for (String claim : clusterClaims) {
                if (previousScopes.contains(PermissionsEnum.fromValue(claim)) ||
                        newScopes.contains(PermissionsEnum.fromValue(claim))) {
                    contactClaims.add(claim);
                }
            }
        }

        if (newContactPermissions.size() > 0) {
            newContactPermissions.addAll(previousContactPermissions);
            newlyRequestedProfileClaims.add(processContactClaims(newContactPermissions));
        } else if (previousContactPermissions.size() > 0) {
            previouslyRequestedProfileClaims.add(processContactClaims(previousContactPermissions));
        }

        Map<String, JSONArray> newDataClusters =
                addProfileDataClusters(newlyRequestedProfileClaims, newDataCluster, newBusinessDataCluster);
        Map<String, JSONArray> previousDataClusters =
                addProfileDataClusters(previouslyRequestedProfileClaims, dataCluster, businessDataCluster);

        JSONObject profileDataObject = new JSONObject();
        profileDataObject.put(CDSConsentExtensionConstants.DATA_CLUSTER,
                previousDataClusters.get(CDSConsentExtensionConstants.DATA_CLUSTER));
        profileDataObject.put(CDSConsentExtensionConstants.NEW_DATA_CLUSTER,
                newDataClusters.get(CDSConsentExtensionConstants.DATA_CLUSTER));
        profileDataObject.put(CDSConsentExtensionConstants.BUSINESS_DATA_CLUSTER,
                previousDataClusters.get(CDSConsentExtensionConstants.BUSINESS_DATA_CLUSTER));
        profileDataObject.put(CDSConsentExtensionConstants.NEW_BUSINESS_DATA_CLUSTER,
                newDataClusters.get(CDSConsentExtensionConstants.BUSINESS_DATA_CLUSTER));
        profileDataObject.put(CDSConsentExtensionConstants.NAME_CLAIMS, String.join(", ", nameClaims));
        profileDataObject.put(CDSConsentExtensionConstants.CONTACT_CLAIMS, String.join(", ", contactClaims));

        return profileDataObject;
    }

    /**
     * Process contact claims and build the contact cluster name.
     * @param contactPermissions contact permissions
     * @return contact cluster name
     */
    private static String processContactClaims(ArrayList<String> contactPermissions) {

        HashSet<String> contactPermissionSet = new HashSet<>(contactPermissions);
        Object[] permissionsArray = contactPermissionSet.toArray();
        Arrays.sort(permissionsArray);
        StringBuilder clusterName = new StringBuilder(CDSConsentExtensionConstants.CONTACT_CLUSTER);

        for (Object permission : permissionsArray) {
            clusterName.append("_");
            clusterName.append(permission.toString());
        }
        return String.valueOf(clusterName);
    }

    /**
     * Add profile scope and other standard claims related data clusters for the provided claims.
     * @param profileClaims profile claims
     * @param individualDataCluster individual customer data cluster
     * @param businessDataCluster business customer data cluster
     * @return map of individual and business data clusters
     */
    private static Map<String, JSONArray> addProfileDataClusters(ArrayList<String> profileClaims,
                                                                 JSONArray individualDataCluster,
                                                                 JSONArray businessDataCluster) {

        for (String claimCluster : profileClaims) {
            Map<String, List<String>> cluster =
                    CDSConsentExtensionConstants.PROFILE_DATA_CLUSTER.get(claimCluster);
            if (cluster == null) {
                log.warn(String.format("No data found for profile scope claim: %s requested.", claimCluster));
            } else {
                JSONObject dataClusterItem = new JSONObject();
                for (Map.Entry<String, List<String>> entry : cluster.entrySet()) {
                    dataClusterItem.put(CDSConsentExtensionConstants.TITLE, entry.getKey());
                    JSONArray requestedData = new JSONArray();
                    requestedData.addAll(entry.getValue());
                    dataClusterItem.put(CDSConsentExtensionConstants.DATA, requestedData);
                }

                individualDataCluster.add(dataClusterItem);
                businessDataCluster.add(dataClusterItem);
            }
        }
        Map<String, JSONArray> dataClusters = new HashMap<>();
        dataClusters.put(CDSConsentExtensionConstants.DATA_CLUSTER, individualDataCluster);
        dataClusters.put(CDSConsentExtensionConstants.BUSINESS_DATA_CLUSTER, businessDataCluster);

        return dataClusters;
    }
}
