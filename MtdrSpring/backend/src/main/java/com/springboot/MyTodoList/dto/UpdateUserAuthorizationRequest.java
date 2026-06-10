package com.springboot.MyTodoList.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.springboot.MyTodoList.model.AccountStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateUserAuthorizationRequest {
    private AccountStatus accountStatus;
    
    public UpdateUserAuthorizationRequest() {
    }

    public UpdateUserAuthorizationRequest(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }
}
