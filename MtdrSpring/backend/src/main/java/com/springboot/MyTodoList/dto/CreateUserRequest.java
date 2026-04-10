package com.springboot.MyTodoList.dto;

import com.springboot.MyTodoList.model.UserRole;

public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
    private String cellPhone;
    private UserRole userRole;

    public CreateUserRequest() {}

    public CreateUserRequest(String username, String email, String password, String cellPhone, UserRole userRole) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.cellPhone = cellPhone;
        this.userRole = userRole;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}