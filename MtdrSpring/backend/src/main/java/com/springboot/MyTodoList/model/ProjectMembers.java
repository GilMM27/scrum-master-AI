package com.springboot.MyTodoList.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import com.springboot.MyTodoList.util.UUIDConverter;

import jakarta.persistence.*;

@Entity
@Table(name = "PROJECT_MEMBERS")
@IdClass(ProjectMembersId.class)
public class ProjectMembers {
    @Id
    @Convert(converter = UUIDConverter.class)
    @Column(name = "PROJECT_ID", nullable = false)
    UUID projectId;

    @Id
    @Convert(converter = UUIDConverter.class)
    @Column(name = "USER_ID", nullable = false)
    UUID userId;

    @Column(name = "JOINED_AT")
    OffsetDateTime joinedAt;

    public ProjectMembers() {
    }

    public ProjectMembers(UUID projectId, UUID userId, OffsetDateTime joinedAt) {
        this.projectId = projectId;
        this.userId = userId;
        this.joinedAt = joinedAt;
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

    public OffsetDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(OffsetDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    @Override
    public String toString() {
        return "ProjectMembers{" +
                "projectId=" + projectId +
                ", userId=" + userId +
                ", joinedAt=" + joinedAt +
                '}';
    }
}