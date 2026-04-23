package com.springboot.MyTodoList.dto;

import java.util.UUID;

public class TaskAssigneeResponse {
    private UUID userId;
    private String username;

    public TaskAssigneeResponse() {
    }

    public TaskAssigneeResponse(UUID userId, String username) {
        this.userId = userId;
        this.username = username;
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
}
