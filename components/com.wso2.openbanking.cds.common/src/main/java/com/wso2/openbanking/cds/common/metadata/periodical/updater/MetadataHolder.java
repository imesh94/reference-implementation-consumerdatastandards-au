/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.common.metadata.periodical.updater;

import java.util.Map;

/**
 * MetadataHolder
 * <p>
 * Holds the JSON data of software product and data recipients.
 * Will be used by status validator to get the data.
 * Can be considered as primary cache that gets overridden every n minutes.
 */
public class MetadataHolder {

    private static volatile MetadataHolder instance;
    private Map<String, String> softwareProduct;
    private Map<String, String> dataRecipient;

    private MetadataHolder() {
    }

    public static MetadataHolder getInstance() {

        if (instance == null) {
            synchronized (MetadataHolder.class) {
                if (instance == null) {
                    instance = new MetadataHolder();
                }
            }
        }
        return instance;
    }


    public Map<String, String> getSoftwareProduct() {
        return this.softwareProduct;
    }

    public void setSoftwareProduct(Map<String, String> softwareProduct) {
        this.softwareProduct = softwareProduct;
    }

    public Map<String, String> getDataRecipient() {
        return this.dataRecipient;
    }

    public void setDataRecipient(Map<String, String> dataRecipient) {
        this.dataRecipient = dataRecipient;
    }
}
