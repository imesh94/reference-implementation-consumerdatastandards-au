#Using Sample Resources

Following configs can be used in TestConfiguration.xml to use sample SSA and keystores for DCR tests.

    <DCR>
        <SSAPath>Path.To.Directory/ssa.txt</SSAPath>
        <!-- SSA SoftwareId -->
        <SoftwareId>oQ4KoaavpOuoE7rvQsZEOV</SoftwareId>
        <!-- SSA Redirect Uri -->
        <RedirectUri>https://www.google.com/redirects/redirect1</RedirectUri>
    </DCR>
Use signing.jks in 'signing-keystore' directory as the Application Keystore.

Sample Keystore information:
Signing key alias = tpp6-signing
Signing keystore password = wso2carbon
Signing Kid = w7NFeMODzCMOZen_WECAlG9N8gg
Transport keystore password = wso2carbon
Transport keystore alias = tpp6-transport