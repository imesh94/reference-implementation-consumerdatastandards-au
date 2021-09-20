#Configure test-config.xml to integrate mock cdr register

ACCC has released a Consumer Data Right (CDR) Mock Register that simulate the role of CDR Register within the CDR ecosystem. 
Please refer https://github.com/ConsumerDataRight/mock-register for source code, documentation and instructions.

1. Pull the CDR Mock Register docker image by running the following command. (Please ensure that you have set up docker in your environment)
   `docker pull consumerdataright/mock-register`

2. Run the CDR Mock Register.
   `docker run -d -h mock-cdr-register -p 7000:7000 -p 7001:7001 -p 7006:7006 --name mock-cdr-register consumerdataright/mock-register`
   
3. The CDR Mock Register is designed to start with a predefined set of metadata of DHs and ADRs.
   The **MockCDRRegisterMetaData.json** at _product-scenarios/ob-test-framework/components/resources/MockCDRRegister_ is used to load a customized set of metadata before running the test classes.


The AU test-framework has the capability to run the test cases along with this Mock CDR register.
The test-config.xml needs to be updated with required configs to enable and utilize the CDR Mock register during AU test framework execution.

1. Set true the following config under `<AUMockCDRRegister>`.
   `<Enabled>true</Enabled>`
   
2. Configure the hostname of the server running the CDR mock register docker image under `<AUMockCDRRegister>.<HostName>`
   
3. Configure transport certificates to establish MTLS connections with the CDR Mock Register.

    - Mock CDR Register mandates a MTLS connection to invoke its **/token** and **/ssa** endpoints. (other endpoints not secured.)

    - Mock Register has issued a **client.pfx** file that can be used as transport certificates of a mock ADR to invoke the Mock Register.
        - This file needs to configured as the keystore for the MTLS connections established with Mock Register.
        - Configure the section under `<AUMockCDRRegister><Transport>` with the **client.pfx** already available at _product-scenarios/ob-test-framework/components/resources/MockCDRRegister_.

          `<KeyStore>
              <Location> absolute path to client.pfx file </Location>
              <Type>pkcs12</Type>
              <Password>#M0ckDataRecipient#</Password>
          </KeyStore>`
    
    - The Mock Register has issued a **ca.pfx** file with their ca certificates.
        - This file needs to configured as the truststore for the MTLS connections established with the Mock Register.
        - Configure the section under `<AUMockCDRRegister><Transport>` with the **ca.pfx** already available at _product-scenarios/ob-test-framework/components/resources/MockCDRRegister_.
          
          `<Truststore>
              <Location> absolute path to ca.pfx file </Location>
              <Type>pkcs12</Type>
              <Password>#M0ckCDRCA#</Password>
          </Truststore>`
          
4. Configure application certificates to sign the requests sent to the Open Banking solution.
   - The mock register has introduces a common mock jwks endpoint for Mock ADRs which is listed in the issued SSAs.
   - The corresponding certificates are available in the **signingkeystore.jks** already available at _product-scenarios/ob-test-framework/components/resources/MockCDRRegister_.
        
       - This file needs to be configured as the application keystore to sign AU requests under `<AUMockCDRRegister><Application>`.

        `<KeyStore>
            <Location> absolute path to 'signingkeystore.jks' file </Location>
            <Alias>adr-sig</Alias>
            <Password>wso2carbon</Password>
        </KeyStore>`

#Configure the solution to integrate AU Mock Cdr Register

The MOCK register mandates a TLS connection to invoke it's jwks endpoints.
  - **Mock Register jwks endpoint** : to validate signature of the SSA.
  - **Mock ADR jwks endpoint exposed by the Mock Register** : to validate the signature of the JWTs sent by the ADR

1. Add Mock CDR register's TLS certificate to AM and IAM truststore (This a self-signed certificate).
   
    - Refer: https://github.com/ConsumerDataRight/mock-register/blob/main/CertificateManagement/README.md.
      The public key needs to be extracted from the tls\mock-register.pfx. 
      
        `openssl pkcs12 -in mock-register.pfx -out mock-cdr-tls-cert.pem -clcerts -nokeys`
      
       This cert file (**mock-cdr-tls-cert.pem**) is already available at _product-scenarios/ob-test-framework/components/resources/MockCDRRegister_.
    
    - Import the certificate to the truststore of IAM and AM server.
       
       `keytool -import -alias mockCDRCert -file mock-cdr-tls-cert.pem -keystore client-truststore.jks -storepass wso2carbon`
    
2. Update the following config to use the mock CDR register's JWKS endpoint to validate the signature of the SSA during the DCR call.

`[open_banking.dcr]
ssa_signature_validation.enable = true
jwks_url = "https://localhost:7000/cdr-register/v1/jwks"`

3. Update the following config to update the metadata cache from the mock CDR Register.

`[open_banking.au.metadata_cache]
enable = true
data_recipient_discovery_url = "https://localhost:7000/cdr-register/v1/banking/data-recipients/status"
software_product_discovery_url = "https://localhost:7000/cdr-register/v1/banking/data-recipients/brands/software-products/status"`
