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

package com.wso2.cds.test.framework.utility

import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.bfsi.test.framework.exception.TestFrameworkException
import com.wso2.openbanking.test.framework.configuration.OBConfigurationService
import com.wso2.openbanking.test.framework.utility.RestAsRequestBuilder
import io.restassured.RestAssured
import io.restassured.config.EncoderConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.apache.http.conn.ssl.SSLSocketFactory

/**
 * AU Class for provide Basic Rest-assured Request Objects
 */
class AURestAsRequestBuilder extends RestAsRequestBuilder{

    private static OBConfigurationService configurationService = new OBConfigurationService()

    /**
     * Get Base Request Specification to invoke mock CDR register.
     *
     * @return request specification.
     */
    static RequestSpecification buildRequestToMockCDRRegister(boolean isMTLSRequired) throws TestFrameworkException {

        if (isMTLSRequired) {
            RestAssuredConfig config = null;
            SSLSocketFactory sslSocketFactory = AUTestUtil.getSslSocketFactoryForMockCDRRegister();

            if (sslSocketFactory != null) {

                config = RestAssuredConfig.newConfig().sslConfig(RestAssured.config()
                        .getSSLConfig()
                        .sslSocketFactory(AUTestUtil.getSslSocketFactoryForMockCDRRegister()));
            } else {
                throw new TestFrameworkException("Unable to retrieve the SSL socket factory");
            }
            return RestAssured.given()
                    .config(config.encoderConfig(EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(AUConstants.CONTENT_TYPE_APPLICATION_JWT, ContentType.TEXT)))
                    .urlEncodingEnabled(true);

        } else {
            // Use relaxed HTTPS validation if MTLS is disabled.
            return RestAssured.given()
                    .relaxedHTTPSValidation()
                    .urlEncodingEnabled(true);
        }
    }
}
