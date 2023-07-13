/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.identity.auth.extensions.response.validator;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonUtil;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * CDSCodeResponseTypeValidator Test class
 */
@PrepareForTest({IdentityCommonUtil.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class CDSCodeResponseTypeValidatorTest extends PowerMockTestCase {

    @Test
    public void checkValidCodeResponseTypeValidation() throws OAuthProblemException, OpenBankingException {

        // Mock
        HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        when(httpServletRequestMock.getParameter("response_type")).thenReturn("code");
        when(httpServletRequestMock.getParameter("client_id")).thenReturn("1234567654321");

        PowerMockito.mockStatic(IdentityCommonUtil.class);
        PowerMockito.when(IdentityCommonUtil.getRegulatoryFromSPMetaData("test")).thenReturn(true);

        CDSCodeResponseTypeValidator uut = spy(new CDSCodeResponseTypeValidator());

        // Act
        uut.validateRequiredParameters(httpServletRequestMock);
    }
}
