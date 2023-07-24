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

package com.wso2.openbanking.cds.identity.metadata.periodical.updater.service.dataholder.responsibility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * DataHolderResponsibilitiesExecutor.
 *
 * Executes CDS data holder responsibilities in DataHolderResponsibility list
 */
public class DataHolderResponsibilitiesExecutor {
    private static final Log LOG = LogFactory.getLog(DataHolderResponsibilitiesExecutor.class);

    private static volatile DataHolderResponsibilitiesExecutor instance;
    private final Map<String, DataHolderResponsibility> responsibilityMap;

    private DataHolderResponsibilitiesExecutor() {
        this.responsibilityMap = new HashMap<>();
    }

    public static DataHolderResponsibilitiesExecutor getInstance() {

        if (instance == null) {
            synchronized (DataHolderResponsibilitiesExecutor.class) {
                if (instance == null) {
                    instance = new DataHolderResponsibilitiesExecutor();
                }
            }
        }
        return instance;
    }

    public void addResponsibilities(Map<String, DataHolderResponsibility> newResponsibilityMap) {
        this.responsibilityMap.putAll(newResponsibilityMap);
    }

    public void addResponsibility(DataHolderResponsibility responsibility) {
        this.responsibilityMap.put(responsibility.getResponsibilityId(), responsibility);
    }

    public synchronized void execute() {
        LOG.debug("Executing data holder responsibilities");

        responsibilityMap.values().parallelStream()
                .filter(Objects::nonNull)
                .filter(DataHolderResponsibility::shouldPerform)
                .forEach(DataHolderResponsibility::perform);

        this.responsibilityMap.clear();
    }
}
