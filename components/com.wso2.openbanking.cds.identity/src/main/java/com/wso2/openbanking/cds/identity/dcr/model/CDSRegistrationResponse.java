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
package com.wso2.openbanking.cds.identity.dcr.model;

import com.google.gson.annotations.SerializedName;
import com.wso2.openbanking.accelerator.identity.dcr.model.RegistrationResponse;

/**
 * CDS Spec specific registration response class
 */
public class CDSRegistrationResponse extends RegistrationResponse {

    @SerializedName(value = "client_name")
    private String clientName;

    @SerializedName("client_description")
    private String clientDescription;

    @SerializedName("client_uri")
    private String clientUri;

    @SerializedName("org_id")
    private String orgId;

    @SerializedName("org_name")
    private String orgName;

    @SerializedName("logo_uri")
    private String logoUri;

    @SerializedName("tos_uri")
    private String tosUri;

    @SerializedName("policy_uri")
    private String policyUri;

    @SerializedName(value = "jwks_uri")
    private String jwksURI;

    @SerializedName("legal_entity_id")
    private String legalEntityId;

    @SerializedName("legal_entity_name")
    private String legalEntityName;

    @SerializedName("sector_identifier_uri")
    private String sectorIdentifierUri;

    @SerializedName("revocation_uri")
    private String revocationUri;

    @SerializedName("recipient_base_uri")
    private String recipientBaseUri;

    @SerializedName("token_endpoint_auth_signing_alg")
    private String tokenEndPointAuthSigningAlg;

    @SerializedName("id_token_encrypted_response_alg")
    private String idTokenEncryptionResponseAlg;

    @SerializedName("id_token_encrypted_response_enc")
    private String idTokenEncryptionResponseEnc;

    @SerializedName("software_roles")
    private String softwareRoles;

    public String getTokenEndPointAuthSigningAlg() {

        return tokenEndPointAuthSigningAlg;
    }

    public void setTokenEndPointAuthSigningAlg(String tokenEndPointAuthSigningAlg) {

        this.tokenEndPointAuthSigningAlg = tokenEndPointAuthSigningAlg;
    }

    public String getIdTokenEncryptionResponseEnc() {

        return idTokenEncryptionResponseEnc;
    }

    public void setIdTokenEncryptionResponseEnc(String idTokenEncryptionResponseEnc) {

        this.idTokenEncryptionResponseEnc = idTokenEncryptionResponseEnc;
    }

    public String getIdTokenEncryptionResponseAlg() {

        return idTokenEncryptionResponseAlg;
    }

    public void setIdTokenEncryptionResponseAlg(String idTokenEncryptionResponseAlg) {

        this.idTokenEncryptionResponseAlg = idTokenEncryptionResponseAlg;
    }

    public String getOrgId() {

        return orgId;
    }

    public void setOrgId(String orgId) {

        this.orgId = orgId;
    }

    public String getOrgName() {

        return orgName;
    }

    public void setOrgName(String orgName) {

        this.orgName = orgName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getJwksURI() {
        return jwksURI;
    }

    public void setJwksURI(String jwksURI) {
        this.jwksURI = jwksURI;
    }

    public String getLegalEntityId() {
        return legalEntityId;
    }

    public void setLegalEntityId(String legalEntityId) {
        this.legalEntityId = legalEntityId;
    }

    public String getLegalEntityName() {
        return legalEntityName;
    }

    public void setLegalEntityName(String legalEntityName) {
        this.legalEntityName = legalEntityName;
    }

    public String getClientDescription() {
        return clientDescription;
    }

    public void setClientDescription(String clientDescription) {
        this.clientDescription = clientDescription;
    }

    public String getClientUri() {
        return clientUri;
    }

    public void setClientUri(String clientUri) {
        this.clientUri = clientUri;
    }

    public String getSectorIdentifierUri() {
        return sectorIdentifierUri;
    }

    public void setSectorIdentifierUri(String sectorIdentifierUri) {
        this.sectorIdentifierUri = sectorIdentifierUri;
    }

    public String getTosUri() {
        return tosUri;
    }

    public void setTosUri(String tosUri) {
        this.tosUri = tosUri;
    }

    public String getLogoUri() {
        return logoUri;
    }

    public void setLogoUri(String logoUri) {
        this.logoUri = logoUri;
    }

    public String getPolicyUri() {
        return policyUri;
    }

    public void setPolicyUri(String policyUri) {
        this.policyUri = policyUri;
    }

    public String getRevocationUri() {
        return revocationUri;
    }

    public void setRevocationUri(String revocationUri) {
        this.revocationUri = revocationUri;
    }

    public String getRecipientBaseUri() {
        return recipientBaseUri;
    }

    public void setRecipientBaseUri(String recipientBaseUri) {
        this.recipientBaseUri = recipientBaseUri;
    }

    public String getSoftwareRoles() {
        return softwareRoles;
    }

    public void setSoftwareRoles(String softwareRoles) {
        this.softwareRoles = softwareRoles;
    }
}
