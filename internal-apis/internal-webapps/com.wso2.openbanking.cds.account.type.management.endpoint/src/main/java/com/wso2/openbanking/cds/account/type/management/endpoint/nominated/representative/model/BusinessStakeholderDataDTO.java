package com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model;

import java.util.List;
import javax.validation.Valid;

/**
 * BusinessStakeholderDataDTO
 */
public class BusinessStakeholderDataDTO {

    private List<String> accountOwners;

    @Valid
    private List<NominatedRepresentativeDTO> nominatedRepresentatives;

    public List<String> getAccountOwners() {
        return accountOwners;
    }

    public void setAccountOwners(List<String> accountOwners) {
        this.accountOwners = accountOwners;
    }

    public List<NominatedRepresentativeDTO> getNominatedRepresentatives() {
        return nominatedRepresentatives;
    }

    public void setNominatedRepresentatives(List<NominatedRepresentativeDTO> nominatedRepresentatives) {
        this.nominatedRepresentatives = nominatedRepresentatives;
    }
}
