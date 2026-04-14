package com.springboot.MyTodoList.dto;

import com.springboot.MyTodoList.model.AccountStatus;

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
