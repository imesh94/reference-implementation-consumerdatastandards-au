/*
 * Copyright (c) 2021-2022, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.identity.listener.application;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth.dto.OAuthConsumerAppDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for Application Management Listener functionality.
 */
public class ApplicationManagementListenerTest {

    private Map<String, Object> spMetaData = new HashMap<>();

    @BeforeClass
    public void beforeClass() {

        spMetaData.put("id_token_encrypted_response_alg", "RSA-OEAP");
        spMetaData.put("id_token_encrypted_response_enc", "A256GCM");
    }

    @Test
    public void testSetOauthPropertiesSuccessScenario() throws OpenBankingException {

        CDSApplicationUpdaterImpl cdsApplicationUpdater = new CDSApplicationUpdaterImpl();
        OAuthConsumerAppDTO oAuthConsumerAppDTO = new OAuthConsumerAppDTO();
        cdsApplicationUpdater.setOauthAppProperties(true, oAuthConsumerAppDTO, spMetaData);
        Assert.assertEquals(oAuthConsumerAppDTO.getIdTokenEncryptionAlgorithm(), "RSA-OEAP");
        Assert.assertEquals(oAuthConsumerAppDTO.getIdTokenEncryptionMethod(), "A256GCM");
    }
}
