/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.models;

import java.util.ArrayList;

/**
 * Ceasing Secondary User - UsersAccountsLegalEntitiesResource
 */
public class UsersAccountsLegalEntitiesResource {
    private String userId;
    private ArrayList<SecondaryUser> secondaryUsers = null;

    public UsersAccountsLegalEntitiesResource(String userId) {
        this.userId = userId;
    }

    // userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        private String secondaryUserId;
        private ArrayList<Account> accounts;


        public SecondaryUser(String secondaryUserId) {
            this.secondaryUserId = secondaryUserId;
        }

        public SecondaryUser(String secondaryUserId, ArrayList<Account> accounts) {
            this.secondaryUserId = secondaryUserId;
            this.accounts = accounts;
        }

        // secondaryUserId
        public String getSecondaryUserId() {
            return secondaryUserId;
        }

        public void setSecondaryUserId(String secondaryUserId) {
            this.secondaryUserId = secondaryUserId;
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

        private String accountId;
        private ArrayList<LegalEntity> legalEntities;

        public Account(String accountId, ArrayList<LegalEntity> legalEntities) {
            this.accountId = accountId;
            this.legalEntities = legalEntities;
        }

        // accountId
        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
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
        private String legalEntityId;
        private String legalEntitySharingStatus;

        public LegalEntity(String legalEntityId, String legalEntitySharingStatus) {
            this.legalEntityId = legalEntityId;
            this.legalEntitySharingStatus = legalEntitySharingStatus;
        }

        // legalEntityId
        public String getLegalEntityId() {
            return legalEntityId;
        }

        public void setLegalEntityId(String legalEntityId) {
            this.legalEntityId = legalEntityId;
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
                "userId='" + userId + '\'' +
                ", secondaryUsers=" + secondaryUsers +
                '}';
    }
}
