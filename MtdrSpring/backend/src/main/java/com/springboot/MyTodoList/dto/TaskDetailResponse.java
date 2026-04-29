package com.springboot.MyTodoList.dto;

import com.springboot.MyTodoList.model.TaskPriority;
import com.springboot.MyTodoList.model.TaskStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class TaskDetailResponse {
    private UUID taskId;
    private UUID projectId;
    private String projectName;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Integer storyPoints;
    private Integer expectedHours;
    private UUID sprintId;
    private String sprintName;
    private List<TaskAssigneeResponse> assignees;
    private Boolean blocked;
    private Boolean inReview;
    private OffsetDateTime createdAt;
    private OffsetDateTime startedAt;
    private OffsetDateTime deliveredAt;
    private OffsetDateTime blockedAt;
    private Double actualHours;
    private Boolean aiFlagged;

    public TaskDetailResponse() {
    }

    public TaskDetailResponse(
            UUID taskId,
            UUID projectId,
            String projectName,
            String title,
            String description,
            TaskStatus status,
            TaskPriority priority,
            Integer storyPoints,
            Integer expectedHours,
            UUID sprintId,
            String sprintName,
            List<TaskAssigneeResponse> assignees,
            Boolean blocked,
            Boolean inReview,
            OffsetDateTime createdAt,
            OffsetDateTime startedAt,
            OffsetDateTime deliveredAt,
            OffsetDateTime blockedAt,
            Double actualHours,
            Boolean aiFlagged
    ) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.projectName = projectName;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.storyPoints = storyPoints;
        this.expectedHours = expectedHours;
        this.sprintId = sprintId;
        this.sprintName = sprintName;
        this.assignees = assignees;
        this.blocked = blocked;
        this.inReview = inReview;
        this.createdAt = createdAt;
        this.startedAt = startedAt;
        this.deliveredAt = deliveredAt;
        this.blockedAt = blockedAt;
        this.actualHours = actualHours;
        this.aiFlagged = aiFlagged;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public Integer getExpectedHours() {
        return expectedHours;
    }

    public void setExpectedHours(Integer expectedHours) {
        this.expectedHours = expectedHours;
    }

    public UUID getSprintId() {
        return sprintId;
    }

    public void setSprintId(UUID sprintId) {
        this.sprintId = sprintId;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public List<TaskAssigneeResponse> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<TaskAssigneeResponse> assignees) {
        this.assignees = assignees;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public Boolean getInReview() {
        return inReview;
    }

    public void setInReview(Boolean inReview) {
        this.inReview = inReview;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(OffsetDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public OffsetDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(OffsetDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public OffsetDateTime getBlockedAt() {
        return blockedAt;
    }

    public void setBlockedAt(OffsetDateTime blockedAt) {
        this.blockedAt = blockedAt;
    }

    public Double getActualHours() {
        return actualHours;
    }

    public void setActualHours(Double actualHours) {
        this.actualHours = actualHours;
    }

    public Boolean getAiFlagged() {
        return aiFlagged;
    }

    public void setAiFlagged(Boolean aiFlagged) {
        this.aiFlagged = aiFlagged;
    }
}