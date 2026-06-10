package com.springboot.MyTodoList.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDeveloperResponse {
    private UUID userId;
    private String username;

    public ProjectDeveloperResponse() {
    }

    public ProjectDeveloperResponse(UUID userId, String username) {
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
