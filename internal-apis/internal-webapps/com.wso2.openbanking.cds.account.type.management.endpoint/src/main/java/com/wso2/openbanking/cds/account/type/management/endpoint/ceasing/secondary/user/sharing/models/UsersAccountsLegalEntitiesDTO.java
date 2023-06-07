/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.sharing.models;

import java.util.ArrayList;

/**
 * Ceasing Secondary User - UsersAccountsLegalEntitiesResource
 */
public class UsersAccountsLegalEntitiesDTO {
    private String userID;
    private ArrayList<SecondaryUser> secondaryUsers = null;

    public UsersAccountsLegalEntitiesDTO(String userID) {
        this.userID = userID;
    }

    // userID
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    // secondaryUsers
    public ArrayList<SecondaryUser> getSecondaryUsers() {
        return secondaryUsers;
    }

    public void setSecondaryUsers(ArrayList<SecondaryUser> secondaryUsers) {
        this.secondaryUsers = secondaryUsers;
    }

    public void addSecondaryUser(SecondaryUser secondaryUser) {
        if (this.secondaryUsers == null) {
            this.secondaryUsers = new ArrayList<>();
        }
        this.secondaryUsers.add(secondaryUser);
    }

    /**
     * Secondary User
     */
    public static class SecondaryUser {
        private String secondaryUserID;
        private ArrayList<Account> accounts;


        public SecondaryUser(String secondaryUserID) {
            this.secondaryUserID = secondaryUserID;
        }

        public SecondaryUser(String secondaryUserID, ArrayList<Account> accounts) {
            this.secondaryUserID = secondaryUserID;
            this.accounts = accounts;
        }

        // secondaryUserID
        public String getSecondaryUserID() {
            return secondaryUserID;
        }

        public void setSecondaryUserID(String secondaryUserID) {
            this.secondaryUserID = secondaryUserID;
        }

        // accounts
        public ArrayList<Account> getAccounts() {
            return accounts;
        }

        public void setAccounts(ArrayList<Account> accounts) {
            this.accounts = accounts;
        }

        public void addAccount(Account account) {
            if (this.accounts == null) {
                this.accounts = new ArrayList<>();
            }
            this.accounts.add(account);
        }
    }

    /**
     * Account
     */
    public static class Account {

        private String accountID;
        private ArrayList<LegalEntity> legalEntities;

        public Account(String accountID, ArrayList<LegalEntity> legalEntities) {
            this.accountID = accountID;
            this.legalEntities = legalEntities;
        }

        // accountID
        public String getAccountID() {
            return accountID;
        }

        public void setAccountID(String accountID) {
            this.accountID = accountID;
        }

        // legalEntities
        public ArrayList<LegalEntity> getLegalEntities() {
            return legalEntities;
        }

        public void setLegalEntities(ArrayList<LegalEntity> legalEntities) {
            this.legalEntities = legalEntities;
        }

        public void addLegalEntity(LegalEntity legalEntity) {
            if (this.legalEntities == null) {
                this.legalEntities = new ArrayList<>();
            }
            this.legalEntities.add(legalEntity);
        }
    }

    /**
     * Legal Entity
     */
    public static class LegalEntity {
        private String legalEntityID;

        private String legalEntityName;
        private String legalEntitySharingStatus;

        public LegalEntity(String legalEntityID, String legalEntityName, String legalEntitySharingStatus) {
            this.legalEntityID = legalEntityID;
            this.legalEntityName = legalEntityName;
            this.legalEntitySharingStatus = legalEntitySharingStatus;
        }

        // legalEntityID
        public String getLegalEntityID() {
            return legalEntityID;
        }

        public void setLegalEntityID(String legalEntityID) {
            this.legalEntityID = legalEntityID;
        }

        // legalEntityName
        public String getLegalEntityName() {
            return legalEntityName;
        }

        public void setLegalEntityName(String legalEntityName) {
            this.legalEntityName = legalEntityName;
        }

        // legalEntitySharingStatus
        public String getLegalEntitySharingStatus() {
            return legalEntitySharingStatus;
        }

        public void setLegalEntitySharingStatus(String legalEntitySharingStatus) {
            this.legalEntitySharingStatus = legalEntitySharingStatus;
        }
    }


    @Override
    public String toString() {
        return "UsersAccountsLegalEntitiesResource{" +
                "userID='" + userID + '\'' +
                ", secondaryUsers=" + secondaryUsers +
                '}';
    }
}
