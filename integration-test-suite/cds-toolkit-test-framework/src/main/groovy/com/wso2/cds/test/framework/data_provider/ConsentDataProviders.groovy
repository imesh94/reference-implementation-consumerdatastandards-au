/*
 * Copyright (c) 2022 - 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.cds.test.framework.data_provider

import com.wso2.cds.test.framework.constant.AUConstants
import org.openqa.selenium.remote.http.HttpMethod
import org.testng.annotations.DataProvider

/**
 *  Data provide class for Accounts tests
 */
class ConsentDataProviders {

    @DataProvider(name = "AccountsRetrievalFlow")
    Object[] getAccountsRetrievalFlow() {

        def accounts = new ArrayList<>()
        accounts.add(AUConstants.BULK_ACCOUNT_PATH as Object)
        accounts.add(AUConstants.BULK_BALANCES_PATH as Object)
        accounts.add(AUConstants.GET_TRANSACTIONS as Object)
        return accounts
    }

    @DataProvider(name = "BankingApis")
    Object[] getBankingAPis() {

        def accounts = new ArrayList<>()
        accounts.add(AUConstants.BULK_ACCOUNT_PATH as Object)
        accounts.add(AUConstants.BULK_BALANCES_PATH as Object)
        accounts.add(AUConstants.GET_TRANSACTIONS as Object)
        accounts.add(AUConstants.BULK_DIRECT_DEBITS_PATH as Object)
        accounts.add(AUConstants.BULK_SCHEDULE_PAYMENTS_PATH as Object)
        accounts.add(AUConstants.BULK_PAYEES as Object)
        return accounts
    }

    @DataProvider(name = "BankingApisBusinessProfile")
    Object[] getBankingApisBusinessProfile() {

        def accounts = new ArrayList<>()
        accounts.add(AUConstants.BULK_ACCOUNT_PATH as Object)
        accounts.add(AUConstants.BULK_BALANCES_PATH as Object)
        accounts.add(AUConstants.GET_BUSINESS_ACCOUNT_TRANSACTIONS as Object)
        accounts.add(AUConstants.BULK_DIRECT_DEBITS_PATH as Object)
        accounts.add(AUConstants.BULK_SCHEDULE_PAYMENTS_PATH as Object)
        accounts.add(AUConstants.BULK_PAYEES as Object)
        return accounts
    }

    @DataProvider(name = "httpMethods")
    Object[] getHttpMethods() {

        def httpMethod = new ArrayList<>()
        httpMethod.add(AUConstants.HTTP_METHOD_PATCH as Object)
        httpMethod.add(AUConstants.HTTP_METHOD_HEAD as Object)
        httpMethod.add(AUConstants.HTTP_METHOD_OPTIONS as Object)
        httpMethod.add(AUConstants.HTTP_METHOD_TRACE as Object)
        httpMethod.add(AUConstants.HTTP_METHOD_CONNECT as Object)
        return httpMethod
    }

    @DataProvider(name = "unsupportedHttpMethods")
    Object[] getUnsupportedHttpMethods() {

        def httpMethod = new ArrayList<>()
        httpMethod.add(AUConstants.HTTP_METHOD_COPY as Object)
        httpMethod.add(AUConstants.HTTP_METHOD_LINK as Object)
        httpMethod.add(AUConstants.HTTP_METHOD_UNLINK as Object)
        httpMethod.add(AUConstants.HTTP_METHOD_PURGE as Object)
        httpMethod.add(AUConstants.HTTP_METHOD_LOCK as Object)
        httpMethod.add(AUConstants.HTTP_METHOD_UNLOCK as Object)
        httpMethod.add(AUConstants.HTTP_METHOD_PROPFIND as Object)
        httpMethod.add(AUConstants.HTTP_METHOD_VIEW as Object)
        return httpMethod
    }

}

