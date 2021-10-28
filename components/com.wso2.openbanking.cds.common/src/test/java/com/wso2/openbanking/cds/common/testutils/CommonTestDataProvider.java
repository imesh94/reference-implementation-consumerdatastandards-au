/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.common.testutils;

import com.wso2.openbanking.cds.common.error.handling.models.CDSErrorMeta;
import com.wso2.openbanking.cds.common.error.handling.util.ErrorConstants;
import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;

/**
 * Data Provider for CDS Common Tests
 */
public class CommonTestDataProvider {

    @DataProvider(name = "ClientErrorTestDataProvider")
    Object[][] getClientErrorTestDataProvider() {

        return new Object[][]{
            {"400", true},
            {"200", false},
            {"500", false}
        };
    }

    @DataProvider(name = "HttpsCodeTestDataProvider")
    Object[][] getHttpsCodeTestDataProvider() {

        return new Object[][]{
                {"400", HttpStatus.SC_BAD_REQUEST},
                {"401", HttpStatus.SC_UNAUTHORIZED},
                {"415", HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE},
                {"403", HttpStatus.SC_FORBIDDEN},
                {"404", HttpStatus.SC_NOT_FOUND},
                {"406", HttpStatus.SC_NOT_ACCEPTABLE},
                {"422", HttpStatus.SC_UNPROCESSABLE_ENTITY}
        };
    }

    @DataProvider(name = "ErrorObjectTestDataProvider")
    Object[][] getErrorObjectTestDataProvider() {

        return new Object[][]{
                {ErrorConstants.AUErrorEnum.CLIENT_AUTH_FAILED, "Client authentication failed", new CDSErrorMeta()},
                {ErrorConstants.AUErrorEnum.UNEXPECTED_ERROR, "Unexpected error", new CDSErrorMeta()},
                {ErrorConstants.AUErrorEnum.BAD_REQUEST, "Bad request", new CDSErrorMeta()}
        };
    }
}
