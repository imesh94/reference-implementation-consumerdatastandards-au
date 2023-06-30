package com.wso2.cds.test.framework.constant

class AUPayloads {

    /**
     * Get the payload for Single User Nomination.
     * @param accountId
     * @param accountOwnerUserID
     * @param nominatedRepUserID
     * @param permissionType
     * @return
     */
    static String getSingleUserNominationPayload(String accountId, String accountOwnerUserID, String nominatedRepUserID,
                                                 String permissionType) {

        return """
               {
                    "data":[
                         {
                         "accountID":"${accountId}",
                         "accountOwners":[                     
                                "${accountOwnerUserID}"
                             ],
                          "nominatedRepresentatives":[
                             {
                                "name": "${nominatedRepUserID}",
                                "permission": "${permissionType}"
                              }
                            ]
                         }
                        ]
                 }
            """.stripIndent()
    }

    /**
     * Get the payload for Multi User Nomination.
     * @param accountId
     * @param accountOwnerUserID
     * @param nominatedRepUserID
     * @param permissionType
     * @return
     */
    static String getMultiUserNominationPayload(String accountId, String accountOwnerUserID, String nominatedRepUserID,
                                                String permissionType, String nominatedRepUserID2, String permissionType2) {

        return """
               {
                    "data":[
                         {
                         "accountID":"${accountId}",
                         "accountOwners":[                     
                                "${accountOwnerUserID}"
                             ],
                          "nominatedRepresentatives":[
                             {
                                "name": "${nominatedRepUserID}",
                                "permission": "${permissionType}"
                              },
                              {
                                "name": "${nominatedRepUserID2}",
                                "permission": "${permissionType2}"
                              }
                            ]
                         }
                        ]
                 }
            """.stripIndent()
    }

    /**
     * Get payload to delete Single Business User Nomination
     * @param accountId
     * @param accountOwnerUserID
     * @param nominatedRepUserID
     * @return
     */
    static String getSingleUserDeletePayload(String accountId, String accountOwnerUserID, String nominatedRepUserID) {

        return """
               {
                  "data":[
                     {
                        "accountID":"${accountId}",
                        "accountOwners":[
                            "${accountOwnerUserID}"
                        ],
                        "nominatedRepresentatives":[
                           "${nominatedRepUserID}"
                        ]
                     }
                  ]
               }
            """.stripIndent()
    }

    /**
     * Get payload to delete Multiple Business User Nomination
     * @param accountId
     * @param accountOwnerUserID
     * @param nominatedRepUserID
     * @return
     */
    static String getMultiUserDeletePayload(String accountId, String accountOwnerUserID, String nominatedRepUserID,
                                            String nominatedRepUserID2) {

        return """
               {
                    "data":[
                         {
                         "accountID":"${accountId}",
                         "accountOwners":[                     
                                "${accountOwnerUserID}"
                             ],
                          "nominatedRepresentatives":[
                             {
                                "name": "${nominatedRepUserID}"
                              },
                              {
                                "name": "${nominatedRepUserID2}"
                              }
                            ]
                         }
                        ]
                 }
            """.stripIndent()
    }

    /**
     * Get the incorrect payload for Single User Nomination.
     * @param accountId
     * @param accountOwnerUserID
     * @param nominatedRepUserID
     * @param permissionType
     * @return
     */
    static String getIncorrectNominationPayload(String accountId, String accountOwnerUserID, String nominatedRepUserID,
                                                String permissionType) {

        return """
               {
                    "data":[
                         {
                         "accountID":${accountId},
                         "accountOwners":[                     
                                "${accountOwnerUserID}"
                             ],
                          "nominatedRepresentatives":[
                             {
                                "name": "${nominatedRepUserID}",
                                "permission": "${permissionType}"
                              }
                            ]
                         }
                        ]
                 }
            """.stripIndent()
    }

    /**
     * Get incorrect payload to delete Single Business User Nomination
     * @param accountId
     * @param accountOwnerUserID
     * @param nominatedRepUserID
     * @return
     */
    static String getIncorrectUserDeletePayload(String accountId, String accountOwnerUserID, String nominatedRepUserID) {

        return """
               {
                  "data":[
                     {
                        "accountID":${accountId},
                        "accountOwners":[
                            "${accountOwnerUserID}"
                        ],
                        "nominatedRepresentatives":[
                           "${nominatedRepUserID}"
                        ]
                     }
                  ]
               }
            """.stripIndent()
    }

    /**
     * Get the payload for Update Disclosure Options Management Service.
     * @param accountId
     * @param disclosureSharingStatus
     * @return
     */
    static String getDOMSStatusUpdatePayload(List <String> accountId, List <String> disclosureSharingStatus) {

        def requestBody
        if (accountId.size() > 1 && disclosureSharingStatus.size() > 1) {
            requestBody = """
            {
               "Data":[
                     {"${accountId[0]}":"${disclosureSharingStatus[0]}"},
                     {"${accountId[1]}":"${disclosureSharingStatus[1]}"}
               ]
            }
            """.stripIndent()
        } else {
            requestBody = """
            {
               "Data":[
                     {"${accountId[0]}":"${disclosureSharingStatus[0]}"}
               ]
            }
            """.stripIndent()
        }
        return requestBody
    }

    /**
     * Get the payload for Secondary User Instruction Permission Update.
     * @param accountId - Secondary Account ID
     * @param disclosureSharingStatus - Secondary Account Instruction Status
     * @return - Payload
     */
    static String getSecondaryUserInstructionPermissionPayload(String secondaryAccountId, String secondaryUserId,
                                                               String secondaryAccountInstructionStatus = "active",
                                                               boolean otherAccountsAvailability = false) {
        return """
        {
            "data": [
                {
                    "secondaryAccountId": "${secondaryAccountId}",
                    "secondaryUserId": "${secondaryUserId}",
                    "otherAccountsAvailability": ${otherAccountsAvailability},
                    "secondaryAccountInstructionStatus": "${secondaryAccountInstructionStatus}"
                }
            ]
        }
        """.stripIndent()
    }

    /**
     * Get Payload for Block Legal Entity
     * @param secondaryUserId - Secondary User ID
     * @param accountId - Secondary Account ID
     * @param legalEntityId - Legal Entity ID
     * @param sharingStatus - Sharing Status
     * @param isMultipleLegalEntity - Multiple Legal Entity
     * @param secondaryUserId2 - Secondary User ID (Pass only if isMultipleLegalEntity is true)
     * @param accountId2 - Secondary Account ID (Pass only if isMultipleLegalEntity is true)
     * @param legalEntityId2 - Legal Entity ID (Pass only if isMultipleLegalEntity is true)
     * @param sharingStatus2 - Sharing Status (Pass only if isMultipleLegalEntity is true)
     * @return - Payload
     */
    static String getBlockLegalEntityPayload(String secondaryUserId, String accountId, String legalEntityId,
                                             String sharingStatus, boolean isMultipleLegalEntity = false,
                                             String secondaryUserId2 = null, String accountId2 = null,
                                             String legalEntityId2 = null, String sharingStatus2 = null) {

        String payload

        if(isMultipleLegalEntity) {

            payload = """
            {
                "data": [
                    {
                        "secondaryUserId": "${secondaryUserId}",
                        "accountId": "${accountId}",
                        "legalEntityId": ${legalEntityId},
                        "sharingStatus": "${sharingStatus}"
                    },
                    {
                        "secondaryUserId": "${secondaryUserId2}",
                        "accountId": "${accountId2}",
                        "legalEntityId": ${legalEntityId2},
                        "sharingStatus": "${sharingStatus2}"
                    }
                ]
            }
            """.stripIndent()

        } else {
            payload = """
            {
                "data": [
                    {
                        "secondaryUserId": "${secondaryUserId}",
                        "accountId": "${accountId}",
                        "legalEntityId": ${legalEntityId},
                        "sharingStatus": "${sharingStatus}"
                    }
                ]
            }
            """.stripIndent()
        }

        return payload
    }
}
