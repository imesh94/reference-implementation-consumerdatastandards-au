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
package com.wso2.openbanking.cds.gateway.executors.core;

/**
 * Constants required for CDSAPIRequestRouter.
 */
public class RequestRouterConstants {

    // Executor list names
    public static final String DEFAULT = "Default";
    public static final String DCR = "DCR";
    public static final String CDS = "CDS";
    public static final String CONSENT = "Consent";

    // API Type constants
    public static final String API_TYPE_CUSTOM_PROP = "x-wso2-api-type";
    public static final String API_TYPE_CONSENT = "consent";
    public static final String API_TYPE_NON_REGULATORY = "non-regulatory";
    public static final String API_TYPE_DCR = "dcr";
    public static final String API_TYPE_CDS = "cds";

    // API Name constants
    public static final String DCR_API_NAME = "CDR Dynamic Client Registration API";
    public static final String CDS_API_NAME = "ConsumerDataStandards";
}
