/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.consent.extensions.util;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.accelerator.consent.extensions.common.ConsentException;
import com.wso2.openbanking.cds.common.utils.CDSStreamProcessorUtils;
import com.wso2.openbanking.cds.consent.extensions.model.DataClusterSharingDateModel;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for data cluster sharing date related operations.
 */
public class DataClusterSharingDateUtil {

    /**
     * Get sharing date map for a given consent ID.
     *
     * @param consentId
     * @throws OpenBankingException
     */
    public static Map<String, DataClusterSharingDateModel> getSharingDateMap(String consentId)
            throws OpenBankingException {

        String spQuery;
        JSONObject sharingDateJsonObject;
        Map<String, DataClusterSharingDateModel> sharingDateDataMap;

        try {

            String appName = "CDSSharingDateSummarizationApp";

            spQuery = "from CDS_SHARING_START_END_DATE select CONSENT_ID, DATA_CLUSTER, " +
                    "SHARING_START_DATE, SHARED_LAST_DATE having CONSENT_ID == '" + consentId + "';";

            sharingDateJsonObject = CDSStreamProcessorUtils.executeQueryOnStreamProcessor(appName, spQuery);

            sharingDateDataMap = getListFromSharingDateData(sharingDateJsonObject);
            return sharingDateDataMap;

        } catch (IOException e) {
            throw new OpenBankingException("Error occurred while retrieving sharing date", e);
        } catch (OpenBankingException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert JSON object returned from sharing date table to a map.
     *
     * @param sharingDateJsonObject
     */
    static Map<String, DataClusterSharingDateModel> getListFromSharingDateData(JSONObject sharingDateJsonObject) {

        JSONArray recordsArray = (JSONArray) sharingDateJsonObject.get("records");
        Map<String, DataClusterSharingDateModel> sharingDateMap = new HashMap<>();

        JSONArray countArray;
        String dataCluster;
        Timestamp sharingStartDate;
        Timestamp sharedLastDate;

        for (Object object : recordsArray) {
            countArray = (JSONArray) object;
            dataCluster = (String) (countArray.get(1));
            sharingStartDate = new Timestamp(((Integer) countArray.get(2)).longValue() * 1000L);;
            sharedLastDate = new Timestamp(((Integer) countArray.get(3)).longValue() * 1000L);;

            DataClusterSharingDateModel sharingDates = new DataClusterSharingDateModel();
            sharingDates.setDataCluster(dataCluster);
            sharingDates.setSharingStartDate(sharingStartDate);
            sharingDates.setSharedLastDate(sharedLastDate);

            sharingDateMap.put(dataCluster, sharingDates);
        }
        return sharingDateMap;
    }
}
