package com.springboot.MyTodoList.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/*
    Representation of the USERS table that exists already
    in the autonomous database
 */
@Entity
@Table(name = "USERS")
public class Users {
    @Id
    @Column(name = "USER_ID", nullable = false)
    int user_id;
    @Column(name = "USERNAME", nullable = false)
    String username;
    @Column(name = "EMAIL", nullable = false, unique = true)
    String email;
    @Column(name = "PASSWORD_HASH", nullable = false)
    String password_hash;
    @Column(name = "TELEGRAM_ID", unique = true)
    String telegram_id;
    @Column(name = "CELL_PHONE", nullable = false)
    String cell_phone;
    @Column(name = "USER_ROLE")
    String user_role;
    @Column(name = "CREATED_AT")
    LocalDateTime created_at;

    public Users() {
    }

    public Users(int user_id, String username, String email, String password_hash, String telegram_id, String cell_phone, String user_role, LocalDateTime created_at) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.password_hash = password_hash;
        this.telegram_id = telegram_id;
        this.cell_phone = cell_phone;
        this.user_role = user_role;
        this.created_at = created_at;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
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
        return password_hash;
    }

    public void setPasswordHash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getTelegramId() {
        return telegram_id;
    }

    public void setTelegramId(String telegram_id) {
        this.telegram_id = telegram_id;
    }

    public String getCellPhone() {
        return cell_phone;
    }

    public void setCellPhone(String cell_phone) {
        this.cell_phone = cell_phone;
    }

    public String getUserRole() {
        return user_role;
    }

    public void setUserRole(String user_role) {
        this.user_role = user_role;
    }

    public LocalDateTime getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Users{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password_hash='" + password_hash + '\'' +
                ", telegram_id='" + telegram_id + '\'' +
                ", cell_phone='" + cell_phone + '\'' +
                ", user_role='" + user_role + '\'' +
                ", created_at=" + created_at +
                '}';
    }
}