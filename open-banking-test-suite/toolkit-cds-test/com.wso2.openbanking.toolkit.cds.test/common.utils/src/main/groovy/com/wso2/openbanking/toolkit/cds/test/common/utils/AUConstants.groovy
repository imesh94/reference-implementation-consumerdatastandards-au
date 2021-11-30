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

package com.wso2.openbanking.toolkit.cds.test.common.utils

import com.wso2.openbanking.test.framework.util.ConfigParser

/**
 * Test Constants for AU.
 */
class AUConstants {

    static config = ConfigParser.getInstance()
    static API_VERSION = config.getApiVersion()

    static final CONSENT_SUBMIT_XPATH = "//*[@id='approve']"
    static final CONSENT_DENY_XPATH = "//*[@value='Deny']"
    static final CONSENT_CANCEL_XPATH = "//*[@value='Cancel']"
    static final CONFIRM_CONSENT_DENY_XPATH = "//*[@value='Deny']"
    static final ADR_NAME_HEADER_XPATH = "//h3[@class='ui header']"
    static final SINGLE_ACCOUNT_XPATH = "//option[contains(text(),'account_1')]"
    static final SINGLE_ACCOUNT_XPATH_200 = "//input[@id='account_1']"
    static final ALT_SINGLE_ACCOUNT_XPATH = "//option[contains(text(),'account_2')]"
    static final ALT_SINGLE_ACCOUNT_XPATH_200 = "//input[@id='account_2']"
    static final LBL_WHERE_TO_MANAGE_INSTRUCTION_XPATH = "//div[contains(text(),'Where to manage this arrangement')]//h5"
    static final LBL_NEW_PAYEES_INDICATOR_XPATH = "//b[contains(text(),'Saved payees')]/following-sibling::span[contains(text(),'New')]"
    static final LBL_NEW_SHARING_DURATION_XPATH = "//span[@id='consent-expiry-date']/following-sibling::span[contains(text(),'New')]"
    static final LBL_ACCOUNT_1_ID_XPATH = "//input[@id='account_1']//following::small[1]"
    static final LBL_ACCOUNT_2_ID_XPATH = "//input[@id='account_2']//following::small[1]"
    static final LBL_SELECT_THE_ACCOUNTS_XPATH = "//h5[contains(text(),'Select the accounts you wish to authorise')]"
    public static final String X_V_HEADER = "x-v"
    public static final String X_MIN_HEADER = "x-min-v"
    static final int CDR_ENDPOINT_VERSION = 1
    static final String CDS_100_PATH = "/cds-au/v1"
    static final String CDS_PATH = "/cds-au/v1"
    static final String ACCEPT = "application/json"
    public static final String X_FAPI_INTERACTION_ID = "x-fapi-interaction-id"
    static final String CONTENT_TYPE = "Content-Type"
    public static final String X_FAPI_AUTH_DATE = "x-fapi-auth-date"
    public static final String X_CDS_CLIENT_HEADERS = "x-cds-client-headers"
    public static final String X_FAPI_CUSTOMER_IP_ADDRESS = "x-fapi-customer-ip-address"
    static final String UUID = AUTestUtil.generateUUID()
    static final String DATE = AUTestUtil.getDateAndTime()
    static final String DATE_FORMAT = AUTestUtil.getDate()
    static final String CONSENT_EXPIRE_DATE = AUTestUtil.getTommorowDateAndTime()
    static final String IP = "2001:0db8:85a3:0000:0000:8a2e:0370:7334"
    static final String PRODUCT_CATEGORY = "TRANS_AND_SAVINGS_ACCOUNTS"
    static final String PRODUCT = "product-category"
    static final String STATUS = "open-status"
    static final String STATUS1 = "OPEN"
    static final String GET_ACCOUNTS = "/banking/accounts"
    static final String GET_BALANCES = "/banking/accounts/balances"
    static final String GET_TRANSACTIONS = "/banking/accounts/" + accountID + "/transactions"
    static final String GET_PRODUCTS = "/banking/products"
    static final String TOKEN = "d94c5b2e-b615-366e-862b-374b429e4d5e"
    static final String accountID = "30080012343456"
    static final String GET_META = "/admin/register/metadata"
    static final String GET_STAT = "/admin/metrics"
    static final String X_FAPI_FINANCIAL_ID = "x-fapi-financial-id"
    public static final int X_V_HEADER_ACCOUNTS = 1
    public static final int X_V_HEADER_METRICS = 1
    public static final int X_V_HEADER_CUSTOMER = 1
    public static final int X_V_HEADER_PRODUCTS = getProductEndpointVersion()
    static final String USERNAME = "admin@wso2.com"
    static final String PASSWORD = "wso2123"
    static final String ACCESS_TOKEN = "token"
    static final String CONTENT = "application/x-www-form-urlencoded"
    static final String CONSENT_CONFIRM_XPATH = "//input[@id='approve']"
    static final String CONSENT_AUTHORIZE_FLOW_BACK_XPATH = "//input[@id='back']"
    static final String CODE = "code"
    static final String ERROR_DESCRIPTION = "error_description"
    static final String ERROR_INVALID_SOFTWARE_PRODUCT = "Invalid Software Product"

    static final CONSENT_EXPIRY_XPATH = "//button[@id='consent-expiry-date']"
    static final NEGATIVE_SHARING_DURATION_ERROR_PATH = "//td[contains(text(),'Negative sharing_duration')]"
    static final long DEFAULT_SHARING_DURATION = 60000
    static final long SINGLE_ACCESS_CONSENT = 0
    static final long ONE_YEAR_DURATION = 31536200
    static final long NEGATIVE_DURATION = -3000
    static final long AMENDED_SHARING_DURATION = 90000
    static final long SHORT_SHARING_DURATION = 20

    enum SCOPES {
        BANK_ACCOUNT_BASIC_READ("bank:accounts.basic:read"),
        BANK_ACCOUNT_DETAIL_READ("bank:accounts.detail:read"),
        BANK_TRANSACTION_READ("bank:transactions:read"),
        BANK_PAYEES_READ("bank:payees:read"),
        BANK_REGULAR_PAYMENTS_READ("bank:regular_payments:read"),
        BANK_CUSTOMER_BASIC_READ("common:customer.basic:read"),
        BANK_CUSTOMER_DETAIL_READ("common:customer.detail:read"),
        CDR_REGISTRATION("cdr:registration"),
        ADMIN_METRICS_BASIC_READ("admin:metrics.basic:read"),
        ADMIN_METADATA_UPDATE("admin:metadata:update"),
        PROFILE("profile"),

        private final String value

        SCOPES(String value) {
            this.value = value
        }

        String getScopeString() {
            return this.value
        }

    }

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
    static final int NOCONTENT = 204
    static final int STATUS_CODE_500 = 500

    static final String BULK_ACCOUNT_PATH = "/banking/accounts"
    static final String SINGLE_ACCOUNT_PATH = "/banking/accounts/" + accountID
    static final String BANKING_PRODUCT_PATH = "/banking/products"
    static final String BULK_BALANCES_PATH = "/banking/accounts/balances"
    static final String BULK_DIRECT_DEBITS_PATH = "/banking/accounts/direct-debits"
    static final String BULK_SCHEDULE_PAYMENTS_PATH = "/banking/payments/scheduled"
    static final String BULK_PAYEES = "/banking/payees"
    static final String BULK_CUSTOMER = "/common/customer"
    static final String CUSTOMER_DETAILS = "/common/customer/detail"
    static final String DISCOVERY_STATUS = "/discovery/status"
    static final String DISCOVERY_OUTAGES = "/discovery/outages"
    static final String ACCOUNTS_CONSENT_PATH = "/au100/accounts-validation"
    static final String CDR_ARRANGEMENT_ENDPOINT = "/arrangements/1.0.0"
    static final String INTROSPECTION_ENDPOINT = "/oauth2/introspect"
    static final String CONSENT_STATUS_ENDPOINT = "/api/openbanking/consent-mgt/uk300"
    public static final String PUSHED_AUTHORISATION_BASE_PATH = config.getAuthorisationServerUrl() +
            "/api/openbanking/push-authorization"
    static final String PAR_ENDPOINT = "/par"
    static final String REVOKE_PATH = "/revoke"
    static final String STATUS_PATH = "/account-confirmation"

    static final LBL_PERMISSION_HEADER_ORG_PROFILE = "//button[contains(text(),'Organisation profile')]"
    static final LBL_PERMISSION_HEADER_ACC_NAME = "//button[contains(text(),'Account name, type, and balance')]"
    static final LBL_PERMISSION_HEADER_ACC_BAL = "//button[contains(text(),'Account balance and details')]"
    static final LBL_PERMISSION_HEADER_TRA_DETAILS = "//button[contains(text(),'Transaction details')]"
    static final LBL_PERMISSION_HEADER_PAYMENT_READ = "//button[contains(text(),'Direct debits and scheduled payments')]"
    static final LBL_PERMISSION_HEADER_PAYEES = "//button[contains(text(),'Saved payees')]"
    static final LBL_PERMISSION_LIST_ITEM_1 = "./following::ul[@class='scopes-list padding']//li[1]"
    static final LBL_PERMISSION_LIST_ITEM_2 = "./following::ul[@class='scopes-list padding']//li[2]"
    static final LBL_PERMISSION_LIST_ITEM_3 = "./following::ul[@class='scopes-list padding']//li[3]"
    static final LBL_PERMISSION_LIST_ITEM_4 = "./following::ul[@class='scopes-list padding']//li[4]"
    static final LBL_PERMISSION_LIST_ITEM_5 = "./following::ul[@class='scopes-list padding']//li[5]"
    static final LBL_PERMISSION_LIST_ITEM_6 = "./following::ul[@class='scopes-list padding']//li[6]"
    static final LBL_PERMISSION_LIST_ITEM_7 = "./following::ul[@class='scopes-list padding']//li[7]"
    static final LBL_PERMISSION_LIST_ITEM_8 = "./following::ul[@class='scopes-list padding']//li[8]"
    static final LBL_PERMISSION_LIST_ITEM_9 = "./following::ul[@class='scopes-list padding']//li[9]"
    static final LBL_PERMISSION_LIST_ITEM_10 = "./following::ul[@class='scopes-list padding']//li[10]"
    static final LBL_PERMISSION_LIST_ITEM_11 = "./following::ul[@class='scopes-list padding']//li[11]"

    static final String BANK_CUSTOMER_BASIC_READ = "Organisation profile*"
    static final String BANK_CUSTOMER_DETAIL_READ = "Organisation profile and contact details*‡"
    static final String BANK_ACCOUNT_BASIC_READ = "Account name, type, and balance"
    static final String BANK_ACCOUNT_DETAIL_READ = "Account balance and details‡"
    static final String BANK_TRANSACTION_READ = "Transaction details"
    static final String BANK_PAYEES_READ = "Saved payees"
    static final String BANK_REGULAR_PAYMENTS_READ = "Direct debits and scheduled payments"
    static final String BANK_CUSTOMER_BASIC_READ_INDIVIDUAL = "Name and occupation"
    static final String BANK_CUSTOMER_BASIC_DETAIL_INDIVIDUAL = "Name, occupation, contact details ‡"

    static final LBL_AGENT_NAME_AND_ROLE = "Agent name and role"
    static final LBL_ORGANISATION_NAME = "Organisation name"
    static final LBL_ORGANISATION_NUMBER = "Organisation numbers (ABN or ACN)"
    static final LBL_CHARITY_STATUS = "Charity status"
    static final LBL_ESTABLISHMENT_DATE = "Establishment date"
    static final LBL_INDUSTRY = "Industry"
    static final LBL_ORGANISATION_TYPE = "Organisation type"
    static final LBL_COUNTRY_OF_REGISTRATION = "Country of registration"
    static final LBL_ORGANISATION_ADDRESS = "Organisation address"
    static final LBL_MAIL_ADDRESS = "Mail address"
    static final LBL_PHONE_NUMBER = "Phone number"
    static final LBL_NAME_OF_ACCOUNT = "Name of account"
    static final LBL_TYPE_OF_ACCOUNT = "Type of account"
    static final LBL_ACCOUNT_BALANCE = "Account balance"
    static final LBL_ACCOUNT_NUMBER = "Account number"
    static final LBL_INTEREST_RATES = "Interest rates"
    static final LBL_FEES = "Fees"
    static final LBL_DISCOUNTS = "Discounts"
    static final LBL_ACCOUNT_TERMS = "Account terms"
    static final LBL_ACCOUNT_MAIL_ADDRESS = "Account mail address"
    static final LBL_INCOMING_AND_OUTGOING_TRANSACTIONS = "Incoming and outgoing transactions"
    static final LBL_AMOUNTS = "Amounts"
    static final LBL_DATES = "Dates"
    static final LBL_DESCRIPTION_OF_TRANSACTION = "Descriptions of transactions"
    static final LBL_NAME_OF_MONEY_RECIPIENT = "Who you have sent money to and received money from(e.g.their name)"
    static final LBL_DIRECT_DEBITS = "Direct debits"
    static final LBL_SCHEDULE_PAYMENTS = "Scheduled payments"
    static final LBL_DETAILS_OF_SAVED_ACCOUNTS = "Names and details of accounts you have saved; " +
            "(e.g. their BSB and Account Number, BPay CRN and Biller code, or NPP PayID)"
    static final LBL_WHERE_TO_MANAGE_INSTRUCTION = "You can review and manage this arrangement on the Data Sharing dashboard by " +
            "going to Settings>Data Sharing on the Mock Company Inc.,Mock Software website or app."

    static final String SOLUTION_VERSION_150 = "1.5.0"
    static final String SOLUTION_VERSION_200 = "2.0.0"

    public static LBL_AUTHORISED_STATUS = "//div[@id='heading0acc']//div[@class='p1 consent-status ml-3 ml-auto align-self-center']"
    public static LBL_REVOKED_STATUS = "//div[@id='heading2acc']//div[@class='p1 consent-status ml-3 ml-auto align-self-center']"
    public static LBL_CONSENT_ACCOUNT_1 = "//div[@id='heading0acc']"
    public static LBL_CONSENT_ACCOUNT_DETAILS_LIST_ITEM_1 = "//div[@id='accordion0acc']//h4[contains(text(), " +
            "'Account name, type and balance')]"
    public static LBL_STOP_SHARING_BUTTON_OF_ACCOUNT_1 = "//button[@id='denyBtn0acc']"
    public static LBL_CONSENT_ACCOUNT_2 = "//div[@id='heading1acc']"
    public static LBL_STOP_SHARING_BUTTON_OF_ACCOUNT_2 = "//button[@id='denyBtn1acc']"
    public static LBL_TEXT_AREA_OF_REVOKE_REASON = "//textarea[@id='revokeReason']"
    public static LBL_REVOKE_BUTTON = "//button[@id='denyConfirmBtn']"
    public static LBL_CONSENT_GRANTED_DATE = "//div[@id='heading0acc']//h6[2]"
    public static LBL_CONSENT_GAVE = "//div[@id='accordion0acc']//h5"
    public static LBL_CONSENT_EXPIRE = "//div[@id='accordion0acc']//h5[2]"
    public static LBL_OF_SHARING_PERIOD = "//div[@id='accordion0acc']//h5[3]"
    public static LBL_CONSENT_GAVE_DATE = "//div[@id='accordion0acc']//div[@id='Directdebitsandscheduledpayments']" +
            "/following-sibling::div/h5[text()='When you gave consent']/following-sibling::ul[1]/li"
    public static LBL_CONSENT_EXPIRE_DATE = "//div[@id='accordion0acc']//div[@id='Directdebitsandscheduledpayments']" +
            "/following-sibling::div/h5[text()='When you gave consent']/following-sibling::ul[2]/li"
    public static LBL_OF_SHARING_PERIOD_DATE = "//div[@id='accordion0acc']//div[@id='Directdebitsandscheduledpayments']" +
            "/following-sibling::div/h5[text()='When you gave consent']/following-sibling::ul[3]/li"
    public static LBL_BANK_ACCOUNT_BASIC_READ = "//div[@id='accordion0acc']//div[@id='headingAccountnametypeandbalance']//h4"
    public static LBL_BANK_ACCOUNT_DETAIL_READ = "//div[@id='accordion0acc']//div[@id='headingAccountnumbersandfeatures']//h4"
    public static LBL_BANK_PAYEES_READ = "//div[@id='accordion0acc']//div[@id='headingSavedpayees']//h4"
    public static LBL_BANK_TRANSACTION_READ = "//div[@id='accordion0acc']//div[@id='headingTransactiondetails']//h4"
    public static LBL_BANK_CUSTOMER_DETAIL_READ = "//div[@id='accordion0acc']//div[@id='headingOrganisationcontactdetails']//h4"
    public static LBL_BANK_CUSTOMER_BASIC_READ = "//div[@id='accordion0acc']//div[@id='headingOrganisationprofile']//h4"
    public static LBL_BANK_REGULAR_PAYMENTS_READ = "//div[@id='accordion0acc']//div[@id='headingDirectdebitsandscheduledpayments']//h4"
    public static LBL_BANK_CUSTOMER_BASIC_READ_INDIVIDUAL = "//div[@id='accordion0acc']//div[@id='headingContactdetails']//h4"
    public static LBL_BANK_CUSTOMER_DETAIL_READ_INDIVIDUAL = "//div[@id='accordion0acc']//div[@id='headingNameandoccupation']//h4"

    public static LBL_NAME_OF_ACCOUNT_PATH = "//div[@id='accordion0acc']//div[@id='Accountnametypeandbalance']//li[1]"
    public static LBL_TYPE_OF_ACCOUNT_PATH = "//div[@id='accordion0acc']//div[@id='Accountnametypeandbalance']//li[2]"
    public static LBL_ACCOUNT_BALANCE_PATH = "//div[@id='accordion0acc']//div[@id='Accountnametypeandbalance']//li[3]"
    public static LBL_ACCOUNT_NUMBER_PATH = "//div[@id='accordion0acc']//div[@id='Accountnumbersandfeatures']//li[1]"
    public static LBL_ACCOUNT_MAIL_ADDRESS_PATH = "//div[@id='accordion0acc']//div[@id='Accountnumbersandfeatures']//li[2]"
    public static LBL_RATES_FEES_DISCOUNT_PATH = "//div[@id='accordion0acc']//div[@id='Accountnumbersandfeatures']//li[3]"
    public static LBL_ACCOUNT_TERMS_PATH = "//div[@id='accordion0acc']//div[@id='Accountnumbersandfeatures']//li[4]"
    public static LBL_DETAILS_OF_SAVED_ACCOUNTS_PATH = "//div[@id='accordion0acc']//div[@id='Savedpayees']//li"
    public static LBL_INCOMING_AND_OUTGOING_TRANSACTIONS_PATH = "//div[@id='accordion0acc']//div[@id='Transactiondetails']//li[1]"
    public static LBL_AMOUNTS_AND_DATES_PATH = "//div[@id='accordion0acc']//div[@id='Transactiondetails']//li[2]"
    public static LBL_DESCRIPTION_OF_TRANSACTION_PATH = "//div[@id='accordion0acc']//div[@id='Transactiondetails']//li[3]"
    public static LBL_NAME_OF_MONEY_RECIPIENT_PATH = "//div[@id='accordion0acc']//div[@id='Transactiondetails']//li[4]"
    public static LBL_PHONE_PATH = "//div[@id='accordion0acc']//div[@id='Contactdetails']//li[1]"
    public static LBL_EMAIL_ADDRESS_PATH = "//div[@id='accordion0acc']//div[@id='Contactdetails']//li[2]"
    public static LBL_MAIL_ADDRESS_PATH = "//div[@id='accordion0acc']//div[@id='Contactdetails']//li[3]"
    public static LBL_RESIDENTIAL_ADDRESS_PATH = "//div[@id='accordion0acc']//div[@id='Contactdetails']//li[4]"
    public static LBL_ORGANISATION_ADDRESS_PATH = "//div[@id='accordion0acc']//div[@id='Organisationcontactdetails']//li[1]"
    public static LBL_ORGANISATION_MAIL_ADDRESS_PATH = "//div[@id='accordion0acc']//div[@id='Organisationcontactdetails']//li[2]"
    public static LBL_PHONE_NUMBER_PATH = "//div[@id='accordion0acc']//div[@id='Organisationcontactdetails']//li[3]"
    public static LBL_NAME_PATH = "//div[@id='accordion0acc']//div[@id='Nameandoccupation']//li[1]"
    public static LBL_OCCUPATION_PATH = "//div[@id='accordion0acc']//div[@id='Nameandoccupation']//li[2]"
    public static LBL_AGENT_NAME_ROLE_PATH = "//div[@id='accordion0acc']//div[@id='Organisationprofile']//li[1]"
    public static LBL_ORGANISATION_NAME_PATH = "//div[@id='accordion0acc']//div[@id='Organisationprofile']//li[2]"
    public static LBL_ORGANISATION_NUMBER_PATH = "//div[@id='accordion0acc']//div[@id='Organisationprofile']//li[3]"
    public static LBL_CHARITY_STATUS_PATH = "//div[@id='accordion0acc']//div[@id='Organisationprofile']//li[4]"
    public static LBL_ESTABLISHMENT_PATH = "//div[@id='accordion0acc']//div[@id='Organisationprofile']//li[5]"
    public static LBL_INDUSTRY_PATH = "//div[@id='accordion0acc']//div[@id='Organisationprofile']//li[6]"
    public static LBL_ORGANISATION_TYPE_PATH = "//div[@id='accordion0acc']//div[@id='Organisationprofile']//li[7]"
    public static LBL_COUNTRY_OF_REGISTRATION_PATH = "//div[@id='accordion0acc']//div[@id='Organisationprofile']//li[8]"
    public static LBL_DIRECT_DEBITS_PATH = "//div[@id='accordion0acc']//div[@id='Directdebitsandscheduledpayments']//li[1]"
    public static LBL_SCHEDULED_PAYMENT_PATH = "//div[@id='accordion0acc']//div[@id='Directdebitsandscheduledpayments']//li[2]"

    static final LBL_AUTHORISED = 'Authorised'
    static final LBL_REVOKED = 'Revoked'
    static final LBL_STOP_SHARING = 'Stop sharing'
    static final LBL_CONSENT_GRANTED = 'Consent granted: '
    static final LBL_CREATED_ON = "Created on: "
    static final LBL_WHEN_YOU_GAVE_CONSENT = 'When you gave consent'
    static final LBL_WHEN_YOUR_CONSENT_EXPIRE = 'When your consent will expire'
    static final LBL_SHARING_PERIOD = 'Sharing period'
    static final LBL_RATES_FEES_DISCOUNT = "Interest rates, Fees, Discounts"
    static final LBL_AMOUNTS_AND_DATES = "Amounts, Dates"
    static final LBL_PHONE = "Phone"
    static final LBL_EMAIL_ADDRESS = "Email address"
    static final LBL_RESIDENTIAL_ADDRESS = "Residential address"
    static final LBL_NAME = "Name"
    static final LBL_OCCUPATION = "Occupation"

    static final CONSENT_MANAGER_URL = ConfigParser.getInstance().getAuthorisationServerUrl() + "/consentmgt"
    static final OAUTH2_INTROSPECT_URL = ConfigParser.getInstance().getAuthorisationServerUrl() + "/oauth2/introspect"
    static final CCPORTAL_URL = ConfigParser.getInstance().getAuthorisationServerUrl() + "/ccportal"

    static final ERROR_DETAIL = "errors[0].detail"
    static final ERROR_SOURCE_PARAMETER = "errors[0].source.parameter"
    static final ERROR_SOURCE_POINTER = "errors[0].source.pointer"
    static final ERROR_TITLE = "errors[0].title"
    static final ERROR_CODE = "errors[0].code"
//    static final ERROR_X_V_INVALID = "x-v header in the request is invalid"
//    static final ERROR_X_V_MISSING = "Mandatory header x-v is missing"
//    static final ERROR_X_MIN_V_INVALID = "x-min-v header in the request is invalid"

    static final ERROR_X_V_INVALID = "Requested x-v version is not supported"
    static final ERROR_X_V_MISSING = "Mandatory header x-v is missing"
    static final ERROR_X_MIN_V_INVALID = "Requested x-v version is not supported"
    static final INVALID_FIELD = "Invalid Field"
    static final INVALID_HEADER = "Invalid Header"
    static final UNSUPPORTED_VERSION = "Unsupported Version Requested"
    static final INVALID_VERSION = "Invalid Version Requested"
    static final PAGE_SIZE_EXCEEDED = "Page Size Exceeded"
    static final MISSING_HEADER = "Missing Required Header"
    static final INVALID_BANK_ACC = "Invalid Banking Account"
    static final ACCOUNT_ID_NOT_FOUND = "ID of the account not found or invalid"
    static final INVALID_CLIENT_METADATA = "invalid_client_metadata"
    static final INVALID_RESOURCE = "Invalid Resource Identifier"
    static final INVALID_AUTHORISATION = "Invalid Authorisation Header"
    static final RESOURCE_FORBIDDEN = "Resource Is Forbidden"
    static final INVALID_REDIRECT_URI = "invalid_redirect_uri"
    static final UNAPPROVED_SOFTWARE_STATEMENT = "unapproved_software_statement"
    static final RESOURCE_NOT_FOUND = "Resource Not Found"
    static final MESSAGE_THROTTLED_OUT = "Message throttled out"


    static final ERROR_CODE_MISSING_HEADER = "AU.CDR.Missing.Header"
    static final ERROR_CODE_INVALID_HEADER = "AU.CDR.Invalid.Header"
    static final ERROR_CODE_INVALID_FIELD = "AU.CDR.Invalid.Field"
    static final ERROR_CODE_UNSUPPORTED_VERSION = "AU.CDR.Unsupported.Version"
    static final ERROR_CODE_INVALID_VERSION = "AU.CDR.Invalid.Version"
    static final ERROR_CODE_INVALID_BANK_ACC = "AU.CDR.Resource.InvalidBankingAccount"
    static final ERROR_CODE_INVALID_RESOURCE = "AU.CDR.Resource.Invalid"
    static final ERROR_CODE_PAGE_SIZE_TOO_LARGE = "AU.CDR.Invalid.PageSizeTooLarge"
    static final ERROR_CODE_UNAUTHORIZED = "AU.CDR.Unauthorized"
    static final ERROR_CODE_RESOURCE_FORBIDDEN = "AU.CDR.Entitlements.Forbidden"
    static final ERROR_CODE_RESOURCE_NOTFOUND = "AU.CDR.Resource.NotFound"
    static final ERROR_CODE_TOO_MANY_REQUESTS = "AU.CDR.TooManyRequests"
    static final ERROR_CODE_INVALID_SP_STATUS = "AU.CDR.Entitlements.InvalidAdrSoftwareProductStatus"
    static final ERROR_TITLE_INVALID_SP_STATUS = "ADR Software Product Status Is Invalid"
    static final ERROR_CODE_INVALID_ADR_STATUS = "AU.CDR.Entitlements.InvalidAdrStatus"
    static final ERROR_TITLE_INVALID_ADR_STATUS = "ADR Status Is Invalid"

    static final PARAM_X_V = "x-v"
    static final PARAM_X_MIN_V = "x-min-v"
    static final PARAM_PRODUCT_CATEGORY = "product-category"
    static final PARAM_CDS_CLIENT_HEADER = "x-cds-client-headers"
    static final PARAM_FAPI_INTERACTION_ID = "x-fapi-interaction-id"
    static final String PARAM_FAPI_CUSTOMER_IP_ADDRESS = "x-fapi-customer-ip-address"
    static final String PARAM_FAPI_AUTH_DATE = "x-fapi-auth-date"
    static final PARAM_ACCOUNT_ID = "accountId"
    static final PARAM_PAGE_SIZE = "page-size"
    static final PARAM_AUTHORIZATION = "Authorization"

    static final LBL_USER_ID = "admin@wso2.com"
    static final LBL_CARBON = "@carbon.super"
    static final LBL_ALL_APPLICATION = "All Applications"
    static final LBL_APPLICATION_ID = "app"

    public static LBL_USER_ID_PATH = "//div[@class='form-group col-md-4']//input[contains(@id, 'user')]"
    public static LBL_SEARCH_BUTTON_PATH = "//input[contains(@id, 'searchBtn')]"
    public static LBL_CONSENT0_STATUS_PATH = "//div[@id='heading0']//div[@class='p1 consent-status ml-3 ml-auto align-self-center']"
    public static LBL_CONSENT1_STATUS_PATH = "//div[@id='heading1']//div[@class='p1 consent-status ml-3 ml-auto align-self-center']"
    public static LBL_CONSENT2_STATUS_PATH = "//div[@id='heading2']//div[@class='p1 consent-status ml-3 ml-auto align-self-center']"
    public static LBL_APPLICATION_MENU_PATH = "//select[contains(@id,'app')]"
    public static LBL_STATUS_PATH = "//input[contains(@id, 'AccountStatus')]"
    public static LBL_DATE_RANGE_PATH = "//input[contains(@id, 'daterange')]"
    public static LBL_APPLY_BUTTON_PATH = "//button[@class='applyBtn btn btn-sm btn-success']"
    public static LBL_CONSENT0_PATH = "//div[@id='heading0']"
    public static LBL_REVOKE_BUTTON_PATH = "//button[@id='denyBtn0']"
    public static LBL_CONFIRM_REVOKE_BUTTON_PATH = "//button[@id='denyConfirm']"
    public static LBL_USER_ID_VERIFY_PATH = "//div[@id='accordion0']//h5//em"
    public static LBL_CONSENT0_CREATED_DATE = "//div[@id='heading0']//h6"
    public static LBL_CONSENT1_CREATED_DATE = "//div[@id='heading1']//h6"

    public static LBL_BANK_ACCOUNT_BASIC_READ_PATH = "//div[@id='accordion0']//div[@id='headingAccountname,typeandbalance']//h4"
    public static LBL_BANK_ACCOUNT_DETAIL_READ_PATH = "//div[@id='accordion0']//div[@id='headingAccountnumbersandfeatures']//h4"
    public static LBL_BANK_PAYEES_READ_PATH = "//div[@id='accordion0']//div[@id='headingSavedpayees']//h4"
    public static LBL_BANK_TRANSACTION_READ_PATH = "//div[@id='accordion0']//div[@id='headingTransactiondetails']//h4"
    public static LBL_BANK_CUSTOMER_BASIC_READ_INDIVIDUAL_PATH = "//div[@id='accordion0']//div[@id='headingContactdetails']//h4"
    public static LBL_BANK_CUSTOMER_DETAIL_READ_PATH = "//div[@id='accordion0']//div[@id='headingOrganisationcontactdetails']//h4"
    public static LBL_BANK_CUSTOMER_DETAIL_READ_INDIVIDUAL_PATH = "//div[@id='accordion0']//div[@id='headingNameandoccupation']//h4"
    public static LBL_BANK_CUSTOMER_BASIC_READ_PATH = "//div[@id='accordion0']//div[@id='headingOrganisationprofile']//h4"
    public static LBL_BANK_REGULAR_PAYMENTS_READ_PATH = "//div[@id='accordion0']//div[@id='headingDirectdebitsandscheduledpayments']//h4"

    public static LBL_CCPORTAL_ACCOUNT_NAME_TYPE_TAB = "//div[@id='accordion0']//div[@id='Accountnametypeandbalance']//li"
    public static LBL_CCPORTAL_ACCOUNT_NUMBER_AND_FEATURES_TAB = "//div[@id='accordion0']//div[@id='Accountnumbersandfeatures']//li"
    public static LBL_CCPORTAL_SAVED_PAYEES_TAB = "//div[@id='accordion0']//div[@id='Savedpayees']//li"
    public static LBL_CCPORTAL_TRANSACTIONS_DETAIL_TAB = "//div[@id='accordion0']//div[@id='Transactiondetails']//li"
    public static LBL_CCPORTAL_CONTACT_DETAILS_TAB = "//div[@id='accordion0']//div[@id='Contactdetails']//li"
    public static LBL_CCPORTAL_ORGANISATION_CONTACT_DETAILS_TAB = "//div[@id='accordion0']//div[@id='Organisationcontactdetails']//li"
    public static LBL_CCPORTAL_NAME_AND_OCCUPATION_TAB = "//div[@id='accordion0']//div[@id='Nameandoccupation']//li"
    public static LBL_CCPORTAL_ORGANISATION_PROFILE_TAB = "//div[@id='accordion0']//div[@id='Organisationprofile']//li"
    public static LBL_CCPORTAL_DIRECT_DEBITS_TAB = "//div[@id='accordion0']//div[@id='Directdebitsandscheduledpayments']//li"

    public static BASE_PATH_TYPE_DCR = "DCR"
    public static BASE_PATH_TYPE_ACCOUNT = "Accounts"
    public static BASE_PATH_TYPE_BALANCES = "Balances"
    public static BASE_PATH_TYPE_TRANSACTIONS = "Transactions"
    public static BASE_PATH_TYPE_DIRECT_DEBIT = "Direct-Debit"
    public static BASE_PATH_TYPE_SCHEDULED_PAYMENT = "Scheduled-Payment"
    public static BASE_PATH_TYPE_PAYEES = "Payees"
    public static BASE_PATH_TYPE_PRODUCTS = "Product"
    public static BASE_PATH_TYPE_CUSTOMER = "Customer"
    public static BASE_PATH_TYPE_DISCOVERY = "Discovery"
    public static BASE_PATH_TYPE_CDR_ARRANGEMENT = "CDR-Arrangement"
    public static BASE_PATH_TYPE_ADMIN = "Admin"

    static final String ADMIN_API_ISSUER = "cdr-register"
    static final String ADMIN_API_AUDIENCE = "https://wso2ob.com"

    /**
     * Get the Product Endpoint Version
     * @return product endpoint version
     */
    static int getProductEndpointVersion() {

        def productEndpoint

        if (API_VERSION.equalsIgnoreCase("1.2.0")) {
            productEndpoint = 2

        } else if (API_VERSION.equalsIgnoreCase("1.3.0")) {
            productEndpoint = 3

        }
        return productEndpoint
    }
}
