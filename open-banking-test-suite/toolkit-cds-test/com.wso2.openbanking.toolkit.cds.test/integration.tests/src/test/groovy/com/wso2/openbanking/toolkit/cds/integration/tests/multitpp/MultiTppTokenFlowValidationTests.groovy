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

import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUDCRConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUMockCDRIntegrationUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURegistrationRequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AbstractAUTests
import org.testng.Assert
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Test Scenarios validates the system's behaviour in token flow with consent bound to different clients.
 */
class MultiTppTokenFlowValidationTests extends AbstractAUTests {

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

		//Write Client Id and Client Secret of TTP2 to config file.
		TestUtil.writeXMLContent(xmlFile.toString(), "Application", "ClientID", clientId,
						appConfigReader.tppNumber)

		doConsentAuthorisation(clientId)
	}

	@Test
	void "OB-1314_Get user access token from authorisation code bound to different Tpp" () {

		appConfigReader.setTppNumber(0)
		def userToken = AURequestBuilder.getUserTokenErrorResponse(authorisationCode, AppConfigReader.getRedirectURL(),
						AppConfigReader.getClientId())

		Assert.assertEquals(userToken.error.httpStatusCode, TestConstants.BAD_REQUEST)
		Assert.assertEquals(userToken.error.code, "invalid_grant")
		Assert.assertEquals(userToken.error.description, "Invalid authorization code received from token request")
	}

	@Test
	void "OB-1315_Get user access token with client_assertion does not bound to the requested client" () {

		appConfigReader.setTppNumber(0)
		doConsentAuthorisation(AppConfigReader.getClientId())
		def userToken = AURequestBuilder.getUserTokenErrorResponse(authorisationCode, AppConfigReader.getRedirectURL(),
						clientId)

		Assert.assertEquals(userToken.error.httpStatusCode, TestConstants.BAD_REQUEST)
		Assert.assertEquals(userToken.error.code, "invalid_grant")
		Assert.assertEquals(userToken.error.description, "Invalid authorization code received from token request")
	}


	@AfterClass (alwaysRun = true)
	void tearDown() {

		appConfigReader.setTppNumber(1)
		accessToken = AURequestBuilder.getApplicationToken(scopes, clientId)

		def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
						.when()
						.delete(registrationPath + clientId)

		Assert.assertEquals(registrationResponse.statusCode(), AUConstants.STATUS_CODE_204)

		//Write Client Id and Client Secret of TTP2 to config file.
		TestUtil.writeXMLContent(xmlFile.toString(), "Application", "ClientID", "",
						appConfigReader.tppNumber)
	}
}
