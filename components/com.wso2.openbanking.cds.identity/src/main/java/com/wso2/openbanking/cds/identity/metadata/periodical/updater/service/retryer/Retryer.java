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

package com.wso2.openbanking.cds.identity.metadata.periodical.updater.service.retryer;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.Callable;

/**
 * Retryer
 * <p>
 * Class uses to re-execute a specific task. It waits until waitTime expires and repeatedly attempts the task up to
 * maxRetryCount times. waitTime multiplies by double for every re-execution.
 *
 * @param <T> return type of the execute method
 */
public class Retryer<T> {

    private static final Log LOG = LogFactory.getLog(Retryer.class);

    private long waitTime;
    private final int maxRetryCount;
    private int attempt;

    public Retryer(long waitTime, int maxRetryCount) {
        this.waitTime = waitTime;
        this.maxRetryCount = maxRetryCount;
        this.attempt = 0;
    }

    public T execute(Callable<T> callable) throws OpenBankingException {

        while (this.maxRetryCount > this.attempt) {
            try {
                return callable.call();
            } catch (Exception e) {
                LOG.error("Error occurred while executing Retryer command on attempt " + (this.attempt + 1) + " of "
                        + this.maxRetryCount + ". Caused by, ", e);
                this.attempt++;
            }
            blockThread(this.waitTime);
            this.waitTime = this.waitTime * 2;
        }
        throw new OpenBankingException("Retryer command has failed on all retries");
    }

    private void blockThread(long waitTime) {

        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            LOG.warn("Exception occurred while blocking Retryer thread. Caused by, ", e);
            Thread.currentThread().interrupt();
        }
    }

}
