/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */
package com.wso2.openbanking.cds.gateway.executors.idpermanence.model;

import com.google.gson.JsonObject;
import com.wso2.openbanking.accelerator.gateway.executor.model.OpenBankingExecutorError;

/**
 * Model representation of a Id Permanence Validation Response
 */
public class IdPermanenceValidationResponse {

    private boolean isValid;
    private int httpStatus;
    private JsonObject decryptedResourceIds;
    //private JSONArray errors;
    private OpenBankingExecutorError error;

    public IdPermanenceValidationResponse() {
    }

    public IdPermanenceValidationResponse(boolean isValid, JsonObject decryptedResourceIds) {
        this.isValid = isValid;
        this.decryptedResourceIds = decryptedResourceIds;
    }

    public IdPermanenceValidationResponse(boolean isValid, int httpStatus, JsonObject decryptedResourceIds,
                                          OpenBankingExecutorError error) {
        this.isValid = isValid;
        this.httpStatus = httpStatus;
        this.decryptedResourceIds = decryptedResourceIds;
        this.error = error;
    }

    /**
     * Get valid status
     *
     * @return True is valid, False if invalid
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Set valid status
     *
     * @param valid valid status
     */
    public void setValid(boolean valid) {
        this.isValid = valid;
    }

    /**
     * Get http status code
     *
     * @return http status code
     */
    public int getHttpStatus() {
        return httpStatus;
    }

    /**
     * Set http status code
     *
     * @param httpStatus http status code
     */
    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    /**
     * Get decrypted masked resource Ids sent in the request path or body
     *
     * @return unmasked resource Ids
     */
    public JsonObject getDecryptedResourceIds() {
        return decryptedResourceIds;
    }

    /**
     * Set decrypted masked resource Ids sent in the request path or body as a Json Object
     *
     * @param decryptedResourceIds unmasked resource Ids
     */
    public void setDecryptedResourceIds(JsonObject decryptedResourceIds) {
        this.decryptedResourceIds = decryptedResourceIds;
    }

    /**
     * Get list of errors
     *
     * @return array of error objects
     */
    public OpenBankingExecutorError getError() {
        return error;
    }

    /**
     * Set error
     *
     * @param error error object
     */
    public void setError(OpenBankingExecutorError error) {
        this.error = error;
    }
}
