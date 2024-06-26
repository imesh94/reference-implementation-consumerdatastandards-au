/*
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 *
 */

package com.wso2.openbanking.cds.metrics.data;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Mock implementation of MetricsDataProvider interface to provide data for unit tests.
 */
public class MockMetricsDataProvider implements MetricsDataProvider {

    @Override
    public JSONObject getAvailabilityMetricsData() throws OpenBankingException {

        String availabilityMetricsData = "{\"records\":" +
                "[[\"2004\"," + getRandomEpochSecondWithinPast13Months() + ",\"scheduled\"," +
                getRandomEpochSecondWithinPast13Months() + "," + getRandomEpochSecondWithinPast13Months() +
                ",\"all\"]," +
                "[\"2004\"," + getRandomEpochSecondWithinPast13Months() + ",\"scheduled\"," +
                getRandomEpochSecondWithinPast13Months() + "," + getRandomEpochSecondWithinPast13Months() +
                ",\"all\"]," +
                "[\"2005\"," + getRandomEpochSecondWithinPast13Months() + ",\"incident\"," +
                getRandomEpochSecondWithinPast13Months() + "," + getRandomEpochSecondWithinPast13Months() +
                ",\"all\"]," +
                "[\"2005\"," + getRandomEpochSecondWithinPast13Months() + ",\"incident\"," +
                getRandomEpochSecondWithinPast13Months() + "," + getRandomEpochSecondWithinPast13Months() +
                ",\"all\"]," +
                "[\"2006\"," + getRandomEpochSecondWithinPast13Months() + ",\"scheduled\"," +
                getRandomEpochSecondWithinPast13Months() + "," + getRandomEpochSecondWithinPast13Months() +
                ",\"all\"]," +
                "[\"2006\"," + getRandomEpochSecondWithinPast13Months() + ",\"scheduled\"," +
                getRandomEpochSecondWithinPast13Months() + "," + getRandomEpochSecondWithinPast13Months() +
                ",\"all\"]," +
                "[\"2007\"," + getRandomEpochSecondWithinPast13Months() + ",\"incident\"," +
                getRandomEpochSecondWithinPast13Months() + "," + getRandomEpochSecondWithinPast13Months() +
                ",\"authenticated\"]," +
                "[\"2007\"," + getRandomEpochSecondWithinPast13Months() + ",\"incident\"," +
                getRandomEpochSecondWithinPast13Months() + "," + getRandomEpochSecondWithinPast13Months() +
                ",\"authenticated\"]," +
                "[\"2028\"," + getRandomEpochSecondWithinPast13Months() + ",\"scheduled\"," +
                getRandomEpochSecondWithinPast13Months() + "," + getRandomEpochSecondWithinPast13Months() +
                ",\"authenticated\"]," +
                "[\"2028\"," + getRandomEpochSecondWithinPast13Months() + ",\"scheduled\"," +
                getRandomEpochSecondWithinPast13Months() + "," + getRandomEpochSecondWithinPast13Months() +
                ",\"unauthenticated\"]," +
                "[\"2029\"," + getRandomEpochSecondWithinPast13Months() + ",\"incident\"," +
                getRandomEpochSecondWithinPast13Months() + "," + getRandomEpochSecondWithinPast13Months() +
                ",\"unauthenticated\"]," +
                "[\"2029\"," + getRandomEpochSecondWithinPast13Months() + ",\"incident\"," +
                getRandomEpochSecondWithinPast13Months() + "," + getRandomEpochSecondWithinPast13Months() +
                ",\"unauthenticated\"]]}";
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        try {
            return (JSONObject) parser.parse(availabilityMetricsData);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONObject getInvocationMetricsData() throws OpenBankingException {

        String invocationMetricsData = "{\"records\":" +
                "[[\"HighPriority\",8," + getRandomEpochMilliWithinPast7Days() + "],[\"LowPriority\",2," +
                getRandomEpochMilliWithinPast7Days() + "]," +
                "[\"Unauthenticated\",1," + getRandomEpochMilliWithinPast7Days() + "],[\"Unattended\",57," +
                getRandomEpochMilliWithinPast7Days() + "]," +
                "[\"LargePayload\",2," + getRandomEpochMilliWithinPast7Days() + "],[\"Unattended\",9," +
                getRandomEpochMilliWithinPast7Days() + "]," +
                "[\"HighPriority\",7," + getRandomEpochMilliWithinPast7Days() + "]]}";

        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(invocationMetricsData);
            JSONArray records = (JSONArray) jsonObject.get("records");

            for (Object record : records) {
                List<Object> recordList = (List<Object>) record;
                if (recordList.get(1) instanceof Long) {
                    Long longValue = (Long) recordList.get(1);
                    int intValue = longValue.intValue();
                    recordList.set(1, intValue);
                }
            }

            return jsonObject;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONObject getInvocationByAspectMetricsData() throws OpenBankingException {

        String invocationMetricsData = "{\"records\":" +
                "[[\"unauthenticated\",8," + getRandomEpochMilliWithinPast7Days() + "],[\"unauthenticated\",2," +
                getRandomEpochMilliWithinPast7Days() + "]," +
                "[\"unauthenticated\",1," + getRandomEpochMilliWithinPast7Days() + "],[\"authenticated\",57," +
                getRandomEpochMilliWithinPast7Days() + "]," +
                "[\"authenticated\",2," + getRandomEpochMilliWithinPast7Days() + "],[\"authenticated\",9," +
                getRandomEpochMilliWithinPast7Days() + "]," +
                "[\"authenticated\",7," + getRandomEpochMilliWithinPast7Days() + "]]}";

        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(invocationMetricsData);
            JSONArray records = (JSONArray) jsonObject.get("records");

            for (Object record : records) {
                List<Object> recordList = (List<Object>) record;
                if (recordList.get(1) instanceof Long) {
                    Long longValue = (Long) recordList.get(1);
                    int intValue = longValue.intValue();
                    recordList.set(1, intValue);
                }
            }

            return jsonObject;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONObject getSessionCountMetricsData() throws OpenBankingException {

        String sessionCountData = "{\"records\":[[2," + getRandomEpochMilliWithinPast7Days() + "],[2," +
                getRandomEpochMilliWithinPast7Days() + "]]}";
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(sessionCountData);
            JSONArray records = (JSONArray) jsonObject.get("records");

            for (Object record : records) {
                List<Object> recordList = (List<Object>) record;
                if (recordList.get(0) instanceof Long) {
                    Long longValue = (Long) recordList.get(0);
                    int intValue = longValue.intValue();
                    recordList.set(0, intValue);
                }
            }
            return jsonObject;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONArray getPeakTPSMetricsData() throws ParseException {
        String peakTPSData = "[{\"event\":{\"total_count\":1,\"MESSAGE_ID\":\"6e80e53c-6a01-4a12-8654-bc5c81141ce9\"," +
                "\"aspect\":\"authenticated\",\"" +
                "TIMESTAMP\":" + getRandomEpochSecondWithinPast7Days() + "}}," +
                "{\"event\":{\"total_count\":2,\"MESSAGE_ID\":\"6e80e53c-6a01-4a12-8654-bc5c81141ce9\"," +
                "\"aspect\":\"authenticated\",\"TIMESTAMP\":" + getRandomEpochSecondWithinPast7Days() + "}}," +
                "{\"event\":{\"total_count\":1,\"MESSAGE_ID\":\"6e80e53c-6a01-4a12-8654-bc5c81141ce9\"," +
                "\"aspect\":\"authenticated\",\"TIMESTAMP\":" + getRandomEpochSecondWithinPast7Days() + "}}]";

        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        try {
            return (JSONArray) parser.parse(peakTPSData);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONObject getErrorMetricsData() throws OpenBankingException {

        String errorData = "{\"records\":[[1," + getRandomEpochMilliWithinPast7Days() + "],[19," +
                getRandomEpochMilliWithinPast7Days() + "]]}";
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(errorData);
            JSONArray records = (JSONArray) jsonObject.get("records");

            for (Object record : records) {
                List<Object> recordList = (List<Object>) record;
                if (recordList.get(0) instanceof Long) {
                    Long longValue = (Long) recordList.get(0);
                    int intValue = longValue.intValue();
                    recordList.set(0, intValue);
                }
            }
            return jsonObject;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONObject getRejectionMetricsData() throws OpenBankingException {
        String rejectionData = "{\"records\":" +
                "[[1," + getRandomEpochSecondWithinPast7Days() + ",\"anonymous\"]," +
                "[1," + getRandomEpochSecondWithinPast7Days() + ",\"anonymous\"]," +
                "[1," + getRandomEpochSecondWithinPast7Days() + ",\"anonymous\"]," +
                "[1," + getRandomEpochSecondWithinPast7Days() + ",\"anonymous\"]," +
                "[1," + getRandomEpochSecondWithinPast7Days() + ",\"anonymous\"]]}";

        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        try {
            return (JSONObject) parser.parse(rejectionData);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONObject getRecipientCountMetricsData() throws OpenBankingException {

        String recipientCountData = "{\"records\":[[1]]}";
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        try {
            return (JSONObject) parser.parse(recipientCountData);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONObject getCustomerCountMetricsData() throws OpenBankingException {

        String customerCountData = "{\"records\":[[1]]}";
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        try {
            return (JSONObject) parser.parse(customerCountData);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONObject getTotalResponseTimeMetricsData() throws OpenBankingException {

        String totalResponseTimeData = "{\"records\":[[\"HighPriority\",1.644," +
                getRandomEpochMilliWithinPast7Days() + "]," +
                "[\"LowPriority\",1.485," + getRandomEpochMilliWithinPast7Days() + "]," +
                "[\"Unauthenticated\",0.0," + getRandomEpochMilliWithinPast7Days() + "]," +
                "[\"Unattended\",267.618," + getRandomEpochMilliWithinPast7Days() + "]," +
                "[\"LargePayload\",0.648," + getRandomEpochMilliWithinPast7Days() + "]," +
                "[\"Unattended\",8.172," + getRandomEpochMilliWithinPast7Days() + "]," +
                "[\"HighPriority\",4.364," + getRandomEpochMilliWithinPast7Days() + "]]}";

        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        try {
            return (JSONObject) parser.parse(totalResponseTimeData);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONObject getSuccessfulInvocationMetricsData() throws OpenBankingException {

        String successfulInvocationsData = "{\"records\":[[68," + getRandomEpochMilliWithinPast7Days() + "],[68,"
                + getRandomEpochMilliWithinPast7Days() + "]]}";
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(successfulInvocationsData);
            JSONArray records = (JSONArray) jsonObject.get("records");

            for (Object record : records) {
                List<Object> recordList = (List<Object>) record;
                if (recordList.get(0) instanceof Long) {
                    Long longValue = (Long) recordList.get(0);
                    int intValue = longValue.intValue();
                    recordList.set(0, intValue);
                }
            }
            return jsonObject;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static long getRandomEpochSecondWithinPast7Days() {

        long now = Instant.now().getEpochSecond();
        long sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS).getEpochSecond();
        return ThreadLocalRandom.current().nextLong(sevenDaysAgo, now);
    }

    private static long getRandomEpochMilliWithinPast7Days() {

        long now = Instant.now().toEpochMilli();
        long sevenDaysAgo = Instant.now().minusSeconds(7 * 24 * 60 * 60).toEpochMilli();
        return ThreadLocalRandom.current().nextLong(sevenDaysAgo, now);
    }

    private static long getRandomEpochSecondWithinPast13Months() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirteenMonthsAgo = now.minus(13, ChronoUnit.MONTHS);
        Instant thirteenMonthsAgoInstant = thirteenMonthsAgo.atZone(ZoneId.of("GMT")).toInstant();
        return ThreadLocalRandom.current().nextLong(thirteenMonthsAgoInstant.getEpochSecond(),
                Instant.now().getEpochSecond());
    }
}
