/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.openbanking.cds.common.metadata.periodical.updater.constants;

/**
 * Constants used across Metadata Updater.
 */
public class MetadataConstants {

    public static final String DR_JSON_ROOT                 = "data";
    public static final String DR_JSON_STATUS               = "status";
    public static final String DR_JSON_SP_KEY               = "softwareProductId";
    public static final String DR_JSON_BRANDS               = "dataRecipientBrands";
    public static final String DR_JSON_SOFTWARE_PRODUCTS    = "softwareProducts";
    public static final String DR_JSON_LEGAL_ENTITY_ID      = "legalEntityId";

    public static final String MAP_DATA_RECIPIENTS          = "MAP_DATA_RECIPIENTS";
    public static final String MAP_SOFTWARE_PRODUCTS        = "MAP_SOFTWARE_PRODUCTS";

    public static final String AUTH_BASIC                   = "Basic ";
    public static final String LEGAL_ENTITY_ID              = "legal_entity_id";
    public static final String SOFTWARE_PRODUCT_ID          = "software_id";
    public static final String LIST                         = "list";
    public static final String APPLICATION_ID               = "applicationId";
    public static final String APPLICATION_NAME             = "name";

    private MetadataConstants() {}
}
