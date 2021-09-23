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

package com.wso2.openbanking.toolkit.cds.integration.tests.multitpp

import com.wso2.openbanking.test.framework.automation.BasicAuthErrorStep
import com.wso2.openbanking.test.framework.automation.BrowserAutomation
import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUAuthorisationBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUDCRConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUMockCDRIntegrationUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURegistrationRequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUTestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Test Scenarios validates the system's behaviour in consent flows with consent bound to different clients.
 */
class MultiTppConsentValidationTests extends AbstractAUTests {

	private String clientId
	File xmlFile = new File(System.getProperty("user.dir").toString()
					.concat("/../../../resources/test-config.xml"))
	private String registrationPath
	static final String CDS_PATH = AUConstants.CDS_PATH
	def appConfigReader = new AppConfigReader()

	@BeforeClass(alwaysRun = true)
	void setup() {

		appConfigReader.setTppNumber(1)

		AUMockCDRIntegrationUtil.loadMetaDataToCDRRegister()
		AURegistrationRequestBuilder.retrieveADRInfo()

		//Register Second TPP.
		registrationPath = AUDCRConstants.REGISTRATION_ENDPOINT

		def registrationResponse = AURegistrationRequestBuilder
						.buildRegistrationRequest(AURegistrationRequestBuilder.getRegularClaims())
						.when()
						.post(registrationPath)

		clientId = TestUtil.parseResponseBody(registrationResponse, "client_id")
		Assert.assertEquals(registrationResponse.statusCode(), TestConstants.CREATED)

		//Write Client Id of TPP2 to config file.
		TestUtil.writeXMLContent(xmlFile.toString(), "Application", "ClientID", clientId,
						appConfigReader.tppNumber)
	}

	@Test
	void "OB-1313_Revoke sharing arrangement bound to different Tpp"() {

		appConfigReader.setTppNumber(0)

		//authorise sharing arrangement
		doConsentAuthorisation()
		Assert.assertNotNull(authorisationCode)

		//obtain cdr_arrangement_id from token response
		def userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
		String cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")
		Assert.assertNotNull(cdrArrangementId)

		//retrieve consumer data successfully
		Response response = AURequestBuilder
						.buildBasicRequest(userAccessToken.tokens.accessToken.toString(),
										AUConstants.CDR_ENDPOINT_VERSION)
						.baseUri(AUTestUtil.getBaseUrl(AUConstants.BASE_PATH_TYPE_ACCOUNT))
						.get("${CDS_PATH}${AUConstants.BULK_ACCOUNT_PATH}")

		Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

		//Get application access token for TPP2
		appConfigReader.setTppNumber(1)
		List<String> scopes = ["openid"]
		def applicationAccessToken = AURequestBuilder.getApplicationToken(scopes, clientId)
		Assert.assertNotNull(applicationAccessToken)

		appConfigReader.setTppNumber(0)
		//revoke sharing arrangement using token of TPP2 and cdrArrangementId of TPP1
		doArrangementRevocationWithPkjwt(applicationAccessToken, cdrArrangementId, AppConfigReader.getClientId())

		Assert.assertEquals(revocationResponse.statusCode(), AUConstants.STATUS_CODE_403)
	}

	@Test
	void "OB-1311_Validate consent authorisation with request_uri bound to different tpp"() {
		appConfigReader.setTppNumber(0)

		//authorise sharing arrangement
		doConsentAuthorisation()
		Assert.assertNotNull(authorisationCode)

		//obtain cdr_arrangement_id from token response
		def userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
		String cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")
		Assert.assertNotNull(cdrArrangementId)

		//retrieve consumer data successfully
		Response response = getAccountRetrieval(userAccessToken.tokens.accessToken.toString())
		Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

		//Send PAR request.
		def parResponse = doPushAuthorisationRequestWithPkjwt(scopes, AUConstants.DEFAULT_SHARING_DURATION,
						true, cdrArrangementId)

		def requestUri = TestUtil.parseResponseBody(parResponse, "request_uri").toURI()
		Assert.assertEquals(parResponse.statusCode(), AUConstants.STATUS_CODE_201)

		appConfigReader.setTppNumber(1)
		//Send consent authorisation using request_uri bound to TPP1 with client id of TPP2
		AUAuthorisationBuilder authorisationBuilder = new AUAuthorisationBuilder(scopes, requestUri, clientId)

		def automation = new BrowserAutomation(BrowserAutomation.DEFAULT_DELAY)
						.addStep(new BasicAuthErrorStep(authorisationBuilder.authoriseUrl))
						.execute()

		Assert.assertTrue(TestUtil.getErrorDescriptionFromUrl(automation.currentUrl.get())
						.contains("Request Object and Authorization request contains unmatched client_id"))

	}

	@Test
	void "OB-1312_Validate PAR request with cdr_arrangemet_id belongs to different TPP"() {
		appConfigReader.setTppNumber(0)

		//authorise sharing arrangement
		doConsentAuthorisation()
		Assert.assertNotNull(authorisationCode)

		//obtain cdr_arrangement_id from token response
		def userAccessToken = AURequestBuilder.getUserToken(authorisationCode)
		String cdrArrangementId = userAccessToken.getCustomParameters().get("cdr_arrangement_id")
		Assert.assertNotNull(cdrArrangementId)

		appConfigReader.setTppNumber(1)
		//Send PAR request.
		def parResponse = doPushAuthorisationRequestWithPkjwt(scopes, AUConstants.DEFAULT_SHARING_DURATION,
						true, cdrArrangementId, clientId)

		Assert.assertEquals(parResponse.statusCode(), AUConstants.STATUS_CODE_400)
	}

	@AfterClass (alwaysRun = true)
	void tearDown() {

		appConfigReader.setTppNumber(1)
		accessToken = AURequestBuilder.getApplicationToken(scopes, clientId)

		def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
						.when()
						.delete(registrationPath + clientId)

		Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_204)

		//Write Client Id of TPP2 to config file.
		TestUtil.writeXMLContent(xmlFile.toString(), "Application", "ClientID", "",
						appConfigReader.tppNumber)
	}
}
