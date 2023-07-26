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

package com.wso2.openbanking.cds.metrics.util;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * class comment.
 */
public class SPJsonProcessorUtilTest {

    JSONObject jsonObject = new JSONObject();

    @Test
    public void testGetSumFromJsonObject() {

        JSONArray jsonArray = new JSONArray();
        JSONArray jsonElement = new JSONArray();
        jsonElement.add(Long.parseLong("1"));
        jsonArray.add(jsonElement);

        jsonObject.put("records", jsonArray);

        Assert.assertNotNull(SPJsonProcessorUtil.getSumFromJsonObject(jsonObject));
    }

    @Test
    public void testGetSumFromJsonObjectRejection() {

        JSONArray jsonArray = new JSONArray();
        JSONArray jsonElement = new JSONArray();
        jsonElement.add(Long.parseLong("1"));
        jsonElement.add("anonymous");
        jsonArray.add(jsonElement);

        jsonObject.put("records", jsonArray);

        Assert.assertNotNull(SPJsonProcessorUtil.getSumFromJsonObjectRejection(jsonObject));
    }

    @Test
    public void testGetMaxFromJsonObject() {

        JSONArray jsonArray = new JSONArray();
        JSONArray jsonElement = new JSONArray();
        jsonElement.add("1");
        jsonArray.add(jsonElement);

        jsonObject.put("records", jsonArray);
        Assert.assertNotNull(SPJsonProcessorUtil.getMaxFromJsonObject(jsonObject));
    }

    @Test
    public void testGetListFromJsonObject() {

        JSONArray jsonArray = new JSONArray();
        JSONArray jsonElement = new JSONArray();
        jsonElement.add("1");
        jsonElement.add(Long.parseLong("1"));
        jsonArray.add(jsonElement);

        jsonObject.put("records", jsonArray);
        Assert.assertNotNull(SPJsonProcessorUtil.getListFromJsonObject(jsonObject));
    }

    @Test
    public void testGetLastElementValueFromJsonObject() {

        JSONArray jsonArray = new JSONArray();
        JSONArray jsonElement = new JSONArray();
        jsonElement.add(Long.parseLong("1"));
        jsonArray.add(jsonElement);

        jsonObject.put("records", jsonArray);
        Assert.assertNotNull(SPJsonProcessorUtil.getLastElementValueFromJsonObject(jsonObject));
    }
}
