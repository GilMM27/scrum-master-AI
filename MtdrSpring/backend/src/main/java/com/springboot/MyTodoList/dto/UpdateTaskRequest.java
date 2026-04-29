package com.springboot.MyTodoList.dto;

import java.util.List;
import java.util.UUID;

import com.springboot.MyTodoList.model.TaskPriority;
import com.springboot.MyTodoList.model.TaskStatus;

public class UpdateTaskRequest {
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private UUID sprintId;
    private Integer storyPoints;
    private Integer expectedHours;
    private List<UUID> assigneeIds;

    public UpdateTaskRequest() {
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

    public UUID getSprintId() {
        return sprintId;
    }

    public void setSprintId(UUID sprintId) {
        this.sprintId = sprintId;
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

    public List<UUID> getAssigneeIds() {
        return assigneeIds;
    }

    public void setAssigneeIds(List<UUID> assigneeIds) {
        this.assigneeIds = assigneeIds;
    }
}
