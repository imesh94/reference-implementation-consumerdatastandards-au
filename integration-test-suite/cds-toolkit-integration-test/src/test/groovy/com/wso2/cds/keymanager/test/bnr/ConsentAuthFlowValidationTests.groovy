/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Software License available at https://wso2.com/licenses/eula/3.1. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.cds.keymanager.test.bnr

import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.automation.consent.AUBasicAuthAutomationStep
import com.wso2.cds.test.framework.constant.AUAccountScope
import com.wso2.cds.test.framework.constant.AUBusinessUserPermission
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.constant.AUPageObjects
import com.wso2.cds.test.framework.utility.AUTestUtil
import com.wso2.openbanking.test.framework.automation.AutomationMethod
import io.restassured.response.Response
import org.openqa.selenium.By
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.nio.charset.Charset

/**
 * Business User Representative Feature - Profile and Account Selection UI Validation Tests.
 * TODO: Enable Profile Selection in order to run this test class
 */
class ConsentAuthFlowValidationTests extends AUTest{

    def clientHeader
    String accountID
    String accountOwnerUserID
    String nominatedRepUserID
    String nominatedRepUserID2

    @BeforeClass(alwaysRun = true)
    void "Nominate Business User Representative"() {
        clientHeader = "${Base64.encoder.encodeToString(getCDSClient().getBytes(Charset.defaultCharset()))}"

        //Get Sharable Account List and Nominate Business Representative with Authorize and View Permissions
        def shareableElements = AUTestUtil.getSharableAccountsList(getSharableBankAccounts())

        accountID =  shareableElements[AUConstants.PARAM_ACCOUNT_ID]
        accountOwnerUserID = shareableElements[AUConstants.ACCOUNT_OWNER_USER_ID]
        nominatedRepUserID = shareableElements[AUConstants.NOMINATED_REP_USER_ID]
        nominatedRepUserID2 = shareableElements[AUConstants.NOMINATED_REP_USER_ID2]

        def updateResponse = updateMultiBusinessUserPermission(clientHeader, accountID, accountOwnerUserID,
                nominatedRepUserID, AUBusinessUserPermission.AUTHORIZE.getPermissionString(), nominatedRepUserID2,
                AUBusinessUserPermission.VIEW.getPermissionString())
        Assert.assertEquals(updateResponse.statusCode(), AUConstants.OK)
    }

    @Test
    void "CDS-477_Verify Profile Selection is displayed in Auth Flow when the configuration is enabled"() {

        //Get Authorisation URL
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri)
                .toURI().toString()

        //Consent Authorisation UI Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Profile and Accounts
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Verify Profile Selection Page contains radio buttons for Business and Individual Profile selections
                        assert authWebDriver.isElementDisplayed(AUPageObjects.BUSINESS_PROFILE_SELECTION)
                        assert authWebDriver.isElementDisplayed(AUPageObjects.INDIVIDUAL_PROFILE_SELECTION)

                    } else {
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath())
                        log.info("Profile Selection is Disabled")
                    }
                }
                .execute()
    }

    @Test
    void "CDS-543_Verify customer language in consent page for individual consumer"() {

        List<AUAccountScope> scopes = [AUAccountScope.BANK_ACCOUNT_BASIC_READ]

        //Get Authorisation URL
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri)
                .toURI().toString()

        //Consent Authorisation UI Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Profile and Accounts
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Select Individual Profile
                        authWebDriver.selectOption(AUPageObjects.INDIVIDUAL_PROFILE_SELECTION)
                        authWebDriver.clickButtonXpath(AUPageObjects.PROFILE_SELECTION_NEXT_BUTTON)

                        //Verify Account Selection Page
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath())
                        authWebDriver.clickButtonXpath(AUTestUtil.getSingleAccountXPath())

                        assert authWebDriver.isElementDisplayed(AUTestUtil.getAltSingleAccountXPath())
                        Assert.assertTrue(authWebDriver.getElementAttribute(AUPageObjects.LBL_PERMISSION_HEADER,
                                AUPageObjects.TEXT).trim().contains(AUConstants.BANK_CUSTOMER_BASIC_READ_INDIVIDUAL))
                        authWebDriver.clickButtonXpath(AUPageObjects.LBL_PERMISSION_HEADER)
                        Assert.assertEquals(authWebDriver.getElementAttribute(AUPageObjects.LBL_PERMISSION_LIST_ITEM_1,
                                AUPageObjects.TEXT).trim(), AUConstants.LBL_NAME)
                        Assert.assertEquals(authWebDriver.getElementAttribute(AUPageObjects.LBL_PERMISSION_LIST_ITEM_2,
                                AUPageObjects.TEXT), AUConstants.LBL_OCCUPATION)

                    } else {
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath())
                        log.info("Profile Selection is Disabled")
                    }
                }
                .execute()
    }

    @Test
    void "CDS-544_Verify customer language in consent page for business consumer"() {

        List<AUAccountScope> scopes = [AUAccountScope.BANK_ACCOUNT_DETAIL_READ]

        //Get Authorisation URL
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri)
                .toURI().toString()

        //Consent Authorisation UI Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Profile and Accounts
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Select Individual Profile
                        authWebDriver.selectOption(AUPageObjects.BUSINESS_PROFILE_SELECTION)
                        authWebDriver.clickButtonXpath(AUPageObjects.PROFILE_SELECTION_NEXT_BUTTON)

                        //Verify Account Selection Page
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getBusinessAccount1CheckBox())
                        authWebDriver.clickButtonXpath(AUTestUtil.getBusinessAccount1CheckBox())

                        //Verify Consent Page
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getBusinessAccount1Label())
                        Assert.assertTrue(authWebDriver.getElementAttribute(AUPageObjects.LBL_PERMISSION_HEADER,
                                AUPageObjects.TEXT).trim().contains(AUConstants.BANK_CUSTOMER_BASIC_READ))
                        Assert.assertEquals(authWebDriver.getElementAttribute(AUPageObjects.LBL_PERMISSION_HEADER_ORG_PROFILE +
                                AUPageObjects.LBL_PERMISSION_LIST_ITEM_1, AUPageObjects.TEXT),
                                AUConstants.LBL_AGENT_NAME_AND_ROLE)
                        Assert.assertEquals(authWebDriver.getElementAttribute(AUPageObjects.LBL_PERMISSION_HEADER_ORG_PROFILE +
                                AUPageObjects.LBL_PERMISSION_LIST_ITEM_2, AUPageObjects.TEXT),
                                AUConstants.LBL_ORGANISATION_NAME)
                        Assert.assertEquals(authWebDriver.getElementAttribute(AUPageObjects.LBL_PERMISSION_HEADER_ORG_PROFILE +
                                AUPageObjects.LBL_PERMISSION_LIST_ITEM_3, AUPageObjects.TEXT),
                                AUConstants.LBL_ORGANISATION_NUMBER)
                        Assert.assertEquals(authWebDriver.getElementAttribute(AUPageObjects.LBL_PERMISSION_HEADER_ORG_PROFILE +
                                AUPageObjects.LBL_PERMISSION_LIST_ITEM_4, AUPageObjects.TEXT),
                                AUConstants.LBL_CHARITY_STATUS)
                        Assert.assertEquals(authWebDriver.getElementAttribute(AUPageObjects.LBL_PERMISSION_HEADER_ORG_PROFILE +
                                AUPageObjects.LBL_PERMISSION_LIST_ITEM_5, AUPageObjects.TEXT),
                                AUConstants.LBL_ESTABLISHMENT_DATE)
                        Assert.assertEquals(authWebDriver.getElementAttribute(AUPageObjects.LBL_PERMISSION_HEADER_ORG_PROFILE +
                                AUPageObjects.LBL_PERMISSION_LIST_ITEM_6, AUPageObjects.TEXT), AUConstants.LBL_INDUSTRY)
                        Assert.assertEquals(authWebDriver.getElementAttribute(AUPageObjects.LBL_PERMISSION_HEADER_ORG_PROFILE +
                                AUPageObjects.LBL_PERMISSION_LIST_ITEM_7, AUPageObjects.TEXT),
                                AUConstants.LBL_ORGANISATION_TYPE)
                        Assert.assertEquals(authWebDriver.getElementAttribute(AUPageObjects.LBL_PERMISSION_HEADER_ORG_PROFILE +
                                AUPageObjects.LBL_PERMISSION_LIST_ITEM_8, AUPageObjects.TEXT),
                                AUConstants.LBL_COUNTRY_OF_REGISTRATION)

                    } else {
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath())
                        log.info("Profile Selection is Disabled")
                    }
                }
                .execute()
    }

    @Test
    void "CDS-484_Verify a Consent denial flow after Business Profile selection"() {

        //Get Authorisation URL
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri)
                .toURI().toString()

        //Consent Authorisation UI Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Profile and Accounts
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Select Individual Profile
                        authWebDriver.selectOption(AUPageObjects.BUSINESS_PROFILE_SELECTION)
                        authWebDriver.clickButtonXpath(AUPageObjects.PROFILE_SELECTION_NEXT_BUTTON)

                        authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_DENY_XPATH)
                        driver.findElement(By.xpath(AUPageObjects.CONFIRM_CONSENT_DENY_XPATH)).click()

                    } else {
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath())
                        log.info("Profile Selection is Disabled")
                    }
                }
                .execute()

        def authUrl = automation.currentUrl.get()
        Assert.assertTrue(AUTestUtil.getDecodedUrl(authUrl).contains("User skip the consent flow"))
        def stateParam = authUrl.split("state=")[1]
        Assert.assertEquals(auAuthorisationBuilder.state, stateParam)
    }

    @Test
    void "CDS-588_Verify a Consent denial flow after Business Account selection"() {

        //Get Authorisation URL
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri)
                .toURI().toString()

        //Consent Authorisation UI Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Profile and Accounts
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Select Individual Profile
                        authWebDriver.selectOption(AUPageObjects.BUSINESS_PROFILE_SELECTION)
                        authWebDriver.clickButtonXpath(AUPageObjects.PROFILE_SELECTION_NEXT_BUTTON)

                        //Select Business Account 1
                        consentedAccount = authWebDriver.getElementAttribute(AUTestUtil.getBusinessAccount1CheckBox(),
                                AUPageObjects.VALUE)
                        authWebDriver.clickButtonXpath(AUTestUtil.getBusinessAccount1CheckBox())

                        authWebDriver.clickButtonXpath(AUPageObjects.CONSENT_DENY_XPATH)
                        driver.findElement(By.xpath(AUPageObjects.CONFIRM_CONSENT_DENY_XPATH)).click()

                    } else {
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath())
                        log.info("Profile Selection is Disabled")
                    }
                }
                .execute()

        def authUrl = automation.currentUrl.get()
        Assert.assertTrue(AUTestUtil.getDecodedUrl(authUrl).contains("User skip the consent flow"))
        def stateParam = authUrl.split("state=")[1]
        Assert.assertEquals(auAuthorisationBuilder.state, stateParam)
    }

    @Test
    void "CDS-540_Consent Authorisation after updating nominated representatives permission from view to authorise"() {

        //Check the permissions of nominated representatives
        def permissionsResponse = getStakeholderPermissions(nominatedRepUserID2, accountID)
        Assert.assertEquals(permissionsResponse.statusCode(), AUConstants.OK)
        Assert.assertEquals(AUTestUtil.parseResponseBody(permissionsResponse, AUTestUtil.getPermissionForUser(accountID)),
                AUBusinessUserPermission.VIEW.getPermissionString())

        //Change Permission from View to Authorise
        def permissionUpdateResponse = updateSingleBusinessUserPermission(clientHeader, accountID,
                accountOwnerUserID, nominatedRepUserID2, AUBusinessUserPermission.AUTHORIZE.getPermissionString())
        Assert.assertEquals(permissionUpdateResponse.statusCode(), AUConstants.OK)

        //Consent Authorisation UI Flow to check the Authorize Permission
        def automation2 = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Profile and Accounts
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Select Individual Profile
                        authWebDriver.selectOption(AUPageObjects.BUSINESS_PROFILE_SELECTION)
                        authWebDriver.clickButtonXpath(AUPageObjects.PROFILE_SELECTION_NEXT_BUTTON)

                        //Check the account selection enabled
                        Assert.assertTrue(authWebDriver.isElementEnabled(AUTestUtil.getBusinessAccount1CheckBox()))

                    } else {
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath())
                        log.info("Profile Selection is Disabled")
                    }
                }
                .execute()
    }

    @Test (dependsOnMethods = "CDS-540_Consent Authorisation after updating nominated representatives permission from view to authorise")
    void "CDS-542_Consent Authorisation after updating nominated representatives permission from authorise to view"() {

        //Check the permissions of nominated representatives
        def permissionsResponse = getStakeholderPermissions(nominatedRepUserID2, accountID)
        Assert.assertEquals(permissionsResponse.statusCode(), AUConstants.OK)
        Assert.assertEquals(AUTestUtil.parseResponseBody(permissionsResponse, AUTestUtil.getPermissionForUser(accountID)),
                AUBusinessUserPermission.AUTHORIZE.getPermissionString())

        //Change Permission from View to Authorise
        def permissionUpdateResponse = updateSingleBusinessUserPermission(clientHeader, accountID,
                accountOwnerUserID, nominatedRepUserID2, AUBusinessUserPermission.VIEW.getPermissionString())
        Assert.assertEquals(permissionUpdateResponse.statusCode(), AUConstants.OK)

        //Consent Authorisation UI Flow to check the VIEW Permission
        def automation2 = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Profile and Accounts
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Select Individual Profile
                        authWebDriver.selectOption(AUPageObjects.BUSINESS_PROFILE_SELECTION)
                        authWebDriver.clickButtonXpath(AUPageObjects.PROFILE_SELECTION_NEXT_BUTTON)

                        //Check the account selection disabled
                        Assert.assertFalse(authWebDriver.isElementEnabled(AUTestUtil.getBusinessAccount1CheckBox()))

                    } else {
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath())
                        log.info("Profile Selection is Disabled")
                    }
                }
                .execute()
    }

    @Test
    void "CDS-589_Verify select all option in account selection page"() {

        //Get Authorisation URL
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri)
                .toURI().toString()

        //Consent Authorisation UI Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Profile and Accounts
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Select Individual Profile
                        authWebDriver.selectOption(AUPageObjects.BUSINESS_PROFILE_SELECTION)
                        authWebDriver.clickButtonXpath(AUPageObjects.PROFILE_SELECTION_NEXT_BUTTON)

                        //Select all accounts
                        authWebDriver.clickButtonXpath(AUPageObjects.BTN_SELECT_ALL)
                        assert authWebDriver.isElementSelected(AUTestUtil.getBusinessAccount1CheckBox())

                    } else {
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath())
                        log.info("Profile Selection is Disabled")
                    }
                }
                .execute()
    }

    void "CDS-510_Verify Users with View Permission are not able to Authorize Consents"() {

        //Get Authorisation URL
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri)
                .toURI().toString()

        //Consent Authorisation UI Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Profile and Accounts
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Select Individual Profile
                        authWebDriver.selectOption(AUPageObjects.BUSINESS_PROFILE_SELECTION)
                        authWebDriver.clickButtonXpath(AUPageObjects.PROFILE_SELECTION_NEXT_BUTTON)

                        //Check the account selection disabled
                        Assert.assertFalse(authWebDriver.isElementEnabled(AUTestUtil.getBusinessAccount1CheckBox()))

                    } else {
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath())
                        log.info("Profile Selection is Disabled")
                    }
                }
                .execute()
    }

    void "CDS-512_Verify a Consent Authorization Flow with non NR"() {

        //Get Authorisation URL
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri)
                .toURI().toString()

        //Consent Authorisation UI Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Profile and Accounts
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Select Individual Profile
                        authWebDriver.selectOption(AUPageObjects.BUSINESS_PROFILE_SELECTION)
                        authWebDriver.clickButtonXpath(AUPageObjects.PROFILE_SELECTION_NEXT_BUTTON)

                        //Check the error message
                        Assert.assertFalse(authWebDriver.isElementDisplayed(AUPageObjects.LBL_NO_ELIGIBLE_ACCOUNTS))

                    } else {
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath())
                        log.info("Profile Selection is Disabled")
                    }
                }
                .execute()
    }

    void "CDS-541_Verify same user nominated for multiple accounts"() {

        //Get Authorisation URL
        response = auAuthorisationBuilder.doPushAuthorisationRequest(scopes, AUConstants.DEFAULT_SHARING_DURATION,
                true, "")
        requestUri = AUTestUtil.parseResponseBody(response, AUConstants.REQUEST_URI)
        authoriseUrl = auAuthorisationBuilder.getAuthorizationRequest(scopes, requestUri)
                .toURI().toString()

        //Consent Authorisation UI Flow
        def automation = getBrowserAutomation(AUConstants.DEFAULT_DELAY)
                .addStep(new AUBasicAuthAutomationStep(authoriseUrl))
                .addStep { driver, context ->
                    AutomationMethod authWebDriver = new AutomationMethod(driver)

                    //Select Profile and Accounts
                    if (auConfiguration.getProfileSelectionEnabled()) {

                        //Select Individual Profile
                        authWebDriver.selectOption(AUPageObjects.BUSINESS_PROFILE_SELECTION)
                        authWebDriver.clickButtonXpath(AUPageObjects.PROFILE_SELECTION_NEXT_BUTTON)

                        //Check account selection page has multiple accounts
                        Assert.assertTrue(authWebDriver.isElementEnabled(AUTestUtil.getBusinessAccount1Label()))
                        Assert.assertTrue(authWebDriver.isElementEnabled(AUTestUtil.getBusinessAccount2Label()))

                    } else {
                        assert authWebDriver.isElementDisplayed(AUTestUtil.getSingleAccountXPath())
                        log.info("Profile Selection is Disabled")
                    }
                }
                .execute()
    }




}
