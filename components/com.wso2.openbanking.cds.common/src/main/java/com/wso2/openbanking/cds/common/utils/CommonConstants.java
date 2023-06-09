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

package com.wso2.openbanking.cds.common.utils;

/**
 * Common Constant Class
 */
public class CommonConstants {

    public static final String OB_CONSENT_ID_PREFIX = "OB_CONSENT_ID_";
    public static final String CDS_API_CONTEXT_PREFIX = "cds-au";

    // Config related constants
    public static final String OB_CONFIG_FILE = "open-banking-cds.xml";
    public static final String ACCOUNT_MASKING = "ConsentManagement.EnableAccountMasking";
    public static final String ID_PERMANENCE_SECRET_KEY = "IdPermanence.SecretKey";
    public static final String TOKEN_ENCRYPTION_ENABLED = "TokenEncryption.Enable";
    public static final String TOKEN_ENCRYPTION_SECRETKEY = "TokenEncryption.SecretKey";
    public static final String INTROSPECT_FILTER_VALIDATORS = "FilterValidators.IntrospectFilterValidators.Validator";
    public static final String REVOKE_FILTER_VALIDATORS = "FilterValidators.RevokeFilterValidators.Validator";
    public static final String PAR_FILTER_VALIDATORS = "FilterValidators.ParFilterValidators.Validator";
    public static final String PRIORITIZE_SHARABLE_ACCOUNTS_RESPONSE = "BNR.PrioritizeSharableAccountsResponse";
    public static final String VALIDATE_ACCOUNTS_ON_RETRIEVAL = "BNR.ValidateAccountsOnRetrieval";
    public static final String ENABLE_CONSENT_REVOCATION = "BNR.EnableConsentRevocation";


    // Http related constants
    public static final String POST_METHOD = "POST";

    // JWT common claims
    public static final String ISSURE_CLAIM = "iss";
    public static final String SUBJECT_CLAIM = "sub";
    public static final String AUDIENCE_CLAIM = "aud";
    public static final String IAT_CLAIM = "iat";
    public static final String EXP_CLAIM = "exp";
    public static final String JTI_CLAIM = "jti";

    // Server configuration constants
    public static final String KEYSTORE_LOCATION = "Security.InternalKeyStore.Location";
    public static final String KEYSTORE_PASSWORD = "Security.InternalKeyStore.Password";
    public static final String KEYSTORE_KEY_ALIAS = "Security.InternalKeyStore.KeyAlias";
    public static final String KEYSTORE_KEY_PASSWORD = "Security.InternalKeyStore.KeyPassword";

    // metadata config constants
    public static final String METADATA_CACHE = "MetaDataCache";
    public static final String METADATA_CACHE_ENABLED = METADATA_CACHE + ".EnableMetaDataCache";
    public static final String METADATA_CACHE_UPDATE_TIME = METADATA_CACHE + ".MetaDataCacheUpdatePeriod";
    public static final String METADATA_CACHE_DATA_RECIPIENTS_URL = METADATA_CACHE + ".DataRecipientsDiscoveryURL";
    public static final String METADATA_CACHE_DCR_INTERNAL_URL = METADATA_CACHE + ".DCRInternalURL";
    public static final String METADATA_CACHE_APPLICATION_SEARCH_URL = METADATA_CACHE + ".APIMApplicationSearchURL";
    public static final String METADATA_CACHE_RETRY_COUNT = METADATA_CACHE + ".RetryCount";
    public static final String METADATA_CACHE_EXPIRY = METADATA_CACHE + ".Expiry";
    public static final String METADATA_CACHE_BULK_EXECUTE = METADATA_CACHE +
            ".DataHolderResponsibilities.BulkExecution";
    public static final String METADATA_CACHE_BULK_EXECUTE_HOUR = METADATA_CACHE +
            ".DataHolderResponsibilities.BulkExecutionHour";
    public static final String HOLDER_SPECIFIC_IDENTIFIER = "Headers.HolderIdentifier";

    // Admin API config constants
    public static final String ADMIN_API_SELF_LINK = "Admin.APISelfLink";

    // DCR configs
    public static final String ENABLE_REQUEST_JTI_VALIDATION = "DCR.EnableRequestJTIValidation";
    public static final String ENABLE_SSA_JTI_VALIDATION = "DCR.EnableSSAJTIValidation";
    public static final String JTI_CACHE_ACCESS_EXPIRY = "DCR.JTICache.CacheAccessExpiry";
    public static final String JTI_CACHE_MODIFY_EXPIRY = "DCR.JTICache.CacheModifiedExpiry";

    // Self-signed JWT Authentication config constants
    public static final String JWT_AUTH_ENABLED = "JWTAuthentication.Enabled";
    public static final String JWT_AUTH_ISS = "JWTAuthentication.Issuer";
    public static final String JWT_AUTH_SUB = "JWTAuthentication.Subject";
    public static final String JWT_AUTH_AUD = "JWTAuthentication.Audience";
    public static final String JWT_AUTH_JWKS_URL = "JWTAuthentication.JWKSUrl";
    public static final String JWT_AUTH_CACHE_EXPIRY_TIME = "JWTAuthentication.CacheExpiryTime";
    public static final String JWT_AUTH_CACHE_MODIFY_EXPIRY = "JWTAuthentication.CacheModifiedExpiry";

    // Default value constants
    public static final String DEFAULT_DATA_RECIPIENT_DISCOVERY_URL =
            "https://api.cdr.gov.au/cdr-register/v1/banking/data-recipients";
    public static final int DEFAULT_META_DATA_CACHE_UPDATE_PERIOD = 5;
    public static final int DEFAULT_CACHE_EXPIRY = 2;
    public static final int DEFAULT_RETRY_COUNT = 2;
    public static final int DEFAULT_BULK_EXECUTION_HOUR_2AM = 2;

    // Secondary user account config constants
    public static final String SECONDARY_USER_ACCOUNTS_ENABLED = "SecondaryUserAccounts.Enable";

    // Ceasing Secondary User Sharing Config Constants
    public static final String CEASING_SECONDARY_USER_SHARING_ENABLED =
            "SecondaryUserAccounts.CeasingSecondaryUserSharingEnabled";

    private CommonConstants() {
    }
}
