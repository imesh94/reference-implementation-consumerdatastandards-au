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
package com.wso2.openbanking.cds.identity.listener.application;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth.dto.OAuthConsumerAppDTO;

import java.util.HashMap;
import java.util.Map;

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
    }
}
