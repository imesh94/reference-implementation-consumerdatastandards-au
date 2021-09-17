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
package com.wso2.openbanking.cds.gateway.executors.idpermanence;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.openbanking.accelerator.gateway.executor.core.OpenBankingGatewayExecutor;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIRequestContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OBAPIResponseContext;
import com.wso2.openbanking.accelerator.gateway.executor.model.OpenBankingExecutorError;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.common.utils.CommonConstants;
import com.wso2.openbanking.cds.gateway.executors.idpermanence.model.IdPermanenceValidationResponse;
import com.wso2.openbanking.cds.gateway.executors.idpermanence.utils.IdPermanenceConstants;
import com.wso2.openbanking.cds.gateway.executors.idpermanence.utils.IdPermanenceUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;

import java.util.ArrayList;

/**
 * IDPermanenceExecutor class
 * This class contains operations to encrypt and decrypt the cds resource ids in payloads and urls.
 */
public class IDPermanenceExecutor implements OpenBankingGatewayExecutor {

    private static final Log log = LogFactory.getLog(IDPermanenceExecutor.class);
    private static final String SECRET_KEY = OpenBankingCDSConfigParser.getInstance().getIdPermanenceSecretKey();

    @Override
    public void preProcessRequest(OBAPIRequestContext obApiRequestContext) {
    }

    @Override
    public void postProcessRequest(OBAPIRequestContext obApiRequestContext) {

        // Engage IDPermanence if config is enabled and cds api request
        if (OpenBankingCDSConfigParser.getInstance().isIdPermanenceEnabled() && obApiRequestContext.
                getApiRequestInfo().getContext().contains(CommonConstants.CDS_API_CONTEXT_PREFIX)) {
            log.debug("IDPermanence Engaged.");
            String uriTemplate = obApiRequestContext.getMsgInfo().getElectedResource();
            String requestedUrl = obApiRequestContext.getMsgInfo().getResource();

            // Handle requests with a body
            if (CommonConstants.POST_METHOD.equals(obApiRequestContext.getMsgInfo().getHttpMethod())) {
                Gson gson = new Gson();
                JsonObject payloadJson = gson.fromJson(obApiRequestContext.getRequestPayload(), JsonObject.class);
                IdPermanenceValidationResponse idPermanenceValidationResponse =
                        IdPermanenceUtils.unmaskRequestBodyAccountIDs(payloadJson, SECRET_KEY);
                if (!idPermanenceValidationResponse.isValid()) {
                    handleError(obApiRequestContext, idPermanenceValidationResponse);
                    return;
                }
                // set decrypted resource ids to the request body
                obApiRequestContext.setModifiedPayload(gson.toJson(idPermanenceValidationResponse.
                        getDecryptedResourceIds()));
            }

            // handle requests with path params
            if (IdPermanenceConstants.REQUEST_URLS_WITH_PATH_PARAMS.contains(uriTemplate)) {
                JsonObject idSet = IdPermanenceUtils.extractUrlParams(uriTemplate, requestedUrl);
                IdPermanenceValidationResponse idPermanenceValidationResponse =
                        IdPermanenceUtils.unmaskRequestPathIDs(idSet, SECRET_KEY);
                if (!idPermanenceValidationResponse.isValid()) {
                    handleError(obApiRequestContext, idPermanenceValidationResponse);
                    return;
                }
                // Set decrypted resource ids to uri
                JsonObject decryptedIdSet = idPermanenceValidationResponse.getDecryptedResourceIds();
                String decryptedSubRequestPath = IdPermanenceUtils.processNewUri(
                        uriTemplate, requestedUrl, decryptedIdSet);
                obApiRequestContext.getMsgInfo().setResource(decryptedSubRequestPath);
                obApiRequestContext.getMsgInfo().getHeaders().put(
                        IdPermanenceConstants.DECRYPTED_SUB_REQUEST_PATH, decryptedSubRequestPath);
            }
        }

    }

    @Override
    public void preProcessResponse(OBAPIResponseContext obApiResponseContext) {
    }

    @Override
    public void postProcessResponse(OBAPIResponseContext obApiResponseContext) {

        // Engage IDPermanence if config is enabled and cds api request
        if (OpenBankingCDSConfigParser.getInstance().isIdPermanenceEnabled() && obApiResponseContext.
                getApiRequestInfo().getContext().contains(CommonConstants.CDS_API_CONTEXT_PREFIX)) {

            // execute if success response
            if (obApiResponseContext.getStatusCode() == HttpStatus.SC_OK) {
                String electedResource = obApiResponseContext.getMsgInfo().getElectedResource();
                String memberId = obApiResponseContext.getApiRequestInfo().getUsername();
                String appId = obApiResponseContext.getApiRequestInfo().getConsumerKey();

                // set encrypted resource ids to the response
                JsonObject payloadJson = new JsonParser().parse(
                        obApiResponseContext.getResponsePayload()).getAsJsonObject();
                JsonObject modifiedPayloadJson = IdPermanenceUtils.maskResponseIDs(
                        payloadJson, electedResource, memberId, appId, SECRET_KEY);
                obApiResponseContext.setModifiedPayload(new Gson().toJson(modifiedPayloadJson));
            }
        }
    }

    /**
     * Add errors to the obApiRequestContext.
     *
     * @param obApiRequestContext            - OBAPIRequestContext
     * @param idPermanenceValidationResponse - Validation response
     */
    private void handleError(OBAPIRequestContext obApiRequestContext, IdPermanenceValidationResponse
            idPermanenceValidationResponse) {

        //catch errors and set to context
        ArrayList<OpenBankingExecutorError> executorErrors = obApiRequestContext.getErrors();
        executorErrors.add(idPermanenceValidationResponse.getError());
        obApiRequestContext.setError(true);
        obApiRequestContext.setErrors(executorErrors);
    }
}
