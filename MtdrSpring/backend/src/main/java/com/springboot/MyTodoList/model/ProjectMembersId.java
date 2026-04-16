package com.springboot.MyTodoList.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ProjectMembersId implements Serializable {

    private UUID projectId;
    private UUID userId;

    public ProjectMembersId() {
    }

    public ProjectMembersId(UUID projectId, UUID userId) {
        this.projectId = projectId;
        this.userId = userId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectMembersId that = (ProjectMembersId) o;
        return Objects.equals(this.projectId, that.projectId) && 
               Objects.equals(this.userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, userId);
    }
}