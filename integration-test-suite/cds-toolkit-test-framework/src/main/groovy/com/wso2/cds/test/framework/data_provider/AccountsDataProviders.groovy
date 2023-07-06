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

package com.wso2.cds.test.framework.data_provider

import com.wso2.cds.test.framework.constant.AUConstants
import org.testng.annotations.DataProvider

/**
 *  Data provide class for Accounts tests
 */
class AccountsDataProviders {

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

}

