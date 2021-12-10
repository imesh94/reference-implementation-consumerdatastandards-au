/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.identity.filter.util;

import com.wso2.openbanking.accelerator.identity.util.IdentityCommonConstants;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.wso2.carbon.identity.core.util.IdentityUtil;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletOutputStream;

public class TestUtil {

    public static Map<String, String> getResponse(ServletOutputStream outputStream) {

        Map<String, String> response = new HashMap<>();
        JSONObject outputStreamMap = new JSONObject(outputStream);
        JSONObject targetStream = new JSONObject(outputStreamMap.get(TestConstants.TARGET_STREAM).toString());
        response.put(IdentityCommonConstants.OAUTH_ERROR,
                targetStream.get(IdentityCommonConstants.OAUTH_ERROR).toString());
        response.put(IdentityCommonConstants.OAUTH_ERROR_DESCRIPTION,
                targetStream.get(IdentityCommonConstants.OAUTH_ERROR_DESCRIPTION).toString());
        return response;
    }

    public static X509Certificate getCertificate(String certificateContent) {

        if (StringUtils.isNotBlank(certificateContent)) {
            // Build the Certificate object from cert content.
            try {
                return (X509Certificate) IdentityUtil.convertPEMEncodedContentToCertificate(certificateContent);
            } catch (CertificateException e) {
                //do nothing
            }
        }
        return null;
    }
}
