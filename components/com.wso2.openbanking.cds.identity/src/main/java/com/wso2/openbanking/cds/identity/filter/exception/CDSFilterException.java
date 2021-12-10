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

package com.wso2.openbanking.cds.identity.filter.exception;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;

/**
 * CDS filter exception
 */
public class CDSFilterException extends OpenBankingException {

    private String errorDescription;
    private int errorCode;

    public CDSFilterException(int errorCode, String error, String errorDescription, Throwable e) {

        super(error, e);
        this.errorDescription = errorDescription;
        this.errorCode = errorCode;
    }

    public CDSFilterException(int errorCode, String error, String errorDescription) {

        super(error);
        this.errorDescription = errorDescription;
        this.errorCode = errorCode;
    }

    public CDSFilterException(String message, Throwable e) {

        super(message, e);
    }

    public String getErrorDescription() {

        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {

        this.errorDescription = errorDescription;
    }

    public int getErrorCode() {

        return errorCode;
    }

    public void setErrorCode(int errorCode) {

        this.errorCode = errorCode;
    }
}
