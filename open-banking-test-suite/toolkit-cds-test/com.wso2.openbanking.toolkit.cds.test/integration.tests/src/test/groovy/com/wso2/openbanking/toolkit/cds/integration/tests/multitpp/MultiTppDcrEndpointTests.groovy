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

import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUDCRConstants
import com.wso2.openbanking.toolkit.cds.test.common.utils.AUMockCDRIntegrationUtil
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURegistrationRequestBuilder
import com.wso2.openbanking.toolkit.cds.test.common.utils.AURequestBuilder
import org.testng.Assert
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * Test Scenarios validates the system's behaviour when accessing dcr endpoints with access
 * token bound to different clients.
 */
class MultiTppDcrEndpointTests {

	private List<String> scopes = [
					AUConstants.SCOPES.BANK_ACCOUNT_BASIC_READ.getScopeString(),
					AUConstants.SCOPES.BANK_TRANSACTION_READ.getScopeString(),
					AUConstants.SCOPES.BANK_CUSTOMER_DETAIL_READ.getScopeString(),
					AUConstants.SCOPES.CDR_REGISTRATION.getScopeString()
	]

	private String accessToken
	private String clientId
	private String registrationPath
	File xmlFile = new File(System.getProperty("user.dir").toString()
					.concat("/../../../resources/test-config.xml"))
	def appConfigReader = new AppConfigReader()

	@BeforeClass(alwaysRun = true)
	void setup() {

		TestSuite.init()
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

		appConfigReader.setTppNumber(0)
		accessToken = AURequestBuilder.getApplicationToken(scopes, AppConfigReader.getClientId())
		Assert.assertNotNull(accessToken)
	}

	@Test
	void "OB-1308_Retrieve registration details with access token bound to a different client"() {

		def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
						.when()
						.get(registrationPath + clientId)

		Assert.assertEquals(registrationResponse.statusCode(), TestConstants.UNAUTHORIZED)
		Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse,AUConstants.ERROR),
						AUConstants.INVALID_CLIENT_METADATA)
		Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse,AUConstants.ERROR_DESCRIPTION),
						"Request failed due to unknown or invalid Client")
	}

	@Test
	void "OB-1309_Update Application with access token bound to a different client"() {

		def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
						.body(AURegistrationRequestBuilder.getSignedRequestObject(AURegistrationRequestBuilder
										.getRegularClaims()))
						.when()
						.put(registrationPath + clientId)

		Assert.assertEquals(registrationResponse.statusCode(), TestConstants.UNAUTHORIZED)
		Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse,AUConstants.ERROR),
						AUConstants.INVALID_CLIENT_METADATA)
		Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse,AUConstants.ERROR_DESCRIPTION),
						"Request failed due to unknown or invalid Client")
	}

	@Test
	void "OB-1310_Delete application with access token bound to a different client"() {

		def registrationResponse = AURegistrationRequestBuilder.buildBasicRequest(accessToken)
						.when()
						.delete(registrationPath + clientId)

		Assert.assertEquals(registrationResponse.statusCode(), TestConstants.UNAUTHORIZED)
		Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse,AUConstants.ERROR),
						AUConstants.INVALID_CLIENT_METADATA)
		Assert.assertEquals(TestUtil.parseResponseBody(registrationResponse,AUConstants.ERROR_DESCRIPTION),
						"Request failed due to unknown or invalid Client")
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
