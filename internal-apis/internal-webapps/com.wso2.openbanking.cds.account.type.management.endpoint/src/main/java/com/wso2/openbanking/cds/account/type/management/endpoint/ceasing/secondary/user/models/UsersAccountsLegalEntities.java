package com.wso2.openbanking.cds.account.type.management.endpoint.ceasing.secondary.user.models;

import java.util.ArrayList;

public class UsersAccountsLegalEntities {
    private String userId;
    private ArrayList<SecondaryUser> secondaryUsers;

    public UsersAccountsLegalEntities(String userId, ArrayList<SecondaryUser> secondaryUsers) {
        this.userId = userId;
        this.secondaryUsers = secondaryUsers;
    }

    public String getUserId() {
        return userId;
    }

    public ArrayList<SecondaryUser> getSecondaryUsers() {
        return secondaryUsers;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setSecondaryUsers(ArrayList<SecondaryUser> secondaryUsers) {
        this.secondaryUsers = secondaryUsers;
    }

    public static class SecondaryUser {
        private String secondaryUserId;
        private ArrayList<Account> accounts;

        public SecondaryUser(String secondaryUserId, ArrayList<Account> accounts) {
            this.secondaryUserId = secondaryUserId;
            this.accounts = accounts;
        }

        public String getSecondaryUserId() {
            return secondaryUserId;
        }

        public ArrayList<Account> getAccounts() {
            return accounts;
        }

        public void setSecondaryUserId(String secondaryUserId) {
            this.secondaryUserId = secondaryUserId;
        }

        public void setAccounts(ArrayList<Account> accounts) {
            this.accounts = accounts;
        }
    }

    public static class Account {
        private String accountId;
        private ArrayList<LegalEntity> legalEntities;

        public Account(String accountId, ArrayList<LegalEntity> legalEntities) {
            this.accountId = accountId;
            this.legalEntities = legalEntities;
        }

        public String getAccountId() {
            return accountId;
        }

        public ArrayList<LegalEntity> getLegalEntities() {
            return legalEntities;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public void setLegalEntities(ArrayList<LegalEntity> legalEntities) {
            this.legalEntities = legalEntities;
        }
    }

    public static class LegalEntity {
        private String legalEntityId;
        private String status;

        public LegalEntity(String legalEntityId, String status) {
            this.legalEntityId = legalEntityId;
            this.status = status;
        }

        public String getLegalEntityId() {
            return legalEntityId;
        }

        public String getStatus() {
            return status;
        }

        public void setLegalEntityId(String legalEntityId) {
            this.legalEntityId = legalEntityId;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
