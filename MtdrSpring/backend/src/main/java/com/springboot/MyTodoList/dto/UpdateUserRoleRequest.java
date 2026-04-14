package com.springboot.MyTodoList.dto;

import com.springboot.MyTodoList.model.UserRole;

public class UpdateUserRoleRequest {
    private UserRole userRole;

    public UpdateUserRoleRequest() {
    }

    public UpdateUserRoleRequest(UserRole userRole) {
        this.userRole = userRole;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
