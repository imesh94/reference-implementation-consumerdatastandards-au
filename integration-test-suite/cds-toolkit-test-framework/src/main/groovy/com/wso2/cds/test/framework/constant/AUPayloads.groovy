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
}
