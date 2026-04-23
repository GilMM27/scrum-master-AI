package com.springboot.MyTodoList.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.springboot.MyTodoList.model.TaskPriority;
import com.springboot.MyTodoList.model.TaskStatus;

public class TaskSummaryResponse {
    private UUID taskId;
    private UUID projectId;
    private String title;
    private TaskStatus status;
    private TaskPriority priority;
    private Integer storyPoints;
    private UUID sprintId;
    private String sprintName;
    private List<TaskAssigneeResponse> assignees;
    private Boolean blocked;
    private Boolean inReview;
    private OffsetDateTime createdAt;
    private OffsetDateTime startedAt;
    private OffsetDateTime deliveredAt;
    private Double actualHours;

    public TaskSummaryResponse() {
    }

    public TaskSummaryResponse(
            UUID taskId,
            UUID projectId,
            String title,
            TaskStatus status,
            TaskPriority priority,
            Integer storyPoints,
            UUID sprintId,
            String sprintName,
            List<TaskAssigneeResponse> assignees,
            Boolean blocked,
            Boolean inReview,
            OffsetDateTime createdAt,
            OffsetDateTime startedAt,
            OffsetDateTime deliveredAt,
            Double actualHours
    ) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.storyPoints = storyPoints;
        this.sprintId = sprintId;
        this.sprintName = sprintName;
        this.assignees = assignees;
        this.blocked = blocked;
        this.inReview = inReview;
        this.createdAt = createdAt;
        this.startedAt = startedAt;
        this.deliveredAt = deliveredAt;
        this.actualHours = actualHours;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Double getActualHours() {
        return actualHours;
    }

    public void setActualHours(Double actualHours) {
        this.actualHours = actualHours;
    }
}
