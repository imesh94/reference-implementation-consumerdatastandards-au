/**
 * Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.openbanking.cds.identity.filter.exception;

import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;

/**
 * CDS filter exception.
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
