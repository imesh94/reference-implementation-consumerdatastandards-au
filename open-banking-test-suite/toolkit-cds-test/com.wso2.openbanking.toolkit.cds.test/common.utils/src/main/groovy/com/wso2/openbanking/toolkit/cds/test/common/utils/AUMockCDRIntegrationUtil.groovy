/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */


package com.wso2.openbanking.toolkit.cds.test.common.utils

import com.wso2.openbanking.test.framework.TestSuite
import com.wso2.openbanking.test.framework.util.TestConstants
import com.wso2.openbanking.test.framework.util.TestUtil
import com.wso2.openbanking.test.framework.util.ConfigParser
import groovy.json.JsonSlurper
import io.restassured.RestAssured
import org.testng.Assert

import java.util.logging.Logger

/**
 * The util class to invoke the Mock CDR Registration endpoints
 */
class AUMockCDRIntegrationUtil {

    static log = Logger.getLogger(AUMockCDRIntegrationUtil.class.toString())

    /**
     * Invoke the admin API to load custom metadata from MockCDRRegisterMetaData.json to the Mock CDR Register
     *
     * @return boolean
     */
    static boolean loadMetaDataToCDRRegister() {

        if (ConfigParser.getInstance().mockCDRRegisterEnabled) {
            def inputFile = new File(ConfigParser.getInstance().getMetaDataFileLocationForMockCDRRegister())
            def inputArray = new JsonSlurper().parseText(inputFile.text)
            def payloadJson = groovy.json.JsonOutput.toJson(inputArray)

            RestAssured.baseURI = MockRegisterConstants.ADMIN_BASE_URL
            def MetaDataLoadResponse =TestSuite.buildRequestToMockCDRRegister(false)
                    .contentType(TestConstants.CONTENT_TYPE_APPLICATION_JSON)
                    .body(payloadJson)
                    .post(MockRegisterConstants.METADATA_ENDPOINT)

            if (Assert.assertEquals(MetaDataLoadResponse.statusCode(), AUConstants.STATUS_CODE_200)) {
                return true
            }
            return false
        }
       return true
    }

    /**
     * Invoke the admin API to update custom metadata of MockCDRRegisterMetaData.json to the Mock CDR Register
     *
     * @return boolean
     */
    static boolean updateMetaDataOfCDRRegister(boolean changeStatus_SP, boolean changeStatus_ADR, int spStatus = 1, int adrStatus = 1) {

        if (ConfigParser.getInstance().mockCDRRegisterEnabled) {
            def inputFile = new File(ConfigParser.getInstance().getMetaDataFileLocationForMockCDRRegister())
            def inputArray = new JsonSlurper().parseText(inputFile.text)

            if(changeStatus_SP) {
                inputArray["LegalEntities"][0]["Participations"][0]["Brands"][0]["SoftwareProducts"][0]["StatusId"] = spStatus
            }

            if(changeStatus_ADR) {
                inputArray["LegalEntities"][0]["Participations"][0]["StatusId"] = adrStatus
            }

            def payloadJson = groovy.json.JsonOutput.toJson(inputArray)

            RestAssured.baseURI = MockRegisterConstants.ADMIN_BASE_URL;
            def MetaDataLoadResponse =TestSuite.buildRequestToMockCDRRegister(false)
                    .contentType(TestConstants.CONTENT_TYPE_APPLICATION_JSON)
                    .body(payloadJson)
                    .post(MockRegisterConstants.METADATA_ENDPOINT)

            if (Assert.assertEquals(MetaDataLoadResponse.statusCode(), AUConstants.STATUS_CODE_200)) {
                return true
            }
            return false
        }
        return true
    }

    /**
     * Invoke the SSA API to retrieve the SSA for a given softwareProductId and ADRBrandId
     *
     * @param accessToken
     * @param ADRBrandId the Brand Id of the ADR client
     * @param softwareProductId the software product Id of the ADR client
     * @return SSA String
     */
    static String getSSAFromMockCDRRegister(String ADRBrandId, String softwareProductId) {

        def accessToken = getApplicationTokenFromMockCDRRegister(softwareProductId)

        RestAssured.baseURI = MockRegisterConstants.INFO_SEC_BASE_URL;
        def SSAResponse =TestSuite.buildRequestToMockCDRRegister(true)
                .header(TestConstants.AUTHORIZATION_HEADER_KEY, "Bearer ${accessToken}")
                .get("${MockRegisterConstants.SSA_ENDPOINT}/${ADRBrandId}/software-products/${softwareProductId}/ssa")

        return SSAResponse.getBody().asString()

    }

    /**
     * Get Application Access Token to invoke the protected API endpoints
     *
     * @param softwareProductId the software product Id of the ADR client
     * @return access token
     */
    static String getApplicationTokenFromMockCDRRegister(String softwareProductId) {

        String clientAssertion = generateClientAssertionFromMockCDRRegister(softwareProductId).toString()

        def payload = ""
        def delimiter = "&"
        payload = payload.concat(TestConstants.GRANT_TYPE_KEY + "=" + "client_credentials" + delimiter)
                .concat(TestConstants.CLIENT_ID_KEY + "=" + softwareProductId + delimiter)
                .concat(TestConstants.CLIENT_ASSERTION_TYPE_KEY + "=" + "urn:ietf:params:oauth:client-assertion-type:jwt-bearer" + delimiter)
                .concat(TestConstants.CLIENT_ASSERTION_KEY + "=" + clientAssertion + delimiter)
                .concat(TestConstants.SCOPE_KEY + "=" + "cdr-register:bank:read")

        RestAssured.baseURI = MockRegisterConstants.INFO_SEC_BASE_URL;
        def tokenResponse = TestSuite.buildRequestToMockCDRRegister(true).contentType(TestConstants.ACCESS_TOKEN_CONTENT_TYPE)
                .body(payload)
                .post(MockRegisterConstants.TOKEN_ENDPOINT)

        def accessToken = TestUtil.parseResponseBody(tokenResponse, "access_token")
        log.info("Got access token $accessToken")

        return accessToken
    }

    /**
     * Invoke mock DR client assertion endpoint to generate the client assertion for a given softwareProductId
     *
     * @param softwareProductId the software product Id of the ADR client
     * @return
     */
    static String generateClientAssertionFromMockCDRRegister(String softwareProductId) {

        RestAssured.baseURI = MockRegisterConstants.ADMIN_BASE_URL;
        def clientAssertionResponse =TestSuite.buildRequestToMockCDRRegister(false)
                .queryParam("iss", softwareProductId)
                .get("${MockRegisterConstants.MOCK_DR_CLIENT_ASSERTION_ENDPOINT}")

        return clientAssertionResponse.getBody().asString()

    }
}
