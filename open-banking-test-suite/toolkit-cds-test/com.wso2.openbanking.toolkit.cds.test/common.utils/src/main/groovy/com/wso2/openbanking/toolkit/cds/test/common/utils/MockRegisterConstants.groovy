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

package com.wso2.openbanking.toolkit.cds.test.common.utils

import com.wso2.openbanking.test.framework.util.ConfigParser


/**
 * Test Constants related to CDR Mock Register.
 */
class MockRegisterConstants {

    static final String MOCK_REGISTER_HOST = ConfigParser.getInstance().getMockCDRRegisterHostName()
    static final String INFO_SEC_BASE_URL = "https://"+ MOCK_REGISTER_HOST + ":7001"
    static final String ADMIN_BASE_URL = "https://"+ MOCK_REGISTER_HOST + ":7006"

    static final String TOKEN_ENDPOINT = "/idp/connect/token"
    public static final String MOCK_DR_CLIENT_ASSERTION_ENDPOINT = "/loopback/MockDataRecipientClientAssertion"
    public static final String SSA_ENDPOINT = "/cdr-register/v1/banking/data-recipients/brands"
    public static final String METADATA_ENDPOINT = "/admin/metadata"

    //Mock ADR brandIds and their software products loaded at the CDR mock Register
    static final ADR_BRAND_ID_1 = "20c0864b-ceef-4de0-8944-eb0962f825eb";
    static final ADR_BRAND_ID_1_SOFTWARE_PRODUCT_1 = "63bc22ac-6fd2-4e85-a979-c2fc7c4db9da"
    static final ADR_BRAND_ID_1_SOFTWARE_PRODUCT_2 = "86ecb655-9eba-409c-9be3-59e7adf7080d"
    static final ADR_BRAND_ID_1_SOFTWARE_PRODUCT_3 = "9381dad2-6b68-4879-b496-c1319d7dfbc9"
    static final ADR_BRAND_ID_1_SOFTWARE_PRODUCT_4 = "d3c44426-e003-4604-aa45-4137e45dfbc4"

    static final ADR_BRAND_ID_2 = "ebbcc2f2-817e-42b8-8a28-cd45902159e0";
    static final ADR_BRAND_ID_2_SOFTWARE_PRODUCT_1 = "5d03d1a6-b83b-4176-a2f4-d0074a205695"
    static final ADR_BRAND_ID_2_SOFTWARE_PRODUCT_2 = "dafa09db-4433-4203-907a-bdf797c8cd21"

}
