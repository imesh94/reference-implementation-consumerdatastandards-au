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
package com.wso2.openbanking.cds.gateway.executors.core;

import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIRequestContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIResponseContext;
import com.wso2.openbanking.cds.gateway.test.util.TestUtil;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class CDSAPIRequestRouterTest {

    CDSAPIRequestRouter cdsApiRequestRouter;
    OpenAPI openAPI;

    @BeforeClass
    public void beforeClass() {

        cdsApiRequestRouter = new CDSAPIRequestRouter();
        cdsApiRequestRouter.setExecutorMap(TestUtil.initExecutors());
        openAPI = new OpenAPI();
        openAPI.setExtensions(new HashMap<>());
    }

    @Test(priority = 1)
    public void testDCRRequestsForRouter() {

        OBAPIRequestContext obapiRequestContext = Mockito.mock(OBAPIRequestContext.class);
        OBAPIResponseContext obapiResponseContext = Mockito.mock(OBAPIResponseContext.class);
        Info apiInfo = Mockito.mock(Info.class);
        openAPI.setInfo(apiInfo);
        Map<String, Object> extensions = new HashMap<>();
        Map<String, String> contextProps = new HashMap<>();
        extensions.put(RequestRouterConstants.API_TYPE_CUSTOM_PROP, RequestRouterConstants.API_TYPE_DCR);
        contextProps.put(RequestRouterConstants.API_TYPE_CUSTOM_PROP, RequestRouterConstants.API_TYPE_DCR);
        openAPI.setExtensions(extensions);
        Mockito.when(obapiRequestContext.getOpenAPI()).thenReturn(openAPI);
        Mockito.when(obapiResponseContext.getContextProps()).thenReturn(contextProps);
        Mockito.when(apiInfo.getTitle()).thenReturn(RequestRouterConstants.DCR_API_NAME);
        Assert.assertNotNull(cdsApiRequestRouter.getExecutorsForRequest(obapiRequestContext));
        Assert.assertNotNull(cdsApiRequestRouter.getExecutorsForResponse(obapiResponseContext));

    }

    @Test(priority = 1)
    public void testCDSRequestsForRouter() {

        OBAPIRequestContext obapiRequestContext = Mockito.mock(OBAPIRequestContext.class);
        OBAPIResponseContext obapiResponseContext = Mockito.mock(OBAPIResponseContext.class);
        Info apiInfo = Mockito.mock(Info.class);
        openAPI.setInfo(apiInfo);
        Map<String, Object> extensions = new HashMap<>();
        Map<String, String> contextProps = new HashMap<>();
        extensions.put(RequestRouterConstants.API_TYPE_CUSTOM_PROP, RequestRouterConstants.API_TYPE_CDS);
        contextProps.put(RequestRouterConstants.API_TYPE_CUSTOM_PROP, RequestRouterConstants.API_TYPE_CDS);
        openAPI.setExtensions(extensions);
        Mockito.when(obapiRequestContext.getOpenAPI()).thenReturn(openAPI);
        Mockito.when(obapiResponseContext.getContextProps()).thenReturn(contextProps);
        Mockito.when(apiInfo.getTitle()).thenReturn(RequestRouterConstants.CDS_API_NAME);
        Assert.assertNotNull(cdsApiRequestRouter.getExecutorsForRequest(obapiRequestContext));
        Assert.assertNotNull(cdsApiRequestRouter.getExecutorsForResponse(obapiResponseContext));

    }

    @Test(priority = 1)
    public void testOtherRequestsForRouter() {

        OBAPIRequestContext obapiRequestContext = Mockito.mock(OBAPIRequestContext.class);
        OBAPIResponseContext obapiResponseContext = Mockito.mock(OBAPIResponseContext.class);
        Info apiInfo = Mockito.mock(Info.class);
        openAPI.setInfo(apiInfo);
        Map<String, Object> extensions = new HashMap<>();
        Map<String, String> contextProps = new HashMap<>();
        openAPI.setExtensions(extensions);
        Mockito.when(obapiRequestContext.getOpenAPI()).thenReturn(openAPI);
        Mockito.when(obapiResponseContext.getContextProps()).thenReturn(contextProps);
        Mockito.when(apiInfo.getTitle()).thenReturn(RequestRouterConstants.CDS_API_NAME);
        Assert.assertNotNull(cdsApiRequestRouter.getExecutorsForRequest(obapiRequestContext));
        Assert.assertNotNull(cdsApiRequestRouter.getExecutorsForResponse(obapiResponseContext));
    }

    @Test(priority = 2)
    public void testNonRegulatoryAPIcall() {

        OBAPIRequestContext obapiRequestContext = Mockito.mock(OBAPIRequestContext.class);
        OBAPIResponseContext obapiResponseContext = Mockito.mock(OBAPIResponseContext.class);
        Map<String, Object> extensions = new HashMap<>();
        Map<String, String> contextProps = new HashMap<>();
        extensions.put(RequestRouterConstants.API_TYPE_CUSTOM_PROP, RequestRouterConstants.API_TYPE_NON_REGULATORY);
        contextProps.put(RequestRouterConstants.API_TYPE_CUSTOM_PROP, RequestRouterConstants.API_TYPE_NON_REGULATORY);
        openAPI.setExtensions(extensions);
        Mockito.when(obapiRequestContext.getOpenAPI()).thenReturn(openAPI);
        Mockito.when(obapiResponseContext.getContextProps()).thenReturn(contextProps);
        Assert.assertEquals(cdsApiRequestRouter.getExecutorsForRequest(obapiRequestContext).size(), 0);
        Assert.assertEquals(cdsApiRequestRouter.getExecutorsForResponse(obapiResponseContext).size(), 0);
    }
}
