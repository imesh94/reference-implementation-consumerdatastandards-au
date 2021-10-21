/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 *
 */

package com.wso2.openbanking.cds.identity.metadata.periodical.updater.internal;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonHelper;
import com.wso2.openbanking.cds.common.metadata.periodical.updater.constants.MetadataConstants;
import com.wso2.openbanking.cds.common.metadata.periodical.updater.utils.DataRecipientStatusEnum;
import com.wso2.openbanking.cds.common.metadata.periodical.updater.utils.SoftwareProductStatusEnum;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.application.common.IdentityApplicationManagementException;
import org.wso2.carbon.identity.application.common.model.InboundAuthenticationConfig;
import org.wso2.carbon.identity.application.common.model.InboundAuthenticationRequestConfig;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.application.common.model.ServiceProviderProperty;
import org.wso2.carbon.user.core.UserStoreException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.wso2.openbanking.cds.common.metadata.periodical.updater.constants.MetadataConstants.MAP_DATA_RECIPIENTS;
import static com.wso2.openbanking.cds.common.metadata.periodical.updater.constants.MetadataConstants.MAP_SOFTWARE_PRODUCTS;

/**
 * Test class for PeriodicalMetaDataUpdateJob
 */
@PrepareForTest({ServiceHolder.class})
public class PeriodicalMetaDataUpdateJobTest extends PowerMockTestCase {

    private static final String DUMMY_SOFTWARE_PRODUCT_ID_1 = "af9f578f-3d96-ea11-a831-000d3a8842e1";
    private static final String DUMMY_SOFTWARE_PRODUCT_ID_2 = "12316470-f7ae-eb11-a822-000d3a884a20";
    private static final String DUMMY_SOFTWARE_PRODUCT_ID_3 = "3051b3ab-4096-ea11-a831-000d3a8842e1";
    private static final String DUMMY_SOFTWARE_PRODUCT_ID_4 = "469811b0-90d8-eb11-a824-000d3a884a20";

    private static final String DUMMY_LEGAL_ENTITY_ID_1 = "379f578f-3d96-ea11-a831-000d3a8842e1";
    private static final String DUMMY_LEGAL_ENTITY_ID_2 = "b850b3ab-4096-ea11-a831-000d3a8842e1";

    private JSONObject responseJson;
    private JSONObject dataRecipient;
    private PeriodicalMetaDataUpdateJob uut;

    @BeforeClass
    public void init() {

        this.uut = new PeriodicalMetaDataUpdateJob();

        // creating dummy data response
        JSONArray softwareProducts1 = new JSONArray();
        softwareProducts1.put(getSoftwareProduct(DUMMY_SOFTWARE_PRODUCT_ID_1, SoftwareProductStatusEnum.ACTIVE));
        softwareProducts1.put(getSoftwareProduct(DUMMY_SOFTWARE_PRODUCT_ID_2, SoftwareProductStatusEnum.INACTIVE));

        JSONArray softwareProducts2 = new JSONArray();
        softwareProducts2.put(getSoftwareProduct(DUMMY_SOFTWARE_PRODUCT_ID_3, SoftwareProductStatusEnum.ACTIVE));
        softwareProducts2.put(getSoftwareProduct(DUMMY_SOFTWARE_PRODUCT_ID_4, SoftwareProductStatusEnum.REMOVED));

        this.dataRecipient = getDataRecipient(DUMMY_LEGAL_ENTITY_ID_1, softwareProducts1,
                DataRecipientStatusEnum.ACTIVE);

        JSONArray data = new JSONArray();
        data.put(dataRecipient);
        data.put(getDataRecipient(DUMMY_LEGAL_ENTITY_ID_2, softwareProducts2, DataRecipientStatusEnum.SUSPENDED));

        this.responseJson = new JSONObject();
        this.responseJson.put(MetadataConstants.DR_JSON_ROOT, data);
    }

    @Test(description = "when valid data recipient provided, should return software products map")
    public void testGetSoftwareProducts() {

        Map<String, String> softwareProducts = uut.getSoftwareProducts(this.dataRecipient);

        Assert.assertEquals(softwareProducts.get(DUMMY_SOFTWARE_PRODUCT_ID_1),
                SoftwareProductStatusEnum.ACTIVE.toString());
        Assert.assertEquals(softwareProducts.get(DUMMY_SOFTWARE_PRODUCT_ID_2),
                SoftwareProductStatusEnum.INACTIVE.toString());
    }

    @Test(description = "when valid response json provided, should return data recipient and software product maps")
    public void testGetDataRecipientsStatusesFromRegister() {

        Map<String, Map<String, String>> statuses = uut.getDataRecipientsStatusesFromRegister(this.responseJson);

        Assert.assertFalse(statuses.get(MetadataConstants.MAP_DATA_RECIPIENTS).isEmpty());
        Assert.assertFalse(statuses.get(MetadataConstants.MAP_SOFTWARE_PRODUCTS).isEmpty());
    }

    @Test(description = "when valid data provided, should return modified maps")
    public void testProcessMetaDataStatus()
            throws IdentityApplicationManagementException, UserStoreException, OpenBankingException {

        //mock
        ServiceProviderProperty spProperty1 = new ServiceProviderProperty();
        spProperty1.setDisplayName(MetadataConstants.LEGAL_ENTITY_ID);
        spProperty1.setValue(DUMMY_LEGAL_ENTITY_ID_1);

        ServiceProviderProperty spProperty2 = new ServiceProviderProperty();
        spProperty2.setDisplayName(MetadataConstants.SOFTWARE_PRODUCT_ID);
        spProperty2.setValue(DUMMY_SOFTWARE_PRODUCT_ID_1);

        InboundAuthenticationRequestConfig config = new InboundAuthenticationRequestConfig();
        config.setInboundAuthKey("apim-store-client-id-1");

        InboundAuthenticationConfig inboundAuthenticationConfig = new InboundAuthenticationConfig();
        inboundAuthenticationConfig
                .setInboundAuthenticationRequestConfigs(new InboundAuthenticationRequestConfig[]{config});

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setSpProperties(new ServiceProviderProperty[]{spProperty1, spProperty2});
        serviceProvider.setInboundAuthenticationConfig(inboundAuthenticationConfig);

        IdentityCommonHelper identityCommonHelperMock = Mockito.mock(IdentityCommonHelper.class);
        Mockito.when(identityCommonHelperMock.getAllServiceProviders())
                .thenReturn(Collections.singletonList(serviceProvider));

        ServiceHolder serviceHolderMock = Mockito.mock(ServiceHolder.class);
        Mockito.when(serviceHolderMock.getIdentityCommonHelper()).thenReturn(identityCommonHelperMock);

        PowerMockito.mockStatic(ServiceHolder.class);
        PowerMockito.when(ServiceHolder.getInstance()).thenReturn(serviceHolderMock);

        //assert
        Map<String, String> dataRecipientsMap = new HashMap<>();
        dataRecipientsMap.put(DUMMY_LEGAL_ENTITY_ID_1, DataRecipientStatusEnum.ACTIVE.toString());

        Map<String, String> softwareProductsMap = new HashMap<>();
        softwareProductsMap.put(DUMMY_SOFTWARE_PRODUCT_ID_1, SoftwareProductStatusEnum.ACTIVE.toString());

        Map<String, Map<String, String>> modifiedMaps = uut.processMetadataStatus(dataRecipientsMap,
                softwareProductsMap);

        Assert.assertFalse(modifiedMaps.get(MAP_DATA_RECIPIENTS).isEmpty());
        Assert.assertFalse(modifiedMaps.get(MAP_SOFTWARE_PRODUCTS).isEmpty());

    }

    private JSONObject getSoftwareProduct(String softwareProductId, SoftwareProductStatusEnum status) {

        JSONObject softwareProduct = new JSONObject();
        softwareProduct.put(MetadataConstants.DR_JSON_SP_KEY, softwareProductId);
        softwareProduct.put(MetadataConstants.DR_JSON_STATUS, status.toString());

        return softwareProduct;
    }


    private JSONObject getDataRecipient(String legalEntityId, JSONArray softwareProducts,
                                        DataRecipientStatusEnum status) {

        JSONObject dataRecipientBrand = new JSONObject();
        dataRecipientBrand.put(MetadataConstants.DR_JSON_SOFTWARE_PRODUCTS, softwareProducts);

        JSONArray dataRecipientBrands = new JSONArray();
        dataRecipientBrands.put(dataRecipientBrand);

        JSONObject dataRecipient = new JSONObject();
        dataRecipient.put(MetadataConstants.DR_JSON_LEGAL_ENTITY_ID, legalEntityId);
        dataRecipient.put(MetadataConstants.DR_JSON_STATUS, status.toString());
        dataRecipient.put(MetadataConstants.DR_JSON_BRANDS, dataRecipientBrands);

        return dataRecipient;
    }
}