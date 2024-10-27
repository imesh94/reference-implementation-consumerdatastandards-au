/**
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
 * <p>
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.openbanking.cds.identity.filter;

import org.wso2.openbanking.cds.identity.utils.CDSIdentityConstants;

import javax.servlet.ServletRequest;

/**
 * Authorize Data Publishing Filter.
 * Implements custom logic related to publishing /authorize request data.
 */
public class AuthorizeDataPublishingFilter extends InfoSecDataPublishingFilter {

    @Override
    public boolean shouldPublishCurrentRequestData(ServletRequest request) {

        // If the sessionDataKey query parameter is present, it is an internal redirect and should not be published.
        return request.getParameter(CDSIdentityConstants.SESSION_DATA_KEY_PARAMETER) == null &&
                super.shouldPublishCurrentRequestData(request);
    }
}
