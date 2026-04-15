package com.springboot.MyTodoList.dto;

import java.util.UUID;

import com.springboot.MyTodoList.model.AccountStatus;
import com.springboot.MyTodoList.model.UserRole;

public class UserSummaryResponse {
    private UUID userId;
    private String username;
    private String email;
    private UserRole userRole;
    private AccountStatus accountStatus;

    public UserSummaryResponse() {
    }

    public UserSummaryResponse(UUID userId, String username, String email, UserRole userRole, AccountStatus accountStatus) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.userRole = userRole;
        this.accountStatus = accountStatus;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }
}
