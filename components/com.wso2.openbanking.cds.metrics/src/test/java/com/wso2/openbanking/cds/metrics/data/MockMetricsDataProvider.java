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

import java.util.List;

/**
 * Mock implementation of MetricsDataProvider interface to provide data for unit tests.
 */
public class MockMetricsDataProvider implements MetricsDataProvider {

    @Override
    public JSONObject getAvailabilityMetricsData() throws OpenBankingException {

        String availabilityMetricsData = "{\"records\":" +
                "[[\"2004\",1714634709,\"scheduled\",1701507600,1701601200]," +
                "[\"2004\",1714635725,\"scheduled\",1701507600,1701601200]," +
                "[\"2005\",1714634709,\"incident\",1701774000,1701777600]," +
                "[\"2005\",1714635725,\"incident\",1701774000,1701777600]," +
                "[\"2006\",1714634709,\"scheduled\",1702396800,1702400400]," +
                "[\"2006\",1714635725,\"scheduled\",1702396800,1702400400]," +
                "[\"2007\",1714634709,\"incident\",1703066400,1703152800]," +
                "[\"2007\",1714635725,\"incident\",1703066400,1703152800]," +
                "[\"2028\",1714634709,\"scheduled\",1704002399,1704067199]," +
                "[\"2028\",1714635725,\"scheduled\",1704002399,1704067199]," +
                "[\"2029\",1714634709,\"incident\",1704002399,1704067199]," +
                "[\"2029\",1714635725,\"incident\",1704002399,1704067199]]}";
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
                "[[\"HighPriority\",8,1716940800000],[\"LowPriority\",2,1716940800000]," +
                "[\"Unauthenticated\",1,1716940800000],[\"Unattended\",57,1716940800000]," +
                "[\"LargePayload\",2,1716940800000],[\"Unattended\",9,1716768000000]," +
                "[\"HighPriority\",7,1716768000000]]}";
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

        String sessionCountData = "{\"records\":[[2,1716768000000],[2,1716940800000]]}";
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
                "TIMESTAMP\":1717003786}}," +
                "{\"event\":{\"total_count\":2,\"MESSAGE_ID\":\"6e80e53c-6a01-4a12-8654-bc5c81141ce9\"," +
                "\"aspect\":\"authenticated\",\"TIMESTAMP\":1716792492}}," +
                "{\"event\":{\"total_count\":1,\"MESSAGE_ID\":\"6e80e53c-6a01-4a12-8654-bc5c81141ce9\"," +
                "\"aspect\":\"authenticated\",\"TIMESTAMP\":1717006916}}]";

        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        try {
            return (JSONArray) parser.parse(peakTPSData);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONObject getErrorMetricsData() throws OpenBankingException {

        String errorData = "{\"records\":[[1,1716940800000],[19,1716768000000]]}";
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
                "[[1,1717009253,\"anonymous\"]," +
                "[1,1717010437,\"anonymous\"]," +
                "[1,1717006951,\"anonymous\"]," +
                "[1,1717009249,\"anonymous\"]," +
                "[1,1717011113,\"anonymous\"]]}";

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

        String totalResponseTimeData = "{\"records\":[[\"HighPriority\",1.644,1716940800000]," +
                "[\"LowPriority\",1.485,1716940800000]," +
                "[\"Unauthenticated\",0.0,1716940800000]," +
                "[\"Unattended\",267.618,1716940800000]," +
                "[\"LargePayload\",0.648,1716940800000]," +
                "[\"Unattended\",8.172,1716768000000]," +
                "[\"HighPriority\",4.364,1716768000000]]}";

        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        try {
            return (JSONObject) parser.parse(totalResponseTimeData);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONObject getSuccessfulInvocationMetricsData() throws OpenBankingException {

        String successfulInvocationsData = "{\"records\":[[68,1716940800000],[68,1716768000000]]}";
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
}
