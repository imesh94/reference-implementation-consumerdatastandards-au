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

package com.wso2.openbanking.cds.identity.metadata.periodical.updater.service.dataholder.responsibility;

import org.mockito.Mockito;
import org.testng.annotations.Test;

/**
 * Test class for Data Holder Responsibilities Executor.
 */
public class DataHolderResponsibilitiesExecutorTest {

    DataHolderResponsibilitiesExecutor uut;

    @Test
    public void testExecute() {
        DataHolderResponsibility responsibilityMock1 = Mockito.mock(DataHolderResponsibility.class);
        Mockito.when(responsibilityMock1.shouldPerform()).thenReturn(true);
        Mockito.when(responsibilityMock1.getResponsibilityId()).thenReturn("appName1-1-CleanupRegistration");
        DataHolderResponsibility responsibilityMock2 = Mockito.mock(DataHolderResponsibility.class);
        Mockito.when(responsibilityMock2.getResponsibilityId()).thenReturn("appName1-1-InvalidateConsents");

        uut = DataHolderResponsibilitiesExecutor.getInstance();
        uut.addResponsibility(responsibilityMock1);
        uut.addResponsibility(responsibilityMock2);

        uut.execute();

        Mockito.verify(responsibilityMock1, Mockito.times(1)).shouldPerform();
        Mockito.verify(responsibilityMock1, Mockito.times(1)).perform();
        Mockito.verify(responsibilityMock2, Mockito.times(1)).shouldPerform();
        Mockito.verify(responsibilityMock2, Mockito.times(0)).perform();
    }

}
