/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.identity.metadata.periodical.updater.internal;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.common.util.Generated;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.common.metadata.periodical.updater.MetadataHolder;
import com.wso2.openbanking.cds.common.metadata.periodical.updater.constants.MetadataConstants;
import com.wso2.openbanking.cds.identity.metadata.periodical.updater.service.dataholder.responsibility.CleanupRegistrationResponsibility;
import com.wso2.openbanking.cds.identity.metadata.periodical.updater.service.dataholder.responsibility.DataHolderResponsibilitiesExecutor;
import com.wso2.openbanking.cds.identity.metadata.periodical.updater.service.dataholder.responsibility.InvalidateConsentsResponsibility;
import com.wso2.openbanking.cds.identity.metadata.periodical.updater.service.retryer.Retryer;
import com.wso2.openbanking.cds.identity.metadata.periodical.updater.utils.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.wso2.carbon.identity.application.common.IdentityApplicationManagementException;
import org.wso2.carbon.identity.application.common.model.InboundAuthenticationRequestConfig;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.application.common.model.ServiceProviderProperty;
import org.wso2.carbon.user.core.UserStoreException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.wso2.openbanking.cds.common.metadata.periodical.updater.constants.MetadataConstants.DR_JSON_BRANDS;
import static com.wso2.openbanking.cds.common.metadata.periodical.updater.constants.MetadataConstants.DR_JSON_LEGAL_ENTITY_ID;
import static com.wso2.openbanking.cds.common.metadata.periodical.updater.constants.MetadataConstants.DR_JSON_SOFTWARE_PRODUCTS;
import static com.wso2.openbanking.cds.common.metadata.periodical.updater.constants.MetadataConstants.DR_JSON_SP_KEY;
import static com.wso2.openbanking.cds.common.metadata.periodical.updater.constants.MetadataConstants.DR_JSON_STATUS;
import static com.wso2.openbanking.cds.common.metadata.periodical.updater.constants.MetadataConstants.LEGAL_ENTITY_ID;
import static com.wso2.openbanking.cds.common.metadata.periodical.updater.constants.MetadataConstants.MAP_DATA_RECIPIENTS;
import static com.wso2.openbanking.cds.common.metadata.periodical.updater.constants.MetadataConstants.MAP_SOFTWARE_PRODUCTS;
import static com.wso2.openbanking.cds.common.metadata.periodical.updater.constants.MetadataConstants.SOFTWARE_PRODUCT_ID;

/**
 * Scheduled Task to get data every n minutes and perform data holder responsibilities depending
 * on the dataRecipient and softwareProduct statuses.
 * <p>
 * Within KM:
 * Uses AuthenticationAdminStub admin service HTTP endpoint to get session cookies.
 * Uses IdentityApplicationManagementServiceStub admin service HTTP endpoint to get applications list.
 * Uses IdentityApplicationManagementServiceStub admin service HTTP endpoint to get Service Provider details.
 * <p>
 * Outside KM:
 * Uses ACCC statuses HTTP endpoint to get statuses.
 * DCR internal HTTP endpoint to delete application.
 * <p>
 * The data returned from directory is a map between softwareProductId - status / dataRecipientId - status.
 * But during authorization flow / data retrieval request, when status validation needs to be done,
 * request only contains ClientID or UserID or ApplicationName. Thus at runtime there is an
 * overhead of doing APIM Admin service call for every request to get the required application attributes,
 * softwareProductId and dataRecipientId, to validate status. To eliminate this, the
 * dataRecipientID-status / softwareProductID-status maps are converted to clientID-dataRecipientIdStatus /
 * clientID-softwareProductIdStatus maps. So the CRON job is defined as follows,
 * <p>
 * Get softwareProductId and dataRecipientId status arrays from directory.
 * Get the list of applications in store.
 * For each application name,
 * Get the KM service provider and its application attributes.
 * Get its softwareProductId and dataRecipientId
 * For each of the OAuth Client IDs of the service provider,
 * Add a new entry of Client ID - status in softwareProductStatus Map.
 * Add a new entry of Client ID - status in dataRecipientStatus Map.
 * If the softwareProductId / dataRecipientId has status that needs to cleanup registration,
 * For each of the OAuth Client IDs of the service provider,
 * Delete the application registered with the ClientID.
 * If the softwareProductId / dataRecipientId has status that needs to expire consents,
 * For each of the OAuth Client IDs of the service provider,
 * Revoke consents registered with the ClientID, UserID.
 * Return the newly created softwareProductStatus / dataRecipientStatus map.
 * Store the returned ClientID - status maps of softwareProductStatus and dataRecipientStatus in data holder.
 * <p>
 * StatusValidator will retrieve the maps in data holder when its cache expires. These maps will contain
 * statuses of dataRecipient and softwareProduct against clientID, but only for the applications
 * found in store, not the complete status list retrieved from directory. Complete list is dropped during the Job.
 * It won't affect the new registrations, as in DCR, status check is not performed.
 */
@DisallowConcurrentExecution
public class PeriodicalMetaDataUpdateJob implements Job, MetaDataUpdate {

    private static final Log LOG = LogFactory.getLog(PeriodicalMetaDataUpdateJob.class);

    /**
     * method used to enforce periodic metadata update
     *
     * @param context JobExecutionContext
     */
    @Override
    public void execute(JobExecutionContext context) {
        updateMetaData();
    }

    /**
     * Method which triggers metadata update
     */
    @Override
    @Generated(message = "Ignoring since all cases are covered from other unit tests")
    public void updateMetaData() {

        LOG.debug("Metadata Scheduled Task is executing.");

        Map<String, Map<String, String>> metaDataStatuses;
        try {
            Retryer<JSONObject> retryer = new Retryer<>(1000, OpenBankingCDSConfigParser.getInstance().getRetryCount());
            JSONObject responseJson = retryer.execute(() -> Utils
                    .readJsonFromUrl(OpenBankingCDSConfigParser.getInstance().getDataRecipientsDiscoveryUrl()));

            if (responseJson == null) {
                // CDR response is null, possible because Common HttpPool is not initialized yet.
                return;
            }
            metaDataStatuses = getDataRecipientsStatusesFromRegister(responseJson);

        } catch (OpenBankingException e) {
            /*
             * Continue to operate with existing metadata as per CDR expectations
             * https://cdr-register.github.io/register/#cdr-register-unavailable
             */
            LOG.error("Error while getting statuses from directory. " +
                    "Continue to operate from existing metadata. Caused by, ", e);
            return;
        }

        // Perform responsibilities and set data only relevant to our store to holders
        try {
            Map<String, Map<String, String>> metaDataStatusMapsInStore = processMetadataStatus(metaDataStatuses
                    .get(MAP_DATA_RECIPIENTS), metaDataStatuses.get(MAP_SOFTWARE_PRODUCTS));

            // updating primary cache
            MetadataHolder.getInstance().setSoftwareProduct(metaDataStatusMapsInStore.get(MAP_SOFTWARE_PRODUCTS));
            MetadataHolder.getInstance().setDataRecipient(metaDataStatusMapsInStore.get(MAP_DATA_RECIPIENTS));

            if (!OpenBankingCDSConfigParser.getInstance().isBulkOperation()) {
                DataHolderResponsibilitiesExecutor.getInstance().execute();
            }
        } catch (OpenBankingException e) {
            LOG.error("Data holder responsibilities were not performed successfully", e);
        }

        LOG.debug("Metadata Scheduled Task is finished.");
    }

    /**
     * Get software product statuses map from ACCC registry response.
     *
     * @return map of software product IDs and statuses
     */
    protected Map<String, String> getSoftwareProducts(@NotNull JSONObject dataRecipient) {

        JSONArray dataRecipientBrands = dataRecipient.getJSONArray(DR_JSON_BRANDS);

        Comparator<String> caseInsensitiveNullsFirstComparator = Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER);
        Map<String, String> softwareProductsMap = new TreeMap<>(caseInsensitiveNullsFirstComparator);

        if (dataRecipientBrands != null) {
            for (int i = 0; i < dataRecipientBrands.length(); i++) {
                JSONObject dataRecipientBrand = dataRecipientBrands.getJSONObject(i);
                JSONArray softwareProducts = dataRecipientBrand.getJSONArray(DR_JSON_SOFTWARE_PRODUCTS);
                for (int j = 0; j < softwareProducts.length(); j++) {
                    JSONObject softwareProduct = softwareProducts.getJSONObject(j);
                    softwareProductsMap
                            .put(softwareProduct.getString(DR_JSON_SP_KEY), softwareProduct.getString(DR_JSON_STATUS));
                }
            }
        }

        return softwareProductsMap;
    }

    /**
     * Get data recipients statuses map from ACCC Registry.
     *
     * @return data recipients map of legalEntityIds and statuses and software products map
     */
    protected Map<String, Map<String, String>> getDataRecipientsStatusesFromRegister(@NotNull JSONObject responseJson) {

        JSONArray dataRecipientsArray = responseJson.getJSONArray(MetadataConstants.DR_JSON_ROOT);

        Comparator<String> caseInsensitiveNullsFirstComparator = Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER);
        Map<String, String> dataRecipientsMap = new TreeMap<>(caseInsensitiveNullsFirstComparator);
        Map<String, String> softwareProductsMap = new HashMap<>();

        for (int jsonElementIndex = 0; jsonElementIndex < dataRecipientsArray.length(); jsonElementIndex++) {
            JSONObject dataRecipient = dataRecipientsArray.getJSONObject(jsonElementIndex);

            dataRecipientsMap.put(dataRecipient.getString(DR_JSON_LEGAL_ENTITY_ID),
                    dataRecipient.getString(DR_JSON_STATUS));
            softwareProductsMap.putAll(getSoftwareProducts(dataRecipient));
        }

        Map<String, Map<String, String>> metaDataMap = new HashMap<>();
        metaDataMap.put(MAP_DATA_RECIPIENTS, dataRecipientsMap);
        metaDataMap.put(MAP_SOFTWARE_PRODUCTS, softwareProductsMap);

        return metaDataMap;
    }

    /**
     * This method executes two tasks,
     * task 1: Modify metadata statuses map by putting client ids
     * task 2: Add data holder responsibilities to DataHolderResponsibilitiesExecutor
     *
     * @param dataRecipientsMap   data recipients fetched from ACCC
     * @param softwareProductsMap software products fetched from ACCC
     * @return in store software products and data recipients map
     */
    protected Map<String, Map<String, String>> processMetadataStatus(Map<String, String> dataRecipientsMap,
                                                                     Map<String, String> softwareProductsMap)
            throws OpenBankingException {

        final List<ServiceProvider> serviceProviders;
        // Get service providers of all applications in store
        try {
            serviceProviders = ServiceHolder.getInstance().getIdentityCommonHelper().getAllServiceProviders();
        } catch (IdentityApplicationManagementException | UserStoreException e) {
            throw new OpenBankingException(
                    "Data holder responsibilities were not performed: service providers details was not retrieved.", e);
        }

        // Maps to store Client-ID status mapping that will be sent to secondary cache
        Map<String, String> softwareProductsMapInStore = new HashMap<>();
        Map<String, String> dataRecipientsMapInStore = new HashMap<>();

        for (ServiceProvider serviceProvider : serviceProviders) {
            String dataRecipientId = getIdFromProperties(serviceProvider.getSpProperties(), LEGAL_ENTITY_ID);
            String softwareProductId = getIdFromProperties(serviceProvider.getSpProperties(), SOFTWARE_PRODUCT_ID);

            String dataRecipientsStatus = dataRecipientsMap.get(dataRecipientId);
            String softwareProductsStatus = softwareProductsMap.get(softwareProductId);

            if (dataRecipientsStatus != null && softwareProductsStatus != null) {

                // Add ClientIDs - Status Map
                for (InboundAuthenticationRequestConfig config :
                        serviceProvider.getInboundAuthenticationConfig().getInboundAuthenticationRequestConfigs()) {
                    softwareProductsMapInStore.put(config.getInboundAuthKey(), softwareProductsStatus);
                    dataRecipientsMapInStore.put(config.getInboundAuthKey(), dataRecipientsStatus);
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Performing data holder responsibilities. dataRecipientId: " + dataRecipientId +
                            ", softwareProductId: " + softwareProductId);
                }
                // Adding responsibilities to the executor
                DataHolderResponsibilitiesExecutor.getInstance().addResponsibility(new CleanupRegistrationResponsibility
                        (dataRecipientsStatus, softwareProductsStatus, serviceProvider));
                DataHolderResponsibilitiesExecutor.getInstance().addResponsibility(new InvalidateConsentsResponsibility
                        (dataRecipientsStatus, softwareProductsStatus, serviceProvider));
            }
        }

        Map<String, Map<String, String>> metaDataStatusMap = new HashMap<>();
        metaDataStatusMap.put(MAP_DATA_RECIPIENTS, dataRecipientsMapInStore);
        metaDataStatusMap.put(MAP_SOFTWARE_PRODUCTS, softwareProductsMapInStore);

        return metaDataStatusMap;
    }

    private String getIdFromProperties(ServiceProviderProperty[] providerProperties, String displayName) {

        return Arrays.stream(providerProperties)
                .filter(property -> displayName.equalsIgnoreCase(property.getDisplayName()))
                .map(ServiceProviderProperty::getValue)
                .filter(Objects::nonNull).findFirst().orElse("");
    }
}
