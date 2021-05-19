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
package com.wso2.openbanking.cds.common;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingRuntimeException;
import com.wso2.openbanking.accelerator.common.util.CarbonUtils;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Test class for Config Parser functionality.
 */
public class OpenBankingCDSConfigParserTest {

    String absolutePathForTestResources;

    @BeforeClass
    public void beforeClass() throws ReflectiveOperationException {

        //to execute util class initialization
        new CarbonUtils();
        System.setProperty("some.property", "property.value");
        System.setProperty("carbon.home", ".");
        injectEnvironmentVariable("CARBON_HOME", ".");
        String path = "src/test/resources";
        File file = new File(path);
        absolutePathForTestResources = file.getAbsolutePath();
    }

    //Runtime exception is thrown here because carbon home is not defined properly for an actual carbon product
    @Test(expectedExceptions = OpenBankingRuntimeException.class, priority = 1)
    public void testConfigParserInitiationWithoutPath() {

        OpenBankingCDSConfigParser openBankingCDSConfigParser = OpenBankingCDSConfigParser.getInstance();
    }

    @Test(expectedExceptions = OpenBankingRuntimeException.class, priority = 2)
    public void testRuntimeExceptionInvalidConfigFile() {

        String path = absolutePathForTestResources + "/open-banking-cds-empty.xml";
        OpenBankingCDSConfigParser openBankingCDSConfigParser = OpenBankingCDSConfigParser.getInstance(path);
    }

    @Test(expectedExceptions = OpenBankingRuntimeException.class, priority = 3)
    public void testRuntimeExceptionNonExistentFile() {

        String path = absolutePathForTestResources + "/open-banking.xml" + "/value";
        OpenBankingCDSConfigParser openBankingCDSConfigParser = OpenBankingCDSConfigParser.getInstance(path);

    }

    @Test(priority = 4)
    public void testConfigParserInit() {

        String dummyConfigFile = absolutePathForTestResources + "/open-banking-cds.xml";

        OpenBankingCDSConfigParser openBankingCDSConfigParser = OpenBankingCDSConfigParser.getInstance(dummyConfigFile);
        Map<String, Object> dcrConfigs = openBankingCDSConfigParser.getConfiguration();

        dcrConfigs.get("DCR.EnableURIValidation").equals("false");
    }

    @Test(priority = 5)
    public void testSingleton() {

        String dummyConfigFile = absolutePathForTestResources + "/open-banking-cds.xml";

        OpenBankingCDSConfigParser instance1 = OpenBankingCDSConfigParser.getInstance(dummyConfigFile);
        OpenBankingCDSConfigParser instance2 = OpenBankingCDSConfigParser.getInstance(dummyConfigFile);
        Assert.assertEquals(instance2, instance1);
    }

    @Test(priority = 6)
    public void testCarbonPath() {

        String carbonConfigDirPath = CarbonUtils.getCarbonConfigDirPath();
        System.setProperty("carbon.config.dir.path", carbonConfigDirPath);
        Assert.assertEquals(CarbonUtils.getCarbonConfigDirPath(), carbonConfigDirPath);
    }

    private void injectEnvironmentVariable(String key, String value)
            throws ReflectiveOperationException {

        Class<?> processEnvironment = Class.forName("java.lang.ProcessEnvironment");

        Field unmodifiableMapField = getAccessibleField(processEnvironment, "theUnmodifiableEnvironment");
        Object unmodifiableMap = unmodifiableMapField.get(null);
        injectIntoUnmodifiableMap(key, value, unmodifiableMap);

        Field mapField = getAccessibleField(processEnvironment, "theEnvironment");
        Map<String, String> map = (Map<String, String>) mapField.get(null);
        map.put(key, value);
    }

    private Field getAccessibleField(Class<?> clazz, String fieldName)
            throws NoSuchFieldException {

        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    private void injectIntoUnmodifiableMap(String key, String value, Object map)
            throws ReflectiveOperationException {

        Class unmodifiableMap = Class.forName("java.util.Collections$UnmodifiableMap");
        Field field = getAccessibleField(unmodifiableMap, "m");
        Object obj = field.get(map);
        ((Map<String, String>) obj).put(key, value);
    }
}
