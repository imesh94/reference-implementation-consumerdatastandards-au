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

package com.wso2.openbanking.cds.demo.backend.services;

import com.wso2.openbanking.cds.demo.backend.BankException;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * BankAccountService class
 */
@Path("/bankaccountservice/")
public class BankAccountService {
    // accounts api with joint account info
    private static final String accountList = "{\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"accountId\": \"30080012343456\",\n" +
            "            \"displayName\": \"account_1\",\n" +
            "            \"authorizationMethod\": \"single\",\n" +
            "            \"nickName\": \"not-working\",\n" +
            "            \"customerAccountType\": \"Individual\",\n" +
            "            \"type\": \"TRANS_AND_SAVINGS_ACCOUNTS\",\n" +
            "            \"isEligible\": true,\n" +
            "            \"isJointAccount\": false,\n" +
            "            \"jointAccountConsentElectionStatus\": false\n" +
            "        },\n" +
            "        {\n" +
            "            \"accountId\": \"30080098763459\",\n" +
            "            \"displayName\": \"account_2\",\n" +
            "            \"authorizationMethod\": \"single\",\n" +
            "            \"nickName\": \"not-working\",\n" +
            "            \"customerAccountType\": \"Individual\",\n" +
            "            \"type\": \"TRANS_AND_SAVINGS_ACCOUNTS\",\n" +
            "            \"isEligible\": true,\n" +
            "            \"isJointAccount\": false,\n" +
            "            \"jointAccountConsentElectionStatus\": false\n" +
            "        },\n" +
            "        {\n" +
            "            \"accountId\": \"650-000 N1232\",\n" +
            "            \"displayName\": \"joint_account_1\",\n" +
            "            \"authorizationMethod\": \"single\",\n" +
            "            \"nickName\": \"not-working\",\n" +
            "            \"customerAccountType\": \"Individual\",\n" +
            "            \"type\": \"TRANS_AND_SAVINGS_ACCOUNTS\",\n" +
            "            \"isEligible\": true,\n" +
            "            \"isJointAccount\": true,\n" +
            "            \"jointAccountConsentElectionStatus\": \"ELECTED\",\n" +
            "            \"jointAccountinfo\": {\n" +
            "              \"linkedMember\": [\n" +
            "                {\n" +
            "                  \"memberId\": \"amy@gold.com@carbon.super\",\n" +
            "                  \"meta\": {}\n" +
            "                },\n" +
            "                {\n" +
            "                  \"memberId\": \"ann@gold.com@carbon.super\",\n" +
            "                  \"meta\": {}\n" +
            "                }" +
            "              ]\n" +
            "            },\n" +
            "            \"meta\": {}\n" +
            "        },\n" +
            "        {\n" +
            "            \"accountId\": \"750-010 N1544\",\n" +
            "            \"displayName\": \"joint_account_2\",\n" +
            "            \"authorizationMethod\": \"single\",\n" +
            "            \"nickName\": \"joint-account-2\",\n" +
            "            \"customerAccountType\": \"Individual\",\n" +
            "            \"type\": \"TRANS_AND_SAVINGS_ACCOUNTS\",\n" +
            "            \"isEligible\": true,\n" +
            "            \"isJointAccount\": true,\n" +
            "            \"jointAccountConsentElectionStatus\": \"UNAVAILABLE\",\n" +
            "            \"jointAccountinfo\": {\n" +
            "                \"linkedMember\": [\n" +
            "                    {\n" +
            "                        \"memberId\": \"amy@gold.com@carbon.super\",\n" +
            "                        \"meta\": {}\n" +
            "                    }\n" +
            "                ]\n" +
            "            },\n" +
            "            \"meta\": {}\n" +
            "        }" +
            "    ]\n" +
            "}";
    @GET
    @Path("/payable-accounts")
    @Produces("application/json; charset=utf-8")
    public Response getPayableAccounts() throws BankException {
        return Response.status(200).entity(accountList).build();
    }

    @GET
    @Path("/sharable-accounts")
    @Produces("application/json; charset=utf-8")
    public Response getSharableAccounts() throws BankException {
        return Response.status(200).entity(accountList).build();
    }

    @POST
    @Path("/payment-charges")
    @Produces("application/json; charset=utf-8")
    @Consumes("application/json")
    public Response calculatePaymentCharges(String request) throws BankException {
        if (StringUtils.isNotBlank(request)) {
            String response = "{\n" +
                    "    \"payment_charges\": \"1.0\",\n" +
                    "    \"payment_currency\": \"GBP\",\n" +
                    "    \"payment_exchange_rate\": \"0.1\"\n" +
                    "}";

            return Response.status(200).entity(response).build();
        }
        return Response.status(403).build();
    }
}
