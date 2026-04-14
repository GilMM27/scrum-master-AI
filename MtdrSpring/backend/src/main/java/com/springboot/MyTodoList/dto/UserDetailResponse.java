package com.springboot.MyTodoList.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.springboot.MyTodoList.model.AccountStatus;
import com.springboot.MyTodoList.model.UserRole;

public class UserDetailResponse {
    private UUID userId;
    private String username;
    private String email;
    private UserRole userRole;
    private AccountStatus accountStatus;
    private String telegramId;
    private String cellPhone;
    private OffsetDateTime createdAt;

    public UserDetailResponse() {
    }

    public UserDetailResponse(UUID userId, String username, String email, UserRole userRole, AccountStatus accountStatus, String telegramId, String cellPhone, OffsetDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.userRole = userRole;
        this.accountStatus = accountStatus;
        this.telegramId = telegramId;
        this.cellPhone = cellPhone;
        this.createdAt = createdAt;
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

    public String getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
