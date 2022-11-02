#Using Sample Resources

Following configs can be used in test-config.xml to use sample SSA and keystores for DCR tests.

    <DCR>
        <SSAPath>Path.To.Directory/ssa.txt</SSAPath>
        <!-- SSA Redirect Uri -->
        <RedirectUri>https://www.google.com/redirects/redirect1</RedirectUri>
         <!-- SSA Alternate Redirect Uri (Use the available uri if not present)-->
         <AlternateRedirectUri>https://www.google.com/redirects/redirect2</AlternateRedirectUri>
    </DCR>

Use signing.jks in 'signing-keystore' directory as the Application Keystore, and transport.jks in 'transport-keystore'
directory as the Transport Keystore.

Sample Keystore information:
- Signing key alias = tpp6-signing
- Signing keystore password = wso2carbon
- Transport key alias = tpp6-transport
- Transport keystore password = wso2carbon
