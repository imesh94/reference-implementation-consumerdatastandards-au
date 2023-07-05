/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.account.type.management.endpoint.disclosure.options.model;

import java.util.List;
import javax.validation.Valid;

/**
 * Disclosure Options Management - DOMSStatusUpdateDTOList
 */
public class DOMSStatusUpdateListDTO {

    @Valid
    private List<DOMSStatusUpdateDTO> data;

    public List<DOMSStatusUpdateDTO> getData() {
        return data;
    }

    public void setData(List<DOMSStatusUpdateDTO> data) {
        this.data = data;
    }

}