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

package com.wso2.openbanking.cds.gateway.test;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestConstants {

    public static final String INVALID_EXECUTOR_CLASS =
            "com.wso2.openbanking.accelerator.gateway.executor.test.executor.InvalidClass";
    public static final String VALID_EXECUTOR_CLASS =
            "com.wso2.openbanking.cds.gateway.test.executor.MockOBExecutor";

    public static final Map<Integer, String> VALID_EXECUTOR_MAP = Stream.of(
            new AbstractMap.SimpleImmutableEntry<>(1, VALID_EXECUTOR_CLASS))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public static final Map<Integer, String> INVALID_EXECUTOR_MAP = Stream.of(
            new AbstractMap.SimpleImmutableEntry<>(1, INVALID_EXECUTOR_CLASS))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    public static final String CUSTOM_PAYLOAD = "{\"custom\":\"payload\"}";
    public static final Map<String, Map<Integer, String>> FULL_VALIDATOR_MAP = Stream.of(
            new AbstractMap.SimpleImmutableEntry<>("Default", VALID_EXECUTOR_MAP),
            new AbstractMap.SimpleImmutableEntry<>("DCR", VALID_EXECUTOR_MAP),
            new AbstractMap.SimpleImmutableEntry<>("CDS", VALID_EXECUTOR_MAP))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
}
