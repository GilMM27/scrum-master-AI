package com.springboot.MyTodoList.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import com.springboot.MyTodoList.util.UUIDConverter;

/*
    Representation of the USERS table that exists already
    in the autonomous database
*/
@Entity
@Table(name = "USERS")
public class Users {
    @Id
    @Convert(converter = UUIDConverter.class)
    @Column(name = "USER_ID", nullable = false)
    UUID userId;
    @Column(name = "USERNAME", nullable = false)
    String username;
    @Column(name = "EMAIL", nullable = false, unique = true)
    String email;
    @Column(name = "PASSWORD_HASH", nullable = false)
    String passwordHash;
    @Column(name = "TELEGRAM_ID", unique = true)
    String telegramId;
    @Column(name = "CELL_PHONE", nullable = false)
    String cellPhone;
    @Enumerated(EnumType.STRING)
    @Column(name = "USER_ROLE", nullable = false)
    UserRole userRole;
    @Column(name = "CREATED_AT")
    OffsetDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "ACCOUNT_STATUS", nullable = false)
    AccountStatus accountStatus;

    public Users() {
    }

    public Users(UUID userId, String username, String email, String passwordHash, String telegramId, String cellPhone, UserRole userRole, OffsetDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.telegramId = telegramId;
        this.cellPhone = cellPhone;
        this.userRole = userRole;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    @Override
    public String toString() {
        return "Users{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", telegramId='" + telegramId + '\'' +
                ", cellPhone='" + cellPhone + '\'' +
                ", userRole='" + userRole + '\'' +
                ", accountStatus='" + accountStatus + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
