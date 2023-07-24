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
 *
 */

package com.wso2.openbanking.cds.identity.metadata.periodical.updater.service.retryer;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.Callable;

/**
 * Test class for Retryer.
 */
public class RetryerTest {

    Retryer<Integer> retryer;

    @BeforeClass
    public void init() {
        this.retryer = new Retryer<>(100, 3);
    }

    @Test
    public void testExecute() throws Exception {
        final int actual = this.retryer.execute(() -> 10);
        Assert.assertEquals(actual, 10);

        Callable<Integer> callableMock = Mockito.mock(TestCallable.class);
        Mockito.when(callableMock.call())
                .thenThrow(new NullPointerException())
                .thenReturn(10);

        this.retryer.execute(callableMock);
        Mockito.verify(callableMock, Mockito.times(2)).call();
    }

    @Test(description = "when all attempts fails, should throw OpenBankingException",
            expectedExceptions = OpenBankingException.class)
    public void testExecuteWithException() throws Exception {
        Callable<Integer> callableMock = Mockito.mock(TestCallable.class);
        Mockito.when(callableMock.call())
                .thenThrow(new NullPointerException())
                .thenThrow(new IndexOutOfBoundsException())
                .thenThrow(new ClassNotFoundException());

        this.retryer.execute(callableMock);
    }

    private static class TestCallable implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            return 10;
        }
    }
}
