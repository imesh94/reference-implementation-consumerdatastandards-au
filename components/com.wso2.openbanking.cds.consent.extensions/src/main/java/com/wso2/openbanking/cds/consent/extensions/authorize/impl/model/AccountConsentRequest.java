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
package com.wso2.openbanking.cds.consent.extensions.authorize.impl.model;

/**
 * The Class Account consent request.
 */
public class AccountConsentRequest {

    private String requestId;

    private AccountData accountData;

    private AccountRisk risk;

    public AccountData getAccountData() {

        return accountData;
    }

    public void setAccountData(AccountData accountData) {

        this.accountData = accountData;
    }

    public AccountRisk getRisk() {

        return risk;
    }

    public void setRisk(AccountRisk risk) {

        this.risk = risk;
    }

    public String getRequestId() {

        return requestId;
    }

    public void setRequestId(String requestId) {

        this.requestId = requestId;
    }
}
