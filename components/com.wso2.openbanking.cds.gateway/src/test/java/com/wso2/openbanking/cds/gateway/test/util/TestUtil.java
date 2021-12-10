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

package com.wso2.openbanking.cds.gateway.test.util;

import com.wso2.openbanking.accelerator.common.util.OpenBankingUtils;
import com.wso2.openbanking.accelerator.gateway.executor.core.OpenBankingGatewayExecutor;
import com.wso2.openbanking.cds.gateway.test.TestConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtil {

    public static Map<String, List<OpenBankingGatewayExecutor>> initExecutors() {

        Map<String, List<OpenBankingGatewayExecutor>> executors = new HashMap<>();
        Map<String, Map<Integer, String>> fullValidatorMap = TestConstants.FULL_VALIDATOR_MAP;
        for (Map.Entry<String, Map<Integer, String>> stringMapEntry : fullValidatorMap.entrySet()) {
            List<OpenBankingGatewayExecutor> executorList = new ArrayList<>();
            Map<Integer, String> executorNames = stringMapEntry.getValue();
            for (Map.Entry<Integer, String> executorEntity : executorNames.entrySet()) {
                OpenBankingGatewayExecutor object = (OpenBankingGatewayExecutor)
                        OpenBankingUtils.getClassInstanceFromFQN(executorEntity.getValue());
                executorList.add(object);
            }
            executors.put(stringMapEntry.getKey(), executorList);
        }
        return executors;

    }
}
