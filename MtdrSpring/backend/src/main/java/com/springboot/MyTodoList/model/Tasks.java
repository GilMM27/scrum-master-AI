package com.springboot.MyTodoList.model;


import com.springboot.MyTodoList.dto.CreateTaskRequest;
import com.springboot.MyTodoList.listener.TasksEntityListener;
import com.springboot.MyTodoList.util.UUIDConverter;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/*
    representation of the TASKS table that exists already
    in the autonomous database
*/
@Entity
@Table(name = "TASKS")
// @EntityListeners(TasksEntityListener.class)
public class Tasks {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "TASK_ID")
    UUID taskId;
    @Column(name = "TITLE", nullable = false)
    String title;
    @Lob
    @Column(name = "DESCRIPTION")
    String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    TaskStatus status;
    @Column(name = "STORY_POINTS")
    int storyPoints;
    @Convert(converter = UUIDConverter.class)
    @Column(name = "SPRINT_ID")
    UUID sprintId;
    @Column(name = "BLOCKED_AT")
    OffsetDateTime blockedAt;
    @Column(name = "IS_AI_FLAGGED")
    Boolean isAiFlagged;
    @Column(name = "CREATED_AT")
    OffsetDateTime createdAt;
    @Column(name = "DELIVERED_AT")
    OffsetDateTime deliveredAt;
    @Column(name = "STARTED_AT")
    OffsetDateTime startedAt;

    public Tasks() {
    }

    public Tasks(UUID taskId, String title, String description, TaskStatus status, int storyPoints,
                 UUID sprintId,
                 OffsetDateTime blockedAt, Boolean isAiFlagged,
                 OffsetDateTime createdAt, OffsetDateTime deliveredAt, OffsetDateTime startedAt) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.storyPoints = storyPoints;
        this.sprintId = sprintId;
        this.blockedAt = blockedAt;
        this.isAiFlagged = isAiFlagged;
        this.createdAt = createdAt;
        this.deliveredAt = deliveredAt;
        this.startedAt = startedAt;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(int storyPoints) {
        this.storyPoints = storyPoints;
    }

    public UUID getSprintId() {
        return sprintId;
    }

    public void setSprintId(UUID sprintId) {
        this.sprintId = sprintId;
    }

    public OffsetDateTime getBlockedAt() {
        return blockedAt;
    }

    public void setBlockedAt(OffsetDateTime blockedAt) {
        this.blockedAt = blockedAt;
    }

    public Boolean getAiFlagged() {
        return isAiFlagged;
    }

    public void setAiFlagged(Boolean isAiFlagged) {
        this.isAiFlagged = isAiFlagged;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(OffsetDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public void setStartedAt(OffsetDateTime startedAt){
        this.startedAt = startedAt;
    }
    public OffsetDateTime getStartedAt(){
        return this.startedAt;
    }

    
    @Override
    public String toString() {
        return "Tasks{" +
                "taskId=" + taskId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", storyPoints=" + storyPoints +
                ", sprintId=" + sprintId +
                ", blockedAt=" + blockedAt +
                ", isAiFlagged=" + isAiFlagged +
                ", createdAt=" + createdAt +
                ", deliveredAt=" + deliveredAt +
                '}';
    }

    public void updateFromDto(CreateTaskRequest dto) {
        if (dto.getTitle() != null) {
            this.title = dto.getTitle();
        }
        if (dto.getDescription() != null) {
            this.description = dto.getDescription();
        }
        if (dto.getSprintId() != null) {
            this.sprintId = dto.getSprintId();
        }
        if (dto.getStoryPoints() != null) {
            this.storyPoints = dto.getStoryPoints();
        }
    }

    @PrePersist
    @PreUpdate
    public void onTaskUpdate() {

        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }

        if (status == TaskStatus.IN_PROGRESS && startedAt == null) {
            this.startedAt = OffsetDateTime.now();
        }

        if (status == TaskStatus.DONE && deliveredAt == null) {
            this.deliveredAt = OffsetDateTime.now();
        }

    }


}
