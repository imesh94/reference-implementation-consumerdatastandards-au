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

package com.wso2.openbanking.cds.consent.extensions.authorize.utils;

import graphql.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Test class for CDSDataRetrievalUtil
 */
public class CDSDataRetrievalUtilTest {

    private static final String SP_QUERY_PARAMS = "redirect_uri=https://www.google.com/redirects/redirect1&" +
            "request=requst-object&client_id=client-id";
    private static final String SCOPES = "common:customer.basic:read common:customer.detail:read openid profile";


    @BeforeClass
    public void initClass() {

    }

    @Test
    public void testExtractRequestObject() {
        String requestObject = CDSDataRetrievalUtil.extractRequestObject(SP_QUERY_PARAMS);
        Assert.assertNotNull(requestObject);
    }

    @Test
    public void testgetRedirectURL() {
        String redirectUrl = CDSDataRetrievalUtil.getRedirectURL(SP_QUERY_PARAMS);
        Assert.assertNotNull(redirectUrl);
    }

    @Test
    public void testgetPermissionList() {
        List<PermissionsEnum> permissionList = CDSDataRetrievalUtil.getPermissionList(SCOPES);
        Assert.assertNotNull(permissionList);
    }
}
