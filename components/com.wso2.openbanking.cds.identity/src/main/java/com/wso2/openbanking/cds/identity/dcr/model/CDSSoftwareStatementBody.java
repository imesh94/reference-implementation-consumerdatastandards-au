/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */
package com.wso2.openbanking.cds.identity.dcr.model;

import com.google.gson.annotations.SerializedName;
import com.wso2.openbanking.accelerator.identity.dcr.model.SoftwareStatementBody;
import com.wso2.openbanking.accelerator.identity.dcr.validation.DCRCommonConstants;
import com.wso2.openbanking.accelerator.identity.dcr.validation.validationgroups.AttributeChecks;
import com.wso2.openbanking.accelerator.identity.dcr.validation.validationgroups.MandatoryChecks;
import com.wso2.openbanking.cds.identity.dcr.validation.annotation.ValidateSSACallbackUris;
import com.wso2.openbanking.cds.identity.dcr.validation.annotation.ValidateSSAIssuer;
import com.wso2.openbanking.cds.identity.dcr.validation.annotation.ValidateSSAScopes;
import com.wso2.openbanking.cds.identity.dcr.validation.annotation.ValidateSSASoftwareRoles;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;


/**
 * CDSSoftwareStatementBody class.
 */
public class CDSSoftwareStatementBody extends SoftwareStatementBody {

    @SerializedName("logo_uri")
    private String logoUri;

    @SerializedName("legal_entity_id")
    private String legalEntityId;

    @SerializedName("legal_entity_name")
    private String legalEntityName;

    @SerializedName("client_description")
    private String clientDescription;

    @SerializedName("client_uri")
    private String clientUri;

    @SerializedName("sector_identifier_uri")
    private String sectorIdentifierUri;

    @SerializedName("tos_uri")
    private String tosUri;

    @SerializedName("policy_uri")
    private String policyUri;

    @SerializedName("revocation_uri")
    private String revocationUri;

    @SerializedName("recipient_base_uri")
    private String recipientBaseUri;

    @SerializedName("software_roles")
    private String softwareRoles;

    public String getLogoUri() {

        return logoUri;
    }

    public void setLogoUri(String logoUri) {

        this.logoUri = logoUri;
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

    public String getPolicyUri() {

        return policyUri;
    }

    public void setPolicyUri(String policyUri) {

        this.policyUri = policyUri;
    }

    public String getJwksRevocationUri() {

        return revocationUri;
    }

    public void setJwksRevocationUri(String revocationUri) {

        this.revocationUri = revocationUri;
    }

    public String getRecipientBaseUri() {

        return recipientBaseUri;
    }

    public void setRecipientBaseUri(String recipientBaseUri) {

        this.recipientBaseUri = recipientBaseUri;
    }

    @ValidateSSASoftwareRoles(message = "Invalid Software Roles in software statement:" +
            DCRCommonConstants.INVALID_META_DATA, groups = AttributeChecks.class)
    @NotBlank(message = "Software Roles can not be null or empty in SSA:" + DCRCommonConstants.INVALID_META_DATA,
            groups = MandatoryChecks.class)
    public String getSoftwareRoles() {

        return softwareRoles;
    }

    public void setSoftwareRoles(String softwareRoles) {

        this.softwareRoles = softwareRoles;
    }

    @Override
    @ValidateSSAIssuer(message = "Invalid Issuer in software statement:" + DCRCommonConstants.INVALID_META_DATA,
            groups = AttributeChecks.class)
    @NotBlank(message = "Issuer can not be null or empty in SSA:" + DCRCommonConstants.INVALID_META_DATA,
            groups = MandatoryChecks.class)
    public String getSsaIssuer() {

        return super.getSsaIssuer();
    }

    @Override
    @ValidateSSACallbackUris(message = "Redirect URIs do not contain the same hostname:" +
            DCRCommonConstants.INVALID_META_DATA, groups = AttributeChecks.class)
    @NotEmpty(message = "Redirect URIs can not be null or empty in SSA:" + DCRCommonConstants.INVALID_META_DATA,
            groups = MandatoryChecks.class)
    public List<String> getCallbackUris() {

        return super.getCallbackUris();
    }

    @Override
    @ValidateSSAScopes(message = "Invalid scopes in software statement:" +
            DCRCommonConstants.INVALID_META_DATA, groups = AttributeChecks.class)
    @NotBlank(message = "Scopes can not be null or empty in SSA:" + DCRCommonConstants.INVALID_META_DATA,
            groups = MandatoryChecks.class)
    public String getScopes() {

        return super.getScopes();
    }
}
