/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.cds.test.framework.constant

import com.wso2.openbanking.test.framework.constant.OBConstants
import com.wso2.cds.test.framework.configuration.AUConfigurationService
import com.wso2.cds.test.framework.utility.AUTestUtil

/**
 * Class for provide AU and OB constants to the
 * AU layer and tests
 */
class AUConstants extends OBConstants {

    static AUConfigurationService auConfiguration = new AUConfigurationService()

    public static final String API_VERSION = auConfiguration.getCommonApiVersion()

    public static final String X_V_HEADER = "x-v"
    public static final String X_MIN_HEADER = "x-min-v"
    public static final int CDR_ENDPOINT_VERSION = 1
    public static final String CDS_100_PATH = "/cds-au/v1"
    public static final String CDS_PATH = "/cds-au/v1"
    public static final String ACCEPT = "application/json"
    public static final String X_FAPI_INTERACTION_ID = "x-fapi-interaction-id"
    public static final String CONTENT_TYPE = "Content-Type"
    public static final String X_FAPI_AUTH_DATE = "x-fapi-auth-date"
    public static final String X_CDS_CLIENT_HEADERS = "x-cds-client-headers"
    public static final String AUTHORIZATION_BEARER_TAG = "Bearer ";
    public static final String X_FAPI_CUSTOMER_IP_ADDRESS = "x-fapi-customer-ip-address"
    public static final String UUID = AUTestUtil.generateUUID()
    public static final String DATE = AUTestUtil.getDateAndTime()
    public static final String DATE_FORMAT = AUTestUtil.getDate()
    public static final String CONSENT_EXPIRE_DATE = AUTestUtil.getTommorowDateAndTime()
    public static final String IP = "2001:0db8:85a3:0000:0000:8a2e:0370:7334"
    public static final String PRODUCT_CATEGORY = "TRANS_AND_SAVINGS_ACCOUNTS"
    public static final String PRODUCT = "product-category"
    public static final String OPEN_STATUS_PARAM = "open-status"
    public static final String STATUS_OPEN = "OPEN"
    public static final String STATUS_CLOSED = "CLOSED"
    public static final String GET_ACCOUNTS = "/banking/accounts"
    public static final String GET_BALANCES = "/banking/accounts/balances"
    public static final String GET_TRANSACTIONS = "/banking/accounts/" + accountID + "/transactions"
    public static final String GET_PRODUCTS = "/banking/products"
    public static final String TOKEN = "d94c5b2e-b615-366e-862b-374b429e4d5e"
    public static final String accountID = "qu4WMZ-59LsndgjMN-kikFfJL4Z87VlRvoxej0mrh307U-WM2t9RagzY82qc7_xZVn-Agk_K8WIAWdgTgofMvmR-Kl9_PaageLbaVzytdVHpEudIRSQDvUivfs6FvgrA"
    public static final String accountID2 = "qu4WMZ-59LsndgjMN-kikFfJL4Z87VlRvoxej0mrh33jm9OC5hnFFpVjCHirvpVGXcVo1GqNjL_PTaCcjZPe-lHCZx_QOOT_PIMnZSWijYkBTvXlMLnFszvdN28n3WWA"
    public static final String GET_META = "/admin/register/metadata"
    public static final String GET_STAT = "/admin/metrics"
    public static final String X_FAPI_FINANCIAL_ID = "x-fapi-financial-id"
    public static final Integer X_V_HEADER_ACCOUNTS = 1
    public static final Integer X_V_HEADER_METRICS = 1
    public static final Integer X_V_HEADER_CUSTOMER = 1
    public static final Integer X_V_HEADER_PRODUCTS = getProductEndpointVersion()
    public static final Integer UNSUPPORTED_X_V_VERSION = 5
    public static final String USERNAME = "admin@wso2.com"
    public static final String PASSWORD = "wso2123"
    public static final String ACCESS_TOKEN = "token"
    public static final String CONTENT = "application/x-www-form-urlencoded"
    public static final String CODE = "code"
    public static final String ERROR_INVALID_SOFTWARE_PRODUCT = "Invalid Software Product"

    public static final long DEFAULT_SHARING_DURATION = 60000
    public static final long SINGLE_ACCESS_CONSENT = 0
    public static final long ONE_YEAR_DURATION = 31536200
    public static final long NEGATIVE_DURATION = -3000
    public static final long AMENDED_SHARING_DURATION = 90000
    public static final long SHORT_SHARING_DURATION = 20

    public static final String BULK_ACCOUNT_PATH = "/banking/accounts"
    public static final String SINGLE_ACCOUNT_PATH = "/banking/accounts/" + accountID
    public static final String BANKING_PRODUCT_PATH = "/banking/products"
    public static final String BULK_BALANCES_PATH = "/banking/accounts/balances"
    public static final String BULK_DIRECT_DEBITS_PATH = "/banking/accounts/direct-debits"
    public static final String BULK_SCHEDULE_PAYMENTS_PATH = "/banking/payments/scheduled"
    public static final String BULK_PAYEES = "/banking/payees"
    public static final String BULK_CUSTOMER = "/common/customer"
    public static final String CUSTOMER_DETAILS = "/common/customer/detail"
    public static final String DISCOVERY_STATUS = "/discovery/status"
    public static final String DISCOVERY_OUTAGES = "/discovery/outages"
    public static final String ACCOUNTS_CONSENT_PATH = "/au100/accounts-validation"
    public static final String CDR_ARRANGEMENT_ENDPOINT = "/arrangements/1.0.0"
    public static final String INTROSPECTION_ENDPOINT = "/oauth2/introspect"
    public static final String CONSENT_STATUS_ENDPOINT = "/api/openbanking/consent-mgt/uk300"
    public static final String PUSHED_AUTHORISATION_BASE_PATH = auConfiguration.getServerAuthorisationServerURL() +
            "/api/openbanking/push-authorization"
    public static final String PAR_ENDPOINT = "/par"
    public static final String REVOKE_PATH = "/revoke"
    public static final String STATUS_PATH = "/account-confirmation"
    public static final String CONSENT_STATUS_AU_ENDPOINT = "/api/openbanking/consent-mgt/au"
    public static final String UPDATE_BUSINESS_USER  = "/account-access-consents/business-stakeholders"
    public static final String SHARABLE_BANK_ACCOUNT_SERVICE= "/api/openbanking/backend-uk"
    public static final String BANK_ACCOUNT_SERVICE  = "/services/bankaccounts/bankaccountservice/sharable-accounts"
    public static final String BUSINESS_USER_PERMISSION  = "/account-access-consents/business-stakeholders/permission"

    public static final String BANK_CUSTOMER_BASIC_READ = "Organisation profile and contact details"
    public static final String BANK_CUSTOMER_DETAIL_READ = "Organisation profile and contact details*‡"
    public static final String BANK_ACCOUNT_BASIC_READ = "Account name, type, and balance"
    public static final String BANK_ACCOUNT_DETAIL_READ = "Account balance and details‡"
    public static final String BANK_TRANSACTION_READ = "Transaction details"
    public static final String BANK_PAYEES_READ = "Saved payees"
    public static final String BANK_REGULAR_PAYMENTS_READ = "Direct debits and scheduled payments"
    public static final String BANK_CUSTOMER_BASIC_READ_INDIVIDUAL = "Name and occupation"
    public static final String BANK_CUSTOMER_BASIC_DETAIL_INDIVIDUAL = "Name, occupation, contact details ‡"

    public static final String LBL_OTP_TIMEOUT = "//div[@id='otpTimeout']";
    public static final String LBL_FOOTER_DESCRIPTION = "//div[@class='ui segment']/div/form/div/div";
    public static final String ELE_CONSENT_PAGE = "//form[@id='oauth2_authz_consent']";
    public static final String LBL_INCORRECT_USERNAME = "//div[@id='error-msg']";
    public static final String LBL_AUTHENTICATION_FAILURE = "//div[@id='failed-msg']";
    public static final String LBL_AGENT_NAME_AND_ROLE = "Agent name and role"
    public static final String LBL_ORGANISATION_NAME = "Organisation name"
    public static final String LBL_ORGANISATION_NUMBER = "Organisation numbers (ABN or ACN)"
    public static final String LBL_CHARITY_STATUS = "Charity status"
    public static final String LBL_ESTABLISHMENT_DATE = "Establishment date"
    public static final String LBL_INDUSTRY = "Industry"
    public static final String LBL_ORGANISATION_TYPE = "Organisation type"
    public static final String LBL_COUNTRY_OF_REGISTRATION = "Country of registration"
    public static final String LBL_ORGANISATION_ADDRESS = "Organisation address"
    public static final String LBL_MAIL_ADDRESS = "Mail address"
    public static final String LBL_PHONE_NUMBER = "Phone number"
    public static final String LBL_NAME_OF_ACCOUNT = "Name of account"
    public static final String LBL_TYPE_OF_ACCOUNT = "Type of account"
    public static final String LBL_ACCOUNT_BALANCE = "Account balance"
    public static final String LBL_ACCOUNT_NUMBER = "Account number"
    public static final String LBL_INTEREST_RATES = "Interest rates"
    public static final String LBL_FEES = "Fees"
    public static final String LBL_DISCOUNTS = "Discounts"
    public static final String LBL_ACCOUNT_TERMS = "Account terms"
    public static final String LBL_ACCOUNT_MAIL_ADDRESS = "Account mail address"
    public static final String LBL_INCOMING_AND_OUTGOING_TRANSACTIONS = "Incoming and outgoing transactions"
    public static final String LBL_AMOUNTS = "Amounts"
    public static final String LBL_DATES = "Dates"
    public static final String LBL_DESCRIPTION_OF_TRANSACTION = "Descriptions of transactions"
    public static final String LBL_NAME_OF_MONEY_RECIPIENT = "Who you have sent money to and received money from" +
            "(e.g.their name)"
    public static final String LBL_DIRECT_DEBITS = "Direct debits"
    public static final String LBL_SCHEDULE_PAYMENTS = "Scheduled payments"
    public static final String LBL_DETAILS_OF_SAVED_ACCOUNTS = "Names and details of accounts you have saved; " +
            "(e.g. their BSB and Account Number, BPay CRN and Biller code, or NPP PayID)"
    public static final String LBL_WHERE_TO_MANAGE_INSTRUCTION = "You can review and manage this arrangement on " +
            "the Data Sharing dashboard by going to Settings>Data Sharing on the Mock Company Inc.," +
            "Mock Software website or app."

    public static final String LBL_AUTHORISED = 'Authorised'
    public static final String LBL_REVOKED = 'Revoked'
    public static final String LBL_STOP_SHARING = 'Stop sharing'
    public static final String LBL_CONSENT_GRANTED = 'Consent granted: '
    public static final String LBL_CREATED_ON = "Created on: "
    public static final String LBL_WHEN_YOU_GAVE_CONSENT = 'When you gave consent'
    public static final String LBL_WHEN_YOUR_CONSENT_EXPIRE = 'When your consent will expire'
    public static final String LBL_SHARING_PERIOD = 'Sharing period'
    public static final String LBL_RATES_FEES_DISCOUNT = "Interest rates, Fees, Discounts"
    public static final String LBL_AMOUNTS_AND_DATES = "Amounts, Dates"
    public static final String LBL_PHONE = "Phone"
    public static final String LBL_EMAIL_ADDRESS = "Email address"
    public static final String LBL_RESIDENTIAL_ADDRESS = "Residential address"
    public static final String LBL_NAME = "Name"
    public static final String LBL_OCCUPATION = "Occupation"

    public static final String CONSENT_MANAGER_URL = auConfiguration.getServerAuthorisationServerURL() + "/consentmgt"
    public static final String OAUTH2_INTROSPECT_URL = auConfiguration.getServerAuthorisationServerURL() + "/oauth2/introspect"
    public static final String CCPORTAL_URL = auConfiguration.getServerAuthorisationServerURL() + "/ccportal"

    public static final String ERROR = "error";
    public static final String ERROR_DESCRIPTION = "error_description";
    public static final String ERROR_DETAIL = "errors[0].detail"
    public static final String ERROR_SOURCE_PARAMETER = "errors[0].source.parameter"
    public static final String ERROR_SOURCE_POINTER = "errors[0].source.pointer"
    public static final String ERROR_TITLE = "errors[0].title"
    public static final String ERROR_CODE = "errors[0].code"
    public static final String ERROR_META_URN = "errors[0].meta.urn"
    public static final String ERROR_OPENSTATUS = "data.accounts.openStatus[0]"

    public static final String ERROR_X_V_INVALID = "x-v header in the request is invalid"
    public static final String ERROR_X_V_MISSING = "Mandatory header x-v is missing"
    public static final String ERROR_X_MIN_V_INVALID = "x-min-v header in the request is invalid"
    public static final String ERROR_X_V_INVALID_VERSION = "Requested x-v version is not supported"
    public static final String ERROR_ENDPOINT_VERSION4 = "Requested endpoint version 4 is not supported"
    public static final String ERROR_ENDPOINT_VERSION5 = "Requested endpoint version 5 is not supported"
    public static final String INVALID_FIELD = "Invalid Field"
    public static final String INVALID_HEADER = "Invalid Header"
    public static final String UNSUPPORTED_VERSION = "Unsupported Version"
    public static final String INVALID_VERSION = "Invalid Version"
    public static final String PAGE_SIZE_EXCEEDED = "Page Size Exceeded"
    public static final String MISSING_HEADER = "Missing Required Header"
    public static final String INVALID_BANK_ACC = "Invalid Banking Account"
    public static final String ACCOUNT_ID_NOT_FOUND = "ID of the account not found or invalid"
    public static final String INVALID_CLIENT_METADATA = "invalid_client_metadata"
    public static final String INVALID_RESOURCE = "Invalid Resource"
    public static final String INVALID_AUTHORISATION = "Invalid Authorisation Header"
    public static final String RESOURCE_FORBIDDEN = "Resource Is Forbidden"
    public static final String INVALID_REDIRECT_URI = "invalid_redirect_uri"
    public static final String UNAPPROVED_SOFTWARE_STATEMENT = "unapproved_software_statement"
    public static final String RESOURCE_NOT_FOUND = "Resource Not Found"
    public static final String MESSAGE_THROTTLED_OUT = "Message throttled out"
    public static final String REQUEST_URI = "requestUri"
    public static final String INVALID_CLIENT = "invalid_client"
    public static final String MISSING_CREDENTIALS = "Missing Credentials"

    public static final String ERROR_CODE_MISSING_HEADER = "urn:au-cds:error:cds-all:Header/Missing"
    public static final String ERROR_CODE_INVALID_HEADER = "urn:au-cds:error:cds-all:Header/Invalid"
    public static final String ERROR_CODE_INVALID_FIELD = "AU.CDR.Invalid.Field"
    public static final String ERROR_CODE_UNSUPPORTED_VERSION = "urn:au-cds:error:cds-all:Header/UnsupportedVersion"
    public static final String ERROR_CODE_INVALID_VERSION = "urn:au-cds:error:cds-all:Header/InvalidVersion"
    public static final String ERROR_CODE_INVALID_BANK_ACC = "urn:au-cds:error:cds-banking:Authorisation/InvalidBankingAccount"
    public static final String ERROR_CODE_INVALID_RESOURCE = "urn:au-cds:error:cds-all:Resource/Invalid"
    public static final String ERROR_CODE_PAGE_SIZE_TOO_LARGE = "AU.CDR.Invalid.PageSizeTooLarge"
    public static final String ERROR_CODE_UNAUTHORIZED = "AU.CDR.Unauthorized"
    public static final String ERROR_CODE_RESOURCE_FORBIDDEN = "AU.CDR.Entitlements.Forbidden"
    public static final String ERROR_CODE_RESOURCE_NOTFOUND = "urn:au-cds:error:cds-all:Resource/NotFound"
    public static final String ERROR_CODE_TOO_MANY_REQUESTS = "AU.CDR.TooManyRequests"
    public static final String ERROR_CODE_INVALID_SP_STATUS = "AU.CDR.Entitlements.InvalidAdrSoftwareProductStatus"
    public static final String ERROR_TITLE_INVALID_SP_STATUS = "ADR Software Product Status Is Invalid"
    public static final String ERROR_CODE_INVALID_ADR_STATUS = "AU.CDR.Entitlements.InvalidAdrStatus"
    public static final String ERROR_TITLE_INVALID_ADR_STATUS = "ADR Status Is Invalid"
    public static final String ERROR_CODE_GENERAL_ERROR_UNEXPECTED = "urn:au-cds:error:cds-all:GeneralError/Unexpected"

    // Headers
    public static final String PARAM_X_V = "x-v"
    public static final String PARAM_X_MIN_V = "x-min-v"
    public static final String PARAM_PRODUCT_CATEGORY = "product-category"
    public static final String PARAM_CDS_CLIENT_HEADER = "x-cds-client-headers"
    public static final String PARAM_FAPI_INTERACTION_ID = "x-fapi-interaction-id"
    public static final String PARAM_FAPI_CUSTOMER_IP_ADDRESS = "x-fapi-customer-ip-address"
    public static final String PARAM_FAPI_AUTH_DATE = "x-fapi-auth-date"
    public static final String PARAM_ACCOUNT_ID = "accountID"
    public static final String PARAM_PAGE_SIZE = "page-size"
    public static final String PARAM_AUTHORIZATION = "Authorization"
    public static final String ACCOUNT_OWNER_USER_ID = "accountOwnerUserID"
    public static final String NOMINATED_REP_USER_ID = "nominatedRepUserID"
    public static final String ACCOUNT_OWNER_USER_ID2 = "accountOwnerUserID2"
    public static final String NOMINATED_REP_USER_ID2 = "nominatedRepUserID2"
    public static final String ACCOUNT_ID = "account_id"
    public static final String BUSINESS_ACCOUNT_INFO = "businessAccountInfo"
    public static final String ACCOUNT_OWNERS = "AccountOwners"
    public static final String NOMINATED_REPRESENTATIVES = "NominatedRepresentatives"
    public static final String MEMBER_ID = "memberId"

    public static final String VALUE_FAPI_AUTH_DATE = "Tue, 78 Jan 1312 80:05:73 GMT"

    public static final String LBL_USER_ID = "admin@wso2.com"
    public static final String LBL_CARBON = "@carbon.super"
    public static final String LBL_ALL_APPLICATION = "All Applications"
    public static final String LBL_APPLICATION_ID = "app"

    public static final String BASE_PATH_TYPE_ACCOUNT = "Accounts"
    public static final String BASE_PATH_TYPE_BALANCES = "Balances"
    public static final String BASE_PATH_TYPE_TRANSACTIONS = "Transactions"
    public static final String BASE_PATH_TYPE_DIRECT_DEBIT = "Direct-Debit"
    public static final String BASE_PATH_TYPE_SCHEDULED_PAYMENT = "Scheduled-Payment"
    public static final String BASE_PATH_TYPE_PAYEES = "Payees"
    public static final String BASE_PATH_TYPE_PRODUCTS = "Product"
    public static final String BASE_PATH_TYPE_CUSTOMER = "Customer"
    public static final String BASE_PATH_TYPE_DISCOVERY = "Discovery"
    public static final String BASE_PATH_TYPE_CDR_ARRANGEMENT = "CDR-Arrangement"
    public static final String BASE_PATH_TYPE_ADMIN = "Admin"

    public static final String ADMIN_API_ISSUER = "cdr-register"
    public static final String ADMIN_API_AUDIENCE = "https://wso2ob.com"

    //Selenium Constants
    public static final int DEFAULT_DELAY = 5;

    // Second Factor Authenticator constants
    public static final String AU_OTP_CODE = "123456"

    // DCR constants
    public static final String DCR_REGISTRATION_ENDPOINT = "/open-banking/0.2/register/"
    public static final String DCR_REGISTRATION_ENDPOINT_WITH_DOMAIN = "https://localhost:8243/open-banking/0.2/register"
    public static final String DCR_SSA = new File(auConfiguration.getAppDCRSSAPath()).text
    public static final String DCR_SOFTWARE_PRODUCT_ID = auConfiguration.getAppDCRSoftwareId()
    public static final String DCR_REDIRECT_URI = auConfiguration.getAppDCRRedirectUri()
    public static final String DCR_ALTERNATE_REDIRECT_URI = auConfiguration.getAppDCRAlternateRedirectUri()
    public static final String DCR_AUD_VALUE = auConfiguration.getConsentAudienceValue()
    public static final String DCR_BASE_PATH_TYPE = "DCR"
    public static final String DCR_WITHOUT_TOKEN_ENDPOINT_SIGNINGALGO = "Required parameter tokenEndPointAuthSigningAlg cannot be null"
    public static final String DCR_WITHOUT_TOKEN_ENDPOINT_AUTHMETHOD = "Required parameter tokenEndPointAuthentication cannot be null"
    public static final String DCR_GRANT_TYPES_NULL = "Required parameter grantTypes cannot be null"
    public static final String DCR_WITHOUT_RESPONSE_TYPES = "Required parameter responseTypes cannot be null"
    public static final String DCR_WITHOUT_SSA = "Required parameter software statement cannot be null"
    public static final String DCR_WITHOUT_ID_TOKEN_RESPONSE_ALGO = "Required parameter idTokenEncryptionResponseAlg cannot be null"
    public static final String DCR_WITHOUT_ID_TOKEN_ENCRYPTION_ALGO = "Required parameter idTokenEncryptionResponseAlg cannot be null"
    public static final String DCR_WITHOUT_ID_TOKEN_ENCRYPTION_METHOD = "Required parameter idTokenEncryptionResponseEnc cannot be null"
    public static final String DCR_INVALID_ID_TOKEN_ENCRYPTION_ALGO = "Invalid idTokenEncryptionResponseAlg provided"
    public static final String DCR_INVALID_ID_TOKEN_ENCRYPTION_METHOD = "Invalid idTokenEncryptionResponseEnc provided"
    public static final String DCR_INVALID_REDIRECT_DESCRIPTION = "Invalid callbackUris provided"
    public static final String INSUFFICIENT_SCOPE = "insufficient_scope"
    public static final String UNSUPPORTED_RESPONSE_MODE = "Unsupported Response Mode"
    public static final String ERROR_CODE_INVALID_PAGE = "urn:au-cds:error:cds-all:Field/InvalidPage"
    public static final String INVALID_PAGE = "Invalid Page"

    /**
     * Mock Register Constants
     */
    public static final String MOCK_REGISTER_HOST = auConfiguration.getMockCDRHostname()
    public static final String MOCK_INFO_SEC_BASE_URL = "https://" + MOCK_REGISTER_HOST + ":7001"
    public static final String MOCK_ADMIN_BASE_URL = "https://" + MOCK_REGISTER_HOST + ":7006"
    public static final String MOCK_TOKEN_ENDPOINT = "/idp/connect/token"
    public static final String MOCK_CLIENT_ASSERTION_ENDPOINT = "/loopback/MockDataRecipientClientAssertion"
    public static final String MOCK_SSA_ENDPOINT = "/cdr-register/v1/banking/data-recipients/brands"
    public static final String MOCK_METADATA_ENDPOINT = "/admin/metadata"

    //Mock ADR brandIds and their software products loaded at the CDR mock Register
    public static final String MOCK_ADR_BRAND_ID_1 = "20c0864b-ceef-4de0-8944-eb0962f825eb";
    public static final String MOCK_ADR_BRAND_ID_1_SOFTWARE_PRODUCT_1 = "63bc22ac-6fd2-4e85-a979-c2fc7c4db9da"
    public static final String MOCK_ADR_BRAND_ID_1_SOFTWARE_PRODUCT_2 = "86ecb655-9eba-409c-9be3-59e7adf7080d"
    public static final String MOCK_ADR_BRAND_ID_1_SOFTWARE_PRODUCT_3 = "9381dad2-6b68-4879-b496-c1319d7dfbc9"
    public static final String MOCK_ADR_BRAND_ID_1_SOFTWARE_PRODUCT_4 = "d3c44426-e003-4604-aa45-4137e45dfbc4"
    public static final String MOCK_ADR_BRAND_ID_2 = "ebbcc2f2-817e-42b8-8a28-cd45902159e0";
    public static final String MOCK_ADR_BRAND_ID_2_SOFTWARE_PRODUCT_1 = "5d03d1a6-b83b-4176-a2f4-d0074a205695"
    public static final String MOCK_ADR_BRAND_ID_2_SOFTWARE_PRODUCT_2 = "dafa09db-4433-4203-907a-bdf797c8cd21"

    //status code added
    static final int STATUS_CODE_200 = 200
    static final int STATUS_CODE_201 = 201
    static final int STATUS_CODE_400 = 400
    static final int STATUS_CODE_401 = 401
    static final int STATUS_CODE_404 = 404
    static final int STATUS_CODE_405 = 405
    static final int STATUS_CODE_406 = 406
    static final int STATUS_CODE_422 = 422
    static final int STATUS_CODE_409 = 409
    static final int STATUS_CODE_204 = 204
    static final int STATUS_CODE_403 = 403
    static final int STATUS_CODE_500 = 500

    /**
     * Get the Product Endpoint Version
     * @return product endpoint version
     */
    static Integer getProductEndpointVersion() {
        def productEndpoint = null
        if (API_VERSION.equalsIgnoreCase("1.2.0")) {
            productEndpoint = 2
        } else if (API_VERSION.equalsIgnoreCase("1.3.0")) {
            productEndpoint = 3
        }
        return productEndpoint
    }

    //Payload Links
    public static final String LINKS_SELF = "links.self"
    public static final String LINKS_FIRST = "links.first"
    public static final String LINKS_PREV = "links.prev"
    public static final String LINKS_NEXT = "links.next"
    public static final String LINKS_LAST = "links.last"
    public static final String DATA = "data"
    public static final String META = "meta"

    //Response Payloads Validations
    public static final String RESPONSE_DATA_BULK_ACCOUNTID_LIST = "data.accounts.accountId"
    public static final String RESPONSE_DATA_SINGLE_ACCOUNTID = "data.accountId"
    public static final String RESPONSE_DATA_BULK_BALANCE_LIST = "data.balances.accountId"
    public static final String RESPONSE_DATA_TRANSACTION_LIST = "data.transactions.accountId"
    public static final String RESPONSE_DATA_DIRECT_DEBIT_AUTH = "data.directDebitAuthorisations"
    public static final String RESPONSE_DATA_SCHEDULE_PAY = "data.scheduledPayments"
    public static final String RESPONSE_DATA_PAYEE = "data.payees"
    public static final String RESPONSE_DATA_TRANSACTIONID = "data.transactionId"
    public static final String RESPONSE_DATA_PAYEEID = "data.payeeId"
    public static final String RESPONSE_DATA_PRODUCTS = "data.products"

    public static final String PARAM_CUSTOMER_ACCOUNT_TYPE = "customerAccountType"
    public static final String INCORRECT_ACC_ID = "1234567"
    public static final String QUERY_PARAM_USERID = "userId"
    public static final String QUERY_PARAM_ACCID = "accountId"
    public static final String PARAM_PERMISSION_STATUS = "PermissionStatus"

    public static final String TPP_ERROR_CATEGORY = "tppMessages[0].category"
    public static final String TPP_ERROR_CODE = "tppMessages[0].code"
    public static final String TPP_ERROR_CODE_FORMAT_ERROR = "FORMAT_ERROR"
    public static final String TPP_ERROR_CODE_VALUE = "ERROR"
    public static final String TPP_ERROR_CODE_FORMAT_INVALID= "REQUESTED_FORMATS_INVALID"

    public static final String VALUE_KEY = "value"
    public static final String HTML_RESPONSE_ATTR = "response="
    public static final String CURRENT = "CURRENT"
    public static final String ALL = "ALL"
    public static final String PAGE_SIZE = "PAGE_SIZE"
    public static final String UPDATED_SINCE = "UPDATED_SINCE"
    public static final String BRAND = "BRAND"
    public static final String EFFECTIVE = "EFFECTIVE"

    public static final String RESPONSE_DATA_CUSTOMERUTYPE = "data.customerUType"
    public static final String RESPONSE_DATA_PERSON = "data.person"
    public static final String RESPONSE_DATA_ORGANIZATION = "data.organisation"
    public static final String RESPONSE_EXPIRES_IN = "expiresIn"
    public static final String CDR_ARRANGEMENT_ID = "cdr_arrangement_id"
    public static final String INVALID_REQUEST = "invalid_request"
    public static final String INVALID_REQUEST_OBJECT = "invalid_request_object"
}

