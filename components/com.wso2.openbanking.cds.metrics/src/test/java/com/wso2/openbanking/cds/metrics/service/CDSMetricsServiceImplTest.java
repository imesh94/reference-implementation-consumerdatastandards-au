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

package com.wso2.openbanking.cds.metrics.service;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.metrics.model.ResponseMetricsListModel;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CDSMetricsServiceImplTest {

    @Test
    public void testGetMetrics() throws OpenBankingException {

        CDSMetricsServiceImpl cdsMetricsServiceSpy = Mockito.spy(new CDSMetricsServiceImpl());

        ResponseMetricsListModel responseMetricsListModelMock = Mockito.mock(ResponseMetricsListModel.class);
        MetricsProcessor metricsProcessorMock = Mockito.mock(MetricsProcessorImpl.class);

        Mockito.doReturn(metricsProcessorMock).when(cdsMetricsServiceSpy).getMetricsProcessor();
        Mockito.doReturn(responseMetricsListModelMock).when(metricsProcessorMock)
                .getResponseMetricsListModel(Mockito.anyString(), Mockito.anyString());

        ResponseMetricsListModel result = cdsMetricsServiceSpy.getMetrics(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString());
        Assert.assertNotNull(result);
    }
}
