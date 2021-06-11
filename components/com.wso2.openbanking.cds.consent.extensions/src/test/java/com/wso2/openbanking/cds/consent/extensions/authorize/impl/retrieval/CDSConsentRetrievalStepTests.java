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
package com.wso2.openbanking.cds.consent.extensions.authorize.impl.retrieval;

import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentResource;
import com.wso2.openbanking.cds.consent.extensions.util.CDSConsentAuthorizeTestConstants;
import net.minidev.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Test class for CDS Consent Retrieval
 */
public class CDSConsentRetrievalStepTests {

    private static CDSConsentRetrievalStep cdsConsentRetrievalStep;
    private static ConsentData consentDataMock;
    private static ConsentResource consentResourceMock;


    @BeforeClass
    public void initClass() {

        cdsConsentRetrievalStep = new CDSConsentRetrievalStep();
        consentDataMock = mock(ConsentData.class);
        consentResourceMock = mock(ConsentResource.class);
    }

    @Test(expectedExceptions = ConsentException.class)
    public void testConsentRetrievalWithEmptyConsentData() {

        cdsConsentRetrievalStep.execute(consentDataMock, new JSONObject());
    }

    @Test
    public void testConsentRetrievalWithValidRequestObject() {

        JSONObject jsonObject = new JSONObject();
        String reqeust = "request=" + CDSConsentAuthorizeTestConstants.VALID_REQUEST_OBJECT;
        String redirectUri = "redirect_uri=https://www.google.com/redirects/redirect1&";
        String scopeString = "common:customer.basic:read common:customer.detail:read openid profile";
        String sampleQueryParams =  redirectUri + reqeust;
        doReturn(sampleQueryParams).when(consentDataMock).getSpQueryParams();
        doReturn(scopeString).when(consentDataMock).getScopeString();
        cdsConsentRetrievalStep.execute(consentDataMock, jsonObject);
        Assert.assertTrue(!jsonObject.isEmpty());
    }

    @Test
    public void testConsentRetrievalWithMoreThanOneYearSharingDuration() {

        JSONObject jsonObject = new JSONObject();
        String reqeust = "request=" + CDSConsentAuthorizeTestConstants.VALID_REQUEST_OBJECT_DIFF;
        String redirectUri = "redirect_uri=https://www.google.com/redirects/redirect1&";
        String scopeString = "common:customer.basic:read common:customer.detail:read openid profile";
        String sampleQueryParams =  redirectUri + reqeust;
        doReturn(sampleQueryParams).when(consentDataMock).getSpQueryParams();
        doReturn(scopeString).when(consentDataMock).getScopeString();
        cdsConsentRetrievalStep.execute(consentDataMock, jsonObject);
        Assert.assertTrue(!jsonObject.isEmpty());
    }

    @Test
    public void testConsentRetrievalWithNoSharingDurationValueInRequestObject() {

        JSONObject jsonObject = new JSONObject();
        String reqeust = "request=" + CDSConsentAuthorizeTestConstants.REQUEST_OBJECT_WITHOUT_SHARING_VAL;
        String redirectUri = "redirect_uri=https://www.google.com/redirects/redirect1&";
        String scopeString = "common:customer.basic:read common:customer.detail:read openid profile";
        String sampleQueryParams =  redirectUri + reqeust;
        doReturn(sampleQueryParams).when(consentDataMock).getSpQueryParams();
        doReturn(scopeString).when(consentDataMock).getScopeString();
        cdsConsentRetrievalStep.execute(consentDataMock, jsonObject);
        Assert.assertTrue(!jsonObject.isEmpty());
    }
}
