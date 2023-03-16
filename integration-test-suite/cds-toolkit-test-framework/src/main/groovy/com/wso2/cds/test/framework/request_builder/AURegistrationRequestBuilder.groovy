/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.cds.test.framework.request_builder

import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.openbanking.test.framework.request_builder.OBRegistrationRequestBuilder
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import io.restassured.RestAssured
import io.restassured.config.EncoderConfig
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.json.JSONArray
import com.wso2.cds.test.framework.utility.AUMockCDRIntegrationUtil
import com.wso2.cds.test.framework.utility.AURestAsRequestBuilder
import com.wso2.cds.test.framework.utility.AUTestUtil

/**
 * Class that provides DCR functions for tests
 */
class AURegistrationRequestBuilder extends OBRegistrationRequestBuilder {

    private AUConfigurationService auConfiguration

    private String SSA
    private String softwareProductId

    AURegistrationRequestBuilder() {
        auConfiguration = new AUConfigurationService()
    }

    /**
     * Provide Software ID
     * Helper function for other functions
     * @return
     */
    String getSoftwareID() {
        if (softwareProductId == null) {
            softwareProductId = ((auConfiguration.getMockCDREnabled())
                    ? AUConstants.MOCK_ADR_BRAND_ID_1_SOFTWARE_PRODUCT_1 : AUConstants.DCR_SOFTWARE_PRODUCT_ID)
        }
        return softwareProductId
    }

    /**
     * Provide SSA from file
     * Helper function for other functions
     * @return
     */
    String getSSA() {
        if (SSA == null) {
            if (auConfiguration.getMockCDREnabled()) {
                SSA = AUMockCDRIntegrationUtil.getSSAFromMockCDRRegister(AUConstants.MOCK_ADR_BRAND_ID_1, softwareProductId)
            } else {
                SSA = AUConstants.DCR_SSA
            }
        }
        return SSA
    }

    void setSoftwareID(String id) {
        softwareProductId = id
    }

    void setSSA(String statement) {
        SSA = statement
    }

    /**
     * Provide subscription payload for DCR
     * @return
     */
    static String getSubscriptionPayload(String applicationId, String apiId) {
        return """
            {
              "applicationId": "$applicationId",
              "apiId": "$apiId",
              "throttlingPolicy": "Unlimited"
            }
            """.stripIndent()
    }

    /**
     * Provide regular payload for DCR
     * @return
     */
    String getAURegularClaims() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc().getClaimsJsonAsString()
    }

    /**
     * Provide regular payload for DCR
     * @return
     * @param softID
     * @param ssa
     * @return
     */
    String getAURegularClaims(String softID, String ssa) {
        return regularClaims.addIssuer(softID).addSoftwareStatement(ssa)
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc().getClaimsJsonAsString()
    }

    /**
     * Provide regular payload for DCR
     * @return
     * @param softID
     * @param ssa
     * @param redirectURI
     * @return
     */
    String getAURegularClaims(String softID, String ssa, String redirectURI) {
        return regularClaims.addIssuer(softID).addSoftwareStatement(ssa)
                .addCustomRedirectURI(new JSONArray().put(redirectURI))
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc().getClaimsJsonAsString()
    }

    String getClaimsWithoutAud() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc()
                .removeKeyValue(AUConstants.AUDIENCE_KEY).getClaimsJsonAsString()
    }

    String getRegularClaimsWithNonMatchingRedirectUri() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addCustomRedirectURI(new JSONArray().put("https://www.google.com/redirects/non-matching-redirect"))
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc().getClaimsJsonAsString()
    }

    String getRegularClaimsWithNewRedirectUri() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addCustomRedirectURI(new JSONArray().put(AUConstants.DCR_ALTERNATE_REDIRECT_URI))
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc().getClaimsJsonAsString()
    }

    String getClaimsWithoutTokenEndpointAuthSigningAlg() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc()
                .removeKeyValue(AUConstants.TOKEN_ENDPOINT_AUTH_SIGNING_ALG_KEY).getClaimsJsonAsString()
    }

    String getClaimsWithoutTokenEndpointAuthMethod() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc()
                .removeKeyValue(AUConstants.TOKEN_ENDPOINT_AUTH_METHOD_KEY).getClaimsJsonAsString()
    }

    String getClaimsWithoutGrantTypes() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc()
                .removeKeyValue(AUConstants.GRANT_TYPES_KEY).getClaimsJsonAsString()
    }

    String getClaimsWithoutResponseTypes() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc()
                .removeKeyValue(AUConstants.RESPONSE_TYPES_KEY).getClaimsJsonAsString()
    }

    String getClaimsWithoutSSA() {
        return regularClaims.addIssuer(getSoftwareID())
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc()
                .removeKeyValue(AUConstants.SOFTWARE_STATEMENT_KEY).getClaimsJsonAsString()
    }

    String getClaimsWithoutIdTokenAlg() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseEnc().getClaimsJsonAsString()
    }

    String getClaimsWithoutIdTokenEnc() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg().getClaimsJsonAsString()
    }

    String getClaimsWithInvalidIdTokenAlg() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg("RSA-OAEP-X").addIDTokenEncResponseEnc()
                .getClaimsJsonAsString()
    }

    String getClaimsWithInvalidIdTokenEnc() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc("A128GCM")
                .getClaimsJsonAsString()
    }

    String getClaimsWithNonMatchingSoftwareIDandISS() {
        return regularClaims.addIssuer("Mock Company").addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc()
                .getClaimsJsonAsString()
    }

    String getRegularClaimsWithGivenJti(String jti) {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc()
                .addJti(jti).getClaimsJsonAsString()
    }

    String getClaimsWithUnsupportedTokenEndpointAuthMethod() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc()
                .addTokenEndpointAuthMethod("mutual_tls").getClaimsJsonAsString()
    }

    String getRegularClaimsWithInvalidGrantTypes() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc()
                .addGrantType(new JSONArray().put("urn:ietf:params:oauth:grant-type:jwt-bearer")).getClaimsJsonAsString()
    }

    String getRegularClaimsWithInvalidResponseTypes() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc()
                .addCustomResponseTypes(new JSONArray().put("code")).getClaimsJsonAsString()
    }

    String getRegularClaimsWithUnsupportedApplicationType() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc()
                .addApplicationType("Mobile").getClaimsJsonAsString()
    }

    String getRegularClaimsWithoutRequestObjectSigningAlg() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc()
                .removeKeyValue(AUConstants.REQUEST_OBJECT_SIGNING_ALG_KEY).getClaimsJsonAsString()
    }

    String getRegularClaimsWithMalformedSSA() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement("fejkfhweuifhuiweufwhfweiio")
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc()
                .getClaimsJsonAsString()
    }

    String getRegularClaimsWithoutRedirectUris() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc()
                .removeKeyValue(AUConstants.REDIRECT_URIS_KEY).getClaimsJsonAsString()
    }

    String getRegularClaimsWithFieldsNotSupported() {
        return regularClaims.addIssuer(getSoftwareID()).addSoftwareStatement(getSSA())
                .addIDTokenEncResponseAlg().addIDTokenEncResponseEnc()
                .addCustomValue("adr_name", "ADR").getClaimsJsonAsString()
    }

    /**
     * Get a basic request.
     *
     * @param accessToken
     * @return
     */
    static RequestSpecification buildBasicRequest(String accessToken) {

        return AURestAsRequestBuilder.buildRequest()
                .header("charset", "UTF-8")
                .header(AUConstants.AUTHORIZATION_HEADER_KEY, "${AUConstants.AUTHORIZATION_BEARER_TAG}${accessToken}")
                .accept("application/json")
                .config(RestAssured.config()
                        .sslConfig(RestAssured.config().getSSLConfig().sslSocketFactory(AUTestUtil.getSslSocketFactory()))
                        .encoderConfig(new EncoderConfig().encodeContentTypeAs(
                                "application/jwt", ContentType.TEXT)))
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.DCR_BASE_PATH_TYPE))
    }

    /**
     * Get a registration request for application creation.
     *
     * @param accessToken
     * @return
     */
    static RequestSpecification buildRegistrationRequest(String claims) {
        AUJWTGenerator generator = new AUJWTGenerator()
        return AURestAsRequestBuilder.buildRequest()
                .contentType("application/jwt")
                .body(generator.getSignedRequestObject(claims))
                .accept("application/json")
                .config(RestAssured.config()
                        .sslConfig(RestAssured.config().getSSLConfig().sslSocketFactory(AUTestUtil.getSslSocketFactory()))
                        .encoderConfig(new EncoderConfig().encodeContentTypeAs(
                                "application/jwt", ContentType.TEXT)))
                .baseUri(AUTestUtil.getBaseUrl(AUConstants.DCR_BASE_PATH_TYPE))
    }

}

