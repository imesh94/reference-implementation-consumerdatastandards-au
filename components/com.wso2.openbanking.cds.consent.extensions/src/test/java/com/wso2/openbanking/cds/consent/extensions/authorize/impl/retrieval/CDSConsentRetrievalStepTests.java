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

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.consent.extensions.authorize.model.ConsentData;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.ConsentResource;
import com.wso2.openbanking.accelerator.identity.push.auth.extension.request.validator.util.PushAuthRequestValidatorUtils;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.common.metadata.status.validator.service.MetadataService;
import com.wso2.openbanking.cds.consent.extensions.authorize.utils.CDSDataRetrievalUtil;
import com.wso2.openbanking.cds.consent.extensions.util.CDSConsentAuthorizeTestConstants;
import net.minidev.json.JSONObject;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth.cache.SessionDataCache;
import org.wso2.carbon.identity.oauth.cache.SessionDataCacheEntry;
import org.wso2.carbon.identity.oauth2.model.OAuth2Parameters;

import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test class for CDS Consent Retrieval
 */
@PrepareForTest({SessionDataCacheEntry.class, SessionDataCache.class, PushAuthRequestValidatorUtils.class,
        CDSDataRetrievalUtil.class, MetadataService.class, OpenBankingCDSConfigParser.class})
@PowerMockIgnore({"com.wso2.openbanking.accelerator.consent.extensions.common.*"})
public class CDSConsentRetrievalStepTests extends PowerMockTestCase {

    private CDSConsentRetrievalStep cdsConsentRetrievalStep;
    private ConsentData consentDataMock;
    private ConsentResource consentResourceMock;
    private OpenBankingCDSConfigParser openBankingCDSConfigParserMock;

    @BeforeClass
    public void initClass() {

        cdsConsentRetrievalStep = new CDSConsentRetrievalStep();
        consentDataMock = mock(ConsentData.class);
        consentResourceMock = mock(ConsentResource.class);
        openBankingCDSConfigParserMock = mock(OpenBankingCDSConfigParser.class);
    }

    @BeforeMethod
    public void beforeMethod() {
        PowerMockito.stub(PowerMockito.method(OpenBankingCDSConfigParser.class, "getInstance"))
                .toReturn(openBankingCDSConfigParserMock);
    }

    @Test(priority = 2)
    public void testConsentRetrievalWithValidRequestObject() throws OpenBankingException {

        JSONObject jsonObject = new JSONObject();
        String request = "request=" + CDSConsentAuthorizeTestConstants.VALID_REQUEST_OBJECT;
        String redirectUri = "redirect_uri=https://www.google.com/redirects/redirect1&";
        String scopeString = "common:customer.basic:read common:customer.detail:read openid profile";
        String clientId = "client-id";
        String spFullName = "sp-full-name";
        String sampleQueryParams =  redirectUri + request;
        when(consentDataMock.getSpQueryParams()).thenReturn(sampleQueryParams);
        when(consentDataMock.getScopeString()).thenReturn(scopeString);
        when(consentDataMock.getClientId()).thenReturn(clientId);
        PowerMockito.stub(PowerMockito.method(CDSDataRetrievalUtil.class, "getServiceProviderFullName"))
                .toReturn(spFullName);
        when(consentDataMock.isRegulatory()).thenReturn(true);
        cdsConsentRetrievalStep.execute(consentDataMock, jsonObject);
        Assert.assertTrue(!jsonObject.isEmpty());
    }

    @Test(priority = 2)
    public void testConsentRetrievalWithMoreThanOneYearSharingDuration() {

        JSONObject jsonObject = new JSONObject();
        String request = "request=" + CDSConsentAuthorizeTestConstants.VALID_REQUEST_OBJECT_DIFF;
        String redirectUri = "redirect_uri=https://www.google.com/redirects/redirect1&";
        String scopeString = "common:customer.basic:read common:customer.detail:read openid profile";
        String clientId = "client-id";
        String spFullName = "sp-full-name";
        String sampleQueryParams =  redirectUri + request;
        when(consentDataMock.getSpQueryParams()).thenReturn(sampleQueryParams);
        when(consentDataMock.getScopeString()).thenReturn(scopeString);
        when(consentDataMock.getClientId()).thenReturn(clientId);
        PowerMockito.stub(PowerMockito.method(CDSDataRetrievalUtil.class, "getServiceProviderFullName"))
                .toReturn(spFullName);
        when(consentDataMock.isRegulatory()).thenReturn(true);
        cdsConsentRetrievalStep.execute(consentDataMock, jsonObject);
        Assert.assertTrue(!jsonObject.isEmpty());
    }

    @Test(priority = 2)
    public void testConsentRetrievalWithNoSharingDurationValueInRequestObject() {

        JSONObject jsonObject = new JSONObject();
        String request = "request=" + CDSConsentAuthorizeTestConstants.REQUEST_OBJECT_WITHOUT_SHARING_VAL;
        String redirectUri = "redirect_uri=https://www.google.com/redirects/redirect1&";
        String scopeString = "common:customer.basic:read common:customer.detail:read openid profile";
        String clientId = "client-id";
        String spFullName = "sp-full-name";
        String sampleQueryParams =  redirectUri + request;
        when(consentDataMock.getSpQueryParams()).thenReturn(sampleQueryParams);
        when(consentDataMock.getScopeString()).thenReturn(scopeString);
        when(consentDataMock.getClientId()).thenReturn(clientId);
        PowerMockito.stub(PowerMockito.method(CDSDataRetrievalUtil.class, "getServiceProviderFullName"))
                .toReturn(spFullName);
        when(consentDataMock.isRegulatory()).thenReturn(true);
        cdsConsentRetrievalStep.execute(consentDataMock, jsonObject);
        Assert.assertTrue(!jsonObject.isEmpty());
    }

    @Test(priority = 2)
    public void testRequestUriFlow() {

        JSONObject jsonObject = new JSONObject();
        String request = "request_uri=" + "urn:ietf:params:oauth:request_uri:XKnDFSbXJWjuf0AY6gOT1EIuvdP8BQLo";
        String redirectUri = "redirect_uri=https://www.google.com/redirects/redirect1&";
        String scopeString = "common:customer.basic:read common:customer.detail:read openid profile";
        String clientId = "client-id";
        String spFullName = "sp-full-name";
        String sampleQueryParams =  redirectUri + request;
        when(consentDataMock.getSpQueryParams()).thenReturn(sampleQueryParams);
        when(consentDataMock.getScopeString()).thenReturn(scopeString);
        when(consentDataMock.getClientId()).thenReturn(clientId);
        PowerMockito.stub(PowerMockito.method(CDSDataRetrievalUtil.class, "getServiceProviderFullName"))
                .toReturn(spFullName);

        String requestObjectString = CDSConsentAuthorizeTestConstants.VALID_REQUEST_OBJECT;
        OAuth2Parameters oAuth2Parameters = new OAuth2Parameters();
        oAuth2Parameters.setEssentialClaims(requestObjectString + ":" + "3600666666");

        SessionDataCache sessionDataCacheMock = mock(SessionDataCache.class);
        SessionDataCacheEntry sessionDataCacheEntry = new SessionDataCacheEntry();
        mockStatic(SessionDataCacheEntry.class);
        mockStatic(SessionDataCache.class);
        when(SessionDataCache.getInstance()).thenReturn(sessionDataCacheMock);
        when(sessionDataCacheMock.getValueFromCache(Mockito.anyObject())).thenReturn(sessionDataCacheEntry);

        sessionDataCacheEntry.setoAuth2Parameters(oAuth2Parameters);

        when(consentDataMock.isRegulatory()).thenReturn(true);
        cdsConsentRetrievalStep.execute(consentDataMock, jsonObject);
        Assert.assertTrue(!jsonObject.isEmpty());
    }

    @Test(priority = 2)
    public void testRequestUriFlowWithEncryptedReqObj() throws Exception {

        JSONObject jsonObject = new JSONObject();
        String request = "request_uri=" + "urn:ietf:params:oauth:request_uri:XKnDFSbXJWjuf0AY6gOT1EIuvdP8BQLo";
        String redirectUri = "redirect_uri=https://www.google.com/redirects/redirect1&";
        String scopeString = "common:customer.basic:read common:customer.detail:read openid profile";
        String clientId = "client-id";
        String spFullName = "sp-full-name";
        String sampleQueryParams =  redirectUri + request;
        when(consentDataMock.getSpQueryParams()).thenReturn(sampleQueryParams);
        when(consentDataMock.getScopeString()).thenReturn(scopeString);
        when(consentDataMock.getClientId()).thenReturn(clientId);
        PowerMockito.stub(PowerMockito.method(CDSDataRetrievalUtil.class, "getServiceProviderFullName"))
                .toReturn(spFullName);
        PowerMockito.stub(PowerMockito.method(MetadataService.class, "shouldFacilitateConsentAuthorisation"))
                .toReturn(true);

        String requestObjectString = CDSConsentAuthorizeTestConstants.ENCRYPTED_JWT;
        OAuth2Parameters oAuth2Parameters = new OAuth2Parameters();
        oAuth2Parameters.setEssentialClaims(requestObjectString + ":" + "3600666666");

        SessionDataCache sessionDataCacheMock = mock(SessionDataCache.class);
        SessionDataCacheEntry sessionDataCacheEntry = new SessionDataCacheEntry();
        mockStatic(SessionDataCacheEntry.class);
        mockStatic(SessionDataCache.class);
        mockStatic(PushAuthRequestValidatorUtils.class);
        when(SessionDataCache.getInstance()).thenReturn(sessionDataCacheMock);
        when(sessionDataCacheMock.getValueFromCache(Mockito.anyObject())).thenReturn(sessionDataCacheEntry);
        when(PushAuthRequestValidatorUtils.decrypt(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(CDSConsentAuthorizeTestConstants.VALID_REQUEST_OBJECT);

        sessionDataCacheEntry.setoAuth2Parameters(oAuth2Parameters);

        when(consentDataMock.isRegulatory()).thenReturn(true);
        cdsConsentRetrievalStep.execute(consentDataMock, jsonObject);
        Assert.assertTrue(!jsonObject.isEmpty());
    }

    @Test(expectedExceptions = ConsentException.class, priority = 1)
    public void testConsentRetrievalWithoutClientId() {
        JSONObject jsonObject = new JSONObject();
        String request = "request=" + CDSConsentAuthorizeTestConstants.VALID_REQUEST_OBJECT;
        String redirectUri = "redirect_uri=https://www.google.com/redirects/redirect1&";
        String scopeString = "common:customer.basic:read common:customer.detail:read openid profile";
        String sampleQueryParams = redirectUri + request;
        doReturn(sampleQueryParams).when(consentDataMock).getSpQueryParams();
        doReturn(scopeString).when(consentDataMock).getScopeString();
        when(consentDataMock.isRegulatory()).thenReturn(true);
        cdsConsentRetrievalStep.execute(consentDataMock, jsonObject);
    }

    @Test(priority = 3, expectedExceptions = ConsentException.class)
    public void testConsentRetrievalWithInvalidMetadataCache() {
        JSONObject jsonObject = new JSONObject();
        String clientId = "client-id";
        when(consentDataMock.getClientId()).thenReturn(clientId);
        when(openBankingCDSConfigParserMock.isMetadataCacheEnabled()).thenReturn(true);
        PowerMockito.stub(PowerMockito.method(OpenBankingCDSConfigParser.class, "getInstance"))
                .toReturn(openBankingCDSConfigParserMock);
        PowerMockito.stub(PowerMockito.method(MetadataService.class, "shouldFacilitateConsentAuthorisation"))
                .toReturn(false);

        cdsConsentRetrievalStep.execute(consentDataMock, jsonObject);
    }
}
