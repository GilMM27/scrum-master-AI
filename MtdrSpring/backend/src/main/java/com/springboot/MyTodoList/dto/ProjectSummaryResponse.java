package com.springboot.MyTodoList.dto;

import java.util.UUID;

public class ProjectSummaryResponse {
    private UUID projectId;
    private String name;

    public ProjectSummaryResponse() {
    }

    public ProjectSummaryResponse(UUID projectId, String name) {
        this.projectId = projectId;
        this.name = name;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
