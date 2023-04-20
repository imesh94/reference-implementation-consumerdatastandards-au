package com.wso2.openbanking.cds.account.type.management.endpoint.nominated.representative.model;

import java.util.List;
import javax.validation.Valid;

/**
 * BusinessStakeholderListDTO
 */
public class BusinessStakeholderListDTO {

    @Valid
    private List<BusinessStakeholderDataDTO> data;

    public List<BusinessStakeholderDataDTO> getData() {
        return data;
    }

    public void setData(List<BusinessStakeholderDataDTO> data) {
        this.data = data;
    }
}
