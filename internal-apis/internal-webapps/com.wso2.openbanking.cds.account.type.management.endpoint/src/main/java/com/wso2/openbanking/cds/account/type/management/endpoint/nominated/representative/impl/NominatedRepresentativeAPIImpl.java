/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.impl;


import com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.api.NominatedRepresentativeAPI;

import javax.ws.rs.core.Response;

/**
 * Implementation of NominatedRepresentativeAPI.
 */
public class NominatedRepresentativeAPIImpl implements NominatedRepresentativeAPI {

    public Response updateNominatedRepresentativePermissions(String requestBody) {
        return null;
    }

    public Response revokeNominatedRepresentativePermissions(String requestBody) {
        return null;
    }

    public Response retrieveNominatedRepresentativePermissions(String userId, String accountIds) {
        return null;
    }

    public Response retrieveNominatedRepresentativeProfiles(String userId) {
        return null;
    }

}
