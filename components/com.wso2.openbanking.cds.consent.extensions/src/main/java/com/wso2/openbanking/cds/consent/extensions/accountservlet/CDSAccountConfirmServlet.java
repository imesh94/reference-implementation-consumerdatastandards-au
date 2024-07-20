/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.consent.extensions.accountservlet;

import com.wso2.openbanking.accelerator.common.util.Generated;
import com.wso2.openbanking.cds.common.data.publisher.CDSDataPublishingService;
import com.wso2.openbanking.cds.common.enums.AuthorisationStageEnum;
import com.wso2.openbanking.cds.common.utils.CDSCommonUtils;
import com.wso2.openbanking.cds.common.utils.CommonConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * This servlet is executed after confirming the account selection.
 */
public class CDSAccountConfirmServlet extends HttpServlet {

    private static final long serialVersionUID = 7306276594632678191L;
    private static final Log log = LogFactory.getLog(CDSAccountConfirmServlet.class);

    @Generated(message = "Excluding from code coverage since this requires a service call")
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Publish data related to abandoned consent flow (Metrics).
        Object requestUriKey = request.getSession().getAttribute(CommonConstants.REQUEST_URI_KEY);
        if (requestUriKey != null) {
            Map<String, Object> abandonedConsentFlowData = CDSCommonUtils.generateAbandonedConsentFlowDataMap(
                    requestUriKey.toString(), null, AuthorisationStageEnum.ACCOUNT_SELECTED);
            log.debug("Publishing abandoned consent flow data in the account selection confirmed stage.");
            CDSDataPublishingService.getCDSDataPublishingService().publishAbandonedConsentFlowData(
                    abandonedConsentFlowData);
        } else {
            log.warn("Request URI key not found in session attributes. Continuing without publishing abandoned " +
                    "consent flow data in the account selection confirmed stage.");
        }

        RequestDispatcher dispatcher = this.getServletContext()
                .getRequestDispatcher("/oauth2_authz_displayconsent.jsp");
        dispatcher.forward(request, response);
    }

}
