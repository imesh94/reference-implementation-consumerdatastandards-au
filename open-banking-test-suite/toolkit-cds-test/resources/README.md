#Configure test-config.xml

The test-config.xml file contains input data which need to execute the test suite. This file need to be configured as below,
before start the test suite execution.

1. Get a copy of the test-config-example.xml file to the ob-compliance-toolkit-cds/open-banking-test-suite/toolkit-cds-test/resources.
2. Rename xml file as "test-config.xml". (Do not commit this file to the repository)
3. Configure created test-config.xml file as below.
    - `<SolutionVersion>` = The version of the Open Banking Solution. (Eg: For OB-300 the SolutionVersion value should
      be 3.0.0)
    - `<OBSpec>` = The specific OB Specification (Eg: OBSpec accepts values AU)
    - `<ApiVersion>` = Version of the API

    - `<Server>`
        - `<BaseURL>` = https://<<am_host>>:8243 [AU with Micro Gateway: the port should be the 9095]
        - `<GatewayURL>` = https://<<am_host>>:9443
        - `<AuthorisationServerURL>` = https://<<am_host>>:9446

        - `<MicroGateway>` (AU Micro Gateway Specific Configurations. These configs should be configured only in Micro
          Gateway testing)
            - `<MicroGatewayEnabled>` = true (This need to be configured as true to run the Micro-gateway test
              scenarios)
            - `<AU><DcrURL>` = https://<<am_host>>:<<port>> [Port of the docker container]
            - `<AU><CdsAuAccountsURL>` = https://<<am_host>>:<<port>> [Port of the docker container]
            - `<AU><CdsAuBalancesURL>` = https://<<am_host>>:<<port>> [Port of the docker container]
            - `<AU><CdsAuTransactionURL>` = https://<<am_host>>:<<port>> [Port of the docker container]
            - `<AU><CdsAuDirectDebitURL>` = https://<<am_host>>:<<port>> [Port of the docker container]
            - `<AU><CdsAuSchedulePaymentURL>` = https://<<am_host>>:<<port>> [Port of the docker container]
            - `<AU><CdsAuPayeeURL>` = https://<<am_host>>:<<port>> [Port of the docker container]
            - `<AU><CdsAuProductURL>` = https://<<am_host>>:<<port>> [Port of the docker container]
            - `<AU><CdsCustomerURL>` = https://<<am_host>>:<<port>> [Port of the docker container]
            - `<AU><CdsDiscoveryURL>` = https://<<am_host>>:<<port>> [Port of the docker container]
            - `<AU><CdrArrangementURL>` = https://<<am_host>>:<<port>> [Port of the docker container]
            - `<AU><CdsAdminURL>` = https://<<am_host>>:<<port>> [Port of the docker container]

    - `<Application.KeyStore>` (Configure according to the steps provided in the README.md file in the resource
      directory)
        - `<Location>` = Path to the signing.jks
        - `<Alias>` = Alias of the application keystore
        - `<Password>` = Password of the application keystore (Eg: wso2carbon)

    - `<Application.Certificate><KID>` = KeyId of the certificate.

    - `<Transport>` (Configure according to the steps provided in the README.md file in resource directory)
        - `<MTLSEnabled>` = true (Set to true when the setup is configured in MTLS)

        - `<KeyStore>`
            - `<Location>` = Path to the transport.jks
            - `<Password>` = Password of the transport keystore (Eg: wso2carbon)
        - `<Truststore>`
            - `<Location>` = Path to the client-truststore.jks of wso2-obam.
            - `<Password>` = Password of the client-truststore.jks keystore (Eg: wso2carbon)

    - `<Application>`
        - `<ClientID>` = Application ClientId
        - `<ClientSecret>` = Application Client Secret
        - `<RedirectURL>` = Application Redirect URL

    - `<NonRegulatoryApplication>`(This need to be configured only for the Non-Regulatory Application related tests)
        - `<ClientID>` = ClientId of the Non-Regulatory Application
        - `<ClientSecret>` = Client Secret of the Non-Regulatory Application
        - `<RedirectURL>` = Redirect URL of the Non-Regulatory Application

    - `<PSUInfo>` = PSU Credentials
    - `<PublisherInfo>` = Publisher Credentials
    - `<TPPInfo>` = TPP Credentials

    - `<BrowserAutomation>`
        - `<HeadlessEnabled>` = true (Execute UI automated tests in Headless mode)
        - `<FirefoxDriverLocation>` = Path to the geckodriver - geckodriver version should be compatible with the
          version of the firefox driver installed in your computer.
          (Eg: financial-open-banking/product-scenarios/test-artifacts/selenium-libs/geckodriver)

    - `<Common>`
        - `<SigningAlgorithm>` = Signing Algorithm (Eg: PS256)

    - `<DCR>` (Configure according to the steps provided in the README.md file in resource directory)
        - `<SSAPath>` = Path to corresponding ssa.txt file
        - `<RedirectUri>` = SSA Redirect Uri
        - `<AlternateRedirectUri>` SSA Alternate Redirect Uri

    - `<ConsentApi>`
        - `<AudienceValue>` = Audience value (Eg: https://<<host>>:8243/token)

    - `<IdPermanence>` (This need to be configured only for AU spec)
      `<SecretKey>` = Resource ID encryption/decryption key (Eg: abc, the default value would be "wso2")

4. Configure "open-banking-test-suite/toolkit-cds-test/resources/code_coverage/server.properties" file.

    - IAMHostName - Hostname of IAM server
    - IAMSeverIp - IP Address of IAM server [This need to be configured only if for the remote server setup]
    - IAMServerSetupPath - path to the IAM pack
    - AMHostName - Hostname of AM server
    - AMSeverIp - IP Address of AM server [This need to be configured only if for the remote server setup]
    - AMServerSetupPath - path to the AM pack
    - ServerCertPath - Path to the cloud.key which used to log in to the test instance. No need to configure this
      property for local setups. [This need to be configured only if for the remote server setup]
    - JavaHome - Path to the JAVA_HOME
    
