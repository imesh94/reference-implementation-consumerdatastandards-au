# Integration Test Suite (integration-test-suite)

This test suite covers all the functional scenarios and use cases of CDS Toolkit.

### How to run Integration Test Suite (integration-test-suite)

1. Clone the master branch of the [financial-open-banking](https://github.com/wso2-enterprise/financial-open-banking/tree/master) repository.
2. Goto "integration-test-suite" and build the following modules (These are the base modules of open banking test framework);
   1. [bfsi-test-framework](https://github.com/wso2-enterprise/financial-open-banking/tree/master/integration-test-suite/bfsi-test-framework)
   2. [open-banking-test-framework](https://github.com/wso2-enterprise/financial-open-banking/tree/master/integration-test-suite/open-banking-test-framework)

    Command : `mvn clean install`

3. Then goto the "[integration-test-suite](https://github.com/wso2-enterprise/ob-compliance-toolkit-cds/tree/main/integration-test-suite)" 
4. module in main branch of "ob-compliance-toolkit-cds" repository.
5. Goto the [resources]https://github.com/wso2-enterprise/ob-compliance-toolkit-cds/tree/main/integration-test-suite/cds-toolkit-test-framework/src/main/resources) 
6. folder in cds-toolkit-test-framework.
7. Take a copy TestConfigurationExample.xml to the same location and rename it as **TestConfiguration.xml**.
8. Configure the TestConfiguration.xml file according to the example given in the file it-self.
9. Then build the "[cds-toolkit-test-framework](cds-toolkit-test-framework)" 

   Command : `mvn clean install`

10. Then goto the "[cds-toolkit-integration-test](cds-toolkit-integration-test)" and build the module by skipping the tests.

   Command : `mvn clean install -Dmaven.test.skip=true`

11. Then you can run the test cases via;
    1. Maven Command: `mvn clean install`
    2. Test Class by Class: Click on run button in front of the class
    3. Run testng.xml: Goto [testng.xml](integration-test-suite/cds-toolkit-integration-test/src/test/resources/testng.xml) 
    in resources folder and right click can run the test suite.
12. Reports will be generated cds-toolkit-integration-test/target/surefire-reports folder.