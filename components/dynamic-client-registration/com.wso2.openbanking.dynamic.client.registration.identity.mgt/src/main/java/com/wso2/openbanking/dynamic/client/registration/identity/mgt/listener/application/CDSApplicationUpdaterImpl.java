/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */
package com.wso2.openbanking.dynamic.client.registration.identity.mgt.listener.application;

import com.wso2.finance.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.finance.openbanking.accelerator.identity.listener.application.ApplicationUpdaterImpl;
import org.wso2.carbon.identity.oauth.dto.OAuthConsumerAppDTO;

import java.util.Map;

/**
 * Implementation class extended from ApplicationUpdaterImpl
 */
public class CDSApplicationUpdaterImpl extends ApplicationUpdaterImpl {

    @Override
    public void setOauthAppProperties(boolean isRegulatoryApp, OAuthConsumerAppDTO oauthApplication,
                                      Map<String, Object> spMetaData) throws OpenBankingException {
        oauthApplication.setIdTokenEncryptionEnabled(true);

        if (spMetaData.get("id_token_encrypted_response_alg") != null &&
                spMetaData.get("id_token_encrypted_response_enc") != null) {
            oauthApplication.setIdTokenEncryptionAlgorithm(spMetaData.get("id_token_encrypted_response_alg")
                    .toString());
            oauthApplication.setIdTokenEncryptionMethod(spMetaData.get("id_token_encrypted_response_enc").toString());
        }
    }
}
