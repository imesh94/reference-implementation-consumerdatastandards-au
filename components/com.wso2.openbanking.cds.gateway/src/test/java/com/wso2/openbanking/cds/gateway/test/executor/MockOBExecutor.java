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

package com.wso2.openbanking.cds.gateway.test.executor;

import com.wso2.openbanking.accelerator.gateway.executor.core.OpenBankingGatewayExecutor;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIRequestContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIResponseContext;

public class MockOBExecutor implements OpenBankingGatewayExecutor {

    @Override
    public void preProcessRequest(OBAPIRequestContext obapiRequestContext) {

        obapiRequestContext.setModifiedPayload("{}");
    }

    @Override
    public void preProcessResponse(OBAPIResponseContext obapiResponseContext) {

    }

    /**
     * Method to handle post response
     *
     * @param obapiResponseContext OB response context object
     */
    @Override
    public void postProcessResponse(OBAPIResponseContext obapiResponseContext) {

    }

    /**
     * Method to handle post request
     *
     * @param obapiRequestContext OB request context object
     */
    @Override
    public void postProcessRequest(OBAPIRequestContext obapiRequestContext) {

    }
}
