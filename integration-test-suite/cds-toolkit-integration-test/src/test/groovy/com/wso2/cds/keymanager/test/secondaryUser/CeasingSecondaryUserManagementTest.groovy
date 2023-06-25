/*
  * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
  *
  * This software is the property of WSO2 LLC. and its suppliers, if any.
  * Dissemination of any information or reproduction of any material contained
  * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
  * You may not alter or remove any copyright or other notice from copies of this content.
  */

package com.wso2.cds.keymanager.test.secondaryUser

import com.wso2.cds.test.framework.AUTest
import com.wso2.cds.test.framework.constant.AUConstants
import com.wso2.cds.test.framework.utility.AUTestUtil
import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.nio.charset.Charset

/**
 * Ceasing Secondary User Management Test. Test Class Verifies the Manage Sharing status and Get Sharing Status Endpoints.
 */
class CeasingSecondaryUserManagementTest extends AUTest {

    def shareableElements, clientHeader
    String accountID, userId, legalEntityId1, legalEntityId2
    Response response
    List <String> legalEntityList

    @BeforeClass
    void "Pre Execution Step"() {

        auConfiguration.setPsuNumber(1)
        clientId = auConfiguration.getAppInfoClientID()
        clientHeader = "${Base64.encoder.encodeToString(getCDSClient().getBytes(Charset.defaultCharset()))}"

        //Get Sharable Account List and Secondary User with Authorize Permission
        shareableElements = AUTestUtil.getSecondaryUserDetails(getSharableBankAccounts())

        String accountID =  shareableElements[AUConstants.PARAM_ACCOUNT_ID]
        String userId = auConfiguration.getUserPSUName()

        //Give Secondary User Instruction Permission
        def updateResponse = updateSecondaryUserInstructionPermission(accountID, userId, AUConstants.ACTIVE)
        Assert.assertEquals(updateResponse.statusCode(), AUConstants.OK)

        //Get Legal Entity ID
        legalEntityList = AUTestUtil.getLegalEntityIdList(getLegalEntityIds(userId), userId, accountID)
        legalEntityId1 = legalEntityList[0]
        legalEntityId2 = legalEntityList[1]
    }

    @Test
    void "CDS-631_Block the sharing status for a legal entity"() {

        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId1, AUConstants.BLOCK_ENTITY)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //Check Sharing Status
        def responseSharingStatusLegalEntity = getSharingStatusOfUserAccount(getLegalEntityIds(userId).getBody(),
                userId, accountID, legalEntityId1)
        Assert.assertEquals(responseSharingStatusLegalEntity, AUConstants.BLOCK_ENTITY)
    }

    @Test (dependsOnMethods = "CDS-631_Block the sharing status for a legal entity")
    void "CDS-632_Block an already blocked legal entity"() {

        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId1, AUConstants.BLOCK_ENTITY)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //Check Sharing Status
        def responseSharingStatusLegalEntity = getSharingStatusOfUserAccount(getLegalEntityIds(userId).getBody(),
                userId, accountID, legalEntityId1)
        Assert.assertEquals(responseSharingStatusLegalEntity, AUConstants.BLOCK_ENTITY)
    }

    @Test (dependsOnMethods = "CDS-632_Block an already blocked legal entity")
    void "CDS-633_Unlock the sharing status for a legal entity"() {

        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId1, AUConstants.ACTIVE)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //Check Sharing Status
        def responseSharingStatusLegalEntity = getSharingStatusOfUserAccount(getLegalEntityIds(userId).getBody(),
                userId, accountID, legalEntityId1)
        Assert.assertEquals(responseSharingStatusLegalEntity, AUConstants.ACTIVE)
    }

    @Test
    void "CDS-634_Block multiple legal entities for same user and same account id"() {

        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId1, AUConstants.BLOCK_ENTITY,
                true, accountID, userId, legalEntityId2, AUConstants.BLOCK_ENTITY)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)

        //Check Sharing Status - Legal Entity 1
        def responseSharingStatusLegalEntity1 = getSharingStatusOfUserAccount(getLegalEntityIds(userId).getBody(),
                userId, accountID, legalEntityId1)
        Assert.assertEquals(responseSharingStatusLegalEntity1, AUConstants.BLOCK_ENTITY)

        //Check Sharing Status - Legal Entity 2
        def responseSharingStatusLegalEntity2 = getSharingStatusOfUserAccount(getLegalEntityIds(userId).getBody(),
                userId, accountID, legalEntityId2)
        Assert.assertEquals(responseSharingStatusLegalEntity2, AUConstants.BLOCK_ENTITY)
    }

    @Test
    void "CDS-635_Block sharing status with incorrect status value"() {

        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId1, "Block_Entity")
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        //TODO: Verify the error description from the response
        //Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION), "Invalid sharing status")
    }

    @Test
    void "CDS-636_Block sharing status with incorrect accountId"() {

        response = updateLegalEntityStatus(clientHeader, "1234", userId, legalEntityId1, AUConstants.ACTIVE)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        //TODO: Verify the error description from the response
        //Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION),
        // "Error occurred while unblocking the sharing status for a particular legal entity/entities.")
    }

    @Test
    void "CDS-637_Block sharing status with incorrect userId"() {

        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId1, AUConstants.BLOCK_ENTITY,
                true, accountID, "sam@gold.com", legalEntityId2, AUConstants.BLOCK_ENTITY)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        //TODO: Verify the error description from the response
        //Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION),
        // "Error occurred while unblocking the sharing status for a particular legal entity/entities.")
    }

    @Test
    void "CDS-638_Block sharing status with incorrect legal entity id"() {

        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId1, AUConstants.BLOCK_ENTITY,
                true, accountID, userId, "ABC", AUConstants.BLOCK_ENTITY)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_400)
        //TODO: Verify the error description from the response
        //Assert.assertEquals(AUTestUtil.parseResponseBody(response, AUConstants.ERROR_DESCRIPTION),
        // "Error occurred while unblocking the sharing status for a particular legal entity/entities.")
    }

    @Test
    void "CDS-639_Block sharing status for multiple user Ids"() {

        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId1, AUConstants.BLOCK_ENTITY,
                true, accountID, "admin@wso2.com", legalEntityId2, AUConstants.BLOCK_ENTITY)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
    }

    @Test
    void "CDS-640_Blocking and activating sharing status for legal entities via same request"() {

        response = updateLegalEntityStatus(clientHeader, accountID, userId, legalEntityId1, AUConstants.ACTIVE,
                true, accountID, userId, legalEntityId2, AUConstants.BLOCK_ENTITY)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
    }

    @Test (dependsOnMethods = "CDS-640_Blocking and activating sharing status for legal entities via same request")
    void "CDS-641_Retrieve sharing status of a particular legal entity"() {

        def responseSharingStatusLegalEntity1 = getSharingStatusOfUserAccount(getLegalEntityIds(userId).getBody(),
                userId, accountID, legalEntityId1)
        Assert.assertEquals(responseSharingStatusLegalEntity1, AUConstants.ACTIVE)

        //Check Sharing Status - Legal Entity 2
        def responseSharingStatusLegalEntity2 = getSharingStatusOfUserAccount(getLegalEntityIds(userId).getBody(),
                userId, accountID, legalEntityId2)
        Assert.assertEquals(responseSharingStatusLegalEntity2, AUConstants.BLOCK_ENTITY)
    }

    @Test
    void "CDS-642_Account owner is able to view all the accounts, secondary users, legal entities"() {

        def response = getLegalEntityIds(userId)
        Assert.assertEquals(response.statusCode(), AUConstants.STATUS_CODE_200)
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "${AUConstants.LEGAL_ENTITY_DETAILS}." +
                "${AUConstants.PAYLOAD_PARAM_ACCOUNT_ID}"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, AUConstants.SECONDARY_USERS_USERID))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "${AUConstants.LEGAL_ENTITY_DETAILS}." +
                "${AUConstants.LEGAL_ENTITIES}.${AUConstants.LEGAL_ENTITY_ID}"))
        Assert.assertNotNull(AUTestUtil.parseResponseBody(response, "${AUConstants.LEGAL_ENTITY_DETAILS}." +
                "${AUConstants.LEGAL_ENTITIES}.${AUConstants.SHARING_STATUS}"))
    }
}
