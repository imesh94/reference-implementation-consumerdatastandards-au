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
package com.wso2.openbanking.cds.identity.filter;

import com.nimbusds.jwt.SignedJWT;
import com.wso2.openbanking.accelerator.common.util.Generated;
import com.wso2.openbanking.accelerator.identity.token.wrapper.RequestWrapper;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonConstants;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonHelper;
import com.wso2.openbanking.accelerator.identity.util.IdentityCommonUtil;
import com.wso2.openbanking.cds.identity.filter.exception.CDSFilterException;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

/**
 * Base class for CDS specific tomcat filters.
 */
public class CDSBaseFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

    }

    /**
     * Append the transport header to the request
     *
     * @param request
     * @param response
     * @return ServletRequest
     * @throws ServletException
     */
    protected ServletRequest appendTransportHeader(ServletRequest request, ServletResponse response) throws
            ServletException, IOException {

        if (request instanceof HttpServletRequest) {
            Object certAttribute = request.getAttribute(IdentityCommonConstants.JAVAX_SERVLET_REQUEST_CERTIFICATE);
            String x509Certificate = ((HttpServletRequest) request).getHeader(IdentityCommonUtil.getMTLSAuthHeader());
            if (certAttribute != null) {
                RequestWrapper requestWrapper = new RequestWrapper((HttpServletRequest) request);
                X509Certificate certificate = getCertificateFromAttribute(certAttribute);
                requestWrapper.setHeader(IdentityCommonUtil.getMTLSAuthHeader(),
                        getCertificateContent(certificate));
                return requestWrapper;
            } else if (new IdentityCommonHelper().isTransportCertAsHeaderEnabled() && x509Certificate != null) {
                return request;
            } else {
                handleValidationFailure((HttpServletResponse) response,
                        HttpServletResponse.SC_BAD_REQUEST, "Transport certificate not found",
                        "Transport certificate not found in the request");
            }
        } else {
            throw new ServletException("Error occurred when handling the request, passed request is not a " +
                    "HttpServletRequest");
        }
        return request;
    }

    private String getCertificateContent(X509Certificate certificate) throws ServletException {

        if (certificate != null) {
            try {
                Base64 encoder = new Base64();
                byte[] encodedContent = certificate.getEncoded();
                return IdentityCommonConstants.BEGIN_CERT + new String(encoder.encode(encodedContent),
                        StandardCharsets.UTF_8) + IdentityCommonConstants.END_CERT;
            } catch (CertificateEncodingException e) {
                throw new ServletException("Certificate not valid", e);
            }
        } else {
            return null;
        }
    }

    private X509Certificate getCertificateFromAttribute(Object certObject) throws ServletException {

        if (certObject instanceof X509Certificate[]) {
            X509Certificate[] cert = (X509Certificate[]) certObject;
            return cert[0];
        } else if (certObject instanceof X509Certificate) {
            return (X509Certificate) certObject;
        }
        return null;
    }

    /**
     * Extracts the client id from the request parameter or from the assertion.
     *
     * @param request servlet request containing the request data
     * @return clientId
     * @throws ParseException
     */
    @Generated(message = "Excluding from code coverage since it requires a service call")
    protected String extractClientId(ServletRequest request) throws CDSFilterException {

        try {
            Optional<String> signedObject =
                    Optional.ofNullable(request.getParameter(IdentityCommonConstants.OAUTH_JWT_ASSERTION));
            Optional<String> clientIdAsReqParam =
                    Optional.ofNullable(request.getParameter(IdentityCommonConstants.CLIENT_ID));
            if (signedObject.isPresent()) {
                SignedJWT signedJWT = SignedJWT.parse(signedObject.get());
                return signedJWT.getJWTClaimsSet().getIssuer();
            } else if (clientIdAsReqParam.isPresent()) {
                return clientIdAsReqParam.get();
            } else {
                throw new CDSFilterException(HttpServletResponse.SC_BAD_REQUEST, "Client ID not retrieved",
                        "Unable to find client id in the request");
            }
        } catch (ParseException e) {
            throw new CDSFilterException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid assertion", "Error " +
                    "occurred while parsing the signed assertion", e);
        }
    }

    /**
     * Respond when there is a failure in filter validation.
     *
     * @param response     HTTP servlet response object
     * @param status       HTTP status code
     * @param error        error
     * @param errorMessage error description
     * @throws IOException
     */
    protected void handleValidationFailure(HttpServletResponse response, int status, String error, String errorMessage)
            throws IOException {

        JSONObject errorJSON = new JSONObject();
        errorJSON.put(IdentityCommonConstants.OAUTH_ERROR, error);
        errorJSON.put(IdentityCommonConstants.OAUTH_ERROR_DESCRIPTION, errorMessage);

        try (OutputStream outputStream = response.getOutputStream()) {
            response.setStatus(status);
            response.setContentType(MediaType.APPLICATION_JSON);
            outputStream.write(errorJSON.toString().getBytes(StandardCharsets.UTF_8));
        }
    }
}
