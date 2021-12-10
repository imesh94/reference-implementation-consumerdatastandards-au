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
package com.wso2.openbanking.cds.identity.filter;

import com.wso2.openbanking.accelerator.common.util.OpenBankingUtils;
import com.wso2.openbanking.accelerator.identity.token.validators.OBIdentityFilterValidator;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

/**
 * CDS Par Filter.
 * Enforces security to the par endpoint.
 */
public class CDSParFilter extends CDSBaseFilter {

    private static final Log log = LogFactory.getLog(CDSParFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        initializeFilterValidators();
    }

    /**
     * Load filter validators form configuration.
     */
    private void initializeFilterValidators() {
        if (validators.isEmpty()) {
            log.info("Adding CDSParFilter validators");
            for (Object element : OpenBankingCDSConfigParser.getInstance().getRevokeFilterValidators()) {
                validators.add((OBIdentityFilterValidator) OpenBankingUtils.
                        getClassInstanceFromFQN(element.toString()));
                log.info(String.format("Added %s as an CDSParFilter validator", element));
            }
        }
    }
}
