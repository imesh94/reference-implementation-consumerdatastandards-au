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

package com.wso2.openbanking.toolkit.cds.test.common.utils

import com.wso2.openbanking.test.framework.util.AppConfigReader
import com.wso2.openbanking.test.framework.util.ConfigParser

/**
 * AU dynamic client registration constants.
 */
class AUDCRConstants {

    static final String REGISTRATION_ENDPOINT = "/open-banking/0.2/register/"

    static final String SSA = new File(AppConfigReader.getSSAFilePath()).text
    static final String SOFTWARE_PRODUCT_ID = AppConfigReader.getSoftwareId()
    static final String REDIRECT_URI = AppConfigReader.getDcrRedirectUri()
    static final String ALTERNATE_REDIRECT_URI = AppConfigReader.getAlternateRedirectUri()
    static final String AUD_VALUE = ConfigParser.getInstance().getAudienceValue()

    static final BASE_PATH_TYPE_DCR = "DCR"
    static final INVALID_CLIENT_METADATA = "invalid_client_metadata"
    static final INVALID_REDIRECT_URI = "invalid_redirect_uri"
    static final UNAPPROVED_SOFTWARE_STATEMENT = "unapproved_software_statement"

    //static final String SSA = "eyJhbGciOiJQUzI1NiIsImtpZCI6ImI4ZmFjZjJmZjM5NDQ0Zjc4MWUwYmU1ZGI0YjE0ZjE2IiwidHlwIjoiSldUIn0.eyJpc3MiOiJjZHItcmVnaXN0ZXIiLCJpYXQiOjE1NzE4MDgxNjcsImV4cCI6MjE0NzQ4MzY0NiwianRpIjoiM2JjMjA1YTFlYmM5NDNmYmI2MjRiMTRmY2IyNDExOTYiLCJvcmdfaWQiOiIzQjBCMEE3Qi0zRTdCLTRBMkMtOTQ5Ny1FMzU3QTcxRDA3QzgiLCJvcmdfbmFtZSI6Ik1vY2sgQ29tcGFueSBJbmMuIiwiY2xpZW50X25hbWUiOiJNb2NrIFNvZnR3YXJlIiwiY2xpZW50X2Rlc2NyaXB0aW9uIjoiQSBtb2NrIHNvZnR3YXJlIHByb2R1Y3QgZm9yIHRlc3RpbmcgU1NBIiwiY2xpZW50X3VyaSI6Imh0dHBzOi8vd3d3Lm1vY2tjb21wYW55LmNvbS5hdSIsInJlZGlyZWN0X3VyaXMiOlsiaHR0cHM6Ly93d3cuZ29vZ2xlLmNvbS9yZWRpcmVjdHMvcmVkaXJlY3QxIiwiaHR0cHM6Ly93d3cuZ29vZ2xlLmNvbS9yZWRpcmVjdHMvcmVkaXJlY3QyIl0sImxvZ29fdXJpIjoiaHR0cHM6Ly93d3cubW9ja2NvbXBhbnkuY29tLmF1L2xvZ29zL2xvZ28xLnBuZyIsInRvc191cmkiOiJodHRwczovL3d3dy5tb2NrY29tcGFueS5jb20uYXUvdG9zLmh0bWwiLCJwb2xpY3lfdXJpIjoiaHR0cHM6Ly93d3cubW9ja2NvbXBhbnkuY29tLmF1L3BvbGljeS5odG1sIiwiandrc191cmkiOiJodHRwczovL2tleXN0b3JlLm9wZW5iYW5raW5ndGVzdC5vcmcudWsvMDAxNTgwMDAwMUhRUXJaQUFYL3NGZ2tpNzJPaXF3Wk5GT1pnNk9hamkuandrcyIsInJldm9jYXRpb25fdXJpIjoiaHR0cHM6Ly9rZXlzdG9yZS5vcGVuYmFua2luZ3Rlc3Qub3JnLnVrLzAwMTU4MDAwMDFIUVFyWkFBWC9yZXZva2VkL0tia3lwQUdwNWxWZWZHZWJmY1hNMTcuandrcyIsInNvZnR3YXJlX2lkIjoiNzQwQzM2OEYtRUNGOS00RDI5LUEyRUEtMDUxNEE2NkIwQ0RFIiwic29mdHdhcmVfcm9sZXMiOiJkYXRhLXJlY2lwaWVudC1zb2Z0d2FyZS1wcm9kdWN0Iiwic2NvcGUiOiJiYW5rOmFjY291bnRzLmJhc2ljOnJlYWQgYmFuazphY2NvdW50cy5kZXRhaWw6cmVhZCBiYW5rOnRyYW5zYWN0aW9uczpyZWFkIGJhbms6cGF5ZWVzOnJlYWQgYmFuazpyZWd1bGFyX3BheW1lbnRzOnJlYWQgY29tbW9uOmN1c3RvbWVyLmJhc2ljOnJlYWQgY29tbW9uOmN1c3RvbWVyLmRldGFpbDpyZWFkIGNkcjpyZWdpc3RyYXRpb24ifQ.RP1jj4xFiDI4dvUMfDj4i8CWKSibpry2u1NL7sYkW9cT4Ou7DP5K7bu65i5B4TD4XFdlYZh8dMHmwV0lAAkrhbaG-lPFYKPmzKrCwoTf9jZie6GNrZ8EsHmYYZpGQ4Q-sEKi7sD8lGxPdhh8vzfRTe42Ogei9jM5sOa_lZ2D_6LXQNN0_4llnExD0IMud3qbeyn31-4CatsHILN8zOHOm4imdZWp4x8tcuy6hdEadl53N6vzRZVk9d1UzjcuKxy3Mf1bFFJgRt6_MQY8SxTDzSRUVlwMbIIRji6mKctATlhBpA2po0uDEamrAcFMXcTKq0lGDGP0yWPvTXqOJQTAvA"

}
