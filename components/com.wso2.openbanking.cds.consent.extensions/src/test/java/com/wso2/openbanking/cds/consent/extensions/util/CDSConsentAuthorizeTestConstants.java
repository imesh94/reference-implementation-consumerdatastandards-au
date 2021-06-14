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
package com.wso2.openbanking.cds.consent.extensions.util;

import com.wso2.openbanking.accelerator.consent.mgt.dao.models.AuthorizationResource;
import com.wso2.openbanking.accelerator.consent.mgt.dao.models.DetailedConsentResource;

import java.util.ArrayList;

/**
 * Constant class for cds consent authorize tests
 */
public class CDSConsentAuthorizeTestConstants {

    public static final String PERMISSION_SCOPES = "openid bank:accounts.basic:read bank:accounts.detail:read " +
            "bank:transactions:read bank:payees:read bank:regular_payments:read common:customer.basic:read " +
            "common:customer.detail:read cdr:registration";

    public static final String PAYLOAD = "{\n" +
            "  \"accountIds\": [\"1234\", \"2345\"],\n" +
            "  \"metadata\": {\n" +
            "          \"commonAuthId\": \"DummyCommonAuthId\",\n" +
            "        }\n" +
            "}";

    public static final String PAYLOAD_WITHOUT_ACCOUNT_DATA = "{\n" +
            "  \"metadata\": {\n" +
            "          \"commonAuthId\": \"DummyCommonAuthId\",\n" +
            "        }\n" +
            "}";

    public static final String PAYLOAD_NON_STRING_ACCOUNT_DATA = "{\n" +
            "  \"accountIds\": [1234, 2345],\n" +
            "  \"metadata\": {\n" +
            "          \"commonAuthId\": \"DummyCommonAuthId\",\n" +
            "        }\n" +
            "}";

    public static final String VALID_REQUEST_OBJECT = "eyJraWQiOiJEd01LZFdNbWo3UFdpbnZvcWZReVhWenlaNlEiLCJ0eXAiOiJKV" +
            "1QiLCJhbGciOiJQUzI1NiJ9.eyJhdWQiOiJodHRwczovL2xvY2FsaG9zdDo5NDQ2L29hdXRoMi90b2tlbiIsIm1heF9hZ2UiOjg2NDA" +
            "wLCJzY29wZSI6ImNvbW1vbjpjdXN0b21lci5iYXNpYzpyZWFkIGNvbW1vbjpjdXN0b21lci5kZXRhaWw6cmVhZCBvcGVuaWQgZ2d3cC" +
            "Bwcm9maWxlIiwiZXhwIjoxOTU0NzA4NzEwLCJjbGFpbXMiOnsic2hhcmluZ19kdXJhdGlvbiI6Nzc3NjAwMCwiY2RyX2FycmFuZ2VtZ" +
            "W50X2lkIjoiMDJlN2M5ZDktY2ZlNy00YzNlLThmNjQtZTkxMTczYzg0ZWNiIiwiaWRfdG9rZW4iOnsiYWNyIjp7InZhbHVlcyI6WyJ1" +
            "cm46Y2RzLmF1OmNkcjoyIl0sImVzc2VudGlhbCI6dHJ1ZX19LCJ1c2VyaW5mbyI6eyJnaXZlbl9uYW1lIjpudWxsLCJmYW1pbHlfbmF" +
            "tZSI6bnVsbH19LCJpc3MiOiJ5R3NmQ1dUTlFoaFdpXzlsVnpVZlJBcFJIYmdhIiwicmVzcG9uc2VfdHlwZSI6ImNvZGUgaWRfdG9rZW" +
            "4iLCJyZWRpcmVjdF91cmkiOiJodHRwczovL3d3dy5nb29nbGUuY29tL3JlZGlyZWN0cy9yZWRpcmVjdDEiLCJzdGF0ZSI6IjBwTjBOQ" +
            "lRIY3ZnZyIsIm5vbmNlIjoibi1qQlhoT21PS0NCZ2ciLCJjbGllbnRfaWQiOiJ5R3NmQ1dUTlFoaFdpXzlsVnpVZlJBcFJIYmdhIn0." +
            "ByhbiWtsgNV5eb4vUcOvYBjy4j_XwILiJoBFSgOa6IlMrBcYpa_9SYbq7v5xACgPZd0nKjpBAs5aEnoRDDWR7PayvAq-aygbrJWOR5Y" +
            "tgQRPPCSq2IcCP5I0EC2fuRFim_PCNqQOuIze4898Gk04c9nCC9zKDzdCp8s67UkbszXmUtisBQb4UOz8HaMF4JPaf6uclUwS8wwbPq" +
            "zZuzPWJQt_-CvGTl3gpjqiYiAF71DNyQwZeYd06Z547l2MMh4NwoGdB9RN9fkgMAo2b-FEufET9mb0uijKVD4doytAEEGGu-YEdZAET" +
            "N9bY3tFBOvdmIy0maYbgYXMXpUMfVoepA";

    public static final String VALID_REQUEST_OBJECT_DIFF = "eyJraWQiOiJEd01LZFdNbWo3UFdpbnZvcWZReVhWenlaNlEiLCJ0eXA" +
            "iOiJKV1QiLCJhbGciOiJQUzI1NiJ9.eyJhdWQiOiJodHRwczovL2xvY2FsaG9zdDo5NDQ2L29hdXRoMi90b2tlbiIsIm1heF9hZ2UiO" +
            "jg2NDAwLCJzY29wZSI6ImNvbW1vbjpjdXN0b21lci5iYXNpYzpyZWFkIGNvbW1vbjpjdXN0b21lci5kZXRhaWw6cmVhZCBvcGVuaWQg" +
            "Z2d3cCBwcm9maWxlIiwiZXhwIjoxOTU0NzA4NzEwLCJjbGFpbXMiOnsic2hhcmluZ19kdXJhdGlvbiI6MzE1MzYyMDAsImNkcl9hcnJ" +
            "hbmdlbWVudF9pZCI6IjAyZTdjOWQ5LWNmZTctNGMzZS04ZjY0LWU5MTE3M2M4NGVjYiIsImlkX3Rva2VuIjp7ImFjciI6eyJ2YWx1ZX" +
            "MiOlsidXJuOmNkcy5hdTpjZHI6MiJdLCJlc3NlbnRpYWwiOnRydWV9fSwidXNlcmluZm8iOnsiZ2l2ZW5fbmFtZSI6bnVsbCwiZmFta" +
            "Wx5X25hbWUiOm51bGx9fSwiaXNzIjoieUdzZkNXVE5RaGhXaV85bFZ6VWZSQXBSSGJnYSIsInJlc3BvbnNlX3R5cGUiOiJjb2RlIGlk" +
            "X3Rva2VuIiwicmVkaXJlY3RfdXJpIjoiaHR0cHM6Ly93d3cuZ29vZ2xlLmNvbS9yZWRpcmVjdHMvcmVkaXJlY3QxIiwic3RhdGUiOiI" +
            "wcE4wTkJUSGN2Z2ciLCJub25jZSI6Im4takJYaE9tT0tDQmdnIiwiY2xpZW50X2lkIjoieUdzZkNXVE5RaGhXaV85bFZ6VWZSQXBSSG" +
            "JnYSJ9.rRexL4Z4z6EnoetnpC7eEXVK_nMOWim7PNA3FyuimlUO3-qDroFlHXoQA16EF6MvKljQv0e5jfHaRrqKc4aoI95vWXFOii8" +
            "ZNLUQKn-RLjpdHEOVXZ4vace9FUE7_4QHsqo_dEFcgGyiBOKSNHzx8hNP5drM41G3eJQJZNUyJHGu8xcE9iho8YTxIz75bqWbGkCvKv" +
            "Wa-fF8BJ-zIF-9hWi_5gkkcw7WD0O_eAStuc9QXFgVFtG3sMMDr3geasXF04E3UZtE1ey6umW61Rawx76SLysmGgw4Q6W7EbJypwUF6" +
            "Szke89kxMa2PRX8otxCltF8spRlGGOrFiiiuu_zwQ";

    public static final String REQUEST_OBJECT_WITHOUT_SHARING_VAL = "eyJraWQiOiJEd01LZFdNbWo3UFdpbnZvcWZReVhWenlaNl" +
            "EiLCJ0eXAiOiJKV1QiLCJhbGciOiJQUzI1NiJ9.eyJhdWQiOiJodHRwczovL2xvY2FsaG9zdDo5NDQ2L29hdXRoMi90b2tlbiIsIm1" +
            "heF9hZ2UiOjg2NDAwLCJzY29wZSI6ImNvbW1vbjpjdXN0b21lci5iYXNpYzpyZWFkIGNvbW1vbjpjdXN0b21lci5kZXRhaWw6cmVhZC" +
            "BvcGVuaWQgZ2d3cCBwcm9maWxlIiwiZXhwIjoxOTU0NzA4NzEwLCJjbGFpbXMiOnsiY2RyX2FycmFuZ2VtZW50X2lkIjoiMDJlN2M5Z" +
            "DktY2ZlNy00YzNlLThmNjQtZTkxMTczYzg0ZWNiIiwiaWRfdG9rZW4iOnsiYWNyIjp7InZhbHVlcyI6WyJ1cm46Y2RzLmF1OmNkcjoy" +
            "Il0sImVzc2VudGlhbCI6dHJ1ZX19LCJ1c2VyaW5mbyI6eyJnaXZlbl9uYW1lIjpudWxsLCJmYW1pbHlfbmFtZSI6bnVsbH19LCJpc3" +
            "MiOiJ5R3NmQ1dUTlFoaFdpXzlsVnpVZlJBcFJIYmdhIiwicmVzcG9uc2VfdHlwZSI6ImNvZGUgaWRfdG9rZW4iLCJyZWRpcmVjdF91" +
            "cmkiOiJodHRwczovL3d3dy5nb29nbGUuY29tL3JlZGlyZWN0cy9yZWRpcmVjdDEiLCJzdGF0ZSI6IjBwTjBOQlRIY3ZnZyIsIm5vbm" +
            "NlIjoibi1qQlhoT21PS0NCZ2ciLCJjbGllbnRfaWQiOiJ5R3NmQ1dUTlFoaFdpXzlsVnpVZlJBcFJIYmdhIn0.EvWowStWnGqJCLak" +
            "JcYG91U7J2ltLvg0t41es1hpwTgs-rrCYeXMx881fzR8NtP3ZtD3aTp_G-8Zm7UpygfEIqYvHO-nwUyrsfaQPQ2dp7sEgiuqPeAaBYz" +
            "KZdbPzs9tfcBdfeP_ISq9ZR0-s7Zep2-pRyLrs1MG3S-MwHOJdQl9tu14jv5ihMIDrhPIl3roNh-qNgdUG7IoRpKA-cb7-INHfMwYlF" +
            "zu5sQTRR3GakfVMzajhSsMmhZ67sgyMv0ZdgVktiP3qtymEUPKN_IKVdy2Ece1dElb_AGX2tRWzr-YIFuvdaNh-0Dy_duPSdlv8WwGr" +
            "ldUMBPzmUX2hPkwUA";

    public static DetailedConsentResource getDetailedConsentResource() {
        DetailedConsentResource detailedConsentResource = new DetailedConsentResource();
        detailedConsentResource.setConsentID("1234");
        detailedConsentResource.setCurrentStatus("awaitingAuthorization");
        detailedConsentResource.setCreatedTime(System.currentTimeMillis() / 1000);
        detailedConsentResource.setUpdatedTime(System.currentTimeMillis() / 1000);
        ArrayList<AuthorizationResource> authorizationResources = new ArrayList<>();
        authorizationResources.add(CDSConsentAuthorizeTestConstants.getAuthorizationResource());
        detailedConsentResource.setAuthorizationResources(authorizationResources);

        return detailedConsentResource;
    }

    public static AuthorizationResource getAuthorizationResource() {
        AuthorizationResource authorizationResource = new AuthorizationResource();
        authorizationResource.setAuthorizationID("DummyAuthId");
        authorizationResource.setAuthorizationStatus("created");
        authorizationResource.setUpdatedTime((long) 163979797);
        return authorizationResource;
    }
}
