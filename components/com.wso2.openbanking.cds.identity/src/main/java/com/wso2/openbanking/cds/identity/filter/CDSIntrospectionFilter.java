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

package com.wso2.openbanking.cds.identity.filter;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.common.util.Generated;
import com.wso2.openbanking.accelerator.common.util.OpenBankingUtils;
import com.wso2.openbanking.accelerator.identity.token.util.TokenFilterException;
import com.wso2.openbanking.accelerator.identity.token.validators.OBIdentityFilterValidator;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonUtil;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.identity.filter.exception.CDSFilterException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * CDS Introspection Filter.
 * Enforces security to the introspection endpoint.
 */
public class CDSIntrospectionFilter extends CDSBaseFilter {

    private String clientId = null;
    private static List<OBIdentityFilterValidator> validators = new ArrayList<>();
    private static final Log log = LogFactory.getLog(CDSIntrospectionFilter.class);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        initializeFilterValidators();
    }

    @Override
    @Generated(message = "Excluding from code coverage since it requires a service call")
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        try {
            clientId = this.extractClientId(servletRequest);
        } catch (CDSFilterException e) {
            handleValidationFailure((HttpServletResponse) servletResponse, e.getErrorCode(),
                    e.getMessage(), e.getErrorDescription());
            return;
        }

        try {
            if (IdentityCommonUtil.getRegulatoryFromSPMetaData(clientId)) {
                servletRequest = appendTransportHeader(servletRequest, servletResponse);
                for (OBIdentityFilterValidator validator : validators) {
                    validator.validate(servletRequest, clientId);
                }
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (TokenFilterException e) {
            handleValidationFailure((HttpServletResponse) servletResponse,
                    e.getErrorCode(), e.getMessage(), e.getErrorDescription());
        } catch (OpenBankingException e) {
            if (e.getMessage().contains("Error occurred while retrieving OAuth2 application data")) {
                handleValidationFailure((HttpServletResponse) servletResponse,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "OAuth2 application data retrieval failed",
                        e.getMessage());
            } else {
                handleValidationFailure((HttpServletResponse) servletResponse,
                        HttpServletResponse.SC_BAD_REQUEST, "Service provider metadata retrieval failed",
                        e.getMessage());
            }
        }

    }

    /**
     * Load filter validators form configuration.
     */
    private void initializeFilterValidators() {
        if (validators.isEmpty()) {
            for (Object element : OpenBankingCDSConfigParser.getInstance().getIntrospectFilterValidators()) {
                validators.add((OBIdentityFilterValidator) OpenBankingUtils.
                        getClassInstanceFromFQN(element.toString()));
            }
        }
    }
}
