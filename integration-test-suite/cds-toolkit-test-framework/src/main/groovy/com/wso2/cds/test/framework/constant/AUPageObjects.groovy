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

/**
 * Class for keep automation Page objects
 */
class AUPageObjects {

    public static String CONSENT_SUBMIT_XPATH = "//*[@id='approve']"
    public static String CONSENT_DENY_XPATH = "//*[@value='Deny']"
    public static String CONSENT_CANCEL_XPATH = "//*[@value='Cancel']"
    public static String CONFIRM_CONSENT_DENY_XPATH = "//*[@value='Deny']"
    public static String ADR_NAME_HEADER_XPATH = "//h3[@class='ui header']"
    public static String SINGLE_ACCOUNT_XPATH = "//option[contains(text(),'account_1')]"
    public static String SINGLE_ACCOUNT_XPATH_200 = "//input[@id='account_1']"
    public static String ALT_SINGLE_ACCOUNT_XPATH = "//option[contains(text(),'account_2')]"
    public static String ALT_SINGLE_ACCOUNT_XPATH_200 = "//input[@id='account_2']"
    public static String LBL_WHERE_TO_MANAGE_INSTRUCTION_XPATH = "//div[contains(text(),'Where to manage this arrangement')]//h5"
    public static String LBL_NEW_PAYEES_INDICATOR_XPATH = "//b[contains(text(),'Saved payees')]/following-sibling::span[contains(text(),'New')]"
    public static String LBL_NEW_SHARING_DURATION_XPATH = "//span[@id='consent-expiry-date']/following-sibling::span[contains(text(),'New')]"
    public static String LBL_ACCOUNT_1_ID_XPATH = "//input[@id='account_1']//following::small[1]"
    public static String LBL_ACCOUNT_2_ID_XPATH = "//input[@id='account_2']//following::small[1]"
    public static String LBL_SELECT_THE_ACCOUNTS_XPATH = "//h5[contains(text(),'Select the accounts you wish to authorise')]"
    public static String CONSENT_EXPIRY_XPATH = "//button[@id='consent-expiry-date']"
    public static String NEGATIVE_SHARING_DURATION_ERROR_PATH = "//td[contains(text(),'Negative sharing_duration')]"
    public static String CONSENT_CONFIRM_XPATH = "//input[@id='approve']"
    public static String CONSENT_AUTHORIZE_FLOW_BACK_XPATH = "//input[@id='back']"

    public static String LBL_PERMISSION_HEADER_ORG_PROFILE = "//button[contains(text(),'Organisation profile')]"
    public static String LBL_PERMISSION_HEADER_ACC_NAME = "//button[contains(text(),'Account name, type, and balance')]"
    public static String LBL_PERMISSION_HEADER_ACC_BAL = "//button[contains(text(),'Account balance and details')]"
    public static String LBL_PERMISSION_HEADER_TRA_DETAILS = "//button[contains(text(),'Transaction details')]"
    public static String LBL_PERMISSION_HEADER_PAYMENT_READ = "//button[contains(text(),'Direct debits and scheduled payments')]"
    public static String LBL_PERMISSION_HEADER_PAYEES = "//button[contains(text(),'Saved payees')]"
    public static String LBL_PERMISSION_LIST_ITEM_1 = "./following::ul[@class='scopes-list padding']//li[1]"
    public static String LBL_PERMISSION_LIST_ITEM_2 = "./following::ul[@class='scopes-list padding']//li[2]"
    public static String LBL_PERMISSION_LIST_ITEM_3 = "./following::ul[@class='scopes-list padding']//li[3]"
    public static String LBL_PERMISSION_LIST_ITEM_4 = "./following::ul[@class='scopes-list padding']//li[4]"
    public static String LBL_PERMISSION_LIST_ITEM_5 = "./following::ul[@class='scopes-list padding']//li[5]"
    public static String LBL_PERMISSION_LIST_ITEM_6 = "./following::ul[@class='scopes-list padding']//li[6]"
    public static String LBL_PERMISSION_LIST_ITEM_7 = "./following::ul[@class='scopes-list padding']//li[7]"
    public static String LBL_PERMISSION_LIST_ITEM_8 = "./following::ul[@class='scopes-list padding']//li[8]"
    public static String LBL_PERMISSION_LIST_ITEM_9 = "./following::ul[@class='scopes-list padding']//li[9]"
    public static String LBL_PERMISSION_LIST_ITEM_10 = "./following::ul[@class='scopes-list padding']//li[10]"
    public static String LBL_PERMISSION_LIST_ITEM_11 = "./following::ul[@class='scopes-list padding']//li[11]"

    public static String LBL_AUTHORISED_STATUS = "//div[@id='heading0acc']//div[@class='p1 consent-status ml-3 ml-auto align-self-center']"
    public static String LBL_REVOKED_STATUS = "//div[@id='heading2acc']//div[@class='p1 consent-status ml-3 ml-auto align-self-center']"
    public static String LBL_CONSENT_ACCOUNT_1 = "//div[@id='heading0acc']"
    public static String LBL_CONSENT_ACCOUNT_DETAILS_LIST_ITEM_1 = "//div[@id='accordion0acc']//h4[contains(text(), " +
            "'Account name, type and balance')]"
    public static String LBL_STOP_SHARING_BUTTON_OF_ACCOUNT_1 = "//button[@id='denyBtn0acc']"
    public static String LBL_CONSENT_ACCOUNT_2 = "//div[@id='heading1acc']"
    public static String LBL_STOP_SHARING_BUTTON_OF_ACCOUNT_2 = "//button[@id='denyBtn1acc']"
    public static String LBL_TEXT_AREA_OF_REVOKE_REASON = "//textarea[@id='revokeReason']"
    public static String LBL_REVOKE_BUTTON = "//button[@id='denyConfirmBtn']"
    public static String LBL_CONSENT_GRANTED_DATE = "//div[@id='heading0acc']//h6[2]"
    public static String LBL_CONSENT_GAVE = "//div[@id='accordion0acc']//h5"
    public static String LBL_CONSENT_EXPIRE = "//div[@id='accordion0acc']//h5[2]"
    public static String LBL_OF_SHARING_PERIOD = "//div[@id='accordion0acc']//h5[3]"
    public static String LBL_CONSENT_GAVE_DATE = "//div[@id='accordion0acc']//div[@id='Directdebitsandscheduledpayments']" +
            "/following-sibling::div/h5[text()='When you gave consent']/following-sibling::ul[1]/li"
    public static String LBL_CONSENT_EXPIRE_DATE = "//div[@id='accordion0acc']//div[@id='Directdebitsandscheduledpayments']" +
            "/following-sibling::div/h5[text()='When you gave consent']/following-sibling::ul[2]/li"
    public static String LBL_OF_SHARING_PERIOD_DATE = "//div[@id='accordion0acc']//div[@id='Directdebitsandscheduledpayments']" +
            "/following-sibling::div/h5[text()='When you gave consent']/following-sibling::ul[3]/li"
    public static String LBL_BANK_ACCOUNT_BASIC_READ = "//div[@id='accordion0acc']//div[@id='headingAccountnametypeandbalance']//h4"
    public static String LBL_BANK_ACCOUNT_DETAIL_READ = "//div[@id='accordion0acc']//div[@id='headingAccountnumbersandfeatures']//h4"
    public static String LBL_BANK_PAYEES_READ = "//div[@id='accordion0acc']//div[@id='headingSavedpayees']//h4"
    public static String LBL_BANK_TRANSACTION_READ = "//div[@id='accordion0acc']//div[@id='headingTransactiondetails']//h4"
    public static String LBL_BANK_CUSTOMER_DETAIL_READ = "//div[@id='accordion0acc']//div[@id='headingOrganisationcontactdetails']//h4"
    public static String LBL_BANK_CUSTOMER_BASIC_READ = "//div[@id='accordion0acc']//div[@id='headingOrganisationprofile']//h4"
    public static String LBL_BANK_REGULAR_PAYMENTS_READ = "//div[@id='accordion0acc']//div[@id='headingDirectdebitsandscheduledpayments']//h4"
    public static String LBL_BANK_CUSTOMER_BASIC_READ_INDIVIDUAL = "//div[@id='accordion0acc']//div[@id='headingContactdetails']//h4"
    public static String LBL_BANK_CUSTOMER_DETAIL_READ_INDIVIDUAL = "//div[@id='accordion0acc']//div[@id='headingNameandoccupation']//h4"

    public static String LBL_NAME_OF_ACCOUNT_PATH = "//div[@id='accordion0acc']//div[@id='Accountnametypeandbalance']//li[1]"
    public static String LBL_TYPE_OF_ACCOUNT_PATH = "//div[@id='accordion0acc']//div[@id='Accountnametypeandbalance']//li[2]"
    public static String LBL_ACCOUNT_BALANCE_PATH = "//div[@id='accordion0acc']//div[@id='Accountnametypeandbalance']//li[3]"
    public static String LBL_ACCOUNT_NUMBER_PATH = "//div[@id='accordion0acc']//div[@id='Accountnumbersandfeatures']//li[1]"
    public static String LBL_ACCOUNT_MAIL_ADDRESS_PATH = "//div[@id='accordion0acc']//div[@id='Accountnumbersandfeatures']//li[2]"
    public static String LBL_RATES_FEES_DISCOUNT_PATH = "//div[@id='accordion0acc']//div[@id='Accountnumbersandfeatures']//li[3]"
    public static String LBL_ACCOUNT_TERMS_PATH = "//div[@id='accordion0acc']//div[@id='Accountnumbersandfeatures']//li[4]"
    public static String LBL_DETAILS_OF_SAVED_ACCOUNTS_PATH = "//div[@id='accordion0acc']//div[@id='Savedpayees']//li"
    public static String LBL_INCOMING_AND_OUTGOING_TRANSACTIONS_PATH = "//div[@id='accordion0acc']//div[@id='Transactiondetails']//li[1]"
    public static String LBL_AMOUNTS_AND_DATES_PATH = "//div[@id='accordion0acc']//div[@id='Transactiondetails']//li[2]"
    public static String LBL_DESCRIPTION_OF_TRANSACTION_PATH = "//div[@id='accordion0acc']//div[@id='Transactiondetails']//li[3]"
    public static String LBL_NAME_OF_MONEY_RECIPIENT_PATH = "//div[@id='accordion0acc']//div[@id='Transactiondetails']//li[4]"
    public static String LBL_PHONE_PATH = "//div[@id='accordion0acc']//div[@id='Contactdetails']//li[1]"
    public static String LBL_EMAIL_ADDRESS_PATH = "//div[@id='accordion0acc']//div[@id='Contactdetails']//li[2]"
    public static String LBL_MAIL_ADDRESS_PATH = "//div[@id='accordion0acc']//div[@id='Contactdetails']//li[3]"
    public static String LBL_RESIDENTIAL_ADDRESS_PATH = "//div[@id='accordion0acc']//div[@id='Contactdetails']//li[4]"
    public static String LBL_ORGANISATION_ADDRESS_PATH = "//div[@id='accordion0acc']//div[@id='Organisationcontactdetails']//li[1]"
    public static String LBL_ORGANISATION_MAIL_ADDRESS_PATH = "//div[@id='accordion0acc']//div[@id='Organisationcontactdetails']//li[2]"
    public static String LBL_PHONE_NUMBER_PATH = "//div[@id='accordion0acc']//div[@id='Organisationcontactdetails']//li[3]"
    public static String LBL_NAME_PATH = "//div[@id='accordion0acc']//div[@id='Nameandoccupation']//li[1]"
    public static String LBL_OCCUPATION_PATH = "//div[@id='accordion0acc']//div[@id='Nameandoccupation']//li[2]"
    public static String LBL_AGENT_NAME_ROLE_PATH = "//div[@id='accordion0acc']//div[@id='Organisationprofile']//li[1]"
    public static String LBL_ORGANISATION_NAME_PATH = "//div[@id='accordion0acc']//div[@id='Organisationprofile']//li[2]"
    public static String LBL_ORGANISATION_NUMBER_PATH = "//div[@id='accordion0acc']//div[@id='Organisationprofile']//li[3]"
    public static String LBL_CHARITY_STATUS_PATH = "//div[@id='accordion0acc']//div[@id='Organisationprofile']//li[4]"
    public static String LBL_ESTABLISHMENT_PATH = "//div[@id='accordion0acc']//div[@id='Organisationprofile']//li[5]"
    public static String LBL_INDUSTRY_PATH = "//div[@id='accordion0acc']//div[@id='Organisationprofile']//li[6]"
    public static String LBL_ORGANISATION_TYPE_PATH = "//div[@id='accordion0acc']//div[@id='Organisationprofile']//li[7]"
    public static String LBL_COUNTRY_OF_REGISTRATION_PATH = "//div[@id='accordion0acc']//div[@id='Organisationprofile']//li[8]"
    public static String LBL_DIRECT_DEBITS_PATH = "//div[@id='accordion0acc']//div[@id='Directdebitsandscheduledpayments']//li[1]"
    public static String LBL_SCHEDULED_PAYMENT_PATH = "//div[@id='accordion0acc']//div[@id='Directdebitsandscheduledpayments']//li[2]"

    public static String LBL_USER_ID_PATH = "//div[@class='form-group col-md-4']//input[contains(@id, 'user')]"
    public static String LBL_SEARCH_BUTTON_PATH = "//input[contains(@id, 'searchBtn')]"
    public static String LBL_CONSENT0_STATUS_PATH = "//div[@id='heading0']//div[@class='p1 consent-status ml-3 ml-auto align-self-center']"
    public static String LBL_CONSENT1_STATUS_PATH = "//div[@id='heading1']//div[@class='p1 consent-status ml-3 ml-auto align-self-center']"
    public static String LBL_CONSENT2_STATUS_PATH = "//div[@id='heading2']//div[@class='p1 consent-status ml-3 ml-auto align-self-center']"
    public static String LBL_APPLICATION_MENU_PATH = "//select[contains(@id,'app')]"
    public static String LBL_STATUS_PATH = "//input[contains(@id, 'AccountStatus')]"
    public static String LBL_DATE_RANGE_PATH = "//input[contains(@id, 'daterange')]"
    public static String LBL_APPLY_BUTTON_PATH = "//button[@class='applyBtn btn btn-sm btn-success']"
    public static String LBL_CONSENT0_PATH = "//div[@id='heading0']"
    public static String LBL_REVOKE_BUTTON_PATH = "//button[@id='denyBtn0']"
    public static String LBL_CONFIRM_REVOKE_BUTTON_PATH = "//button[@id='denyConfirm']"
    public static String LBL_USER_ID_VERIFY_PATH = "//div[@id='accordion0']//h5//em"
    public static String LBL_CONSENT0_CREATED_DATE = "//div[@id='heading0']//h6"
    public static String LBL_CONSENT1_CREATED_DATE = "//div[@id='heading1']//h6"

    public static String LBL_BANK_ACCOUNT_BASIC_READ_PATH = "//div[@id='accordion0']//div[@id='headingAccountname,typeandbalance']//h4"
    public static String LBL_BANK_ACCOUNT_DETAIL_READ_PATH = "//div[@id='accordion0']//div[@id='headingAccountnumbersandfeatures']//h4"
    public static String LBL_BANK_PAYEES_READ_PATH = "//div[@id='accordion0']//div[@id='headingSavedpayees']//h4"
    public static String LBL_BANK_TRANSACTION_READ_PATH = "//div[@id='accordion0']//div[@id='headingTransactiondetails']//h4"
    public static String LBL_BANK_CUSTOMER_BASIC_READ_INDIVIDUAL_PATH = "//div[@id='accordion0']//div[@id='headingContactdetails']//h4"
    public static String LBL_BANK_CUSTOMER_DETAIL_READ_PATH = "//div[@id='accordion0']//div[@id='headingOrganisationcontactdetails']//h4"
    public static String LBL_BANK_CUSTOMER_DETAIL_READ_INDIVIDUAL_PATH = "//div[@id='accordion0']//div[@id='headingNameandoccupation']//h4"
    public static String LBL_BANK_CUSTOMER_BASIC_READ_PATH = "//div[@id='accordion0']//div[@id='headingOrganisationprofile']//h4"
    public static String LBL_BANK_REGULAR_PAYMENTS_READ_PATH = "//div[@id='accordion0']//div[@id='headingDirectdebitsandscheduledpayments']//h4"

    public static String LBL_CCPORTAL_ACCOUNT_NAME_TYPE_TAB = "//div[@id='accordion0']//div[@id='Accountnametypeandbalance']//li"
    public static String LBL_CCPORTAL_ACCOUNT_NUMBER_AND_FEATURES_TAB = "//div[@id='accordion0']//div[@id='Accountnumbersandfeatures']//li"
    public static String LBL_CCPORTAL_SAVED_PAYEES_TAB = "//div[@id='accordion0']//div[@id='Savedpayees']//li"
    public static String LBL_CCPORTAL_TRANSACTIONS_DETAIL_TAB = "//div[@id='accordion0']//div[@id='Transactiondetails']//li"
    public static String LBL_CCPORTAL_CONTACT_DETAILS_TAB = "//div[@id='accordion0']//div[@id='Contactdetails']//li"
    public static String LBL_CCPORTAL_ORGANISATION_CONTACT_DETAILS_TAB = "//div[@id='accordion0']//div[@id='Organisationcontactdetails']//li"
    public static String LBL_CCPORTAL_NAME_AND_OCCUPATION_TAB = "//div[@id='accordion0']//div[@id='Nameandoccupation']//li"
    public static String LBL_CCPORTAL_ORGANISATION_PROFILE_TAB = "//div[@id='accordion0']//div[@id='Organisationprofile']//li"
    public static String LBL_CCPORTAL_DIRECT_DEBITS_TAB = "//div[@id='accordion0']//div[@id='Directdebitsandscheduledpayments']//li"

    //Selenium Constants
    public static String AU_USERNAME_FIELD_ID = "usernameUserInput"
    public static String AU_USERNAME_FIELD_XPATH_200 = "//form[@id=\"identifierForm\"]/div//input[@id=\"usernameUserInput\"]"
    public static String AU_PASSWORD_FIELD_ID = "password"
    public static String AU_HEADLESS_TAG = "--headless"
    public static String AU_ACCOUNT_SELECT_DROPDOWN_XPATH = "//*[@id=\"accselect\"]"
    public static String AU_AUTH_SIGNIN_XPATH = "//button[contains(text(),'Sign In')]"
    public static String AU_AUTH_SIGNIN_XPATH_200 = "//input[@value=\"Next\"]"
    public static String AU_CONSENT_DENY_XPATH = "//input[@value='Deny']"
    public static String AU_CONSENT_APPROVE_SUBMIT_ID = "approve"
    public static String AU_IS_USERNAME_ID = "txtUserName"
    public static String AU_IS_PASSWORD_ID = "txtPassword"
    public static String AU_BTN_IS_SIGNING = "//input[@value='Sign-in']"
    public static String AU_BTN_DEVPORTAL_SIGNIN = "//span[contains(text(),'Sign-in')]"
    public static String AU_BTN_CONTINUE = "//button[contains(text(),'Continue')]"
    public static String AU_TAB_APPLICATIONS = "//span[contains(text(),'Applications')]"
    public static String AU_TBL_ROWS = "//tbody/tr"
    public static String AU_TAB_SUBSCRIPTIONS = "//p[text()='Subscriptions']"
    public static String AU_CCPORTAL_SIGNIN_XPATH = "//button[contains(text(),'Sign in')]";

    // Second Factor Authenticator constants
    public static String AU_LBL_SMSOTP_AUTHENTICATOR = "//h2[text()='Authenticating with SMSOTP']"
    public static String AU_TXT_OTP_CODE_ID = "OTPcode"
    public static String AU_BTN_AUTHENTICATE = "//input[@id='authenticate']"
}
