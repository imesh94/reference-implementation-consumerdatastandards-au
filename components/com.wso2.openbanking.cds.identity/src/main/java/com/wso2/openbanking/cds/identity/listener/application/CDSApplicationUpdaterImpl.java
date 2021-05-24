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
import com.wso2.openbanking.accelerator.identity.listener.application.ApplicationUpdaterImpl;
import com.wso2.openbanking.cds.identity.dcr.constants.CDSValidationConstants;
import org.wso2.carbon.identity.oauth.dto.OAuthConsumerAppDTO;

import java.util.Map;

/**
 * Implementation class extended from ApplicationUpdaterImpl
 */
public class CDSApplicationUpdaterImpl extends ApplicationUpdaterImpl {

    @Override
    public void setOauthAppProperties(boolean isRegulatoryApp, OAuthConsumerAppDTO oauthApplication,
                                      Map<String, Object> spMetaData) throws OpenBankingException {

        if (spMetaData.get(CDSValidationConstants.ID_TOKEN_ENCRYPTION_RESPONSE_ALG) != null &&
                spMetaData.get(CDSValidationConstants.ID_TOKEN_ENCRYPTION_RESPONSE_ENC) != null) {
            oauthApplication.setIdTokenEncryptionEnabled(true);
            oauthApplication.setIdTokenEncryptionAlgorithm(spMetaData
                    .get(CDSValidationConstants.ID_TOKEN_ENCRYPTION_RESPONSE_ALG).toString());
            oauthApplication.setIdTokenEncryptionMethod(spMetaData
                    .get(CDSValidationConstants.ID_TOKEN_ENCRYPTION_RESPONSE_ENC).toString());
        }
    }

    @Override
    public void publishData(Map<String, Object> spMetaData, OAuthConsumerAppDTO oAuthConsumerAppDTO) 
            throws OpenBankingException {
        super.publishData(spMetaData, oAuthConsumerAppDTO);
        // TODO: 2021-03-22 Add data publishing logic for application here later 
    }
}
