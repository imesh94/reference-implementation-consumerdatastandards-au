/*
 * Copyright (c) 2021-2022, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.gateway.mediators;

import com.wso2.openbanking.accelerator.common.config.OpenBankingConfigParser;
import com.wso2.openbanking.accelerator.data.publisher.common.util.OBDataPublisherUtil;
import com.wso2.openbanking.cds.gateway.utils.GatewayConstants;
import org.apache.axiom.om.OMElement;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.config.SynapseConfiguration;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;


import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({OpenBankingConfigParser.class, OBDataPublisherUtil.class, JsonUtil.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class GatewayErrorMediatorTest extends PowerMockTestCase {

    GatewayErrorMediator gatewayErrorMediator = new GatewayErrorMediator();

    @Test
    public void testMediatorForGeneralAuthError() throws Exception {

        MessageContext messageContext = getData();
        messageContext.setProperty(GatewayConstants.ERROR_CODE, 900900);
        Assert.assertTrue(gatewayErrorMediator.mediate(messageContext));
    }

    @Test
    public void testMediatorForForbiddenAuthError() throws Exception {

        MessageContext messageContext = getData();
        messageContext.setProperty(GatewayConstants.ERROR_CODE, 900906);
        Assert.assertTrue(gatewayErrorMediator.mediate(messageContext));
    }

    @Test
    public void testMediatorForUnauthenticatedError() throws Exception {

        MessageContext messageContext = getData();
        messageContext.setProperty(GatewayConstants.ERROR_CODE, 900902);
        Assert.assertTrue(gatewayErrorMediator.mediate(messageContext));
    }

    @Test
    public void testMediatorForOtherError() throws Exception {

        MessageContext messageContext = getData();
        messageContext.setProperty(GatewayConstants.ERROR_CODE, 900980);
        Assert.assertTrue(gatewayErrorMediator.mediate(messageContext));
    }

    @Test
    public void testMediatorNotFoundResourceFailureError() throws Exception {

        MessageContext messageContext = getData();
        messageContext.setProperty(GatewayConstants.ERROR_CODE, 404);
        Assert.assertTrue(gatewayErrorMediator.mediate(messageContext));
    }

    @Test
    public void testMediatorForGeneralResourceFailureError() throws Exception {

        MessageContext messageContext = getData();
        messageContext.setProperty(GatewayConstants.ERROR_CODE, 400);
        Assert.assertTrue(gatewayErrorMediator.mediate(messageContext));
    }

    @Test
    public void testErrorCodeNullScenario() throws Exception {

        MessageContext messageContext = getData();
        Assert.assertTrue(gatewayErrorMediator.mediate(messageContext));
    }

    @Test(priority = 1)
    public void testErrorCodeNullScenarioWithExpectedErrors() throws Exception {

        MessageContext messageContext = getData();
        messageContext.setProperty(GatewayConstants.HTTP_RESPONSE_STATUS_CODE, 400);
        Assert.assertTrue(gatewayErrorMediator.mediate(messageContext));
    }

    @Test()
    public void testGeneralSchemaErrors() throws Exception {

        MessageContext messageContext = getData();
        messageContext.setProperty(GatewayConstants.ERROR_CODE, 400);
        messageContext.setProperty(GatewayConstants.ERROR_DETAIL, GatewayConstants.SCHEMA_FAIL_MSG);
        Assert.assertTrue(gatewayErrorMediator.mediate(messageContext));
    }

    @Test()
    public void testServerSchemaErrors() throws Exception {

        MessageContext messageContext = getData();
        messageContext.setProperty(GatewayConstants.ERROR_CODE, 500);
        messageContext.setProperty(GatewayConstants.ERROR_DETAIL, GatewayConstants.SCHEMA_FAIL_MSG);
        Assert.assertTrue(gatewayErrorMediator.mediate(messageContext));
    }

    private MessageContext getData() throws Exception {

        Map<String, Object> configs = new HashMap<>();
        configs.put("DataPublishing.Enabled", "true");
        configs.put(GatewayConstants.CLIENT_USER_AGENT, "dummyAgent");

        mockStatic(OpenBankingConfigParser.class);
        OpenBankingConfigParser openBankingConfigParserMock = mock(OpenBankingConfigParser.class);
        when(OpenBankingConfigParser.getInstance()).thenReturn(openBankingConfigParserMock);
        when(openBankingConfigParserMock.getConfiguration()).thenReturn(configs);

        SynapseConfiguration synapseConfigurationMock = mock(SynapseConfiguration.class);
        SynapseEnvironment synapseEnvironmentMock = mock(SynapseEnvironment.class);
        org.apache.axis2.context.MessageContext messageContextMock =
                mock(org.apache.axis2.context.MessageContext.class);
        MessageContext messageContext = new Axis2MessageContext(messageContextMock, synapseConfigurationMock,
                synapseEnvironmentMock);

        messageContext.setProperty(GatewayConstants.HTTP_RESPONSE_STATUS_CODE, 500);
        org.apache.axis2.context.MessageContext axis2MessageContext = new org.apache.axis2.context.MessageContext();
        axis2MessageContext.setProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS, configs);
        ((Axis2MessageContext) messageContext).setAxis2MessageContext(axis2MessageContext);

        mockStatic(OBDataPublisherUtil.class);
        doNothing().when(OBDataPublisherUtil.class, "publishData", Mockito.anyString(), Mockito.anyString(),
                Mockito.anyObject());

        mockStatic(JsonUtil.class);
        OMElement omElementMock = mock(OMElement.class);
        when(JsonUtil.getNewJsonPayload(Mockito.anyObject(), Mockito.anyString(), Mockito.anyBoolean(),
                Mockito.anyBoolean())).thenReturn(omElementMock);
        return messageContext;
    }
}
