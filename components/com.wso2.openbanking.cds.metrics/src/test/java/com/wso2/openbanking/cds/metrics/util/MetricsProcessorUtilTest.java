/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 *
 */

package com.wso2.openbanking.cds.metrics.util;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricsProcessorUtilTest {

    @Test
    public void testDivideListEqualSizeNoZeroDivisor() throws OpenBankingException {
        List<BigDecimal> list1 = Arrays.asList(BigDecimal.valueOf(10), BigDecimal.valueOf(20));
        List<BigDecimal> list2 = Arrays.asList(BigDecimal.valueOf(2), BigDecimal.valueOf(5));
        List<BigDecimal> expected = Arrays.asList(new BigDecimal("5.000"), new BigDecimal("4.000"));
        List<BigDecimal> result = MetricsProcessorUtil.divideList(list1, list2);
        Assert.assertEquals(result, expected);
    }

    @Test(expectedExceptions = OpenBankingException.class)
    public void testDivideListUnequalSizes() throws OpenBankingException {
        List<BigDecimal> list1 = Arrays.asList(BigDecimal.valueOf(10), BigDecimal.valueOf(20));
        List<BigDecimal> list2 = Arrays.asList(BigDecimal.valueOf(2));
        MetricsProcessorUtil.divideList(list1, list2);
    }

    @Test
    public void testDivideListWithZeroDivisor() throws OpenBankingException {
        List<BigDecimal> list1 = Arrays.asList(BigDecimal.valueOf(10), BigDecimal.valueOf(20));
        List<BigDecimal> list2 = Arrays.asList(BigDecimal.valueOf(2), BigDecimal.ZERO);
        List<BigDecimal> expected = Arrays.asList(new BigDecimal("5.000"), new BigDecimal("0"));
        List<BigDecimal> result = MetricsProcessorUtil.divideList(list1, list2);
        Assert.assertEquals(result, expected);
    }

    @Test
    public void testGetLastElementValueFromJsonObject() {
        JSONObject jsonObject = new JSONObject();
        JSONArray records = new JSONArray();
        JSONArray innerArray = new JSONArray();
        innerArray.add("5");
        records.add(innerArray);
        jsonObject.put("records", records);

        int result = MetricsProcessorUtil.getLastElementValueFromJsonObject(jsonObject);
        Assert.assertEquals(result, 5);
    }

    @Test
    public void testGetLastElementValueFromEmptyJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("records", new JSONArray());

        int result = MetricsProcessorUtil.getLastElementValueFromJsonObject(jsonObject);
        Assert.assertEquals(result, 0);
    }

    @Test
    public void testGetTotalInvocationsForEachDay_BasicScenario() {
        Map<PriorityEnum, List<BigDecimal>> invocationMetricsMap = new HashMap<>();
        invocationMetricsMap.put(PriorityEnum.HIGH_PRIORITY, Arrays.asList(new BigDecimal("10"), new BigDecimal("20")));
        invocationMetricsMap.put(PriorityEnum.LOW_PRIORITY, Arrays.asList(new BigDecimal("30"), new BigDecimal("40")));
        invocationMetricsMap.put(PriorityEnum.UNATTENDED, Arrays.asList(new BigDecimal("5"), new BigDecimal("10")));
        invocationMetricsMap.put(PriorityEnum.UNAUTHENTICATED, Arrays.asList(new BigDecimal("5"),
                new BigDecimal("10")));
        invocationMetricsMap.put(PriorityEnum.LARGE_PAYLOAD, Arrays.asList(new BigDecimal("5"), new BigDecimal("10")));

        List<BigDecimal> result = MetricsProcessorUtil.getTotalInvocationsForEachDay(invocationMetricsMap);
        List<BigDecimal> expected = Arrays.asList(new BigDecimal("55"), new BigDecimal("90"));

        Assert.assertEquals(result, expected);
    }

    @Test
    public void testInitializeListWithZeros() {
        int numberOfDays = 7;
        ArrayList<BigDecimal> result = MetricsProcessorUtil.initializeListWithZeros(numberOfDays);
        Assert.assertEquals(result.size(), numberOfDays, "The list size should be exactly " + numberOfDays);
        for (BigDecimal value : result) {
            Assert.assertEquals(value, BigDecimal.ZERO, "Each entry in the list should be BigDecimal.ZERO.");
        }
    }

}
